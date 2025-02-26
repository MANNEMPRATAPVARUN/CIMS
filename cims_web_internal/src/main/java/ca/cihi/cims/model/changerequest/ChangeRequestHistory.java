package ca.cihi.cims.model.changerequest;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ChangeRequestHistory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long changeRequestHistoryId ;
	private Long changeRequestId;
	private String modifiedByUser;
	private Date createdDate ;
	private ActionType action;
	private List<ChangeRequestHistoryItem> historyItems;

	public Long getChangeRequestHistoryId() {
		return changeRequestHistoryId;
	}
	public void setChangeRequestHistoryId(Long changeRequestHistoryId) {
		this.changeRequestHistoryId = changeRequestHistoryId;
	}
	public Long getChangeRequestId() {
		return changeRequestId;
	}
	public void setChangeRequestId(Long changeRequestId) {
		this.changeRequestId = changeRequestId;
	}
	public String getModifiedByUser() {
		return modifiedByUser;
	}
	public void setModifiedByUser(String modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public ActionType getAction() {
		return action;
	}
	public void setAction(ActionType action) {
		this.action = action;
	}
	public List<ChangeRequestHistoryItem> getHistoryItems() {
		return historyItems;
	}
	public void setHistoryItems(List<ChangeRequestHistoryItem> historyItems) {
		this.historyItems = historyItems;
	}



}
