package ca.cihi.cims.service.folioclamlexport;

public enum HtmlOutputServiceStatus {
	NEW("N", "New"), GENERATING("G", "Generating"), CONVERTING("C", "Converting"), PERSISTENCE("P",
			"Persistence"), DONE("D", "Done"), FAILURE("F", "Failed");

	private String status;
	private String description;

	private HtmlOutputServiceStatus(String status, String desc) {
		this.status = status;
		this.description = desc;
	}

	public String getStatus() {
		return this.status;
	}

	public static HtmlOutputServiceStatus forStatusCode(String code) {
		HtmlOutputServiceStatus value = null;
		switch (code) {
		case "N":
			value = NEW;
			break;
		case "G":
			value = GENERATING;
			break;
		case "C":
			value = CONVERTING;
			break;
		case "P":
			value = PERSISTENCE;
			break;
		case "D":
			value = DONE;
			break;
		case "F":
			value = FAILURE;
			break;

		default:
			value = null;
		}

		return value;
	}

	public String getDescription() {
		return this.description;
	}

	@Override
	public String toString() {
		return this.getStatus();
	}
}
