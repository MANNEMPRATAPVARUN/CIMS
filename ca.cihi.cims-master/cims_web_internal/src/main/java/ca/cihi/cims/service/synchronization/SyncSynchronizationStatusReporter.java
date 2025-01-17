package ca.cihi.cims.service.synchronization;

import ca.cihi.cims.CIMSException;
import ca.cihi.cims.model.changerequest.OptimisticLock;

public class SyncSynchronizationStatusReporter implements SynchronizationStatusReporter {

	@Override
	public void finish(OptimisticLock lock) {
	}

	public void setTotal(int total) {
	}

	@Override
	public void start(OptimisticLock lock) {
	}

	@Override
	public void startNext(String code) {
	}

	@Override
	public void throwError(Throwable ex) {
		throw new CIMSException(ex.getMessage());
	}

}
