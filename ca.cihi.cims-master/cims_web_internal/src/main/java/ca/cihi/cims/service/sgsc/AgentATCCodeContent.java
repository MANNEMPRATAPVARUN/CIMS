package ca.cihi.cims.service.sgsc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.cihi.cims.Language;
import ca.cihi.cims.model.sgsc.AgentGroupDTO;
import ca.cihi.cims.model.sgsc.DeviceAgentATC;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;

public class AgentATCCodeContent extends SupplementContentGenerator {

	@Override
	public String generateSupplementContent(SupplementContentRequest request) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("contextId", request.getCurrentContextId());
		params.put("languageCode", request.getLanguageCode());
		params.put("sectionId", getConceptService().getCCISectionIdBySectionCode("1", request.getCurrentContextId()));
		params.put("deviceAgentClassId", getViewService().getCCIClassID("ConceptVersion", "DeviceAgent"));
		params.put("deviceAgentCpvClassId",
				getViewService().getCCIClassID("ConceptPropertyVersion", "DeviceAgentToSectionCPV"));
		params.put("agentGroupClassId", getViewService().getCCIClassID("ConceptVersion", "AgentGroup"));
		params.put("agentGroupIndicatorClassId",
				getViewService().getCCIClassID("ConceptPropertyVersion", "AgentGroupIndicator"));
		params.put("agentGroupCodeClassId", getViewService().getCCIClassID("TextPropertyVersion", "DomainValueCode"));
		params.put("agentGroupDescriptionClassId",
				getViewService().getCCIClassID("TextPropertyVersion", "DomainValueDescription"));
		params.put("componentCodeClassId", getViewService().getCCIClassID("TextPropertyVersion", "ComponentCode"));
		params.put("agentTypeDescriptionClassId",
				getViewService().getCCIClassID("TextPropertyVersion", "AgentTypeDescription"));
		params.put("agentExampleClassId", getViewService().getCCIClassID("TextPropertyVersion", "AgentExample"));
		params.put("agentATCCodeClassId", getViewService().getCCIClassID("TextPropertyVersion", "AgentATCCode"));
		List<AgentGroupDTO> agentGroups = getSgscMapper().findAgentATCCodes(params);
		StringBuilder result = new StringBuilder();
		result.append("<tr><td colspan='4'>");
		result.append(getHeader(request.getLanguage()));
		result.append("<table style='width:auto'>");
		for (AgentGroupDTO dto : agentGroups) {
			result.append("<tr><td colspan='4' style='border: 1px solid black;font-weight:bold;'>")
					.append(dto.getAgentGroupDescription()).append("</td></tr>");
			for (DeviceAgentATC atc : dto.getDeviceAgentATCs()) {
				result.append("<tr>");
				result.append("<td style='border: 1px solid black;min-width: 104px;width:104px;'>")
						.append(atc.getCode()).append("</td>");
				result.append("<td style='border: 1px solid black;min-width: 300px;width:300px'>")
						.append(atc.getAgentType()).append("</td>");
				result.append("<td style='border: 1px solid black;min-width: 300px;width:300px'>")
						.append(atc.getAgentExample() == null ? "" : atc.getAgentExample()).append("</td>");
				result.append("<td style='border: 1px solid black;min-width: 79px;width:79px;'>")
						.append(atc.getAtcCode()).append("</td>");
				result.append("</tr>");
			}
		}
		result.append("</table>");
		result.append("</td></tr>");
		return result.toString();
	}

	private String getHeader(String language) {
		StringBuilder header = new StringBuilder();
		if (Language.ENGLISH.getCode().equalsIgnoreCase(language)) {
			header.append(
					"<div id='sticker'><table style='width:auto'><thead><tr><th style='border: 1px solid black;min-width: 104px;width:104px;text-align:center'>CCI Qualifier Codes</th><th style='border: 1px solid black;min-width: 300px;width:300px;text-align:center'>Agents (Local and Systemic)</th><th style='border: 1px solid black;min-width: 300px;width:300px;text-align:center'>Examples</th><th style='border: 1px solid black;min-width: 79px;width:79px;text-align:center'>ATC Codes</th></tr></thead></table></div>");
		} else {
			header.append(
					"<div id='sticker'><table style='width:auto'><thead><tr><th style='border: 1px solid black;min-width: 104px;width:104px;text-align:center'>Codes qualificateurs de la CCI</th><th style='border: 1px solid black;min-width: 300px;width:300px;text-align:center'>Agents (local et systémique)</th><th style='border: 1px solid black;min-width: 300px;width:300px;text-align:center'>Examples</th><th style='border: 1px solid black;min-width: 79px;width:79px;text-align:center'>Codes ATC</th></tr></thead></table></div>");
		}
		return header.toString();
	}

}
