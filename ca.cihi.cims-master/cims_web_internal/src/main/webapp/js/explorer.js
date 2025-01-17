var g_viewMode=true;
var g_lastNode=null;
	
   function initializeTree(markupId, options) {
	   $('#'+markupId).dynatree(
				{
					title : "CIHI Tree",
					fx : {
						height : "toggle",
						cache  : false,
						duration : 200
					},
					autoFocus : false, // Set focus to first child, when expanding or lazy-loading.			
					initAjax : {
						url : "getTreeData.htm",
						data: {classification: options.classification, contextId: options.contextId, language: options.language, chRequestId : options.changeRequestId},
						contentType:"application/json; charset=utf-8"
					},
					onActivate : function(node){
						if(options.viewMode) {
							viewCurrentNode(node);
						}
						else {
							editCurrentNode(node);
						}
						$('#activateNode').text(node.data.conceptId);
	                },
	                onPostInit: function(isReloading, isError) {
	                    var key = $('#activateNode').text();
	                    if( key ) {
	                    	$.ajax({
	                            url: "getConceptIdPathByConceptId.htm?classification="+options.classification+"&contextId="+options.contextId+"&conceptId=" + key,
	      					   success: function(data, textStatus){
	      						   EventManager.publish("pathselected", data);
	      					   }
	      				     }
	                    	);
	                    }
	                },
	                onExpand : function(expanded,node) {
	                	$.ajax({
	                		  cache: false,
	                          url: "getTitle.htm?classification="+node.data.classification
								+"&contextId="+node.data.contextId
								+"&language="+node.data.language
								+"&conceptId=" + node.data.conceptId,
	                          success: function(data, textStatus){
	                        	  node.setTitle(data);
	                          }
	                	 });
	                	if (!expanded){
	                	    node.resetLazy();
					    }
	                },
					onLazyRead : function(node) {
						node.appendAjax( {
								url : "getTreeData.htm"
								+"?classification="+node.data.classification
								+"&contextId="+node.data.contextId
								+"&language="+node.data.language
								+"&conceptId=" + node.data.conceptId
								+"&containerConceptId=" + node.data.containerConceptId 
								+"&chRequestId="+node.data.chRequestId,
								
								contentType:"application/json; charset=utf-8"
						});
			        }
				});
	   
	   //callback for when navigation of the page changes
	   //and we need to reselect the concept and/or tree node
	   	var navigationCallback = function(event, data) {
	   		if(typeof data != "undefined" && data != null) {
				var userData = data.data;
				if(typeof userData != "undefined" && userData != null) {
					if(typeof userData.conceptId != "undefined" && userData.conceptId != null) {
						var node = selectNode(userData.conceptId);
						if(node != null) {
							EventManager.publish("nodeselected",node);
						}
					}
					
					if(typeof userData.isViewMode != "undefined" && userData.isViewMode != null && g_viewMode != userData.isViewMode) {
						g_viewMode = data;
						EventManager.publish("viewmodechanged", userData.isViewMode);
					}
				}
	   		}
	   	};
	   	
	   	//view mode callback
	   	var viewModeCallback = function(event, data) {
	   		//alert('viewModeCallback: 1');
	   		if(typeof data != "undefined" && data != null) {
	   			//alert(viewModeCallback: 2');
	   			if(g_viewMode != data) {
	   				//alert('viewModeCallback: 3');
	   				g_viewMode = data;
	   				viewOrEditNode();
	   			}
	   		}
	   	};
	   	
	   	//search results callback
		var searchHandler = function(event, path) {
			var tree = getTree();
			tree.loadKeyPath( path, function(node, status) {
				var activeNode = tree.getActiveNode();
				if(activeNode != null){
					activeNode.deactivate();
				}
			    if(status == "loaded") {
			    	// expand as we go
			        node.expand(); 
			    } else if(status == "ok") {
			    	// implies a leaf node
			    	// activate() expands the whole branch to this point
			        node.activate();
			    	scrollToNode(node);
			    	EventManager.publish("nodeselected",node);
			    } else if(status == "notfound") {
			    	// Not supported in IE. Yay IE.
					// console.log("Loading a path not found within the dynatree: " + path);
			    }
			});
		};
	   	
	   	EventManager.subscribe("pathselected", searchHandler);
		EventManager.subscribe("viewmodechanged", viewModeCallback);
		EventManager.subscribe("contentreplaced", navigationCallback);
   };
	
   
	function getTree(){
		return $("#tree").dynatree("getTree");
	}
	
	function deactiveNode(){
	   var tree = getTree();
	   var activeNode = tree.getActiveNode();
	   if(activeNode != null) {
		   activeNode.deactivate();
	   }
	}

	function viewOrEditNode(){
		var tree =  getTree();
		var activeNode = tree.getActiveNode();
		viewCurrentNode(activeNode);
	}

	function viewCurrentNode(node){
		if(node==null){
			node=$("#tree").dynatree("getRoot").getChildren()[0];
		}
		//alert("viewCurrentNode: "+node)
		g_lastNode=node;
		if(g_viewMode){
			viewCurrentNodeInternal(node);
		}else{
			editCurrentNodeInternal(node);
		}
	}

	function setNodeTitle(conceptId, conceptCode, title){
		var node=getTree().getNodeByKey(conceptId);
		if(node != null) {
			node.setTitle(title);
			node.data.conceptCode=conceptCode;
		}
	}
	
	function removeNode(deletedConceptId){
		var node=getTree().getNodeByKey(deletedConceptId);
		if(node!=null){
			node.remove();
			if(node == g_lastNode) {
				var parent = node.parent;
				if(typeof parent != "undefined" && parent != null) {
					g_lastNode = selectNode(parent.data.conceptId);
				}
			}
		}
	}

	function refreshLastNodeChildren(newConceptId){
		if(g_lastNode!=null){
			if(newConceptId==null){
				g_lastNode.reloadChildren();
			}else{
				g_lastNode.reloadChildren(function(){
					var newNode = selectNode(newConceptId);
					if(newNode != null) {
						g_lastNode = newNode;
					}
				});
			}
		}
	}
	
	function refreshLastNodeParent(conceptId){
		if(g_lastNode!=null){
			g_lastNode.parent.reloadChildren(function(){
				var newNode = selectNode(conceptId);
				if(newNode != null) {
					g_lastNode = newNode;
				}
			});
		}
	}
	
	function editCurrentNodeInternal(node){
		var conceptType=node.data.conceptType;
		//alert("editCurrentNodeInternal:" + conceptType)
		var page="tabulars";
		if(conceptType.indexOf("Index")!=-1){
			page="indexes";
		}else if(conceptType.indexOf("Supplement")!=-1){
			page="supplements";
		}
		var dataUrl= (page)
			+ "/basicInfo/edit.htm"
			+"?ccp_bc="+node.data.classification
			+"&ccp_cid="+node.data.contextId
			+"&ccp_rid="+node.data.chRequestId
			+"&language="+node.data.language
			+"&ccp_on=1"
			+"&id="+node.data.conceptId
			+"&chRequestId="+node.data.chRequestId;
		var data = {isViewMode: g_viewMode, conceptId: node.data.conceptId, conceptCode: node.data.conceptCode};
		//alert(dataUrl);
		NavigationController.navigate(dataUrl, data);
	}
	
	function viewCurrentNodeInternal(node){
		var dataUrl="contents.htm?classification="+node.data.classification
					+"&contextId="+node.data.contextId
					+"&language="+node.data.language
					+"&chRequestId="+node.data.chRequestId
					+"&conceptId=" + (node.data.containerConceptId == '0' ? node.data.conceptId : node.data.containerConceptId)
					+"&containerConceptId="+node.data.containerConceptId;
		
		var data = {isViewMode: g_viewMode, conceptId: node.data.conceptId, conceptCode: node.data.conceptCode};
		NavigationController.navigate(dataUrl, data);
    }
	
	function selectNode(nodeId) {
		var node = null;
		if(typeof nodeId != "undefined" && nodeId != null) {
			var tree = getTree();
			if(tree != null) {
				node = tree.getNodeByKey(nodeId);
				var activeNode = tree.getActiveNode();
				if(node != null && node != activeNode) {
					node.activateSilently();
					scrollToNode(node);
				}
			}
		}
		return node;
	}
	
	function scrollToNode(node) {
		if(typeof node != "undefined" && node != null) {
			if(node.li != null) {
				setTimeout(function(){node.li.scrollIntoView(false);},0);
			}
		}
	}