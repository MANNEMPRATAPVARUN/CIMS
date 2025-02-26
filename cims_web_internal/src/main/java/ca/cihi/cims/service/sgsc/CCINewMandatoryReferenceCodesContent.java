package ca.cihi.cims.service.sgsc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.cihi.cims.Language;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.model.prodpub.CCIGenericAttribute;
import ca.cihi.cims.model.prodpub.CCIReferenceAttribute;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;

public class CCINewMandatoryReferenceCodesContent extends SupplementContentGenerator {

	@Override
	public String generateSupplementContent(SupplementContentRequest request) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("referenceAttributeCPVClassId",
				getViewService().getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ReferenceAttributeCPV"));
		params.put("genericAttributeCPVClassId",
				getViewService().getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "GenericAttributeCPV"));
		params.put("attributeCodeClassId",
				getViewService().getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "AttributeCode"));
		params.put("attributeDescriptionClassId",
				getViewService().getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "AttributeDescription"));
		params.put("attributeMandatoryIndicatorClassId",
				getViewService().getCCIClassID(WebConstants.BOOLEAN_PROPERTY_VERSION, "AttributeMandatoryIndicator"));
		params.put("referenceAttributeClassId",
				getViewService().getCCIClassID(WebConstants.CONCEPT_VERSION, "ReferenceAttribute"));
		params.put("currentContextId", request.getCurrentContextId());
		params.put("priorContextId", request.getPriorContextId());
		params.put("languageCode", request.getLanguageCode());

		List<CCIReferenceAttribute> newCodes = getSgscMapper().findCCINewMandatoryReferenceCodes(params);

		StringBuilder result = new StringBuilder();
		result.append("<tr><td colspan='4'>");
		// Table header
		result.append("<div id='sticker'>");
		result.append(getTableHeader(request.getLanguage()));
		result.append("</div>");
		result.append("<table style='width:auto;'>");
		for (CCIReferenceAttribute attribute : newCodes) {

			String rowspan = calculateRowspan(
					attribute.getGenericAttributes() != null ? attribute.getGenericAttributes().size() : 0);
			result.append("<tr><td style='min-width:77px;width:77px;border: 1px solid black;' ").append(rowspan)
					.append(">");
			result.append(attribute.getCode()).append("</td>");
			result.append("<td style='min-width:200px;width:200px;border: 1px solid black;' ").append(rowspan)
					.append(">");
			result.append(attribute.getDescription()).append("</td>");
			if ((attribute.getGenericAttributes() != null) && (attribute.getGenericAttributes().size() > 0)) {
				int i = 0;
				for (CCIGenericAttribute genericAttribute : attribute.getGenericAttributes()) {
					if (i == 0) {
						result.append("<td style='min-width:67px;width:67px;border: 1px solid black;'>")
								.append(genericAttribute.getCode())
								.append("</td><td style='min-width:300px;width:300px;border: 1px solid black;'>")
								.append(genericAttribute.getDescription()).append("</td></tr>");
					} else {
						result.append("<tr><td style='min-width:67px;width:67px;border: 1px solid black;'>")
								.append(genericAttribute.getCode())
								.append("</td><td style='min-width:300px;width:300px;border: 1px solid black;'>")
								.append(genericAttribute.getDescription()).append("</td></tr>");
					}
				}
			} else {
				result.append(
						"<td style='min-width:67px;width:67px;border: 1px solid black;'>&nbsp;</td><td style='min-width:300px;width:300px;border: 1px solid black;'>&nbsp;</td></tr>");
			}

		}
		result.append("</table>");
		result.append("</tr>");
		return result.toString();
	}

	private String getTableHeader(String language) {
		StringBuilder sb = new StringBuilder();
		if (Language.ENGLISH.getCode().equalsIgnoreCase(language)) {
			sb.append(APPENDIX_GH_ENG_HEADER);
		} else {
			sb.append(APPENDIX_GH_FRA_HEADER);
		}

		return sb.toString();
	}

}
