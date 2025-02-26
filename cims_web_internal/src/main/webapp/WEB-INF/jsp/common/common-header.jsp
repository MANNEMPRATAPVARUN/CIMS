<head>
	<%@ include file="/WEB-INF/jsp/common/include.jsp"%>	
	<meta name="description" content="${project.artifactId} - ${version}" />
	<meta http-equiv="Content-Type" content="${CONTENT_TYPE}">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge">
	<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
    <meta http-equiv="Pragma" content="no-cache" />
    <meta http-equiv="Expires" content="0" />
	<!--Blueprint Framework CSS -->
	<link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/screen.css" />" type="text/css" media="screen, projection" />
	<link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/print.css" />" type="text/css"  media="print" />
	<!--[if IE]><link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/ie.css" />" type="text/css" media="screen, projection" /><![endif]-->
	<link rel="stylesheet" type="text/css" href="<cc:resUrl value="/css/jquery/jquery-ui.css" />">
	<link rel="stylesheet" media="screen" href="<cc:resUrl value="/css/nav/superfish-dropdown.css" />">
	<link rel="stylesheet" type="text/css" href="<cc:resUrl value="/css/main.css" />">

	<link rel="stylesheet" type="text/css" href="<c:url value="/css/skin-vista/ui.dynatree.css"/>">
	<link rel="stylesheet" type="text/css" href="<c:url value="/css/cims.css"/>" >
	<link rel="stylesheet" type="text/css" href="<c:url value="/jquery/select2/select2.css"/>" >
	
	<!-- JQuery dependencies, always include -->
	<script type="text/javascript" src="<cc:resUrl value="/js/jquery/jquery.js" />"></script>
	<script type="text/javascript" src="<cc:resUrl value="/js/jquery/jquery-ui-1.8.10.custom.min.js" />"></script>
	<script type="text/javascript" src="<c:url value="/jquery/jquery.cookie.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/jquery/jquery.dynatree.js"/>"></script>
	<!-- Javascript for menu-->
	<script type="text/javascript" src="<cc:resUrl value="/js/nav/hoverIntent.js" />"></script>
	<script type="text/javascript" src="<cc:resUrl value="/js/nav/superfish.js" />"></script>
	<script type="text/javascript" src="<cc:resUrl value="/js/nav/supersubs.js" />"></script>
	<script type="text/javascript" src="<cc:resUrl value="/js/nav/jquery.bgiframe.min.js" />"></script>
	<script type="text/javascript" src="<c:url value="/jquery/jquery.textarearesizer.compressed.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/jquery/select2/select2.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/jquery/jquery.floatThead.js"/>"></script>
	
	<script type="text/javascript" src="<c:url value="/js/diff_match_patch.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.pretty-text-diff.js"/>"></script>
	
	<script type="text/javascript" src="<c:url value="/js/cims.js" />"></script>
	<script type="text/javascript" src="<c:url value="/js/ajax-util.js" />"></script>
	<script type="text/javascript" src="<c:url value="/js/feedback.js" />"></script>
	<script type="text/javascript" src="<c:url value="/js/event-manager.js" />"></script>
	<%
		// If the "titleKey" parameter has been set, then we should use that, otherwise set a default
		String TITLE_KEY_PARAM_NAME = "titleKey";
		String DEFAULT_TITLE_KEY = "cims.common.header";
		
		String keyParam = request.getParameter(TITLE_KEY_PARAM_NAME);
	
		if(keyParam == null || keyParam.trim().equals("")) {
			request.setAttribute(TITLE_KEY_PARAM_NAME, DEFAULT_TITLE_KEY);
		} else {
			request.setAttribute( TITLE_KEY_PARAM_NAME, keyParam );
		}
	%>
	<title><fmt:message key="${titleKey}" /></title>	
</head>