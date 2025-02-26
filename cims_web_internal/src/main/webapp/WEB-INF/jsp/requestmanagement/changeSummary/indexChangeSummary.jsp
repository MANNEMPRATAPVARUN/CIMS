<%@ include file="/WEB-INF/jsp/requestmanagement/changeSummary/changeSummaryHeader.jsp"%>

<div class="content">

    <!-- change request lifecycle buttons begins -->
    <form:form id="changeRequestDTO"  modelAttribute="changeRequestDTO" method="post" >
         <div id="button-header" class="fixed">
           <div class="wrapper">
            <div class="alignRight">
                <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_SEND_BACK")}'>
	              <input type="button"  value="Send Back" class="button" onclick="javascript:openSendBackDialogBox();" >&nbsp;&nbsp; 
	            </c:if>
	            <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_ACCEPT")}'>
	              <input type="button"  value="Accept" class="button" onclick="javascript:acceptChangeRequest();" >&nbsp;&nbsp; 
	            </c:if>	            
	            <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_READY_FOR_REALIZE")}'>
	              <input type="button"  value="Ready For Realization" class="button" onclick="javascript:readyForRealizeChangeRequest();" >&nbsp;&nbsp; 
	            </c:if>
	         
	            <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_READY_FOR_VALIDATION")}'>
	              <input type="button"  value="Ready For Validation" class="button" onclick="javascript:readyForValidationChangeRequest();" >&nbsp;&nbsp; 
	            </c:if>
	            <c:if test="${isOldestOpenContext}"> 
	               <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_REALIZE")}'>
	                  <input type="button"  value="Realize" class="button" onclick="javascript:realizeChangeRequest();" >&nbsp;&nbsp; 
	               </c:if>
	            </c:if>
	            <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_QA_DONE")}'>
	              <input type="button"  value="QA Done" class="button" onclick="javascript:qaDoneChangeRequest();" >&nbsp;&nbsp; 
	            </c:if>
	             <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_APPROVE")}'>
	              <input type="button"  value="Approve" class="button" onclick="javascript:approveChangeRequest();" >&nbsp;&nbsp; 
	            </c:if>
	            	<input type="button"  value="Print" class="button" onclick="javascript:printIndexChangeSummary(${changeRequestDTO.changeRequestId});" >&nbsp;&nbsp;
	       </div>
         </div>
       </div>
       
          <form:hidden path="changeRequestId"/>
          <form:hidden path="baseVersionCode"/>
          <form:hidden path="status"/>
          <form:hidden path="category"/>
          <form:hidden path="lastUpdatedTime"/>
          <form:hidden path="rationaleForIncomplete" id="rational"/>
        
          
        <c:if test="${updatedSuccessfully}"> 
        <div id="updatedSuccessfully" class="success" >
             Changes have been updated successfully. 
        </div>
        </c:if>
        <c:if test="${errorMsg!=null}"> 
           <div id="errorMsg" class="error" >
              ${errorMsg} 
           </div>
        </c:if>   
        
       <c:if test="${runningRealization!=null}"> 
         <div id="runningRealization" class="info" >
             The realization for change request - ${runningRealization.changeRequestId} is running, Please wait for a few minutes and click the realize button again.
        </div>
        </c:if>
        <c:if test="${realization!=null}"> 
           <div id="currentRealization" class="info" >
               <c:if test="${realization.realizationStatus.statusCode=='PROCESS_ENDS_SUCCEED'}"> 
                 All classification changes within this change request up to this point have been successfully realized.
               </c:if>
              
               <c:if test="${not empty realization.failedReason}"> 
                 ${realization.failedReason}
               </c:if>
           </div>
        </c:if>

        <div id="concurrentError" class="error" style="display:none;">
        </div>
       
      
       <div id="progressStep" style="display: none;">
         <h2 class="pageTitle">Now processing</h2>
            <div id="theStep">
                <div id="progressStepBox">
                    <div id="progressStepBoxContent"></div>
                </div>
                 <div id="progressStepText" class="success"></div>
            </div>
      </div>
      
     
   </form:form>     	
      	
  <!-- change request lifecycle buttons ends -->

		<div id="send_back_dialog" class="dialog_box" title="Send Back Change Request - ${changeRequestDTO.changeRequestId}">
              
               Please specify the following
               <table class="accordion_table">
                    
                   <tr>
                         <td valign="top"><span class="required">*</span> Message :  </td>
                         <td >
                            <textarea id="rationaleForIncomplete"  maxlength="200" cssStyle=" height:60px; width:300px; white-space: normal; overflow-y: scroll; overflow-x: hidden;"></textarea>
                             
    				      </td>
                     </tr>
                </table>     
        </div>
      	<c:if test='${changeRequestDTO.changeSummary.failedRealization}'>         
               <span style="color: #ff0000; font-weight: bold;">   
                 <fmt:message  key="change.summary.errorMessage.failedRealization" /> 
               </span>  
          </c:if>	 
		<div id="summaryTitle"><fmt:message key="manage.change.request.name" />:  ${changeRequestDTO.name}</div>
		<div id="reportLink" align="right">
		<c:if test='${isIncomplete }'><span style="color: #ff0000; font-weight: bold;"><fmt:message key="change.summary.incomplete.message"/></span>  </c:if><a href="javascript:popupIncompleteReport('viewIndexIncompleteReport.htm?changeRequestId=${changeRequestDTO.changeRequestId}')"><fmt:message key="change.summary.incompleteReport" /></a>&nbsp;&nbsp;
		 <c:if test='${cf:hasWriteAccess(currentUser,"SECTION_CHANGE_REQUEST_BASIC")}'>
		  <a href="javascript:popupResolveConflicts('resolveIndexConflicts.htm?changeRequestId=${changeRequestDTO.changeRequestId}');" ><fmt:message key="change.summary.resolveConflicts" /></a>
	     </c:if> 
		</div> 
	
    <div class="sectionHeader" align="center"><fmt:message key="change.summary.modificationSummary" /></div>
    <c:choose>
    	<c:when test="${changeRequestDTO.changeSummary.noChange}">
    	    <fmt:message  key="change.summary.noChange" />
    	</c:when>
    	<c:otherwise>    	
		    <c:forEach var="conceptModification" items="${changeRequestDTO.changeSummary.conceptModifications}">
		    	<div class="section">
			    	<div id ="left" class="left_section">
						<img src='<c:url value="/img/icons/Expand.png" />' alt="Expanded Mode" onclick="javascript:hideShowDiv(this);"/>
					</div>
					<div class="right_section">			   
			     		<div><fmt:message key="change.summary.indexTerm" />: ${conceptModification.code}<br/>	
			     		     <fmt:message key="change.summary.breadCrumbs" />: ${conceptModification.breadCrumbs}
			     		</div>		 
			 		</div>			 		
			 		<div id="right" class="center_section" >	
			 		   	<c:if test="${fn:length(conceptModification.proposedIndexChanges) gt 0 or conceptModification.proposedIndexRefChange != null}">	 		     
			 		    	<div class="sectionHeader"><fmt:message key="change.summary.proposedChanges" /></div>
			 		    </c:if>
			 		    <c:if test="${fn:length(conceptModification.proposedIndexChanges) gt 0}">	 		
						 	<table id="proposedIndexChanges" style="width: 100%; margin-top: 10px;" class="listTable">
									<thead>
										<tr>
											<th class="tableHeader" style="width:20%;border:1px"><fmt:message key="change.summary.fieldName" /></th>								
											<th class="tableHeader" style="width:20%;border:1px"><fmt:message key="change.summary.oldValue" /></th>
											<th class="tableHeader" style="width:20%;border:1px"><fmt:message key="change.summary.proposedValue" /></th>
											<th class="tableHeader" style="width:20%;border:1px"><fmt:message key="change.summary.diff" /></th>
											<th class="tableHeader" style="width:20%;border:1px"><fmt:message key="change.summary.conflictValue" /></th>						
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
																<c:when test="${proposedChange.oldValue != null and proposedChange.oldValue != 'no_value' and proposedChange.oldValue != ''}">
																	<c:choose>
																		<c:when test="${proposedChange.proposedValue != null and proposedChange.proposedValue != 'no_value' and proposedChange.proposedValue != ''}">
																			<a href="javascript:popupXmlProperty6('viewXmlPropertyDiff.htm?xmlPropertyIdOriginal=${proposedChange.oldValue}&xmlPropertyIdChanged=${proposedChange.proposedValue}&category=Proposed%20Change&fieldName=Proposed%20${proposedChange.fieldName}&code=${conceptModification.code}');"><fmt:message key="change.summary.diff" /> ${proposedChange.fieldName}</a>
																		</c:when>
																		<c:otherwise>
																			<a href="javascript:popupXmlProperty6('viewXmlPropertyDiff.htm?xmlPropertyIdOriginal=${proposedChange.oldValue}&xmlPropertyIdChanged=0&category=Proposed%20Change&fieldName=Proposed%20${proposedChange.fieldName}&code=${conceptModification.code}');"><fmt:message key="change.summary.diff" /> ${proposedChange.fieldName}</a>
																		</c:otherwise>
																	</c:choose>
																</c:when>
																<c:otherwise>
																	<c:choose>
																		<c:when test="${proposedChange.proposedValue != null and proposedChange.proposedValue != 'no_value' and proposedChange.proposedValue != ''}">
																			<a href="javascript:popupXmlProperty6('viewXmlPropertyDiff.htm?xmlPropertyIdOriginal=0&xmlPropertyIdChanged=${proposedChange.proposedValue}&category=Proposed%20Change&fieldName=Proposed%20${proposedChange.fieldName}&code=${conceptModification.code}');"><fmt:message key="change.summary.diff" /> ${proposedChange.fieldName}</a>
																		</c:when>
																		<c:otherwise>
																		</c:otherwise>
																	</c:choose>
																</c:otherwise>
															</c:choose>
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
														<td class="original">
															<c:if test="${proposedChange.oldValue != null and proposedChange.oldValue != 'no_value' and proposedChange.oldValue != ''}">
																${proposedChange.oldValue}
															</c:if>
														</td>
														<td  class="changed">
															<c:if test="${proposedChange.proposedValue != null and proposedChange.proposedValue != 'no_value' and proposedChange.proposedValue != ''}">
																${proposedChange.proposedValue}
															</c:if>
														</td>
														<td class="diff"></td>
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
						
						<c:if test="${conceptModification.proposedIndexRefChange != null}">	 		
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
												     		${conceptModification.proposedIndexRefChange.proposedValue}
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
												     		<td/><td/><td/><td/><td/><td/><td/>
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
												     		<td/><td/><td/><td/><td/><td/><td/>
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
										<th class="tableHeader" style="width:20%;border:1px"><fmt:message key="change.summary.fieldName" /></th>
										<th class="tableHeader" style="width:20%;border:1px"><fmt:message key="change.summary.oldValue" /></th>
										<th class="tableHeader" style="width:30%;border:1px"><fmt:message key="change.summary.newValue" /></th>		
										<th class="tableHeader" style="width:30%;border:1px"><fmt:message key="change.summary.diff" /></th>					
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
													<td>
														<c:choose>
															<c:when test="${realizedChange.oldValue != null and realizedChange.oldValue != 'no_value' and realizedChange.oldValue != ''}">
																<c:choose>
																	<c:when test="${realizedChange.newValue != null and realizedChange.newValue != 'no_value' and realizedChange.newValue != ''}">
																		<a href="javascript:popupXmlProperty6('viewXmlPropertyDiff.htm?xmlPropertyIdOriginal=${realizedChange.oldValue}&xmlPropertyIdChanged=${realizedChange.newValue}&category=Proposed%20Change&fieldName=Proposed%20${realizedChange.fieldName}&code=${conceptModification.code}');"><fmt:message key="change.summary.diff" /> ${realizedChange.fieldName}</a>
																	</c:when>
																	<c:otherwise>
																		<a href="javascript:popupXmlProperty6('viewXmlPropertyDiff.htm?xmlPropertyIdOriginal=${realizedChange.oldValue}&xmlPropertyIdChanged=0&category=Proposed%20Change&fieldName=Proposed%20${realizedChange.fieldName}&code=${conceptModification.code}');"><fmt:message key="change.summary.diff" /> ${realizedChange.fieldName}</a>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${realizedChange.newValue != null and realizedChange.newValue != 'no_value' and realizedChange.newValue != ''}">
																		<a href="javascript:popupXmlProperty6('viewXmlPropertyDiff.htm?xmlPropertyIdOriginal=0&xmlPropertyIdChanged=${realizedChange.newValue}&category=Proposed%20Change&fieldName=Proposed%20${realizedChange.fieldName}&code=${conceptModification.code}');"><fmt:message key="change.summary.diff" /> ${realizedChange.fieldName}</a>
																	</c:when>
																	<c:otherwise>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</td>											
												</c:when>												
												<c:otherwise>
													<td class="original">
														<c:if test="${realizedChange.oldValue != null and realizedChange.oldValue != '' and realizedChange.oldValue !='no_value'}"> 
															${realizedChange.oldValue}
														</c:if>
													</td>
												    <td class="changed">
												    	<c:if test="${realizedChange.newValue != null and realizedChange.newValue != '' and realizedChange.newValue != 'no_value'}"> 
															${realizedChange.newValue}
														</c:if>
												    </td class="diff">
												    <td></td>
												</c:otherwise>
											</c:choose>
										</tr>
								    </c:forEach>													
								</tbody>
							</table>
						</c:if>
						<c:if test="${conceptModification.realizedIndexRefChange != null}">	 		
						 		<fmt:message key="change.summary.referencesAndSymbols" />							       					       
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
												     		${conceptModification.realizedIndexRefChange.newValue}
												     	</c:when>
												     	<c:otherwise>
												     		<td/><td/><td/><td/><td/><td/>
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
												     		<td/><td/><td/><td/><td/><td/><td/>
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
												     		<td/><td/><td/><td/><td/><td/><td/>
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