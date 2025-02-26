package ca.cihi.cims.web.controller.refset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.model.AuxTableValue;
import ca.cihi.cims.refset.service.concept.RefsetVersion;
import ca.cihi.cims.service.refset.RefsetService;
import ca.cihi.cims.util.RefsetUtils;
import ca.cihi.cims.web.bean.KeyValueBean;
import ca.cihi.cims.web.bean.refset.RefsetCatalogBean;
import ca.cihi.cims.web.bean.refset.RefsetCatalogViewBean;

@Controller
@RequestMapping("/refset/refsetCatalog")
@SessionAttributes({ WebConstants.REFSETCATALOG_VIEW_BEAN })
public class RefsetCatalogController {
	private static final Log LOGGER = LogFactory.getLog(RefsetCatalogController.class);

	static final String REFSET_CATALOG_VIEW = "/refset/refsetCatalog";

	static final String REDIREC_REFSET_CATALOG_VIEW = "redirect:/refset/refsetCatalog.htm";

	@Autowired
	private RefsetService refsetService;

	private RefsetCatalogViewBean viewBean;

	// ----------------------------------------------------------------------------------

	@ModelAttribute("refSetCategoryList")
	public Collection<KeyValueBean> populateRefSetCategoryList() throws Exception {
		ArrayList<KeyValueBean> keyValues = new ArrayList<KeyValueBean>();
		keyValues.add(new KeyValueBean("", "All"));

		List<AuxTableValue> refSetCategoryList = refsetService.getCategoryList();

		for (AuxTableValue auxTableValue : refSetCategoryList) {
			keyValues.add(new KeyValueBean(auxTableValue.getAuxValueCode(), auxTableValue.getAuxEngLable()));
		}

		return keyValues;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String setUpForm(HttpServletRequest request, HttpSession session, ModelMap model) throws Exception {
		LOGGER.debug("refsetCatalogForm");

		RefsetCatalogViewBean viewBean = new RefsetCatalogViewBean();

		List<RefsetVersion> refsetVersionList = refsetService.getRefsetVersions();

		List<RefsetCatalogBean> refsetCatalogtBeanList = getRefsetCatalogtBeanList(refsetVersionList);

		viewBean.setRefsetCatalogtBeanList(refsetCatalogtBeanList);

		model.addAttribute(WebConstants.REFSETCATALOG_VIEW_BEAN, viewBean);

		return REFSET_CATALOG_VIEW;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String displayForm(HttpServletRequest request, HttpSession session, ModelMap model,
			@ModelAttribute(WebConstants.REFSETCATALOG_VIEW_BEAN) RefsetCatalogViewBean viewBean) throws Exception {
		LOGGER.debug("< display: " + viewBean.getRefsetCategory());

		viewBean.setRefsetCatalogtBeanList(Collections.<RefsetCatalogBean> emptyList());
		if (viewBean.getRefsetCategory().isEmpty()) {
			LOGGER.debug("Select all");
			List<RefsetVersion> refsetVersionList = refsetService.getRefsetVersions();
			List<RefsetCatalogBean> refsetCatalogtBeanList = getRefsetCatalogtBeanList(refsetVersionList);
			viewBean.setRefsetCatalogtBeanList(refsetCatalogtBeanList);

		} else {
			List<RefsetCatalogBean> refsetCatalogtBeanList = new ArrayList<RefsetCatalogBean>();

			List<AuxTableValue> refSetCategoryList = refsetService.getCategoryList();

			String refsetCategory = viewBean.getRefsetCategory();

			List<RefsetVersion> refsetVersionList = null;

			for (AuxTableValue auxTableValue : refSetCategoryList) {
				if (auxTableValue.getAuxValueCode().equalsIgnoreCase(refsetCategory)) {
					Long categoryId = auxTableValue.getAuxTableValueId();
					refsetVersionList = refsetService.getRefsetVersions(categoryId);

					for (RefsetVersion refsetVersion : refsetVersionList) {
						RefsetCatalogBean refsetCatalogBean = new RefsetCatalogBean();

						refsetCatalogBean.setCategory(refsetVersion.getCategoryName());
						refsetCatalogBean.setRefsetName(refsetVersion.getRefsetName());

						refsetCatalogBean
								.setRefsetVersionName(RefsetUtils.getRefsetVersionName(refsetVersion.getRefsetCode(),
										refsetVersion.getEffectiveYearFrom() != null
												? refsetVersion.getEffectiveYearFrom().intValue() : null,
								refsetVersion.getEffectiveYearTo() != null
										? refsetVersion.getEffectiveYearTo().intValue() : null,
								refsetVersion.getVersionCode()));

						refsetCatalogBean.setContextId(refsetVersion.getContextIdentifier().getElementVersionId());
						refsetCatalogBean.setElementId(refsetVersion.getRefsetIdentifier().getElementId());
						refsetCatalogBean
								.setElementVersionId(refsetVersion.getRefsetIdentifier().getElementVersionId());

						refsetCatalogtBeanList.add(refsetCatalogBean);
					}

					break;
				}

			}

			viewBean.setRefsetCatalogtBeanList(refsetCatalogtBeanList);
		}

		model.addAttribute(WebConstants.REFSETCATALOG_VIEW_BEAN, viewBean);

		// return REDIREC_REFSET_CATALOG_VIEW;
		return REFSET_CATALOG_VIEW;

	}

	private List<RefsetCatalogBean> getRefsetCatalogtBeanList(List<RefsetVersion> refsetVersionList) {

		List<RefsetCatalogBean> refsetCatalogtBeanList = new ArrayList<RefsetCatalogBean>();

		// List<RefsetVersion> refsetVersionList = refsetService.getRefsetVersions();

		for (RefsetVersion refsetVersion : refsetVersionList) {
			if (!refsetVersion.getRefsetStatus().getStatus().equalsIgnoreCase("Disabled")) {
				RefsetCatalogBean refsetCatalogBean = new RefsetCatalogBean();

				refsetCatalogBean.setCategory(refsetVersion.getCategoryName());
				refsetCatalogBean.setRefsetName(refsetVersion.getRefsetName());

				refsetCatalogBean.setRefsetVersionName(RefsetUtils.getRefsetVersionName(refsetVersion.getRefsetCode(),
						refsetVersion.getEffectiveYearFrom() != null ? refsetVersion.getEffectiveYearFrom().intValue()
								: null,
						refsetVersion.getEffectiveYearTo() != null ? refsetVersion.getEffectiveYearTo().intValue()
								: null,
						refsetVersion.getVersionCode()));

				refsetCatalogBean.setContextId(refsetVersion.getContextIdentifier().getElementVersionId());
				refsetCatalogBean.setElementId(refsetVersion.getRefsetIdentifier().getElementId());
				refsetCatalogBean.setElementVersionId(refsetVersion.getRefsetIdentifier().getElementVersionId());

				refsetCatalogtBeanList.add(refsetCatalogBean);
			}
		}
		return refsetCatalogtBeanList;
	}

	public void setRefsetService(RefsetService refsetService) {
		this.refsetService = refsetService;
	}

	public RefsetCatalogViewBean getViewBean() {
		return viewBean;
	}

	public void setViewBean(RefsetCatalogViewBean viewBean) {
		this.viewBean = viewBean;
	}
}