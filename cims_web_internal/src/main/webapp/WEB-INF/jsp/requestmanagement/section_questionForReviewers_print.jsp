      <%@ include file="/WEB-INF/jsp/common/include.jsp" %>
      <c:if test="${fn:length(changeRequestDTO.questionForReviewers) gt 0}">
       <div id="questions" class="section">
              <div>
                 <div class="sectionHeader" ><span class="section-header">Questions for Reviewers</span></div>
			  
                    <div id="div_questionForReviewers">                 
                    <table id="tbl_questions">
                      <tbody>
                      <c:forEach items="${changeRequestDTO.questionForReviewers}" var="questionForReviewer" varStatus="status">
                       <tr>
                       <td width="20%" class="alignTop label">Question: #${status.index+1}</td> <td width="80%" class="alignTop">${questionForReviewer.questionForReviewerTxt }</td>
                       </tr>
                       <tr>
                       <td class="alignTop label">Question #${status.index+1} Reviewer:</td><td class="alignTop">${questionForReviewer.reviewer.name }</td>
                       </tr>
                       
    		           <c:forEach items="${questionForReviewer.questionComments}" var="questionComment" varStatus="statusComment">
    		           <tr>
    		           <td class="alignTop label">Comment #${status.index+1} - ${statusComment.index+1}(${questionComment.commmentUser.username}):</td><td class="alignTop">${questionComment.userCommentTxt}</td>
    		           </tr>
    		           </c:forEach>
    		           </c:forEach>
                       </tbody>
		             </table>
		                       
		             </div>
		              
		       </div>
        </div>
        </c:if>
