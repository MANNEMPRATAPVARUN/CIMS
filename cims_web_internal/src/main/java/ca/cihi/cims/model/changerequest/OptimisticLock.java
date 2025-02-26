package ca.cihi.cims.model.changerequest;

import java.util.Date;

public class OptimisticLock {

	private long timestamp;

	// --------------------------------------

	public OptimisticLock() {
	}

	public OptimisticLock(Date timestamp) {
		this(timestamp.getTime());
	}

	public OptimisticLock(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp.getTime();
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
