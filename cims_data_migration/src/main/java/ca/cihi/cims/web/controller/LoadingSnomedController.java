package ca.cihi.cims.web.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.Constants;
import ca.cihi.cims.model.snomed.ETLLog;
import ca.cihi.cims.model.snomed.SCTBase;
import ca.cihi.cims.model.snomed.SCTConcept;
import ca.cihi.cims.model.snomed.SCTDesc;
import ca.cihi.cims.model.snomed.SCTRefsetLang;
import ca.cihi.cims.model.snomed.SCTRelationship;
import ca.cihi.cims.model.snomed.SCTVersion;
import ca.cihi.cims.service.PropertyService;
import ca.cihi.cims.service.SnomedService;
import ca.cihi.cims.web.bean.LoadingSnomedViewBean;

/**
 * Controller for load SNOMED CT files
 * 
 * @author LZhu
 *
 */
@Controller
public class LoadingSnomedController {

	private static final Log LOGGER = LogFactory.getLog(LoadingSnomedController.class);
	public static final String SNOMED_VIEW = "/migration/snomed";
	public static final String SNOMED_STATUS_VIEW_PAGE = "/snomedStatus.htm?sctVersion=";
	private static final String VIEW_BEAN = "viewBean";
	
	private static final String SNOMED_FILE_DELIMITER = "\t";
	
	private static final String SNOMED_FILE_DECODER = "UTF-8";
	
	private static final String  IS_LOADING= "Loading";
	
	private static final String  PRE_STATUS_COMPLETE= "Complete";
	                                       
	@Autowired
	private SnomedService snomedService;
	@Autowired
	private PropertyService propertyService;

	public PropertyService getPropertyService() {
		return propertyService;
	}

	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	public SnomedService getSnomedService() {
		return snomedService;
	}

	public void setSnomedService(SnomedService snomedService) {
		this.snomedService = snomedService;
	}

	@RequestMapping(value="/snomed.htm", method = RequestMethod.GET)
	public ModelAndView setupForm() throws Exception {
		ModelAndView mav = new ModelAndView();
		LoadingSnomedViewBean viewBean = new LoadingSnomedViewBean();
		mav.addObject(VIEW_BEAN, viewBean);
		mav.setViewName(SNOMED_VIEW);
		mav.addObject("sctVersionsActive",snomedService.getVersionsByStatus(Constants.SCT_STATUS_CODE_ACTIVE));
		mav.addObject("sctVersionsPending",snomedService.getVersionsByStatus(Constants.SCT_STATUS_CODE_PENDING));
	
		return mav;
	}
	
	
	@RequestMapping(value = "snomedLoadingStatus.htm", method = RequestMethod.GET)
	public @ResponseBody
	String snomedStatus(@RequestParam(value = "sctVersionCode") String sctVersionCode) throws Exception {
		LOGGER.debug("in loadingSnomedController status sctVersionCode="+sctVersionCode);
		ETLLog log = snomedService.getLatestETLLog(sctVersionCode);
		if (log!=null && log.getMessage()!=null){
			//LOGGER.debug("log.getMessage()="+log.getMessage());
			if (log.getMessage().equals(PRE_STATUS_COMPLETE)){
				return IS_LOADING;
			}
			return log.getMessage();
		}else {
			return IS_LOADING;
		}
	}		
    
    
	
	@RequestMapping(value="/snomed.htm", method = RequestMethod.POST)
	public String loadFile(final Model model, @ModelAttribute(VIEW_BEAN) final LoadingSnomedViewBean viewBean) throws Exception {
		LOGGER.debug("Started loading file");
		LOGGER.debug("viewBean.getSctVersion()="+viewBean.getSctVersion());
		String sctVersionCode = viewBean.getSctVersion().split(Constants.SCT_CODE_DESC_SEPARATOR)[0];
		String sctVersionDesc = viewBean.getSctVersion().split(Constants.SCT_CODE_DESC_SEPARATOR)[1];
		LOGGER.debug("sctVersionCode="+sctVersionCode);					
		MultipartFile conceptFile = viewBean.getConceptFile();
		LOGGER.debug("conceptFile.getOriginalFilename()="+conceptFile.getOriginalFilename());
		MultipartFile descFile = viewBean.getDescFile();
		MultipartFile relationshipFile = viewBean.getRelationshipFile();
		MultipartFile refsetLangFile = viewBean.getRefsetLangFile();
		String snomedFilePath = propertyService.getSnomedFileDirectory();
		LOGGER.debug("snomedFilePath="+snomedFilePath);
		
		LOGGER.debug("Truncate log table");
		snomedService.truncateLogTable();
		LOGGER.debug("Insert to log table");
		snomedService.insertLog("Started loading file", sctVersionCode);		
	
		snomedService.uploadFile(conceptFile,snomedFilePath);
		snomedService.uploadFile(descFile,snomedFilePath);
		snomedService.uploadFile(relationshipFile,snomedFilePath);
		snomedService.uploadFile(refsetLangFile,snomedFilePath);
		Thread thread = new Thread(() -> startProcess(snomedFilePath,conceptFile,descFile,relationshipFile,refsetLangFile,sctVersionCode));
		thread.start();
		return "redirect:" + SNOMED_STATUS_VIEW_PAGE+viewBean.getSctVersion();	
	}				
	
	private void startProcess(String snomedFilePath, MultipartFile conceptFile,MultipartFile descFile,MultipartFile relationshipFile,MultipartFile refsetLangFile,String sctVersionCode) {
        try {
			
			LOGGER.debug("snomedFilePath="+snomedFilePath);
					
			List<String> fileList = new ArrayList<String>();
			fileList.add(snomedFilePath+conceptFile.getOriginalFilename());
			fileList.add(snomedFilePath+descFile.getOriginalFilename());
			fileList.add(snomedFilePath+relationshipFile.getOriginalFilename());
			fileList.add(snomedFilePath+refsetLangFile.getOriginalFilename());
			
			List<SCTBase> beanList = new ArrayList<SCTBase>();
			beanList.add(new SCTConcept());
			beanList.add(new SCTDesc());
			beanList.add(new SCTRelationship());
			beanList.add(new SCTRefsetLang());	
			
			snomedService.loadAll(fileList, beanList, SNOMED_FILE_DELIMITER,SNOMED_FILE_DECODER, sctVersionCode);
        } catch (Exception e) {
        	LOGGER.error("Loading SNOMED CT throws exception: ", e);
        	try {
				snomedService.insertLog("Failed in loading file", sctVersionCode);
			} catch (Exception e1) {
				LOGGER.error("Failed in insert log");
			}		
        }
	}
	
	@ExceptionHandler(Exception.class)
	public ModelAndView handleProcessInProgress(Exception exception) {
		ModelAndView mav = new ModelAndView();
		LoadingSnomedViewBean viewBean = new LoadingSnomedViewBean();
		mav.addObject(VIEW_BEAN, viewBean);
		mav.setViewName(SNOMED_VIEW);		
		mav.addObject("exception", exception);
		return mav;
	}
    
	 
}
