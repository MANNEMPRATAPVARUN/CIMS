<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<spring:eval var="snomedUrl"
	expression="@applicationProperties.getProperty('snomedservice.url')" />

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
        $("#add").attr('src', '<c:url value="/img/icons/Add.png" />');  
          
        enableAddClickEvent();    
    }

    function disableAddButton() {
        $("#add").attr('src', '<c:url value="/img/icons/AddGrey.png" />');  
        
        disableAddClickEvent();    
    }

    function enableAddClickEvent() {
        $("#add").unbind('click').click(function() {
            addColumnConfiguration();
        });
    }

    function disableAddClickEvent() {
        $('#add').unbind("click");        
    }

    function enableSaveButton() {
        $("#save").attr('src', '<c:url value="/img/icons/Save.png" />');

        var selectedRadioId = $('input[type=radio][name=picklistColumn]:checked').attr('id');

        if (selectedRadioId == 'curPickListColumn') {
            enableSaveNewRecordClickEvent();
        } else {
            enableSaveClickEvent();
        }        
    }

    function disableSaveButton() {
        $("#save").attr('src', '<c:url value="/img/icons/SaveGrey.png" />');
        
        disableSaveClickEvent();
    }

    function enableSaveNewRecordClickEvent() {
        $("#save").unbind('click').click(function() {
            newColumnConfiguration();
        });
    }

    function enableSaveClickEvent() {
        $("#save").unbind('click').click(function() {
            saveColumnConfiguration();
        });
    }

    function disableSaveClickEvent() {
        $('#save').unbind("click");        
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
            removeRecord();
        });
    }

    function disableRemoveClickEvent() {
        $('#remove').unbind("click");        
    }

    function enableCancelButton() {
        $("#cancel").attr('src', '<c:url value="/img/icons/Cancel.png" />'); 
               
        var selectedRadioId = $('input[type=radio][name=picklistColumn]:checked').attr('id');

        if (selectedRadioId == 'curPickListColumn') {
            enableNewColumnCancelClickEvent();
        } else {
            enableCancelClickEvent();
        }
    }
    
    function disableCancelButton() {
        $("#cancel").attr('src', '<c:url value="/img/icons/CancelGrey.png" />');
        
        disableCancelClickEvent();
    }

    function enableNewColumnCancelClickEvent() {
        $("#cancel").unbind('click').click(function() {
            cancelNewColumnConfiguration();
        });
    }

    function enableCancelClickEvent() {
        $("#cancel").unbind('click').click(function() {
            cancelColumnConfiguration();
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
            editColumnConfiguration();
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
            resetColumnConfiguration();
        });
    }

    function disableResetClickEvent() {
        $('#reset').unbind("click");        
    }

    function enableBrowseButton() {
        $("#browse").attr('src', '<c:url value="/img/icons/Browse.jpg" />');
        
        enableBrowseClickEvent();
    }
    
    function disableBrowseButton() {
        $("#browse").attr('src', '<c:url value="/img/icons/BrowseGrey.jpg" />');
        
        disableBrowseClickEvent();
    }

    function enableBrowseClickEvent() {
        var w = 1000;
        var h = 800;

        var left = window.top.outerWidth / 2 + window.top.screenX - w / 2;
        var top = window.top.outerHeight / 2 + window.top.screenY - h / 2;

        left = left > 0 ? left : 1;
        top = top > 0 ? top : 1;
        
        $("#browse").unbind('click').click(function() {
            window.open('${snomedUrl}?version=${sctVersionCode}', 'snomedSearch', 'resizable=yes,width=' + w + ',height=' + h + ',top=' + top + ',left=' + left);
        });
    }

    function disableBrowseClickEvent() {
        $('#browse').unbind("click");        
    }

    function editColumnConfiguration() {
        hideMessage();
        
        var selectedRadioId = $('input[type=radio][name=picklistColumn]:checked').attr('id');
        var ids = selectedRadioId.split('|');
        var recordElementId = ids[0];
        
        var columns = $('#classificationSearch').data('columns');        
        var hasSnomedColumn = false;        
        
        $.each(columns, function(index, e) {
            var cId = 'Id_' + recordElementId + '_' + e.columnElementId;
            var currentInputValue = $('#' + cId).html();                        

            var editableHtml = getLookupProcessorByType(e.columnLookupType).getInputHtmlForEdit(e, currentInputValue);            
             
            if (editableHtml != null) {
                if (currentInputValue != null && currentInputValue != undefined) {                
                    $('#' + cId).attr('currentInputValue', currentInputValue);
                }    
                
                $('#' + cId).html(editableHtml);                
                
                var tdId = 'tdId' + e.columnElementId;
                $('#' + cId).attr('id', tdId);
                
                getLookupProcessorByType(e.columnLookupType).setInputValueForEdit(e);                                
                
                getLookupProcessorByType(e.columnLookupType).processAfterNewRecord(e);                    
            }

            hasSnomedColumn = hasSnomedColumn || e.columnType == 'SCT Concept ID';            
        });

        enableSaveButton();
        enableCancelButton();
        disableEditButton();
        disableResetButton();
        disableAddButton();

        $('#classificationSearch').val('');
        $('#classificationSearch').attr("disabled", true);
        $('input[type=radio][name=picklistColumn]:checked').focus();//added Focus JIRA CSREII-254;        
        $('input[type=radio][name=picklistColumn]').attr("disabled", true);
        
        

        if (hasSnomedColumn) {
            enableBrowseButton();
        }        
    }

    function resetColumnConfiguration() {        
        var columns = $('#classificationSearch').data('columns');        
        $('#browse').data('curSnomedSearchSelection', null);
        
        $.each(columns, function(index, e) {
            var currentInputValue = $('#' + 'tdId' + e.columnElementId).attr('currentInputValue');             

            getLookupProcessorByType(e.columnLookupType).processReset(e, currentInputValue);            
        });  

        disableResetButton();      
    }

    function addColumnConfiguration() {
        var columnTypeSearchPropertyMapper = $('#classificationSearch').data('columnTypeSearchPropertyMapper');    
        var columns = $('#classificationSearch').data('columns');        
        
        var selectedRadioId = $('input[type=radio][name=picklistColumn]:checked').attr('id');

        if (selectedRadioId != undefined) {
            if (selectedRadioId == 'curPickListColumn') {
                return;
            }
            
            var ids = selectedRadioId.split('|');            
            var prevRecordElementId = ids[0];
            
            $.each(columns, function(index, e) {                    
                var changeHtmlForReadOnly = getLookupProcessorByType(e.columnLookupType).getChangeHtmlForReadOnly(e);

                if (changeHtmlForReadOnly != null) {
                    var cId = 'Id_' + prevRecordElementId + '_' + e.columnElementId;

                    $('#' + cId).html(changeHtmlForReadOnly);
                }                       
            });
        }
        
        var rowHtml = '<tr><td style="border: 1px solid #96BEBD;"><input type="radio" name="picklistColumn" id="curPickListColumn" checked="checked" onclick="handleColumnSelected();" /></td>';
                    
        $.each(columns, function(index, e) {
            rowHtml += getLookupProcessorByType(e.columnLookupType).getNewHtml(e);
        });

        rowHtml += '</tr>';

        $("#picklistColumns").find('tbody').prepend(rowHtml);

        var hasSnomedColumn = false;        
        
        $.each(columns, function(index, e) {
            getLookupProcessorByType(e.columnLookupType).processAfterNewRecord(e);

            hasSnomedColumn = hasSnomedColumn || e.columnType == 'SCT Concept ID';
        });
        
        disableAddButton();
        disableEditButton();  
        disableRemoveButton();  
        enableSaveButton();    
        enableCancelButton();

        if (hasSnomedColumn) {
            enableBrowseButton();
        }
        
        $('input[type=radio][name=picklistColumn]').attr("disabled", true);
        $('#classificationSearch').attr("disabled", true);

        return;
    }

    function saveColumnConfiguration() { 
        disableSaveButton();
           
        var selectedRadioId = $('input[type=radio][name=picklistColumn]:checked').attr('id');
        var ids = selectedRadioId.split('|');
        var containerSublist = $('#classificationSearch').data('containerSublist');
        
        var columnData = {'contextId': $('#classificationSearch').data('contextId'),
                          'containerElementId': $('#classificationSearch').data('containerElementId'),
                          'containerElementVersionId': $('#classificationSearch').data('containerElementVersionId'),
                          'recordElementId': ids[0],
                          'recordElementVersionId': ids[1],
                          'containerSublist': containerSublist, 
                          'values': []                           
        };

        var columns = $('#classificationSearch').data('columns');
        var validationRuleMapper = $('#classificationSearch').data('validationRuleMapper');
        var errors = [];
        
        $.each(columns, function(index, e) {
            var editable = getLookupProcessorByType(e.columnLookupType).isColumnEditable(e);            

            if (editable) {  
                var inputValue = getLookupProcessorByType(e.columnLookupType).getInputValue(e);

                if (inputValue != null) {                    
                    columnData.values.push(inputValue); 

                    if (inputValue.textValue != null) {
                        var v = $.trim(inputValue.textValue);
                        
                        if (v != '') {
                            if (validationRuleMapper != undefined && validationRuleMapper != null) {
                                if (validationRuleMapper[e.columnType] != undefined) {
                                    if (!v.match(/^[. 0-9a-zA-Z]+$/)) {
                                        errors.push(validationRuleMapper[e.columnType].message);
                                    }
                                }
                            }
                        }
                    }                   
                }
            }        
        });

        if (errors.length > 0) {
            var errorMsg = '';
            var errorImg = '<img src="<c:url value="/img/icons/Error.png" />">';            

            $.each(errors, function(index, value) {                 
                errorMsg += errorImg + value + '<br/>';
            });

            showErrorMessages(errorMsg);
            enableSaveButton();

            return true;
        }        

        showProcessingScreen();
        
        $.ajax({
             type: 'post',
             url: "<c:url value='/refset/picklist/savePicklistColumnValue.htm' />",
             contentType: "application/json",
             dataType: 'json',
             data: JSON.stringify(columnData),
             success: function(response) {
            	 hideProcessingScreen();
            	 
                 if (response.status == 'SUCCESS') {
                     $('#browse').data('curSnomedSearchSelection', null);
                     $('#classificationSearch').data('prevRecordElementId', ids[0]);
                     
                     disableCancelButton();      
                     disableResetButton(); 
                     disableBrowseButton();                               
                     enableRemoveButton(); 
                     enableEditButton(); 

                     containerSublist ? enableAddButton() : disableAddButton();                                 
                 
                     $('input[type=radio][name=picklistColumn]').attr("disabled", false);    
                     $('#classificationSearch').attr("disabled", false);             
                 
                     $.each(columns, function(index, e) {
                         getLookupProcessorByType(e.columnLookupType).processAfterChange(response.result.elementId, e);                     
                     });                  

                     showInfoMessage(response.message);
                 } else {
                     showErrorMessages(response.message);

                     enableSaveButton();
                 }
             },    
             error : function(response) {
            	 hideProcessingScreen();
            	 
                 if (response.redirect) {
                     window.location.href = response.redirect;

                     return;
                 }
                 
                 var errorMessages = "System error occurred, please contact System administrator.";

                 showErrorMessages(errorMessages);
                 enableSaveButton();
             }            
        });

        return true;  
    }

    function newColumnConfiguration() {  
        disableSaveButton();
        
        var containerSublist = $('#classificationSearch').data('containerSublist');
        
        var columnData = {'contextId': $('#classificationSearch').data('contextId'),
                          'containerElementId': $('#classificationSearch').data('containerElementId'),
                          'containerElementVersionId': $('#classificationSearch').data('containerElementVersionId'),
                          'containerSublist': containerSublist, 
                          'values': []                           
        };

        if (containerSublist) {
            columnData.recordElementId = $('#classificationSearch').data('recordElementId');
            columnData.recordElementVersionId = $('#classificationSearch').data('recordElementVersionId');
        }

        var columns = $('#classificationSearch').data('columns');
        var validationRuleMapper = $('#classificationSearch').data('validationRuleMapper');
        var errors = [];
        
        $.each(columns, function(index, e) {
            var inputValue = getLookupProcessorByType(e.columnLookupType).getInputValue(e);           

            if (inputValue != null) {                    
                columnData.values.push(inputValue); 

                if (inputValue.textValue != null) {
                    var v = $.trim(inputValue.textValue);
                    
                    if (v != '') {
                        if (validationRuleMapper != undefined && validationRuleMapper != null) {
                            if (validationRuleMapper[e.columnType] != undefined) {
                                if (!v.match(/^[. 0-9a-zA-Z]+$/)) {
                                    errors.push(validationRuleMapper[e.columnType].message);
                                }
                            }
                        }
                    }
                }                   
            }           
        });

        if (errors.length > 0) {
            var errorMsg = '';
            var errorImg = '<img src="<c:url value="/img/icons/Error.png" />">';            

            $.each(errors, function(index, value) {                 
                errorMsg += errorImg + value + '<br/>';
            });

            showErrorMessages(errorMsg);
            enableSaveButton();

            return true;
        }            

        showProcessingScreen();
        
        $.ajax({
             type: 'post',
             url: "<c:url value='/refset/picklist/addPicklistColumnValue.htm' />",
             contentType: "application/json",
             dataType: 'json',
             data: JSON.stringify(columnData),
             success: function(response) {
            	 hideProcessingScreen();
            	 
                 if (response.status == 'SUCCESS') {
                     $('#browse').data('curSnomedSearchSelection', null);
                     
                     $('#curPickListColumn').attr('id', response.result.elementId + '|' + response.result.elementVersionId);
                     $('#classificationSearch').data('prevRecordElementId', response.result.elementId);
                 
                     if (!containerSublist) {
                         var conceptIds = $('#classificationSearch').data('conceptIds');
                         var curClassificationSearchSelection = $('#classificationSearch').data('curClassificationSearchSelection');
                 
                         conceptIds[curClassificationSearchSelection.conceptId] = response.result.elementId + '|' + response.result.elementVersionId;

                         $('#classificationSearch').data('conceptIds', conceptIds);
                     }                 
                     
                     disableCancelButton();     
                     disableResetButton();
                     disableBrowseButton();                
                     enableRemoveButton(); 

                     containerSublist ? enableAddButton() : disableAddButton();                         
                             
                     $('#classificationSearch').val('');
                     $('#classificationSearch').attr("disabled", false);
                     $('input[type=radio][name=picklistColumn]').attr("disabled", false);
                    
                     var editable = false;
                 
                     $.each(columns, function(index, e) {
                         getLookupProcessorByType(e.columnLookupType).processAfterSave(response.result.elementId, e);
                         editable = editable || getLookupProcessorByType(e.columnLookupType).isColumnEditable(e);

                         var readOnlyHtml = getLookupProcessorByType(e.columnLookupType).getInputHtmlForReadOnly(e);

                         if (readOnlyHtml != null) {
                             var cId = 'Id_' + response.result.elementId + '_' + e.columnElementId;
                              
                             $('#' + cId).html(readOnlyHtml);    
                         }
                     });

                     if (editable) {
                         enableEditButton();
                     }                    

                     showInfoMessage(response.message);
                 } else {                    
                     showErrorMessages(response.message);

                     enableSaveButton();
                 }
             },    
             error : function(response) {
            	 hideProcessingScreen();
            	 
                 if (response.redirect) {
                     window.location.href = response.redirect;

                     return;
                 }
                 
                 var errorMessages = "System error occurred, please contact System administrator.";

                 showErrorMessages(errorMessages);
                 enableSaveButton();
             }            
        }); 

        return true; 
    }

    function removeRecord() {
        var containerSublist = $('#classificationSearch').data('containerSublist');    
        
        $("#removalConfirmation").text(!containerSublist ? 
                'Do you want to Delete the Common Term Record from the Picklist?' : 'Do you want to Delete the Record from the Sublist?');

        $('#removalConfirmation').dialog({
            title : 'Confirmation: Removal Record',
            width : 380,
            height : 180,
            modal : true,
            closeOnEscape : true,
            resizable : false,
            draggable : false,
            zIndex : 10000,
            buttons : [ {
                text : 'Remove',
                click : function() {
                    $(this).dialog('close');
                    removeColumnConfiguration();
                }
            }, {
                text : 'Cancel',
                click : function() {
                    $(this).dialog('close');
                }
            } ]
        });
    }

    function removeColumnConfiguration() {
        var selectedRadioId = $('input[type=radio][name=picklistColumn]:checked').attr('id');
        var ids = selectedRadioId.split('|');
        var containerSublist = $('#classificationSearch').data('containerSublist');

        $.ajax({
            url: "<c:url value='/refset/picklist/deletePicklistColumnValue.htm' />",
            contentType:"application/json; charset=UTF-8",
            data: {
                'contextId': $('#classificationSearch').data('contextId'),
                'elementId': ids[0],
                'elementVersionId': ids[1],
                'containerSublist': containerSublist      
            },
            success: function(response) {
                if (response.status == 'SUCCESS') {
                    showInfoMessage(response.message);

                    deleteRow();
                    $('input[type=radio][name=picklistColumn]').attr("disabled", false);                
                    $('#classificationSearch').attr("disabled", false);

                    disableRemoveButton();
                    disableEditButton();
                    disableResetButton();
                    disableCancelButton();
                    disableBrowseButton();    

                    var conceptIds = $('#classificationSearch').data('conceptIds');
                    var elementId = ids[0] + '|' + ids[1];
                    var deleteConceptId  = 0;

                    $.each(conceptIds, function(conceptId, eId) { 
                        if (eId == elementId) {
                            deleteConceptId = conceptId;
                        }  
                    });               

                    delete conceptIds[deleteConceptId];
                    $('#classificationSearch').data('conceptIds', conceptIds);
                } else {
                    showErrorMessages(response.message);
                }
            },    
            error : function(response) {
                var errorMessages = "System error occurred, please contact System administrator.";

                showErrorMessages(errorMessages);
            }   
        });
    }

    function cancelNewColumnConfiguration() {
        hideMessage();
        deleteRow();
        
        $('input[type=radio][name=picklistColumn]').attr("disabled", false);
        $('#classificationSearch').val('');
        $('#classificationSearch').attr("disabled", false);    
        $('#browse').data('curSnomedSearchSelection', null);

        var containerSublist = $('#classificationSearch').data('containerSublist');

        disableCancelButton();
        disableSaveButton();                      
        disableRemoveButton(); 
        disableResetButton();
        disableBrowseButton();        
        disableEditButton(); 

        containerSublist ? enableAddButton() : disableAddButton();
    }

    function cancelColumnConfiguration() {
        hideMessage();
        
        var columns = $('#classificationSearch').data('columns');        

        var selectedRadioId = $('input[type=radio][name=picklistColumn]:checked').attr('id');
        var ids = selectedRadioId.split('|');

        $.each(columns, function(index, e) {
            var tdId = 'tdId' + e.columnElementId;
            var cId = 'Id_' + ids[0] + '_' + e.columnElementId;

            var currentInputValue = $('#' + tdId).attr('currentInputValue');

            if (currentInputValue != undefined) {
                $('#' + tdId).attr('id', cId);
                
                $('#' + cId).html(currentInputValue);
            }   

            getLookupProcessorByType(e.columnLookupType).processCancel(e);        
        });

        var containerSublist = $('#classificationSearch').data('containerSublist');
        
        containerSublist ? enableAddButton() : disableAddButton();
        
        disableCancelButton();    
        disableResetButton();    
        disableSaveButton();    
        disableBrowseButton();                  
        enableRemoveButton();
        enableEditButton();         

        $('input[type=radio][name=picklistColumn]').attr("disabled", false);
        $('#classificationSearch').val('');
        $('#classificationSearch').attr("disabled", false);    
        $('#browse').data('curSnomedSearchSelection', null);
    }

    function deleteRow() {
        $('input[type=radio][name=picklistColumn]:checked').closest('tr').remove();        
    }

    function handleColumnSelected() {
        hideMessage();
        
        <c:if test="${refsetPermission == 'WRITE'}">    
            enableRemoveButton();
        </c:if>    

        var selectedRadioId = $('input[type=radio][name=picklistColumn]:checked').attr('id');

        if (selectedRadioId == 'curPickListColumn') {
            return true;
        }

        var ids = selectedRadioId.split('|');
        var recordElementId = ids[0];
        var prevRecordElementId = $('#classificationSearch').data('prevRecordElementId');
        $('#classificationSearch').data('prevRecordElementId', recordElementId);        

        var columns = $('#classificationSearch').data('columns');
        var subColumns = $('#classificationSearch').data('subColumns');
        var enableEdit = false;

        $.each(columns, function(index, e) {            
            enableEdit = enableEdit || getLookupProcessorByType(e.columnLookupType).isColumnEditable(e);

            var readOnlyHtml = getLookupProcessorByType(e.columnLookupType).getInputHtmlForReadOnly(e);

            if (readOnlyHtml != null) {  
                if (prevRecordElementId != undefined) {
                    var changeHtmlForReadOnly = getLookupProcessorByType(e.columnLookupType).getChangeHtmlForReadOnly(e);

                    if (changeHtmlForReadOnly != null) {
                        var cId = 'Id_' + prevRecordElementId + '_' + e.columnElementId;

                        $('#' + cId).html(changeHtmlForReadOnly);
                    }
                } 
                              
                var cId = 'Id_' + recordElementId + '_' + e.columnElementId;
                
                $('#' + cId).html(readOnlyHtml);                             
            }            
        });

        <c:if test="${refsetPermission == 'WRITE'}">
            if (enableEdit) {
                enableEditButton();
            }
        </c:if>  

        var containerSublist = $('#classificationSearch').data('containerSublist'); 

        if (!containerSublist) {   
            var conceptIds = $('#classificationSearch').data('conceptIds');

            if (conceptIds != undefined && conceptIds != null) {
                $.each(conceptIds, function(key, el) {
                    var ids = el.split('|');

                    if (recordElementId == ids[0]) {
                        $('#classificationSearch').data('curConceptId', key);
                    }
                });
            }
        }

        return true;        
    }

    /**
     * the 'factory' object that creates new products(column lookup type processor)
     * implements 'factoryMethod' which returns newly created column lookup type processor.
     */
    function columnLookupTypeProcessorFactory() {
        this.createLookupProcessor = function(columnLookupType) {
            var columnLookupTypeProcessor;
            console.log(columnLookupType);
            if (columnLookupType == 'LOOKUP') {
                columnLookupTypeProcessor = new lookupProcessor();
            } else if (columnLookupType == 'SUBLIST') {
                columnLookupTypeProcessor = new sublistProcessor();
            } else if (columnLookupType == 'EXTEND_LOOKUP') {
                columnLookupTypeProcessor = new extendLookupProcessor();
            } else if (columnLookupType == 'FREE_TYPE') {
                columnLookupTypeProcessor = new freeTypeProcessor();
            } else if (columnLookupType == 'SNOMED') {
                columnLookupTypeProcessor = new snomedProcessor();
            } else {
                columnLookupTypeProcessor = new NAProcessor()
            }
            
            columnLookupTypeProcessor.columnLookupType = columnLookupType;
            
            return columnLookupTypeProcessor;
        }  
    }

    var lookupProcessor = function() {
        this.getViewHtml = function(column, record) {
            var cId = 'Id_' + record.recordIdentifier.elementId + '_' + column.columnElementId;
            var v = '';

            if (record.values[column.columnElementId] != undefined) {
                if (record.values[column.columnElementId].textValue != null) {
                    v = record.values[column.columnElementId].textValue;
                }
            }              
              
            return '<td style="border: 1px solid #96BEBD;" id="' + cId + '">' + v + '</td>';
        };

        this.getNewHtml = function(column) {
            var columnTypeSearchPropertyMapper = $('#classificationSearch').data('columnTypeSearchPropertyMapper');                  
            var curClassificationSearchSelection = $('#classificationSearch').data('curClassificationSearchSelection');
              
            var classification = columnTypeSearchPropertyMapper[column.columnType];
            var v = '';

            if (classification != undefined) {
                if (curClassificationSearchSelection[classification.searchPropertyName] != undefined) {
                    v = curClassificationSearchSelection[classification.searchPropertyName];
                }    
            }

            var cId = 'id-' + column.columnElementId + '-' + curClassificationSearchSelection.conceptId;
            
            return '<td style="border: 1px solid #96BEBD;" id="' + cId + '">' + v + '</td>';                    
        };

        this.processAfterNewRecord = function(column) {
            var columnTypeSearchPropertyMapper = $('#classificationSearch').data('columnTypeSearchPropertyMapper');
            var curClassificationSearchSelection = $('#classificationSearch').data('curClassificationSearchSelection');
                
            var classification = columnTypeSearchPropertyMapper[column.columnType];
            var v = '';
            
            if (classification != undefined) { 
                if (curClassificationSearchSelection[classification.searchPropertyName] != undefined) {
                    v = curClassificationSearchSelection[classification.searchPropertyName];
                }
                
                if (classification.codeFormatter) {
                    $.ajax({                
                        url: "<c:url value='/refset/picklist/getCodeFormatterResult.htm' />",
                        contentType:"application/json; charset=UTF-8",
                        data: {
                            columnType: column.columnType,
                            conceptId: curClassificationSearchSelection.conceptId,
                            conceptValue : v,
                            columnElementId: column.columnElementId                                   
                        },
                        success: function(data) {      
                            var uId = data.columnElementId + '-' + data.conceptId;              
                            var tdId = 'id-' + uId;
                
                            $("#" + tdId).html(data.concepetValue);                    
                        }
                    });
                }
            }
        };

        this.processAfterSearch = function(column, elementId) {
            return;
        };

        this.getInputValue = function(column) {
            var columnTypeSearchPropertyMapper = $('#classificationSearch').data('columnTypeSearchPropertyMapper');
            var curClassificationSearchSelection = $('#classificationSearch').data('curClassificationSearchSelection');
              
            var classification = columnTypeSearchPropertyMapper[column.columnType];
            var v = '';
            var inputValue = null;

            if (classification != undefined) {
                if (curClassificationSearchSelection[classification.searchPropertyName] != undefined) {
                    v = curClassificationSearchSelection[classification.searchPropertyName];

                    if (classification.codeFormatter) {
                        var uId = 'id-' + column.columnElementId + '-' + curClassificationSearchSelection.conceptId;  
                    
                        v = $.trim($("#" + uId).text());
                    }

                    inputValue = {'columnElementId': column.columnElementId,
                                  'columnElementVersionId': column.columnElementVersionId,
                                  'idValue': curClassificationSearchSelection[classification.searchIdName],
                                  'textValue': v,
                                  'languageCode': classification.languageCode};
                }
            }

            return inputValue;
        };  

        this.processAfterSave = function(recordElementId, column) {
            return;
        };    

        this.isColumnEditable = function(column) {
            return false;
        };  

        this.getInputHtmlForEdit = function(column, inputValue) {     
            return null;    
        };    

        this.setInputValueForEdit = function(column) {     
            return;      
        };

        this.getInputHtmlForReadOnly = function(column) {
            return null;
        };

        this.getChangeHtmlForReadOnly = function(column) {              
            return null;
        };

        this.processReset = function(column, resetValue) {
            return;
        };   

        this.processAfterChange = function(recordElementId, column) {
            return;            
        };        

        this.processCancel = function(column) {
            return;
        };
    };

    var sublistProcessor = function() {
        this.getViewHtml = function(column, record) {
            var cId = 'Id_' + record.recordIdentifier.elementId + '_' + column.columnElementId;
            var subColumns = $('#classificationSearch').data('subColumns');
            
            return subColumns[column.columnElementId] != undefined ? '<td style="border: 1px solid #96BEBD;" id="' + cId + '"><img class="viewMode" title="Sublist Add" src="' + 
                       '<c:url value="/img/icons/SublistGreyIcon.jpg" />' + '" /></td>' : '<td style="border: 1px solid #96BEBD;" id="' + cId + '"></td>';
        };

        this.getNewHtml = function(column) {
            var tdId = 'tdId' + column.columnElementId;
            
            return '<td style="border: 1px solid #96BEBD;" id="' + tdId + '"></td>';
        };

        this.processAfterNewRecord = function(column) {
            return;
        };

        this.processAfterSearch = function(column, elementId) {
            return;
        };

        this.getInputValue = function(column) {
            return null;
        };

        this.processAfterSave = function(recordElementId, column) {            
            var tdId = 'tdId' + column.columnElementId;        
            var cId = 'Id_' + recordElementId + '_' + column.columnElementId;
                        
            $('#' + tdId).attr('id', cId);
        };

        this.isColumnEditable = function(column) {
            var subColumns = $('#classificationSearch').data('subColumns');           
            
            return subColumns[column.columnElementId] != undefined ? true : false;
        };

        this.getInputHtmlForEdit = function(column, inputValue) {     
            return '&nbsp;';    
        };

        this.setInputValueForEdit = function(column) {     
            return;      
        };

        this.getInputHtmlForReadOnly = function(column) {     
            var subColumns = $('#classificationSearch').data('subColumns');
                       
            return subColumns[column.columnElementId] != undefined ? '<img id="sublist" class="viewMode" title="Sublist Add" src="' + 
                    '<c:url value="/img/icons/SublistIcon.jpg" />' + '" onclick="getSublistView(' + column.columnElementId + ');" />' : '&nbsp;';    
        };

        this.getChangeHtmlForReadOnly = function(column) {  
            var subColumns = $('#classificationSearch').data('subColumns');           
            
            return subColumns[column.columnElementId] != undefined ?
                     '<img id="sublist" class="viewMode" title="Sublist Add" src="' + '<c:url value="/img/icons/SublistGreyIcon.jpg" />' + '" />' : '&nbsp;';
        };

        this.processReset = function(column, resetValue) {
            return;
        };

        this.processAfterChange = function(recordElementId, column) {            
            var tdId = 'tdId' + column.columnElementId;        
            var cId = 'Id_' + recordElementId + '_' + column.columnElementId;
            
            $('#' + tdId).attr('id', cId); 
            $('#' + cId).html(this.getInputHtmlForReadOnly(column));       
            
            return;            
        };
        
        this.processCancel = function(column) {
            return;
        };
    };

    var extendLookupProcessor = function() {        
        this.getViewHtml = function(column, record) {
            var cId = 'Id_' + record.recordIdentifier.elementId + '_' + column.columnElementId;
            var savedValue = record.values[column.columnElementId] != undefined ? record.values[column.columnElementId].textValue != null ? 
                    record.values[column.columnElementId].textValue : '' : '';
                                                
            return '<td style="border: 1px solid #96BEBD;" id="' + cId + '">' + savedValue + '</td>';
        };

        this.getNewHtml = function(column) {
            var inputId = 'fId' + column.columnElementId;
            var tdId = 'tdId' + column.columnElementId;

            return '<td style="border: 1px solid #96BEBD;" id="' + tdId + '"><input type="text" id="' + inputId + '" class="searchbox" maxlength="600" style="width: 80px; position:relative; z-index: 10000" /></td>';
        };

        this.processAfterNewRecord = function(column) {
            var inputId = 'fId' + column.columnElementId;
            var conceptId = getCurrentConceptId();
            
            var autoCompleteOption = {
                source: function(request, response) {
                    $.ajax({
                          url: "<c:url value='/refset/picklist/getFreeSearchResult.htm' />",
                          contentType: "application/json; charset=UTF-8",
                          data: {
                              term : request.term,    
                              columnType: column.columnType,
                              conceptId: conceptId,
                              maxResults: 20                       
                          },
                          success: function(data) {
                              response($.map(data, function(item) {
                                  return {
                                      label: item,
                                      value: item
                                  }
                              }));
                          }
                    });
                },
                minLength: 2,
                position: { collision: "flip none" }
            };            

            $('#' + inputId).autocomplete(autoCompleteOption);  

            var selectedRadioId = $('input[type=radio][name=picklistColumn]:checked').attr('id');             

            if (selectedRadioId != 'curPickListColumn') {
                $('#' + inputId).keyup(function() {
                    enableResetButton();
                });
            }  

            $('#' + inputId).keypress(function() {                
                var cs = $(this).val().length;

                if (cs > 10) {
                    $(this).width(80 + (cs - 10) * 5);
                }
            });             

            var typingTimer;                
            var doneTypingInterval = 2000;  

            $('#' + inputId).keyup(function() { 
                clearTimeout(typingTimer);               
                
                if ($('#' + inputId).val) {
                    typingTimer = setTimeout(function() { 
                        $('#' + inputId).width(80);
                    }, doneTypingInterval);
                }
            });     
        };

        this.processAfterSearch = function(column, elementId) {
            return;
        };

        this.getInputValue = function(column) {
            var inputId = 'fId' + column.columnElementId; 
            var conceptId = getCurrentConceptId();              
        
            return {'columnElementId': column.columnElementId,
                    'columnElementVersionId': column.columnElementVersionId,   
                    'idValue': conceptId,                                     
                    'textValue': $("#" + inputId).val()};
        };

        this.processAfterSave = function(recordElementId, column) {
            var inputId = 'fId' + column.columnElementId;
            var inputValue = $('#' + inputId).val();

            $('#' + inputId).remove();
            
            var tdId = 'tdId' + column.columnElementId;        
            var cId = 'Id_' + recordElementId + '_' + column.columnElementId;        
                
            $('#' + tdId).html(inputValue);
            $('#' + tdId).attr('id', cId);            
        };

        this.isColumnEditable = function(column) {
            return true;
        };

        this.getInputHtmlForEdit = function(column, inputValue) {     
            var inputId = 'fId' + column.columnElementId;
                          
            return '<input type="text" id="' + inputId + '" class="searchbox" maxlength="600" style="width: 80px; position:relative; z-index: 10000" />';         
        };

        this.setInputValueForEdit = function(column) {     
            var inputId = 'fId' + column.columnElementId;
            var tdId = 'tdId' + column.columnElementId;

            var currentValue = $('#' + tdId).attr('currentinputvalue');

            if (currentValue != null && currentValue != undefined) {
                $('#' + inputId).val(currentValue).focus();//added Focus JIRA CSREII-254
            }       
        };

        this.getInputHtmlForReadOnly = function(column) {
            return null;
        };

        this.getChangeHtmlForReadOnly = function(column) {              
            return null;
        };

        this.processReset = function(column, resetValue) {
            if (resetValue != undefined) {                
                var inputId = 'fId' + column.columnElementId;

                $('#' + inputId).val(resetValue);
            }
            
            return;
        };    

        this.processAfterChange = function(recordElementId, column) {            
            var inputId = 'fId' + column.columnElementId;
            var inputValue = $('#' + inputId).val();
            
            $('#' + inputId).remove();
                
            var tdId = 'tdId' + column.columnElementId;        
            var cId = 'Id_' + recordElementId + '_' + column.columnElementId;    
                
            $('#' + tdId).html(inputValue);
            $('#' + tdId).attr('id', cId);                    
        };        

        this.processCancel = function(column) {
            return;
        };   
    };

    var freeTypeProcessor = function() {
        this.getViewHtml = function(column, record) {
            var cId = 'Id_' + record.recordIdentifier.elementId + '_' + column.columnElementId;            
            var savedValue = record.values[column.columnElementId] != undefined ? record.values[column.columnElementId].textValue != null ? 
                    record.values[column.columnElementId].textValue : '' : '';
                                                
            return '<td style="border: 1px solid #96BEBD;" id="' + cId + '">' + savedValue + '</td>';
        };

        this.getNewHtml = function(column) {
            var inputId = 'fId' + column.columnElementId;
            var tdId = 'tdId' + column.columnElementId;

            return '<td style="border: 1px solid #96BEBD;" id="' + tdId + '"><input type="text" id="' + inputId + '" class="searchbox" maxlength="600" style="width: 80px; position:relative; z-index: 10000" /></td>';
        };

        this.processAfterNewRecord = function(column) {
            var inputId = 'fId' + column.columnElementId;
            var selectedRadioId = $('input[type=radio][name=picklistColumn]:checked').attr('id');             

            if (selectedRadioId != 'curPickListColumn') {
                $('#' + inputId).keyup(function() {
                    enableResetButton();
                });
            } 

            $('#' + inputId).keypress(function() {                
                var cs = $(this).val().length;

                if (cs > 10) {
                    $(this).width(80 + (cs - 10) * 5);
                }
            });             

            var typingTimer;                
            var doneTypingInterval = 2000;  

            $('#' + inputId).keyup(function() { 
                clearTimeout(typingTimer);               
                
                if ($('#' + inputId).val) {
                    typingTimer = setTimeout(function() { 
                        $('#' + inputId).width(80);
                    }, doneTypingInterval);
                }
            });         
        };

        this.processAfterSearch = function(column, elementId) {
            return;
        };

        this.getInputValue = function(column) {
            var inputId = 'fId' + column.columnElementId;                
        
            return {'columnElementId': column.columnElementId,
                    'columnElementVersionId': column.columnElementVersionId,                                        
                    'textValue': $("#" + inputId).val()};
        };

        this.processAfterSave = function(recordElementId, column) {            
            var inputId = 'fId' + column.columnElementId;
            var inputValue = $('#' + inputId).val();

            $('#' + inputId).remove();
            
            var tdId = 'tdId' + column.columnElementId;        
            var cId = 'Id_' + recordElementId + '_' + column.columnElementId;        
                
            $('#' + tdId).html(inputValue);
            $('#' + tdId).attr('id', cId);        
        };

        this.isColumnEditable = function(column) {
            return true;
        };

        this.getInputHtmlForEdit = function(column, inputValue) {     
            var inputId = 'fId' + column.columnElementId;  
              
            return '<input type="text" id="' + inputId + '" class="searchbox" maxlength="600" style="width: 80px; position:relative; z-index: 10000" />';         
        };

        this.setInputValueForEdit = function(column) {     
            var inputId = 'fId' + column.columnElementId;
                          
            var tdId = 'tdId' + column.columnElementId;

            var currentValue = $('#' + tdId).attr('currentinputvalue');

            if (currentValue != null && currentValue != undefined) {
                $('#' + inputId).val(currentValue).focus();//added Focus JIRA CSREII-254
            }           
        };

        this.getInputHtmlForReadOnly = function(column) {
            return null;
        };

        this.getChangeHtmlForReadOnly = function(column) {              
            return null;
        };

        this.processReset = function(column, resetValue) {
            if (resetValue != undefined) {
                var inputId = 'fId' + column.columnElementId;

                $('#' + inputId).val(resetValue);
            }            
            
            return;
        };

        this.processAfterChange = function(recordElementId, column) {
            var inputId = 'fId' + column.columnElementId;
            var inputValue = $('#' + inputId).val();

            $('#' + inputId).remove();        
            
            var tdId = 'tdId' + column.columnElementId;        
            var cId = 'Id_' + recordElementId + '_' + column.columnElementId;        
                
            $('#' + tdId).html(inputValue);
            $('#' + tdId).attr('id', cId);            
        };       

        this.processCancel = function(column) {
            return;
        };
    };

    var NAProcessor = function() {
        this.getViewHtml = function(column, record) {
            return '<td style="border: 1px solid #96BEBD;"></td>';
        };

        this.getNewHtml = function(column) {
            return '<td style="border: 1px solid #96BEBD;"></td>';
        };

        this.processAfterNewRecord = function(column) {
            return;
        };

        this.processAfterSearch = function(column, elementId) {
            return;
        };

        this.getInputValue = function(column) {
            return null;
        };

        this.processAfterSave = function(recordElementId, column) {
            return;
        };

        this.isColumnEditable = function(column) {
            return false;
        };

        this.getInputHtmlForEdit = function(column, inputValue) {     
            return null;    
        };

        this.setInputValueForEdit = function(column) {     
            return;      
        };

        this.getInputHtmlForReadOnly = function(column) {
            return null;
        };

        this.getChangeHtmlForReadOnly = function(column) {              
            return null;
        };

        this.processReset = function(column, resetValue) {            
            return;
        };    

        this.processAfterChange = function(recordElementId, column) {
            return;        
        };
       
        this.processCancel = function(column) {
            return;
        };
    };

    var snomedProcessor = function() {
        this.getViewHtml = function(column, record) {
            var cId = 'Id_' + record.recordIdentifier.elementId + '_' + column.columnElementId;
            var v = '';  
            var selectedId = '';
            
            if (record.values[column.columnElementId] != undefined) {
                if (record.values[column.columnElementId].textValue != null) {
                    v = record.values[column.columnElementId].textValue;                     
                }

                if (record.values[column.columnElementId].idValue != null) {
                    selectedId = record.values[column.columnElementId].idValue;
                }
            }                                       
              
            return '<td style="border: 1px solid #96BEBD;" id="' + cId + '" selectedId="' + selectedId + '">' + v + '</td>';
        };

        this.getNewHtml = function(column) {
            var cId = 'id-' + column.columnElementId;
            
            return '<td style="border: 1px solid #96BEBD;" id="' + cId + '"></td>';    
        };

        this.processAfterNewRecord = function(column) {
            return;
        };

        this.processAfterSearch = function(column, elementId) {
            var columnTypeSearchPropertyMapper = $('#browse').data('snomedSearchPropertyMapper');
            var curSnomedSearchSelection = $('#browse').data('curSnomedSearchSelection');
              
            var classification = columnTypeSearchPropertyMapper[column.columnType];    

            if (classification != undefined) {
                if (curSnomedSearchSelection != undefined && curSnomedSearchSelection != null) {
                    if (curSnomedSearchSelection[classification.searchPropertyName] != undefined) {    
                        var v = curSnomedSearchSelection[classification.searchPropertyName];                       

                        $('#' + elementId).html(v != undefined && v != null ? v : '');

                        if (curSnomedSearchSelection[classification.searchIdName] != undefined && 
                                curSnomedSearchSelection[classification.searchIdName] != null) {
                            $('#' + elementId).attr('selectedId', curSnomedSearchSelection[classification.searchIdName]);

                            var selectedRadioId = $('input[type=radio][name=picklistColumn]:checked').attr('id');  
                              
                            if (selectedRadioId != 'curPickListColumn') {
                                enableResetButton();
                            }
                        }                        
                    }
                }
            }            
              
            return;
        };

        this.getInputValue = function(column) {            
            var selectedRadioId = $('input[type=radio][name=picklistColumn]:checked').attr('id');    
            var elementId = null;

            if (selectedRadioId == 'curPickListColumn') {
                elementId = 'id-' + column.columnElementId;
            } else {
                var ids = selectedRadioId.split('|');
                elementId = 'Id_' + ids[0] + '_' + column.columnElementId;
            }
              
            var snomedSearchPropertyMapper = $('#browse').data('snomedSearchPropertyMapper');      
            var classification = snomedSearchPropertyMapper[column.columnType];    
                    
            var inputValue = null;            
            
            var idValue = $('#' + elementId).attr('selectedId');
            var textValue = $('#' + elementId).html();            
                
            if (idValue != undefined && idValue != null && idValue != 'null') {
                inputValue = {'columnElementId': column.columnElementId,
                              'columnElementVersionId': column.columnElementVersionId,
                              'idValue': idValue,
                              'textValue': textValue,
                              'languageCode': classification != undefined && classification != null ? classification.languageCode : 'NOLANGUAGE'};    
            }            
            
            return inputValue;
        };

        this.setInputValueForEdit = function(column) {     
            return;      
        };

        this.processAfterSave = function(recordElementId, column) {
            var tdId = 'id-' + column.columnElementId;        
            var cId = 'Id_' + recordElementId + '_' + column.columnElementId;
                        
            $('#' + tdId).attr('id', cId);
            
            return;
        };

        this.isColumnEditable = function(column) {
            return true;
        };

        this.getInputHtmlForEdit = function(column, inputValue) {
            var selectedRadioId = $('input[type=radio][name=picklistColumn]:checked').attr('id');    
            var elementId = null;

            if (selectedRadioId != 'curPickListColumn') {               
                var ids = selectedRadioId.split('|');
                elementId = 'Id_' + ids[0] + '_' + column.columnElementId;
            } 

            if (elementId != null) {
                $('#' + elementId).attr('currentInputValue', inputValue);
            } 
            
            return null;    
        };

        this.getInputHtmlForReadOnly = function(column) {
            return null;
        };

        this.getChangeHtmlForReadOnly = function(column) {              
            return null;
        };

        this.processReset = function(column, resetValue) { 
            var selectedRadioId = $('input[type=radio][name=picklistColumn]:checked').attr('id');    
            var elementId = null;

            if (selectedRadioId != 'curPickListColumn') {               
                var ids = selectedRadioId.split('|');
                elementId = 'Id_' + ids[0] + '_' + column.columnElementId;
            } 

            if (elementId != null) { 
                var resetValue = $('#' + elementId).attr('currentInputValue');

                if (resetValue != undefined) {               
                    $('#' + elementId).html(resetValue);                    
                } 

                $('#' + elementId).removeAttr('selectedId');             
            }
                     
            return;
        };    

        this.processAfterChange = function(recordElementId, column) {
            return;        
        };
       
        this.processCancel = function(column) {
            this.processReset(column, null);

            return;
        };
    };

    var factory = new columnLookupTypeProcessorFactory();
    var cLookupTypeProcessors = [];    
    
    cLookupTypeProcessors.push(factory.createLookupProcessor('LOOKUP'));
    cLookupTypeProcessors.push(factory.createLookupProcessor('SUBLIST'));
    cLookupTypeProcessors.push(factory.createLookupProcessor('EXTEND_LOOKUP'));
    cLookupTypeProcessors.push(factory.createLookupProcessor('FREE_TYPE'));
    cLookupTypeProcessors.push(factory.createLookupProcessor('SNOMED'));
    cLookupTypeProcessors.push(factory.createLookupProcessor('NA'));   

    function getLookupProcessorByType(lookupType) {
        var processor;
        
        for (var i = 0; i < cLookupTypeProcessors.length; i++) {
            if (lookupType == cLookupTypeProcessors[i].columnLookupType) {
                processor = cLookupTypeProcessors[i];
            }
        }
        
        return processor;
    }; 

    function getSublistView(columnElementId) { 
    	hideMessage();
        
        var subColumns = $('#classificationSearch').data('subColumns');
        var selectedSubColumn = subColumns[columnElementId];
        var containerElementVersionId = null;

        if (selectedSubColumn == undefined) {
            return;
        } 

        var selectedRadioId = $('input[type=radio][name=picklistColumn]:checked').attr('id');
        var ids = selectedRadioId.split('|');        
        
        var columns = $('#classificationSearch').data('columns');        
        
        $.each(columns, function(s, e) {
            if (e.columnElementId == columnElementId) {
                title = e.columnName;
                containerElementVersionId = e.columnElementVersionId;
            }           
        });    

        var w = 1200;
        var h = 800;

        var left = window.top.outerWidth / 2 + window.top.screenX - w / 2;
        var top = window.top.outerHeight / 2 + window.top.screenY - h / 2;

        left = left > 0 ? left : 1;
        top = top > 0 ? top : 1;

        var conceptId = getCurrentConceptId();
        var sublistURL = "<c:url value='/refset/picklist/viewSublist.htm' />" + '?contextId=' + ${param.contextId} + 
                         '&picklistElementId=' + ${param.picklistElementId} + '&picklistElementVersionId=' + ${param.picklistElementVersionId} + 
                         '&parentColumnId=' + columnElementId + '&parentColumnVersionId=' + containerElementVersionId + 
                         '&recordElementId=' + ids[0] + '&recordElementVersionId=' + ids[1] +
                         '&elementId=' + ${param.elementId} + '&elementVersionId=' + ${param.elementVersionId} + '&conceptId=' + conceptId;
                          
        window.open(sublistURL, 'viewSublist', 'resizable=yes,width=' + w + ',height=' + h + ',top=' + top + ',left=' + left);       
    }

    function sortPickListColumn(toggle) {
        var $table = $('#picklistColumns');

        var rows = $table.find('tbody>tr').get();

        rows.sort(function(a, b) {
            var A = getVal(a);
            var B = getVal(b);

            var result = A < B ? -1 : A > B ? 1 : 0;
            
            return toggle ? result : result * -1;
        });

        function getVal(elm){
            var v = $(elm).children('td').eq(1).text().toUpperCase();
            
            return v;
        }            

        $.each(rows, function(index, row) {
            $table.children('tbody').append(row);
        });
    }

    function update(selected) { 
        $('#browse').data('curSnomedSearchSelection', selected);

        var columns = $('#classificationSearch').data('columns');    
        var selectedRadioId = $('input[type=radio][name=picklistColumn]:checked').attr('id');        

        $.each(columns, function(index, e) {    
            var elementId = null;

            if (selectedRadioId == 'curPickListColumn') {
                elementId = 'id-' + e.columnElementId;
            } else {
                var ids = selectedRadioId.split('|');
                
                elementId = 'Id_' + ids[0] + '_' + e.columnElementId;
            }         
            
            getLookupProcessorByType(e.columnLookupType).processAfterSearch(e, elementId);        
        });    
    }

    function getCurrentConceptId() {            
        var curConceptId = $('#classificationSearch').data('curConceptId');    

        if (curConceptId != undefined && curConceptId != null) {
            return curConceptId;
        } 

        return 0;
    }

    function refreshPicklist() {
    	showProcessingScreen();
    	$.ajax({
            url: "<c:url value='/refset/picklist/refresh.htm' />" + '?contextId=' + ${param.contextId} + 
            '&picklistElementId=' + ${param.picklistElementId} + '&picklistElementVersionId=' + ${param.picklistElementVersionId} + 
            '&elementId=' + ${param.elementId} + '&elementVersionId=' + ${param.elementVersionId},
            contentType:"application/json; charset=UTF-8",
            data: {},
            success: function(response) {
                hideProcessingScreen();
                location.reload(true);
            }                
      }); 
    }
</script>

<tiles:insertAttribute name="refset-picklist-menu" />

<div class="content" id='sublistColumns' style='z-index: 10000;'>
	<div class="icons">
		<ul style="padding-left: .9em;">
			<li style="float: left; list-style-type: none;">
				<div id="loadingInfo" class="info"
					style="display: none; margin-bottom: 0.1em; width: 900px; padding-top: 0.5em; padding-bottom: 0.5em;">Loading</div>
			</li>
		</ul>
	</div>

	<div class="icons inline" style="margin-top: 15px; width: 100%">
		<span style="margin-left: 20px;" id='picklistNamelable'><fmt:message
				key="picklist.name" />:</span> <span style="margin-left: 15px:"
			id='picklistName'>${picklist.name}</span> <span style="float: right">
			<ul style="padding-left: .9em;">
				<li id="iconsLI"
					style="float: right; top: 0px; border: 0px; background: #ffffff; list-style-type: none;">
					<img id="save" class="viewMode" title="Save"
					src="<c:url value='/img/icons/SaveGrey.png' />" /> <label
					id="searchTitle"><fmt:message
							key="classification.viewer.search" /></label> <input type="text"
					name="classificationSearch" id="classificationSearch"
					class="searchbox" style="width: 120px;" /> <img id="add"
					class="viewMode" title="Add"
					src="<c:url value='/img/icons/AddGrey.png' />" /> <img id="remove"
					class="viewMode" title="Delete"
					src="<c:url value='/img/icons/RemoveGrey.png' />" /> <img
					id="edit" class="viewMode" title="Edit"
					src="<c:url value='/img/icons/EditGrey.png' />" /> <img
					id="cancel" class="viewMode" title="Cancel"
					src="<c:url value='/img/icons/CancelGrey.png' />" /> <img
					id="reset" class="viewMode" title="Reset"
					src="<c:url value='/img/icons/ResetGrey.png' />" /> <img
					id="browse" class="viewMode" title="Browse"
					src="<c:url value='/img/icons/BrowseGrey.jpg' />" /> 
					
					<c:if test="${refreshEnabled}">
					<img
					id="refresh" class="viewMode" title="Refresh"
					style="height: 32px; width: 32px;" onclick="refreshPicklist()"
					src="<c:url value='/img/icons/Refresh.jpg'/>" />
					</c:if>
				</li>
			</ul>
		</span>
	</div>

	<div id="removalConfirmation" style="display: none;">confirm</div>

	<div class="content" style="margin-top: 20px; overflow-x: auto;">
		<table class="listTable" style="width: 100%; margin-top: 0px;"
			id='picklistColumns'>
			<thead>
				<script type="text/javascript">
                    var columns = [];
                    var subColumns = {};
                    var contextId = ${picklist.contextId};
                    var picklistElementId = ${picklist.picklistElementId};
                    var picklistElementVersionId = ${picklist.picklistElementVersionId};
                    var containerElementId = null;
                    var containerElementVersionId = null;
                
                    <c:forEach items='${picklist.listColumn}' var='listColumnItem' varStatus="loopStatus">
                        <c:if test="${!listColumnItem.sublistColumn}">
                            columns.push({'columnElementId': ${listColumnItem.columnElementId}, 
                                          'columnElementVersionId': ${listColumnItem.columnElementVersionId},
                                          'contextId': ${picklist.contextId},
                                          'columnType': '${listColumnItem.columnType}',
                                          'columnName': '${fn:replace(listColumnItem.columnName, "'", "\\'")}',
                                          'sublistAvailable': ${listColumnItem.sublistAvailable},
                                          'columnLookupType': '${listColumnItem.columnLookupType}'
                            });

                            containerElementId = ${listColumnItem.containerElementId};
                            containerElementVersionId = ${listColumnItem.containerElementVersionId};                        
                        </c:if>
                        <c:if test="${listColumnItem.sublistColumn}">
                            if (subColumns[${listColumnItem.containerElementId}] != undefined) {
                                subColumns[${listColumnItem.containerElementId}].push({'columnElementId': ${listColumnItem.columnElementId}, 
                                                                                       'columnElementVersionId': ${listColumnItem.columnElementVersionId},
                                                                                       'contextId': ${picklist.contextId},
                                                                                       'columnType': '${listColumnItem.columnType}',
                                                                                       'columnName': '${fn:replace(listColumnItem.columnName, "'", "\\'")}',
                                                                                       'sublistAvailable': ${listColumnItem.sublistAvailable},
                                                                                       'columnLookupType': '${listColumnItem.columnLookupType}'
                                });
                            } else {
                                var subColumn = [];

                                subColumn.push({'columnElementId': ${listColumnItem.columnElementId}, 
                                                'columnElementVersionId': ${listColumnItem.columnElementVersionId},
                                                'contextId': ${picklist.contextId},
                                                'columnType': '${listColumnItem.columnType}',
                                                'columnName': '${fn:replace(listColumnItem.columnName, "'", "\\'")}',
                                                'sublistAvailable': ${listColumnItem.sublistAvailable},
                                                'columnLookupType': '${listColumnItem.columnLookupType}'
                                });

                                subColumns[${listColumnItem.containerElementId}] = subColumn;
                            }                            
                        </c:if>
                    </c:forEach>

                    $('#classificationSearch').data('columns', columns);
                    $('#classificationSearch').data('subColumns', subColumns);
                    $('#classificationSearch').data('contextId', contextId);
                    $('#classificationSearch').data('containerElementId', picklistElementId);
                    $('#classificationSearch').data('containerElementVersionId', picklistElementVersionId);    
                    $('#classificationSearch').data('containerSublist', false);                                            
                </script>
			</thead>
			<tbody></tbody>
		</table>
	</div>
</div>

<script type="text/javascript">
     $(document).ready(function() {          
          var concepts = function(request, response) {
              $.ajax({
                  url: "<c:url value='/refset/picklist/getClassificationCodeSearchResult.htm' />",
                  contentType:"application/json; charset=UTF-8",
                  data: {
                      classification: '${picklist.classificationStandard}',
                      term: request.term,  
                      contextId: ${classificationContextId}                                 
                  },
                  success: function(data) {
                      var filteredResponse = [];
                      var conceptIds = $('#classificationSearch').data('conceptIds');
                      
                      $.each(data, function(index, el) {
                          if (conceptIds[el.conceptId] == undefined) {
                              filteredResponse.push(el);
                          }
                      });
                      
                      response(filteredResponse);
                  }
              });
          };

          var selectCallback = function(event, ui) {  
              hideMessage();
              
              var conceptIds = $('#classificationSearch').data('conceptIds');

              if (conceptIds != undefined && conceptIds != null) {
                  if (conceptIds[ui.item.conceptId] != undefined) {
                      return;
                  } 
              } 

              $('#classificationSearch').data('curClassificationSearchSelection', ui.item);    
              $('#classificationSearch').data('curConceptId', ui.item.conceptId);           

              enableAddButton();              

              return;                         
          };
                     
          $("#classificationSearch").autocomplete({source: concepts, 
                                                   select: selectCallback,                                                   
                                                   position: {collision : "flip none"}
          });

          $.ajax({
                url: "<c:url value='/refset/picklist/getColumnTypeSearchPropertyMapper.htm' />",
                contentType:"application/json; charset=UTF-8",
                data: {},
                success: function(response) {
                    var columnTypeSearchPropertyMapper = {};
                    
                    $.each(response, function(index, el) {                        
                        columnTypeSearchPropertyMapper[el.columnTypeCode] = {'searchPropertyName': el.searchPropertyName, 
                                                                             'languageCode': el.languageCode,
                                                                             'codeFormatter': el.hasCodeFormatter == 'Y',
                                                                             'searchIdName': el.searchIdName};                        
                    });

                    $('#classificationSearch').data('columnTypeSearchPropertyMapper', columnTypeSearchPropertyMapper);
                }                
          }); 

          $.ajax({
                url: "<c:url value='/refset/picklist/getSnomedSearchPropertyMapper.htm' />",
                contentType:"application/json; charset=UTF-8",
                data: {},
                success: function(response) {
                    var snomedSearchPropertyMapper = {};
                    
                    $.each(response, function(index, el) {                        
                        snomedSearchPropertyMapper[el.columnTypeCode] = {'searchPropertyName': el.searchPropertyName, 
                                                                         'languageCode': el.languageCode,
                                                                         'codeFormatter': el.hasCodeFormatter == 'Y',
                                                                         'searchIdName': el.searchIdName};                        
                    });

                    $('#browse').data('snomedSearchPropertyMapper', snomedSearchPropertyMapper);
                }                
          }); 

          var pickColumns = $('#classificationSearch').data('columns');
          var headerHtml = '<tr><th class="tableHeader sizeThirty"></th>';
          var sortImgHtml = '<img id="sort" class="viewMode" title="Sort" src="' + '<c:url value="/img/icons/up-down.png" />' + '" />';
          
          $.each(pickColumns, function(index, e) {
              headerHtml += '<th class="tableHeader" ' + (index == 0 ? 'nowrap' : '') + '><div style="display:inline-block;">' + e.columnName + '</div>' + 
                            '<div style="display:inline-block;' + (index == 0 ? 'margin-left: 3px;"' : '"') + '>' + (index == 0 ? sortImgHtml : '') + '</div></th>';
          }); 

          headerHtml += '</tr>';
          $("#picklistColumns").find('thead').append(headerHtml);

          showProcessingScreen();
          
          $.ajax({
                url: "<c:url value='/refset/picklist/getPicklistColumnValue.htm' />",
                contentType:"application/json; charset=UTF-8",
                data: {
                    'contextId': ${picklist.contextId},
                    'containerElementId': ${picklist.picklistElementId},
                    'containerElementVersionId': ${picklist.picklistElementVersionId},
                    'recordElementId': 0,
                    'recordElementVersionId': 0,
                    'containerSublist': ${containerSublist}
                },
                success: function(response) {
                    hideProcessingScreen();
                        
                    var conceptIds = {};                    
                    var columns = $('#classificationSearch').data('columns');                                
                    
                    $.each(response, function(index, record) {
                        var recordId = record.recordIdentifier.elementId + '|' + record.recordIdentifier.elementVersionId; 
                                                     
                        var rowHtml = '<tr><td style="border: 1px solid #96BEBD;"><input type="radio" name="picklistColumn" id="' + recordId + '" onclick="handleColumnSelected();" /></td>'; 

                        $.each(columns, function(s, e) {
                            var columnElementId = e.columnElementId;
 
                            rowHtml += getLookupProcessorByType(e.columnLookupType).getViewHtml(e, record);                                               

                            if (record.values[columnElementId] != undefined) {
                                if (record.values[columnElementId].idValue != null) {                                   
                                    conceptIds[record.values[columnElementId].idValue] = recordId;
                                }                                                                  
                            }                             
                        });
                        
                        rowHtml += '</tr>';
                        $("#picklistColumns").find('tbody').append(rowHtml);                                     
                    });                    
                    
                    $('#classificationSearch').data('conceptIds', conceptIds); 

                    <c:if test="${not empty param.sort}">
                        sortPickListColumn(${param.sort});
                    </c:if>     

                    <c:if test="${not empty param.recordId}">
                        var preRecordId = ${param.recordId};
                       
                        $('input[type=radio][name=picklistColumn]').each(function() {
                            var radioIds = $(this).attr('id');

                            var ids = radioIds.split('|');

                            if (preRecordId == ids[0]) {
                                $(this).attr('checked', true);
                                $(this).focus();

                                handleColumnSelected();
                            }
                        });
                    </c:if>                                
                },
                error: function(response) {
                    hideProcessingScreen();
                    
                    var errorMessages = "System error occurred, please contact System administrator.";

                    showErrorMessages(errorMessages);
                }                    
          }); 

          $.ajax({
              url: "<c:url value='/refset/picklist/valueValidationRules.htm' />",
              contentType:"application/json; charset=UTF-8",
              data: {},
              success: function(response) {
                  var validationRuleMapper = {};
                  
                  $.each(response, function(index, el) {                        
                      validationRuleMapper[el.columnType] = {'message': el.messageKey, 
                                                             'rule': el.regexRule};                                                       
                  });   

                  $('#classificationSearch').data('validationRuleMapper', validationRuleMapper);              
              }                
          }); 

          $('#classificationSearch').keyup(function(e) {
              if (e.keyCode != 13) {
                  disableAddButton();
              }
          });   

          var toggle = false;
          
          $('#sort').click(function() {              
              toggle = !toggle;
              
              sortPickListColumn(toggle);
              $('#classificationSearch').data('sort', toggle);
          });   

          <c:if test="${refsetPermission != 'WRITE'}">
              $('#classificationSearch').attr('disabled', true);              
          </c:if>              
     });     
</script>
