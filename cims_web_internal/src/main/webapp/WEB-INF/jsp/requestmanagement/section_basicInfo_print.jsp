 <%@ include file="/WEB-INF/jsp/common/include.jsp" %>
 
          <div id="basicInfo" class="section"> 
            
           <div>
           <div class="sectionHeader" ><span class="section-header">Basic Information</span></div>
          
             <table class="accordion_table">
               <tr>
               	<td class="alignTop label"><span id="languageCodeLabel">  Language: </span>   </td>
               	<td class="alignTop">
                   <c:if test="${changeRequestDTO.languageCode eq 'ENG'}">English</c:if>
                     <c:if test="${changeRequestDTO.languageCode eq 'FRA'}">French</c:if>
                     <c:if test="${changeRequestDTO.languageCode eq 'ALL'}">English & French</c:if>
			    </td>
                <td class="alignTop label"><span id="baseClassificationLabel"> Classification: </span> </td>
                 <td class="alignTop">
                   ${changeRequestDTO.baseClassification}
    		     </td>
    		     <td class="alignTop label"> Year: </td>
                 <td class="alignTop">
                    ${changeRequestDTO.baseVersionCode}
			     </td>
              </tr>  
              <tr>
                 <td class="alignTop label"> <span id="nameLabel"> Request Name: </span> </td>
                 <td colspan="5" class="alignTop">
                    ${changeRequestDTO.name}
                 </td>
              </tr> 
			  <tr>  
			     
			    <td class="alignTop label"> <span id="categoryLabel"> Request Category: </span> </td>
                 <td class="alignTop">
                     <c:if test="${changeRequestDTO.category eq 'T'}">Tabular List</c:if>
                     <c:if test="${changeRequestDTO.category eq 'I'}">Index</c:if>
                     <c:if test="${changeRequestDTO.category eq 'S'}">Supplements</c:if>
                 </td>
			     <td class="alignTop label"> Nature of Change:  </td>
                 <td class="alignTop">
                     ${changeRequestDTO.changeNature.auxEngLable}
                 </td>
                 <td class="alignTop label"> Type of Change:  </td>
                 <td class="alignTop">
                    ${changeRequestDTO.changeType.auxEngLable}
                 </td>
			 </tr>
             
                 
              <tr>
               	
                 <td class="alignTop label"> <span id="categoryLabel"> Request Status: </span> </td>
                 <td class="alignTop">
                     ${changeRequestDTO.status.statusCode}
                 </td>
                 
                 <td class="alignTop label"> Requestor:  </td>
                 <td class="alignTop">
                      ${changeRequestDTO.requestor.auxEngLable}
                 </td>
                 <td>&nbsp;</td>
                 <td>&nbsp;</td>
              </tr> 
            
              <tr>
                <td class="alignTop label"> <span id="categoryLabel"> Review Groups: </span> </td>
                 <td colspan="5" class="alignTop">
                 	<c:forEach var="selectedReviewGroup" items="${changeRequestDTO.reviewGroups}" varStatus="counter">
                 		<c:if test="${counter.count > 1}">, </c:if>${selectedReviewGroup.name}
                 	</c:forEach>
                 </td>
                 
              </tr> 
              <tr>
                <td colspan="6" class="alignTop">
                <span class="label">Conversion Required:</span> <c:if test="${!changeRequestDTO.conversionRequired}">No&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</c:if><c:if test="${changeRequestDTO.conversionRequired}">Yes&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
                <span class="label">Evolution Required:</span> <c:if test="${!changeRequestDTO.evolutionRequired}">No&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</c:if><c:if test="${changeRequestDTO.evolutionRequired}">Yes&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
				<span class="label">Index Required:</span> <c:if test="${!changeRequestDTO.indexRequired}">No&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</c:if><c:if test="${changeRequestDTO.indexRequired}">Yes&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
                <span class="label">Pattern Change Required:</span> <c:if test="${!changeRequestDTO.patternChange}">No&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</c:if><c:if test="${changeRequestDTO.patternChange}">Yes&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="label">Pattern Topic:</span> ${changeRequestDTO.patternTopic }</c:if>
                </td>
              </tr>
              <c:if test="${changeRequestDTO.evolutionRequired}">
              <tr>
                <td class="alignTop label">Evolution Codes :</td>
                 <td colspan="5" class="alignTop">
                 	${changeRequestDTO.evolutionInfo.evolutionCodes}
                 </td>
              </tr>  
              <tr>
                <td class="alignTop label">Evolution English Comments :</td>
                 <td colspan="5" class="alignTop">
                 	${changeRequestDTO.evolutionInfo.evolutionTextEng}
                 </td>
              </tr>
              <tr>
                <td class="alignTop label">Evolution French Comments :</td>
                 <td colspan="5" class="alignTop">
                 	${changeRequestDTO.evolutionInfo.evolutionTextFra}
                 </td>
              </tr>  
              </c:if>
            </table>
            
          </div>
        </div>
