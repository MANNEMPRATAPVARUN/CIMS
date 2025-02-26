package ca.cihi.cims.web.controller.classification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.CciCodeValidation;
import ca.cihi.cims.model.ClassificationViewerModel;
import ca.cihi.cims.model.CodeDescription;
import ca.cihi.cims.model.ContentViewerModel;
import ca.cihi.cims.model.IcdCodeValidation;
import ca.cihi.cims.model.SearchResultModel;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.ConceptService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.TransformQualifierlistService;
import ca.cihi.cims.service.ViewService;
import ca.cihi.cims.util.PropertyManager;
import ca.cihi.cims.web.bean.CciValidationReportViewBean;
import ca.cihi.cims.web.bean.CodeSearchResultBean;
import ca.cihi.cims.web.bean.ConceptViewBean;
import ca.cihi.cims.web.bean.IcdValidationReportViewBean;
import ca.cihi.cims.web.bean.ReferenceReportViewBean;

@Controller
// @SessionAttributes({ WebConstants.VIEW_BEAN })
public class ClassificationViewerController {

	static final String CCI_VALIDATION_REPORT_VIEW = "/classification/view/cciValidationReport";

	protected static final String CLASSIFICATION_VIEWER_ALLBOOKINDEXES_KEY = "allBookIndexes";
	// KEYS
	protected static final String CLASSIFICATION_VIEWER_MODEL_KEY = "viewerModel";
	static final String CLASSIFICATION_VIEWER_VIEW = "/classification/view/viewClassification";
	static final String CODE_REFERENCE_REPORT_VIEW = "/classification/view/referenceReport";

	static final String CONTENT_DETAIL_DIALOG_VIEW = "/classification/view/conceptDetail";
	static final String CONTENT_VIEW = "/classification/view/contents";
	protected static final String CONTEXT_IDENTIFIERS_MODEL_KEY = "contextIdentifiers";
	static final String ICD_VALIDATION_REPORT_VIEW = "/classification/view/icdValidationReport";
	/**
	 * The name of the request parameter that JQueryUI's autocomplete widget uses to submit the user's search string.
	 */
	private static final String JQUERY_UI_AUTOCOMPLETE_PARAMETER = "term";
	private static final Log LOGGER = LogFactory.getLog(ClassificationViewerController.class);
	/**
	 * How many search results should we display in the autocomplete flyout list?
	 */
	private static final int MAX_AUTOCOMPLETE_SEARCH_RESULTS = 18;

	public static String SEARCHBY_BOOKINDEX = "bookIndex";
	public static String SEARCHBY_CODE = "code";
	// Views
	static final String SELECT_CLASSIFICATION_VIEW = "selectClassification";
	protected static final String TITLE = "title";

	// ---------------------------------------------------------------------------

	@Autowired
	private ChangeRequestService changeRequestService;
	@Autowired
	private ConceptService conceptService;
	private ContextProvider contextProvider;

	@Autowired
	private LookupService lookupService;

	private PropertyManager propertyManager;

	private TransformQualifierlistService transformationServiceQualifierlist;
	private ViewService viewService;

	/**
	 * Displays attributes for a selected reference attributes The following method was calling the framework and was
	 * slow. Decision has been made to bypass the framework until performance improves at which time initial
	 * functionality to be recovered from history.
	 */
	@RequestMapping(value = "attributePopup.htm", method = RequestMethod.GET)
	public String attributePopup(final Model model, final HttpServletRequest request,
			@RequestParam("classification") final String classification,
			@RequestParam("contextId") final Long contextId, @RequestParam("language") final String language,
			@RequestParam("refid") final String refCode) {
		LOGGER.debug("attributePopup");
		ReferenceReportViewBean viewBean = new ReferenceReportViewBean();
		viewBean.setRefCode(refCode);
		List<ContentViewerModel> myList = viewService.getAttributesFromReferenceCode(refCode, classification, contextId,
				language);
		List<CodeDescription> codeAttributes = new ArrayList<CodeDescription>();
		String title = "";
		for (ContentViewerModel cvm : myList) {
			String refNote = cvm.getAttributeRefNote();
			if (!StringUtils.isEmpty(refNote)) {
				viewBean.setRefNote(transformationServiceQualifierlist.transformQualifierlistString(refNote));
			}
			CodeDescription codeAttribute = new CodeDescription();
			codeAttribute.setCode(cvm.getAttributeCode());
			codeAttribute.setDescription(cvm.getAttributeDescription());
			String attrNote = cvm.getAttributeNote();
			if (!StringUtils.isEmpty(attrNote)) {
				codeAttribute.setNote(attrNote);
			}
			codeAttributes.add(codeAttribute);
			title = refCode + "- " + cvm.getAttributeRefDesc();
		}
		viewBean.setAttributes(codeAttributes);
		model.addAttribute(TITLE, title);
		model.addAttribute(WebConstants.VIEW_BEAN, viewBean);
		return CODE_REFERENCE_REPORT_VIEW;
	}

	@RequestMapping(value = "cciValidationPopup.htm", method = RequestMethod.GET)
	public String cciValidationPopup(final Model model, final HttpServletRequest request,
			@RequestParam("classification") final String classification,
			@RequestParam("contextId") final String contextId, @RequestParam("language") final String language,
			@RequestParam("refid") final String conceptCode) {
		LOGGER.debug("cciValidationPopup");
		List<CciCodeValidation> myList = viewService.getHierCCIValidationRulesForRubric(conceptCode, classification,
				contextId, language);
		CciValidationReportViewBean viewBean = new CciValidationReportViewBean();
		viewBean.setConceptCode(conceptCode);
		viewBean.setValidations(myList);
		model.addAttribute(WebConstants.VIEW_BEAN, viewBean);
		return CCI_VALIDATION_REPORT_VIEW;
	}

	// ---------------------------------------------------------------------------

	@RequestMapping(value = "viewClassification.htm", params = { "classification" }, method = RequestMethod.GET)
	public ModelAndView classificationViewerForm(final ClassificationViewerModel viewerModel, HttpSession session,
			HttpServletRequest request) {
		LOGGER.debug("classificationViewerForm");
		ModelAndView mav = new ModelAndView();
		mav.addObject(CLASSIFICATION_VIEWER_MODEL_KEY, viewerModel);
		String classification = viewerModel.getClassification();
		Long contextId = viewerModel.getContextId();
		String language = viewerModel.getLanguage();
		List<CodeDescription> allBookIndexes = viewService.getAllBookIndexes(classification, contextId, language);
		mav.addObject(CLASSIFICATION_VIEWER_ALLBOOKINDEXES_KEY, allBookIndexes);
		session.setAttribute("viewMode", Boolean.TRUE);
		mav.setViewName(CLASSIFICATION_VIEWER_VIEW);
		if (request.getParameter("key") != null) {
			mav.addObject("activateNode", request.getParameter("key"));
		}
		return mav;
	}

	@RequestMapping(value = "conceptDetailPopup.htm", method = RequestMethod.GET)
	public String conceptDetailPopup(final Model model, final HttpServletRequest request,
			@RequestParam("classification") final String classification,
			@RequestParam("contextId") final Long contextId, @RequestParam("language") final String language,
			@RequestParam("refid") final String conceptCode) {
		LOGGER.debug("conceptDetailPopup");
		ConceptViewBean viewBean = new ConceptViewBean();
		String shortPresentation = viewService.getConceptShortPresentation(conceptCode, classification, contextId,
				language);
		viewBean.setConceptCode(conceptCode);
		viewBean.setShortPresentation(shortPresentation);
		model.addAttribute(WebConstants.VIEW_BEAN, viewBean);
		return CONTENT_DETAIL_DIALOG_VIEW;
	}

	@RequestMapping(value = "contents.htm", method = RequestMethod.GET)
	public String contentsForm(final Model model, final HttpServletRequest request,
			@RequestParam("classification") final String classification,
			@RequestParam("contextId") final Long contextId, @RequestParam("language") final String language,
			@RequestParam(value = "chRequestId", required = false) final String chRequestId,
			HttpServletResponse response) {
		String lang = language;
		LOGGER.debug("contentViewerForm");
		if ((language == null) || language.toUpperCase().equals("ALL")) {
			lang = "ENG";
		}
		String conceptId = request.getParameter("conceptId");
		String containerConceptId = request.getParameter("containerConceptId");
		final ConceptViewBean viewBean = new ConceptViewBean();
		if ((containerConceptId != null) && !containerConceptId.equals("0") && !containerConceptId.trim().isEmpty()) {
			viewBean.setConceptList(viewService.getContentList(containerConceptId, classification, contextId, lang,
					chRequestId, true, Boolean.FALSE));
		} else {
			viewBean.setConceptList(viewService.getContentList(conceptId, classification, contextId, lang, chRequestId,
					false, Boolean.FALSE));
		}

		viewBean.setContextId(contextId);
		model.addAttribute(WebConstants.VIEW_BEAN, viewBean);
		// response.setHeader("Connection", "Closed"); // gets overriden by apache server
		return CONTENT_VIEW;
	}

	@RequestMapping("/getCodeSearchResult")
	public @ResponseBody List<CodeSearchResultBean> getCodeSearchResult(
			@RequestParam(value = "activeOnly", defaultValue = "true", required = false) boolean activeOnly,
			HttpServletRequest request, HttpServletResponse response) {
		String classification = request.getParameter("classification");
		Long contextId = Long.valueOf(request.getParameter("contextId"));
		String language = request.getParameter("language");
		String searchBy = request.getParameter("searchBy");
		boolean lookupLeaf = request.getParameter("leaf") != null;
		Long indexElementId = null;
		if (SEARCHBY_BOOKINDEX.equalsIgnoreCase(searchBy)) {
			indexElementId = Long.valueOf(request.getParameter("indexElementId"));
		}
		boolean serachByCode = "code".equals(searchBy);
		String searchString = request.getParameter(JQUERY_UI_AUTOCOMPLETE_PARAMETER);
		List<CodeSearchResultBean> results = new ArrayList<CodeSearchResultBean>();
		for (SearchResultModel result : viewService.getSearchResults(classification, contextId, language, searchBy,
				indexElementId, searchString, MAX_AUTOCOMPLETE_SEARCH_RESULTS, activeOnly)) {
			CodeSearchResultBean bean = null;
			if (serachByCode) {
				bean = new CodeSearchResultBean(result.getConceptCode() + ": " + result.getLongDescription(),
						result.getConceptCode(), result.getConceptId());
				bean.setType(result.getConceptType());
			} else {
				// TODO: [result.conceptType] is not supported and is NULL
				bean = new CodeSearchResultBean(result.getLongDescription(), result.getConceptCode(),
						result.getConceptId());
			}
			if (lookupLeaf) {
				bean.setLeaf(conceptService.isValidCode(Long.parseLong(result.getConceptId()), contextId));
			}
			results.add(bean);
		}
		return results;
	}

	@RequestMapping("/getConceptIdPathByConceptId")
	public @ResponseBody String getConceptIdPathByElemId(HttpServletRequest request, HttpServletResponse response) {
		String classification = request.getParameter("classification");
		Long contextId = Long.valueOf(request.getParameter("contextId"));
		Long elementId = Long.valueOf(request.getParameter("conceptId"));
		String conceptIdPath = viewService.getConceptIdPathByElementId(classification, contextId, elementId);
		return conceptIdPath;
	}

	public ContextProvider getContextProvider() {
		return contextProvider;
	}

	@RequestMapping("/getDiagram")
	public @ResponseBody String getDiagram(@RequestParam("diagramFileName") String diagramFileName,
			@RequestParam("contextId") Long contextId, HttpServletResponse response) {
		String diagramData = "";
		byte[] diagramBytes = conceptService.getDiagram(diagramFileName, contextId);

		if ((diagramBytes == null) || (diagramBytes.length == 0)) {
			LOGGER.error("There is no active image with this filename: " + diagramFileName);
			diagramData = "There is no active image with this filename: " + diagramFileName;
		} else {

			diagramData = new String(Base64.encode(diagramBytes));
		}

		return diagramData;
	}

	public LookupService getLookupService() {
		return lookupService;
	}

	public PropertyManager getPropertyManager() {
		return propertyManager;
	}

	public TransformQualifierlistService getTransformationServiceQualifierlist() {
		return transformationServiceQualifierlist;
	}

	@RequestMapping("/getTreeData")
	public @ResponseBody List<ConceptViewBean> getTreeData(HttpServletRequest request, HttpServletResponse response) {
		List<ConceptViewBean> result = new ArrayList<ConceptViewBean>();
		String conceptId = request.getParameter("conceptId");
		String classification = request.getParameter("classification");
		Long contextId = Long.valueOf(request.getParameter("contextId"));
		String language = request.getParameter("language");
		if ((language == null) || language.toUpperCase().equals("ALL")) {
			language = "ENG";
		}
		String chRequestId = request.getParameter("chRequestId");
		LOGGER.debug("getTreeData classification=" + classification + " conceptId=" + conceptId + " chRequestId="
				+ chRequestId);
		String containerId = request.getParameter("containerConceptId");
		List<ContentViewerModel> childNode = viewService.getTreeNodes(conceptId, classification, contextId, language,
				containerId);
		String chConceptType = null;
		for (ContentViewerModel concept : childNode) {
			ConceptViewBean childTreeBean = new ConceptViewBean();
			childTreeBean.setConceptId(concept.getConceptId());
			if (concept.getUnitConceptId() != null) {
				childTreeBean.setContainerConceptId(concept.getUnitConceptId());
			} else {
				childTreeBean.setContainerConceptId("0");
			}
			childTreeBean.setTitle(concept.getTitle());
			if ((chRequestId != null) && !"".equals(chRequestId) && concept.hasRequests()) {
				childTreeBean.setTitle(childTreeBean.getTitle()
						+ " <span style='font-size:small; color:white; background-color:#003366; border-radius:3px'>&nbsp;"
						+ concept.getRequestCount() + "&nbsp;</span>");
			}
			childTreeBean.setConceptCode(concept.getConceptCode());
			childTreeBean.setFolder(concept.getHasChildren().equals("Y"));
			childTreeBean.setLazy(concept.getHasChildren().equals("Y"));
			childTreeBean.setClassification(classification);
			String conceptType = concept.getConceptType();
			if (conceptType == null) {
				if ((chConceptType == null) && !StringUtils.isEmpty(chRequestId)) {
					ChangeRequest r = changeRequestService
							.findLightWeightChangeRequestById(Long.parseLong(chRequestId));
					chConceptType = r.getCategory().getSubject();
				}
				conceptType = chConceptType;
			}
			childTreeBean.setConceptType(conceptType);
			childTreeBean.setContextId(contextId);
			childTreeBean.setLanguage(language);
			childTreeBean.setChRequestId(chRequestId);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("\t" + childTreeBean.getKey() + "\t" + childTreeBean.getTitle());
			}

			childTreeBean.setHasChildren(concept.getHasChildren());
			result.add(childTreeBean);
		}
		return result;
	}

	public ViewService getViewService() {
		return viewService;
	}

	@RequestMapping(value = "icdValidationPopup.htm", method = RequestMethod.GET)
	public String icdValidationPopup(final Model model, final HttpServletRequest request,
			@RequestParam("classification") final String classification,
			@RequestParam("contextId") final String contextId, @RequestParam("language") final String language,
			@RequestParam("refid") final String conceptCode) {
		LOGGER.debug("icdValidationPopup");
		List<IcdCodeValidation> myList = viewService.getHierICDValidationRulesForCategory(conceptCode, classification,
				contextId, language);
		IcdValidationReportViewBean viewBean = new IcdValidationReportViewBean();
		viewBean.setConceptCode(conceptCode);
		viewBean.setValidations(myList);
		model.addAttribute(WebConstants.VIEW_BEAN, viewBean);
		return ICD_VALIDATION_REPORT_VIEW;
	}

	@RequestMapping(value = "/popupDiagram.htm", produces = "image/gif")
	public @ResponseBody byte[] popupDiagram(@RequestParam("diagramFileName") String diagramFileName,
			@RequestParam("contextId") Long contextId, HttpServletResponse response) {
		byte[] diagramBytes = conceptService.getDiagram(diagramFileName, contextId);

		if ((diagramBytes == null) || (diagramBytes.length == 0)) {

			diagramBytes = propertyManager.getMessage("no.active.image").getBytes();
		}

		return diagramBytes;
	}

	@RequestMapping("/getTitle")
	public @ResponseBody String refreshCurrentNodeTitle(HttpServletRequest request, HttpServletResponse response) {
		String conceptId = request.getParameter("conceptId");
		String classification = request.getParameter("classification");
		Long contextId = Long.valueOf(request.getParameter("contextId"));
		String language = request.getParameter("language");
		String title = viewService.getTitleForNode(conceptId, classification, contextId, language);
		int numOfChangeRequests = changeRequestService.findNumOfChangeRequests(Long.parseLong(conceptId));
		String chRequestId = request.getParameter("chRequestId");
		if ((chRequestId != null) && !"".equals(chRequestId) && (numOfChangeRequests > 0)) {
			title = title
					+ " <span style='font-size:small; color:white; background-color:#003366; border-radius:3px'>&nbsp;"
					+ numOfChangeRequests + "&nbsp;</span>";
		}
		return title;
	}

	@RequestMapping(value = "selectClassification.htm", params = { "classification" }, method = RequestMethod.GET)
	public ModelAndView selectClassificationForm(HttpSession session,
			@RequestParam(value = "classification", required = true) String classification) {
		LOGGER.debug("setUpForm");
		ModelAndView mav = new ModelAndView();
		ClassificationViewerModel viewerModel = new ClassificationViewerModel();
		mav.addObject(CLASSIFICATION_VIEWER_MODEL_KEY, viewerModel);
		Collection<ContextIdentifier> contextIdentifiers = lookupService.findBaseContextIdentifiers(classification);
		mav.addObject(CONTEXT_IDENTIFIERS_MODEL_KEY, contextIdentifiers);
		mav.setViewName(SELECT_CLASSIFICATION_VIEW);
		return mav;
	}

	@Autowired
	public void setContextProvider(ContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	@Autowired
	public void setPropertyManager(PropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}

	@Autowired
	public void setTransformationServiceQualifierlist(
			TransformQualifierlistService transformationServiceQualifierlist) {
		this.transformationServiceQualifierlist = transformationServiceQualifierlist;
	}

	@Autowired
	public void setViewService(ViewService viewService) {
		this.viewService = viewService;
	}

}
