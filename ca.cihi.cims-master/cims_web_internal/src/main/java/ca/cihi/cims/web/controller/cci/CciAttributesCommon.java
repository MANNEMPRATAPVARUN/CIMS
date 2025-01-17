package ca.cihi.cims.web.controller.cci;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.FreezingStatus;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.service.LookupService;

public class CciAttributesCommon {

	public static final int pageSize = 300;
	protected static final String CCI = "CCI";

	public static final String LIST_MAIN_PAGE = "cciAttributes";
	public static final String cciAttributesForViewer = "cciAttributesForViewer";
	public static final String PATH_PREFIX = "/classification/cci/attributes";

	public static final String MODEL_KEY_RESULT_SIZE = "resultSize";
	protected static final String MODEL_KEY_VERSION_CODES = "versionCodes";
	protected static final String MODEL_KEY_VERSION_CODE_DEFAULT = "vcDefault";
	protected static final String MODEL_KEY_READ_ONLY = "readOnly";
	protected static final String MODEL_KEY_PAGE_SIZE = "pageSize";

	@Autowired
	protected LookupService lookupService;
	@Autowired
	protected MessageSource messageSource;
	@Autowired
	protected ContextProvider contextProvider;

	// ------------------------------------------------------------------

	@ModelAttribute(MODEL_KEY_PAGE_SIZE)
	public int buildCciAttributeModel() {
		return pageSize;
	}

	@ModelAttribute("attributeTypes")
	public Map<String, String> getAttributeTypes() {
		Map<String, String> attributeTypes = new LinkedHashMap<String, String>();
		attributeTypes.put("S", "Status");
		attributeTypes.put("L", "Location");
		attributeTypes.put("M", "Mode of Delivery");
		attributeTypes.put("E", "Extent");
		return attributeTypes;
	}

	@ModelAttribute("attributeViewTypes")
	public Map<String, String> getAttributeViewTypes() {
		// Attribute View Types
		Map<String, String> attributeViewTypes = new LinkedHashMap<String, String>();
		attributeViewTypes.put("generic", "Generic Attribute");
		attributeViewTypes.put("reference", "Reference Value");
		return attributeViewTypes;
	}

	@ModelAttribute("baseClassifications")
	public List<String> getBaseClassifications() {
		// Base Classifications
		List<String> baseClassifications = new ArrayList<String>();
		baseClassifications.add(CCI);
		return baseClassifications;
	}

	/*
	 * public boolean getContextFreezingStatus(String versionCode) { ContextIdentifier contextId =
	 * lookupService.findBaseContextIdentifierByClassificationAndYear(CCI, versionCode);
	 * 
	 * return (FreezingStatus.TAB == contextId.getFreezingStatus() || FreezingStatus.ALL == contextId
	 * .getFreezingStatus()) ? true : false; }
	 */

	public FreezingStatus getContextFreezingStatus(String versionCode) {
		ContextIdentifier contextId = lookupService.findBaseContextIdentifierByClassificationAndYear(CCI, versionCode);
		return contextId.getFreezingStatus();
	}

	@ModelAttribute("status")
	public List<String> getStatusOptions() {
		List<String> status = new ArrayList<String>();
		status.add("All");
		status.add(WordUtils.capitalizeFully(ConceptStatus.ACTIVE.name()));
		status.add(WordUtils.capitalizeFully(ConceptStatus.DISABLED.name()));
		return status;
	}

	@ModelAttribute(MODEL_KEY_VERSION_CODES)
	public List<String> getVersionCodes(ModelMap model) {
		String defaultVersionCode = null;
		Map<String, Boolean> versionCodesOpen = new HashMap<String, Boolean>();
		// Version Codes
		Collection<ContextIdentifier> contextIdentifiers = contextProvider
				.findBaseClassificationVersionYearVersionCodes(CCI);
		List<String> versionCodes = new ArrayList<String>();
		for (ContextIdentifier context : contextIdentifiers) {
			versionCodes.add(context.getVersionCode());
			versionCodesOpen.put(context.getVersionCode(), context.isContextOpen());

			if (context.isContextOpen()) {
				if (defaultVersionCode == null) {
					defaultVersionCode = context.getVersionCode();
				} else {
					int defVerCode = Integer.parseInt(defaultVersionCode);
					long verCode = Integer.parseInt(context.getVersionCode());

					if (verCode < defVerCode) {
						defaultVersionCode = context.getVersionCode();
					}
				}
			}
		}
		Collections.sort(versionCodes);
		Collections.reverse(versionCodes);
		model.addAttribute(MODEL_KEY_VERSION_CODE_DEFAULT, defaultVersionCode);
		model.addAttribute(MODEL_KEY_READ_ONLY, versionCodesOpen);
		return versionCodes;
	}

	public void setContextProvider(ContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
