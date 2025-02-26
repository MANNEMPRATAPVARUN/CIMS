<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<sec:authentication var="user" property="principal" />

<script type="text/javascript">
	var g_readonly=true;
	var g_changed = false;

	function isCurrentUserAssignee(){
		var returnValue = false;
		if ($('input:radio:checked').length > 0) {
			$("input[type=radio]:checked").parent().parent().find("td").each(function(index, element) {
				if(index == 3){
				  var assigneeName = $(this).html(); 
				  assigneeName = $.trim(assigneeName);
				  var currentUser = $("#currentUser").val();

				  if(currentUser == assigneeName){
					  returnValue = true;
				  }
			   }
			});
		}

		return returnValue; 
	}

	function setupIcons(){
		 if(g_readonly){
			 $("input[type='radio']").click(function() {
				if(isCurrentUserAssignee()){
					g_readonly = false;
				   $("#edit").attr("src", "<c:url value='/img/icons/Edit.png'/>");
				}
				else{
					g_readonly = true;
				   $("#edit").attr("src", "<c:url value='/img/icons/EditGrey.png'/>");
				}
			 });
		 }
	}
	
	
	$(document).ready(function() {
		if(g_readonly){
			setupIcons();		
			checkRadioButton();	
		}
	});

	function checkRadioButton(){
		var contextId = $("#contextId").val();
		var elementId = $("#elementId").val();			
		var elementVersionId = $("#elementVersionId").val();
		var id = contextId+"_"+elementId+"_"+elementVersionId;
		$("table#refsetStatus input[type=radio]").each(function(element){			
			if($(this).val() == id){
				$(this).attr("checked", true);
			}
		});

	}
	
	function enableInput() {
		if(g_readonly){return false;}

		if ($('input:radio:checked').length > 0) {
			$("input[type=radio]:checked").parent().parent().find("td").each(function(index, element) {
				if(index == 2){
					  var tmp = $(this).html(); 
			          tmp = $.trim(tmp);
	
					  var id = "id_" + $('input:radio:checked').val();
	
					  if ( !$( "#"+id ).length){         
				          var aChecked = tmp=="Active"?"selected":"";
				          var dChecked = aChecked==""?"selected":"";
				          
				          var selectHTML = '<select id="'+id+'" name="status" onchange="statusChanged();">';
				          selectHTML += '<option value="A" ' + aChecked + '>Active</option>';
				          selectHTML += '<option value="D" ' + dChecked+'>Disabled</option>';
				          selectHTML += '</select>';
				          
				          $(this).empty().append(selectHTML).append('<input type="hidden" name="status" size="60" value="'+tmp+'" />');
					 }
				}

				$('input[name=refsetElementId]').attr("disabled", true);	

				$("#saveE").attr("src", "<c:url value='/img/icons/Save.png'/>");
				$("#cancel").attr("src", "<c:url value='/img/icons/Cancel.png'/>");
					
			});
		} else {
			alert('<fmt:message key="choose.item"/>');
		}
	}

	function statusChanged(){
		var checkedRadioButton = $("table#refsetStatus input[type=radio]:checked");
		var params = checkedRadioButton.val().split("_");
		var contextId = params[0];
		var elementId = params[1];
		var elementVersionId = params[2];
		var newStatus = $("#id_" + checkedRadioButton.val()).val();

		$("#contextId").val(contextId);
		$("#elementId").val(elementId);
		$("#elementVersionId").val(elementVersionId);
		$("#newStatus").val(newStatus);

		g_changed = true;
	}
	
	function goBack(){
		var refsetCatalogPage = '<c:url value='/refset/refsetCatalog.htm'/>';
		window.location.href = refsetCatalogPage;			
	}

	function save(){
		if((!g_readonly) && g_changed){
			showLoading("<fmt:message key='progress.msg.save'/>");
		  		
	    	$("form[name=refsetStatus]")[0].action="<c:url value='/refset/refsetStatus/updateRefsetStatus.htm'/>";
	    	$("form[name=refsetStatus]")[0].method="POST";
			$("form[name=refsetStatus]")[0].submit();
		}
	}

	function updateStatusFilter(){
    	$("form[name=refsetStatus]")[0].action="<c:url value='/refset/refsetStatus/updateRefsetStatusFilter.htm'/>";
    	$("form[name=refsetStatus]")[0].method="POST";
		$("form[name=refsetStatus]")[0].submit();
		
	}

	function cancelEdit(){
		if(!g_readonly){
			checkRadioButton();
			updateStatusFilter();
			checkRadioButton();
		}
	}

</script>

<h4 class="contentTitle"> 
	<fmt:message key="cims.menu.refset" /> &#62;
	<fmt:message key="refset.satus.manage" />
</h4>


<div class="content">
<form:form method="POST" modelAttribute="refsetStatusViewBean" name="refsetStatus">	
<fieldset>
<legend>
	<fmt:message key="refset.satus.manage" />
</legend> 

 <div id ="infoMessage">
  	<form:errors path="*" cssClass="errorMsg" />    
 </div> 

<security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR','ROLE_REFSET_DEVELOPER')">   
	<table border="0" style="margin-bottom:0px; ">
		<tr>
			<td class="fieldlabel">
	  			<fmt:message key="refset.status.select" />
	  			&nbsp;&nbsp;
	  			<form:select path="statusGroup">
	  				<form:option value="">All</form:option>
	  				<form:option value="ACTIVE">Active</form:option>
	  				<form:option value="DISABLED">Disabled</form:option>
	  			</form:select>
	  			&nbsp;&nbsp;               
	        	<input id="refsetStatus" class="button" type="button" onclick="updateStatusFilter();" 
	        		value="<fmt:message key='refset.status.view.button'/>" />
	 		</td>

	  		<td style="text-align: right; ">
		       	 <img id="saveE" class="viewMode" title="SaveEdit" src="<c:url value="/img/icons/SaveGrey.png"/>"  onclick="save();" />
				 <img id="edit" class="viewMode" title="Edit" src="<c:url value="/img/icons/EditGrey.png"/>" onclick="enableInput();" />				 
				 <img id="cancel" class="viewMode" title="Cancel" src="<c:url value="/img/icons/CancelGrey.png"/>"  onclick="cancelEdit();" />        
			     <img id="back" class="viewMode" title="Back" src="<c:url value="/img/icons/Back.png"/>" onclick="goBack();" />
	  		</td> 
		</tr>
	</table>

	<c:if test="${not empty refsetStatusViewBean.refsetVersionList}">

		<form:input type="hidden" id="contextId" name="contextId" path="contextId" value=""/>
		<form:input type="hidden" id="elementId" name="elementId" path="elementId" value=""/>
		<form:input type="hidden" id="elementVersionId" name="elementVersionId" path="elementVersionId" value=""/>
		<form:input type="hidden" id="newStatus" name="newStatus" path="newStatus" value=""/>

		<input type="hidden" id="currentUser" name="currentUser" value="${sessionScope.currentUser.username}">
		
  		<display:table name="refsetStatusViewBean.refsetVersionList" id="refsetStatus" class="listTable" defaultsort="2"
			style="width: 100%; table-layout:fixed;" requestURI="" excludedParams="*">
				
			<display:column class="refsetElementId" headerClass="tableHeader sizeThirty" > 
				<input type="radio" id="refsetElementId" name="refsetElementId" value="${refsetStatus.contextIdentifier.elementVersionId}_${refsetStatus.elementIdentifier.elementId}_${refsetStatus.elementIdentifier.elementVersionId}" />
			</display:column>
		
			<display:column property="refsetName" class="refsetName" sortable="true" titleKey="refset.status.refset.name" headerClass="tableHeader" />
		
		 	<display:column sortable="true" class="status" titleKey="refset.status.refset.status" headerClass="tableHeader sizeTwoFifty" style="text-align:center;">			
				${cims:capitalizeFully(refsetStatus.refsetStatus.status)}
			</display:column>

			<display:column property="assigneeName" class="assigneeName" headerClass="tableHeaderHidden" style="display:none;" />

  		</display:table>

	</c:if>
</security:authorize>

</fieldset>
</form:form>
</div>

