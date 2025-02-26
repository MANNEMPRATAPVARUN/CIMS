<!DOCTYPE html>

<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<html style="height: 100%;">
<%@ include file="/WEB-INF/jsp/common/common-header.jsp"%>

<style type="text/css" media="all">
textarea {
     width: 100%; 
     box-sizing: border-box;
     -webkit-box-sizing:border-box;
     -moz-box-sizing: border-box;
}
</style>

<script type="text/javascript">

	var glb_defEng;
	var glb_defFra;
	var editMode = false;
	var timeoutValue = 200;

	var processedMessage = {};
	processedMessage["class"] = '';
	processedMessage["message"] = '';
	processedMessage["image"] = '';
	
	$(document).ready(function() {
		window.parent.parent.hideMessage();
		setSuperEditMode();
	});
	
	function setSuperEditMode() {	
		var superEditMode = window.parent.parent.superEditMode;
		
		if (superEditMode === false) {		
			$("#edit").attr("src", "<c:url value="/img/icons/EditGrey.png"/>");
			$("#remove").attr("src", "<c:url value="/img/icons/RemoveGrey.png"/>");
			$("#edit").attr('onclick','').unbind('click');
			$("#remove").attr('onclick','').unbind('click');				
		}
	}
	
	/*********************************************************************************************************
	* NAME:          Update
    * DESCRIPTION:   
	*********************************************************************************************************/	
	function update() {
		
		var elementId = $("#hiddenElementId").text();
		var hiddenCode = $("#hiddenCode").text();
		var defEng = $('#tmpDefENG').val();
		var defFra = $('#tmpDefFRA').val();

		defEng = ((defEng == undefined) ? '' : defEng);
		defFra = ((defFra == undefined) ? '' : defFra);
		
		$.ajax({
			url : "<c:url value='diagram.htm'/>",
 			type : "POST",
			data : { e : elementId, de : defEng, df : defFra },
			dataType: "json",
			success : function(response) {

				if (response.status == 'FAIL') {				
					window.parent.parent.hideMessage();
					window.parent.parent.hideProcessingScreen();
					showErrorMessagesFromResponse(response);
				} else {			
					glb_defEng = defEng;
					glb_defFra = defFra;
					
					cancelEdit();		
					window.parent.parent.hideProcessingScreen();
					
					showSuccessMessage("Component " + hiddenCode + " successfully saved");
				}

			},
			error: function (xhRequest, ErrorText, thrownError) {		        
		        console.log('something went wrong');
		    },
			beforeSend: function(){
				showInfoMessage('Saving');
				window.parent.parent.showProcessingScreen();
	        }			
		});		
		
	}

	/*********************************************************************************************************
	* NAME:          Edit
    * DESCRIPTION:   
	*********************************************************************************************************/	
	function editComponent() { 

		glb_defEng = $('#tmpDefENG').val();
		glb_defFra = $('#tmpDefFRA').val();
		
		$('#tmpDefENG').attr('disabled', false);
		$('#tmpDefFRA').attr('disabled', false);
		
 		changeEditMode(true);
 		hideMessage();
	}

	/*********************************************************************************************************
	* NAME:          Cancel Edit
    * DESCRIPTION:   
	*********************************************************************************************************/	
	function cancelEdit() {
		
		$('#tmpDefENG').val(glb_defEng);
		$('#tmpDefFRA').val(glb_defFra);
		
		$('#tmpDefENG').attr('disabled', true);
		$('#tmpDefFRA').attr('disabled', true);
		
		changeEditMode(false);
		hideMessage();
	}

	/*********************************************************************************************************
	* NAME:          Reset Changes
    * DESCRIPTION:   Restore the changes to the current value
	*********************************************************************************************************/	
	function resetChanges() {
		$('#tmpDefENG').val(glb_defEng);
		$('#tmpDefFRA').val(glb_defFra);		
	}

	/*********************************************************************************************************
	* NAME:          Change Edit Mode
    * DESCRIPTION:   
	*********************************************************************************************************/	
	function changeEditMode(isEdit) {
		
		editMode = isEdit;
		
		if (isEdit) {
			$(".editMode").show();
			$(".viewMode").hide();				
		} else {
			$(".editMode").hide();
			$(".viewMode").show();			
		}		
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
	
	function showInfoMessage(messageToShow) {
		processedMessage["message"] = messageToShow;
		processedMessage["class"] = "info";
		
		showMessage();
	}

	function showSuccessMessage(messageToShow) {
		processedMessage["message"] = messageToShow;
		processedMessage["class"] = "success";		
		processedMessage["image"] = "<img src=\"../img/icons/Ok.png\"/>";
		
		showMessage();
	}

	function showErrorMessagesFromResponse(response) {
		var errorMessages = "";
		for ( var i = 0; i < response.errorMessageList.length; i++) {
			var item = response.errorMessageList[i];
			errorMessages += "<img src=\"../img/icons/Error.png\"/> " + item.defaultMessage;
			errorMessages += "<br/>";
		}

		processedMessage["message"] = errorMessages;
		processedMessage["class"] = "error";		
		
		showMessage();		
	}	
</script>

<div class="content">

	<div id="block_container">
		<div style="display:inline-block; width:35%;">
			<label>Section:</label>
			&nbsp;
			<font color=red>${viewer.sectionTitle}</font>
		</div>		
		<div style="display:inline-block; width:15%;">
			<label>Code Component:</label>
			&nbsp;
			<font color=red>${compModel.code}</font>
		</div>
		<div style="display:inline-block; width:30%;">
			<label>English Short Description:</label>
			&nbsp;
			<font color=red>${compModel.shortDescriptionEng}</font>
		</div>
	</div>
</div>

<div class="icons">
	<ul style="padding-left:.8em; margin-top: 20px;">
	
		<li style="float: left; list-style-type: none; width: 50%">
			<div id="loadingInfo" class="info" style="display: none; margin-bottom: 0.1em; width: 100%; padding: 0.2em;">Loading</div>			
		</li>	
	
		<li style="float: right; top: 0px; border: 0px; background: #ffffff; list-style-type: none;">				
			<security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR')">			
			<img id="save" class="editMode" title="Save" src="<c:url value="/img/icons/Save.png"/>" onclick="update();" style="display: none;"/>
			<img id="cancel" class="editMode" title="Cancel" src="<c:url value="/img/icons/Cancel.png"/>" onclick="cancelEdit();" style="display: none;"/>
			<img id="reset" class="editMode" title="Reset" src="<c:url value="/img/icons/Reset.png"/>" onclick="resetChanges();" style="display: none;"/>	
			<img id="edit" class="viewMode" title="Edit" src="<c:url value="/img/icons/Edit.png"/>" onclick="editComponent();"/>
			<img id="remove" class="viewMode" title="Remove" src="<c:url value="/img/icons/Remove.png"/>"/>			
			</security:authorize>	
		</li>
	</ul>
</div>

<div class="content">
	<table id="group" style="width: 100%; margin-top: 20px; table-layout:fixed;" class="listTable">
		<thead>
			<tr>
				<th class="tableHeader">English Definition</th>
				<th class="tableHeader">French Definition</th>
			</tr>
		</thead>
		<tbody>
			<tr class="odd">
				<td><textarea id='tmpDefENG' style='height:200px; word-wrap: break-word;' disabled="true">${definitionEng}</textarea></td>
				<td><textarea id='tmpDefFRA' style='height:200px; word-wrap: break-word;' disabled="true">${definitionFra}</textarea></td>
		</tbody>
	</table>

	<div id="removalConfirmation" style="display: none;">
    	Do you want to remove the definition for ${compModel.code} in this version of classification?
	</div>
	
	<label id="hiddenElementId" style="display: none;">${compModel.elementId}</label>
	<label id="hiddenCode" style="display: none;">${compModel.code}</label>
	<label id="hiddenYear" style="display: none;">${viewer.versionCode}</label>
	<label id="editMode" style="display: none;"></label>
</div>

<script>

	/*********************************************************************************************************
	* NAME:          Remove button click
	* DESCRIPTION:   Binds to the remove click.  Displays a dialog box to verify removal
	*********************************************************************************************************/	
	$("#remove").click(function () {
		
		$('#removalConfirmation').dialog({
			  title: 'Confirmation: Removal of Definition',
			  width: 300, height: 200, modal: true, resizable: false, draggable: false,
			  buttons: [{
			  text: 'Remove',
			  click: function() {
				  $(this).dialog('close');	
				  $('#tmpDefENG').val('');
				  $('#tmpDefFRA').val('');	  
				  update();
			    }
			  },
			  {
			  text: 'Cancel',
			  click: function() {
			      $(this).dialog('close');
			    }
			  }]
			});
		
	});

</script>
</html>

