package ca.cihi.cims.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class SynchronizationStatus {

	@XmlElement(name = "total")
	private int total;
	@XmlElement(name = "current")
	private int current;
	@XmlElement(name = "currentCode")
	private String currentCode;

	@XmlElement(name = "error")
	private String error;

	@XmlElement(name = "instanceId")
	private long instanceId;
	
	@XmlElement(name = "lockTimestamp")
	private long lockTimestamp;

	// -----------------------------------------------

	public SynchronizationStatus() {
	}

	public SynchronizationStatus(long instanceId, long lockTimestamp) {
		this.instanceId = instanceId;
		this.lockTimestamp = lockTimestamp;
	}

	public synchronized int getCurrent() {
		return current;
	}

	public synchronized String getCurrentCode() {
		return currentCode;
	}

	public synchronized String getError() {
		return error;
	}

	public long getInstanceId() {
		return instanceId;
	}

	public synchronized int getTotal() {
		return total;
	}

	public synchronized void setCurrent(int current) {
		this.current = current;
	}

	public synchronized void setCurrentCode(String currentCode) {
		this.currentCode = currentCode;
	}

	public synchronized void setError(String error) {
		this.error = error;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public synchronized void setTotal(int total) {
		this.total = total;
	}

	public long getLockTimestamp() {
		return lockTimestamp;
	}

	public void setLockTimestamp(long lockTimestamp) {
		this.lockTimestamp = lockTimestamp;
	}

	@Override
	public String toString() {
		return "SynchronizationStatus [current=" + current + ", currentCode=" + currentCode + ", error=" + error
				+ ", instanceId=" + instanceId + ", total=" + total + ", lockTimestamp=" +lockTimestamp + "]";
	}

}
