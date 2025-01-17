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

<script type="text/javascript">

	var glb_agentTypeEng;
	var glb_agentTypeFra;
	var glb_agentExEng;
	var glb_agentExFra;
	var glb_agentATCCode;
	var glb_agentGroupCode;
	
	var editMode = false;
	var timeoutValue = 200;

	$(document).ready(function() {		
		$('#agentGroups').val($('#hiddenGroupCode').text());
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
		var hiddenCode = $("#hiddenCode").text();
		var agentTypeEng = $('#tmpAgentTypeENG').val();
		var agentTypeFra = $('#tmpAgentTypeFRA').val();
		var agentExEng = $('#tmpAgentExENG').val();
		var agentExFra = $('#tmpAgentExFRA').val();
		
		var agentATCCode = $('#tmpAgentATCCode').val();
		var agentGroup = $('#agentGroups').val();
		
		$("#statBar").text('Saving');
		$("#statBar").attr("class", 'info');
		$("#statBar").show();

		$.ajax({
			'url' : "<c:url value='diagram.htm'/>",
 			'type' : 'POST',
			'data' : { e : elementId, 
				ate : agentTypeEng, atf : agentTypeFra,
				aee : agentExEng, aef : agentExFra,
				atcCode: agentATCCode, ag : agentGroup },			
			'success' : function(response) {

				if (response.status == 'FAIL') {				
					showErrorMessagesFromResponse(response);
				} else {
					glb_agentTypeEng = decodeURIComponent(agentTypeEng);
					glb_agentTypeFra = decodeURIComponent(agentTypeFra);					
					glb_agentExEng = decodeURIComponent(agentExEng);
					glb_agentExFra = decodeURIComponent(agentExFra);
					glb_agentATCCode = agentATCCode;
					$('#hiddenGroupCode').text(agentGroup);
					
					cancelEdit();		
					window.parent.parent.hideProcessingScreen();
					
					$("#statBar").text("Component " + hiddenCode + " successfully saved");
					$("#statBar").attr("class", 'success');
					$("#statBar").show();					
				}
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
		var tableRow = $('#group tr').eq(1);
		
		var agentTypeEng = tableRow.children("td:nth-child(2)"); 
		var agentTypeFra = tableRow.children("td:nth-child(4)"); 

		glb_agentTypeEng = $.trim(agentTypeEng.text());
		glb_agentTypeFra = $.trim(agentTypeFra.text());
		
		agentTypeEng.html("<input type='text' id='tmpAgentTypeENG'/>");
		agentTypeFra.html("<input type='text' id='tmpAgentTypeFRA'/>");
		
		$('#tmpAgentTypeENG').val(glb_agentTypeEng);
		$('#tmpAgentTypeFRA').val(glb_agentTypeFra);
		
		tableRow = $('#group tr').eq(2);
		
		var agentExEng = tableRow.children("td:nth-child(2)"); 
		var agentExFra = tableRow.children("td:nth-child(4)"); 

		glb_agentExEng = $.trim(agentExEng.text());
		glb_agentExFra = $.trim(agentExFra.text());
		
		agentExEng.html("<textarea id='tmpAgentExENG' rows='3' style='word-wrap: break-word;'/>"); 
		agentExFra.html("<textarea id='tmpAgentExFRA' rows='3' style='word-wrap: break-word;'/>");
		
		$('#tmpAgentExENG').val(glb_agentExEng);
		$('#tmpAgentExFRA').val(glb_agentExFra);
		
		var agentATCCode = $("#agentATCCode");
		glb_agentATCCode = $.trim(agentATCCode.text());		
		agentATCCode.html("<input type='text' maxlength=20 style='width:150px;' id='tmpAgentATCCode' value='" + glb_agentATCCode + "'/>");
		 
		$('#agentGroups').attr('disabled', false);
 		changeEditMode(true);
	}

	/*********************************************************************************************************
	* NAME:          Cancel Edit
    * DESCRIPTION:   
	*********************************************************************************************************/	
	function cancelEdit() {
    	var tableRow = $('#group tr').eq(1);
	
		var agentTypeEng = tableRow.children("td:nth-child(2)"); 
		var agentTypeFra = tableRow.children("td:nth-child(4)");
		
		agentTypeEng.html(glb_agentTypeEng);
		agentTypeFra.html(glb_agentTypeFra);
		
    	tableRow = $('#group tr').eq(2);
    	
		var agentExEng = tableRow.children("td:nth-child(2)"); 
		var agentExFra = tableRow.children("td:nth-child(4)");
		
		agentExEng.html(glb_agentExEng);
		agentExFra.html(glb_agentExFra);
		
		var agentATCCode = $("#agentATCCode");
		agentATCCode.html(glb_agentATCCode);
		
		//var agentGroupCode = $("#agentGroupCodeDiv");
		//agentGroupCode.html(glb_agentGroupCode);
		
		$('#agentGroups').val($('#hiddenGroupCode').text());
		$('#agentGroups').attr('disabled', true);
		
		changeEditMode(false);
		hideErrorMessage();
	}

	/*********************************************************************************************************
	* NAME:          Reset Changes
    * DESCRIPTION:   
	*********************************************************************************************************/
	function resetChanges() {
		$('#tmpAgentTypeENG').val(glb_agentTypeEng);
		$('#tmpAgentTypeFRA').val(glb_agentTypeFra);		
		$('#tmpAgentExENG').val(glb_agentExEng);
		$('#tmpAgentExFRA').val(glb_agentExFra);
		$('#tmpAgentATCCode').val(glb_agentATCCode);
		$('#agentGroups').val($('#hiddenGroupCode').text());
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

		hideErrorMessage();
		
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
	
		<div style="display:inline-block; width:35%;">
			<label>Section:</label>
			&nbsp;
			<font color=red>${viewer.sectionTitle}</font>
		</div>		
		<div style="display:inline-block; width:15%;">
			<label>Code Component:</label>
			&nbsp;
			<font color=red>${code}</font>
		</div>
		<div style="display:inline-block; width:30%;">
			<label>English Short Description:</label>
			&nbsp;
			<font color=red>${shortTitleEng}</font>
		</div>
		
		<div style="display:inline-block; text-align: right; float:right; top:0px; border:0px; background: #ffffff;">	
			<security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR')">
				
			<img id="save" class="editMode" title="Save" src="<c:url value="/img/icons/Save.png"/>" onclick="update();" style="display: none;"/>
			<img id="cancel" class="editMode" title="Cancel" src="<c:url value="/img/icons/Cancel.png"/>" onclick="cancelEdit();" style="display: none;"/>
			<img id="reset" class="editMode" title="Reset" src="<c:url value="/img/icons/Reset.png"/>" onclick="resetChanges();" style="display: none;"/>	
			<img id="edit" class="viewMode" title="Edit" src="<c:url value="/img/icons/Edit.png"/>" onclick="editComponent();"/>
			
			</security:authorize>			
		</div>		
	</div>	
	
	<div id="block_container">
		<div style="display:inline-block; width:30%;">
			<label>Agent ATC Code:</label>
			&nbsp;
			<font color=red><div id="agentATCCode" style="display:inline-block;">${atcCode}</div></font>
		</div>
		<div style="display:inline-block; width:15%;"><label>Agent Group Code:</label></div>
		<div id="agentGroupCodeDiv" style="display:inline-block; "><!-- ${groupCode} -->
		
			<select id="agentGroups" style="disabled:true;" disabled>
				<c:forEach var="agentGroup" items="${agentGroups}">
					<option value="${agentGroup.key}">
						${agentGroup.value}
					</option>
				</c:forEach>					
			</select>
		</div>
	</div>
	
	<div id="errorMessageList" class="error" style="display: none;"></div>
	
	<table id="group" style="width: 100%; margin-top: 20px; border: none; table-layout:fixed;" class="listTable">

		<thead>
			<tr>
				<th class="sizeOneFifty" style="background:#FFFFFF; border: none;">&nbsp;</th>
				<th class="tableHeader" style="border: none;">English</th>
				<th style="background:#FFFFFF; width:1px; border: none;">&nbsp;</th>
				<th class="tableHeader" style="border: none;">French</th>
			</tr>
		</thead>
		<tbody>
			<tr class="odd">
				<td style="background:#FFFFFF; border: none;">Agent Type:</td>
				<td style="border: none;">${agentTypeEng}</td>
				<td style="background:#FFFFFF; border: none;">&nbsp;</td>
				<td style="border: none;">${agentTypeFra}</td>
			</tr>
			<tr class="even">
				<td style="background:#FFFFFF; border: none;">Agent Examples:</td>
				<td style="border: none; word-wrap:break-word;">${agentExampleEng}</td>
				<td style="background:#FFFFFF; border: none;">&nbsp;</td>
				<td style="border: none; word-wrap:break-word;">${agentExampleFra}</td>
			</tr>			
		</tbody>
	</table>

	<div id="removalConfirmation" style="display: none;">
    	Do you want to remove the definition for ${code} in this version of classification?
	</div>

	<div id="statBar" class="info" style="display: none; margin-top: 10px;">Loading</div>
	<label id="hiddenElementId" style="display: none;">${elementId}</label>
	<label id="editMode" style="display: none;"></label>
	<label id="hiddenCode" style="display: none;">${code}</label>
	<label id="hiddenGroupCode" style="display: none;">${groupCode}</label>
</div>

</html>

