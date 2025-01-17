<!DOCTYPE html>
<html>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<head>
<style> 
	.slim {width: 200px;}
	#serchLeadTermTree ul.dynatree-container a {color: black;text-decoration: none;}
</style>
</head>
<body>
<div class="contentContainer">
	<%@ include file="indexHeader.jsp"%>
	<div class="content">
		<form:form id="formConcept" method="POST" modelAttribute="bean"
			action="indexes/termreferences/edit.htm?${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}"
		>
			<input type="hidden" name="tab" value="TERM" />
			<input type="hidden" name="lockTimestamp" value="${bean.lockTimestamp}" />
			<table class="nobottom" >
				<tr>
					<td colspan="3">
						<table style="width:100%;" class="nobottom">
							<tr>
								<c:if test="${bean.editable}">
									<td style="width:250px;vertical-align:top">
										<table>
											<tr>
												<td>
													<b>Search Lead Term:</b><br/>
													<input type="text" id="serchLeadTerm" size="50" maxlength="100" style="width:250px"/>
												</td>
											</tr>
											<tr>
												<td>
													<div id="serchLeadTermTree" style="height:300px;width:250px;float:right;overflow: scroll"></div>
												</td>
											</tr>
											<tr>
												<td>
													<input id="addReference" type="button" value="Add Reference" />
												</td>
											</tr>
										</table>
									</td>
								</c:if>
								<td align="left" style="vertical-align:top">
									<table style="width:600px">
										<tr>
											<td>
												<b>Index References:</b>
											</td>
											<td style="text-align:right">
												<c:if test="${bean.editable}"><span class="required">*</span></c:if>
												<form:radiobutton path="model.seeAlso" value="false" disabled="${!bean.editable}"/>See
												<form:radiobutton path="model.seeAlso" value="true"  disabled="${!bean.editable}"/>See Also
											</td>
										</tr>
										<tr>
											<td colspan="2">
												<table id="referenceTable" class="listTable" style="height:330px;width:100%">
													<thead>
														<tr>
															<c:if test="${bean.editable}">
																<th class="tableHeader"></th>
															</c:if>
															<th class="tableHeader">Reference Index Term</th>
															<th class="tableHeader">Custom Description</th>
														</tr>
													</thead>
													<tbody>
														<c:forEach items="${bean.references}" var="ref" varStatus="i" begin="0" >
									                        <tr class="odd" style="display:${i.index==0?'none':''}">
									                        	<c:if test="${bean.editable}">
										                        	<td>
										                        		<form:checkbox path="references[${i.index}].deleted"   value="true"/>
										                        		<form:hidden   path="references[${i.index}].elementId" />
										                        	</td>
									                        	</c:if>
									                            <td>${ref.breadCrumbs}</td>
									                            <td>
									                            	<c:choose>
																		<c:when test="${bean.editable}">
																			<form:input path="references[${i.index}].customDescription" size="50" maxlength="200"/>
																		</c:when>
																		<c:otherwise>${ref.customDescription}&nbsp;</c:otherwise>							                            		
									                            	</c:choose>
									                            </td>
									                        </tr>
														</c:forEach>
														<tr class="odd" height="100%">
															<c:if test="${bean.editable}"><td>&nbsp;</td></c:if>
															<td>&nbsp;</td><td>&nbsp;</td>
														</tr>
													</tbody>
												</table>
												<c:if test="${bean.editable}">
													<input id="deleteReference" type="button" value="Delete Reference" />
												</c:if>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
					<table style="width: 100%;">
						<tr>
							<td align="right" nowrap="nowrap" valign="top">
								<c:if test="${bean.editable}">
									<a id="formSave" href="#"><img title="Save" src="<c:url value="/img/icons/Save.png"/>"/></a>
									<a id="formReset" href="<c:url value='/indexes/termreferences/edit.htm?${automaticContextParams}&id=${bean.elementId}&tab=TERM&language=${param.language}'/>"><img
										title="Reset" src="<c:url value="/img/icons/Reset.png"/>"/></a>
								</c:if>
							</td>
							<%-- <td width="100%" valign="top">
								<div id="status">
								<div id="statusResult"><c:if test="${!(empty bean.result)}">
									<c:choose>
										<c:when test="${bean.result eq 'SUCCESS'}">
											<div class="success">Changes have been successfully saved.</div>
										</c:when>
										<c:when test="${bean.result eq 'INVALID'}">
											<div class="notice">Validation errors.</div>
										</c:when>
										<c:when test="${bean.result eq 'INCOMPLETE'}">
											<div class="success">The incomplete data has been successfully saved.</div>
										</c:when>
										<c:otherwise>
											<div class="error">Error: <c:out value="${bean.errorMessage}" /></div>
										</c:otherwise>
									</c:choose>
								</c:if></div>
								</div>
							</td>
							 --%>
						</tr>
					</table>
					</td>
				</tr>
			</table>
		</form:form>
	</div>
</div>
<script>
	$('#crLastUpdatedTime').text('${bean.lockTimestamp}');
	$(document).ready(function(){
		var result = "${bean.result}";
		
		if(result != null && result != "") {
			if(result == 'SUCCESS') {
				Feedback.success("<fmt:message key='save.request.success'/>");
			}
			else if(result == 'INVALID') {
				Feedback.warn("<fmt:message key='save.request.error.validation'/>");
			}
			else {
				Feedback.error("<fmt:message key='save.request.error'><fmt:param value='${bean.errorMessage}'/></fmt:message>");
			}
		}
	});
	$(function() {
		$("#formSave").click(function(event) {
			showLoading("<fmt:message key='progress.msg.save'/>");
			var replaceContent = function(data) {
				hideLoading();
				ContentPaneController.replaceContentWith(data);
			};
			var url = $("#formConcept")[0].action;
			url=turnOnTimestampCheck(url,parent.document.body);
			$("#formConcept")[0].action=url;
			AjaxUtil.submit("formConcept", replaceContent, null);
			return false;
		});
		$("#formReset").click(function(event) {
			event.preventDefault();
			ContentPaneController.replaceContent(this.href,null,hideLoading,hideLoading,function(){showLoading("<fmt:message key='progress.msg.load'/>");});
		});
		$("#deleteReference").click(function() {
			var selected = $("#referenceTable").find("input:checked");
			selected.each(function() {
				var ch = $(this);
				var tr = ch.parent().parent();
				tr.hide();
			});
			return false;
		});
		$("#addReference").click(function() {
			var selected=null;
			var tree = $("#serchLeadTermTree").dynatree("getTree");
			if(tree!=null){
				selected=tree.getSelectedNodes();
			}
			if(selected==null||selected.length==0){
				var ch=$("#rootTerm:checked");
				if(ch.size()!=0){
					var n={
						data:{
							key:ch.val(),
							title:$("#rootTermContainer").text()
						},
						toggleSelect:function(){
							ch.attr('checked', false);
						}
					};
					selected=[n];
				}
			}
			if(selected!=null && selected.length!=0){
				var ids=[];
				for(var i=0;i<selected.length;i++){
					ids.push(selected[i].data.key);
				}
				showLoading();
				$.ajax({
					url: "${pageContext.request.contextPath}/indexes/termreferences/edit/breadCrumbs.htm?${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}",
					contentType: "application/json; charset=UTF-8",
					data: {ids: ids.join()},
					success: function(keyBreadCrump) {
						hideLoading();
						var tb = $("#referenceTable");
						var trs = tb.find("tr");
						var nextIndex = trs.size() - 2;
						var tr = trs.eq(1);
						for(var i=0;i<selected.length;i++){
							var n=selected[i].data;
							var key=n.key;
							var ds = $("input:hidden[value=\'" + key + "\']");
							var duplicate = false;
							if (ds.size() != 0) {
								ds.each(function() {
									var d = $(this);
									var id = d.attr("id");
									id = replaceAll(id, ".elementId", "\\.deleted1");
									var c = $("#" + id);
									if (c.size() != 0) {
										if (!c.is(":checked")) {
											alert("[" + n.title + "] is already added");
											duplicate = true;
										}
									}
								});
							}
							if (!duplicate) {
								var bread=keyBreadCrump[n.key];
								var html = tr.clone().wrapAll('<div></div>').parent().html();
								html = replaceAll(html, "[0]", "[" + nextIndex + "]");
								html = replaceAll(html, "\"1\"", "\"" + key + "\"");
								html = replaceAll(html, "breadCrumbs", bread);
								html = replaceAll(html, "references0", "references" + nextIndex);
								html = replaceAll(html, "\"customDescription\"", "\"" + replaceAll(bread, " >", ",") + "\"");
								var newtr = $('<div/>').html(html).contents();
								newtr.attr("style", "");
								newtr.insertAfter(tb.find("tr").eq(nextIndex));
								selected[i].toggleSelect();
								nextIndex++;
							}
						}//for
					}//success
				});
			}
		});
		$("#serchLeadTerm").autocomplete({
			source: function(request, response) {
				$.ajax({
					url: "${pageContext.request.contextPath}/getCodeSearchResult.htm?classification=${param.ccp_bc}&contextId=${param.ccp_cid}&language=${param.language}",
					contentType: "application/json; charset=UTF-8",
					data: {
						term: request.term,
						searchBy: "bookIndex",
						indexElementId: ${bean.model.bookElementId}
					},
					success: function(data) {response(data);}
				});
			},
			select: function(event, ui) {
				buildDynamicTree(ui.item.conceptId, ui.item.label);
			}
		}).css({
			backgroundColor: "yellow"
		});
	});
	
	function buildDynamicTree(conceptId, title) {
		var container = $("#serchLeadTermTree");
		container.dynatree({
			checkbox: true,
			selectMode: 2,
			fx: {
				//height: "toggle",
				cache: false,
				duration: 200
			},
			autoFocus: false,
			initAjax: {
				url: "${pageContext.request.contextPath}/getTreeData.htm?classification=${param.ccp_bc}&contextId=${param.ccp_cid}&language=${param.language}&chRequestId=${param.ccp_rid}&conceptId=" + conceptId,
				contentType: "application/json; charset=utf-8"
			},
			onPostInit: function(isReloading, isError) {
				$("#rootTermContainer").remove();
				var treeContainer=$(".dynatree-container");
				var root=this.getRoot();
				var children=root.getChildren();
				if(children==null||children.length==0){
					root.removeChildren();
					var str="<div id='rootTermContainer'><input id='rootTerm' type='checkbox' value='"+conceptId+"'/>"+ title+"</div>";
					container.prepend(str);
				}else{
               		var datas=[];
	                for(var i=0;i<children.length;i++){
	                	datas.push(children[i].data);
	                }
	                root.removeChildren();
					var data={
						"key": ""+conceptId+"",
						title:title,
						expand:true,
						isFolder:true,
						isLazy:false,
						children: datas,
						conceptId:conceptId,
						language:"${param.language}",
						contextId:${param.ccp_cid},
						chRequestId:${param.ccp_rid},
						classification:"${param.ccp_bc}"
					};
	                var node=root.addChild(data);
	                node.setLazyNodeStatus(DTNodeStatus_Ok);
					//var nc=node.addChild({title:'xxxx'});
					//nc.remove();
	                this.reactivate();
				}
			},
			onExpand: function(expanded, node) {
				$.ajax({
					cache: false,
					url: "${pageContext.request.contextPath}/getTitle.htm?classification=" + node.data.classification + "&contextId=" + node.data.contextId + "&language=" + node.data.language + "&conceptId=" + node.data.conceptId,
					success: function(data, textStatus) {
						node.setTitle(data);
					}
				});
				if (!expanded && node.isLazy()) {
					node.resetLazy();
				}
			},
			onLazyRead: function(node) {
				node.appendAjax({
					url: "${pageContext.request.contextPath}/getTreeData.htm?classification=" + node.data.classification + "&contextId=" + node.data.contextId + "&language=" + node.data.language + "&conceptId=" + node.data.conceptId + "&containerConceptId=" + node.data.containerConceptId + "&chRequestId=" + node.data.chRequestId,
					contentType: "application/json; charset=utf-8"
				});
			}
		});
		
		var tree=container.dynatree("getTree");
		if(tree!=null){
			try{tree.reload();}
			catch(e){}
		}
	}
</script>
</body>
</html>


