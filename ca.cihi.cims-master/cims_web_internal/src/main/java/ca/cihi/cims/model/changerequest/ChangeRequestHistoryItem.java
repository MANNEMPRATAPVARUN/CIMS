package ca.cihi.cims.model.changerequest;

import java.io.Serializable;

public class ChangeRequestHistoryItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long changeRequestHistoryItemId ; //CHANGE_REQUEST_HISTORY_ITEM_ID

	private Long  changeRequestHistoryId ; //CHANGE_REQUEST_HISTORY_ID

	private LabelType labelCode ;   // Lable_Code

	private String item;   // CLOB

	private String  labelDescOverride;    //LABEL_DESC_OVERRIDE

	public Long getChangeRequestHistoryItemId() {
		return changeRequestHistoryItemId;
	}

	public void setChangeRequestHistoryItemId(Long changeRequestHistoryItemId) {
		this.changeRequestHistoryItemId = changeRequestHistoryItemId;
	}

	public Long getChangeRequestHistoryId() {
		return changeRequestHistoryId;
	}

	public void setChangeRequestHistoryId(Long changeRequestHistoryId) {
		this.changeRequestHistoryId = changeRequestHistoryId;
	}

	public LabelType getLabelCode() {
		return labelCode;
	}

	public void setLabelCode(LabelType labelCode) {
		this.labelCode = labelCode;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getLabelDescOverride() {
		return labelDescOverride;
	}

	public void setLabelDescOverride(String labelDescOverride) {
		this.labelDescOverride = labelDescOverride;
	}



}
