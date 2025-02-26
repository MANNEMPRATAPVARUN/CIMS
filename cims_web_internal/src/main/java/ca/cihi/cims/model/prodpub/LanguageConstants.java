package ca.cihi.cims.model.prodpub;

/**
 * 
 * @author tyang
 * 
 */
public enum LanguageConstants {

	ENG("ENG", "English", "Sector", "Reference", "Extent", "Status", "Location", "New Rule", "Old Rule"), FRA("FRA",
			"French", "Secteur", "Référence", "Étendue", "Situation", "Lieu", "Règle", "Règle");
	public static LanguageConstants getConstant(String languageCode) {
		if ("ENG".equals(languageCode)) {
			return ENG;
		} else {
			return FRA;
		}
	}

	private String languageCode;

	private String sector;

	private String extent;

	private String status;

	private String location;

	private String reference;

	private String languageDescription;

	private String newRule;

	private String oldRule;

	private LanguageConstants(String languageCode, String languageDescription, String sector, String reference,
			String extent, String status, String location, String newRule, String oldRule) {
		this.languageCode = languageCode;
		this.sector = sector;
		this.reference = reference;
		this.extent = extent;
		this.status = status;
		this.location = location;
		this.languageDescription = languageDescription;
		this.newRule = newRule;
		this.oldRule = oldRule;
	}

	public String getExtent() {
		return extent;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public String getLanguageDescription() {
		return languageDescription;
	}

	public String getLocation() {
		return location;
	}

	public String getNewRule() {
		return newRule;
	}

	public String getOldRule() {
		return oldRule;
	}

	public String getReference() {
		return reference;
	}

	public String getSector() {
		return sector;
	}

	public String getStatus() {
		return status;
	}
}
