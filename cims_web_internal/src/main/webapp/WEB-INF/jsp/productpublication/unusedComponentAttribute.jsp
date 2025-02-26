<!DOCTYPE html> 
<%@page language="java" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

 




<html style="height:100%;">
    <%@ include file="/WEB-INF/jsp/common/common-header.jsp" %>
	<body  style="height:100%;">
	
	 <div class="fixed" style="width: 80%; overflow: visible !important; " >
    		 <div class="contentContainer" >
				<div class="content">
                   <fieldset>
                     <legend>Unused Components</legend>
                   <display:table name="unusedComponents" id="unusedComponent"  requestURI="" class="listTable" style="width: 55%; margin-top: 0px;">
		              <display:column  titleKey="production.unusedcomponent.section" headerClass="tableHeader" style="text-align:center;" >
                          ${unusedComponent.section}
                      </display:column>
                      <display:column  titleKey="production.unusedcomponent.type" headerClass="tableHeader" style="text-align:center;" >
                          ${unusedComponent.type}
                      </display:column>
                    
                    
                      <display:column titleKey="production.unusedcomponent.code" headerClass="tableHeader"  style="text-align:center;">
        	             ${unusedComponent.code}
                    </display:column>
                  </display:table>
                 </fieldset>
               
                 <fieldset>
                     <legend>Unused Generic Attribute Codes</legend>
                   <display:table name="unusedGenericAttributes" id="unusedGenericAttribute"  requestURI="" class="listTable" style="width: 55%; margin-top: 0px;">
		              <display:column  titleKey="production.unusedgenericattribute.type" headerClass="tableHeader" style="text-align:center;" >
                          ${unusedGenericAttribute.type}
                      </display:column>
                      <display:column titleKey="production.unusedgenericattribute.code" headerClass="tableHeader"  style="text-align:center;">
        	             ${unusedGenericAttribute.code}
                    </display:column>
                  </display:table>
                 </fieldset>


                 <fieldset>
                     <legend>Unused Reference Values</legend>
                   <display:table name="unusedReferenceValues" id="unusedReferenceValue"  requestURI="" class="listTable" style="width: 55%; margin-top: 0px;">
		              <display:column  titleKey="production.unusedreferencevalue.type" headerClass="tableHeader" style="text-align:center;" >
                          ${unusedReferenceValue.type}
                      </display:column>
                      <display:column titleKey="production.unusedreferencevalue.code" headerClass="tableHeader"  style="text-align:center;">
        	             ${unusedReferenceValue.code}
                    </display:column>
                  </display:table>
                 </fieldset>
             </div>
        </div>
	  </div>
		    
	

  </body>


</html>
