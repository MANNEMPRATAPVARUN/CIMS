package ca.cihi.cims.refset.enums;

import static ca.cihi.cims.refset.config.RefsetConstants.LONG_TITLE;

import java.util.HashMap;
import java.util.Map;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.refset.util.CCICodeFormatter;
import ca.cihi.cims.refset.util.CodeFormatter;
import ca.cihi.cims.refset.util.ICD10CACodeFomatter;
import ca.cihi.cims.refset.util.RomanNumberFormatter;

public enum ColumnType {
	// @formatter:off
	CIMS_ICD10CA_CODE("CIMS ICD-10-CA Code", false, Language.NOLANGUAGE, "N", CIMSConstants.ICD_10_CA, false, 1, false, null,"Code"),
	ICD10CA_CODE("ICD-10-CA Code", false, Language.NOLANGUAGE, "Y", CIMSConstants.ICD_10_CA, true, 2, false, new ICD10CACodeFomatter(), "Code"),
	CIMS_ICD10CA_DESCRIPTION_ENG("CIMS ICD-10-CA Description (ENG)", false, Language.ENG, "Y", CIMSConstants.ICD_10_CA, true, 3, false, null, LONG_TITLE),
	CIMS_ICD10CA_DESCRIPTION_FRA("CIMS ICD-10-CA Description (FRA)", false, Language.FRA, "Y", CIMSConstants.ICD_10_CA, true, 4, false, null, LONG_TITLE),
	CIMS_CHAPTER_NUMBER("CIMS Chapter Number", false, Language.NOLANGUAGE, "Y", CIMSConstants.ICD_10_CA, true, 5, false, new RomanNumberFormatter(), "Code", true),
	CHAPTER_DESCRIPTION_ENG("Chapter Description (ENG)", false, Language.ENG, "Y", CIMSConstants.ICD_10_CA, true, 6, false, null, LONG_TITLE, true),
	CHAPTER_DESCRIPTION_FRA("Chapter Description (FRA)", false, Language.FRA, "Y", CIMSConstants.ICD_10_CA, true, 7, false, null, LONG_TITLE, true),
	CIMS_CCI_CODE("CIMS CCI Code", false, Language.NOLANGUAGE, "N", CIMSConstants.CCI, false, 8, false, null,"Code"),
	CCI_CODE("CCI Code", false, Language.NOLANGUAGE, "Y", CIMSConstants.CCI, true, 9, false, new CCICodeFormatter(), "Code"),
	CIMS_CCI_DESCRIPTION_ENG("CIMS CCI Code Description (ENG)", false, Language.ENG, "Y", CIMSConstants.CCI, true, 10, false, null, LONG_TITLE),
	CIMS_CCI_DESCRIPTION_FRA("CIMS CCI Code Description (FRA)", false, Language.FRA, "Y", CIMSConstants.CCI, true, 11, false, null, LONG_TITLE),
	CIMS_SECTION_NUMBER("CIMS Section Number", false, Language.NOLANGUAGE, "Y", CIMSConstants.CCI, true, 12, false, null,"Code", true),
	CIMS_SECTION_DESCRIPTION_ENG("CIMS Section Description (ENG)", false, Language.ENG, "Y", CIMSConstants.CCI, true, 13, false, null, LONG_TITLE, true),
	CIMS_SECTION_DESCRIPTION_FRA("CIMS Section Description (FRA)", false, Language.FRA, "Y", CIMSConstants.CCI, true, 14, false, null, LONG_TITLE, true),
	SUBLIST_COLUMN("Sublist Column", true, Language.NOLANGUAGE, "N", "", true, 15, false),
	SCT_CONCEPT_ID("SCT Concept ID", false, Language.NOLANGUAGE, "N", CIMSConstants.SCT, true, 16, true),
	SCT_FULLY_SPECIFIED_NAME_ID("SCT-Fully Specified Name ID", false, Language.NOLANGUAGE, "Y", CIMSConstants.SCT, true, 17, true),
	SCT_FULLY_SPECIFIED_NAME("SCT-Fully Specified Name", false, Language.NOLANGUAGE, "Y", CIMSConstants.SCT, true, 18, true),
	SCT_SYNONYM_ID("SCT-Synonym ID", false, Language.NOLANGUAGE, "Y", CIMSConstants.SCT, true, 19, true),
	SCT_SYNONYM_NAME("SCT-Synonym", false, Language.NOLANGUAGE, "Y", CIMSConstants.SCT, true, 20, true),
	SCT_PREFFERED_TERM_ID("SCT-Preferred Term ID", false, Language.NOLANGUAGE, "Y", CIMSConstants.SCT, true, 21, true),
	SCT_PREFFERED_TERM("SCT-Preferred Term", false, Language.NOLANGUAGE, "Y", CIMSConstants.SCT, true, 22, true),
	CONCEPT_TYPE("Concept Type", false, Language.NOLANGUAGE, "Y", CIMSConstants.SCT, true, 23, true),
	SCT_DESCRIPTION_ID("SCT-Description ID", false, Language.NOLANGUAGE, "Y", CIMSConstants.SCT, true, 24, true),
	SCT_DESCRIPTION("SCT-Description", false, Language.NOLANGUAGE, "Y", CIMSConstants.SCT, true, 25, true),
	SCT_DESCRIPTION_TYPE("SCT-Description Type", false, Language.NOLANGUAGE, "Y", CIMSConstants.SCT, true, 26, true),
	DESC_COMMON_TERM_ENG("Desc-Common Term (ENG)", false, Language.ENG, "Y", CIMSConstants.NONE, true, 27, true),
	DESC_COMMON_TERM_FRA("Desc-Common Term (FRA)", false, Language.FRA, "Y", CIMSConstants.NONE, true, 28, true),
	DESC_PREFERRED_TERM_ENG("Desc-Preferred Term (ENG)", false, Language.ENG, "Y", CIMSConstants.NONE, true, 29, true),
	DESC_PREFERRED_TERM_FRA("Desc-Preferred Term (FRA)", false, Language.FRA, "Y", CIMSConstants.NONE, true, 30, true),
	DESC_SYNONYM_ENG("Desc-Synonym (ENG)", false, Language.ENG, "Y", CIMSConstants.NONE, true, 31, true),
	DESC_SYNONYM_FRA("Desc-Synonym (FRA)", false, Language.FRA, "Y", CIMSConstants.NONE, true, 32, true),
	CUSTOM_GROUP_1_ENG("Custom Group 1 (ENG)", false, Language.ENG, "N", CIMSConstants.NONE, true, 33, true),
	CUSTOM_GROUP_2_ENG("Custom Group 2 (ENG)", false, Language.ENG, "N", CIMSConstants.NONE, true, 34, true),
	CUSTOM_GROUP_3_ENG("Custom Group 3 (ENG)", false, Language.ENG, "N", CIMSConstants.NONE, true, 35, true),
	CUSTOM_GROUP_1_FRA("Custom Group 1 (FRA)", false, Language.FRA, "N", CIMSConstants.NONE, true, 36, true),
	CUSTOM_GROUP_2_FRA("Custom Group 2 (FRA)", false, Language.FRA, "N", CIMSConstants.NONE, true, 37, true),
	CUSTOM_GROUP_3_FRA("Custom Group 3 (FRA)", false, Language.FRA, "N", CIMSConstants.NONE, true, 38, true),
	CUSTOM_GROUP_1("Custom Group 1", false, Language.NOLANGUAGE, "N", CIMSConstants.NONE, true, 39, true),
	CUSTOM_GROUP_2("Custom Group 2", false, Language.NOLANGUAGE, "N", CIMSConstants.NONE, true, 40, true),
	CUSTOM_GROUP_3("Custom Group 3", false, Language.NOLANGUAGE, "N", CIMSConstants.NONE, true, 41, true),
	NOTE_ENG("NOTE (ENG)", false, Language.ENG, "N", CIMSConstants.NONE, true, 42, true),
	NOTE_FRA("NOTE (FRA)", false, Language.FRA, "N", CIMSConstants.NONE, true, 43, true);

	// @formatter:on
	private String columnTypeDisplay;

	/**
	 * Flag to indicate if allow the same column type appears in the picklist multiple times.
	 */
	private boolean allowMultiple;

	private Language language;

	private String autoPopulate;
	private String classification;
	private boolean allowDelete;
	private Integer order;
	private boolean sublistAvailable;
	private CodeFormatter codeFomatter;
	private String textPropertyClasssName;
	private boolean chapterOrSectionProperty;

	private static Map<String, ColumnType> COLUMN_TYPE_MAP = new HashMap<String, ColumnType>();

	static {
		for (ColumnType columnType : ColumnType.values()) {
			COLUMN_TYPE_MAP.put(columnType.getColumnTypeDisplay(), columnType);
		}
	}

	public boolean isSublistAvailable() {
		return sublistAvailable;
	}

	public Integer getOrder() {
		return order;
	}

	public boolean isAllowDelete() {
		return allowDelete;
	}

	private ColumnType(final String columnTypeDisplay, boolean allowMultiple, Language language, String autoPopulate,
			String classification, boolean allowDelete, Integer order, boolean sublistAvailable) {
		this.columnTypeDisplay = columnTypeDisplay;
		this.allowMultiple = allowMultiple;
		this.language = language;
		this.autoPopulate = autoPopulate;
		this.classification = classification;
		this.allowDelete = allowDelete;
		this.order = order;
		this.sublistAvailable = sublistAvailable;
	}

    private ColumnType(final String columnTypeDisplay, boolean allowMultiple, Language language, String autoPopulate,
            String classification, boolean allowDelete, Integer order, boolean sublistAvailable,
            CodeFormatter codeFormatter, String textPropertyClasssName) {
        this(columnTypeDisplay, allowMultiple, language, autoPopulate, classification, allowDelete, order,
                sublistAvailable);
        this.codeFomatter = codeFormatter;
        this.textPropertyClasssName = textPropertyClasssName;
    }

    private ColumnType(final String columnTypeDisplay, boolean allowMultiple, Language language, String autoPopulate,
            String classification, boolean allowDelete, Integer order, boolean sublistAvailable,
            CodeFormatter codeFormatter, String textPropertyClasssName, boolean chapterOrSectionProperty) {
        this(columnTypeDisplay, allowMultiple, language, autoPopulate, classification, allowDelete, order,
                sublistAvailable, codeFormatter, textPropertyClasssName);
        this.chapterOrSectionProperty = chapterOrSectionProperty;
    }

	public String getColumnTypeDisplay() {
		return columnTypeDisplay;
	}

	public Language getLanguage() {
		return language;
	}

	public String getAutoPopulate() {
		return autoPopulate;
	}

	public String getClassification() {
		return classification;
	}

	public boolean isAllowMultiple() {
		return allowMultiple;
	}

	public static ColumnType getColumnTypeByType(String type) {
		if (type == null) {
			return null;
		}

		return COLUMN_TYPE_MAP.get(type.trim());
	}

	public CodeFormatter getCodeFomatter() {
		return codeFomatter;
	}

	public String getTextPropertyClasssName() {
		return textPropertyClasssName;
	}

	public boolean isChapterOrSectionProperty() {
		return chapterOrSectionProperty;
	}
}
