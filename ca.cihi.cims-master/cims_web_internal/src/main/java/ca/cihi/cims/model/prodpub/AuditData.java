package ca.cihi.cims.model.prodpub;

public class AuditData {
	private String newDescription;
	private String oldDescription;

	public String getNewDescription() {
		return newDescription;
	}

	public String getNewDescriptionAuditReport() {
		return newDescription != null ? newDescription.substring(1) : "";
	}

	public String getOldDescription() {
		return oldDescription;
	}

	public String getOldDescriptionAuditReport() {
		return oldDescription != null ? oldDescription.substring(1) : "";
	}

	public void setNewDescription(String newDescription) {
		this.newDescription = newDescription;
	}

	public void setOldDescription(String oldDescription) {
		this.oldDescription = oldDescription;
	}
}
