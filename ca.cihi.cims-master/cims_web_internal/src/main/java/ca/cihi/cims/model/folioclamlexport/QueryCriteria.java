package ca.cihi.cims.model.folioclamlexport;

import java.io.Serializable;

public class QueryCriteria implements Serializable {
	private static final long serialVersionUID = -1927514874886459441L;
	/**
	 * ICD-10-CA or CCI
	 */
	private String classification;
	/**
	 * Concept code used for generate anchor
	 */
	private String conceptCode;
	/**
	 * ConceptId of the navigation tree node
	 */
	private String conceptId;
	/**
	 * The chapterId
	 */
	private String containerConceptId;

	/**
	 * ContextId for which the folio is generated on
	 */
	private Long contextId;

	/**
	 * Language code for the content to be generated
	 */
	private String language;

	private String year;

	public String getClassification() {
		return classification;
	}

	public String getConceptCode() {
		return conceptCode;
	}

	public String getConceptId() {
		return conceptId;
	}

	public String getContainerConceptId() {
		return containerConceptId;
	}

	public Long getContextId() {
		return contextId;
	}

	public String getLanguage() {
		return language;
	}

	public String getYear() {
		return year;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public void setConceptCode(String conceptCode) {
		this.conceptCode = conceptCode;
	}

	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}

	public void setContainerConceptId(String containerConceptId) {
		this.containerConceptId = containerConceptId;
	}

	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setYear(String year) {
		this.year = year;
	}

	@Override
	public String toString() {
		return "classification=" + this.classification + ", conceptId=" + this.conceptId + ", containerConceptId="
				+ this.containerConceptId + ", contextId=" + this.contextId + ", language=" + this.language + ", year="
				+ this.year;
	}

}
