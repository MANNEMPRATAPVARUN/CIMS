package ca.cihi.cims.model.prodpub;

public enum GenerateFileStatus {
	I("Initialized"), E("Ended"), F("Failed");

	private String description;

	private GenerateFileStatus(final String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
