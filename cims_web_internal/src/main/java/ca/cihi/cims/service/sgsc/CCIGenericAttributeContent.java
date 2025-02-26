package ca.cihi.cims.service.sgsc;

import java.util.ArrayList;
import java.util.List;

import ca.cihi.cims.Language;
import ca.cihi.cims.model.IdCodeDescription;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;

public class CCIGenericAttributeContent extends SupplementContentGenerator {

	private static final String MODE_OF_DELIVERY_ENG = "Mode of Delivery";
	private static final String MODE_OF_DELIVERY_FRA = "Méthode utilisée";

	private void addContent(StringBuilder result, List<IdCodeDescription> genAttrModel) {
		for (IdCodeDescription idCode : genAttrModel) {
			result.append("<tr><td style='border: 1px solid black;min-width: 50px;width:50px;text-align:center'>")
					.append(idCode.getCode())
					.append("</td><td style='border: 1px solid black;min-width: 270px;width:270px'>")
					.append(idCode.getDescription()).append("</td></tr>");
		}
	}

	@Override
	public String generateSupplementContent(SupplementContentRequest request) {

		StringBuilder result = new StringBuilder();
		result.append("<tr><td colspan='4'><div id='sticker'>");
		result.append(getTableHead(request.getType(), request.getLanguageCode()));
		result.append("</div>");
		result.append("<table style='width:auto'>");
		if (SupplementContentRequest.Type.LOCATION.getType().equalsIgnoreCase(request.getType())) {
			List<IdCodeDescription> genAttrModel = new ArrayList<IdCodeDescription>();
			genAttrModel.addAll(getViewService().getGenericAttributesForSupplement(CCI, request.getCurrentContextId(),
					"L", request.getLanguageCode()));
			addContent(result, genAttrModel);
			result.append("<tr><td colspan='2' style='border: 1px solid black;font-weight:bold;'>")
					.append(Language.ENGLISH.getCode().equalsIgnoreCase(request.getLanguage()) ? MODE_OF_DELIVERY_ENG
							: MODE_OF_DELIVERY_FRA)
					.append("</td></tr>");
			genAttrModel = new ArrayList<IdCodeDescription>();
			genAttrModel.addAll(getViewService().getGenericAttributesForSupplement(CCI, request.getCurrentContextId(),
					"M", request.getLanguageCode()));
			addContent(result, genAttrModel);
		} else {
			List<IdCodeDescription> genAttrModel = new ArrayList<IdCodeDescription>();
			genAttrModel.addAll(getViewService().getGenericAttributesForSupplement(CCI, request.getCurrentContextId(),
					SupplementContentRequest.Type.getCodeByType(request.getType()), request.getLanguageCode()));
			addContent(result, genAttrModel);
		}

		result.append("</table></td></tr>");
		return result.toString();
	}

	private String getGenericTitle(String type, String languageCode) {
		StringBuilder sb = new StringBuilder();
		if (Language.ENGLISH.getCode().equals(languageCode)) {
			sb.append(SupplementContentRequest.Type.getByType(type).getGenericTitleEng());
		} else {
			sb.append(SupplementContentRequest.Type.getByType(type).getGenericTitleFra());
		}
		return sb.toString();
	}

	private String getTableHead(String type, String languageCode) {
		StringBuilder result = new StringBuilder();
		result.append(
				"<table style='width:auto'><thead><tr><th style='border: 1px solid black;min-width: 336px;width:336px;text-align:left' colspan='2'>")
				.append(getGenericTitle(type, languageCode)).append("</th></tr></thead></table>");

		return result.toString();
	}
}
