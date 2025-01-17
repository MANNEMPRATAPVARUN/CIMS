package ca.cihi.cims.model.changerequest.legacy;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

public class LegacyRequestSearchModel implements Serializable {
	private static final long serialVersionUID = 201410081327L;

	@NotNull(message = "Version must not be blank")
	private List<String> versionCodes;  // version year

	@NotBlank(message = "Classification must not be blank")
	private String classificationTitleCode;

	@NotBlank(message = "Language must not be empty")
	private String languageCode;

    // Disposition
	private String requestStatusCode;

    // Section
	private String sectionCode;

    // Nature of Change
	private String changeNatureCode;

    // Type of Change
	private String changeTypeCode;

	public List<String> getVersionCodes() {
		return versionCodes;
	}

	public void setVersionCodes(List<String> versionCodes) {
		this.versionCodes = versionCodes;
	}

	public String getClassificationTitleCode() {
		return classificationTitleCode;
	}

	public void setClassificationTitleCode(String classificationTitleCode) {
		this.classificationTitleCode = classificationTitleCode;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getRequestStatusCode() {
		return requestStatusCode;
	}

	public void setRequestStatusCode(String requestStatusCode) {
		this.requestStatusCode = requestStatusCode;
	}

	public String getSectionCode() {
		return sectionCode;
	}

	public void setSectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
	}

	public String getChangeNatureCode() {
		return changeNatureCode;
	}

	public void setChangeNatureCode(String changeNatureCode) {
		this.changeNatureCode = changeNatureCode;
	}
	
	public String getChangeTypeCode() {
		return changeTypeCode;
	}

	public void setChangeTypeCode(String changeTypeCode) {
		this.changeTypeCode = changeTypeCode;
	}


}
