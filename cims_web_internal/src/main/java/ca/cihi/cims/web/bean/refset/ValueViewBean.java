package ca.cihi.cims.web.bean.refset;

import java.io.Serializable;

public class ValueViewBean implements Serializable {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((columnElementId == null) ? 0 : columnElementId.hashCode());
		result = (prime * result) + ((columnElementVersionId == null) ? 0 : columnElementVersionId.hashCode());
		result = (prime * result) + ((idValue == null) ? 0 : idValue.hashCode());
		result = (prime * result) + ((languageCode == null) ? 0 : languageCode.hashCode());
		result = (prime * result) + ((textValue == null) ? 0 : textValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ValueViewBean other = (ValueViewBean) obj;
		if (columnElementId == null) {
			if (other.columnElementId != null) {
				return false;
			}
		} else if (!columnElementId.equals(other.columnElementId)) {
			return false;
		}
		if (columnElementVersionId == null) {
			if (other.columnElementVersionId != null) {
				return false;
			}
		} else if (!columnElementVersionId.equals(other.columnElementVersionId)) {
			return false;
		}
		if (idValue == null) {
			if (other.idValue != null) {
				return false;
			}
		} else if (!idValue.equals(other.idValue)) {
			return false;
		}
		if (languageCode == null) {
			if (other.languageCode != null) {
				return false;
			}
		} else if (!languageCode.equals(other.languageCode)) {
			return false;
		}
		if (textValue == null) {
			if (other.textValue != null) {
				return false;
			}
		} else if (!textValue.equals(other.textValue)) {
			return false;
		}
		return true;
	}

	/**
	 *
	 */
	private static final long serialVersionUID = -4867151024774541647L;
	private Long columnElementId;
	private Long columnElementVersionId;
	private Long idValue;
	private String textValue;
	private String languageCode;

	public Long getColumnElementId() {
		return columnElementId;
	}

	public void setColumnElementId(Long columnElementId) {
		this.columnElementId = columnElementId;
	}

	public Long getColumnElementVersionId() {
		return columnElementVersionId;
	}

	public void setColumnElementVersionId(Long columnElementVersionId) {
		this.columnElementVersionId = columnElementVersionId;
	}

	public Long getIdValue() {
		return idValue;
	}

	public void setIdValue(Long conceptId) {
		this.idValue = conceptId;
	}

	public String getTextValue() {
		return textValue;
	}

	public void setTextValue(String value) {
		this.textValue = value;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	@Override
	public String toString() {
		return "ValueViewBean [columnElementId=" + columnElementId + ", columnElementVersionId="
				+ columnElementVersionId + ", idValue=" + idValue + ", textValue=" + textValue + ", languageCode="
				+ languageCode + "]";
	}
}
