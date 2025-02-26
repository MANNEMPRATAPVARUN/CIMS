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
			action="indexes/codereferences/editICD34.htm?${automaticContextParams}&id=${bean.model.elementId}&language=${param.language}"
		>
			<input type="hidden" name="tab" value="CODE" />
			<input type="hidden" name="lockTimestamp" value="${bean.lockTimestamp}" />
			<table class="nobottom" >
				<tr>
					<td colspan="3">
						<table id="referenceTable" class="listTable">
							<thead>
								<tr>
									<th class="tableHeader">Reference Category</th>
									<th class="tableHeader">Reference Code Value</th>
									<th class="tableHeader">Custom Description</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${bean.model.icd3}">
										<tr>
											<td>Poisoning - Chapter XIX:</td>
											<c:choose>
												<c:when test="${bean.editable}">
													<td>
														<form:hidden path="references['CHAPTER_XIX'].elementId" />
														<form:input path="references['CHAPTER_XIX'].code" size="10" cssClass="auto limit-19-T" />
													</td>
													<td>
														<form:input path="references['CHAPTER_XIX'].customDescription" size="10"/>
													</td>
												</c:when>
												<c:otherwise>
													<td>${bean.references['CHAPTER_XIX'].code}</td>
													<td>${bean.references['CHAPTER_XIX'].customDescription}</td>
												</c:otherwise>
											</c:choose>
										</tr>
										<tr>
											<td>Poisoning - Accidental:</td>
											<c:choose>
												<c:when test="${bean.editable}">
													<td>
														<form:hidden path="references['ACCIDENTAL'].elementId" />
														<form:input path="references['ACCIDENTAL'].code" size="10" cssClass="auto limit-20-X" />
													</td>
													<td>
														<form:input path="references['ACCIDENTAL'].customDescription" size="10"/>
													</td>
												</c:when>
												<c:otherwise>
													<td>${bean.references['ACCIDENTAL'].code}</td>
													<td>${bean.references['ACCIDENTAL'].customDescription}</td>
												</c:otherwise>
											</c:choose>
										</tr>
										<tr>
											<td>Poisoning - Intentional Self-Harm:</td>
											<c:choose>
												<c:when test="${bean.editable}">
													<td>
														<form:hidden path="references['INT_SELF_HARM'].elementId" />
														<form:input path="references['INT_SELF_HARM'].code" size="10" cssClass="auto limit-20-X" />
													</td>
													<td>
														<form:input path="references['INT_SELF_HARM'].customDescription" size="10"/>
													</td>
												</c:when>
												<c:otherwise>
													<td>${bean.references['INT_SELF_HARM'].code}</td>
													<td>${bean.references['INT_SELF_HARM'].customDescription}</td>
												</c:otherwise>
											</c:choose>
										</tr>
										<tr>
											<td>Poisoning - Undetermined Intent:</td>
											<c:choose>
												<c:when test="${bean.editable}">
													<td>
														<form:hidden path="references['UNDE_INTENT'].elementId" />
														<form:input path="references['UNDE_INTENT'].code" size="10" cssClass="auto limit-20-Y" />
													</td>
													<td>
														<form:input path="references['UNDE_INTENT'].customDescription" size="10"/>
													</td>
												</c:when>
												<c:otherwise>
													<td>${bean.references['UNDE_INTENT'].code}</td>
													<td>${bean.references['UNDE_INTENT'].customDescription}</td>
												</c:otherwise>
											</c:choose>
										</tr>
										<tr>
											<td>Adverse effect in therapeutic use:</td>
											<c:choose>
												<c:when test="${bean.editable}">
													<td>
														<form:hidden path="references['AETU'].elementId" />
														<form:input path="references['AETU'].code" size="10" cssClass="auto limit-20-Y" />
													</td>
													<td>
														<form:input path="references['AETU'].customDescription" size="10"/>
													</td>
												</c:when>
												<c:otherwise> 
													<td>${bean.references['AETU'].code}</td>
													<td>${bean.references['AETU'].customDescription}</td>
												</c:otherwise>
											</c:choose>
										</tr>
									</c:when>
									<c:otherwise>
										<tr>
											<td>Malignant - Primary:</td>
											<c:choose>
												<c:when test="${bean.editable}">
													<td>
														<form:hidden path="references['MALIGNANT_PRIMARY'].elementId" />
														<form:input path="references['MALIGNANT_PRIMARY'].code" size="10" cssClass="auto limit-2-C" />
													</td>
													<td>
														<form:input path="references['MALIGNANT_PRIMARY'].customDescription" size="10"/>
													</td>
												</c:when>
												<c:otherwise>
													<td>${bean.references['MALIGNANT_PRIMARY'].code}</td>
													<td>${bean.references['MALIGNANT_PRIMARY'].customDescription}</td>
												</c:otherwise>
											</c:choose>
										</tr>
										<tr>
											<td>Malignant - Secondary:</td>
											<c:choose>
												<c:when test="${bean.editable}">
													<td>
														<form:hidden path="references['MALIGNANT_SECONDARY'].elementId" />
														<form:input path="references['MALIGNANT_SECONDARY'].code" size="10" cssClass="auto limit-2-C" />
													</td>
													<td>
														<form:input path="references['MALIGNANT_SECONDARY'].customDescription" size="10"/>
													</td>
												</c:when>
												<c:otherwise>
													<td>${bean.references['MALIGNANT_SECONDARY'].code}</td>
													<td>${bean.references['MALIGNANT_SECONDARY'].customDescription}</td>
												</c:otherwise>
											</c:choose>
										</tr>
										<tr>
											<td>In Situ:</td>
											<c:choose>
												<c:when test="${bean.editable}">
													<td>
														<form:hidden path="references['IN_SITU'].elementId" />
														<form:input path="references['IN_SITU'].code" size="10" cssClass="auto limit-2-D" />
													</td>
													<td>
														<form:input path="references['IN_SITU'].customDescription" size="10"/>
													</td>
												</c:when>
												<c:otherwise>
													<td>${bean.references['IN_SITU'].code}</td>
													<td>${bean.references['IN_SITU'].customDescription}</td>
												</c:otherwise>
											</c:choose>
										</tr>
										<tr>
											<td>Benign:</td>
											<c:choose>
												<c:when test="${bean.editable}">
													<td>
														<form:hidden path="references['BENIGN'].elementId" />
														<form:input path="references['BENIGN'].code" size="10" cssClass="auto limit-2-D" />
													</td>
													<td>
														<form:input path="references['BENIGN'].customDescription" size="10"/>
													</td>
												</c:when>
												<c:otherwise>
													<td>${bean.references['BENIGN'].code}</td>
													<td>${bean.references['BENIGN'].customDescription}</td>
												</c:otherwise>
											</c:choose>
										</tr>
										<tr>
											<td>Uncertain or unknown behaviour:</td>
											<c:choose>
												<c:when test="${bean.editable}">
													<td>
														<form:hidden path="references['UU_BEHAVIOUR'].elementId" />
														<form:input path="references['UU_BEHAVIOUR'].code" size="10" cssClass="auto limit-2-D" />
													</td>
													<td>
														<form:input path="references['UU_BEHAVIOUR'].customDescription" size="10"/>
													</td>
												</c:when>
												<c:otherwise>
													<td>${bean.references['UU_BEHAVIOUR'].code}</td>
													<td>${bean.references['UU_BEHAVIOUR'].customDescription}</td>
												</c:otherwise>
											</c:choose>
										</tr>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
					</td>
				</tr>
				<tr>
					<td>
					<table style="width: 100%;" >
						<tr>
							<td align="right" nowrap="nowrap" valign="top">
								<c:if test="${bean.editable}">
									<a id="formSave" href="#"><img title="Save" src="<c:url value="/img/icons/Save.png"/>"/></a>
									<a id="formReset" href="<c:url value='/indexes/codereferences/editICD34.htm?${automaticContextParams}&id=${bean.elementId}&tab=CODE&language=${param.language}'/>"><img
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
	var g_autoSelected=true;
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
	function getLetter(input){
		var letter=null;
		$(input.attr('class').split(' ')).each(function() { 
	        if (this.indexOf("limit-")==0) {
	        	letter=this.split("-")[2].toUpperCase();
	        }    
	    });
	    return letter;
	}
	function getAutoId(mainCode){
		var id=mainCode.attr("id");
		//alert(id);
		id=replaceAll(id,"references","");
		id=replaceAll(id,".code","");
		id=replaceAll(id,"'","");
		id="\\'"+id+"\\'";
		return id;
	}
	$(function() {
		$("#formReset").click(function() {
			event.preventDefault();
			ContentPaneController.replaceContent(this.href,null,hideLoading,hideLoading,function(){showLoading("<fmt:message key='progress.msg.load'/>");});
		});
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
		$(".auto").keydown(function(e){
			var input=$(this);
			var id=getAutoId(input);
			$("#references"+id+"\\.elementId").val("0");
			var letter=getLetter(input);
		    if(letter!=null){
				var start = e.target.selectionStart;
		    	var val=input.val().toUpperCase();
		    	//remove selection filters for code search
		    	/*if(start==0|| val.indexOf(letter)!=0){
		    		input.val("");
		    		var key=String.fromCharCode(e.keyCode).toUpperCase();
		    		if(key!=letter){
			    		//2021-06-07 - allow any codes to be searched and added as values
			    		//return false;
			    	}
				 }*/
			}
		});
		$(".auto").focusout(function(e){
			$(this).change(e);	
		});
		$(".auto").change(function(e){
			//alert(elementId);
			if(!g_autoSelected){
				var mainCode=$(this);
				var id=getAutoId(mainCode);
				var elementId=$("#references"+id+"\\.elementId").val();
				mainCode.val("");
				$("#references"+id+"\\.customDescription").val("");
			}
		});
		$(".auto").autocomplete({
			source: function(request, response) {
				g_autoSelected=false;
		        $.ajax({
		            url: "${pageContext.request.contextPath}/getCodeSearchResult.htm?classification=${param.ccp_bc}&contextId=${param.ccp_cid}&language=${param.language}&leaf=true",
		            contentType:"application/json; charset=UTF-8",
		            data: {
		              term : request.term,
		              searchBy : "code"
		            },
		            success: function(data) { response(data); }
		          });
			},
			// id="references&#39;AETU&#39;.code"
			select : function(event, ui) {
				var mainCode=$(this);
				var id=getAutoId(mainCode);
				mainCode.val(ui.item.value);
				//CM-RU175
				var description=ui.item.value;
				if(!ui.item.leaf){
					if(description.length==3){
						description+=".-";
					}else{
						description+="-";
					}
				}
				$("#references"+id+"\\.elementId").val(ui.item.conceptId);
				$("#references"+id+"\\.customDescription").val(description);
				g_autoSelected=true;
			}
		}).css({backgroundColor : "yellow"});
	});
	//the following line is used by browser's dev tools
	//to show the name of the evaluated JS
	//# sourceURL=indexCodeReferencesICD34.js
</script>
</body>
</html>


