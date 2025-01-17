
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<style type="text/css">
 
 div .index{
 	display : none;
 }
 
</style>

<script language="JavaScript" type="text/javascript" >

	function refreshForm(){
		var classification = $('#classification').val();
		var requestCategory = $('#requestCategory').val();

/*		
		$.ajax({
            url: "${pageContext.request.contextPath}/reports/reviewGroup/selectClassification.htm?baseClassification="+classification,
            success: function(data) {
            	var options = '';
                for (var i = 0; i < data.length; i++) 
                {
                    options += '<option value="' + data[i].versionCode + '" rel="'+data[i].contextId+'">' + data[i].versionCode + '</option>';
                }
                $('#year').html(options);
            }
            });
*/
		$.ajax({
            url: "${pageContext.request.contextPath}/reports/reviewGroup/selectRequestCategory.htm?baseClassification="+classification+"&requestCategory="+requestCategory,
            success: function(data) {
            	var options = '';
                for (var i = 0; i < data.length; i++) 
                {
                    options += '<option value="' + data[i].versionCode + '" rel="'+data[i].contextId+'">' + data[i].versionCode + '</option>';
                }
                $('#year').html(options);
            }
            });

		$('#reviewGroupName').val($('#reviewGroup option:selected').text());

		//var requestCategory = $('#requestCategory').val();
		if(requestCategory=="Tabular"){
			$('.index').hide();
			$('.tabular').show();
            	        $("#language").val('');
		        $('#languageDesc').val($('#language option:selected').text());
            	        $("#language option[value='ALL']").removeAttr('disabled');
			$("#indexBook").val('');
			$('#leadTerm').val('');
			$('#leadTermElementId').val('');
			$('#codeFrom').val('');
			$('#codeTo').val('');
			$('#patternTopic').val('');
		}else{
			$('.index').show();
			$('.tabular').hide();
            	        $("#language").val('');
		        $('#languageDesc').val($('#language option:selected').text());
     	                $("#language option[value='ALL']").attr("disabled","disabled");
			$('#leadTerm').val('');
			$('#leadTermElementId').val('');
			$('#codeFrom').val('');
			$('#codeTo').val('');
			$('#patternTopic').val('');
			$.ajax({
	            url: "${pageContext.request.contextPath}/reports/listIndexBooks.htm?classification="+classification,
	            success: function(data) {
	            	var options = '';
                        options += '<option value=""></option>';
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
		var validationUrl = '<c:url value="/reports/reviewGroup/validate.htm"/>';
		var generationUrl = '<c:url value="/reports/reviewGroup/generateCompiledResponsesReport.htm"/>';
		var checkUrl = '<c:url value="/reports/reviewGroup/checkDownloadProgress.htm"/>';
		generateReport(validationUrl, generationUrl,checkUrl);
	}
	
	function changeIndexBook(){
		$('#indexBook').val($('#indexElementId option:selected').text());
		$('#leadTerm').val('');
		$('#leadTermElementId').val('');
	}
	
	function changeLanguage(){
		$('#languageDesc').val($('#language option:selected').text());
	}

	function changeReviewGroup(){
		$('#reviewGroupName').val($('#reviewGroup option:selected').text());
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
		$('#patternTopic').autocomplete({
			// it is very important to use contentType with charset=UTF-8, otherwise french characters won't be properly handled by the search
			source: function(request, response) {
		        $.ajax({
		            url: "${pageContext.request.contextPath}/reports/reviewGroup/searchPatternTopics.htm?classification="+$('#classification').val(),
		            contentType:"application/json; charset=UTF-8",
		            data: {
		              term : request.term
		            },
		            success: function(data) {
		              response(data);
		            }
		          });
			}
		});
		
	});

    function popupReviewGroupCompiledResponsesReport() {

	var validationUrl = '<c:url value="/reports/reviewGroup/validate.htm"/>';
	var generationUrl = '<c:url value="/reports/reviewGroup/generateCompiledResponsesReportHtml.htm"/>';
	var checkUrl = '<c:url value="/reports/reviewGroup/checkDownloadProgress.htm"/>';

	var url=validationUrl+"?q="+Math.random();
	$('#errorMessages').html('');
	$.ajax({
		url : url,
	    data: $('#viewForm').serialize(),
	    success: function(data){
	    	if("FAILED"==data.status){
	    		var errorMessages = '';
	    		for(var i=0 ; i<data.errors.length;i++){
	    			errorMessages += '<li>'+ data.errors[i] + '</li>';
	    		}
	    		$('#errorMessages').html(errorMessages);
	    	}else{
	    		var token = data.token;
	    		var url1 = generationUrl+"?token="+token+"&"+$('#viewForm').serialize();
                        var newwindow = window.open(url1, "Review Group Compiled Responses Report", "width=1380,height=750,resizable=no,scrollbars=yes, menubar=yes ");
                        newwindow.document.title = 'Review Group Compiled Responses Report';
	                if (window.focus)  {
	                    newwindow.focus();
	                }
	    		showProcessingScreen();
			checkDownloadProgress(checkUrl, token);
	    	}
	    }
	});

     }		

</script>

<h4 class="contentTitle">Reports > Review Group Compiled Responses Report</h4> 

<div class="content">

<fieldset id="reportCriteria">
   <legend>Review Group Compiled Responses Report</legend>

<form:form modelAttribute="reportViewBean" id="viewForm">
	<ul class="errorMsg" id="errorMessages"></ul>
	<form:hidden path="indexBook" id="indexBook"/>
	<form:hidden path="leadTermElementId" id="leadTermElementId"/>
	<form:hidden path="ccp_cid" id="ccp_cid"/>
	<form:hidden path="reviewGroupName" id="reviewGroupName"/>
	<form:hidden path="languageDesc" id="languageDesc"/>

	 <fieldset>	
	 	<table>
	 	<tr>
		<td width="15%"><span class="required">*</span><label>Classification:</label></td>		
		<td width="15%">
		<form:select  path="classification" onchange="refreshForm();" id="classification">
		   <c:forEach var="baseClassification" items="${baseClassifications}">
              <form:option value="${baseClassification}"> ${baseClassification}</form:option>
           </c:forEach>
		</form:select>	
		</td>	
		<td width="15%"><span class="required">*</span><label>Request Category:</label></td>
		<td width="15%">
		<form:select  path="requestCategory" onchange="refreshForm();" id="requestCategory">
		   <form:option value="Tabular">Tabular</form:option>
		   <form:option value="Index">Index</form:option>
	    </form:select>
	    </td>
	    <td width="40%">&nbsp;</td>
	    </tr>	

	 	<tr>
		<td><span class="required">*</span><label>Year:</label></td>	
		<td>
		<form:select path="year" id="year">
	    </form:select>
	 	</td>
	    <td><label>Language:</label></td>	
	    <td>
		<form:select path="language" onchange="changeLanguage();" id="language">
                    <form:option value=""></form:option>
                    <c:forEach var="language" items="${languages}">
                        <form:option value="${language.languageCode}">${language.languageDesc}</form:option>
                    </c:forEach>
	    </form:select>
	    </td>
	    <td>&nbsp;</td>
	    </tr>	

	 	<tr>
	    <td><span class="required">*</span><label>Review Group:</label></td>
	    <td>		
		<form:select path="reviewGroup" onchange="changeReviewGroup();" id="reviewGroup">
                    <c:forEach var="reviewGroup" items="${reviewGroupList}">
                        <form:option value="${reviewGroup.distributionlistid}">${reviewGroup.name}</form:option>
                    </c:forEach>
	    </form:select>
	    </td>
	    <td colspan="3">&nbsp;</td>
	    </tr>	

		<tr>
		<td><label for="patternTopic">Pattern Topic:</label></td>
		<td colspan="4">
		<form:input id="patternTopic" path="patternTopic" size="50" maxLength="50"/>
		</td>
		</tr>
		
		<tr class="tabular">
		<td colspan="4">
			<table>
				<tr>
					<td><label>Code Range:</label></td><td><label>From:</label></td><td><form:input  path="codeFrom" size="10" maxLength="7"/></td><td><label>To:</label></td><td><form:input  path="codeTo" size="10" maxLength="7"/></td>
				</tr>
			</table>
		</td>
		<td>&nbsp;</td>
		</tr>
		
		<tr class="index">
		<td><label for="indexBook">Index book:</label></td>
		<td colspan="4">		
		<form:select  path="indexElementId" id="indexElementId" onchange="changeIndexBook();">
		<%--
			<c:forEach var="indexBook" items="${allIndexBooks}">
               <form:option value="${indexBook.code}"> ${indexBook.description}</form:option>
            </c:forEach>
                --%>
		</form:select>
		</td>
		</tr>
		<tr class="index">
		<td ><label for="leadTerm">Lead Index Term:</label></td>
		<td colspan="3">
		<form:input id="leadTerm" path="leadTerm" size="50" maxLength="50"/>
		</td>
		<td>&nbsp;</td>
		</tr>

		</table>
		<div>
		<input class="button" type="button" value="<fmt:message key='cims.reports.reviewgroup.html.submitButton'/>" 
		       onclick="javascript:popupReviewGroupCompiledResponsesReport();"/>

		<input style="visibility:hidden;" class="button" type="button" value="<fmt:message key='cims.reports.reviewgroup.excel.submitButton'/>" onclick="download();"/>
		</div>
		</fieldset>
		
</form:form>

<div id="loadingInfo" class="info" style="display: none; margin-bottom: 0.1em; width: 800px; padding: 0.2em;">Loading</div>
</div>