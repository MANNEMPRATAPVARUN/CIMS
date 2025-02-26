package ca.cihi.cims.service.synchronization;

import java.util.List;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.model.ContentToSynchronize;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.OptimisticLock;

public interface InternalSynchronizationService {

	void synchronize(OptimisticLock lock, User user, long changeRequestId, ContextAccess access,
			SynchronizationStatusReporter reporter);

	void synchronizeContent(OptimisticLock lock, User user, long changeRequestId, ContextAccess access,
			SynchronizationStatusReporter reporter, List<ContentToSynchronize> contents, ContentToSynchronize content);

}