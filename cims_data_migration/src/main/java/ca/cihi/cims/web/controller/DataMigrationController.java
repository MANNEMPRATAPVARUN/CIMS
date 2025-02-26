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
import ca.cihi.cims.service.MigrationService;
import ca.cihi.cims.web.WebConstants;
import ca.cihi.cims.web.bean.BasicInfoBean;

/**
 * Controller for the data migration page.
 * 
 * @author wxing
 */
@Controller
public class DataMigrationController extends BaseController {

	private static final Log LOGGER = LogFactory.getLog(DataMigrationController.class);

	private static final String DATA_MIGRATION_VIEW = "/migration/dataMigration";
	private static final String NAV_MIGRATION_URL = "redirect:/migration.htm";
	private static final String VIEW_BEAN = "viewBean";

	private MigrationService migrationService;

	public MigrationService getMigrationService() {
		return migrationService;
	}

	@RequestMapping(value = "/dataMigration.htm", params = "migrate", method = RequestMethod.POST)
	public ModelAndView migrateData(final Model model, @RequestParam(Constants.FISCAL_YEAR) final String fiscalYear,
			@RequestParam(Constants.CLASSIFICATION) final String classification) {

		LOGGER.debug(">>>migrateData");
		ModelAndView mav;
		if (migrationService.checkRunStatus(classification)) {
			model.addAttribute(Constants.FISCAL_YEAR, fiscalYear);
			model.addAttribute(Constants.CLASSIFICATION, classification);
			model.addAttribute(WebConstants.ACTION, "");
			mav = new ModelAndView(NAV_MIGRATION_URL);
		} else {
			model.addAttribute("errorMessage", "cims.migrationViewer.runStatusIsNotReady");
			BasicInfoBean viewBean = new BasicInfoBean();
			mav = new ModelAndView(DATA_MIGRATION_VIEW);
			mav.addObject(VIEW_BEAN, viewBean);
		}

		return mav;
	}

	@Autowired
	public void setMigrationService(MigrationService migrationService) {
		this.migrationService = migrationService;
	}

	@RequestMapping(value = "/dataMigration.htm", method = RequestMethod.GET)
	public ModelAndView setupForm(final Model model) {
		LOGGER.debug("setUpMigrationForm");
		ModelAndView mav = new ModelAndView();
		BasicInfoBean viewBean = new BasicInfoBean();
		mav.addObject(VIEW_BEAN, viewBean);
		mav.setViewName(DATA_MIGRATION_VIEW);
		return mav;
	}

	@RequestMapping(value = "/dataMigration.htm", params = "viewMigrationReport", method = RequestMethod.POST)
	public ModelAndView viewMigrationReport(final Model model,
			@RequestParam(Constants.FISCAL_YEAR) final String fiscalYear,
			@RequestParam(Constants.CLASSIFICATION) final String classification) {

		LOGGER.debug(">>>migrateData");

		model.addAttribute(Constants.FISCAL_YEAR, fiscalYear);
		model.addAttribute(Constants.CLASSIFICATION, classification);
		model.addAttribute(WebConstants.ACTION, WebConstants.ACTION_VIEW_REPORT);

		return new ModelAndView(NAV_MIGRATION_URL);
	}
}