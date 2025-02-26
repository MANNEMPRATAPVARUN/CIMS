package ca.cihi.cims.service.synchronization;

import ca.cihi.cims.model.changerequest.OptimisticLock;

public interface SynchronizationStatusReporter {

	void finish(OptimisticLock lock);

	void setTotal(int size);

	void start(OptimisticLock lock);

	void startNext(String code);

	void throwError(Throwable ex);

}
