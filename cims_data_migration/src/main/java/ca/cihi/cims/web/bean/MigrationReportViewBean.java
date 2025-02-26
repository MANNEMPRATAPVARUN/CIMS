package ca.cihi.cims.web.bean;

import java.util.List;


public class MigrationReportViewBean extends BasicInfoBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3105367715814388456L;
	private String startTime;
	private String endTime;
	private List<LogMessage> logMessageList;

	public String getEndTime() {
		return endTime;
	}

	public List<LogMessage> getLogMessageList() {
		return logMessageList;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public void setLogMessageList(List<LogMessage> logMessageList) {
		this.logMessageList = logMessageList;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
}