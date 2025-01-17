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
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.service.MigrationService;
import ca.cihi.cims.web.bean.BasicInfoBean;

/**
 * Controller for the context creation page.
 * 
 * @author wxing
 */
@Controller
public class ContextCreationController extends BaseController {

	private static final Log LOGGER = LogFactory.getLog(ContextCreationController.class);

	private static final String CONTEXT_CREATION_VIEW = "/migration/createContext";
	private static final String VIEW_BEAN = "viewBean";
	private static final String LATEST_VERSION_YEAR = "2017";

	private MigrationService migrationService;

	@RequestMapping(value = "/createContext.htm", params = "create", method = RequestMethod.POST)
	public ModelAndView createConext(final Model model, @RequestParam(Constants.FISCAL_YEAR) final String version,
			@RequestParam(Constants.CLASSIFICATION) final String baseClassification) {

		LOGGER.debug(">>>Create context");
		ModelAndView mav = new ModelAndView();
		BasicInfoBean viewBean = new BasicInfoBean();
		viewBean.setClassification(baseClassification);
		viewBean.setFiscalYear(version);
		mav.addObject(VIEW_BEAN, viewBean);
		mav.setViewName(CONTEXT_CREATION_VIEW);

		try {
			final ContextAccess context = contextProvider.findContext(ContextDefinition.forVersion(baseClassification,
					version));
			ContextIdentifier contextId = context.getContextId();

			boolean isVersionYear = false;
			if (LATEST_VERSION_YEAR.equalsIgnoreCase(version)) {
				isVersionYear = true;

				// Close the version of 2015
				migrationService.close2015();
			}

			contextProvider.createContext(contextId, isVersionYear);
			model.addAttribute("successMessage", "cims.contextCreationViewer.successMessage");
			return mav;
		}
		// Catch exceptions like no data for the specified classification and fiscal year
		catch (Exception exception) {
			model.addAttribute("errorMessage", exception.getMessage());
			return mav;
		}
	}

	public MigrationService getMigrationService() {
		return migrationService;
	}

	@Autowired
	public void setMigrationService(MigrationService migrationService) {
		this.migrationService = migrationService;
	}

	@RequestMapping(value = "/createContext.htm", method = RequestMethod.GET)
	public ModelAndView setupForm(final Model model) {
		LOGGER.debug("setUpContextCreationForm");
		ModelAndView mav = new ModelAndView();
		BasicInfoBean viewBean = new BasicInfoBean();
		mav.addObject(VIEW_BEAN, viewBean);
		mav.setViewName(CONTEXT_CREATION_VIEW);
		return mav;
	}

}