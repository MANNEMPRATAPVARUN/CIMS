package ca.cihi.cims.framework.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * MINIMAL REGULAR COMPLETE
 *
 * @author miftimie
 * @version 1.0
 * @created 13-Jun-2016 11:21:22 AM
 */
public enum ConceptLoadDegree {
	 NONE("NONE"), COMPLETE("COMPLETE"), MINIMAL("MINIMAL"), REGULAR("REGULAR");

	private static final Map<String, ConceptLoadDegree> STRING_TO_ENUM = new HashMap<String, ConceptLoadDegree>();

	static {
		for (ConceptLoadDegree cld : values()) {
			STRING_TO_ENUM.put(cld.getDegree(), cld);
		}
	}

	private String degree;

	private ConceptLoadDegree(final String degree) {
		this.degree = degree;
	}

	public String getDegree() {
		return degree;
	}
}