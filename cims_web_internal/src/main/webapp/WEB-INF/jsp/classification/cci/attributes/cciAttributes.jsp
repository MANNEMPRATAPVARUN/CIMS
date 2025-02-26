<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<style type="text/css" media="all">

/* .ui-dialog { */
/*     z-index:1100 !important;  */
/* } */
</style>

<script type="text/javascript">
	var glb_cod;
	var glb_de;
	var glb_df;
	var glb_ma;
	var glb_st;
	var editMode = false;
	var superEditMode = false;
	var addMode = false;			// Page is in ADD mode
	var attributeAdded = false;  	// Used to tell iFrame that a attribute has been created.  Shows message after reload
	var attributeRemoved = false;	// Used to tell iFrame that a attribute has been removed.  Shows message after reload	
	var attributeCodeToDisplay;  	// Tells iFrame which code was added/removed.  Shows in message
	
	var processedMessage = {};
	processedMessage["class"] = '';
	processedMessage["message"] = '';
	processedMessage["image"] = '';
	
	$(document).ready(function() {
		$('#versionCode').val($("#vcDefault").text());
		displayIcons();
	});

	function setSuperEditMode() {			
		var currentVC = $('#versionCode').val();
		var isContextFrozen = $("#isContextFrozen").text();
		
		superEditMode = ( ($('#vcOpen' + currentVC).text() === "true") && (isContextFrozen === "false") );
		$("#add").attr('src', (superEditMode === true) ? "img/icons/Add.png" : "img/icons/AddGrey.png");
	}
	
	/*********************************************************************************************************
	* NAME:          Display Icons
	* DESCRIPTION:   Icon List Item is not viewable by default.  After a classification has been selected
	*                then display icons
	*********************************************************************************************************/	
	function displayIcons() {
		var ensureSelected = ensureClassificationIsSelected();
		if (ensureSelected == false) {
			return false;
		}
		
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
	
	function hideMessage() {
		$("#loadingInfo").hide();		
	}	

	/*********************************************************************************************************
	* NAME:          Context Frozen Message    
	* DESCRIPTION:   Shows an info message when the context is frozen
	*********************************************************************************************************/	
	function contextFrozenMessage(isContextFrozen) {

		if (isContextFrozen) {
			var image = "<img src=\"img/icons/Info.png\"/>";
			var message = "The CCI classification table package is being generated. Changes to generic attributes, " +
				"reference values and in-context generic description are restricted.";
			
			$("#contextFrozen").text("");
			$("#contextFrozen").append(image);
			$("#contextFrozen").append(" " + message);
			$("#contextFrozen").show();
		} else {
			$("#contextFrozen").hide();
		}
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

	function greyButtons() {
		$("#edit").attr('src', 'img/icons/EditGrey.png');
		$("#remove").attr('src', 'img/icons/RemoveGrey.png');
		$("#reference").attr('src', 'img/icons/BookGrey.png');		
	}
	
	function colorButtons() {
	    if (superEditMode === true) {
	    	$("#edit").attr('src', 'img/icons/Edit.png');
			$("#remove").attr('src', 'img/icons/Remove.png');	    	
	    } 
	    
		$("#reference").attr('src', 'img/icons/Book.png');
	}
	
	function resizeIframe(iframe) {
		iframe.height = iframe.contentWindow.document.body.scrollHeight + "px";
	}
	
	function destroyIframe() {
		var iframe = getSelectedIFrame();
 	    iframe.attr("src", "");
	}

	/*********************************************************************************************************
	 * NAME:          Reference Link
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function referenceAlert() {
		var iframe = getSelectedIFrame();

		var radioId = iframe.contents().find('input[name=radioSelection]:checked').attr('id');
		if (typeof radioId === 'undefined') {
			console.log('No radio button selected');
			return false;
		}
		
		var tableRow = iframe.contents().find('input[name=radioSelection]:checked').parent().parent();
		var genericCode = tableRow.children("td:nth-child(2)").html().trim();
		var postPage = "<c:url value='/" + $("#attributeViewType").val() + "Attributes/" + radioId + "/references.htm?'/>";
		
		window.open(postPage + "c=" + genericCode, 'Reference Links', 
				'resizable=yes, scrollbars=yes, toolbar=no ,location=0, status=no, titlebar=no, menubar=no, width=1100, height=500');
	}
	
	/*********************************************************************************************************
	 * NAME:          Edit Attribute
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function editAttribute() {

		var ensureSelected = ensureClassificationIsSelected();
		var iframe = getSelectedIFrame();

		if ((iframe == false) || (ensureSelected == false) || (superEditMode === false)) {
			console.log('Attribute not ready yet');
			hideMessage();
			return false;
		}
		
		var radioId = iframe.contents().find('input[name=radioSelection]:checked').attr('id');
		if (typeof radioId === 'undefined') {
			console.log('No radio button selected');
			return false;
		}

		var tableRow = iframe.contents().find('input[name=radioSelection]:checked').parent().parent();
		var code = tableRow.children("td:nth-child(2)");
		var descriptionEng = tableRow.children("td:nth-child(3)");
		var descriptionFra = tableRow.children("td:nth-child(4)");		
		var mandatory = tableRow.children("td:nth-child(5)");
		var status = tableRow.children("td:nth-child(7)");
		var isNewlyCreated = iframe.contents().find('#' + radioId + '_isNewlyCreated');
		
		glb_cod = $.trim(code.text());		
		glb_de = $.trim(descriptionEng.text());
		glb_df = $.trim(descriptionFra.text());
		glb_ma = $.trim(mandatory.text()) == 'Yes' ? 'Y' : 'N';
		glb_st = $.trim(status.text().toUpperCase());

		descriptionEng.html("<input type='text' id='tmpDE' maxlength='255'/>");
		descriptionFra.html("<input type='text' id='tmpDF' maxlength='255'/>");

		iframe.contents().find('#tmpDE').val(glb_de);
		iframe.contents().find('#tmpDF').val(glb_df);
		
		var statusSelected = "<select id='tmpS' name='tmpS'>";
		
		if ( (glb_st != 'ACTIVE') || (isNewlyCreated.text() == 'true') )  {
			statusSelected += "<option value='ACTIVE'>Active</option>";			
		} else {
			statusSelected += "<option value='ACTIVE' SELECTED>Active</option><option value='DISABLED'>Disabled</option>";			
		}		

		statusSelected += "</select>";
		status.html(statusSelected);
		
		if ($("#attributeViewType").val() == 'reference') {
			var mandatorySelected = "<select id='tmpMA' name='tmpMA'>";
			if (glb_ma == 'Y') {
				mandatorySelected += "<option value='Y' SELECTED>Yes</option><option value='N'>No</option>";
			} else {
				mandatorySelected += "<option value='Y'>Yes</option><option value='N' SELECTED>No</option>";
			}

			mandatorySelected += "</select>";
			mandatory.html(mandatorySelected);
		}
		
		changeEditMode(true);
	}	
	
	/*********************************************************************************************************
	 * NAME:          Add new table row
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function addAttribute() {

		var ensureSelected = ensureClassificationIsSelected();
		var iframe = getSelectedIFrame();

		if ((iframe == false) || (ensureSelected == false) || (superEditMode === false)) {
			console.log('Attribute not ready yet');
			hideMessage();
			return false;
		}

		addMode = true;

		var tbl = iframe.contents().find('#attributeTable');
		var tableRow = iframe.contents().find('#attributeTable tr:last');
		var lastRowClass = tableRow.attr('class');
		var colFive = tableRow.children("td:nth-child(5)").attr('style');
		var colSix = tableRow.children("td:nth-child(6)").attr('style');
		var colEight = tableRow.children("td:nth-child(6)").attr('style');
		var newRowClass = (lastRowClass == 'odd') ? 'even' : 'odd';
		
		glb_sde = "";
		glb_sdf = "";
		glb_lde = "";
		glb_ldf = "";
		glb_ma = "";
		glb_st = "ACTIVE";

		var code = "<input type='text' id='tmpCode' value='' maxlength='3' style='text-transform:uppercase; width:40px;' " +
			"onblur='this.value=this.value.toUpperCase()'/>";
		var descriptionEng = "<input type='text' id='tmpDE' value='' maxlength='255'/>";
		var descriptionFra = "<input type='text' id='tmpDF' value='' maxlength='255'/>";
		var mandatorySelected = "<select id='tmpMA'><option value='Y'>Yes</option><option value='N'>No</option></select>";
		var statusSelected = "<select id='tmpS'><option value='ACTIVE' SELECTED>Active</option></select>";

		var newRow = "<tr class=\"" + newRowClass + "\">";

		newRow += "<td></td>"; // Radio button
		newRow += "<td>" + code + "</td>"; // Code
		newRow += "<td>" + descriptionEng + "</td>"; // Eng Desc
		newRow += "<td>" + descriptionFra + "</td>"; // Fra Desc
		
		if ($("#attributeViewType").val() == 'reference') {
			newRow += "<td style='text-align:center;'>" + mandatorySelected + "</td>"; // Column Five - Mandatory
		} else {
			newRow += "<td style=\"" + colFive + "\"></td>"; // Column Five - Mandatory
		}
		
		if ($("#attributeViewType").val() == 'reference') {
			newRow += "<td></td>"; // Column Six - Notes
		} else {
			newRow += "<td style=\"" + colSix + "\"></td>"; // Column Six - Notes	
		}
			    
		newRow += "<td style='text-align:center;'>" + statusSelected + "</td>"; // Column Seven - Status
		
		if ($("#attributeViewType").val() == 'reference') {
			newRow += "<td></td>"; // Column Eight - Associated Generic Attributes
		} else {
			newRow += "<td style=\"" + colEight + "\"></td>"; // Column Eight - Associated Generic Attributes
		}
		
		newRow += "</tr>";

		tbl.append(newRow);
		changeEditMode(true);
		
		var lastRowOffSet = iframe.contents().find('#attributeTable tr:last').offset().top;
		iframe.contents().scrollTop(lastRowOffSet);			
	}
	
	/*********************************************************************************************************
	 * NAME:          Change Edit Mode
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function changeEditMode(isEdit) {

		editMode = isEdit;
		var iframe = getSelectedIFrame();

		$("#editMode").html(isEdit.toString());

		iframe.contents().find('input[name=radioSelection]').attr('disabled', isEdit);

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
			iframe.contents().find(".tableHeader a").bind('click', function(e){ e.preventDefault();});
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
			iframe.contents().find(".tableHeader a").unbind('click');
		}

		
		//hideMessage();
	}
	
	/*********************************************************************************************************
	 * NAME:          Cancel Edit     
	 * DESCRIPTION:   Revert the row back to non edit mode, and replace back to before
	 *********************************************************************************************************/
	function cancelEdit() {
	
		if (addMode == true) {
			cancelAdd();
			return false;
		}

		var yesLabel = "<fmt:message key='common.label.yes'/>";
		var noLabel = "<fmt:message key='common.label.no'/>";
		var iframe = getSelectedIFrame();
		var tableRow = iframe.contents().find('input[name=radioSelection]:checked').parent().parent();

		var descriptionEng = tableRow.children("td:nth-child(3)");
		var descriptionFra = tableRow.children("td:nth-child(4)");
		var mandatory = tableRow.children("td:nth-child(5)");
		var status = tableRow.children("td:nth-child(7)");
		
		descriptionEng.html(glb_de);
		descriptionFra.html(glb_df);
		mandatory.html(glb_ma == 'Y' ? yesLabel : noLabel);
		status.html(glb_st.substring(0, 1).toUpperCase() + glb_st.substring(1).toLowerCase());

		changeEditMode(false);
	}

	/*********************************************************************************************************
	 * NAME:          Cancel Add 
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function cancelAdd() {

		var iframe = getSelectedIFrame();
		iframe.contents().find('#attributeTable tr:last').remove();
		addMode = false;
		changeEditMode(false);
	}

	/*********************************************************************************************************
	 * NAME:          Reset Changes    
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function resetChanges() {
		var iframe = getSelectedIFrame();

		iframe.contents().find('#tmpDE').val(glb_de);
		iframe.contents().find('#tmpDF').val(glb_df);
		iframe.contents().find('#tmpMA').val(glb_ma);
		iframe.contents().find('#tmpS').val(glb_st);
	}

	/*********************************************************************************************************
	 * NAME:          Collect Form Data    
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function collectFormData(fields) {
		var data = {};
		for ( var i = 0; i < fields.length; i++) {
			var $item = $(fields[i]);
			data[$item.attr('name')] = $item.val();
		}
		return data;
	}
	
	/*********************************************************************************************************
	 * NAME:          Save New Attribute				          
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function saveNewAttribute() {

		showInfoMessage('Saving');
		showProcessingScreen();

		var iframe = getSelectedIFrame();

		var data = {};
		data['code'] = iframe.contents().find('#tmpCode').val();
		data['descriptionEng'] = iframe.contents().find('#tmpDE').val();
		data['descriptionFra'] = iframe.contents().find('#tmpDF').val();
		data['status'] = iframe.contents().find('#tmpS').val();
		
		if ($("#attributeViewType").val() == 'reference') {
			data['mandatory'] = iframe.contents().find('#tmpMA').val();
		}		

		attributeCodeToDisplay = iframe.contents().find('#tmpCode').val();
		
		var postPage = "<c:url value='/" + $("#attributeViewType").val() + "Attributes.htm?'/>";

		$.post(postPage, data, function(response) {

			if (response.status == 'FAIL') {
				hideMessage();
				hideProcessingScreen();
				showErrorMessagesFromResponse(response);
			} else {
				hideMessage();
				addMode = false;				
				attributeAdded = true;
				setTimeout("reloadIFrame();", 200);				
			}
		}, 'json');
	}
	
	function reloadIFrame() {
		var iframe = getSelectedIFrame();
		iframe[0].contentWindow.location.reload(true);
	}
	
	/*********************************************************************************************************
	 * NAME:          Load Attribute				          
	 * DESCRIPTION:   Replaces iFrame src with a page.  The page depends on whether Generic or Reference 
	 *				  attribute was selected
	 *********************************************************************************************************/
	function loadAttribute() {
				
		var iframe = getSelectedIFrame();
		var ensureSelected = ensureClassificationIsSelected();
		var attributeViewType = $("#attributeViewType").val();
		var pageToLoad = attributeViewType + "Attributes.htm";
		
		if (ensureSelected == false) {
			return false;
		}

		iframe.attr("src", pageToLoad);		
	}	
	
	function ensureClassificationIsSelected() {
		var ensure = $("#vc").text();

		if (ensure.length == 0) {
			return false;
		}
	}
	
	function getSelectedIFrame() {
		var iframe = $("#attributeiFrame");
		return iframe;
	}
	
	function ensureRadioButtonSelected(iframe) {
		var radioId = iframe.contents().find('input[name=radioSelection]:checked').attr('id');
		if (typeof radioId === 'undefined') {
			console.log('No radio button selected');
			return true;
		}
	}
	
	/*********************************************************************************************************
	 * NAME:          Remove Attribute	
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function removeAttribute() {

		var ensureSelected = ensureClassificationIsSelected();
		var iframe = getSelectedIFrame();

		if ((iframe == false) || (ensureSelected == false) || (superEditMode === false)) {
			console.log('Nothing to remove');
			hideMessage();
			return false;
		}

		var radioId = iframe.contents().find('input[name=radioSelection]:checked').attr('id');
		if (typeof radioId === 'undefined') {
			console.log('No radio button selected');
			return false;
		}

		var tableRow = iframe.contents().find('input[name=radioSelection]:checked').parent().parent();
		var code = tableRow.children("td:nth-child(2)");
		code = $.trim(code.text());	
		attributeCodeToDisplay = code;
		
		var postPage = "<c:url value='/" + $("#attributeViewType").val() + "Attributes/" + radioId + ".htm'/>";
		
		$.ajax({
			'url' : postPage,
			'type' : 'DELETE',
			'success' : function(response) {
	
				if (response.status == 'FAIL') {
					hideProcessingScreen();
					hideMessage();
					showErrorMessagesFromResponse(response);					
				} else {
					attributeRemoved = true;
					setTimeout("reloadIFrame();", 200);
				}
			},
			beforeSend : function() {
				showInfoMessage('Removing');
				showProcessingScreen();
			}
		});		
		
	}
	
	function printFrame() {
        var frm = getSelectedIFrame();
        var ensureSelected = ensureClassificationIsSelected();
		var attributeViewType = $("#attributeViewType").val();
		//console.log(attributeViewType);
		var pageToLoad = attributeViewType + "Attributes.htm?print=Y";
		//console.log(pageToLoad);
		
		if (ensureSelected == false) {
			console.log('Attribute not ready yet');
			hideMessage();
			return false;
		}

		var newwindow=window.open(pageToLoad, 'Print CCI Attributes', "width=1050,height=750,resizable=yes,scrollbars=yes ");
        if (window.focus)  {
			  newwindow.focus();
		  }
	}

</script>

<security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR')">
<h4 class="contentTitle"><fmt:message key="cims.menu.administration" /> &#62; <fmt:message key="cims.menu.admin.sub.manage.cci.attributes" /></h4>
</security:authorize> 

<security:authorize access="!hasAnyRole('ROLE_ADMINISTRATOR')">
<h4 class="contentTitle"><fmt:message key="cims.menu.administration" /> &#62; <fmt:message key="cims.menu.admin.sub.view.cci.attributes" /></h4>
</security:authorize> 

<div class="content">
	<form:form method="POST" id="classificationSelector">

		<label id="editMode" style="display: none;"></label>
		<label id="vcDefault" style="display: none;">${vcDefault}</label>
		<label id="vc" style="display: none;">${cciAttributesForViewer.versionCode}</label>
		<label id="isContextFrozen" style="display: none;">${cciAttributesForViewer.contextFrozen}</label>

		<c:forEach var="vcOpen" items="${readOnly}">
			<label id="vcOpen${vcOpen.key}" style="display: none;">${vcOpen.value}</label>	
		</c:forEach>
		
		<table style="border: none;">
			<tr>
				<td style="width: 10%">
					<label>Classification:</label>&nbsp;&nbsp;
					<form:select id="baseClassification" path="baseClassification">
						<c:forEach var="baseClassification" items="${baseClassifications}">
							<form:option value="${baseClassification}">
								${baseClassification}</form:option>
						</c:forEach>
					</form:select></td>
				<td style="width: 10%">
					<label>Year:</label>&nbsp;&nbsp;
					<form:select id="versionCode" path="versionCode">
						<c:forEach var="versionCode" items="${versionCodes}">
							<form:option value="${versionCode}">${versionCode}</form:option>
						</c:forEach>
					</form:select>
				</td>
				<td style="width: 20%">
					<label>Attribute type:</label>&nbsp;&nbsp;
					<form:select id="attributeType" path="attributeType">
						<c:forEach var="attributeType" items="${attributeTypes}">
							<form:option value="${attributeType.key}">${attributeType.value}</form:option>
						</c:forEach>
					</form:select>
				</td>				
				<td style="width: 15%">
					<label>View:</label>&nbsp;&nbsp;
					<form:select id="attributeViewType" path="attributeViewType">
						<c:forEach var="attributeViewType" items="${attributeViewTypes}">
							<form:option value="${attributeViewType.key}">${attributeViewType.value}</form:option>
						</c:forEach>
					</form:select>
				</td>				
				<td style="width: 10%"><label>Status:</label>&nbsp;&nbsp;				
					<form:select id="status" path="status">
						<c:forEach var="status" items="${status}">
							<form:option value="${status}">${status}</form:option>
						</c:forEach>
					</form:select>
				</td>
				<td style="width: 10%">
					<input id="viewButton" class="button" type="button" 
						value="<fmt:message key='cims.icd10.conceptViewer.submitButton'/>" />
				</td>
			</tr>
		</table>

	</form:form>
</div>

<div class="content">
	<div id="contextFrozen" class="notice" style="display: none; margin-bottom: 0.9em; /*width: 95%; margin-left: 0.9em*/"></div>
</div>

<div class="icons">
	<ul style="padding-left:.9em; ">
		<li style="float: left; list-style-type: none; ">
			<div id="loadingInfo" class="info" style="display: none; margin-bottom: 0.1em; width: 900px; padding-top: 0.5em;padding-bottom: 0.5em;">Loading</div>
		</li>	
	
		<li id="iconsLI" style="float: right; top: 0px; border: 0px; background: #ffffff; list-style-type: none; display: none;">				
			<security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR')">			
				<img id="save" class="editMode" title="Save" src="img/icons/Save.png" style="display: none;" /> 
				<img id="cancel" class="editMode" title="Cancel" src="img/icons/Cancel.png" onclick="cancelEdit();" style="display: none;" /> 
				<img id="reset" class="editMode" title="Reset" src="img/icons/Reset.png" onclick="resetChanges();" style="display: none;" /> 
				<img id="edit" class="viewMode" title="Edit" src="img/icons/EditGrey.png" onclick="editAttribute();" /> 
				<img id="add" class="viewMode" title="Add" src="img/icons/AddGrey.png" onclick="addAttribute();" /> 
				<img id="remove" class="viewMode" title="Remove" src="img/icons/RemoveGrey.png" />						
			</security:authorize> 						
			<img id="print" class="viewMode" title="Print" src="img/icons/Print.png" onclick="printFrame();" /> 
			<img id="reference" class="viewMode" title="Reference Link" src="img/icons/BookGrey.png" onclick="referenceAlert();" />
			
		</li>
	</ul>
</div>
<div id="attributesDiv">
	<iframe id="attributeiFrame" 
		src="about:blank" width="100%" onload="resizeIframe(this)" style="height: 550px;"> </iframe>
</div>

<div id="removalConfirmation" style="display: none;">
	test
</div>

<div id="inContextDialog" style="display:none; overflow:hidden;" title="View/Modify List">
	<iframe name="inContextIframe" id="inContextIframe"						
		src="about:blank" width="100%" height="100%"></iframe>
</div>

<script>

	/*********************************************************************************************************
	* NAME:          Remove Attribute
	* DESCRIPTION:   
	**********************************************************************************************************/
	$("#remove").click(function() {
	  if (superEditMode) {
		var ensureSelected = ensureClassificationIsSelected();
		var iframe = getSelectedIFrame();

		if ((iframe == false) || (ensureSelected == false) || (ensureRadioButtonSelected(iframe))) {
			hideMessage();
			hideProcessingScreen();

			return false;
		}
		
		$("#removalConfirmation").text('Please confirm that you want to permanently remove the selected ' + 
				$("#attributeViewType option:selected").text() + ' from the system.');
	
		$('#removalConfirmation').dialog({
			title : 'Confirmation: Removal of Attribute',
			width : 350, height : 150, modal : true, resizable : false, draggable : false,
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
	**********************************************************************************************************/
	$("#save").click(function() {
	
		if (addMode == true) {
			saveNewAttribute();
			return false;
		}
	
		var iframe = getSelectedIFrame();
		
		var data = {};
		data['code'] = glb_cod;
		data['descriptionEng'] = $.trim(iframe.contents().find('#tmpDE').val());
		data['descriptionFra'] = $.trim(iframe.contents().find('#tmpDF').val());
		data['status'] = $.trim(iframe.contents().find('#tmpS').val());		
		data['elementId'] = iframe.contents().find('input[name=radioSelection]:checked').attr('id');
		
		var attributeId = iframe.contents().find('input[name=radioSelection]:checked').attr('id');
		
		if ($("#attributeViewType").val() == 'reference') {
			data['mandatory'] = $.trim(iframe.contents().find('#tmpMA').val());
		}		
		
		var postPage = "<c:url value='/" + $("#attributeViewType").val() + "Attributes/" + attributeId + ".htm'/>";
		
		$.ajax({
			'url' : postPage,
			'type' : 'POST',
			'data' : data,
			'success' : function(response) {
	
				if (response.status == 'FAIL') {	
					hideProcessingScreen();					
					showErrorMessagesFromResponse(response);
				} else {
					hideMessage();										
					hideProcessingScreen();
					glb_de = $.trim(iframe.contents().find('#tmpDE').val());
					glb_df = $.trim(iframe.contents().find('#tmpDF').val());
					glb_ma = $.trim(iframe.contents().find('#tmpMA').val());
					glb_st = $.trim(iframe.contents().find('#tmpS').val());

					cancelEdit();
					showSuccessMessage(glb_cod + " successfully updated");
				}
			},
			beforeSend : function() {
				showInfoMessage('Saving');
				showProcessingScreen();
			}
		});
	
	});

	/*********************************************************************************************************
	 * NAME:          View Button
	 * DESCRIPTION:   When the user clicks the view button 
	 *********************************************************************************************************/
	$("#viewButton").click(function() {
		$("#vc").text($("#versionCode").val());
		
		var data = {};
		data['baseClassification'] = $("#baseClassification").val();
		data['versionCode'] = $("#versionCode").val();
		data['attributeType'] = $("#attributeType").val();
		data['attributeViewType'] = $("#attributeViewType").val();
		data['status'] = $("#status").val();
		data['contextFrozen'] = $("#isContextFrozen").text();
		
		$.ajax({
			'url' : "<c:url value='/cciAttributes.htm'/>",
			'type' : 'POST',
			'data' : data,
			'success' : function(response) {
	
				if (response.status == 'FAIL') {
					hideProcessingScreen();
					showErrorMessagesFromResponse(response);
				} else {
					//console.log(response);
					$("#isContextFrozen").text(response.contextFrozen);
					contextFrozenMessage(response.contextFrozen);	
					setSuperEditMode();
					loadAttribute();
				}
			},
			beforeSend : function() {
				showInfoMessage('Loading');
				showProcessingScreen();
			}
		});
		
	});	
	
	$("#inContextDialog").dialog({	
	    position: { my: "center", at: "center", of: window } ,
	    title: 'Associated Generic Attributes',
	    draggable: false, width : 1300, height : 800, autoOpen: false, modal : true,
	    open: function( event, ui ) {
	    	//console.log('I am starting to open, and will call showProcessingScreen()');
	    	//showProcessingScreen();
	    },	    
	    close: function( event, ui ) {
	    	var iframe = $("#inContextIframe");
			iframe.attr("src", "");
	    }
	});		
</script>
