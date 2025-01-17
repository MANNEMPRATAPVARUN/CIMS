package ca.cihi.cims.web.controller.changerequest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.UserSearchCriteria;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.notification.NotificationDTO;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.NotificationService;

/*
 * this controller deal with the home page
 */
@Controller
public class ChangeRequestHomeController {

	private static final Log LOGGER = LogFactory.getLog(ChangeRequestHomeController.class);
	protected static final String MY_HOME = "myHome";
	protected static final String MY_NOTIFICATIONS_VIEW = "/requestmanagement/listNotifications";

	protected static final String MY_CHANGEREQUESTS_VIEW = "/requestmanagement/listChangeRequests";

	protected static final String ALL_CHANGE_REQUESTS_VIEW = "allChangeRequests";
	
	protected static final String SEARCH_RESULT = "/requestmanagement/searchResults";

	public static final int pageSize = 10;

	@Autowired
	private ChangeRequestService changeRequestService;
	@Autowired
	private NotificationService notificationService;

	/*
	 * when the user click the complete button
	 */
	@RequestMapping("/completeTask.htm")
	public String completeTask(@RequestParam("notificationId") Long notificationId, Model model, HttpSession session,
			HttpServletRequest request) {

		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		notificationService.completeTask(notificationId, currentUser.getUserId()); //
		return findMyNotices(model, session, request);
	}

	@RequestMapping("/findAllCCIChangeRequests.htm")
	public String findAllCCIChangeRequests(Model model) {
		List<ChangeRequest> allChangeRequests = changeRequestService.findAllCCIChangeRequests();
		model.addAttribute("allChangeRequests", allChangeRequests);
		model.addAttribute("classification", "CCI");
		return ALL_CHANGE_REQUESTS_VIEW;
	}

	@RequestMapping("/findAllChangeRequests.htm")
	public String findAllChangeRequests(Model model) {
		List<ChangeRequest> allChangeRequests = changeRequestService.findAllChangeRequests();
		model.addAttribute("allChangeRequests", allChangeRequests);
		return ALL_CHANGE_REQUESTS_VIEW;
	}

	@RequestMapping("/findAllICDChangeRequests.htm")
	public String findAllICDChangeRequests(Model model) {
		List<ChangeRequest> allChangeRequests = changeRequestService.findAllICDChangeRequests();
		model.addAttribute("allChangeRequests", allChangeRequests);
		model.addAttribute("classification", "ICD-10-CA");
		return ALL_CHANGE_REQUESTS_VIEW;
	}

	/*
	 * @RequestMapping("/myNotifications.htm") public String findMyNotices(Model model, HttpSession session) {
	 * 
	 * // List<Notification> myNotifications =notificationService.findMyNotification(); User currentUser = (User)
	 * session.getAttribute(WebConstants.CURRENT_USER);
	 * 
	 * 
	 * List<NotificationDTO> myNotifications = notificationService.findNotificationsByUserId(currentUser.getUserId());
	 * 
	 * model.addAttribute("myNotifications", myNotifications); NotificationDTO notificationDTO = new NotificationDTO();
	 * model.addAttribute("notificationDTO", notificationDTO);
	 * 
	 * return MY_NOTIFICATIONS_VIEW; }
	 */

	@RequestMapping("/myChangeRequests.htm")
	public String findMyChangeRequests(HttpSession session, Model model, HttpServletRequest request) {

		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		String tableId = "changeRequest";
		String sortNameParameter = new ParamEncoder(tableId).encodeParameterName(TableTagParameters.PARAMETER_SORT);
		String sortOrderParameter = new ParamEncoder(tableId).encodeParameterName(TableTagParameters.PARAMETER_ORDER);

		int startRow = 1;
		String pageNumParameter = new ParamEncoder(tableId).encodeParameterName(TableTagParameters.PARAMETER_PAGE);
		if (request.getParameter(pageNumParameter) != null) {
			startRow = (Integer.parseInt(request.getParameter(pageNumParameter)) - 1) * pageSize + 1;
		}
		int endRow = startRow + pageSize - 1;
		String ascOrDesc = "1"; // default to asc
		if (request.getParameter(sortOrderParameter) != null) {
			ascOrDesc = request.getParameter(sortOrderParameter);
		}
		String sortByName = "NAME"; // default sort by request name
		if (request.getParameter(sortNameParameter) != null) {
			sortByName = request.getParameter(sortNameParameter);
		}
		UserSearchCriteria searchCriteria = new UserSearchCriteria();
		searchCriteria.setUserId(currentUser.getUserId());
		searchCriteria.setStartRow(startRow);
		searchCriteria.setEndRow(endRow);
		searchCriteria.setSortBy(sortByName);
		searchCriteria.setAscending(ascOrDesc.equals("1") ? true : false);

		List<ChangeRequest> changeRequests = changeRequestService.findChangeRequestsBySearchCriteria(searchCriteria);
		int numOfChangeRequests = changeRequestService.findNumOfMyChangeRequests(currentUser.getUserId());

		// List<ChangeRequest> changeRequests = changeRequestService.findAllChangeRequests();
		model.addAttribute("myChangeRequests", changeRequests);
		model.addAttribute("resultSize", numOfChangeRequests);
		model.addAttribute("pageSize", pageSize);
		return MY_CHANGEREQUESTS_VIEW;
	}
	
	@RequestMapping("/searchResults.htm")
	public String searchChangeRequests(final Model model, HttpSession session) {
	
		return SEARCH_RESULT;
	}

	@RequestMapping("/myNotifications.htm")
	public String findMyNotices(Model model, HttpSession session, HttpServletRequest request) {

		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		String tableId = "myNotification";
		String sortNameParameter = new ParamEncoder(tableId).encodeParameterName(TableTagParameters.PARAMETER_SORT);
		String sortOrderParameter = new ParamEncoder(tableId).encodeParameterName(TableTagParameters.PARAMETER_ORDER);

		int startRow = 1;
		String pageNumParameter = new ParamEncoder(tableId).encodeParameterName(TableTagParameters.PARAMETER_PAGE);
		if (request.getParameter(pageNumParameter) != null) {
			startRow = (Integer.parseInt(request.getParameter(pageNumParameter)) - 1) * pageSize + 1;
		}
		int endRow = startRow + pageSize - 1;
		
		String ascOrDesc = "1" ; // default to ASC  as the  changeRequestName  is the default column for sort when the user logs into CIMS
		if (request.getParameter(sortOrderParameter) != null) {
			ascOrDesc = request.getParameter(sortOrderParameter);
		}
		String sortByName = "changeRequestName";// default sort by changeRequestName
		
		if (request.getParameter(sortNameParameter) != null) {
			sortByName = request.getParameter(sortNameParameter);
		}
		UserSearchCriteria searchCriteria = new UserSearchCriteria();
		searchCriteria.setUserId(currentUser.getUserId());
		searchCriteria.setStartRow(startRow);
		searchCriteria.setEndRow(endRow);
		searchCriteria.setSortBy(sortByName);
		searchCriteria.setAscending(ascOrDesc.equals("1") ? true : false);
		List<NotificationDTO> myNotifications = notificationService
				.findNotificationsByUserSerachCriteria(searchCriteria);
		int numOfNotifications = notificationService.findNumOfMyNotifications(currentUser.getUserId());

		model.addAttribute("myNotifications", myNotifications);
		model.addAttribute("resultSize", numOfNotifications);
		model.addAttribute("pageSize", pageSize);

		NotificationDTO notificationDTO = new NotificationDTO();
		model.addAttribute("notificationDTO", notificationDTO);

		return MY_NOTIFICATIONS_VIEW;
	}

	/*
	 * when the user click the home link
	 */
	@RequestMapping("/myhome")
	public String findMyNoticesAndRequest(Model model, HttpServletRequest request) {
		return MY_HOME;
	}

	@RequestMapping("/removeCheckedNotifications.htm")
	public String removeMyNotification(NotificationDTO notificationDTO, final BindingResult result, Model model,
			HttpSession session, HttpServletRequest request) {
		// List<Notification> myNotifications =notificationService.findMyNotification();
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		if (notificationDTO.getNotificationIds() != null && notificationDTO.getNotificationIds().size() > 0) {
			notificationService.removeMyNotifications(currentUser, notificationDTO.getNotificationIds()); // disable
		}
		return findMyNotices(model, session, request);
	}

	@RequestMapping("/removeNotification.htm")
	public String removeOneNotification(NotificationDTO notificationDTO, final BindingResult result, Model model,
			HttpSession session, HttpServletRequest request) {
		// List<Notification> myNotifications =notificationService.findMyNotification();
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		if (notificationDTO.getNotificationId() != null) {
			notificationService.removeMyNotification(currentUser.getUserId(), notificationDTO.getNotificationId()); // disable
			// it
		}
		return findMyNotices(model, session, request);
	}

	public void setChangeRequestService(ChangeRequestService changeRequestService) {
		this.changeRequestService = changeRequestService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

}
