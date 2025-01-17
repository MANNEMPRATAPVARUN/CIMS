<!DOCTYPE html> 
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<html>
<head>
<meta http-equiv="x-ua-compatible" content="IE=Edge"/> 
<meta http-equiv="Content-Type" content="${CONTENT_TYPE}">

 <title>
	 	<fmt:message key="classification.viewer.title" />
 </title>

<link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/screen.css" />" type="text/css" media="screen, projection" />
<link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/print.css" />" type="text/css"  media="print" />
 
<link rel="stylesheet" type="text/css" href="<cc:resUrl value="/css/main.css" />">
<link rel="stylesheet" type="text/css" href="<c:url value="/css/cims.css"/>" >
<link rel="stylesheet" type="text/css" href="<cc:resUrl value="/css/jquery/jquery-ui.css" />">

<script type="text/javascript" src="<cc:resUrl value="/js/jquery/jquery.js" />"></script>
<script type="text/javascript" src="<cc:resUrl value="/js/jquery/jquery-ui-1.8.10.custom.min.js" />"></script>

<script type="text/javascript" src="<c:url value="/js/cims.js" />"></script>
<script type="text/javascript" src="<c:url value="/js/xmltemplates.js" />"></script>
<script type="text/javascript" src="<c:url value="/js/diagrams.js" />"></script>

<!--[if IE]>
<script src="js/html5.js"></script>
<![endif]-->


<style type="text/css">
	html,body{
		height:100%;width:100%; 
		margin:0;padding:0;overflow: hidden;
	}
	#viewerheader{background:white; height: 100px;}
	#splitterContainer {
		height:calc(100% - 100px);
	}
</style>

<jsp:include page="../classification/view/classificationViewer-include.jsp"/>
</head>
<body>
	<div id="viewerheader" >
		<jsp:include page="changeRequest-headerAndTabs.jsp"/>
		<jsp:include page="manageClassification-search.jsp"/>
	</div>
	<jsp:include page="../classification/view/classificationViewer.jsp">
	    <jsp:param name="classification" value="${changeContext.baseClassification}" />
		<jsp:param name="contextId" value="${changeContext.contextId}" />
		<jsp:param name="language" value="${language}" />
		<jsp:param name="changeRequestId" value="${changeRequestDTO.changeRequestId}" />
		<jsp:param name="viewMode" value="${viewMode}" />
	</jsp:include>
</body>
</html>
