package ca.cihi.cims.service.synchronization;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.content.shared.TabularConcept;
import ca.cihi.cims.content.shared.index.Index;
import ca.cihi.cims.content.shared.Supplement;
import ca.cihi.cims.model.ContentToSynchronize;
import ca.cihi.cims.model.SynchronizationStatus;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.ClassificationService;
import ca.cihi.cims.service.ViewService;
import ca.cihi.cims.web.filter.CurrentContext;

public class SynchronizationServiceImpl implements SynchronizationService, InternalSynchronizationService {

	private final static long instanceId = RandomUtils.nextLong();
	@Autowired
	private ApplicationContext appContext;

	@Autowired
	private ChangeRequestService changeRequestService;
	@Autowired
	private CurrentContext context;
	private final ExecutorService executor = Executors.newFixedThreadPool(10);
	private final Logger log = LogManager.getLogger(SynchronizationServiceImpl.class);
	@Autowired
	private ClassificationService service;
	private final Map<Long, SynchronizationStatus> statuses = new ConcurrentHashMap<Long, SynchronizationStatus>();

	@Autowired
	private ClassPathResource stylesheetCims;

	private final SyncSynchronizationStatusReporter syncSynchronizationStatusReporter = new SyncSynchronizationStatusReporter();
	private InternalSynchronizationService transactional;
	@Autowired
	private ViewService viewService;

	// ---------------------------------------------------------------------------------------------

	@Override
	public List<ContentToSynchronize> getContentToSynchronize() {
		return viewService.getContentToSynchronize(service.getCurrentContextId());
	}

	@Override
	public long getInstanceId() {
		return instanceId;
	}

	@Override
	public SynchronizationStatus getSynchronizationStatus(long changeRequestId) {
		SynchronizationStatus status = statuses.get(changeRequestId);
		if (status == null) {
			status = new SynchronizationStatus(instanceId, Long.MIN_VALUE);
			status.setTotal(-1);
		}
		return status;
	}

	private InternalSynchronizationService getTrx() {
		if (transactional == null) {
			synchronized (this) {
				if (transactional == null) {
					Map<String, InternalSynchronizationService> beans = appContext
							.getBeansOfType(InternalSynchronizationService.class);
					transactional = beans.values().iterator().next();
				}
			}
		}
		return transactional;
	}

	@Override
	@Transactional
	public void synchronize(OptimisticLock lock, User user, long changeRequestId) {
		synchronize(lock, user, changeRequestId, context.context(), syncSynchronizationStatusReporter);
	}

	@Override
	@Transactional
	public void synchronize(OptimisticLock lock, User user, long changeRequestId, ContextAccess access,
			SynchronizationStatusReporter reporter) {
		reporter.start(lock);
		try {
			log.info("Synchronization " + changeRequestId + ": loading ...");
			context.makeCurrentContext(access);
			List<ContentToSynchronize> contents = getContentToSynchronize();
			log.info("Synchronization " + changeRequestId + ": loaded " + contents.size());
			reporter.setTotal(contents.size());
			if (!contents.isEmpty()) {
				for (ContentToSynchronize content : contents) {
					getTrx().synchronizeContent(lock, user, changeRequestId, access, reporter, contents, content);
				}
			}
			log.info("Synchronization " + changeRequestId + ": complete");
			reporter.finish(lock);
		} catch (Exception ex) {
			log.error("Synchronization " + changeRequestId + ": " + ex.getMessage(), ex);
			reporter.throwError(ex);
		}
	}

	@Override
	public void synchronizeAsync(final OptimisticLock lock, final User user, final long changeRequestId) {
		statuses.put(changeRequestId, new SynchronizationStatus(instanceId, lock.getTimestamp()));
		final ContextAccess ctx = context.context();
		executor.execute(() -> {
			log.info("Synchronization " + changeRequestId + ": async execution started");
			getTrx().synchronize(lock, user, changeRequestId, ctx,
					new AsyncSyncronizationStatusReporter(instanceId, changeRequestId, statuses));
		});
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void synchronizeContent(OptimisticLock lock, User user, long changeRequestId, ContextAccess access,
			SynchronizationStatusReporter reporter, List<ContentToSynchronize> contents, ContentToSynchronize content) {
		Object concept = access.load(content.getElementId());
		String code = null;
		if (concept instanceof TabularConcept) {
			TabularConcept tabular = (TabularConcept) concept;
			code = tabular.getCode();
			reporter.startNext(code);
			service.transformConcept(lock, user, tabular);
		} else if (concept instanceof Index) {
			Index index = (Index) concept;
			code = index.getDescription();
			reporter.startNext(code);
			service.transformIndex(lock, user, index);
		} else if (concept instanceof Supplement) {
			Supplement supplement = (Supplement) concept;
			code = supplement.getSupplementDescription(supplement.getLanguage());
			reporter.startNext(code);
			service.transformSupplement(lock, user, supplement);
		}
		log.info("Synchronization " + changeRequestId + ": synchronizing " + code + " of " + contents.size() + ": "
				+ content);
	}

}
