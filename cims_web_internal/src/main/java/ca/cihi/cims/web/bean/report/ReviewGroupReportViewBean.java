package ca.cihi.cims.web.bean.report;

import java.io.Serializable;

public class ReviewGroupReportViewBean extends ReportViewBean {

	/**
	 *
	 */

	private String language;
	private String languageDesc;
	private String reviewGroup;
	private String reviewGroupName;
	private String patternTopic;

	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguageDesc() {
		return languageDesc;
	}
	public void setLanguageDesc(String languageDesc) {
		this.languageDesc = languageDesc;
	}

	public String getReviewGroup() {
		return reviewGroup;
	}
	public void setReviewGroup(String reviewGroup) {
		this.reviewGroup = reviewGroup;
	}

	public String getReviewGroupName() {
		return reviewGroupName;
	}
	public void setReviewGroupName(String reviewGroupName) {
		this.reviewGroupName = reviewGroupName;
	}

	public String getPatternTopic() {
		return patternTopic;
	}
	public void setPatternTopic(String patternTopic) {
		this.patternTopic = patternTopic;
	}


}
