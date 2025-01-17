package ca.cihi.cims.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import ca.cihi.cims.service.TransformCCIComponentService;
import ca.cihi.cims.web.WebConstants;
import ca.cihi.cims.web.bean.BasicInfoBean;

@Controller
public class CciComponentTransformReportController extends BaseController {

	private static final Log LOGGER = LogFactory.getLog(CciComponentTransformReportController.class);

	// KEYS
	private static final String VIEW_BEAN = "viewBean";
	private static final String ERROR_LIST = "errorList";

	// Views
	private static final String NAV_DATA_TRANSFORMATION = "redirect:/cciComponentDataTransformation.htm";
	private static final String TRANSFORMATION_REPORT_VIEW = "/migration/cciComponentTransformationReport";

	// -------------------------------------
	private TransformCCIComponentService transformService;
	private BaseTransformationService baseTransformService;

	@RequestMapping(value = "/cciComponentTransformation.htm", params = "back", method = RequestMethod.POST)
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

	public TransformCCIComponentService getTransformService() {
		return transformService;
	}

	@Autowired
	public void setBaseTransformService(BaseTransformationService baseTransformService) {
		this.baseTransformService = baseTransformService;
	}

	@Autowired
	public void setTransformService(TransformCCIComponentService transformService) {
		this.transformService = transformService;
	}

	@RequestMapping(value = "/cciComponentTransformation.htm", method = RequestMethod.GET)
	public ModelAndView setupForm(final Model model,
			final @RequestParam(Constants.CLASSIFICATION) String classification,
			final @RequestParam(Constants.FISCAL_YEAR) String fiscalYear,
			final @RequestParam(WebConstants.ACTION) String action, final HttpServletRequest request,
			final HttpSession session) {
		LOGGER.debug("Transformation.setupForm");

		ModelAndView mav = new ModelAndView();
		BasicInfoBean viewBean = new BasicInfoBean();
		viewBean.setClassification(classification);
		viewBean.setFiscalYear(fiscalYear);

		List<TransformationError> errors;
		if (WebConstants.ACTION_VIEW_REPORT.equals(action)) {
			errors = transformService.getAllErrors(fiscalYear, classification);
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
