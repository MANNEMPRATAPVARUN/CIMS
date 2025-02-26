<html>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<body>
<div class="contentContainer">
	<%@ include file="supplementHeader.jsp"%>
	<c:choose>
		<c:when test="${bean.add}">
			<c:set var="formAction" value="supplements/children/add.htm?${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}"/>
		</c:when>
		<c:otherwise>
			<c:set var="formAction" value="supplements/basicInfo/edit.htm?${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}"/>
		</c:otherwise>
	</c:choose>
	<div class="content">
		<form:form id="formConcept" method="POST" modelAttribute="bean"
			action="${formAction}">
			<table class="nobottom" style="width:100%; padding: 5px;">
				<tr>
					<td colspan="3">
						<table width="100%" class="nobottom">
							<tr>
								<td class="formRequiredLabel" style="width:50px"><c:if test="${bean.statusEditable}"><span class="required">*</span></c:if> Status:</td>
								<td class="dark-red-bold">
									<form:select path="model.status" disabled="${!bean.statusEditable}">
										<form:option value="ACTIVE">Active</form:option>
										<form:option value="DISABLED">Disabled</form:option>
									</form:select>
									<form:errors cssClass="err" path="model.status" />
								</td>
							</tr>
							<tr>
								<td class="formRequiredLabel">
									<c:if test="${bean.editable}"><span class="required">*</span></c:if> Description:
								</td>
								<td class="dark-red-bold">
									<form:input path="model.description" cssStyle="width:398px" disabled="${!bean.editable}"/>
									<br/>
									<form:errors cssClass="err" path="model.description" />
								</td>
							</tr>
							<tr>
								<td class="formRequiredLabel">
									<c:if test="${bean.editable}"><span class="required">*</span></c:if> Sort Order:
								</td>
								<td>
									<form:input path="model.sortOrder" disabled="${!bean.editable}"/><form:errors cssClass="err" path="model.sortOrder" />
									
									<c:if test="${bean.model.level eq 1}">
										<span style="margin-left:100px" />
										<c:if test="${bean.editable}"><span class="required">*</span></c:if> Front/Back Matter:
										<form:select path="model.matter" disabled="${!bean.editable}" >
											<form:option value="FRONT">Front</form:option>
											<form:option value="BACK">Back</form:option>
										</form:select> <form:errors cssClass="err" path="model.matter" />
									</c:if>									
								</td>
							</tr>
							<tr>
								<td class="formLabel">Markup:</td>
								<td class="dark-red-bold">
									<form:textarea id="markupXml" path="model.markup" cssClass="input indexNote" cssStyle="width:100%" readonly="${!bean.editable}"/>
									<br/>
									<form:errors cssClass="err" path="model.markup" />
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
					<table width="100%">
						<tr>
							<td align="right" nowrap="nowrap" valign="top">
								<c:if test="${bean.saveVisible}">
									<a id="formSave" href="#"><img title="Save" src="<c:url value="/img/icons/Save.png"/>"/></a>
								</c:if>
								<c:if test="${bean.edit}">
									<c:if test="${bean.addVisible}">
										<a class="add" href="<c:url value="/supplements/children/add.htm?${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}"/>"><img title="Add" src="<c:url value="/img/icons/Add.png"/>"/></a>
									</c:if>
									<c:if test="${bean.resetVisible}">
										<a id="formReset" href="#"><img title="Reset" src="<c:url value="/img/icons/Reset.png"/>"/></a>
									</c:if>
									<c:if test="${bean.removeVisible}">
										<a id="lnkRemove" href="#"><img title="Remove" src="<c:url value="/img/icons/Remove.png"/>"/></a>
									</c:if>
								</c:if>
								<c:if test="${bean.add}">
									<a class="add" href="<c:url value="/supplements/basicInfo/edit.htm?${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}"/>"><img title="Cancel" src="<c:url value="/img/icons/Cancel.png"/>" /></a>
								</c:if>
								<c:if test="${bean.editable}">
									<a id="lnkDiagrams" href="#" ><img title="Manage Diagrams" src="<c:url value="/img/icons/Picture.png"/>"/></a>
								</c:if>
							</td>
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
				var added = "${bean.add}" == "true";
				setNodeTitle("${bean.model.elementId}", "${bean.model.code}", '${bean.nodeTitle}');
				Feedback.success("<fmt:message key='save.request.success'/>");
				if(added) {
					refreshLastNodeChildren("${bean.model.elementId}");
					var editUrl="supplements/basicInfo/edit.htm?${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}";
					var data = {isViewMode: false, conceptId: "${bean.model.elementId}", conceptCode: "${bean.model.code}"};
					NavigationController.navigate(editUrl,data);
				}else if("${bean.sortingChanged}"=="true"){
					refreshLastNodeParent("${bean.model.elementId}");
				}
			}
			else if(result == 'INVALID') {
				Feedback.warn("<fmt:message key='save.request.error.validation'/>");
			}
			else if(result == 'INCOMPLETE') {
				Feedback.success("<fmt:message key='save.request.success'/>");
				Feedback.incomplete("<fmt:message key='save.request.incomplete'/>");
			}
			else {
				Feedback.error("<fmt:message key='save.request.error'><fmt:param value='${bean.errorMessage}'/></fmt:message>");
			}
		}
	});
	$(function() {
		$("#lnkDiagrams").click(function(event) {
			event.preventDefault();
			manageDiagrams('${pageContext.request.contextPath}', '${param.ccp_bc}', ${bean.year});
		});
		$("#lnkRemove").click(function(event) {
			event.preventDefault();
			removeBox({
				title:"<fmt:message key='supplement.remove.title'/>",
				text:"<fmt:message key='supplement.remove.message'/>",
				callback: function(){
					var url = "supplements/remove.htm?${automaticContextParams}&id=${bean.model.elementId}&redirectErrorBack=true";
					url=turnOnTimestampCheck(url);
					var successCallback = function(data) {
						hideLoading();
						if(data.key != "undefined" && data.key == "error") {
							Feedback.error(data.value);
						}
						else {
							ContentPaneController.replaceContentWith("");
							Feedback.success("<fmt:message key='supplement.remove.success'/>");
							removeNode("${bean.model.elementId}");
						}
					};
					AjaxUtil.ajax(url,null,successCallback,null,function(){showLoading("<fmt:message key='progress.msg.remove'/>");});
				}
			});
		});
		$("#formSave").click(function(event) {
			showLoading("<fmt:message key='progress.msg.save'/>");
			var url = $("#formConcept")[0].action;
			url=turnOnTimestampCheck(url);
			$("#formConcept")[0].action=url;
			var replaceContent = function(data) {
				hideLoading();
				ContentPaneController.replaceContentWith(data);
			};
			AjaxUtil.submit("formConcept", replaceContent, null);
			return false;
		});
		$(".add").click(function(event) {
			event.preventDefault();
			ContentPaneController.replaceContent(this.href,null,hideLoading,hideLoading,function(){showLoading("<fmt:message key='progress.msg.load'/>");});
		});
		$("#formReset").click(function() {
			$("#formConcept")[0].reset();
			return false;
		});
		registerXmlTemplateButton("${pageContext.request.contextPath}", "NOTE", "formXml", ["markupXml"]);
	});
	//# sourceURL=supplementBasicInfo.jsp
</script>
</body>
</html>
