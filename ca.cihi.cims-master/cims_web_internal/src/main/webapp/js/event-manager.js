/**
 * Event management bus utility module that allows arbitrary code to subscribe
 * to various events
 * 
 * Author: Rostislav Shnaper
 * Date: 12/11/2014
 * (c) Canadian Institute for Health Information
 */
var EventManager = (function($,EventManager){
	/**
	 * Subscribes the specified callback to be called
	 * upon invocation of the specified event
	 */
	EventManager.subscribe = function(event,callback) {
		$(EventManager).bind(event,callback);
	};
	
	/**
	 * Publishes the event, invoking any registered
	 * callbacks and passing the optional data value
	 */
	EventManager.publish = function(event,data) {
		$(EventManager).trigger(event,data);
	};
	
	return EventManager;
}(jQuery,EventManager || {}));