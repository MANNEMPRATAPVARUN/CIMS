 <%@ include file="/WEB-INF/jsp/common/include.jsp" %>
       <div id="refrences" class="section">
                <div>
                 <div class="sectionHeader" ><span class="section-header">References</span></div>
			     <div id="div_refrences" style="margin-top:5px;">     
                      <div class="tableHeader section-header">Coding Questions </div>
                      <table id="tbl_codingQuestions" style="margin-bottom:5px;">
                      <tbody>
                     <c:forEach items="${changeRequestDTO.codingQuestions}" var="codingQuestion" varStatus="status">
                      <tr>
                          <td width="5%" class="label alignTop">${status.index+1}. </td>
                          <td width="15%" class="alignTop"><span class="label">ID:</span> &nbsp;${codingQuestion.eQueryId} </td>
		                  <td width="80%" class="alignTop"><span class="label">URL:</span> &nbsp;${codingQuestion.url}</td>
		              </tr>
                    </c:forEach>
                    </tbody>
                   </table>
		           <c:if test="${changeRequestDTO.baseClassification !='CCI' }" >  
		           <c:if test="${(fn:length(changeRequestDTO.urcAttachments) gt 0) || (fn:length(changeRequestDTO.urcLinks) gt 0)}"> 
		           <div id="div_urc_documents">
		             <div class="tableHeader section-header" >URC Documents </div>
		             <c:if test="${(fn:length(changeRequestDTO.urcAttachments) gt 0)}">
                      <table id="tbl_urcDocuments" style="margin-bottom:5px;">
                        <span class="label">URC Attachments</span>
                      <tbody>
                     <c:forEach items="${changeRequestDTO.urcAttachments}" var="urcAttachment" varStatus="status">
                      <tr>
                          <td width="20%" class="label alignTop"> ${status.index+1}.</td>
                          <td width="80%" class="alignTop">${urcAttachment.fileName}</td>
		              </tr>
		              </c:forEach>
		              </tbody>
                    </table>
                     </c:if>
                     <c:if test="${(fn:length(changeRequestDTO.urcLinks) gt 0)}">
		            <table id="tbl_urcLinks" style="margin-bottom:5px;">
		              <span class="label">URC Links</span>
		              <tbody>
		             <c:forEach items="${changeRequestDTO.urcLinks}" var="urcLink" varStatus="status">
                      <tr>
                          <td width="20%" class="label alignTop">${status.index+1}. </td>
                          <td width="80%" class="alignTop">${urcLink.url} </td>
		              </tr>
		              </c:forEach>
		              </tbody>
                    </table> 
                    </c:if> 
		           </div>  
		           </c:if> 
		           </c:if> 
		           <c:if test="${(fn:length(changeRequestDTO.otherAttachments) gt 0) || (fn:length(changeRequestDTO.otherLinks) gt 0)}"> 
		            <div class="tableHeader section-header" >Other Attachments in Support of Change  </div>
		            <c:if test="${(fn:length(changeRequestDTO.otherAttachments) gt 0)}">
                      <table id="tbl_otherAttachments" style="margin-bottom:5px;">
                      <span class="label">Other Attachments</span>
                      <tbody>
                      <c:forEach items="${changeRequestDTO.otherAttachments}" var="otherAttachment" varStatus="status">
                      <tr>
                          <td width="20%" class="label alignTop">${status.index+1}.</td>
                          <td width="80%" class="alignTop">${otherAttachment.fileName}</td>
		              </tr>
		              </c:forEach>
		              </tbody>
                    </table>
		            </c:if>
		            <c:if test="${(fn:length(changeRequestDTO.otherLinks) gt 0)}"> 
		            <table id="tbl_otherLinks" style="margin-bottom:5px;">
                      <span class="label">Other Links</span>
                      <tbody>
                      <c:forEach items="${changeRequestDTO.otherLinks}" var="otherLink" varStatus="status">
                      <tr>
                          <td width="20%" class="label alignTop">${status.index+1}.</td>
                          <td width="80%" class="alignTop">${otherLink.url}</td>
		              </tr>
		              </c:forEach>
		               </tbody>
                    </table>
                    </c:if>
		           </c:if>  
		           </div>
	            </div> 
          	</div>
 