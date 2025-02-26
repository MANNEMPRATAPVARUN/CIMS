package ca.cihi.cims.model.refset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ca.cihi.cims.refset.enums.ColumnType;

public class ColumnConceptLookupMapper implements Serializable {
    /**
     * Default Serial Version UID.
     */
    private static final long serialVersionUID = 9037891L;

    /**
     * Column Type Search Property Mapper.
     */
    public static List<ColumnTypeSearchPropertyMapper> COLUMN_TYPE_SEARCH_PROPERTY_MAPPER = new ArrayList<ColumnTypeSearchPropertyMapper>();

    /**
     * Snomed Search Property Mapper.
     */
    public static List<ColumnTypeSearchPropertyMapper> SNOMED_SEARCH_PROPERTY_MAPPER = new ArrayList<ColumnTypeSearchPropertyMapper>();

    static {
        COLUMN_TYPE_SEARCH_PROPERTY_MAPPER
                .add(new ColumnTypeSearchPropertyMapper(ColumnType.CIMS_ICD10CA_CODE.getColumnTypeDisplay(),
                        "conceptCode", ColumnType.CIMS_ICD10CA_CODE.getLanguage().getCode(),
                        ColumnType.CIMS_ICD10CA_CODE.getCodeFomatter() != null, "conceptId"));
        COLUMN_TYPE_SEARCH_PROPERTY_MAPPER
                .add(new ColumnTypeSearchPropertyMapper(ColumnType.ICD10CA_CODE.getColumnTypeDisplay(), "conceptCode",
                        ColumnType.ICD10CA_CODE.getLanguage().getCode(),
                        ColumnType.ICD10CA_CODE.getCodeFomatter() != null, "conceptId"));
        COLUMN_TYPE_SEARCH_PROPERTY_MAPPER
                .add(new ColumnTypeSearchPropertyMapper(ColumnType.CIMS_ICD10CA_DESCRIPTION_ENG.getColumnTypeDisplay(),
                        "descriptionEnglish", ColumnType.CIMS_ICD10CA_DESCRIPTION_ENG.getLanguage().getCode(),
                        ColumnType.CIMS_ICD10CA_DESCRIPTION_ENG.getCodeFomatter() != null, "conceptId"));
        COLUMN_TYPE_SEARCH_PROPERTY_MAPPER
                .add(new ColumnTypeSearchPropertyMapper(ColumnType.CIMS_ICD10CA_DESCRIPTION_FRA.getColumnTypeDisplay(),
                        "descriptionFrench", ColumnType.CIMS_ICD10CA_DESCRIPTION_FRA.getLanguage().getCode(),
                        ColumnType.CIMS_ICD10CA_DESCRIPTION_FRA.getCodeFomatter() != null, "conceptId"));
        COLUMN_TYPE_SEARCH_PROPERTY_MAPPER
                .add(new ColumnTypeSearchPropertyMapper(ColumnType.CIMS_CHAPTER_NUMBER.getColumnTypeDisplay(),
                        "contentNumber", ColumnType.CIMS_CHAPTER_NUMBER.getLanguage().getCode(),
                        ColumnType.CIMS_CHAPTER_NUMBER.getCodeFomatter() != null, "contentId"));
        COLUMN_TYPE_SEARCH_PROPERTY_MAPPER
                .add(new ColumnTypeSearchPropertyMapper(ColumnType.CHAPTER_DESCRIPTION_ENG.getColumnTypeDisplay(),
                        "contentDescEnglish", ColumnType.CHAPTER_DESCRIPTION_ENG.getLanguage().getCode(),
                        ColumnType.CHAPTER_DESCRIPTION_ENG.getCodeFomatter() != null, "contentId"));
        COLUMN_TYPE_SEARCH_PROPERTY_MAPPER
                .add(new ColumnTypeSearchPropertyMapper(ColumnType.CHAPTER_DESCRIPTION_FRA.getColumnTypeDisplay(),
                        "contentDescFrench", ColumnType.CHAPTER_DESCRIPTION_FRA.getLanguage().getCode(),
                        ColumnType.CHAPTER_DESCRIPTION_FRA.getCodeFomatter() != null, "contentId"));

        COLUMN_TYPE_SEARCH_PROPERTY_MAPPER
                .add(new ColumnTypeSearchPropertyMapper(ColumnType.CIMS_CCI_CODE.getColumnTypeDisplay(), "conceptCode",
                        ColumnType.CIMS_CCI_CODE.getLanguage().getCode(),
                        ColumnType.CIMS_CCI_CODE.getCodeFomatter() != null, "conceptId"));
        COLUMN_TYPE_SEARCH_PROPERTY_MAPPER.add(new ColumnTypeSearchPropertyMapper(
                ColumnType.CCI_CODE.getColumnTypeDisplay(), "conceptCode", ColumnType.CCI_CODE.getLanguage().getCode(),
                ColumnType.CCI_CODE.getCodeFomatter() != null, "conceptId"));
        COLUMN_TYPE_SEARCH_PROPERTY_MAPPER
                .add(new ColumnTypeSearchPropertyMapper(ColumnType.CIMS_CCI_DESCRIPTION_ENG.getColumnTypeDisplay(),
                        "descriptionEnglish", ColumnType.CIMS_CCI_DESCRIPTION_ENG.getLanguage().getCode(),
                        ColumnType.CIMS_CCI_DESCRIPTION_ENG.getCodeFomatter() != null, "conceptId"));
        COLUMN_TYPE_SEARCH_PROPERTY_MAPPER
                .add(new ColumnTypeSearchPropertyMapper(ColumnType.CIMS_CCI_DESCRIPTION_FRA.getColumnTypeDisplay(),
                        "descriptionFrench", ColumnType.CIMS_CCI_DESCRIPTION_FRA.getLanguage().getCode(),
                        ColumnType.CIMS_CCI_DESCRIPTION_FRA.getCodeFomatter() != null, "conceptId"));
        COLUMN_TYPE_SEARCH_PROPERTY_MAPPER
                .add(new ColumnTypeSearchPropertyMapper(ColumnType.CIMS_SECTION_NUMBER.getColumnTypeDisplay(),
                        "contentNumber", ColumnType.CIMS_SECTION_NUMBER.getLanguage().getCode(),
                        ColumnType.CIMS_SECTION_NUMBER.getCodeFomatter() != null, "contentId"));
        COLUMN_TYPE_SEARCH_PROPERTY_MAPPER
                .add(new ColumnTypeSearchPropertyMapper(ColumnType.CIMS_SECTION_DESCRIPTION_ENG.getColumnTypeDisplay(),
                        "contentDescEnglish", ColumnType.CIMS_SECTION_DESCRIPTION_ENG.getLanguage().getCode(),
                        ColumnType.CIMS_SECTION_DESCRIPTION_ENG.getCodeFomatter() != null, "contentId"));
        COLUMN_TYPE_SEARCH_PROPERTY_MAPPER
                .add(new ColumnTypeSearchPropertyMapper(ColumnType.CIMS_SECTION_DESCRIPTION_FRA.getColumnTypeDisplay(),
                        "contentDescFrench", ColumnType.CIMS_SECTION_DESCRIPTION_FRA.getLanguage().getCode(),
                        ColumnType.CIMS_SECTION_DESCRIPTION_FRA.getCodeFomatter() != null, "contentId"));

        SNOMED_SEARCH_PROPERTY_MAPPER
                .add(new ColumnTypeSearchPropertyMapper(ColumnType.SCT_CONCEPT_ID.getColumnTypeDisplay(), "conceptId",
                        ColumnType.SCT_CONCEPT_ID.getLanguage().getCode(), false, "conceptId"));
        SNOMED_SEARCH_PROPERTY_MAPPER.add(new ColumnTypeSearchPropertyMapper(
                ColumnType.SCT_FULLY_SPECIFIED_NAME_ID.getColumnTypeDisplay(), "conceptFsnId",
                ColumnType.SCT_FULLY_SPECIFIED_NAME_ID.getLanguage().getCode(), false, "conceptFsnId"));
        SNOMED_SEARCH_PROPERTY_MAPPER.add(new ColumnTypeSearchPropertyMapper(
                ColumnType.SCT_FULLY_SPECIFIED_NAME.getColumnTypeDisplay(), "conceptFsn",
                ColumnType.SCT_FULLY_SPECIFIED_NAME.getLanguage().getCode(), false, "conceptFsnId"));
        SNOMED_SEARCH_PROPERTY_MAPPER
                .add(new ColumnTypeSearchPropertyMapper(ColumnType.SCT_SYNONYM_ID.getColumnTypeDisplay(), "synonymId",
                        ColumnType.SCT_SYNONYM_ID.getLanguage().getCode(), false, "synonymId"));
        SNOMED_SEARCH_PROPERTY_MAPPER
                .add(new ColumnTypeSearchPropertyMapper(ColumnType.SCT_SYNONYM_NAME.getColumnTypeDisplay(),
                        "synonym", ColumnType.SCT_SYNONYM_NAME.getLanguage().getCode(), false, "synonymId"));
        SNOMED_SEARCH_PROPERTY_MAPPER.add(new ColumnTypeSearchPropertyMapper(
                ColumnType.SCT_PREFFERED_TERM_ID.getColumnTypeDisplay(), "conceptPreferredId",
                ColumnType.SCT_PREFFERED_TERM_ID.getLanguage().getCode(), false, "conceptPreferredId"));
        SNOMED_SEARCH_PROPERTY_MAPPER.add(new ColumnTypeSearchPropertyMapper(
                ColumnType.SCT_PREFFERED_TERM.getColumnTypeDisplay(), "conceptPreferred",
                ColumnType.SCT_PREFFERED_TERM.getLanguage().getCode(), false, "conceptPreferredId"));
        SNOMED_SEARCH_PROPERTY_MAPPER
                .add(new ColumnTypeSearchPropertyMapper(ColumnType.CONCEPT_TYPE.getColumnTypeDisplay(), "conceptType",
                        ColumnType.CONCEPT_TYPE.getLanguage().getCode(), false, "conceptId"));
        SNOMED_SEARCH_PROPERTY_MAPPER.add(new ColumnTypeSearchPropertyMapper(
                ColumnType.SCT_DESCRIPTION_ID.getColumnTypeDisplay(), "selectedTermId",
                ColumnType.SCT_DESCRIPTION_ID.getLanguage().getCode(), false, "selectedTermId"));
        SNOMED_SEARCH_PROPERTY_MAPPER
                .add(new ColumnTypeSearchPropertyMapper(ColumnType.SCT_DESCRIPTION.getColumnTypeDisplay(),
                        "selectedTerm", ColumnType.SCT_DESCRIPTION.getLanguage().getCode(), false, "selectedTermId"));
        SNOMED_SEARCH_PROPERTY_MAPPER.add(new ColumnTypeSearchPropertyMapper(
                ColumnType.SCT_DESCRIPTION_TYPE.getColumnTypeDisplay(), "selectedTermType",
                ColumnType.SCT_DESCRIPTION_TYPE.getLanguage().getCode(), false, "selectedTermId"));
    }
}
