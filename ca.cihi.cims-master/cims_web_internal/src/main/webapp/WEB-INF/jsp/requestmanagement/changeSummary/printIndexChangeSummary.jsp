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
			     		<div><span class="label"><fmt:message key="change.summary.indexTerm" />: ${conceptModification.code}<br/>	
			     		     <fmt:message key="change.summary.breadCrumbs" />: ${conceptModification.breadCrumbs}</span></div>	     		 
			 		</div>			 		
			 		<div>	
			 			<c:if test="${fn:length(conceptModification.proposedIndexChanges) gt 0 or conceptModification.proposedIndexRefChange != null}">	 		     
			 		    	<div class="sectionHeader section-header"><fmt:message key="change.summary.proposedChanges" /></div>
			 		    </c:if>
			 		    <c:if test="${fn:length(conceptModification.proposedIndexChanges) gt 0}">	 		
						 	<table id="proposedIndexChanges" style="width: 100%; margin-top: 10px;" class="listTable">
									<thead>
										<tr>
											<th class="tableHeader" style="width:25%;border:1px"><fmt:message key="change.summary.fieldName" /></th>								
											<th class="tableHeader" style="width:25%;border:1px"><fmt:message key="change.summary.oldValue" /></th>
											<th class="tableHeader" style="width:25%;border:1px"><fmt:message key="change.summary.proposedValue" /></th>
											<th class="tableHeader" style="width:25%;border:1px"><fmt:message key="change.summary.conflictValue" /></th>						
										</tr>
									</thead>
									<tbody>
									    <c:forEach var="proposedChange" items="${conceptModification.proposedIndexChanges}" varStatus="status">
										     <tr class="${status.index%2==0 ? 'even' : 'odd'}">
												<td>
													<c:choose>
												        <c:when test="${proposedChange.tableName=='HTMLPropertyVersion'}">
												        	<fmt:message key="change.summary.reference" />
												        </c:when>
												        <c:otherwise>
															 ${proposedChange.fieldName} 
														</c:otherwise>
													</c:choose>
												</td>
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
						</c:if>	<c:if test="${conceptModification.proposedIndexRefChange != null}">	 		
						 		<fmt:message key="change.summary.references" />							       					       
						       <c:choose>
							       <c:when test="${conceptModification.conceptClassName == 'DrugsAndChemicalsIndex'}">	
								       <table id="proposedIndexRefChanges" style="width: 70%; margin-top: 0px; align:center" class="listTable" border="0" cellspacing="0" cellpadding="0">
								           <thead>
									         <tr>
									            <th width="10%" rowspan="2" align="left" class="th_drugs_top"><fmt:message key="change.summary.reference.value" /></th>	
									            <th width="25%" rowspan="2" align="left" class="th_drugs_top"><fmt:message key="change.summary.reference.indexReference" /></th>			            
									            <th colspan="4" align="center" class="th_drugs_top"><fmt:message key="change.summary.reference.Poisioning" /></th>
									            <th width="15%" rowspan="2" align="center" class="th_drugs_top"><fmt:message key="change.summary.reference.AETU" /></th>
									         </tr>
									         <tr>
									            <th width="10%" align="center" class="th_drugs_bottom"><fmt:message key="change.summary.reference.ChapterXIX" /></th>
									            <th width="10%" align="center" class="th_drugs_bottom"><fmt:message key="change.summary.reference.Accidental" /></th>
									            <th width="15%" align="center" class="th_drugs_bottom"><fmt:message key="change.summary.reference.IntentionalSelfHarm" /></th>
									            <th width="15%" align="center" class="th_drugs_bottom"><fmt:message key="change.summary.reference.UndeterminedIntent" /></th>
									         </tr>
									        </thead>
											<tbody>
											     <tr>
											     	<td><fmt:message key="change.summary.old" /></td>
											     	<c:choose>
												     	<c:when test="${conceptModification.proposedIndexRefChange.oldValue != null 
												     	              and conceptModification.proposedIndexRefChange.oldValue != 'no_value' 
												     	              and conceptModification.proposedIndexRefChange.oldValue != ''}">
												     		${conceptModification.proposedIndexRefChange.oldValue}
												     	</c:when>
												     	<c:otherwise>
												     		<td/><td/><td/><td/><td/>
												     	</c:otherwise>
											     	</c:choose>										     	
											     </tr>
											     <tr>
											    	<td><fmt:message key="change.summary.proposed" /></td>
											    	<c:choose>
												     	<c:when test="${conceptModification.proposedIndexRefChange.proposedValue != null 
												     					and conceptModification.proposedIndexRefChange.proposedValue != 'no_value' 
												     					and conceptModification.proposedIndexRefChange.proposedValue != ''}">
												     		${conceptModification.proposedIndexRefChange.proposedValue}
												     	</c:when>
												     	<c:otherwise>
												     		<td/><td/><td/><td/><td/>
												     	</c:otherwise>
											     	</c:choose>
											     </tr>
											     <c:choose>
											     	<c:when test="${conceptModification.proposedIndexRefChange.conflictValue=='no_conflict'}">
													  	   <!-- present nothing -->
												  	</c:when>
												  	<c:when test="${conceptModification.proposedIndexRefChange.conflictValue=='no_value'}">
												  	 	<tr>
											     			<td><fmt:message key="change.summary.conflict" /></td>
												  			<td colspan="5"><fmt:message key="change.summary.noValue"/></td>
												  		</tr>
												  	</c:when>														  	
												  	<c:otherwise>
												  		<tr>
											     			<td><fmt:message key="change.summary.conflict" /></td>
												  			 ${conceptModification.proposedIndexRefChange.conflictValue}
												  		</tr>
												  	</c:otherwise>
											      </c:choose>
											</tbody>
										  </table>
								  	</c:when>
								  	<c:when test="${conceptModification.conceptClassName == 'NeoplasmIndex'}">									  	
									  	<table id="proposedIndexRefChanges"  style="width: 70%; margin-top: 0px;align:center" class="listTable" border="0" cellspacing="0" cellpadding="0">
										  <thead>
											  <tr>
											    <th width="10%" rowspan="2" align="left" class="th_drugs_top"><fmt:message key="change.summary.reference.value" /></th>	
											    <th width="25%" rowspan="2" align="left" class="th_drugs_top"><fmt:message key="change.summary.reference.indexReference" /></th>											    						            
									            <th width="5%" rowspan="2" align="left" class="th_drugs_top"><fmt:message key="change.summary.reference.siteIndicator" /></th>										            
											    <th colspan="2"  class="th_drugs_top" align="center"><fmt:message key="change.summary.reference.Malignant"/></th>
											    <th width="10%" rowspan="2"  class="th_drugs_top" align="center"><fmt:message key="change.summary.reference.inSitu"/></th>
											    <th width="10%" rowspan="2"  class="th_drugs_top" align="center"><fmt:message key="change.summary.reference.Benign"/></th>
											    <th width="20%"  class="th_drugs_top" rowspan="2" align="center"><fmt:message key="change.summary.reference.UUB"/></th>
											  </tr>
											  <tr>
											    <th width="10%" class="th_drugs_bottom" align="center"><fmt:message key="change.summary.reference.Primary"/></th>
											    <th width="10%" class="th_drugs_bottom" align="center"><fmt:message key="change.summary.reference.Secondary"/></th>
											  </tr>
										  </thead>
										  <tbody>
											     <tr>
											     	<td><fmt:message key="change.summary.old" /></td>
											     	<c:choose>
												     	<c:when test="${conceptModification.proposedIndexRefChange.oldValue != null 
												     	              and conceptModification.proposedIndexRefChange.oldValue != 'no_value' 
												     	              and conceptModification.proposedIndexRefChange.oldValue != ''}">
												     		${fn:replace(conceptModification.proposedIndexRefChange.oldValue, '&amp;diams;', '&diams;')}
												     	</c:when>
												     	<c:otherwise>
												     		<td/><td/><td/><td/><td/><td/>
												     	</c:otherwise>
											     	</c:choose>										     	
											     </tr>
											     <tr>
											    	<td><fmt:message key="change.summary.proposed" /></td>
											    	<c:choose>
												     	<c:when test="${conceptModification.proposedIndexRefChange.proposedValue != null 
												     					and conceptModification.proposedIndexRefChange.proposedValue != 'no_value' 
												     					and conceptModification.proposedIndexRefChange.proposedValue != ''}">
												     		${fn:replace(conceptModification.proposedIndexRefChange.proposedValue, '&amp;diams;', '&diams;')}
												     	</c:when>
												     	<c:otherwise>
												     		<td/><td/><td/><td/><td/><td/>
												     	</c:otherwise>
											     	</c:choose>
											     </tr>
											     <c:choose>
											     	<c:when test="${conceptModification.proposedIndexRefChange.conflictValue=='no_conflict'}">
													  	   <!-- present nothing -->
												  	</c:when>
												  	<c:when test="${conceptModification.proposedIndexRefChange.conflictValue=='no_value'}">
												  	 	<tr>
											     			<td><fmt:message key="change.summary.conflict" /></td>
												  			<td colspan="5"><fmt:message key="change.summary.noValue"/></td>
												  		</tr>
												  	</c:when>														  	
												  	<c:otherwise>
												  		<tr>
											     			<td><fmt:message key="change.summary.conflict" /></td>
												  			 ${fn:replace(conceptModification.proposedIndexRefChange.conflictValue, '&amp;diams;', '&diams;')}
												  		</tr>
												  	</c:otherwise>
											      </c:choose>
											</tbody>
										  </table>
								  	</c:when>
								  	<c:otherwise>
								  		<!-- present nothing -->
								  	</c:otherwise>
								</c:choose>
						</c:if>							
							
						<c:if test="${fn:length(conceptModification.realizedIndexChanges) gt 0 or conceptModification.realizedIndexRefChange != null}">	 		     
			 		    	<div class="sectionHeader"><fmt:message key="change.summary.realizedChanges" /></div> 
			 		    </c:if>						
						<c:if test="${fn:length(conceptModification.realizedIndexChanges) gt 0}">	
							<table id="realizedIndexChanges" style="width: 100%; margin-top: 10px;" class="listTable">
								<thead>
									<tr>
										<th class="tableHeader" style="width:30%;border:1px"><fmt:message key="change.summary.fieldName" /></th>
										<th class="tableHeader" style="width:30%;border:1px"><fmt:message key="change.summary.oldValue" /></th>
										<th class="tableHeader" style="width:40%;border:1px"><fmt:message key="change.summary.newValue" /></th>						
									</tr>
								</thead>
								<tbody>
								    <c:forEach var="realizedChange" items="${conceptModification.realizedIndexChanges}"  varStatus="status">
									     <tr class="${status.index%2==0 ? 'even' : 'odd'}">	
									           <td>										
												    <c:choose>
												        <c:when test="${realizedChange.tableName=='HTMLPropertyVersion'}">
												        	<fmt:message key="change.summary.reference" />
												        </c:when>
												        <c:otherwise>
															 ${realizedChange.fieldName} 
														</c:otherwise>
													</c:choose>
												</td>											
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
												<c:otherwise>
													<td>
														<c:if test="${realizedChange.oldValue != null and realizedChange.oldValue != '' and realizedChange.oldValue !='no_value'}"> 
															${realizedChange.oldValue}
														</c:if>
													</td>
												    <td>
												    	<c:if test="${realizedChange.newValue != null and realizedChange.newValue != '' and realizedChange.newValue != 'no_value'}"> 
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
						<c:if test="${conceptModification.realizedIndexRefChange != null}">	 		
						 		<fmt:message key="change.summary.references" />							       					       
						       <c:choose>
							       <c:when test="${conceptModification.conceptClassName == 'DrugsAndChemicalsIndex'}">	
								       <table id="realizedIndexRefChanges" style="width: 50%; margin-top: 0px; align:center" class="listTable" border="0" cellspacing="0" cellpadding="0">
								           <thead>
									         <tr>
									            <th width="10%" rowspan="2" align="left" class="th_drugs_top"><fmt:message key="change.summary.reference.value" /></th>	
									            <th width="25%" rowspan="2" align="left" class="th_drugs_top"><fmt:message key="change.summary.reference.indexReference" /></th>			            
									            <th colspan="4" align="center" class="th_drugs_top"><fmt:message key="change.summary.reference.Poisioning" /></th>
									            <th width="15%" rowspan="2" align="center" class="th_drugs_top"><fmt:message key="change.summary.reference.AETU" /></th>
									         </tr>
									         <tr>
									            <th width="10%" align="center" class="th_drugs_bottom"><fmt:message key="change.summary.reference.ChapterXIX" /></th>
									            <th width="10%" align="center" class="th_drugs_bottom"><fmt:message key="change.summary.reference.Accidental" /></th>
									            <th width="15%" align="center" class="th_drugs_bottom"><fmt:message key="change.summary.reference.IntentionalSelfHarm" /></th>
									            <th width="15%" align="center" class="th_drugs_bottom"><fmt:message key="change.summary.reference.UndeterminedIntent" /></th>
									         </tr>
									        </thead>
											<tbody>
											     <tr>
											     	<td><fmt:message key="change.summary.old" /></td>
											     	<c:choose>
												     	<c:when test="${conceptModification.realizedIndexRefChange.oldValue != null 
												     	              and conceptModification.realizedIndexRefChange.oldValue != 'no_value' 
												     	              and conceptModification.realizedIndexRefChange.oldValue != ''}">
												     		${conceptModification.realizedIndexRefChange.oldValue}
												     	</c:when>
												     	<c:otherwise>
												     		<td/><td/><td/><td/><td/>
												     	</c:otherwise>
											     	</c:choose>										     	
											     </tr>
											     <tr>
											    	<td><fmt:message key="change.summary.new" /></td>
											    	<c:choose>
												     	<c:when test="${conceptModification.realizedIndexRefChange.newValue != null 
												     					and conceptModification.realizedIndexRefChange.newValue != 'no_value' 
												     					and conceptModification.realizedIndexRefChange.newValue != ''}">
												     		${conceptModification.realizedIndexRefChange.newValue}
												     	</c:when>
												     	<c:otherwise>
												     		<td/><td/><td/><td/><td/>
												     	</c:otherwise>
											     	</c:choose>
											     </tr>
											</tbody>
										  </table>
								  	</c:when>
								  	<c:when test="${conceptModification.conceptClassName == 'NeoplasmIndex'}">									  	
									  	<table id="realizedIndexRefChange"  style="width: 50%; margin-top: 0px;align:center" class="listTable" border="0" cellspacing="0" cellpadding="0">
											<thead>
											  <tr>
											    <th width="10%" rowspan="2" align="left" class="th_drugs_top"><fmt:message key="change.summary.reference.value" /></th>	
											    <th width="25%" rowspan="2" align="left" class="th_drugs_top"><fmt:message key="change.summary.reference.indexReference" /></th>											    						            
									            <th width="5%" rowspan="2" align="left" class="th_drugs_top"><fmt:message key="change.summary.reference.siteIndicator" /></th>										            
											    <th colspan="2"  class="th_drugs_top" align="center"><fmt:message key="change.summary.reference.Malignant"/></th>
											    <th width="10%" rowspan="2"  class="th_drugs_top" align="center"><fmt:message key="change.summary.reference.inSitu"/></th>
											    <th width="10%" rowspan="2"  class="th_drugs_top" align="center"><fmt:message key="change.summary.reference.Benign"/></th>
											    <th width="20%"  class="th_drugs_top" rowspan="2" align="center"><fmt:message key="change.summary.reference.UUB"/></th>
											  </tr>
											  <tr>
											    <th width="10%" class="th_drugs_bottom" align="center"><fmt:message key="change.summary.reference.Primary"/></th>
											    <th width="10%" class="th_drugs_bottom" align="center"><fmt:message key="change.summary.reference.Secondary"/></th>
											  </tr>
										  </thead>
										  <tbody>
											     <tr>
											     	<td><fmt:message key="change.summary.old" /></td>
											     	<c:choose>
												     	<c:when test="${conceptModification.realizedIndexRefChange.oldValue != null 
												     	              and conceptModification.realizedIndexRefChange.oldValue != 'no_value' 
												     	              and conceptModification.realizedIndexRefChange.oldValue != ''}">
												     		${fn:replace(conceptModification.realizedIndexRefChange.oldValue, '&amp;diams;', '&diams;')}
												     	</c:when>
												     	<c:otherwise>
												     		<td/><td/><td/><td/><td/><td/>
												     	</c:otherwise>
											     	</c:choose>										     	
											     </tr>
											     <tr>
											    	<td><fmt:message key="change.summary.new" /></td>
											    	<c:choose>
												     	<c:when test="${conceptModification.realizedIndexRefChange.newValue != null 
												     					and conceptModification.realizedIndexRefChange.newValue != 'no_value' 
												     					and conceptModification.realizedIndexRefChange.newValue != ''}">
												     		${fn:replace(conceptModification.realizedIndexRefChange.newValue, '&amp;diams;', '&diams;')}
												     	</c:when>
												     	<c:otherwise>
												     		<td/><td/><td/><td/><td/><td/>
												     	</c:otherwise>
											     	</c:choose>
											     </tr>											     
											</tbody>
										  </table>
								  	</c:when>
								  	<c:otherwise>
								  		<!-- present nothing -->
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