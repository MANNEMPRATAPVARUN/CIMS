package ca.cihi.cims.model;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

/**
 * @author szhang
 */
public class Concept extends BaseSerializableCloneableObject {

	private static final long serialVersionUID = -7458818168516982639L;
	
	private Integer conceptId;
	
	private Integer chapterId;
	
	public Integer getChapterId() {
		return chapterId;
	}

	public void setChapterId(Integer chapterId) {
		this.chapterId = chapterId;
	}

	public Integer getConceptId() {
		return conceptId;
	}

	public void setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
	}

	//private String parentConceptId;
	private String conceptCode;
	private String conceptShortDesc;
	private String conceptLongDesc;
	//private String conceptType;
	//private boolean isLeaf;

	public String getConceptCode() {
		return conceptCode;
	}

	public void setConceptCode(String conceptCode) {
		this.conceptCode = conceptCode;
	}

	public String getConceptLongDesc() {
		return conceptLongDesc;
	}

	public void setConceptLongDesc(String conceptLongDesc) {
		this.conceptLongDesc = conceptLongDesc;
	}
	

	public String getConceptShortDesc() {
		return conceptShortDesc;
	}

	public void setConceptShortDesc(String conceptShortDesc) {
		this.conceptShortDesc = conceptShortDesc;
	}
}
