<!DOCTYPE html>
<html>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<head>
<style> 
	.err  {color: red;}
	.slim {width: 200px;}
</style>
</head>
<body>
<div class="contentContainer">
	<%@ include file="indexHeader.jsp"%>
	<div class="content">
		<form:form id="formConcept" method="POST" modelAttribute="bean"
			action="indexes/codereferences/editICD12CCI.htm?${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}">
			<input type="hidden" name="tab" value="CODE" />
			<table class="nobottom" >
				<tr>
					<td colspan="3">
						<table id="referenceTable" border="1" cellpadding="0" cellspacing="0" class="listTable">
							<thead>
								<tr>
									
									<c:choose>
										<c:when test="${bean.model.icd1}">
											<th class="tableHeader">Main Code Value</th>
											<th class="tableHeader">Main Customized Description</th>
											<th class="tableHeader">Dagger</th>
											<th class="tableHeader">Paired Code Value</th>
											<th class="tableHeader">Paired Customized Description</th>
											<th class="tableHeader">Asterisk</th>
										</c:when>
										<c:otherwise>
											<th class="tableHeader">Reference Code Value</th>
											<th class="tableHeader">Custom Description</th>
										</c:otherwise>
									</c:choose>
									<c:if test="${bean.editable}">
										<th class="tableHeader"></th>
									</c:if>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${bean.references}" var="ref" varStatus="i" begin="0" >
			                      <tr class="odd" style="display:${i.index==0?'none':''}">
			                        <td>
			                        	<c:choose>
											<c:when test="${bean.editable}">
					                        	<form:hidden path="references[${i.index}].deleted" />
					                        	<form:hidden path="references[${i.index}].mainElementId" />
					                        	<form:input path="references[${i.index}].mainCode" size="10" cssClass="auto" />
					                        </c:when>
					                        <c:otherwise>${bean.references[i.index].mainCode}</c:otherwise>
					                     </c:choose>
			                        </td>
			                      	<td>
			                      		<c:choose>
											<c:when test="${bean.editable}">
			                      				<form:input path="references[${i.index}].mainCustomDescription" size="20" maxlength="200"/>
			                      			</c:when>
					                        <c:otherwise>${bean.references[i.index].mainCustomDescription}</c:otherwise>
					                	</c:choose>
			                      	</td>
			                      	<c:if test="${bean.model.icd1}">
				                        <td>
					                        <c:choose>
												<c:when test="${bean.editable}">
						                          	<form:select path="references[${i.index}].mainDaggerAsterisk">
														<form:option value="">&nbsp;</form:option>
														<form:option value="+">+</form:option>
														<form:option value="*">*</form:option>
													</form:select>
												</c:when>
												<c:otherwise>${bean.references[i.index].mainDaggerAsterisk}</c:otherwise>
											</c:choose>
				                        </td>
				                        <td>
				                        	<c:choose>
												<c:when test="${bean.editable}">
													<form:hidden path="references[${i.index}].pairedElementId" />
				                        			<form:input path="references[${i.index}].pairedCode" size="10" cssClass="auto" />
												</c:when>
				                        		<c:otherwise>${bean.references[i.index].pairedCode}</c:otherwise>
											</c:choose>
				                        </td>
				                      	<td>
				                      		<c:choose>
												<c:when test="${bean.editable}">
				                      				<form:input path="references[${i.index}].pairedCustomDescription" size="20" maxlength="200"/>
				                      			</c:when>
				                      			<c:otherwise>${bean.references[i.index].pairedCustomDescription}</c:otherwise>
				                      		</c:choose>
				                      	</td>
				                        <td>
				                        	<c:choose>
												<c:when test="${bean.editable}">
						                          	<form:select path="references[${i.index}].pairedDaggerAsterisk">
														<form:option value="">&nbsp;</form:option>
														<form:option value="*">*</form:option>
													</form:select>
												</c:when>
												<c:otherwise>${bean.references[i.index].pairedDaggerAsterisk}</c:otherwise>
											</c:choose>
				                        </td>
			                        </c:if>
			                        <c:if test="${bean.editable}">
				                        <td>
				                        	<a id="references${i.index}.delete" href="#" class="delete"><img src="<c:url value='/img/icons/Delete.png'/>"/></a>
				                        </td>
			                        </c:if>
			                      </tr>
								</c:forEach>
							</tbody>
						</table>
						<c:if test="${bean.editable}">
							<a id="addReference" href="">Add Row</a>
						</c:if>
					</td>
				</tr>
				<tr>
					<td>
					<table style="width: 100%;">
						<tr>
							<td align="right" nowrap="nowrap" valign="top">
								<c:if test="${bean.editable}">
									<a id="formSave" href="#"><img title="Save" src="<c:url value="/img/icons/Save.png"/>"/></a>
									<a id="formReset" href="<c:url value='/indexes/codereferences/editICD12CCI.htm?${automaticContextParams}&id=${bean.elementId}&tab=CODE&language=${param.language}'/>"><img
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
	var g_autoSelected=true;
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
		$('#crLastUpdatedTime').text('${bean.lockTimestamp}');
		$("#formReset").click(function(event) {
			event.preventDefault();
			ContentPaneController.replaceContent(this.href,null,hideLoading,hideLoading,function(){showLoading('Loading...')});
		});
		$("#formSave").click(function(event) {
			showLoading("Saving...");
			var url = $("#formConcept")[0].action;
			 url=turnOnTimestampCheck(url,parent.document.body);
			 $("#formConcept")[0].action=url;
			var replaceContent = function(data) {
				hideLoading();
				ContentPaneController.replaceContentWith(data);
			};
			AjaxUtil.submit("formConcept", replaceContent, null);
			return false;
		});
		$("#addReference").click(function() {
			var tb=$("#referenceTable");
			var trs=tb.find("tr");
			var tr=trs.eq(1);
			var html=tr.clone().wrapAll('<div></div>').parent().html();
			var nextIndex=trs.size()-1;
			html=replaceAll(html, "[0]", "["+nextIndex+"]");
			html=replaceAll(html, "0.", nextIndex+".");
			var newtr=$('<div/>').html(html).contents();
			newtr.attr("style", "");
			newtr.insertAfter(tb.find("tr").eq(nextIndex));
			register();
			return false;
		});
		register();
	});

	function registerDelete(){
		$(".delete").click(function() {
			deleteReference($(this));
			return false;
		});
	}
	
	function deleteReference(ref) {
		if(ref != "undefined" && ref != null) {
			var id=ref.attr("id");
			id=replaceAll(id,"references","");
			id=replaceAll(id,".delete","");
			$("#references"+id+"\\.deleted").val("true");
			ref.parent().parent().hide();
		}
	}

	function registerAutocomplete(){
		$(".auto").change(function(e){
			g_autoSelected=false;
		});
		$(".auto").focusout(function(e){
			//alert(g_autoSelected);
			if(!g_autoSelected){
				var mainCode=$(this);
				var id=mainCode.attr("id");
				var type=id.indexOf("main")==-1?"paired":"main";
				id=replaceAll(id,"references","");
				id=replaceAll(id,"."+type+"Code","");
				$("#references"+id+"\\."+type+"ElementId").val(1);
				$("#references"+id+"\\."+type+"Code").val("");
			}
		});
		$(".auto").autocomplete(
			{
				source: function(request, response) {
					g_autoSelected=false;
			        $.ajax({
			            url: "${pageContext.request.contextPath}/getCodeSearchResult.htm?classification=${param.ccp_bc}&contextId=${param.ccp_cid}&language=${param.language}&leaf=${bean.model.icd}",
			            contentType:"application/json; charset=UTF-8",
			            data: {
			              term : request.term,
			              searchBy : "code"
			            },
			            success: function(data) { response(data); }
			          });
				},
				select : function(event, ui) {
					var mainCode=$(this);
					var id=mainCode.attr("id");
					var type=id.indexOf("main")==-1?"paired":"main";
					id=replaceAll(id,"references","");
					id=replaceAll(id,"."+type+"Code","");
					$("#references"+id+"\\."+type+"Code").val(ui.item.value);
					$("#references"+id+"\\."+type+"ElementId").val(ui.item.conceptId);
					var desc=$("#references"+id+"\\."+type+"CustomDescription");
					//CM-RU175
					var description=ui.item.value;
					if(${bean.model.icd}){
						if(!ui.item.leaf){
							if(description.length==3){
								description+=".-";
							}else{
								description+="-";
							}
						}
					}
					desc.val(description);
					g_autoSelected=true;
				}
			}
		).css({backgroundColor : "yellow"});
	}

	function register(){
		registerAutocomplete();
		registerDelete();
	}
	//the following line is used by browser's dev tools
	//to show the name of the evaluated JS
	//# sourceURL=indexCodeReferencesICD12CCI.js
</script>
</body>
</html>


