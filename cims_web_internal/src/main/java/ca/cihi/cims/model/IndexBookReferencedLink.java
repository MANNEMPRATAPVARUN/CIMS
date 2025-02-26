package ca.cihi.cims.model;

public class IndexBookReferencedLink {

	private String indexTerm;
	private String referenceFlag;
	private String language;

	// -----------------------------------

	public String getIndexTerm() {
		return indexTerm;
	}

	public String getLanguage() {
		return language;
	}

	public String getReferenceFlag() {
		return referenceFlag;
	}

	public void setIndexTerm(String indexTerm) {
		this.indexTerm = indexTerm;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setReferenceFlag(String referenceFlag) {
		this.referenceFlag = referenceFlag;
	}

	@Override
	public String toString() {
		return "TabularReferencedLink [indexTerm=" + indexTerm + ", language=" + language + ", referenceFlag="
				+ referenceFlag + "]";
	}

}
