<html>
<%-- <%@ include file="/WEB-INF/jsp/common/common-header.jsp"%> --%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<body>
<div class="contentContainer">
	<%@ include file="tabularHeader.jsp"%>

	<div class="content">
		<form:form id="formConcept" method="POST" modelAttribute="bean" action="tabulars/validation/edit.htm?${automaticContextParams}&tab=VALIDATION&id=${bean.elementId}&dh=${bean.dataHoldingId}&add=true&language=${param.language}">
			<table border="1" cellpadding="0" cellspacing="0" width="400">
				<tr>
					<td width="100" style="padding-right: 15px;">
						<table border="0" cellpadding="0" cellspacing="0" style="width:450px">
							<tr>
								<td class="formRequiredLabel" style="width:122px"><span class="required">*</span> Data Holding:</td>
								<td>
									<form:select id="dataHoldingId" path="dataHoldingId" items="${bean.dataHoldings}" itemValue="elementId" itemLabel="title" cssClass="cciCombo"/>
									<form:errors cssClass="err" path="dataHoldingId" />
								</td>
							</tr>
							<c:choose>
								<c:when test="${empty bean.model}">
									<tr>
										<td colspan="2" class="dark-red-bold">No validations are configured for this data holding.</td>
									</tr>
								</c:when>
								<c:otherwise>
									<tr>
										<td class="formRequiredLabel"><span class="required">*</span> Gender:</td>
										<td>
											<form:select path="model.genderCode" cssClass="cciCombo" disabled="${!bean.editable}">
												<form:option value="">&nbsp;</form:option>
												<form:options items="${bean.genders}" itemValue="code" itemLabel="title"/>
											</form:select>
											<br />
											<form:errors cssClass="err" path="model.genderCode" />
										</td>
									</tr>
									<tr>
										<td class="formRequiredLabel"><span class="required">*</span> Age Range:</td>
										<td nowrap="nowrap">
											<form:input path="model.ageMinimum" maxlength="3" size="3" disabled="${!bean.editable}"/> - 
											<form:input path="model.ageMaximum" maxlength="3" size="3" disabled="${!bean.editable}"/>
											<br />
											<form:errors cssClass="err" path="model.ageMinimum" />
											<form:errors cssClass="err" path="model.ageMaximum" />
										</td>
									</tr>
									<c:choose>
										<c:when test="${bean.classification=='ICD'}">
											<tr>
												<td class="formRequiredLabel"><span class="required">*</span> Dx Type:</td>
												<td>
													<form:input id="dxTypeId" path="model.dxTypeId"  style="display:none"/>
														<div id="dxTypeTableSelected">
															<c:if test="${not empty bean.icdDxType}">
																<table class="listTable" style="width:100%">
																	<tr>
																		<th class="tableHeader">MRDx</th>
																		<th class="tableHeader">1</th><th class="tableHeader">2</th><th class="tableHeader">3</th>
																		<th class="tableHeader">4</th><th class="tableHeader">6</th><th class="tableHeader">9</th>
																		<th class="tableHeader">W</th><th class="tableHeader">X</th><th class="tableHeader">Y</th>
																	</tr>
																	<tr>
																		<td>${bean.icdDxType.main}</td>
																		<td>${bean.icdDxType.t1}</td>
																		<td>${bean.icdDxType.t2}</td>
																		<td>${bean.icdDxType.t3}</td>
																		<td>${bean.icdDxType.t4}</td>
																		<td>${bean.icdDxType.t6}</td>
																		<td>${bean.icdDxType.t9}</td>
																		<td>${bean.icdDxType.w}</td>
																		<td>${bean.icdDxType.x}</td>
																		<td>${bean.icdDxType.y}</td>
																	</tr>
																</table>
															</c:if>
														</div>
													<c:if test="${bean.editable}">
														<a href="#" id="dxTypeChange">Add/Modify Dx Type</a>
														<form:errors cssClass="err" path="model.dxTypeId" />
														<div id="dxTypesDialog" style="display:none">
															<table id="dxTypeTable" class="listTable" style="width:100%">
																<tr>
																	<th class="tableHeader">&nbsp;</th>
																	<th class="tableHeader">MRDx</th>
																	<th class="tableHeader">1</th><th class="tableHeader">2</th><th class="tableHeader">3</th>
																	<th class="tableHeader">4</th><th class="tableHeader">6</th><th class="tableHeader">9</th>
																	<th class="tableHeader">W</th><th class="tableHeader">X</th><th class="tableHeader">Y</th>
																</tr>
																<c:forEach var="dxType" items="${bean.icdDxTypes}" varStatus="status">
																	<tr class="${(status.index)%2 eq 0?'odd':'even'}">
																		<td><input id="dxType${dxType.id}" type="radio" name="dxType" value="${dxType.id}" size="2"/></td>
																			<td>${dxType.main}</td>
																			<td>${dxType.t1}</td>
																			<td>${dxType.t2}</td>
																			<td>${dxType.t3}</td>
																			<td>${dxType.t4}</td>
																			<td>${dxType.t6}</td>
																			<td>${dxType.t9}</td>
																			<td>${dxType.w}</td>
																			<td>${dxType.x}</td>
																			<td>${dxType.y}</td>
																	</tr>
																</c:forEach>
															</table>
															<div style="float:right">
																<input id="btnDcTypeOk" type="button" value="OK"/>
																<input id="btnDcTypeCamcel" type="button" value="Cancel"/>
															</div>
														</div>
													</c:if>
												</td>
											</tr>
											<tr>
												<td class="formRequiredLabel"><span class="required">*</span> New Born:</td>
												<td>
													<form:radiobutton path="model.newBorn" value="${true}" disabled="${!bean.editable}"/> Y
													<form:radiobutton path="model.newBorn" value="${false}" disabled="${!bean.editable}"/> N
													<form:errors cssClass="err" path="model.newBorn" />
												</td>
											</tr>
										</c:when>
										<c:otherwise>
											<tr>
												<td>Status Reference:</td>
												<td>
													<form:select path="model.statusReferenceCode" cssClass="cciCombo" disabled="${!bean.editable}">
														<form:option value="">&nbsp;</form:option>
														<form:options items="${bean.cciStatusReferences}" itemValue="code" itemLabel="codeDescription" />
													</form:select>
													<br/><form:errors cssClass="err" path="model.statusReferenceCode" />
												</td>
											</tr>
											<tr>
												<td>Location Reference:</td>
												<td>
													<form:select path="model.locationReferenceCode" cssClass="cciCombo" disabled="${!bean.editable}">
														<form:option value="">&nbsp;</form:option>
														<form:options items="${bean.cciLocationReferences}" itemValue="code" itemLabel="codeDescription"/>
													</form:select>
													<br/><form:errors cssClass="err" path="model.locationReferenceCode" />
												</td>
											</tr>
											<tr>
												<td>Mode Of Delivery Reference:</td>
												<td>
													<form:select path="model.deliveryReferenceCode" cssClass="cciCombo" disabled="${!bean.editable}">
														<form:option value="">&nbsp;</form:option>
														<form:options items="${bean.cciModeOfDeliveryReferences}" itemValue="code" itemLabel="codeDescription"/>
													</form:select>
													<br/><form:errors cssClass="err" path="model.deliveryReferenceCode" />
												</td>
											</tr>
											<tr>
												<td>Extent Reference:</td>
												<td>
													<form:select path="model.extentReferenceCode" cssClass="cciCombo" disabled="${!bean.editable}">
														<form:option value="">&nbsp;</form:option>
														<form:options items="${bean.cciExtentReferences}" itemValue="code" itemLabel="codeDescription"/>
													</form:select>
													<br/><form:errors cssClass="err" path="model.extentReferenceCode" />
												</td>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
						</table>
					</td>
					<c:if test="${bean.editable and not empty bean.model}">
						<td valign="top" style="border-left: 1px solid whitesmoke; vertical-align:top;">
							<table>
								
									<tr>
										<td>
											<table>
												<tr>
													<td colspan="2" class="saveConfig">Save Configuration <span class="dark-red-bold" style="font-size:10px">*Settings to be configured for each save</span></td>
												</tr>
												<tr>
													<td>
														<form:checkbox id="extendValidation" path="extendValidationToOtherDataHoldings" /> extend validation set to other holding(s):<br/>
														<form:select id="otherDataHoldings" multiple="true" path="selectedOtherDataHoldings" cssClass="cciCombo" disabled="${extendValidationToOtherDataHoldings}">
															<form:options items="${bean.otherDataholdings}" itemValue="elementId" itemLabel="title"/>
														</form:select>
													</td>
												</tr>
											</table>
										</td>
									</tr>
								
							</table>
						</td>
					</c:if>
				</tr>
				<tr>
					<td colspan="2">
						<table width="100%" cellpadding="0" cellspacing="0" border="1" >
							<tr>
								<td align="left" nowrap="nowrap" valign="top">
									<c:if test="${bean.editable}">
										<a id="formSave" href="#" ><img title="Save" src="<c:url value="/img/icons/Save.png"/>" /></a>
										<c:if test="${false and (!bean.model.disabled and bean.classification eq 'CCI')}">
											<a href="#" id="lnkQualifier"><img title="Request New Reference Value" height="32" src="<c:url value="/img/icons/EmailArrow.png"/>"/></a>
										</c:if>
										<a id="formReset" href="#"><img title="Reset" src="<c:url value="/img/icons/Reset.png"/>"/></a>
										<c:if test="${!bean.model.disabled}">
											<a id="formRemove" href="#"><img title="Remove" src="<c:url value="/img/icons/Remove.png"/>"/></a>
										</c:if>
									</c:if>
									<c:if test="${bean.addable}">
										<a id="formAdd" class="add" href="#"><img title="Add" src="<c:url value="/img/icons/Add.png"/>"/></a>
									</c:if>
									<a id="lnkReference"
									   href="<c:url value="/tabulars/report/validationSets.htm?${automaticContextParams}&id=${bean.elementId}"/>"
									><img title="Validation Report" src="<c:url value="/img/icons/Book.png"/>"/></a>
								</td>
<%-- 								<td width="100%" valign="top">
									<div id="status">
										<div id="feedback" class="info" style="display: none" ></div>
										<div id="statusResult">
											<c:if test="${!(empty bean.result)}">
												<c:choose>
													<c:when test="${bean.result eq 'SUCCESS'}">
														<div class="success">
															<c:choose>
																<c:when test="${empty bean.successMessage}">
																	Changes have been successfully saved.
																</c:when>
																<c:otherwise>
																	<c:out value="${bean.successMessage}" />
																</c:otherwise>
															</c:choose>
														</div>
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
			</table>
		</form:form>
	</div>
</div>
<div id="dialogQualifierDiv"></div>
<script>
	$('#crLastUpdatedTime').text('${bean.lockTimestamp}');
	$(document).ready(function(){
		var result = "${bean.result}";
		
		if(result != null && result != "") {
			if(result == 'SUCCESS') {
				var message = "${bean.successMessage}";
				if(message != null && message != "") {
					Feedback.success(message);
				}
				else {
					Feedback.success("<fmt:message key='save.request.success'/>");
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
		$("#formReset").click(function(event) {
			event.preventDefault();
			$("#formConcept")[0].reset();
			var selected=$("#dxTypeId").val();
			selectDxType(selected);
		});
		$("#formSave").click(function(event) {
			event.preventDefault();
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
		$("#formAdd").click(function(event) {
			event.preventDefault();
			var url = "<c:url value='/tabulars/validation/edit.htm?${automaticContextParams}&tab=VALIDATION&id=${bean.elementId}&dh=${bean.dataHoldingId}&add=true'/>" + $(this).val();
			
			var afterCallback = function(data) {
				hideLoading();
			};
			var beforeCallback = function() {
				showLoading("<fmt:message key='progress.msg.add'/>");
			};
			ContentPaneController.replaceContent(url,null,afterCallback,afterCallback,beforeCallback);
		});
		$("#formRemove").click(function(event) {
			event.preventDefault();
			removeBox({
				title:"<fmt:message key='validation.set.remove.title'/>",
				text:"<fmt:message key='validation.set.remove.message'/>",
				callback: function(){
					var url = "<c:url value='/tabulars/validation/remove.htm?${automaticContextParams}&tab=VALIDATION&id=${bean.elementId}&dh=${bean.dataHoldingId}'/>";
					url=turnOnTimestampCheck(url);
					var afterCallback = function(data) {
						hideLoading();
					};
					var beforeCallback = function() {
						showLoading("<fmt:message key='progress.msg.remove'/>");
					};
					ContentPaneController.replaceContent(url,null,afterCallback,afterCallback,beforeCallback);
				}
			});
		});
		$("#dataHoldingId").change(function() {
			var url = "<c:url value='/tabulars/validation/edit.htm?${automaticContextParams}&tab=VALIDATION&id=${bean.elementId}&dh='/>" + $(this).val();
			var afterCallback = function(data) {
				hideLoading();
			};
			var beforeCallback = function() {
				showLoading("<fmt:message key='progress.msg.load'/>");
			};
			ContentPaneController.replaceContent(url,null,afterCallback,afterCallback,beforeCallback);
		});
		$("#dxTypeChange").click(function() {
			 var selected=$("#dxTypeId").val();
			 $("#dxType"+selected).attr("checked", true); 
			 var div=$("#dxTypesDialog");
			 div.dialog({
				   autoOpen: false,
				   width: 750,
				   height: 350,
				   title: "<fmt:message key='validation.set.dx.change.title'/>",
				   resizable: true,
				   modal: true
				});
			 div.dialog("open");
		});
		$("#btnDcTypeCamcel").click(function() {
			$("#dxTypesDialog").dialog("close");
		});
		$("#btnDcTypeOk").click(function() {
			var selected=$("input[name=dxType]:radio:checked").val();
			selectDxType(selected);
			$("#dxTypesDialog").dialog("close");
		});
		$("#extendValidation").change(function() {
			 $("#otherDataHoldings").attr('disabled', !$(this).is(':checked'));
		});
		$("#lnkQualifier").click(function(){
			   var div=$("#dialogQualifierDiv");
			   div.empty();
			   div.dialog({
				   autoOpen: false,
				   height: 350,
				   width: 525,
				   title: "<fmt:message key='new.qualifier.title'/>",
				   resizable: false,
				   modal: true
				});
			   div.load("<c:url value="/tabulars/qualifier/add.htm?${automaticContextParams}"/>");
			   div.dialog("open");
			   return false;
			});
		$("#lnkReference").click(function(){
			 	popupwindow($(this).attr("href"), null, 870, 550);
			   return false;
			});
	});
	function selectDxType(selected){
		$("#dxTypeId").val(selected);
		$("#dxTypeTableSelected").empty();
		if(selected!=null&&selected!=""){
			var cloned= $("#dxTypeTable").clone(true).attr("id","");
			var rows=cloned.find("tr");
			rows.each(function(index,element){
			    if(index!=selected && index!=0){
			    	$(element).remove();
			    }else{
				    var row=$(element);
			   		row.removeClass("even");
			   		row.find("th:first").remove();
			   		row.find("td:first").remove();
			    }
			});
			cloned.appendTo("#dxTypeTableSelected");
		}
	}
</script>

</body>
</html>
