package ca.cihi.cims.service.folioclamlexport;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.util.StringUtils;

import ca.cihi.cims.CIMSConstants;

public class NodeLinkConvertor extends LinkConvertor {
	private static final Logger logger = LogManager.getLogger(NodeLinkConvertor.class);

	@Override
	protected String convertRealUrl(String paramString) {
		String[] idPath = paramString.split("/");

		String elementId = idPath[idPath.length - 1];

		Map<String, Object> params = new HashMap<>();
		params.put("baseClassification", getQueryCriteria().getClassification());
		params.put("contextId", getQueryCriteria().getContextId());
		params.put("elemId", elementId);
		logger.debug("Retrieve page for folio with parameters:" + params);
		Long containingPageId = getConceptService().retrievePagebyIdForFolio(params);
		String conceptCode = getViewService().getConceptCode(elementId, getQueryCriteria().getContextId());
		if ((containingPageId == null) || (containingPageId == 0l)) {
			// for index link, the containingPageId will be the direct parent
			containingPageId = Long.parseLong(idPath[idPath.length - 2]);
		}

		if (CIMSConstants.CCI.equals(getQueryCriteria().getClassification()) && !StringUtils.isEmpty(conceptCode)) {
			if (idPath.length > 3
					&& (conceptCode.startsWith("6") || conceptCode.startsWith("7") || conceptCode.startsWith("8"))) {
				containingPageId = Long.parseLong(idPath[2]);
			} else if (idPath.length > 4 && (conceptCode.startsWith("1") || conceptCode.startsWith("2")
					|| conceptCode.startsWith("3") || conceptCode.startsWith("4") || conceptCode.startsWith("5"))) {
				containingPageId = Long.parseLong(idPath[4]);
			}
		}

		return containingPageId + FolioClamlFileGenerator.HTML_FILE_EXTENSION + "#"
				+ ((!StringUtils.isEmpty(conceptCode)) ? conceptCode : elementId);

	}

}
