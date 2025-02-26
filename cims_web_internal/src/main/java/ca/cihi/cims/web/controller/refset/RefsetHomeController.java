package ca.cihi.cims.web.controller.refset;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.model.User;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.concept.RefsetVersion;
import ca.cihi.cims.service.refset.RefsetService;
import ca.cihi.cims.util.RefsetUtils;
import ca.cihi.cims.web.bean.refset.RefsetCatalogBean;

/**
 *
 * This is controller deals with all refset related requests from home page.
 *
 */
@Controller
public class RefsetHomeController {
	/**
	 * Reference to logger.
	 */
	private static final Log LOGGER = LogFactory.getLog(RefsetHomeController.class);

	/**
	 * Home page my assigned refsets view.
	 */
	private static final String MY_ASSIGNED_REFSET_VIEW = "/refset/assignedRefsets";

	/**
	 * Default Page Size.
	 */
	public static final int DEFAULT_PAGE_SIZE = 10;

	/**
	 * Reference to refset service.
	 */
	@Autowired
	private RefsetService refsetService;

	public RefsetService getRefsetService() {
		return refsetService;
	}

	public void setRefsetService(RefsetService refsetService) {
		this.refsetService = refsetService;
	}

	@RequestMapping("/myAssignedRefset.htm")
	public String findMyAssignedRefsets(HttpSession session, Model model, HttpServletRequest request) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);

		try {
			List<RefsetVersion> refsets = refsetService.getOpenActiveRefsetVersions();
			List<RefsetVersion> filteredRefsets = filterRefsetVersionByAssignee(currentUser.getUserId(), refsets);

			List<RefsetCatalogBean> refsetCatalogtBeanList = getRefsetCatalogtBeanList(filteredRefsets);

			if (refsetCatalogtBeanList != null) {
				model.addAttribute("myAssignedRefsets", refsetCatalogtBeanList);
				model.addAttribute("resultSize", refsetCatalogtBeanList.size());
				model.addAttribute("pageSize", DEFAULT_PAGE_SIZE);
			}
		} catch (Exception e) {
			LOGGER.error("exception: " + e);
		}

		return MY_ASSIGNED_REFSET_VIEW;
	}

	/**
	 * Retrieve refset catalog lists.
	 *
	 * @param refsetVersionList
	 *            the refset version list.
	 * @return the refset catalog list.
	 */
	private List<RefsetCatalogBean> getRefsetCatalogtBeanList(List<RefsetVersion> refsetVersionList) {
		List<RefsetCatalogBean> refsetCatalogtBeanList = new ArrayList<RefsetCatalogBean>();

		for (RefsetVersion refsetVersion : refsetVersionList) {
			Refset refset = refsetService.getRefset(refsetVersion.getContextIdentifier().getElementVersionId(),
					refsetVersion.getRefsetIdentifier().getElementId(),
					refsetVersion.getRefsetIdentifier().getElementVersionId());

			if (refset == null) {
				continue;
			}

			RefsetCatalogBean refsetCatalogBean = new RefsetCatalogBean();

			refsetCatalogBean.setCategory(refsetVersion.getCategoryName());
			refsetCatalogBean.setRefsetName(refsetVersion.getRefsetName());

			refsetCatalogBean.setRefsetVersionName(RefsetUtils.getRefsetVersionName(refsetVersion.getRefsetCode(),
					refsetVersion.getEffectiveYearFrom() != null ? refsetVersion.getEffectiveYearFrom().intValue()
							: null,
					refsetVersion.getEffectiveYearTo() != null ? refsetVersion.getEffectiveYearTo().intValue() : null,
					refsetVersion.getVersionCode()));

			refsetCatalogBean.setContextId(refsetVersion.getContextIdentifier().getElementVersionId());
			refsetCatalogBean.setElementId(refsetVersion.getRefsetIdentifier().getElementId());
			refsetCatalogBean.setElementVersionId(refsetVersion.getRefsetIdentifier().getElementVersionId());

			refsetCatalogtBeanList.add(refsetCatalogBean);
		}

		return refsetCatalogtBeanList;
	}

	/**
	 * Filter the refsets to include only active refsets assigned to the assignee.
	 *
	 * @param assigneeId
	 *            the assignee Id.
	 * @param refsets
	 *            the refsets.
	 * @return the refsets assigned to the assignee.
	 */
	private List<RefsetVersion> filterRefsetVersionByAssignee(long assigneeId, List<RefsetVersion> refsets) {
		return refsets == null ? new ArrayList<RefsetVersion>()
				: refsets.stream().filter(refsetVersion -> refsetVersion.getAssigneeId() == assigneeId)
						.collect(Collectors.toList());
	}
}
