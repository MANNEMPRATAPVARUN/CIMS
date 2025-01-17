<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<security:authorize access="hasAnyRole('ROLE_IT_ADMINISTRATOR')">
	<li><a href="#"><fmt:message key="cims.menu.migrate.data" /></a>
	    <ul>
	        <li><a href="<c:url value="/dataMigration.htm"/>"><fmt:message key="cims.menu.migrate.data" /></a></li> 
	    </ul>
	</li>
	<li><a href="#"><fmt:message key="cims.menu.transform.tabular.data" /></a>
	    <ul>
	        <li><a href="<c:url value="/tabularDataTransformation.htm"/>"><fmt:message key="cims.menu.transform.tabular.data" /></a></li> 
	    </ul>
	</li>
	<li><a href="#"><fmt:message key="cims.menu.transform.index.data" /></a>
	    <ul>
	        <li><a href="<c:url value="/indexDataTransformation.htm"/>"><fmt:message key="cims.menu.transform.index.data" /></a></li> 
	    </ul>
	</li>
	<li><a href="#"><fmt:message key="cims.menu.transform.supplement.data" /></a>
	    <ul>
	        <li><a href="<c:url value="/supplementDataTransformation.htm"/>"><fmt:message key="cims.menu.transform.supplement.data" /></a></li> 
	    </ul>
	</li>
	<li><a href="#"><fmt:message key="cims.menu.transform.ccicomponent.data" /></a>
	    <ul>
	        <li><a href="<c:url value="/cciComponentDataTransformation.htm"/>"><fmt:message key="cims.menu.transform.ccicomponent.data" /></a></li> 
	    </ul>
	</li>
	<li><a href="#"><fmt:message key="cims.menu.create.context" /></a>
	    <ul>
	        <li><a href="<c:url value="/createContext.htm"/>"><fmt:message key="cims.menu.create.context" /></a></li> 
	    </ul>
	</li>
	<li><a href="#"><fmt:message key="cims.menu.asot" /></a>
	    <ul>
	        <li><a href="<c:url value="/asot.htm"/>"><fmt:message key="cims.menu.asot" /></a></li> 
	    </ul>
	</li>
	<li><a href="#"><fmt:message key="cims.menu.snomed" /></a>
	    <ul>
	        <li><a href="<c:url value="/snomed.htm"/>"><fmt:message key="cims.menu.snomed" /></a></li> 
	    </ul>
	</li>
</security:authorize>
