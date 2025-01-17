<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<script type="text/javascript">
	var glb_cod;
	var glb_sde;
	var glb_sdf;
	var glb_ldf;
	var glb_ldf;
	var glb_st;
	var editMode = false;
	var superEditMode = false;
	var addMode = false;			// Page is in ADD mode
	var componentAdded = false;  	// Used to tell iFrame that a component has been created.  Shows message after reload
	var componentRemoved = false;	// Used to tell iFrame that a component has been removed.  Shows message after reload
	var componentCodeToDisplay;  	// Used to tell iFrame which code was added/removed.  Shows in message
	
	var definitionNewXMLENG = "";	// Holds the XML from the dialog when adding a new Intervention component
	var definitionNewXMLFRA = "";	// Holds the XML from the dialog when adding a new Intervention component

	var processedMessage = {};
	processedMessage["class"] = '';
	processedMessage["message"] = '';
	
	$(document).ready(function() {
		$('#versionCode').val($("#vcDefault").text());
	});

	function setSuperEditMode() {	
		var currentVC = $('#versionCode').val();
		var isContextFrozen = $("#isContextFrozen").text();
		
		superEditMode = ( ($('#vcOpen' + currentVC).text() === "true") && (isContextFrozen === "false") );
		$("#add").attr('src', (superEditMode === true) ? "img/icons/Add.png" : "img/icons/AddGrey.png");	
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
// 		processedMessage["image"] = "<img src=\"img/icons/Error.png\"/>";
		
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
	
	/*********************************************************************************************************
	 * NAME:          Call Section List
	 * DESCRIPTION:   Gets executued once a version code is selected
	 *********************************************************************************************************/
	function callSectionList() {

		$("#vc").text('');
		 
		$.ajax({
			'url' : "<c:url value='/sections.htm'/>",
			'type' : 'GET',
			'data' : {
				versionCode : $("#versionCode").val()
			},
			'success' : function(data) {

				if (data.status == 'FAIL') {
					showErrorMessagesFromResponse(data);
				} else {
					hideMessage();

					//$("#section").empty();
					$.each(data, function(key, item) {
						var optionvalue = '<option value="' + key +'" >' + item + '</option>';
						$("#section").append(optionvalue);
					});

					greyButtons();
					$("#add").attr('src', 'img/icons/AddGrey.png');  
					
					//setSuperEditMode();
					hideProcessingScreen();
				}
			},

			beforeSend : function() {
				$("#section").empty();
				showProcessingScreen();
				var iframe = getSelectedIFrame();
		 	    iframe.attr("src", "");				
			}
		});
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
		
		var tabName = $(".ui-tabs-selected a").text();

		var postPage = "<c:url value='/listComponentReferences.htm?'/>";
		
		window.open(postPage + "e=" + radioId + "&tabName=" + tabName, 'Reference Links - List of Associated Code Values', 
				'resizable=yes, scrollbars=yes, toolbar=no ,location=0, status=no, titlebar=no, menubar=no, width=1100, height=500');	
	}

	/*********************************************************************************************************
	 * NAME:          Edit Component
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function editComponent() {

		var iframe = getSelectedIFrame();
		
		if ((iframe == false) || (superEditMode === false)) {
			hideMessage();
			hideProcessingScreen();
			return false;
		}

		var radioId = iframe.contents().find('input[name=radioSelection]:checked').attr('id');
		if (typeof radioId === 'undefined') {
			console.log('No radio button selected');
			return false;
		}

		var tableRow = iframe.contents().find('input[name=radioSelection]:checked').parent().parent();
		var code = tableRow.children("td:nth-child(2)");
		var shortDescriptionEng = tableRow.children("td:nth-child(3)");
		var shortDescriptionFra = tableRow.children("td:nth-child(4)");
		var longDescriptionEng = tableRow.children("td:nth-child(5)");
		var longDescriptionFra = tableRow.children("td:nth-child(6)");
		var status = tableRow.children("td:nth-child(8)");
		var isNewlyCreated = iframe.contents().find('#' + radioId + '_isNewlyCreated');
		
		glb_cod = $.trim(code.text());		
		glb_sde = $.trim(shortDescriptionEng.text());
		glb_sdf = $.trim(shortDescriptionFra.text());
		glb_lde = $.trim(longDescriptionEng.text());
		glb_ldf = $.trim(longDescriptionFra.text());		
		glb_st = $.trim(status.text().toUpperCase());

		shortDescriptionEng.html("<input type='text' id='tmpSDE' maxlength='50'/>");
		shortDescriptionFra.html("<input type='text' id='tmpSDF' maxlength='50'/>");

		longDescriptionEng.html("<input type='text' id='tmpLDE' maxlength='255'/>");
		longDescriptionFra.html("<input type='text' id='tmpLDF' maxlength='255'/>");
		
		iframe.contents().find('#tmpSDE').val(glb_sde);
		iframe.contents().find('#tmpSDF').val(glb_sdf);
		iframe.contents().find('#tmpLDE').val(glb_lde);
		iframe.contents().find('#tmpLDF').val(glb_ldf);		

		var statusSelected = "<select id='tmpS' name='tmpS'>";		
		if ( (glb_st != 'ACTIVE') || (isNewlyCreated.text() == 'true') )  {
			statusSelected += "<option value='ACTIVE'>Active</option>";			
		} else {
			statusSelected += "<option value='ACTIVE' SELECTED>Active</option><option value='DISABLED'>Disabled</option>";			
		}		
	
		statusSelected += "</select>";
		status.html(statusSelected);
		changeEditMode(true);
	}

	/*********************************************************************************************************
	 * NAME:          Add new table row
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function addComponent() {

		var iframe = getSelectedIFrame();
		var componentModelType = iframe.contents().find('#componentModelType').val();
		
		if ((iframe == false) || (superEditMode === false)) {
			console.log('Component not ready yet');
			hideMessage();
			hideProcessingScreen();
			return false;
		}

		if (addMode == true) {
			console.log("Add has already been clicked.  No More!");
			return false;
		}

		addMode = true;

		var tbl = iframe.contents().find('#componentTable');
		var tableRow = iframe.contents().find('#componentTable tr:last');
		var lastRowClass = tableRow.attr('class');
		var diagramStyle = tableRow.children("td:nth-child(7)").attr('style');
		var newRowClass = (lastRowClass == 'odd') ? 'even' : 'odd';

		glb_sde = "";
		glb_sdf = "";
		glb_lde = "";
		glb_ldf = "";
		glb_st = "ACTIVE";

		var code = "<input type='text' id='tmpCode' value='' style='text-transform:uppercase; width:40px;' onblur='this.value=this.value.toUpperCase()'/>";
		var shortDescriptionEng = "<input type='text' id='tmpSDE' value='' maxlength='50'/>";
		var shortDescriptionFra = "<input type='text' id='tmpSDF' value='' maxlength='50'/>";

		var longDescriptionEng = "<input type='text' id='tmpLDE' value='' maxlength='255'/>";
		var longDescriptionFra = "<input type='text' id='tmpLDF' value='' maxlength='255'/>";
		var definition = "<img title=\"Definition\" src=\"img/icons/Note.png\" onclick=\"addNewDefinition('hi there');\"/>";
		
		if (componentModelType != "intervention") {
			definition = "";
		}
		
		var statusSelected = "<select id='tmpS'><option value='ACTIVE' SELECTED>Active</option></select>";

		var newRow = "<tr class=\"" + newRowClass + "\">";

		newRow += "<td></td>"; // Radio button
		newRow += "<td>" + code + "</td>"; // Code
		newRow += "<td>" + shortDescriptionEng + "</td>"; // Eng Short Desc
		newRow += "<td>" + shortDescriptionFra + "</td>"; // Fra Short Desc
		newRow += "<td>" + longDescriptionEng + "</td>";
		newRow += "<td>" + longDescriptionFra + "</td>";
		newRow += "<td style=\"" + diagramStyle + "\">" + definition + "</td>"; // Diagram	    
		newRow += "<td style='text-align:center;'>" + statusSelected + "</td>"; // Status
		newRow += "</tr>";

		tbl.append(newRow);
		changeEditMode(true);
		
		var lastRowOffSet = iframe.contents().find('#componentTable tr:last').offset().top;
		iframe.contents().scrollTop(lastRowOffSet);				
	}

	/*********************************************************************************************************
	 * NAME:          Change Edit Mode
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function changeEditMode(isEdit) {

	    if (superEditMode === false) {
	    	console.log("Cant change edit mode, its not open.");
	    	return;
	    }
		 
		editMode = isEdit;
		var iframe = getSelectedIFrame();

		$("#editMode").html(isEdit.toString());

		iframe.contents().find('input[name=radioSelection]').attr('disabled', isEdit);

		if (isEdit) {
			$(".editMode").show();
			$(".viewMode").hide();
			$(".appTabPanel").tabs({ disabled : [ 0, 1, 2, 3, 4 ] });
			$("#viewButton").attr('disabled', true);
			$("#viewButton").addClass('ui-state-disabled');
			$("#versionCode").attr('disabled', true);			
			$("#baseClassification").attr('disabled', true);
			$("#section").attr('disabled', true);
			$("#status").attr('disabled', true);
			iframe.contents().find(".tableHeader a").bind('click', function(e){ e.preventDefault();});
		} else {
			$(".editMode").hide();
			$(".viewMode").show();
			$(".appTabPanel").tabs({ disabled : false });
			$("#viewButton").removeAttr('disabled');
			$("#viewButton").removeClass('ui-state-disabled');
			$("#versionCode").removeAttr('disabled');			
			$("#baseClassification").removeAttr('disabled');
			$("#section").removeAttr('disabled');
			$("#status").removeAttr('disabled');
			iframe.contents().find(".tableHeader a").unbind('click');
		}

		hideMessage();
	}

	/*********************************************************************************************************
	 * NAME:          Cancel Edit     
	 * DESCRIPTION:   Revert the row back to non edit mode, and replace back to before
	 *********************************************************************************************************/
	function cancelEdit() {

		// Remove any Intervention definitions which may have been set during Intervention Add Component		 
		definitionNewXMLENG = "";	
		definitionNewXMLFRA = "";	
		
		if (addMode == true) {
			cancelAdd();
			return false;
		}

		var iframe = getSelectedIFrame();
		var tableRow = iframe.contents().find('input[name=radioSelection]:checked').parent().parent();

		var shortDescriptionEng = tableRow.children("td:nth-child(3)");
		var shortDescriptionFra = tableRow.children("td:nth-child(4)");
		var longDescriptionEng = tableRow.children("td:nth-child(5)");
		var longDescriptionFra = tableRow.children("td:nth-child(6)");
		var status = tableRow.children("td:nth-child(8)");

		shortDescriptionEng.html(glb_sde);
		shortDescriptionFra.html(glb_sdf);
		longDescriptionEng.html(glb_lde);
		longDescriptionFra.html(glb_ldf);
		status.html(glb_st.substring(0, 1).toUpperCase() + glb_st.substring(1).toLowerCase());

		changeEditMode(false);
	}

	/*********************************************************************************************************
	 * NAME:          Cancel Add 
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function cancelAdd() {

		var iframe = getSelectedIFrame();
		iframe.contents().find('#componentTable tr:last').remove();
		addMode = false;
		changeEditMode(false);
	}

	/*********************************************************************************************************
	 * NAME:          Reset Changes    
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function resetChanges() {

		var iframe = getSelectedIFrame();

		iframe.contents().find('#tmpSDE').val(glb_sde);
		iframe.contents().find('#tmpSDF').val(glb_sdf);
		iframe.contents().find('#tmpLDE').val(glb_lde);
		iframe.contents().find('#tmpLDF').val(glb_ldf);
		iframe.contents().find('#tmpS').val(glb_st);
	}

	/*********************************************************************************************************
	* NAME:          Save New Component				          
	* DESCRIPTION:    
	**********************************************************************************************************/
	function saveNewComponent() {
		
		var iframe = getSelectedIFrame();		
		var componentModelType = iframe.contents().find('#componentModelType').val();
		
		showInfoMessage('Saving');
		showProcessingScreen();

		var data = {};
		data['code'] = iframe.contents().find('#tmpCode').val();
		data['shortDescriptionEng'] = iframe.contents().find('#tmpSDE').val();
		data['shortDescriptionFra'] = iframe.contents().find('#tmpSDF').val();
		data['longDescriptionEng'] = iframe.contents().find('#tmpLDE').val();
		data['longDescriptionFra'] = iframe.contents().find('#tmpLDF').val();
		data['status'] = iframe.contents().find('#tmpS').val();
		data['componentModelType'] = componentModelType;

		if (componentModelType == "intervention") {
	 		data['de'] = definitionNewXMLENG;
	 		data['df'] = definitionNewXMLFRA;
		}

		componentCodeToDisplay = iframe.contents().find('#tmpCode').val();
		$.post("<c:url value='/saveNewComponent.htm'/>", data, function(response) {

			if (response.status == 'FAIL') {
				hideMessage();
				hideProcessingScreen();
				showErrorMessagesFromResponse(response);
			} else {
				hideMessage();
				addMode = false;
				componentAdded = true;		

				// Remove any Intervention definitions which may have been set during Intervention Add Component		 
				definitionNewXMLENG = "";	
				definitionNewXMLFRA = "";	
				
				setTimeout("reloadIFrame();", 200);
			}
		}, 'json');
	}

	function reloadIFrame() {		
		var iframe = getSelectedIFrame();
		iframe[0].contentWindow.location.reload(true);
	}

	/*********************************************************************************************************
	 * NAME:          Load Component				          
	 * DESCRIPTION:   Detects the selected iFrame and replaces the src with data-src.  This causes the 
	 *				  page to load
	 *********************************************************************************************************/
	function loadComponent() {

		var ensureSelected = ensureClassificationIsSelected();
		var iframe = getSelectedIFrame();

		if ((iframe == false) || (ensureSelected == false)) {
			console.log('Component not ready yet');
			hideMessage();
			hideProcessingScreen();
			return false;
		}

		iframe.attr("src", iframe.data("src"));		
	}
	
	function ensureClassificationIsSelected() {
		var ensure = $("#vc").text();

		if (ensure.length == 0) {
			return false;
		}		
	}
	
	function getSelectedIFrame() {
		var tabId = $("#tabSelected").text();
		var iframe = $("#comp" + tabId);
		return iframe;
	}

	/*********************************************************************************************************
	 * NAME:          Ensure Radio Button Selected
	 * DESCRIPTION:   Boolean function checks if button is clicked
	 *********************************************************************************************************/	
	function ensureRadioButtonSelected(iframe) {
		var radioId = iframe.contents().find('input[name=radioSelection]:checked').attr('id');
		return(typeof radioId === 'undefined');
	}

	/*********************************************************************************************************
	 * NAME:          Remove Component	
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	function removeComponent() {

		var iframe = getSelectedIFrame();

		if ((iframe == false) || (superEditMode === false)) {
			console.log('Nothing to remove');
			hideMessage();
			hideProcessingScreen();
			return false;
		}
		
		hideMessage();
		showInfoMessage('Removing');
		showProcessingScreen();
		
		var radioId = iframe.contents().find('input[name=radioSelection]:checked').attr('id');
		if (typeof radioId === 'undefined') {
			console.log('No radio button selected');
			return false;
		}
		
		var tableRow = iframe.contents().find('input[name=radioSelection]:checked').parent().parent();
		var code = tableRow.children("td:nth-child(2)");		
		code = $.trim(code.text());	
		componentCodeToDisplay = code;
		
		$.post("<c:url value='/removeComponent.htm'/>", {
			elementIdToRemove : radioId,
			componentCode : code
		}, function(response) {

			if (response.status == 'FAIL') {
				hideMessage();
				hideProcessingScreen();
				showErrorMessagesFromResponse(response);
			} else {
				componentRemoved = true;				
				setTimeout("reloadIFrame();", 200);
			}
		}, 'json');
	}

	/*********************************************************************************************************
	* NAME:          Show Post Success Message	
	* DESCRIPTION:   
	**********************************************************************************************************/	
	function showPostSuccessMessage() {
		
		if (componentAdded == true) {
			componentAdded = false;
			
			showSuccessMessage("Component " + window.parent.componentCodeToDisplay + " successfully added");			
			componentCodeToDisplay = '';
		}

		if (componentRemoved == true) {
			componentRemoved = false;
			showSuccessMessage("Component " + window.parent.componentCodeToDisplay + " successfully removed");
			componentCodeToDisplay = '';
		}		
	}	
	
	/*********************************************************************************************************
	* NAME:          Print Frame	
	* DESCRIPTION:   
	**********************************************************************************************************/	
	function printFrame() {
        var frm = getSelectedIFrame();
        if (editMode === true) {
			console.log('Cannot perform while in edit mode');
			return false;
		}
        var src=frm.data('src')+"?print=Y";
        var newwindow=window.open(src, 'Print CCI Componets', "width=1050,height=750,resizable=yes,scrollbars=yes ");
        if (window.focus)  {
			  newwindow.focus();
		  }
	}
</script>

<security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR')">
<h4 class="contentTitle"><fmt:message key="cims.menu.administration" /> &#62; <fmt:message key="cims.menu.admin.sub.manage.cci.components" /></h4>
</security:authorize> 

<security:authorize access="!hasAnyRole('ROLE_ADMINISTRATOR')">
<h4 class="contentTitle"><fmt:message key="cims.menu.administration" /> &#62; <fmt:message key="cims.menu.admin.sub.view.cci.components" /></h4>						
</security:authorize> 

<div class="content" style="padding-bottom: 0px; ">
	<form:form method="POST" id="classificationSelector">

		<label id="editMode" style="display: none;"></label>
		<label id="vcDefault" style="display: none;">${vcDefault}</label>
		<label id="tabSelected" style="display: none;">0</label>
		<label id="vc" style="display: none;">${cciComponentsForViewer.versionCode}</label>
		<label id="isContextFrozen" style="display: none;">${cciComponentsForViewer.contextFrozen}</label>

		<c:forEach var="vcOpen" items="${readOnly}">
			<label id="vcOpen${vcOpen.key}" style="display: none;">${vcOpen.value}</label>	
		</c:forEach>

		<table style="border: none;">
			<tr>
				<td style="width: 15%">
					<label>Classification:</label>&nbsp;&nbsp;
					<form:select id="baseClassification" path="baseClassification">
						<c:forEach var="baseClassification" items="${baseClassifications}">
							<form:option value="${baseClassification}">
								${baseClassification}</form:option>
						</c:forEach>
					</form:select>
				</td>
				<td style="width: 15%">
					<label>Year:</label>&nbsp;&nbsp;				
					<form:select id="versionCode" path="versionCode" onchange="javascript:callSectionList();">
						<c:forEach var="versionCode" items="${versionCodes}">
							<form:option value="${versionCode}">${versionCode}</form:option>
						</c:forEach>
					</form:select>
				</td>
				<td style="width: 35%">
					<label>Section:</label>&nbsp;&nbsp;				
					<form:select id="section" path="section">
						<c:forEach var="section" items="${sections}">
							<form:option value="${section.key}">${section.value}</form:option>
						</c:forEach>
					</form:select>
				</td>
				<td style="width: 15%">
					<label>Status:</label>&nbsp;&nbsp;
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
				<td style="width: 10%">&nbsp;</td>
			</tr>
		</table>
	</form:form>
</div>

<div class="content">
	<div id="contextFrozen" class="notice" style="display: none; margin-bottom: 0.9em; /*width: 95%; margin-left: 0.9em*/"></div>
</div>

<div style="width: 100%; overflow: hidden;">
    <div style="float: left;">
    	<div id="loadingInfo" class="info" style="display: none; width: 800px; padding-top: 0.5em;padding-bottom: 0.5em;">Loading</div>
    </div>
    <div style="float: right;">&nbsp;</div>
</div>

<div class="appTabPanel">
	<ul>
		<li style="height: 34px;"><a id="t0" href="#tab0">Group</a></li>
		<li style="height: 34px;"><a id="t1" href="#tab1">Intervention</a></li>
		<li style="height: 34px;"><a id="t2" href="#tab2">Approach/Technique (Qualifier 1)</a></li>
		<li style="height: 34px;"><a id="t3" href="#tab3">Device/Agent (Qualifier 2)</a></li>
		<li style="height: 34px;"><a id="t4" href="#tab4">Tissue (Qualifier 3)</a></li>
		<li style="float: right; top: 0px; border: 0px; background: #ffffff;">				
			<security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR')">			
				<img id="save" class="editMode" title="Save" src="img/icons/Save.png" style="display: none;" /> 
				<img id="cancel" class="editMode" title="Cancel" src="img/icons/Cancel.png" onclick="cancelEdit();" style="display: none;" /> 
				<img id="reset" class="editMode" title="Reset" src="img/icons/Reset.png" onclick="resetChanges();" style="display: none;" /> 
				<img id="edit" class="viewMode" title="Edit" src="img/icons/EditGrey.png" onclick="editComponent();" /> 
				<img id="add" class="viewMode" title="Add" src="img/icons/AddGrey.png" onclick="addComponent();" /> 
				<img id="remove" class="viewMode" title="Remove" src="img/icons/RemoveGrey.png" />						
			</security:authorize>					
			<img id="print" class="viewMode" title="Print" src="img/icons/Print.png" onclick="printFrame();" /> 
			<img id="reference" class="viewMode" title="Reference Link" src="img/icons/BookGrey.png" onclick="referenceAlert();" />
			
		</li>
	</ul>
	<div id="tab0">
		<iframe id="comp0" data-src="groupComponents.htm" width="100%" style="height: 550px;"> </iframe>
	</div>
	<div id="tab1">
		<iframe id="comp1" data-src="intComponents.htm" width="100%" style="height: 550px;"> </iframe>
	</div>
	<div id="tab2">
		<iframe id="comp2" data-src="appTechComponents.htm" width="100%" style="height: 550px;"> </iframe>
	</div>
	<div id="tab3">
		<iframe id="comp3" data-src="daComponents.htm" width="100%" style="height: 550px;"> </iframe>
	</div>
	<div id="tab4">
		<iframe id="comp4" data-src="tissueComponents.htm" width="100%" style="height: 550px;"> </iframe>
	</div>
</div>

<div id="removalConfirmation" style="display: none;"></div>

<script>
	(function($) {
		$(".appTabPanel").tabs({
			select : function(event, ui) {
				showInfoMessage('Loading');
				showProcessingScreen();				
				$("#tabSelected").text(ui.index);
				loadComponent();
			}
		});

	})(jQuery);

	/*********************************************************************************************************
	 * NAME:          Remove Component
	 * DESCRIPTION:   Responds to the remove icon being clicked.  Loads dialog box
	 *********************************************************************************************************/
	$("#remove").click(function() {
	  if (superEditMode) {
		var iframe = getSelectedIFrame();		
		if (ensureRadioButtonSelected(iframe)) {
			return false;
		}

		$("#removalConfirmation").text('Please confirm that you want to permanently remove the selected ' + 
				$(".ui-tabs-selected a").text() + ' from the system.');
		$('#removalConfirmation').dialog({
			title : 'Confirmation: Removal of ' + $(".ui-tabs-selected a").text(),
			width : 350, height : 150, modal : true, resizable : false, draggable : false,
			buttons : [ {
				text : 'Remove',
				click : function() {
					$(this).dialog('close');
					removeComponent();
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
	 * NAME:          Save Component
	 * DESCRIPTION:   Responds when the save button is clicked.  Processes both Edits and Adds
	 *				  Using 'addMode', determine whether it is a add or edit
	 *
	 *********************************************************************************************************/
	$("#save").click(function() {

		if (addMode == true) {
			saveNewComponent();
			return false;
		}
		
		var iframe = getSelectedIFrame();
		
		var data = {};
		data['code'] = glb_cod;
		data['shortDescriptionEng'] = $.trim(iframe.contents().find('#tmpSDE').val());
		data['shortDescriptionFra'] = $.trim(iframe.contents().find('#tmpSDF').val());
		data['longDescriptionEng'] = $.trim(iframe.contents().find('#tmpLDE').val());
		data['longDescriptionFra'] = $.trim(iframe.contents().find('#tmpLDF').val());
		data['status'] = $.trim(iframe.contents().find('#tmpS').val());
		data['elementId'] = iframe.contents().find('input[name=radioSelection]:checked').attr('id');
		data['componentModelType'] = iframe.contents().find('#componentModelType').val();
		
		$.ajax({
			'url' : "<c:url value='/updateComponent.htm'/>",
			'type' : 'POST',
			'data' : data,
			'success' : function(response) {

				if (response.status == 'FAIL') {
					hideProcessingScreen();
					showErrorMessagesFromResponse(response);
				} else {					
					hideMessage();					
					hideProcessingScreen();
					
					// Overwrite global variables so it displays the new text
					glb_sde = $.trim(iframe.contents().find('#tmpSDE').val());
					glb_sdf = $.trim(iframe.contents().find('#tmpSDF').val());
					glb_lde = $.trim(iframe.contents().find('#tmpLDE').val());
					glb_ldf = $.trim(iframe.contents().find('#tmpLDF').val());
					glb_st = $.trim(iframe.contents().find('#tmpS').val()); 				
					
					cancelEdit();
					showSuccessMessage("Component " + glb_cod + " successfully updated");
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
	 * DESCRIPTION:   When the user clicks the view button, which sets the CCI Year, Section, and Status
	 *********************************************************************************************************/
	$("#viewButton").click(function() {

		if (editMode === true) {
			console.log('Cannot perform while in edit mode');
			return false;
		}
	
		var data = {};
		data['baseClassification'] = $("#baseClassification").val();
		data['versionCode'] = $("#versionCode").val();
		data['section'] = $("#section").val();
		data['sectionTitle'] = $("#section option:selected").text();
		data['status'] = $("#status").val();
		data['contextFrozen'] = $("#isContextFrozen").text();
		
		$.ajax({
			'url' : "<c:url value='/cciComponents.htm'/>",
			'type' : 'POST',
			'data' : data,
			'success' : function(response) {

				if (response.status == 'FAIL') {					
					hideProcessingScreen();
					showErrorMessagesFromResponse(response);
				} else {
					// Setting this indicates to the page that the view button has been clicked
					// This is then used to load components
					$("#vc").text($("#versionCode").val());
									
					//console.log(response);
					$("#isContextFrozen").text(response.contextFrozen);
					contextFrozenMessage(response.contextFrozen);
					setSuperEditMode();
					loadComponent();
				}
			},
			beforeSend : function() {
				showProcessingScreen();
			}
		});
		
	});
	
</script>
