package ca.cihi.cims.service.sgsc;

import java.util.List;

import ca.cihi.cims.Language;
import ca.cihi.cims.model.CciComponentType;
import ca.cihi.cims.model.IdCodeDescription;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;

public class CCIRubricFinderContent extends SupplementContentGenerator {

	@Override
	public String generateSupplementContent(SupplementContentRequest request) {

		StringBuilder result = new StringBuilder();
		result.append("<tr><td colspan='4'>");
		if ((request.getSection() <= 3)) {
			long sectionId = getConceptService().getCCISectionIdBySectionCode(request.getSection().toString(),
					request.getCurrentContextId());
			List<IdCodeDescription> cciGroups = getConceptService().getCciComponentsPerSectionLongTitle(sectionId,
					request.getCurrentContextId(), Language.fromString(request.getLanguage()),
					CciComponentType.GroupComp, "code", null);
			if (!request.getFolio()) {
				result.append("<div><table>");
				result.append("<tr>");
				int i = 0;
				for (IdCodeDescription idCodeDescription : cciGroups) {
					if (idCodeDescription.getCode().length() == 1) {
						if (((++i % 2) == 1) && (i > 1)) {
							result.append("</tr><tr>");
						}
						result.append("<td><a href=\"javascript:getRubricContent('").append(request.getLanguageCode())
								.append("',").append(request.getCurrentContextId()).append(",")
								.append(request.getSection()).append(",'").append(idCodeDescription.getCode())
								.append("',").append(idCodeDescription.getId()).append(");\">(")
								.append(idCodeDescription.getCode()).append(") ")
								.append(idCodeDescription.getDescription()).append("</a></td>");
					}
				}
				result.append("</tr>");
				result.append("</table></div>");
				result.append("<div id='supplementContent' style='margin-top:20px;'></div>");
			} else {
				for (IdCodeDescription idCodeDescription : cciGroups) {
					if (idCodeDescription.getCode().length() == 1) {
						result.append(getClassificationService().getCCIRubricContent(request.getLanguageCode(),
								request.getCurrentContextId(), request.getSection().toString(),
								idCodeDescription.getCode(), idCodeDescription.getId()));
					}
				}
			}
		} else {
			result.append(getClassificationService().getCCIRubricContent(request.getLanguageCode(),
					request.getCurrentContextId(), request.getSection().toString(), null, null));
		}
		result.append("</td></tr>");
		return result.toString();
	}

}
