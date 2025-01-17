package ca.cihi.cims.service.sgsc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.cihi.cims.model.sgsc.CodeDescription;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;

public class ICDNewCodeContent extends SupplementContentGenerator {

	@Override
	public String generateSupplementContent(SupplementContentRequest request) {
		StringBuilder sb = new StringBuilder();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("languageCode", request.getLanguageCode());
		paramMap.put("codeClassId", getConceptService().getClassId(ICD10CA, "TextPropertyVersion", "Code"));
		paramMap.put("categoryClassId", getConceptService().getClassId(ICD10CA, "ConceptVersion", "Category"));
		paramMap.put("longTitleClassId", getConceptService().getClassId(ICD10CA, "TextPropertyVersion", "LongTitle"));
		paramMap.put("currentContextId", request.getCurrentContextId());
		paramMap.put("priorContextId", request.getPriorContextId());

		List<CodeDescription> results = getSgscMapper().findICDNewCodes(paramMap);

		if (results != null && results.size() > 0) {
			sb.append("<tr><td colspan='4'>");
			sb.append("<table style='width:600px; margin-left:60px;'>");
			for (CodeDescription codeDescription : results) {
				sb.append("<tr><td style='text-align:left;width:60px;'>")
						.append(codeDescription.getConceptCode())
						.append("</td><td style='text-align:left;width:540px;'>")
						.append(codeDescription.getDescription()).append("</td>");
			}
			sb.append("</table></td></tr>");
		}

		return sb.toString();
	}

}
