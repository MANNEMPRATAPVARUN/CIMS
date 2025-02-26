<!DOCTYPE html>

<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<html style="height: 100%;">
<%@ include file="/WEB-INF/jsp/common/common-header.jsp"%>

<style type="text/css" media="all">
input[type="text"] {
     width: 100%; 
     box-sizing: border-box;
     -webkit-box-sizing:border-box;
     -moz-box-sizing: border-box;
}

.ui-dialog {
    z-index:1199 !important; /* The default is 100. !important overrides the default. */
}
</style>

<script src="<c:url value="/js/displayTagService.js"/>"></script>
<script type="text/javascript">

	var glb_cod;
	var glb_de;
	var glb_df;
	var editMode = false;
	var superEditMode = false;
	var addHasBeenClicked = false;
	var hasConceptBeenPublished = false;
	
	var processedMessage = {};
	processedMessage["class"] = '';
	processedMessage["message"] = '';

	$(document).ready(function() {
		window.opener.hideProcessingScreen();  
		changeEditMode(false);			
		hideMessage();
		hideProcessingScreen();		
		greyButtons();
		modifyPageLinks();	
		setSuperEditMode();
		setSuccessMessage();
	});

	/*********************************************************************************************************
	* NAME:          Set Success Message
	* DESCRIPTION:   Checks against local storage to display a message.  Used to display a message after 
	*                a page reload
	**********************************************************************************************************/
	function setSuccessMessage() {
		var successMessage = localStorage.getItem('successMessage');
		if (successMessage !== null) {		
			showSuccessMessage(successMessage);	
		}
		
		localStorage.removeItem('successMessage');
	}
	
	function setSuperEditMode() {	
		superEditMode = window.opener.parent.superEditMode;
		hasConceptBeenPublished = $('#hasConceptBeenPublished').text();
		
		if (superEditMode === true) {
			if (hasConceptBeenPublished == 'true') {
				//Disable ability to add, but retain edit privledges
				$("#add").attr("src", "<c:url value="/img/icons/AddGrey.png"/>");
				$("#add").attr('onclick','').unbind('click');
				$("#remove").attr("src", "<c:url value="/img/icons/RemoveGrey.png"/>");
				$("#remove").attr('onclick','').unbind('click');
			}
		}
		
		if (superEditMode === false) {
			$("#edit").attr("src", "<c:url value="/img/icons/EditGrey.png"/>");
			$("#remove").attr("src", "<c:url value="/img/icons/RemoveGrey.png"/>");
			$("#add").attr("src", "<c:url value="/img/icons/AddGrey.png"/>");
			$("#edit").attr('onclick','').unbind('click');
			$("#remove").attr('onclick','').unbind('click');		
			$("#add").attr('onclick','').unbind('click');
		}
	}

	function greyButtons() {
		$("#edit").attr('src', '<c:url value="/img/icons/EditGrey.png"/>');
		$("#remove").attr('src', '<c:url value="/img/icons/RemoveGrey.png"/>');	
	}
	
	function colorButtons() {
	    if (superEditMode === true) {
	    	$("#edit").attr('src', '<c:url value="/img/icons/Edit.png"/>');
			
			if (hasConceptBeenPublished != 'true') {
				$("#remove").attr('src', '<c:url value="/img/icons/Remove.png"/>');
			}						
	    } 
	}
	
	/*********************************************************************************************************
	 * NAME:          Edit Attribute
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function editAttribute() {

		if (superEditMode === false) {
			console.log('Attribute not ready yet');
			hideMessage();
			return false;
		}
		
		var radioId = $('input[name=radioSelection]:checked').attr('id');
		if (typeof radioId === 'undefined') {
			console.log('No radio button selected');
			return false;
		}

		var tableRow = $('input[name=radioSelection]:checked').parent().parent();
		var code = tableRow.children("td:nth-child(2)");
		var descriptionEng = tableRow.children("td:nth-child(3)");
		var descriptionFra = tableRow.children("td:nth-child(4)");		
		
		glb_cod = $.trim(code.text());		
		glb_de = $.trim(descriptionEng.text());
		glb_df = $.trim(descriptionFra.text());
		
		descriptionEng.html("<input type='text' id='tmpDE' maxlength='255'/>");
		descriptionFra.html("<input type='text' id='tmpDF' maxlength='255'/>");
		
		$('#tmpDE').val(glb_de);
		$('#tmpDF').val(glb_df);
		
		changeEditMode(true);
	}	
	
	/*********************************************************************************************************
	 * NAME:          Add new table row
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function addAttribute() {

		if (superEditMode === false) {
			console.log('Attribute not ready yet');
			hideMessage();
			return false;
		}

		if (addHasBeenClicked == true) {
			console.log("Add has already been clicked.  No More!");
			return false;
		}

		addHasBeenClicked = true;

		var tbl = $('#attributeTable');
		var tableRow = $('#attributeTable tr:last');
		var lastRowClass = tableRow.attr('class');
		var newRowClass = (lastRowClass == 'odd') ? 'even' : 'odd';

		glb_de = "";
		glb_df = "";

		var descriptionEng = "<input type='text' id='tmpDE' maxlength='255' value=''/>";
		var descriptionFra = "<input type='text' id='tmpDF' maxlength='255' value=''/>";
		
		var newRow = "<tr class=\"" + newRowClass + "\">";

		newRow += "<td></td>"; // Radio button
		newRow += "<td></td>"; // Code
		newRow += "<td>" + descriptionEng + "</td>"; // Eng Desc
		newRow += "<td>" + descriptionFra + "</td>"; // Fra Desc
		
		newRow += "<td></td>"; // Notes
		newRow += "</tr>";

		tbl.append(newRow);
		
		tableRow = $('#attributeTable tr:last');		
		rowTwo = tableRow.children("td:nth-child(2)");

		$("#genAttrCodes").clone().attr('id', 'newGenAttrCodes').attr('style', '').appendTo(rowTwo);
		
		
		changeEditMode(true);
	}
	
	/*********************************************************************************************************
	 * NAME:          Change Edit Mode
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function changeEditMode(isEdit) {

		editMode = isEdit;
		$("#editMode").html(isEdit.toString());

		$('input[name=radioSelection]').attr('disabled', isEdit);

		if (isEdit) {
			$(".editMode").show();
			$(".viewMode").hide();
			$("#viewButton").attr('disabled', true);
			$("#viewButton").addClass('ui-state-disabled');
			$("#versionCode").attr('disabled', true);			
			$("#baseClassification").attr('disabled', true);
			$("#attributeType").attr('disabled', true);
			$("#attributeViewType").attr('disabled', true);
			$("#status").attr('disabled', true);
			$(".tableHeader a").bind('click', function(e){ e.preventDefault();});
		} else {
			$(".editMode").hide();
			$(".viewMode").show();

			$("#viewButton").removeAttr('disabled');
			$("#viewButton").removeClass('ui-state-disabled');
			$("#versionCode").removeAttr('disabled');			
			$("#baseClassification").removeAttr('disabled');
			$("#attributeType").removeAttr('disabled');
			$("#attributeViewType").removeAttr('disabled');
			$("#status").removeAttr('disabled');
			$(".tableHeader a").unbind('click');
		}

		hideMessage();
	}
	
	/*********************************************************************************************************
	 * NAME:          Cancel Edit     
	 * DESCRIPTION:   Revert the row back to non edit mode, and replace back to before
	 *********************************************************************************************************/
	function cancelEdit() {

		if (addHasBeenClicked == true) {
			cancelAdd();
			return false;
		}
		
		var tableRow = $('input[name=radioSelection]:checked').parent().parent();

		var descriptionEng = tableRow.children("td:nth-child(3)");
		var descriptionFra = tableRow.children("td:nth-child(4)");
		
		descriptionEng.html(glb_de);
		descriptionFra.html(glb_df);

		changeEditMode(false);
	}

	/*********************************************************************************************************
	 * NAME:          Cancel Add 
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function cancelAdd() {

		$('#attributeTable tr:last').remove();
		addHasBeenClicked = false;
		changeEditMode(false);
	}

	/*********************************************************************************************************
	 * NAME:          Reset Changes    
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function resetChanges() {

		$('#tmpDE').val(glb_de);
		$('#tmpDF').val(glb_df);
	}
	
	/*********************************************************************************************************
	 * NAME:          Save New Attribute				          
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function saveNewAttribute() {

		var data = {};
		
		data['genericAttributeCode'] = $('#newGenAttrCodes').val();		
 		data['descriptionEng'] = $('#tmpDE').val();
 		data['descriptionFra'] = $('#tmpDF').val();
		
		
		var postPage = "<c:url value='inContext.htm'/>";

		$.ajax({
			'url' : postPage,
			'type' : 'POST',
			'data' : data,
			'success' : function(response) {
				
				if (response.status == 'FAIL') {
					hideProcessingScreen();
					hideMessage();
					showErrorMessagesFromResponse(response);		
				} else {
					localStorage.setItem('successMessage', "Changes have been successfully saved");
					location.reload();
				}
			},
			'error' : function(response) {
				hideProcessingScreen();
				hideMessage();			
			},				
			beforeSend : function() {
				showInfoMessage('Saving');						
				showProcessingScreen();
			}
		});		
	}
	
	function ensureRadioButtonSelected() {
		var radioId = $('input[name=radioSelection]:checked').attr('id');
		if (typeof radioId === 'undefined') {
			return true;
		}
	}
	
	/*********************************************************************************************************
	 * NAME:          Remove Attribute	
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function removeAttribute() {
		
		if (superEditMode === false) {
			console.log('Nothing to remove');
			hideMessage();
			return false;
		}

		var radioId = $('input[name=radioSelection]:checked').attr('id');
		if (typeof radioId === 'undefined') {
			console.log('No radio button selected');
			return false;
		}
		
		var tableRow = $('input[name=radioSelection]:checked').parent().parent();
		var code = tableRow.children("td:nth-child(2)");
		code = $.trim(code.text());	
		attributeCodeToDisplay = code;		
		
		$.ajax({
			'url' : "<c:url value='inContext/" + radioId + ".htm'/>",
			'type' : 'DELETE',
			'success' : function(response) {
	
				if (response.status == 'FAIL') {
					hideProcessingScreen();
					hideMessage();
					showErrorMessagesFromResponse(response);
				} else {
					localStorage.setItem('successMessage', "Attribute has been successfully removed");
					location.reload();
				}
			},
			beforeSend : function() {
				showInfoMessage('Removing');
				showProcessingScreen();
			}
		});		
		
	}	
	
	function notePopup(elementId,versionCode,disableEditing) {
		var editMode = window.opener.editMode;
		var postPage = "<c:url value='inContext/" + elementId + "/notes.htm?versionCode="+versionCode+"&disableEditing="+disableEditing+"'/>";

		if (editMode == true) {
			console.log('Cannot perform while in edit mode');
			return false;
		}
		
		var iframe = $("#notesIframe");
						
		iframe.attr("src", postPage);
	
		$("#notesDialog").dialog("open");		
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
		processedMessage["image"] = "<img src=\"../../img/icons/Ok.png\"/>";
		
		showMessage();
	}

	function showErrorMessagesFromResponse(response) {
		var errorMessages = "";
		for ( var i = 0; i < response.errorMessageList.length; i++) {
			var item = response.errorMessageList[i];
			errorMessages += item.defaultMessage;
			errorMessages += "<br/>";
		}

		processedMessage["message"] = errorMessages;
		processedMessage["class"] = "error";		
		processedMessage["image"] = "<img src=\"../../img/icons/Error.png\"/>";
		
		showMessage();		
	}	
</script>


<div class="content">

	<spring:url value="referenceAttributes.htm" var="formUrl" />
	
	<div id="block_container">
		<div style="display:inline-block; width:30%;">
			<label>Reference Value Code:</label>&nbsp;&nbsp;
			<font color=red>${refAttrModel.code}</font>
		</div>				
		<div style="display:inline-block; width:60%;">
			<label>English Description:</label>&nbsp;&nbsp;
			<font color=red>${refAttrModel.descriptionEng}</font>
		</div>	
	</div>
	<br/>
	<div class="icons no-print">
		<ul style="padding-left:.9em; ">
		
			<li style="float: left; list-style-type: none; ">
				<div id="loadingInfo" class="info" style="display: none; margin-bottom: 0.1em; width: 800px; padding: 0.2em;">Loading</div>
			</li>	
		
			<li style="float: right; top: 0px; border: 0px; background: #ffffff; list-style-type: none;">				
				<security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR')">
					<c:if test="${disableEditing == false}">			
						<img id="save" class="editMode" title="Save" src="<c:url value="/img/icons/Save.png"/>" style="display: none;" /> 
						<img id="cancel" class="editMode" title="Cancel" src="<c:url value="/img/icons/Cancel.png"/>" onclick="cancelEdit();" style="display: none;" /> 
						<img id="reset" class="editMode" title="Reset" src="<c:url value="/img/icons/Reset.png"/>" onclick="resetChanges();" style="display: none;" /> 
						<img id="edit" class="viewMode" title="Edit" src="<c:url value="/img/icons/EditGrey.png"/>" onclick="editAttribute();" /> 
						<img id="add" class="viewMode" title="Add" src="<c:url value="/img/icons/Add.png"/>" onclick="addAttribute();" /> 
						<img id="remove" class="viewMode" title="Remove" src="<c:url value="/img/icons/RemoveGrey.png"/>" />		
					</c:if>				
				</security:authorize> 					
				<img id="print" class="viewMode" title="Print" src="<c:url value="/img/icons/Print.png"/>" onclick="window.print();" /> 
			</li>
		</ul>
	</div>

	<display:table name="inContextAttributes" id="attributeTable" defaultsort="2" requestURI="" pagesize="${pageSize}" 
		size="resultSize" class="listTable" style="width: 100%; margin-top: 0px; table-layout:fixed;" sort="list">
		
		<display:setProperty name="paging.banner.placement" value="bottom" />
		<display:setProperty name="paging.banner.some_items_found" value="" />
		<display:setProperty name="basic.empty.showtable" value="true" />
		<display:setProperty name="basic.msg.empty_list_row" 
			value="<tr class='odd'><td></td><td></td><td></td><td></td><td></td></tr>"/>
				
		<display:column headerClass="tableHeader sizeThirty no-print" class="no-print">
			<input name="radioSelection" id="${attributeTable.attributeElementId}" type="radio">
		</display:column>
		
		<display:column sortable="true" titleKey="attribute.reference.generic.code" headerClass="tableHeader sizeOneFifty">
			${attributeTable.genericAttributeCode}
		</display:column>

		<display:column titleKey="attribute.reference.generic.incontextdescription.eng" headerClass="tableHeader" style="word-wrap:break-word;">
			${attributeTable.descriptionEng}
		</display:column>

		<display:column titleKey="attribute.reference.generic.incontextdescription.fra" headerClass="tableHeader" style="word-wrap:break-word;">
			${attributeTable.descriptionFra}
		</display:column>
		
		<display:column titleKey="attribute.notes" headerClass="tableHeader sizeEighty no-print" style="text-align:center;" class="no-print">
			<img title="Notes" src="<c:url value="/img/icons/Note.png"/>" onclick="notePopup(${attributeTable.attributeElementId},${versionCode},${disableEditing});"/>
		</display:column>
	</display:table>

	<label id="referenceAttributeElementId" style="display: none;">${refAttrModel.elementId}</label>
	<label id="hasConceptBeenPublished" style="display: none;">${hasConceptBeenPublished}</label>

	<c:import url="/WEB-INF/jsp/common/displayTagService.jsp"/>
	
	<select id="genAttrCodes" style="display: none;">
		<c:forEach var="genericAttributeCode" items="${genericAttributeCodes}">
			<option value="${genericAttributeCode}">
				${genericAttributeCode}
			</option>
		</c:forEach>					
	</select>
	
	<div id="notesDialog" style="display:none; overflow:hidden;" title="Notes">
		<iframe name="notesIframe" id="notesIframe"					
			src="about:blank" width="100%" height="100%"></iframe>
	</div>

	<div id="removalConfirmation" style="display: none;">
		Please confirm that you want to permanently remove the selected in-context generic description from the system.
	</div>

</div>

<script>
	/*********************************************************************************************************
	 * NAME:          Remove Attribute
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	$("#remove").click(function() {
		if (superEditMode) {
		$('#removalConfirmation').dialog({
			title : 'Confirmation: Removal of Attribute',
			width : 350, height : 250, modal : true, resizable : false, draggable : false,
			buttons : [ {
				text : 'Remove',
				click : function() {
					$(this).dialog('close');
					removeAttribute();
				}
			}, {
				text : 'Cancel',
				click : function() {
					$(this).dialog('close');
				}
			} ]
		});
		}
	});
	
	
	/*********************************************************************************************************
	 * NAME:          Save Button Clicked
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	$("#save").click(function() {
	
		if (addHasBeenClicked == true) {
			saveNewAttribute();
			return false;
		}
	
		var radioId = $('input[name=radioSelection]:checked').attr('id');
		var descriptionEng = $('#tmpDE').val();
		var descriptionFra = $('#tmpDF').val();
		
		var data = {};
		data['genericAttributeCode'] = glb_cod;
		data['descriptionEng'] = descriptionEng;
		data['descriptionFra'] = descriptionFra;
		data['attributeElementId'] = radioId;
				
		var postPage = "<c:url value='inContext/" + radioId + ".htm'/>";

		$.ajax({
			'url' : postPage,
			'type' : 'POST',
			'data' : data,
			'success' : function(response) {
	
				if (response.status == 'FAIL') {					
					hideProcessingScreen();
					hideMessage();
					showErrorMessagesFromResponse(response);
				} else {
					glb_de = descriptionEng;
					glb_df = descriptionFra;
					
					hideProcessingScreen();
					cancelEdit();
					showSuccessMessage("Changes have been successfully saved");
				}
			},
			beforeSend : function() {
				showInfoMessage('Saving');
				showProcessingScreen();
			}
		});
	
	});
	
	$("#notesDialog").dialog({	
	    position: { my: "center", at: "top", of: window } ,
	    title: 'In-Context Generic Description - Notes',
	    draggable: false, width : 650, height : 450, autoOpen: false, modal : true, 
	    close: function( event, ui ) {
	    	var iframe = $("#notesIframe");
			iframe.attr("src", "");
	    }
	});
	
	/*********************************************************************************************************
	 * NAME:          Radio Button clicked
	 * DESCRIPTION:   
	 *********************************************************************************************************/	
	 $("input[type='radio']").click(function(){
		 colorButtons();
	});
	
	 $(".tableHeader a").click(function() {		 
		 showInfoMessage('Loading');
	 });
</script>
</html>

