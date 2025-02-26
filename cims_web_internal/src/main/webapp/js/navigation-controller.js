/**
 * Navigation controller module that controls AJAX based navigation
 * within the classification viewer
 * 
 * Author: Rostislav Shnaper
 * Date: 12/17/2014
 * (c) Canadian Institute for Health Information
 */
var NavigationController = (function(window, $, NavigationController){
	var originalUrl = History.getState().url;
	
	var stateChangeCallback = function() {
		var state = History.getState();
		var data = state.data;
		
		if(typeof data != "undefined" && data != null) {
			EventManager.publish("navigationchange", data);
		}
	};
    // Bind to StateChange Event
    History.Adapter.bind(window,'statechange', stateChangeCallback);
    
	/**
	 * IE doesn't handle backspace key appropriately by default
	 * if there's no focus on the document, so we have to resort
	 * to handle it manually
	 */
	var backspaceCallback = function(event) {
		 if(event.which === 8 && !$(event.target).is("input, textarea")) {
			 event.preventDefault();   
		     History.go(-1);
		 }
	};
    $(window.document).bind("keydown",backspaceCallback);
    
    /**
     * Performs the navigation to the specified URL
     * without modifying the original URL within the window
     */
    NavigationController.navigate = function(url,data) {
    	//add a timestamp to the state so that
    	//even states with the same data will be unique
    	//and thus trigger a state change
    	var state = {url: url, data: data, timestamp: new Date()};
    	History.pushState(state, null, originalUrl);
    };
    
	return NavigationController;
}(window, jQuery, NavigationController || {}));