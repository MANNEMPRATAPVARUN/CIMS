<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<script type="text/javascript">
  
   function popupCreateChangeRequestWindow() {
	      var link = "<c:url value="/initCreateChangeRequest.htm"/>";
		  var newwindow = window.open(link, "changeRequest", "resizable=yes,scrollbars=yes ");
		  newwindow.moveTo(0,0);
		  newwindow.resizeTo(screen.width, (screen.height-(screen.height/20)));
		  if (window.focus)  {
			  newwindow.focus();
		  }
    }		

</script>

<security:authorize access="hasAnyRole('ROLE_READ_ONLY','ROLE_INITIATOR','ROLE_ENG_CONTENT_DEVELOPER','ROLE_FRA_CONTENT_DEVELOPER','ROLE_ADMINISTRATOR','ROLE_REVIEWER','ROLE_RELEASE_OPERATOR','ROLE_REFSET_DEVELOPER')">
 
 <li><a href="#"><fmt:message key="cims.menu.home" /></a>
    <ul>
    	<li><a href="<c:url value="/myhome.htm"/>"><fmt:message key="cims.menu.home" /></a></li>           
    </ul>
 </li> 
 
 <li><a href="#"><fmt:message key="cims.menu.change.requests" /></a>
    <ul>    
        <%-- removed, as we have search now
        <li><a href="<c:url value="/findAllChangeRequests.htm"/>">All Change Requests</a></li> 
        --%>
         <security:authorize access="hasAnyRole('ROLE_INITIATOR','ROLE_ENG_CONTENT_DEVELOPER','ROLE_FRA_CONTENT_DEVELOPER','ROLE_ADMINISTRATOR')">
        <li><a href="javascript:popupCreateChangeRequestWindow();">Create Change Request</a></li> 
        </security:authorize>

        <li><a href="<c:url value="/legacyChangeRequests.htm"/>">CRD Legacy Search</a></li> 
        
        <!-- SEARCH -->
        <security:authorize access="hasAnyRole('ROLE_READ_ONLY','ROLE_INITIATOR','ROLE_ENG_CONTENT_DEVELOPER','ROLE_FRA_CONTENT_DEVELOPER','ROLE_REVIEWER','ROLE_ADMINISTRATOR')">
        <li>
        	<a href="#"><fmt:message key="cims.menu.search.cr.icd"/></a>
        	<ul>
<%-- 		        <security:authorize access="!hasAnyRole('ROLE_REVIEWER')"> --%>
		        <li><a href="<c:url value="/findAllICDChangeRequests.htm"/>">All ICD-10-CA Change Requests</a></li> 
		      <%--   </security:authorize> --%>
        		<li><a href="<c:url value="/search/list.htm?classification=ICD-10-CA&searchType=cr.properties"/>"><fmt:message key="cims.menu.search.cr.properties"/></a></li>
        		<li><a href="<c:url value="/search/list.htm?classification=ICD-10-CA&searchType=cr.icd.tabular"/>"><fmt:message key="cims.menu.search.cr.tabular"/></a></li>
        		<li><a href="<c:url value="/search/list.htm?classification=ICD-10-CA&searchType=cr.index"/>"><fmt:message key="cims.menu.search.cr.index"/></a></li>
        	</ul>
        </li>
        <li>
        	<a href="#"><fmt:message key="cims.menu.search.cr.cci"/></a>
        	<ul>
		      <%--   <security:authorize access="!hasAnyRole('ROLE_REVIEWER')"> --%>
		        <li><a href="<c:url value="/findAllCCIChangeRequests.htm"/>">All CCI Change Requests</a></li> 
		    <%--     </security:authorize> --%>
        		<li><a href="<c:url value="/search/list.htm?classification=CCI&searchType=cr.properties"/>"><fmt:message key="cims.menu.search.cr.properties"/></a></li>
        		<li><a href="<c:url value="/search/list.htm?classification=CCI&searchType=cr.cci.tabular"/>"><fmt:message key="cims.menu.search.cr.tabular"/></a></li>
        		<li><a href="<c:url value="/search/list.htm?classification=CCI&searchType=cr.index"/>"><fmt:message key="cims.menu.search.cr.index"/></a></li>
        	</ul>
        </li>
        </security:authorize>
    </ul>
 </li>
 <li><a href="#"><fmt:message key="cims.menu.icd10" /></a>
    <ul>
        <li><a href="<c:url value="/selectClassification.htm?classification=ICD-10-CA"/>"><fmt:message key="cims.menu.icd10" /></a></li>
         <!-- SEARCH -->
        <security:authorize access="hasAnyRole('ROLE_READ_ONLY','ROLE_INITIATOR','ROLE_ENG_CONTENT_DEVELOPER','ROLE_FRA_CONTENT_DEVELOPER','ROLE_REVIEWER','ROLE_ADMINISTRATOR')">
        <li><a href="<c:url value="/search/list.htm?classification=ICD-10-CA&searchType=tab.icd.simple"/>"><fmt:message key="cims.menu.search.tabular.simple"/></a></li>
   		<li><a href="<c:url value="/search/list.htm?classification=ICD-10-CA&searchType=tab.icd.comparative"/>"><fmt:message key="cims.menu.search.tabular.comparative"/></a></li>
        </security:authorize>
    </ul>
 </li>
 <li><a href="#"><fmt:message key="cims.menu.cci" /></a>
    <ul>
       <li><a href="<c:url value="/selectClassification.htm?classification=CCI"/>"><fmt:message key="cims.menu.cci" /></a></li>
        <!-- SEARCH -->
        <security:authorize access="hasAnyRole('ROLE_READ_ONLY','ROLE_INITIATOR','ROLE_ENG_CONTENT_DEVELOPER','ROLE_FRA_CONTENT_DEVELOPER','ROLE_REVIEWER','ROLE_ADMINISTRATOR')">
        <li><a href="<c:url value="/search/list.htm?classification=CCI&searchType=tab.cci.simple"/>"><fmt:message key="cims.menu.search.tabular.simple"/></a></li>
   		<li><a href="<c:url value="/search/list.htm?classification=CCI&searchType=tab.cci.comparative"/>"><fmt:message key="cims.menu.search.tabular.comparative"/></a></li>
        <li><a href="<c:url value="/search/list.htm?classification=CCI&searchType=ref.cci.comparative"/>"><fmt:message key="cims.menu.search.ref.comparative"/></a></li>
        </security:authorize> 
    </ul>
 </li>
 
  <li><a href="#"><fmt:message key="cims.menu.reports" /></a>
    <ul>
       <li><a href="<c:url value="/reports/classificationChange.htm"/>"><fmt:message key="cims.menu.reports.classificationchange" /></a></li> 
       <li><a href="<c:url value="/reports/missingValidation.htm"/>"><fmt:message key="cims.menu.reports.missingvalidation" /></a></li>
       <li><a href="<c:url value="/reports/icdModifiedValidations.htm"/>"><fmt:message key="cims.menu.reports.icdmodifiedvalidations" /></a></li> 
       <li><a href="<c:url value="/reports/cciModifiedValidations.htm"/>"><fmt:message key="cims.menu.reports.ccimodifiedvalidations" /></a></li>
       <li><a href="<c:url value="/reports/cciNewTableCodes.htm"/>"><fmt:message key="cims.menu.reports.ccinewtablecodes" /></a></li>
       <li><a href="<c:url value="/reports/icdModifiedValidCode.htm"/>"><fmt:message key="cims.menu.reports.icdmodifiedvalidcode" /></a></li>
       <li><a href="<c:url value="/reports/reviewGroup/outboundQuestions.htm"/>"><fmt:message key="cims.menu.reports.reviewgroup.outboundquestions" /></a></li> 
       <li><a href="<c:url value="/reports/reviewGroup/compiledResponses.htm"/>"><fmt:message key="cims.menu.reports.reviewgroup.compiledresponses" /></a></li>
       <li><a href="<c:url value="/reports/qaSummaryMetrics.htm"/>"><fmt:message key="cims.menu.reports.qasummarymetrics" /></a></li> 
       <li><a href="<c:url value="/reports/qaErrorDescriptions.htm"/>"><fmt:message key="cims.menu.reports.qaerror.descriptions" /></a></li> 
    </ul>
 </li>
  <security:authorize access="hasAnyRole('ROLE_RELEASE_OPERATOR','ROLE_ADMINISTRATOR','ROLE_IT_ADMINISTRATOR')">
   <li><a href="#"><fmt:message key="cims.menu.publication" /></a>
    <ul>
      <li><a href="<c:url value="/showGenerateClassificationTables.htm"/>"><fmt:message key="cims.menu.publication.generatetables" /></a></li> 
    </ul>
   </li>
  </security:authorize>
 <li><a href="#"><fmt:message key="cims.menu.myPreferences" /></a>
    <ul>
       <li><a href="<c:url value="/profile/myPreferences.htm"/>"><fmt:message key="cims.menu.myPreferences" /></a></li> 
    </ul>
 </li>
<security:authorize access="hasAnyRole('ROLE_IT_ADMINISTRATOR','ROLE_ADMINISTRATOR','ROLE_ENG_CONTENT_DEVELOPER','ROLE_FRA_CONTENT_DEVELOPER','ROLE_REVIEWER','ROLE_INITIATOR','ROLE_READ_ONLY')">
  <li><a href="#"><fmt:message key="cims.menu.administration" /></a>
	<ul>
	<security:authorize access="hasAnyRole('ROLE_IT_ADMINISTRATOR')">
		<li><a href="<c:url value="/admin/user.htm"/>"><fmt:message key="cims.menu.admin.sub.manage.user" /></a></li>
     </security:authorize>
     
    <security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR','ROLE_IT_ADMINISTRATOR')">
    	<li><a href="<c:url value="/admin/distribution.htm"/>"><fmt:message key="cims.menu.admin.sub.manage.distribution" /></a></li>		
    </security:authorize>

   	<security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR')">
       	<li><a href="<c:url value="/admin/auxiliary.htm"/>"><fmt:message key="cims.menu.admin.sub.manage.auxiliary.change.request" /></a></li>
		<li><a href="<c:url value="/admin/auxiliary.htm?classification=true"/>"><fmt:message key="cims.menu.admin.sub.manage.auxiliary.classifications" /></a></li>
     </security:authorize>
     <security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR','ROLE_ENG_CONTENT_DEVELOPER','ROLE_FRA_CONTENT_DEVELOPER','ROLE_REVIEWER','ROLE_INITIATOR','ROLE_READ_ONLY')">
		<li><a href="<c:url value="/cciComponents.htm"/>"><fmt:message key="cims.menu.admin.sub.manage.cci.components" /></a></li>
		<li><a href="<c:url value="/cciAttributes.htm"/>"><fmt:message key="cims.menu.admin.sub.manage.cci.attributes" /></a></li>
	</security:authorize>	
   	<security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR')">
		<li><a href="<c:url value="/admin/initCloseYear.htm"/>"><fmt:message key="cims.menu.admin.sub.manage.closeyear" /></a></li>
     </security:authorize>
   	<security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR')">
		<li><a href="<c:url value="/admin/processExportFolioRequest.htm"/>"><fmt:message key="cims.menu.admin.sub.manage.exportFolioFile" /></a></li>
     </security:authorize>
     <security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR')">
		<li><a href="<c:url value="/admin/processExportClamlRequest.htm"/>"><fmt:message key="cims.menu.admin.sub.manage.exportClamlFile" /></a></li>
     </security:authorize>
	
	</ul>   
   
  </li>
</security:authorize>
 
  <li><a href="#"><fmt:message key="cims.menu.icd11" /></a>  
     <ul>  
         <li><a href="<c:url value="http://apps.who.int/classifications/icd11/browse"/>"><fmt:message key="cims.menu.icd11" /></a></li>   
     </ul>  
 </li> 
 
 <security:authorize access="hasAnyRole('ROLE_IT_ADMINISTRATOR','ROLE_ADMINISTRATOR','ROLE_ENG_CONTENT_DEVELOPER','ROLE_FRA_CONTENT_DEVELOPER','ROLE_REVIEWER','ROLE_INITIATOR','ROLE_READ_ONLY','ROLE_REFSET_DEVELOPER')">
  <li><a href="#"><fmt:message key="cims.menu.refset" /></a>
	<ul>

   <%-- <security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR')"> --%>
       	<li><a href="<c:url value="/refset/refsetCatalog.htm"/>"><fmt:message key="cims.menu.refset.sub.catalog" /></a></li>
   <%-- </security:authorize> --%>
	
	</ul>   
   
  </li>
</security:authorize>
 
</security:authorize>