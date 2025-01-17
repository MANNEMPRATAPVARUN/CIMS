 <%@ include file="/WEB-INF/jsp/common/include.jsp"%>
   
  <div class="appTab ui-tabs ui-widget ui-widget-content ui-corner-all">
     <ul  class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all"> 
        
        <li class="${activeTab=='generateTables'? 'ui-state-default ui-corner-top ui-tabs-selected ui-state-active':'ui-state-default ui-corner-top'}">
          <a href='<c:url value="/showGenerateClassificationTables.htm"></c:url> '>
            <span>Generate Tables</span>
          </a>
        </li>
        
        <li class="${activeTab=='reviewTables'? 'ui-state-default ui-corner-top ui-tabs-selected ui-state-active':'ui-state-default ui-corner-top'}">
          <a href='<c:url value="/showReviewTables.htm"></c:url> '>
            <span>Review Tables</span>
          </a>
        </li>
         <li class="${activeTab=='releaseTables'? 'ui-state-default ui-corner-top ui-tabs-selected ui-state-active':'ui-state-default ui-corner-top'}">
           <a href='<c:url value="/showReleaseTables.htm"></c:url>'>
             <span>Release Table</span>
            </a>
        </li>
     
        <li class="${activeTab=='releaseHistory'? 'ui-state-default ui-corner-top ui-tabs-selected ui-state-active':'ui-state-default ui-corner-top'}">
           <a href='<c:url value="/showReleaseHistory.htm"></c:url>'>
              <span>Release History</span>
           </a>
        </li>
       
        <li class="${activeTab=='unfreezeSystem'? 'ui-state-default ui-corner-top ui-tabs-selected ui-state-active':'ui-state-default ui-corner-top'}">
           <a href='<c:url value="/showUnfreezeSystem.htm"></c:url>'>
              <span>Unfreeze System</span>
           </a>
        </li>
       
     </ul>
   </div>