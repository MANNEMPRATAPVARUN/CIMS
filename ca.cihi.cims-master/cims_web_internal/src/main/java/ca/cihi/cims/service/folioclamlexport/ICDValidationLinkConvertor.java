package ca.cihi.cims.service.folioclamlexport;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import ca.cihi.cims.model.IcdCodeValidation;

public class ICDValidationLinkConvertor extends LinkConvertor {

	public static final String ICD_VALIDATION_PREFIX = "icdValidation_";

	private static final Logger logger = LogManager.getLogger(ICDValidationLinkConvertor.class);

	@Override
	protected String convertRealUrl(String url) {
		int start = url.indexOf("?");
		if (start == -1) {
			return HASH;
		}
		String paramString = url.substring(start + 1);
		StringTokenizer st = new StringTokenizer(paramString, "&");
		if ((st != null) && (st.countTokens() == 3)) {
			String conceptCode = getParamValue(st.nextToken());

			String fileName = ICD_VALIDATION_PREFIX + conceptCode + FolioClamlFileGenerator.HTML_FILE_EXTENSION;
			File file = new File(FolioClamlFileGenerator.getFilePath(getExportFolder(), getQueryCriteria(), fileName));
			if (!file.exists()) {
				logger.debug("get ICD Validation Rules for Category with parameters: conceptCode=" + conceptCode
						+ ", classification=" + getQueryCriteria().getClassification() + ", contextId="
						+ getQueryCriteria().getContextId() + ", language=" + getQueryCriteria().getLanguage());
				List<IcdCodeValidation> myList = getViewService().getHierICDValidationRulesForCategory(conceptCode,
						getQueryCriteria().getClassification(), getQueryCriteria().getContextId().toString(),
						getQueryCriteria().getLanguage());
				Map<String, Object> model = new HashMap<>();
				model.put("TITLE", getIcdValidationTitle() + conceptCode);
				model.put("LANGUAGE", getQueryCriteria().getLanguage().substring(0, 2));
				model.put("CONTENT_TYPE", FolioClamlFileGenerator.CONTENT_TYPE);
				model.put("validations", myList);
				String template = getMessageSource().getMessage(FOLIO_ICDVALIDATION_TEMPLATE, null,
						Locale.getDefault());
				try {
					FolioClamlFileGenerator.wirteFileUsingTemplate(file, model, template, getVelocityEngine());
				} catch (Exception e) {
					logger.error("Write icd validation html for: " + conceptCode + " encountered exception. ", e);
					// TODO do we stop process?
				}
			}

			return fileName;
		} else {
			logger.error("Wrong url: " + url + " found while converting icd validation link!!!");
			return HASH;
		}
	}

}
