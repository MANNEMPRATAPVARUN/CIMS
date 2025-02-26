<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<h4 class="contentTitle">${title}</h4>
<div class="content" id="content">
	<div id="feedback" style="display: none;"></div>
	<div id="dialog_div" style="display: none;"></div>
	<tiles:insertAttribute name="contentBody" /> 
</div>
<script>
	var feedbackMessage = "${feedbackMessage}";
	if(feedbackMessage != "") {
		Feedback.success(feedbackMessage);
	}
</script> 