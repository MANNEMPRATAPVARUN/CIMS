<%@ include file="../common/include.jsp"%>


<script type="text/javascript">
function confirmDeletion(){
	return confirm('<fmt:message key="confirm.delete"/>');
}
</script>
<h3 class="pageTitle"><fmt:message key="home.change.request.title" /></h3>

<div class="content">
<form:form method="POST" modelAttribute="requestViewBean" name="changeRequest">
<spring:bind path="*">
    <div class="errorMsg">
      <ul>
      <c:forEach var="error" items="${status.errorMessages}">
        <li><c:out value="${error}" escapeXml="false"/></li>
      </c:forEach>
      </ul>
    </div>
</spring:bind>

<fieldset>
   <legend><fmt:message key="home.subtitle.notification"/></legend>
   <display:table name="requestViewBean.notifications" id="notification" requestURI="" defaultsort="1" class="listTable" pagesize="2" sort="list" style="width: 80%; margin-top: 20px;">
		
		<display:column sortable="true" titleKey="home.notification.id" headerClass="tableHeader">
        ${notification.notificationId}
        </display:column>       
        <display:column sortable="true" titleKey="home.notification.versionCode" headerClass="tableHeader">
        ${notification.versionCode}
        </display:column>
		<display:column sortable="true" titleKey="home.notification.subject" headerClass="tableHeader">
        ${notification.subject}
        </display:column>
		<display:column sortable="true" titleKey="home.notification.sender" headerClass="tableHeader">
        ${notification.sender}
        </display:column>
        <display:column sortable="true" titleKey="home.notification.recipient" headerClass="tableHeader">
        ${notification.recipient}
        </display:column>
		<display:column sortable="true" format="{0,date,MM/dd/yyyy HH:mm:ss}" titleKey="home.notification.createDate" headerClass="tableHeader">
        ${notification.createDate}
        </display:column>  
        <display:column sortable="true" titleKey="home.notification.notificationType" headerClass="tableHeader">
        ${notification.notificationType}
        </display:column> 
        
	</display:table>
	
</fieldset>
<fieldset>
   <legend><fmt:message key="home.subtitle.assigned"/></legend>
	<display:table name="requestViewBean.notifications" id="notification" class="listTable" pagesize="2" sort="list" style="width: 80%; margin-top: 20px;">
		<display:column sortable="true" titleKey="manage.change.request.id" headerClass="tableHeader">
        <a href="request.htm?chRequestId=${notification.notificationId}&type=manage">${notification.notificationId}</a>
               
        </display:column>
        
	</display:table>
	<div style="padding-top: 10px;">
    <button class="button" type="button" onclick="window.location.href='request.htm?chRequestId=&type=add'"><fmt:message key="add.new.change.request" /></button>
    </div>
</fieldset>
</form:form>
</div>


