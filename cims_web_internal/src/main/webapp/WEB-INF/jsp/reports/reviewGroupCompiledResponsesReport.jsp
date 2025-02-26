<!DOCTYPE html>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<html style="height: 100%;">
<%@ include file="/WEB-INF/jsp/common/common-header.jsp"%>

<style type="text/css" media="all">
input[type="text"] {
     width: 100%; 
     box-sizing: border-box;
     -webkit-box-sizing:border-box;
     -moz-box-sizing: border-box;
}
</style>

<script src="<c:url value="/js/displayTagService.js"/>"></script>
<script type="text/javascript">

    $( window ).load(function() {
	$("*").css("cursor", "auto");
	$('.modal').remove();
    });
    
</script>

<div class="content" style="border: 0;">
<fieldset>
<legend>Review Group Compiled Responses</legend>

    <table style="width: 98%; margin-top: 20px; margin-left:10px; border-collapse: collapse;">
        <tr>
            <td style="font-weight:bold; width: 15%; text-align:right;"><span id="classificationLabel">Classification:</span></td>
            <td style="width: 10%;">${reportData.classification}</td>
            <td style="font-weight:bold; width: 35%; text-align:right;"><span id="patternTopicLabel">Pattern Topic:</span></td>
            <td style="width: 40%;">${reportData.patternTopic}</td>
        </tr>
        <tr>
            <td style="font-weight:bold; width: 15%; text-align:right;"><span id="versionsLabel">Year:</span></td>
            <td style="width: 10%;">${reportData.year}</td>
            <td style="font-weight:bold; width: 35%; text-align:right;"><span id="fromLabel">Code Value From:</span></td>
            <td style="width: 40%;">${reportData.valueFrom}</td>
        </tr>
        <tr>
            <td style="font-weight:bold; width: 15%; text-align:right;"><span id="requestCategoryLabel">Request Category:</span></td>
            <td style="width: 10%;">${reportData.requestCategory}</td>
            <td style="font-weight:bold; width: 35%; text-align:right;"><span id="toLabel">Code Value To:</span></td>
            <td style="width: 40%;">${reportData.valueTo}</td>
        </tr>
        <tr>
            <td style="font-weight:bold; width: 15%; text-align:right;"><span id="languageLabel">Language:</span></td>
            <td style="width: 10%;">${reportData.language}</td>
            <td style="font-weight:bold; width: 35%; text-align:right;"><span id="indexBookLabel">Index Book:</span></td>
            <td style="width: 40%;">${reportData.indexBook}</td>
        </tr>
        <tr>
            <td style="font-weight:bold; width: 15%; text-align:right;"><span id="reviewGroupLabel">Review Group:</span></td>
            <td style="width: 10%;">${reportData.reviewGroup}</td>
            <td style="font-weight:bold; width: 35%; text-align:right;"><span id="leadIndexTermLabel">Lead Index Term:</span></td>
            <td style="width: 40%;">${reportData.leadIndexTerm}</td>
        </tr>
        <tr><td>&nbsp;<td><td>&nbsp;<td><td>&nbsp;<td><td>&nbsp;<td></tr>
        <tr><td>&nbsp;<td><td>&nbsp;<td><td>&nbsp;<td><td>&nbsp;<td></tr>
        
    </table>

<c:if test="${not empty reportDetail}">  
  <c:choose>
    <c:when test="${resultSize>1000}">
        More than 1000 records are found.  Showing first 1000 records.
    </c:when>
    <c:otherwise>
        ${resultSize} records are found. 
    </c:otherwise>
  </c:choose>
</c:if>

    <display:table name="reportDetail" id="reportDetail" defaultsort="2" defaultorder="ascending" requestURI="" 
		   size="resultSize" class="listTable" style="width: 100%; margin-top: 0px;" sort="list">

	<display:column sortable="true" titleKey="cims.reports.reviewgroup.compiled.responses.changerequestid" sortProperty="changeRequestId" 
	                headerClass="tableHeader" style="word-wrap:break-word; width:12%;">
			${reportDetail.changeRequestId}
	</display:column>

	<display:column sortable="true" titleKey="cims.reports.reviewgroup.compiled.responses.changerequestname" sortProperty="changeRequestName" 
	                headerClass="tableHeader" style="word-wrap:break-word; width:18%;">
			${reportDetail.changeRequestName}
	</display:column>

<%--
	<display:column sortable="true" titleKey="cims.reports.reviewgroup.compiled.responses.questionid" sortProperty="questionForReviewerId" 
	                headerClass="tableHeader" style="word-wrap:break-word; width:7%;">
			${reportDetail.questionForReviewerId}
	</display:column>
--%>

	<display:column sortable="false" titleKey="cims.reports.reviewgroup.compiled.responses.question" 
	                headerClass="tableHeader" style="word-wrap:break-word; width:35%;">
			${reportDetail.questionForReviewerTxt}
	</display:column>

<%--
	<display:column sortable="false" titleKey="cims.reports.reviewgroup.compiled.responses.responseid" 
	                headerClass="tableHeader" style="word-wrap:break-word; width:7%;">
			${reportDetail.responseId}
	</display:column>
--%>

	<display:column sortable="false" titleKey="cims.reports.reviewgroup.compiled.responses.response" 
	                headerClass="tableHeader" style="word-wrap:break-word; width:35%;">
			${reportDetail.response}
	</display:column>

    </display:table>


</fieldset>
</div>

</html>
