package ca.cihi.cims.service.sgsc;

import java.util.ArrayList;
import java.util.List;

import ca.cihi.cims.Language;
import ca.cihi.cims.model.prodpub.CCIGenericAttribute;
import ca.cihi.cims.model.prodpub.CCIReferenceAttribute;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;

public class CCIReferenceValueContent extends SupplementContentGenerator {

	@Override
	public String generateSupplementContent(SupplementContentRequest request) {
		List<CCIReferenceAttribute> referenceAttributes = new ArrayList<CCIReferenceAttribute>();
		if (SupplementContentRequest.Type.LOCATION.getType().equals(request.getType())) {
			referenceAttributes.addAll(getViewService().getCCIReferenceAttributesForSupplement(
					request.getCurrentContextId(), "L", request.getLanguageCode()));
			referenceAttributes.addAll(getViewService().getCCIReferenceAttributesForSupplement(
					request.getCurrentContextId(), "M", request.getLanguageCode()));
		} else {
			referenceAttributes
					.addAll(getViewService().getCCIReferenceAttributesForSupplement(request.getCurrentContextId(),
							SupplementContentRequest.Type.getCodeByType(request.getType()), request.getLanguageCode()));
		}
		StringBuilder result = new StringBuilder();
		result.append("<tr><td colspan='4'>");
		// Table header
		result.append("<div id='sticker'>");
		result.append(getTableHead(request.getType(), request.getLanguageCode()));
		result.append("</div>");
		result.append("<table style='width:auto;'>");
		for (CCIReferenceAttribute attribute : referenceAttributes) {
			if ((attribute.getGenericAttributes() != null) && (attribute.getGenericAttributes().size() > 0)) {
				String rowspan = calculateRowspan(attribute.getGenericAttributes().size());
				result.append("<tr><td style='min-width:77px;width:77px;border: 1px solid black;' ").append(rowspan)
						.append(">");
				result.append(attribute.getCode()).append("</td>");
				result.append("<td style='min-width:200px;width:200px;border: 1px solid black;' ").append(rowspan)
						.append(">");
				result.append(attribute.getDescription()).append("</td>");
				int i = 0;
				for (CCIGenericAttribute genericAttribute : attribute.getGenericAttributes()) {
					if (i == 0) {
						result.append("<td style='min-width:81px;width:81px;border: 1px solid black;'>")
								.append(genericAttribute.getCode())
								.append("</td><td style='min-width:300px;width:300px;border: 1px solid black;'>")
								.append(genericAttribute.getDescription()).append("</td></tr>");
					} else {
						result.append("<tr><td style='min-width:81px;width:81px;border: 1px solid black;'>")
								.append(genericAttribute.getCode())
								.append("</td><td style='min-width:300px;width:300px;border: 1px solid black;'>")
								.append(genericAttribute.getDescription()).append("</td></tr>");
					}
				}
			}

		}
		result.append("</table>");
		result.append("</tr>");
		return result.toString();
	}

	private String getCodeTitle(String type, String languageCode) {
		StringBuilder sb = new StringBuilder();
		if (Language.ENGLISH.getCode().equals(languageCode)) {
			sb.append(SupplementContentRequest.Type.getByType(type).getAttributeCodeEng());
		} else {
			sb.append(SupplementContentRequest.Type.getByType(type).getAttributeCodeFra());
		}
		return sb.toString();
	}

	private String getDescriptionTitle(String type, String languageCode) {
		StringBuilder sb = new StringBuilder();
		if (Language.ENGLISH.getCode().equals(languageCode)) {
			sb.append(SupplementContentRequest.Type.getByType(type).getAttributeDescriptionEng());
		} else {
			sb.append(SupplementContentRequest.Type.getByType(type).getAttributeDescriptionFra());
		}
		return sb.toString();
	}

	private String getTableHead(String type, String languageCode) {
		StringBuilder result = new StringBuilder();
		if (Language.ENGLISH.getCode().equals(languageCode)) {
			result.append(
					"<table style='width:auto'><thead><tr><th style='min-width:77px;width:77px;border: 1px solid black;'>Reference Number</th><th style='min-width:200px;width:200px;border: 1px solid black;'>Reference Description</th><th style='min-width:81px;width:81px;border: 1px solid black;'>")
					.append(getCodeTitle(type, languageCode))
					.append("</th><th style='min-width:300px;width:300px;border: 1px solid black;'>")
					.append(getDescriptionTitle(type, languageCode)).append("</th></tr></thead></table>");

		} else {
			result.append(
					"<table style='width:auto'><thead><tr><th style='min-width:77px;width:77px;border: 1px solid black;'>Numéro de référence</th><th style='min-width:200px;width:200px;border: 1px solid black;'>Description de référence</th><th style='min-width:81px;width:81px;border: 1px solid black;'>")
					.append(getCodeTitle(type, languageCode))
					.append("</th><th style='min-width:300px;width:300px;border: 1px solid black;'>")
					.append(getDescriptionTitle(type, languageCode)).append("</th></tr></thead></table>");
		}
		return result.toString();
	}

}
