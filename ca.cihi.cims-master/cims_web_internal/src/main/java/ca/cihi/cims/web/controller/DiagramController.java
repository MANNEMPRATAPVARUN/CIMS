package ca.cihi.cims.web.controller;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.shared.Diagram;
import ca.cihi.cims.dal.CommonElementOperations;
import ca.cihi.cims.dal.ConceptVersion;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ContextOperations;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.dal.NonContextOperations;
import ca.cihi.cims.model.ClassificationDiagram;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.service.DisplayTagUtilService;
import ca.cihi.cims.web.bean.ValidationResponse;

@Controller
@SessionAttributes( { "vcDefault", "baseClassification" })
@RequestMapping("/diagrams")
public class DiagramController {

	@Autowired
	private ContextProvider contextProvider;

	@Autowired
	private ElementOperations elementOperations;

	@Autowired
	private ContextOperations operations;

	@Autowired
	private NonContextOperations nonContextOperations;

	@Autowired
	private CommonElementOperations commonOperations;

	@Autowired
	private AdminService adminService;

	@Autowired
	private DisplayTagUtilService dtService;

	protected final Log LOGGER = LogFactory.getLog(getClass());
	protected final String defaultBaseClassification = "ICD-10-CA"; // Both classifications have the same years, so this
	// is redundant

	public static final String PATH_PREFIX = "/admin";
	public static final String LIST_DIAGRAMS = PATH_PREFIX + "/listDiagrams";

	public static final int pageSize = 50;

	protected static final String MODEL_KEY_VERSION_CODES = "versionCodes";
	protected static final String MODEL_KEY_VERSION_CODES_OPEN = "versionCodesOpen";
	protected static final String MODEL_KEY_PAGE_SIZE = "pageSize";
	protected static final String MODEL_KEY_BASE_CLASSIFICATIONS = "baseClassifications";
	protected static final String MODEL_KEY_VERSION_CODE_DEFAULT = "vcDefault";
	protected static final String MODEL_KEY_BASE_CLASSIFICATION = "baseClassification";
	protected static final String MODEL_KEY_RESULT_SIZE = "resultSize";

	/*
	 * CREATE
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody
	ValidationResponse create(HttpSession session, ModelMap model, @Valid ClassificationDiagram cm, BindingResult result) {
		String year = (String) session.getAttribute(MODEL_KEY_VERSION_CODE_DEFAULT);
		String baseClassification = (String) session.getAttribute(MODEL_KEY_BASE_CLASSIFICATION);
		List<FieldError> errorList = new ArrayList<FieldError>();

		MultipartFile file = null;
		if (cm.getDiagramFile() != null) {
			file = cm.getDiagramFile();
		}

		// Check that the file is a Gif
		if (file != null) {
			if (!"image/gif".equalsIgnoreCase(file.getContentType())) {
				errorList.add(new FieldError("", "", "Only .gif images may be uploaded."));
			}
		}

		// Check filename uniqueness in a classification
		if (!isDiagramFileNameUnique(cm.getFileName(), baseClassification, year)) {
			errorList.add(new FieldError("", "", "Please select an image file with a unique filename."));
		}

		if (result.hasErrors()) {
			errorList.addAll(result.getFieldErrors());
		}

		ValidationResponse res = new ValidationResponse();

		if (errorList.size() > 0) {
			res.setStatus(ValidationResponse.Status.FAIL.name());
			res.setErrorMessageList(errorList);
		} else {
			ContextDefinition cd = ContextDefinition.forVersion(baseClassification, year);
			// Fix this method to accept context definitions
			ContextAccess baseContext = contextProvider.findContext(cd);
			ContextAccess context = baseContext.createChangeContext(null);

			try {
				Diagram diagram = Diagram.create(context);

				diagram.setDiagramFileName(cm.getFileName());
				diagram.setDiagramDescription(cm.getDescription());
				diagram.setStatus(cm.getStatus());

				if (file != null) {
					diagram.setDiagramFigure(file.getBytes());
				}

				context.persist();
				context.realizeChangeContext(true);

				res.setStatus(ValidationResponse.Status.SUCCESS.name());
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				res.setStatus(ValidationResponse.Status.FAIL.name());

				errorList.add(new FieldError("", "Diagram ", "Diagram update has errors. " + e.getLocalizedMessage()));

				res.setErrorMessageList(errorList);
			}
		}
		return res;
	}

	/*
	 * DELETE
	 */
	@RequestMapping(value = "/{diagramId}.htm", method = RequestMethod.DELETE)
	public @ResponseBody
	ValidationResponse delete(HttpSession session, @PathVariable("diagramId") Long diagramId) {
		String year = (String) session.getAttribute(MODEL_KEY_VERSION_CODE_DEFAULT);
		String baseClassification = (String) session.getAttribute(MODEL_KEY_BASE_CLASSIFICATION);
		List<FieldError> errorList = new ArrayList<FieldError>();

		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, year);
		// Fix this method to accept context definitions
		ContextAccess baseContext = contextProvider.findContext(cd);
		ContextAccess context = baseContext.createChangeContext(null);

		LOGGER.debug("trying to remove " + diagramId);
		ValidationResponse res = new ValidationResponse();

		// Load the element
		ElementVersion ev = elementOperations.loadElement(context.getContextId(), diagramId);
		ConceptVersion conceptToRemove = (ConceptVersion) ev;

		boolean isElligible = commonOperations.isConceptEligibleForRemoval(context.getContextId(), conceptToRemove);
		boolean hasConceptBeenPublished = operations.hasConceptBeenPublished(diagramId);

		if (!isElligible) {
			errorList.add(new FieldError("", "", "Diagram is not eligible to be removed."));
		}

		if (hasConceptBeenPublished) {
			errorList.add(new FieldError("", "", "Diagram has been published before."));
		}

		if (errorList.size() > 0) {
			res.setStatus(ValidationResponse.Status.FAIL.name());
			res.setErrorMessageList(errorList);
		} else {
			nonContextOperations.remove(context.getContextId(), diagramId);
			res.setStatus(ValidationResponse.Status.SUCCESS.name());
			context.persist();
			context.realizeChangeContext(true);
		}
		return res;
	}

	@ModelAttribute(MODEL_KEY_BASE_CLASSIFICATIONS)
	public List<String> getBaseClassifications() {
		List<String> bc = new ArrayList<String>();
		bc.add("ICD-10-CA");
		bc.add("CCI");

		return bc;
	}

	@ModelAttribute(MODEL_KEY_PAGE_SIZE)
	public int getPageSize() {
		return pageSize;
	}

	@ModelAttribute(MODEL_KEY_VERSION_CODES)
	public List<String> getVersionCodes(ModelMap model) {
		List<String> versionCodes = new ArrayList<String>();
		Map<String, Boolean> versionCodesOpen = new HashMap<String, Boolean>();
		String defaultVersionCode = null;

		Collection<ContextIdentifier> contextIdentifiers = contextProvider
				.findBaseClassificationVersionYearVersionCodes(defaultBaseClassification);

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

		model.addAttribute(MODEL_KEY_VERSION_CODES_OPEN, versionCodesOpen);

		Collections.sort(versionCodes);
		Collections.reverse(versionCodes);

		return versionCodes;
	}

	private boolean isDiagramFileNameUnique(String fileName, String baseClassification, String year) {
		if (StringUtils.isEmpty(fileName)) {
			return false;
		}

		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, year);
		ContextAccess context = contextProvider.findContext(cd);

		Ref<Diagram> diagramRef = ref(Diagram.class);
		List<Diagram> diagram = context.findList(diagramRef, diagramRef.eq("diagramFileName", fileName));

		return diagram.size() == 0;
	}

	@SuppressWarnings("unused")
	private Set<ClassificationDiagram> listAllDiagramsCCI(String year) {

		ContextDefinition cd = ContextDefinition.forVersion("CCI", year);
		ContextAccess context = contextProvider.findContext(cd);

		Iterator<Diagram> iterator = context.findAll(Diagram.class);
		Set<ClassificationDiagram> classificationDiagramSet = new HashSet<ClassificationDiagram>();

		while (iterator.hasNext()) {
			Diagram diag = iterator.next();
			classificationDiagramSet.add(ClassificationDiagram.convert(diag/* , "CCI" */));
		}

		return classificationDiagramSet;
	}

	@SuppressWarnings("unused")
	private Set<ClassificationDiagram> listAllDiagramsICD(String year) {
		ContextDefinition cd = ContextDefinition.forVersion("ICD-10-CA", year);
		ContextAccess context = contextProvider.findContext(cd);

		Iterator<Diagram> iterator = context.findAll(Diagram.class);
		Set<ClassificationDiagram> classificationDiagramSet = new HashSet<ClassificationDiagram>();

		while (iterator.hasNext()) {
			Diagram diag = iterator.next();
			classificationDiagramSet.add(ClassificationDiagram.convert(diag/* , "ICD-10-CA" */));
		}

		return classificationDiagramSet;
	}

	/*
	 * READ
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String read(HttpServletRequest request, HttpSession session, ModelMap model,
			@RequestParam("year") final String year, @RequestParam("bc") final String baseClassification) {
		// TODO: If year is empty or bad format, we should retrieve the first year available...
		model.addAttribute(MODEL_KEY_VERSION_CODE_DEFAULT, year);
		model.addAttribute(MODEL_KEY_BASE_CLASSIFICATION, baseClassification);

		List<ClassificationDiagram> diagrams = adminService.getDiagrams(year, baseClassification);
		model.addAttribute("diagrams", diagrams);

		model.addAllAttributes(dtService.addForPageLinks(request, "diagramsTable"));

		return LIST_DIAGRAMS;
	}

	/*
	 * READ Graphic
	 */
	@RequestMapping(value = "/{diagramId}/graphic", method = RequestMethod.GET, produces = "image/gif")
	public @ResponseBody
	byte[] readGraphic(@PathVariable("diagramId") Long diagramId, HttpSession session, ModelMap model) {
		String year = (String) session.getAttribute(MODEL_KEY_VERSION_CODE_DEFAULT);
		String baseClassification = (String) session.getAttribute(MODEL_KEY_BASE_CLASSIFICATION);

		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, year);
		ContextAccess context = contextProvider.findContext(cd);

		Ref<Diagram> diagamRef = ref(Diagram.class);
		Iterator<Diagram> iterator = context.find(diagamRef, diagamRef.eq("elementId", diagramId));

		Diagram diagram = iterator.next();
		byte[] bytes = diagram.getDiagramFigure();

		return bytes;

	}

	/*
	 * UPDATE
	 */
	@RequestMapping(value = "/{diagramId}.htm", method = RequestMethod.POST)
	public @ResponseBody
	ValidationResponse update(@PathVariable("diagramId") Long diagramId, HttpSession session, ModelMap model,
			@Valid ClassificationDiagram cm, BindingResult result) {
		String year = (String) session.getAttribute(MODEL_KEY_VERSION_CODE_DEFAULT);
		String baseClassification = (String) session.getAttribute(MODEL_KEY_BASE_CLASSIFICATION);
		ValidationResponse res = new ValidationResponse();
		List<FieldError> errorList = new ArrayList<FieldError>();
		MultipartFile file = null;

		if (cm.getDiagramFile() != null) {
			file = cm.getDiagramFile();
		}

		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, year);
		ContextAccess baseContext = contextProvider.findContext(cd);

		// Load the element
		ElementVersion ev = elementOperations.loadElement(baseContext.getContextId(), diagramId);
		ConceptVersion concept = (ConceptVersion) ev;

		boolean doesConceptHaveRangeLinks = commonOperations.doesConceptHasRangeLinks(concept);

		if (doesConceptHaveRangeLinks && cm.getStatus().equalsIgnoreCase(ConceptStatus.DISABLED.name())) {
			errorList.add(new FieldError("", "", "Diagram is not eligible to be removed."));
		}

		if (result.hasErrors()) {
			errorList.addAll(result.getFieldErrors());
		}

		if (errorList.size() > 0) {
			res.setStatus(ValidationResponse.Status.FAIL.name());
			res.setErrorMessageList(result.getFieldErrors());
		} else {
			ContextAccess context = baseContext.createChangeContext(null);

			try {
				Ref<Diagram> diagramRef = ref(Diagram.class);
				Diagram diagram = context.findOne(diagramRef, diagramRef.eq("elementId", diagramId));

				diagram.setDiagramDescription(cm.getDescription());
				diagram.setStatus(cm.getStatus());

				if (file != null) {
					diagram.setDiagramFigure(file.getBytes());
				}

				context.persist();
				context.realizeChangeContext(true);

				res.setStatus(ValidationResponse.Status.SUCCESS.name());
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				res.setStatus(ValidationResponse.Status.FAIL.name());

				errorList.add(new FieldError("Diagram", "Diagram ", "Diagram update has errors. "
						+ e.getLocalizedMessage()));
				res.setErrorMessageList(errorList);
			}
		}
		return res;
	}
}
