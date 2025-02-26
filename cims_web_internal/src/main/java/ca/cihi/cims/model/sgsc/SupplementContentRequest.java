package ca.cihi.cims.model.sgsc;

import java.io.Serializable;

import ca.cihi.cims.Language;

public class SupplementContentRequest implements Serializable {

	public enum Qaulifier {
		APPTECH(1), DEVAGENT(2), TISSUE(3);

		private final int type;

		private Qaulifier(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}
	}

	public enum SRC {
		CCIAGENTATC("CCIAgentATC"), CCIDISABLEDCODE("CCIDisabledCode"), CCIDISABLEDREFVALUE(
				"CCIDisabledRefValue"), CCIGENATTRB("CCIGenAttrb"), CCIGROUP("CCIGroup"), CCIINTERVENTION(
						"CCIIntervention"), CCINEWCODE("CCINewCode"), CCINEWREFVALUE("CCINewRefValue"), CCIQUALIFIER(
								"CCIQualifier"), CCIREFVALUE("CCIRefValue"), CCIRUBRICFINDER(
										"CCIRubricFinder"), CCIRUBRICFINDER8("CCIRubricFinder_8"), ICDDISABLEDCODE(
												"ICDDisabledCode"), ICDNEWCODE("ICDNewCode");

		private final String src;

		private SRC(String src) {
			this.src = src;
		}

		public String getSrc() {
			return src;
		}
	}

	public enum Type {
		EXTENT("extent", "E", "Extent Attibute Code", "Codes des attributs d' Étendue", "Extent Attribute Description",
				"Descriptions des attributs d' Étendue", "Extent (Generic Descriptions)",
				"Étendue"), LOCATION("location", "L", "Location Attibute Code", "Codes des attributs de Lieu",
						"Location Attribute Description", "Descriptions des attributs de Lieu",
						"Location (Generic Descriptions)", "Lieu"), STATUS("status", "S", "Status Attibute Code",
								"Codes des attributs de Situation", "Status Attribute Description",
								"Descriptions des attributs de Situation", "Status (Generic Descriptions)",
								"Situation");

		public static Type getByType(String type) {
			for (Type typeE : Type.values()) {
				if (typeE.getType().equals(type)) {
					return typeE;
				}
			}
			return null;
		}

		public static String getCodeByType(String type) {
			for (Type typeE : Type.values()) {
				if (typeE.getType().equals(type)) {
					return typeE.getCode();
				}
			}
			return null;
		}

		private final String attributeCodeEng;

		private final String attributeCodeFra;

		private final String attributeDescriptionEng;
		private final String attributeDescriptionFra;
		private final String code;
		private final String genericTitleEng;
		private final String genericTitleFra;
		private final String type;

		private Type(String type, String code, String attributeCodeEng, String attributeCodeFra,
				String attributeDescriptionEng, String attributeDescriptionFra, String genericTitleEng,
				String genericTitleFra) {
			this.type = type;
			this.code = code;
			this.attributeCodeEng = attributeCodeEng;
			this.attributeCodeFra = attributeCodeFra;
			this.attributeDescriptionEng = attributeDescriptionEng;
			this.attributeDescriptionFra = attributeDescriptionFra;
			this.genericTitleEng = genericTitleEng;
			this.genericTitleFra = genericTitleFra;

		}

		public String getAttributeCodeEng() {
			return attributeCodeEng;
		}

		public String getAttributeCodeFra() {
			return attributeCodeFra;
		}

		public String getAttributeDescriptionEng() {
			return attributeDescriptionEng;
		}

		public String getAttributeDescriptionFra() {
			return attributeDescriptionFra;
		}

		public String getCode() {
			return code;
		}

		public String getGenericTitleEng() {
			return genericTitleEng;
		}

		public String getGenericTitleFra() {
			return genericTitleFra;
		}

		public String getType() {
			return type;
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = -4768915152164009203L;

	private long currentContextId;
	private Boolean folio;
	private String language;
	private long priorContextId;
	private Integer qualifier;
	private String reportSrc;
	private Long section;
	private String type;

	public SupplementContentRequest(String reportSrc, String language, Long section, String type, Integer qualifier,
			long currentContextId, long priorContextId) {
		this.reportSrc = reportSrc;
		this.language = language;
		this.section = section;
		this.type = type;
		this.qualifier = qualifier;
		this.currentContextId = currentContextId;
		this.priorContextId = priorContextId;
		this.folio = Boolean.FALSE;
	}

	public SupplementContentRequest(String reportSrc, String language, Long section, String type, Integer qualifier,
			long currentContextId, long priorContextId, Boolean folio) {
		this.reportSrc = reportSrc;
		this.language = language;
		this.section = section;
		this.type = type;
		this.qualifier = qualifier;
		this.currentContextId = currentContextId;
		this.priorContextId = priorContextId;
		this.folio = folio;
	}

	public long getCurrentContextId() {
		return currentContextId;
	}

	public Boolean getFolio() {
		return folio;
	}

	public String getLanguage() {
		return language;
	}

	public String getLanguageCode() {
		return Language.fromString(getLanguage()).getCode();
	}

	public long getPriorContextId() {
		return priorContextId;
	}

	public Integer getQualifier() {
		return qualifier;
	}

	public String getReportSrc() {
		return reportSrc;
	}

	public Long getSection() {
		return section;
	}

	public String getType() {
		return type;
	}

	public void setCurrentContextId(long currentContextId) {
		this.currentContextId = currentContextId;
	}

	public void setFolio(Boolean folio) {
		this.folio = folio;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setPriorContextId(long priorContextId) {
		this.priorContextId = priorContextId;
	}

	public void setQualifier(Integer qualifier) {
		this.qualifier = qualifier;
	}

	public void setReportSrc(String reportSrc) {
		this.reportSrc = reportSrc;
	}

	public void setSection(Long section) {
		this.section = section;
	}

	public void setType(String type) {
		this.type = type;
	}

}
