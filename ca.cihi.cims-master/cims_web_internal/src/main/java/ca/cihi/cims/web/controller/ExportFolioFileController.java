package ca.cihi.cims.web.controller;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.folioclamlexport.GenerationStatus;
import ca.cihi.cims.model.folioclamlexport.HtmlOutputLog;
import ca.cihi.cims.model.folioclamlexport.QueryCriteria;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.folioclamlexport.HtmlOutputService;
import ca.cihi.cims.service.folioclamlexport.HtmlOutputServiceStatus;
import ca.cihi.cims.web.bean.HtmlOutputViewBean;

@Controller
public class ExportFolioFileController {
	private static final Log LOGGER = LogFactory.getLog(ExportFolioFileController.class);

	public static final String PROCESS_EXPORT_FOLIO_REQUEST = "/admin/processExportFolioRequest";
	public static final String INIT_EXPORT_FOLIO_REQUEST_PAGE = "/admin/initExportFolioRequestPage";
	public static final String SHOW_EXPORT_FOLIO_REQUEST_PAGE = "/admin/exportFolioFile";
	public static final String EXPORT_FOLIO_STATUS_PAGE = "/admin/exportFolioFileStatus";
	public static final String GET_EXPORT_FOLIO_STATUS = "/admin/getExportFolioFileStatus";
	public static final String GET_EXPORT_FOLIO_ZIPFILENAME = "/admin/getExportFolioZipFileName";
	public static final String GET_EXPORT_FOLIO_DETAIL_LOG = "/admin/getExportFolioDetailLog";
	public static final String GENERATE_FOLIO_FILE = "/admin/GenerateFolioFile";
	public static final String SHOW_EXPORT_FOLIO_DETAIL_LOG_PAGE = "/admin/exportFolioFileDetailLog";
	public static final String GET_CONTEXT_YEARS = "/admin/ExportFolioFile/GetContextYears";
	public static final String DOWNLOAD_ZIP_FILE = "/admin/ExportFolioFile/Download";
	public static final String DEFAULT_LANGUAGE_CODE = "ENG";

	@Autowired
	private LookupService lookupService;

	@Autowired
	private HtmlOutputService htmlOutputService;


	public LookupService getLookupService() {
		return lookupService;
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	public HtmlOutputService getHtmlOutputService() {
		return htmlOutputService;
	}

	public void setHtmlOutputService(HtmlOutputService htmlOutputService) {
		this.htmlOutputService = htmlOutputService;
	}

	private List<String> findContextYears(String baseClassification) {
		List<ContextIdentifier> baseContextIdentifiers = lookupService.findBaseContextIdentifiers(baseClassification);
		return baseContextIdentifiers.stream().map(identifier -> identifier.getVersionCode()).collect(toList());
	}

	private List<String> getBaseClassifications() {
		return lookupService.findBaseClassifications();
	}

	@RequestMapping(value = GET_CONTEXT_YEARS, method = RequestMethod.GET)
	public @ResponseBody List<String> getContextYears(
			@RequestParam("baseClassification") final String baseClassification) {
		return findContextYears(baseClassification);
	}

	private GenerationStatus convertLogObj(HtmlOutputLog log) {
		HtmlOutputLog currentLogStatusObj = htmlOutputService.getCurrentLogStatusObj();
		GenerationStatus status = new GenerationStatus();
		status.setHtmlOutputLogId(log.getHtmlOutputLogId());
		status.setClassification(log.getClassificationCode());
		status.setDownloadUrl(log.getZipFileName());
		status.setGeneratedDate(log.getCreationDate());
		status.setLanguage(log.getLanguageCode());
		status.setStatus(HtmlOutputServiceStatus.forStatusCode(log.getStatusCode()).getDescription());
		status.setYear(log.getFiscalYear());
		status.setHasDetailedLog(hasDetailedLog(log.getHtmlOutputLogId()));
		if (currentLogStatusObj != null && currentLogStatusObj.getHtmlOutputLogId().equals(log.getHtmlOutputLogId())) {
			status.setLastGeneration(true);
		} else {
			status.setLastGeneration(false);
		}
		return status;
	}

	public List<String> getDetailedLog(Long htmlOutputLogId) {
		return htmlOutputService.getDetailedLog(htmlOutputLogId);
	}

	public Boolean hasDetailedLog(Long htmlOutputLogId) {
		List<String> logs = getDetailedLog(htmlOutputLogId);
		return logs != null && !logs.isEmpty();
	}

	@RequestMapping(value = GET_EXPORT_FOLIO_DETAIL_LOG, method = RequestMethod.GET)
	public @ResponseBody Map<Integer, String> getLastDetailedLog(
			@RequestParam("lastDetailedLogSeqNum") final int lastDetailedLogSeqNum) {
		List<String> detailedLogs = this.htmlOutputService.getDetailedLog();

		int size = detailedLogs.size();
		Map<Integer, String> result = new HashMap<>();
		for (int i = lastDetailedLogSeqNum + 1; i < size; i++) {
			result.put(i, detailedLogs.get(i));
		}

		return result;
	}

	@RequestMapping(value = SHOW_EXPORT_FOLIO_DETAIL_LOG_PAGE, method = RequestMethod.GET)
	public String showDetailedLogPage(final Model model, @RequestParam("htmlOutputLogId") Long htmlOutputLogId,
			@RequestParam("classification") String classification, @RequestParam("fiscalYear") String fiscalYear,
			@RequestParam("language") String language) {
		QueryCriteria criteria = new QueryCriteria();
		criteria.setClassification(classification);
		criteria.setYear(fiscalYear);
		criteria.setLanguage(language);
		model.addAttribute("queryCriteria", criteria);

		return SHOW_EXPORT_FOLIO_DETAIL_LOG_PAGE;
	}

	@RequestMapping(value = EXPORT_FOLIO_STATUS_PAGE, method = RequestMethod.GET)
	public String viewAllStatuses(final Model model) {
		List<HtmlOutputLog> htmlOutputLogs = this.htmlOutputService.getHtmlOutputLogService().getHtmlOutputLogs();

		List<GenerationStatus> allGenerationStatuses = htmlOutputLogs.stream().map(log -> convertLogObj(log))
				.collect(toList());
		model.addAttribute("allGenerationStatuses", allGenerationStatuses);
		return EXPORT_FOLIO_STATUS_PAGE;
	}

	@RequestMapping(value = GET_EXPORT_FOLIO_STATUS, method = RequestMethod.GET)
	public @ResponseBody String getStatus() {
		return htmlOutputService.getStatus().getDescription();
	}
	
	@RequestMapping(value = GET_EXPORT_FOLIO_ZIPFILENAME, method = RequestMethod.GET)
	public @ResponseBody String getZipFileName() {
		return htmlOutputService.getZipFileName();
	}
	

	@RequestMapping(value = GENERATE_FOLIO_FILE, method = RequestMethod.POST)
	public String generate(final Model model, HtmlOutputViewBean viewBean, final BindingResult result,
			final HttpSession session) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		String classification = viewBean.getBaseClassification();
		String fiscalYear = viewBean.getFiscalYear();
		String language = viewBean.getLanguage();
		long contextId = 0L;

		if (classification != null && fiscalYear != null && language != null) {
			List<ContextIdentifier> baseContextIdentifiers = lookupService.findBaseContextIdentifiers(classification);
			Optional<Long> contextIdOpiton = baseContextIdentifiers.stream()
					.filter(id -> id.getVersionCode().equals(fiscalYear)).map(id -> id.getContextId()).findFirst();
			if (contextIdOpiton.isPresent()) {
				contextId = contextIdOpiton.get();
			}
			QueryCriteria queryCriteria = new QueryCriteria();
			queryCriteria.setClassification(classification);
			queryCriteria.setContextId(contextId);
			queryCriteria.setLanguage(language);
			queryCriteria.setYear(fiscalYear);

			Thread thread = new Thread(() -> htmlOutputService.exportToHtml(queryCriteria, currentUser));
			thread.start();
		}

		return "redirect:" + EXPORT_FOLIO_STATUS_PAGE + ".htm";
	}

	private boolean canStartNewGeneration() {
		return htmlOutputService.getStatus() == HtmlOutputServiceStatus.NEW
				|| htmlOutputService.getStatus() == HtmlOutputServiceStatus.FAILURE
				|| htmlOutputService.getStatus() == HtmlOutputServiceStatus.DONE ? true : false;

	}

	@RequestMapping(value = PROCESS_EXPORT_FOLIO_REQUEST, method = RequestMethod.GET)
	public String processGenerationRequest() {
		// redirect to exportFolioFile page if there is no export process is
		// running
		// otherwise redirect to the exportFolioFileStatus page to display the
		// status of current export process
		if (canStartNewGeneration()) {
			return "redirect:" + INIT_EXPORT_FOLIO_REQUEST_PAGE + ".htm";
		} else {
			return "redirect:" + EXPORT_FOLIO_STATUS_PAGE + ".htm";
		}
	}

	@RequestMapping(value = INIT_EXPORT_FOLIO_REQUEST_PAGE, method = RequestMethod.GET)
	public ModelAndView setupForm(final HttpServletRequest request, final HttpSession session) {

		ModelAndView mav = new ModelAndView();
		HtmlOutputViewBean generateFolioCriteria = new HtmlOutputViewBean();
		List<String> baseClassifications = getBaseClassifications();
		final List<String> contextYears = new ArrayList<>();

		// get the first item in the list as default value
		baseClassifications.stream().findFirst().ifPresent(defaultClassification -> {
			generateFolioCriteria.setBaseClassification(defaultClassification);
			contextYears.clear();
			contextYears.addAll(findContextYears(defaultClassification));
		});

		contextYears.stream().findFirst().ifPresent(firstContext -> generateFolioCriteria.setFiscalYear(firstContext));

		generateFolioCriteria.setLanguage(DEFAULT_LANGUAGE_CODE);

		mav.setViewName(SHOW_EXPORT_FOLIO_REQUEST_PAGE);
		mav.addObject("generateFolioCriteria", generateFolioCriteria);
		mav.addObject("baseClassifications", baseClassifications);
		mav.addObject("contextYears", contextYears);

		return mav;
	}

	@RequestMapping(value = DOWNLOAD_ZIP_FILE, method = RequestMethod.GET)
	public void downloadZipFile(@RequestParam("zipFileName") String zipFileName, HttpServletResponse response) {
		String filePathToBeServed = htmlOutputService.getExportFolder() + zipFileName;
		File fileToDownload = new File(filePathToBeServed);
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/force-download");
			response.setHeader("Content-Disposition", "attachment; filename=" + zipFileName);
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		} catch (Exception e) {
			LOGGER.error("Downloading file: " + zipFileName + " failed:" + e.getMessage());
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					LOGGER.error("Downloading file: " + zipFileName + " failed:" + e.getMessage());
				}
			}
		}
	}

}