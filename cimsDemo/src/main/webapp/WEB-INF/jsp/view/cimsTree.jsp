<%@ include file="../common/header.jsp"%>

<script type="text/javascript">
	$(function() {

		$("#div_id_tree")
				.dynatree(
						{
							title : "CIHI Tree",
							fx : {
								height : "toggle",
								duration : 200
							},
							autoFocus : false, // Set focus to first child, when expanding or lazy-loading.
							// In real life we would call a URL on the server like this:
							//          initAjax: {
							//              url: "/getTopLevelNodesAsJson",
							//              data: { mode: "funnyMode" }
							//              },
							// .. but here we use a local file instead:
							initAjax : {url : "getTreeData.htm?key=3"
							//url: "sample-data3.json"
							},

							onActivate : function(node) {
								$("#nodeKey").text(node.data.key);
								$("#nodeChapter").text(node.data.chapterId);
								$("#nodeTitle").text(node.data.title);								
								$("#nodeDesc").text(node.data.desc);
							},

							onLazyRead : function(node) {
								// In real life we would call something like this:
							//              node.appendAjax({
							//                  url: "/getChildrenAsJson",
							//                data: {key: node.data.key,
							//                       mode: "funnyMode"
							//                         }
							//              });
							// .. but here we use a local file instead:
							node.appendAjax( {url : "getTreeData.htm?key="+ node.data.key
								            +"&chapterId=" + node.data.chapterId
										// We don't want the next line in production code:
										//debugLazyDelay : 750
									});
						}
						});

	});
</script>

<table>
	<tr>
		<td>
		<div id="div_id_tree" style="float: left;"></div>
		</td>

		<td>
		  <div style="display:table-cell; ">		  		
			This Node's Key:<span id="nodeKey"></span><br />
			This Node's Chapter Key:<span id="nodeChapter"></span><br />						
			Text Property(short desc forTitle):<span id="nodeTitle"></span><br />
			Text Property(User_Desc):<span id="nodeDesc"></span><br />
		 </div>	
		</td>
	</tr>
</table>

<%@ include file="../common/footer.jsp"%>
