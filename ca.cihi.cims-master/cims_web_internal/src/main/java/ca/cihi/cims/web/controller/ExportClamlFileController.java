package ca.cihi.cims.web.controller;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
import ca.cihi.cims.model.folioclamlexport.QueryCriteria;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.folioclamlexport.ClamlOutputService;
import ca.cihi.cims.web.bean.HtmlOutputViewBean;

@Controller
public class ExportClamlFileController {
	private static final Log LOGGER = LogFactory.getLog(ExportClamlFileController.class);

	public static final String PROCESS_EXPORT_CLAML_REQUEST = "/admin/processExportClamlRequest";
	public static final String INIT_EXPORT_CLAML_REQUEST_PAGE = "/admin/initExportClamlRequestPage";
	public static final String SHOW_EXPORT_CLAML_REQUEST_PAGE = "/admin/exportClamlFile";
	public static final String EXPORT_CLAML_STATUS_PAGE = "/admin/exportClamlStatus";
	public static final String GET_EXPORT_CLAML_STATUS = "/admin/getExportClamlFileStatus";
	public static final String GET_EXPORT_CLAML_ZIPFILENAME = "/admin/getExportClamlZipFileName";
	public static final String GET_EXPORT_CLAML_DETAIL_LOG = "/admin/getExportClamlDetailLog";
	public static final String GENERATE_CLAML_FILE = "/admin/GenerateClamlFile";
	public static final String SHOW_EXPORT_CLAML_DETAIL_LOG_PAGE = "/admin/exportClamlFileDetailLog";
	public static final String GET_CONTEXT_YEARS = "/admin/ExportClamlFile/GetContextYears";
	public static final String DOWNLOAD_ZIP_FILE = "/admin/ExportClamlFile/Download";
	public static final String DEFAULT_LANGUAGE_CODE = "ENG";

	@Autowired
	private LookupService lookupService;

	@Autowired
	private ClamlOutputService clamlOutputService;
	
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


	@RequestMapping(value = SHOW_EXPORT_CLAML_DETAIL_LOG_PAGE, method = RequestMethod.GET)
	public String showDetailedLogPage(final Model model, @RequestParam("htmlOutputLogId") Long htmlOutputLogId,
			@RequestParam("classification") String classification, @RequestParam("fiscalYear") String fiscalYear,
			@RequestParam("language") String language) {
		QueryCriteria criteria = new QueryCriteria();
		criteria.setClassification(classification);
		criteria.setYear(fiscalYear);
		criteria.setLanguage(language);
		model.addAttribute("queryCriteria", criteria);

		return SHOW_EXPORT_CLAML_DETAIL_LOG_PAGE;
	}

	@RequestMapping(value = EXPORT_CLAML_STATUS_PAGE, method = RequestMethod.GET)
	public String viewAllStatuses(final Model model) {
		return EXPORT_CLAML_STATUS_PAGE;
	}

	@RequestMapping(value = GET_EXPORT_CLAML_STATUS, method = RequestMethod.GET)
	public @ResponseBody String getStatus() {
		return "ok";
	}
	
	@RequestMapping(value = GET_EXPORT_CLAML_ZIPFILENAME, method = RequestMethod.GET)
	public @ResponseBody String getZipFileName() {
		return clamlOutputService.getZipFileName();
	}
	

	@RequestMapping(value = GENERATE_CLAML_FILE, method = RequestMethod.POST)
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
			//clamlOutputService.exportToClaml(queryCriteria, currentUser);
			clamlOutputService.createNewClamlOutputLog(queryCriteria, currentUser);
            Thread thread = new Thread(() -> {
                clamlOutputService.exportToClaml(queryCriteria, currentUser);}, "Claml Generator");
			thread.start();
			String zipFullPaht = clamlOutputService.getZipFilePath(queryCriteria);
		}

		return "redirect:" + INIT_EXPORT_CLAML_REQUEST_PAGE + ".htm";
	}

	@RequestMapping(value = PROCESS_EXPORT_CLAML_REQUEST, method = RequestMethod.GET)
	public String processGenerationRequest() {
		// redirect to exportClamlFile page if there is no export process is
		// running
		// otherwise redirect to the exportClamlFileStatus page to display the
		// status of current export process
		if (canStartNewGeneration()) {
			return "redirect:" + INIT_EXPORT_CLAML_REQUEST_PAGE + ".htm";
		} else {
			return "redirect:" + EXPORT_CLAML_STATUS_PAGE + ".htm";
		}
	}

    private boolean canStartNewGeneration() {
        return true;

    }
	
	
	@RequestMapping(value = INIT_EXPORT_CLAML_REQUEST_PAGE, method = RequestMethod.GET)
	public ModelAndView setupForm(final HttpServletRequest request, final HttpSession session) {

		ModelAndView mav = new ModelAndView();
		HtmlOutputViewBean generateClamlCriteria = new HtmlOutputViewBean();
		List<String> baseClassifications = getBaseClassifications();
		final List<String> contextYears = new ArrayList<>();

		// get the first item in the list as default value
		baseClassifications.stream().findFirst().ifPresent(defaultClassification -> {
			generateClamlCriteria.setBaseClassification(defaultClassification);
			contextYears.clear();
			contextYears.addAll(findContextYears(defaultClassification));
		});

		contextYears.stream().findFirst().ifPresent(firstContext -> generateClamlCriteria.setFiscalYear(firstContext));

		generateClamlCriteria.setLanguage(DEFAULT_LANGUAGE_CODE);

		mav.setViewName(SHOW_EXPORT_CLAML_REQUEST_PAGE);
		mav.addObject("generateClamlCriteria", generateClamlCriteria);
		mav.addObject("baseClassifications", baseClassifications);
		mav.addObject("contextYears", contextYears);

		return mav;
	}

	@RequestMapping(value = DOWNLOAD_ZIP_FILE, method = RequestMethod.GET)
	public void downloadZipFile(@RequestParam("zipFileName") String zipFileName, HttpServletResponse response) {
		String filePathToBeServed = clamlOutputService.getExportFolder() + zipFileName;
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
