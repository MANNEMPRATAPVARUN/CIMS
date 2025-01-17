package ca.cihi.cims.model;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

public class SearchResultModel extends BaseSerializableCloneableObject {

	private static final long serialVersionUID = 3253622881538293036L;

	private String conceptId;
	private String conceptIdPath;
	private String conceptType;
	private String conceptCodeDesc;
	private String conceptCode;
	private String longDescription;

	public String getConceptId() {
		return conceptId;
	}

	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}

	public String getConceptIdPath() {
		return conceptIdPath;
	}

	public void setConceptIdPath(String conceptIdPath) {
		this.conceptIdPath = conceptIdPath;
	}

	public String getConceptType() {
		return conceptType;
	}

	public void setConceptType(String conceptType) {
		this.conceptType = conceptType;
	}

	public String getConceptCodeDesc() {
		return conceptCodeDesc;
	}

	public void setConceptCodeDesc(String conceptCodeDesc) {
		this.conceptCodeDesc = conceptCodeDesc;
	}

	public String getConceptCode() {
		return conceptCode;
	}

	public void setConceptCode(String conceptCode) {
		this.conceptCode = conceptCode;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

}
