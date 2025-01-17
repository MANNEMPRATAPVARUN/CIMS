/**
 * Common AJAX utilities module
 * 
 * Author: Rostislav Shnaper
 * Date: 12/2/2014
 * (c) Canadian Institute for Health Information
 */
var AjaxUtil = (function($, AjaxUtil){
	
	/**
	 * Utility function for invoking AJAX requests
	 */
	var errorCallbackDefault = function(data) {
		hideLoading();
		var responseData = data.responseText;
		if(responseData != "undefined" && responseData != null) {
			Feedback.error(responseData);
		}else
			Feedback.error("<fmt:message key='process.request.error'/>");
	};
	
	AjaxUtil.ajax = function(url, data, successCallback, errorCallback, beforeSendCallback) {
		$.ajax({
	  		cache: false,
            url: url,
            data: data,
            success: function(data){
	        	successCallback(data);
	        },
	        error: function(data){
	        	if(typeof errorCallback != "undefined" && errorCallback != null) {
					errorCallback(data);
				}else{
					errorCallbackDefault(data);
				}
	        },
            beforeSend: beforeSendCallback
	  	 });
	};
	
	/**
	 * Replaces the content of the node with the specified markup id using the AJAX response
	 */
	AjaxUtil.replaceContent = function(markupId, url, data, successCallback, errorCallback, beforeSendCallback) {
		AjaxUtil.ajax(url, data, function(data){
			$("#"+markupId).html(data);
			if(typeof successCallback != "undefined" && successCallback != null) {
				successCallback(data);
			}
		},function(data){
			if(typeof errorCallback != "undefined" && errorCallback != null) {
				errorCallback(data);
			}else{
				errorCallbackDefault(data);
			}
		},beforeSendCallback);
	};
	
	/**
	 * Submits the form data via AJAX
	 */
	AjaxUtil.submit = function(formId, successCallback, errorCallback) {
		var $form = $("#"+formId);
	    $.ajax({
	        url: $form.attr("action"),
	        data: $form.serialize(),
	        type: "POST",
	        success: function(data){
	        	successCallback(data);
	        },
	        error: function(data){
	        	if(typeof errorCallback != "undefined" && errorCallback != null) {
					errorCallback(data);
				}else{
					errorCallbackDefault(data);
				}
	        }
	    });
	};
	
	return AjaxUtil;
}(jQuery, AjaxUtil || {}));