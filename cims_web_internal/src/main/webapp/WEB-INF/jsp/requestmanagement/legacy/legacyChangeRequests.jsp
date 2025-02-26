<!DOCTYPE html> 

<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<style type="text/css" media="all">

   .btn_line {
      position:absolute;
      right:15px;
    }
    .alignRight{
      text-align: right;
    }   

   .ui-dialog {
       z-index:1100 !important; 
   }

  #versionCodes{
       margin-top: 0px;
       height: 160px;
       width: 80px;
       overflow: scroll;
   }
   
   #sectionCode {
       width: 300px;
       overflow: scroll;
   }
   
</style>

<script type="text/javascript">

	var processingSomething = true;

	var processedMessage = {};
	processedMessage["class"] = '';
	processedMessage["message"] = '';

	$(document).ready(function() {
		processingSomething = false;

                //hideProcessingScreen();

	});
	
    $( window ).load(function() {

        //console.log( "window loaded" );
    	hideProcessingScreen();
    });


	function resizeIframe(iframe) {
		iframe.height = iframe.contentWindow.document.body.scrollHeight + "px";
	}

	function busyProcessingSomething() {
		if (processingSomething == true) {
			console.log('Im doing something so dont bother me');
			return true;
		}
	}

	function showProcessingScreen() {
		$("*").css("cursor", "progress");
		$('body').append('<div class="modal">');
	}

	function hideProcessingScreen() {
		$("*").css("cursor", "auto");
		$('.modal').remove();
	}

	function showErrorMessagesFromResponse(response) {
		var errorMessages = "";
		for ( var i = 0; i < response.errorMessageList.length; i++) {
			var item = response.errorMessageList[i];
			errorMessages += item.message;
			errorMessages += "<br/>";
		}

		$("#errorMessageList").html(errorMessages);
		$("#errorMessageList").show();
	}

	function showProcessedScreenMessage() {

		if (processedMessage["message"].length > 1) {
			$("#loadingInfo").attr("class", processedMessage["class"]);
			$("#loadingInfo").text(processedMessage["message"]);
			processedMessage["message"] = "";
			$("#loadingInfo").show();
		} 
	}

	function showInfoMessage(messageToShow) {
		processedMessage["message"] = messageToShow;
		processedMessage["class"] = "info";
		
		showProcessedScreenMessage();
	}


	function getSelectedIFrame() {
		var iframe = $("#resultsiFrame");
		return iframe;
	}

    function searchLegacyRequest(){

            processingSomething = true;
            showProcessingScreen();
            
    	    $('form#legacyRequestSearchModel').serialize();

            //loadLegacyRequests();
    	    $("#legacyRequestSearchModel")[0].action="<c:url value='/legacyChangeRequests.htm'/>";
            $("#legacyRequestSearchModel")[0].submit();

     }


    function popupLegacyRequestDetailViewer(requestId) {
              var title = "cims.legacy.request.record";
	      var link = "legacyRequestDetail.htm?titleKey="+title+"&requestId="+requestId;
		  var newwindow = window.open(link, "legacyRequest"+requestId, "width=1000,height=750,resizable=yes,scrollbars=yes, menubar=yes ");

		  newwindow.document.title = 'CRD Legacy Record';
		  if (window.focus)  {
			  newwindow.focus();
		  }
		  
    }		

</script>

<h4 class="contentTitle"><fmt:message key="cims.menu.search"/> &#62; CRD Legacy Search</h4>

<div class="content">
<form:form method="POST" modelAttribute="legacyRequestSearchModel" id="legacyRequestSearchModel">
<fieldset>
<legend>Search Parameters</legend>

<form:errors path="*" cssClass="errorMsg" />

    <%--<table style="border: 1px solid black;">--%>
    <table>
        <tr>
            <td style="font-weight:bold; width: 8%"><span class="required">*</span> <span id="versionsLabel"> Versions: </span></td>
            <%--
            <td style="font-weight:bold; width: 9.5%"><span class="required">*</span> <span id="classificationLabel"> Classification: </span> </td>
            --%>
            <td style="font-weight:bold; width: 10.4%"><span class="required">*</span> <span id="classificationLabel"> Classification: </span> </td>
            <td style="width: 15.5%; align: left;">
                <form:select  path="classificationTitleCode" >
                    <c:forEach var="classification" items="${classificationTitleCodes}">
                        <form:option value="${classification}"> ${classification}  </form:option>
                    </c:forEach>
                </form:select>
            </td>
            <td style="width: 10%"> &nbsp; </td>
            <td style="font-weight:bold; width: 9%"><span class="required">*</span> <span id="languageCodeLabel">  Language: </span> </td>
            <%--
            <td style="width: 10%; align: left;">
            <td style="width: 52%; align: left;">
            --%>
            <td style="width: 51.1%; align: left;">
                <form:select id="languageCode"  path="languageCode" >
                    <c:forEach var="language" items="${languages}">
                        <form:option value="${language.languageCode}"> ${language.languageDesc}  </form:option>
                    </c:forEach>
                </form:select>
            </td>
            <%--
            <td style="width: 52%"> &nbsp; </td>
            --%>
        </tr>
        <tr>
            <td>
                <form:select multiple="multiple"  id="versionCodes" path="versionCodes"  >
                    <c:forEach var="versionCode" items="${allVersionCodes}">
                        <form:option value="${versionCode}" > ${versionCode}  </form:option>
  	            </c:forEach>
                </form:select>
            </td>
            <td colspan="5" style="width: 68%"> 
            <%--<table border: 1px solid black>--%>
            <table>
            <%--
            <tr> <td colspan="2"> &nbsp; </td> </tr>
            <tr> <td colspan="2"> &nbsp; </td> </tr>
            --%>
            <tr>
                <%--
                <td style="font-weight:bold; width: 10%"> <span id="dispositionLabel"> Disposition: </span> </td>
                --%>
                <td style="font-weight:bold; width: 11%"> <span id="dispositionLabel"> Disposition: </span> </td>
                <td>
                <form:select id="requestStatusCode"  path="requestStatusCode" >
                    <form:option value=""></form:option>
                    <c:forEach var="disposition" items="${dispositions}">
                        <form:option value="${disposition.requestStatusCode}"> ${disposition.requestStatusDesc}  </form:option>
                    </c:forEach>
                </form:select>
                </td>
            </tr>    
            <tr>
                <td style="font-weight:bold;"> <span id="sectionLabel"> Section: </span> </td>
                <td>
                <form:select id="sectionCode"  path="sectionCode" >
                    <form:option value=""></form:option>
                    <c:forEach var="section" items="${sections}">
                        <form:option value="${section.sectionCode}"> ${section.sectionDesc}  </form:option>
                    </c:forEach>
                </form:select>
                </td>
            </tr>    
            <tr>
                <td style="font-weight:bold;"> <span id="natureOfChangeLabel"> Nature of Change: </span> </td>
                <td>
                <form:select id="changeNatureCode"  path="changeNatureCode" >
                    <form:option value=""></form:option>
                    <c:forEach var="changeNature" items="${changeNatures}">
                        <form:option value="${changeNature.changeNatureCode}"> ${changeNature.changeNatureDesc}  </form:option>
                    </c:forEach>
                </form:select>
                </td>
            </tr>    
            <tr>
                <td style="font-weight:bold;"> <span id="typeOfChangeLabel"> Type of Change: </span> </td>
                <td>
                <form:select id="changeTypeCode"  path="changeTypeCode" >
                    <form:option value=""></form:option>
                    <c:forEach var="changeType" items="${changeTypes}">
                        <form:option value="${changeType.changeTypeCode}"> ${changeType.changeTypeDesc}  </form:option>
                    </c:forEach>
                </form:select>
                </td>
            </tr>    
            </table>
            </td>
            <%--
            <td style="width: 32%"> &nbsp; </td>
            --%>
            <td style="width: 31%"> &nbsp; </td>
        </tr>
        <tr>
            <td> &nbsp; </td> 
            <td> &nbsp; </td> 
            <td> &nbsp; </td> 
            <td> &nbsp; </td> 
            <td>
                   <input type="button"  value="<fmt:message key='Legacy.Request.searchButton'/>" class="button" onclick="javascript:searchLegacyRequest();" >&nbsp;&nbsp; 
            </td>
            <td> &nbsp; </td> 
            <%--<td style="width: 32%"> &nbsp; </td>--%>
        </tr>
    </table>

</fieldset>
</form:form>
</div>

<div class="icons">
	<ul style="padding-left:.9em; ">
	
		<li style="float: left; list-style-type: none; ">
			<div id="loadingInfo" class="info" style="display: none; margin-bottom: 0.1em; width: 800px; padding: 0.2em;">Loading</div>
			<div id="errorMessageList" class="error" style="display: none;margin-bottom: 0.1em; width: 800px; padding: 0.2em;"></div>
		</li>	
	</ul>
</div>


<div id="searchResultsDiv" class="content">

<fieldset>
<legend>Search Results</legend>

<c:if test="${(empty legacyRequestResults)}">
  <c:if test="${(not empty legacyRequestSearchModel.versionCodes) && (legacyRequestSearchModel.classificationTitleCode!=null) && (legacyRequestSearchModel.languageCode!=null) }">
		No records found.
  </c:if>
</c:if>

<c:if test="${not empty legacyRequestResults}">  
<%--<div class="scroll" style="overflow:scroll">--%>
<div>
  <c:choose>
    <c:when test="${resultSize>2000}">
        More than 2000 records are found.  Showing first 2000 records.
    </c:when>
    <c:otherwise>
        ${resultSize} records are found. 
    </c:otherwise>
  </c:choose>

	<display:table name="legacyRequestResults" id="legacyRequestsTable" defaultsort="1" defaultorder="ascending" requestURI="" 
		size="resultSize" class="listTable" style="width: 100%; margin-top: 0px;" sort="list">
		
		<display:setProperty name="paging.banner.placement" value="bottom" />
		<display:setProperty name="paging.banner.some_items_found" value="" />
		<display:setProperty name="basic.empty.showtable" value="true" />
		<display:setProperty name="basic.msg.empty_list_row" 
			value="<tr class='odd'><td></td><td></td><td></td><td></td><td style='display:none;'></td><td style='display:none;'></td><td></td></tr>"/>
		
		<display:column sortable="true" titleKey="legacy.request.name" sortProperty="requestName" headerClass="tableHeader" style="word-wrap:break-word; width:43%;" >
          <a href="javascript:popupLegacyRequestDetailViewer(${legacyRequestsTable.requestId});">${legacyRequestsTable.requestName}</a>
		</display:column>

		<display:column sortable="true" titleKey="legacy.version.code" sortProperty="versionCode" headerClass="tableHeader" style="word-wrap:break-word; width:7%;">
			${legacyRequestsTable.versionCode}
		</display:column>

<%--
		<display:column titleKey="legacy.classification.title" headerClass="tableHeader" style="word-wrap:break-word; width:8%;">
			${legacyRequestsTable.classificationTitleCode}
		</display:column>

		<display:column titleKey="legacy.language" headerClass="tableHeader" style="word-wrap:break-word; width:7%;">
			${legacyRequestsTable.language}
		</display:column>
--%>

		<display:column sortable="true" titleKey="legacy.request.status" sortProperty="requestStatus" headerClass="tableHeader" style="word-wrap:break-word; width:8%;">
			${legacyRequestsTable.requestStatus}
		</display:column>

		<display:column titleKey="legacy.section.code" headerClass="tableHeader" style="word-wrap:break-word; width:22%;">
			<%-- ${legacyRequestsTable.sectionCode} --%>
			${legacyRequestsTable.sectionDesc}
		</display:column>

		<display:column titleKey="legacy.change.nature" headerClass="tableHeader" style="word-wrap:break-word; width:12%;">
			${legacyRequestsTable.changeNature}
		</display:column>

		<display:column titleKey="legacy.change.type" headerClass="tableHeader" style="word-wrap:break-word; width:8%;">
			${legacyRequestsTable.changeType}
		</display:column>

	</display:table>

</div>
</c:if>

</fieldset>
</div>
