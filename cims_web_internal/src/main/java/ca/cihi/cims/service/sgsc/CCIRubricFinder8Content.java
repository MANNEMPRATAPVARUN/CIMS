package ca.cihi.cims.service.sgsc;

import ca.cihi.cims.model.sgsc.SupplementContentRequest;

public class CCIRubricFinder8Content extends SupplementContentGenerator {

	@Override
	public String generateSupplementContent(SupplementContentRequest request) {

		StringBuilder result = new StringBuilder();
		result.append("<tr><td colspan='4'>");
		result.append(getClassificationService().getCCIRubricContent8(request.getLanguageCode(),
				request.getCurrentContextId(), "8", null));
		result.append("</td></tr>");
		return result.toString();
	}

}
