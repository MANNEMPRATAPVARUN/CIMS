<!DOCTYPE html> 
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<html>
<head>
<meta http-equiv="x-ua-compatible" content="IE=Edge"/> 
<meta http-equiv="Content-Type" content="${CONTENT_TYPE}">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<!-- The following meta is due to a confirmed IE8 bug that manifests itself when resizing the window and the iframes are resized properly.
--Note that when we move to next IE version hopefully we can remove it.
-->

 <title>
	 	<fmt:message key="classification.viewer.title" />
 </title>
 
<link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/screen.css" />" type="text/css" media="screen, projection" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/cims.css"/>" >
<link rel="stylesheet" type="text/css" href="<cc:resUrl value="/css/jquery/jquery-ui.css" />">

<!-- JQuery dependencies, always include -->
<script type="text/javascript" src="<cc:resUrl value="/js/jquery/jquery.js" />"></script>
<script type="text/javascript" src="<cc:resUrl value="/js/jquery/jquery-ui-1.8.10.custom.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/jquery/jquery.cookie.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/cims.js" />"></script>

<!--[if IE]>
<script src="js/html5.js"></script>
<![endif]-->


<style type="text/css">
	html,body{
		height:100%;width:100%; 
		margin:0;padding:0;overflow: hidden;
	}
	#viewerheader {
		height: 35px;
	}
	#splitterContainer {
		height:calc(100% - 35px);
	}
</style>

<jsp:include page="classificationViewer-include.jsp"/>
</head>
<body>
	<div id="viewerheader" >
		<jsp:include page="conceptSearchBox.jsp">
			<jsp:param name="classification" value="${viewerModel.classification}" />
			<jsp:param name="contextId" value="${viewerModel.contextId}" />
			<jsp:param name="language" value="${viewerModel.language}" />
			<jsp:param name="parentPage" value="viewClassification.htm" />
			<jsp:param name="showLanguageOptions" value="true" />
		</jsp:include>
	</div>
	<jsp:include page="classificationViewer.jsp">
		<jsp:param name="classification" value="${viewerModel.classification}" />
		<jsp:param name="contextId" value="${viewerModel.contextId}" />
		<jsp:param name="language" value="${viewerModel.language}" />
		<jsp:param name="changeRequestId" value="${changeRequestDTO.changeRequestId}" />
		<jsp:param name="viewMode" value="${viewMode}" />
	</jsp:include>
</body>
</html>
