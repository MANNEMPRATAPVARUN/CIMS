<html>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<body>
<div class="contentContainer">
	<%@ include file="indexHeader.jsp"%>
	<style>
		.longInput{
			width:398px;
		}
		.slim{
			width:50px;
		}
	</style>
	<c:choose>
		<c:when test="${bean.add}">
			<c:set var="formAction" value="indexes/children/add.htm?${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}"/>
		</c:when>
		<c:otherwise>
			<c:set var="formAction" value="indexes/basicInfo/edit.htm?${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}"/>
		</c:otherwise>
	</c:choose>
	<div class="content">
		<form:form id="formConcept" method="POST" modelAttribute="bean"
			action="${formAction}">
			<c:if test="${bean.add}">
				<input type="hidden" name="type" value="${bean.model.type}" />
			</c:if>
			<table class="nobottom" style="padding: 5px;">
				<tr>
					<td colspan="1" align="left" width="50">
						<table width="100%" class="nobottom">
							<tr>
								<td class="formRequiredLabel slim"><c:if test="${bean.statusEditable}"><span class="required">*</span></c:if> Status:</td>
								<td class="dark-red-bold">
									<form:select path="model.status" disabled="${!bean.statusEditable}">
										<form:option value="ACTIVE">Active</form:option>
										<form:option value="DISABLED">Disabled</form:option>
									</form:select>
									<form:errors cssClass="err" path="model.status" />
									<c:if test="${bean.model.icdSection4}">
										<span style="color:black;margin-left:20px">Site indicator:&nbsp;&nbsp;</span>
										<c:choose>
											<c:when test="${bean.editable}">
												<form:select path="model.siteIndicatorCode" cssClass="cciCombo">
													<form:option value="">&nbsp;</form:option>
													<form:options items="${bean.siteIndicators}" itemValue="code" itemLabel="description"/>
												</form:select>
												<form:errors cssClass="err" path="model.siteIndicatorCode" />
											</c:when>
											<c:otherwise>${bean.model.siteIndicatorCode}</c:otherwise>
										</c:choose>
									</c:if>		
								</td>
							</tr>
							<tr>
								<td class="formRequiredLabel">
									<c:if test="${bean.editable}"><span class="required">*</span></c:if> Description:
								</td>
								<c:choose>
									<c:when test="${bean.editable}">
										<td class="dark-red-bold">
											<form:input path="model.description" cssClass="longInput" disabled="${!bean.editable}"/>
											<br/>
											<form:errors cssClass="err" path="model.description" />
										</td>
									</c:when>
									<c:otherwise><td><c:out value="${bean.model.description}"/></td></c:otherwise>
								</c:choose>
							</tr>
							<c:if test="${bean.model.type ne 'ICD_LETTER_INDEX' and bean.model.type ne 'CCI_LETTER_INDEX'}">
								<tr>
									<td class="formLabel">Note:</td>
									<td class="dark-red-bold">
										<form:textarea id="noteXml" path="model.note" cssClass="input indexNote" cssStyle="width:100%" readonly="${!bean.editable}"/>
										<br/>
										<form:errors cssClass="err" path="model.note" />
									</td>
								</tr>
							</c:if>
						</table>
					</td>
				</tr>
				<tr>
					<td>
					<table width="100%">
						<tr>
							<td align="right" nowrap="nowrap" valign="top">
								<c:if test="${bean.saveVisible}">
									<a id="formXml" href="#" ><img title="Xml Template" src="<c:url value="/img/icons/Xml.png"/>" /></a>
									<a id="formSave" href="#"><img title="Save" src="<c:url value="/img/icons/Save.png"/>"/></a>
								</c:if>
								<c:if test="${bean.edit}">
									<c:if test="${bean.addVisible}">
										<a class="add" href="<c:url value="/indexes/children/add.htm?type=${bean.addType}&${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}"/>"><img title="Add" src="<c:url value="/img/icons/Add.png"/>"/></a>
									</c:if>
									<c:if test="${bean.resetVisible}">
										<a id="formReset" href="#"><img title="Reset" src="<c:url value="/img/icons/Reset.png"/>"/></a>
									</c:if>
									<c:if test="${bean.removeVisible}">
										<a id="lnkRemove" href="#"><img title="Remove" src="<c:url value="/img/icons/Remove.png"/>"/></a>
									</c:if>
								</c:if>
								<c:if test="${bean.add}">
									<a class="add" href="<c:url value="/indexes/basicInfo/edit.htm?${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}"/>"><img title="Cancel" src="<c:url value="/img/icons/Cancel.png"/>" /></a>
								</c:if>
							</td>
<%-- 							<td width="100%" valign="top">
								<div id="status">
								<div id="statusResult"><c:if test="${!(empty bean.result)}">
									<c:choose>
										<c:when test="${bean.result eq 'SUCCESS'}">
											<script>
												setNodeTitle("${bean.model.elementId}", "${bean.model.code}", "${bean.nodeTitle}");
											</script>
											<c:if test="${bean.add}">
												<script>refreshLastNodeChildren("${bean.model.elementId}");</script>
											</c:if>
											<div class="success">Changes have been successfully saved.</div>
											<c:if test="${bean.add}">
												<script>
													var editUrl = "<c:url value="/indexes/basicInfo/edit.htm?${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}"/>";
													ContentPaneController.replaceContent(editUrl,null,hideLoading,hideLoading,function(){showLoading("Loading...")});
												</script>
											</c:if>
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
							</td> --%>
							
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
					
					var editUrl="indexes/basicInfo/edit.htm?${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}";
					var data = {isViewMode: false, conceptId: "${bean.model.elementId}", conceptCode: "${bean.model.code}"};
					NavigationController.navigate(editUrl,data);
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
		$("#lnkRemove").click(function(event) {
			event.preventDefault();
			removeBox({
				title:"<fmt:message key='index.remove.title'/>",
				text:"<fmt:message key='index.remove.message'/>",
				callback: function(){
					var url = "indexes/remove.htm?${automaticContextParams}&id=${bean.model.elementId}&redirectErrorBack=true&language=${param.language}";
					url=turnOnTimestampCheck(url);
					var successCallback = function(data) {
						hideLoading();
						if(data.key != "undefined" && data.key == "error") {
							Feedback.error(data.value);
						}
						else {
							ContentPaneController.replaceContentWith("");
							Feedback.success("<fmt:message key='index.remove.success'/>");
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
		registerXmlTemplateButton("${pageContext.request.contextPath}", "NOTE", "formXml", ["noteXml"]);
	});
	//# sourceURL=indexBasicInfo.jsp
</script>
</body>
</html>
