package ca.cihi.cims.service.sgsc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.cihi.cims.model.sgsc.CodeDescription;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;

public class CCINewCodeContent extends SupplementContentGenerator {

	@Override
	public String generateSupplementContent(SupplementContentRequest request) {
		StringBuilder result = new StringBuilder();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("languageCode", request.getLanguageCode());
		paramMap.put("codeClassId", getViewService().getCCIClassID("TextPropertyVersion", "Code"));
		paramMap.put("cciCodeClassId", getViewService().getCCIClassID("ConceptVersion", "CCICode"));
		paramMap.put("longTitleClassId", getViewService().getCCIClassID("TextPropertyVersion", "LongTitle"));
		paramMap.put("currentContextId", request.getCurrentContextId());
		paramMap.put("priorContextId", request.getPriorContextId());

		List<CodeDescription> codes = getSgscMapper().findCCINewCodes(paramMap);
		if ((codes != null) && (codes.size() > 0)) {
			result.append("<tr><td colspan='4'>");
			result.append("<table style='width:690px; margin-left:60px;'>");
			for (CodeDescription codeDescription : codes) {
				result.append("<tr><td style='text-align:left;width:150px;'>").append(codeDescription.getConceptCode())
						.append("</td><td style='text-align:left;width:540px;'>")
						.append(codeDescription.getDescription()).append("</td>");
			}
			result.append("</table></td></tr>");
		}
		return result.toString();
	}

}
