<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<script type="text/javascript">
	var pickListMode = '';
	var pickListColumnOrder = '';
	var pickListRevisedColumnName = '';
	var isSublistColumn = false;	

	var processedMessage = {};
	processedMessage["class"] = '';
	processedMessage["message"] = '';
	processedMessage["image"] = '';

	var pickListAvailableColumnTypes = [];

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

	function isRadioButtonSelected() {
		var radioId = $('input[name=picklistColumn]:checked').attr('id');

		return (typeof radioId === 'undefined') ? false : true;
	}

	function addPicklistColumn(containerElementId, containerElementVersionId,
			columnElementId, columnElementVersionId) {
		hideMessage();

		if (pickListMode == 'add' || pickListMode == 'edit') {
			return true;
		}

		$('#containerSublist').val('false');

		var result = getPicklistAvailableColumn(containerElementId,
				containerElementVersionId, columnElementId,
				columnElementVersionId);

		if (!result) {
			return true;
		}

		pickListMode = 'add';
		disableAddButton();
		disableRemoveButton();
		enableSaveButton();
		enableCancelButton();
		disableResetButton();

		$("#picklistTable")
				.find('tbody')
				.append(
						"<tr id='curPickListColumnRow'><td style='border: 1px solid #96BEBD;'><input type='radio' name='picklistColumn' id='curPickListColumn' checked='checked' onclick='handleColumnSelected();' /></td>"
								+ "<td style='border: 1px solid #96BEBD;' id='curColumnOrder'><input type='text' id='curPickListColumnOrder' name='picklistColumnOrder' maxlength='5' style='width: 50px;' onchange='validateColumnOrder();' /></td>"
								+ "<td style='border: 1px solid #96BEBD;' id='curColumnType'><select name='pickListColumnType' id='curPickListColumnType' onchange='changeColumnType(this);'></select></td>"
								+ "<td style='border: 1px solid #96BEBD;'></td><td style='border: 1px solid #96BEBD;'></td>"
								+ "<td style='border: 1px solid #96BEBD;' id='curRevisedColumnName'><input type='text' id='curPickListRevisedColumnName' name='pickListRevisedColumnName' maxlength='35' style='width: 230px;' onchange='validateRevisedColumnName();' /></td></tr>");

		$.each(pickListAvailableColumnTypes, function(index, value) {
			$('#curPickListColumnType').append(
					$("<option></option>").attr("value", value).text(value));
		});

		$('input[name="actionType"]').val('create');
		$('#curContainerSublist').val('false');
		$('#curContainerElementId').val(columnElementId);
		$('#curContainerElementVersionId').val(columnElementVersionId);

		$('input[name=picklistColumn]').attr("disabled", true);
	}

	function addPicklistSublistColumn(containerElementId,
			containerElementVersionId, columnElementId, columnElementVersionId,
			el) {
		cancelPicklistColumn();
		
		hideMessage();		

		$('#containerSublist').val('true');

		var result = getPicklistAvailableColumn(containerElementId,
				containerElementVersionId, columnElementId,
				columnElementVersionId);

		if (!result) {
			return true;
		}

		pickListMode = 'add';
		disableAddButton();
		disableRemoveButton();
		enableSaveButton();
		enableCancelButton();
		disableResetButton();
		$('input[name=picklistColumn]').attr("disabled", true);

		var $this = $(el), $parentTR = $this.closest('tr');

		$(
				"<tr id='curPickListColumnRow'><td style='border: 1px solid #96BEBD;'><input type='radio' name='picklistColumn' id='curPickListColumn' checked='checked' onclick='handleColumnSelected();' /></td>"
						+ "<td style='border: 1px solid #96BEBD;'></td><td style='border: 1px solid #96BEBD;'></td>"
						+ "<td style='border: 1px solid #96BEBD;' id='curColumnOrder'><input type='text' id='curPickListColumnOrder' name='picklistColumnOrder' maxlength='5' style='width: 50px;' onchange='validateColumnOrder();' /></td>"
						+ "<td style='border: 1px solid #96BEBD;' id='curColumnType'><select name='pickListColumnType' id='curPickListColumnType' onchange='changeColumnType(this);'></select></td>"
						+ "<td style='border: 1px solid #96BEBD;' id='curRevisedColumnName'><input type='text' id='curPickListRevisedColumnName' name='pickListRevisedColumnName' maxlength='35' style='width: 230px;' onchange='validateRevisedColumnName();' /></td></tr>")
				.insertAfter($parentTR);

		$.each(pickListAvailableColumnTypes, function(index, value) {
			$('#curPickListColumnType').append(
					$("<option></option>").attr("value", value).text(value));
		});

		$('input[name="actionType"]').val('create');
		$('#curContainerSublist').val('true');
		$('#curContainerElementId').val(columnElementId);
		$('#curContainerElementVersionId').val(columnElementVersionId);

		$this.attr('src', '<c:url value="/img/icons/AddGrey-small.png" />');
		$this.unbind('click');		 
	}

	function getPicklistAvailableColumn(containerElementId,
			containerElementVersionId, columnElementId, columnElementVersionId) {
		$('#containerElementId').val(containerElementId);
		$('#containerElementVersionId').val(containerElementVersionId);
		$('#columnElementId').val(columnElementId);
		$('#columnElementVersionId').val(columnElementVersionId);

		var result = true;

		$
				.ajax({
					url : "<c:url value='/refset/picklist/availableColumnTypes.htm' />",
					data : $("#picklistAvailableColumnForm").serialize(),
					type : "POST",
					async : false,
					cache : false,
					success : function(data) {
						pickListAvailableColumnTypes = data.result;
						pickListAvailableColumnTypes.unshift('');

						if (data.message != null && $.trim(data.message) != '') {
							showInfoMessage($.trim(data.message));

							result = false;
						}
					},
					error : function(data) {
						var errorMessages = "System error occurred, please contact System administrator.";
						showErrorMessages(errorMessages);

						result = false;
					}
				});

		return result;
	}
	
	function savePicklistColumn() {
		var actionType = $('input[name="actionType"]').val();
		var result = true;

		if (actionType != 'drop') {
			var columnOrderValid = isColumnOrderValid();

			if (!columnOrderValid) {
				validateColumnOrder();
			}

			var columnTypeValid = isColumnTypeValid();

			if (!columnTypeValid) {
				validateColumnType();
			}

			var revisedColumnNameValid = isRevisedColumnNameValid();

			if (!revisedColumnNameValid) {
				validateRevisedColumnName();
			}

			if (!columnOrderValid || !columnTypeValid
					|| !revisedColumnNameValid) {
				return true;
			}
		}

		disableSaveButton();
		showProcessingScreen();			

		var columnOrder = $('#curPickListColumnOrder').val();
		var columnType = $('#curPickListColumnType').val();
		var revisedColumnName = $('#curPickListRevisedColumnName').val();
		var curContainerElementId = $('#curContainerElementId').val();
		var curContainerElementVersionId = $('#curContainerElementVersionId')
				.val();
		var curContainerSublist = $('#curContainerSublist').val();

		$('input[name="columnOrder"]').val(columnOrder);
		$('input[name="columnType"]').val(columnType);
		$('input[name="revisedColumnName"]').val(revisedColumnName);		
		
		$
				.ajax({
					url : "<c:url value='/refset/picklist/savePicklistColumn.htm' />",
					data : $("#picklistColumnAddForm").serialize(),					
					type : "POST",					
					cache : false,
					success : function(data) {
						hideProcessingScreen();
						
						if (data.status == 'SUCCESS') {
							pickListMode = '';

							if (actionType != 'drop') {
								if (data.result.sublist) {
									var addSublistHtml = '<div style="text-align: right"><img name="addSublist" class="viewMode" title="Add" src="'
											+ '<c:url value="/img/icons/AddGrey-small.png" />' + '" /></div>';
											
									$('#curPickListColumnRow').find('td:eq(4)')
											.html(addSublistHtml);
								}

								$('#curPickListColumnRow').attr(
										'id',
										'row-' + curContainerElementId + '|'
												+ curContainerElementVersionId
												+ '|' + data.result.elementId
												+ '|'
												+ data.result.elementVersionId);
								$('#curPickListColumn').attr(
										'id',
										curContainerElementId + '|'
												+ curContainerElementVersionId
												+ '|' + data.result.elementId
												+ '|'
												+ data.result.elementVersionId
												+ '|' + curContainerSublist
												+ '|' + columnType + '|' + 'true');

								$('#curColumnOrder').html(columnOrder);
								$('#curColumnOrder').removeAttr('id');

								$('#curColumnType').html(columnType);
								$('#curColumnType').removeAttr('id');

								$('#curRevisedColumnName').html(
										revisedColumnName);
								$('#curRevisedColumnName').removeAttr('id');

								$('#curColumnElementId').val(
										data.result.elementId);
								$('#curColumnElementVersionId').val(
										data.result.elementVersionId);

								$('input[name=picklistColumn]').attr(
										"disabled", false);

								disableSaveButton();
								disableCancelButton();
								disableResetButton();
								enableEditButton();
								enableAddButton();
								enableRemoveButton();

								showInfoMessage(data.message);

								result = true;
							} else {
							    disableRemoveButton();
							    disableSaveButton();
							    disableCancelButton();
							    disableResetButton();
							    disableEditButton();
							    enableAddButton();

							    showInfoMessage(data.message);

							    result = true;
							}
						} else {
							var errorMessages = "";

							for (var i = 0; i < data.errors.length; i++) {
								var item = data.errors[i];
								errorMessages += item.message;
								errorMessages += "<br />";
							}

							showErrorMessages(errorMessages);
							enableSaveButton();

							result = false;
						}						
					},
					error : function(data) {
						hideProcessingScreen();
						
						var errorMessages = "System error occurred, please contact System administrator.";
						showErrorMessages(errorMessages);
						enableSaveButton();
						
						result = false;
					}
				});

		return result;
	}

	function removePicklistColumn() {
		var selectedRadioId = $(
				'input[type=radio][name=picklistColumn]:checked').attr('id');

		if (typeof selectedRadioId === 'undefined') {
			return true;
		}

		if (selectedRadioId == 'curPickListColumn') {
			deleteRow();

			disableRemoveButton();
			disableSaveButton();
			disableCancelButton();
			disableResetButton();
			disableEditButton();
			enableAddButton();

			showInfoMessage('Column successfully deleted from the Picklist.');
		} else {
			$('input[name="actionType"]').val('drop');

			var result = savePicklistColumn();			
			
			if (result) {
				deleteRow();				
            } else {
                disableRemoveButton();
                disableSaveButton();
                disableCancelButton();
                disableResetButton();
                disableEditButton();
                enableAddButton();

                $('input[type=radio][name=picklistColumn]:checked').removeAttr("checked");    			
            }			
		}

		pickListMode = '';
	}
	
	function deleteRow() {
		var selectedRadioId = $(
				'input[type=radio][name=picklistColumn]:checked').attr('id');
		var ids = selectedRadioId.split('|');
		var currentElementVersionId = ids[3];

		$('input[type=radio][name=picklistColumn]').each(function() {
			var radioIds = $(this).attr('id');
			var pids = radioIds.split('|');

			var parentElementId = pids[1];

			if (currentElementVersionId == parentElementId) {
				$(this).closest('tr').remove();
			}
		});


		$('input[type=radio][name=picklistColumn]:checked').closest('tr')
				.remove();
		$('input[name=picklistColumn]').attr("disabled", false);
	}

	function enableRemoveButton() {
		$("#remove").attr('src', '<c:url value="/img/icons/Remove.png" />');

		$("#remove").unbind('click').click(function() {
			processRemove();
		});		
	}

	function disableRemoveButton() {
		$("#remove").attr('src', '<c:url value="/img/icons/RemoveGrey.png" />');

		$("#remove").unbind('click');
	}

	function enableEditButton() {
		$("#edit").attr('src', '<c:url value="/img/icons/Edit.png" />');

		$("#edit").unbind('click').click(function() {
			editPicklistColumn();
		});		
	}

	function disableEditButton() {
		$("#edit").attr('src', '<c:url value="/img/icons/EditGrey.png" />');

		$("#edit").unbind('click');
	}

	function enableAddButton() {
		$("#add").attr('src', '<c:url value="/img/icons/Add.png" />');		
	}

	function disableAddButton() {
		$("#add").attr('src', '<c:url value="/img/icons/AddGrey.png" />');
		$('img[name=addSublist]').attr('src',
				'<c:url value="/img/icons/AddGrey-small.png" />');
	}

	function enableSaveButton() {
		$("#save").attr('src', '<c:url value="/img/icons/Save.png" />');

		$("#save").unbind('click').click(function() {
			savePicklistColumn();
		});
	}

	function disableSaveButton() {
		$("#save").attr('src', '<c:url value="/img/icons/SaveGrey.png" />');

		$('#save').unbind('click');
	}

	function enableCancelButton() {
		$("#canel").attr('src', '<c:url value="/img/icons/Cancel.png" />');

		$("#canel").unbind('click').click(function() {
			cancelPicklistColumn();
		});		
	}

	function disableCancelButton() {
		$("#canel").attr('src', '<c:url value="/img/icons/CancelGrey.png" />');

		$("#canel").unbind('click');
	}

	function enableResetButton() {
		$("#reset").attr('src', '<c:url value="/img/icons/Reset.png" />');

		$("#reset").unbind('click').click(function() {
			resetPicklistColumn();
		});		
	}

	function disableResetButton() {
		$("#reset").attr('src', '<c:url value="/img/icons/ResetGrey.png" />');

		$("#reset").unbind('click');
	}

	function changeColumnType(selectedColumnType) {
		$('#curPickListRevisedColumnName').val(selectedColumnType.value);
		validateColumnType();
		validateRevisedColumnName();
	}

	function validateColumnOrder() {
		$('#curPickListColumnOrder').css('border-color',
				!isColumnOrderValid() ? 'red' : '');
	}

	function isColumnOrderValid() {
		var curColumnOrder = $('#curPickListColumnOrder').val();

		if (curColumnOrder == '' || $.trim(curColumnOrder) == '') {
			curColumnOrder = getDefaulitColumnOrder();
			$('#curPickListColumnOrder').val(curColumnOrder);
		}

		return !(curColumnOrder == '' || $.trim(curColumnOrder) == '' || isNaN(curColumnOrder));
	}

	function isColumnTypeValid() {
		var curColumnType = $('#curPickListColumnType').val();

		return !(curColumnType == '' || $.trim(curColumnType) == '');
	}

	function validateColumnType() {
		$('#curPickListColumnType').css('border-color',
				!isColumnTypeValid() ? 'red' : '');
	}

	function isRevisedColumnNameValid() {
		var revisedColumnName = $('#curPickListRevisedColumnName').val();
		var isDuplicateColumnName = isDuplicateRevisedColumnName();		

		return !(revisedColumnName == '' || $.trim(revisedColumnName) == '' || isDuplicateColumnName);
	}

	function validateRevisedColumnName() {
		var isDuplicateColumnName = isDuplicateRevisedColumnName();

		if (isDuplicateColumnName) {
			showErrorMessages('Picklist Columns cannot have duplicate names.');
		} else {
			hideMessage();
		}

		$('#curPickListRevisedColumnName').css(
				'border-color',
				!isRevisedColumnNameValid() || isDuplicateColumnName ? 'red'
						: '');
	}

	function handleColumnSelected() {
		var selectedRadioId = $(
				'input[type=radio][name=picklistColumn]:checked').attr('id');

		if (selectedRadioId != 'curPickListColumn') {
			var ids = selectedRadioId.split('|');

			$('#curContainerElementId').val(ids[0]);
			$('#curContainerElementVersionId').val(ids[1]);
			$('#curColumnElementId').val(ids[2]);
			$('#curColumnElementVersionId').val(ids[3]);

			enableRemoveButton();
		}

		enableEditButton();
		disableAddButton();
	}

	function cancelPicklistColumn() {
		if (pickListMode != 'add' && pickListMode != 'edit') {
			return true;
		}

		pickListMode = '';

		disableCancelButton();
		disableResetButton();
		disableSaveButton();
		enableAddButton();

		$('input[name=picklistColumn]').attr("disabled", false);

		var selectedRadioId = $(
				'input[type=radio][name=picklistColumn]:checked').attr('id');

		if (selectedRadioId == 'curPickListColumn') {
			removePicklistColumn();

			disableRemoveButton();
			disableEditButton();

			return true;
		}

		enableRemoveButton();
		enableEditButton();

		var ids = selectedRadioId.split('|');
		isSublistColumn = ids[4] === 'true';

		var columnOrderHtml = pickListColumnOrder;
		if (isSublistColumn) {
			$('input[type=radio][name=picklistColumn]:checked').closest('tr')
					.find('td:eq(3)').html(columnOrderHtml);
			$('input[type=radio][name=picklistColumn]:checked').closest('tr')
					.find('td:eq(3)').removeAttr('id');
		} else {
			$('input[type=radio][name=picklistColumn]:checked').closest('tr')
					.find('td:eq(1)').html(columnOrderHtml);
			$('input[type=radio][name=picklistColumn]:checked').closest('tr')
					.find('td:eq(1)').removeAttr('id');
		}

		var revisedColumnNameHtml = pickListRevisedColumnName;
		$('input[type=radio][name=picklistColumn]:checked').closest('tr').find(
				'td:eq(5)').html(revisedColumnNameHtml);
		$('input[type=radio][name=picklistColumn]:checked').closest('tr').find(
				'td:eq(5)').removeAttr('id');

		var sublistElement = $('input[type=radio][name=picklistColumn]:checked').closest('tr').find('td:eq(4) img');

		if (sublistElement.length > 0) {
			sublistElement.attr('src', '<c:url value="/img/icons/AddGrey-small.png" />');

			sublistElement.unbind('click');
		} 

		return true;
	}

	function resetPicklistColumn() {
		if (pickListMode != 'add' && pickListMode != 'edit') {
			return true;
		}

		var selectedRadioId = $(
				'input[type=radio][name=picklistColumn]:checked').attr('id');

		if (selectedRadioId == 'curPickListColumn') {
			return true;
		}

		var ids = selectedRadioId.split('|');
		isSublistColumn = ids[4] === 'true';

		$('#curContainerSublist').val(isSublistColumn);

		var columnOrderHtml = "<input type='text' id='curPickListColumnOrder' name='picklistColumnOrder' maxlength='5' style='width: 50px;' onchange='validateColumnOrder();' value='"
				+ pickListColumnOrder + "' />";
		if (isSublistColumn) {
			$('input[type=radio][name=picklistColumn]:checked').closest('tr')
					.find('td:eq(3)').html(columnOrderHtml);
			$('input[type=radio][name=picklistColumn]:checked').closest('tr')
					.find('td:eq(3)').attr('id', 'curColumnOrder');
		} else {
			$('input[type=radio][name=picklistColumn]:checked').closest('tr')
					.find('td:eq(1)').html(columnOrderHtml);
			$('input[type=radio][name=picklistColumn]:checked').closest('tr')
					.find('td:eq(1)').attr('id', 'curColumnOrder');
		}

		var revisedColumnNameHtml = "<input type='text' id='curPickListRevisedColumnName' name='pickListRevisedColumnName' style='width: 230px;' onchange='validateRevisedColumnName();' value='"
				+ pickListRevisedColumnName
				+ "' />"
				+ "<input type='hidden' id='curPickListColumnType' value='" + ids[5] + "' />";
		$('input[type=radio][name=picklistColumn]:checked').closest('tr').find(
				'td:eq(5)').html(revisedColumnNameHtml);
		$('input[type=radio][name=picklistColumn]:checked').closest('tr').find(
				'td:eq(5)').attr('id', 'curRevisedColumnName');

		$('#curPickListRevisedColumnName').val(pickListRevisedColumnName);
        
		return true;
	}

	function editPicklistColumn() {
		hideMessage();

		if (pickListMode == 'add' || pickListMode == 'edit') {
			return true;
		}

		var selectedRadioId = $(
				'input[type=radio][name=picklistColumn]:checked').attr('id');

		if (typeof selectedRadioId === 'undefined'
				|| selectedRadioId == 'curPickListColumn') {
			return true;
		}

		pickListMode = 'edit';
		enableSaveButton();
		enableCancelButton();
		enableResetButton();
		disableEditButton();

		$('input[name="actionType"]').val('save');

		pickListColumnOrder = '';
		pickListRevisedColumnName = '';

		var ids = selectedRadioId.split('|');
		isSublistColumn = ids[4] === 'true';
		var isDeleteable = ids[6] === 'true';

		pickListColumnOrder = (isSublistColumn) ? $(
				'input[type=radio][name=picklistColumn]:checked').closest('tr')
				.find('td:eq(3)').text() : $(
				'input[type=radio][name=picklistColumn]:checked').closest('tr')
				.find('td:eq(1)').text();
		pickListColumnOrder = $.trim(pickListColumnOrder);
		pickListRevisedColumnName = $(
				'input[type=radio][name=picklistColumn]:checked').closest('tr')
				.find('td:eq(5)').text();

		$('#curContainerSublist').val(isSublistColumn);

		var columnOrderHtml = "<input type='text' id='curPickListColumnOrder' name='picklistColumnOrder' maxlength='5' style='width: 50px;' onchange='validateColumnOrder();' value='"
				+ pickListColumnOrder
				+ "' "
				+ (isDeleteable ? '' : 'disabled')
				+ " />";

		if (isSublistColumn) {
			$('input[type=radio][name=picklistColumn]:checked').closest('tr')
					.find('td:eq(3)').html(columnOrderHtml);
			$('input[type=radio][name=picklistColumn]:checked').closest('tr')
					.find('td:eq(3)').attr('id', 'curColumnOrder');
		} else {
			$('input[type=radio][name=picklistColumn]:checked').closest('tr')
					.find('td:eq(1)').html(columnOrderHtml);
			$('input[type=radio][name=picklistColumn]:checked').closest('tr')
					.find('td:eq(1)').attr('id', 'curColumnOrder');
		}

		var sublistElement = $('input[type=radio][name=picklistColumn]:checked').closest('tr').find('td:eq(4) img');

		if (sublistElement.length > 0) {
			sublistElement.attr('src', '<c:url value="/img/icons/Add-small.png" />');

			sublistElement.click(function() {
				addPicklistSublistColumn(ids[2], ids[3], ids[2], ids[3], this);
			});
		} 

		var revisedColumnNameHtml = "<input type='text' id='curPickListRevisedColumnName' name='pickListRevisedColumnName' style='width: 230px;' maxlength='35' onchange='validateRevisedColumnName();' value='"
				+ pickListRevisedColumnName
				+ "' />"
				+ "<input type='hidden' id='curPickListColumnType' value='" + ids[5] + "' />";
		$('input[type=radio][name=picklistColumn]:checked').closest('tr').find(
				'td:eq(5)').html(revisedColumnNameHtml);
		$('input[type=radio][name=picklistColumn]:checked').closest('tr').find(
				'td:eq(5)').attr('id', 'curRevisedColumnName');

		$('input[name=picklistColumn]').attr("disabled", true);
        $('#curPickListRevisedColumnName').val(pickListRevisedColumnName);
	}

	function sortPickListColumn() {
		var $table = $('#picklistTable');

		var rows = $table.find('tbody>tr').get();

		rows.sort(function(a, b) {
			var keyA = $(a).attr('columnSortOrder');
			var keyB = $(b).attr('columnSortOrder');

			var keyAIds = keyA.split('|');
			var keyBIds = keyB.split('|');

			if (parseInt(keyAIds[0]) < parseInt(keyBIds[0])) {
				return -1;
			} else if (parseInt(keyAIds[0]) > parseInt(keyBIds[0])) {
				return 1;
			}

			return parseInt(keyAIds[1]) < parseInt(keyBIds[1]) ? -1
					: parseInt(keyAIds[1]) > parseInt(keyBIds[1]) ? 1 : 0;
		});

		$.each(rows, function(index, row) {
			$table.children('tbody').append(row);
		});
	}

	function isDuplicateRevisedColumnName() {
		var $table = $('#picklistTable');

		var rows = $table.find('tbody>tr').get();
		var curRevisedColumnName = $('#curPickListRevisedColumnName').val();
		var result = false;

		$
				.each(
						rows,
						function() {
							var trId = $(this).attr('id');

							if (trId != 'curPickListColumnRow') {
								var revisedColumnName = $(this)
										.find('td:eq(5)').text();

								if ($.trim(revisedColumnName).toUpperCase() == curRevisedColumnName
										.toUpperCase()) {
									result = true;
								}
							}
						});

		return result;
	}

	function processRemove() {
		var selectedRadioId = $(
				'input[type=radio][name=picklistColumn]:checked').attr('id');

		if (typeof selectedRadioId === 'undefined') {
			return true;
		}

		if (selectedRadioId == 'curPickListColumn') {
			return true;
		}

		hideMessage();
		hideProcessingScreen();

		$("#removalConfirmation").text('Do you want to Delete this Column?');

		$('#removalConfirmation').dialog({
			title : 'Confirmation: Removal of Picklist Column',
			width : 350,
			height : 150,
			modal : true,
			closeOnEscape : true,
			resizable : false,
			draggable : false,
			zIndex : 10000,
			buttons : [ {
				text : 'Remove',
				click : function() {
					$(this).dialog('close');
					removePicklistColumn();
				}
			}, {
				text : 'Cancel',
				click : function() {
					$(this).dialog('close');
				}
			} ]
		});
	}

	function isPicklistNameValid() {
		var curPicklistName = $('#picklistName').val();

		return !(curPicklistName == '' || $.trim(curPicklistName) == '');
	}

	function isPicklistNameChanged() {
		var curPicklistName = $('#picklistName').val();
		var prevName = $('input[name="prevName"]').val();

		return $.trim(curPicklistName) != $.trim(prevName);
	}

	function validatePicklistName() {
		var picklistNameValid = isPicklistNameValid();

		$('#picklistName').css('border-color', !picklistNameValid ? 'red' : '');

		var curPicklistName = $('#picklistName').val();
		var prevName = $('input[name="prevName"]').val();

		if (picklistNameValid && $.trim(curPicklistName) != $.trim(prevName)) {
			$("#resetPicklistName").attr('src',
					'<c:url value="/img/icons/Reset.png" />');
			$("#savePicklistName").attr('src',
					'<c:url value="/img/icons/Save.png" />');
		} else {
			$("#resetPicklistName").attr('src',
					'<c:url value="/img/icons/ResetGrey.png" />');
			$("#savePicklistName").attr('src',
					'<c:url value="/img/icons/SaveGrey.png" />');
		}
	}

	function resetName() {
		$('#picklistName').val($('input[name="prevName"]').val());

		$("#resetPicklistName").attr('src',
				'<c:url value="/img/icons/ResetGrey.png" />');
		$("#savePicklistName").attr('src',
				'<c:url value="/img/icons/SaveGrey.png" />');
	}

	function saveName() {
		hideMessage();

		if (!isPicklistNameValid() || !isPicklistNameChanged()) {
			return true;
		}

		$('input[name="name"]').val($('#picklistName').val());

		$
				.ajax({
					url : "<c:url value='/refset/picklist/savePicklistName.htm' />",
					data : $("#picklistEditForm").serialize(),
					type : "POST",
					async : false,
					cache : false,
					success : function(data) {
						if (data.status == 'SUCCESS') {
							$('input[name="prevName"]').val(
									$('#picklistName').val());

							$("#resetPicklistName")
									.attr('src',
											'<c:url value="/img/icons/ResetGrey.png" />');
							$("#savePicklistName")
									.attr('src',
											'<c:url value="/img/icons/SaveGrey.png" />');

							showInfoMessage($.trim(data.message));
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

	function getDefaulitColumnOrder() {
		var $table = $('#picklistTable');

		var rows = $table.find('tbody>tr').get();
		var defaultColumnOrder = 0;

		$
				.each(
						rows,
						function() {
							var trId = $(this).attr('id');

							if (trId != 'curPickListColumnRow') {
								var columnOrder = $(this).find('td:eq(1)')
										.text();

								if (columnOrder != ''
										&& $.trim(columnOrder) != '') {
									if (!isNaN($.trim(columnOrder))) {
										defaultColumnOrder = parseInt($
												.trim(columnOrder)) > defaultColumnOrder ? parseInt($
												.trim(columnOrder))
												: defaultColumnOrder;
									}
								}

								columnOrder = $(this).find('td:eq(3)').text();

								if (columnOrder != ''
										&& $.trim(columnOrder) != '') {
									if (!isNaN($.trim(columnOrder))) {
										defaultColumnOrder = parseInt($
												.trim(columnOrder)) > defaultColumnOrder ? parseInt($
												.trim(columnOrder))
												: defaultColumnOrder;
									}
								}
							}
						});

		return defaultColumnOrder + 1;
	}

	function toggle(element) {
		 var toggle = $(element);
		 var section = toggle.parents(".section");
		 
		 toggleSection(section, true);
	}

	function toggleSection(section, animate) {
		 if (!(section instanceof jQuery)) {
			 section = $(section);
		 }
		 
		 var toggle = section.find(".sectionHeader > a > img");
		 var sectionContent = section.find(".sectionContent").first();
		 
		 var isHidden = isSectionHidden(section);
		 
		 if (isHidden){
		     (animate ? sectionContent.slideDown() : sectionContent.show());
	         toggle.attr("src", "<c:url value='/img/icons/Expand.png'/>");
		 } else{
			 (animate ? sectionContent.slideUp() : sectionContent.hide());
			 toggle.attr("src", "<c:url value='/img/icons/Collapse.png'/>");
		 }
		 
		 $.cookie("section."+section.attr('name')+".collapsed",!isHidden);
	}

	function isSectionHidden(section) {
		if (!(section instanceof jQuery)) {
			 section = $(section);
		 }
		 
		return section.find(".sectionContent").first().is(":hidden");
	}
</script>

<tiles:insertAttribute name="refset-picklist-menu" />

<div class="content">
	<div class="icons">
		<ul style="padding-left: .9em;">
			<li style="float: left; list-style-type: none;">
				<div id="loadingInfo" class="info"
					style="display: none; margin-bottom: 0.1em; width: 900px; padding-top: 0.5em; padding-bottom: 0.5em;">Loading</div>
			</li>
		</ul>
	</div>

    <div class="section" style="padding: 2px;">
	    <div class="content" style="display: inline-block; width: 98%;">		
            <div class="sectionHeader">
                <a href="#"><img src="<c:url value='/img/icons/Expand.png' />" alt="Toggle" onclick="javascript:toggle(this);" style="vertical-align: middle;"/></a>
                <div style="display: inline-block; vertical-align: middle;">Basic Information</div>
            </div>
	    </div>

        <div class="sectionContent">
			<div class="span-24 inline" style="margin-top: 15px;">
				<span class="mandatory" style="margin-left: 20px;"> <fmt:message
						key="picklist.name" />:
				</span> <span><input type='text' id='picklistName'
					name='pickListName' maxlength="100" style='width: 200px;'					
					${refsetPermission != 'WRITE' ? 'disabled ' : ''} /></span> <span
					style="margin-left: 30px;"><img id="resetPicklistName"
					class="viewMode" title="Reset"
					src="<c:url value='/img/icons/ResetGrey.png' />"
					<c:if test="${refsetPermission == 'WRITE'}">onclick="resetName();"</c:if> /></span>
				<span><img id="savePicklistName" class="viewMode"
					title="Save" src="<c:url value='/img/icons/SaveGrey.png' />"
					<c:if test="${refsetPermission == 'WRITE'}">onclick="saveName();"</c:if> /></span>
				<span style="margin-left: 30px;"><fmt:message
						key="picklist.code" />:</span> <span>${picklist.code}</span> <span
					style="margin-left: 30px;"><fmt:message
						key="picklist.classificationstandard" />:</span> <span>${picklist.classificationStandard}</span>
			</div>
		</div>
	</div>

	<div class="section" style="padding: 2px;">
		<div class="content" style="display: inline-block; width: 98%; margin-top: 15px;">
			<div class="sectionHeader">
				<a href="#"><img src="<c:url value='/img/icons/Expand.png' />"
					alt="Toggle" onclick="javascript:toggle(this);"
					style="vertical-align: middle;" /></a> <span><fmt:message
						key="picklist.columns.from.pool.of.columns" /></span>
			</div>
		</div>

        <div id="removalConfirmation" style="display: none;">confirm</div>
        
		<div class="sectionContent">
			<div class="icons" style="margin-top: 15px; margin-right: 20px;">
				<ul style="padding-left: .9em;">
					<li id="iconsLI" style="float: right; top: 0px; border: 0px; background: #ffffff; list-style-type: none;">
						<img id="save" class="viewMode" title="Save" src="<c:url value='/img/icons/SaveGrey.png' />" /> 
						<c:if test="${refsetPermission == 'WRITE'}">
							<img id="add" class="viewMode" title="Add" src="<c:url value='/img/icons/Add.png' />"
								onclick="addPicklistColumn('${picklist.picklistElementId}', '${picklist.picklistElementVersionId}', '${picklist.picklistElementId}', '${picklist.picklistElementVersionId}');" />
						</c:if> 
						<c:if test="${refsetPermission != 'WRITE'}">
							<img id="add" class="viewMode" title="Add" src="<c:url value='/img/icons/AddGrey.png' />" />
						</c:if> 
						<img id="remove" class="viewMode" title="Delete" src="<c:url value='/img/icons/RemoveGrey.png' />" /> 
						<img id="edit" class="viewMode"	title="Edit" src="<c:url value='/img/icons/EditGrey.png' />" /> 
						<img id="canel"	class="viewMode" title="Cancel" src="<c:url value='/img/icons/CancelGrey.png' />" /> 
						<img id="reset"	class="viewMode" title="Reset"	src="<c:url value='/img/icons/ResetGrey.png' />" />
					</li>
				</ul>
			</div>

			<div class="content" style="margin-top: 30px; display: inline-block; width: 98%;">
				<fieldset>
					<legend>
						<fmt:message key="picklist.table" />
					</legend>

					<table class="listTable" style="width: 100%; margin-top: 0px;"
						id="picklistTable">
						<thead>
							<tr>
								<th class="tableHeader sizeThirty"></th>
								<th class="tableHeader sizeEighty"><fmt:message
										key="picklist.table.column.order" /></th>
								<th class="tableHeader sizeOneSeventy"><fmt:message
										key="picklist.table.column.type" /></th>
								<th class="tableHeader sizeEighty"><fmt:message
										key="picklist.table.sub.column.order" /></th>
								<th class="tableHeader sizeTwoFifty"><fmt:message
										key="picklist.table.sub.column.type" /></th>
								<th class="tableHeader sizeOneEighty"><fmt:message
										key="picklist.table.revised.column.name" /></th>
							</tr>
						</thead>
						<tbody id="picklistColumnTBody">
							<c:forEach items='${picklist.listColumn}' var='listColumnItem'
								varStatus="loopStatus">
								<tr id='row-${listColumnItem.id}'>
									<td style='border: 1px solid #96BEBD;'><input type='radio'
										name='picklistColumn' id='${listColumnItem.id}'
										onclick='handleColumnSelected();'
										<c:if test="${refsetPermission != 'WRITE'}">disabled</c:if> /></td>
									<td style='border: 1px solid #96BEBD;'><c:if
											test="${!listColumnItem.sublistColumn}">
                                    ${listColumnItem.columnOrder}
                                </c:if></td>
									<td style='border: 1px solid #96BEBD;'><c:if
											test="${!listColumnItem.sublistColumn}">
                                    ${listColumnItem.columnType}
                                </c:if></td>
									<td style='border: 1px solid #96BEBD;'><c:if
											test="${listColumnItem.sublistColumn}">
                                    ${listColumnItem.columnOrder}
                                </c:if></td>
									<td style='border: 1px solid #96BEBD;'><c:if
											test="${listColumnItem.sublistColumn}">
                                    ${listColumnItem.columnType}
                                </c:if> <c:if
											test="${listColumnItem.sublistAvailable}">
											<div style="text-align: right">
												<img name="addSublist" class="viewMode" title="Add"
													src="<c:url value='/img/icons/AddGrey-small.png' />" />
											</div>
										</c:if></td>
									<td style='border: 1px solid #96BEBD;'>${listColumnItem.columnName}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</fieldset>
			</div>
		</div>
	</div>

	<form:form method="POST" modelAttribute="picklistColumnViewBean"
		id="picklistColumnAddForm">
		<input type="hidden" name="actionType" />
		<input type="hidden" name="containerSublist" value='false'
			id="curContainerSublist" />
		<input type="hidden" name="contextId" value="${picklist.contextId}" />
		<input type="hidden" name="picklistElementId"
			value="${picklist.picklistElementId}" />
		<input type="hidden" name="picklistElementVersionId"
			value="${picklist.picklistElementVersionId}" />
		<input type="hidden" name="containerElementId"
			id="curContainerElementId" />
		<input type="hidden" name="containerElementVersionId"
			id="curContainerElementVersionId" />
		<input type="hidden" name="columnElementId" id="curColumnElementId" />
		<input type="hidden" name="columnElementVersionId"
			id="curColumnElementVersionId" />
		<input type="hidden" name="columnOrder" />
		<input type="hidden" name="columnType" />
		<input type="hidden" name="revisedColumnName" />
	</form:form>

	<form:form method="POST"
		modelAttribute="picklistAvailableColumnViewBean"
		id="picklistAvailableColumnForm">
		<input type="hidden" name="contextId" value="${picklist.contextId}" />
		<input type="hidden" name="picklistElementId"
			value="${picklist.picklistElementId}" />
		<input type="hidden" name="picklistElementVersionId"
			value="${picklist.picklistElementVersionId}" />
		<input type="hidden" name="containerElementId" id="containerElementId" />
		<input type="hidden" name="containerElementVersionId"
			id="containerElementVersionId" />
		<input type="hidden" name="columnElementId" id="columnElementId" />
		<input type="hidden" name="columnElementVersionId"
			id="columnElementVersionId" />
		<input type="hidden" name="containerSublist" value='false'
			id="containerSublist" />
	</form:form>

	<form:form method="POST" modelAttribute="viewBean"
		id="picklistEditForm">
		<input type="hidden" name="contextId" value="${picklist.contextId}" />
		<input type="hidden" name="picklistElementId"
			value="${picklist.picklistElementId}" />
		<input type="hidden" name="picklistElementVersionId"
			value="${picklist.picklistElementVersionId}" />
		<input type="hidden" name="actionType" value='save' />
		<input type="hidden" name="name" />
		<input type="hidden" name="prevName" value='${picklist.name}' />
	</form:form>
</div>

<script type="text/javascript">
    $('#picklistName').keyup(function() {
    	validatePicklistName();
    });

    $('#picklistName').val('${fn:replace(picklist.name, "'", "\\'")}');
</script>
