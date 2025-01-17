package ca.cihi.cims.web.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;

/**
 * This filter looks at the request parameters, and if the appropriate ones are set (see {@link CurrentContextParams})
 * then it opens a ContextAccess and makes it available via {@link CurrentContext} for the duration of request
 * processing.
 * 
 * @see CurrentContextParams
 * @see CurrentContext
 * 
 * @author MPrescott
 * 
 */
@Component
public class CurrentContextFilter implements HandlerInterceptor {

	private static final Log LOGGER = LogFactory.getLog(CurrentContextFilter.class);
	private static final String CURRENT_CONTEXT_PARAMS_REQUEST_ATTRIBUTE_NAME = "automaticContextParams";

	@Autowired
	private CurrentContext currentContext;
	@Autowired
	private ContextProvider contextProvider;

	// ----------------------------------------------------------------

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		if (currentContext.context() != null) {
			LOGGER.trace("Deactivating current context.");
			currentContext.makeCurrentContext(null);
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		ContextDefinition definition = new CurrentContextParams().definition(request);
		if (definition == null) {
			LOGGER.trace("No parameters found to prepare a current context.");
		} else {
			LOGGER.trace("Activating current context for " + definition);
			ContextAccess context = contextProvider.findContext(definition);
			currentContext.makeCurrentContext(context);
			request.setAttribute(CURRENT_CONTEXT_PARAMS_REQUEST_ATTRIBUTE_NAME, new CurrentContextParams()
					.urlParameters(context.getContextId()));
		}
		return true;
	}

	public void setContextProvider(ContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	public void setCurrentContext(CurrentContext currentContext) {
		this.currentContext = currentContext;
	}

}
