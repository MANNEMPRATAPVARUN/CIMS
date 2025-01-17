package ca.cihi.cims.service.sgsc;

import java.util.List;

import ca.cihi.cims.Language;
import ca.cihi.cims.model.CciComponentType;
import ca.cihi.cims.model.IdCodeDescription;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;

public class CCIGroupContent extends SupplementContentGenerator {

	private void formatGroups(StringBuilder result, SupplementContentRequest request,
			List<IdCodeDescription> cciGroups) {

		if ((request.getSection() <= 3)) {
			if (!request.getFolio()) {
				result.append("<table>");
				result.append("<tr>");
				int i = 0;
				for (IdCodeDescription idCodeDescription : cciGroups) {
					if (idCodeDescription.getCode().length() == 1) {
						if (((++i % 2) == 1) && (i > 1)) {
							result.append("</tr><tr>");
						}
						result.append("<td><a href=\"javascript:getGroupContent('").append(request.getLanguageCode())
								.append("',").append(request.getCurrentContextId()).append(",")
								.append(request.getSection()).append(",'").append(idCodeDescription.getCode())
								.append("',").append(idCodeDescription.getId()).append(");\">(")
								.append(idCodeDescription.getCode()).append(") ")
								.append(idCodeDescription.getDescription()).append("</a></td>");
					}
				}
				result.append("</tr>");
				result.append("</table>");
				result.append("<div id='supplementContent' style='margin-top:20px;'></div>");
			} else {
				for (IdCodeDescription idCodeDescription : cciGroups) {
					if (idCodeDescription.getCode().length() == 1) {
						result.append(getClassificationService().getCCIGroupContent(request.getLanguageCode(),
								request.getCurrentContextId(), request.getSection().toString(),
								idCodeDescription.getCode(), idCodeDescription.getId()));
					}
				}
			}
		} else {
			result.append("<table>");

			for (IdCodeDescription idCodeDescription : cciGroups) {
				result.append("<tr><td style='min-width:60px;width:60px;border: 1px solid black;'>");
				result.append("(").append(idCodeDescription.getCode()).append(")");
				result.append("</td><td style='min-width:300px;width:30px;border: 1px solid black;'>")
						.append(idCodeDescription.getDescription()).append("</td></tr>");
			}

			result.append("</table>");
		}

	}

	@Override
	public String generateSupplementContent(SupplementContentRequest request) {
		long sectionId = getConceptService().getCCISectionIdBySectionCode(request.getSection().toString(),
				request.getCurrentContextId());
		List<IdCodeDescription> cciGroups = getConceptService().getCciComponentsPerSectionLongTitle(sectionId,
				request.getCurrentContextId(), Language.fromString(request.getLanguage()), CciComponentType.GroupComp,
				"code", null);
		StringBuilder result = new StringBuilder();
		result.append("<tr><td colspan='4'>");
		if ((cciGroups != null) && (cciGroups.size() > 0)) {

			formatGroups(result, request, cciGroups);

		}
		result.append("</td></tr>");
		return result.toString();
	}

}
