package ca.cihi.cims.service.sgsc;

import java.util.List;

import ca.cihi.cims.CIMSException;
import ca.cihi.cims.Language;
import ca.cihi.cims.model.CciComponentType;
import ca.cihi.cims.model.IdCodeDescription;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;

public class CCIQualifierContent extends SupplementContentGenerator {

	@Override
	public String generateSupplementContent(SupplementContentRequest request) {
		long sectionId = getConceptService().getCCISectionIdBySectionCode(request.getSection().toString(),
				request.getCurrentContextId());

		List<IdCodeDescription> cciQualifiers = getConceptService().getCciComponentsPerSectionLongTitle(sectionId,
				request.getCurrentContextId(), Language.fromString(request.getLanguage()),
				getQualifier(request.getQualifier()), "code", null);
		StringBuilder result = new StringBuilder();
		result.append("<tr><td colspan='4'>");
		if ((cciQualifiers != null) && (cciQualifiers.size() > 0)) {
			result.append("<table style='width:auto;margin-left:60px;'>");
			for (IdCodeDescription codeDesc : cciQualifiers) {
				result.append("<tr><td style='border: 1px solid black;min-width: 60px;width:60px;text-align:center'>")
						.append(codeDesc.getCode())
						.append("</td><td style='border: 1px solid black;min-width: 450px;width:450px'>")
						.append(codeDesc.getDescription()).append("</td></tr>");
			}
			result.append("</table>");

		}
		result.append("</td></tr>");
		return result.toString();
	}

	private CciComponentType getQualifier(Integer qualifier) {
		if (qualifier == 1) {
			return CciComponentType.ApproachTechnique;
		} else if (qualifier == 2) {
			return CciComponentType.DeviceAgent;
		} else if (qualifier == 3) {
			return CciComponentType.Tissue;
		}
		throw new CIMSException("Qialifier is undefined: " + qualifier);
	}

}
