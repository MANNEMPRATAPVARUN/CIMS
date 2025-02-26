package ca.cihi.cims.web.controller.index;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.ClassificationService;
import ca.cihi.cims.util.CimsConfiguration;

public abstract class IndexBaseController {

	public static final String ATTRIBUTE_BEAN = "bean";
	public static final String ATTRIBUTE_LOADTIME = "loadtime";
	public static final String ATTRIBUTE_LOADDETAILS = "loaddetails";

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	protected CimsConfiguration config;
	@Autowired
	protected ClassificationService service;
	@Autowired
	protected ElementOperations elementOperations;
	@Autowired
	protected ChangeRequestService changeRequestService;

	// ------------------------------------------------------------------

	protected String getBreadcrumbs(Long elementId, boolean trim) {
		String breadCrumbs = elementOperations.getIndexPath(service.getCurrentContextId(), //
				elementId == null ? 0 : elementId.longValue());
		if (trim && breadCrumbs != null) {
			int last = breadCrumbs.lastIndexOf(">");
			if (last == -1) {
				breadCrumbs = null;
			} else {
				breadCrumbs = breadCrumbs.substring(0, last + 1);
			}
		}
		return breadCrumbs;
	}

	public ChangeRequestService getChangeRequestService() {
		return changeRequestService;
	}

	public void setChangeRequestService(ChangeRequestService changeRequestService) {
		this.changeRequestService = changeRequestService;
	}

	public void setConfig(CimsConfiguration config) {
		this.config = config;
	}

	public void setElementOperations(ElementOperations elementOperations) {
		this.elementOperations = elementOperations;
	}

	public void setService(ClassificationService service) {
		this.service = service;
	}

}
