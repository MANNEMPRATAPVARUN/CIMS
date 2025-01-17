<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<fieldset>
	<legend class="searchLegend"><fmt:message key="search.criteria.legend.set"/></legend>
	<div class="span-8 inline form-row">
		<div class="span-4 label">
			<div><label for="classification"><fmt:message key="search.criteria.classification"/>:</label></div>
			<div><label for="requestCategory" class="${search.searchTypeName == 'cr.properties' ? 'mandatory' : ''}"><fmt:message key="search.criteria.request.category"/>:</label></div>
		</div>
		<div class="span-4 last">
			<div><span id="classification">${search.classificationName}</span></div>
			<div>
				<form:select id="requestCategory" path="requestCategory" items="${requestCategories}" itemLabel="code" cssErrorClass="fieldError" disabled="${search.searchTypeName != 'cr.properties'}"/>
			</div>
		</div>
	</div>
	<div class="span-8 inline form-row">
		<div class="span-3 label">
			<div><label for="language"><fmt:message key="search.criteria.language"/>:</label></div>
			<div><label for="statusIds"><fmt:message key="search.criteria.status"/>:</label></div>
		</div>
		<div class="span-5 last">
			<div>
				<form:select id="language" path="language" cssErrorClass="fieldError">
					<c:if test="${search.searchTypeName != 'cr.index'}">
						<form:option value=""></form:option>
					</c:if>
					<form:options items="${languages}" itemLabel="label" itemValue="code"/>
				</form:select>
			</div>
			<div>
				<c:choose>
					<c:when test="${search.searchTypeName == 'cr.properties'}"><form:select id="statusIds" path="statusIds" items="${statuses}" itemValue="statusId" itemLabel="subStatusDescription" multiple="true" size="5" style="width: 250px;" cssErrorClass="fieldError"/></c:when>
					<c:otherwise><form:select id="statusIds" path="statusIds" items="${statuses}" itemValue="statusId" itemLabel="subStatusDescription" style="width: 250px;" multiple="false" cssErrorClass="fieldError"/></c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
	<div class="span-8 inline form-row">
		<div class="span-3 label">
			<div><label for="contextIds" class="mandatory"><fmt:message key="search.criteria.year"/>:</label></div>
		</div>
		<div class="span-5 last">
			<c:choose>
					<c:when test="${search.searchTypeName == 'cr.properties'}">
						<form:select id="contextIds" path="contextIds" items="${contextIds}" itemLabel="versionCode" itemValue="contextId" multiple="true" size="4" cssErrorClass="fieldError"/>
						<div><fmt:message key="search.criteria.year.help"/></div>
					</c:when>
					<c:otherwise><form:select id="contextIds" path="contextIds" items="${contextIds}" itemLabel="versionCode" itemValue="contextId" multiple="false" cssErrorClass="fieldError"/></c:otherwise>
			</c:choose>
		</div>
	</div>
</fieldset>
<fieldset>
	<fmt:message key="search.criteria.label.true" var="trueLabel"/>
	<fmt:message key="search.criteria.label.false" var="falseLabel"/>
	<legend class="searchLegend"><fmt:message key="search.criteria.legend.enumerated"/></legend>
	<div class="span-8 inline form-row">
		<div class="span-3 label">
			<div><label for="typeOfChange"><fmt:message key="search.criteria.change.type"/>:</label></div>
			<div><label for="natureOfChange"><fmt:message key="search.criteria.change.nature"/>:</label></div>
		</div>
		<div class="span-5 last">
			<div>
				<form:select id="typeOfChange" path="changeTypeId" cssErrorClass="fieldError">
					<form:option value="" ></form:option>
					<form:options items="${changeTypes}" itemLabel="auxEngLable" itemValue="auxTableValueId"/>
				</form:select>
			</div>
			<div>
				<form:select id="natureOfChange" path="changeNatureId" cssErrorClass="fieldError">
					<form:option value=""/>
					<form:options items="${changeNatures}" itemLabel="auxEngLable" itemValue="auxTableValueId"/>
				</form:select>
			</div>
		</div>
	</div>
	<div class="span-10 inline form-row">
		<div class="span-4 label">
			<div><label for="requestor"><fmt:message key="search.criteria.requestor"/>:</label></div>
			<div><label for="evolutionRequired"><fmt:message key="search.criteria.evolution"/>:</label></div>
		</div>
		<div class="span-6 last">
			<div>
				<form:select id="requestor" path="requestorId" cssErrorClass="fieldError" style="width: 100%;">
					<form:option value=""></form:option>
					<form:options items="${requestors}" itemLabel="auxEngLable" itemValue="auxTableValueId"/>
				</form:select>
			</div>
			<div>
				<form:select id="evolutionRequired" path="evolutionRequired" cssErrorClass="fieldError">
					<form:option value=""/>
					<form:option value="${true}" label="${trueLabel}"/>
					<form:option value="${false}" label="${falseLabel}"/>
				</form:select>
				<span>&nbsp;&nbsp;&nbsp;</span>
				<label for="indexRequired"><fmt:message key="search.criteria.index"/>:&nbsp;</label>
				<form:select id="indexRequired" path="indexRequired" cssErrorClass="fieldError">
					<form:option value=""/>
					<form:option value="${true}" label="${trueLabel}"/>
					<form:option value="${false}" label="${falseLabel}"/>
				</form:select>
			</div>
		</div>
	</div>
	<div class="span-9 inline form-row">
		<div class="span-4 label">
			<div><label for="patternChange"><fmt:message key="search.criteria.change.pattern"/>:</label></div>
			<div><label for="patternTopic"><fmt:message key="search.criteria.pattern.topic"/>:</label></div>
		</div>
		<div class="span-5 last">
			<div>
				<form:select id="patternChange" path="patternChange" cssErrorClass="fieldError">
					<form:option value=""/>
					<form:option value="${true}" label="${trueLabel}"/>
					<form:option value="${false}" label="${falseLabel}"/>
				</form:select>
			</div>
			<div>
				<form:input id="patternTopic" path="patternTopic" class="searchbox" disabled="true" cssErrorClass="searchbox fieldError"/>
			</div>
		</div>
	</div>
</fieldset>
<fieldset>
	<legend class="searchLegend"><fmt:message key="search.criteria.legend.text"/></legend>
	<div class="span-8 inline form-row">
		<div class="span-1 label">
			<label for="searchText"><fmt:message key="search.criteria.text"/>:</label>
		</div>
		<div class="span-7 last">
			<form:input id="searchText" path="searchText" cssErrorClass="fieldError"/>
		</div>
	</div>
	<div class="span-10 inline form-row">
		<div class="span-3 label">
			<label><fmt:message key="search.criteria.search.in"/>:</label>
		</div>
		<div class="span-7 last">
			<label class="simple"><form:radiobutton path="searchTextType" value="RequestName" cssErrorClass="fieldError"/><fmt:message key="search.criteria.search.request.name"/></label>
			<label class="simple"><form:radiobutton path="searchTextType" value="RationaleChange" cssErrorClass="fieldError"/><fmt:message key="search.criteria.search.rationale.change"/></label>
		</div>
	</div>
</fieldset>
<fieldset>
	<legend class="searchLegend"><fmt:message key="search.criteria.legend.user"/></legend>
	<div class="span-9 inline form-row">
		<div class="span-4">
			<label class="simple"><form:radiobutton id="searchByOwner" path="searchUserType" value="Owner"/><fmt:message key="search.criteria.owner"/></label>
			<label class="simple"><form:radiobutton id="searchByAssignee" path="searchUserType" value="Assignee"/><fmt:message key="search.criteria.assignee"/></label>
		</div>
		<div class="last">
			<div>
				<fmt:message key="search.criteria.list.user" var="userListLabel"/>
				<fmt:message key="search.criteria.list.distribution" var="distributionListLabel"/>
				<form:select id="searchUserId" path="searchUserId" cssErrorClass="fieldError">
					<form:option value=""/>
					<optgroup label="${distributionListLabel}" id="distributionList">
						<form:options items="${distributionList}" itemLabel="name" itemValue="code"/>
					</optgroup>
					<optgroup label="${userListLabel}" id="userList">
						<form:options items="${userList}" itemLabel="name" itemValue="code"/>
					</optgroup>
				</form:select>
			</div>
		</div>
	</div>
</fieldset>
<fieldset>
	<legend class="searchLegend"><fmt:message key="search.criteria.legend.date"/></legend>
	<div class="span-24 inline form-row">
		<div class="span-6">
			<label class="simple"><form:radiobutton id="searchByDateCreated" path="searchDateType" value="Created" cssErrorClass="fieldError"/><fmt:message key="search.criteria.date.created"/></label>
			<label class="simple"><form:radiobutton id="searchByDateModified" path="searchDateType" value="Modified" cssErrorClass="fieldError"/><fmt:message key="search.criteria.date.modified"/></label>
		</div>
		<div class="last">
			<div class="inline" style="padding-right: 10px;">
				<label for="dateFrom"><fmt:message key="search.criteria.from"/>:&nbsp;</label><form:input id="dateFrom" path="dateFrom" class="datepicker" cssErrorClass="datepicker fieldError"/>
			</div>
			<div class="inline">
				<label for="dateTo"><fmt:message key="search.criteria.to"/>:&nbsp;</label><form:input id="dateTo" path="dateTo" class="datepicker" cssErrorClass="datepicker fieldError"/>
			</div>
		</div>
	</div>
</fieldset>
<script>
	$(document).ready(function(){
		$("#patternTopic").attr("disabled", ("${search.patternChange}" == "true" ? null : "disabled"));
		$("#patternChange").change(function(){
			var enabled = $(this).val() == "true";
			var $patternTopic = $("#patternTopic");
			$patternTopic.attr("disabled",(enabled ? null : "disabled"));
			if(!enabled) {
				$patternTopic.val(null);
			}
		});
		
	    var searchCallback = function(request, response) {
	    	var contextIds = "";
	    	$("#contextIds option:selected").each(function(i,option){
	    		if(i != 0) {
	    			contextIds += ",";
	    		}
	    		contextIds += option.value;
	    	});
	        $.ajax({
	            url: "<c:url value='/search/patternTopic.htm'/>",
	            contentType:"application/json; charset=UTF-8",
	            cache: false, /*CSRE-890*/
	            data: {
	              query : request.term,
	              contextIds : contextIds
	            },
	            success: function(data) {
	              response(data);
	            }
	          });
		};

	    $("#patternTopic").autocomplete({source : searchCallback,  position: { collision : "flip none" }, delay: 500})
	    .focus(function(){$(this).select();});
	    
	    //invoke autocomplete when user pastes the value
	    $("#patternTopic").bind("paste", function () {
	        setTimeout(function () {
	            $("#patternTopic").autocomplete("search", $("#patternTopic").val());
	        }, 0);
	    });
	    
	    var hideDistributionList = function() {
	    	var distributionList = $("#distributionList");
	    	if(typeof distributionList != "undefined" && distributionList != null) {
	    		distributionList.remove();
	    		$("#searchUserId").data("distributionList",distributionList);
	    	}
	    };
	    
	    var showDistributionList = function() {
	    	var distributionList = $("#searchUserId").data("distributionList");
	    	if(typeof distributionList != "undefined" && distributionList != null) {
	    		$("#searchUserId").find("optgroup").first().before(distributionList);
	    	}
	    };
	    
	    $("#searchByOwner").change(function(){
	    	hideDistributionList();
	    });
	    $("#searchByAssignee").change(function(){
	    	showDistributionList();
	    });
	    
	    //hide distribution list if required
	    if($("#searchByOwner").is(":checked")) {
	    	hideDistributionList();
	    }
	    
	    //initialize date pickers
	    $(".datepicker").datepicker({dateFormat: "yy-mm-dd", changeYear: true, changeMonth: true});
	    
	    //initalize request status select2 widget
	    var statusIds = $("#statusIds");
	    if(statusIds.attr("multiple")) {
	    	statusIds.select2({placeholder: "<fmt:message key='search.criteria.status.select'/>",
	    	  allowClear: true});
		}
	    
	    //notify of language changes
	    $("#language").change(function(){
	    	EventManager.publish("languagechanged", $(this).val());
	    });
	    
	    //notify of context changes
	    $("#contextIds").change(function(){
	    	EventManager.publish("contextchanged", $(this).val());
	    });
	});
</script>