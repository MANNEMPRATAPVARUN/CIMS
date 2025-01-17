package ca.cihi.cims.web.controller.tabular;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.ClassificationService;
import ca.cihi.cims.util.CimsConfiguration;

public abstract class TabularBaseController {

	public static final String ATTRIBUTE_BEAN = "bean";
	public static final String ATTRIBUTE_LOADTIME = "loadtime";
	public static final String ATTRIBUTE_LOADDETAILS = "loaddetails";

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	protected CimsConfiguration config;
	@Autowired
	protected ClassificationService service;
	@Autowired
	protected ChangeRequestService changeRequestService;

	// ------------------------------------------------------------------

	public ChangeRequestService getChangeRequestService() {
		return changeRequestService;
	}

	public void setChangeRequestService(ChangeRequestService changeRequestService) {
		this.changeRequestService = changeRequestService;
	}

	public void setConfig(CimsConfiguration config) {
		this.config = config;
	}

	public void setService(ClassificationService service) {
		this.service = service;
	}

}
