package ca.cihi.cims.model;

import org.springframework.stereotype.Component;

/**
 * A class to wrap up Transformation error information.
 * 
 * @author wxing
 */
@Component
public class TransformationError {

	private Long errorId;
	private String classification;
	private String version;
	private String conceptCode;
	private String conceptTypeCode;
	private String errorMessage;
	private String xmlString;
	private String createDate;
	private Long runId;

	public TransformationError() {
		this.classification = "";
		this.version = "";
		this.conceptCode = "";
		this.conceptTypeCode = "";
		this.errorMessage = "";
		this.xmlString = "";
	};

	public TransformationError(final String classification, final String version, final String conceptCode,
			final String conceptTypeCode, final String errorMessage, final String xmlString) {
		this.classification = classification;
		this.version = version.length() > 4 ? version.substring(0, 4) : version; // trim the data according to the db
		// schema
		this.conceptCode = conceptCode.length() > 20 ? conceptCode.substring(0, 20) : conceptCode; // trim the data
		// according to the
		// db schema
		this.conceptTypeCode = conceptTypeCode;
		this.errorMessage = errorMessage != null && errorMessage.length() > 2000 ? errorMessage.substring(0, 2000)
				: errorMessage; // trim the
		// data
		// according
		// to the db
		// schema
		this.xmlString = xmlString.length() > 4000 ? xmlString.substring(0, 4000) : xmlString; // trim the data
		// according to the db
		// schema
	}

	public String getClassification() {
		return classification;
	}

	public String getConceptCode() {
		return conceptCode;
	}

	public String getConceptTypeCode() {
		return conceptTypeCode;
	}

	public String getCreateDate() {
		return createDate;
	}

	public Long getErrorId() {
		return errorId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public Long getRunId() {
		return runId;
	}

	public String getVersion() {
		return version;
	}

	public String getXmlString() {
		return xmlString;
	}

	public void setClassification(final String classification) {
		this.classification = classification;
	}

	public void setConceptCode(final String conceptCode) {
		// trim the data according to the db schema
		this.conceptCode = conceptCode.length() > 20 ? conceptCode.substring(0, 20) : conceptCode;
	}

	public void setConceptTypeCode(final String conceptTypeCode) {
		this.conceptTypeCode = conceptTypeCode;
	}

	public void setCreateDate(final String createDate) {
		this.createDate = createDate;
	}

	public void setErrorId(final Long errorId) {
		this.errorId = errorId;
	}

	public void setErrorMessage(final String errorMessage) {
		// trim the data according to the db schema
		this.errorMessage = errorMessage.length() > 2000 ? errorMessage.substring(0, 2000) : errorMessage;
	}

	public void setRunId(final Long runId) {
		this.runId = runId;
	}

	public void setVersion(final String version) {
		// trim the data according to the db schema
		this.version = version.length() > 4 ? version.substring(0, 4) : version;
	}

	public void setXmlString(final String xmlString) {
		// trim the data according to the db schema
		this.xmlString = xmlString.length() > 4000 ? xmlString.substring(0, 4000) : xmlString;
	}
}