package ca.cihi.cims.service.prodpub;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.content.icd.IcdValidationXml;
import ca.cihi.cims.model.prodpub.CodeValidationAudit;
import ca.cihi.cims.model.prodpub.FileFormat;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.LanguageConstants;
import ca.cihi.cims.model.prodpub.PublicationStatistics;
import ca.cihi.cims.model.prodpub.ValidationRuleSet;
import ca.cihi.cims.util.XmlUtils;

public class ICDValidationFileGenerator extends ValidationFileGenerator {

	private static final int MAX_LENGTH_ICD_VALIDATIONRULE = 12;

	private static final String DX_TYPE_3 = "3";
	private static final String DX_TYPE_4 = "4";
	private static final String DX_TYPE_6 = "6";
	private static final String DX_TYPE_9 = "9";

	@Override
	protected void buildAuditLists(Long lastVersionICDContextId) {

		List<ValidationRuleSet> icdPriorCodeValidations = findICDValidationRules(lastVersionICDContextId);

		buildCacheList(priorValidationSetMap, icdPriorCodeValidations, lastVersionICDContextId);

	}

	@Override
	protected int buildLineFixedWidth(Object source, BufferedWriter bw) throws IOException {
		StringBuilder sb_line = new StringBuilder();
		int count = 1;
		CimsFileUtils.padBlanksToString(((ValidationRuleSet) source).getCode(), MAX_LENGTH_ICD_CODE, sb_line);

		CimsFileUtils.padBlanksToString(((ValidationRuleSet) source).getValidationRuleText(),
				MAX_LENGTH_ICD_VALIDATIONRULE, sb_line);
		sb_line.append(CimsFileUtils.LINE_SEPARATOR);
		bw.write(sb_line.toString());
		return count;
	}

	@Override
	protected List<ValidationRuleSet> findChildCodes(Long contextId, Long conceptId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("contextId", contextId);
		params.put("conceptId", conceptId);
		params.put("narrowerClassId",
				getConceptService().getICDClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "Narrower"));

		params.put("codeClassId", getConceptService().getICDClassID(WebConstants.TEXT_PROPERTY_VERSION, "Code"));
		params.put("catRubricClassId", getConceptService().getICDClassID(WebConstants.CONCEPT_VERSION, "Category"));
		return getPublicationMapper().findICDChildCodes(params);
	}

	protected List<ValidationRuleSet> findICDValidationRules(Long contextId) {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("contextId", contextId);
		params.put("validationCPVClassId",
				getConceptService().getICDClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ValidationICDCPV"));
		params.put("validationClassId", getConceptService()
				.getICDClassID(WebConstants.CONCEPT_VERSION, "ValidationICD"));
		params.put("catRubricClassId", getConceptService().getICDClassID(WebConstants.CONCEPT_VERSION, "Category"));
		params.put("validationFacilityClassId",
				getConceptService().getICDClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ValidationFacility"));
		params.put("facilityTypeClassId",
				getConceptService().getICDClassID(WebConstants.CONCEPT_VERSION, "FacilityType"));
		params.put("domainValueCodeClassId", getConceptService()
				.getICDClassID("TextPropertyVersion", "DomainValueCode"));
		params.put("codeClassId", getConceptService().getICDClassID("TextPropertyVersion", "Code"));

		return getPublicationMapper().findICDValidationRules(params);
	}

	@Override
	protected String generateAuditFileName(String languageCode, Long currentVersionYear) {
		StringBuilder fileName = new StringBuilder();
		fileName.append(pubDirectory)
				.append(SUB_FOLDER_ICD)
				.append(FILE_SEPARATOR)
				.append(SUB_FOLDER_VALIDATION)
				.append(FILE_SEPARATOR)
				.append(languageCode)
				.append(FILE_SEPARATOR)
				.append(LANGUAGE_CODE_ENG.equals(languageCode) ? CimsFileUtils
						.findICDValidationAuditFileNameEnglish(currentVersionYear.toString()) : CimsFileUtils
						.findICDValidationAuditFileNameFrench(currentVersionYear.toString()));
		return fileName.toString();
	}

	@Override
	protected void generateEnglishAuditFile(Long currentOpenContextId, Long lastVersionICDContextId,
			GenerateReleaseTablesCriteria generateTablesModel) throws IOException {

		buildWorksheet("10CA", LANGUAGE_CODE_ENG, null, currentOpenContextId, lastVersionICDContextId,
				generateTablesModel.getCurrentOpenYear(), 0);
	}

	@Override
	protected void generateEnglishFile(Long currentOpenContextId, GenerateReleaseTablesCriteria generateTablesModel,
			List<PublicationStatistics> statisticsSummary) throws IOException {
		File folderEng = prepareFolder(SUB_FOLDER_ICD, SUB_FOLDER_VALIDATION, SUB_FOLDER_ENG);
		String fileName = CimsFileUtils.buildAsciiFileName("ICD", "Code_Validation", null,
				String.valueOf(generateTablesModel.getCurrentOpenYear()), null,
				getDelimitedType(generateTablesModel.getFileFormat()), null);
		File icdValidationEng = new File(folderEng.getCanonicalPath() + FILE_SEPARATOR + fileName);
		BufferedWriter bwEng = new BufferedWriter(new FileWriter(icdValidationEng));

		File folderFra = prepareFolder(SUB_FOLDER_ICD, SUB_FOLDER_VALIDATION, SUB_FOLDER_FRA);
		File icdValidationFra = new File(folderFra.getCanonicalPath() + FILE_SEPARATOR + fileName);

		List<ValidationRuleSet> icdCodeValidations = findICDValidationRules(currentOpenContextId);
		buildCacheList(currentValidationSetMap, icdCodeValidations, currentOpenContextId);

		int count = writeValidationFile(currentValidationSetMap, generateTablesModel.getFileFormat(), bwEng);

		PublicationStatistics icdValidationEngStatistics = new PublicationStatistics("ICD_VALIDATION_ENG",
				LanguageConstants.ENG.getLanguageDescription(), count);
		statisticsSummary.add(icdValidationEngStatistics);

		PublicationStatistics icdValidationFraStatistics = new PublicationStatistics("ICD_VALIDATION_FRA",
				LanguageConstants.FRA.getLanguageDescription(), count);
		statisticsSummary.add(icdValidationFraStatistics);

		FileUtils.copyFile(icdValidationEng, icdValidationFra);
	}

	@Override
	protected void generateFrenchAuditFile(Long currentOpenContextId, Long lastVersionICDContextId,
			GenerateReleaseTablesCriteria generateTablesModel) throws IOException {

		buildWorksheet("10CA", LANGUAGE_CODE_FRA, null, currentOpenContextId, lastVersionICDContextId,
				generateTablesModel.getCurrentOpenYear(), 0);

	}

	@Override
	protected String generateValidationRuleSet(String dhCode, String xmlString) {
		StringBuilder builder = new StringBuilder();
		builder.append(dhCode);
		IcdValidationXml validationXml = XmlUtils.deserialize(IcdValidationXml.class, xmlString);

		builder.append(getSPIndicator(validationXml));

		builder.append(validationXml.getMRDxMain());
		builder.append(validationXml.getNewBorn());

		builder.append(get5THCode(validationXml));

		builder.append(validationXml.getGenderCode());
		builder.append(padAge(validationXml.getAgeMin()));
		builder.append(padAge(validationXml.getAgeMax()));

		return builder.toString();
	}

	/**
	 * Get 5TH character of ICD validation rule See CSRE - End Product Management Rules RU-174
	 * 
	 * @param validationXml
	 * @return the 5th character
	 */

	private String get5THCode(IcdValidationXml validationXml) {
		if (!(CODE_VALUE_N.equals(validationXml.getDxType1()) && CODE_VALUE_N.equals(validationXml.getDxType2())
				&& CODE_VALUE_N.equals(validationXml.getDxType3()) && CODE_VALUE_Y.equals(validationXml.getMRDxMain()))
				&& !(CODE_VALUE_Y.equals(validationXml.getDxType1()) && CODE_VALUE_Y.equals(validationXml.getDxType2()) && CODE_VALUE_Y
						.equals(validationXml.getDxType3()))) {
			if (CODE_VALUE_Y.equals(validationXml.getDxType4())) {
				return DX_TYPE_4;
			} else if (CODE_VALUE_Y.equals(validationXml.getDxType9())) {
				return DX_TYPE_9;
			} else if (CODE_VALUE_N.equals(validationXml.getMRDxMain())
					&& CODE_VALUE_N.equals(validationXml.getDxType3())
					&& CODE_VALUE_N.equals(validationXml.getDxType6())) {
				// RU174 #7
				return DX_TYPE_6;
			} else if (CODE_VALUE_Y.equals(validationXml.getDxType3())
					&& CODE_VALUE_Y.equals(validationXml.getDxType6())) {
				// RU174 #3
				return DX_TYPE_6;
			} else if (CODE_VALUE_Y.equals(validationXml.getDxType6())) {
				return DX_TYPE_6;
			} else if (CODE_VALUE_Y.equals(validationXml.getDxType3())) {
				return DX_TYPE_3;
			}
		}
		return BLANK;
	}

	@Override
	protected String[] getDisabledHeaderDescs(String languageCode, Long lastVersionYear) {
		String[] headerDescs = new String[3];
		headerDescs[0] = LANGUAGE_CODE_ENG.equals(languageCode) ? CIMSConstants.ICD_10_CA : "CIM-10-CA";
		headerDescs[1] = LanguageConstants.getConstant(languageCode).getSector();
		headerDescs[2] = (LanguageConstants.getConstant(languageCode).getOldRule()) + BLANK
				+ (LANGUAGE_CODE_ENG.equals(languageCode) ? "10CA" : "") + lastVersionYear;

		return headerDescs;
	}

	@Override
	protected String getDisabledTitleValue(String languageCode, String currentVersion) {
		StringBuilder sb = new StringBuilder();
		sb.append(LANGUAGE_CODE_ENG.equals(languageCode) ? ("10CA" + currentVersion + " ICD-10-CA Disabled Validation Rule")
				: ("CIM-10-CA " + currentVersion + " Règles de validation désactivées"));
		return sb.toString();
	}

	@Override
	protected String[] getNewHeaderDescs(String languageCode, Long currentVersionYear) {
		String[] headerDescs = new String[3];
		headerDescs[0] = LANGUAGE_CODE_ENG.equals(languageCode) ? CIMSConstants.ICD_10_CA : "CIM-10-CA";
		headerDescs[1] = LanguageConstants.getConstant(languageCode).getSector();
		headerDescs[2] = (LanguageConstants.getConstant(languageCode).getNewRule()) + BLANK
				+ (LANGUAGE_CODE_ENG.equals(languageCode) ? "10CA" : "") + currentVersionYear;

		return headerDescs;
	}

	@Override
	protected String getNewTitleValue(String languageCode, String currentVersion) {
		StringBuilder sb = new StringBuilder();
		sb.append(LANGUAGE_CODE_ENG.equals(languageCode) ? ("10CA" + currentVersion + " ICD-10-CA New Validation Rule")
				: ("CIM-10-CA " + currentVersion + " Règles de validation nouvelles"));
		return sb.toString();
	}

	@Override
	protected String[] getRevisedHeaderDescs(String languageCode, Long currentVersionYear, Long lastVersionYear) {
		String[] headerDescs = new String[4];
		headerDescs[0] = LANGUAGE_CODE_ENG.equals(languageCode) ? CIMSConstants.ICD_10_CA : "CIM-10-CA";
		headerDescs[1] = LanguageConstants.getConstant(languageCode).getSector();
		headerDescs[2] = (LanguageConstants.getConstant(languageCode).getNewRule()) + BLANK
				+ (LANGUAGE_CODE_ENG.equals(languageCode) ? "10CA" : "") + currentVersionYear;
		headerDescs[3] = (LanguageConstants.getConstant(languageCode).getOldRule()) + BLANK
				+ (LANGUAGE_CODE_ENG.equals(languageCode) ? "10CA" : "") + lastVersionYear;

		return headerDescs;
	}

	@Override
	protected String getRevisionsTitleValue(String languageCode, String currentVersion) {
		StringBuilder sb = new StringBuilder();
		sb.append(LANGUAGE_CODE_ENG.equals(languageCode) ? ("10CA" + currentVersion + " ICD-10-CA Validation Rule Revisions")
				: ("CIM-10-CA " + currentVersion + " Règles de validation révisées"));
		return sb.toString();
	}

	private String getSPIndicator(IcdValidationXml validationXml) {

		if (CODE_VALUE_Y.equals(validationXml.getDxType4()) || CODE_VALUE_Y.equals(validationXml.getDxType9())) {
			return "S";
		} else {
			return "P";
		}
	}

	@Override
	protected String getWorksheetName(String languageCode) {
		return LANGUAGE_CODE_ENG.equals(languageCode) ? "ICD10CA_Code_Validation" : "Validation CIM-10-CA";
	}

	@Override
	protected void processExistingValidation(String code, ValidationRuleSet currentRule, ValidationRuleSet priorRule,
			Map<String, ValidationRuleSet> priorDhMap) {
		if (!currentRule.getValidationRuleText().equals(priorRule.getValidationRuleText())) {
			CodeValidationAudit audit = new CodeValidationAudit();
			audit.setCode(code);
			audit.setDhcode(currentRule.getDhcode());
			audit.setNewDescription(currentRule.getValidationRuleText());
			audit.setOldDescription(priorRule.getValidationRuleText());
			revisedCodeValidations.add(audit);
		}
		priorDhMap.remove(code);
	}

	private int writeValidationFile(Map<String, Map<String, ValidationRuleSet>> validationRuleSetMap,
			FileFormat fileFormat, BufferedWriter bw) throws IOException {
		int count = 0;
		for (String dhCode : validationRuleSetMap.keySet()) {
			Map<String, ValidationRuleSet> dhMap = validationRuleSetMap.get(dhCode);
			for (String code : dhMap.keySet()) {
				ValidationRuleSet validationRuleSet = dhMap.get(code);
				if (FileFormat.TAB.equals(fileFormat)) {
					count += buildLineTab(validationRuleSet, bw);
				} else {
					count += buildLineFixedWidth(validationRuleSet, bw);
				}
			}
		}
		bw.close();
		return count;
	}
}
