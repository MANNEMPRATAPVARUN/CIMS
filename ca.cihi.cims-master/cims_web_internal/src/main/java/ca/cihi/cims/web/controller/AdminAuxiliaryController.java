package ca.cihi.cims.web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.exception.AlreadyInUseException;
import ca.cihi.cims.model.AuxTableValue;
import ca.cihi.cims.service.AuxTableService;
import ca.cihi.cims.util.PropertyManager;
import ca.cihi.cims.web.bean.AuxiliaryViewBean;
import ca.cihi.cims.web.bean.KeyValueBean;
import ca.cihi.cims.web.bean.ValidationResponse;

@Controller
@RequestMapping("/admin/auxiliary")
@SessionAttributes( { WebConstants.AUXILIARY_VIEW_BEAN })
public class AdminAuxiliaryController {

	private static final Log LOGGER = LogFactory.getLog(AdminAuxiliaryController.class);

	private static final String LIST_VIEW = "/admin/listAuxiliary";
	private static final String CLASSIFICATION_PARAM = "classification";

	@Autowired
	private AuxTableService auxService;
	@Autowired
	private PropertyManager propertyManager;

	// ---------------------------------------------------------------------------------------

	@ResponseBody
	@RequestMapping(params = "action=change", method = RequestMethod.GET)
	public ValidationResponse changeAjax(
			Model model,
			@ModelAttribute(WebConstants.AUXILIARY_VIEW_BEAN) @Valid AuxiliaryViewBean viewBean,
			BindingResult result,
			@RequestParam(value = "auxId", required = false) String auxTableValueId, //
			@RequestParam(value = "auxValueCode", required = false) String auxValueCode,
			@RequestParam(value = "auxEngLable", required = false) String auxEngLable,
			@RequestParam(value = "auxEngDesc", required = false) String auxEngDesc,
			@RequestParam(value = "auxFraLable", required = false) String auxFraLable,
			@RequestParam(value = "auxFraDesc", required = false) String auxFraDesc,
			@RequestParam("status") String status) {
		try {
			//check for duplicate data
			if (StringUtils.isBlank(auxTableValueId)
					&& auxService.isRefsetCodeNotUnique(auxValueCode)) {
				result.rejectValue("", "", propertyManager.getMessage(WebConstants.LABEL_AUX_VALUE_CODE) + " should be unique.");
			}
			if (StringUtils.isBlank(auxTableValueId)
					&& auxService.isRefsetNameNotUnique(auxEngLable)) {
				result.rejectValue("", "", propertyManager.getMessage(WebConstants.LABEL_AUX_ENGLISH_LABEL) + " should be unique.");
			}	
			if (StringUtils.isBlank(auxTableValueId)
					&& (StringUtils.isBlank(auxValueCode) || auxValueCode.length() > 3)) {
				result.rejectValue("", "", propertyManager.getMessage(WebConstants.LABEL_AUX_VALUE_CODE)
						+ " size must be between 1 and 3");
			}
			if (StringUtils.isBlank(auxEngLable) || auxEngLable.length() > 50) {
				result.rejectValue("", "", propertyManager.getMessage(WebConstants.LABEL_AUX_ENGLISH_LABEL)
						+ " size must be between 1 and 50");
			}				
			if (!StringUtils.isBlank(auxEngDesc) && auxEngDesc.length() > 255) {
				result.rejectValue("", "", propertyManager.getMessage(WebConstants.LABEL_ENGLISH_MEANING)
						+ " size must be less then or equal to 255");
			}
			if (viewBean.isClassification()) {
				if (StringUtils.isBlank(auxFraLable) || auxFraLable.length() > 50) {
					result.rejectValue("", "", propertyManager.getMessage(WebConstants.LABEL_AUX_FRENCH_LABEL)
							+ " size must be between 1 and 50");
				}
				if (!StringUtils.isBlank(auxFraDesc) && auxFraDesc.length() > 255) {
					result.rejectValue("", "", propertyManager.getMessage(WebConstants.LABEL_FRENCH_MEANING)
							+ " size must be less then or equal to 255");
				}
			}
			if (result.hasErrors()) {
				return toResponse(result);
			}
			viewBean.setStatus(status);
			viewBean.setAuxEngLable(auxEngLable);
			viewBean.setAuxEngDesc(auxEngDesc);
			if (viewBean.isClassification()) {
				viewBean.setAuxFraLable(auxFraLable);
				viewBean.setAuxFraDesc(auxFraDesc);
			}
			if (auxTableValueId == null || auxTableValueId.isEmpty()) {
				long id = auxService.insertAuxTableValue(viewBean.toAux());
				return ValidationResponse.success(id);
			} else {
				viewBean.setAuxTableValueId(Long.valueOf(auxTableValueId));
				auxService.updateAuxTableValue(viewBean.toAux());
			}
			// viewBean.setAuxTableValues(getAuxTableValues(viewBean));
		} catch (Exception ex) {
			return ValidationResponse.fail(ex.getMessage());
		}
		return null;
	}

	@ResponseBody
	@RequestMapping(params = "action=delete", method = RequestMethod.GET)
	public ValidationResponse deleteAjax(Model model,
			@ModelAttribute(WebConstants.AUXILIARY_VIEW_BEAN) AuxiliaryViewBean viewBean, BindingResult result,
			@RequestParam("auxId") String auxTableValueId) {
		LOGGER.debug("> deleteAux");
		try {
			viewBean.setAuxTableValueId(Long.valueOf(auxTableValueId));
			auxService.deleteAux(viewBean.toAux());
			return null;
		} catch (AlreadyInUseException ae) {
			return ValidationResponse.fail("Unable to delete since it is already associated");
		} catch (Exception ex) {
			return ValidationResponse.fail(ex.getMessage());
		}
	}

	@ModelAttribute("auxList")
	public Collection<KeyValueBean> populateAuxTableCode(
			@RequestParam(value = CLASSIFICATION_PARAM, defaultValue = "false") boolean classification) {
		ArrayList<KeyValueBean> keyValues = new ArrayList<KeyValueBean>();
		keyValues.add(new KeyValueBean("", "Please select "));
		if (classification) {
			for (Map.Entry<String, String> entry : auxService.getClassificationTableCodes().entrySet()) {
				keyValues.add(new KeyValueBean(entry.getKey(), entry.getValue()));
			}
		} else {
			List<String> list = auxService.getChangeRequestTableCodes();
			Map<String, String> friendlyName = new HashMap<String, String>();
			friendlyName.put("CHANGETYPE", "Change Type");
			friendlyName.put("CHANGENATURE", "Change Nature");
			friendlyName.put("REQUESTOR", "Requestor");
			friendlyName.put("REFSETCATEGORY", "Refset Category");
			for (String auxTableCode : list) {
				keyValues.add(new KeyValueBean(auxTableCode, friendlyName.get(auxTableCode)));
			}
		}
		return keyValues;
	}

	/** Classification table years */
	@ModelAttribute("auxYearsList")
	public Collection<KeyValueBean> populateAuxYears() {
		ArrayList<KeyValueBean> keyValues = new ArrayList<KeyValueBean>();
		keyValues.add(new KeyValueBean("", "Please select "));
		for (Long year : auxService.getCciVersionCodes()) {
			keyValues.add(new KeyValueBean(year.toString()));
		}
		return keyValues;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String process(ModelMap model, @ModelAttribute(WebConstants.AUXILIARY_VIEW_BEAN) AuxiliaryViewBean viewBean,
			BindingResult result) {
		LOGGER.debug("< process action : " + viewBean.getAuxCode() + " - " + viewBean.getYear());
		viewBean.setAuxTableValues(Collections.<AuxTableValue> emptyList());
		if (!StringUtils.isEmpty(viewBean.getAuxCode())) {
			if (viewBean.isClassification()) {
				if (viewBean.getYear() != null) {
					long auxTableId = auxService.getClassificationTableIdByCode(viewBean.getAuxCode());
					viewBean.setAuxTableId(auxTableId);
					viewBean.setAuxTableValues(auxService.getClassificationTableValues(viewBean.getAuxCode(), viewBean
							.getYear()));
					boolean readonlyYear = auxService.isReadonlyYear(viewBean.getYear());
					model.addAttribute("readonly", readonlyYear ? "true" : "false");
					model.addAttribute("enableAdd", readonlyYear ? "N" : "Y");
				}
			} else {
				Long auxTableId = auxService.getChangeRequestTableIdByCode(viewBean.getAuxCode());
				viewBean.setAuxTableId(auxTableId);
				viewBean.setFirstRecord(false);
				if (!auxService.getChangeRequestTableValues(viewBean.getAuxCode()).isEmpty()){
					viewBean.setAuxTableValues(auxService.getChangeRequestTableValues(viewBean.getAuxCode()));
				} else {
					List<AuxTableValue> auxTableValues = new ArrayList<AuxTableValue>();
					AuxTableValue auxTableValue = new AuxTableValue();
					auxTableValue.setAuxValueCode(" ");
					auxTableValue.setAuxEngLable(" ");
					
					auxTableValues.add(auxTableValue);
					
					viewBean.setAuxTableValues(auxTableValues);
					viewBean.setFirstRecord(true);
				}
				model.addAttribute("enableAdd", CollectionUtils.isEmpty(viewBean.getAuxTableValues()) ? "N" : "Y");
				model.addAttribute("readonly", "false");
			}
		}
		LOGGER.debug("> process");
		return LIST_VIEW;
	}

	public void setAuxService(AuxTableService auxService) {
		this.auxService = auxService;
	}

	public void setPropertyManager(PropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(HttpServletRequest request, HttpSession session, ModelMap model,
			@RequestParam(value = CLASSIFICATION_PARAM, defaultValue = "false") boolean classification) {
		AuxiliaryViewBean viewBean = null;
		AuxiliaryViewBean sessionAuxViewBean = (AuxiliaryViewBean) session
				.getAttribute(WebConstants.AUXILIARY_VIEW_BEAN);
		if (sessionAuxViewBean != null && sessionAuxViewBean.isClassification() == classification) {
			viewBean = sessionAuxViewBean;
			model.addAttribute("enableAdd", viewBean.getAuxTableValues().size() > 0 ? "Y" : "N");
		} else {
			viewBean = new AuxiliaryViewBean();
			viewBean.setClassification(classification);
		}
		model.addAttribute(WebConstants.AUXILIARY_VIEW_BEAN, viewBean);
		return LIST_VIEW;
	}

	private ValidationResponse toResponse(BindingResult result) {
		List<ObjectError> errorList = new ArrayList<ObjectError>();
		errorList.addAll(result.getAllErrors());
		ValidationResponse bean = new ValidationResponse();
		bean.setStatus(ValidationResponse.Status.FAIL);
		bean.setErrorMessageList(errorList);
		return bean;
	}

}