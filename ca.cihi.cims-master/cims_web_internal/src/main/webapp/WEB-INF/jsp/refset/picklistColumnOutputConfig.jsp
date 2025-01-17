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
            savePicklistOutputConfiguration();
        });
    }

    function disableSaveClickEvent() {
        $('#save').unbind('click'); 
    }

    function processSaveButtonEnable() {        
        var excelOutputTabName = $.trim($('#excelOutputTabName').val());
        var tableNameDesc = $.trim($('#tableNameDesc').val());
        var columnChecked = false; 

        $('input[type=checkbox]').each(function() {
            columnChecked = columnChecked || this.checked;
        });        
                
        excelOutputTabName != null && excelOutputTabName != '' &&
        tableNameDesc != null && tableNameDesc != '' && columnChecked ? enableSaveButton() : disableSaveButton();
    } 

    function processSublistColumn(columnElementId) {
        var checked = $('#cb-' + columnElementId).is(':checked');

        if (!checked) {
            $('#order-' + columnElementId).html('');        

            var sublistAvailable = $('#cb-' + columnElementId).attr('sublistAvailable');

            if (sublistAvailable == 'true') {
                $('#displayMode-' + columnElementId).val('CLP');
                $('#displayMode-' + columnElementId).attr('disabled', true);
            }
        }

        $('input[type=checkbox][name=columns]').each(function() {
            var parentElementId = $(this).attr('parentElementId');

            if (parentElementId != undefined && parentElementId != null) {
                if (parentElementId == columnElementId) {
                    $(this).attr('disabled', !checked);

                    if (!checked) {
                        $(this).attr('checked', false);
                        $('#order-' + $(this).attr('columnElementId')).html('');
                    }                    
                }
            }
        });
    }

    function editColumnSelected(columnElementId) {        
        var columnOrder = getColumnOrderByColumnElementId(columnElementId);       
        var columnOrderHtml = '<input type="text" id="co-' + columnElementId + '" maxlength="5" style="width: 50px;" />';
        var sublistAvailable = $('#cb-' + columnElementId).attr('sublistAvailable');

        if (sublistAvailable == 'true') {
            $('#displayMode-' + columnElementId).attr('disabled', false);
        }
            
        $('#order-' + columnElementId).html(columnOrderHtml);
        $('#co-' + columnElementId).val(columnOrder);

        $('#co-' + columnElementId).keydown(function(e) {
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
        });         

        var typingTimer;                
        var doneTypingInterval = 2000;  

        $('#co-' + columnElementId).keyup(function() { 
            clearTimeout(typingTimer);              

            typingTimer = setTimeout(function() { 
                var co = $.trim($('#co-' + columnElementId).val());
                    
                if (co == null || co == '') {
                    $('#co-' + columnElementId).val(columnOrder);
                }
            }, doneTypingInterval);            
        });  

        var parentElementId = $('#cb-' + columnElementId).attr('parentElementId');   

        if (parentElementId != undefined && parentElementId != null) {
            processDisplayModeChange(parentElementId);
        }   
    }

    function savePicklistOutputConfiguration() {
        hideMessage();
        
        var columns = [];
        var errors = {};
        var multipleExpandModeCounter = 0;
        
        $('input[type=checkbox][name=columns]').each(function() {
            var sublistColumn = $(this).attr('sublistColumn'); 
            var elementId = $(this).attr('columnElementId');

            if (sublistColumn == 'false') {
                var picklistColumnOutputId = $(this).attr('picklistColumnOutputId');
                
                if ($(this).is(':checked') || (picklistColumnOutputId != undefined && picklistColumnOutputId != null)) {
                    var sublistAvailable = $(this).attr('sublistAvailable');  
                    var orderNumber = getColumnOrderByColumnElementId(elementId);                         
                    
                    var column = {'elementId': elementId,
                                  'orderNumber': orderNumber, 
                                  'checked': $(this).is(':checked'),                                                                              
                                  'sublist': addSublistColumnConfiguration(elementId)};

                    if (sublistAvailable == 'true') {
                        column.displayMode = $('#displayMode-' + elementId).val();


                        if ($(this).is(':checked')) {
                            var numSublistColumnSelected = 0;
                            
                            $.each(column.sublist, function(index, record) {
                                numSublistColumnSelected += record.checked ? 1 : 0;                            
                            });

                            if (numSublistColumnSelected == 0) {
                                errors.noSublistColumnSelected = '<fmt:message key="picklist.output.error.sublist.column.selected" />';
                            }

                            if (numSublistColumnSelected > 1 && column.displayMode == 'CLP') {
                                errors.sublistDisplayMode = '<fmt:message key="picklist.output.error.sublist.display.mode" />';
                            }

                            multipleExpandModeCounter += column.displayMode == 'EXP' ? 1 : 0;
                        }  
                    }

                    if (picklistColumnOutputId != undefined) {
                        column.picklistColumnOutputId = picklistColumnOutputId;
                    }

                    columns.push(column);
                }               
            }
        });

        if (multipleExpandModeCounter > 1) {
            errors.multipleExpandMode = '<fmt:message key="picklist.output.error.sublist.expand.display.mode" />';
        }
        
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
        
        var columnOutputRequest = {'refsetContextId': ${param.contextId},
                                   'picklistOutputId': ${param.picklistOutputId},
                                   'asotReleaseIndCode': $.trim($('input[name=asotReleaseIndCode]:checked').val()),
                                   'outputTabName': $.trim($('#excelOutputTabName').val()),
                                   'dataTableDescription': $.trim($('#tableNameDesc').val()),
                                   'picklistColumnOutputList': columns                          
        };

        $.ajax({
            type: 'post',
            url: "<c:url value='/refset/picklist/savePicklistColumnOutput.htm' />",
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify(columnOutputRequest),
            success: function(response) {
                if (response.status == 'SUCCESS') {
                    window.location.reload();
                } else {                    
                    showErrorMessages(response.message);
                }
            },    
            error : function(response) {
                var errorMessages = "System error occurred, please contact System administrator.";

                showErrorMessages(errorMessages);

                enableSaveButton();
            }            
        });

        return; 
    }

    function getColumnOrderByColumnElementId(columnElementId) {
        var co = $('#co-' + columnElementId).val();       

        if (co != undefined && co != null) {
            co = $.trim(co);                     
        }        

        return co != undefined && co != null && co != '' ? co : getNextColumnOrder();
    }

    function getNextColumnOrder() {
        var nextColumnOrder = 0;
        
        $('input[type=checkbox][name=columns]').each(function() {
            if ($(this).is(':checked')) {
                var elementId = $(this).attr('columnElementId');
                var orderNumber = $('#co-' + elementId).val();
                
                if (orderNumber != undefined && orderNumber != null) {
                    orderNumber = parseInt($.trim(orderNumber));

                    if (orderNumber != '' && orderNumber > nextColumnOrder) {
                        nextColumnOrder = orderNumber;
                    }
                }
            }
        });

        nextColumnOrder = nextColumnOrder + 1;

        return nextColumnOrder;
    }

    function getSublistColumnConfiguration(columnElementId) {
        var columns = [];
        
        $('input[type=checkbox][name=columns]').each(function() {
            var parentElementId = $(this).attr('parentElementId');

            if (parentElementId != undefined && parentElementId != null) {
                if (parentElementId == columnElementId) {
                    var elementId = $(this).attr('columnElementId');
                    
                    columns.push({'elementId': elementId,
                                  'orderNumber': getColumnOrderByColumnElementId(elementId),
                                  'checked': $(this).is(':checked')});        
                }
            }
        });

        return columns;
    }

    function addSublistColumnConfiguration(columnElementId) {
        var columns = [];
        
        $('input[type=checkbox][name=columns]').each(function() {
            var parentElementId = $(this).attr('parentElementId');

            if (parentElementId != undefined && parentElementId != null) {
                if (parentElementId == columnElementId) {
                    var elementId = $(this).attr('columnElementId');
                    var picklistColumnOutputId = $(this).attr('picklistColumnOutputId');

                    if ($(this).is(':checked') || (picklistColumnOutputId != undefined && picklistColumnOutputId != null)) {
                        var column = {'elementId': elementId,
                                      'orderNumber': getColumnOrderByColumnElementId(elementId),
                                      'checked': $(this).is(':checked')};                       

                        if (picklistColumnOutputId != undefined && picklistColumnOutputId != null) {
                            column.picklistColumnOutputId = picklistColumnOutputId;
                        } 

                        columns.push(column);  
                    }                        
                }
            }
        });

        return columns;
    }

    function disableNoChildSublistColumn() {
        $('input[type=checkbox][name=columns]').each(function() {
            var sublistAvailable = $(this).attr('sublistAvailable');

            if (sublistAvailable == 'true') {
                var columnElementId = $(this).attr('columnElementId');
                var subColumns = getSublistColumnConfiguration(columnElementId);

                if (subColumns.length == 0) {
                    if (!($(this).is(":checked"))) {
                        $(this).attr('disabled', true);
                        $('#displayMode-' + columnElementId).attr('disabled', true);
                    }
                }
            }
        });
    }

    function processDisplayModeChange(parentElementId) {
        var subColumns = getSublistColumnConfiguration(parentElementId);
        var numSublistColumnSelected = 0;
        
        $.each(subColumns, function(index, subColumn) {
            if (subColumn.checked) {
                numSublistColumnSelected++;
            }
        });
        
        if (numSublistColumnSelected > 1) {
            $('#displayMode-' + parentElementId).val('EXP');
        }
    }

    function getLanguageByCode(languageCode) {
        var languages = {'ENG': '<fmt:message key="common.english" />',
                         'FRA': '<fmt:message key="common.french" />'};

        return languages[languageCode];
    }
    
    function displayOutputConfig(languageCode, asotIndicator) {
        $('#languageCode').html(getLanguageByCode(languageCode));
        
        if ('Y' == asotIndicator) {
        	$("#asotYes").attr('checked', 'checked');
        } else {
        	$("#asotNo").attr('checked', 'checked');
        }
    }

    function displayAccessbility() {
        <c:if test="${not empty picklistOutput.tabName}">
            $('#excelOutputTabName').val('${fn:replace(picklistOutput.tabName, "'", "\\'")}');
        </c:if>

        <c:if test="${not empty picklistOutput.tableName}">           
            $('#tableNameDesc').val('${fn:replace(picklistOutput.tableName, "'", "\\'")}');            
        </c:if>
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

    <div class="icons inline" style="margin-top: 15px; width: 70%">
        <span style="float: right">
            <ul style="padding-left: .9em;">
                <li id="iconsLI" style="float: right; top: 0px; border: 0px; background: #ffffff; list-style-type: none;">
                    <img id="save" class="viewMode" title="Save" src="<c:url value='/img/icons/SaveGrey.png' />" />                  
                </li>
            </ul>
        </span>
    </div>

    <div id="removalConfirmation" style="display: none;">confirm</div> 
    
    <div class="section" style="padding: 2px;">
        <div class="content" style="display: inline-block; width: 70%;">        
            <div class="sectionHeader">
                <a href="#"><img src="<c:url value='/img/icons/Expand.png' />" alt="Toggle" onclick="javascript:toggle(this);" style="vertical-align: middle;"/></a>
                <div style="display: inline-block; vertical-align: middle;"><fmt:message key="picklist.output.configuration" /></div>
            </div>
        </div>
        
        <div class="sectionContent">
            <div class="span-24 inline" style='margin-left: 20px; margin-top: 20px;'>
                <div>
                    <span><fmt:message key="picklist.language.picklist.output" />:</span> 
                    <span id='languageCode'></span>
                </div>
            </div>
            
            <div class="span-24 inline" style='margin-top: 20px; margin-left: 20px;'>
                <div>
                    <span><fmt:message key="picklist.output.configuration.name" />:</span> 
                    <span>${picklistOutput.name}</span>
                </div>
            </div>
            
            <div class="span-24 inline" style="margin-top: 20px; margin-left: 20px;">
                <span class="mandatory" style="margin-left: 20px;">
                    <fmt:message key="picklist.output.configuration.releasequestion" />:
                </span>
                
                <span style="margin-left: 75px;">
                    <input type="radio" name='asotReleaseIndCode' id='asotYes' value='Y'/> Yes <input type="radio" name='asotReleaseIndCode' id='asotNo' value='N'/> No 
                </span>
            </div>
            
            <div class="span-24 inline" style='margin-top: 30px; margin-bottom: 10px;'>
                <span style="margin-left: 20px;">
                    <fmt:message key="picklist.output.configuration.table" />:
                </span>
            </div>
            
            <div class="content">
                <table class="listTable" style="width: 70%; margin-left: 10px;" id='picklistColumns'>
                    <thead>
                        <tr>
                            <th class="tableHeader sizeThirty"></th>
                            <th class="tableHeader sizeEighty"><fmt:message key="picklist.table.column.order" /></th>
                            <th class="tableHeader sizeOneSeventy"><fmt:message key="picklist.table.column.type" /></th>
                            <th class="tableHeader sizeEighty"><fmt:message key="picklist.table.sub.column.order" /></th>
                            <th class="tableHeader sizeOneSeventy"><fmt:message key="picklist.table.sub.column.type" /></th>
                            <th class="tableHeader sizeOneTen"><fmt:message key="picklist.sublist.display.mode" /></th>
                        </tr>
                    </thead>
                    <tbody>                            
                        <c:forEach items='${picklist.listColumn}' var='listColumnItem' varStatus="loopStatus">
                            <c:if test="${!listColumnItem.sublistColumn}">
                                <tr>
                                    <td style='border: 1px solid #96BEBD;'><input type='checkbox' id='cb-${listColumnItem.columnElementId}' name='columns' /></td>
                                    <td style='border: 1px solid #96BEBD;' id='order-${listColumnItem.columnElementId}'></td>
                                    <td style='border: 1px solid #96BEBD;'>${listColumnItem.columnName}</td>
                                    <td style='border: 1px solid #96BEBD;'></td>
                                    <td style='border: 1px solid #96BEBD;'></td>
                                    <td style='border: 1px solid #96BEBD;'>
                                        <c:if test="${listColumnItem.sublistAvailable}">
                                            <select name='displayMode' id='displayMode-${listColumnItem.columnElementId}' disabled>
                                                <option value='CLP'><fmt:message key="picklist.sublist.display.mode.collapse" /></option>
                                                <option value='EXP'><fmt:message key="picklist.sublist.display.mode.expand" /></option>
                                            </select>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:if>
                            <c:if test="${listColumnItem.sublistColumn}">
                                <tr>
                                    <td style='border: 1px solid #96BEBD;'><input type='checkbox' id='cb-${listColumnItem.columnElementId}' name='columns' /></td>
                                    <td style='border: 1px solid #96BEBD;'></td>
                                    <td style='border: 1px solid #96BEBD;'></td>
                                    <td style='border: 1px solid #96BEBD;' id='order-${listColumnItem.columnElementId}'></td>
                                    <td style='border: 1px solid #96BEBD;'>${listColumnItem.columnName}</td>
                                    <td style='border: 1px solid #96BEBD;'></td>
                                </tr>
                            </c:if>
                            
                            <script type="text/javascript">
                                $('#cb-${listColumnItem.columnElementId}').attr('sublistColumn', '${listColumnItem.sublistColumn}');
                                $('#cb-${listColumnItem.columnElementId}').attr('columnElementId', '${listColumnItem.columnElementId}');
                                $('#cb-${listColumnItem.columnElementId}').attr('columnOrder', '${listColumnItem.columnOrder}');
                                $('#cb-${listColumnItem.columnElementId}').attr('sublistAvailable', '${listColumnItem.sublistAvailable}');

                                <c:if test="${listColumnItem.sublistColumn}">
                                    $('#cb-${listColumnItem.columnElementId}').attr('parentElementId', '${listColumnItem.containerElementId}');
                                    $('#cb-${listColumnItem.columnElementId}').attr('disabled', true);
                                </c:if>                                
                                
                                $('#cb-${listColumnItem.columnElementId}').change(function() {                                        
                                    processSublistColumn(${listColumnItem.columnElementId});

                                    if ($(this).is(":checked")) {
                                        editColumnSelected(${listColumnItem.columnElementId}); 
                                    }  

                                    processSaveButtonEnable();                                     
                                }); 

                                $('#displayMode-${listColumnItem.columnElementId}').change(function() {
                                    processSaveButtonEnable();
                                }); 
                                $('#order-${listColumnItem.columnElementId}').change(function() {
                                    processSaveButtonEnable();
                                });
                                $('#order-${listColumnItem.columnElementId}').change(function() {
                                    processSaveButtonEnable();
                                });
                                                                                  
                            </script>
                        </c:forEach>                        
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    
    <div class="section" style="padding: 2px;">
        <div class="content" style="display: inline-block; width: 70%;">        
            <div class="sectionHeader">
                <a href="#"><img src="<c:url value='/img/icons/Expand.png' />" alt="Toggle" onclick="javascript:toggle(this);" style="vertical-align: middle;"/></a>
                <div style="display: inline-block; vertical-align: middle;"><fmt:message key="picklist.specifications.accessbility" /></div>
            </div>
        </div>
        
        <div class="sectionContent">
            <div class="span-24 inline" style="margin-top: 15px;">
                <span class="mandatory" style="margin-left: 20px;">
                    <fmt:message key="picklist.excel.output.tab.name" />:
                </span>
                
                <span style="margin-left: 75px;">
                    <input type='text' name='excelOutputTabName' id='excelOutputTabName' maxlength='31' style="width: 300px;" />
                </span>
            </div>
            
            <div class="span-24 inline" style="margin-top: 15px; height: 100px;">
                <span style="margin-left: 20px; line-height: 100px; vertical-align: top;">
                    <fmt:message key="picklist.data.table.name.description" />:
                    <lable style="color: red; line-height: 100px; vertical-align: top;">*</lable>
                </span>
                
                <span style="margin-left: 20px;">
                    <textarea name='tableNameDesc' id='tableNameDesc' maxlength='250' style='width: 400px; height: 80px;'></textarea>
                </span>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function() {        
        $('#excelOutputTabName, #tableNameDesc').keyup(function() {
            processSaveButtonEnable();
        });       

        $('input[name=asotReleaseIndCode]:radio').change(function() {
            processSaveButtonEnable();
        }); 
        
        displayOutputConfig('${picklistOutput.languageCode}', '${picklistOutput.asotReleaseIndCode}');
        displayAccessbility();

        $.ajax({
            url: "<c:url value='/refset/picklist/getPicklistColumnOutputConfig.htm' />",
            contentType:"application/json; charset=UTF-8",
            data: {'picklistOutputId': ${param.picklistOutputId}},
            success: function(response) {
                $.each(response, function(index, record) {
                    $('#cb-' + record.columnId).attr('checked', true);
                    $('#cb-' + record.columnId).attr('picklistColumnOutputId', record.pickListColumnOutputId);   
                    
                    processSublistColumn(record.columnId);
                    editColumnSelected(record.columnId);

                    $('#co-' + record.columnId).val(record.orderNumber);

                    $('#co-' + record.columnId).change(function() {
                        processSaveButtonEnable();
                    });

                    if (record.displayModeCode != undefined && record.displayModeCode != null) {
                        $('#displayMode-' + record.columnId).val(record.displayModeCode);
                        $('#displayMode-' + record.columnId).attr('disabled', false);
                    }                     

                    <c:if test="${refsetPermission != 'WRITE' || refsetExport != 'Y'}">                         
                        $('#cb-' + record.columnId).attr('disabled', true);
                        $('#co-' + record.columnId).attr('disabled', true);
                        $('#displayMode-' + record.columnId).attr('disabled', true);                                         
                    </c:if>    
                });

                disableNoChildSublistColumn();      

                <c:if test="${refsetPermission != 'WRITE' || refsetExport != 'Y'}">
                    $('input[type=checkbox]').attr('disabled', true);
                    $('input[type=text]').attr('disabled', true);
                    $('select').attr('disabled', true);
                    $('textarea').attr('disabled', true);
                </c:if>                                                        
            }                
        });        
    });
</script>
