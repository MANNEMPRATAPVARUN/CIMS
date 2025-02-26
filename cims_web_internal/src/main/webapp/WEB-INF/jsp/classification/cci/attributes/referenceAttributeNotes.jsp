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

textarea {
     width: 100%; 
     box-sizing: border-box;
     -webkit-box-sizing:border-box;
     -moz-box-sizing: border-box;
}
</style>

<script src="<c:url value="/js/xmltemplates.js"/>"></script>
<script type="text/javascript">

	var glb_notesEng;
	var glb_notesFra;
	var editMode = false;
	var timeoutValue = 200;

	$(document).ready(function() {
		registerXmlTemplateButton("${pageContext.request.contextPath}", "NOTE", "formXml", ["tmpNotesENG", "tmpNotesFRA"]);
		
		window.parent.parent.hideMessage();
		window.parent.parent.hideProcessingScreen();
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
		var postPage = "<c:url value='/referenceAttributes/" + elementId + "/notes.htm?'/>";		
		var hiddenCode = $("#hiddenCode").text();
		var notesEng = $('#tmpNotesENG').val();
		var notesFra = $('#tmpNotesFRA').val();
		
		notesEng = ((notesEng == undefined) ? '' : notesEng);
		notesFra = ((notesFra == undefined) ? '' : notesFra);
		
		$("#statBar").text('Saving');		
		$("#statBar").attr("class", 'info');
		$("#statBar").show();
		
		$.ajax({
			url : postPage,
 			type : "POST",
			data : { ne : notesEng, nf : notesFra },
			dataType: "json",
			success : function(response) {

				if (response.status == 'FAIL') {				
					showErrorMessagesFromResponse(response);
				} else {					
					glb_notesEng = notesEng;
					glb_notesFra = notesFra;
					
					cancelEdit();		
					window.parent.parent.hideProcessingScreen();
					
					$("#statBar").text("Attribute " + hiddenCode + " successfully saved");
					$("#statBar").attr("class", 'success');
					$("#statBar").show();											
				}

			},
			error: function (xhRequest, ErrorText, thrownError) {		        
		        console.log('something went wrong');
		    },
			beforeSend: function(){
				window.parent.parent.showProcessingScreen();
	        }			
		});		
		
	}

	/*********************************************************************************************************
	* NAME:          Edit
    * DESCRIPTION:   
	*********************************************************************************************************/	
	function editComponent() { 

    	glb_notesEng = $('#tmpNotesENG').val();
    	glb_notesFra = $('#tmpNotesFRA').val();
		
		$('#tmpNotesENG').attr('disabled', false);
		$('#tmpNotesFRA').attr('disabled', false);		
		
 		changeEditMode(true);
 		hideErrorMessage();
	}

	/*********************************************************************************************************
	* NAME:          Cancel Edit
    * DESCRIPTION:   
	*********************************************************************************************************/	
	function cancelEdit() {
		
		$('#tmpNotesENG').val(glb_notesEng);
		$('#tmpNotesFRA').val(glb_notesFra);
		
		$('#tmpNotesENG').attr('disabled', true);
		$('#tmpNotesFRA').attr('disabled', true);
		
		changeEditMode(false);
		hideErrorMessage();
	}

	/*********************************************************************************************************
	* NAME:          Reset Changes
    * DESCRIPTION:   Restore the changes to the current value
	*********************************************************************************************************/	
	function resetChanges() {
		$('#tmpNotesENG').val(glb_notesEng);
		$('#tmpNotesFRA').val(glb_notesFra);		
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
	
	function showErrorMessagesFromResponse(response) {
		var errorMessages = "";
		for (var i = 0; i < response.errorMessageList.length; i++) {
			var item = response.errorMessageList[i];
			errorMessages += item.defaultMessage;
			errorMessages += "<br/>";
		}
		
		$("#errorMessageList").html(errorMessages);
		$("#errorMessageList").show();
	}

	function hideErrorMessage() {		
		$("#errorMessageList").hide();
	}
	
</script>

<div class="content">

	<div id="block_container">
		<div style="display:inline-block; width:20%;">
			<label>Code:</label>&nbsp;&nbsp;
			<font color=red>${attrModel.code}</font>
		</div>

		<div style="display:inline-block; width:40%;">
			<label>English Description:</label>&nbsp;&nbsp;
			<font color=red>${attrModel.descriptionEng}</font>
		</div>
		
		<div style="display:inline-block; text-align: right; float:right; top:0px; border:0px; background: #ffffff;">
			<security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR')">
			
			<img id="formXml" class="editMode" title="Xml Template" src="<c:url value="/img/icons/Xml.png"/>" style="display: none;"/>
			<img id="save" class="editMode" title="Save" src="<c:url value="/img/icons/Save.png"/>" onclick="update();" style="display: none;"/>
			<img id="cancel" class="editMode" title="Cancel" src="<c:url value="/img/icons/Cancel.png"/>" onclick="cancelEdit();" style="display: none;"/>
			<img id="reset" class="editMode" title="Reset" src="<c:url value="/img/icons/Reset.png"/>" onclick="resetChanges();" style="display: none;"/>	
			<img id="edit" class="viewMode" title="Edit" src="<c:url value="/img/icons/Edit.png"/>" onclick="editComponent();"/>
			<img id="remove" class="viewMode" title="Remove" src="<c:url value="/img/icons/Remove.png"/>"/>			
			</security:authorize>			
		</div>		
	</div>
	
	<div id="errorMessageList" class="error" style="display: none;"></div>
	
	<table id="notes" style="width: 100%; margin-top: 20px; table-layout:fixed;" class="listTable">
		<thead>
			<tr>
				<th class="tableHeader sizeOneFifty">English</th>
				<th class="tableHeader sizeOneFifty">French</th>
			</tr>
		</thead>
		<tbody>
			<tr class="odd">
				<td><textarea id='tmpNotesENG' style='height:200px; word-wrap: break-word;' disabled="true">${notesEng}</textarea></td>
				<td><textarea id='tmpNotesFRA' style='height:200px; word-wrap: break-word;' disabled="true">${notesFra}</textarea></td>				
		</tbody>
	</table>

	<div id="removalConfirmation" style="display: none;">
    	Please confirm that you want to permanently remove the selected notes from the system.
	</div>

	<div id="statBar" class="info" style="display: none; margin-top: 10px;">Loading</div>
	<label id="hiddenElementId" style="display: none;">${attrModel.elementId}</label>
	<label id="hiddenCode" style="display: none;">${attrModel.code}</label>
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
			title: 'Confirmation: Removal of Notes',
			width: 300, height: 200, modal: true, resizable: false, draggable: false,
			buttons: [{
				text: 'Remove',
				click: function() {
					$(this).dialog('close');
					$('#tmpNotesENG').val('');
					$('#tmpNotesFRA').val('');
					glb_notesEng = '';
					glb_notesFra = '';						
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

