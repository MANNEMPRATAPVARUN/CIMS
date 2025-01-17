package ca.cihi.cims.model.reports;

import java.io.Serializable;

public class QASummaryMetricsModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1970237051405134829L;
	private int valid;
	private int accepted;
	private int translationDone;
	private int validationDone;
	private int realized;

	private int qaDone;

	private long changeRequestId;

	public int getAccepted() {
		return accepted;
	}

	public long getChangeRequestId() {
		return changeRequestId;
	}

	public int getQaDone() {
		return qaDone;
	}

	public int getRealized() {
		return realized;
	}

	public int getTranslationDone() {
		return translationDone;
	}

	public int getValid() {
		return valid;
	}

	public int getValidationDone() {
		return validationDone;
	}

	public void setAccepted(int accepted) {
		this.accepted = accepted;
	}

	public void setChangeRequestId(long changeRequestId) {
		this.changeRequestId = changeRequestId;
	}

	public void setQaDone(int qaDone) {
		this.qaDone = qaDone;
	}

	public void setRealized(int realized) {
		this.realized = realized;
	}

	public void setTranslationDone(int translationDone) {
		this.translationDone = translationDone;
	}

	public void setValid(int valid) {
		this.valid = valid;
	}

	public void setValidationDone(int validationDone) {
		this.validationDone = validationDone;
	}
}
