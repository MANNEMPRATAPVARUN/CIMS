package ca.cihi.cims.model.sgsc;

import java.io.Serializable;

public class CodeDescription implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6538527691563388022L;
	private String conceptCode;
	private String description;

	public String getConceptCode() {
		return conceptCode;
	}

	public String getDescription() {
		return description;
	}

	public void setConceptCode(String conceptCode) {
		this.conceptCode = conceptCode;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
