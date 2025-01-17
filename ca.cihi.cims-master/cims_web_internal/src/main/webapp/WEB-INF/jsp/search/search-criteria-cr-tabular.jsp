<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<fieldset>
	<legend class="searchLegend"><fmt:message key="search.criteria.legend.change.type"/><label class="mandatory"></label></legend>
	<div class="span-17 inline form-row">
		<form:checkbox path="newCodeValues" id="newCodeValues" class="mainChangeTypes"/><label for="newCodeValues" class="simple"><fmt:message key="search.criteria.new.code"/></label>
		<form:checkbox path="disabledCodeValues" id="disabledCodeValues" class="mainChangeTypes"/><label for="disabledCodeValues" class="simple"><fmt:message key="search.criteria.disabled.code"/></label>
		<form:checkbox path="modifiedProperties" id="modifiedProperties" class="mainChangeTypes"/><label for="modifiedProperties" class="simple"><fmt:message key="search.criteria.modified.property"/></label>
		<form:checkbox path="validations" id="validations" class="mainChangeTypes"/><label for="validations" class="simple"><fmt:message key="search.criteria.validations"/></label>
		<form:checkbox path="conceptMovement" id="conceptMovement"/><label id="conceptMovementLabel" for="conceptMovement" class="simple"><fmt:message key="search.criteria.conceptMovement"/></label>
	</div>
	<div id="languageDiv" class="span-8 inline form-row">
		<div><label for="evolutionLanguage"><fmt:message key="search.criteria.language"/>:</label>
			<form:select id="evolutionLanguage" path="evolutionLanguage" cssErrorClass="fieldError">
    			<form:option value="ENG">English</form:option>
    			<form:option value="FRA">French</form:option>
			</form:select>
		</div>
	</div>
</fieldset>
<fieldset>
	<legend class="searchLegend"><fmt:message key="search.criteria.legend.hierarchy.level"/></legend>
	<div class="span-24 inline form-row">
		<div class="span-4">
			<c:choose>
				<c:when test="${!fn:containsIgnoreCase(search.classificationName,'cci')}">
					<div>
						<form:radiobutton path="level" value="Category" name="level" id="levelCategory"/>
						<label for="levelCategory" class="simple"><fmt:message key="search.criteria.level.category"/></label>
					</div>
				</c:when>
				<c:otherwise>
					<div>
						<form:radiobutton path="level" value="Group" name="level" id="levelGroup"/>
						<label for="levelGroup" class="simple"><fmt:message key="search.criteria.level.group"/></label>
					</div>
					<div>
						<form:radiobutton path="level" value="Rubric" name="level" id="levelRubric"/>
						<label for="levelRubric" class="simple"><fmt:message key="search.criteria.level.rubric"/></label>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="span-20 last">
			<div class="span-4 label"><label><fmt:message key="search.criteria.level.within"/>:</label></div>
			<div class="span-16 last">
				<div id="withinLevelLabel"></div>
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
<script type="text/javascript">
$(document).ready(function(){
	var setWithinLevelLabel = function(level) {
		var labelCategory = "<fmt:message key='search.criteria.level.within.category'/>"
			, labelGroup = "<fmt:message key='search.criteria.level.within.group'/>"
			, labelRubric = "<fmt:message key='search.criteria.level.within.rubric'/>";
		
		var label;
		if(level == 'Category') {
			label = labelCategory;
		}
		else if(level == 'Group') {
			label = labelGroup;
		}
		else if(level == 'Rubric') {
			label = labelRubric;
		}
		$("#withinLevelLabel").text(label);
	};
	
	var setDefaultCodeForLevel = function(level) {
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
	};

	var displayEvolutionContent = function() {
		if($('#evolutionRequired option:selected').text() == 'Yes') {
			$('#conceptMovement').show();
			$('#conceptMovementLabel').show();
			$('#languageDiv').show();
			if($('#modifiedProperties').is(':checked')) {
				$('select#evolutionLanguage').attr('disabled', false);
			} else {
				$('select#evolutionLanguage').attr('disabled', true);
			}
		} else {
			$('#conceptMovement').hide();
			$('#conceptMovementLabel').hide();
			$('#languageDiv').hide();
		}
	};

	
	//set the current level label and default values
	var currentLevel = $("input[type='radio'][name='level']:checked").val();
	setWithinLevelLabel(currentLevel);
	
	var currentCodeFrom = $("#codeFrom").val();
	if(currentCodeFrom == null || currentCodeFrom == "") {
		setDefaultCodeForLevel(currentLevel);
	}
	
	$("input[type='radio'][name='level']").click(function(){
		var level = $(this).val();
		setWithinLevelLabel(level);
		setDefaultCodeForLevel(level);
	});

	displayEvolutionContent();

	$('#conceptMovement').click(function(){
		if($('#conceptMovement').is(':checked')) {
			$('#newCodeValues').attr('checked', false);
			$('#disabledCodeValues').attr('checked', false);
			$('#modifiedProperties').attr('checked', false);
			$('#validations').attr('checked', false);
			$('select#evolutionLanguage').attr('disabled', true);
		} 
	});

	$('.mainChangeTypes').click(function(){
		if($('#newCodeValues').is(':checked') || $('#disabledCodeValues').is(':checked') ||
		   $('#modifiedProperties').is(':checked') || $('#validations').is(':checked')){
			$('#conceptMovement').attr('checked', false);
		}
		if($('#modifiedProperties').is(':checked')){
			$('select#evolutionLanguage').attr('disabled', false);
		} else {
			$('select#evolutionLanguage').attr('disabled', true);
		}
	});
	
	$('#evolutionRequired').change(function(){
		displayEvolutionContent()
	});

	
});
</script>