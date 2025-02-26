<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=10">
	<meta http-equiv="Content-Type" content="${CONTENT_TYPE}">
		<!--Blueprint Framework CSS -->
	<link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/screen.css" />" type="text/css" media="screen, projection" />
	<link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/print.css" />" type="text/css"  media="print" />
	<!--[if IE]><link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/ie.css" />" type="text/css" media="screen, projection" /><![endif]-->
	<link rel="stylesheet" type="text/css" href="<cc:resUrl value="/css/main.css" />">
	<link rel="stylesheet" type="text/css" href="<c:url value="/css/cims.css"/>" >
	
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
	<script src="js/displayTagService.js"></script>
	<script type="text/javascript">

		$(document).ready(function() {
			parent.changeEditMode(false);			
			window.parent.hideMessage();
			window.parent.hideProcessingScreen();
			window.parent.processingSomething = false;
			window.parent.greyButtons();
			modifyPageLinks();
			showMessageBox();
		});

	</script>
	<style type="text/css" media="all">
		img {border : 0;}
		.header  {
		    font-weight: bold;
		    font-size: 1.2em;
		}
		
		.section-header {
		    font-weight: bold;
		    font-size: 1.1em;
		}
		
		.label{
			font-weight: bold;
		}
	</style>
</head>