<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<style type="text/css">
   #instruction{
       height:50px;
       width:550px;
   }
 </style>
<script type="text/javascript">
  
  function closeYear(){
	
	  $('form#generateReleaseTablesCriteria').serialize();
	  $("#generateReleaseTablesCriteria")[0].action="<c:url value='/admin/closeYear.htm'/>"
	  disableAllButtons();
	  $("#generateReleaseTablesCriteria")[0].submit();
	  showProcessingScreen();
  }
  
  function disableAllButtons(){
	  $("input[type=button]").attr("disabled", "disabled");
	  $("input[type=button]").attr("class", "disabledButton");
  }
  
  function showProcessingScreen() {
		$("*").css("cursor", "progress");
		$('body').append('<div class="modal">');
	}
		
</script>


<h4 class="contentTitle">
	<fmt:message key="cims.menu.administration" /> &#62; <fmt:message key="admin.closeyear" />
	
</h4>

<div class="content">

<form:form method="POST" id="generateReleaseTablesCriteria" modelAttribute="generateReleaseTablesCriteria">
   
    <div id="instruction">
  	   To close a year, both classifications have to be frozen, all change requests must be in proper status and official release must have been completed.
    </div>	
   
   <div id ="infoMessage">
    	 <form:errors path="*" cssClass="errorMsg" />
   </div>
   <c:if test='${closeYearSuccess}'>
         <div  class="info"> Year ${closedYear} has been successfully closed
               <c:if test='${newOpenedYears!=null}'>
                  and more years have been opened up:
                    <c:forEach items="${newOpenedYears}" var="newOpenedYear">
                        ${newOpenedYear} &nbsp;
                    </c:forEach>
	                  
               </c:if>
        
         </div>
   </c:if> 
   <table  style="width: 555px;" >			
		   
		     <tr ><td>
		          <label> &nbsp;&nbsp;Year to be Closed:	&nbsp;&nbsp;&nbsp;&nbsp;</label>	
		          <font color="IndianRed"> ${generateReleaseTablesCriteria.currentOpenYear} </font>
			      <form:hidden path="currentOpenYear"/>
		       </td>	
		       <td>
		       <c:choose>
                   <c:when test='${bothClassificationFrozen && officialReleased}'>
		   	            <input type="button"  value="Close" class="button" onclick="javascript:closeYear();" >&nbsp;&nbsp; 
		   	      </c:when>
			      <c:otherwise>
			           <input type="button"  value="Close" class="disabledButton" disabled="disabled" >&nbsp;&nbsp; 
			      </c:otherwise>
			   </c:choose>   
		       </td>	
		    </tr>
	</table>
   
</form:form>
</div>

