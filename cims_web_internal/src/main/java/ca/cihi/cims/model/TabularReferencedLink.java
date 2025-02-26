package ca.cihi.cims.model;

public class TabularReferencedLink {

	private String code;
	private String level;
	private String location;
	private String language;

	// -----------------------------------

	public String getCode() {
		return code;
	}

	public String getLanguage() {
		return language;
	}

	public String getLevel() {
		return level;
	}

	public String getLocation() {
		return location;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "TabularReferencedLink [code=" + code + ", language=" + language + ", level=" + level + ", location="
				+ location + "]";
	}

}
