<html>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<link
	rel="stylesheet"
	href="<cc:resUrl value="/css/blueprint/screen.css" />"
	type="text/css"
	media="screen, projection"
/>
<link
	rel="stylesheet"
	href="<cc:resUrl value="/css/blueprint/print.css" />"
	type="text/css"
	media="print"
/>
<!--[if IE]><link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/ie.css" />" type="text/css" media="screen, projection" /><![endif]-->

<link
	rel="stylesheet"
	media="screen"
	href="<cc:resUrl value="/css/nav/superfish-dropdown.css" />"
>
<link
	rel="stylesheet"
	type="text/css"
	href="<cc:resUrl value="/css/main.css" />"
>
<script
	type="text/javascript"
	src="<cc:resUrl value="/js/jquery/jquery.js" />"
></script>
<script
	type="text/javascript"
	src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.11/jquery-ui.min.js"
></script>
<link
	rel="stylesheet"
	href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/themes/ui-lightness/jquery-ui.css"
/>
<link
	rel="stylesheet"
	type="text/css"
	href="<cc:resUrl value="/css/jquery/jquery-ui.css" />"
>
<style>
.ui-progressbar {
	position: relative;
}

#progresslabel {
	_position: absolute;
	_top: 4px;
	font-weight: bold;
	text-shadow: 1px 1px 0 #fff;
	_left: 40%;
	_width: 50%;
	_display: table;
	_margin: 0 auto;
	_display: inline-block;
	text-align: center;
}

#progressbar{
	text-align: center;
	margin: 0 auto;
}

</style>
<script>
	function progress(p, text){
		$("#progresslabel").text(text);
		$("#progressbar").progressbar({value:p});
	}
	<c:if test="${empty concurrentError}">
	$(function(){
		progress(0, "Initializing...");poll();
	});
	</c:if>
	<c:if test="${not empty concurrentError}">
	$(function(){
		$('#dialog_div', parent.document.body).dialog("option", { height: 140 } );
		$("#progresslabel").text('<spring:message code="change.request.concurrent.update"/>').addClass("error");
	});
	</c:if>

	function schedulePoll(){
		setTimeout(function() { poll(); }, 500);
	}
	
	function poll() {
		   $.ajax({
		       	url: '<c:url value="/synchronization/status.htm?ccp_rid=${param.ccp_rid}"/>',
				dataType: "json",
				cache: false,
		     	success: function(s){
			     	if(s.instanceId!=${instanceId}){
			     		alert('Cluster failed. Please restart synchronization.');
				    }else{
				     	if(s.total==-1){
				     		progress(100, "Complete");
				     		$('#crLastUpdatedTime', parent.document.body).text(s.lockTimestamp);
					    }else{
					     	if(s.error!=null){
					     		progress(100, "Error: " + s.error);
						    }else{
					       		if(s.current!=0){
					       			var p=(s.current-1)/s.total*100;
					       			var code=s.currentCode;
					       			if(code.lenght>20){
						       			code= code.substring(0,19)+ "...";
						       		}
									progress(p, code+" ("+s.current+" out of "+s.total+")");
								}
					       		schedulePoll();	
						    }
					    }
				    }
				},
				error:function(){
					alert('error');
				}
		    });
		}
</script>
<body>
<div id="progressbar">
	<div id="progresslabel">Loading...</div>
</div>
</body>
</html>
