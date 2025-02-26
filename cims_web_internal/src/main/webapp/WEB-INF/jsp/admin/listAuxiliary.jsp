<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<style type="text/css">
#auxTableValue input[type="text"] {
     width: 100%; 
     box-sizing: border-box;
     -webkit-box-sizing:border-box;
     -moz-box-sizing: border-box;
}
</style>

<script type="text/javascript">

	var g_classification=${auxiliaryViewBean.classification};
	var g_readonly="${readonly}"=="true";

	$(document).ready(function() {
		if(!g_readonly){
			enableAdd();
		}
	});
	
	function enableAdd() {
		if(g_readonly){return;}
		var enableAdd = $("#enableAdd").text();
		if (enableAdd == 'Y') {
			$("#add").attr("src", "<c:url value='/img/icons/Add.png'/>");	
		} 
	}
	
	function submitForm(){
		showLoading("<fmt:message key='progress.msg.save'/>");
		document.auxiliary.submit();
	}
	
	function enableInput() {
		if(g_readonly){return false;}
		if ($('input:radio:checked').length > 0) {
			 $("input[type=radio]:checked").parent().parent().find("td").each(function(index, element) {
			      if(index == 2 ) {
			          var tmp = $(this).html();
			          $(this).empty()
			          	.append('<input type="text" name="auxEngLable" size="60" value="'+tmp+'" />')
			            .append('<input type="hidden" name="auxEngLable" size="60" value="'+tmp+'" />');
			      } else if((index == 3 && !g_classification) || (index == 4 && g_classification)) {
			          var tmp = $(this).html();
			          $(this).empty()
			          .append('<input type="text" name="auxEngDesc" size="60" value="'+tmp+'" />')
			          .append('<input type="hidden" name="auxEngDesc" size="60" value="'+tmp+'" />');
			      } else if(index == 3 && g_classification ) {
			          var tmp = $(this).html();
			          $(this).empty()
			          .append('<input type="text" name="auxFraLable" size="60" value="'+tmp+'" />')
			          .append('<input type="hidden" name="auxFraLable" size="60" value="'+tmp+'" />');
			      }else if(index == 5 && g_classification) {
			          var tmp = $(this).html();
			          $(this).empty()
			          	.append('<input type="text" name="auxFraDesc" size="60" value="'+tmp+'" />')
			          	.append('<input type="hidden" name="auxFraDesc" size="60" value="'+tmp+'" />');
			      } else if((index == 4 && !g_classification) || (index == 6 && g_classification)) {
			          var tmp = $(this).html();
			          tmp = $.trim(tmp);
			          
			          var aChecked = tmp=="Active"?"selected":"";
			          var dChecked = aChecked==""?"selected":"";
			          
			          var selectHTML = '<select name="status">';
			          selectHTML += '<option value="A" ' + aChecked + '>Active</option>';
			          selectHTML += '<option value="D" ' + dChecked+'>Disabled</option>';
			          selectHTML += '</select>';
			          
			          $(this).empty()
			          .append(selectHTML)
			          .append('<input type="hidden" name="status" size="60" value="'+tmp+'" />');
			      }      
			  });    
			  $(".editMode").hide();
			  $(".viewMode").show();
			  $('input[name=auxTableValueId]').attr("disabled", true);	 
		} else {
			alert('<fmt:message key="choose.item"/>');
		}
	}
	
	function appendToTable() {
		if(g_readonly){return false;} 
		$("#actionMode").text('add');
	    var insertLine = '<tr>';
		insertLine += '<td class="auxTableValueId"></td>';
		insertLine += '<td class="auxValueCode"><input type="text" name="auxValueCode" size="5" maxlength="3"/></td>';
		insertLine += '<td class="auxEngLable"><input type="text" name="auxEngLable" size="60" maxlength="50"/></td>';
		if(g_classification){
			insertLine += '<td class="auxFraLable"><input type="text" name="auxFraLable" size="60" maxlength="50"/></td>';
		}
		insertLine += '<td class="auxEngDesc"><input type="text" name="auxEngDesc" size="60" maxlength="255"/></td>';
		if(g_classification){
			insertLine += '<td class="auxFraDesc"><input type="text" name="auxFraDesc" size="60" maxlength="255"/></td>';
		}
		insertLine += '<td class="auxStatus" style="text-align:center;"><select name="status"><option value="A">Active</option></select></td>';
		insertLine += '</tr>';
	    	
		$("table#auxTableValue").find("tbody").prepend(insertLine);
		$("table#auxTableValue").find("tbody").find("tr:odd").attr("class","odd");
		$("table#auxTableValue").find("tbody").find("tr:even").attr("class","even");
	    
	    $(".editMode").hide();
	    $(".hiddenMode").show();
	    $('input[name=auxTableValueId]').attr("disabled", true);
	}
	
	function cancelChange() {
		var actionMode = $("#actionMode").text();
		commit();
		if (actionMode == 'add') {
			$('#auxTableValue tbody tr:first').remove();			
		} else {
			var tr= $("input[type=radio]:checked").parent().parent();
			tr.find("input[type=text]").remove();
			tr.find("select").remove();
			tr.find("input[type=hidden]").each(function(index, element) {
				 var hidden = $(this);
				 hidden.parent().empty().append(hidden.val());
				 hidden.remove();
			});
		}	

		$('input[name=auxTableValueId]').attr("disabled", false);	
	}

	function commit(){
		$("#actionMode").text('');		
		$(".hiddenMode").hide();
		$(".viewMode").hide();
		$(".editMode").show();
		$('input[name=auxTableValueId]').attr("disabled", false);
	}

	function getClassificationParam(){
		return "&classification="+(g_classification?"true":"false");
	}

	function getEditUrl(){
		var editUrl = "auxiliary.htm";
		editUrl += "?action=change";
		editUrl += getClassificationParam();
		editUrl += "&auxEngLable=" + encodeURI($('input[name=auxEngLable]').val());
		if(g_classification){
			editUrl += "&auxFraLable=" + encodeURI($('input[name=auxFraLable]').val());
		}
		editUrl += "&auxEngDesc=" + encodeURI($('input[name=auxEngDesc]').val());
		if(g_classification){
			editUrl += "&auxFraDesc=" + encodeURI($('input[name=auxFraDesc]').val());
		}
		editUrl += "&status=" +encodeURI ($('select[name=status]').find('option:selected').val());
		return editUrl; 
	}

	function checkRequiredFields(checkCode){
		return true;
	}
	
	function saveChange(){
		if(g_readonly){return false;}
		if(!checkRequiredFields(false)){return false;}
		var editUrl = getEditUrl();
		editUrl += "&auxId=" + $('input[name=auxTableValueId]:checked').val();
		talert(editUrl);
		if (editUrl.toLowerCase().indexOf("undefined") >= 0) {		
			alert('<fmt:message key="choose.item"/>');
		} else{
			showLoading("<fmt:message key='progress.msg.save'/>");
			AjaxUtil.ajax(
				editUrl,
				null,
				function(response){
					if (response.status == 'FAIL') {
						showErrorMessagesFromResponseEx(response);
					}else{
						var tr= $("input[type=radio]:checked").parent().parent();
						applyValues(tr);
						commit();
						showSuccessMessageEx("Successfully updated");
					}
					hideLoading();
				}
			);
		}
	}

	function applyValues(tr){
		var auxEngLable=$('input[name=auxEngLable]').val();
		var auxFraLable=$('input[name=auxFraLable]').val();
		var auxEngDesc=$('input[name=auxEngDesc]').val();
		var auxFraDesc=$('input[name=auxFraDesc]').val();
		var auxStatus=$('select[name=status]').find('option:selected').val()=="A"?"Active":"Disabled";
		
		//alert(auxEngLable);
		tr.find("td.auxEngLable").empty().append(auxEngLable);
		tr.find("td.auxFraLable").empty().append(auxFraLable);
		tr.find("td.auxEngDesc").empty().append(auxEngDesc);
		tr.find("td.auxFraDesc").empty().append(auxFraDesc);
		tr.find("td.auxStatus").empty().append(auxStatus);
	}
	
	function saveInsert() {
		if(g_readonly){return false;}
		if(!checkRequiredFields(true)){return false;}
		var code=$('input[name=auxValueCode]').val();
		var insertUrl = getEditUrl();
		insertUrl += "&auxId=&auxValueCode="+code;

		showLoading("<fmt:message key='progress.msg.save'/>");
		talert(insertUrl);

		AjaxUtil.ajax(
			insertUrl,
			null,
			function(response){
				//alert(data);
				if (response.status == 'FAIL') {
					showErrorMessagesFromResponseEx(response);
				}else{
					var newId=response.value;
					var tr=$("table#auxTableValue").find("tbody").find("tr").first();
					tr.find("td.auxTableValueId").empty().append('<input type="radio" name="auxTableValueId" value="'+newId+'"/>');
					tr.find("td.auxValueCode").empty().append(code);
					applyValues(tr);
					commit();
					setupRadios();
					showSuccessMessageEx("Successfully inserted");
				}
				hideLoading();
			}
		);
	}
	
	function deleteAux() {
		if(g_readonly){return;}
		var url="auxiliary.htm?action=delete&auxId="+$('input[name=auxTableValueId]:checked').val();
		url+=getClassificationParam();
		talert(url);
		if (url.toLowerCase().indexOf("undefined") >= 0) {		
			alert('<fmt:message key="choose.item"/>');
		} else {
			var r = confirm('<fmt:message key="admin.aux.confirm.delete"/>');	
			if (r==true) {
				showLoading("<fmt:message key='progress.msg.remove'/>");		
				AjaxUtil.ajax(
					url,
					null,
					function(response){
						//alert(data);
						if (response.status == 'FAIL') {
							showErrorMessagesFromResponseEx(response);
						}else{
							var tr= $("input[type=radio]:checked").parent().parent();
							tr.remove();
							commit();
							showSuccessMessageEx("Successfully deleted");
						}
						hideLoading();
					}
				);
			}
		}
	}

	function talert(msg){
		//alert(msg);
	}
</script>

<h4 class="contentTitle"> 
	<fmt:message key="cims.menu.administration" /> &#62;
	<c:choose>
		<c:when test="${auxiliaryViewBean.classification}">
			<fmt:message key="cims.menu.admin.sub.manage.auxiliary.classifications" />
		</c:when>
		<c:otherwise>
			<fmt:message key="cims.menu.admin.sub.manage.auxiliary.change.request" />
		</c:otherwise>
	</c:choose>
</h4>
<div class="content">
<form:form method="POST" modelAttribute="auxiliaryViewBean" name="auxiliary" action="auxiliary.htm">
	<form:hidden path="classification"/>
	<label id="enableAdd" style="display: none;">${enableAdd}</label>
	<label id="actionMode" style="display: none;"></label>
	
<fieldset>

<legend>
	<c:choose>
		<c:when test="${auxiliaryViewBean.classification}">
			<fmt:message key="cims.menu.admin.sub.manage.auxiliary.classifications" />
		</c:when>
		<c:otherwise>
			<fmt:message key="cims.menu.admin.sub.manage.auxiliary.change.request" />
		</c:otherwise>
	</c:choose>
</legend> 

<spring:bind path="*">
	<div class="errorMsg">
		<ul>
			<c:forEach var="error" items="${status.errorMessages}">
				<li><c:out value="${error}" escapeXml="false"/></li>
			</c:forEach>
		</ul>
	</div>
</spring:bind>

<div style="width: 100%; overflow: hidden;">
    <div style="float: left;">
    	<div id="loadingInfo" class="info" style="display: none; width: 800px; padding-top: 0.5em;padding-bottom: 0.5em;">Loading</div>
    </div>
    <div style="float: right;">&nbsp;</div>
</div>

<security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR')">   
	<table border="0" style="margin-bottom:0px; ">
	     <tr>
	  		<td class="fieldlabel">
	  			<fmt:message key="admin.auxiliary.table" />
	  			&nbsp;&nbsp;
	  			<form:select path="auxCode" items="${auxList}" itemLabel="value" itemValue="key" />
	  			<c:if test="${auxiliaryViewBean.classification}">
		  			&nbsp;&nbsp;
	  				<fmt:message key="admin.auxiliary.year" />
		  			&nbsp;&nbsp;
		  			<form:select path="year" items="${auxYearsList}" itemLabel="value" itemValue="key" />
	  			</c:if>
	  			&nbsp;&nbsp;               
	        	<input id="auxButton" class="button" type="button" onclick="submitForm();" 
	        		value="<fmt:message key='admin.auxiliary.get.button'/>" />
	        </td>
	        <td style="text-align: right; ">
		       	 <img id="saveE" class="viewMode" title="SaveEdit" src="<c:url value="/img/icons/Save.png"/>" onclick="saveChange();" style="display: none;"  />
		       	 <img id="saveI" class="hiddenMode" title="SaveInsert" src="<c:url value="/img/icons/Save.png"/>" onclick="saveInsert();" style="display: none;" />			 
				 <img id="edit" class="editMode" title="Edit" src="<c:url value="/img/icons/EditGrey.png"/>" onclick="enableInput();" />				 
				 <img id="add" class="editMode" title="Add" src="<c:url value="/img/icons/AddGrey.png"/>" onclick="appendToTable();" />
				 <c:choose>	
				 <c:when test="${auxiliaryViewBean.auxCode != 'REFSETCATEGORY'}">	   
			     <img id="delete" class="editMode" title="Delete" src="<c:url value="/img/icons/RemoveGrey.png"/>" onclick="deleteAux();" />
			     </c:when>
			     <c:otherwise>
			     <img id="delete" class="editMode" title="Delete" src="<c:url value="/img/icons/RemoveGrey.png"/>" />
			     </c:otherwise>
			     </c:choose>
				 <img id="cancel" class="hiddenMode viewMode" title="Cancel" src="<c:url value="/img/icons/Cancel.png"/>" 
				 	onclick="cancelChange()" style="display: none;"/>        
	        </td> 
		</tr>  
	</table>
</security:authorize>
    <c:if test="${not empty auxiliaryViewBean.auxTableValues}">
	<display:table name="auxiliaryViewBean.auxTableValues" id="auxTableValue" class="listTable" defaultsort="2"
		style="width: 100%; table-layout:fixed;" requestURI="" excludedParams="*">
		<display:column class="auxTableValueId" headerClass="tableHeader sizeThirty" > 
			<input type="radio" name="auxTableValueId" value="${auxTableValue.auxTableValueId}" 
			<c:if test="${auxiliaryViewBean.firstRecord}">
				disabled = "disabled"
			</c:if>/>
		</display:column>
		<display:column property="auxValueCode" class="auxValueCode" sortable="true" titleKey="admin.aux.value.code" headerClass="tableHeader sizeEighty"/>
		<display:column property="auxEngLable"  class="auxEngLable" sortable="true" titleKey="admin.aux.english.lable" headerClass="tableHeader"/>
		<c:if test="${auxiliaryViewBean.classification}">
			<display:column property="auxFraLable" class="auxFraLable" sortable="true" titleKey="admin.aux.french.lable" headerClass="tableHeader"/>
		</c:if>
		<display:column property="auxEngDesc" class="auxEngDesc" sortable="true" titleKey="admin.user.english.meaning" headerClass="tableHeader" style="word-wrap:break-word;" />
		<c:if test="${auxiliaryViewBean.classification}">
			<display:column property="auxFraDesc" class="auxFraDesc" sortable="true" titleKey="admin.user.french.meaning" headerClass="tableHeader" style="word-wrap:break-word;" />
		</c:if>
		<c:choose>
		<c:when test="${auxiliaryViewBean.firstRecord}">
		<display:column sortable="true" class="auxStatus" titleKey="admin.aux.status" headerClass="tableHeader sizeEighty" style="text-align:center;">			
			${' '}
		</display:column>
		</c:when>
		<c:otherwise>
		<display:column sortable="true" class="auxStatus" titleKey="admin.aux.status" headerClass="tableHeader sizeEighty" style="text-align:center;">			
			${cims:statusConvertFromLetter(auxTableValue.status)}
		</display:column>
		</c:otherwise>
		</c:choose>
	</display:table>
    </c:if>
</fieldset> 

</form:form>
</div>

<script>
	/*********************************************************************************************************
	 * NAME:          Radio Button clicked
	 * DESCRIPTION:   
	 *********************************************************************************************************/
	 function setupRadios(){
		 if(!g_readonly){
			 $("input[type='radio']").click(function() {
				 $("#edit").attr("src", "<c:url value='/img/icons/Edit.png'/>");
				 if (${auxiliaryViewBean.auxCode != 'REFSETCATEGORY'}){
				 	$("#delete").attr("src", "<c:url value='/img/icons/Remove.png'/>");
			 	}
			 });
		 }
	}
	setupRadios();
</script>