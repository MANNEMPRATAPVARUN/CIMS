function getQueryVariable(variable) {
	  var query = window.location.search.substring(1);
	  var vars = query.split("&");
	  for (var i=0;i<vars.length;i++) {
	    var pair = vars[i].split("=");
	    if (pair[0] == variable) {
	      return pair[1];
	    }
	  } 
	}

function popupConceptDetail(url){	
	var currentContextId = document.getElementById("currentContextId");
	url = url + "&contextId=" + currentContextId.getAttribute("value");
	setTimeout(function(){openWindow(url, 'conceptDetailPopup', 'scrollbars=1,resizable');},0);
}
	
function popupCciValidation(url){
	var currentContextId = document.getElementById("currentContextId");	
	url = url + "&contextId=" + currentContextId.getAttribute("value");
	newWindow = window.open(url, 'cciValidationPopup', 'height=400,width=850,scrollbars=0,resizable');
}

function popupIcdValidation(url){
	var currentContextId = document.getElementById("currentContextId");
	url = url + "&contextId=" + currentContextId.getAttribute("value");
	newWindow = window.open(url, 'icdValidationPopup', 'height=400,width=850,scrollbars=0,resizable');
}

function popupAttribute(url){
	var currentContextId = document.getElementById("currentContextId");
	url = url + "&contextId=" + currentContextId.getAttribute("value");
	setTimeout(function(){openWindow(url, 'attributePopup', 'scrollbars=1,resizable');},0);
}

function popupDiagram(url){
	var currentContextId = document.getElementById("currentContextId");
	url = url + "&contextId=" + currentContextId.getAttribute("value");
	setTimeout(function(){openWindow(url, 'diagramPopup', 'scrollbars=1,resizable');},0);
}

function popupMissingGraphicMessage(){
	alert("<xref> or <graphic> XML content for this diagram are incorrect");
}

function navigateFromDynaTree(path) {
	EventManager.publish("pathselected",path);
} 

function openWindow(url,title,options) {
	var defaultWidth = 200;
	var defaultHeight = 100;
	var left = (screen.width/2)-(defaultWidth/2);
	var top = (screen.height/2)-(defaultHeight/2);
	
	var win = window.open(url,title,options+",width="+defaultWidth+",height="+defaultHeight+",left="+left+",top="+top);
	if(typeof win != "undefined" && win != null) {
		var resizeWindow = function() {
			if(typeof win != "undefined" && win != null) {
				//calculate max defaults
				var width = Math.min(win.document.body.scrollWidth, 800);
				var height = Math.min(win.document.body.scrollHeight, 600);
				
				//50px padding for the window chrome
				win.resizeTo(width + 50, height + 50);
				
				var left = (screen.width/2)-(width/2);
				var top = (screen.height/2)-(height/2);
				win.moveTo(left,top);
				if(win.focus) {
					win.focus();
				}
			}
		}.bind(win);
		win[win.addEventListener ? 'addEventListener' : 'attachEvent']((win.attachEvent ? 'on' : '') + 'load', resizeWindow, false);
	}
	return win;
}
