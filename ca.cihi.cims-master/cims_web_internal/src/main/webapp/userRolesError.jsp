<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<p>Your Role is not set up correctly , Please contact cims administrator </p>
<p>You have Roles: </p>
<p>
 <c:forEach items="${currentUser.roles}" var="role" varStatus="status">
    ${role.role} ,&nbsp;
 </c:forEach>
</p>
<p>The following is cims Role assignment rules: </p>
<p>The Initiator role must not be combined with any other roles except the Reviewer role and Refset Developer Role</p>
<p>The Administrator role must not be combined with any other roles </p>
<p>The English Content Developer and French Content Developer roles are mutually exclusive <p>
