<!DOCTYPE html>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<jsp:include page="../../common/common-header.jsp"/>

<html>
<h4 style="text-align: center;"> Proposed and Conflict Validation Value</h4>

 <c:choose>
		 <c:when test="${changeRequest.baseClassification == 'CCI'}">						       
		    <table id="proposedValidationChanges" style="width: 100%; margin-top: 20px;" class="listTable">
				<thead>
		 		  <tr>
		 			<th class="tableHeader" style="width:10%;border:1px"><fmt:message key="change.summary.validation.value" /></th>	
					<th class="tableHeader" style="width:15%;border:1px"><fmt:message key="code.cci.validation.report.dataHolding" /></th>	
					<th class="tableHeader" style="width:15%;border:1px"><fmt:message key="code.cci.validation.report.gender" /></th>	
					<th class="tableHeader" style="width:15%;border:1px"><fmt:message key="code.cci.validation.report.ageRange" /></th>	
					<th class="tableHeader" style="width:15%;border:1px"><fmt:message key="code.cci.validation.report.statusRef" /></th>	
					<th class="tableHeader" style="width:15%;border:1px"><fmt:message key="code.cci.validation.report.locationRef" /></th>	
					<th class="tableHeader" style="width:15%;border:1px"><fmt:message key="code.cci.validation.report.extentRef" /></th>	
				  </tr>
				 </thead>
				 <tbody>
					<c:forEach var="validationChange" items="${conceptModification.proposedValidationChanges}" varStatus="status">
				     <tr class="${status.index%2==0 ? 'even' : 'odd'}">
				    	<td>${validationChange.value}</td>
						<td>${validationChange.dataHolding}</td>
						<c:choose>
							<c:when test="${validationChange.status == null}">
							<td>${validationChange.cciValidationXml.genderDescriptionEng}</td>
							<td>${validationChange.cciValidationXml.ageRange}</td>
							<td>${validationChange.cciValidationXml.statusReferenceCode}</td>
							<td>${validationChange.cciValidationXml.locationReferenceCode}</td>
							<td>${validationChange.cciValidationXml.extentReferenceCode}</td>
							</c:when>
						    <c:otherwise>
							 <td colspan="5" align="center">${validationChange.status}</td>														
							</c:otherwise>
						</c:choose>														
					 </tr>
				   </c:forEach>
			     </tbody>
			  </table>
		  </c:when>
		  <c:otherwise>
		  	 <table id="proposedValidationChanges" style="width: 100%; margin-top: 20px;" class="listTable">
		       <thead>
			 	<tr>
			  	 <th class="tableHeader" style="width:5%;border:1px"><fmt:message key="change.summary.validation.value" /></th>	
				 <th class="tableHeader" style="width:15%;border:1px"><fmt:message key="code.icd.validation.report.dataHolding" /></th>	
				 <th class="tableHeader" style="width:15%;border:1px"><fmt:message key="code.icd.validation.report.gender" /></th>	
				 <th class="tableHeader" style="width:10%;border:1px"><fmt:message key="code.icd.validation.report.ageRange" /></th>	
				 <th class="tableHeader" style="width:5%;border:1px"><fmt:message key="code.icd.validation.report.mrdxMain" /></th>	
				 <th class="tableHeader" style="width:5%;border:1px"><fmt:message key="code.icd.validation.report.dxType1" /></th>	
				 <th class="tableHeader" style="width:5%;border:1px"><fmt:message key="code.icd.validation.report.dxType2" /></th>
				 <th class="tableHeader" style="width:5%;border:1px"><fmt:message key="code.icd.validation.report.dxType3" /></th>
				 <th class="tableHeader" style="width:5%;border:1px"><fmt:message key="code.icd.validation.report.dxType4" /></th>
				 <th class="tableHeader" style="width:5%;border:1px"><fmt:message key="code.icd.validation.report.dxType6" /></th>
				 <th class="tableHeader" style="width:5%;border:1px"><fmt:message key="code.icd.validation.report.dxType9" /></th>													
				 <th class="tableHeader" style="width:5%;border:1px"><fmt:message key="code.icd.validation.report.dxTypeW" /></th>
				 <th class="tableHeader" style="width:5%;border:1px"><fmt:message key="code.icd.validation.report.dxTypeX" /></th>
				 <th class="tableHeader" style="width:5%;border:1px"><fmt:message key="code.icd.validation.report.dxTypeY" /></th>
				 <th class="tableHeader" style="width:5%;border:1px"><fmt:message key="code.icd.validation.report.newBorn" /></th>
				</tr>
			   </thead>
			   <tbody>
				<c:forEach var="validationChange" items="${conceptModification.proposedValidationChanges}" varStatus="status">
				 <tr class="${status.index%2==0 ? 'even' : 'odd'}">
				   <td>${validationChange.value}</td>
				   <td>${validationChange.dataHolding}</td>
					<c:choose>
				    	<c:when test="${validationChange.status == null}">
						<td>${validationChange.icdValidationXml.genderDescriptionEng}</td>
						<td>${validationChange.icdValidationXml.ageRange}</td>
						<td>${validationChange.icdValidationXml.MRDxMain}</td>
						<td>${validationChange.icdValidationXml.dxType1}</td>
						<td>${validationChange.icdValidationXml.dxType2}</td>
						<td>${validationChange.icdValidationXml.dxType3}</td>
						<td>${validationChange.icdValidationXml.dxType4}</td>
						<td>${validationChange.icdValidationXml.dxType6}</td>
						<td>${validationChange.icdValidationXml.dxType9}</td>
						<td>${validationChange.icdValidationXml.dxTypeW}</td>
						<td>${validationChange.icdValidationXml.dxTypeX}</td>
						<td>${validationChange.icdValidationXml.dxTypeY}</td>
						<td>${validationChange.icdValidationXml.newBorn}</td>
						</c:when>
					    <c:otherwise>
					     <td colspan="13" align="center">${validationChange.status}</td>														
						</c:otherwise>
					</c:choose>
				</tr>
			 </c:forEach>
			</tbody>
		</table>
	  </c:otherwise>
	</c:choose>					


</html>