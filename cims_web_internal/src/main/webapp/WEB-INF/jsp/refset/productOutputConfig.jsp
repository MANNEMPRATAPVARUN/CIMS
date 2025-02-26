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
        processedMessage["image"] = "<img src=\"img/icons/Ok.png\"/>";

        showMessage();
    }

    function showErrorMessagesFromResponse(response) {
        var errorMessages = "";
        for (var i = 0; i < response.errorMessageList.length; i++) {
            var item = response.errorMessageList[i];
            errorMessages += "<img src=\"img/icons/Error.png\"/> "
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
            
            action == 'NEW' ? addRefsetOutputConfiguration() : saveRefsetOutputConfiguration();
        });
    }

    function disableSaveClickEvent() {
        $('#save').unbind('click'); 
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
        $('#productOutputConfiguration').find('tbody').append("<tr id='curOutputConfig'><td style='border: 1px solid #96BEBD;'></td>" + 
            "<td style='border: 1px solid #96BEBD;'></td><td style='border: 1px solid #96BEBD;'></td><td style='border: 1px solid #96BEBD;'></td></tr>");

        $('#curOutputConfig').find('td:eq(0)').html("<input type='radio' name='outputConfig' id='outConfig'>");
        $('#curOutputConfig').find('td:eq(1)').html("<input type='text' id='outputConfigName' name='outputConfigName' maxlength='150' style='width: 200px;'>");
        $('#curOutputConfig').find('td:eq(2)').html("<input type='text' id='outputConfigFileName' name='outputConfigFileName' maxlength='150' style='width: 200px;'>");
        $('#curOutputConfig').find('td:eq(3)').html("<select name='language' id='outputConfigLanguage'></select>");

        $('#outputConfigLanguage').append($('<option></option>').attr('value', 'ENG').text('<fmt:message key="common.english" />'));
        $('#outputConfigLanguage').append($('<option></option>').attr('value', 'FRA').text('<fmt:message key="common.french" />'));
        $('#outConfig').attr('checked', true);

        $('#curOutputConfig').attr('action', 'NEW');

        $('#outputConfigName').keyup(function() {
            processSaveButtonEnable();
        });   

        $('#outputConfigFileName').keyup(function() {
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

    function enableConfigSelection() {
        $('input[type=radio][name=outputConfig]').attr('disabled', false);
    } 

    function disableConfigSelection() {
        $('input[type=radio][name=outputConfig]').attr('disabled', true);
    } 

    function processSaveButtonEnable() {
        var outputConfigName = $.trim($('#outputConfigName').val());
        
        if (outputConfigName == null || outputConfigName == '') {
            disableSaveButton();
            
            return;
        }

        var outputConfigFileName = $.trim($('#outputConfigFileName').val());
        
        if (outputConfigFileName == null || outputConfigFileName == '') {
            disableSaveButton();
            
            return;
        }

        enableSaveButton();
        
        return;
    }

    function getLanguageByCode(languageCode) {
        var languages = {'ENG': '<fmt:message key="common.english" />',
                         'FRA': '<fmt:message key="common.french" />'};

        return languages[languageCode];
    }

    function handleConfigSelected() {        
        disableSaveButton();

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
               
        disableCancelButton();
        disableResetButton();

        var refsetOutputId = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('refsetOutputId');

        if (refsetOutputId == undefined || refsetOutputId == null) {
            return;
        }

        $.ajax({
            url: "<c:url value='/refset/getRefsetOutputConfiguration.htm' />",
            contentType:"application/json; charset=UTF-8",
            data: {'contextId': ${param.contextId},
                   'refsetOutputId': refsetOutputId
            },
            success: function(response) {
                hideProcessingScreen();

                if (response.result != undefined && response.result != null) {  
                    if (response.result.length > 0) {
                        var exportURL = '<c:url value="/refset/exportExcel.htm" />' + '?refsetOutputId=' + refsetOutputId; 
                        var exportImg = '<img class="viewMode" title="Export" src="<c:url value="/img/icons/Export.jpg" />">';
                        $('#export').html('<a href="' + exportURL + '">' + exportImg + '</a>');        
                    } else {
                        $('#export').html('<img class="viewMode" title="Export" src="<c:url value="/img/icons/ExportGrey.jpg" />" />');
                    }
                }               
            },
            error: function(response) {
                hideProcessingScreen();
                
                var errorMessages = "System error occurred, please contact System administrator.";

                showErrorMessages(errorMessages);
            }                    
        });

        return;
    }

    function addRefsetOutputConfiguration() {
        hideMessage();

        var errors = validateDuplicateName();

        var errorMsg = '';
        var errorImg = '<img src="<c:url value="/img/icons/Error.png" />">';
        var hasError = false;

        $.each(errors, function(key, value) {                 
            errorMsg += errorImg + value + '<br/>';   

            hasError = true;             
        });

        if (hasError) {     
            showErrorMessages(errorMsg);
            
            return;
        }    
        
        disableSaveButton();
        
        var refsetOutput = {'refsetContextId': ${param.contextId},
                            'refsetId': ${param.elementId},
                            'name': $.trim($('#outputConfigName').val()),
                            'filename': $.trim($('#outputConfigFileName').val()), 
                            'languageCode': $('#outputConfigLanguage').val()};
        
        $.ajax({
            type: 'post',
            url: "<c:url value='/refset/addRefsetOutput.htm' />",
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify(refsetOutput),
            success: function(response) {
                if (response.status == 'SUCCESS') {
                    $('#outConfig').attr('id', 'out_' + response.result.refsetOutputId);
                    $('#curOutputConfig').attr('id', 'Id_' + response.result.refsetOutputId);

                    var columnOutputURL = "<c:url value='/refset/productOutputConfigDetail.htm?contextId=' />" + ${param.contextId} +                         
                        '&elementId=' + ${param.elementId} + 
                        '&elementVersionId=' + ${param.elementVersionId} + 
                        '&refsetOutputId=' + response.result.refsetOutputId;   
                    
                    $('#Id_' + response.result.refsetOutputId).find('td:eq(1)').html('<a href="' + columnOutputURL + '">' + response.result.name + '</a>');
                    $('#Id_' + response.result.refsetOutputId).find('td:eq(2)').html(response.result.filename);
                    $('#Id_' + response.result.refsetOutputId).find('td:eq(3)').html(getLanguageByCode(response.result.languageCode));
                    $('#Id_' + response.result.refsetOutputId).attr('action', 'SAVE');
                    $('#Id_' + response.result.refsetOutputId).attr('refsetOutputId', response.result.refsetOutputId);
                    $('#Id_' + response.result.refsetOutputId).attr('outputName', response.result.name);
                    $('#Id_' + response.result.refsetOutputId).attr('outputFilename', response.result.filename);
                    $('#Id_' + response.result.refsetOutputId).attr('languageCode', response.result.languageCode);
                    
                    $('#out_' + response.result.refsetOutputId).click(function() {
                        handleConfigSelected();
                    });
                    
                    enableAddButton();
                    enableRemoveButton();
                    enableEditButton();
                    disableCancelButton();
                    disableResetButton();
                    $('#export').html('<img class="viewMode" title="Export" src="<c:url value="/img/icons/ExportGrey.jpg" />" />');

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

        return;
    }

    function saveRefsetOutputConfiguration() { 
        hideMessage();
        
        var refsetOutputId = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('refsetOutputId');

        if (refsetOutputId == undefined || refsetOutputId == null) {
            return;
        }

        var errors = validateDuplicateName();

        var errorMsg = '';
        var errorImg = '<img src="<c:url value="/img/icons/Error.png" />">';
        var hasError = false;

        $.each(errors, function(key, value) {                 
            errorMsg += errorImg + value + '<br/>';   

            hasError = true;             
        });

        if (hasError) {     
            showErrorMessages(errorMsg);
            
            return;
        }    

        disableSaveButton();        
        
        var refsetOutput = {'refsetOutputId': refsetOutputId,
                            'refsetContextId': ${param.contextId},
                            'refsetId': ${param.elementId},
                            'name': $.trim($('#outputConfigName').val()),
                            'filename': $.trim($('#outputConfigFileName').val()), 
                            'languageCode': $('#outputConfigLanguage').val()};

        $.ajax({
            type: 'post',
            url: "<c:url value='/refset/saveRefsetOutput.htm' />",
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify(refsetOutput),
            success: function(response) {
                if (response.status == 'SUCCESS') {
                    $('#outConfig').attr('id', 'out_' + refsetOutputId);
                    $('#curOutputConfig').attr('id', 'Id_' + refsetOutputId);

                    var columnOutputURL = "<c:url value='/refset/productOutputConfigDetail.htm?contextId=' />" + ${param.contextId} +                         
                        '&elementId=' + ${param.elementId} + 
                        '&elementVersionId=' + ${param.elementVersionId} + 
                        '&refsetOutputId=' + refsetOutputId;  
                    
                    $('#Id_' + refsetOutputId).find('td:eq(1)').html('<a href="' + columnOutputURL + '">' + $.trim($('#outputConfigName').val()) + '</a>');
                    $('#Id_' + refsetOutputId).find('td:eq(2)').html($.trim($('#outputConfigFileName').val()));
                    $('#Id_' + refsetOutputId).find('td:eq(3)').html(getLanguageByCode($('#outputConfigLanguage').val()));
                    $('#Id_' + refsetOutputId).attr('action', 'SAVE');
                    $('#Id_' + refsetOutputId).attr('refsetOutputId', refsetOutputId);
                    $('#Id_' + refsetOutputId).attr('outputName', $.trim($('#outputConfigName').val()));
                    $('#Id_' + refsetOutputId).attr('outputFilename', $.trim($('#outputConfigFileName').val()));
                    $('#Id_' + refsetOutputId).attr('languageCode', $('#outputConfigLanguage').val());
      
                    $('#out_' + refsetOutputId).unbind('click').click(function() {
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

    function editOutputConfiguration() {
        hideMessage();
        
        var selectedRadioId = $('input[type=radio][name=outputConfig]:checked').attr('id');

        if (selectedRadioId == undefined || selectedRadioId == null) {
            return;
        }

        var refsetOutputId = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('refsetOutputId');

        if (refsetOutputId == undefined || refsetOutputId == null) {
            return;
        }

        var outputConfigName = $.trim($('#Id_' + refsetOutputId).attr('outputName'));
        var outputFileName = $.trim($('#Id_' + refsetOutputId).attr('outputFilename'));
        var languageCode = $('#Id_' + refsetOutputId).attr('languageCode');
        
        $('#Id_' + refsetOutputId).attr('id', 'curOutputConfig');
        
        $('#out_' + refsetOutputId).attr('id', 'outConfig');
        $('#curOutputConfig').find('td:eq(1)').html("<input type='text' id='outputConfigName' name='outputConfigName' maxlength='150' style='width: 200px;'>");
        $('#outputConfigName').val(outputConfigName);
        $('#curOutputConfig').find('td:eq(2)').html("<input type='text' id='outputConfigFileName' name='outputConfigFileName' maxlength='150' style='width: 200px;'>");
        $('#outputConfigFileName').val(outputFileName);        
        $('#curOutputConfig').find('td:eq(3)').html("<select name='language' id='outputConfigLanguage' disabled></select>");
        $('#outputConfigLanguage').append($('<option></option>').attr('value', 'ENG').text('<fmt:message key="common.english" />'));
        $('#outputConfigLanguage').append($('<option></option>').attr('value', 'FRA').text('<fmt:message key="common.french" />'));
        $('#outputConfigLanguage').val(languageCode);

        $('#outputConfigName').keyup(function() {
            processSaveButtonEnable();
            processResetButtonEnable();
        }); 

        $('#outputConfigFileName').keyup(function() {
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

    function processResetButtonEnable() {
        var outputConfigName = $.trim($('#outputConfigName').val());
        var outputConfigFilename = $.trim($('#outputConfigFileName').val());
        var languageCode = $('#outputConfigLanguage').val();

        var savedOutputConfigName = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('outputName');
        var savedOutputConfigFilename = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('outputFilename');
        var savedLanguageCode = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('languageCode');
        
        if (savedOutputConfigName == undefined || savedOutputConfigName == null || 
                savedOutputConfigFilename == undefined || savedOutputConfigFilename == null ||
                languageCode == undefined || languageCode == null) {
            return;
        } 

        if (savedOutputConfigName != outputConfigName || savedLanguageCode != languageCode || 
                savedOutputConfigFilename != outputConfigFilename) {
            enableResetButton();

            return;
        }

        disableResetButton();
        
        return;                
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
        var outputFilename = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('outputFilename');
        var languageCode = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('languageCode');

        $('#outputConfigName').val(outputName);
        $('#outputConfigFileName').val(outputFilename);
        $('#outputConfigLanguage').val(languageCode);
    }

    function deleteRow() {
        $('input[type=radio][name=outputConfig]:checked').closest('tr').remove();
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

        var refsetOutputId = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('refsetOutputId');
        var outputName = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('outputName');
        var outputFilename = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('outputFilename');
        var languageCode = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('languageCode');
            
        $('#outConfig').attr('id', id='out_' + refsetOutputId);
        $('#curOutputConfig').attr('id', 'Id_' + refsetOutputId);

        var columnOutputURL = ""; 

        $('#Id_' + refsetOutputId).find('td:eq(1)').html('<a href="' + columnOutputURL + '">' + outputName + '</a>');
        $('#Id_' + refsetOutputId).find('td:eq(2)').html(outputFilename);
        $('#Id_' + refsetOutputId).find('td:eq(3)').html(getLanguageByCode(languageCode));
        $('#Id_' + refsetOutputId).attr('action', 'SAVE');        

        $('#out_' + refsetOutputId).unbind('click').click(function() {
            handleConfigSelected();
        });

        return;
    }

    function removeOutputConfig() {
        hideMessage();
        
        var selectedRadioId = $('input[type=radio][name=outputConfig]:checked').attr('id');

        if (selectedRadioId == undefined || selectedRadioId == null) {
            return;
        }

        var refsetOutputId = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('refsetOutputId');

        if (refsetOutputId == undefined || refsetOutputId == null) {
            return;
        }

        var exportImg = '<img class="viewMode" title="Export" src="<c:url value="/img/icons/ExportGrey.jpg" />">';
        $('#export').html(exportImg); 

        $.ajax({
            url: "<c:url value='/refset/deleteRefsetOutput.htm' />",
            contentType:"application/json; charset=UTF-8",
            data: {
                'refsetOutputId': refsetOutputId,                 
            },
            success: function(response) {
                if (response.status == 'SUCCESS') {
                    deleteRow();                  

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

    function validateDuplicateName() {
        var selectedRadioId = $('input[type=radio][name=outputConfig]:checked').attr('id');      

        if (selectedRadioId == undefined || selectedRadioId == null) {
            return;
        }

        var outputName = $.trim($('#outputConfigName').val());
        var outputFilename = $.trim($('#outputConfigFileName').val());
        var errors = {};        
        
        $('input[type=radio][name=outputConfig]').each(function() {
            if (!$(this).is(':checked')) {
                var currentOutputName = $.trim($(this).closest('tr').attr('outputName'));
                var currentOutputFilename = $.trim($(this).closest('tr').attr('outputFilename'));

                if (outputName.toUpperCase() == currentOutputName.toUpperCase()) {
                    errors.duplicateOutputName = '<fmt:message key="refset.duplicate.output.name" />';
                }

                if (outputFilename.toUpperCase() == currentOutputFilename.toUpperCase()) {
                    errors.duplicateOutputFilename = '<fmt:message key="refset.duplicate.output.filename" />';
                }
            }
        }); 

        return errors; 
    }     
</script>

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
                </li>
            </ul>
        </span>
    </div>

    <div id="removalConfirmation" style="display: none;">confirm</div> 
    
    <div class="content" style="margin-top: 20px;">
        <table class="listTable" style="width: 60%; margin-top: 0px;" id='productOutputConfiguration'>
            <thead>
                <tr>
                    <th class="tableHeader"></th>
                    <th class="tableHeader"><fmt:message key="refset.product.output.configuration.name" /></th>
                    <th class="tableHeader"><fmt:message key="refset.product.output.file.name" /></th>
                    <th class="tableHeader"><fmt:message key="reports.language" /></th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function() {
        $.ajax({
            url: "<c:url value='/refset/getRefsetOutputConfig.htm' />",
            contentType:"application/json; charset=UTF-8",
            data: {
                'contextId': ${param.contextId},
                'refsetElementId': ${param.elementId}                
            },
            success: function(response) {
                hideProcessingScreen();

                $.each(response, function(index, record) {
                    $('#productOutputConfiguration').find('tbody').append("<tr id='Id_" + record.refsetOutputId + "'><td style='border: 1px solid #96BEBD;'></td>" + 
                        "<td style='border: 1px solid #96BEBD;'></td><td style='border: 1px solid #96BEBD;'></td><td style='border: 1px solid #96BEBD;'></td></tr>");
                     
                    $('#Id_' + record.refsetOutputId).find('td:eq(0)').html("<input type='radio' name='outputConfig' id='out_" + record.refsetOutputId + "' disabled>");

                    var columnOutputURL = "<c:url value='/refset/productOutputConfigDetail.htm?contextId=' />" + ${param.contextId} +                         
                        '&elementId=' + ${param.elementId} + 
                        '&elementVersionId=' + ${param.elementVersionId} + 
                        '&refsetOutputId=' + record.refsetOutputId; 
                            
                    $('#Id_' + record.refsetOutputId).find('td:eq(1)').html('<a href="' + columnOutputURL + '">' + record.name + '</a>');
                    $('#Id_' + record.refsetOutputId).find('td:eq(2)').html(record.filename);
                    $('#Id_' + record.refsetOutputId).find('td:eq(3)').html(getLanguageByCode(record.languageCode));   
                    $('#Id_' + record.refsetOutputId).attr('action', 'SAVE');  
                    $('#Id_' + record.refsetOutputId).attr('refsetOutputId', record.refsetOutputId);
                    $('#Id_' + record.refsetOutputId).attr('outputName', record.name);
                    $('#Id_' + record.refsetOutputId).attr('outputFilename', record.filename);
                    $('#Id_' + record.refsetOutputId).attr('languageCode', record.languageCode);

                    $('#out_' + record.refsetOutputId).unbind('click').click(function() {
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
            },
            error: function(response) {
                hideProcessingScreen();
                showErrorMessages("System error occurred, please contact System administrator.");
            }                    
        });           
    });
</script>
