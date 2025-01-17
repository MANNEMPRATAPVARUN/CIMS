package ca.cihi.cims.bll.hg;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.Collection;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.ContextUtils;
import ca.cihi.cims.content.shared.RootConcept;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ContextOperations;

@Component
public class HGContextProvider implements ContextProvider, BeanFactoryAware {

	private final Logger LOGGER = LogManager.getLogger(HGContextProvider.class);

	/*
	 * Used to obtain properly injected prototype-scoped instances of the context transactions.
	 */
	private BeanFactory beanFactory;
	private boolean autoPersist = false;
	@Autowired
	private ContextOperations contextOperations;

	// -----------------------------------------------------------------

	@Override
	public ContextAccess createChangeContext(ContextIdentifier baseCI, Long requestId) {
		return createChangeContext(baseCI.getBaseClassification(), getChangeContextVersionCode(baseCI
				.getBaseClassification()), baseCI.isVersionYear(), baseCI.getContextId(), requestId);
	}

	private ContextAccess createChangeContext(String baseClassification, String ccVersionCode, boolean isVersionYear,
			Long baseStructureId, Long requestId) {
		ContextIdentifier base = contextOperations.findContextById(baseClassification, baseStructureId);
		ContextUtils.ensureContextIsOpen(base);
		ContextUtils.ensureNotAChangeContext(base);
		ContextIdentifier ctxId = contextOperations.createChangeContext(baseClassification, ccVersionCode,
				isVersionYear, baseStructureId, requestId);
		HGContextAccess tx = (HGContextAccess) beanFactory.getBean("HGContextAccess", ctxId, autoPersist);
		return tx;
	}

	@Override
	public ContextAccess createContext(ContextIdentifier baseCI, boolean isVersionYear) {
		ContextUtils.ensureNotAChangeContext(baseCI);

		int newYear = Integer.parseInt(baseCI.getVersionCode());
		newYear++;
		String sNewYear = String.valueOf(newYear);

		ContextIdentifier ctxId = contextOperations.createContext(baseCI, sNewYear, isVersionYear);
		HGContextAccess tx = (HGContextAccess) beanFactory.getBean("HGContextAccess", ctxId, autoPersist);

		updateContextToNewYear(sNewYear, tx);

		return tx;
	}

	@Override
	public Collection<String> findBaseClassifications() {
		return contextOperations.findBaseClassifications();
	}

	@Override
	public Collection<String> findBaseClassificationVersionCodes(String baseClassification) {
		return contextOperations.findBaseClassificationVersionCodes(baseClassification);
	}

	@Override
	public Collection<ContextIdentifier> findBaseClassificationVersionYearVersionCodes(String baseClassification) {
		return contextOperations.findBaseClassificationVersionYearVersionCodes(baseClassification);
	}

	@Override
	public Collection<ContextIdentifier> findBaseContextIdentifiers(String baseClassification) {
		return contextOperations.findBaseContextIdentifiers(baseClassification);
	}

	@Override
	public ContextAccess findContext(ContextDefinition def) {
		ContextIdentifier contextId = null;
		if (def.getVersionCode() != null) {
			contextId = contextOperations.findContextForVersion(def.getBaseClassification(), def.getVersionCode());
		} else {
			contextId = contextOperations.findContextById(def.getBaseClassification(), def.getChangeContextd());
		}
		ContextAccess ctx = findContext(contextId);
		Long changeRequestId = def.getChangeRequestId();
		// FIXME: not sure why [contextId.requestId] is sometimes null
		if (contextId.getRequestId() == null && changeRequestId != null) {
			contextId.setRequestId(changeRequestId);
		}
		return ctx;
	}

	@Override
	public ContextAccess findContext(ContextIdentifier ctxId) {
		if (ctxId == null) {
			throw new IllegalArgumentException("Please provide a non-null context identifier.");
		}
		HGContextAccess tx = (HGContextAccess) beanFactory.getBean("HGContextAccess", ctxId, autoPersist);
		return tx;
	}

	@Override
	public Collection<String> findLanguageCodes(String baseClassification) {
		return contextOperations.findLanguageCodes();
	}

	@Override
	public Collection<ContextIdentifier> findOpenBaseContextIdentifiers(String baseClassification) {
		return contextOperations.findOpenBaseContextIdentifiers(baseClassification);
	}

	@Override
	public Collection<String> findVersionCodes(String baseClassification) {
		return contextOperations.findVersionCodes(baseClassification);
	}

	private synchronized String getChangeContextVersionCode(String baseClassification) {
		return "[" + baseClassification + "] CC " + System.currentTimeMillis();
	}

	@Override
	public boolean isAutoPersist() {
		return autoPersist;
	}

	@Override
	public void setAutoPersist(boolean autoPersist) {
		this.autoPersist = autoPersist;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	private void updateContextToNewYear(String sNewYear, HGContextAccess tx) {
		// Do not attempt on change requests
		ContextUtils.ensureNotAChangeContext(tx.getContextId());

		ContextAccess context = createChangeContext(tx.getContextId(), null);
		RootConcept root = context.findOne(ref(RootConcept.class));

		root.setShortDescription(Language.ENGLISH.getCode(), root.getShortDescription(Language.ENGLISH.getCode())
				.replaceAll(" \\S*$", " " + sNewYear));
		root.setLongDescription(Language.ENGLISH.getCode(), root.getLongDescription(Language.ENGLISH.getCode())
				.replaceAll(" \\S*$", " " + sNewYear));
		root.setUserDescription(Language.ENGLISH.getCode(), root.getUserDescription(Language.ENGLISH.getCode())
				.replaceAll(" \\S*$", " " + sNewYear));

		root.setShortDescription(Language.FRENCH.getCode(), root.getShortDescription(Language.FRENCH.getCode())
				.replaceAll(" \\S*$", " " + sNewYear));
		root.setLongDescription(Language.FRENCH.getCode(), root.getLongDescription(Language.FRENCH.getCode())
				.replaceAll(" \\S*$", " " + sNewYear));
		root.setUserDescription(Language.FRENCH.getCode(), root.getUserDescription(Language.FRENCH.getCode())
				.replaceAll(" \\S*$", " " + sNewYear));

		context.persist();
		context.realizeChangeContext(true);
	}
}
