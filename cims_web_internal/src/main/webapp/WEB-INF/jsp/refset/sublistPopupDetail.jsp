<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<spring:eval var="snomedUrl" expression="@applicationProperties.getProperty('snomedservice.url')" />

<script type="text/javascript">
     var subColumns = [];  
     var validationRuleMapper = {};
     var columnTypeSearchPropertyMapper = {};
     var snomedSearchPropertyMapper = {};
     var curSnomedSearchSelection = null;

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

         var selectedRadioId = $('input[type=radio][name=sublistColumn]:checked').attr('id');

         if (selectedRadioId == 'curSubListColumn') {
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
                
         var selectedRadioId = $('input[type=radio][name=sublistColumn]:checked').attr('id');

         if (selectedRadioId == 'curSubListColumn') {
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

     function update(selected) { 
         curSnomedSearchSelection = selected;   
         var selectedRadioId = $('input[type=radio][name=sublistColumn]:checked').attr('id');    

         $.each(subColumns, function(index, e) {    
             var elementId = null;

             if (selectedRadioId == 'curSubListColumn') {
                 elementId = 'id-' + e.columnElementId;
             } else {
                 var ids = selectedRadioId.split('|');
                 
                 elementId = 'Id_' + ids[0] + '_' + e.columnElementId;
             }         
             
             getLookupProcessorByType(e.columnLookupType).processAfterSearch(e, elementId);        
         });    
     }   

     function addColumnConfiguration() {
         var selectedRadioId = $('input[type=radio][name=sublistColumn]:checked').attr('id');

         if (selectedRadioId != undefined) {
             if (selectedRadioId == 'curSubListColumn') {
                 return;
             }
             
             var ids = selectedRadioId.split('|');            
             var prevRecordElementId = ids[0];
             
             $.each(subColumns, function(index, e) {                    
                 var changeHtmlForReadOnly = getLookupProcessorByType(e.columnLookupType).getChangeHtmlForReadOnly(e);

                 if (changeHtmlForReadOnly != null) {
                     var cId = 'Id_' + prevRecordElementId + '_' + e.columnElementId;

                     $('#' + cId).html(changeHtmlForReadOnly);
                 }                       
             });
         }
         
         var rowHtml = '<tr><td style="border: 1px solid #96BEBD;"><input type="radio" name="sublistColumn" id="curSubListColumn" checked="checked" onclick="handleColumnSelected();" /></td>';
                     
         $.each(subColumns, function(index, e) {
             rowHtml += getLookupProcessorByType(e.columnLookupType).getNewHtml(e);
         });

         rowHtml += '</tr>';

         $("#sublistColumns").find('tbody').prepend(rowHtml);

         var hasSnomedColumn = false;        
         
         $.each(subColumns, function(index, e) {
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
         
         $('input[type=radio][name=sublistColumn]').attr("disabled", true);        
     }

     function newColumnConfiguration() {  
         disableSaveButton();        
         
         var columnData = {'contextId': ${param.contextId},
                           'containerElementId': ${param.parentColumnId},
                           'containerElementVersionId': ${param.parentColumnVersionId},
                           'recordElementId': ${param.recordElementId},
                           'recordElementVersionId': ${param.recordElementVersionId},
                           'containerSublist': true, 
                           'values': []                           
         };                   
         
         var errors = [];
         
         $.each(subColumns, function(index, e) {
             var inputValue = getLookupProcessorByType(e.columnLookupType).getInputValue(e);           

             if (inputValue != null) {                    
                 columnData.values.push(inputValue); 

                 if (inputValue.textValue != null) {
                     var v = $.trim(inputValue.textValue);
                     
                     if (v != '') {
                         if (validationRuleMapper != undefined && validationRuleMapper != null) {
                             if (validationRuleMapper[e.columnType] != undefined) {
                                 if (!v.match(/^[0-9a-zA-Z]+$/)) {
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
                      $('#curSubListColumn').attr('id', response.result.elementId + '|' + response.result.elementVersionId);                            
                      
                      disableCancelButton();     
                      disableResetButton();
                      disableBrowseButton();                
                      enableRemoveButton(); 

                      enableAddButton();  
                      $('input[type=radio][name=sublistColumn]').attr("disabled", false);
                     
                      var editable = false;
                  
                      $.each(subColumns, function(index, e) {
                          getLookupProcessorByType(e.columnLookupType).processAfterSave(response.result.elementId, e);
                          editable = editable || getLookupProcessorByType(e.columnLookupType).isColumnEditable(e);                         
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

     function saveColumnConfiguration() { 
         disableSaveButton();
            
         var selectedRadioId = $('input[type=radio][name=sublistColumn]:checked').attr('id');
         var ids = selectedRadioId.split('|');
                  
         var columnData = {'contextId': ${param.contextId},
                           'containerElementId': ${param.parentColumnId},
                           'containerElementVersionId': ${param.parentColumnVersionId},
                           'recordElementId': ids[0],
                           'recordElementVersionId': ids[1],
                           'containerSublist': true, 
                           'values': []                           
         };         
        
         var errors = [];
         
         $.each(subColumns, function(index, e) {
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
                                     if (!v.match(/^[0-9a-zA-Z]+$/)) {
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
                      curSnomedSearchSelection = null;                     
                      
                      disableCancelButton();      
                      disableResetButton(); 
                      disableBrowseButton();                               
                      enableRemoveButton(); 
                      enableEditButton(); 
                      enableAddButton();                                 
                  
                      $('input[type=radio][name=picklistColumn]').attr("disabled", false);    
                      $('#classificationSearch').attr("disabled", false);             
                  
                      $.each(subColumns, function(index, e) {
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

     function removeRecord() {                
         $("#removalConfirmation").text('Do you want to Delete the Record from the Sublist?');

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
         var selectedRadioId = $('input[type=radio][name=sublistColumn]:checked').attr('id');
         var ids = selectedRadioId.split('|');
        
         $.ajax({
             url: "<c:url value='/refset/picklist/deletePicklistColumnValue.htm' />",
             contentType:"application/json; charset=UTF-8",
             data: {
                 'contextId': ${param.contextId},
                 'elementId': ids[0],
                 'elementVersionId': ids[1],
                 'containerSublist': true      
             },
             success: function(response) {
                 if (response.status == 'SUCCESS') {
                     showInfoMessage(response.message);

                     deleteRow();
                     $('input[type=radio][name=sublistColumn]').attr("disabled", false);  

                     disableRemoveButton();
                     disableEditButton();
                     disableResetButton();
                     disableCancelButton();
                     disableBrowseButton();                      
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

     function deleteRow() {
         $('input[type=radio][name=sublistColumn]:checked').closest('tr').remove();        
     }  

     function editColumnConfiguration() {
         hideMessage();
         
         var selectedRadioId = $('input[type=radio][name=sublistColumn]:checked').attr('id');
         var ids = selectedRadioId.split('|');
         var recordElementId = ids[0];         
           
         var hasSnomedColumn = false;        
         
         $.each(subColumns, function(index, e) {
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
               
         $('input[type=radio][name=sublistColumn]').attr("disabled", true);    

         if (hasSnomedColumn) {
             enableBrowseButton();
         }        
     }

     function cancelNewColumnConfiguration() {
         hideMessage();
         deleteRow();
         
         $('input[type=radio][name=sublistColumn]').attr("disabled", false);         
         curSnomedSearchSelection = null;

         disableCancelButton();
         disableSaveButton();                      
         disableRemoveButton(); 
         disableResetButton();
         disableBrowseButton();        
         disableEditButton(); 
         enableAddButton();
     }

     function cancelColumnConfiguration() {
         hideMessage();
        
         var selectedRadioId = $('input[type=radio][name=sublistColumn]:checked').attr('id');
         var ids = selectedRadioId.split('|');

         $.each(subColumns, function(index, e) {
             var tdId = 'tdId' + e.columnElementId;
             var cId = 'Id_' + ids[0] + '_' + e.columnElementId;

             var currentInputValue = $('#' + tdId).attr('currentInputValue');

             if (currentInputValue != undefined) {
                 $('#' + tdId).attr('id', cId);
                 
                 $('#' + cId).html(currentInputValue);
             }   

             getLookupProcessorByType(e.columnLookupType).processCancel(e);        
         });

         enableAddButton();         
         disableCancelButton();    
         disableResetButton();    
         disableSaveButton();    
         disableBrowseButton();                  
         enableRemoveButton();
         enableEditButton();         

         $('input[type=radio][name=sublistColumn]').attr("disabled", false);         
         curSnomedSearchSelection = null;
     }

     function resetColumnConfiguration() {   
         curSnomedSearchSelection = null;
         
         $.each(subColumns, function(index, e) {
             var currentInputValue = $('#' + 'tdId' + e.columnElementId).attr('currentInputValue');             

             getLookupProcessorByType(e.columnLookupType).processReset(e, currentInputValue);            
         });  

         disableResetButton();      
     }

     function handleColumnSelected() {
         hideMessage();
         
         <c:if test="${refsetPermission == 'WRITE'}">    
             enableRemoveButton();
         </c:if>    

         var selectedRadioId = $('input[type=radio][name=sublistColumn]:checked').attr('id');

         if (selectedRadioId == 'curSubListColumn') {
             return true;
         }
         
         var enableEdit = false;

         $.each(subColumns, function(index, e) {            
             enableEdit = enableEdit || getLookupProcessorByType(e.columnLookupType).isColumnEditable(e);
         });

         <c:if test="${refsetPermission == 'WRITE'}">
             if (enableEdit) {
                 enableEditButton();
             }
         </c:if>  

         return true;        
     }         
    
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

             return '<td style="border: 1px solid #96BEBD;" id="' + tdId + '"><input type="text" id="' + inputId + '" class="searchbox" maxlength="250" style="width: 80px; position:relative;" /></td>';
         };

         this.processAfterNewRecord = function(column) {
             var inputId = 'fId' + column.columnElementId;
             var conceptId = ${param.conceptId};
             
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

             var selectedRadioId = $('input[type=radio][name=sublistColumn]:checked').attr('id');             

             if (selectedRadioId != 'curSubListColumn') {
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
             var conceptId = ${param.conceptId};              
         
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
                           
             return '<input type="text" id="' + inputId + '" class="searchbox" maxlength="250" style="width: 80px; position:relative; z-index: 10000" />';         
         };

         this.setInputValueForEdit = function(column) {     
             var inputId = 'fId' + column.columnElementId;
             var tdId = 'tdId' + column.columnElementId;

             var currentValue = $('#' + tdId).attr('currentinputvalue');

             if (currentValue != null && currentValue != undefined) {
                 $('#' + inputId).val(currentValue);
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

             return '<td style="border: 1px solid #96BEBD;" id="' + tdId + '"><input type="text" id="' + inputId + '" class="searchbox" maxlength="250" style="width: 80px; position:relative; z-index: 10000" /></td>';
         };

         this.processAfterNewRecord = function(column) {
             var inputId = 'fId' + column.columnElementId;
             var selectedRadioId = $('input[type=radio][name=sublistColumn]:checked').attr('id');             

             if (selectedRadioId != 'curSubListColumn') {
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
               
             return '<input type="text" id="' + inputId + '" class="searchbox" maxlength="250" style="width: 80px; position:relative; z-index: 10000" />';         
         };

         this.setInputValueForEdit = function(column) {     
             var inputId = 'fId' + column.columnElementId;
                           
             var tdId = 'tdId' + column.columnElementId;

             var currentValue = $('#' + tdId).attr('currentinputvalue');

             if (currentValue != null && currentValue != undefined) {
                 $('#' + inputId).val(currentValue);
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
             var classification = snomedSearchPropertyMapper[column.columnType];   

             if (classification != undefined) {
                 if (curSnomedSearchSelection != undefined && curSnomedSearchSelection != null) {
                     if (curSnomedSearchSelection[classification.searchPropertyName] != undefined) {    
                         var v = curSnomedSearchSelection[classification.searchPropertyName];                       

                         $('#' + elementId).html(v != undefined && v != null ? v : '');

                         if (curSnomedSearchSelection[classification.searchIdName] != undefined && 
                                 curSnomedSearchSelection[classification.searchIdName] != null) {
                             $('#' + elementId).attr('selectedId', curSnomedSearchSelection[classification.searchIdName]);

                             var selectedRadioId = $('input[type=radio][name=sublistColumn]:checked').attr('id');  
                               
                             if (selectedRadioId != 'curSubListColumn') {
                                 enableResetButton();
                             }
                         }                        
                     }
                 }
             }            
               
             return;
         };

         this.getInputValue = function(column) {            
             var selectedRadioId = $('input[type=radio][name=sublistColumn]:checked').attr('id');    
             var elementId = null;

             if (selectedRadioId == 'curSubListColumn') {
                 elementId = 'id-' + column.columnElementId;
             } else {
                 var ids = selectedRadioId.split('|');
                 elementId = 'Id_' + ids[0] + '_' + column.columnElementId;
             }              
                 
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
             var selectedRadioId = $('input[type=radio][name=sublistColumn]:checked').attr('id');    
             var elementId = null;

             if (selectedRadioId != 'curSubListColumn') {               
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
             var selectedRadioId = $('input[type=radio][name=sublistColumn]:checked').attr('id');    
             var elementId = null;

             if (selectedRadioId != 'curSubListColumn') {               
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

     /**
      * the 'factory' object that creates new products(column lookup type processor)
      * implements 'factoryMethod' which returns newly created column lookup type processor.
      */
     function columnLookupTypeProcessorFactory() {
         this.createLookupProcessor = function(columnLookupType) {
             var columnLookupTypeProcessor;
             
             if (columnLookupType == 'EXTEND_LOOKUP') {
                 columnLookupTypeProcessor = new extendLookupProcessor();
             } else if (columnLookupType == 'FREE_TYPE') {
                 columnLookupTypeProcessor = new freeTypeProcessor();
             } else if (columnLookupType == 'SNOMED') {
                 columnLookupTypeProcessor = new snomedProcessor();
             } else {
                 columnLookupTypeProcessor = new NAProcessor();
             }
             
             columnLookupTypeProcessor.columnLookupType = columnLookupType;
             
             return columnLookupTypeProcessor;
         }  
     }            
</script>

<div class="content">
    <div class="icons">
        <ul style="padding-left: .9em;">
            <li style="float: left; list-style-type: none;">
                <div id="loadingInfo" class="info"
                    style="display: none; margin-bottom: 0.1em; width: 900px; padding-top: 0.5em; padding-bottom: 0.5em;">Loading</div>
            </li>
        </ul>
    </div>
    
    <div class="icons inline" style="margin-top: 15px; width: 100%">        
        <span style="float: right">
            <ul style="padding-left: .9em;">
                <li id="iconsLI" style="float: right; top: 0px; border: 0px; background: #ffffff; list-style-type: none;">
                    <img id="save" class="viewMode" title="Save" src="<c:url value='/img/icons/SaveGrey.png' />" /> 
                    <img id="add" class="viewMode" title="Add" src="<c:url value='/img/icons/AddGrey.png' />" /> 
                    <img id="remove" class="viewMode" title="Delete" src="<c:url value='/img/icons/RemoveGrey.png' />" /> 
                    <img id="edit" class="viewMode" title="Edit" src="<c:url value='/img/icons/EditGrey.png' />" /> 
                    <img id="cancel" class="viewMode" title="Cancel" src="<c:url value='/img/icons/CancelGrey.png' />" /> 
                    <img id="reset" class="viewMode" title="Reset" src="<c:url value='/img/icons/ResetGrey.png' />" /> 
                    <img id="browse" class="viewMode" title="Browse" src="<c:url value='/img/icons/BrowseGrey.jpg' />" />
                </li>
            </ul>
        </span>
    </div>

    <div id="removalConfirmation" style="display: none;">confirm</div>
    
    <div class="content" style="margin-top: 20px; overflow-x: auto;">
        <table class="listTable" style="width: 100%; margin-top: 0px;" id='sublistColumns'>
            <thead>
                <script type="text/javascript">
                    <c:forEach items='${sublistColumn}' var='listColumnItem' varStatus="loopStatus">
                        subColumns.push({'columnElementId': ${listColumnItem.columnElementId}, 
                                         'columnElementVersionId': ${listColumnItem.columnElementVersionId},
                                         'contextId': ${param.contextId},
                                         'columnType': '${listColumnItem.columnType}',
                                         'columnName': '${fn:replace(listColumnItem.columnName, "'", "\\'")}',
                                         'sublistAvailable': ${listColumnItem.sublistAvailable},
                                         'columnLookupType': '${listColumnItem.columnLookupType}'
                        });                         
                    </c:forEach>
                </script>
            </thead>
            <tbody></tbody>
        </table>
    </div>    
</div>

<script type="text/javascript">
    $(document).ready(function() {  
    	var headerHtml = '<tr><th class="tableHeader sizeThirty"></th>';
        
        $.each(subColumns, function(index, e) {
            headerHtml += '<th class="tableHeader">' + e.columnName + '</th>';         
        }); 

        headerHtml += '</tr>';

        $("#sublistColumns").find('thead').html(headerHtml);  

        showProcessingScreen();
        
        $.ajax({
            url: "<c:url value='/refset/picklist/getPicklistColumnValue.htm' />",
            contentType:"application/json; charset=UTF-8",
            data: {
                'contextId': ${param.contextId},
                'containerElementId': ${param.parentColumnId},
                'containerElementVersionId': ${param.parentColumnVersionId},
                'recordElementId': ${param.recordElementId},
                'recordElementVersionId': ${param.recordElementVersionId},
                'containerSublist': true
            },
            success: function(response) { 
            	hideProcessingScreen();
            	
                $.each(response, function(index, record) {
                    var recordId = record.recordIdentifier.elementId + '|' + record.recordIdentifier.elementVersionId; 
                                              
                    var rowHtml = '<tr><td style="border: 1px solid #96BEBD;"><input type="radio" name="sublistColumn" id="' + recordId + '" onclick="handleColumnSelected();" /></td>'; 

                    $.each(subColumns, function(s, e) {
                        var columnElementId = e.columnElementId;

                        rowHtml += getLookupProcessorByType(e.columnLookupType).getViewHtml(e, record);    
                    });
                 
                    rowHtml += '</tr>';
                    $("#sublistColumns").find('tbody').append(rowHtml);                        
                }); 

                enableAddButton();

                <c:if test="${refsetPermission != 'WRITE'}">        
                    disableAddButton();
                    $('input[type=radio][name=sublistColumn]').attr("disabled", true); 
                </c:if>      
            }                
        });

        $.ajax({
            url: "<c:url value='/refset/picklist/valueValidationRules.htm' />",
            contentType:"application/json; charset=UTF-8",
            data: {},
            success: function(response) {
                $.each(response, function(index, el) {                        
                    validationRuleMapper[el.columnType] = {'message': el.messageKey, 
                                                           'rule': el.regexRule};                                                       
                }); 
            }                
        });

        $.ajax({
            url: "<c:url value='/refset/picklist/getColumnTypeSearchPropertyMapper.htm' />",
            contentType:"application/json; charset=UTF-8",
            data: {},
            success: function(response) {
                $.each(response, function(index, el) {                        
                    columnTypeSearchPropertyMapper[el.columnTypeCode] = {'searchPropertyName': el.searchPropertyName, 
                                                                         'languageCode': el.languageCode,
                                                                         'codeFormatter': el.hasCodeFormatter == 'Y',
                                                                         'searchIdName': el.searchIdName};                        
                });
            }                
        });  

        $.ajax({
            url: "<c:url value='/refset/picklist/getSnomedSearchPropertyMapper.htm' />",
            contentType:"application/json; charset=UTF-8",
            data: {},
            success: function(response) {                
                $.each(response, function(index, el) {                        
                    snomedSearchPropertyMapper[el.columnTypeCode] = {'searchPropertyName': el.searchPropertyName, 
                                                                     'languageCode': el.languageCode,
                                                                     'codeFormatter': el.hasCodeFormatter == 'Y',
                                                                     'searchIdName': el.searchIdName};                        
                });
            }                
      });           
    });
</script>