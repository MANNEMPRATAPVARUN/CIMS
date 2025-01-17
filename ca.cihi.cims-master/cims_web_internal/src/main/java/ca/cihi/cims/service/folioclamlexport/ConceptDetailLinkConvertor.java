package ca.cihi.cims.service.folioclamlexport;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ConceptDetailLinkConvertor extends LinkConvertor {

	public static final String CONCEPT_DETAIL_PREFIX = "conceptDetail_";

	private static final Logger logger = LogManager.getLogger(ConceptDetailLinkConvertor.class);

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

			String fileName = CONCEPT_DETAIL_PREFIX + conceptCode + FolioClamlFileGenerator.HTML_FILE_EXTENSION;
			File file = new File(FolioClamlFileGenerator.getFilePath(getExportFolder(), getQueryCriteria(), fileName));
			if (!file.exists()) {
				String shortPresentation = getViewService().getConceptShortPresentation(conceptCode,
						getQueryCriteria().getClassification(), getQueryCriteria().getContextId(),
						getQueryCriteria().getLanguage());
				Map<String, Object> model = new HashMap<>();
				model.put("conceptCode", conceptCode);
				model.put("shortPresentation", shortPresentation);
				model.put("CONTENT_TYPE", FolioClamlFileGenerator.CONTENT_TYPE);
				model.put("LANGUAGE", getQueryCriteria().getLanguage().substring(0, 2));
				String template = getMessageSource().getMessage(FOLIO_CONCEPTDETAIL_TEMPLATE, null,
						Locale.getDefault());
				try {
					FolioClamlFileGenerator.wirteFileUsingTemplate(file, model, template, getVelocityEngine());
				} catch (Exception e) {
					logger.error("Generate concept detail for: " + conceptCode + " encountered exception. ", e);
					// TODO do we stop process?
				}
			}

			return fileName;
		} else {
			logger.error("Wrong url: " + paramString + " found while converting concept detail link!!!");
			return HASH;
		}
	}

}
