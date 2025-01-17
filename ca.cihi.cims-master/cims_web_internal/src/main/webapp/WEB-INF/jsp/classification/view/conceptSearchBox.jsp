 <%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
 <%@ include file="/WEB-INF/jsp/common/include.jsp"%>  
<form onsubmit="return false;">
<c:set var="refreshEngURL" value="${param.parentPage}?language=ENG&contextId=${param.contextId}&classification=${param.classification}&changeRequestId=${param.changeRequestId}" />
<c:set var="refreshFraURL" value="${param.parentPage}?language=FRA&contextId=${param.contextId}&classification=${param.classification}&changeRequestId=${param.changeRequestId}" />
 <div>
   	<c:if test="${param.showLanguageOptions == 'true'}">
   		<div class="inline padded">
			 <c:choose> 
				 <c:when test="${param.language=='ENG' }">
				 	<a href="javascript:switchLanguage('${refreshFraURL}');"> <fmt:message key="classification.viewer.switchLanguage" /> </a>
				 </c:when>
				 <c:when test="${param.language=='FRA' }">
				 	<a href="javascript:switchLanguage('${refreshEngURL}');"> <fmt:message key="classification.viewer.switchLanguage" /> </a>
				 </c:when>
			 </c:choose>
	 	</div>
 	</c:if>
 	<div class="inline padded">
 		<input type="radio" name="searchBy" id="searchByCode" value="code" checked/>
 		<label for="searchByCode" style="font-weight: normal;">
		 	<fmt:message key="classification.viewer.code" />
	 	</label>
	 	<input type="radio" name="searchBy" id="searchByIndex" value="bookIndex" />
	 	<label for="searchByIndex" style="font-weight: normal;">
	 		<fmt:message key="classification.viewer.index" /> 
	 	</label>
		<select id="indexElementId" name="indexElementId" class="indexList">
		     <c:forEach var="codeDescription" items="${allBookIndexes}">
		          <option value="${codeDescription.code}"> ${codeDescription.description}  </option>
		     </c:forEach>
		</select>
	</div>
	<div class="inline padded">
		<b><fmt:message key="classification.viewer.search" /></b>
		<input type="text" name="explorersearch" id="explorersearch" value="" class="searchbox"/>
	</div>
 </div>
</form>
<script type="text/javascript">
	function getRadioValue () {
    	  if( $('input[name=searchBy]:radio:checked').length > 0 ) {
    	      return $('input[name=searchBy]:radio:checked').val();
    	  }
     }
     function switchLanguage(sUrl){
    	 var key=$('#activateNode').text();
    	 window.location.href=sUrl+'&key='+key;
     }
     $(document).ready( function() {
    	    $("#indexElementId").attr("disabled", "disabled").
    	    change(function() {
    	    	EventManager.publish("searchtypechanged", null);
    	    });
    	    
    	    $('input[name=searchBy]:radio').click( function() {
    	        // Will get the newly selected value
    	        var radio_button_value = getRadioValue();
    	        if (radio_button_value=='bookIndex'){
    	        	$("#indexElementId").removeAttr("disabled");
    	        }else{
    	        	$("#indexElementId").attr("disabled", "disabled");
    	        }
    	        EventManager.publish("searchtypechanged", null);
    	    });
    	    
    	 	// it is very important to use contentType with charset=UTF-8, otherwise french characters won't be properly handled by the search
    	    var searchCallback = function(request, response) {
		        $.ajax({
		            url: "getCodeSearchResult.htm?classification=${param.classification}&contextId=${param.contextId}&language=${param.language}",
		            contentType:"application/json; charset=UTF-8",
		            data: {
		              term : request.term,
		              searchBy : $('input[name=searchBy]:radio:checked').val(),
		              indexElementId: $( "#indexElementId" ).val()
		            },
		            success: function(data) {
		              response(data);
		            }
		          });
			};
			
			var selectCallback = function(event, ui) {
				$.ajax({
                      url: "getConceptIdPathByConceptId.htm?classification=${param.classification}&contextId=${param.contextId}&conceptId=" + ui.item.conceptId,
					   success: function(data, textStatus){
						   EventManager.publish("pathselected", data);
					   }
				     }
				);
			};

    	    $("#explorersearch").autocomplete({source : searchCallback, select : selectCallback,  position: { collision : "flip none" }})
    	    .focus(function(){$(this).select();});
    	    
    	    //invoke autocomplete when user pastes the value
    	    $("#explorersearch").bind("paste", function () {
    	        setTimeout(function () {
    	            $("#explorersearch").autocomplete("search", $("#explorersearch").val());
    	        }, 0);
    	    });
    	    
    	    //invoke autocomplete when user changes the search type parameters
    	    EventManager.subscribe("searchtypechanged", function(event, data){
    	    	setTimeout(function () {
    	            $("#explorersearch").autocomplete("search", $("#explorersearch").val());
    	        }, 0);
    	    });
    	}
     );
    //# sourceURL=conceptSearchBox.jsp
</script>
