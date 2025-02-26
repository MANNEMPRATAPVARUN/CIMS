/**
 * Content pane controller module that controls replacement of a
 * specific node in the document
 * 
 * Author: Rostislav Shnaper
 * Date: 12/4/2014
 * (c) Canadian Institute for Health Information
 */
var ContentPaneController = (function($, ContentPaneController){
	var contentMarkupId, lastNavigation;
	
	//tracks any sticky headers that might exist in the content pane
	var trackStickyTableHeader = function() {
		$("#"+contentMarkupId).scroll(function() {  
				var header = $("#sticker"); 
				if(header.length > 0) {
					var stickyHeader = $("#sticker-clone");
					
					if(stickyHeader.length && header.position().top > 0 && (header.position().top > header.height()||(header.position().top + header.height() >= stickyHeader.position().top))) {
						stickyHeader.remove();
					}
					else if(stickyHeader.length == 0 && header.position().top < 0) {
						stickyHeader = header.clone().attr("id","sticker-clone");
						stickyHeader.addClass("stick");
						stickyHeader.width(header.width());
						header.after(stickyHeader);
					}
					
					if(stickyHeader.length){
						stickyHeader.offset({top: $(this).offset().top, left:header.offset().left});
					}
				}
		});
	};
	
	var dimensions = function($element) {
		return {
			scroll: {
				top: $element.scrollTop(),
				left: $element.scrollLeft()
			},
			rect: (function () {
				var r = $element.length > 0 ? $element[0].getBoundingClientRect() : {top: 0, left: 0, bottom: 0, right: 0};
				return {
					top: r.top,
					left: r.left,
					bottom: r.bottom,
					right: r.right
				};
			})()
		};
	};
	
	var navigationCallback = function(event, data) {
		if(lastNavigation == null || lastNavigation.url != data.url) {
        	var successCallback = function() {
        		EventManager.publish("contentreplaced", data);
        		hideProcessingScreen();
        	}.bind(data);
    		ContentPaneController.replaceContent(data.url,null,successCallback,hideLoading,showProcessingScreen);
        }
		else {
			EventManager.publish("contentreplaced", data);
		}
		lastNavigation = data;
	};
	
	
	var nodeSelectionCallback = function(event, node) {
		if(typeof node != "undefined" && node != null) {
			var data = node.data;
			if(typeof data != "undefined" && data != null) {
				var conceptCodeElement = $("a[name='"+data.conceptCode+"']");
				if(conceptCodeElement.length == 0) {
					conceptCodeElement = $("a[name='"+data.conceptId+"']");
				}
		
				$(".highlightedConcept").removeClass("highlightedConcept");
				
				if(conceptCodeElement.length > 0) {	
					var $element = conceptCodeElement.first();
					$element.addClass("highlightedConcept");
		
					ContentPaneController.scrollToElement($element);
				}
			}
		}
	};
	
	var scrollTo = function($element) {
		if(typeof $element != "undefined" && $element != null) {
			var $stickyHeader = $("#sticker");
			var $scroller = $("#"+ContentPaneController.getContentMarkupId());
			
			var dStickyHeader = dimensions($stickyHeader);
			var dScroller = dimensions($scroller);
			var dElement = dimensions($element);
			
			var relTop =  dElement.rect.top - dScroller.rect.top;
			var relBottom = dScroller.rect.bottom - dElement.rect.bottom;
			
			//find out whether the header is already sticky or not
			var stickyHeaderHeight = Math.abs(dStickyHeader.rect.bottom-dStickyHeader.rect.top);
			var isStickyHeaderSticky = $("#sticker-clone").length > 0;
			var stickyHeaderOffset = (isStickyHeaderSticky ? stickyHeaderHeight : 2 * stickyHeaderHeight);
			
			var scrollTop = dScroller.scroll.top + relTop - stickyHeaderOffset;
			
			if(scrollTop > 0) {
				$scroller.scrollTop(scrollTop);
			}
		}
	};
	
	ContentPaneController.setContentMarkupId = function(markupId) {
		contentMarkupId = markupId;
		trackStickyTableHeader();
		EventManager.subscribe("navigationchange",navigationCallback);
		EventManager.subscribe("nodeselected", nodeSelectionCallback);
	};
	
	ContentPaneController.getContentMarkupId = function() {
		return contentMarkupId;
	};
	
	/**
	 * Replaces the content of a node using the AJAX response
	 */
	ContentPaneController.replaceContent = function(url, data, successCallback, errorCallback, beforeSendCallback) {
		AjaxUtil.ajax(url, data, function(result){
			ContentPaneController.replaceContentWith(result);
			if(typeof successCallback != "undefined" && successCallback != null) {
				successCallback(result);
			}
		},function(result){
			if(typeof result != "undefined" && result != null) {
				if(typeof result.responseText != "undefined") {
					ContentPaneController.replaceContentWith(result.responseText);
				}
			}
			if(typeof errorCallback != "undefined" && errorCallback != null) {
				errorCallback(result);
			}
		},beforeSendCallback);
	};

	ContentPaneController.replaceContentWith = function(data) {
		if(typeof data != "undefined" && data != null) {
			var contentContainer = $("#"+ContentPaneController.getContentMarkupId());
			contentContainer.html(data);
			contentContainer.scrollTop(0);
		}
	};
	
	
	/**
	 * Scrolls the content pane to show the specified element, accounting
	 * the sticky header (if visible)
	 */
	ContentPaneController.scrollToElement = function($element) {
		scrollTo($element);
	};
	
	return ContentPaneController;
}(jQuery, ContentPaneController || {}));