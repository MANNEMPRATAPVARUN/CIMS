package ca.cihi.cims.web.controller.cci;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.FreezingStatus;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.util.DtdValidator;

public class CciComponentsCommon {

	// No need to look this up. This page is specifically for CCI
	protected static final String baseClassification = "CCI";
	private final String xmlDtd = "cihi_component_definition.dtd";

	public static final String LIST_MAIN_PAGE = "cciComponents";
	public static final String PATH_PREFIX = "/classification/cci/components";
	public static final String LIST_COMPONENT_REFERENCES = PATH_PREFIX + "/listComponentReferences";

	protected static final String MODEL_KEY_VERSION_CODES = "versionCodes";
	protected static final String MODEL_KEY_VERSION_CODE_DEFAULT = "vcDefault";
	protected static final String MODEL_KEY_READ_ONLY = "readOnly";
	protected static final String MODEL_KEY_PAGE_SIZE = "pageSize";
	protected static final String MODEL_KEY_RESULT_SIZE = "resultSize";
	public static final int pageSize = 300;

	@Autowired
	private LookupService lookupService;
	private DtdValidator dtdValidator = new DtdValidator();

	// -----------------------------------------------------------------

	@ModelAttribute("baseClassifications")
	public List<String> getBaseClassifications() {
		// Base Classifications
		List<String> baseClassifications = new ArrayList<String>();
		baseClassifications.add(baseClassification);
		return baseClassifications;
	}

	/*
	 * public boolean getContextFreezingStatus(String versionCode) { ContextIdentifier contextId =
	 * lookupService.findBaseContextIdentifierByClassificationAndYear( baseClassification, versionCode); return
	 * contextId.getFreezingStatus() == null ? false : true; }
	 */

	public FreezingStatus getContextFreezingStatus(String versionCode) {
		ContextIdentifier contextId = lookupService.findBaseContextIdentifierByClassificationAndYear(
				baseClassification, versionCode);
		return contextId.getFreezingStatus();
	}

	@ModelAttribute(MODEL_KEY_PAGE_SIZE)
	public int getPageSize() {
		return pageSize;
	}

	@ModelAttribute("status")
	public List<String> getStatusOptions() {
		List<String> status = new ArrayList<String>();
		status.add("All");
		status.add(WordUtils.capitalizeFully(ConceptStatus.ACTIVE.name()));
		status.add(WordUtils.capitalizeFully(ConceptStatus.DISABLED.name()));
		return status;
	}

	public void setDtdValidator(DtdValidator dtdValidator) {
		this.dtdValidator = dtdValidator;
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	public FieldError validateXml(String xml, String language, boolean allowEmpty) {
		FieldError fieldError = null;
		if (!StringUtils.isEmpty(xml)) {
			String rootElement = "block";
			String error = dtdValidator.validateSegment(rootElement, xmlDtd, xml);
			if (error != null) {
				fieldError = new FieldError("", "Component ", error);
			}
		} else {
			if (!allowEmpty) {
				// Add logic here to return an error if the xml is null
				fieldError = new FieldError("", "Component ", language + " Definition cannot be empty");
			}
		}
		return fieldError;
	}

}
