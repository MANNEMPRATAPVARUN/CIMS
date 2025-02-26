<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<style>
<!--
#createProgress {
    position: relative;
    width: 100%;
    height: 30px;
    background-color: grey;
}
#createBar {
    position: absolute;
    width: 1%;
    height: 100%;
    background-color: green;
}
#createLabel {
    text-align: center; 
    line-height: 30px; 
    color: white;
}
-->
</style>
<script>
	$(document).ready(function(){
		$('a').click(function (event){
			if($(this).attr("href") == "#") {
				event.preventDefault();
				return false;
			}
		});
		$("#save").click(function(event) {
			event.preventDefault();
		});
		$("#drop").click(function(event) {
			event.preventDefault();
		});
		$("#assign").click(function(event) {
			event.preventDefault();
		});
		$("#close").click(function(event) {
			event.preventDefault();
		});
		$("#create").click(function(event) {
			event.preventDefault();
		});

		$('input[type=radio][name=versionType]').change(function(event){
			var $id = $(this).attr('id');
			if($id==='major'){
				$('#newVersionFrom').removeAttr('disabled');
				$('#newVersionTo').removeAttr('disabled');
				$('#newICDYear').removeAttr('disabled');
				$('#newCCIYear').removeAttr('disabled');
				$('#newSCTVersion').removeAttr('disabled');
			}else{
				$('#newVersionFrom').attr('disabled', 'disabled');
				$('#newVersionTo').attr('disabled', 'disabled');
				$('#newICDYear').removeAttr('disabled');
				$('#newCCIYear').removeAttr('disabled');
				$('#newSCTVersion').removeAttr('disabled');
			}
			enableCreateNewVersionConfirm();
		});

		resetVersionFields();

		$("#assign_refset_dialog").dialog({
	          autoOpen: false,
	          height: 200,
	          width: 420,
	          modal: true,
	          zIndex: 10000,
	          appendTo: "#refsetEditDetailForm",
	          buttons: {
	              "Assign": function() {
	            	$(this).appendTo('form#refsetEditDetailForm');
	            	assignRefset(); 
	               $( this ).dialog( "close" );
	               $(this).remove();
	              },
	              Cancel: function() {
	                $( this ).dialog( "close" );
	              }
	          }
	      });

		$("#create_newversion_dialog").dialog({
	          autoOpen: false,
	          height: 300,
	          width: 550,
	          modal: true,
	          zIndex: 10000,
	          appendTo: "#refsetEditDetailForm",
	          close: function() {
					resetVersionFields();
			  }
	      });

		$("#drop").click(function() {	
			$("#removalConfirmation").text('Please confirm that you want to permanently drop the refset from the system.');

			$('#removalConfirmation').dialog({
				title : 'Confirmation: Drop Refset',
				width : 350, height : 150, modal : true, resizable : false, draggable : false, zIndex: 10000,	
				buttons : [ {
					text : 'Yes',
					click : function() {
						$(this).dialog('close');
						dropRefset();
					}
				}, {
					text : 'No',
					click : function() {
						$(this).dialog('close');
					}
				} ]
			});
		});

		$("#close").click(function() {	
			$("#closeConfirmation").text('Do you want to close this Refset Version ?');

			$('#closeConfirmation').dialog({
				title : 'Close Refset Version',
				width : 350, height : 150, modal : true, resizable : false, draggable : false, zIndex: 10000,	
				buttons : [ {
					text : 'YES',
					click : function() {
						$(this).dialog('close');
						closeRefset();
					}
				}, {
					text : 'NO',
					click : function() {
						$(this).dialog('close');
					}
				} ]
			});
		});


		$("#create").click(function(){
			openCreateNewVersionDialogBox();
		});
		
	});

	var interval;

	function startProgress() {
	    var width = 1;
	    interval = setInterval(progress, 10);
		function progress() {
	        if (width >= 100) {
	            width=1;
	        } else {
	            width++;
	            $('#createBar').css("width",width + '%');
	        }
	    }
	}

	function resetVersionFields(){
		$("#newVersionSystemMessage").html('');
		$('input[type=radio][name=versionType]').attr('checked', false);
		$('#newVersionFrom').attr('disabled', 'disabled');
		$('#newVersionTo').attr('disabled', 'disabled');
		$('#newICDYear').attr('disabled', 'disabled');
		$('#newCCIYear').attr('disabled', 'disabled');
		$('#newSCTVersion').attr('disabled', 'disabled');
		$('#newVersionFrom').val('${viewBean.effectiveYearFrom}');
		$('#newVersionTo').val('${viewBean.effectiveYearTo}');
		$('#newICDYear').val('${viewBean.ICD10CAContextInfo}');
		$('#newCCIYear').val('${viewBean.CCIContextInfo}');
		$('#newSCTVersion').val('${viewBean.SCTVersionCode}');
		disableCreateNewVersionConfirm();
		$('#createBar').css("width",'1%');
		clearInterval(interval);
		$( "#createProgress" ).hide();
		
		
		
	}
	function openCreateNewVersionDialogBox(){
		$('#create_newversion_dialog').dialog("open");
	}

	function disableCreateNewVersionConfirm(){
		 //$("body").css("cursor", "progress");
		$("#confirm").attr('src', '<c:url value="/img/icons/OkGrey.png" />');
		 $("#confirm").unbind('click');
		 $("#reset").attr('src', '<c:url value="/img/icons/ResetGrey.png" />');
		 $("#reset").unbind('click');
		 
	}

	function enableCreateNewVersionConfirm(){
		$("#confirm").attr('src', '<c:url value="/img/icons/Ok.png" />');
		$("#confirm").unbind('click').click(function() {
			 createNewVersion();
	    });
		$("#reset").attr('src', '<c:url value="/img/icons/Reset.png" />');
		 $("#reset").unbind('click').click(function() {
			 resetVersionFields();
		 });
		
		$( "#createProgress" ).hide();
	}
		
	function createNewVersion(){
		 disableCreateNewVersionConfirm();
		 $( "#createProgress" ).show();    
		 startProgress();
    	 $('#actionType').remove();
		  $input = $('<input type="hidden" id="actionType" name="actionType"/>').val("CREATE");
		  $('#refsetEditDetailForm').append($input);
		  var $data = $("#refsetEditDetailForm").serialize();
		  $data+='&versionType='+$('input[type=radio][name=versionType]:checked').val();
		  $data+='&effectiveYearFrom='+$('#newVersionFrom').val();
		  $data+='&effectiveYearTo='+$('#newVersionTo').val();
		  $data+='&ICD10CAContextInfo='+$('#newICDYear').val();
		  $data+='&CCIContextInfo='+$('#newCCIYear').val();
		  $data+='&SCTVersionCode='+$('#newSCTVersion').val();
		  $.ajax({
			  	url: "<c:url value='/refset/refsetEditDetail.htm'/>",
				data: $data,
				type: "POST",
		        async: true,
		        cache: false,
		        success: function(data){
					if (data.status=='SUCCESS'){
						//var newAssigneeText = $("#newAssignee").val();
						//$("#assignee").html(newAssigneeText);	 	
						//disableAllButtons();
						var editPage = "<c:url value='/refset/refsetEditDetail.htm?contextId="+data.contextId+"&elementId="+data.elementId+"&elementVersionId="+data.elementVersionId + "'/>";
				 		window.location.href = editPage;		
					}else {
						var errorMessages = "";
						for (var i = 0; i < data.errors.length; i++) {
							var item = data.errors[i];
							errorMessages += item;
							errorMessages += "<br/>";
						}
						$("#newVersionSystemMessage").html(errorMessages);
						$('#createBar').css("width",'1%');
						clearInterval(interval);
						enableCreateNewVersionConfirm();
					}
		        },
		        error: function(data){  
		        	var errorMessages = "System error occurred, please contact System administrator,";
		        	$("#newVersionSystemMessage").html(errorMessages);
		        	$('#createBar').css("width",'1%');
		    		clearInterval(interval);
		        	enableCreateNewVersionConfirm();
		        }	
		  });
    }

	function toggle(element) {
		 var toggle = $(element);
		 var section = toggle.parents(".section");
		 toggleSection(section, true);
	}

	function toggleSection(section, animate) {
		 if(!(section instanceof jQuery)) {
			 section = $(section);
		 }
		 var toggle = section.find(".sectionHeader > a > img");
		 var sectionContent = section.find(".sectionContent").first();
		 var isHidden = isSectionHidden(section);
		 if (isHidden){
		     (animate ? sectionContent.slideDown() : sectionContent.show());
	         toggle.attr("src", "<c:url value='/img/icons/Expand.png'/>");
		 }else{
			 (animate ? sectionContent.slideUp() : sectionContent.hide());
			 toggle.attr("src", "<c:url value='/img/icons/Collapse.png'/>");
		 }
		 $.cookie("section."+section.attr('name')+".collapsed",!isHidden);
	}

	function isSectionHidden(section) {
		if(!(section instanceof jQuery)) {
			 section = $(section);
		 }
		return section.find(".sectionContent").first().is(":hidden");
	}

	function openAssignDialogBox(){
		 $("#assign_refset_dialog").dialog("open");
    }

	function assignRefset(){
		  $('#actionType').remove();
		  $input = $('<input type="hidden" id="actionType" name="actionType"/>').val("ASSIGN");
		  $('#refsetEditDetailForm').append($input);
		  $.ajax({
			    url: "<c:url value='/refset/refsetEditDetail.htm'/>",
		        data: $("#refsetEditDetailForm").serialize(),
		        type: "POST",
		        async: false,
		        cache: false,
		        success: function(data){
					if (data.status=='SUCCESS'){
						//var newAssigneeText = $("#newAssignee").val();
						//$("#assignee").html(newAssigneeText);	 	
						//disableAllButtons();
						var editPage = "<c:url value='/refset/refsetEditDetail.htm?contextId="+${viewBean.contextId}+"&elementId="+${viewBean.elementId}+"&elementVersionId="+${viewBean.elementVersionId} + "'/>";
				 		window.location.href = editPage;		
					}else {
						var errorMessages = "";
						for (var i = 0; i < data.errors.length; i++) {
							var item = data.errors[i];
							errorMessages += item;
							errorMessages += "<br/>";
						}
						$("#refSetSystemMessage").html(errorMessages);
					}
		        },
		        error: function(data){  	
		        	var errorMessages = "System error occurred, please contact System administrator,";
		        	$("#refSetSystemMessage").html(errorMessages);
		        }		       
		  });
	 }

	 function disableAllButtons(){
		  $("input[type=submit]").attr("disabled", "disabled");
		  $("input[type=submit]").attr("class", "disabledButton");
	  }

	function saveRefset() {
		$("#refSetSystemMessage").html("");
		$("#updatedSuccessfully").html("");
		$('#actionType').remove();
		$input = $('<input type="hidden" id="actionType" name="actionType"/>').val("SAVE");
		$('#refsetEditDetailForm').append($input);
		$.ajax({
		        url: "<c:url value='/refset/refsetEditDetail.htm'/>",
		        data: $("#refsetEditDetailForm").serialize(),
		        type: "POST",
		        async: false,
		        cache: false,
		        success: function(data){
					if (data.status=='SUCCESS'){
						//alert("Saved!"); 	
						$("#updatedSuccessfully").html("Changes have been updated successfully."); 	
					}else {
						var errorMessages = "";
						for (var i = 0; i < data.errors.length; i++) {
							var item = data.errors[i];
							errorMessages += item;
							errorMessages += "<br/>";
						}
						$("#refSetSystemMessage").html(errorMessages);		
						if (data.errorType=='REVOKED'){
							disableForm();
							disableAllButtons();
					    }
					    	
					}
		        },
		        error: function(data){  	
		        	var errorMessages = "System error occurred, please contact System administrator.";
		        	$("#refSetSystemMessage").html(errorMessages);
		        }		       
		    });
	  }	

	function dropRefset() {
		$('#actionType').remove();
		$input = $('<input type="hidden" id="actionType" name="actionType"/>').val("DROP");
		$('#refsetEditDetailForm').append($input);
		$.ajax({
		        url: "<c:url value='/refset/refsetEditDetail.htm'/>",
		        data: $("#refsetEditDetailForm").serialize(),
		        type: "POST",
		        async: false,
		        cache: false,
		        success: function(data){
					if (data.status=='SUCCESS'){						
						var summaryPage = "<c:url value='/refset/refsetCatalog.htm'/>";	
				 		window.location.href = summaryPage;		 			 				 			
					}else {
						var errorMessages = "";
						for (var i = 0; i < data.errors.length; i++) {
							var item = data.errors[i];
							errorMessages += item;
							errorMessages += "<br/>";
						}
						$("#refSetSystemMessage").html(errorMessages);
					}
		        },
		        error: function(data){  	
		        	var errorMessages = "System error occurred, please contact System administrator";
		        	$("#refSetSystemMessage").html(errorMessages);
		        }		       
		    });
	  }	

	function closeRefset() {
		$("#refSetSystemMessage").html("");
		$("#updatedSuccessfully").html("");
		$('#actionType').remove();
		$input = $('<input type="hidden" id="actionType" name="actionType"/>').val("CLOSE");
		$('#refsetEditDetailForm').append($input);
		$.ajax({
		        url: "<c:url value='/refset/refsetEditDetail.htm'/>",
		        data: $("#refsetEditDetailForm").serialize(),
		        type: "POST",
		        async: false,
		        cache: false,
		        success: function(data){
					if (data.status=='SUCCESS'){												 
						$("#versionStatus").html("CLOSED");
						disableForm();
						disableButton("#save");
						disableButton("#drop");
						disableButton("#close");
						enableButton("#create"); 		 			
					}else {
						var errorMessages = "";
						for (var i = 0; i < data.errors.length; i++) {
							var item = data.errors[i];
							errorMessages += item;
							errorMessages += "<br/>";
						}
						$("#refSetSystemMessage").html(errorMessages);
					}
		        },
		        error: function(data){  	
		        	var errorMessages = "System error occurred, please contact System administrator";
		        	$("#refSetSystemMessage").html(errorMessages);
		        }		       
		    });
	  }	

	 function disableButton(buttonName){
		 $(buttonName).attr("disabled", "disabled");
		 $(buttonName).attr("class", "disabledButton");
	 }

	 function enableButton(buttonName){
		 $(buttonName).removeAttr('disabled');
		 $(buttonName).attr("class", "button");
	 }
	 
	 function disableForm(){
		  $("#refsetNameFRE").attr("disabled", "disabled");
		  $("#categoryId").attr("disabled", "true");
		  $("#definition").attr("disabled", "disabled");
		  $("#notes").attr("disabled", "disabled");
	  }
	
</script>

<div id="removalConfirmation" style="display: none;">
	confirm
</div>
<div id="closeConfirmation" style="display: none;">
	confirm
</div>
<span id="refSetSystemMessage" class="errorMsg"></span>        
<span id="updatedSuccessfully" class="updateMsg"></span>          

 <div class="content">
	<form:form method="POST" modelAttribute="viewBean" id="refsetEditDetailForm" >
	      <p>&nbsp;</p>
	      <div align="right">
		     <c:choose>
		    	<c:when test="${refsetSavePermission=='WRITE'}">
		 			<input class="button" type="submit" name="save" id="save" value="Save" onclick="saveRefset()"/>
		 		</c:when>
		        <c:otherwise>
		    		<input class="disabledButton" type="submit" name="save" id="save" value="Save" disabled />
		    	 </c:otherwise>
		     </c:choose> 
		     <c:choose>
		                                                                                                                               
		    	<c:when test="${refsetAssignPermission=='WRITE'}">					
					<input class="button" type="submit" name="assign" id="assign" value="Assign" onclick="javascript:openAssignDialogBox();"/>
                </c:when>
                <c:otherwise>	
                	<input class="disabledButton" type="submit" name="assign" id="assign" value="Assign" disabled />
                </c:otherwise>	
             </c:choose> 	
             <c:choose>	
              	<c:when test="${refsetDropPermission=='WRITE'}">						
					<input class="button" type="submit" name="drop" id="drop" value="Drop Refset Version" />
				</c:when>
				<c:otherwise>
				    <input class="disabledButton" type="submit" name="drop" id="drop" value="Drop Refset Version" disabled />
				</c:otherwise>	
			</c:choose> 	
			<c:choose>	
              	<c:when test="${refsetClosePermission=='WRITE'}">						
					<input class="button" type="submit" name="close" id="close" value="Close Refset Version" />
				</c:when>
				<c:otherwise>
				    <input class="disabledButton" type="submit" name="close" id="close" value="Close Refset Version" disabled />
				</c:otherwise>	
			</c:choose> 				
			<c:choose>	
		        <c:when test="${refsetCreatePermission=='WRITE'}">           						
					<input class="button" type="submit" name="create" id="create" value="Create New Version"/>
				</c:when>
				<c:otherwise>
				    <input class="disabledButton" type="submit" name="create" id="create" value="Create New Version" disabled />
				</c:otherwise>	
			</c:choose> 
			
			
  	    </div>
   	    <p>&nbsp;</p>  
        <div class="section" style="padding: 2px;" name="refsetInfo"> 
        <div class="sectionHeader">
            <a href="#"><img src="/cims_web_internal/img/icons/Expand.png" alt="Toggle" onclick="javascript:toggle(this);" style="vertical-align: middle;"/></a>
            <div style="display: inline-block; vertical-align: middle;"><fmt:message key="refset.edit.detail.header.name" /></div>
        </div>
        <div class="sectionContent">
         <table style="width:100%;">	
        	<tr>
        		<td align="left" colspan="12">Refset Code <span class="required">*</span>:&nbsp;${viewBean.refsetCode}</td>
        	</tr>
        	<tr>
        		<td align="left" colspan="6">Refset Name (English) <span class="required">*</span>:&nbsp;${viewBean.refsetNameENG}</td>
             
             	<c:choose>
		            <c:when test='${viewBean.readOnly}'>
		               <td align="left" colspan="6">Refset Name (French) :&nbsp;<input id="refsetNameFRE" name="refsetNameFRE" value="${viewBean.refsetNameFRE}" size="50" maxlength="100" disabled/></td>
		            </c:when>
		            <c:otherwise>
		               <td align="left" colspan="6">Refset Name (French) :&nbsp;<input id="refsetNameFRE" name="refsetNameFRE" value="${viewBean.refsetNameFRE}" size="50" maxlength="100"/></td>
		            </c:otherwise>
		         </c:choose>         
             	
        	</tr>
        	<tr>
        		<td align="left" colspan="3">Refset Effective Year From <span class="required">*</span>:&nbsp;${viewBean.effectiveYearFrom}</td>
       			<td align="left" colspan="3">Refset Effective Year To :&nbsp;${viewBean.effectiveYearTo}</td>
       			<td align="left" colspan="6">Catalog Category <span class="required">*</span>:&nbsp;
		       		<c:choose>
		            <c:when test='${viewBean.readOnly}'>
		              <%-- ${viewBean.categoryName} --%>
		              <form:select path="categoryId" disabled="true">
		                    <c:forEach items="${CategoryList}" var="category"  varStatus="loop">	                    
						    	<option <c:if test="${category.auxTableValueId eq viewBean.categoryId}">selected="selected"</c:if>  value="${category.auxTableValueId}">${category.auxEngLable}</option>				    	
						    </c:forEach>
				      </form:select>	   
		            </c:when>
		            <c:otherwise>
		            	<form:select path="categoryId">
		                    <c:forEach items="${CategoryList}" var="category"  varStatus="loop">	                    
						    	<option <c:if test="${category.auxTableValueId eq viewBean.categoryId}">selected="selected"</c:if>  value="${category.auxTableValueId}">${category.auxEngLable}</option>				    	
						    </c:forEach>
				        </form:select>	       
		            </c:otherwise>
		            </c:choose>        
        		 
			    </td>
        	</tr>
        	<tr>
        		<td align="left" colspan="3">Assignee :&nbsp;<span id="assignee">${viewBean.assignee}</span></td>
        		<td align="left" colspan="3">Refset Status :&nbsp;${viewBean.status}</td>
        		<td align="left" colspan="6">&nbsp;</td>
        	</tr>
        	<tr>
        		<td align="left" colspan="3">Refset Version Code :&nbsp;${viewBean.versionCode}</td>
        		<td align="left" colspan="3">Refset Version Status :&nbsp;<span id="versionStatus">${viewBean.versionStatus}</span></td>
        		<td align="left" colspan="6">Refset Version Name :&nbsp;${viewBean.versionName}</td>
        	</tr>
        </table>

        <div>	      
	        <fieldset>
	            <legend>Classification Version Year<span class="required">*</span></legend>	                                   
	            <div>		        
				    <table>
				    	<tr>
				    		<td><span id="ICD10CAYear">ICD-10-CA Classification Year :&nbsp;${viewBean.ICD10CAYear}</span></td>
				    		<td><span id="CCIYear">CCI Classification Year :&nbsp;${viewBean.CCIYear}</span></td>
				    		<td><span id="sctVersion">SNOMED CT Version Year :&nbsp;${viewBean.SCTVersionDesc}</span></td>
				    	</tr>
				    </table>
			    </div>   
	        </fieldset>   
	        <div>
	            <table>
		            <tr>
			            <td style="text-align:left;width:8%;">Refset Purpose<span class="required">*</span>:</td>
			            <td style="text-align:left;width:92%;">
						    <c:choose>
				            <c:when test='${viewBean.readOnly}'>
				               	<textarea id="definition" name="definition" rows="5" cols="80" maxlength="500" disabled>${viewBean.definition}</textarea> 
				            </c:when>
				            <c:otherwise>
				               	<textarea id="definition" name="definition" rows="5" cols="80" maxlength="500">${viewBean.definition}</textarea>   
				            </c:otherwise>
				            </c:choose>        			            
			            </td>
		            </tr>
	            </table>
	      </div>        
          </div> 
          </div>  
        </div>
        
        <div class="section" style="padding: 2px;" name="refsetNotes"> 
        <div class="sectionHeader">
            <a href="#"><img src="/cims_web_internal/img/icons/Expand.png" alt="Toggle" onclick="javascript:toggle(this);" style="vertical-align: middle;"/></a>
            <div style="display: inline-block; vertical-align: middle;">Notes</div>
        </div>
        <div class="sectionContent">    
            <table>
            	<tr>
            		<td style="text-align:right;vertical-align:top;width:5%;">Notes :</td>
            		<td style="text-align:left;width:95%;">
            			<c:choose>
				            <c:when test='${viewBean.readOnly}'>                
				                <textarea id="notes" name="notes" rows="5" cols="80" maxlength="500" disabled>${viewBean.notes}</textarea>       
				            </c:when>
				            <c:otherwise>
				                <textarea id="notes" name="notes" rows="5" cols="80" maxlength="500">${viewBean.notes}</textarea>  
				            </c:otherwise>
			            </c:choose>         
            		</td>
            	</tr>
            </table>
     		
        </div>  
        </div> 
        
        <input type="hidden" id="contextId" name="contextId" value="${viewBean.contextId}">
        <input type="hidden" id="elementId" name="elementId" value="${viewBean.elementId}">
        <input type="hidden" id="elementVersionId" name="elementVersionId" value="${viewBean.elementVersionId}">  
        <input type="hidden" id="displayAssignee" name="displayAssignee" value="${viewBean.displayAssignee}">
        <input type="hidden" name="oldICD10CAYear" value="${viewBean.oldICD10CAYear }">
        <input type="hidden" name="oldCCIYear" value="${viewBean.oldCCIYear }">
        <input type="hidden" name="oldSCTVersionCode" value="${viewBean.oldSCTVersionCode }">
        <input type="hidden" name="oldEffectiveYearFrom" value="${viewBean.oldEffectiveYearFrom }">
        
        <div id="assign_refset_dialog" class="dialog_box" title="Assign Refset - ${viewBean.refsetCode}">
               Please specify the following
               <table class="accordion_table">
                     <tr>
                         <td >Recipient :  </td>
                         <td >
                          <form:select  path="newAssignee" >
    				          <c:forEach var="recipent" items="${allAssigneeRecipents}">
  	  	                         <form:option value="${recipent}"> ${recipent}  </form:option>
  	  	                      </c:forEach>
    			          </form:select>
                          </td>
                     </tr>
                  
              </table>       
          </div>   
          
          <div id="create_newversion_dialog" class="dialog_box" title="Type of Version Change">
          		<span id="newVersionSystemMessage" class="errorMsg"></span>
          		<span style="float: right">
		            <ul style="padding-left: .9em;">
		                <li id="iconsLI"
		                    style="float: right; top: 0px; border: 0px; background: #ffffff; list-style-type: none;">
		                    <img id="confirm" class="viewMode" title="Confirm"
		                    src="<c:url value='/img/icons/OkGrey.png' />"/> <img
		                    id="reset" class="viewMode" title="Reset"
		                    src="<c:url value='/img/icons/Reset.png' />"/>
		                </li>
		            </ul>
		        </span>
          		<table id="versionFields">
          			<tr>
          				<td colspan="2"><input type="radio" name="versionType" id="major" value="major"/> Major Version Change</td><td colspan="2"><input type="radio" name="versionType" id="minor" value="minor"/> Minor Version Change</td>
          			</tr>
	          			<tr id="majorFields">
	          				<td style="text-align:left;">Refset Effective Year From <span class="required">*</span>:</td>
	          				<td>
	          					<form:select  path="effectiveYearFrom" id="newVersionFrom">
	          						<c:forEach items="${effectiveYearFromList}" var="effectiveYearFrom"  varStatus="loop">
							        	<option value="${effectiveYearFrom}" ${effectiveYearFrom eq viewBean.effectiveYearFrom ? 'selected' : ''}>${effectiveYearFrom}</option>
							    	</c:forEach>
	          					</form:select>
	          				</td>
	          				<td style="text-align:left;">Refset Effective Year To:</td>
	          				<td>
	          					<form:select path="effectiveYearTo" id="newVersionTo">
	          						<option value="">&nbsp;</option>
	          						<c:forEach items="${effectiveYearToList}" var="effectiveYearTo"  varStatus="loop">
							        	<option value="${effectiveYearTo}" ${effectiveYearTo eq viewBean.effectiveYearTo ? 'selected' : ''}>${effectiveYearTo}</option>
							    	</c:forEach>
	          					</form:select>
	          				</td>
	          			</tr>
	          			<tr>
	          				<td>ICD-10-CA Classification Year <span class="required">*</span>:</td>
	          				<td>
	          					<form:select path="ICD10CAContextInfo" id="newICDYear">
	          						<c:forEach items="${ICD10CAContextInfoList}" var="infoItemICD10CA" varStatus="loop">
							        	<option value="${infoItemICD10CA.contextBaseInfo}" ${infoItemICD10CA eq viewBean.ICD10CAContextInfo ? 'selected' : ''}>${infoItemICD10CA.versionCode}</option>
							    	</c:forEach>
	          					</form:select>
	          				</td>
	          				<td>CCI Classification Year <span class="required">*</span>:</td>
	          				<td>
	          					<form:select path="CCIContextInfo" id="newCCIYear">
	          						<c:forEach items="${CCIContextInfoList}" var="infoItemCCI" varStatus="loop">
							        	<option value="${infoItemCCI.contextBaseInfo}" ${infoItemCCI eq viewBean.CCIContextInfo ? 'selected' : ''}>${infoItemCCI.versionCode}</option>
							    	</c:forEach>
	          					</form:select>
	          				</td>
	          			</tr>
	          			<tr>
	          				<td>SNOMED CT Version Year <span class="required">*</span>:</td>
	          				<td colspan="3">
	          					<form:select path="SCTVersionCode" id="newSCTVersion">
	          						<c:forEach items="${SCTVersionList}" var="sctVersionItem" varStatus="loop">
							        	<option value="${sctVersionItem.versionCode}" ${sctVersionItem eq viewBean.SCTVersionCode ? 'selected' : ''}>${sctVersionItem.versionDesc}</option>
							    	</c:forEach>
	          					</form:select>
	          				</td>
	          			</tr>
          		</table>
          		
          		<div id="createProgress">
          			<div id="createBar">
          				<div id="createLabel">Creating...</div>
          			</div>
          		</div>
          </div>   
         
                    
	</form:form>
</div>


