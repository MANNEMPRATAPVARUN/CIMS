package ca.cihi.cims.web.controller;

import java.util.List;
import java.util.Map;

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
import ca.cihi.cims.service.MigrationService;
import ca.cihi.cims.web.WebConstants;
import ca.cihi.cims.web.bean.LogMessage;
import ca.cihi.cims.web.bean.MigrationReportViewBean;

/**
 * Controller for migration.
 * 
 * @author wxing
 * 
 */
@Controller
public class MigrationReportController {

	private static final Log LOGGER = LogFactory.getLog(MigrationReportController.class);

	private MigrationService migrationService;

	private static final String NAV_DATA_MIGRATION = "redirect:/dataMigration.htm";
	private static final String MIGRATION_REPORT_VIEW = "/migration/migrationReport";

	private static final String VIEW_BEAN = "viewBean";
	private static final String MIGRATION_LOG = "logMessageList";
	private static final String START_TIME = "startTime";
	private static final String END_TIME = "endTime";

	@RequestMapping(value = "/migration.htm", params = "back", method = RequestMethod.POST)
	public ModelAndView back(final Model model) {
		LOGGER.debug("back to data migration page");

		return new ModelAndView(NAV_DATA_MIGRATION);
	}

	public MigrationService getMigrationService() {
		return migrationService;
	}

	@Autowired
	public void setMigrationService(MigrationService migrationService) {
		this.migrationService = migrationService;
	}

	@RequestMapping(value = "/migration.htm", method = RequestMethod.GET)
	public ModelAndView setupForm(final @RequestParam(Constants.CLASSIFICATION) String classification,
			final @RequestParam(Constants.FISCAL_YEAR) String fiscalYear,
			final @RequestParam(WebConstants.ACTION) String action, final HttpServletRequest request,
			final HttpSession session) {
		LOGGER.debug("Migration.setupForm");

		ModelAndView mav = new ModelAndView();
		MigrationReportViewBean viewBean = new MigrationReportViewBean();
		viewBean.setClassification(classification);
		viewBean.setFiscalYear(fiscalYear);

		// Check if the list should be set in the session or not.
		// If any such params exist in our request we ignore setting the list in the session because it is already set.
		Map<String, Object> paramMap = WebUtils.getParametersStartingWith(request, "d-");

		List<LogMessage> logMessageList;
		if (paramMap.isEmpty()) {
			if (WebConstants.ACTION_VIEW_REPORT.equalsIgnoreCase(action)) {
				logMessageList = migrationService.getLogMessage(classification, fiscalYear);
			} else {
				// viewBean.setStartTime(new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss a",
				// Locale.CANADA).format(Calendar.getInstance().getTime()));
				logMessageList = migrationService.migrateData(fiscalYear, classification);
				// viewBean.setEndTime(new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss a",
				// Locale.CANADA).format(Calendar.getInstance().getTime()));
			}

			WebUtils.setSessionAttribute(request, MIGRATION_LOG, logMessageList);
		} else {
			viewBean.setStartTime(request.getParameter(START_TIME));
			viewBean.setEndTime(request.getParameter(END_TIME));
		}

		mav.addObject(VIEW_BEAN, viewBean);
		mav.setViewName(MIGRATION_REPORT_VIEW);
		return mav;
	}
}