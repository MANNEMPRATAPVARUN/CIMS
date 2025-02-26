package ca.cihi.cims.web.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import ca.cihi.cims.service.ChangeRequestService;

public class ConcurrentUpdateInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	ChangeRequestService changeRequestService;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String checkTimestamp = request.getParameter("checkTimestamp");
		if("Y".equals(checkTimestamp)){
			long changeRequestId = 0l;
			if(request.getParameter("changeRequestId")!=null)
				changeRequestId = Long.parseLong(request.getParameter("changeRequestId"));
			else
				changeRequestId = Long.parseLong(request.getParameter("ccp_rid"));
			long lockTimestamp = Long.parseLong(request.getParameter("lockTimestamp"));
			changeRequestService.checkChangeRequestIsLocked(changeRequestId, lockTimestamp);
		}
		return true;
	}

}
