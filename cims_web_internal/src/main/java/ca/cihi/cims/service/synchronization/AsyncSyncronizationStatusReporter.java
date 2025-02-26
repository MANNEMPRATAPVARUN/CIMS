package ca.cihi.cims.service.synchronization;

import java.util.Map;

import ca.cihi.cims.model.SynchronizationStatus;
import ca.cihi.cims.model.changerequest.OptimisticLock;

public class AsyncSyncronizationStatusReporter implements SynchronizationStatusReporter {

	private final long instanceId;
	private final long changeRequestId;
	private final Map<Long, SynchronizationStatus> statuses;
	private SynchronizationStatus status;

	// ------------------------------------------------------------------------

	public AsyncSyncronizationStatusReporter(long instanceId, long changeRequestId,
			Map<Long, SynchronizationStatus> statuses) {
		this.instanceId = instanceId;
		this.changeRequestId = changeRequestId;
		this.statuses = statuses;
	}

	@Override
	public void finish(OptimisticLock lock) {
		SynchronizationStatus status = statuses.get(changeRequestId);
		if (status == null) {
			status = new SynchronizationStatus(instanceId, Long.MIN_VALUE);
		}
		status.setLockTimestamp(lock.getTimestamp());
		status.setTotal(-1);
	}

	@Override
	public void setTotal(int total) {
		status.setTotal(total);

	}

	@Override
	public void start(OptimisticLock lock) {
		status = new SynchronizationStatus(instanceId, lock.getTimestamp());
		statuses.put(changeRequestId, status);
	}

	@Override
	public void startNext(String code) {
		status.setCurrentCode(code);
		status.setCurrent(status.getCurrent() + 1);
	}

	@Override
	public void throwError(Throwable ex) {
		status.setError(ex.getMessage());
	}

}
