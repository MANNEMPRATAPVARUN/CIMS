
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<script type="text/javascript">
    function editPicklist() {
		window.location.href = "<c:url value='/refset/picklist/edit.htm?contextId=' />"
				+ ${viewBean.contextId}
				+ '&picklistElementId='
				+ ${param.picklistElementId}
				+ '&picklistElementVersionId=' + ${param.picklistElementVersionId} + '&elementId=' + ${viewBean.elementId}	+ '&elementVersionId=' + ${viewBean.elementVersionId};		
	}

    function viewPicklist() {
		window.location.href = "<c:url value='/refset/picklist/view.htm?contextId=' />"
				+ ${viewBean.contextId}
				+ '&picklistElementId='
				+ ${param.picklistElementId}
				+ '&picklistElementVersionId=' + ${param.picklistElementVersionId} + '&elementId=' + ${viewBean.elementId}	+ '&elementVersionId=' + ${viewBean.elementVersionId};		
	}

    function outputConfigPicklist() {
		window.location.href = "<c:url value='/refset/picklist/picklistOutputConfig.htm?contextId=' />"
				+ ${viewBean.contextId}
				+ '&picklistElementId='
				+ ${param.picklistElementId}
				+ '&picklistElementVersionId=' + ${param.picklistElementVersionId} + '&elementId=' + ${viewBean.elementId}	+ '&elementVersionId=' + ${viewBean.elementVersionId};                		
	}
</script>	

<div class="span-24 last" style="width: 1366px; overflow: visible !important; ">
	<div class="wrapper">
         <div class="appTab ui-tabs ui-widget ui-widget-content ui-corner-all noborder">
              <ul class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
                  <li class="ui-state-default ui-corner-top ${activePicklistSubTab == 'picklistColumnConfiguration' ? 'ui-tabs-selected ui-state-active' : ''}"> 
                      <span><a href="#" onclick="editPicklist();"><fmt:message key="picklist.column.configuration" /></a></span>          
                  </li>
                  <li class="ui-state-default ui-corner-top ${activePicklistSubTab == 'picklistView' ? 'ui-tabs-selected ui-state-active' : ''}">          
                      <span><a href="#" onclick="viewPicklist();"><fmt:message key="picklist.view.picklist" /></a></span>       
                  </li>
                  <li class="ui-state-default ui-corner-top ${activePicklistSubTab == 'picklistOutputConfig' ? 'ui-tabs-selected ui-state-active' : ''}">           
                      <span><a href="#" onclick="outputConfigPicklist();"><fmt:message key="picklist.output.configuration" /></a></span>           
                  </li>        
              </ul>
         </div>
    </div>
</div>



