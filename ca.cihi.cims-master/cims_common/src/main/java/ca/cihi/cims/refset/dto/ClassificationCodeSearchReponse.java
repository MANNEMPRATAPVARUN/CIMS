package ca.cihi.cims.refset.dto;

import org.apache.commons.lang.StringEscapeUtils;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

/**
 *
 * Refset Picklist Classification Code Search Response.
 *
 */
public class ClassificationCodeSearchReponse extends BaseSerializableCloneableObject {
	/**
	 * Default Serial Version UID.
	 */
	private static final long serialVersionUID = 19012367L;

	/**
	 * Dagger Html Number.
	 */
	private final static String daggerHtmlNumber = "&#134;";

	/**
	 * Dagger Entity Code.
	 */
	private final static String daggerEntityCode = "&dagger;";

	/**
	 * Concept Id.
	 */
	private Long conceptId;

	/**
	 * Concept Code.
	 */
	private String conceptCode;

	/**
	 * English Description.
	 */
	private String descriptionEnglish;

	/**
	 * French Description.
	 */
	private String descriptionFrench;

	/**
	 * Content Id (chapterElementId or SectionElementId)
	 */
	private Long contentId;

	/**
	 * Content Number.
	 */
	private String contentNumber;

	/**
	 * English Content Description.
	 */
	private String contentDescEnglish;

	/**
	 * French Content Description.
	 */
	private String contentDescFrench;

	public Long getConceptId() {
		return conceptId;
	}

	public String getConceptCode() {
		return conceptCode;
	}

	public String getDescriptionEnglish() {
		return descriptionEnglish;
	}

	public String getDescriptionFrench() {
		return descriptionFrench;
	}

	public void setConceptId(Long conceptId) {
		this.conceptId = conceptId;
	}

	public void setConceptCode(String conceptCode) {
		this.conceptCode = conceptCode;
	}

	public void setDescriptionEnglish(String descriptionEnglish) {
		this.descriptionEnglish = descriptionEnglish;
	}

	public void setDescriptionFrench(String descriptionFrench) {
		this.descriptionFrench = descriptionFrench;
	}

	/**
	 * The label for each search result is displayed the fly-out search results list.
	 */
	public String getLabel() {
		return StringEscapeUtils
				.unescapeHtml((conceptCode + ": " + descriptionEnglish).replaceAll(daggerHtmlNumber, daggerEntityCode));
	}

	/**
	 * When a search result is selected, the 'value' replaces whatever was originally typed into the search text field.
	 * Ideally, this should be set to some canonical value for the search result.
	 */
	public String getValue() {
		return conceptCode;
	}

	public String getContentNumber() {
		return contentNumber;
	}

	public String getContentDescEnglish() {
		return contentDescEnglish;
	}

	public String getContentDescFrench() {
		return contentDescFrench;
	}

	public void setContentNumber(String contentNumber) {
		this.contentNumber = contentNumber;
	}

	public void setContentDescEnglish(String contentDescEnglish) {
		this.contentDescEnglish = contentDescEnglish;
	}

	public void setContentDescFrench(String contentDescFrench) {
		this.contentDescFrench = contentDescFrench;
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + ((conceptId == null) ? 0 : conceptId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ClassificationCodeSearchReponse other = (ClassificationCodeSearchReponse) obj;
		if (conceptId == null) {
			if (other.conceptId != null) {
				return false;
			}
		} else if (!conceptId.equals(other.conceptId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ClassificationCodeSearchReponse [conceptId=" + conceptId + ", conceptCode=" + conceptCode
				+ ", descriptionEnglish=" + descriptionEnglish + ", descriptionFrench=" + descriptionFrench
				+ ", contentNumber=" + contentNumber + ", contentDescEnglish=" + contentDescEnglish
				+ ", contentDescFrench=" + contentDescFrench + "]";
	}
}
