var Feedback = (function($,Feedback){
	
	Feedback.info = function(message) {
		showFeedback(message, {level: "info"});
	};
	
	Feedback.error = function(message) {
		showFeedback(message, {level: "error"});
	};
	
	Feedback.warn = function(message) {
		showFeedback(message,{level: "warn"});
	};
	
	Feedback.success = function(message) {
		showFeedback(message,{level: "success"});
	};
	
	Feedback.incomplete = function(message) {
		showIncomplete(message, {level: "warn"});
	}
	
	Feedback.hide = function() {
		hideFeedback();
	};
	
	var showFeedback = function(message, options) {
		var feedbackElement = getFeedbackElement();
		if(feedbackElement != null) {
			applyFeedbackStyle(feedbackElement,options.level);
			feedbackElement.html(message);
			feedbackElement.show();
		}
	};
	
	var showIncomplete = function(message, options) {
		var incompleteElement = getIncompleteElement();
		if(incompleteElement != null) {
			applyFeedbackStyle(incompleteElement,options.level);
			incompleteElement.html(message);
			incompleteElement.show();
		}
	};
	
	var hideFeedback = function() {
		var feedbackElement = getFeedbackElement();
		if(feedbackElement != null) {
			feedbackElement.hide();
		}
		var incompleteElement = getIncompleteElement();
		if(incompleteElement != null){
			incompleteElement.hide();
		}
	};
	
	var getIncompleteElement = function() {
		var incompleteElement = $("#incomplete");
		if(incompleteElement.length == 0) {
			incompleteElement = createDomElement("incomplete");
		}
		return incompleteElement;
	};
	
	var getFeedbackElement = function() {
		var feedbackElement = $("#feedback");
		if(feedbackElement.length == 0) {
			feedbackElement = createDomElement("feedback");
		}
		return feedbackElement;
	};
	
	var createDomElement = function(markupId) {
		var feedbackElement = $("<div></div>").attr("id",markupId);
		$("#content").append(feedbackElement);
		return feedbackElement;
	};
	
	var applyFeedbackStyle = function($element, level) {
		if(typeof $element != "undefined" && $element != null) {
			var feedbackClass = "info";
			if(level == "error") {
				feedbackClass = "error";
			}
			else if(level == "warn") {
				feedbackClass = "notice";
			}
			else if(level == "success") {
				feedbackClass = "success";
			}
			$element.removeClass();
			$element.addClass(feedbackClass);
		}
	};
	
	return Feedback;
}(jQuery, Feedback || {}));