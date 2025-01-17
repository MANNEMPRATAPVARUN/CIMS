<!DOCTYPE html>

<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<html style="height: 100%;">
<%@ include file="/WEB-INF/jsp/common/common-header.jsp"%>

<style type="text/css">
input[type="text"] {
     width: 100%; 
     box-sizing: border-box;
     -webkit-box-sizing:border-box;
     -moz-box-sizing: border-box;
}
</style>

<script src="<c:url value="/js/displayTagService.js"/>"></script>
<script type="text/javascript">

	var glb_fi;
	var glb_de;
	var glb_st;

	var processedMessage = {};
	processedMessage["class"] = '';
	processedMessage["message"] = '';
	processedMessage["image"] = '';
	
	var readOnly = false;				//Sets page permission
	var addHasBeenClicked = false;		//Keep track if in add mode.  Cancel/Reset differ in this mode compared to Edit 
	var files;							//Holds uploaded file

	$(document).ready(function() {		
		$('#versionCode').val($("#vcDefault").text());
		$('#baseClassification').val($("#baseClassificationDefault").text());
		setPageMode();
		setSuccessMessage();
		
	});

	/*********************************************************************************************************
	* NAME:          Set Page Mode
	* DESCRIPTION:   Sets the page permission
	**********************************************************************************************************/
	function setPageMode() {
		
		var isOpen = $('#vcOpen' + $("#vcDefault").text()).text();
		
		if (isOpen == "false") {
			readOnly = true;
		} else {
			readOnly = false;
		}
		
		if (readOnly === true) {
			$("#edit").attr('onclick','').unbind('click');
			$("#remove").attr('onclick','').unbind('click');
			$("#add").attr('onclick','').unbind('click');
		} else {
			//Colorize the Add button
			$("#add").attr('src', 'img/icons/Add.png');
		}
	}

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

	/*********************************************************************************************************
	* NAME:          View Diagram
	* DESCRIPTION:   Opens a new window with the diagram image
	**********************************************************************************************************/
	function viewDiagramFigure(elementId) {
					
		var postPage = "<c:url value='/diagrams/" + elementId + "/graphic.htm'/>";
		
		var width = 400;
		var height = 400;
		var left = (screen.width/2)-(width/2);
		var top = (screen.height/2)-(height/2);
		
		window.open(postPage, 'Window', 
				'resizable=yes, scrollbars=yes, toolbar=no ,location=0, status=no, titlebar=no, menubar=no, ' +
				'width=' + width + ', height=' + height + ', top=' + top + ', left='+left);
	}

	/*********************************************************************************************************
	* NAME:          Update Year
	* DESCRIPTION:   Responds when the view button is clicked.  Refreshes page with the new year
	**********************************************************************************************************/
	function updateYear() {

		showInfoMessage('Retrieving data');
		showProcessingScreen();
		
		var year = $("#versionCode").val();
		var baseClassification = $('#baseClassification').val();
 		var postPage = "<c:url value='/diagrams.htm?year=" + year + "&bc=" + baseClassification + "'/>";

 		window.location.href = postPage;		
	}	

	/*********************************************************************************************************
	* NAME:          Edit Diagram
	* DESCRIPTION:   Modifies the selected table row so that the user can update the diagram
	**********************************************************************************************************/
	function editDiagram() {

		if (readOnly === true) {
			return false;
		}	
		
		var radioId = $('input[name=radioSelection]:checked').attr('id');
		if (typeof radioId === 'undefined') {
			alert('No radio button selected');
			return false;
		} 
		
		var tableRow = $('input[name=radioSelection]:checked').parent().parent();
		var filename = tableRow.children("td:nth-child(2)");
		var description = tableRow.children("td:nth-child(3)");
		var status = tableRow.children("td:nth-child(4)");
		
		glb_fi = $.trim(filename.text());		
		glb_de = $.trim(description.text());
		glb_st = $.trim(status.text().toUpperCase());

		description.html("<input type='text' id='tmpDE' maxlength='200'/>");		
		$('#tmpDE').val(glb_de);		
		
		var statusSelected = "<select id='tmpS' name='tmpS'>";
		if (glb_st == 'ACTIVE') {
			statusSelected += "<option value='ACTIVE' SELECTED>Active</option><option value='DISABLED'>Disabled</option>";
		} else {
			statusSelected += "<option value='ACTIVE'>Active</option><option value='DISABLED' SELECTED>Disabled</option>";
		}

		statusSelected += "</select>";
		status.html(statusSelected);
		
		changeEditMode(true);		
	}

	/*********************************************************************************************************
	* NAME:          Save New Diagram				          
	* DESCRIPTION:   
	**********************************************************************************************************/
	function saveNewDiagram() {

		var tableRow = $('#diagramsTable tr:last');
		var colTwo = tableRow.children("td:nth-child(2)");
		var filename = colTwo.text();
		var baseClassification = $("#baseClassificationDefault").text();
		 
		var formData = new FormData();
    	formData.append("fileName", filename.trim());
    	formData.append("description", $('#tmpDE').val());
    	formData.append("status", $('#tmpS').val());
    	formData.append("baseClassification", baseClassification);		 
	 
    	//Only working with one file, but could support multiple.  Replace using 'key'
    	if (typeof files === 'undefined') {
    		console.log('no file is being uploaded');
    	} else {
    		$.each(files, function(key, value)
			{
  		    	formData.append("diagramFile", value);
  		    });	
    	}		 

		var postPage = "<c:url value='/diagrams.htm?'/>";

		$.ajax({
			'url' : postPage,
			'type' : 'POST',
			//'dataType': 'text',  //Dont set.  The response is in JSON
			'data' : formData,
 			'processData': false, // Don't process the files
 	        'contentType': false, // Set content type to false as jQuery will tell the server its a query string request
			success : function(response) {
	
				if (response.status == 'FAIL') {
 					hideProcessingScreen();
 					hideMessage();
 					showErrorMessagesFromResponse(response);					
				} else {
					localStorage.setItem('successMessage', "Diagram has been successfully saved.");
					location.reload();
				}
			},

			beforeSend : function() {
				hideMessage();					
				showInfoMessage('Saving');				
 				showProcessingScreen();
			}
		});    	
	}
	
	/*********************************************************************************************************
	* NAME:          Cancel Edit Or Add     
	* DESCRIPTION:   Revert the row back to non edit mode, and replace the table back to before edit/add
	**********************************************************************************************************/
	function cancelEditOrAdd() {

		//In case the user uploaded a file, set to undefied so it doesn't get uploaded to another diagram by accident
		files = undefined;
		
		//Remove any error messages, since they wont be relevant anymore
		hideMessage();
		
 		if (addHasBeenClicked == true) {
 			$('#diagramsTable tr:last').remove();
 			addHasBeenClicked = false;
 		} else {
 			var tableRow = $('input[name=radioSelection]:checked').parent().parent();
 			var description = tableRow.children("td:nth-child(3)");
 			var status = tableRow.children("td:nth-child(4)");
 			
 			description.html(glb_de);
 			status.html(glb_st.substring(0, 1).toUpperCase() + glb_st.substring(1).toLowerCase());	
 		}

		changeEditMode(false);
	}
	
	/*********************************************************************************************************
	* NAME:          Add Diagram
	* DESCRIPTION:   Adds a row to the table so that the user can add a diagram
	**********************************************************************************************************/	
	function addDiagram() {
		
		if (readOnly === true) {
			return false;
		}
		
 		addHasBeenClicked = true;

		var tbl = $('#diagramsTable');
		var tableRow = $('#diagramsTable tr:last');
		var lastRowClass = tableRow.attr('class');
		var colTwo = tableRow.children("td:nth-child(2)").attr('style');
		var colThree = tableRow.children("td:nth-child(3)").attr('style');
		var colFour = tableRow.children("td:nth-child(4)").attr('style');
		//var colFive = tableRow.children("td:nth-child(5)").attr('style');
		var newRowClass = (lastRowClass == 'odd') ? 'even' : 'odd';
		
		glb_fi = "";
		glb_de = "";
		glb_st = "ACTIVE";

		var description = "<input type='text' id='tmpDE' value='' maxlength='255'/>";
		var statusSelected = "<select id='tmpS'><option value='ACTIVE' SELECTED>Active</option></select>";
		//var baseClassification = "<select id='tmpBC'><option value='ICD-10-CA' SELECTED>ICD-10-CA</option>" + 
		//	"<option value='CCI'>CCI</option></select>";

		var newRow = "<tr class=\"" + newRowClass + "\">";

		newRow += "<td></td>"; // Radio button
		newRow += "<td style=\"" + colTwo + "\"></td>"; // Filename
		newRow += "<td style=\"" + colThree + "\">" + description + "</td>"; // Description
		newRow += "<td style=\"" + colFour + "\">" + statusSelected + "</td>"; // Status
		//newRow += "<td style=\"" + colFive + "\">" + baseClassification + "</td>"; // Base Classification
		newRow += "</tr>";

		tbl.append(newRow);
		changeEditMode(true);
		
		var lastRowOffSet = $('#diagramsTable tr:last').offset().top;
		$(document).scrollTop(lastRowOffSet);
	}
	
	/*********************************************************************************************************
	* NAME:          Remove Diagram	
	* DESCRIPTION:   Remove the diagram from the selected year   
	**********************************************************************************************************/
	function removeDiagram() {
				 
 		var radioId = $('input[name=radioSelection]:checked').attr('id');
 		if (typeof radioId === 'undefined') { 			
 			return false;
 		} 
 		
		var postPage = "<c:url value='/diagrams/" + radioId + ".htm?'/>";
		
		$.ajax({
			'url' : postPage,
			'type' : 'DELETE',
			'success' : function(response) {
	
				if (response.status == 'FAIL') {
					hideProcessingScreen();
					hideMessage();
					showErrorMessagesFromResponse(response);					
				} else {
					localStorage.setItem('successMessage', "Diagram has been successfully removed.");
					location.reload();
				}
			},
			beforeSend : function() {
				showInfoMessage('Removing');
				showProcessingScreen();
			}
		});		 		
	}
	
	
	/*********************************************************************************************************
	* NAME:          Change Edit Mode
	* DESCRIPTION:   Pass in true if you want edit mode, false to turn it off
	**********************************************************************************************************/
	function changeEditMode(isEdit) {

		$("#editMode").html(isEdit.toString());

		$('input[name=radioSelection]').attr('disabled', isEdit);

		if (isEdit) {
			$(".editMode").show();
			$(".viewMode").hide();
			$(".tableHeader a").bind('click', function(e){ e.preventDefault();});
		} else {
			$(".editMode").hide();
			$(".viewMode").show();
			$(".tableHeader a").unbind('click');
		}
	}
	
	/*********************************************************************************************************
	* NAME:          Reset Changes    
	* DESCRIPTION:   In Add/Edit mode, this function restores the value to the original
	**********************************************************************************************************/
	function resetChanges() {

		$('#tmpDE').val(glb_de);		
		$('#tmpS').val(glb_st);
	}

	/*********************************************************************************************************
	* NAME:          Color Buttons    
	* DESCRIPTION:   Change the images to the colorized ones if page is not read only
	*********************************************************************************************************/
	function colorButtons() {
		if (readOnly === false) {
			$("#edit").attr('src', 'img/icons/Edit.png');
			$("#remove").attr('src', 'img/icons/Remove.png');
		}		
	}

	/*********************************************************************************************************
	* NAME:          Triggers the file window    
	* DESCRIPTION:   
	*********************************************************************************************************/	
	function browseForFile() {
	    $('input[type=file]').trigger('click'); 
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
		for ( var i = 0; i < response.errorMessageList.length; i++) {
			var item = response.errorMessageList[i];
			errorMessages += "<img src=\"img/icons/Error.png\"/> " + item.defaultMessage;
			errorMessages += "<br/>";	
		}

		processedMessage["message"] = errorMessages;
		processedMessage["class"] = "error";		
		
		showMessage();		
	}
</script>

<div class="content" style="padding-left: 0px;margin-left: 20px;">

	<label id="editMode" style="display: none;"></label>
	<label id="vcDefault" style="display: none;">${vcDefault}</label>
	<label id="baseClassificationDefault" style="display: none;">${baseClassification}</label>
	<c:forEach var="vcOpen" items="${versionCodesOpen}">
		<label id="vcOpen${vcOpen.key}" style="display: none;">${vcOpen.value}</label>	
	</c:forEach>
	<input type="file" name="fileInput" accept="image/gif" style="display: none;"/>

	<table style="border: none;">
		<tr>
			<td style="width: 45%">
				<label>Classification:</label>&nbsp;&nbsp;
				<select id="baseClassification" path="baseClassification">
					<c:forEach var="baseClassification" items="${baseClassifications}">
						<option value="${baseClassification}">${baseClassification}</option>
					</c:forEach>
				</select>
			</td>
			
			<td style="width: 25%">
				<label>Year:</label>&nbsp;&nbsp;
				<select id="versionCode" path="versionCode">
					<c:forEach var="versionCode" items="${versionCodes}">
						<option value="${versionCode}">${versionCode}</option>
					</c:forEach>
				</select>
			</td>
			<td style="width: 30%">
				<input id="viewButton" class="button" type="button" 
					value="<fmt:message key='cims.icd10.conceptViewer.submitButton'/>"
					onclick="updateYear();" />
			</td>
		</tr>
	</table>
</div>

<div class="icons">
<!-- 	<ul style="padding-left:.9em; margin-top: 20px; margin-left: 20px; margin-right: 20px;"> -->
	<ul style="padding-left:0; margin-top: 20px; margin-left: 20px; margin-right: 20px;">
	
		<li style="float: left; list-style-type: none; width: 50%">
			<div id="loadingInfo" class="info" style="display: none; margin-bottom: 0.1em; width: 100%; padding: 0.2em;">Loading</div>			
		</li>	
	
		<li style="float: right; top: 0px; border: 0px; background: #ffffff; list-style-type: none;">				
			<img id="edit" class="viewMode" title="Edit" src="img/icons/EditGrey.png" onclick="editDiagram();" /> 
			<img id="add" class="viewMode" title="Add" src="img/icons/AddGrey.png" onclick="addDiagram();" /> 
			<img id="remove" class="viewMode" title="Remove" src="img/icons/RemoveGrey.png" />							 									
			<img id="save" class="editMode" title="Save" src="img/icons/Save.png" style="display: none;" /> 
			<img id="browse" class="editMode" title="Browse" src="img/icons/Folder.png" onclick="browseForFile();" style="display: none;" />
			<img id="cancel" class="editMode" title="Cancel" src="img/icons/Cancel.png" onclick="cancelEditOrAdd();" style="display: none;" /> 
			<img id="reset" class="editMode" title="Reset" src="img/icons/Reset.png" onclick="resetChanges();" style="display: none;" />
		</li>
	</ul>
</div>
<div id="diagramDiv" style="clear:both; padding-left:20px; padding-right:20px;">
	<display:table name="diagrams" id="diagramsTable" defaultsort="2" requestURI="" pagesize="${pageSize}" 
		size="resultSize" class="listTable" style="width:100%; table-layout:fixed;" sort="list">
		
		<display:setProperty name="paging.banner.placement" value="bottom" />
		<display:setProperty name="paging.banner.some_items_found" value="" />
		<display:setProperty name="basic.empty.showtable" value="true" />

		<display:column headerClass="tableHeader sizeThirty no-print" class="no-print">
			<input name="radioSelection" id="${diagramsTable.elementId}" type="radio">
		</display:column>
		
		<display:column titleKey="diagram.filename" headerClass="tableHeader sizeOneSixty" sortable="true">
 			<label id="fileName" style="display: none;">${diagramsTable.fileName}</label> <%-- DT sorting gets tripped up due to links --%>
			<a href="#" onClick="viewDiagramFigure('${diagramsTable.elementId}');">${diagramsTable.fileName}</a>
		</display:column>
				
		<display:column titleKey="diagram.description" headerClass="tableHeader" sortable="true">
			${diagramsTable.description}
		</display:column>
		
		<display:column titleKey="diagram.status" headerClass="tableHeader sizeEighty" sortable="true"
			style="text-align:center;">
			
			${cims:capitalizeFully(diagramsTable.status)}
		</display:column>

<%-- 		<display:column titleKey="diagram.classification" headerClass="tableHeader sizeOneTen" sortable="true" --%>
<%-- 			style="text-align:center;"> --%>
<%-- 			${diagramsTable.baseClassification}      	 --%>
<%-- 		</display:column>		       	 --%>
	</display:table>	

	<c:import url="/WEB-INF/jsp/common/displayTagService.jsp"/>
</div>

<div id="removalConfirmation" style="display: none;"></div>

<script>

	/*********************************************************************************************************
	* NAME:          Save Button Clicked
	* DESCRIPTION:   
	**********************************************************************************************************/
	$("#save").click(function() {
	
		if (addHasBeenClicked == true) {
			saveNewDiagram();
			return false;
		}

		var tableRow = $('input[name=radioSelection]:checked').parent().parent();
		var fileNameRow = tableRow.children("td:nth-child(2)");
		var fileName = fileNameRow.children('#fileName').text();
		//var filename = tableRow.children("td:nth-child(2)").text();
		var baseClassification = tableRow.children("td:nth-child(5)").text();
		var radioId = $('input[name=radioSelection]:checked').attr('id');
		var description = $('#tmpDE').val();
		var status = $('#tmpS').val();
		 
    	var formData = new FormData();
    	formData.append("elementId", radioId);
    	formData.append("fileName", fileName.trim());
    	formData.append("description", description);
    	formData.append("status", status);
    	formData.append("baseClassification", baseClassification.trim());
    	
    	//Only working with one file, but could support multiple.  Replace using 'key'
    	if (typeof files === 'undefined') {
    		console.log('no file is being uploaded');
    	} else {
    		$.each(files, function(key, value)
			{
  		    	formData.append("diagramFile", value);
  		    });	
    	}

		var postPage = "<c:url value='/diagrams/" + radioId + ".htm'/>";

		$.ajax({
			'url' : postPage,
			'type' : 'POST',
			//'dataType': 'text',  //Dont set.  The response is in JSON
			'data' : formData,
 			'processData': false, // Don't process the files
 	        'contentType': false, // Set content type to false as jQuery will tell the server its a query string request
			success : function(response) {
	
				if (response.status == 'FAIL') {
 					hideProcessingScreen();
 					hideMessage();
 					showErrorMessagesFromResponse(response);					
				} else {
					localStorage.setItem('successMessage', "Diagram has been successfully updated");
					location.reload();
				}
			},

			beforeSend : function() {									
				showInfoMessage('Saving');				
 				showProcessingScreen();
			}
		});
	
	});
	
	/*********************************************************************************************************
	* NAME:          Remove Diagram
	* DESCRIPTION:   
	**********************************************************************************************************/
	$("#remove").click(function() {

		var radioId = $('input[name=radioSelection]:checked').attr('id');
		if (typeof radioId === 'undefined') {
			console.log('No radio button selected');
			return true;
		}
		
		$("#removalConfirmation").text('Please confirm you wish to permanently remove the selected ' + 
				' diagram from the system.');
	
		$('#removalConfirmation').dialog({
			title : 'Confirmation: Removal of Diagram',
			width : 350, height : 150, modal : true, resizable : false, draggable : false,
			buttons : [ {
				text : 'Remove',
				click : function() {
					$(this).dialog('close');
					removeDiagram();
				}
			}, {
				text : 'Cancel',
				click : function() {					
					$(this).dialog('close');
				}
			} ]
		});
	
	});	

	/*********************************************************************************************************
	* NAME:          Radio button clicked
	* DESCRIPTION:   Respond whenever a radio button is clicked
	**********************************************************************************************************/	
	$("input[type='radio']").click(function() {
		colorButtons();
	});	

	/*********************************************************************************************************
	* NAME:          File has been selected for upload
	* DESCRIPTION:   Could support multiple files at some point.
	*				 This is also responsible for populating the 'filename' field on Adding new diagram
	**********************************************************************************************************/		
	$('input[type=file]').change(function(e) {
		files = e.target.files; 
		
	  	for (var i = 0, file; file = files[i]; i++) {
	    	console.log(file);
	    	console.log(file.name);
	    	
			if (addHasBeenClicked == true) {
				var tableRow = $('#diagramsTable tr:last');
				var colTwo = tableRow.children("td:nth-child(2)");
				colTwo.text(file.name);
			}
	  	}
	});

	
</script>