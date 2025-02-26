package ca.cihi.cims.service.prodpub;

import java.io.IOException;
import java.util.List;

import ca.cihi.cims.model.prodpub.CCIReferenceAttribute;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationStatistics;

public class CCIStatusFileGenerator extends CCIReferenceFileGenerator {

	private static final int STATUS_DESC_INDEX = 2;

	@Override
	protected void generateEnglishAuditFile(Long currentOpenContextId, Long lastVersionContextId,
			GenerateReleaseTablesCriteria generateTablesModel, String pubDirectory) throws IOException {
		buildWorksheet("CCI", LANGUAGE_CODE_ENG, null, currentOpenContextId, lastVersionContextId,
				generateTablesModel.getCurrentOpenYear(), STATUS_DESC_INDEX);

	}

	@Override
	protected void generateEnglishFile(Long currentOpenContextId, GenerateReleaseTablesCriteria generateTablesModel,
			List<PublicationStatistics> statisticsSummary) throws IOException {
		generateFile(currentOpenContextId, generateTablesModel, pubDirectory, "Status", LANGUAGE_CODE_ENG, "S",
				statisticsSummary, 7);

	}

	@Override
	protected void generateFrenchAuditFile(Long currentOpenContextId, Long lastVersionContextId,
			GenerateReleaseTablesCriteria generateTablesModel, String pubDirectory) throws IOException {
		buildWorksheet("CCI", LANGUAGE_CODE_FRA, null, currentOpenContextId, lastVersionContextId,
				generateTablesModel.getCurrentOpenYear(), STATUS_DESC_INDEX);

	}

	@Override
	protected void generateFrenchFile(Long currentOpenContextId, GenerateReleaseTablesCriteria generateTablesModel,
			List<PublicationStatistics> statisticsSummary) throws IOException {
		generateFile(currentOpenContextId, generateTablesModel, pubDirectory, "Status", LANGUAGE_CODE_FRA, "S",
				statisticsSummary, 8);
	}

	@Override
	protected List<CCIReferenceAttribute> getCCIReferenceAttributes(String languageCode, Long contextId) {

		return getCCIReferenceAttributes(languageCode, "S", contextId);
	}

	@Override
	protected String[] getDisabledHeaderDescs(String languageCode, Long lastVersionYear) {
		String[] headerDescs = new String[3];
		headerDescs[0] = LANGUAGE_CODE_ENG.equals(languageCode) ? "Reference" : "Référence";
		headerDescs[1] = LANGUAGE_CODE_ENG.equals(languageCode) ? "Status" : "Situation";
		headerDescs[2] = (LANGUAGE_CODE_ENG.equals(languageCode) ? "English Disabled Description " : "Description ")
				+ lastVersionYear;
		return headerDescs;
	}

	@Override
	protected String getDisabledTitleValue(String languageCode, String currentVersion) {
		StringBuilder titleValue = new StringBuilder();
		titleValue.append(currentVersion).append(
				LANGUAGE_CODE_ENG.equals(languageCode) ? " Disabled CCI Status English Description"
						: " Situation Description désactivées");
		return titleValue.toString();
	}

	@Override
	protected String[] getNewHeaderDescs(String languageCode, Long currentVersionYear) {
		String[] headerDescs = new String[3];
		headerDescs[0] = LANGUAGE_CODE_ENG.equals(languageCode) ? "Reference" : "Référence";
		headerDescs[1] = LANGUAGE_CODE_ENG.equals(languageCode) ? "Status" : "Situation";
		headerDescs[2] = (LANGUAGE_CODE_ENG.equals(languageCode) ? "English New Description " : "Description ")
				+ currentVersionYear;
		return headerDescs;
	}

	@Override
	protected String getNewTitleValue(String languageCode, String currentVersion) {
		StringBuilder titleValue = new StringBuilder();
		titleValue.append(currentVersion).append(
				LANGUAGE_CODE_ENG.equals(languageCode) ? " New CCI Status English Description"
						: " Situation Description nouveaux");
		return titleValue.toString();
	}

	@Override
	protected String[] getRevisedHeaderDescs(String languageCode, Long currentVersionYear, Long lastVersionYear) {
		String[] headerDescs = new String[4];
		headerDescs[0] = LANGUAGE_CODE_ENG.equals(languageCode) ? "Reference" : "Référence";
		headerDescs[1] = LANGUAGE_CODE_ENG.equals(languageCode) ? "Status" : "Situation";
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
				LANGUAGE_CODE_ENG.equals(languageCode) ? " CCI Status English Description Revisions"
						: " Situation Description révisées");
		return titleValue.toString();
	}

	@Override
	protected String getWorksheetName(String languageCode) {
		return LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI_Status_Eng" : "CCI_Situation_Fra";
	}

}
