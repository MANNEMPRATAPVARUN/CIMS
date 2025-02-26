      <%@ include file="/WEB-INF/jsp/common/include.jsp" %>
       <div id="questions" class="section">
          	  <div id ="icon_questions" class="left_section">
                    <img id="iconExpandOrCollapse" src='<c:url value="/img/icons/Expand.png" />' alt="Expanded Mode" onclick="javascript:hideShowDiv('icon_questions','div_questionForReviewers');" />
              </div> 
              <div  class="right_section">
                 <div class="sectionHeader" > Questions for Reviewers </div>
			  
                    <div id="div_questionForReviewers">                 
                    <table id="tbl_questions">
                      <tbody>
                      <c:forEach items="${changeRequestDTO.questionForReviewers}" var="questionForReviewer" varStatus="status">
                       <tr>
                       <td class="alignTop" > ${status.index+1} . </td>
                       <td>
                         <form:hidden path="questionForReviewers[${status.index}].questionForReviewerId" />
                         <form:hidden path="questionForReviewers[${status.index}].reviewerId" />
                         
                         <form:textarea path="questionForReviewers[${status.index}].questionForReviewerTxt" rows="3"  />
                          
                          <div class="alignRight">
		                     Reviewer :  <form:select class="reviewerDropDown"  path="questionForReviewers[${status.index}].reviewerId"   disabled="true" >
		                                         <form:option value="9"> DL - Classification </form:option>
                                                 <c:forEach var="selectedReviewGroup" items="${selectedReviewGroups}">
                                                     <form:option value="${selectedReviewGroup.distributionlistid}"> ${selectedReviewGroup.description}  </form:option>
  	  	                                         </c:forEach>      
    		                              </form:select>
    		                              <form:hidden path="questionForReviewers[${status.index}].beenSentOut" />
    		                              <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_SEND_FOR_REVIEW")}'>
    		                                 <c:if test="${!questionForReviewer.beenSentOut}">
    		                                    <input type="button"  value="Send for Review" class="button" onclick="javascript:sendForReviewChangeRequest(${status.index});" >&nbsp;&nbsp;
 		                                     </c:if>   
 		                                  </c:if>
    		                  </div>
    		                <c:forEach items="${questionForReviewer.questionComments}" var="questionComment" varStatus="statusComment">
    		                   <c:set var="backwardIndex"  value="${fn:length(questionForReviewer.questionComments)-statusComment.index-1}"/>
    		                   <c:set var="questionCommentBackward"  value="${questionForReviewer.questionComments[backwardIndex]}"/>
    		                <div class="topMargin5px">
    		                 <form:hidden path="questionForReviewers[${status.index}].questionComments[${backwardIndex}].userCommentId" />
    		                   Comment ${status.index+1} -  ${backwardIndex+1} .  &nbsp;&nbsp; Commented by: ${questionCommentBackward.commmentUser.username}    
    		                         <form:textarea path="questionForReviewers[${status.index}].questionComments[${backwardIndex}].userCommentTxt" cssStyle=" height:50px; width:650px; white-space: normal; overflow-y: scroll; overflow-x: hidden;" disabled="true"/>
    		                   <script>
    			                  CKEDITOR.replace( 'questionForReviewers[${status.index}].questionComments[${backwardIndex}].userCommentTxt' ,{readOnly :  true });
                               </script>
    		                </div>
    		                
    		                </c:forEach>
    		           
    		               <c:if test="${questionForReviewer.beenSentOut}">
    		               <c:if test='${cf:isUserQuestionRecipient(currentUser,questionForReviewer)}'>
    		                <c:set var="questionCommentsLength"  value="${fn:length(questionForReviewer.questionComments)}"/>
    		                <div class="topMargin5px">
    		                Comment ${status.index+1} - ${fn:length(questionForReviewer.questionComments)+1} .
    		                         <form:textarea path="questionForReviewers[${status.index}].questionComments[${fn:length(questionForReviewer.questionComments)}].userCommentTxt" cssStyle=" height:50px; width:650px; white-space: normal; overflow-y: scroll; overflow-x: hidden;"/>
    		                    <script>
    			                   CKEDITOR.replace( 'questionForReviewers[${status.index}].questionComments[${questionCommentsLength}].userCommentTxt',{readOnly :  false } );
                                </script>
    		                </div>
    		                  <div class="btn_alignRight">
    		                     <input type="button"  value="Add Comment" class="button" onclick="javascript:addCommentForQuestion(${questionForReviewer.questionForReviewerId});" >&nbsp;&nbsp; 
    		                  </div>   
		                    </c:if></c:if>                     
		               </td>
		                <td class="alignTop" >
		                <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_SEND_FOR_REVIEW")}'>
		                 <c:if test="${!questionForReviewer.beenSentOut}">
		                    <div class="alignRight"> <img src='<c:url value="/img/icons/Delete.png" />' alt="Remove" onclick="javascript:removeTableRow(this);"/> </div>
		                 </c:if> 
		                 </c:if>   
		                </td>
		               <script>
    			          CKEDITOR.replace( 'questionForReviewers[${status.index}].questionForReviewerTxt', {readOnly : true} );
                        </script>
                       </tr>
                      </c:forEach>
                       </tbody>
		             </table>
		                       
		             </div>
		              
		       </div>
        </div>
