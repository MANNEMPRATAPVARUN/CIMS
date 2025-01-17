package ca.cihi.cims.web.controller.supplement;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.cihi.cims.Language;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.exception.RootElementExeption;
import ca.cihi.cims.model.supplement.SupplementModel;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.ClassificationService;
import ca.cihi.cims.util.CimsConfiguration;

public abstract class SupplementBaseController {

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

	protected String getBreadcrumbs(SupplementModel model, Language lang, boolean trim) {
		List<String> crumbs = new ArrayList<String>();
		if (model.getEntity() == null) {
			// ADD case
			try {
				model = service.getSupplementById(model.getElementId(), lang);
			} catch (RootElementExeption ex) {
				return "";
			}
		}
		while (model != null) {
			crumbs.add(model.getDescription());
			SupplementModel parent = model.getParent();
			if (model != null) {
				if (model.getEntity() != null) {
					try {
						parent = service.getSupplementById(model.getEntity().getParent().getElementId(), lang);
					} catch (Exception ex) {
					}
				}
			}
			model = parent;
		}
		StringBuilder b = new StringBuilder();
		int finish = trim ? 1 : 0;
		for (int i = crumbs.size() - 1; i >= finish; i--) {
			b.append(crumbs.get(i));
			b.append(" > ");
		}
		return b.toString();
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
