package ca.cihi.cims.web.controller.tabular;

import static ca.cihi.cims.Language.ENGLISH;
import static ca.cihi.cims.WebConstants.CURRENT_USER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.cci.CciComponent;
import ca.cihi.cims.content.cci.CciTabular;
import ca.cihi.cims.model.IdCodeDescription;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.model.tabular.TabularConceptModel;
import ca.cihi.cims.model.tabular.TabularConceptType;
import ca.cihi.cims.validator.ErrorBuilder;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.tabular.TabularAddBean;
import ca.cihi.cims.web.filter.CurrentContext;
import ca.cihi.cims.web.filter.CurrentContextParams;

@Controller
@SessionAttributes(CURRENT_USER)
@RequestMapping(value = "/tabulars/children/add")
public class TabularAddController extends TabularBaseController {

	public static final String VIEW = "/classification/tabular/tabularBasicInfo";
	private final Log log = LogFactory.getLog(getClass());

	@Autowired
	private CurrentContext currentContext;
	@Autowired
	private ContextProvider contextProvider;
	// ------------------------------------------------------------------

	private String getComponentLabel(CciComponent c) {
		return c.getCode() + " - " + c.shortTitle(ENGLISH);
	}

	@ModelAttribute(ATTRIBUTE_BEAN)
	public TabularAddBean loadBean(HttpServletRequest request,
			@RequestParam(value = "id", required = false) Long parentId,
			@RequestParam(value = "type") TabularConceptType type,
			@RequestParam(value = "root", defaultValue = "false") boolean parentRoot) {
		StopWatch w = new StopWatch("loadBean: " + parentId);
		ContextDefinition definition = new CurrentContextParams().definition(request);
		ContextAccess context = contextProvider.findContext(definition);
		currentContext.makeCurrentContext(context);
		request.setAttribute("automaticContextParams", new CurrentContextParams()
					.urlParameters(context.getContextId()));
		
		TabularAddBean bean = loadBeanMinimal(parentId, type, parentRoot);
		boolean loadDetails = request.getMethod().equals("GET");
		if (loadDetails) {
			loadDetails(bean, w);
		}
		if (config.isTracePerformanceEnabled()) {
			request.setAttribute(ATTRIBUTE_LOADTIME, w.getTotalTimeMillis());
			request.setAttribute(ATTRIBUTE_LOADDETAILS, StringUtils.replace(w.toString(), ";", "\n"));
			if (log.isDebugEnabled()) {
				log.debug("Tabular [" + parentId + "/" + type + "] load time: " + w.getTotalTimeMillis());
			}
		}
		log.info(w);
		return bean;
	}

	private TabularAddBean loadBeanMinimal(Long parentId, TabularConceptType type, boolean parentRoot) {
		TabularConceptModel model = new TabularConceptModel();
		// workaround
		model.setElementId(parentId);
		model.setType(type);
		TabularAddBean bean = new TabularAddBean(model);
		bean.setParentElementId(parentId);
		bean.setParentRoot(parentRoot);
		bean.setLockTimestamp(service.getChangeRequestTimestamp());
		return bean;
	}

	private void loadDetails(TabularAddBean bean, StopWatch w) {
		TabularConceptModel model = bean.getModel();
		w.start("loadParent");
		TabularConceptModel parent = bean.getParentElementId() == null || bean.isParentRoot() ? null : service
				.getTabularConceptLightById(bean.getParentElementId());
		w.stop();
		model.setParent(parent);
		if (parent != null) {
			model.setParentCode(parent.getCode());
			if (parent.getType() == model.getType()) {
				model.setNestingLevel(parent.getNestingLevel() + 1);
			} else {
				model.setNestingLevel(1);
			}
		}
		bean.setEdit(false);
		bean.setSaveVisible(true);
		bean.setAddQualifierVisible(service.isAddQualifierEnabled(model.getType()));
		switch (model.getType()) {
		case CCI_GROUP: {
			w.start("getContainingPageId");
			long sectionId = service.getContainedPageId(parent.getElementId());
			w.stop();
			w.start("getSortedSectionGroupComponents");
			bean.setCciGroups(toMap2(service.getSortedSectionGroupComponents(sectionId, Language.ENGLISH)));
			w.stop();
			break;
		}
		case CCI_RUBRIC: {
			w.start("getContainingPageId");
			long sectionId = service.getContainedPageId(parent.getElementId());
			w.stop();
			w.start("getGroupComponent");
			// FIXME: avoid framework loading!!
			CciTabular cciParent = (CciTabular) service.getTabularById(bean.getParentElementId());
			bean.setCciGroupName(getComponentLabel(cciParent.getGroupComponent()));
			w.stop();
			w.start("getSortedSectionInterventionComponents");
			bean
					.setCciInterventions(toMap(service.getSortedSectionInterventionComponents(sectionId,
							Language.ENGLISH)));
			w.stop();
			break;
		}
		case CCI_CCICODE: {
			w.start("getContainingPageId");
			long sectionId = service.getContainedPageId(parent.getElementId());
			w.stop();
			w.start("getSortedSectionApproachTechniqueComponents");
			bean.setCciTechniques(toMap(service
					.getSortedSectionApproachTechniqueComponents(sectionId, Language.ENGLISH)));
			w.stop();
			w.start("getSortedSectionDeviceAgentComponents");
			bean.setCciDevices(toMap(service.getSortedSectionDeviceAgentComponents(sectionId, Language.ENGLISH)));
			w.stop();
			w.start("getSortedSectionTissueComponents");
			bean.setCciTissues(toMap(service.getSortedSectionTissueComponents(sectionId, Language.ENGLISH)));
			w.stop();
			break;
		}
		default:
			bean.setEditable(true);
			bean.setCodeVisible(true);
			bean.setCodeEditable(true);
		}
	}

	@RequestMapping(method = POST)
	public String save(@ModelAttribute(value = CURRENT_USER) User user,
			@ModelAttribute(ATTRIBUTE_BEAN) TabularAddBean bean, BindingResult result) {
		StopWatch w = new StopWatch("save");

		Long parentId = bean.isParentRoot() ? null : bean.getParentElementId();
		ErrorBuilder errors = new ErrorBuilder(null, result);
		long tabularId = 0;
		TabularConceptModel model = bean.getModel();
		model.trimSpaces();
		OptimisticLock lock = new OptimisticLock(bean.getLockTimestamp());
		try {
			switch (model.getType()) {
			case CCI_GROUP:
				Long cciGroup = bean.getCciGroup();
				if (cciGroup == null) {
					errors.rejectValue("cciGroup", "required");
				} else {
					tabularId = service.createTabularCciGroup(lock, errors, user, parentId, cciGroup);
				}
				break;
			case CCI_RUBRIC:
				Long cciIntervention = bean.getCciIntervention();
				if (cciIntervention == null) {
					errors.rejectValue("cciIntervention", "required");
				} else {
					tabularId = service.createTabularCciRubric(lock, errors, user, parentId, cciIntervention);
				}
				break;
			case CCI_CCICODE:
				tabularId = service.createTabularCciCode(lock, errors, user, parentId, bean.getCciTechnique(), bean
						.getCciDevice(), bean.getCciTissue());
				break;
			default:
				tabularId = service.createTabular(lock, errors, user, model.getType(), parentId, model.getCode());
			}
			if (result.hasErrors()) {
				loadDetails(bean, w);
				bean.setResult(BeanResult.INVALID);
			} else {
				bean.getModel().setElementId(tabularId);
				bean.setResult(BeanResult.SUCCESS);
			}
		} catch (Exception ex) {
			log.error("Error saving", ex);
			bean.setResult(BeanResult.ERROR);
			bean.setErrorMessage(ex.getMessage());
			loadDetails(bean, w);
		}
		log.info(w);
		return VIEW;
	}

	@RequestMapping(method = GET)
	public String show() {
		return VIEW;
	}

	private Map<Long, String> toMap(List<IdCodeDescription> list) {
		return toMap(list, 0);
	}

	private Map<Long, String> toMap(List<IdCodeDescription> list, int length) {
		Map<Long, String> map = new LinkedHashMap<Long, String>(list.size());
		if (length == 0) {
			for (IdCodeDescription c : list) {
				map.put(c.getId(), c.getCodeDescription());
			}
		} else {
			for (IdCodeDescription c : list) {
				if (c.getCode().length() == length) {
					map.put(c.getId(), c.getCodeDescription());
				}
			}
		}
		return map;
	}

	private Map<Long, String> toMap2(List<IdCodeDescription> list) {
		return toMap(list, 2);
	}

}
