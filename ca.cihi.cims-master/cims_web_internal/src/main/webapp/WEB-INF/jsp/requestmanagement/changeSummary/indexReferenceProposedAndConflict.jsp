<!DOCTYPE html>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<jsp:include page="../../common/common-header.jsp"/>

<html>
<h4 style="text-align: center;">Proposed and Conflict Index References and Symbols </h4>
                   <div class="right_section">			   
			     		<div><fmt:message key="change.summary.indexTerm" />: ${conceptModification.code}<br/>	
			     		     <fmt:message key="change.summary.breadCrumbs" />: ${conceptModification.breadCrumbs}
			     		</div>		 
			 		</div>			
                     <fmt:message key="change.summary.referencesAndSymbols" />	
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



</html>