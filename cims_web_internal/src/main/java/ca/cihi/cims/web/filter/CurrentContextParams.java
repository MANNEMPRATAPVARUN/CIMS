package ca.cihi.cims.web.filter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.dal.ContextIdentifier;

/**
 * This class helps controllers interact with the {@link CurrentContextFilter}.
 */
public class CurrentContextParams {

	private static final String ENCODING = "ISO-8859-1";

	public static final String ACTIVATE = "ccp_on";
	public static final String REQUEST_ID = "ccp_rid";
	public static final String CONTEXT_ID = "ccp_cid";
	public static final String BASECLASSIFICATION = "ccp_bc";
	private static final String FORMAT = REQUEST_ID + "=%s&" + CONTEXT_ID + "=%s&" + BASECLASSIFICATION + "=%s&"
			+ ACTIVATE + "=%s";

	// -----------------------------------------------------------------

	public ContextDefinition definition(HttpServletRequest request) {
		if (request.getParameter(ACTIVATE) == null) {
			return null;
		} else {
			String changeRequest = request.getParameter(REQUEST_ID);
			Long changeRequestId = changeRequest == null ? null : Long.parseLong(changeRequest);
			return ContextDefinition.forChangeContext(//
					changeRequestId, //
					request.getParameter(BASECLASSIFICATION), // //
					Long.parseLong(request.getParameter(CONTEXT_ID)));
		}
	}

	private String encode(Object val) {
		try {
			return URLEncoder.encode("" + val, ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	public String urlParameters(ContextIdentifier contextId) {
		return String.format(FORMAT, encode(contextId.getRequestId()), encode(contextId.getContextId()),
				encode(contextId.getBaseClassification()), encode(true));
	}

}
