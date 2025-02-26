package ca.cihi.cims.web.controller.tabular;

import static ca.cihi.cims.WebConstants.CURRENT_USER;
import static ca.cihi.cims.model.Classification.CCI;
import static ca.cihi.cims.model.Classification.ICD;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.shared.FacilityType;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.access.ChangeRequestPermission;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.model.tabular.TabularConceptModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptIcdValidationSetModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptValidationDadHoldingModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptValidationGenderModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptValidationSetModel;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.ClassificationService;
import ca.cihi.cims.validator.ErrorBuilder;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.tabular.TabularValidationBean;
import ca.cihi.cims.web.filter.CurrentContext;
import ca.cihi.cims.web.filter.CurrentContextParams;

@Controller
@SessionAttributes(CURRENT_USER)
@RequestMapping(value = "/tabulars/validation")
public class TabularValidationController {

	public static final String VIEW_BASE = "/classification/tabular/";
	public static final String VIEW_EDIT = VIEW_BASE + "validationInfo";

	public static final String ATTRIBUTE_BEAN = "bean";

	@Autowired
	private ClassificationService service;
	@Autowired
	private ChangeRequestService changeRequestService;

	public ChangeRequestService getChangeRequestService() {
		return changeRequestService;
	}

	public void setChangeRequestService(ChangeRequestService changeRequestService) {
		this.changeRequestService = changeRequestService;
	}

	private final Log log = LogFactory.getLog(getClass());

	@Autowired
	ContextProvider contextProvider;
	@Autowired
	CurrentContext currentContext;
	// ------------------------------------------------------------------

	@ModelAttribute(ATTRIBUTE_BEAN)
	public TabularValidationBean loadBean(
			HttpServletRequest request,
			@ModelAttribute(value = CURRENT_USER) User user,
			@RequestParam("id") long tabularId, //
			@RequestParam(value = "dh", defaultValue = "-1") long dataHoldingId,
			@RequestParam(value = "add", defaultValue = "false") boolean add,
			@RequestParam(value = "language", defaultValue = "ENG") String localeLanguage) {
		//Need to manually set context as HandlerInterceptor is called after @ModelAttribute resolution 
		ContextDefinition definition = new CurrentContextParams().definition(request);
		ContextAccess context = contextProvider.findContext(definition);
		currentContext.makeCurrentContext(context);
		request.setAttribute("automaticContextParams", new CurrentContextParams()
				.urlParameters(context.getContextId()));
		Language lang = Language.fromString(localeLanguage);
		List<TabularConceptValidationDadHoldingModel> dataHoldings = service.getDataHoldings(lang);
		List<TabularConceptValidationDadHoldingModel> otherDataHoldings = new ArrayList<TabularConceptValidationDadHoldingModel>();
		if (dataHoldingId == -1) {
			dataHoldingId = dataHoldings.get(0).getElementId();
			for (TabularConceptValidationDadHoldingModel m : dataHoldings) {
				if (m.getCode().equals(FacilityType.CODE_DAD)) {
					dataHoldingId = m.getElementId();
					break;
				}
			}
		}
		TabularConceptModel concept = service.getTabularConceptById(tabularId, true);
		TabularConceptValidationSetModel model = service.getTabularValidationSet(tabularId, dataHoldingId);
		if (model.isDisabled() && !add) {
			model = null;
		}
		TabularValidationBean bean = new TabularValidationBean();
		bean.setLockTimestamp(service.getChangeRequestTimestamp());
		bean.setModel(model);
		bean.setCode(concept.getCode());
		bean.setType(concept.getType());
		bean.setParentCode(concept.getParentCode());
		bean.setElementId(concept.getElementId());
		bean.setDataHoldingId(dataHoldingId);
		bean.setEnglishShortTitle(concept.getShortTitleEng());

		// lists
		bean.setGenders(service.getGenders(lang));
		if (add) {
			model.setGenderCode(TabularConceptValidationGenderModel.CODE_MALE_FEMALE_OTHER);
		}
		bean.setDataHoldings(dataHoldings);
		for (TabularConceptValidationDadHoldingModel dh : dataHoldings) {
			if (dh.getElementId() != dataHoldingId) {
				otherDataHoldings.add(dh);
			}
		}
		bean.setOtherDataholdings(otherDataHoldings);
		if (bean.getType().getClassification() == CCI) {
			bean.setCciExtentReferences(service.getCciExtentReferences(lang));
			bean.setCciLocationReferences(service.getCciLocationReferences(lang));
			bean.setCciModeOfDeliveryReferences(service.getCciModeOfDeliveryReferences(lang));
			bean.setCciStatusReferences(service.getCciStatusReferences(lang));
		} else {
			loadIcdDxtype(bean, lang);
			bean.setIcdDxTypes(service.getIcdDxTypes(lang));
		}
		ChangeRequestPermission p = service.getChangeRequestClassificationPermission(user, ChangeRequestCategory.T);
		bean.setEditable(p.isCanWrite());
		return bean;
	}

	private void loadIcdDxtype(TabularValidationBean bean, Language lang) {
		TabularConceptValidationSetModel model = bean.getModel();
		if (model != null && bean.getClassification() == ICD) {
			TabularConceptIcdValidationSetModel modelIcd = (TabularConceptIcdValidationSetModel) model;
			if (modelIcd.getDxTypeId() != null) {
				bean.setIcdDxType(service.getIcdDxType(modelIcd.getDxTypeId(), lang));
			}
		}
	}

	@RequestMapping(method = GET, value = "remove")
	public String remove(@ModelAttribute(value = CURRENT_USER) User user,
			@RequestParam(value = "language", defaultValue = "ENG") String localeLanguage,
			@ModelAttribute(ATTRIBUTE_BEAN) TabularValidationBean bean, BindingResult result) throws Exception {
		try {
			OptimisticLock lock = new OptimisticLock(bean.getLockTimestamp());
			service.deleteTabularValidationSet(lock, user, bean.getElementId(), bean.getDataHoldingId());
			bean.resetModel();
			bean.setResult(BeanResult.SUCCESS);
			bean.setSuccessMessage("Validations were removed successfuly");
			bean.setLockTimestamp(lock.getTimestamp());
		} catch (Exception ex) {
			bean.setResult(BeanResult.ERROR);
			bean.setErrorMessage(ex.getMessage());
		}
		return VIEW_EDIT;
	}

	@RequestMapping(method = POST, value = "edit")
	public String save(@ModelAttribute(value = CURRENT_USER) User user,
			@RequestParam(value = "language", defaultValue = "ENG") String localeLanguage,
			@ModelAttribute(ATTRIBUTE_BEAN) TabularValidationBean bean, BindingResult result, HttpServletRequest request) {
		long changeRequestId = 0l;
		try {
			if (request.getParameter("changeRequestId") != null) {
				changeRequestId = Long.parseLong(request.getParameter("changeRequestId"));
			} else {
				changeRequestId = Long.parseLong(request.getParameter("ccp_rid"));
			}
		} catch (NumberFormatException e) {
			log.info("Can not get change requestId");
		}
		try {
			TabularConceptValidationSetModel model = bean.getModel();
			List<Long> extendToOtherDataHoldings = bean.isExtendValidationToOtherDataHoldings() ? bean
					.getSelectedOtherDataHoldings() : null;
			if (extendToOtherDataHoldings == null) {
				extendToOtherDataHoldings = Collections.<Long> emptyList();
			}
			OptimisticLock lock = new OptimisticLock(bean.getLockTimestamp());
			service.saveTabularValidationSet(lock, new ErrorBuilder("model", result), user, bean.getElementId(), model,
					extendToOtherDataHoldings);
			loadIcdDxtype(bean, Language.fromString(localeLanguage));
			if (result.hasErrors()) {
				bean.setResult(BeanResult.INVALID);
			} else {
				bean.setExtendValidationToOtherDataHoldings(false);
				bean.setSelectedOtherDataHoldings(Collections.<Long> emptyList());
				if (changeRequestService.isIncomplete(changeRequestId)) {
					bean.setResult(BeanResult.INCOMPLETE);
				} else {
					bean.setResult(BeanResult.SUCCESS);
				}
				bean.setLockTimestamp(lock.getTimestamp());
			}
		} catch (Exception ex) {
			bean.setResult(BeanResult.ERROR);
			bean.setErrorMessage(ex.getMessage());
		}
		return VIEW_EDIT;
	}

	public void setService(ClassificationService service) {
		this.service = service;
	}

	@RequestMapping(method = GET, value = "edit")
	public String show() {
		return VIEW_EDIT;
	}

}
