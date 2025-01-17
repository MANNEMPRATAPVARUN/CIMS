package ca.cihi.cims.web.controller.changerequest.legacy;

import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.validation.FieldError;

import ca.cihi.cims.model.changerequest.AttachmentFormat;
import ca.cihi.cims.model.changerequest.legacy.LegacyRequestResultsModel;
import ca.cihi.cims.model.changerequest.legacy.LegacyRequestSearchModel;
import ca.cihi.cims.model.changerequest.legacy.LegacyRequestDetailModel;
import ca.cihi.cims.model.changerequest.legacy.Language;
import ca.cihi.cims.model.changerequest.legacy.RequestStatus;
import ca.cihi.cims.model.changerequest.legacy.Section;
import ca.cihi.cims.model.changerequest.legacy.ChangeNature;
import ca.cihi.cims.model.changerequest.legacy.ChangeType;
import ca.cihi.cims.service.legacy.LegacyRequestService;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.DisplayTagUtilService;
import ca.cihi.cims.service.FileService;


/*
 * this controller deal with the legacy search page
 */
@Controller
@SessionAttributes({ "legacyRequestResults", "legacyRequestSearchModel", "resultSize" })
public class LegacyRequestController {

	private static final Log LOGGER = LogFactory.getLog(LegacyRequestController.class);

	private static final int BUFSIZE = 4096;
	private static final String CONTENT_TYPE_PDF = "application/pdf";
	private static final String CONTENT_TYPE_EXCEL = "application/vnd.ms-excel";
	private static final String CONTENT_TYPE_TXT = "text/plain";
	private static final String CONTENT_TYPE_HTML = "text/html";
	
	protected static final String LEGACY_CHANGEREQUEST_VIEW = "legacyChangeRequests";
	protected static final String LEGACY_CHANGEREQUEST_DETAIL_VIEW = "/requestmanagement/legacy/legacyChangeRequestDetail";

	//public static final int pageSize = 10;

	//@Autowired
	private LegacyRequestService legacyRequestService;
	public LegacyRequestService getLegacyRequestService() {
		return legacyRequestService;
	}
	@Autowired
	public void setLegacyRequestService(LegacyRequestService legacyRequestService) {
		this.legacyRequestService = legacyRequestService;
	}

	//@Autowired
	private DisplayTagUtilService dtService;
	public DisplayTagUtilService getDtService() {
		return dtService;
	}
	@Autowired
	public void setDtService(DisplayTagUtilService dtService) {
		this.dtService = dtService;
	}

	@Autowired
	private FileService fileService;

	/**
	 * when the user click the Search => CRD Legacy Search from menu
	 */
	@RequestMapping(value = "/legacyChangeRequests", method = RequestMethod.GET)
	public String loadLegacySearch(HttpServletRequest request, HttpSession session, ModelMap model) {

		LOGGER.info("LegacyRequestController.loadLegacySearch()> begin...");

		LegacyRequestSearchModel legacyRequestSearchModel = (LegacyRequestSearchModel) session.getAttribute("legacyRequestSearchModel");

		if (legacyRequestSearchModel == null) {
		    legacyRequestSearchModel = new LegacyRequestSearchModel();
		}
		
		model.addAttribute("legacyRequestSearchModel", legacyRequestSearchModel);
		
		populateDropDownBoxes(model);

		List<LegacyRequestResultsModel> legacyRequestResults = (List<LegacyRequestResultsModel>) session.getAttribute("legacyRequestResults");
        //int resultSize = 0;
        Integer resultSize = (Integer)session.getAttribute("resultSize");
        LOGGER.info("LegacyRequestController.loadLegacySearch()> resultSize=" + resultSize);
        
        if (legacyRequestResults != null) {
        	//resultSize = legacyRequestResults.size();
        	model.addAttribute("legacyRequestResults", legacyRequestResults);
		    model.addAllAttributes(dtService.addForPageLinks(request, "legacyRequestsTable"));
		    model.addAttribute("resultSize", resultSize);
        }
		
		return LEGACY_CHANGEREQUEST_VIEW;
	}

	/*
	 * user click search button to search for legacy change requests
	 */
	@RequestMapping(value = "/legacyChangeRequests", method = RequestMethod.POST)
	public String runLegacySearch(HttpServletRequest request, HttpSession session, @Valid LegacyRequestSearchModel legacyRequestSearchModel,
		final BindingResult result, ModelMap model) {

		LOGGER.info("LegacyRequestController.runLegacySearch()> begin...");

		populateDropDownBoxes(model);
		
		if (result.hasErrors()) {
			List<FieldError> fieldErrors = result.getFieldErrors();
			model.addAttribute("fieldErrors", fieldErrors);
   		    return LEGACY_CHANGEREQUEST_VIEW;
		}

		List<LegacyRequestResultsModel> legacyRequestResults = legacyRequestService.findLegacyChangeRequestsBySearchModel(legacyRequestSearchModel);
        //int resultSize = legacyRequestResults.size();
        Integer resultSize = legacyRequestService.findNumOfLegacyChangeRequestsBySearchModel(legacyRequestSearchModel);
        //if (resultSize > 500) resultSize = 500;
        
		model.addAttribute("legacyRequestResults", legacyRequestResults);
		model.addAllAttributes(dtService.addForPageLinks(request, "legacyRequestsTable"));
		model.addAttribute("resultSize", resultSize);


		return LEGACY_CHANGEREQUEST_VIEW;

	}

	@RequestMapping("/legacyRequestDetail.htm")
	public String loadLegacyResultDetail(final Model model, @RequestParam("requestId") Long requestId,
			HttpServletRequest request, HttpSession session) {

		LOGGER.info("LegacyRequestController.loadLegacyResultDetail()> begin: requestId=" + requestId);

		List<LegacyRequestDetailModel> legacyRequestDetails = (List<LegacyRequestDetailModel>)legacyRequestService.findLegacyChangeRequestByRequestId(requestId);
		LegacyRequestDetailModel legacyRequestDetailModel = null;
		if (legacyRequestDetails != null && legacyRequestDetails.size() == 1) {
            legacyRequestDetailModel = (LegacyRequestDetailModel)legacyRequestDetails.get(0);
		} else {
			LOGGER.info("LegacyRequestController.loadLegacyResultDetail()> something wrong: requestId=" + requestId);
		}

		List<String> legacyRequestAttachments = (List<String>)legacyRequestService.findLegacyChangeRequestAttachmentsByRequestId(requestId);
	    if (legacyRequestDetailModel != null && legacyRequestAttachments != null && legacyRequestAttachments.size() > 0) {
	    	legacyRequestDetailModel.setAttachmentFileNames(legacyRequestAttachments);
	    }

		List<String> legacyRequestQueryRefNums = (List<String>)legacyRequestService.findLegacyChangeRequestQueryRefNumsByRequestId(requestId);
	    if (legacyRequestDetailModel != null && legacyRequestQueryRefNums != null && legacyRequestQueryRefNums.size() > 0) {
	    	legacyRequestDetailModel.setQueryRefNums(legacyRequestQueryRefNums);
	    }
	    
		model.addAttribute("legacyRequestDetailModel", legacyRequestDetailModel);

		return LEGACY_CHANGEREQUEST_DETAIL_VIEW;

	}

	@RequestMapping("/downloadAttachmentFile.htm")
	public String downloadAttachmentFile( final Model model, @RequestParam("attachmentFileName") String attachmentFileName, 
			                              final HttpServletRequest request, final HttpServletResponse response) {
		
		LOGGER.info("LegacyRequestController.downloadAttachmentFile()> attachmentFileName: " + attachmentFileName);

        String errorMessage = null;
        
		DataInputStream inputStream = null;
		ServletOutputStream stream = null;
		int length = 0;

		String  fileExtension = null;
		if (attachmentFileName.lastIndexOf(".")>0){
			fileExtension = attachmentFileName.substring(attachmentFileName.lastIndexOf(".")+1);
		}
		
		try{
			stream = response.getOutputStream();

			File file = fileService.getHistFile(attachmentFileName);
			
			byte[] bbuf = new byte[BUFSIZE];
			inputStream = new DataInputStream(new FileInputStream(file));
			// set response headers
			setHeaders(response, file, attachmentFileName, fileExtension);
			while ((inputStream != null) && ((length = inputStream.read(bbuf)) != -1)) {
				stream.write(bbuf, 0, length);
			}
		}catch(IOException e){
			new RuntimeException(e);
			LOGGER.info("LegacyRequestController.downloadAttachmentFile()> RuntimeException: " + e.getMessage());
			errorMessage = "File not found.";
			model.addAttribute("errorMessage", errorMessage);
			return LEGACY_CHANGEREQUEST_DETAIL_VIEW;
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (stream != null) {
					stream.close();
					stream.flush();
				}
			} catch (IOException ioe) {
				LOGGER.error(ioe.getCause());
			}
		}

        return null;
	}


	private void populateDropDownBoxes(ModelMap model) {
		List<String> allVersionCodes = legacyRequestService.findVersionCodes();
		List<String> classificationTitleCodes = legacyRequestService.findClassificationTitleCodes();
		List<Language> languages = legacyRequestService.findLanguages();
		List<RequestStatus> dispositions = legacyRequestService.findDispositions();
		List<Section> sections = legacyRequestService.findSections();
		List<ChangeNature> changeNatures = legacyRequestService.findChangeNatures();
		List<ChangeType> changeTypes = legacyRequestService.findChangeTypes();

		model.addAttribute("allVersionCodes", allVersionCodes);
		model.addAttribute("classificationTitleCodes", classificationTitleCodes);
		model.addAttribute("languages", languages);
		model.addAttribute("dispositions", dispositions);
		model.addAttribute("sections", sections);
		model.addAttribute("changeNatures", changeNatures);
		model.addAttribute("changeTypes", changeTypes);
    	
    }
	
	private void setHeaders(
			final HttpServletResponse response,
			final File file,
			final String fileName,
			final String type) {
		response.setContentLength((int) file.length());
		response.addHeader("Content-Disposition", "attachment; filename="
				+ fileName);

		response.setContentType(getContentType(type));
	}

	private String getContentType(final String type) {
		if (AttachmentFormat.PDF_FILE.getCode().equals(type)) {
			return CONTENT_TYPE_PDF;
		} else if (AttachmentFormat.EXCEL_FILE.getCode().equals(type)) {
			return CONTENT_TYPE_EXCEL;
		} else if (AttachmentFormat.TXT_FILE.getCode().equals(type)) {
			return CONTENT_TYPE_TXT;
		}
		return CONTENT_TYPE_HTML;
	}
	
}
