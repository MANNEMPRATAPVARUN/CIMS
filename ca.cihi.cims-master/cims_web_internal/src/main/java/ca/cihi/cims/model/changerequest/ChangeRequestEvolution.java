package ca.cihi.cims.model.changerequest;

import java.io.Serializable;

import javax.validation.constraints.Size;

public class ChangeRequestEvolution implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long changeRequestId;
	@Size(max = 4000, message = "Evolution Codes can not be over 4000 characters")
	private String evolutionCodes;  //EVOLUTION_CODES
	@Size(max = 4000, message = "Evolution English Content can not be over 4000 characters")
	private String evolutionTextEng;  //EVOLUTION_TEXT_ENG
	@Size(max = 4000, message = "Evolution French Content can not be over 4000 characters")
	private String evolutionTextFra ;  //EVOLUTION_TEXT_FRA



	public Long getChangeRequestId() {
		return changeRequestId;
	}
	public void setChangeRequestId(Long changeRequestId) {
		this.changeRequestId = changeRequestId;
	}
	public String getEvolutionCodes() {
		return evolutionCodes;
	}
	public void setEvolutionCodes(String evolutionCodes) {
		this.evolutionCodes = evolutionCodes;
	}
	public String getEvolutionTextEng() {
		return evolutionTextEng;
	}
	public void setEvolutionTextEng(String evolutionTextEng) {
		this.evolutionTextEng = evolutionTextEng;
	}
	public String getEvolutionTextFra() {
		return evolutionTextFra;
	}
	public void setEvolutionTextFra(String evolutionTextFra) {
		this.evolutionTextFra = evolutionTextFra;
	}
}
