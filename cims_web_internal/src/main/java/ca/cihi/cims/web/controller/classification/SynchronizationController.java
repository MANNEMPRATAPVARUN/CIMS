package ca.cihi.cims.web.controller.classification;

import static ca.cihi.cims.WebConstants.CURRENT_USER;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import ca.cihi.cims.exception.ConcurrentUpdateException;
import ca.cihi.cims.model.SynchronizationStatus;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.service.synchronization.SynchronizationService;

@Controller
@SessionAttributes(CURRENT_USER)
@RequestMapping("synchronization")
public class SynchronizationController {

	public static final String INSTANCE_ID = "instanceId";
	public static final String CONCURRENT_ERROR = "concurrentError";
	public static final String SYNCHRONIZATION_VIEW = "/classification/view/synchronization";

	@Autowired
	private SynchronizationService synchronizationService;

	@ResponseBody
	@RequestMapping(value = "status", method = RequestMethod.GET)
	public SynchronizationStatus getSynchronizationStatus(@RequestParam("ccp_rid") long changeRequestId)
			throws Exception {
		return synchronizationService.getSynchronizationStatus(changeRequestId);
	}

	// --------------------------------------------------------
	@ExceptionHandler(ConcurrentUpdateException.class)
	public void handleConcurrentException(HttpSession session, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		session.setAttribute(CONCURRENT_ERROR, "Y");
		String autoParams = (String) request.getAttribute("automaticContextParams");
		String lockTimestamp = request.getParameter("lockTimestamp");
		response.sendRedirect(request.getContextPath() + "/synchronization/start.htm?" + autoParams + "&lockTimestamp="
				+ lockTimestamp);

	}

	public void setSynchronizationService(SynchronizationService synchronizationService) {
		this.synchronizationService = synchronizationService;
	}

	@RequestMapping(value = "start", method = RequestMethod.GET)
	public String startSynchronization(@ModelAttribute(value = CURRENT_USER) User user,
			@RequestParam("ccp_rid") long changeRequestId, @RequestParam(value = "lockTimestamp") long lockTimestamp,
			HttpServletRequest request, HttpSession session) throws Exception {
		String concurrentError = (String) session.getAttribute(CONCURRENT_ERROR);
		if ("Y".equals(concurrentError)) {
			session.removeAttribute(CONCURRENT_ERROR);
			request.setAttribute(CONCURRENT_ERROR, "Y");
		} else {
			synchronizationService.synchronizeAsync(new OptimisticLock(lockTimestamp), user, changeRequestId);
		}
		request.setAttribute(INSTANCE_ID, synchronizationService.getInstanceId());
		return SYNCHRONIZATION_VIEW;
	}

}
