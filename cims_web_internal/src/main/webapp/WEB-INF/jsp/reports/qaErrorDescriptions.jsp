
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<style type="text/css">
</style>

<script language="JavaScript" type="text/javascript" >

    function changeClassification(){		
		var classification = $('#classification').val();
		$.ajax({
            url: "${pageContext.request.contextPath}/reports/qaErrorDescriptions/getBaseContextYears.htm?baseClassification="+classification,
            success: function(data) {
            	var options = '';
            	//alert('data.length=' + data.length);
                for (var i = 0; i < data.length; i++) 
                {
                    options += '<option value="' + data[i] +'">' + data[i] + '</option>';
                    //options += '<option value="' + data[i].versionCode + '" rel="'+data[i].contextId+'">' + data[i].versionCode + '</option>';
                    //options += '<option value="' + data[i].versionCode + '">' + data[i].versionCode + '</option>';
                }
                $('#year').html(options);
            }
            
		
            });

	    $('#languageDesc').val($('#language option:selected').text());
	    //$('#owner').val('');
	    $('#ownerUserName').val($('#owner option:selected').text());

	    $('#statusFromCode').val($('#statusFrom option:selected').text());

	    //$('#dateFrom').val('');
	    //$('#dateTo').val('');
	    //$('#statusFrom').val('');
	    //$('#qaErrorCount').val('');

	}

	function download(){
		$('#reportType').val('QAErrorDescriptions');
		var validationUrl = '<c:url value="/reports/qaErrorDescriptions/validate.htm"/>';
		//var generationUrl = '<c:url value="/reports/generate.htm"/>';
		var generationUrl = '<c:url value="/reports/qaErrorDescriptions/generateQAErrorDescriptionsReport.htm"/>';
		var checkUrl = '<c:url value="/reports/checkDownloadProgress.htm"/>';
		generateReport(validationUrl, generationUrl,checkUrl);
	}



	
	function changeLanguage(){
		$('#languageDesc').val($('#language option:selected').text());
	}

	function changeOwner(){
		$('#ownerUserName').val($('#owner option:selected').text());
	}

	function changeStatus(){
		$('#statusFromCode').val($('#statusFrom option:selected').text());
	}

	$(function() {

                $( "#dateFrom" ).datepicker();
                $( "#dateTo" ).datepicker();

		//refreshForm();
		changeClassification();

	});

</script>

<h4 class="contentTitle">Reports > Send Back Detail Report</h4> 

<div class="content">

<fieldset id="reportCriteria">
   <legend>Send Back Detail Report</legend>

<form:form modelAttribute="reportViewBean" id="viewForm">
	<ul class="errorMsg" id="errorMessages"></ul>
	<%--<form:hidden path="ccp_cid" id="ccp_cid"/>--%>
	<form:hidden path="languageDesc" id="languageDesc"/>
	<form:hidden path="ownerUserName" id="ownerUserName"/>
	<form:hidden path="statusFromCode" id="statusFromCode"/>
	<form:hidden path="reportType" id="reportType"/>

	<fieldset>	
	    <table>
		    <tr>
			    <td width="10%">
				<label for="classification">Classification:</label>
				</td width="15%">
				<td>
				<%--<form:select  path="classification" onchange="refreshForm();" id="classification">--%>
				<form:select  path="classification" onchange="changeClassification();" id="classification">
		            <form:option value=""></form:option>
				    <c:forEach var="baseClassification" items="${baseClassifications}">
		                <form:option value="${baseClassification}"> ${baseClassification}</form:option>
		            </c:forEach>
				</form:select>		
				</td>
		
				<td width="5%">
				<label for="year"><span class="required">*</span>Year:</label>	
				</td>
				<td width="15%">	
				<form:select path="year" id="year">
			        </form:select>
			    </td>
			    <td width="55%">&nbsp;</td>
		    </tr>	

		    <tr>
			    <td>
			        <label for="language">Language:</label>	
			    </td>
			    <td>	
					<form:select path="language" onchange="changeLanguage();" id="language">
		                    <form:option value=""></form:option>
		                    <c:forEach var="language" items="${languages}">
		                        <form:option value="${language.languageCode}">${language.languageDesc}</form:option>
		                    </c:forEach>
			        </form:select>
				</td>
				<td>
				<label for="owner">Owner:</label>	
				</td>
				<td>	
				<form:select  path="owner" onchange="changeOwner();" id="owner">
		                    <form:option value=""></form:option>
				            <c:forEach var="owner" items="${owners}">
		                      <form:option value="${owner.userProfileId}"> ${owner.userName}</form:option>
		                    </c:forEach>
				</form:select>	
				</td>	
				<td>&nbsp;</td>
		    </tr>	

		    <tr>
		    	<td>
				<label for="fromDateLabel">From Date:</label>	
				</td>
				<td>	
				<form:input  path="dateFrom" size="10" maxLength="7" id="dateFrom"/>
				</td>	
				<td>
				<label for="toDateLabel">To Date:</label>
				</td>
				<td>		
				<form:input  path="dateTo" size="10" maxLength="7" id="dateTo"/>
				</td>
				<td>&nbsp;</td>
		    </tr>	
	
		    <tr>
		    	<td>
				<label for="fromStatusLabel">From Status:</label>		
				</td>
				<td>
				<form:select path="statusFrom" onchange="changeStatus();" id="statusFrom">
		                    <form:option value=""></form:option>
		                    <c:forEach var="fromStatus" items="${fromStatuses}">
		                        <form:option value="${fromStatus}">${fromStatus}</form:option>
		                    </c:forEach>
			        </form:select>
			    <td colspan="3">&nbsp;</td>
		    </tr>	
		</table>
		<div>
		<input class="button" type="button" value="<fmt:message key='cims.reports.qaerror.descriptions.submitButton'/>" onclick="download();"/>
		</div>
		</fieldset>
</form:form>

<div id="loadingInfo" class="info" style="display: none; margin-bottom: 0.1em; width: 800px; padding: 0.2em;">Loading</div>
</div>