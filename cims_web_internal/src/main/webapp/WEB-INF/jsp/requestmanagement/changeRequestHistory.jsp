<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<style type="text/css">
table.historyTable {
	text-align: left;
	width: auto;
}

table.historyTable td {
	vertical-align: top;
}

table.historyTable th {
	background-color: #CDE1E0;
	color: #006666;
	padding: 3px;
	font-size: 9pt;
	text-align: left;
}


table.historyTable tr.even {
	background: #EEEEEE;
	color: #000000;
}

table.historyTable tr.odd {
	background: #FFFFFF;
	color: #000000;
}

table.innerTable td {
	padding-top: 0px;
}
table {
	margin-bottom: 0px;
}


</style>


<script type="text/javascript" src="<c:url value="/ckeditor/ckeditor.js"/>"></script>
<script type="text/javascript">
   var editor;
   var lastDivId;
   function toggleDiv(divId){
	   if (editor){
		   editor.destroy(); 
		   if (lastDivId && lastDivId!=divId){
			   $('#' + lastDivId).hide(); 
		   }
	   }
	   $('#' + divId).toggle();
	   var isHidden = $('#' + divId).is(':hidden');
	   if (!isHidden){
		   editor = CKEDITOR.replace( divId, {readOnly : true} );
	   }
	   lastDivId = divId;
	     
   }   
  </script>


<div class="content">
    <div >
           Owner: &nbsp; ${changeRequestDTO.owner.username}  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
           Current Assignee:   &nbsp; 
           <c:choose>
                <c:when test="${changeRequestDTO.userAssignee !=null }">
                    ${changeRequestDTO.userAssignee.username}
               </c:when>
                <c:otherwise>
                    ${changeRequestDTO.dlAssignee.name}
               </c:otherwise>  
             </c:choose>    
    </div>
  
<table id="changeRequestHistorys" class="historyTable"  style="width: 100%; ">
  <thead>
    <tr>
       <th class="tableHeader">Modification Date/Time</th>
       <th class="tableHeader">Modification Summary</th>
       <th class="tableHeader">Modified By</th>
   </tr>
  </thead>
 <tbody>
 <c:forEach items="${changeRequestHistorys}" var="changeRequestHistory" varStatus="status">
     <tr class="${status.index%2==0 ? 'even' : 'odd'}">
         <td class="alignTop">   <fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${changeRequestHistory.createdDate}" />        </td>
         <td class="alignTop">
            <table class="innerTable" >
                <c:forEach items="${changeRequestHistory.historyItems}" var="historyItem" varStatus="itemStatus">
                   <tr>
                     <td class="alignTop" style="width: 35%;">
                         <c:choose>
                          <c:when test="${historyItem.labelDescOverride ==null }">
                              ${historyItem.labelCode.label} :
                           </c:when>
                           <c:otherwise>
                               ${historyItem.labelDescOverride} :
                           </c:otherwise>   
                          </c:choose>
                     </td>
                     <td > 
                        <c:if test="${historyItem.labelCode.isLink}">
                           <a href="javascript:toggleDiv('${historyItem.changeRequestHistoryItemId}');" title="${fn:escapeXml(historyItem.item)}" > ${historyItem.labelCode.label} </a>
                           <div id="${historyItem.changeRequestHistoryItemId}" style="display:none" >
                                ${historyItem.item}
                           </div>
                         </c:if>
                        
                        <c:if test="${!historyItem.labelCode.isLink}">
                          ${historyItem.item} 
                        </c:if>  
                     </td>
                  </tr>
                </c:forEach>
            </table>
         
         </td>
         <td  class="alignTop">${changeRequestHistory.modifiedByUser} </td>
     </tr>
 </c:forEach>
 </tbody>
</table>

</div>


