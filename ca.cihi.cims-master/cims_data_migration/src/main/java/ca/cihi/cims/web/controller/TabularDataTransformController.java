package ca.cihi.cims.web.controller;

import java.util.Iterator;

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

import ca.cihi.cims.Constants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.content.shared.TabularConcept;
import ca.cihi.cims.service.BaseTransformationService;
import ca.cihi.cims.service.TransformationService;
import ca.cihi.cims.web.WebConstants;
import ca.cihi.cims.web.bean.BasicInfoBean;

/**
 * Controller for the data transformation page.
 * 
 * @author wxing
 */
@Controller
public class TabularDataTransformController extends BaseController {

	private static final Log LOGGER = LogFactory.getLog(TabularDataTransformController.class);

	private static final String DATA_TRANSFORMATION_VIEW = "/migration/tabularDataTransformation";
	private static final String NAV_TRANSFORMATION_URL = "redirect:/tabularTransformation.htm";
	private static final String VIEW_BEAN = "viewBean";

	private TransformationService transformService;
	private BaseTransformationService baseTransformService;

	public BaseTransformationService getBaseTransformService() {
		return baseTransformService;
	}

	public TransformationService getTransformService() {
		return transformService;
	}

	@RequestMapping(value = "/tabularDataTransformation.htm", method = RequestMethod.POST)
	public ModelAndView printErrorMessage(final Model model) {
		LOGGER.debug("Print Error Mesasge in transformation Form");
		ModelAndView mav = new ModelAndView();
		BasicInfoBean viewBean = new BasicInfoBean();
		mav.addObject(VIEW_BEAN, viewBean);
		mav.setViewName(DATA_TRANSFORMATION_VIEW);
		return mav;
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

	@RequestMapping(value = "/tabularDataTransformation.htm", method = RequestMethod.GET)
	public ModelAndView setupForm(final Model model) {
		LOGGER.debug("setUp transformation Form");
		ModelAndView mav = new ModelAndView();
		BasicInfoBean viewBean = new BasicInfoBean();
		mav.addObject(VIEW_BEAN, viewBean);
		mav.setViewName(DATA_TRANSFORMATION_VIEW);
		return mav;
	}

	@RequestMapping(value = "/tabularDataTransformation.htm", params = "transform", method = RequestMethod.POST)
	public ModelAndView transformData(final Model model, @RequestParam(Constants.FISCAL_YEAR) final String fiscalYear,
			@RequestParam(Constants.CLASSIFICATION) final String classification) {

		LOGGER.debug(">>>transformData");
		ModelAndView mav = new ModelAndView();
		if (baseTransformService.checkRunStatus(fiscalYear, classification)) {
			model.addAttribute(Constants.FISCAL_YEAR, fiscalYear);
			model.addAttribute(Constants.CLASSIFICATION, classification);
			model.addAttribute(WebConstants.ACTION, "");

			try {
				final ContextAccess context = contextProvider.findContext(ContextDefinition.forVersion(classification,
						fiscalYear));

				Iterator<? extends TabularConcept> concepts = transformService.getAllConcepts(classification, context);
				if (concepts != null && concepts.hasNext()) {

					// Run id for this transformation process
					Long runId = baseTransformService.getRunId();

					transformService.transformTabularData(classification, fiscalYear, concepts, runId, context);
					model.addAttribute(WebConstants.RUN_ID, runId);
					mav = new ModelAndView(NAV_TRANSFORMATION_URL);
				} else {
					model.addAttribute("errorMessage", "cims.migrationViewer.noData");
					BasicInfoBean viewBean = new BasicInfoBean();
					viewBean.setClassification(classification);
					viewBean.setFiscalYear(fiscalYear);
					mav.setViewName(DATA_TRANSFORMATION_VIEW);
					mav.addObject(VIEW_BEAN, viewBean);
					LOGGER.debug("Failed to process the transformation because no data is set for the specified version.");
				}
			}
			// Catch exceptions like no data for the specified classification and fiscal year
			catch (Exception exception) {
				model.addAttribute("errorMessage", exception.getMessage());
				BasicInfoBean viewBean = new BasicInfoBean();
				viewBean.setClassification(classification);
				viewBean.setFiscalYear(fiscalYear);
				mav.setViewName(DATA_TRANSFORMATION_VIEW);
				mav.addObject(VIEW_BEAN, viewBean);
				LOGGER.error("Failed to transform data", exception);
				return mav;
			}

		} else {
			model.addAttribute("errorMessage", "cims.migrationViewer.runStatusIsNotReady");
			BasicInfoBean viewBean = new BasicInfoBean();
			viewBean.setClassification(classification);
			viewBean.setFiscalYear(fiscalYear);
			mav.setViewName(DATA_TRANSFORMATION_VIEW);
			mav.addObject(VIEW_BEAN, viewBean);
			LOGGER.debug("Failed to process the transformation because another one is running currently");
		}

		return mav;
	}

	@RequestMapping(value = "/tabularDataTransformation.htm", params = "viewTransformationReport", method = RequestMethod.POST)
	public ModelAndView viewTransformationReport(final Model model,
			@RequestParam(Constants.FISCAL_YEAR) final String fiscalYear,
			@RequestParam(Constants.CLASSIFICATION) final String classification) {

		LOGGER.debug(">>>migrateData");

		model.addAttribute(Constants.FISCAL_YEAR, fiscalYear);
		model.addAttribute(Constants.CLASSIFICATION, classification);
		model.addAttribute(WebConstants.ACTION, WebConstants.ACTION_VIEW_REPORT);

		return new ModelAndView(NAV_TRANSFORMATION_URL);
	}
}