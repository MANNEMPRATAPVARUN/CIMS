package ca.cihi.cims.web.controller.prodpub;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ca.cihi.cims.FreezingStatus;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.ComponentAndAttributeElementModel;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.prodpub.GenerateFileStatus;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationRelease;
import ca.cihi.cims.model.prodpub.PublicationSnapShot;
import ca.cihi.cims.model.prodpub.ReleaseType;
import ca.cihi.cims.service.EmailService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.PublicationService;
import ca.cihi.cims.validator.PublicationValidator;

@Controller
public class ProductPublicationController {

	public static final String GENERATE_TABLES = "generateTables";
	public static final String REVIEW_TABLES = "reviewTables";
	public static final String RELEASE_TABLES = "releaseTables";
	public static final String RELEASE_HISTORY = "releaseHistory";
	public static final String RELEASE_DETAILS = "/productpublication/releaseDetails";
	public static final String RELEASE_EMAIL = "/productpublication/releaseEmail";

	public static final String UNFREEZE_SYSTEM = "unfreezeSystem";

	public static final String QA_RESULTS = "/productpublication/qaResults";
	public static final String PROCESS_NOTES = "/productpublication/processNotes";
	public static final String UNUSED_COMPONENT_ATTRIBUTE = "/productpublication/unusedComponentAttribute";

	private static final Log LOGGER = LogFactory.getLog(ProductPublicationController.class);

	@Autowired
	private LookupService lookupService;

	@Autowired
	private PublicationService publicationService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private PublicationValidator publicationValidator;

	private List<ReleaseHistoryModel> buildReleaseHistory(List<PublicationRelease> allReleases, Long currentOpenYear) {

		List<ReleaseHistoryModel> releaseHistoryList = new ArrayList<ReleaseHistoryModel>();
		String releaseHistoryKey = null; // year + icd snapshotid + cci snapshotId
		ReleaseHistoryModel releaseHistory = null;
		PublicationRelease lastRelease = null;
		boolean isLatestRelease = true;
		for (PublicationRelease publicationRelease : allReleases) {
			StringBuilder sb_currentKey = new StringBuilder();
			sb_currentKey.append(publicationRelease.getFiscalYear());
			List<PublicationSnapShot> snapShots = publicationRelease.getPublicationSnapShots();
			PublicationSnapShot idcSnapShot = null;
			PublicationSnapShot cciSnapShot = null;
			for (PublicationSnapShot snapShot : snapShots) {
				sb_currentKey.append(snapShot.getSnapShotId());
				if (snapShot.getClassification().equalsIgnoreCase("ICD")) {
					idcSnapShot = snapShot;
				} else {
					cciSnapShot = snapShot;
				}
			}
			String currentKey = sb_currentKey.toString();
			if (!currentKey.equalsIgnoreCase(releaseHistoryKey)) { // create a new ReleaseHistoryModel

				releaseHistory = new ReleaseHistoryModel();
				releaseHistory.setVersionYear(publicationRelease.getFiscalYear());
				releaseHistory.setIcdSnapShotDate(idcSnapShot.getCreatedDate());
				releaseHistory.setCciSnapShotDate(cciSnapShot.getCreatedDate());
				reflectReleaseHistoryModel(releaseHistory, publicationRelease, isLatestRelease, currentOpenYear);
				if (isLatestRelease) {
					isLatestRelease = false;
				}

				releaseHistoryList.add(releaseHistory);
				releaseHistoryKey = currentKey;
			} else { // same year and icd snapshot id ,cci snapshot id
				if (lastRelease.getReleaseType().compareTo(publicationRelease.getReleaseType()) != 0) {
					reflectReleaseHistoryModel(releaseHistory, publicationRelease, isLatestRelease, currentOpenYear);
				}
			}
			lastRelease = publicationRelease;
		}

		return releaseHistoryList;
	}

	@RequestMapping(value = "/generateClassificationTables")
	public String generateClassificationTables(HttpSession session,
			@Valid GenerateReleaseTablesCriteria generateTablesCriteria, final BindingResult result, Model model) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		// validate whether the selected classification has open change requests
		publicationValidator.validateGenerateTablesBtn(generateTablesCriteria, result);

		if (result.hasErrors()) {

			prepareModel(model, generateTablesCriteria, currentUser);
			// model.addAttribute("validationFailed", Boolean.TRUE);
			model.addAttribute("generateReleaseTablesCriteria", generateTablesCriteria);
			model.addAttribute("activeTab", "generateTables");
			return GENERATE_TABLES;
		}

		//
		boolean isAnotherProcessRunning = publicationService.isGenerateFileProcessRunning(generateTablesCriteria);
		if (!isAnotherProcessRunning) {
			try {
				publicationService.generateClassificationTables(generateTablesCriteria, currentUser, session.getId());
			} catch (Exception e) {
				emailService.sendGenerateTableFailedEmail(generateTablesCriteria, currentUser, e);
				throw new RuntimeException(e);
			}
			emailService.sendGenerateTableSuccessEmail(generateTablesCriteria, currentUser);
			model.addAttribute("generateTablesSuccess", Boolean.TRUE);
		} else {
			model.addAttribute("isAnotherProcessRunning", isAnotherProcessRunning);
		}
		prepareModel(model, generateTablesCriteria, currentUser);
		model.addAttribute("generateReleaseTablesCriteria", generateTablesCriteria);
		model.addAttribute("activeTab", "generateTables");
		model.addAttribute("cutDate", new java.util.Date());

		return GENERATE_TABLES;
	}

	@RequestMapping("/getGeneratingTablesState.htm")
	public void getGeneratingTablesState(HttpServletResponse response, HttpSession session,
			@RequestParam("classification") String classification) throws IOException {

		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.flushBuffer();
		String currentProcessingYear = publicationService.getCurrentProcessingYear();
		String sessionKey = session.getId() + classification;

		String currentProcessingClassification = publicationService.getCurrentProcessingFile(sessionKey);
		if (currentProcessingClassification != null) {
			StringBuilder sb_msg = new StringBuilder();
			sb_msg.append("The ").append(currentProcessingYear).append(" ");
			sb_msg.append(currentProcessingClassification);
			PrintWriter out = response.getWriter();
			out.write(sb_msg.toString());
			out.flush();
			out.close();
		}
	}

	/*
	 * @RequestMapping("/getReleaseTablesState.htm") public void getReleaseTablesState(HttpServletResponse response,
	 * HttpSession session) throws IOException {
	 * 
	 * response.setContentType("text/html; charset=UTF-8"); response.setCharacterEncoding("UTF-8");
	 * response.flushBuffer(); DateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	 * 
	 * String currentProcessingYear = publicationService.getCurrentProcessingYear(); String currentProcessingFile =
	 * publicationService.getCurrentProcessingFile(session.getId()); StringBuilder sb_msg = new StringBuilder();
	 * sb_msg.append("The ").append(currentProcessingYear).append(" ");
	 * sb_msg.append(currentProcessingFile).append(" "); sb_msg.append(" is currently in progress for ");
	 * sb_msg.append(sf.format(Calendar.getInstance().getTime())).append("."); PrintWriter out = response.getWriter();
	 * out.write(sb_msg.toString()); out.flush(); out.close(); }
	 */

	@InitBinder
	public void initBinder(final WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping(value = "/notifyUsersToWrapupWork")
	public String notifyUsersToWrapupWork(HttpSession session,
			@Valid GenerateReleaseTablesCriteria generateTablesCriteria, final BindingResult result, Model model) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		if (result.hasErrors()) {
			prepareModel(model, generateTablesCriteria, currentUser);
			return GENERATE_TABLES;
		}

		publicationService.notifyUsersToWrapupWork(generateTablesCriteria, currentUser);
		prepareModel(model, generateTablesCriteria, currentUser);
		model.addAttribute("generateReleaseTablesCriteria", generateTablesCriteria);
		model.addAttribute("notificationSent", Boolean.TRUE);
		model.addAttribute("activeTab", "generateTables");
		model.addAttribute("cutDate", new java.util.Date());
		return GENERATE_TABLES;
	}

	@RequestMapping(value = "/popupProcessNotes", method = RequestMethod.GET)
	public String popupProcessNotes(final Model model, @RequestParam("contextId") Long contextId, HttpSession session) {
		List<PublicationSnapShot> snapShots = publicationService.findAllSnapShotsByContextId(contextId);
		model.addAttribute("snapShots", snapShots);

		return PROCESS_NOTES;
	}

	@RequestMapping(value = "/popupQAResults", method = RequestMethod.GET)
	public String popupQAResults(final Model model, @RequestParam("contextId") Long contextId, HttpSession session) {

		// User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);

		List<PublicationSnapShot> snapShots = publicationService.findAllSnapShotsByContextId(contextId);

		PublicationSnapShot publicationSnapShot = snapShots.get(0);
		snapShots.remove(0);
		model.addAttribute("publicationSnapShot", publicationSnapShot);
		model.addAttribute("snapShots", snapShots);

		return QA_RESULTS;
	}

	@RequestMapping(value = "/popupReleaseDetails", method = RequestMethod.GET)
	public String popupReleaseDetails(final Model model, @RequestParam("releaseId") Long releaseId, HttpSession session) {

		// User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);

		PublicationRelease publicationRelease = publicationService.findPublicationReleaseById(releaseId);
		model.addAttribute("publicationRelease", publicationRelease);

		return RELEASE_DETAILS;
	}

	@RequestMapping(value = "/popupReleaseEmail", method = RequestMethod.GET)
	public String popupReleaseEmail(final Model model, @RequestParam("releaseId") Long releaseId, HttpSession session) {

		// User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);

		PublicationRelease publicationRelease = publicationService
				.findPublicationReleaseAndReleaseMsgTmpById(releaseId);

		model.addAttribute("publicationRelease", publicationRelease);

		return RELEASE_EMAIL;
	}

	@RequestMapping(value = "/popupUnusedComponentAttributesReport", method = RequestMethod.GET)
	public String popupUnusedComponentAttributesReport(final Model model, HttpSession session) {

		// User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		Long cciCurrentOpenYear = lookupService.findCCICurrentOpenYear();
		Long cciLastClosedYear = cciCurrentOpenYear - 1;
		ContextIdentifier cciCurrentOpenBaseContext = lookupService.findBaseContextIdentifierByClassificationAndYear(
				"CCI", String.valueOf(cciCurrentOpenYear));
		ContextIdentifier cciLastClosedBaseContext = lookupService.findBaseContextIdentifierByClassificationAndYear(
				"CCI", String.valueOf(cciLastClosedYear));

		List<ComponentAndAttributeElementModel> unusedComponents = publicationService.findUnusedComponentElements(
				cciCurrentOpenBaseContext.getContextId(), cciLastClosedBaseContext.getContextId());

		List<ComponentAndAttributeElementModel> unusedGenericAttributes = publicationService
				.findUnusedGenericAttributes(cciCurrentOpenBaseContext.getContextId(),
						cciLastClosedBaseContext.getContextId());

		List<ComponentAndAttributeElementModel> unusedReferenceValues = publicationService.findUnusedReferenceValues(
				cciCurrentOpenBaseContext.getContextId(), cciLastClosedBaseContext.getContextId());

		model.addAttribute("unusedComponents", unusedComponents);
		model.addAttribute("unusedGenericAttributes", unusedGenericAttributes);
		model.addAttribute("unusedReferenceValues", unusedReferenceValues);

		return UNUSED_COMPONENT_ATTRIBUTE;
	}

	private void prepareModel(Model model, GenerateReleaseTablesCriteria generateTablesCriteria, User currentUser) {
		// cciCurrentOpenYear and icdCurrentOpenYear are always same
		Long cciCurrentOpenYear = lookupService.findCCICurrentOpenYear();
		Long icdCurrentOpenYear = lookupService.findICDCurrentOpenYear();

		ContextIdentifier icdBaseContext = lookupService.findBaseContextIdentifierByClassificationAndYear("ICD-10-CA",
				String.valueOf(icdCurrentOpenYear));
		FreezingStatus icdFreezingStatus = icdBaseContext.getFreezingStatus();
		ContextIdentifier cciBaseContext = lookupService.findBaseContextIdentifierByClassificationAndYear("CCI",
				String.valueOf(cciCurrentOpenYear));
		FreezingStatus cciFreezingStatus = cciBaseContext.getFreezingStatus();

		PublicationSnapShot latestICDSnapShot = publicationService.findLatestSnapShotByContextId(icdBaseContext
				.getContextId());
		PublicationSnapShot latestCCISnapShot = publicationService.findLatestSnapShotByContextId(cciBaseContext
				.getContextId());

		if (cciCurrentOpenYear.longValue() == icdCurrentOpenYear.longValue()) {
			generateTablesCriteria.setCurrentOpenYear(cciCurrentOpenYear);
			model.addAttribute("currentOpenYear", cciCurrentOpenYear);
			model.addAttribute("icdBaseContext", icdBaseContext);
		} else {
			model.addAttribute("currentOpenYearIsDifferent", Boolean.TRUE);
		}
		PublicationRelease latestPublicationRelease = publicationService
				.findLatestPublicationReleaseByFiscalYear(String.valueOf(cciCurrentOpenYear));
		if (latestPublicationRelease != null && GenerateFileStatus.I == latestPublicationRelease.getStatus()) {
			model.addAttribute("releaseInProgress", Boolean.TRUE);
		}

		model.addAttribute("icdFreezingStatus", icdFreezingStatus);
		model.addAttribute("cciFreezingStatus", cciFreezingStatus);
		model.addAttribute("latestICDSnapShot", latestICDSnapShot);
		model.addAttribute("latestCCISnapShot", latestCCISnapShot);

		model.addAttribute("cutDate", new java.util.Date());
		// model.addAttribute("generateTablesCriteria", generateTablesCriteria);
	}

	private void reflectReleaseHistoryModel(ReleaseHistoryModel releaseHistory, PublicationRelease publicationRelease,
			boolean isLatestRelease, Long currentOpenYear) {
		boolean isReleaseForCurrentYear = false;
		if (currentOpenYear.longValue() == Long.valueOf(publicationRelease.getFiscalYear())) {
			isReleaseForCurrentYear = true;
		}

		if (publicationRelease.getReleaseType().compareTo(ReleaseType.PRELIMINARY_INTERNAL_QA) == 0) {
			releaseHistory.setPreliminaryInternalQAReleaseId(publicationRelease.getReleaseId());
			releaseHistory.setPreliminaryInternalQARelease(publicationRelease.getReleaseNum());
			if (isReleaseForCurrentYear && isLatestRelease && !publicationRelease.isNotificationSent()) {
				releaseHistory.setEmailPreliminaryInternalQARelease(true);
			}
		}
		if (publicationRelease.getReleaseType().compareTo(ReleaseType.PRELIMINARY) == 0) {
			releaseHistory.setPreliminaryReleaseId(publicationRelease.getReleaseId());
			releaseHistory.setPreliminaryRelease(publicationRelease.getReleaseNum());
			if (isReleaseForCurrentYear && isLatestRelease && !publicationRelease.isNotificationSent()) {
				releaseHistory.setEmailPreliminaryRelease(true);
			}
		}
		if (publicationRelease.getReleaseType().compareTo(ReleaseType.OFFICIAL_INTERNAL_QA) == 0) {
			releaseHistory.setOfficialInternalQAReleaseId(publicationRelease.getReleaseId());
			releaseHistory.setOfficialInternalQARelease(publicationRelease.getReleaseNum());
			if (isReleaseForCurrentYear && isLatestRelease && !publicationRelease.isNotificationSent()) {
				releaseHistory.setEmailOfficialInternalQARelease(true);
			}
		}
		if (publicationRelease.getReleaseType().compareTo(ReleaseType.OFFICIAL) == 0) {
			releaseHistory.setOfficialReleaseId(publicationRelease.getReleaseId());
			releaseHistory.setOfficialRelease(publicationRelease.getReleaseNum());
			if (isReleaseForCurrentYear && isLatestRelease && !publicationRelease.isNotificationSent()) {
				releaseHistory.setEmailOfficialRelease(true);
			}
		}
	}

	@RequestMapping(value = "/releaseClassificationTables")
	public String releaseClassificationTables(HttpSession session,
			@Valid GenerateReleaseTablesCriteria generateReleaseTablesCriteria, final BindingResult result, Model model) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		publicationValidator.validateReleaseBtn(generateReleaseTablesCriteria, result);
		if (result.hasErrors()) {
			prepareModel(model, generateReleaseTablesCriteria, currentUser);
			PublicationRelease latestPublicationRelease = publicationService
					.findLatestHighestSuccessPublicationReleaseByFiscalYear(String
							.valueOf(generateReleaseTablesCriteria.getCurrentOpenYear()));
			model.addAttribute("generateReleaseTablesCriteria", generateReleaseTablesCriteria);
			model.addAttribute("latestPublicationRelease", latestPublicationRelease);

			model.addAttribute("activeTab", "releaseTables");
			return RELEASE_TABLES;
		}
		if (!publicationService.areBothClassificationFixedWidthFilesGenerated(generateReleaseTablesCriteria)) {
			model.addAttribute("needFixedWidthFiles", Boolean.TRUE);
		} else {
			try {
				publicationService.releaseClassificationTables(generateReleaseTablesCriteria, currentUser,
						session.getId());
				model.addAttribute("releaseTableSuccess", Boolean.TRUE);
				emailService.sendReleaseTableSuccessEmail(generateReleaseTablesCriteria, currentUser);
			} catch (Exception e) {
				LOGGER.error(e);
				emailService.sendReleaseTableFailedEmail(generateReleaseTablesCriteria, currentUser, e);
				model.addAttribute("releaseTableFailed", Boolean.TRUE);
			}

		}

		return showReleaseTablesPage(model, session);
	}

	@RequestMapping(value = "/sendReleaseEmailNotification")
	public String sendReleaseEmailNotification(HttpSession session, PublicationRelease publicationRelease,
			final BindingResult result, Model model) {
		publicationService.sendReleaseEmailNotification(publicationRelease);
		model.addAttribute("publicationRelease", publicationRelease);
		model.addAttribute("emailSent", Boolean.TRUE);

		return RELEASE_EMAIL;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	public void setPublicationService(PublicationService publicationService) {
		this.publicationService = publicationService;
	}

	public void setPublicationValidator(PublicationValidator publicationValidator) {
		this.publicationValidator = publicationValidator;
	}

	@RequestMapping(value = "/showGenerateClassificationTables", method = RequestMethod.GET)
	public String showGenerateClassificationTablesPage(final Model model, HttpSession session) {
		GenerateReleaseTablesCriteria generateReleaseTablesCriteria = new GenerateReleaseTablesCriteria();
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		prepareModel(model, generateReleaseTablesCriteria, currentUser);
		model.addAttribute("generateReleaseTablesCriteria", generateReleaseTablesCriteria);
		model.addAttribute("activeTab", "generateTables");
		model.addAttribute("cutDate", new java.util.Date());
		return GENERATE_TABLES;
	}

	@RequestMapping(value = "/showReleaseHistory", method = RequestMethod.GET)
	public String showReleaseHistoryPage(final Model model, HttpSession session) {
		Long cciCurrentOpenYear = lookupService.findCCICurrentOpenYear();

		List<PublicationRelease> allReleases = publicationService.findAllReleases();
		List<ReleaseHistoryModel> allReleaseHistory = buildReleaseHistory(allReleases, cciCurrentOpenYear);
		model.addAttribute("activeTab", "releaseHistory");
		model.addAttribute("allReleaseHistory", allReleaseHistory);

		return RELEASE_HISTORY;
	}

	@RequestMapping(value = "/showReleaseTables", method = RequestMethod.GET)
	public String showReleaseTablesPage(final Model model, HttpSession session) {

		GenerateReleaseTablesCriteria generateReleaseTablesCriteria = new GenerateReleaseTablesCriteria();
		generateReleaseTablesCriteria.setClassification(GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_BOTH);

		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		prepareModel(model, generateReleaseTablesCriteria, currentUser);

		PublicationRelease latestPublicationRelease = publicationService
				.findLatestHighestSuccessPublicationReleaseByFiscalYear(String.valueOf(generateReleaseTablesCriteria
						.getCurrentOpenYear()));
		model.addAttribute("generateReleaseTablesCriteria", generateReleaseTablesCriteria);
		model.addAttribute("latestPublicationRelease", latestPublicationRelease);

		model.addAttribute("activeTab", "releaseTables");

		return RELEASE_TABLES;
	}

	@RequestMapping(value = "/showReviewTables", method = RequestMethod.GET)
	public String showReviewTables(final Model model, HttpSession session) {
		List<PublicationSnapShot> allLatestSnapShots = publicationService.findAllSuccessLatestSnapShots();
		model.addAttribute("activeTab", "reviewTables");
		model.addAttribute("allLatestSnapShots", allLatestSnapShots);
		return REVIEW_TABLES;
	}

	@RequestMapping(value = "/showUnfreezeSystem", method = RequestMethod.GET)
	public String showUnfreezeSystem(final Model model, HttpSession session) {

		GenerateReleaseTablesCriteria generateReleaseTablesCriteria = new GenerateReleaseTablesCriteria();
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);

		prepareModel(model, generateReleaseTablesCriteria, currentUser);

		PublicationRelease latestPublicationRelease = publicationService
				.findLatestPublicationReleaseByFiscalYear(String.valueOf(generateReleaseTablesCriteria
						.getCurrentOpenYear()));
		boolean releaseInProgress = false;
		if (latestPublicationRelease != null && GenerateFileStatus.I == latestPublicationRelease.getStatus()) {
			// release in progress
			releaseInProgress = true;
		}
		model.addAttribute("releaseInProgress", releaseInProgress);
		model.addAttribute("activeTab", "unfreezeSystem");
		model.addAttribute("generateReleaseTablesCriteria", generateReleaseTablesCriteria);
		return UNFREEZE_SYSTEM;
	}

	@RequestMapping(value = "/unfreezeCCI", method = RequestMethod.POST)
	public String unfreezeCCI(final Model model, GenerateReleaseTablesCriteria generateReleaseTablesCriteria,
			final BindingResult result, HttpSession session) {
		// publicationService.
		publicationValidator.validateUnfreezeCCIBtn(generateReleaseTablesCriteria, result);
		if (result.hasErrors()) {
			model.addAttribute("activeTab", "unfreezeSystem");
			return UNFREEZE_SYSTEM;
		}

		ContextIdentifier cciBaseContextIdentifier = lookupService.findBaseContextIdentifierByClassificationAndYear(
				"CCI", String.valueOf(generateReleaseTablesCriteria.getCurrentOpenYear()));
		publicationService.unfreezeTabularChanges(cciBaseContextIdentifier.getContextId());
		model.addAttribute("unfreezeCCISuccess", Boolean.TRUE);
		return showUnfreezeSystem(model, session);
	}

	@RequestMapping(value = "/unfreezeICD", method = RequestMethod.POST)
	public String unfreezeICD(final Model model, GenerateReleaseTablesCriteria generateReleaseTablesCriteria,
			final BindingResult result, HttpSession session) {
		publicationValidator.validateUnfreezeICDBtn(generateReleaseTablesCriteria, result);
		if (result.hasErrors()) {
			model.addAttribute("activeTab", "unfreezeSystem");
			return UNFREEZE_SYSTEM;
		}
		ContextIdentifier icdBaseContextIdentifier = lookupService.findBaseContextIdentifierByClassificationAndYear(
				"ICD-10-CA", String.valueOf(generateReleaseTablesCriteria.getCurrentOpenYear()));
		publicationService.unfreezeTabularChanges(icdBaseContextIdentifier.getContextId());
		model.addAttribute("unfreezeICDSuccess", Boolean.TRUE);
		return showUnfreezeSystem(model, session);
	}

	@RequestMapping(value = "/updateQAResult", method = RequestMethod.POST)
	public String updateQAResults(final Model model, PublicationSnapShot publicationSnapShot, HttpSession session) {

		// User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);

		publicationService.updatePublicationSnapShotQANote(publicationSnapShot);
		List<PublicationSnapShot> snapShots = publicationService.findAllSnapShotsByContextId(publicationSnapShot
				.getStructureId());

		publicationSnapShot = snapShots.get(0);
		snapShots.remove(0);
		model.addAttribute("publicationSnapShot", publicationSnapShot);
		model.addAttribute("snapShots", snapShots);

		return QA_RESULTS;
	}

}
