<html>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<body>
<div class="contentContainer">
	<%@ include file="tabularHeader.jsp"%>

	<div class="content">
		<form:form id="formConcept" method="POST" modelAttribute="bean" action="tabulars/xml/edit.htm?${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}">
			<input type="hidden" name="tab" value="${bean.model.type}" />
			<table class="nopadding fullwidth">
				<c:if test="${bean.englishVisible}">
					<tr>
						<th class="language-column">English Definition</th>
					</tr>
					<tr>
						<td>
							<form:textarea id="englishXml" path="model.englishXml" cssClass="input xml" readonly="${!bean.englishEditable}"/>
							<br/><form:errors cssClass="err" path="model.englishXml" />
						</td>
					</tr>
				</c:if>
				<c:if test="${bean.frenchVisible}">
					<tr>
						<th class="language-column">French Definition</th>
					</tr>
					<tr>
						<td>
							<form:textarea id="frenchXml" path="model.frenchXml" cssClass="input xml" readonly="${!bean.frenchEditable}" />
							<br/><form:errors cssClass="err" path="model.frenchXml" />
						</td>
					</tr>
				</c:if>
				<c:if test="${bean.saveVisible}">
					<tr>
						<td>
							<table class="fullwidth" >
								<tr>
									<td align="right" nowrap="nowrap" valign="top">
											<a id="formXml" href="#" ><img title="Xml Template" src="<c:url value="/img/icons/Xml.png"/>" /></a>
											<a id="formSave" href="#" ><img title="Save" src="<c:url value="/img/icons/Save.png"/>" /></a>
											<a id="formReset" href="#"><img title="Reset" src="<c:url value="/img/icons/Reset.png"/>"/></a>
									</td>
<%-- 									<td width="100%" valign="top">
										<div id="status">
											<div id="feedback" class="info" style="display: none" ></div>
											<div id="statusResult">
												<c:if test="${!(empty bean.result)}">
													<c:choose>
														<c:when test="${bean.result eq 'SUCCESS'}">
															<div class="success">Changes have been successfully saved.</div>
														</c:when>
														<c:when test="${bean.result eq 'INVALID'}">
															<div class="notice">Validation errors.</div>
														</c:when>
														<c:otherwise>
															<div class="error">Error: <c:out value="${bean.errorMessage}" /></div>
														</c:otherwise>
													</c:choose>
												</c:if>
											</div>
										</div>
									</td> --%>
								</tr>
							</table>
						</td>
					</tr>
				</c:if>
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
			else if(result == 'INCOMPLETE') {
				Feedback.success("<fmt:message key='save.request.incomplete'/>");
			}
			else {
				Feedback.error("<fmt:message key='save.request.error'><fmt:param value='${bean.errorMessage}'/></fmt:message>");
			}
		}
	});
	$(function() {
		$("#formSave").click(function() {
			showLoading("<fmt:message key='progress.msg.save'/>");
			var url = $("#formConcept")[0].action;
			url=turnOnTimestampCheck(url);
			$("#formConcept")[0].action=url;
			var replaceContent = function(data) {
				hideLoading();
				ContentPaneController.replaceContentWith(data);
			};
			AjaxUtil.submit("formConcept", replaceContent, null);
		});
		$("#formReset").click(function() {
			$("#formConcept")[0].reset();
		});
		registerXmlTemplateButton("${pageContext.request.contextPath}", "${bean.model.type}", "formXml", ["englishXml","frenchXml"]);
	});
</script>

</body>
</html>
