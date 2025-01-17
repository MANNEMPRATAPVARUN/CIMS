package ca.cihi.cims.refset.config.validation;

import java.io.Serializable;

public class ValueValidationMetadata implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 2116400607148712458L;
	private String columnType;
	private String regexRule;
	private String messageKey;

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	public String getRegexRule() {
		return regexRule;
	}

	public void setRegexRule(String regexRule) {
		this.regexRule = regexRule;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
}
