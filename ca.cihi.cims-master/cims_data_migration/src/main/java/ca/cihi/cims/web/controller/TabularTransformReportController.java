package ca.cihi.cims.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import ca.cihi.cims.Constants;
import ca.cihi.cims.model.TransformationError;
import ca.cihi.cims.service.BaseTransformationService;
import ca.cihi.cims.service.TransformationService;
import ca.cihi.cims.web.WebConstants;
import ca.cihi.cims.web.bean.BasicInfoBean;
import ca.cihi.cims.web.bean.TransformationReportViewBean;

/**
 * Controller for transformation.
 * 
 * @author wxing
 */
@Controller
public class TabularTransformReportController {

	private static final Log LOGGER = LogFactory.getLog(TabularTransformReportController.class);

	private TransformationService transformService;
	private BaseTransformationService baseTransformService;

	private static final String NAV_DATA_TRANSFORMATION = "redirect:/tabularDataTransformation.htm";

	private static final String TRANSFORMATION_REPORT_VIEW = "/migration/tabularTransformationReport";

	private static final String VIEW_BEAN = "viewBean";
	private static final String ERROR_LIST = "errorList";

	@RequestMapping(value = "/tabularTransformation.htm", params = "back", method = RequestMethod.POST)
	public ModelAndView back(final @RequestParam(Constants.CLASSIFICATION) String classification,
			final @RequestParam(Constants.FISCAL_YEAR) String fiscalYear) {
		LOGGER.debug("back to data transformation page");
		ModelAndView mav = new ModelAndView();
		BasicInfoBean viewBean = new BasicInfoBean();
		viewBean.setClassification(classification);
		viewBean.setFiscalYear(fiscalYear);
		mav.setViewName(NAV_DATA_TRANSFORMATION);
		mav.addObject(VIEW_BEAN, viewBean);

		return mav;
	}

	public BaseTransformationService getBaseTransformService() {
		return baseTransformService;
	}

	public TransformationService getTransformService() {
		return transformService;
	}

	@Autowired
	public void setBaseTransformService(BaseTransformationService baseTransformService) {
		this.baseTransformService = baseTransformService;
	}

	@Autowired
	@Qualifier("transformationService")
	public void setTransformService(TransformationService transformService) {
		this.transformService = transformService;
	}

	@RequestMapping(value = "/tabularTransformation.htm", method = RequestMethod.GET)
	public ModelAndView setupForm(final Model model,
			final @RequestParam(Constants.CLASSIFICATION) String classification,
			final @RequestParam(Constants.FISCAL_YEAR) String fiscalYear,
			final @RequestParam(WebConstants.ACTION) String action, final HttpServletRequest request,
			final HttpSession session) {
		LOGGER.debug("Transformation.setupForm");

		ModelAndView mav = new ModelAndView();
		TransformationReportViewBean viewBean = new TransformationReportViewBean();
		viewBean.setClassification(classification);
		viewBean.setFiscalYear(fiscalYear);

		List<TransformationError> errors;
		if (WebConstants.ACTION_VIEW_REPORT.equals(action)) {
			errors = baseTransformService.getAllErrors(fiscalYear, classification);
		} else {
			if (request.getParameter(WebConstants.RUN_ID) == null) {
				model.addAttribute("errorMessage", "cims.transformationReportViewer.noRunId");
				errors = new ArrayList<TransformationError>();
			} else {
				Long runId = Long.parseLong(request.getParameter(WebConstants.RUN_ID));
				errors = baseTransformService.getAllErrors(runId);
			}
		}
		WebUtils.setSessionAttribute(request, ERROR_LIST, errors);

		mav.setViewName(TRANSFORMATION_REPORT_VIEW);
		mav.addObject(VIEW_BEAN, viewBean);
		return mav;
	}

}