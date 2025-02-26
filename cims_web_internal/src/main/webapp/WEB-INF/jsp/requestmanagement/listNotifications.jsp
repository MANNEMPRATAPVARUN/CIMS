<!DOCTYPE html> 

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<html style="height:100%;">
    <%@ include file="/WEB-INF/jsp/common/common-header.jsp" %>
<style type="text/css" media="all">
 
   .btn_line {
      position:absolute;
      right:15px;
    }
    .alignRight{
      text-align: right;
    }   

</style>
<script type="text/javascript">
     function confirmDeletion(){
	   return confirm('<fmt:message key="confirm.delete"/>');
     }
     
     function disableAllButtons(){
   	     $("input[type=button]").attr("disabled", "disabled");
   	     $("input[type=button]").attr("class", "disabledButton");
     }
     
     function completeRequest(notificationId){
    	 disableAllButtons();
    	 var url = "completeTask.htm?notificationId="+notificationId;
    	 window.location.href = url;
     }

     $(document).ready(function(){
    	 var $selectAll=$('<input/>').attr({type : 'checkbox', name: 'checkAll'}).addClass("selectAll");
    	 $('.checkAll').append($selectAll);
    	 $('.selectAll').css({"margin-left" : "-19px"});
    	 $('.selectAll').click(function(){
			if(!$(this).is(':checked')) {
				$('.checkBoxes').attr('checked', false);
			} else {
				$('.checkBoxes').attr('checked', true);
			}
    	  });
   	  
     }); $('.checkAll').css({"margin-left" : "-19px"});
     
     $(function(){ 
    	 $('a.dialog_detail').each(function() {  
    		   $.data(this, 'dialog', 
    		         $(this).next('.dialog_box').dialog({
    		              autoOpen: false,  
    		              modal: true,  
    		              width: 700,  
    		              height: 200 
    		          })
    		     );  
    		  }).click(function() {  
    		      $.data(this, 'dialog').dialog('open');  
    		       return false;  
    		  });  
    	
     });
     
     function removeCheckedNotifications(){
 	  
 	    $("#notificationDTO")[0].action="<c:url value='/removeCheckedNotifications.htm'/>";
     	$("#notificationDTO")[0].submit();
    }
    function popupChangeRequestViewer(changeRequestId) {
	      var link = "manageChangeRequest.htm?changeRequestId="+changeRequestId ;
		  var newwindow = window.open(link, "changeRequest"+changeRequestId, "resizable=yes,scrollbars=yes");
		  newwindow.moveTo(0,0);
		  newwindow.resizeTo(screen.width, (screen.height-(screen.height/20)));
		  if (window.focus)  {
			  newwindow.focus();
		  }
    }		 
     
</script>

<div class="content">
<form:form id="notificationDTO"  modelAttribute="notificationDTO"  >
<fieldset>
   <legend>My Notifications</legend>
    <c:if test="${fn:length(myNotifications) gt 0}">
       <div class="alignRight">
         <a href="javascript:removeCheckedNotifications();" onclick="return confirmDeletion();"> <img src='<c:url value="/img/icons/Remove.png" />' alt="remove"> </a>
       </div>
	
	<display:table name="myNotifications" id="myNotification" defaultsort="5" defaultorder="descending" requestURI=""  pagesize="${pageSize}" sort="external" partialList="true" size="resultSize"  class="listTable"  style="width: 100%; margin-top: 0px;">
		
		 <display:column  headerClass="tableHeader checkAll">
            <form:checkbox path="notificationIds" class="checkBoxes" value="${myNotification.notificationId}"/>
         </display:column>
		<display:column sortable="true" titleKey="home.notification.id" headerClass="tableHeader" sortName="notificationId">
          <a href="#" class="dialog_detail" >${myNotification.notificationId}</a>
          <div class="dialog_box" title="Notification-${myNotification.notificationId}">
                  <table class="accordion_table">
                    <tr>
                         <td style="text-align: right; width:100px;">Notification Type :  </td>
                         <td ><font color="IndianRed">  ${myNotification.subject} </font> </td>
                     </tr>
                     <tr>
                         <td style="text-align: right; width:100px;">Change Request :  </td>
                         <td > 
                          <font color="IndianRed">
                           <c:if test="${myNotification.changeRequestId ==null}">
                                N/A
                           </c:if>
                           <a href="javascript:popupChangeRequestViewer(${myNotification.changeRequestId});">${myNotification.changeRequestId}</a>
                          </font>
                          </td>
                     </tr>
                    
                     <tr>
                         <td style="text-align: right; width:100px;">Sender :  </td>
                         <td > <font color="IndianRed"> ${myNotification.sender.username}</font> </td>
                     </tr>
                     <tr>
                         <td style="text-align: right;vertical-align: top ; width:100px;" >Message :  </td>
                         <td style="width:350px;"> <font color="IndianRed"> ${myNotification.message} </font></td>
                     </tr>
                 </table>  
                 <c:if test="${myNotification.completionRequiredInd}">
                   <div class="btn_line"> Mark task as: &nbsp;&nbsp;
                      <input type="button"  value="Complete" class="button" onclick="javascript:completeRequest(${myNotification.notificationId});" >&nbsp;&nbsp; 
                   </div>
                 </c:if>
         </div>
         
         
        </display:column>
        <display:column sortable="true" titleKey="home.notification.versionCode" headerClass="tableHeader" sortName="fiscalYear">
             ${myNotification.fiscalYear}
        </display:column>
         <display:column sortable="true" titleKey="home.notification.changeRequestId" headerClass="tableHeader" sortName="changeRequestId">
              <c:choose> 
               <c:when test="${myNotification.changeRequestId !=null}"> 
                 <a href="javascript:popupChangeRequestViewer(${myNotification.changeRequestId});">${myNotification.changeRequestId}</a>
              </c:when>
               <c:otherwise> 
                  N/A
               </c:otherwise>  
          </c:choose>   
        </display:column>
         <display:column sortable="true" titleKey="home.notification.changeRequestName" headerClass="tableHeader" sortName="changeRequestName">
           <c:if test="${myNotification.changeRequestId ==null}">
              N/A
           </c:if>
          <a href="javascript:popupChangeRequestViewer(${myNotification.changeRequestId});" title="${myNotification.changeRequest.name}">
          <c:choose> 
               <c:when test="${fn:length(myNotification.changeRequest.name)<=80}">
                 ${myNotification.changeRequest.name}
               </c:when>
               <c:otherwise> 
                   ${fn:substring(myNotification.changeRequest.name,0,80)}...
               </c:otherwise>  
          </c:choose>   
          </a>  
        </display:column>
         <display:column sortable="true" titleKey="home.notification.notificationType" headerClass="tableHeader" sortName="subject">
             ${myNotification.subject}
        </display:column>
         <display:column sortable="true" titleKey="home.notification.sender" headerClass="tableHeader" sortName="sender">
             ${myNotification.sender.username}
        </display:column>
         <display:column sortable="true" titleKey="home.notification.recipients" headerClass="tableHeader" >
             <c:choose> 
               <c:when test="${not empty myNotification.dlRecipients}">
                 <c:forEach var="dlRecipient" items="${myNotification.dlRecipients}">
                    ${dlRecipient.name}  </br>
                 </c:forEach>
               </c:when>
               <c:otherwise> 
                 ${myNotification.recipient.username}
               </c:otherwise>
              </c:choose>
        </display:column>
         <display:column sortable="true" titleKey="home.notification.createDate" headerClass="tableHeader" sortName="createDate">
            <fmt:formatDate pattern="yyyy-MM-dd" value="${myNotification.createdDate}" />
        </display:column>
        
      </display:table>
    </c:if>
    <c:if test="${fn:length(myNotifications) eq 0}">
        You have no notifications
    </c:if>
    
</fieldset>


</form:form>
</div>
</html>



