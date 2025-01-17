
 <%@ include file="/WEB-INF/jsp/common/include.jsp" %>
      <div id="basicInfo" class="section"> 
            <div id ="icon_basicInfo" class="left_section">
                   <img id="iconExpandOrCollapse" src='<c:url value="/img/icons/Expand.png" />' alt="Expanded Mode"  onclick="javascript:hideShowDiv('icon_basicInfo','div_basicInfo');"/>
            </div> 
           <div  class="right_section">
             <div class="sectionHeader" >Basic Information</div>
            </div> 
            <div  id="div_basicInfo" class="right_section">
             <table class="accordion_table">
               <tr>
                
                 <td ><span class="required">*</span> <span id="categoryLabel"><b> Request Status:</b> </span> </td>
                 <td >
                   <b> ${changeRequestDTO.status.statusCode} </b>
                 </td>
                
			    <td nowrap><span class="required">*</span><span id="baseClassificationLabel"> Classification: </span> </td>
                 <td>
                   <c:choose>
                     <c:when test="${changeRequestDTO.status.statusCode =='New' || changeRequestDTO.status.statusCode=='Closed-Deferred' || changeRequestDTO.status.statusCode=='Rejected'}">
                       <form:select  path="baseClassification"  onchange="javascript:checkDivUrcDocumentsAndCallVersionList();" >
    				     <c:forEach var="baseClassification" items="${baseClassifications}">
  	  	                    <form:option value="${baseClassification}"> ${baseClassification}  </form:option>
  	  	                  </c:forEach>
    			        </form:select>
    			     </c:when>
                    <c:otherwise>
    			      <form:select  path="baseClassification"  disabled="true" >
    				   <c:forEach var="baseClassification" items="${baseClassifications}">
  	  	                  <form:option value="${baseClassification}"> ${baseClassification}  </form:option>
  	  	               </c:forEach>
    			     </form:select>
    			      <form:hidden path="baseClassification"/>
    			    </c:otherwise>
                </c:choose>
    			
    			   
			     </td>
			     <td ><span class="required">*</span> <span id="categoryLabel"> Request Category: </span> </td>
                 <td >
                     <c:choose>
                       <c:when test="${changeRequestDTO.status.statusCode =='New' || changeRequestDTO.status.statusCode=='Closed-Deferred' || changeRequestDTO.status.statusCode=='Rejected'}">
                        <form:select path="category" id="category"  >
                         <form:option value=""></form:option>
    					<form:option value="T" onclick="javascript:selectBilingual();">Tabular List</form:option>
    					<form:option value="I" onclick="javascript:disableBilingual();">Index</form:option>
    					<form:option value="S" onclick="javascript:disableBilingual();">Supplements</form:option>
    			       </form:select>
    			       </c:when>
                    <c:otherwise>
    			      <form:select path="category" id="category" disabled="true"  >
                        <form:option value=""></form:option>
    					<form:option value="T" onclick="javascript:selectBilingual();">Tabular List</form:option>
    					<form:option value="I" onclick="javascript:disableBilingual();">Index</form:option>
    					<form:option value="S" onclick="javascript:disableBilingual();">Supplements</form:option>
    			     </form:select>
    			      <form:hidden path="category"/>
    			    </c:otherwise>
                </c:choose>
                 </td>
			  </tr>
              <tr>
               <td><span class="required">*</span> Year: </td>
                 <td>
                   <c:choose>
                       <c:when test="${changeRequestDTO.status.statusCode =='New' || changeRequestDTO.status.statusCode=='Closed-Deferred' || changeRequestDTO.status.statusCode=='Rejected'}">
                    <form:select path="baseContextId" id="baseContextId" onchange="javascript:refreshDeferDropDown();">
    					 <c:forEach var="contextIdentifier" items="${contextIdentifiers}">
  	  	                   <form:option value="${contextIdentifier.contextId}"> ${contextIdentifier.versionCode} </form:option>
  	  	                </c:forEach>
    			    </form:select>
    			       </c:when>
                    <c:otherwise> 
                     <form:select path="baseContextId" id="baseContextId" disabled="true" >
    					 <c:forEach var="contextIdentifier" items="${contextIdentifiers}">
  	  	                   <form:option value="${contextIdentifier.contextId}"> ${contextIdentifier.versionCode} </form:option>
  	  	                </c:forEach>
    			    </form:select>
    			      <form:hidden path="baseContextId"/>
    			    </c:otherwise>
                </c:choose>
    			    
			     </td>
			     <td ><span class="required">*</span><span id="languageCodeLabel">  Language: </span>   </td>
                 <td>
                    <c:choose>
                       <c:when test="${changeRequestDTO.status.statusCode =='New' || changeRequestDTO.status.statusCode=='Closed-Deferred' || changeRequestDTO.status.statusCode=='Rejected'}">
                     <form:select path="languageCode" id="languageCode"  >
    					<form:option value="ENG">English</form:option>
    					<form:option value="FRA">French</form:option>
    					<form:option value="ALL">English & French</form:option>
    			   </form:select>
    			      </c:when>
                    <c:otherwise> 
                         <form:select path="languageCode" id="languageCode" disabled="true" >
    					<form:option value="ENG">English</form:option>
    					<form:option value="FRA">French</form:option>
    					<form:option value="ALL">English & French</form:option>
    			   </form:select>
                          <form:hidden path="languageCode"/>
                    </c:otherwise>
                </c:choose>
    			   
			    </td>
			    <td >&nbsp; </td>
                <td >&nbsp; </td>
              </tr>
              
              <tr>
                 <td ><span class="required">*</span> <span id="nameLabel">Request Name: </span> </td>
                 <td colspan="5">
                    <form:input path="name" id="name" size="120"/>
                 </td>
              </tr>   
              <tr>
                 <td nowrap ><span class="required">*</span> Nature of Change:  </td>
                 <td >
                     <form:select path="changeNatureId" id="changeNatureId"  >
                          <form:option value=""></form:option>
                        <c:forEach var="changeNature" items="${changeNatures}">
                          <form:option value="${changeNature.auxTableValueId}"> ${changeNature.auxEngLable}  </form:option>
  	  	               </c:forEach>
    				 </form:select>
                 </td>
                 <td nowrap><span class="required">*</span> Type of Change:  </td>
                 <td >
                    <form:select path="changeTypeId" id="changeTypeId"  >
                          <form:option value=""></form:option>
                        <c:forEach var="changeType" items="${changeTypes}">
                          <form:option value="${changeType.auxTableValueId}"> ${changeType.auxEngLable}  </form:option>
  	  	               </c:forEach>
    				 </form:select>
                 </td>
                 <td nowrap><span class="required">*</span> Requestor:  </td>
                 <td >
                      <form:select path="requestorId" id="requestorId"  >
                          <form:option value=""></form:option>
                        <c:forEach var="requestor" items="${requestors}">
                          <form:option value="${requestor.auxTableValueId}"> ${requestor.auxEngLable}  </form:option>
  	  	               </c:forEach>
    				 </form:select>
                 </td>
              </tr> 
              <tr>
                <td class="alignTop"><span class="required">*</span> <span id="categoryLabel"> Review Groups: </span> </td>
                 <td >
                   <select multiple="multiple" id='lstBox1'>
                      <c:forEach var="reviewGroup" items="${reviewGroups}">
                          <option value="${reviewGroup.distributionlistid}"> ${reviewGroup.name}  </option>
  	  	               </c:forEach>
                   </select>
                 </td>
                 <td style='width:50px;text-align:left;vertical-align:middle;'>
                           <input type='button' id='btnRight' value ='  >>  '/>
                      <br/><input type='button' id='btnLeft' value ='  <<  '/>
                  </td>
                 <td >
           
                 <form:select multiple="true" id="lstBox2" path="reviewGroups"  >
                       <form:option value="9"  disabled="true"> DL-Classification </form:option>
                       <c:forEach var="selectedReviewGroup" items="${selectedReviewGroups}">
                          <form:option value="${selectedReviewGroup.distributionlistid}" > ${selectedReviewGroup.name}  </form:option>
  	  	               </c:forEach>
                 </form:select>
                 
                 </td>
                 <td >&nbsp; </td>
                 <td >
                    &nbsp; 
                 </td>
              </tr> 
              <tr>
                <td colspan="6">
                   <form:checkbox path="indexRequired"/> Index Required &nbsp; 
                   <form:checkbox path="evolutionRequired" id ="evolutionRequired" onchange="javascript:hideShowEvolutionInfo();"/> Evolution Required &nbsp;
                   <form:checkbox path="conversionRequired"/> Conversion Required &nbsp;
                   <form:checkbox path="patternChange" id ="patternChange" onchange="javascript:hideShowPatternTopic();"/> Pattern Change &nbsp;&nbsp;&nbsp;
                    <div id="patternChangeTopic">
                      <span class="required">*</span> Pattern Topic: <form:input path="patternTopic" size="40" maxlength="150"/>
                   </div> 
                </td>
               
              </tr> 
            </table>
             <div id="div_evolutionInfo" class="right_section">
               <span class="required">*</span>  Evolution Codes :    
                  	<form:textarea path="evolutionInfo.evolutionCodes" rows="3" />
			    <script>
    			   CKEDITOR.replace( 'evolutionInfo.evolutionCodes',{toolbarStartupExpanded : false} );
    	        </script>
    	        <br/>
    	        <span class="required">*</span> Evolution English Comments :   
                  	<form:textarea path="evolutionInfo.evolutionTextEng" rows="3" />
			    <script>
    			   CKEDITOR.replace( 'evolutionInfo.evolutionTextEng',{toolbarStartupExpanded : false} );
    	        </script>
    	        <br/>
    	        <span class="required">*</span> Evolution French Comments :     
                  	<form:textarea path="evolutionInfo.evolutionTextFra" rows="3" />
			    <script>
    			   CKEDITOR.replace( 'evolutionInfo.evolutionTextFra',{toolbarStartupExpanded : false} );
    	        </script>
    	        
			 </div>
            
          </div>
        </div>
