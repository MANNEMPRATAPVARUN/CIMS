package ca.cihi.cims.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.Constants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.content.shared.index.BookIndex;
import ca.cihi.cims.service.BaseTransformationService;
import ca.cihi.cims.service.TransformIndexService;
import ca.cihi.cims.web.WebConstants;
import ca.cihi.cims.web.bean.IndexTransformationViewBean;

/**
 * Controller for the index data transformation page.
 * 
 * @author wxing
 */
@Controller
public class IndexDataTransformController extends BaseController {

	private static final Log LOGGER = LogFactory.getLog(IndexDataTransformController.class);

	private static final String DATA_TRANSFORMATION_VIEW = "/migration/indexDataTransformation";
	private static final String NAV_TRANSFORMATION_URL = "redirect:/indexTransformation.htm";
	private static final String VIEW_BEAN = "viewBean";
	private static final String LANGUAGE = "language";
	private static final String BOOK_INDEX_TYPE = "bookIndexType";

	private TransformIndexService transformIndexService;
	private BaseTransformationService baseTransformService;

	public BaseTransformationService getBaseTransformService() {
		return baseTransformService;
	}

	public TransformIndexService getTransformIndexService() {
		return transformIndexService;
	}

	@RequestMapping(value = "/indexDataTransformation.htm", method = RequestMethod.POST)
	public ModelAndView printErrorMessage(final Model model) {
		LOGGER.debug("Print Error Mesasge in transformation Form");
		ModelAndView mav = new ModelAndView();
		IndexTransformationViewBean viewBean = new IndexTransformationViewBean();
		mav.addObject(VIEW_BEAN, viewBean);
		mav.setViewName(DATA_TRANSFORMATION_VIEW);
		return mav;
	}

	@Autowired
	public void setBaseTransformService(BaseTransformationService baseTransformService) {
		this.baseTransformService = baseTransformService;
	}

	@Autowired
	public void setTransformIndexService(TransformIndexService transformIndexService) {
		this.transformIndexService = transformIndexService;
	}

	@RequestMapping(value = "/indexDataTransformation.htm", method = RequestMethod.GET)
	public ModelAndView setupForm(final Model model) {
		LOGGER.debug("setUp transformation Form");
		ModelAndView mav = new ModelAndView();
		IndexTransformationViewBean viewBean = new IndexTransformationViewBean();
		mav.addObject(VIEW_BEAN, viewBean);
		mav.setViewName(DATA_TRANSFORMATION_VIEW);
		return mav;
	}

	@RequestMapping(value = "/indexDataTransformation.htm", params = "transform", method = RequestMethod.POST)
	public ModelAndView transformData(final Model model, @RequestParam(Constants.FISCAL_YEAR) final String fiscalYear,
			@RequestParam(Constants.CLASSIFICATION) final String classification,
			@RequestParam(BOOK_INDEX_TYPE) final String bookIndexType, @RequestParam(LANGUAGE) final String language) {

		LOGGER.debug(">>>transformData");
		ModelAndView mav = new ModelAndView();
		if (transformIndexService.checkRunStatus(fiscalYear, classification, bookIndexType, language)) {
			model.addAttribute(Constants.FISCAL_YEAR, fiscalYear);
			model.addAttribute(Constants.CLASSIFICATION, classification);
			model.addAttribute(BOOK_INDEX_TYPE, bookIndexType);
			model.addAttribute(LANGUAGE, language);
			model.addAttribute(WebConstants.ACTION, "");

			IndexTransformationViewBean viewBean = new IndexTransformationViewBean();
			viewBean.setClassification(classification);
			viewBean.setFiscalYear(fiscalYear);
			viewBean.setBookIndexType(bookIndexType);
			viewBean.setLanguage(language);
			mav.addObject(VIEW_BEAN, viewBean);

			try {
				final ContextAccess context = contextProvider.findContext(ContextDefinition.forVersion(classification,
						fiscalYear));
				BookIndex bookIndex = transformIndexService.getBookIndex(context, bookIndexType, language);

				if (bookIndex == null) {
					model.addAttribute("errorMessage", "cims.migrationViewer.noData");
					mav.setViewName(DATA_TRANSFORMATION_VIEW);
					LOGGER.debug("Failed to process the transformation because no data is set for the specified version.");
				} else {
					// Run id for this transformation process
					Long runId = baseTransformService.getRunId();

					transformIndexService.transformIndexBook(classification, fiscalYear, bookIndex, language, runId,
							context);
					model.addAttribute(WebConstants.RUN_ID, runId);
					mav = new ModelAndView(NAV_TRANSFORMATION_URL);
				}

			}
			// Catch exceptions like no data for the specified classification and fiscal year
			catch (Exception exception) {
				model.addAttribute("errorMessage", exception.getMessage());
				mav.setViewName(DATA_TRANSFORMATION_VIEW);
				LOGGER.error("Failed to transform data", exception);
				return mav;
			}

		} else {
			model.addAttribute("errorMessage", "cims.migrationViewer.runStatusIsNotReady");
			mav.setViewName(DATA_TRANSFORMATION_VIEW);
			IndexTransformationViewBean viewBean = new IndexTransformationViewBean();
			viewBean.setClassification(classification);
			viewBean.setFiscalYear(fiscalYear);
			viewBean.setBookIndexType(bookIndexType);
			viewBean.setLanguage(language);
			mav.addObject(VIEW_BEAN, viewBean);
			LOGGER.debug("Failed to process the transformation because another one is running currently");
		}

		return mav;
	}

	@RequestMapping(value = "/indexDataTransformation.htm", params = "viewTransformationReport", method = RequestMethod.POST)
	public ModelAndView viewTransformationReport(final Model model,
			@RequestParam(Constants.FISCAL_YEAR) final String fiscalYear,
			@RequestParam(Constants.CLASSIFICATION) final String classification,
			@RequestParam(BOOK_INDEX_TYPE) final String bookIndexType, @RequestParam(LANGUAGE) final String language) {

		LOGGER.debug(">>>migrateData");

		model.addAttribute(Constants.FISCAL_YEAR, fiscalYear);
		model.addAttribute(Constants.CLASSIFICATION, classification);
		model.addAttribute(BOOK_INDEX_TYPE, bookIndexType);
		model.addAttribute(LANGUAGE, language);
		model.addAttribute(WebConstants.ACTION, WebConstants.ACTION_VIEW_REPORT);

		return new ModelAndView(NAV_TRANSFORMATION_URL);
	}
}