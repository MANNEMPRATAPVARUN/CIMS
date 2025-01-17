<html>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<body>
<div class="contentContainer">
<%@ include file="tabularHeader.jsp"%>
<div class="content">

<c:choose>
<c:when test="${bean.add}">
<c:set var="formAction" value="tabulars/children/add.htm?${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}"/>
</c:when>
<c:otherwise>
<c:set var="formAction" value="tabulars/basicInfo/edit.htm?${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}"/>
</c:otherwise>
</c:choose>
<form:form
	id="formConcept"
	method="POST"
	modelAttribute="bean"
	action="${formAction}"
	enctype="multipart/form-data">
	<c:if test="${bean.add}">
		<input type="hidden" name="type" value="${bean.model.type}" />
		<input type="hidden" name="root" value="${bean.parentRoot}" />
	</c:if>
	<table class="nobottom" style="width: 100%;">
		<tr>
			<td colspan="3">
			<table class="nobottom" style="width: 100%; padding-bottom: 5px;">
				<tr class="slim">
					<c:if test="${bean.codeVisible}">
						<td class="formRequiredLabel leftcolumn"><span class="required">*</span> Code Value:</td>
						<td class="dark-red-bold" style="width:15%;">
							<c:choose>
								<c:when test="${bean.codeEditable}">
									<form:input path="model.code" maxlength="9" size="9"/>
									<br/>
									<form:errors cssClass="err" path="model.code" />
									<form:errors cssClass="err" path="code" />
								</c:when>
								<c:otherwise>${bean.model.code}</c:otherwise>
							</c:choose>
						</td>
					</c:if>
					<td class="formRequiredLabel">Hierarchical Level:</td>
					<td class="dark-red-bold">${bean.model.typeLabel}</td>
					<c:if test="${bean.statusVisible}">
						<td class="formRequiredLabel"><span class="required">*</span> Status:</td>
						<td class="dark-red-bold">
							<form:select path="model.status" disabled="${!bean.statusEditable}" style="width: 100px;">
								<form:option value="ACTIVE">Active</form:option>
								<form:option value="DISABLED">Disabled</form:option>
							</form:select>
							<br />
							<form:errors cssClass="err" path="model.status" />
						</td>
					</c:if>
				</tr>
				<c:if test="${bean.add}">
					<c:if test="${bean.cciGroupsVisible}">
						<tr>
							<td class="formRequiredLabel"><span class="required">*</span> CCI Group:</td>
							<td>
								<c:choose>
									<c:when test="${empty bean.cciGroups}">Qualifiers are not available for this Section</c:when>
									<c:otherwise>
										<form:select path="cciGroup" cssClass="ccicombo">
											<form:option value="">&nbsp;</form:option>
											<form:options items="${bean.cciGroups}"/>
										</form:select>
										<br />
										<form:errors cssClass="err" path="cciGroup" />
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:if>
					<c:if test="${bean.cciInterventionsVisible}">
						<tr>
							<td class="formLabel">CCI Group:</td>
							<td class="dark-red-bold"><c:out value="${bean.cciGroupName}" /></td>
						</tr>
						<tr>
							<td class="formRequiredLabel"><span class="required">*</span> CCI Intervention:</td>
							<td>
								<c:choose>
									<c:when test="${empty bean.cciInterventions}">Qualifiers are not available for this Section</c:when>
									<c:otherwise>
										<form:select path="cciIntervention" cssClass="ccicombo">
											<form:option value="">&nbsp;</form:option>
											<form:options items="${bean.cciInterventions}"/>
										</form:select>
										<br />
										<form:errors cssClass="err" path="cciIntervention" />
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:if>
					<c:if test="${bean.cciTechniquesVisible}">
						<tr>
							<td class="formLabel">CCI Approach/Technique:</td>
							<td>
								<c:choose>
									<c:when test="${empty bean.cciTechniques}">Qualifiers are not available for this Section</c:when>
									<c:otherwise>
										<form:select path="cciTechnique" cssClass="ccicombo">
											<form:option value="">&nbsp;</form:option>
											<form:options items="${bean.cciTechniques}"/>
										</form:select>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:if>
					<c:if test="${bean.cciDevicesVisible}">
						<tr>
							<td class="formLabel">CCI Device/Agent:</td>
							<td>
								<c:choose>
									<c:when test="${empty bean.cciDevices}">Qualifiers are not available for this Section</c:when>
									<c:otherwise>
										<form:select path="cciDevice" cssClass="ccicombo">
											<form:option value="">&nbsp;</form:option>
											<form:options items="${bean.cciDevices}"/>
										</form:select>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:if>
					<c:if test="${bean.cciTissuesVisible}">
						<tr>
							<td class="formLabel">CCI Tissue:</td>
							<td>
								<c:choose>
									<c:when test="${empty bean.cciTissues}">Qualifiers are not available for this Section</c:when>
									<c:otherwise>
										<form:select path="cciTissue" cssClass="ccicombo">
											<form:option value="">&nbsp;</form:option>
											<form:options items="${bean.cciTissues}"/>
										</form:select>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:if>
				</c:if>
				<c:if test="${bean.edit}">
					<tr class="slim">
						<c:if test="${bean.invasivenessLevelVisible}">
							<td nowrap="nowrap" class="formLabel leftcolumn">Invasiveness Level:</td>
							<td>
								<form:select path="model.invasivenessLevel" items="${bean.cciInvasivenessLevels}" 
									disabled="${!bean.invasivenessLevelEditable}"/>
								<br/><form:errors cssClass="err" path="model.invasivenessLevel" />
							</td>
						</c:if>
						<c:if test="${bean.daggerAsteriskVisible}">
							<td class="formLabel leftcolumn">Dagger/Asterisk:</td>
							<td>
								<form:select path="model.daggerAsteriskId" disabled="${!bean.daggerAsteriskEditable}">
									<form:option value="${null}">&nbsp;</form:option>
									<c:forEach items="${bean.daggerAsteriskTypes}" var="daggerAsterisk">
										<form:option value="${daggerAsterisk.elementId}">${daggerAsterisk.code}</form:option>
									</c:forEach>
								</form:select> <br />
								<form:errors cssClass="err" path="model.daggerAsteriskId" />
							</td>
						</c:if>
						<c:if test="${bean.canadianEnhancementVisible}">
							<td class="formLabel">Canadian Enhancement:</td>
							<td>
								<form:select path="model.canadianEnhancement" disabled="${!bean.canadianEnhancementEditable}">
									<form:option value="${true}">Yes</form:option>
									<form:option value="${false}">No</form:option>
								</form:select> <br />
								<form:errors cssClass="err" path="model.canadianEnhancement" />
							</td>
						</c:if>
						<c:if test="${bean.childTableVisible}">
							<td class="formLabel">Child Table:</td>
							<td class="dark-red-bold">
								<c:choose>
									<c:when test="${bean.childTable}">Yes</c:when>
									<c:otherwise>No</c:otherwise>
								</c:choose>
							</td>
						</c:if>
						<td colspan="4">&nbsp;</td>
					</tr>
				</c:if>
			</table>
			</td>
		</tr>
		<tr>
			<td colspan="3">
			<c:if test="${bean.languagesVisible}">
				<table class="nobottom language-table">
					<tr>
						<th>&nbsp;</th>
						<c:if test="${bean.englishVisible}">
							<th class="language-column value">English</th>
						</c:if>
						<th>&nbsp;</th>
						<c:if test="${bean.frenchVisible}">
							<th class="language-column value">French</th>
						</c:if>
					</tr>
					<tr>
						<td
							class="formRequiredLabel leftcolumn"
							nowrap="nowrap"
						><span class="required">*</span> Short Title:</td>
						<c:if test="${bean.englishVisible}">
							<td class="value nopadding"><form:input
								id="shortTitleEng"
								path="model.shortTitleEng"
								size="61"
								maxlength="60"
								cssClass="input"
								disabled="${!bean.englishEditable}"
							/> <br />
							character count: <span id="shortTitleEngCounter">0</span> (maximum is 60) <br />
							<form:errors cssClass="err" path="model.shortTitleEng" /></td>
						</c:if>
						<c:if test="${bean.frenchVisible}">
							<td>&nbsp;</td>
							<td class="value nopadding"><form:input
								id="shortTitleFra"
								path="model.shortTitleFra"
								size="61"
								maxlength="60"
								cssClass="input"
								disabled="${!bean.frenchEditable}"
							/> <br />
							character count: <span id="shortTitleFraCounter">0</span> (maximum is 60) <br />
							<form:errors cssClass="err" path="model.shortTitleFra" /></td>
						</c:if>
					</tr>
					<tr>
						<td
							class="formRequiredLabel leftcolumn"
							nowrap="nowrap"
						><span class="required">*</span> Long Title:</td>
						<c:if test="${bean.englishVisible}">
							<td class="value nopadding"><form:textarea
								path="model.longTitleEng"
								cssClass="input"
								disabled="${!bean.englishEditable}"
							/><br/><form:errors cssClass="err" path="model.longTitleEng" />
							</td>
						</c:if>
						<c:if test="${bean.frenchVisible}">
							<td>&nbsp;</td>
							<td  class="value nopadding"><form:textarea
								path="model.longTitleFra"
								cssClass="input"
								disabled="${!bean.frenchEditable}"
							/><br/><form:errors cssClass="err" path="model.longTitleFra" />
							</td>
						</c:if>
					</tr>
					<tr>
						<td
							class="formRequiredLabel leftcolumn"
							nowrap="nowrap"
						><span class="required">*</span> User Title:</td>
						<c:if test="${bean.englishVisible}">
							<td class="value nopadding"><form:textarea
								path="userTitleEng"
								cssClass="input"
								disabled="${!(bean.englishEditable and bean.userTitleEditable)}"
							/><br/><form:errors cssClass="err" path="model.userTitleEng" />
							</td>
						</c:if>
						<c:if test="${bean.frenchVisible}">
							<td>&nbsp;</td>
							<td class="value nopadding"><form:textarea
								path="userTitleFra"
								cssClass="input"
								disabled="${!(bean.frenchEditable and bean.userTitleEditable)}"
							/><br/><form:errors cssClass="err" path="model.userTitleFra" />
							</td>
						</c:if>
					</tr>
<%-- 					
					<tr>
						<td class="formLabel leftcolumn">Diagram:</td>
						<c:if test="${bean.englishVisible}">
							<td class="value">
								<c:if test="${bean.englishEditable and bean.userTitleEditable}">
									<input type="file" name="model.diagramEng.file" accept="image/gif" /> <br />
									<form:errors cssClass="err" path="model.diagramEng.file" />
								</c:if>
								<c:if test="${!empty(bean.model.diagramEng.name)}">
									<div id="englishDiagramInfo">
										<a class="dialog" href="<c:url value="/tabulars/diagram/show.htm?${automaticContextParams}&id=${bean.model.elementId}&lang=ENGLISH"/>"
										>${bean.model.diagramEng.name}</a>&nbsp;
										<c:if test="${bean.englishEditable and bean.userTitleEditable}">
											<a id="englishDiagramDelete" href="#"/><img src="<c:url value="/img/icons/Delete.png"/>" /></a>
											<form:hidden id="englishDiagramDeleteFlag" path="model.diagramEng.remove"/>
										</c:if>
									</div>
								</c:if>
							</td>
						</c:if>
						<c:if test="${bean.frenchVisible}">
							<td>&nbsp;</td>
							<td>
								<c:if test="${bean.frenchEditable and bean.userTitleEditable}">
									<input type="file" name="model.diagramFra.file" accept="image/gif"/> <br />
									<form:errors cssClass="err" path="model.diagramFra.file" />
								</c:if>
								<c:if test="${!empty(bean.model.diagramFra.name)}">
									<div id="frenchDiagramInfo">
										<a class="dialog" href="<c:url value="/tabulars/diagram/show.htm?${automaticContextParams}&id=${bean.model.elementId}&lang=FRENCH"/>"
										>${bean.model.diagramFra.name}</a>&nbsp;
										<c:if test="${bean.frenchEditable and bean.userTitleEditable}">
											<a id="frenchDiagramDelete" href="#" /><img src="<c:url value="/img/icons/Delete.png"/>" /></a> 
											<form:hidden id="frenchDiagramDeleteFlag" path="model.diagramFra.remove"/>
										</c:if>
									</div>
								</c:if>
							</td>
						</c:if>
					</tr>
--%>				
				</table>
			</c:if>
			</td>
		</tr>
		<tr>
			<td>
			<table style="width: 100%;">
				<tr>
					<td align="right" nowrap="nowrap" valign="top">
					<c:if test="${bean.saveVisible}">
						<a
							id="formSave"
							href="#"
						><img
							title="Save"
							src="<c:url value="/img/icons/Save.png"/>"
						/></a>
					</c:if>
					<c:if test="${bean.add}">
						<c:if test="${false and bean.addQualifierVisible}">
							<a
								id="lnkQualifier"
								href="#"
							><img
								height="32"
								title="Request New Component/Qualifier"
								src="<c:url value="/img/icons/Email.png"/>"
							/></a>
						</c:if>
						<a 
							class="add"
							href="<c:url value="/tabulars/basicInfo/edit.htm?${automaticContextParams}&id=${bean.parentElementId}&language=${param.language}"/>"
						><img
							title="Cancel"
							src="<c:url value="/img/icons/Cancel.png"/>"
						/></a>
					</c:if>
					<c:if test="${bean.edit}">
						<c:if test="${bean.resetVisible}">
							<a
								id="formReset"
								href="#"
							><img
								title="Reset"
								src="<c:url value="/img/icons/Reset.png"/>"
							/></a>
						</c:if>
					</c:if>
					<c:if test="${bean.referenceLinksVisible}">
						<a
							id="lnkReference"
							href="<c:url value="/tabulars/report/referenceLinks.htm?${automaticContextParams}&code=${bean.model.code}&language=${param.language}"/>"
						><img
							title="Reference Links"
							src="<c:url value="/img/icons/Book.png"/>"
						/></a>
					</c:if>
					<c:if test="${bean.edit}">
						<c:if test="${bean.addCodeVisible}">
							<a
								class="add"
								href="<c:url value="/tabulars/children/add.htm?type=${bean.addType}&${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}"/>"
							><img
								title="Add"
								src="<c:url value="/img/icons/Add.png"/>"
							/></a>
						</c:if>
						<c:if test="${bean.addBlockVisible}">
							<a
								class="add"
								href="<c:url value="/tabulars/children/add.htm?type=${bean.addBlockType}&${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}"/>"
							> <img
								title="Add Block"
								src="<c:url value="/img/icons/AddBlock.png"/>"
							/></a>
						</c:if>
						<c:if test="${bean.addCategoryVisible}">
							<a
								class="add"
								href="<c:url value="/tabulars/children/add.htm?type=${bean.addCategoryType}&${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}"/>"
							> <img
								title="Add Category"
								src="<c:url value="/img/icons/AddCategory.png"/>"
							/></a>
						</c:if>
						<c:if test="${bean.addGroupVisible}">
							<a
								class="add"
								href="<c:url value="/tabulars/children/add.htm?type=${bean.addGroupType}&${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}"/>"
							><img
								title="Add Group"
								src="<c:url value="/img/icons/AddGroup.png"/>"
							/></a>
						</c:if>
						<c:if test="${bean.removeVisible}">
							<a
								id="lnkRemove"
								href="#"
							><img
								title="Remove"
								src="<c:url value="/img/icons/Remove.png"/>"
							/></a>
						</c:if>
					</c:if>
					</td>
<%-- 					<td width="100%" valign="top">
						<div id="status">
						<div id="statusResult"><c:if test="${!(empty bean.result)}">
							<c:choose>
								<c:when test="${bean.result eq 'SUCCESS'}">
									<script>
										setNodeTitle("${bean.model.elementId}", "${bean.model.code}", '${bean.nodeTitle}');
									</script>
									<c:if test="${bean.add}">
										<script>refreshLastNodeChildren("${bean.model.elementId}");</script>
									</c:if>
									<div class="success">Changes have been successfully saved.</div>
 									<c:if test="${bean.add}">
										<script>
											var editUrl="<c:url value="/tabulars/basicInfo/edit.htm?${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}"/>";
											ContentPaneController.replaceContent(editUrl,null,hideLoading,hideLoading,showLoading);
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
<div id="dialogDiv"><img id="dialogImg" /></div>
<div id="dialogQualifierDiv"></div>
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
					
					var editUrl="tabulars/basicInfo/edit.htm?${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}";
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
				title:"<fmt:message key='code.value.remove.title'/>",
				text:"<fmt:message key='code.value.remove.message'/>",
				callback: function(){
					var url = "tabulars/remove.htm?${automaticContextParams}&id=${bean.model.elementId}&redirectErrorBack=true&language=${param.language}";
					url=turnOnTimestampCheck(url);
					var successCallback = function(data) {
						hideLoading();
						if(data.key != "undefined" && data.key == "error") {
							Feedback.error(data.value);
						}
						else {
							ContentPaneController.replaceContentWith("");
							Feedback.success("<fmt:message key='code.value.remove.success'/>");
							removeNode("${bean.model.elementId}");
						}
					};
					AjaxUtil.ajax(url,null,successCallback,null,function(){showLoading("<fmt:message key='progress.msg.remove'/>");});
				}
			});
		});
		$("#formSave").click(function(event) {
			showLoading("<fmt:message key='progress.msg.save'/>");
			//$("#formConcept").submit();
			var url = $("#formConcept")[0].action;
			url=turnOnTimestampCheck(url);
			$("#formConcept")[0].action=url;
			var replaceContent = function(data) {
				hideLoading();
				ContentPaneController.replaceContentWith(data);
			};
			AjaxUtil.submit("formConcept", replaceContent);
			return false;
		});
		$(".add").click(function(event) {
			event.preventDefault();
			ContentPaneController.replaceContent(this.href,null,hideLoading,hideLoading,function(){showLoading("<fmt:message key='progress.msg.load'/>");});
		});
		$("#formReset").click(function() {
			$("#englishDiagramInfo").show();
			$("#englishDiagramDeleteFlag").val(false);
			$("#frenchDiagramInfo").show();
			$("#frenchDiagramDeleteFlag").val(false);
			$("#formConcept")[0].reset();
			$("#shortTitleEng").keyup();
			$("#shortTitleFra").keyup();
			return false;
		});
		$("#englishDiagramDelete").click(function() {
			$("#englishDiagramInfo").hide();
			$("#englishDiagramDeleteFlag").val(true);
			return false;
		});
		$("#frenchDiagramDelete").click(function() {
			$("#frenchDiagramInfo").hide();
			$("#frenchDiagramDeleteFlag").val(true);
			return false;
		});
		createCounter("shortTitleEng");
		createCounter("shortTitleFra");

		$("#dialogDiv").dialog({
			   autoOpen: false,
			   height: 750,
			   width: 500,
			   resizable: true,
			   modal: true
			});
		$('a.dialog').click(function(){
			   var url = $(this).attr('href');
			   showImageDialog(url);
			   return false;
			});

		$("#lnkQualifier").click(function(){
			   var div=$("#dialogQualifierDiv");
			   div.empty();
			   div.dialog({
				   autoOpen: false,
				   height: 200,
				   width: 525,
				   title: "<fmt:message key='new.qualifier.title'/>",
				   resizable: false,
				   modal: true
				});
			   div.load('<c:url value="/tabulars/qualifier/add.htm?${automaticContextParams}"/>');
			   div.dialog("open");
			   return false;
			});
		$("#lnkReference").click(function(){
			   popupwindow($(this).attr("href"), null, 870, 550);
			   return false;
			});
		
		//prevent default form submission
		$("#formConcept").submit(function(event){event.preventDefault();});
	});

	function createCounter(inputId){
		inputId="#" + inputId;
		$(inputId).keyup(function(){
			$(inputId+"Counter").text($(inputId).val().length);
		});
		$(inputId).keyup();
	}

	function showImageDialog(url){
		var img=$("#dialogImg");
	    img.attr("src", url);
	    $("#dialogDiv").dialog("option", "title", "<fmt:message key='progress.msg.load'/>").dialog("open");   
	    img.load(function(){
	    	$("#dialogDiv").dialog("close");
	    	$("#dialogDiv").dialog("option", "title", "").dialog("open");  
		});
	}
	
	//# sourceURL=tabularBasicInfo.jsp
</script>
</body>
</html>
