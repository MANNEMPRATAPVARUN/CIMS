
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<style type="text/css">

 tr .index{
 	display : none;
 }
 
</style>

<script language="JavaScript" type="text/javascript" >
	
	function refreshForm(){
		var classification = $('#classification').val();
		var requestCategory = $('#requestCategory').val();
		if(requestCategory=="Tabular"){
			$('.index').hide();
			$('.tabular').show();
			$("#indexBook").val('');
			$('#leadTerm').val('');
			$('#codeFrom').val('');
			$('#codeTo').val('');
		}else{
			$('.index').show();
			$('.tabular').hide();
			$('#leadTerm').val('');
			$('#codeFrom').val('');
			$('#codeTo').val('');
			$.ajax({
	            url: "${pageContext.request.contextPath}/reports/listIndexBooks.htm?classification="+classification,
	            success: function(data) {
	            	var options = '';
	                for (var i = 0; i < data.length; i++) 
                    {
                        options += '<option value="' + data[i].code + '">' + data[i].description + '</option>';
                    }
	                $('#indexElementId').html(options);
	                $("#indexBook").val($("#indexElementId option:first-child").text());
	            }
	          });
		}
	}
	
	function download(){
		var requestCategory = $('#requestCategory').val();
		if(requestCategory=="Tabular"){
			$('#reportType').val('ClassificationChangeTabular');
		}else{
			$('#reportType').val('ClassificationChangeIndex');
		}
		var validationUrl = '<c:url value="/reports/classificationChange/validate.htm"/>';
		var generationUrl = '<c:url value="/reports/generate.htm"/>';
		var checkUrl = '<c:url value="/reports/checkDownloadProgress.htm"/>';
		generateReport(validationUrl, generationUrl,checkUrl);
	}
	
	function changeIndexBook(){
		$('#indexBook').val($('#indexElementId option:selected').text());
		$('#leadTerm').val('');
	}
	$(function() {
		refreshForm();
		$("#leadTerm")
				.autocomplete(
						{
							// it is very important to use contentType with charset=UTF-8, otherwise french characters won't be properly handled by the search
							source: function(request, response) {
						        $.ajax({
						            url: "${pageContext.request.contextPath}/reports/searchIndexBooks.htm?classification="+$('#classification').val(),
						            contentType:"application/json; charset=UTF-8",
						            data: {
						              term : request.term,
						              indexElementId: $( "#indexElementId" ).val()
						            },
						            success: function(data) {
						              response(data);
						            }
						          });
							},
							select : function(event, ui) {
								$('#leadTermElementId').val(ui.item.conceptId);								
							}
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
</script>

<h4 class="contentTitle">Reports > Classification Change Report</h4> 

<div class="content">

<form:form modelAttribute="reportViewBean" id="viewForm">
	<ul class="errorMsg" id="errorMessages"></ul>
	<form:hidden path="indexBook" id="indexBook"/>
	<form:hidden path="leadTermElementId" id="leadTermElementId"/>
	<form:hidden path="ccp_cid" id="ccp_cid"/>
	<form:hidden path="reportType" id="reportType"/>
<fieldset id="reportCriteria">
   <legend>Classification Change Report</legend>
   <fieldset>
	 <table>
	 	<tr>
			<td width="12%"><span class="required">*</span><label>Classification:</label></td>
			<td width="15%">
				<form:select  path="classification" id="classification" onchange="refreshForm();">
				   <c:forEach var="baseClassification" items="${baseClassifications}">
		              <form:option value="${baseClassification}"> ${baseClassification}</form:option>
		           </c:forEach>
				</form:select>
			</td>
			<td width="20%"><span class="required">*</span><label>Request Category:</label></td>
			<td width="15%">
				<form:select path="requestCategory" onchange="refreshForm();" id="requestCategory">
				   <form:option value="Tabular">Tabular</form:option>
				   <form:option value="Index">Index</form:option>
			    </form:select>
			</td>
			<td width="38%">&nbsp;</td>
		</tr>
		<tr class="tabular">
			<td colspan="3">
			<table>
				<tr>
					<td><span class="required">*</span><label>Code Range:</label></td><td><label>From:</label></td><td><form:input  path="codeFrom" size="7" maxLength="7"/></td><td><label>To:</label></td><td><form:input  path="codeTo" size="7" maxLength="7"/></td>
				</tr>
			</table>
			</td>
			<td colspan="2">&nbsp;</td>
		</tr>
		
		<tr class="index">
			
			<td><span class="required">*</span><label>Index Book:</label></td>
			<td colspan="4">
				<form:select path="indexElementId" id="indexElementId" onchange="changeIndexBook();">
					<c:forEach var="indexBook" items="${allIndexBooks}">
		               <form:option value="${indexBook.code}"> ${indexBook.description}</form:option>
		            </c:forEach>
				</form:select>
			</td>
		</tr>
		
		<tr class="index">
			<td><span class="required">*</span><label>Lead Index Term:</label></td>
			<td colspan="3">
				<form:input id="leadTerm" path="leadTerm" size="40" maxLength="50"/>
			</td>
			<td>&nbsp;</td>
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