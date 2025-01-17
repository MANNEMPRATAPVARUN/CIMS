package ca.cihi.cims.web.bean;

import org.apache.commons.lang.StringEscapeUtils;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

public class CodeSearchResultBean extends BaseSerializableCloneableObject {

	private static final long serialVersionUID = -2682018736702905355L;

	private final String label;
	private final String value;
	private final String conceptId;
	private String type;
	@Deprecated
	private boolean leaf;

	final static private String daggerHtmlNumber = "&#134;";
	final static private String daggerEntityCode = "&dagger;";

	// ----------------------------------------------------------------------------

	public CodeSearchResultBean(String label, String value, String conceptId) {
		this.label = label;
		this.value = value;
		this.conceptId = conceptId;
	}

	public String getConceptId() {
		return conceptId;
	}

	/**
	 * The label for each search result is displayed the fly-out search results list.
	 */
	public String getLabel() {
		return StringEscapeUtils.unescapeHtml(("" + label).replaceAll(daggerHtmlNumber, daggerEntityCode));
	}

	public String getType() {
		return type;
	}

	/**
	 * When a search result is selected, the 'value' replaces whatever was originally typed into the search text field.
	 * Ideally, this should be set to some canonical value for the search result.
	 */
	public String getValue() {
		return value;
	}

	@Deprecated
	public boolean isLeaf() {
		return leaf;
	}

	@Deprecated
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public void setType(String type) {
		this.type = type;
	}

}
