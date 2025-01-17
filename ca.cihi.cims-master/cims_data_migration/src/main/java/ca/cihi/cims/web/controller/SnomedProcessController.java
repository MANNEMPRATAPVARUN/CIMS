package ca.cihi.cims.web.controller;


import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.Constants;
import ca.cihi.cims.model.snomed.ETLLog;
import ca.cihi.cims.service.SnomedService;
import ca.cihi.cims.web.bean.LoadingSnomedViewBean;

/**
 * Controller for load SNOMED CT files
 * 
 * @author LZhu
 *
 */
@Controller
public class SnomedProcessController {

	private static final Log LOGGER = LogFactory.getLog(SnomedProcessController.class);
	public static final String SNOMED_STATUS_VIEW = "/migration/snomedStatus";
	private static final String VIEW_BEAN = "viewBean";
	private static final String PARAMETER_SCT_VERSION =  "sctVersion";
	
	private static final String  IS_LOADING= "Loading";
	
	private static final String  PRE_STATUS_COMPLETE= "Complete";
	                                       
	@Autowired
	private SnomedService snomedService;
	
	public SnomedService getSnomedService() {
		return snomedService;
	}

	public void setSnomedService(SnomedService snomedService) {
		this.snomedService = snomedService;
	}

	@RequestMapping(value = "/snomedStatus.htm", method = RequestMethod.GET)
	public ModelAndView setupForm( final HttpServletRequest request) throws Exception {
		ModelAndView mav = new ModelAndView();
		LoadingSnomedViewBean viewBean = new LoadingSnomedViewBean();
		mav.addObject(VIEW_BEAN, viewBean);
		mav.setViewName(SNOMED_STATUS_VIEW);
		mav.addObject("sctVersionsActive",snomedService.getVersionsByStatus(Constants.SCT_STATUS_CODE_ACTIVE));
		mav.addObject("sctVersionsPending",snomedService.getVersionsByStatus(Constants.SCT_STATUS_CODE_PENDING));
		mav.addObject("sctVersionLoading",request.getParameter(PARAMETER_SCT_VERSION));
		//LOGGER.debug("sctVersion="+request.getParameter(PARAMETER_SCT_VERSION));
		return mav;
	}
	
	@RequestMapping(value = "/snomedProcessingStatus.htm", method = RequestMethod.GET)
	public @ResponseBody
	String snomedStatus(@RequestParam(value = "sctVersionCode") String sctVersionCode) throws Exception {
		LOGGER.debug("in SnomedStatusController status sctVersionCode="+sctVersionCode);
		ETLLog log = snomedService.getLatestETLLog(sctVersionCode);
		if (log!=null && log.getMessage()!=null){
			//LOGGER.debug("log.getMessage()="+log.getMessage());
		/*	if (log.getMessage().equals(PRE_STATUS_COMPLETE)){
				return IS_LOADING;
			}*/
			return log.getMessage();
		}else {
			return IS_LOADING;
		}
	}		
    
	@ExceptionHandler(Exception.class)
	public ModelAndView handleProcessInProgress(Exception exception) {
		ModelAndView mav = new ModelAndView();
		LoadingSnomedViewBean viewBean = new LoadingSnomedViewBean();
		mav.addObject(VIEW_BEAN, viewBean);
		mav.setViewName(SNOMED_STATUS_VIEW);		
		mav.addObject("exception", exception);
		return mav;
	}
    
	 
}
