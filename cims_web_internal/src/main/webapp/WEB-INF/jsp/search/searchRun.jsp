<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<c:url var="formUrl"  value="/search/save.htm" />
<spring:hasBindErrors name="searchResultCount">
	<div class="error">
		<c:forEach var="error" items="${errors.allErrors}">
			<div><spring:message message="${error}"/></div>
		</c:forEach>
	</div>
</spring:hasBindErrors>
<form:form id="searchForm" action="${formUrl}" method="post" modelAttribute="search">
	<form:errors path="*" cssClass="error" element="div"/>
	<div style="vertical-align: middle;" class="floatRight">
		<security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR')">
			<c:if test="${search.ownerId == sessionScope.currentUser.userId}">
				<div class="floatRight" style="padding-right: 32px;">
					<form:radiobutton path="shared" value="false" id="shared"/><label for="sared" class="simple"><fmt:message key="search.access.private"/></label>
					<form:radiobutton path="shared" value="true" id="private"/><label for="private" class="simple"><fmt:message key="search.access.shared"/></label>
				</div>
			</c:if>
		</security:authorize>
		<div class="clear">
			<div class="inline">
				<label for="searchName"><fmt:message key="search.header.name"/>:</label>
				<form:input id="searchName" path="searchName" size="35" maxlength="50" cssErrorClass="fieldError"/>
			</div>
			<div class="inline">
				<a href="javascript:saveSearch()"><img src="<c:url value='/img/icons/Save.png'/>" alt="Save" style="height: 24px; vertical-align: middle;"/></a>
			</div>
		</div>
	</div>
	<div class="section" style="padding: 2px;" name="criteria"> 
            <div class="sectionHeader">
               <a href="#"><img src="<c:url value='/img/icons/Expand.png'/>" alt="Toggle" onclick="javascript:toggle(this);" style="vertical-align: middle;"/></a>
               <div style="display: inline-block; vertical-align: middle;">
               		<c:choose>
               			<c:when test="${fn:containsIgnoreCase(search.searchTypeName,'cr.')}"><fmt:message key="search.header.criteria.request"/></c:when>
               			<c:otherwise><fmt:message key="search.header.criteria"/></c:otherwise>
               		</c:choose>
               </div>
            </div>
           <div class="sectionContent"><tiles:insertAttribute name="criteria"/></div>
     </div>
     <c:if test="${search.searchTypeName == 'cr.icd.tabular' || search.searchTypeName == 'cr.cci.tabular' || search.searchTypeName == 'cr.index'}">
     	<div class="section" style="padding: 2px;" name="criteria"> 
            <div class="sectionHeader">
               <a href="#"><img src="<c:url value='/img/icons/Expand.png'/>" alt="Toggle" onclick="javascript:toggle(this);" style="vertical-align: middle;"/></a>
               <div style="display: inline-block; vertical-align: middle;"><fmt:message key="search.header.criteria.classification"/></div>
            </div>
           <div class="sectionContent"><tiles:insertAttribute name="classificationCriteria"/></div>
    	 </div>
     </c:if>
     <div class="section" style="padding: 2px;" name="output"> 
            <div class="sectionHeader">
               <a href="#"><img src="<c:url value='/img/icons/Expand.png'/>" alt="Toggle" onclick="javascript:toggle(this);" style="vertical-align: middle;"/></a>
               <div style="display: inline-block; vertical-align: middle;"><fmt:message key="search.header.output"/></div>
            </div>
           <div class="sectionContent"><tiles:insertAttribute name="output"/></div>
     </div>
     <form:hidden path="searchId"/>
     <form:hidden path="searchTypeId"/>
     <form:hidden path="searchTypeName"/>
     <form:hidden path="ownerId"/>
     <form:hidden path="classificationName"/>
</form:form>
     <div class="section" style="padding: 2px;" name="results"> 
            <div class="sectionHeader">
               <a href="#"><img src="<c:url value='/img/icons/Expand.png'/>" alt="Toggle" onclick="javascript:toggle(this);" style="vertical-align: middle;"/></a>
               <div style="display: inline-block; vertical-align: middle;"><fmt:message key="search.header.result"/></div>
            </div>
           <div class="sectionContent">
                <c:url value="/" var="baseUrl"/>
	           	<div class="floatRight searchActionButtons">
					<a href="javascript:executeSearch();" id="executeSearch"><img src="<c:url value='/img/icons/Run.png'/>" title="<fmt:message key='search.list.btn.run'/>"/></a>				
					<a href="javascript:exportToExcel();" id="exportToExcel"><img src="<c:url value='/img/icons/ExportExcel.png'/>" title="<fmt:message key='search.list.btn.run'/>"/></a>	
					<a href="javascript:popupResult();" id="popupResult"><img src="<c:url value='/img/icons/NewWindowWord.png'/>" title="<fmt:message key='search.list.btn.run'/>"/></a>				          	
				</div>
				<div id="searchResultsContainer" class="clear">
           			<tiles:insertAttribute name="results"/>
           		</div>
           </div>
     </div>
<script>
$(document).ready(function(){
	$('a').click(function (event){
		if($(this).attr("href") == "#") {
			event.preventDefault();
			return false;
		}
	});
	
	
	var searchId = Number("${search.searchId}");
	if(searchId > 0) {
		//restore accordion states if search is not brand new
		restoreSectionState();
		//fetch the search results
		executeSearch();
	}
});

function toggle(element) {
	 var toggle = $(element);
	 var section = toggle.parents(".section");
	 toggleSection(section, true);
}

function toggleSection(section, animate) {
	 if(!(section instanceof jQuery)) {
		 section = $(section);
	 }
	 var toggle = section.find(".sectionHeader > a > img");
	 var sectionContent = section.find(".sectionContent").first();
	 var isHidden = isSectionHidden(section);
	 if (isHidden){
	     (animate ? sectionContent.slideDown() : sectionContent.show());
         toggle.attr("src", "<c:url value='/img/icons/Expand.png'/>");
	 }else{
		 (animate ? sectionContent.slideUp() : sectionContent.hide());
		 toggle.attr("src", "<c:url value='/img/icons/Collapse.png'/>");
	 }
	 $.cookie("section."+section.attr('name')+".collapsed",!isHidden);
}

function isSectionHidden(section) {
	if(!(section instanceof jQuery)) {
		 section = $(section);
	 }
	return section.find(".sectionContent").first().is(":hidden");
}

function restoreSectionState() {
	$(".section").each(function(i,section){
		var sectionName = $(section).attr('name');
		var isCollapsed = $.cookie("section."+sectionName+".collapsed") == "true";
		var isHidden = isSectionHidden(section);
		if(isHidden != isCollapsed) {
			toggleSection(section, false);
		}
	});
}

function saveSearch() {
	var successCallback = function(result) {
		hideProcessingScreen();
		if(typeof result != "undefined" && result != null) {
			Feedback.error(result);
		}
		else {
			Feedback.success("<fmt:message key='search.save.success'/>");
		}
	};
	var errorCallback = function(result) {
		hideProcessingScreen();
		Feedback.error("<fmt:message key='search.save.error'/>");
	};
	
	showProcessingScreen();
	$('#searchForm').submit();
}

function executeSearch() {
	var enableExecuteButton = function(enable) {
		var button = $("#executeSearch");
		button.css("opacity",enable ? 1 : .5);
		if(enable){
			button.attr("href",button.data("href"));
		}
		else {
		 	button.data("href",button.attr("href")).attr("href","#");
		}
	};
	var successCallback = function(data) {
		$("#searchResultsContainer").html(data);
		enableExecuteButton(true);
	};
	var errorCallback = function(data) {
		$("#searchResultsContainer").html(data);
		enableExecuteButton(true);
	};
	var beforSendCallback = function() {
		var resultContainer = $("#searchResultsContainer");
		var progressIndicator = $("<div class='progress' style='display: table-cell; height: 150px; text-align:center; vertical-align: middle;'><fmt:message key='search.result.loading'/></div>");
		progressIndicator.width(resultContainer.width());
		resultContainer.html(progressIndicator);
		enableExecuteButton(false);
	};
	 $.ajax({
	        url: "<c:url value='/search/execute.htm'/>",
	        data: $("#searchForm").serialize(),
	        type: "POST",
	        success: function(data){
	        	successCallback(data);
	        },
	        error: function(data){
	        	errorCallback(data);
	        },
	        beforeSend: beforSendCallback
	    });
}

function exportToExcel(){
	var validationUrl = '<c:url value="/search/validate.htm"/>';
	var generationUrl = '<c:url value="/search/exportToExcel.htm"/>';
	var checkUrl = '<c:url value="/search/checkDownloadProgress.htm"/>';
	exportReport(validationUrl, generationUrl,checkUrl,'#searchForm','#searchResultsContainer','frmSearchExport');
}

function popupResult(){	
	popupResultViewer('${baseUrl}','search/popupResult.htm','#searchForm','Search Result', 'width=1380,height=750,resizable=yes,scrollbars=yes');
}

</script>
