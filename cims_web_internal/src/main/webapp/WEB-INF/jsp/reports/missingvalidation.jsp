
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<style type="text/css">
<!--
-->
</style>
<script language="JavaScript" type="text/javascript" >
	
	$(function() {
		var classification = $('#classification').val();
		changeClassification(classification);
		$('#ccp_bc').val(classification);
		$("#classification").change( function () {
			classification=$(this).val();
			$('#ccp_bc').val(classification);
			changeClassification(classification);
			
		});
		$('#year').change(function () {
            $('#codeFrom').val("");
            $('#codeTo').val("");
            classification = $('#classification').val();
            var contextId=$('#year option:selected').attr('rel');
            $('#ccp_cid').val(contextId);
			$.ajax({
               	url:"${pageContext.request.contextPath}/reports/missingValidation/selectVersionCode.htm?ccp_on=Y&ccp_bc="+classification+"&ccp_cid="+contextId,
               	success: function(data){
               		var dataHoldings = '';
               		for (var i=0; i< data.length; i++){
               			dataHoldings += '<option value="'+data[i].code+'">'+data[i].title+'</option>';
               		}
               		$('#dataHoldingCode').html(dataHoldings);
               		$("#dataHoldingCode").val( '1' ).attr('selected',true);
               		$('#dataHolding').val($('#dataHoldingCode option:selected').text());
               	}
			});
		});
		
		$('#dataHoldingCode').change(function(){
			$('#dataHolding').val($('#dataHoldingCode option:selected').text());
		});
		
		$('#codeFrom').autocomplete({
			// it is very important to use contentType with charset=UTF-8, otherwise french characters won't be properly handled by the search
			source: function(request, response) {
		        $.ajax({
		            url: "${pageContext.request.contextPath}/reports/searchCodeValues.htm?classification="+$('#classification').val(),
		            contentType:"application/json; charset=UTF-8",
		            data: {
		              term : request.term,
		              ccp_cid : $('#ccp_cid').val()
		            },
		            success: function(data) {
		              response(data);
		            }
		          });
			}
		});
		
		$('#codeTo').autocomplete({
			// it is very important to use contentType with charset=UTF-8, otherwise french characters won't be properly handled by the search
			source: function(request, response) {
		        $.ajax({
		            url: "${pageContext.request.contextPath}/reports/searchCodeValues.htm?classification="+$('#classification').val(),
		            contentType:"application/json; charset=UTF-8",
		            data: {
		              term : request.term,
		              ccp_cid : $('#ccp_cid').val()
		            },
		            success: function(data) {
		              response(data);
		            }
		          });
			}
		});
	});
	
	function download(){
		var classification=$('#classification').val();
		if(classification=="CCI"){
			$('#reportType').val('MissingValidationCCI');
		}else{
			$('#reportType').val('MissingValidationICD-10-CA');
		}
		var validationUrl = '<c:url value="/reports/missingValidation/validate.htm"/>';
		var generationUrl = '<c:url value="/reports/generate.htm"/>';
		var checkUrl = '<c:url value="/reports/checkDownloadProgress.htm"/>';
		generateReport(validationUrl, generationUrl,checkUrl);
	}
	
	function changeClassification(classification){
		$.ajax({
            url: "${pageContext.request.contextPath}/reports/missingValidation/selectClassification.htm?baseClassification="+classification,
            success: function(data) {
            	var options = '';
                for (var i = 0; i < data.length; i++) 
                {
                    options += '<option value="' + data[i].versionCode + '" rel="'+data[i].contextId+'">' + data[i].versionCode + '</option>';
                }
                $('#year').html(options);
                $('#codeFrom').val("");
                $('#codeTo').val("");

                var contextId=$('#year option:first-child').attr('rel');
                $('#ccp_cid').val(contextId);
                $.ajax({
                	url:"${pageContext.request.contextPath}/reports/missingValidation/selectVersionCode.htm?ccp_on=Y&ccp_bc="+classification+"&ccp_cid="+contextId,
                	success: function(data){
                		var dataHoldings = '';
                		for (var i=0; i< data.length; i++){
                			dataHoldings += '<option value="'+data[i].code+'">'+data[i].title+'</option>';
                		}
                		$('#dataHoldingCode').html(dataHoldings);
                		$("#dataHoldingCode").val( '1' ).attr('selected',true);
                   		$('#dataHolding').val($('#dataHoldingCode option:selected').text());
                	}
                });
            }
          });
	}
</script>

<h4 class="contentTitle">Reports > Missing Validation Report</h4> 

<div class="content">
<form:form method="POST" modelAttribute="reportViewBean" id="viewForm">
	<ul class="errorMsg" id="errorMessages"></ul>
	<form:hidden path="ccp_bc" id="ccp_bc"/>
	<form:hidden path="ccp_cid" id="ccp_cid"/>
	<form:hidden path="dataHolding" id="dataHolding"/>
	<form:hidden path="reportType" id="reportType"/>
<fieldset id="reportCriteria">
   <legend>Missing Validation Report</legend>
	<fieldset>
		<table>
			<tr>
				<td width="10%"><span class="required">*</span><label>Classification:</label></td>
				<td width="20%">
					<form:select  path="classification" id="classification">
					   <c:forEach var="baseClassification" items="${baseClassifications}">
			              <form:option value="${baseClassification}"> ${baseClassification}</form:option>
			           </c:forEach>
					</form:select>
				</td>
				<td width="5%"><span class="required">*</span><label>Year:</label></td>
				<td width="10%">
					<form:select path="year" id="year">
	    			</form:select>
				</td>
				<td width="55%">&nbsp;</td>
			</tr>
			<tr>
				<td><span class="required">*</span><label>Data Holding:</label></td>
				<td>
					<form:select path="dataHoldingCode" id="dataHoldingCode"></form:select>
				</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td colspan="2">
				<table>
					<tr>
						<td><span class="required">*</span><label>Code Range:</label></td><td><label>From:</label></td><td><form:input  path="codeFrom" size="7" maxLength="7"/></td><td><label>To:</label></td><td><form:input  path="codeTo" size="7" maxLength="7"/></td>
					</tr>
				</table>
				</td>
				<td colspan="3">&nbsp;</td>
			</tr>
		</table>
	 	
		<div>
		<input class="button" type="button" value="<fmt:message key='reports.view'/>" onclick="download();"/>
		</div>
	</fieldset>
</fieldset>
</form:form>
<div id="loadingInfo" class="info" style="display: none; margin-bottom: 0.1em; width: 800px; padding: 0.2em;">Loading</div>
</div>