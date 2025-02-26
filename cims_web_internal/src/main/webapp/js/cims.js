function removeBox(params){
	var div=getDialog();
	div.html(params.text);
	div.dialog(
		{
			title:params.title,
			width : 350, height : 150, modal : true, resizable : false, draggable : false,
			buttons : [ {
				text : 'Remove',
				click : function() {
					$(this).dialog('close');
					params.callback();
				}
			}, {
				text : 'Cancel',
				click : function() {
					processingSomething = false;
					$(this).dialog('close');
				}
			}]
		}
	);
	div.dialog("open");
}

function getDialog(){
	var div=$("#dialog_div");
	if(div.length==0){
		div=$("<div id='dialog_div' style='display:none;'/>").appendTo('body');
	}
	return div;
}

function showIframeDialog(title, width, height, url){
	var div=getDialog();
	div.dialog(
		{
			title:title,
			width : width,
			height: height,
			modal : true,
			resizable : false,
			draggable : false,
			buttons : []
	    });
	div.html('<iframe frameborder="0" scrolling="no" width="100%" height="100%" src="'+url+'"/></iframe>');
}

function showProcessingScreen() {
	setTimeout(function(){
		var modalVeil = $(".modal");
		if(modalVeil.length == 0) {
			$('body').append('<div class="modal">');
		}
		else {
			modalVeil.show();
		}
	},0);
}

function showIframeContentLoading(iframeName){
	var fr=$("#" + iframeName);
	var sp=document.getElementById(iframeName).contentWindow.showProcessingScreen;
	if(sp!=null){
		sp();
	}
}

function hideProcessingScreen() {
	setTimeout(function(){
		$('.modal').hide();
	},0);
}

function showLoading(message){
	if(message!=null){
		Feedback.info(message);
	}
	showProcessingScreen();
}

function hideLoading(){
	hideProcessingScreen();
}

function popupwindow(url, title, w, h) {
	var left = (screen.width/2)-(w/2);
	var top = (screen.height/2)-(h/2);
	var newWindow = window.open(url, title, 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, copyhistory=no, width='+w+', height='+h+', top='+top+', left='+left);
	if(newWindow) {
		newWindow.focus();
	}
}

function replaceAll(str, find, replace) {
	return str.split(find).join(replace);
}


function checkDownloadProgress(url, token){
	
	var url2 = url+"?token="+token+"&q="+Math.random();
	$.ajax({
		url: url2,
		success: function(res){
			token=res.token;
			if(token!=null){
				setTimeout(checkDownloadProgress(url,token), 1000); 
			}else{
				hideProcessingScreen();
				//setTimeout($('#frmExportFile').remove(), 1000); 
				//$('#frmExportFile').remove();
			}
		}		
	});
	
	
}

function checkFrmDownloadProgress(url, token, frameId){
	
	var url2 = url+"?token="+token+"&q="+Math.random();
	$.ajax({
		url: url2,
		success: function(res){
			token=res.token;
			if(token!=null){
				setTimeout(checkFrmDownloadProgress(url,token), 1000); 
			}else{
				hideProcessingScreen();
				$(frameId).remove();
			}
		}		
	});
	
	
}


function generateReport(validationUrl, generationUrl,checkUrl){
	var url=validationUrl+"?q="+Math.random();
	$('#errorMessages').html('');
	$.ajax({
		url : url,
	    data: $('#viewForm').serialize(),
	    success: function(data){
	    	if("FAILED"==data.status){
	    		var errorMessages = '';
	    		for(var i=0 ; i<data.errors.length;i++){
	    			errorMessages += '<li>'+ data.errors[i] + '</li>';
	    		}
	    		$('#errorMessages').html(errorMessages);
	    	}else{
	    		var token = data.token;
	    		var url1 = generationUrl+"?token="+token+"&"+$('#viewForm').serialize();
	    		var iframe = $('<iframe id="frmExportFile" target="_blank" name="frmExportFile" style="display:none" src="' + url1 + '"></iframe>');
	    		iframe.appendTo('body');
	    		showProcessingScreen();
	    		checkDownloadProgress(checkUrl, token);
	    	}
	    }
	});
}

function exportReport(validationUrl, generationUrl, checkUrl, frmName, errorContainer, iframeId){
	var url=validationUrl+"?q="+Math.random();
	$.ajax({
		url : url,
	    data: $(frmName).serialize(),
	    success: function(data){
	    	if("FAILED"==data.status){
	    		var errorMessages = '<div class="error">';
	    		for(var i=0 ; i < data.errors.length;i++){
	    			errorMessages += '<div>'+data.errors[i]+'</div>';
	    		}
	    		errorMessages += '</div>';
	    		$(errorContainer).html(errorMessages);
	    	}else{
	    		var token = data.token;
	    		var url1 = generationUrl+"?token="+token;
	    		var iframe = $('<iframe id=iframeId target="_blank" name=iframeId style="display:none" src="' + url1 + '"></iframe>');
	    		iframe.appendTo('body');
	    		showProcessingScreen();
	    		checkFrmDownloadProgress(checkUrl, token, iframeId);
	    	}
	    }
	});
}

function turnOnTimestampCheck(url, parentDom){
	var lockTimestamp ;
	if(parentDom == null){
		lockTimestamp = $('#crLastUpdatedTime').text();
	}else{
		lockTimestamp = $('#crLastUpdatedTime', parentDom).text();
	}
	var apendUrl = 'lockTimestamp='+lockTimestamp+'&checkTimestamp=Y';
	if(url.indexOf('?')==-1){
		return url+'?'+apendUrl;
	}else{
		return url+'&'+apendUrl;
	}
}

function popupChangeRequestViewer(baseUrl, changeRequestId) {
    var link = baseUrl+"manageChangeRequest.htm?changeRequestId="+changeRequestId;
	  var newwindow = window.open(link, "changeRequest"+changeRequestId, "width=1200,height=750,resizable=yes,scrollbars=yes");
	  if (window.focus)  {
		  newwindow.focus();
	  }
}	

function popupResultViewer(baseUrl, subUrl, frmName, title, size) {
    var link = baseUrl+subUrl+'?'+$(frmName).serialize();
    var newwindow = window.open(link, title, size);	  
	if (window.focus)  {
	    newwindow.focus();
	}
}

//=============================================================================

function getProcessedMessage(){
	var processedMessage = {};
	processedMessage["class"] = '';
	processedMessage["message"] = '';	
	return processedMessage;
}

function showMessageEx(processedMessage) {
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

function hideMessageEx() {
	$("#loadingInfo").hide();		
}

function showInfoMessageEx(messageToShow) {
	var processedMessage=getProcessedMessage();
	processedMessage["message"] = messageToShow;
	processedMessage["class"] = "info";
	showMessageEx(processedMessage);
}

function showSuccessMessageEx(messageToShow) {
	var processedMessage=getProcessedMessage();
	processedMessage["message"] = messageToShow;
	processedMessage["class"] = "success";		
	processedMessage["image"] = "<img src=\""+getRootWebSitePath()+"/img/icons/Ok.png\"/>";
	showMessageEx(processedMessage);
}
	
function showErrorMessagesFromResponseEx(response) {
	var errorMessages = "";
	for ( var i = 0; i < response.errorMessageList.length; i++) {
		var item = response.errorMessageList[i];
		errorMessages += "<img src=\""+getRootWebSitePath()+"/img/icons/Error.png\"/> " + item.defaultMessage;
		errorMessages += "<br/>";
	}
	var processedMessage=getProcessedMessage();
	processedMessage["message"] = errorMessages;
	processedMessage["class"] = "error";		
//		processedMessage["image"] = "<img src=\"img/icons/Error.png\"/>";
	showMessageEx(processedMessage);
}

function getRootWebSitePath(){
    var _location = document.location.toString();
    var applicationNameIndex = _location.indexOf('/', _location.indexOf('://') + 3);
    var applicationName = _location.substring(0, applicationNameIndex) + '/';
    var webFolderIndex = _location.indexOf('/', _location.indexOf(applicationName) + applicationName.length);
    var webFolderFullPath = _location.substring(0, webFolderIndex);
    return webFolderFullPath;
}








