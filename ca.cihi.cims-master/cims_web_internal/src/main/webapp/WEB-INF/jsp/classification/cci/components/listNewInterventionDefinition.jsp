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

	var defaultDefinitionXMLENG;
	var defaultDefinitionXMLFRA;

	var processedMessage = {};
	processedMessage["class"] = '';
	processedMessage["message"] = '';
	
	$(document).ready(function() {
		
		//var iFrame = $("#comp1", parent.parent.document);

		//defaultDefinitionXMLENG = iFrame.get(0).contentWindow.definitionNewXMLENG;
		//defaultDefinitionXMLFRA = iFrame.get(0).contentWindow.definitionNewXMLFRA;
		
		defaultDefinitionXMLENG = parent.parent.definitionNewXMLENG;
		defaultDefinitionXMLFRA = parent.parent.definitionNewXMLFRA;
		
		$('#tmpDefENG').val(defaultDefinitionXMLENG);
		$('#tmpDefFRA').val(defaultDefinitionXMLFRA);
	});


	/*********************************************************************************************************
	* NAME:          ValidateXML
	* DESCRIPTION:   We should validate the XML if possible
	*********************************************************************************************************/	
	function validateXML() {

		var defEng = $('#tmpDefENG').val();
		var defFra = $('#tmpDefFRA').val();
		
		defEng = ((defEng == undefined) ? '' : defEng);
		defFra = ((defFra == undefined) ? '' : defFra);
		
		$.ajax({
			'url' : "<c:url value='validateDefinitionXML.htm'/>",
			'type' : 'POST',
			'data' : { de : defEng, df : defFra },
			'success' : function(response) {
	
				if (response.status == 'FAIL') {
					window.parent.parent.hideProcessingScreen();					
					showErrorMessagesFromResponse(response);
				} else {					
					parent.parent.definitionNewXMLENG = defEng;
					parent.parent.definitionNewXMLFRA = defFra;

					showSuccessMessage("English and French definition are valid");
					window.parent.parent.hideProcessingScreen();
				}
			},
			
			beforeSend: function(){
				hideMessage();		// clear out any error message before validating
				window.parent.parent.showProcessingScreen();
	        }			
		});	
	}

	/*********************************************************************************************************
	* NAME:          Reset Changes
    * DESCRIPTION:   
	*********************************************************************************************************/	
	function resetChanges() {
		$('#tmpDefENG').val(defaultDefinitionXMLENG);
		$('#tmpDefFRA').val(defaultDefinitionXMLFRA);		
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
// 		processedMessage["image"] = "<img src=\"../img/icons/Error.png\"/>";
		
		showMessage();	
	}	
</script>

<div class="icons">
	<ul style="padding-left:.9em; ">
	
		<li style="float: left; list-style-type: none; ">
			<div id="loadingInfo" class="info" style="display: none;margin-bottom: 0.1em; width: 600px; padding: 0.2em;"></div>
		</li>	
	
		<li style="float: right; top: 0px; border: 0px; background: #ffffff; list-style-type: none;">				
			<security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR')">
				
			<img id="save" title="Save" src="<c:url value="/img/icons/Save.png"/>" onclick="validateXML();" />			
			<img id="reset" title="Reset" src="<c:url value="/img/icons/Reset.png"/>" onclick="resetChanges();" />	
			
			</security:authorize>	
		</li>
	</ul>
</div>

<div class="content">
	
	<table id="group" style="width: 100%; margin-top: 20px; table-layout:fixed;" class="listTable">
		<thead>
			<tr>
				<th class="tableHeader" style="width:50%;">English Definition</th>
				<th class="tableHeader" style="width:50%;">French Definition</th>
			</tr>
		</thead>
		<tbody>
			<tr class="odd">
				<td><textarea id='tmpDefENG' style='height:200px; word-wrap: break-word;'></textarea></td>
				<td><textarea id='tmpDefFRA' style='height:200px; word-wrap: break-word;'></textarea></td>
		</tbody>
	</table>

</div>

</html>
