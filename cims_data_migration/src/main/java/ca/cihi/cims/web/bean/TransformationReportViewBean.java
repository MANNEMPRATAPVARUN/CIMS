package ca.cihi.cims.web.bean;

/**
 * 
 * 
 * @author wxing
 * 
 */
public class TransformationReportViewBean extends BasicInfoBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9042921888376652847L;
	private String startTime;
	private String endTime;
	private Long conceptCount;

	public Long getConceptCount() {
		return conceptCount;
	}

	public String getEndTime() {
		return endTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setConceptCount(Long conceptCount) {
		this.conceptCount = conceptCount;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

}
