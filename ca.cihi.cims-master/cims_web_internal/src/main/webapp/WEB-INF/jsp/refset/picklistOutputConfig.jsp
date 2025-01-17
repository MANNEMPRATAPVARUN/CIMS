<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<spring:eval var="snomedUrl" expression="@applicationProperties.getProperty('snomedservice.url')" />

<script type="text/javascript">
    var processedMessage = {};
    processedMessage["class"] = '';
    processedMessage["message"] = '';
    processedMessage["image"] = '';

    
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
        processedMessage["image"] = "<img src='"+"<c:url value='/img/icons/Ok.png' />"+"'/>";

        showMessage();
    }

    function showErrorMessagesFromResponse(response) {
        var errorMessages = "";
        for (var i = 0; i < response.errorMessageList.length; i++) {
            var item = response.errorMessageList[i];
            errorMessages += "<img src='"+"<c:url value='/img/icons/Error.png' />"+"'/> "
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

	function enableSaveButton() {
        $('#save').attr('src', '<c:url value="/img/icons/Save.png" />');
        
        enableSaveClickEvent();                
    }

    function disableSaveButton() {
        $('#save').attr('src', '<c:url value="/img/icons/SaveGrey.png" />');
        
        disableSaveClickEvent();
    }

    function enableSaveClickEvent() {
        $('#save').unbind('click').click(function() {
            var action = $('#curOutputConfig').attr('action'); 
            
            action == 'NEW' ? addPicklistOutputConfiguration() : savePicklistOutputConfiguration();
        });
    }

    function disableSaveClickEvent() {
        $('#save').unbind('click'); 
    }  

    function enableAddButton() {
        $('#add').attr('src', '<c:url value="/img/icons/Add.png" />');
        
        enableAddClickEvent();                
    }

    function disableAddButton() {
        $('#add').attr('src', '<c:url value="/img/icons/AddGrey.png" />');
        
        disableAddClickEvent();
    }

    function enableAddClickEvent() {
        $('#add').unbind('click').click(function() {
            addNewOutputConfiguration();
        });
    }

    function disableAddClickEvent() {
        $('#add').unbind('click'); 
    }  

    function enableRemoveButton() {
        $("#remove").attr('src', '<c:url value="/img/icons/Remove.png" />');   
             
        enableRemoveClickEvent();
    }
    
    function disableRemoveButton() {
        $("#remove").attr('src', '<c:url value="/img/icons/RemoveGrey.png" />');
        
        disableRemoveClickEvent();
    }

    function enableRemoveClickEvent() {
        $("#remove").unbind('click').click(function() {
            removeOutputConfig();
        });
    }

    function disableRemoveClickEvent() {
        $('#remove').unbind("click");        
    }

    function enableCancelButton() {
        $("#cancel").attr('src', '<c:url value="/img/icons/Cancel.png" />');

        enableCancelClickEvent() 
    }
    
    function disableCancelButton() {
        $("#cancel").attr('src', '<c:url value="/img/icons/CancelGrey.png" />');
        
        disableCancelClickEvent();
    }    

    function enableCancelClickEvent() {
        $("#cancel").unbind('click').click(function() {
            cancelOutputConfiguration();
        });
    }

    function disableCancelClickEvent() {
        $('#cancel').unbind("click");        
    }

    function enableEditButton() {
        $("#edit").attr('src', '<c:url value="/img/icons/Edit.png" />');
        
        enableEditClickEvent();
    }
    
    function disableEditButton() {
        $("#edit").attr('src', '<c:url value="/img/icons/EditGrey.png" />');
        
        disableEditClickEvent();
    }

    function enableEditClickEvent() {
        $("#edit").unbind('click').click(function() {
            editOutputConfiguration();
        });
    }

    function disableEditClickEvent() {
        $('#edit').unbind("click");        
    }

    function enableResetButton() {
        $("#reset").attr('src', '<c:url value="/img/icons/Reset.png" />');
        
        enableResetClickEvent();
    }
    
    function disableResetButton() {
        $("#reset").attr('src', '<c:url value="/img/icons/ResetGrey.png" />');
        
        disableResetClickEvent();
    }

    function enableResetClickEvent() {
        $("#reset").unbind('click').click(function() {
            resetOutputConfiguration(); 
        });
    }

    function disableResetClickEvent() {
        $('#reset').unbind("click");        
    }    

    function addNewOutputConfiguration() {
        hideMessage();
        
        $('#picklistOutputConfiguration').find('tbody').append("<tr id='curOutputConfig'><td style='border: 1px solid #96BEBD;'></td><td style='border: 1px solid #96BEBD;'></td>" + 
                "<td style='border: 1px solid #96BEBD;'></td><td style='border: 1px solid #96BEBD;'></td><td style='border: 1px solid #96BEBD;'></td></tr>");

        $('#curOutputConfig').find('td:eq(0)').html("<input type='radio' name='outputConfig' id='outConfig'>");
        $('#curOutputConfig').find('td:eq(1)').html("<input type='text' id='outputCode' name='outputCode' maxlength='10' style='width: 80px;'>");
        $('#curOutputConfig').find('td:eq(2)').html("<input type='text' id='outputConfigName' name='outputConfigName' maxlength='150' style='width: 200px;'>");
        $('#curOutputConfig').find('td:eq(3)').html("<select name='language' id='outputConfigLanguage'></select>");

        $('#outputConfigLanguage').append($('<option></option>').attr('value', 'ENG').text('<fmt:message key="common.english" />'));
        $('#outputConfigLanguage').append($('<option></option>').attr('value', 'FRA').text('<fmt:message key="common.french" />'));

        $('#curOutputConfig').find('td:eq(4)').html("No");
        $('#outConfig').attr('checked', true);

        $('#curOutputConfig').attr('action', 'NEW');

        $('#outputConfigName').keyup(function() {
            processSaveButtonEnable();
        });  

        $('#outputCode').keyup(function() {
            processSaveButtonEnable();
        });  

        disableSaveButton();
        disableAddButton(); 
        disableRemoveButton();
        disableEditButton();
        enableCancelButton();
        disableResetButton();

        disableConfigSelection();

        var exportImg = '<img class="viewMode" title="Export" src="<c:url value="/img/icons/ExportGrey.jpg" />">';
        $('#export').html(exportImg); 
    }

    function processSaveButtonEnable() {
        var outputConfigName = $.trim($('#outputConfigName').val());
        
        if (outputConfigName == null || outputConfigName == '') {
            disableSaveButton();
            
            return;
        }

        if ($('#outputCode').length > 0) {
            var outputCode = $.trim($('#outputCode').val());
        
            if (outputCode == null || outputCode == '') {
                disableSaveButton();
                
                return;
            }
        }
        
        var prevConfigName = $('#outputConfigName').attr('prevConfigName');

        if (prevConfigName != null && prevConfigName != undefined) {
            if (prevConfigName.toUpperCase() == outputConfigName.toUpperCase()) {
                disableSaveButton();
                
                return;
            }
        }

        enableSaveButton();
        
        return;
    } 

    function processResetButtonEnable() {
        var outputConfigName = $.trim($('#outputConfigName').val());
        var languageCode = $('#outputConfigLanguage').val();

        var savedOutputConfigName = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('outputName');
        var savedLanguageCode = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('languageCode');

        if (savedOutputConfigName == undefined || savedOutputConfigName == null || 
                languageCode == undefined || languageCode == null) {
            return;
        } 

        if (savedOutputConfigName != outputConfigName || savedLanguageCode != languageCode) {
            enableResetButton();
        }
        
        return;                
    }

    function addPicklistOutputConfiguration() {
        hideMessage();
        disableSaveButton();
        
        var picklistOutput = {'picklistId': $('#save').data('picklistElementId'),
                              'refsetContextId': $('#save').data('contextId'),
                              'name': $.trim($('#outputConfigName').val()),
                              'outputCode': $.trim($('#outputCode').val()),
                              'languageCode': $('#outputConfigLanguage').val()};
        
        $.ajax({
            type: 'post',
            url: "<c:url value='/refset/picklist/addPicklistOutput.htm' />",
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify(picklistOutput),
            success: function(response) {
                if (response.status == 'SUCCESS') {
                    $('#outConfig').attr('id', id='out_' + response.result.picklistOutputId);
                    $('#curOutputConfig').attr('id', 'Id_' + response.result.picklistOutputId);

                    var columnOutputURL = "<c:url value='/refset/picklist/picklistColumnOutputConfig.htm?contextId=' />" + ${viewBean.contextId} + 
                            '&picklistElementId=' + ${param.picklistElementId} + 
                            '&picklistElementVersionId=' + ${param.picklistElementVersionId} + 
                            '&elementId=' + ${viewBean.elementId} + 
                            '&elementVersionId=' + ${viewBean.elementVersionId} + 
                            '&picklistOutputId=' + response.result.picklistOutputId + 
                            '&language=' + response.result.languageCode +
                            '&asotReleaseIndCode=N'; 

                    $('#Id_' + response.result.picklistOutputId).find('td:eq(1)').html(response.result.outputCode);
                    $('#Id_' + response.result.picklistOutputId).find('td:eq(2)').html('<a href="' + columnOutputURL + '">' + response.result.name + '</a>');
                    $('#Id_' + response.result.picklistOutputId).find('td:eq(3)').html(getLanguageByCode(response.result.languageCode));
                    $('#Id_' + response.result.picklistOutputId).find('td:eq(4)').html(getReleaseIndicator(response.result.asotReleaseIndCode));
                    $('#Id_' + response.result.picklistOutputId).attr('action', 'SAVE');
                    $('#Id_' + response.result.picklistOutputId).attr('picklistOutputId', response.result.picklistOutputId);
                    $('#Id_' + response.result.picklistOutputId).attr('outputName', response.result.name);
                    $('#Id_' + response.result.picklistOutputId).attr('languageCode', response.result.languageCode);
                    $('#Id_' + response.result.picklistOutputId).attr('asotReleaseIndCode', response.result.asotReleaseIndCode);
                    
                    $('#out_' + response.result.picklistOutputId).click(function() {
                        handleConfigSelected();
                    });
                    
                    enableAddButton();
                    enableRemoveButton();
                    enableEditButton();
                    disableCancelButton();
                    disableResetButton();

                    enableConfigSelection();                    
                } else {                    
                    showErrorMessages(response.message);

                    enableSaveButton();
                }
            },    
            error: function(response) {
                var errorMessages = "System error occurred, please contact System administrator.";

                showErrorMessages(errorMessages);
            }            
        }); 
    }

    function savePicklistOutputConfiguration() {
        hideMessage();
        
        var picklistOutputId = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('picklistOutputId');

        if (picklistOutputId == undefined || picklistOutputId == null) {
            return;
        }

        disableSaveButton();        
        
        var picklistOutput = {'picklistOutputId': picklistOutputId,
                              'picklistId': $('#save').data('picklistElementId'),
                              'refsetContextId': $('#save').data('contextId'),
                              'name': $.trim($('#outputConfigName').val()),
                              'languageCode': $('#outputConfigLanguage').val()};

        $.ajax({
            type: 'post',
            url: "<c:url value='/refset/picklist/updatePicklistOutput.htm' />",
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify(picklistOutput),
            success: function(response) {
                if (response.status == 'SUCCESS') {
                    $('#outConfig').attr('id', 'out_' + response.result.picklistOutputId);
                    $('#curOutputConfig').attr('id', 'Id_' + response.result.picklistOutputId);

                    var columnOutputURL = "<c:url value='/refset/picklist/picklistColumnOutputConfig.htm?contextId=' />" + ${viewBean.contextId} + 
                            '&picklistElementId=' + ${param.picklistElementId} + 
                            '&picklistElementVersionId=' + ${param.picklistElementVersionId} + 
                            '&elementId=' + ${viewBean.elementId} + 
                            '&elementVersionId=' + ${viewBean.elementVersionId} + 
                            '&picklistOutputId=' + response.result.picklistOutputId + 
                            '&language=' + response.result.languageCode; 
                    
                    $('#Id_' + response.result.picklistOutputId).find('td:eq(2)').html('<a href="' + columnOutputURL + '">' + response.result.name + '</a>');
                    $('#Id_' + response.result.picklistOutputId).find('td:eq(3)').html(getLanguageByCode(response.result.languageCode));
                    $('#Id_' + response.result.picklistOutputId).find('td:eq(4)').html(getReleaseIndicator(response.result.asotReleaseIndCode));
                    $('#Id_' + response.result.picklistOutputId).attr('action', 'SAVE');
                    $('#Id_' + response.result.picklistOutputId).attr('picklistOutputId', response.result.picklistOutputId);
                    $('#Id_' + response.result.picklistOutputId).attr('outputName', response.result.name);
                    $('#Id_' + response.result.picklistOutputId).attr('languageCode', response.result.languageCode);
                    $('#Id_' + response.result.picklistOutputId).attr('asotReleaseIndCode', response.result.asotReleaseIndCode);
      
                    $('#out_' + response.result.picklistOutputId).unbind('click').click(function() {
                       handleConfigSelected();
                    }); 

                    enableAddButton();
                    enableRemoveButton();
                    enableEditButton();
                    disableCancelButton();
                    disableResetButton();                   

                    enableConfigSelection();
                } else {                    
                    showErrorMessages(response.message);

                    enableSaveButton(); 
                }
            },    
            error : function(response) {
                var errorMessages = "System error occurred, please contact System administrator.";

                showErrorMessages(errorMessages);
            }            
        }); 

        return;
    }

    function getLanguageByCode(languageCode) {
        var languages = {'ENG': '<fmt:message key="common.english" />',
                         'FRA': '<fmt:message key="common.french" />'};

        return languages[languageCode];
    }

    function getReleaseIndicator(asotReleaseIndCode){
		var indCodes = {'Y':'Yes', 'N':'No'};

		return indCodes[asotReleaseIndCode];
    }

    function handleConfigSelected() {   
    	<security:authorize access="!hasAnyRole('ROLE_IT_ADMINISTRATOR')">     
            disableSaveButton();
            disableCancelButton();
            disableResetButton();

            <c:if test="${refsetPermission == 'WRITE'}">
                enableAddButton();
                enableRemoveButton();
                enableEditButton(); 
            </c:if>
                   
            <c:if test="${refsetPermission != 'WRITE'}">
                disableAddButton();
                disableRemoveButton();
                disableEditButton(); 
            </c:if>

            var picklistOutputId = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('picklistOutputId');

            if (picklistOutputId == undefined || picklistOutputId == null) {
                return;
            }

            $.ajax({
                url: "<c:url value='/refset/picklist/getPicklistColumnOutputConfig.htm' />",
                contentType:"application/json; charset=UTF-8",
                data: {'picklistOutputId': picklistOutputId},
                success: function(response) {
                    if (response != null && response.length > 0) {
                        var exportURL = '<c:url value="/refset/picklist/exportExcel.htm" />' + '?contextId=${param.contextId}&picklistElementId=${param.picklistElementId}&picklistOutputId=' + picklistOutputId; 
                        var exportImg = '<img class="viewMode" title="Export" src="<c:url value="/img/icons/Export.jpg" />">';
                        $('#export').html('<a href="' + exportURL + '">' + exportImg + '</a>');  

                        <c:choose>
                            <c:when test="${picklistColumnsEvolutionPermission == 'WRITE'}">                
	                   	        var downloadEvolutionURL = '<c:url value="/refset/picklist/generateEvolution.htm" />' + '?contextId=${param.contextId}&elementId=${param.elementId}&elementVersionId=${param.elementVersionId}&picklistElementId=${param.picklistElementId}&picklistOutputId=' + picklistOutputId;			                               
	                	        var downloadEvolutionImg = '<img class="viewMode" title="DownloadEvolution" src="<c:url value="/img/icons/DownloadEvolution.png" />">';
	                	        $('#downloadEvolution').html('<a href="' + downloadEvolutionURL + '">' + downloadEvolutionImg + '</a>');  
		                    </c:when>
	                        <c:otherwise>
	                	        $('#downloadEvolution').html('<img class="viewMode" title="DownloadEvolution" src="<c:url value="/img/icons/DownloadEvolutionGrey.png" />" />');
	                        </c:otherwise>
	                    </c:choose>   
                    } else {
                        $('#export').html('<img class="viewMode" title="Export" src="<c:url value="/img/icons/ExportGrey.jpg" />" />');
                 	    $('#downloadEvolution').html('<img class="viewMode" title="DownloadEvolution" src="<c:url value="/img/icons/DownloadEvolutionGrey.png" />" />');
                    }                                                
                }                
            });  
        </security:authorize>

        <security:authorize access="hasAnyRole('ROLE_IT_ADMINISTRATOR')">
            handleASOT();
        </security:authorize>

        return;     
    }

    function removeOutputConfig() {
        hideMessage();
        
        var selectedRadioId = $('input[type=radio][name=outputConfig]:checked').attr('id');

        if (selectedRadioId == undefined || selectedRadioId == null) {
            return;
        }

        var picklistOutputId = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('picklistOutputId');

        if (picklistOutputId == undefined || picklistOutputId == null) {
            return;
        }

        var exportImg = '<img class="viewMode" title="Export" src="<c:url value="/img/icons/ExportGrey.jpg" />">';
        $('#export').html(exportImg); 

        $.ajax({
            url: "<c:url value='/refset/picklist/deletePicklistOutputConfig.htm' />",
            contentType:"application/json; charset=UTF-8",
            data: {
                'picklistOutputId': picklistOutputId,                 
            },
            success: function(response) {
                if (response.status == 'SUCCESS') {
                    deleteRow();
                    
                    $('input[type=radio][name=outputConfig]').attr("disabled", false);   

                    disableRemoveButton();
                    disableEditButton();
                    disableResetButton();
                    disableCancelButton();                    
                    enableAddButton();  

                    enableConfigSelection();                  
                } else {
                    showErrorMessages(response.message);
                }
            },    
            error : function(response) {
                var errorMessages = "System error occurred, please contact System administrator.";

                showErrorMessages(errorMessages);
            }   
        });
        
        return;
    }

    function deleteRow() {
        $('input[type=radio][name=outputConfig]:checked').closest('tr').remove();
    }

    function editOutputConfiguration() {
        hideMessage();
        
        var selectedRadioId = $('input[type=radio][name=outputConfig]:checked').attr('id');

        if (selectedRadioId == undefined || selectedRadioId == null) {
            return;
        }

        var picklistOutputId = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('picklistOutputId');

        if (picklistOutputId == undefined || picklistOutputId == null) {
            return;
        }

        var outputConfigName = $.trim($('#Id_' + picklistOutputId).attr('outputName'));
        var languageCode = $('#Id_' + picklistOutputId).attr('languageCode');
        var asotReleaseIndCode = $('#Id_' + picklistOutputId).attr('asotReleaseIndCode');
        
        $('#Id_' + picklistOutputId).attr('id', 'curOutputConfig');

        $('#out_' + picklistOutputId).attr('id', 'outConfig');
        $('#curOutputConfig').find('td:eq(2)').html("<input type='text' id='outputConfigName' name='outputConfigName' maxlength='150' style='width: 200px;'>");
        $('#outputConfigName').val(outputConfigName);
        $('#outputConfigName').attr('prevConfigName', outputConfigName);
        
        $('#curOutputConfig').find('td:eq(3)').html("<select name='language' id='outputConfigLanguage' disabled></select>");
        $('#outputConfigLanguage').append($('<option></option>').attr('value', 'ENG').text('<fmt:message key="common.english" />'));
        $('#outputConfigLanguage').append($('<option></option>').attr('value', 'FRA').text('<fmt:message key="common.french" />'));
        $('#outputConfigLanguage').val(languageCode);

        $('#curOutputConfig').find('td:eq(4)').html(asotReleaseIndCode);

        $('#outputConfigName').keyup(function() {
            processSaveButtonEnable();
            processResetButtonEnable();
        });         

        disableSaveButton();
        disableAddButton();
        enableRemoveButton();
        disableEditButton();
        enableCancelButton();
        disableResetButton();

        disableConfigSelection();
                
        return;
    }

    function cancelOutputConfiguration() {
        hideMessage();
        
        disableSaveButton();
        enableAddButton();
        enableRemoveButton();
        enableEditButton();
        disableCancelButton();
        disableResetButton();

        enableConfigSelection();
        
        var isNewRecord = $('#curOutputConfig').attr('action') == 'NEW';

        if (isNewRecord) {             
            disableRemoveButton();
            disableEditButton();
                       
            deleteRow();
            
            return;
        }

        var picklistOutputId = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('picklistOutputId');
        var outputName = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('outputName');
        var languageCode = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('languageCode');
            
        $('#outConfig').attr('id', id='out_' + picklistOutputId);
        $('#curOutputConfig').attr('id', 'Id_' + picklistOutputId);

        var asotIndicator = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('asotReleaseIndCode');
        
        var columnOutputURL = "<c:url value='/refset/picklist/picklistColumnOutputConfig.htm?contextId=' />" + ${viewBean.contextId} + 
                '&picklistElementId=' + ${param.picklistElementId} + 
                '&picklistElementVersionId=' + ${param.picklistElementVersionId} + 
                '&elementId=' + ${viewBean.elementId} + 
                '&elementVersionId=' + ${viewBean.elementVersionId} + 
                '&picklistOutputId=' + picklistOutputId + 
                '&language=' + languageCode +
                '&asotReleaseIndCode=' + asotIndicator; 

        $('#Id_' + picklistOutputId).find('td:eq(2)').html('<a href="' + columnOutputURL + '">' + outputName + '</a>');
        $('#Id_' + picklistOutputId).find('td:eq(3)').html(getLanguageByCode(languageCode));
        $('#Id_' + picklistOutputId).find('td:eq(4)').html(getReleaseIndicator(asotIndicator));
        $('#Id_' + picklistOutputId).attr('action', 'SAVE');        

        $('#out_' + picklistOutputId).unbind('click').click(function() {
            handleConfigSelected();
        });

        return;
    }

    function disableConfigSelection() {
        $('input[type=radio][name=outputConfig]').attr('disabled', true);
        disableEvolutionButton();
    }

    function disableASOT() {
    	$("#asot").attr('src', '<c:url value="/img/icons/ReleaseGrey.png" />');
    	$('#asot').unbind("click");    
    }

    function enableASOT() {    	
    	$("#asot").attr('src', '<c:url value="/img/icons/Release.png" />');
        
    	$('#asot').unbind("click").click(function(){
			releaseToASOT();
        }); 
    }
    
    function handleASOT(){
    	var asotIndicator = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('asotReleaseIndCode');
        if (asotIndicator == undefined || asotIndicator == null || asotIndicator!='Y') {
            disableASOT();
        }else{
			enableASOT();
        }
    }

    function releaseToASOT(){
    	var selectedRadioId = $('input[type=radio][name=outputConfig]:checked').attr('id');

        if (selectedRadioId == undefined || selectedRadioId == null) {
            return;
        }

        var picklistOutputId = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('picklistOutputId');

        if (picklistOutputId == undefined || picklistOutputId == null) {
            return;
        }

        var releaseImg = '<img class="viewMode" title="Release to ASOT" src="<c:url value="/img/icons/ReleaseGrey.jpg" />">';
        $('#asot').html(releaseImg); 
        hideMessage();
        showProcessingScreen();

        $.ajax({
            url: "<c:url value='/refset/picklist/releaseToASOT.htm' />",
            contentType:"application/json; charset=UTF-8",
            data: {
                'picklistOutputId': picklistOutputId, 
                'contextId': ${viewBean.contextId},
                'refsetElementId': ${viewBean.elementId},
                'refsetElementVersionId': ${viewBean.elementVersionId}                
            },
            success: function(response) {
            	hideProcessingScreen();
                if (response.status == 'SUCCESS') {
                    $('input[type=radio][name=outputConfig]').attr("disabled", false);   

                    enableConfigSelection();  
                    showSuccessMessage(response.message);                
                } else {
                    showErrorMessages(response.message);
                }
            },    
            error : function(response) {
            	hideProcessingScreen();
                var errorMessages = "System error occurred, please contact System administrator.";

                showErrorMessages(errorMessages);
            }   
        });
        
        return;
    }    

    function disableEvolutionButton() {
    	$('#downloadEvolution').html('<img class="viewMode" title="Export" src="<c:url value="/img/icons/DownloadEvolutionGrey.png" />" />');
    	$('#downloadEvolution').unbind("click");    
    }
    
    function enableConfigSelection() {
        $('input[type=radio][name=outputConfig]').attr('disabled', false);
    	<c:choose>
        <c:when test="${picklistColumnsEvolutionPermission == 'WRITE'}">                
        	enableEvolutionButton();  
        </c:when>
        <c:otherwise>
        	disableEvolutionButton();  
        </c:otherwise>
        </c:choose>           
    }

    function enableEvolutionButton(){
    	var picklistOutputId = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('picklistOutputId');
        if (picklistOutputId == undefined || picklistOutputId == null) {
        	return;
        }
   	    var downloadEvolutionURL = '<c:url value="/refset/picklist/generateEvolution.htm" />' + '?contextId=${param.contextId}&elementId=${param.elementId}&elementVersionId=${param.elementVersionId}&picklistElementId=${param.picklistElementId}&picklistOutputId=' + picklistOutputId;			                               
        var downloadEvolutionImg = '<img class="viewMode" title="DownloadEvolution" src="<c:url value="/img/icons/DownloadEvolution.png" />">';
        $('#downloadEvolution').html('<a href="' + downloadEvolutionURL + '">' + downloadEvolutionImg + '</a>'); 
    }
    
    function resetOutputConfiguration() {
        hideMessage();
        
        enableSaveButton();
        disableAddButton();
        enableRemoveButton();
        disableEditButton();
        enableCancelButton();
        disableResetButton();
        
        var outputName = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('outputName');
        var languageCode = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('languageCode');

        $('#outputConfigName').val(outputName);
        $('#outputConfigLanguage').val(languageCode);
    }    
</script>

<tiles:insertAttribute name="refset-picklist-menu" />

<div class="content" id='picklistOutput' style='z-index: 10000;'>
    <div class="icons">
        <ul style="padding-left: .9em;">
            <li style="float: left; list-style-type: none;">
                <div id="loadingInfo" class="info"
                    style="display: none; margin-bottom: 0.1em; width: 900px; padding-top: 0.5em; padding-bottom: 0.5em;">Loading</div>
            </li>
        </ul>
    </div>

    <div class="icons inline" style="margin-top: 15px; width: 60%">
        <span style="float: right">
            <ul style="padding-left: .9em;">
                <li id="iconsLI" style="float: right; top: 0px; border: 0px; background: #ffffff; list-style-type: none;">
                    <img id="save" class="viewMode" title="Save" src="<c:url value='/img/icons/SaveGrey.png' />" /> 
                    <img id="add" class="viewMode" title="Add" src="<c:url value='/img/icons/AddGrey.png' />" />  
                    <img id="remove" class="viewMode" title="Delete" src="<c:url value='/img/icons/RemoveGrey.png' />" />
                    <img id="edit" class="viewMode" title="Edit" src="<c:url value='/img/icons/EditGrey.png' />" /> 
                    <img id="cancel" class="viewMode" title="Cancel" src="<c:url value='/img/icons/CancelGrey.png' />" /> 
                    <img id="reset" class="viewMode" title="Reset" src="<c:url value='/img/icons/ResetGrey.png' />" /> 
                    <span id="export"><img class="viewMode" title="Export" src="<c:url value='/img/icons/ExportGrey.jpg' />" /></span>        
                    <span id="downloadEvolution"><img class="viewMode" title="downloadEvolution" src="<c:url value='/img/icons/DownloadEvolutionGrey.png' />" /></span>
                    <security:authorize access="hasAnyRole('ROLE_IT_ADMINISTRATOR')">
                    <img id="asot" class="viewMode" title="Release to ASOT" src="<c:url value='/img/icons/ReleaseGrey.png' />" />
                    </security:authorize>               
                </li>
            </ul>
        </span>
    </div>

    <div id="removalConfirmation" style="display: none;">confirm</div> 
    
    <div class="content" style="margin-top: 20px;">
        <table class="listTable" style="width: 60%; margin-top: 0px;" id='picklistOutputConfiguration'>
            <thead>
                <tr>
                    <th class="tableHeader"></th>
                    <th class="tableHeader"><fmt:message key="picklist.output.configuration.code" /></th>
                    <th class="tableHeader"><fmt:message key="picklist.output.configuration.name" /></th>
                    <th class="tableHeader"><fmt:message key="reports.language" /></th>
                    <th class="tableHeader"><fmt:message key="picklist.output.configuration.asotindicator" /></th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function() {        
        $('#save').data('contextId', '${param.contextId}');
        $('#save').data('picklistElementId', '${param.picklistElementId}');

        $.ajax({
            url: "<c:url value='/refset/picklist/getPicklistOutputConfig.htm' />",
            contentType:"application/json; charset=UTF-8",
            data: {
                'contextId': ${param.contextId},
                'picklistElementId': ${param.picklistElementId}                
            },
            success: function(response) {
                hideProcessingScreen();

                $.each(response, function(index, record) {
                    $('#picklistOutputConfiguration').find('tbody').append("<tr id='Id_" + record.picklistOutputId + "'><td style='border: 1px solid #96BEBD;'></td><td style='border: 1px solid #96BEBD;'></td>" + 
                        "<td style='border: 1px solid #96BEBD;'></td><td style='border: 1px solid #96BEBD;'></td><td style='border: 1px solid #96BEBD;'></td></tr>");
                     
                    $('#Id_' + record.picklistOutputId).find('td:eq(0)').html("<input type='radio' name='outputConfig' id='out_" + record.picklistOutputId + "' disabled>");

                    var columnOutputURL = "<c:url value='/refset/picklist/picklistColumnOutputConfig.htm?contextId=' />" + ${viewBean.contextId} + 
                            '&picklistElementId=' + ${param.picklistElementId} + 
                            '&picklistElementVersionId=' + ${param.picklistElementVersionId} + 
                            '&elementId=' + ${viewBean.elementId} + 
                            '&elementVersionId=' + ${viewBean.elementVersionId} + 
                            '&picklistOutputId=' + record.picklistOutputId + 
                            '&language=' + record.languageCode +
                            '&asotReleaseIndCode=' + record.asotReleaseIndCode; 

                    $('#Id_' + record.picklistOutputId).find('td:eq(1)').html(record.outputCode);       
                    $('#Id_' + record.picklistOutputId).find('td:eq(2)').html('<a href="' + columnOutputURL + '">' + record.name + '</a>');
                    $('#Id_' + record.picklistOutputId).find('td:eq(3)').html(getLanguageByCode(record.languageCode));
                    $('#Id_' + record.picklistOutputId).find('td:eq(4)').html(getReleaseIndicator(record.asotReleaseIndCode));     
                    $('#Id_' + record.picklistOutputId).attr('action', 'SAVE');  
                    $('#Id_' + record.picklistOutputId).attr('picklistOutputId', record.picklistOutputId);
                    $('#Id_' + record.picklistOutputId).attr('outputName', record.name);
                    $('#Id_' + record.picklistOutputId).attr('languageCode', record.languageCode);
                    $('#Id_' + record.picklistOutputId).attr('asotReleaseIndCode', record.asotReleaseIndCode);

                    $('#out_' + record.picklistOutputId).unbind('click').click(function() {
                        handleConfigSelected();
                    });                                                     
                });
                
                <c:if test="${refsetPermission == 'WRITE'}">                    
                    enableAddButton();
                    enableConfigSelection();                     
                </c:if>

                <c:if test="${refsetPermission != 'WRITE' && writeForlastestClosedVersion == 'Y'}">
                    <c:if test="${refsetExport == 'Y'}">                        
                        enableConfigSelection(); 
                    </c:if>
                </c:if>
                
                <security:authorize access="hasAnyRole('ROLE_IT_ADMINISTRATOR')">
                    disableAddButton();
                    enableConfigSelection(); 
                </security:authorize>                                
            },
            error: function(response) {
                hideProcessingScreen();
                showErrorMessages("System error occurred, please contact System administrator.");
            }                    
        }); 
    });
</script>
