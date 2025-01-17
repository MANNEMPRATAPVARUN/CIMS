
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<script language="JavaScript" type="text/javascript" >
$(function() {
	//initialize date pickers
	$(".datepicker").datepicker({dateFormat: "yy-mm-dd", changeYear: true, changeMonth: true});
	var classification = $('#classification').val();
	changeClassification(classification);
	$("#classification").change( function () {
		classification=$(this).val();
		changeClassification(classification);
		
	});
});
	function download(){
		var validationUrl = '<c:url value="/reports/sendBackDetail/validate.htm"/>';
		var generationUrl = '<c:url value="/reports/generate.htm"/>';
		var checkUrl = '<c:url value="/reports/checkDownloadProgress.htm"/>';
		generateReport(validationUrl, generationUrl,checkUrl);
	}
	
	function changeClassification(classification){
		$.ajax({
            url: "${pageContext.request.contextPath}/reports/sendBackDetail/selectClassification.htm?baseClassification="+classification,
            success: function(data) {
            	var options = '';
                for (var i = 0; i < data.length; i++) 
                {
                    options += '<option value="' + data[i].versionCode + '" rel="'+data[i].contextId+'">' + data[i].versionCode + '</option>';
                }
                $('#year').html(options);
            }
          });
	}
</script>

<h4 class="contentTitle">Reports > Send Back Detail</h4> 

<div class="content">

<form:form method="POST" modelAttribute="reportViewBean" id="viewForm">
	<input type="hidden" name="reportType" value="SendBackDetail"/>
	<ul class="errorMsg" id="errorMessages"></ul>
<fieldset>
   <legend>Send Back Detail Report</legend>
	 	<div class="span-9 inline form-row">
			<div class="span-5 label">
				<div><label for="classification"><fmt:message key="reports.classification"/>:</label></div>
				<div><label for="owner" ><fmt:message key="reports.owner"/>:</label></div>
			</div>
			<div class="span-4 last">
				<div>
					<form:select  path="classification" id="classification">
					   <c:forEach var="baseClassification" items="${baseClassifications}">
			              <form:option value="${baseClassification}"> ${baseClassification}</form:option>
			           </c:forEach>
					</form:select>
				</div>
				<div>
					<form:select id="owner" path="owner">.
						<form:option value=""></form:option>
						<c:forEach var="owner" items="${users}">
			              <form:option value="${owner.username}"> ${owner.username}</form:option>
			           </c:forEach>
					</form:select>
				</div>
			</div>
		</div>
		
		<div class="span-8 inline form-row">
			<div class="span-4 label">
				<div><label for="language"><fmt:message key="reports.language"/>:</label></div>
				<div><label for="fromStatus" ><fmt:message key="reports.fromStatus"/>:</label></div>
			</div>
			<div class="span-4 last">
				<div>
					<form:select  path="language" id="language">
					   <form:option value=""></form:option>
					   <form:options items="${languages}" itemLabel="label" itemValue="label"/>
					</form:select>
				</div>
				<div>
					<form:select  path="fromStatus" id="fromStatus">
					   <form:option value=""></form:option>
					   <form:options items="${statuses}" itemLabel="name" itemValue="statusCode"/>
					</form:select>
				</div>
			</div>
		</div>
	 	
		<div class="span-7 inline form-row">
			<div class="span-3 label">
				<div><label for="year"><fmt:message key="reports.year"/>:</label></div>
			</div>
			<div class="span-4 last">
				<div>
					<form:select path="year" id="year">
						
				    </form:select>
				</div>
			</div>
		</div>
		<div class="span-24 inline form-row">
			<div class="span-5 label">
				<label for="datarange"><fmt:message key="reports.status.daterange"/>:</label>
			</div>
			<div class="last">
				<div class="inline" style="padding-right: 10px;">
					<label for="fromDate"><fmt:message key="reports.fromdate"/>:&nbsp;</label><form:input id="fromDate" path="fromDate" class="datepicker" cssErrorClass="datepicker fieldError"/>
				</div>
				<div class="inline">
					<label for="toDate"><fmt:message key="reports.todate"/>:&nbsp;</label><form:input id="toDate" path="toDate" class="datepicker" cssErrorClass="datepicker fieldError"/>
				</div>
			</div>
		</div>
		<div class="span-24 inline form-row">
		<input class="button" type="button" value="<fmt:message key='reports.view'/>" onclick="download();"/>
		</div>
</fieldset>
</form:form>
<div id="loadingInfo" class="info" style="display: none; margin-bottom: 0.1em; width: 800px; padding: 0.2em;">Loading</div>
</div>