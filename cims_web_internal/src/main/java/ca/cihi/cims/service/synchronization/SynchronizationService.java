package ca.cihi.cims.service.synchronization;

import java.util.List;

import ca.cihi.cims.model.ContentToSynchronize;
import ca.cihi.cims.model.SynchronizationStatus;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.OptimisticLock;

public interface SynchronizationService {

	List<ContentToSynchronize> getContentToSynchronize();

	long getInstanceId();

	SynchronizationStatus getSynchronizationStatus(long changeRequestId);

	void synchronize(OptimisticLock lock, User user, long changeRequestId);

	void synchronizeAsync(OptimisticLock lock, User user, long changeRequestId);

}