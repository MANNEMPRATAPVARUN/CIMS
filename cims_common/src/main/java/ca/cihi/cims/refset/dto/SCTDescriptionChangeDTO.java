package ca.cihi.cims.refset.dto;

/**
 * @author miftimie
 * @version 1.0
 * @created 27-Sep-2016 8:45:20 AM
 */
public class SCTDescriptionChangeDTO {

	private String newDescription;
	private Long newId;
	private String oldDescription;
	private Long oldId;

	public SCTDescriptionChangeDTO() {

	}

	public String getNewDescription() {
		return newDescription;
	}

	public void setNewDescription(String newDescription) {
		this.newDescription = newDescription;
	}

	public Long getNewId() {
		return newId;
	}

	public void setNewId(Long newId) {
		this.newId = newId;
	}

	public String getOldDescription() {
		return oldDescription;
	}

	public void setOldDescription(String oldDescription) {
		this.oldDescription = oldDescription;
	}

	public Long getOldId() {
		return oldId;
	}

	public void setOldId(Long oldId) {
		this.oldId = oldId;
	}

}