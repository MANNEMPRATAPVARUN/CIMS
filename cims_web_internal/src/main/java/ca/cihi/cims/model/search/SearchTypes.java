package ca.cihi.cims.model.search;

/**
 * Enumeration of all available search types
 * 
 * @author rshnaper
 * 
 */
public enum SearchTypes {
	ChangeRequestProperties("cr.properties"), ChangeRequestICDTabular("cr.icd.tabular"), ChangeRequestCCITabular(
			"cr.cci.tabular"), ChangeRequestIndex("cr.index"), ICDTabularComparative("tab.icd.comparative"), CCITabularComparative(
			"tab.cci.comparative"), CCITabularSimple("tab.cci.simple"), ICDTabularSimple("tab.icd.simple"), CCIReferenceValuesComparative(
			"ref.cci.comparative");

	/**
	 * Retrieves the enumeration by name
	 * 
	 * @param name
	 * @return
	 */
	public static SearchTypes forName(String name) {
		if (name != null) {
			for (SearchTypes type : values()) {
				if (type.getTypeName().equalsIgnoreCase(name)) {
					return type;
				}
			}
		}
		return null;
	}

	private String name;

	SearchTypes(String name) {
		this.name = name;
	}

	public String getTypeName() {
		return name;
	}

	@Override
	public String toString() {
		return getTypeName();
	}
}
