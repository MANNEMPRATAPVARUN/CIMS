<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<script type="text/javascript">
	var processedMessage = {};
	processedMessage["class"] = '';
	processedMessage["message"] = '';
	processedMessage["image"] = '';

	$(document).ready(function() {
		displayIcons();
	});

	function displayIcons() {

		$("#iconsLI").show();
	}

	/*********************************************************************************************************
	 * NAME:          Show Message    
	 * DESCRIPTION:   Called by other functions, responsible for showing messages to the user
	 *********************************************************************************************************/
	function showMessage() {

		var image = processedMessage["image"];

		$("#loadingInfo").text("");
		$("#loadingInfo").append(image);

		if (processedMessage["message"].length > 1) {
			$("#loadingInfo").attr("class", processedMessage["class"]);
			$("#loadingInfo").append(" " + processedMessage["message"]);
			processedMessage["message"] = "";
			$("#loadingInfo").show();
		}

		processedMessage["image"] = "";
	}

	/*********************************************************************************************************
	 * NAME:          Hide Message    
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function hideMessage() {
		$("#loadingInfo").hide();
	}

	/*********************************************************************************************************
	 * NAME:          Show Processing Screen    
	 * DESCRIPTION:   Spinner
	 *********************************************************************************************************/
	function showProcessingScreen() {
		$("*").css("cursor", "progress");
		$('body').append('<div class="modal">');
	}

	/*********************************************************************************************************
	 * NAME:          Hide Processing Screen    
	 * DESCRIPTION:   Hide Spinner
	 *********************************************************************************************************/
	function hideProcessingScreen() {
		$("*").css("cursor", "auto");
		$('.modal').remove();
	}

	function showInfoMessage(messageToShow) {
		processedMessage["message"] = messageToShow;
		processedMessage["class"] = "info";

		showMessage();
	}

	function showSuccessMessage(messageToShow) {
		processedMessage["message"] = messageToShow;
		processedMessage["class"] = "success";
		processedMessage["image"] = "<img src=\"img/icons/Ok.png\"/>";

		showMessage();
	}

	function showErrorMessagesFromResponse(response) {
		var errorMessages = "";
		for (var i = 0; i < response.errorMessageList.length; i++) {
			var item = response.errorMessageList[i];
			errorMessages += "<img src=\"img/icons/Error.png\"/> "
					+ item.defaultMessage;
			errorMessages += "<br/>";
		}

		processedMessage["message"] = errorMessages;
		processedMessage["class"] = "error";

		showMessage();
	}

	function showErrorMessages(errorMessages) {
		processedMessage["message"] = errorMessages;
		processedMessage["class"] = "error";

		showMessage();
	}

	function disableRemoveButton() {		
		$("#remove").attr('src', '<c:url value="/img/icons/RemoveGrey.png"/>');
	}

	function enableRemoveButton() {		
		$("#remove").attr('src', '<c:url value="/img/icons/Remove.png"/>');
	}
	
	function ensureRadioButtonSelected() {
		var radioId = $('input[name=radioSelection]:checked').attr('id');
		if (typeof radioId === 'undefined') {
			console.log('No radio button selected');
			return true;
		}
	}

	function editPicklist(contextId, picklistElementId, picklistElementVersionId) {
		window.location.href = "<c:url value='/refset/picklist/view.htm?contextId=' />"
				+ contextId
				+ '&picklistElementId='
				+ picklistElementId
				+ '&picklistElementVersionId=' + picklistElementVersionId + '&elementId=' + ${viewBean.elementId}	+ '&elementVersionId=' + ${viewBean.elementVersionId};		
	}

	function removePicklist() {
		var radioId = $('input[name=radioSelection]:checked').attr('id');
		if (typeof radioId === 'undefined') {
			console.log('No radio button selected');
			return false;
		}
		
		var ids = radioId.split('|');
		var postPage = "<c:url value='/refset/picklist/add.htm?actionType=drop&contextId='/>"
				+ ids[0]
				+ '&picklistElementId='
				+ ids[1]
				+ '&picklistElementVersionId=' + ids[2];

		$
				.ajax({
					url : postPage,
					type : 'POST',
					success : function(data) {
						if (data.status == 'FAILED') {							
							hideMessage();
							
							var errorMessages = "";
							
							for (var i = 0; i < data.errors.length; i++) {
								var item = data.errors[i];
								errorMessages += item.message;
								errorMessages += "<br/>";
							}
							
							showErrorMessages(errorMessages);
						} else {
							showInfoMessage($.trim(data.message));

							deleteSelectedRow();

							disableSaveButton();							
							disableCancelButton();
							enableAddButton();
						}
					},
					error : function(data) {
						var errorMessages = "System error occurred, please contact System administrator.";
						
						showErrorMessages(errorMessages);
					}					
				});
	}

	function savePicklist() {
		if (!isPicklistNameValid() || !isPicklistCodeValid() || !isPicklistClassificationStandardValid()) {
			return false;
		}

		var curPicklistName = $('#curPicklistName').val();
		var curPicklistCode = $('#curPicklistCode').val();
		var curPicklistClassificationStandard = $('#curPicklistClassificationStandard :selected').val();
		
		$('#name').val(curPicklistName);
		$('#code').val(curPicklistCode);
		$('#classificationStandard').val(curPicklistClassificationStandard);		

		$
				.ajax({
					url : "<c:url value='/refset/picklist/add.htm?actionType=save' />",
					data : $("#picklistAddForm").serialize(),
					type : "POST",
					async : false,
					cache : false,
					success : function(data) {
						if (data.status == 'SUCCESS') {
							showInfoMessage($.trim(data.message));	

							$('input[type=radio][name=radioSelection]:checked').attr(
									'id',
									'${viewBean.contextId}' + '|' + data.result.elementId + '|'	+ data.result.elementVersionId);

							var picklistNameHtml = '<a href="#" onclick="editPicklist(${viewBean.contextId},' + data.result.elementId + ',' + data.result.elementVersionId + ');">' + $.trim(curPicklistName) + '</a>';
							
							$('#curPickListRow').find('td:eq(1)').html($.trim(curPicklistCode));
							$('#curPickListRow').find('td:eq(2)').html(picklistNameHtml);
							$('#curPickListRow').find('td:eq(3)').html(curPicklistClassificationStandard);		
							$('#curPickListRow').removeAttr('id');

							disableSaveButton();							
							disableCancelButton();
							enableAddButton();
							enableRemoveButton();
							$('input[name=radioSelection]').attr("disabled", false);													
						} else {
							var errorMessages = "";
							
							for (var i = 0; i < data.errors.length; i++) {
								var item = data.errors[i];
								errorMessages += item.message;
								errorMessages += "<br/>";
							}
							
							showErrorMessages(errorMessages);
						}
					},
					error : function(data) {
						var errorMessages = "System error occurred, please contact System administrator.";
						
						showErrorMessages(errorMessages);
					}
				});

		return true;
	}

	function addPicklistRow() {
		hideMessage();
		
		var pickListAvailableClassificationStandard = [ 'ICD-10-CA', 'CCI' ];

		$("#picklist")
				.find('tbody')
				.append(
						"<tr id='curPickListRow'><td style='border: 1px solid #96BEBD;'><input type='radio' name='radioSelection' id='curPickListColumn' checked='checked' onclick='handlePicklistSelected();' /></td>"
						        + "<td style='border: 1px solid #96BEBD;'><input type='text' id='curPicklistCode' name='curPicklistCode' maxlength='10' onchange='validatePicklistCode();' onmouseout='validatePicklistCode();'/></td>"
								+ "<td style='border: 1px solid #96BEBD;'><input type='text' id='curPicklistName' name='curPicklistName' maxlength='100' onchange='validatePicklistName();' onmouseout='validatePicklistName();' /></td>"								
								+ "<td style='border: 1px solid #96BEBD;'><select name='curPicklistClassificationStandard' id='curPicklistClassificationStandard' onchange='validatePicklistClassificationStandard();'></select></td></tr>");

		pickListAvailableClassificationStandard.unshift('');

		$.each(pickListAvailableClassificationStandard, function(index, value) {
			$('#curPicklistClassificationStandard').append(
					$("<option></option>").attr("value", value).text(value));
		});

		$('input[name=radioSelection]').attr("disabled", true);
		
		enableSaveButton();
		enableCancelButton();
		disableAddButton();
		disableRemoveButton();
	}

	function isInputEmpty(input) {
		return (input == '' || $.trim(input) == '');
	}

	function isPicklistNameValid() {
		var curPicklistName = $('#curPicklistName').val();

		return !isInputEmpty(curPicklistName);
	}

	function validatePicklistName() {
		$('#curPicklistName').css('border-color',
				!isPicklistNameValid() ? 'red' : '');
	}

	function isPicklistCodeValid() {
		var curPicklistCode = $('#curPicklistCode').val();

		return !isInputEmpty(curPicklistCode);
	}

	function validatePicklistCode() {
		$('#curPicklistCode').css('border-color',
				!isPicklistCodeValid() ? 'red' : '');
	}

	function isPicklistClassificationStandardValid() {
		var curPicklistisClassificationStandard = $(
				'#curPicklistClassificationStandard :selected').val();

		return !isInputEmpty(curPicklistisClassificationStandard);
	}

	function validatePicklistClassificationStandard() {
		$('#curPicklistClassificationStandard').css('border-color',
				!isPicklistClassificationStandardValid() ? 'red' : '');
	}

	function enableSaveButton() {
		$("#save").attr('src', '<c:url value="/img/icons/Save.png" />');
	}

	function disableSaveButton() {
		$("#save").attr('src', '<c:url value="/img/icons/SaveGrey.png" />');
	}	

	function enableCancelButton() {
		$("#canel").attr('src', '<c:url value="/img/icons/Cancel.png" />');
	}

	function disableCancelButton() {
		$("#canel").attr('src', '<c:url value="/img/icons/CancelGrey.png" />');
	}

	function enableAddButton() {
		$("#add").attr('src', '<c:url value="/img/icons/Add.png" />');	

		$("#add").unbind('click').click(function() {
		    addPicklistRow();
		});	
	}

	function disableAddButton() {
		$("#add").attr('src', '<c:url value="/img/icons/AddGrey.png" />');	

		$("#add").unbind('click');	
	}

	function cancelPicklist() {
		var selectedRadioId = $('input[type=radio][name=radioSelection]:checked').attr('id');

		if (selectedRadioId != 'curPickListColumn') {
			return true;
		}
		
		deleteSelectedRow();

		disableCancelButton();
		disableSaveButton();
		enableAddButton();

		return true;
	}

	function deleteSelectedRow() {
		$('input[type=radio][name=radioSelection]:checked').closest('tr')
				.remove();
		$('input[name=radioSelection]').attr("disabled", false);
	}

	function processRemove() {
		var selectedRadioId = $('input[type=radio][name=radioSelection]:checked').attr('id');

		if (typeof selectedRadioId === 'undefined') {
			return true;
		}		

		if (selectedRadioId == 'curPickListColumn') {
			return true;
		}

		$("#removalConfirmation")
				.text(
						'Please confirm that you want to permanently remove the selected picklist from the system.');

		$('#removalConfirmation').dialog({
			title : 'Confirmation: Removal of Picklist',
			width : 350,
			height : 150,
			modal : true,
			resizable : false,
			draggable : false,
			zIndex : 10000,
			buttons : [ {
				text : 'Remove',
				click : function() {
					$(this).dialog('close');
					removePicklist();
				}
			}, {
				text : 'Cancel',
				click : function() {
					$(this).dialog('close');
				}
			} ]
		});
	}

	function handlePicklistSelected() {
		var selectedRadioId = $('input[type=radio][name=radioSelection]:checked').attr('id');

		if (selectedRadioId == 'curPickListColumn') {
			enableCancelButton();
			enableSaveButton();
			disableRemoveButton();
			disableAddButton();
			
			return true;
		}

		disableCancelButton();
		disableSaveButton();
		enableRemoveButton();		
		
		return true;
	}
</script>

<div id="removalConfirmation" style="display: none;">confirm</div>
<div class="icons">
	<ul style="padding-left: .9em;">
		<li style="float: left; list-style-type: none;">
			<div id="loadingInfo" class="info"
				style="display: none; margin-bottom: 0.1em; width: 900px; padding-top: 0.5em; padding-bottom: 0.5em;">Loading</div>
		</li>

		<li id="iconsLI"
			style="float: right; top: 0px; border: 0px; background: #ffffff; list-style-type: none; display: none;">
			<img id="save" class="viewMode" title="Save"
			src="<c:url value='/img/icons/SaveGrey.png' />"
			onclick="savePicklist();" /> 
			<img id="add" class="viewMode" title="Add" src="<c:url value='/img/icons/AddGrey.png'/>" />
			<img id="canel" class="viewMode" title="Cancel"
			src="<c:url value='/img/icons/CancelGrey.png' />"
			onclick="cancelPicklist();" /> <img id="remove" class="viewMode"
			title="Delete" src="<c:url value='/img/icons/RemoveGrey.png'/>"
			onclick="processRemove();" />
		</li>
	</ul>
</div>

<div class="content" style="display: inline-block; width: 98%;">
	<div class="sectionHeader">Picklists</div>
</div>

<div class="content">
	<table class="listTable" style="width: 1100px; margin-top: 0px;"
		id="picklist">
		<thead>
			<tr>
				<th class="tableHeader sizeThirty"></th>				
				<th class="tableHeader sizeOneEighty"><fmt:message
						key="picklist.code" /></th>
				<th class="tableHeader sizeOneEighty"><fmt:message
						key="picklist.name" /></th>
				<th class="tableHeader sizeOneTen"><fmt:message
						key="picklist.classificationstandard" /></th>
			</tr>
		</thead>
		<tbody id="picklistTBody">
			<c:forEach items='${pickLists}' var='picklist' varStatus="loopStatus">
				<tr>
					<td style='border: 1px solid #96BEBD;'><input
						name="radioSelection"
						id="${viewBean.contextId}|${picklist.elementIdentifier.elementId}|${picklist.elementIdentifier.elementVersionId}"
						type="radio" onclick='handlePicklistSelected();' 
						<c:if test="${refsetPermission != 'WRITE'}">disabled</c:if> /></td>
					<td style='border: 1px solid #96BEBD;'>
					    ${picklist.code}
					</td>
					<td style='border: 1px solid #96BEBD;'>
					    <a href="#" onclick="editPicklist(${viewBean.contextId}, ${picklist.elementIdentifier.elementId}, ${picklist.elementIdentifier.elementVersionId});">${picklist.name}</a>
					</td>					
					<td style='border: 1px solid #96BEBD;'>
						${picklist.classificationStandard}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>

	<form:form method="POST" modelAttribute="viewBean" id="picklistAddForm">
		<input type="hidden" name="contextId" value="${viewBean.contextId }" />
		<input type="hidden" name="elementId" value="${viewBean.elementId }" />
		<input type="hidden" name="elementVersionId"
			value="${viewBean.elementVersionId }" />
		<input type="hidden" name="name" id="name" />
		<input type="hidden" name="code" id="code" />
		<input type="hidden" name="classificationStandard"
			id="classificationStandard" />
	</form:form>
</div>

<script type="text/javascript">
    $(document).ready(function() {
        <c:if test="${refsetPermission == 'WRITE'}">
            enableAddButton();
        </c:if>
    });
</script>