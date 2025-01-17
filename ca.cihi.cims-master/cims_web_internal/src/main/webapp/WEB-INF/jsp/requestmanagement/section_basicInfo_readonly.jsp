 <%@ include file="/WEB-INF/jsp/common/include.jsp" %>
 
          <div id="basicInfo" class="section"> 
            <div id ="icon_basicInfo" class="left_section">
                   <img id="iconExpandOrCollapse" src='<c:url value="/img/icons/Expand.png" />' alt="Expanded Mode" onclick="javascript:hideShowDiv('icon_basicInfo','div_basicInfo');"/>
            </div> 
           <div  class="right_section">
           <div class="sectionHeader" >Basic Information</div>
          
             <table class="accordion_table">
               <tr>
                 <td ><span class="required">*</span> <span id="categoryLabel"> <b>Request Status:</b> </span> </td>
                 <td >
                     <b>${changeRequestDTO.status.statusCode}</b>
                 </td>
                <td nowrap><span class="required">*</span><span id="baseClassificationLabel"> Classification: </span> </td>
                 <td>
                   <form:select  path="baseClassification" disabled="true">
    				   <c:forEach var="baseClassification" items="${baseClassifications}">
  	  	                  <form:option value="${baseClassification}"> ${baseClassification}  </form:option>
  	  	               </c:forEach>
    			   </form:select>
    			   <form:hidden path="baseClassification"/>
    		     </td>
			    <td ><span class="required">*</span> <span id="categoryLabel"> Request Category: </span> </td>
                 <td >
                     <form:select path="category" id="category"  disabled="true">
                        <form:option value=""></form:option>
    					<form:option value="T" onclick="javascript:selectBilingual();">Tabular List</form:option>
    					<form:option value="I" onclick="javascript:disableBilingual();">Index</form:option>
    					<form:option value="S" onclick="javascript:disableBilingual();">Supplements</form:option>
    			     </form:select>
    			     <form:hidden path="category"/>
                 </td>
              </tr>   
			  <tr>  
			     <td><span class="required">*</span> Year: </td>
                 <td>
                    <form:select path="baseContextId" id="baseContextId"  disabled="true" >
    					 <c:forEach var="contextIdentifier" items="${contextIdentifiers}">
  	  	                  <form:option value="${contextIdentifier.contextId}"> ${contextIdentifier.versionCode} </form:option>
  	  	               </c:forEach>
    			    </form:select>
    			     <form:hidden path="baseContextId"/>
			     </td>
			     <td ><span class="required">*</span><span id="languageCodeLabel">  Language: </span>   </td>
                 <td>
                   <form:select path="languageCode" id="languageCode" disabled="true">
    					<form:option value="ENG" >English</form:option>
    					<form:option value="FRA">French</form:option>
    					<form:option value="ALL">English & French</form:option>
    			   </form:select>
    			   <form:hidden path="languageCode"/>
			    </td>
			    <td >&nbsp; </td>
                <td >&nbsp; </td>
			 </tr>
             
              <tr>
                 <td ><span class="required">*</span> <span id="nameLabel"> Request Name: </span> </td>
                 <td colspan="5">
                 
                 <c:choose>
                    <c:when test="${changeRequestDTO.status.statusCode == 'Closed-Approved'  or changeRequestDTO.status.statusCode == 'Rejected' or changeRequestDTO.status.statusCode == 'Closed-Deferred'}" > 
                            <form:input path="name" id="name" size="120"  readonly="true" cssClass="textGray"/>
                    </c:when>
  	               <c:otherwise>
                           <form:input path="name" id="name" size="120"/>
  					</c:otherwise>
     
				</c:choose>
                       
                 </td>
              </tr>   
              <tr>
               
                 <td nowrap ><span class="required">*</span> Nature of Change:  </td>
                 <td >
                     <form:select path="changeNatureId" id="changeNatureId" disabled="true" >
                        <c:forEach var="changeNature" items="${changeNatures}">
                          <form:option value="${changeNature.auxTableValueId}"> ${changeNature.auxEngLable}  </form:option>
  	  	               </c:forEach>
    				 </form:select>
    				 <form:hidden path="changeNatureId"/>
                 </td>
                 <td nowrap><span class="required">*</span> Type of Change:  </td>
                 <td >
                    <form:select path="changeTypeId" id="changeTypeId"  disabled="true">
                        <c:forEach var="changeType" items="${changeTypes}">
                          <form:option value="${changeType.auxTableValueId}"> ${changeType.auxEngLable}  </form:option>
  	  	               </c:forEach>
    				 </form:select>
    				 <form:hidden path="changeTypeId"/>
                 </td>
                 <td nowrap><span class="required">*</span> Requestor:  </td>
                 <td >
                      <form:select path="requestorId" id="requestorId" disabled="true" >
                        <c:forEach var="requestor" items="${requestors}">
                          <form:option value="${requestor.auxTableValueId}"> ${requestor.auxEngLable}  </form:option>
  	  	               </c:forEach>
    				 </form:select>
    				 <form:hidden path="requestorId"/>
                 </td>
              </tr> 
            
              <tr>
                <td class="alignTop"><span class="required">*</span> <span id="categoryLabel"> Review Groups: </span> </td>
                 <td >
                   <select multiple="multiple" id='lstBox1' disabled="true">
                      <c:forEach var="reviewGroup" items="${reviewGroups}">
                          <option value="${reviewGroup.distributionlistid}"> ${reviewGroup.name}  </option>
  	  	               </c:forEach>
                   </select>
                 </td>
                 <td style='width:50px;text-align:left;vertical-align:middle;'>
                           <input type='button' id='btnRight' value ='  >>  ' disabled/>
                      <br/><input type='button' id='btnLeft' value ='  <<  ' disabled/>
                  </td>
                 <td >
           
                 <form:select multiple="true" id="lstBox2" path="reviewGroups" cssClass="textGray">
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
                   <form:checkbox path="indexRequired" disabled="true"/> Index Required &nbsp;
                     <form:hidden path="indexRequired"/>
                   <form:checkbox path="evolutionRequired" disabled="true"/> Evolution Required &nbsp;
                   <form:hidden path="evolutionRequired"/>
                   <form:checkbox path="conversionRequired" disabled="true"/> Conversion Required &nbsp;
                   <form:hidden path="conversionRequired"/>
                   <form:checkbox path="patternChange" disabled="true"/> Pattern Change &nbsp;&nbsp;&nbsp;
                   <form:hidden path="patternChange"/>
                   <div id="patternChangeTopic">
                      Pattern Topic: <form:input path="patternTopic" size="40" maxlength="150" readonly="true" cssClass="textGray" />
                   </div> 
                </td>
               
              </tr> 
            </table>
             <div id="div_evolutionInfo" class="right_section">
               <span class="required">*</span> Evolution Codes :  
                  	<form:textarea path="evolutionInfo.evolutionCodes" rows="3"  readonly="true"/>
			    <script>
    			   CKEDITOR.replace( 'evolutionInfo.evolutionCodes',{readOnly :  true});
    	        </script>
    	        <br/>
    	      <span class="required">*</span> Evolution English Comments :    
                  	<form:textarea path="evolutionInfo.evolutionTextEng" rows="3" readonly="true" />
			    <script>
    			   CKEDITOR.replace( 'evolutionInfo.evolutionTextEng',{readOnly :  true} );
    	        </script>
    	       <br> 
    	      <span class="required">*</span> Evolution French Comments :    
                  	<form:textarea path="evolutionInfo.evolutionTextFra" rows="3" readonly="true" />
			    <script>
    			   CKEDITOR.replace( 'evolutionInfo.evolutionTextFra',{readOnly :  true} );
    	        </script>
    	        
			 </div>
            
            
          </div>
        </div>
