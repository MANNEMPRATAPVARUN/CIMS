 <%@ include file="/WEB-INF/jsp/common/include.jsp" %>
       <div id="refrences" class="section">
          	   <div id ="icon_refrences" class="left_section">
          	     <img src='<c:url value="/img/icons/Expand.png" />' alt="Expanded Mode" onclick="javascript:hideShowDiv('icon_refrences','div_refrences');"/>
          	   </div>  
                <div  class="right_section">
                 <div class="sectionHeader" >  References </div>
			     <div id="div_refrences" class="right_section">     
                      <div class="tableHeader" >Coding Questions </div>
                      <table id="tbl_codingQuestions">
                      <tbody>
                     <c:forEach items="${changeRequestDTO.codingQuestions}" var="codingQuestion" varStatus="status">
                      <tr>
                          <td>${status.index+1} . </td>
                          <td>ID: &nbsp;	<form:input path="codingQuestions[${status.index}].eQueryId"  size="10" maxlength="10" readonly="true" cssClass="textGray" /> </td>
		                  <td>URL: &nbsp;	<form:input path="codingQuestions[${status.index}].url" size="50" maxlength="250" readonly="true" cssClass="textGray" /> </td>
                          <td class="alignLeft"> 
                           <c:choose>
                              <c:when  test="${!fn:contains(codingQuestion.url, 'http') && fn:contains(codingQuestion.url, 'www')}">
                                 <a href="http://${codingQuestion.url}" target="_blank">${codingQuestion.url}</a> 
                              </c:when>
                             <c:otherwise>
                                  <a href="${codingQuestion.url}" target="_blank">${codingQuestion.url}</a> 
                             </c:otherwise>  
                           </c:choose>  
                          </td>
                          <td> </td>
		              </tr>
                    </c:forEach>
                    </tbody>
                   </table>
		           <c:if test="${changeRequestDTO.baseClassification !='CCI' }" >   
		           <div id="div_urc_documents">
		             <div class="tableHeader" >URC Documents </div>
                      <table id="tbl_urcDocuments">
                        URC Attachments
                      <tbody>
                     <c:forEach items="${changeRequestDTO.urcAttachments}" var="urcAttachment" varStatus="status">
                      <tr>
                          <td> ${status.index+1} . </td>
                          <td>	
                          <a href="javascript:popupChangeRequestFile('${changeRequestDTO.changeRequestId}','${urcAttachment.documentReferenceId}','urc');">${urcAttachment.fileName}</a> 
                             <form:hidden path="urcAttachments[${status.index}].documentReferenceId"/>
                             <form:hidden path="urcAttachments[${status.index}].fileName"/>
                          </td>
                          <td></td>
		              </tr>
		              </c:forEach>
		              </tbody>
                    </table>
                     
		            <table id="tbl_urcLinks">
		              URC Links
		              <tbody>
		             <c:forEach items="${changeRequestDTO.urcLinks}" var="urcLink" varStatus="status">
                      <tr>
                          <td> ${status.index+1} . </td>
                          <td> <form:input path="urcLinks[${status.index}].url" size="50" maxlength="250" readonly="true" cssClass="textGray"/> </td>
		                  <td> 
		                    <c:choose>
                              <c:when  test="${!fn:contains(urcLink.url, 'http') && fn:contains(urcLink.url, 'www')}">
                                 <a href="http://${urcLink.url}" target="_blank">${urcLink.url}</a> 
                              </c:when>
                             <c:otherwise>
                                  <a href="${urcLink.url}" target="_blank">${urcLink.url}</a> 
                             </c:otherwise>  
                           </c:choose>  
		                 
		                  </td> 
		                  <td>  </td>
		              </tr>
		              </c:forEach>
		              </tbody>
                    </table>  
		           </div>   
		           </c:if> 
		            <div class="tableHeader" >Other Attachments in Support of Change  </div>
                      <table id="tbl_otherAttachments">
                      Other Attachments
                      <tbody>
                      <c:forEach items="${changeRequestDTO.otherAttachments}" var="otherAttachment" varStatus="status">
                      <tr>
                          <td> ${status.index+1} . </td>
                          <td>  <a href="javascript:popupChangeRequestFile('${changeRequestDTO.changeRequestId}','${otherAttachment.documentReferenceId}','other');">${otherAttachment.fileName}</a> 
                             <form:hidden path="otherAttachments[${status.index}].documentReferenceId"/> 
                             <form:hidden path="otherAttachments[${status.index}].fileName"/> 
                          </td>
		                  <td> </td>
		              </tr>
		              </c:forEach>
		              </tbody>
                    </table>
		             
		            <table id="tbl_otherLinks">
                      Other Links
                      <tbody>
                      <c:forEach items="${changeRequestDTO.otherLinks}" var="otherLink" varStatus="status">
                      <tr>
                          <td> ${status.index+1} .  </td>
                          <td><form:input path="otherLinks[${status.index}].url" size="50" maxlength="250" readonly="true" cssClass="textGray" /></td>
		                  <td> 
		                    <c:choose>
                              <c:when  test="${!fn:contains(otherLink.url, 'http') && fn:contains(otherLink.url, 'www')}">
                                 <a href="http://${otherLink.url}" target="_blank">${otherLink.url}</a> 
                              </c:when>
                             <c:otherwise>
                                  <a href="${otherLink.url}" target="_blank">${otherLink.url}</a> 
                             </c:otherwise>  
                           </c:choose>  
		                  </td>
		                  <td>   </td>
		              </tr>
		              </c:forEach>
		               </tbody>
                    </table>
		             
		           </div>
	            </div> 
          	</div>
 