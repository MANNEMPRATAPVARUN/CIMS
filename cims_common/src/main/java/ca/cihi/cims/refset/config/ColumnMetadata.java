package ca.cihi.cims.refset.config;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import ca.cihi.cims.refset.enums.ColumnCategory;
import ca.cihi.cims.refset.enums.ColumnType;

public class ColumnMetadata {

	private static Map<ColumnCategory, List<String>> columnConfigurations;

	static {
		columnConfigurations = new EnumMap<>(ColumnCategory.class);
		List<String> icdColumnTypes = new ArrayList<>();
		// icdColumnTypes.add(ColumnType.CIMS_ICD10CA_CODE);
		icdColumnTypes.add(ColumnType.ICD10CA_CODE.getColumnTypeDisplay());
		icdColumnTypes.add(ColumnType.CIMS_ICD10CA_DESCRIPTION_ENG.getColumnTypeDisplay());
		icdColumnTypes.add(ColumnType.CIMS_ICD10CA_DESCRIPTION_FRA.getColumnTypeDisplay());
		icdColumnTypes.add(ColumnType.CIMS_CHAPTER_NUMBER.getColumnTypeDisplay());
		icdColumnTypes.add(ColumnType.CHAPTER_DESCRIPTION_ENG.getColumnTypeDisplay());
		icdColumnTypes.add(ColumnType.CHAPTER_DESCRIPTION_FRA.getColumnTypeDisplay());

		columnConfigurations.put(ColumnCategory.ICD10CAAUTOPOPULATE, icdColumnTypes);

		List<String> icdRefreshColumnTypes = new ArrayList<>();
		icdRefreshColumnTypes.add(ColumnType.CIMS_ICD10CA_DESCRIPTION_ENG.getColumnTypeDisplay());
		icdRefreshColumnTypes.add(ColumnType.CIMS_ICD10CA_DESCRIPTION_FRA.getColumnTypeDisplay());
		icdRefreshColumnTypes.add(ColumnType.CHAPTER_DESCRIPTION_ENG.getColumnTypeDisplay());
		icdRefreshColumnTypes.add(ColumnType.CHAPTER_DESCRIPTION_FRA.getColumnTypeDisplay());

		columnConfigurations.put(ColumnCategory.ICD10CAREFRESHABLE, icdRefreshColumnTypes);

		List<String> cciColumnTypes = new ArrayList<>();
		cciColumnTypes.add(ColumnType.CCI_CODE.getColumnTypeDisplay());
		cciColumnTypes.add(ColumnType.CIMS_CCI_DESCRIPTION_ENG.getColumnTypeDisplay());
		cciColumnTypes.add(ColumnType.CIMS_CCI_DESCRIPTION_FRA.getColumnTypeDisplay());
		cciColumnTypes.add(ColumnType.CIMS_SECTION_NUMBER.getColumnTypeDisplay());
		cciColumnTypes.add(ColumnType.CIMS_SECTION_DESCRIPTION_ENG.getColumnTypeDisplay());
		cciColumnTypes.add(ColumnType.CIMS_SECTION_DESCRIPTION_FRA.getColumnTypeDisplay());

		columnConfigurations.put(ColumnCategory.CCIAUTOPOPULATE, cciColumnTypes);

		List<String> cciRefreshColumnTypes = new ArrayList<>();
		cciRefreshColumnTypes.add(ColumnType.CIMS_CCI_DESCRIPTION_ENG.getColumnTypeDisplay());
		cciRefreshColumnTypes.add(ColumnType.CIMS_CCI_DESCRIPTION_FRA.getColumnTypeDisplay());
		cciRefreshColumnTypes.add(ColumnType.CIMS_SECTION_DESCRIPTION_ENG.getColumnTypeDisplay());
		cciRefreshColumnTypes.add(ColumnType.CIMS_SECTION_DESCRIPTION_FRA.getColumnTypeDisplay());

		columnConfigurations.put(ColumnCategory.CCIREFRESHABLE, cciRefreshColumnTypes);

		List<String> snomedColumnTypes = new ArrayList<>();
		snomedColumnTypes.add(ColumnType.SCT_DESCRIPTION.getColumnTypeDisplay());
		snomedColumnTypes.add(ColumnType.SCT_DESCRIPTION_ID.getColumnTypeDisplay());
		snomedColumnTypes.add(ColumnType.SCT_DESCRIPTION_TYPE.getColumnTypeDisplay());
		snomedColumnTypes.add(ColumnType.SCT_FULLY_SPECIFIED_NAME_ID.getColumnTypeDisplay());
		snomedColumnTypes.add(ColumnType.SCT_FULLY_SPECIFIED_NAME.getColumnTypeDisplay());
		snomedColumnTypes.add(ColumnType.SCT_PREFFERED_TERM_ID.getColumnTypeDisplay());
		snomedColumnTypes.add(ColumnType.SCT_PREFFERED_TERM.getColumnTypeDisplay());
		snomedColumnTypes.add(ColumnType.SCT_SYNONYM_ID.getColumnTypeDisplay());
		snomedColumnTypes.add(ColumnType.SCT_SYNONYM_NAME.getColumnTypeDisplay());
		snomedColumnTypes.add(ColumnType.CONCEPT_TYPE.getColumnTypeDisplay());

		columnConfigurations.put(ColumnCategory.SNOMED, snomedColumnTypes);

		List<String> customColumnTypes = new ArrayList<>();
		customColumnTypes.add(ColumnType.CUSTOM_GROUP_1.getColumnTypeDisplay());
		customColumnTypes.add(ColumnType.CUSTOM_GROUP_1_ENG.getColumnTypeDisplay());
		customColumnTypes.add(ColumnType.CUSTOM_GROUP_1_FRA.getColumnTypeDisplay());
		customColumnTypes.add(ColumnType.CUSTOM_GROUP_2.getColumnTypeDisplay());
		customColumnTypes.add(ColumnType.CUSTOM_GROUP_2_ENG.getColumnTypeDisplay());
		customColumnTypes.add(ColumnType.CUSTOM_GROUP_2_FRA.getColumnTypeDisplay());
		customColumnTypes.add(ColumnType.CUSTOM_GROUP_3.getColumnTypeDisplay());
		customColumnTypes.add(ColumnType.CUSTOM_GROUP_3_ENG.getColumnTypeDisplay());
		customColumnTypes.add(ColumnType.CUSTOM_GROUP_3_FRA.getColumnTypeDisplay());

		customColumnTypes.add(ColumnType.NOTE_ENG.getColumnTypeDisplay());
		customColumnTypes.add(ColumnType.NOTE_FRA.getColumnTypeDisplay());

		columnConfigurations.put(ColumnCategory.CUSTOM, customColumnTypes);

		List<String> sublistColumnTypes = new ArrayList<>();
		sublistColumnTypes.add(ColumnType.SUBLIST_COLUMN.getColumnTypeDisplay());

		columnConfigurations.put(ColumnCategory.SUBLIST, sublistColumnTypes);

		List<String> alphaNumericColumns = new ArrayList<>();
		alphaNumericColumns.add(ColumnType.CUSTOM_GROUP_1.getColumnTypeDisplay());
		alphaNumericColumns.add(ColumnType.CUSTOM_GROUP_2.getColumnTypeDisplay());
		alphaNumericColumns.add(ColumnType.CUSTOM_GROUP_3.getColumnTypeDisplay());

		columnConfigurations.put(ColumnCategory.CUSTOM_ALPHANUMERIC, alphaNumericColumns);

	}

	public static List<String> getColumnTypeByCategory(ColumnCategory category) {
		return columnConfigurations.get(category);
	}
}
