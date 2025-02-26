package ca.cihi.cims.sct.web.domain;

import java.io.Serializable;

public class ConceptType  implements Serializable   {

	private static final long serialVersionUID = 1L;
	
	private String conceptTypeCode;
	private String conceptTypePrefDesc;
	private String conceptTypeFsnDesc;
	
	public String getConceptTypeCode() {
		return conceptTypeCode;
	}
	public void setConceptTypeCode(String conceptTypeCode) {
		this.conceptTypeCode = conceptTypeCode;
	}
	public String getConceptTypePrefDesc() {
		return conceptTypePrefDesc;
	}
	public void setConceptTypePrefDesc(String conceptTypePrefDesc) {
		this.conceptTypePrefDesc = conceptTypePrefDesc;
	}
	public String getConceptTypeFsnDesc() {
		return conceptTypeFsnDesc;
	}
	public void setConceptTypeFsnDesc(String conceptTypeFsnDesc) {
		this.conceptTypeFsnDesc = conceptTypeFsnDesc;
	}
	
}
