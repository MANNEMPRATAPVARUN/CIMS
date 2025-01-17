package ca.cihi.cims.service.sgsc;

import java.util.List;

import ca.cihi.cims.model.sgsc.CCIComponentSupplement;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;

public class CCIInterventionContent extends SupplementContentGenerator {

	@Override
	public String generateSupplementContent(SupplementContentRequest request) {
		List<CCIComponentSupplement> components = getViewService().getCciInterventionComponentsWithDefinition(
				request.getLanguageCode(), request.getCurrentContextId(), request.getSection().toString(),
				"description");
		StringBuilder result = new StringBuilder();
		if (components != null && components.size() > 0) {
			for (CCIComponentSupplement component : components) {

				result.append("<tr><td colspan='4'><span class='title'>").append(component.getDescription())
						.append(" (").append(component.getConceptCode()).append(")");
				result.append("</td></tr>");
				result.append(component.getNote());
				result.append("<tr><td height='10' colspan='4'>&nbsp;</td></tr>");
			}

		}
		return result.toString();
	}

}
