<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<style type="text/css" media="all">
iframe {
    border: none;
    outline: none;
    overflow: visible;
}
</style>
<script type="text/javascript">
	function resizeIframe(iframe) {
		iframe.height = iframe.contentWindow.document.body.scrollHeight + "px";
	}

</script>

<h4 class="contentTitle">
	<fmt:message key="cims.menu.home"/> &#62; 
	${currentUser.firstname}&nbsp;${currentUser.lastname} 
</h4>

<div class="content">

	<div id="myHomeContainer">
	<!-- 	<div id="welcome"> -->
	<%-- 		&nbsp;&nbsp;&nbsp;&nbsp;Welcome : ${currentUser.firstname}&nbsp;${currentUser.lastname} --%>
	<!-- 		<br/><br/>		 -->
	<!-- 	</div>	 -->
	
	   	<div id="notificationList">
		    <iframe name="notifications" src="/cims_web_internal/myNotifications.htm"
				scrolling="no" frameBorder="0" width="100%" onload="resizeIframe(this)">
			</iframe>
		</div>
		<div id="changerequestList" >
			<iframe name="changerequests" src="/cims_web_internal/myChangeRequests.htm?"
				scrolling="no" frameBorder="0" width="100%" onload="resizeIframe(this)">
			</iframe>
		 </div>
		 <div id="assignedRefsetList" >
			<iframe name="assignedRefset" src="/cims_web_internal/myAssignedRefset.htm"
				scrolling="no" frameBorder="0" width="100%" onload="resizeIframe(this)">
			</iframe>
		 </div>
		 <div id="searchResultList">
		 	<iframe id ="searchResultsIframe" name="searchResults" src="/cims_web_internal/searchResults.htm"
		 		scrolling="no" frameBorder="0" width="100%" onload="resizeIframe(this)">
		 	</iframe>
		 </div>
		 
	</div>

</div>