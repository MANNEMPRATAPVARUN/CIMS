<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<fieldset>
	<legend class="searchLegend"><fmt:message key="search.criteria.legend.set"/></legend>
	<div class="span-7 form-row">
		<div class="span-3 label">
			<div><label for="year"><fmt:message key="search.criteria.year"/>:</label></div>
			<div><label for="priorYear"><fmt:message key="search.criteria.year.prior"/>:</label></div>
		</div>
		<div class="span-4 last">
			<div><form:select id="year" path="contextId" items="${contextIds}" itemLabel="versionCode" itemValue="contextId" cssErrorClass="fieldError"></form:select></div>
			<div><form:select id="priorYear" path="priorContextId" items="${contextIds}" itemLabel="versionCode" itemValue="contextId" cssErrorClass="fieldError"></form:select></div>
		</div>
	</div>
	<div class="span-15 form-row">
		<div class="span-4 label">
			<label><fmt:message key="search.criteria.comparative.type"/>:</label>
		</div>
		<div class="span-11 last">
			<div class="span-3">
				<div><form:radiobutton path="comparativeType" id="newCode" name="comparativeType" value="NewCode"/><label for="newCode" class="simple"><fmt:message key="search.criteria.comparative.type.code.new"/></label></div>
				<div><form:radiobutton path="comparativeType" id="disabledCode" name="comparativeType" value="DisabledCode"/><label for="disabledCode" class="simple"><fmt:message key="search.criteria.comparative.type.code.disabled"/></label></div>
			</div>
			<div class="span-8 last">
				<div><form:radiobutton path="comparativeType" id="modifiedCode" name="comparativeType" value="ModifiedCodeTitle"/><label for="modifiedCode" class="simple"><fmt:message key="search.criteria.comparative.type.code.modified"/></label></div>
				<div><form:radiobutton path="comparativeType" id="modifiedViewer" name="comparativeType" value="ModifiedViewerContent"/><label for="modifiedViewer" class="simple"><fmt:message key="search.criteria.comparative.type.viewer.modified"/></label></div>
			</div>
		</div>
	</div>
	
	<div id="searchLanguage"><label for="modifiedLanguage"><fmt:message key="search.criteria.language"/>:</label>
		<form:select id="modifiedLanguage" name="modifiedLanguage" path="modifiedLanguage" cssErrorClass="fieldError">
    		<form:option value="ENG">English</form:option>
    		<form:option value="FRA">French</form:option>
		</form:select>
	</div>
	
</fieldset>
<fieldset>
	<legend class="searchLegend"><fmt:message key="search.criteria.legend.hierarchy.level"/></legend>
	<div class="span-24 inline form-row">
		<div class="span-2">
			<label for="levelChapter"><fmt:message key="search.criteria.level"/>:</label>
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
		<div id="codeButtons" class = "label">
			<c:choose>
				<c:when test="${!fn:containsIgnoreCase(search.classificationName,'cci')}">
					<form:checkbox path="codesOnly" id="codesOnly" value="codesOnly"/>
					<label for="codesOnly"><fmt:message key="search.criteria.validCodesOnly"/></label>
				</c:when>
				<c:otherwise>
					<form:checkbox path="codesOnly" id="codesOnly" value="CodesOnly"/>
					<label for="codesOnly"><fmt:message key="search.criteria.codesOnly"/></label>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</fieldset>
<script type="text/javascript">
$(document).ready(function(){
	
	var isCCI = "${fn:containsIgnoreCase(search.classificationName,'cci') ? 'true' : 'false'}" == "true";
	
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

	var displayCodeButtons = function(level) {
		if(($('#newCode').is(':checked') || $('#disabledCode').is(':checked')) && (level == 'Category' || level == 'Rubric')) {
			$('#codeButtons').show();
		} else {
			$('#codeButtons').hide();
		} 

		isCCI ? $('#codeButtons').addClass('span-13') : $('#codeButtons').addClass('span-4');
	};

	var displayLanguageDropDown = function(comparativeType) {
			(comparativeType == 'ModifiedViewerContent' || comparativeType == 'ModifiedCodeTitle') ? $('#searchLanguage').show() : $('#searchLanguage').hide();
	};
	
	//set the current level label and default values
	var currentLevel = $("input[type='radio'][name='hierarchyLevel']:checked").val();
	var currentComparativeType = $("input[type='radio'][name='comparativeType']:checked").val();
	setWithinLevelLabel(currentLevel);
	toggleCodeRanges(currentLevel);
	displayCodeButtons(currentLevel);
	displayLanguageDropDown();
	
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
		displayCodeButtons(level);
	});

	$("input[type='radio'][name='comparativeType']").click(function(){
		var comparativeType = $(this).val();
		var level = $("input[type='radio'][name='hierarchyLevel']:checked").val();
		displayCodeButtons(level);
		displayLanguageDropDown(comparativeType);
	});

	var setFinalColumnTypeIds = function() {
		var list = "";
		$("#selectedColumns option").each(function(i,option){
			if(i > 0) {
				list += ",";
			}
			list += $(option).val();
		});
		$("#columnTypeIds").val(list);
	};
	
	$('#modifiedLanguage').change(function(){

		if(isCCI) {
			if($('#modifiedLanguage option:selected').val() == 'FRA') {
				$("#availColumns").append($('#selectedColumns option[value="99"]'));
				$('#selectedColumns option[value="99"]').remove();
				$("#availColumns").append($('#selectedColumns option[value="100"]'));
				$('#selectedColumns option[value="100"]').remove();

				$("#selectedColumns").append($('#availColumns option[value="101"]'));
				$('#availColumns option[value="101"]').remove();
				$("#selectedColumns").append($('#availColumns option[value="102"]'));
				$('#availColumns option[value="102"]').remove();

			} else {
				$("#availColumns").append($('#selectedColumns option[value="101"]'));
				$('#selectedColumns option[value="101"]').remove();
				$("#availColumns").append($('#selectedColumns option[value="102"]'));
				$('#selectedColumns option[value="102"]').remove();

				$("#selectedColumns").append($('#availColumns option[value="99"]'));
				$('#availColumns option[value="99"]').remove();
				$("#selectedColumns").append($('#availColumns option[value="100"]'));
				$('#availColumns option[value="100"]').remove();

			}
		} else {

			if($('#modifiedLanguage option:selected').val() == 'FRA') {
				$("#availColumns").append($('#selectedColumns option[value="79"]'));
				$('#selectedColumns option[value="79"]').remove();
				$("#availColumns").append($('#selectedColumns option[value="80"]'));
				$('#selectedColumns option[value="80"]').remove();

				$("#selectedColumns").append($('#availColumns option[value="83"]'));
				$('#availColumns option[value="83"]').remove();
				$("#selectedColumns").append($('#availColumns option[value="84"]'));
				$('#availColumns option[value="84"]').remove();

			} else {
				$("#availColumns").append($('#selectedColumns option[value="83"]'));
				$('#selectedColumns option[value="83"]').remove();
				$("#availColumns").append($('#selectedColumns option[value="84"]'));
				$('#selectedColumns option[value="84"]').remove();

				$("#selectedColumns").append($('#availColumns option[value="79"]'));
				$('#availColumns option[value="79"]').remove();
				$("#selectedColumns").append($('#availColumns option[value="80"]'));
				$('#availColumns option[value="80"]').remove();

			}
			
		}
		setFinalColumnTypeIds();

	});



});
</script>