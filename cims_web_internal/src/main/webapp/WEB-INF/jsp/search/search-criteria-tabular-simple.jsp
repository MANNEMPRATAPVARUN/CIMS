<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<fieldset>
	<legend class="searchLegend"><fmt:message key="search.criteria.legend.set"/></legend>
	<div class="span-8 form-row">
		<div class="span-3 label">
			<label for="contextIds" class="mandatory"><fmt:message key="search.criteria.year"/>:</label>
		</div>
		<div class="span-5 last">
		       <form:select id="contextIds" path="contextIds" items="${contextIds}" itemLabel="versionCode" itemValue="contextId" multiple="true" size="4" cssErrorClass="fieldError"/>
			   <div><fmt:message key="search.criteria.year.help"/></div>			
		</div>
	</div>
	<div class="span-8 form-row">
		<div class="span-3 label">
			<label for="statusCode"><fmt:message key="search.criteria.status"/>:</label>
		</div>
	    <div class="span-5 last">
			 <form:select id="statusCode" path="statusCode" class="short" cssErrorClass="fieldError">
			 		<form:option value="${null}"></form:option>
			 		<form:options items="${conceptStatuses}" itemValue="key" itemLabel="value"></form:options>
			 </form:select>
	    </div>
	</div>
</fieldset>
<fieldset>
	<legend class="searchLegend"><fmt:message key="search.criteria.legend.hierarchy.level"/></legend>
	<div class="span-24 inline form-row">
		<div class="span-2">
			<label for="levelChapter" class="mandatory"><fmt:message key="search.criteria.level"/>:</label>
		</div>
		<div class="span-8">
			<c:choose>
				<c:when test="${!fn:containsIgnoreCase(search.classificationName,'cci')}">
					<div>
						<form:radiobutton path="hierarchyLevel" value="Chapter" id="levelChapter"/>
						<label for="levelChapter" class="simple"><fmt:message key="search.criteria.level.chapter"/></label>
					</div>
					<div>
						<form:radiobutton path="hierarchyLevel" value="Block" id="levelBlock"/>
						<label for="levelBlock" class="simple"><fmt:message key="search.criteria.level.block"/></label>
					</div>
					<div>
						<form:radiobutton path="hierarchyLevel" value="Category" id="levelCategory"/>
						<label for="levelCategory" class="simple"><fmt:message key="search.criteria.level.category"/></label>
					</div>
				</c:when>
				<c:otherwise>
					<div class="span-2">
						<div>
							<form:radiobutton path="hierarchyLevel" value="Section" id="levelSection"/>
							<label for="levelSection" class="simple"><fmt:message key="search.criteria.level.section"/></label>
						</div>
						<div>
							<form:radiobutton path="hierarchyLevel" value="Block" id="levelBlock"/>
							<label for="levelBlock" class="simple"><fmt:message key="search.criteria.level.block"/></label>
						</div>
					</div>
					<div class="span-5">
						<div>
							<form:radiobutton path="hierarchyLevel" value="Group" id="levelGroup"/>
							<label for="levelGroup" class="simple"><fmt:message key="search.criteria.level.group"/></label>
						</div>
						<div>
							<form:radiobutton path="hierarchyLevel" value="Rubric" id="levelRubric"/>
							<label for="levelRubric" class="simple"><fmt:message key="search.criteria.level.rubric"/></label>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="span-14 last" id="rangeContainer" style="display: none;">
			<div class="span-2 label"><label><fmt:message key="search.criteria.level.within"/>:</label></div>
			<div class="span-12 last">
				<div>
					<span id="withinLevelLabel"></span>
					<div class="inline">
						<c:choose>
							<c:when test="${!fn:containsIgnoreCase(search.classificationName,'cci')}">
								<form:input id="codeOther" path="chapterCode" class="short" cssErrorClass="short fieldError"/>
							</c:when>
							<c:otherwise>
								<form:input id="codeOther" path="sectionCode" class="short" cssErrorClass="short fieldError"/>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
				<div class="last">
					<div class="inline">
						<label for="codeFrom"><fmt:message key="search.criteria.from"/>:&nbsp;</label><form:input id="codeFrom" path="codeFrom" class="short" cssErrorClass="short fieldError"/>
					</div>
					<div class="inline">
						<label for="codeTo"><fmt:message key="search.criteria.to"/>:&nbsp;</label><form:input id="codeTo" path="codeTo" class="short" cssErrorClass="short fieldError"/>
					</div>
				</div>
			</div>
		</div>
	</div>
</fieldset>
<fieldset>
	 <legend class="searchLegend"><fmt:message key="search.criteria.legend.enumerated"/></legend>
	 <c:choose>
		<c:when test="${!fn:containsIgnoreCase(search.classificationName,'cci')}">
				<div class="span-10 form-row">
					<div class="span-5 label">
						<label for="daggerAsterisk"><fmt:message key="search.criteria.enumerated.dagger.asterisk"/>:</label>
					</div>
					<div class="span-5 last">
					      <form:select id="daggerAsterisk" path="daggerAsteriskId" class="short" cssErrorClass="fieldError">
					      		<form:option value="${null}">&nbsp;</form:option>
								<c:forEach items="${daggerAsteriskTypes}" var="daggerAsterisk">
									<form:option value="${daggerAsterisk.elementId}">${daggerAsterisk.code}</form:option>
								</c:forEach>
						 </form:select>
					</div>
				</div>
				<div class="span-10 form-row">
					<div class="span-5 label">
						<label for="canEnhancementFlag"><fmt:message key="search.criteria.enumerated.canadian.enhancement"/>:</label>
					</div>
					 <div class="span-5 last">
					   		<form:select id="canEnhancementFlag" path="canEnhancementFlag" class="short" cssErrorClass="fieldError">
					   		    <form:option value="${null}">&nbsp;</form:option>
								<form:option value="${true}"><fmt:message key="search.criteria.label.true"/></form:option>
								<form:option value="${false}"><fmt:message key="search.criteria.label.false"/></form:option>								
							</form:select> 
					 </div>
				</div>
		</c:when>
		<c:otherwise>
			<div class="span-10 form-row">
				<div class="span-4 label">
					<label for="invasivenessLevel"><fmt:message key="search.criteria.enumarated.invasiveness.level"/>:</label>
				</div>
				<div class="span-6 last">
					<form:select id="invasivenessLevel" path="invasivenessLevel" class="short" cssErrorClass="fieldError">
						<form:option value="${null}">&nbsp;</form:option>
						<c:forEach items="${invasivenessLevels}" var="level">
						 	<form:option value="${level.key}">${level.value}</form:option>	
						 </c:forEach>									
					</form:select>
				</div>
			</div>
		</c:otherwise>
	</c:choose>
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
	<div class="span-18 inline form-row">
		<div class="span-3 label">
			<label><fmt:message key="search.criteria.search.in"/>:</label>
		</div>
		<div class="span-10 last">
		    <div class="inline">
		    	<label class="simple"><form:checkbox id="isEnglishShort" path="isEnglishShort" value="EnglishShort" cssErrorClass="fieldError"/><fmt:message key="search.criteria.search.english.short"/></label>
				<label class="simple"><form:checkbox id="isEnglishLong" path="isEnglishLong" value="EnglishLong" cssErrorClass="fieldError"/><fmt:message key="search.criteria.search.english.long"/></label>
				<label class="simple"><form:checkbox id="isEnglishUser" path="isEnglishUser" value="EnglishUser" cssErrorClass="fieldError"/><fmt:message key="search.criteria.search.english.user"/></label>
			 </div>
			 <div class="inline">
		    	<label class="simple"><form:checkbox id="isFrenchShort" path="isFrenchShort" value="FrenchShort" cssErrorClass="fieldError"/><fmt:message key="search.criteria.search.french.short"/></label>
				<label class="simple"><form:checkbox id="isFrenchLong" path="isFrenchLong" value="FrenchLong" cssErrorClass="fieldError"/><fmt:message key="search.criteria.search.french.long"/></label>
				<label class="simple"><form:checkbox id="isFrenchUser" path="isFrenchUser" value="FrenchUser" cssErrorClass="fieldError"/><fmt:message key="search.criteria.search.french.user"/></label>
			 </div>
			 <div class="inline">
			     <label class="simple"><form:checkbox id="isEnglishViewerContent" path="isEnglishViewerContent" value="EnglishViewerContent" cssErrorClass="fieldError"/><fmt:message key="search.criteria.search.english.viewer.content"/></label>
				 <label class="simple"><form:checkbox id="isFrenchViewerContent" path="isFrenchViewerContent" value="FrenchViewContent" cssErrorClass="fieldError"/><fmt:message key="search.criteria.search.french.viewer.content"/></label>
			 </div>			 
		</div>
	</div>
</fieldset>
<c:if test="${fn:containsIgnoreCase(search.classificationName,'cci')}">    
	<fieldset>
		<legend class="searchLegend"><fmt:message key="search.criteria.legend.reference.value.attribute"/></legend>
		<div class="span-8 form-row">
			<div class="span-2 label">
				<label for="refValueStatusCode"><fmt:message key="search.criteria.reference.value.attribute.status"/>:</label>
			</div>
			<div class="span-6 last">
				<form:input id="refValueStatusCode" path="refValueStatusCode" class="short" cssErrorClass="fieldError"/>
			</div>
		</div>
		<div class="span-8 form-row">
			<div class="span-3 label">
				<label for="refValueLocationModeCode"><fmt:message key="search.criteria.reference.value.attribute.location"/>:</label>
			</div>
			<div class="span-5 last">
				<form:input id="refValueLocationModeCode" path="refValueLocationModeCode" class="short" cssErrorClass="fieldError" />
			</div>
		</div>
		<div class="span-8 form-row">
			<div class="span-2 label">
				<label for="refValueExtentCode"><fmt:message key="search.criteria.reference.value.attribute.extent"/>:</label>
			</div>
			<div class="span-6 last">
				<form:input id="refValueExtentCode" path="refValueExtentCode" class="short" cssErrorClass="fieldError" />
			</div>
		</div>
	</fieldset>
</c:if>
<script type="text/javascript">
$(document).ready(function(){
	var isCCI = "${fn:containsIgnoreCase(search.classificationName,'cci') ? 'true' : 'false'}" == "true";
	
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
	
	var setWithinLevelLabel = function(level) {	
		var labelCategory = "<fmt:message key='search.criteria.level.within.category'/>"
			, labelGroup = "<fmt:message key='search.criteria.level.within.group'/>"
			, labelRubric = "<fmt:message key='search.criteria.level.within.rubric'/>"
			, labelChapter = "<fmt:message key='search.criteria.level.within.chapter'/>"
			, labelSection = "<fmt:message key='search.criteria.level.within.section'/>";
		
		var labelText = null;
		if(level == 'Category') {
			labelText = labelCategory;
		}
		else if(level == 'Group') {
			labelText = labelGroup;
		}
		else if(level == 'Rubric') {
			labelText = labelRubric;
		}
		else if(level == 'Block') {
			if(isCCI) {
				labelText = labelSection;
			}
			else {
				labelText = labelChapter;
			}
		}
		
		var $label = $("#withinLevelLabel");
		$label.text(labelText);
	};
	
	var setDefaultCodeForLevel = function(level) {		
		if(level == 'Chapter' || level == 'Section') {
			$("#codeFrom").val(null);
			$("#codeTo").val(null);
			$("#codeOther").val(null);
		}
		else if(level == 'Block') {
			$("#codeFrom").val(null);
			$("#codeTo").val(null);
			if(isCCI) {
				$("#codeOther").val("1");
			}
			else {
				$("#codeOther").val("01");
			}
		}
		else {
			var min, max;
			if(level == 'Category') {
				min = '8000/0';
				max = 'Z99';
			}
			else if(level == 'Group') {
				min = '1.AA';
				max = '9.ZZ';
			}
			else if(level == 'Rubric') {
				min = '1.AA.00';
				max = '9.ZZ.99';
			}
			$("#codeFrom").val(min);
			$("#codeTo").val(max);
			$("#codeOther").val(null);
		}
	};
	
	var toggleRefAttributes = function(level){
		if (level == 'Rubric' || level == 'Group'){
			$("#refValueStatusCode").removeAttr('disabled');
			$("#refValueLocationModeCode").removeAttr('disabled');
			$("#refValueExtentCode").removeAttr('disabled');
		}
		else{
			$("#refValueStatusCode").attr('disabled', 'disabled').val(null);
			$("#refValueLocationModeCode").attr('disabled', 'disabled').val(null);
			$("#refValueExtentCode").attr('disabled', 'disabled').val(null);
		}
	};
	
	var toggleCodeRanges = function(level) {
		var $rangeContainer = $("#rangeContainer");
		if(level == 'Chapter' || level == 'Section') {
			$rangeContainer.hide();
		}
		else if(level == 'Block') {
			$("#codeFrom").parent().hide();
			$("#codeTo").parent().hide();
			$("#codeOther").parent().show();
			$rangeContainer.show();
		}
		else {
			$("#codeFrom").parent().show();
			$("#codeTo").parent().show();
			$("#codeOther").parent().hide();
			$rangeContainer.show();
		}
	};
	
	var toggleSearchCheckboxes = function(searchText){
		if (!searchText.trim()){
			$("#isEnglishShort").attr('disabled', true);
			$("#isEnglishLong").attr('disabled', true);
			$("#isEnglishUser").attr('disabled', true);
			$("#isFrenchShort").attr('disabled', true);
			$("#isFrenchLong").attr('disabled', true);
			$("#isFrenchUser").attr('disabled', true);
			$("#isEnglishViewerContent").attr('disabled', true);
			$("#isFrenchViewerContent").attr('disabled', true);			
		}
		else{
			$("#isEnglishShort").removeAttr('disabled');
			$("#isEnglishLong").removeAttr('disabled');
			$("#isEnglishUser").removeAttr('disabled');
			$("#isFrenchShort").removeAttr('disabled');
			$("#isFrenchLong").removeAttr('disabled');
			$("#isFrenchUser").removeAttr('disabled');
			$("#isEnglishViewerContent").removeAttr('disabled');
			$("#isFrenchViewerContent").removeAttr('disabled');
		}
	}
	
	//set the current level label and default values
	var currentLevel = $("input[type='radio'][name='hierarchyLevel']:checked").val();
	setWithinLevelLabel(currentLevel);
	toggleCodeRanges(currentLevel);
	toggleRefAttributes(currentLevel);
	
	// toggle search checkboxes
	var searchText = $("#searchText").val();
	toggleSearchCheckboxes(searchText);
	
	var currentCodeFrom = $("#codeFrom").val();
	var currentOtherCode = $("#codeOther").val();
	if((currentCodeFrom == null || currentCodeFrom == "") && (currentOtherCode == null || currentOtherCode == "")) {
		setDefaultCodeForLevel(currentLevel);
	}
	
	$("input[type='radio'][name='hierarchyLevel']").click(function(){
		var level = $(this).val();
		setWithinLevelLabel(level);
		setDefaultCodeForLevel(level);
		toggleCodeRanges(level);
		toggleRefAttributes(level);
	});
	
	$("#searchText").bind("input",function(event){
		toggleSearchCheckboxes($(this).val());
	});
});
</script>