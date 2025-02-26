package ca.cihi.cims.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class SessionTimeOutFilter implements Filter {
	private static final Log LOGGER = LogFactory.getLog(SessionTimeOutFilter.class);
	
	private ArrayList<String> urlList;
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		LOGGER.debug("Session time out filter");
		 HttpServletRequest request = (HttpServletRequest) req;
	     HttpServletResponse response = (HttpServletResponse) res;
	     String url = request.getServletPath();
	      boolean allowedRequest = false;
	         
	      if(urlList.contains(url)) {
	            allowedRequest = true;
	      }
	      if (!allowedRequest) {
	            HttpSession session = request.getSession(false);
	            if (null == session || session.isNew() ) {
	                response.sendRedirect(request.getContextPath()+"/sessionTimeOut.htm");
	            }
	       }
	         
	       chain.doFilter(req, res);
   }

	@Override
	public void init(FilterConfig config) throws ServletException {
		 String urls = config.getInitParameter("avoid-urls");
	        StringTokenizer token = new StringTokenizer(urls, ",");
	 
	        urlList = new ArrayList<String>();
	 
	        while (token.hasMoreTokens()) {
	            urlList.add(token.nextToken());
	 
	        }

	}

}
