package ca.cihi.cims.web.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.Constants;
import ca.cihi.cims.ProcessInProgressException;
import ca.cihi.cims.dao.bean.AsotETLLog;
import ca.cihi.cims.service.ASOTService;
import ca.cihi.cims.web.bean.BasicInfoBean;

/**
 * Controller for manually ASOT data generation
 * 
 * @author TYang
 * 
 */
@Controller
@RequestMapping("/asot")
public class ASOTController extends BaseController {

	public static final String ASOT_VIEW = "/migration/asot";
	private static final Log LOGGER = LogFactory.getLog(ASOTController.class);
	private static final String VIEW_BEAN = "viewBean";

	private ASOTService asotService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView asotLanding() {
		ModelAndView mav = new ModelAndView();
		BasicInfoBean viewBean = new BasicInfoBean();
		mav.addObject(VIEW_BEAN, viewBean);
		List<String> versionYears = asotService.findVersionYears();
		mav.addObject("versionYears", versionYears);
		mav.setViewName(ASOT_VIEW);
		return mav;
	}

	@RequestMapping(value = "/status", method = RequestMethod.GET)
	public @ResponseBody
	String asotStatus(@RequestParam(value = "fiscalYear") String fiscalYear) {
		AsotETLLog log = asotService.getLatestETLLog(fiscalYear);
		return log.getAsotETLLog();
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView generateASOT(final @RequestParam(Constants.FISCAL_YEAR) String fiscalYear) {
		LdapUserDetails user = (LdapUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String email = user.getUsername() + "@cihi.ca";

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Start ASOT ...");
		}
		ModelAndView mav = new ModelAndView();
		mav.setViewName(ASOT_VIEW);
		try {
			getAsotService().generateASOT(fiscalYear, email);
			mav.addObject("message", "The ASOT tables for " + fiscalYear + " has been successfully generated.");
			mav.addObject("generateTablesSuccess", Boolean.TRUE);
		} catch (Exception e) {
			LOGGER.error("Generate ASOT table throws exception: ", e);
			mav.addObject("exception", e);
			mav.addObject("generateTablesFail", Boolean.TRUE);
		}
		BasicInfoBean viewBean = new BasicInfoBean();
		viewBean.setFiscalYear(fiscalYear);
		mav.addObject(VIEW_BEAN, viewBean);
		List<String> versionYears = asotService.findVersionYears();
		mav.addObject("versionYears", versionYears);
		return mav;
	}

	public ASOTService getAsotService() {
		return asotService;
	}

	@ExceptionHandler(ProcessInProgressException.class)
	public ModelAndView handleProcessInProgress(ProcessInProgressException e) {
		ModelAndView mav = new ModelAndView();
		BasicInfoBean viewBean = new BasicInfoBean();
		mav.addObject(VIEW_BEAN, viewBean);
		mav.setViewName(ASOT_VIEW);
		List<String> versionYears = asotService.findVersionYears();
		mav.addObject("versionYears", versionYears);
		mav.addObject("exception", e);
		mav.addObject("processInProgress", Boolean.TRUE);
		return mav;
	}

	@Autowired
	public void setAsotService(ASOTService asotService) {
		this.asotService = asotService;
	}
}
