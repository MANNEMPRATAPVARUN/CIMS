<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<script type="text/javascript">

var supplementMode = '';

var uploadedFileName = '';

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
	//alert("DONE...");
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
	//alert(messageToShow);
	showMessage();
}

function showErrorMessagesFromResponse(response) {
	var errorMessages = "";
	for ( var i = 0; i < response.errorMessageList.length; i++) {
		var item = response.errorMessageList[i];
		errorMessages += "<img src=\"img/icons/Error.png\"/> " + item.defaultMessage;
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

function greyButtons() {
	 $("#edit").attr('src', '<c:url value="/img/icons/EditGrey.png"/>');
     $("#remove").attr('src', '<c:url value="/img/icons/RemoveGrey.png"/>');
}

function changeButtons() {
    $("#edit").attr('src', '<c:url value="/img/icons/Edit.png"/>');
	$("#remove").attr('src', '<c:url value="/img/icons/Remove.png"/>');
	//$("#canel").attr('src', '<c:url value="/img/icons/Cancel.png"/>');
	//$("#add").attr('src', '<c:url value="/img/icons/AddGrey.png" />');
}

function deleteSelectedRow() {
	$('input[type=radio][name=radioSelection]:checked').closest('tr')
			.remove();
	$('input[name=radioSelection]').attr("disabled", false);
}

function disableCancelButton() {
	$("#canel").attr('src', '<c:url value="/img/icons/CancelGrey.png" />');
}

function disableSaveButton() {
	$("#save").attr('src', '<c:url value="/img/icons/SaveGrey.png" />');

	$("#save").unbind('click');
}	

function enableAddButton() {
	$("#add").attr('src', '<c:url value="/img/icons/Add.png" />');	

	$("#add").unbind('click').click(function() {
	    appendToTable();
	});
}

function enableRemoveButton() {
	$("#remove").attr('src', '<c:url value="/img/icons/Remove.png" />');
}

function disableRemoveButton() {
	$("#remove").attr('src', '<c:url value="/img/icons/RemoveGrey.png" />');
}

function cancelSupplement() {

	var radioId = $('input[name=radioSelection]:checked').attr('id');
	if (typeof radioId === 'undefined') {
		console.log('No radio button selected');
		return false;
	}

	if (supplementMode != 'add' && supplementMode != 'edit') {
		return false;
	}

	disableCancelButton();
	disableSaveButton();
	enableAddButton();
	if (supplementMode == 'edit') {
		enableEditButton();
	}
	disableResetButton();
	enableRemoveButton();

	$('input[name=radioSelection]').attr("disabled", false);

	if (supplementMode == 'add' && radioId == 'curSupplementColumn') {
	
		deleteSelectedRow();
		supplementMode = '';
		return true;
	}

	var uploadedFileNameHtml = uploadedFileName;
	$('input[type=radio][name=radioSelection]:checked').closest('tr').find(
			'td:eq(3)').html(uploadedFileNameHtml);
	$('input[type=radio][name=radioSelection]:checked').closest('tr').find(
			'td:eq(3)').removeAttr('id');
	supplementMode = '';

	return true;

}

/**
 * Sets the odd even class to tr in the table
 */
function setTableColor()
{
	$("table[id='supplement'] tbody tr:nth-of-type(odd)").removeClass('even').addClass("odd");
	$("table[id='supplement'] tbody tr:nth-of-type(even)").removeClass('odd').addClass("even");
}



function saveSupplement() {

	//alert("saveSupplement");
	var formData = new FormData();
	
	formData.append('code', $('#code').val());
	formData.append('name', $('#name').val());
	formData.append('contextId', $('#contextId').val());
	formData.append('elementId', $('#elementId').val());
	formData.append('elementVersionId', $('#elementVersionId').val());

	var radioId = $('input[name=radioSelection]:checked').attr('id');
	var ids = radioId.split('|');
	
	
		if (ids[1]!=null){
		formData.append('supplementElementId', ids[1]);
		formData.append('supplementElementVersionId', ids[2]);
	}

	var fileInput = document.getElementById('file');
	var file = fileInput.files[0];
	formData.append('file', file);

	
	$.ajax({
        url: "<c:url value='/refset/supplement/add.htm?actionType=save'/>",
        /* data: $("#supplementAddForm").serialize(), */
        data: formData,
        processData: false, // Don't process the files
	    contentType: false, // Set content type to false as jQuery will tell the server its a query string request
        type: "POST",
        async: false,
        cache: false,
        success: function(data){
			if (data.status=='SUCCESS'){
				showInfoMessage(data.message);
				/*Mimmicking whats in pickList.jsp --AA*/
				
				
				$('#curSupplementRow').find('td:eq(1)').html($('#code').val());
				if (supplementMode == 'add')
					{
					var supplementNameHtml ='<a href="/cims_web_internal/refset/supplement/file.htm?contextId='+ $('#contextId').val()+'&elementId='+ data.supplementViewBean.elementId+'&elementVersionId='+data.supplementViewBean.supplementElementVersionId+'" >'+$('#name').val()+' </a>';
					$('#curSupplementRow').find('td:eq(2)').html(supplementNameHtml );
				}
				

				$('#curSupplementRow').find('td:eq(3)').html(document.getElementById('file').files[0].name);		
				

				disableSaveButton();
				disableCancelButton();
				enableAddButton();
				enableRemoveButton();
				disableResetButton();
				//disableEditButton();
				enableEditButton(); 
				supplementMode = '';
				$('input[name=radioSelection]').attr("disabled", false);
				//alert("TT");
				//var editPage = "<c:url value='/refset/supplement.htm?contextId="+data.contextId+"&elementId="+data.elementId+"&elementVersionId="+data.elementVersionId + "'/>";
				//window.location.href = editPage;
				setTableColor();
				var newId=$('#contextId').val()+"|"+data.supplementViewBean.elementId+"|"+data.supplementViewBean.supplementElementVersionId;
				$('#curSupplementColumn').attr("id",newId);
				$('#curSupplementRow').removeAttr('id');
							 				 			
			}else {
				var errorMessages = "";
				for (var i = 0; i < data.errors.length; i++) {
					var item = data.errors[i];
					errorMessages += item;
					errorMessages += "<br/>";
				}
				showErrorMessages(errorMessages);
			}
        },
        error: function(data){  	
        	var errorMessages = "System error occurred, please contact System administrator,";
        	showErrorMessages(errorMessages);
        }		       
    });
}

function appendToTable() {

	supplementMode = 'add';
	
    var insertLine = '<tr id="curSupplementRow">';
	/* insertLine += '<td class="no-print"></td>'; */
	insertLine += '<td><input type="radio" name="radioSelection" id="curSupplementColumn" checked="checked" class="no-print" /></td>';
	insertLine += '<td><input id="code" name="code" maxlength="10" /></td>';
	insertLine += '<td><input id="name" name="name" /></td>';
	insertLine += '<td><input type="text" id="filename" name="filename" disabled="disabled" /><input type="file" id="file" name="file" accept=".xlsx" style="cursor: pointer; display: none" onchange="displayFilename();" /><img name="browse" class="viewMode" title="Browse" src="' + '<c:url value="/img/icons/Browse-small.png" />" onclick="browseUploadFile();" />' + '</td>';
	insertLine += '</tr>';
    	
	$("table#supplement").find("tbody").append(insertLine);
    disableSaveButton();
    enableCancelButton();
	disableAddButton();
	disableRemoveButton();
	disableResetButton();
	disableEditButton();
	$('input[name=radioSelection]').attr("disabled", 'disabled');
	setTableColor();
}

function editSupplement() {

	console.log("editSupplement");
	if (supplementMode == 'add') {
		return false;
	}
	supplementMode = 'edit';
	var radioId = $('input[name=radioSelection]:checked').attr('id');
	if (typeof radioId === 'undefined') {
		console.log('No radio button selected');
		return false;
	}
	disableSaveButton();
	enableCancelButton();
	disableResetButton();
	disableEditButton();
	disableRemoveButton();
    disableAddButton();
	uploadedFileName = '';
	uploadedFileName = $('input[type=radio][name=radioSelection]:checked').closest('tr').find('td:eq(3)').text();
	var uploadedFileHtml = '<td><input type="text" id="filename" name="filename" disabled="disabled" /><input type="file" id="file" name="file" accept=".xlsx" style="cursor: pointer; display: none" onchange="displayFilename();" /><img name="browse" class="viewMode" title="Browse" src="' + '<c:url value="/img/icons/Browse-small.png" />" onclick="browseUploadFile();" />' + '</td>';
	$('input[type=radio][name=radioSelection]:checked').closest('tr').attr("id", "curSupplementRow");
	$('input[type=radio][name=radioSelection]:checked').closest('tr').find('td:eq(3)').html(uploadedFileHtml);
	$('input[name=radioSelection]').attr("disabled", true);
	$('#filename').val($.trim(uploadedFileName));
}


function removeSupplement() {
	var radioId = $('input[name=radioSelection]:checked').attr('id');
	if (typeof radioId === 'undefined') 
		{
		console.log('No radio button selected');
		return false;
	}
	//alert(radioId);	
	var ids = radioId.split('|');
	var elementId = document.getElementById("elementId").value;
	var elementVersionId = document.getElementById("elementVersionId").value;
	
	var postPage = "<c:url value='/refset/supplement/add.htm?actionType=drop&contextId='/>"+ids[0]+'&supplementElementId='+ids[1]+'&supplementElementVersionId='+ids[2]+'&ElementId='+elementId+'&ElementVersionId='+elementVersionId;
	
	
	$.ajax({
		url : postPage,
		type : 'POST',
		success : function(data) {

			if (data.status == 'FAILED') {
				hideProcessingScreen();
				hideMessage();
				var errorMessages = "";
				for (var i = 0; i < data.errors.length; i++) {
					var item = data.errors[i];
					errorMessages += item;
					errorMessages += "<br/>";
				}
				showErrorMessages(errorMessages);
							
			} else {
				hideProcessingScreen();
				 $('input[name=radioSelection]:checked').closest('tr').remove();
				 setTableColor(); 
				 showInfoMessage(data.message);

			}
			 setTableColor(); 
		},
        error: function(data){  	
        	var errorMessages = "System error occurred, please contact System administrator,";
        	
        	showErrorMessages(errorMessages);
        },
		beforeSend : function() {
			showInfoMessage('Removing');
			showProcessingScreen();
		}
	});	
}

function resetSupplement() {
	
	var radioId = $('input[name=radioSelection]:checked').attr('id');
	if (typeof radioId === 'undefined') {
		console.log('No radio button selected');
		return false;
	}

	if (supplementMode != 'edit') {
		return true;
	}

	disableSaveButton();
	enableCancelButton();
	disableEditButton();
	disableResetButton();

	var uploadedFileHtml = '<td><input type="text" id="filename" name="filename" disabled="disabled" /><input type="file" id="file" name="file" accept=".xlsx" style="cursor: pointer; display: none" onchange="displayFilename();" /><img name="browse" class="viewMode" title="Browse" src="' + '<c:url value="/img/icons/Browse-small.png" />" onclick="browseUploadFile();" />' + '</td>';

	$('input[type=radio][name=radioSelection]:checked').closest('tr')
	.find('td:eq(3)').html(uploadedFileHtml);

	$('#filename').val($.trim(uploadedFileName));	
}

function disableEditButton() {
	$("#edit").attr('src', '<c:url value="/img/icons/EditGrey.png" />');
}

function enableEditButton() {
	$("#edit").attr('src', '<c:url value="/img/icons/Edit.png" />');
}

function enableResetButton() {
	$("#reset").attr('src', '<c:url value="/img/icons/Reset.png" />');

	$("#reset").unbind('click').click(function() {
	    resetSupplement();
	});
}

function disableResetButton() {
	$("#reset").attr('src', '<c:url value="/img/icons/ResetGrey.png" />');

	$('#reset').unbind("click");
}

function back() {	
	var supplementPage = '<c:url value="/refset/supplement.htm"><c:param name="contextId" value="${viewBean.contextId}" /><c:param name="elementId" value="${viewBean.elementId}" /><c:param name="elementVersionId" value="${viewBean.elementVersionId}" /> </c:url>';
	window.location.href = supplementPage;			 
}

function enableCancelButton() {
	$("#canel").attr('src', '<c:url value="/img/icons/Cancel.png" />');
}

function disableAddButton() {
	$("#add").attr('src', '<c:url value="/img/icons/AddGrey.png" />');	

	$("#add").unbind('click');	
}

function disableRemoveButton() {		
	$("#remove").attr('src', '<c:url value="/img/icons/RemoveGrey.png"/>');
}



function enableSaveButton() {
	$("#save").attr('src', '<c:url value="/img/icons/Save.png" />');

	$("#save").unbind('click').click(function() {
	    saveSupplement();
    });    
}

function ensureRadioButtonSelected() {
	var radioId = $('input[name=radioSelection]:checked').attr('id');
	if (typeof radioId === 'undefined') {
		console.log('No radio button selected');
		return true;
	}
}



function browseUploadFile() {
	$("#file").trigger('click');
}

function displayFilename() {
	var filename = $("#file").val().split('\\').pop();

    $('#filename').val(filename);
    if (supplementMode == 'edit')enableResetButton();
    conditionalEnableSaveAndReset();
}

function conditionalEnableSaveAndReset()
{
	if (supplementMode == 'edit')enableResetButton();
    enableSaveButton();
	}

</script>

<div id="removalConfirmation" style="display: none;">
	confirm
</div>
<div class="icons">
	<ul style="padding-left:.9em; ">
		<li style="float: left; list-style-type: none; ">
			<div id="loadingInfo" class="info" style="display: none; margin-bottom: 0.1em; width: 900px; padding-top: 0.5em;padding-bottom: 0.5em;">Loading</div>
		</li>	
	
		<li id="iconsLI" style="float: right; top: 0px; border: 0px; background: #ffffff; list-style-type: none; display: none;">	
		        <img id="save" class="viewMode" title="Save" src="<c:url value='/img/icons/SaveGrey.png'/>" />
				<img id="add" class="viewMode" title="Add" src="<c:url value='/img/icons/AddGrey.png'/>" />				
				<img id="remove" class="viewMode" title="Remove" src="<c:url value='/img/icons/RemoveGrey.png'/>" />	
				<img id="edit" class="viewMode" title="Edit" src="<c:url value='/img/icons/EditGrey.png'/>" onclick="editSupplement();" />			
				<img id="canel" class="viewMode" title="Cancel" src="<c:url value='/img/icons/CancelGrey.png' />" onclick="cancelSupplement();" />
				<img id="reset" class="viewMode" title="Reset" src="<c:url value='/img/icons/ResetGrey.png' />" />			                			
		</li>
	</ul>
</div> 
<div class="content">
  <form:form method="POST" modelAttribute="viewBean" id="supplementAddForm" action="add.htm" enctype="multipart/form-data" name="supplementAddForm">
    	
    	<input type="hidden" id="contextId" name="contextId" value="${viewBean.contextId }"/>
		<input type="hidden" id="elementId" name="elementId" value="${viewBean.elementId }"/>
		<input type="hidden" id="elementVersionId" name="elementVersionId" value="${viewBean.elementVersionId }"/>
		
	<display:table name="subList" id="supplement" defaultsort="2" requestURI="" pagesize="${pageSize}" 
		size="resultSize" class="listTable" style="width: 100%; margin-top: 0px; table-layout:fixed;" sort="list">
		
		<display:setProperty name="paging.banner.placement" value="bottom" />
		<display:setProperty name="paging.banner.some_items_found" value="" />
		<display:setProperty name="basic.empty.showtable" value="true" />
		<display:setProperty name="basic.msg.empty_list_row" value="" />
				
		<display:column headerClass="tableHeader sizeThirty no-print" class="no-print"  > 
			<input name="radioSelection" 
				    id="${viewBean.contextId}|${supplement.elementIdentifier.elementId}|${supplement.elementIdentifier.elementVersionId}" 
				    type="radio" 
				    ${refsetPermission != 'WRITE' ? 'disabled ' : ''} />
		</display:column>
		
		<display:column sortable="true" titleKey="supplement.code" headerClass="tableHeader sizeEighty">
			${supplement.code}
		</display:column>
		
		<display:column sortable="true" titleKey="supplement.name" headerClass="tableHeader sizeEighty">
			 <a href='<c:url value="/refset/supplement/file.htm"><c:param name="contextId" value="${viewBean.contextId}" /><c:param name="elementId" value="${supplement.elementIdentifier.elementId}" /><c:param name="elementVersionId" value="${supplement.elementIdentifier.elementVersionId}" /></c:url> '>
            ${supplement.name}
          </a>
		</display:column>
		
		<display:column sortable="true" titleKey="supplement.file" headerClass="tableHeader sizeEighty">
			${supplement.filename}
		</display:column>		
	</display:table>
  </form:form>
</div>
<script type="text/javascript">
    $(document).ready(function() {	
        <c:if test="${refsetPermission == 'WRITE'}">
		    enableAddButton();
        </c:if>
    });
</script>

<script type="text/javascript">
$("#edit").click(function() {
	if ((ensureRadioButtonSelected())) {
		hideMessage();
		hideProcessingScreen();
		return false;
	}
});

$("#remove").click(function() {
	if ((ensureRadioButtonSelected())) {
		hideMessage();
		hideProcessingScreen();
		return false;
	}
	if (supplementMode == 'add' || supplementMode == 'edit') {
		return false;
	}
	$("#removalConfirmation").text('Please confirm that you want to permanently remove the selected supplement from the system.');
	$('#removalConfirmation').dialog({
		title : 'Confirmation: Removal of Supplement',
		width : 350, height : 150, modal : true, resizable : false, draggable : false,
		buttons : [ {
			text : 'Remove',
			click : function() {
				$(this).dialog('close');
				removeSupplement();
			}
		}, {
			text : 'Cancel',
			click : function() {
				$(this).dialog('close');
			}
		} ]
	});
});

$("input[type='radio']").click(function(){
	 changeButtons();
});



</script>