<%@ include file="/WEB-INF/jsp/common/include.jsp"%>	
<link href="css/jquery.splitter.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/skin-vista/ui.dynatree.css"/>">

<script src="js/jquery.splitter-0.14.0.js"></script>
<script type="text/javascript" src="<c:url value="/jquery/jquery.dynatree.js"/>"></script>
<script type="text/javascript" src="<c:url value="/jquery/jquery.history.js" />"></script>
<script type="text/javascript" src="<c:url value="/jquery/jquery.textarearesizer.compressed.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/explorer.js" />"></script>
<script type="text/javascript" src="<c:url value="/js/contents.js" />"></script>
<script type="text/javascript" src="<c:url value="/js/ajax-util.js" />"></script>
<script type="text/javascript" src="<c:url value="/js/content-controller.js" />"></script>
<script type="text/javascript" src="<c:url value="/js/event-manager.js" />"></script>
<script type="text/javascript" src="<c:url value="/js/navigation-controller.js" />"></script>
<script type="text/javascript" src="<c:url value="/js/feedback.js" />"></script>

<style type="text/css" media="all">
	#splitterContainer {
		overflow: initial;
		width:100%;
		margin:0;
		padding:0;
	}
	.vsplitter {
		background-color: #6FA6A1 !important;
	}
	#leftPane{
		float:left;
		width:30%;
		height:calc(100% - 1px);
		border-top: 1px solid #6FA6A1;
	}
	#rightPane{	/*Contains toolbar and horizontal splitter*/
		float:right;
		width:70%;
		height:calc(100% - 1px);
		border-top: 1px solid #6FA6A1;
	}
	#tree {
		height: 100%; 
		overflow: auto;
	}
	#content {
		height: 100%;
		overflow: auto;
		padding-left: 5px;
		padding-right: 5px;
	}
</style>