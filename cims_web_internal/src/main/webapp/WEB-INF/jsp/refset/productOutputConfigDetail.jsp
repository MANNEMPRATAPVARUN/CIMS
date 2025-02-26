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

    function toggle(element) {
		 var toggle = $(element);
		 var section = toggle.parents(".section");
		 
		 toggleSection(section, true);
	}

	function toggleSection(section, animate) {
		 if (!(section instanceof jQuery)) {
			 section = $(section);
		 }
		 
		 var toggle = section.find(".sectionHeader > a > img");
		 var sectionContent = section.find(".sectionContent").first();
		 
		 var isHidden = isSectionHidden(section);
		 
		 if (isHidden){
		     (animate ? sectionContent.slideDown() : sectionContent.show());
	         toggle.attr("src", "<c:url value='/img/icons/Expand.png'/>");
		 } else{
			 (animate ? sectionContent.slideUp() : sectionContent.hide());
			 toggle.attr("src", "<c:url value='/img/icons/Collapse.png'/>");
		 }
		 
		 $.cookie("section."+section.attr('name')+".collapsed",!isHidden);
	}

	function isSectionHidden(section) {
		if (!(section instanceof jQuery)) {
			 section = $(section);
		 }
		 
		return section.find(".sectionContent").first().is(":hidden");
	} 

	function getLanguageByCode(languageCode) {
        var languages = {'ENG': '<fmt:message key="common.english" />',
                         'FRA': '<fmt:message key="common.french" />'};

        return languages[languageCode];
    }

	function displayOutputConfig() {
        $('#languageCode').html(getLanguageByCode('${refsetOutput.languageCode}'));
    }

    function handleSpecificationSelected() {
        var specification = $('input[type=radio][name=specification]:checked').val();

        if (specification == 'Y') {
            $('#title').attr('disabled', false);
            $('#supplementName').attr('disabled', true);
        }

        if (specification == 'N') {
            $('#title').attr('disabled', true);
            $('#title').val('');

            $('#supplementName').attr('disabled', false);
        }
    }

    function enableSaveSpecificationButton() {
        $('#save2').attr('src', '<c:url value="/img/icons/Save.png" />');
        
        enableSaveSpecificationClickEvent();                
    }

    function disableSaveSpecificationButton() {
        $('#save2').attr('src', '<c:url value="/img/icons/SaveGrey.png" />');
        
        disableSaveSpecificationClickEvent();
    }

    function enableSaveSpecificationClickEvent() {
        $('#save2').unbind('click').click(function() {
            saveRefsetOutputTitle();
        });
    }

    function disableSaveSpecificationClickEvent() {
        $('#save2').unbind('click'); 
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
            saveRefsetOutputConfiguration();
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

    function processSaveSpecificationEnable() {
        var specification = $('input[type=radio][name=specification]:checked').val();

        if (specification == 'Y') {
            var title = $('#title').val();

            if ($.trim(title) != null && $.trim(title) != '') {
                enableSaveSpecificationButton();

                return;
            }
        }

        if (specification == 'N') {
            if ($('#supplementName').val() != null) {
                enableSaveSpecificationButton();

                return;
            }
        }

        disableSaveSpecificationButton();

        return;
    }

    function saveRefsetOutputTitle() {
        hideMessage();        
        
        disableSaveSpecificationButton();
        
        var refsetOutputTitle = {'refsetOutputId': ${param.refsetOutputId}};
        var specification = $('input[type=radio][name=specification]:checked').val();

        if (specification == 'Y') {
            refsetOutputTitle.title = $.trim($('#title').val());
        }

        if (specification == 'N') {
            refsetOutputTitle.supplementId = $('#supplementName').val();
        }

        var refsetOutputId = $('#save2').attr('refsetOutputId');
        
        $.ajax({
            type: 'post',
            url: refsetOutputId == undefined || refsetOutputId == null ? "<c:url value='/refset/addRefsetOutputTitle.htm' />" : "<c:url value='/refset/updateRefsetOutputTitle.htm' />" ,
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify(refsetOutputTitle),
            success: function(response) {
                if (response.status == 'SUCCESS') {
                    disableSaveSpecificationButton();

                    $('#save2').attr('refsetOutputId', response.result.refsetOutputId);
                } else {                    
                    showErrorMessages(response.message);

                    enableSaveSpecificationButton();
                }
            },    
            error: function(response) {
                var errorMessages = "System error occurred, please contact System administrator.";

                showErrorMessages(errorMessages);
            }            
        }); 

        return;
    } 

    function addNewOutputConfiguration() {
        hideMessage();
        
        $('#refsetOutputTable').find('tbody').append("<tr id='curOutputConfig'><td style='border: 1px solid #96BEBD;'></td>" + 
             "<td style='border: 1px solid #96BEBD;'></td><td style='border: 1px solid #96BEBD;'></td></tr>");

        $('#curOutputConfig').find('td:eq(0)').html("<input type='radio' name='outputConfig' id='outConfig'>");
        $('#curOutputConfig').find('td:eq(1)').html("<select name='outputConfigSelection' id='outputConfigSelection'></select>");
        $('#curOutputConfig').find('td:eq(2)').html("<input type='text' id='orderNumber' name='orderNumber' maxlength='5' style='width: 40px;'>");                        

        $('#outConfig').attr('checked', true);
        $('#curOutputConfig').attr('action', 'NEW');  

        getAvailableOutputSelection(); 

        $('#orderNumber').keydown(function(e) {
            checkNumberInput(e);
        });                

        enableSaveButton();
        disableAddButton(); 
        disableRemoveButton();
        disableEditButton();
        enableCancelButton();
        disableResetButton();

        disableConfigSelection();                    
    } 

    function enableConfigSelection() {
        $('input[type=radio][name=outputConfig]').attr('disabled', false);
    } 

    function disableConfigSelection() {
        $('input[type=radio][name=outputConfig]').attr('disabled', true);
    } 

    function getAvailableOutputSelection() {
        $.ajax({
            url: "<c:url value='/refset/getAvailableRefsetOutputConfiguration.htm' />",
            contentType:"application/json; charset=UTF-8",
            data: {
                'contextId': ${param.contextId},
                'elementId': ${param.elementId},
                'elementVersionId': ${param.elementVersionId},
                'refsetOutputId': ${param.refsetOutputId}, 
                'language': '${refsetOutput.languageCode}'                               
            },
            success: function(response) {
                if (response.status == 'SUCCESS') {
                    if (response.result != undefined && response.result != null) {
                        var hasItem = false;
                        
                        $.each(response.result, function(index, record) {
                            hasItem = true;
                            
                            $('#outputConfigSelection').append($('<option></option>').attr('value', record.outputId).attr('type', record.type).text(record.displayName));
                        });

                        if (!hasItem) {
                            var isNewRecord = $('#curOutputConfig').attr('action') == 'NEW';

                            if (isNewRecord) {
                                cancelOutputConfiguration();

                                showInfoMessage('<fmt:message key="refset.output.no.available.picklist.supplement" />');
                            }

                            disableAddButton();
                        }
                    }                  
                }
            },
            error: function(response) {
                hideProcessingScreen();
                
                var errorMessages = "System error occurred, please contact System administrator.";

                showErrorMessages(errorMessages);
            }                    
        });        
    }

    function isOutputSelectionAvailable() {
        $.ajax({
            url: "<c:url value='/refset/getAvailableRefsetOutputConfiguration.htm' />",
            contentType:"application/json; charset=UTF-8",
            data: {
                'contextId': ${param.contextId},
                'elementId': ${param.elementId},
                'elementVersionId': ${param.elementVersionId},
                'refsetOutputId': ${param.refsetOutputId}, 
                'language': '${refsetOutput.languageCode}'                               
            },
            success: function(response) {
                if (response.status == 'SUCCESS') {
                    if (response.result != undefined && response.result != null) {
                        var hasItem = false;
                        
                        $.each(response.result, function(index, record) {
                            hasItem = true;                           
                        });

                        if (!hasItem) {                           
                            disableAddButton();
                        }
                    }                  
                }
            },
            error: function(response) {
                hideProcessingScreen();
                
                var errorMessages = "System error occurred, please contact System administrator.";

                showErrorMessages(errorMessages);
            }                    
        });
    } 

    function checkNumberInput(e) {
        /**
         * Allow: backspace, delete, tab, escape, enter and .
         */
        if ($.inArray(e.keyCode, [46, 8, 9, 27, 13, 110]) !== -1 ||
            /**
             * Allow: Ctrl+A, Command+A
             */
            (e.keyCode === 65 && (e.ctrlKey === true || e.metaKey === true)) || 
            /**
             * Allow: home, end, left, right, down, up
             */
            (e.keyCode >= 35 && e.keyCode <= 40)) {
            /**
             * let it happen, don't do anything
             */
            return;
        }
            
        /**
         * Ensure that it is a number and stop the keypress
         */
        if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
            e.preventDefault();
        }

        return;
    }         

    function getDefaultColumnOrder() {
        var $table = $('#refsetOutputTable');

        var rows = $table.find('tbody>tr').get();
        var defaultColumnOrder = 0;

		$.each(rows, function() {
            var trId = $(this).attr('id');

            if (trId != 'curOutputConfig') {
                var columnOrder = $(this).find('td:eq(2)').text();

                if (columnOrder != '' && $.trim(columnOrder) != '') {
                    if (!isNaN($.trim(columnOrder))) {
                        defaultColumnOrder = parseInt($.trim(columnOrder)) > defaultColumnOrder ? parseInt($.trim(columnOrder))	: defaultColumnOrder;
                    }
                }
            }
		});

		return defaultColumnOrder + 1;
	}

    function saveRefsetOutputConfiguration() {
        var action = $('#curOutputConfig').attr('action'); 

        hideMessage();
        
        var selectedRadioId = $('input[type=radio][name=outputConfig]:checked').attr('id');

        if (selectedRadioId == undefined || selectedRadioId == null) {
            return;
        }

        disableSaveButton();  

        var orderNumber = $('#orderNumber').val();         

        if (orderNumber == '' || $.trim(orderNumber) == '') {
            orderNumber = getDefaultColumnOrder(); 
        }

        var outputId = $('#outputConfigSelection').val();
        var type = $('#outputConfigSelection option:selected').attr('type');
        
        var refsetOutput = {'refsetOutputId': ${param.refsetOutputId},
                            'refsetContextId': ${param.contextId},
                            'orderNumber': orderNumber,
                            'outputId': outputId,
                            'type': type
        };

        if (action == 'SAVE') {
            refsetOutput.origType = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('type');
            refsetOutput.origOutputId = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('outputId');
        }

        $.ajax({
            type: 'post',
            url: action == 'NEW' ? "<c:url value='/refset/addRefsetOutputConfiguration.htm' />" : "<c:url value='/refset/saveRefsetOutputConfiguration.htm' />",
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify(refsetOutput),
            success: function(response) {
                if (response.status == 'SUCCESS') {
                    $('#outConfig').attr('id', 'out_' + outputId);
                    $('#curOutputConfig').attr('id', 'Id_' + outputId);   

                    var displayName = $('#outputConfigSelection option:selected').text();
                    
                    $('#Id_' + outputId).find('td:eq(1)').html(displayName);
                    $('#Id_' + outputId).find('td:eq(2)').html(orderNumber);
                   
                    $('#Id_' + outputId).attr('action', 'SAVE');
                    $('#Id_' + outputId).attr('outputId', outputId);
                    $('#Id_' + outputId).attr('displayName', displayName);
                    $('#Id_' + outputId).attr('orderNumber', orderNumber);
                    $('#Id_' + outputId).attr('type', type);
                    
                    $('#out_' + outputId).unbind('click').click(function() {
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

        isOutputSelectionAvailable();
        
        return;
    }

    function sortRefsetOutput() {
        var $table = $('#refsetOutputTable');

        var rows = $table.find('tbody>tr').get();

        rows.sort(function(a, b) {
            var A = getVal(a);
            var B = getVal(b);

            return A < B ? -1 : A > B ? 1 : 0;
        });

        function getVal(elm){
            var v = $(elm).children('td').eq(2).text().toUpperCase();
            
            return parseInt(v);
        }            

        $.each(rows, function(index, row) {
            $table.children('tbody').append(row);
        });
    }

    function handleConfigSelected() {        
        disableSaveButton();
        enableAddButton();
        enableRemoveButton();
        enableEditButton();        
        disableCancelButton();
        disableResetButton();
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

        var outputId = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('outputId');
        var displayName = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('displayName');
        var orderNumber = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('orderNumber');        
            
        $('#outConfig').attr('id', id='out_' + outputId);
        $('#curOutputConfig').attr('id', 'Id_' + outputId);

        $('#Id_' + outputId).find('td:eq(1)').html(displayName);
        $('#Id_' + outputId).find('td:eq(2)').html(orderNumber);        
        $('#Id_' + outputId).attr('action', 'SAVE');        

        $('#out_' + outputId).unbind('click').click(function() {
            handleConfigSelected();
        });

        return;
    }

    function editOutputConfiguration() {
        hideMessage();
        
        var selectedRadioId = $('input[type=radio][name=outputConfig]:checked').attr('id');

        if (selectedRadioId == undefined || selectedRadioId == null) {
            return;
        }

        var outputId = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('outputId');

        if (outputId == undefined || outputId == null) {
            return;
        }

        var displayName = $.trim($('#Id_' + outputId).attr('displayName'));
        var orderNumber = $.trim($('#Id_' + outputId).attr('orderNumber'));
        var type = $.trim($('#Id_' + outputId).attr('type'));
        
        $('#Id_' + outputId).attr('id', 'curOutputConfig');        
        $('#out_' + outputId).attr('id', 'outConfig');
        
        $('#curOutputConfig').find('td:eq(1)').html("<select name='outputConfigSelection' id='outputConfigSelection'></select>");
        $('#outputConfigSelection').append($('<option></option>').attr('value', outputId).attr('type', type).text(displayName));
        
        $('#curOutputConfig').find('td:eq(2)').html("<input type='text' id='orderNumber' name='orderNumber' maxlength='5' style='width: 40px;'>");
        $('#orderNumber').val(orderNumber);        

        getAvailableOutputSelection(); 

        $('#outputConfigSelection').change(function() {            
            processResetButtonEnable();
        }); 

        $('#orderNumber').keyup(function() {            
            processResetButtonEnable();
        });  

        $('#outputConfigLanguage').change(function() {
            processResetButtonEnable();
        });

        enableSaveButton();
        disableAddButton();
        enableRemoveButton();
        disableEditButton();
        enableCancelButton();
        disableResetButton();

        disableConfigSelection();
                
        return;
    }

    function processResetButtonEnable() {
        var outputId = $.trim($('#outputConfigSelection').val());
        var orderNumber = $.trim($('#orderNumber').val());        

        var savedOutputId = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('outputId');
        var savedOrderNumber = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('orderNumber');
                
        if (savedOutputId == undefined || savedOutputId == null || 
                savedOrderNumber == undefined || savedOrderNumber == null) {
            return;
        } 

        if (savedOutputId != outputId || savedOrderNumber != orderNumber) {
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
        
        var outputId = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('outputId');
        var orderNumber = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('orderNumber');        

        $('#outputConfigSelection').val(outputId);
        $('#orderNumber').val(orderNumber);        
    }

    function deleteRow() {
        $('input[type=radio][name=outputConfig]:checked').closest('tr').remove();
    }

    function removeOutputConfig() {
        hideMessage();
        
        var selectedRadioId = $('input[type=radio][name=outputConfig]:checked').attr('id');

        if (selectedRadioId == undefined || selectedRadioId == null) {
            return;
        }

        var outputId = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('outputId');

        if (outputId == undefined || outputId == null) {
            return;
        }

        var type = $('input[type=radio][name=outputConfig]:checked').closest('tr').attr('type');

        if (type == undefined || type == null) {
            return;
        }

        $.ajax({
            url: "<c:url value='/refset/deleteRefsetOutputConfigDetail.htm' />",
            contentType:"application/json; charset=UTF-8",
            data: {
                'refsetOutputId': ${param.refsetOutputId}, 
                'outputId': outputId,
                'type': type               
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
</script>

<div class="content" style='z-index: 10000;'>
    <div class="icons">
        <ul style="padding-left: .9em;">
            <li style="float: left; list-style-type: none;">
                <div id="loadingInfo" class="info"
                    style="display: none; margin-bottom: 0.1em; width: 900px; padding-top: 0.5em; padding-bottom: 0.5em;">Loading</div>
            </li>
        </ul>
    </div>

    <div id="removalConfirmation" style="display: none;">confirm</div>

    <div class="section" style="padding: 2px;">
        <div class="content" style="display: inline-block; width: 70%;">
            <div class="sectionHeader">
                <a href="#"><img src="<c:url value='/img/icons/Expand.png' />" alt="Toggle"
                    onclick="javascript:toggle(this);" style="vertical-align: middle;" /></a>
                <div style="display: inline-block; vertical-align: middle;">
                    <fmt:message key="refset.configuration.details" />
                </div>
            </div>
        </div>

        <div class="sectionContent">
            <div class="span-24 inline" style='margin-left: 20px; margin-top: 20px;'>
                <span><fmt:message key="refset.product.output.language" />:</span> <span
                    id='languageCode' style="margin-left: 5px;"></span> <span
                    style="margin-left: 120px;"><fmt:message
                        key="refset.product.output.configuration.name" />:</span> <span
                    style="margin-left: 5px;">${refsetOutput.name}</span>
            </div>

            <div class="span-24 inline" style='margin-left: 20px; margin-top: 20px;'>
                <span><fmt:message key="refset.product.output.file.name" />:</span> <span
                    style="margin-left: 5px;">${refsetOutput.filename}</span>
            </div>

            <div class="span-24 inline" style='width: 70%; margin-top: 20px; margin-left: 20px;'>
                <span> <fmt:message key="refset.product.output.configuration.table" />:
                </span> <span style="float: right">
                    <ul style="padding-left: .9em;">
                        <li id="iconsLI"
                            style="float: right; top: 0px; border: 0px; background: #ffffff; list-style-type: none;">
                            <img id="save" class="viewMode" title="Save"
                            src="<c:url value='/img/icons/SaveGrey.png' />" /> <img id="add"
                            class="viewMode" title="Add"
                            src="<c:url value='/img/icons/AddGrey.png' />" /> <img id="remove"
                            class="viewMode" title="Delete"
                            src="<c:url value='/img/icons/RemoveGrey.png' />" /> <img id="edit"
                            class="viewMode" title="Edit"
                            src="<c:url value='/img/icons/EditGrey.png' />" /> <img id="cancel"
                            class="viewMode" title="Cancel"
                            src="<c:url value='/img/icons/CancelGrey.png' />" /> <img id="reset"
                            class="viewMode" title="Reset"
                            src="<c:url value='/img/icons/ResetGrey.png' />" />
                        </li>
                    </ul>
                </span>
            </div>

            <div class="content">
                <table class="listTable" style="width: 70%; margin-left: 10px;"
                    id='refsetOutputTable'>
                    <thead>
                        <tr>
                            <th class="tableHeader sizeThirty"></th>
                            <th class="tableHeader sizeOneSeventy"><fmt:message
                                    key="picklist.output.configuration.or.supplement" /></th>
                            <th class="tableHeader sizeOneSeventy"><fmt:message
                                    key="tab.order.of.picklist.or.supplement" /></th>
                        </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="section" style="padding: 2px;">
        <div class="content" style="display: inline-block; width: 70%;">
            <div class="sectionHeader">
                <a href="#"><img src="<c:url value='/img/icons/Expand.png' />" alt="Toggle"
                    onclick="javascript:toggle(this);" style="vertical-align: middle;" /></a>
                <div style="display: inline-block; vertical-align: middle;">
                    <fmt:message key="refset.specifications.for.accessibility" />
                </div>
            </div>
        </div>

        <div class="sectionContent">
            <div class="span-24 inline" style="margin-top: 15px;"width: 70%;">
                <span class="mandatory" style="margin-left: 20px;"> <fmt:message
                        key="refset.system.generated.title.page.hint" />
                </span> <span>:</span> <span style="float: right">
                    <ul style="padding-left: .9em;">
                        <li id="iconsLI"
                            style="float: right; top: 0px; border: 0px; background: #ffffff; list-style-type: none;">
                            <img id="save2" class="viewMode" title="Save"
                            src="<c:url value='/img/icons/SaveGrey.png' />" />
                        </li>
                    </ul>
                </span>
            </div>

            <div class="span-24 inline" style="margin-top: 15px;">
                <span style="margin-left: 15px;"><input type='radio' name='specification'
                    value='Y' id='documentTitle'></span> <span style="margin-left: 10px;"> <fmt:message
                        key="common.label.yes" />
                </span>
            </div>

            <div class="span-24 inline" style="margin-top: 15px;">
                <span class="mandatory" style="margin-left: 20px;"> <fmt:message
                        key="refset.product.output.document.title" />
                </span> <span>:</span> <span style="margin-left: 10px;"><input type='text'
                    id='title' name='title' maxlength='300' style='width: 250px;' disabled></span>
            </div>

            <div class="span-24 inline" style="margin-top: 15px;" id='supplementChoice'>
                <span style="margin-left: 15px;"><input type='radio' name='specification'
                    value='N' id='supplement'></span> <span style="margin-left: 10px;"> <fmt:message
                        key="common.label.no" />
                </span>
            </div>

            <div class="span-24 inline" style="margin-top: 15px;" id='supplementSelection'>
                <span class="mandatory" style="margin-left: 20px;"> <fmt:message
                        key="refset.select.title.from.supplement.list" />
                </span> <span>:</span> <span style='margin-left: 10px;'><select
                    name='supplementList' id='supplementName' style="width: 150px;"></select></span>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function() {
        displayOutputConfig();

        $.ajax({
            url: "<c:url value='/refset/getSupplementList.htm' />",
            contentType:"application/json; charset=UTF-8",
            data: {
                'contextId': ${param.contextId},
                'elementId': ${param.elementId},
                'elementVersionId': ${param.elementVersionId}               
            },
            success: function(response) {
                hideProcessingScreen();
                var hasSupplement = false;
                
                $.each(response, function(index, record) {
                    $('#supplementName').append($('<option></option>').attr("value", record.elementId).text(record.name));

                    hasSupplement = true;             
                });

                $('#supplementName').attr('disabled', true);

                if (!hasSupplement) {
                    $('#supplementChoice').hide();
                    $('#supplementSelection').hide();
                }

                $.ajax({
                    url: "<c:url value='/refset/getRefsetOutputTitle.htm' />",
                    contentType:"application/json; charset=UTF-8",
                    data: {
                        'refsetOutputId': ${param.refsetOutputId}                                   
                    },
                    success: function(response) {
                        if (response.status == 'SUCCESS') {
                            if (response.result != undefined && response.result != null) {
                                $('#save2').attr('refsetOutputId', response.result.refsetOutputId);

                                if (response.result.title != undefined && response.result.title != null) {
                                    $('#title').val(response.result.title);

                                    $('input[name=specification][value=Y]').attr('checked', true);
                                    $('input[name=specification][value=N]').attr('checked', false);
                                    $('#supplementName').attr('disabled', true);
                                } else if (response.result.supplementId != undefined && response.result.supplementId != null) {
                                    $('#title').val('');

                                    $('input[name=specification][value=Y]').attr('checked', false);
                                    $('input[name=specification][value=N]').attr('checked', true);
                                    
                                    $('#supplementName').attr('disabled', false);
                                    $('#supplementName').val(response.result.supplementId);
                                }
                            }

                            handleSpecificationSelected();

                            <c:if test="${refsetPermission != 'WRITE' || refsetExport != 'Y'}">                                
                                $('input[type=radio][name=specification]').attr('disabled', true);
                                $('#supplementName').attr('disabled', true);
                                $('#title').attr('disabled', true);                                
                            </c:if>  
                        }
                    },
                    error: function(response) {
                        hideProcessingScreen();
                        
                        var errorMessages = "System error occurred, please contact System administrator.";

                        showErrorMessages(errorMessages);
                    }                    
                });
            },
            error: function(response) {
                hideProcessingScreen();
                
                var errorMessages = "System error occurred, please contact System administrator.";

                showErrorMessages(errorMessages);
            }                    
        });

        $('input[type=radio][name=specification]').unbind('click').click(function() {
            handleSpecificationSelected();
            processSaveSpecificationEnable();
        }); 
        
        $('#title').keyup(function() {
            processSaveSpecificationEnable();
        }); 

        $('#supplementName').change(function() {
            processSaveSpecificationEnable();
        });       

        $.ajax({
            url: "<c:url value='/refset/getRefsetOutputConfiguration.htm' />",
            contentType:"application/json; charset=UTF-8",
            data: {'contextId': ${param.contextId},
                   'refsetOutputId': ${param.refsetOutputId}
            },
            success: function(response) {
                hideProcessingScreen();

                if (response.result != undefined && response.result != null) {               
                    $.each(response.result, function(index, record) {
                        var outputId = record.outputId;
                        
                        $('#refsetOutputTable').find('tbody').append("<tr id='Id_" + outputId + "'><td style='border: 1px solid #96BEBD;'></td>" + 
                            "<td style='border: 1px solid #96BEBD;'></td><td style='border: 1px solid #96BEBD;'></td></tr>");

                        $('#Id_' + outputId).find('td:eq(0)').html("<input type='radio' name='outputConfig' id='out_" + outputId + "' disabled>");
                        $('#Id_' + outputId).find('td:eq(1)').html(record.displayName);
                        $('#Id_' + outputId).find('td:eq(2)').html(record.orderNumber);    
                        
                        $('#Id_' + outputId).attr('action', 'SAVE');  
                        $('#Id_' + outputId).attr('type', record.type); 
                        $('#Id_' + outputId).attr('outputId', outputId);
                        $('#Id_' + outputId).attr('displayName', record.displayName);
                        $('#Id_' + outputId).attr('orderNumber', record.orderNumber); 

                        $('#out_' + outputId).click(function() {
                            handleConfigSelected();
                        });   
                    });

                    sortRefsetOutput();
                }                

                <c:if test="${refsetPermission == 'WRITE' || writeForlastestClosedVersion == 'Y'}">
                    <c:if test="${refsetExport == 'Y'}">
                        enableAddButton();
                        enableConfigSelection(); 
                    </c:if>
                </c:if>  

                isOutputSelectionAvailable();
            },
            error: function(response) {
                hideProcessingScreen();
                
                var errorMessages = "System error occurred, please contact System administrator.";

                showErrorMessages(errorMessages);
            }                    
        });                          
    });    
</script>
