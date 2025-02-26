<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<!DOCTYPE html>

<html style="height:100%;">
<head>
	<meta http-equiv="Content-Type" content="${CONTENT_TYPE}">
		<!--Blueprint Framework CSS -->
	<link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/print.css" />" type="text/css"  media="print" />
	
	<link rel="stylesheet" type="text/css" href="<cc:resUrl value="/css/main.css" />">
	<link href="css/cims.css" rel="stylesheet">
	
	<title>Print Change Request Summary</title>
</head>
<body>
<style type="text/css" media="all">

.header  {
    font-weight: bold;
    font-size: 1.2em;
}

.section-header {
    font-weight: bold;
    font-size: 1.1em;
}

.label{
	font-weight: bold;
}

#changerequest-header.fixed {
    height: 80px;
    margin-bottom: 20px;
}

#changerequest-header.fixed .wrapper {
    height: 75px;
    position: fixed;
    top: 0;
    left: 10px;
    z-index: 100;
    padding: 0;
}
 table {
   width: 100%;
  }
  td{
    text-align: left;
    padding-right: 1px;
    padding-left: 1px;
    padding-top: 5px;
    padding-bottom: 5px;
  }
  img {border : 0;}
  body{
	  -webkit-print-color-adjust:exact;
	}

</style>
<div id="changerequest-header" style="width: 100%; overflow: visible !important; " class="header">
   <table style="margin-bottom: 0px; width:100%;">
		<tr>
			<td width="30%">Change Request ID:</td><td width="15%">${changeRequestDTO.changeRequestId}</td>
			<td width="25%">Classification:</td><td width="30%">${changeRequestDTO.baseClassification}</td>
		</tr>
		<tr>
		    <td>Year:</td><td>${changeRequestDTO.baseVersionCode}</td>
			 <td>Request Category:</td><td>${changeRequestDTO.category.code}</td> 
		</tr>
   </table>	
</div>
<div class="content">
   
   	<div id="button-header">
            <div class="alignRight">
	      
           <div class="btn_alignRight header">
               <c:if test="${changeRequestDTO.deferredChangeRequestId !=null }">
                 Original Deferring CR: &nbsp;   ${changeRequestDTO.deferredChangeRequestId} &nbsp;&nbsp;&nbsp;
               </c:if>
              
               <c:if test="${changeRequestDTO.deferredTo !=null }">
                 This change request has been deferred to ${changeRequestDTO.deferredTo.baseVersionCode} ; Deferred CR:&nbsp;  ${changeRequestDTO.deferredTo.changeRequestId} &nbsp;&nbsp;&nbsp;
               </c:if>
              
              Owner: &nbsp; ${changeRequestDTO.owner.username}  &nbsp;&nbsp;&nbsp;
              Assignee:   &nbsp; 
             <c:choose>
                <c:when test="${changeRequestDTO.userAssignee !=null }">
                    ${changeRequestDTO.userAssignee.username}
               </c:when>
                <c:otherwise>
                    ${changeRequestDTO.dlAssignee.name}
               </c:otherwise>  
             </c:choose>    
           </div>
         </div>
       </div>
	
    <div class="sectionHeader section-header" align="center" style="margin-bottom:5px;"><fmt:message key="change.summary.modificationSummary" /></div>
    <c:choose>
    	<c:when test="${changeRequestDTO.changeSummary.noChange}">
    	    <fmt:message  key="change.summary.noChange" />
    	</c:when>
    	<c:otherwise>    	
		    <c:forEach var="conceptModification" items="${changeRequestDTO.changeSummary.conceptModifications}">
		    	<div class="section">
					<div>			   
			     		<div><span class="label"><fmt:message key="change.summary.codeValue" />:</span> ${conceptModification.code}</div>	     		 
			 		</div>			 		
			 		<div>	
			 		   	<c:if test="${fn:length(conceptModification.proposedTabularChanges) gt 0 or fn:length(conceptModification.proposedValidationChanges) gt 0}">	 		     
			 		    	<div class="sectionHeader section-header"><fmt:message key="change.summary.proposedChanges" /></div>
			 		    </c:if>
			 		    <c:if test="${fn:length(conceptModification.proposedTabularChanges) gt 0}">	 		
						 	<table id="proposedTabularChanges" style="width: 100%; margin-top: 10px;" class="listTable">
									<thead>
										<tr>
											<th class="tableHeader" style="width:25%;border:1px"><fmt:message key="change.summary.fieldName" /></th>								
											<th class="tableHeader" style="width:25%;border:1px"><fmt:message key="change.summary.oldValue" /></th>
											<th class="tableHeader" style="width:25%;border:1px"><fmt:message key="change.summary.proposedValue" /></th>
											<th class="tableHeader" style="width:25%;border:1px"><fmt:message key="change.summary.conflictValue" /></th>						
										</tr>
									</thead>
									<tbody>
									    <c:forEach var="proposedChange" items="${conceptModification.proposedTabularChanges}" varStatus="status">
										     <tr class="${status.index%2==0 ? 'even' : 'odd'}">
												<td>${proposedChange.fieldName}</td>
												<c:choose>										
													<c:when test="${proposedChange.tableName=='XMLPropertyVersion'}">
													    <td>
													    	<c:if test="${proposedChange.oldValue != null and proposedChange.oldValue != 'no_value' and proposedChange.oldValue != ''}">
													    		<a href="javascript:popupXmlProperty1('viewXmlProperty.htm?xmlPropertyId=${proposedChange.oldValue}&category=Proposed%20Change&fieldName=Old%20${proposedChange.fieldName}&code=${conceptModification.code}');"> <fmt:message key="change.summary.old" /> ${proposedChange.fieldName}</a>
													        </c:if>
													    </td>													    
														<td>
														   <c:if test="${proposedChange.proposedValue != null and proposedChange.proposedValue != 'no_value' and proposedChange.proposedValue != ''}">
														    <a href="javascript:popupXmlProperty2('viewXmlProperty.htm?xmlPropertyId=${proposedChange.proposedValue}&category=Proposed%20Change&fieldName=Proposed%20${proposedChange.fieldName}&code=${conceptModification.code}');"><fmt:message key="change.summary.proposed" /> ${proposedChange.fieldName}</a>
														   </c:if>
														</td>
														<td>
														  <c:choose>
														    <c:when test="${proposedChange.conflictValue=='no_conflict'}">
														  	   <!-- present nothing -->
														  	</c:when>
														  	<c:when test="${proposedChange.conflictValue=='no_value'}">
														  		<fmt:message key="change.summary.noValue"/>
														  	</c:when>														  	
														  	<c:otherwise>														  	    
														  	     <a href="javascript:popupXmlProperty3('viewXmlProperty.htm?xmlPropertyId=${proposedChange.conflictValue}&category=Proposed%20Change&fieldName=Conflict%20${proposedChange.fieldName}&code=${conceptModification.code}');"><fmt:message key="change.summary.conflict" /> ${proposedChange.fieldName}</a>
														  	</c:otherwise>
														  </c:choose>													
													    </td>	
													</c:when>
													<c:when test="${proposedChange.tableName=='HTMLPropertyVersion'}">
														<td>
															<c:if test="${proposedChange.oldValue != null and proposedChange.oldValue != '' and proposedChange.oldValue != 'no_value'}">
																<a href="javascript:popupXmlProperty1('viewHtmlProperty.htm?htmlPropertyId=${proposedChange.oldValue}&category=Proposed%20Change&fieldName=Old%20${proposedChange.fieldName}&code=${conceptModification.code}');"> <fmt:message key="change.summary.old" /> ${proposedChange.fieldName}</a>
															</c:if>
													    </td>
														<td>
															<c:if test="${proposedChange.proposedValue != null and proposedChange.proposedValue != '' and proposedChange.proposedValue != 'no_value'}">
																<a href="javascript:popupXmlProperty2('viewHtmlProperty.htm?htmlPropertyId=${proposedChange.proposedValue}&category=Proposed%20Change&fieldName=Proposed%20${proposedChange.fieldName}&code=${conceptModification.code}');"><fmt:message key="change.summary.proposed" /> ${proposedChange.fieldName}</a>
															</c:if>
														</td>
														<td>
														   <c:choose>
														    <c:when test="${proposedChange.conflictValue=='no_conflict'}">
														  	   <!-- present nothing -->
														  	</c:when>
														  	<c:when test="${proposedChange.conflictValue=='no_value'}">
														  		<fmt:message key="change.summary.noValue"/>
														  	</c:when>														  	
														  	<c:otherwise>														  	    
														  	     <a href="javascript:popupXmlProperty3('viewHtmlProperty.htm?htmlPropertyId=${proposedChange.conflictValue}&category=Proposed%20Change&fieldName=Conflict%20${proposedChange.fieldName}&code=${conceptModification.code}');"><fmt:message key="change.summary.conflict" /> ${proposedChange.fieldName}</a>
														  	</c:otherwise>
														  </c:choose>														 
													    </td>	
													</c:when>													
													<c:otherwise>
														<td>
															<c:if test="${proposedChange.oldValue != null and proposedChange.oldValue != 'no_value' and proposedChange.oldValue != ''}">
																${proposedChange.oldValue}
															</c:if>
														</td>
														<td>
															<c:if test="${proposedChange.proposedValue != null and proposedChange.proposedValue != 'no_value' and proposedChange.proposedValue != ''}">
																${proposedChange.proposedValue}
															</c:if>
														</td>
														<td>
															 <c:choose>
															    <c:when test="${proposedChange.conflictValue=='no_conflict'}">
															  	   <!-- present nothing -->
															  	</c:when>
															  	<c:when test="${proposedChange.conflictValue=='no_value'}">
															  		<fmt:message key="change.summary.noValue"/>
															  	</c:when>														  	
															  	<c:otherwise>														  	    
															  	    ${proposedChange.conflictValue}
															  	</c:otherwise>
															  </c:choose>
														</td>	
													</c:otherwise>	
												</c:choose>							
											</tr>
									    </c:forEach>																	
									</tbody>
								</table>
						</c:if>							
						<c:if test="${fn:length(conceptModification.proposedValidationChanges) gt 0}">	
						     <fmt:message key="change.summary.validation" />							       					       
						       <c:choose>
							       <c:when test="${changeRequestDTO.baseClassification == 'CCI'}">						       
								       <table id="proposedValidationChanges" style="width: 100%; margin-top: 0px;" class="listTable">
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
														    	<c:choose>
														    		<c:when test="${validationChange.status== 'no_value'}">
														    			<td colspan="5" style="text-align:center;"><fmt:message key="change.summary.noValue"/></td>	
														    		</c:when>
														    		<c:otherwise>
														    			<td colspan="5" style="text-align:center;">${validationChange.status}</td>	
														    		</c:otherwise>
														    	</c:choose>														        													
															</c:otherwise>
														</c:choose>														
													</tr>
												</c:forEach>
											</tbody>
									  </table>
								  </c:when>
								  <c:otherwise>
								  	 <table id="proposedValidationChanges" style="width: 100%; margin-top: 0px;" class="listTable">
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
														    	<c:choose>
														    		<c:when test="${validationChange.status=='no_value'}">
														    			<td colspan="13" style="text-align:center;"><fmt:message key="change.summary.noValue"/></td>	
														    		</c:when>
														    		<c:otherwise>
														    			<td colspan="13" style="text-align:center;">${validationChange.status}</td>	
														    		</c:otherwise>
														    	</c:choose>														
															</c:otherwise>
														</c:choose>
													</tr>
												</c:forEach>
											</tbody>
									  </table>
								  </c:otherwise>
							  </c:choose>							  
						</c:if>						
						<c:if test="${fn:length(conceptModification.realizedTabularChanges) gt 0 or fn:length(conceptModification.realizedValidationChanges) gt 0}">	 		     
			 		    	<div class="sectionHeader"><fmt:message key="change.summary.realizedChanges" /></div> 
			 		    </c:if>						
						<c:if test="${fn:length(conceptModification.realizedTabularChanges) gt 0}">	
							<table id="realizedChanges" style="width: 100%; margin-top: 10px;" class="listTable">
								<thead>
									<tr>
										<th class="tableHeader" style="width:30%;border:1px"><fmt:message key="change.summary.fieldName" /></th>
										<th class="tableHeader" style="width:30%;border:1px"><fmt:message key="change.summary.oldValue" /></th>
										<th class="tableHeader" style="width:40%;border:1px"><fmt:message key="change.summary.newValue" /></th>						
									</tr>
								</thead>
								<tbody>
								    <c:forEach var="realizedChange" items="${conceptModification.realizedTabularChanges}"  varStatus="status">
									     <tr class="${status.index%2==0 ? 'even' : 'odd'}">
											<td>${realizedChange.fieldName}</td>
											<c:choose>
												<c:when test="${realizedChange.tableName=='XMLPropertyVersion'}">
													<td>
													   <c:if test="${realizedChange.oldValue != null and realizedChange.oldValue != '' and realizedChange.oldValue !='no_value'}">
															<a href="javascript:popupXmlProperty4('viewXmlProperty.htm?xmlPropertyId=${realizedChange.oldValue}&category=Realized%20Change&fieldName=Old%20${realizedChange.fieldName}&code=${conceptModification.code}');"><fmt:message key="change.summary.old" /> ${realizedChange.fieldName}</a>
														</c:if>
													</td>
													<td>
													   <c:if test="${realizedChange.newValue != null and realizedChange.newValue != '' and realizedChange.newValue != 'no_value'}">
													   		<a href="javascript:popupXmlProperty5('viewXmlProperty.htm?xmlPropertyId=${realizedChange.newValue}&category=Realized%20Change&fieldName=New%20${realizedChange.fieldName}&code=${conceptModification.code}');"><fmt:message key="change.summary.new" /> ${realizedChange.fieldName}</a>
														</c:if>													
													</td>												
												</c:when>
												<c:when test="${realizedChange.tableName=='HTMLPropertyVersion'}">
													<td>
														<c:if test="${realizedChange.oldValue != null and realizedChange.oldValue != '' and realizedChange.oldValue !='no_value'}">
															<a href="javascript:popupXmlProperty4('viewHtmlProperty.htm?htmlPropertyId=${realizedChange.oldValue}&category=Realized%20Change&fieldName=Old%20${realizedChange.fieldName}&code=${conceptModification.code}');"><fmt:message key="change.summary.old" /> ${realizedChange.fieldName}</a>
														</c:if>
													</td>
													<td>
														<c:if test="${realizedChange.newValue != null and realizedChange.newValue != '' and realizedChange.newValue != 'no_value'}">
															<a href="javascript:popupXmlProperty5('viewHtmlProperty.htm?htmlPropertyId=${realizedChange.newValue}&category=Realized%20Change&fieldName=New%20${realizedChange.fieldName}&code=${conceptModification.code}');"><fmt:message key="change.summary.new" /> ${realizedChange.fieldName}</a>
														</c:if>	
													</td>												
												</c:when>
												<c:otherwise>
													<td>
														<c:if test="${realizedChange.oldValue !='no_value'}"> 
															${realizedChange.oldValue}
														</c:if>
													</td>
												    <td>
												    	<c:if test="${realizedChange.newValue !='no_value'}"> 
															${realizedChange.newValue}
														</c:if>
												    </td>
												</c:otherwise>
											</c:choose>
										</tr>
								    </c:forEach>																	
								</tbody>
							</table>
						</c:if>							
						<c:if test="${fn:length(conceptModification.realizedValidationChanges) gt 0}">	
						     <fmt:message key="change.summary.validation" />							       					       
						       <c:choose>
							       <c:when test="${changeRequestDTO.baseClassification == 'CCI'}">						       
								       <table id="realizedValidationChanges" style="width: 100%; margin-top: 0px;" class="listTable">
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
												<c:forEach var="validationChange" items="${conceptModification.realizedValidationChanges}" varStatus="status">
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
														        <td colspan="5" style="text-align:center;">${validationChange.status}</td>														
															</c:otherwise>
														</c:choose>
													</tr>
												</c:forEach>
											</tbody>
									  </table>
								  </c:when>
								  <c:otherwise>
								  	 <table id="realizedValidationChanges" style="width: 100%; margin-top: 0px;" class="listTable">
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
												<c:forEach var="validationChange" items="${conceptModification.realizedValidationChanges}" varStatus="status">
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
														        <td colspan="13" style="text-align:center;">${validationChange.status}</td>														
															</c:otherwise>
														</c:choose>
													</tr>
												</c:forEach>
											</tbody>
									  </table>
								  </c:otherwise>
							  </c:choose>							  
						</c:if>											
					</div>
		        </div>
		    </c:forEach>
    	</c:otherwise>
	</c:choose>
</div>
	<div style="display:inline-block; text-align: right; float:right; top:0px; border:0px; background: #ffffff;" class="no-print">		
		<img id="print" class="viewMode" title="Print" src="img/icons/Print.png" onclick="window.print();" />			
	</div>   		
</body>
</html>