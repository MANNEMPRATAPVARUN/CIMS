package ca.cihi.cims.service.prodpub;

import java.io.IOException;
import java.util.List;

import ca.cihi.cims.model.prodpub.CCIReferenceAttribute;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationStatistics;

public class CCIExtentFileGenerator extends CCIReferenceFileGenerator {

	private static final int EXTENT_DESC_INDEX = 6;

	@Override
	protected void generateEnglishAuditFile(Long currentOpenContextId, Long lastVersionContextId,
			GenerateReleaseTablesCriteria generateTablesModel, String pubDirectory) throws IOException {
		buildWorksheet("CCI", LANGUAGE_CODE_ENG, null, currentOpenContextId, lastVersionContextId,
				generateTablesModel.getCurrentOpenYear(), EXTENT_DESC_INDEX);

	}

	@Override
	protected void generateEnglishFile(Long currentOpenContextId, GenerateReleaseTablesCriteria generateTablesModel,
			List<PublicationStatistics> statisticsSummary) throws IOException {
		generateFile(currentOpenContextId, generateTablesModel, pubDirectory, "Extent", LANGUAGE_CODE_ENG, "E",
				statisticsSummary, 13);
	}

	@Override
	protected void generateFrenchAuditFile(Long currentOpenContextId, Long lastVersionContextId,
			GenerateReleaseTablesCriteria generateTablesModel, String pubDirectory) throws IOException {
		buildWorksheet("CCI", LANGUAGE_CODE_FRA, null, currentOpenContextId, lastVersionContextId,
				generateTablesModel.getCurrentOpenYear(), EXTENT_DESC_INDEX);

	}

	@Override
	protected void generateFrenchFile(Long currentOpenContextId, GenerateReleaseTablesCriteria generateTablesModel,
			List<PublicationStatistics> statisticsSummary) throws IOException {
		generateFile(currentOpenContextId, generateTablesModel, pubDirectory, "Extent", LANGUAGE_CODE_FRA, "E",
				statisticsSummary, 14);
	}

	@Override
	protected List<CCIReferenceAttribute> getCCIReferenceAttributes(String languageCode, Long contextId) {
		return getCCIReferenceAttributes(languageCode, "E", contextId);
	}

	@Override
	protected String[] getDisabledHeaderDescs(String languageCode, Long lastVersionYear) {
		String[] headerDescs = new String[3];
		headerDescs[0] = LANGUAGE_CODE_ENG.equals(languageCode) ? "Reference" : "Référence";
		headerDescs[1] = LANGUAGE_CODE_ENG.equals(languageCode) ? "Extent" : "Étendue";
		headerDescs[2] = (LANGUAGE_CODE_ENG.equals(languageCode) ? "English Disabled Description " : "Description ")
				+ lastVersionYear;
		return headerDescs;
	}

	@Override
	protected String getDisabledTitleValue(String languageCode, String currentVersion) {
		StringBuilder titleValue = new StringBuilder();
		titleValue.append(currentVersion).append(
				LANGUAGE_CODE_ENG.equals(languageCode) ? " Disabled CCI Extent English Description"
						: " Étendue Description désactivées");
		return titleValue.toString();
	}

	@Override
	protected String[] getNewHeaderDescs(String languageCode, Long currentVersionYear) {
		String[] headerDescs = new String[3];
		headerDescs[0] = LANGUAGE_CODE_ENG.equals(languageCode) ? "Reference" : "Référence";
		headerDescs[1] = LANGUAGE_CODE_ENG.equals(languageCode) ? "Extent" : "Étendue";
		headerDescs[2] = (LANGUAGE_CODE_ENG.equals(languageCode) ? "English New Description " : "Description ")
				+ currentVersionYear;
		return headerDescs;
	}

	@Override
	protected String getNewTitleValue(String languageCode, String currentVersion) {
		StringBuilder titleValue = new StringBuilder();
		titleValue.append(currentVersion).append(
				LANGUAGE_CODE_ENG.equals(languageCode) ? " New CCI Extent English Description"
						: " Étendue Description nouveaux");
		return titleValue.toString();
	}

	@Override
	protected String[] getRevisedHeaderDescs(String languageCode, Long currentVersionYear, Long lastVersionYear) {
		String[] headerDescs = new String[4];
		headerDescs[0] = LANGUAGE_CODE_ENG.equals(languageCode) ? "Reference" : "Référence";
		headerDescs[1] = LANGUAGE_CODE_ENG.equals(languageCode) ? "Extent" : "Étendue";
		headerDescs[2] = (LANGUAGE_CODE_ENG.equals(languageCode) ? "English New Description " : "Description ")
				+ currentVersionYear;
		headerDescs[3] = (LANGUAGE_CODE_ENG.equals(languageCode) ? "English Old Description " : "Description ")
				+ lastVersionYear;

		return headerDescs;
	}

	@Override
	protected String getRevisionsTitleValue(String languageCode, String currentVersion) {
		StringBuilder titleValue = new StringBuilder();
		titleValue.append(currentVersion).append(
				LANGUAGE_CODE_ENG.equals(languageCode) ? " CCI Extent English Description Revisions"
						: " Étendue Description révisées");
		return titleValue.toString();
	}

	@Override
	protected String getWorksheetName(String languageCode) {
		return LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI_Extent_Eng" : "CCI_Étendue_Fra";
	}

}
