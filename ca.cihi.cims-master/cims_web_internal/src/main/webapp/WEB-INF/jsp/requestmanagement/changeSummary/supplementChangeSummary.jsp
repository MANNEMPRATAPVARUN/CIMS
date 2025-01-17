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
	            	<input type="button"  value="Print" class="button" onclick="javascript:printSupplementChangeSummary(${changeRequestDTO.changeRequestId});" >&nbsp;&nbsp;
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
		<c:if test='${isIncomplete }'><span style="color: #ff0000; font-weight: bold;"><fmt:message key="change.summary.incomplete.message"/></span>  </c:if><a href="javascript:popupIncompleteReport('viewSupplementIncompleteReport.htm?changeRequestId=${changeRequestDTO.changeRequestId}')"><fmt:message key="change.summary.incompleteReport" /></a>&nbsp;&nbsp;
		 <c:if test='${cf:hasWriteAccess(currentUser,"SECTION_CHANGE_REQUEST_BASIC")}'>
		  <a href="javascript:popupResolveConflicts('resolveSupplementConflicts.htm?changeRequestId=${changeRequestDTO.changeRequestId}');" ><fmt:message key="change.summary.resolveConflicts" /></a>
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
			     		<div><fmt:message key="change.summary.supplement" />: ${conceptModification.code}</div>
			     		<fmt:message key="change.summary.breadCrumbs" />: <fmt:message key="change.summary.breadCrumbsRoot" /><c:if test="${conceptModification.breadCrumbs != ''}"> &gt; </c:if>${conceptModification.breadCrumbs}     		 
			 		</div>			 		
			 		<div id="right" class="right_section">	
			 		   	<c:if test="${fn:length(conceptModification.proposedSupplementChanges) gt 0}">	 		     
			 		    	<div class="sectionHeader"><fmt:message key="change.summary.proposedChanges" /></div>
			 		    </c:if>
			 		    <c:if test="${fn:length(conceptModification.proposedSupplementChanges) gt 0}">	 		
						 	<table id="proposedSupplementChanges" style="width: 100%; margin-top: 10px;" class="listTable">
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
									    <c:forEach var="proposedChange" items="${conceptModification.proposedSupplementChanges}" varStatus="status">
										     <tr class="${status.index%2==0 ? 'even' : 'odd'}">												
												<c:choose>										
													<c:when test="${proposedChange.tableName=='XMLPropertyVersion'}">
													    <td><fmt:message key="change.summary.supplement.markup"/></td>
													    <td>
													    	<c:if test="${proposedChange.oldValue != null and proposedChange.oldValue != 'no_value' and proposedChange.oldValue != ''}">
													    		<a href="javascript:popupXmlProperty1('viewXmlProperty.htm?xmlPropertyId=${proposedChange.oldValue}&category=Proposed%20Change&fieldName=Old%20${proposedChange.fieldName}&code=${conceptModification.code}');"> <fmt:message key="change.summary.old" /> <fmt:message key="change.summary.supplement.markup"/></a>
													        </c:if>
													    </td>													    
														<td>
														   <c:if test="${proposedChange.proposedValue != null and proposedChange.proposedValue != 'no_value' and proposedChange.proposedValue != ''}">
														    <a href="javascript:popupXmlProperty2('viewXmlProperty.htm?xmlPropertyId=${proposedChange.proposedValue}&category=Proposed%20Change&fieldName=Proposed%20${proposedChange.fieldName}&code=${conceptModification.code}');"><fmt:message key="change.summary.proposed" /> <fmt:message key="change.summary.supplement.markup"/></a>
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
														  	     <a href="javascript:popupXmlProperty3('viewXmlProperty.htm?xmlPropertyId=${proposedChange.conflictValue}&category=Proposed%20Change&fieldName=Conflict%20${proposedChange.fieldName}&code=${conceptModification.code}');"><fmt:message key="change.summary.conflict" /> <fmt:message key="change.summary.supplement.markup"/></a>
														  	</c:otherwise>
														  </c:choose>													
													    </td>	
													</c:when>																																						
													<c:otherwise>
													   <td>
														    <c:choose>
														    	<c:when test="${proposedChange.tableName=='NumericPropertyVersion'}">
														    		<fmt:message key="change.summary.supplement.sortOrder"/>
														    	</c:when>
														    	<c:when test="${proposedChange.tableName=='TextPropertyVersion'}">
														    		<fmt:message key="change.summary.supplement.description"/>
														    	</c:when>
														    	<c:when test="${proposedChange.tableName=='ConceptPropertyVersion'}">
														    		<fmt:message key="change.summary.supplement.frontBackMatter"/>
														    	</c:when>
														    	<c:otherwise>
														    	   ${proposedChange.fieldName}
														    	</c:otherwise>
														    </c:choose>	
													    </td>												   
														<td class="original">
															<c:if test="${proposedChange.oldValue != null and proposedChange.oldValue != 'no_value' and proposedChange.oldValue != ''}">
																${proposedChange.oldValue}
															</c:if>
														</td>
														<td class="changed">
															<c:if test="${proposedChange.proposedValue != null and proposedChange.proposedValue != 'no_value' and proposedChange.proposedValue != ''}">
																${proposedChange.proposedValue}
															</c:if>
														</td>
														<c:choose>
															<c:when test="${proposedChange.tableName=='TextPropertyVersion'}">
													    		<td class="diff"></td>
													    	</c:when>
													    	<c:otherwise>
													    		<td></td>
													    	</c:otherwise>
													    </c:choose>
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
						<c:if test="${fn:length(conceptModification.realizedSupplementChanges) gt 0}">	 		     
			 		    	<div class="sectionHeader"><fmt:message key="change.summary.realizedChanges" /></div> 
			 		    </c:if>						
						<c:if test="${fn:length(conceptModification.realizedSupplementChanges) gt 0}">	
							<table id="realizedChanges" style="width: 100%; margin-top: 10px;" class="listTable">
								<thead>
									<tr>
										<th class="tableHeader" style="width:20%;border:1px"><fmt:message key="change.summary.fieldName" /></th>
										<th class="tableHeader" style="width:20%;border:1px"><fmt:message key="change.summary.oldValue" /></th>
										<th class="tableHeader" style="width:30%;border:1px"><fmt:message key="change.summary.newValue" /></th>	
										<th class="tableHeader" style="width:30%;border:1px"><fmt:message key="change.summary.diff" /></th>					
									</tr>
								</thead>
								<tbody>
								    <c:forEach var="realizedChange" items="${conceptModification.realizedSupplementChanges}"  varStatus="status">
									     <tr class="${status.index%2==0 ? 'even' : 'odd'}">
											<c:choose>
												<c:when test="${realizedChange.tableName=='XMLPropertyVersion'}">
												    <td><fmt:message key="change.summary.supplement.markup"/></td>
													<td>
													   <c:if test="${realizedChange.oldValue != null and realizedChange.oldValue != '' and realizedChange.oldValue !='no_value'}">
															<a href="javascript:popupXmlProperty4('viewXmlProperty.htm?xmlPropertyId=${realizedChange.oldValue}&category=Realized%20Change&fieldName=Old%20${realizedChange.fieldName}&code=${conceptModification.code}');"><fmt:message key="change.summary.old" /> <fmt:message key="change.summary.supplement.markup"/></a>
														</c:if>
													</td>
													<td>
													   <c:if test="${realizedChange.newValue != null and realizedChange.newValue != '' and realizedChange.newValue != 'no_value'}">
													   		<a href="javascript:popupXmlProperty5('viewXmlProperty.htm?xmlPropertyId=${realizedChange.newValue}&category=Realized%20Change&fieldName=New%20${realizedChange.fieldName}&code=${conceptModification.code}');"><fmt:message key="change.summary.new" /> <fmt:message key="change.summary.supplement.markup"/></a>
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
												   <td>
												       <c:choose>
													    	<c:when test="${realizedChange.tableName=='NumericPropertyVersion'}">
													    		<fmt:message key="change.summary.supplement.sortOrder"/>
													    	</c:when>
													    	<c:when test="${realizedChange.tableName=='TextPropertyVersion'}">
													    		<fmt:message key="change.summary.supplement.description"/>
													    	</c:when>
													    	<c:when test="${realizedChange.tableName=='ConceptPropertyVersion'}">
														    		<fmt:message key="change.summary.supplement.frontBackMatter"/>
														    	</c:when>
													    	<c:otherwise>
													    	  ${realizedChange.fieldName}
													    	</c:otherwise>
													    </c:choose>	
												    </td>
													<td class="original">
														<c:if test="${realizedChange.oldValue !='no_value'}"> 
															${realizedChange.oldValue}
														</c:if>
													</td>
												    <td class="changed">
												    	<c:if test="${realizedChange.newValue !='no_value'}"> 
															${realizedChange.newValue}
														</c:if>
												    </td>
												    <c:choose>
														<c:when test="${proposedChange.tableName=='TextPropertyVersion'}">
												    		<td class="diff"></td>
												    	</c:when>
												    	<c:otherwise>
												    		<td></td>
												    	</c:otherwise>
												    </c:choose>
												</c:otherwise>
											</c:choose>
										</tr>
								    </c:forEach>																	
								</tbody>
							</table>
						</c:if>								
					</div>
		        </div>
		    </c:forEach>
    	</c:otherwise>
	</c:choose>
</div>