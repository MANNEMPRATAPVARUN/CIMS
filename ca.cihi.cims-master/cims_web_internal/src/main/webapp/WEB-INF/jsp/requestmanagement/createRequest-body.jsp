<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<style type="text/css">
   .btn_line {margin:5px 5px 5px 550px;}
   select {width: 170px;}
  
   .red  { color:red; }
   
   th, td, caption {
     padding: 0px 0px 0px 5px;
   }

  #lstBox1, #lstBox2{
       height: 100px;
       width:180 px;
   }
  #patternChangeTopic {
     display:inline-block;
  }
  
 
  #button-header .wrapper {
    background: #FFF;
    border-bottom: 2px solid #333;
    padding-top: 20px;
    height: 45px;
  }

#button-header.fixed {
    height: 55px;
    margin-bottom: 5px;
}

#button-header.fixed .wrapper {
    height: 50px;
    position: fixed;
    top: 75px;
    z-index: 100;
    padding: 0;
}
  
</style>

 <script type="text/javascript" src="<c:url value="/ckeditor/ckeditor.js"/>"></script>
 <script type="text/javascript">  
	
     $(document).ready(function(){    
    	
    	checkDivUrcDocuments();
        
    	hideShowPatternTopic();
        hideShowEvolutionInfo();
      	checkCategory();
        
        $('#btnRight').click(function(e) {
             var selectedOpts = $('#lstBox1 option:selected');
             if (selectedOpts.length == 0) {
                 e.preventDefault();
             }
              
             $('#lstBox2').append($(selectedOpts).clone());
            
             $('.reviewerDropDown').append($(selectedOpts).clone());
             $(selectedOpts).remove();
             e.preventDefault();
         });

         $('#btnLeft').click(function(e) {
             var selectedOpts = $('#lstBox2 option:selected');
             if (selectedOpts.length == 0) {
                 e.preventDefault();
             }

             $('#lstBox1').append($(selectedOpts).clone());
             for (var i=0; i<selectedOpts.length; i++){
            	 var selectedOptVal=selectedOpts[i].value;
                 $('.reviewerDropDown option[value="' + selectedOptVal + '"]').remove();
             }
             
             $(selectedOpts).remove();
             e.preventDefault();
         });
      	
         $("#patternTopic").autocomplete(
     		    {
     			  // it is very important to use contentType with charset=UTF-8, otherwise french characters won't be properly handled by the search
     			   source: function(request, response) {
     			        $.ajax({
     				            url: "<c:url value='/searchPatternTopic.htm'/>",
     					        contentType:"application/json; charset=UTF-8",
     					        data: {
     					                 term : request.term,
     					                 baseContextId: $("#baseContextId").val()
     					             },
     					        success: function(data) {
     					                response(data);
     					        }
     					     });
     				}
     	       });
         
       var isLink;
       $('a').live('click', function() { isLink = true; });
         
       $(':input').each(function() { 
     	    $(this).data('initialValue', $(this).val()); 
       }); 
       
       $(window).bind("beforeunload", function(e) { 
     	    var msg = 'Do you really want to close? If you have any changes, Please Save it first'; 
     	    var isDirty = false; 
     	    $(':input').each(function () { 
     	        if($(this).data('initialValue') != $(this).val()){ 
     	            isDirty = true; 
     	        } 
     	    });
     	    if(!isLink){ 
   	           if (isDirty){
   	    	      return msg; 
   	           }
   	        } else{
   	    	    isLink = false; //reset it to false;
   	        }
     	});
       
     });  
    
     function checkDivUrcDocumentsAndCallVersionList(){    
    	checkDivUrcDocuments();
    	//callVersionList();
    	callVersionListWithNoCache();
     }   
     
     function checkDivUrcDocuments(){    
         if ($("#baseClassification").val() =='CCI'){
    		  $("#div_urc_documents").hide();
    	 }else{
    		 $("#div_urc_documents").show();
    	 }
     }  
    
     function callVersionListWithNoCache(){
    	 var baseClassification= $("#baseClassification").val();
    	 $.ajax({
    		    url: "<c:url value='/getBaseClassificationVersions.htm'/>?baseClassification=" +baseClassification,
    		    cache: false,
    		    dataType: "json",
    		    success: function(data) {
    		    	   $("#baseContextId").empty();    
                       $.each(data, function(key, item) {    
                     	  var optionvalue= '<option value="' + item.contextId +'" >' + item.versionCode + '</option>';   
                     	  $("#baseContextId").append(optionvalue);    
                       });    
    		    }
    		});
     }

     /*
     function callVersionList(){
    	 $.get("<c:url value='/getBaseClassificationVersions.htm'/>", 
           	  {baseClassification: $("#baseClassification").val()}, 
       		  function(data) {    
                     $("#baseContextId").empty();    
                     $.each(data, function(key, item) {    
                   	  var optionvalue= '<option value="' + item.contextId +'" >' + item.versionCode + '</option>';   
                   	  $("#baseContextId").append(optionvalue);    
                     });    
            });    
     }*/
     
   
     function selectBilingual(){
    	 $("#languageCode option[value='ALL']").removeAttr('disabled');
    	 $("#languageCode").val( 'ALL' );
    	 // don't disable others
    	 // $("#languageCode option[value='ENG']").attr("disabled","disabled");
    	 //$("#languageCode option[value='FRA']").attr("disabled","disabled");
    	 
     }
     
     function disableBilingual(){
    	// $("#languageCode option[value='ENG']").removeAttr('disabled');
    	 //$("#languageCode option[value='FRA']").removeAttr('disabled');
         $("#languageCode").val( 'ENG' );
    	 $("#languageCode option[value='ALL']").attr("disabled","disabled");
     }
     
     function selectCategory(selectedVal){
    	 if (selectedVal=='T'){
    		 selectBilingual();
    	 }else{
    		 disableBilingual();
    	 }
     }
     
     function checkCategory(){
         if ($('#category').val() =='I'){
        	 $("#languageCode option[value='ALL']").attr("disabled","disabled");
    	     }
         else if ($('#category').val() =='S'){
        	 $("#languageCode option[value='ALL']").attr("disabled","disabled");
    	     }
         else{
        	 $("#languageCode option[value='ALL']").removeAttr('disabled');

         }

     }

     function disableAllButtons(){
   	     $("input[type=button]").attr("disabled", "disabled");
   	     $("input[type=button]").attr("class", "disabledButton");
     }   
     
     function hideShowPatternTopic(){
    	 if ($('#patternChange').attr('checked') ){
    		 $("#patternChangeTopic").show();
         }else{
        	 $("#patternTopic").val('');
             $("#patternChangeTopic").hide();
         }
     }
     
     function hideShowEvolutionInfo(){
    	
    	 if ($('#evolutionRequired').attr('checked') ){
    		 $("#div_evolutionInfo").show();
         }else{
        	 $("#evolutionInfo.evolutionCodes").val('');
        	 $("#evolutionInfo.evolutionTextEng").val('');
        	 $("#evolutionInfo.evolutionTextFra").val('');
             $("#div_evolutionInfo").hide();
         }
     }
     
     
     function createChangeRequest(){
    	    $("#lstBox2 option[value='9']").removeAttr('disabled');
    	    $("#lstBox2 option").attr('selected', 'selected');
    	    $('form#changeRequestDTO').serialize();
    	    disableAllButtons();
    	    $(window).unbind("beforeunload");
    	    $("#changeRequestDTO")[0].action="<c:url value='/createChangeRequest.htm'/>";
        	$("#changeRequestDTO")[0].submit();
     }
     
   
     
    function removeTableRow(icon){
    	$(icon).closest('tr').remove();
    }
   
   
     function addCodingQuestion(){
    	 var currentNum= $('#tbl_codingQuestions tbody tr:last td:first').text();
    	 var nextNum =1;
    	 var nextIndex =0;
    	 if (currentNum){
     	    nextNum = parseInt(currentNum, 10) +1;
     	    nextIndex = parseInt(currentNum, 10);
    	 }
    	 
    	 var cq_eQueryId_elementId ="codingQuestions"+nextIndex+".eQueryId";
     	 var cq_eQueryId_elementName= "codingQuestions["+nextIndex+"].eQueryId";
     	 var cq_url_elementId ="codingQuestions"+nextIndex+".url";
    	 var cq_url_elementName= "codingQuestions["+nextIndex+"].url";
    	 var rowHtml =  nextNum+".";
     	 
     	 var row = ' <tr> '+
              '<td>'+ rowHtml +' </td>'+
              '<td>ID: &nbsp;<input id="'+cq_eQueryId_elementId +'" name="'+cq_eQueryId_elementName +'" type="text" value="" size="20" maxlength="10" />'+
              '</td>'+
              '<td>URL: &nbsp;<input id="'+cq_url_elementId +'" name="'+cq_url_elementName +'" type="text" value="" size="70" maxlength="250" />'+
              '</td>'+
              '<td><div class="alignRight"> <img id="iconRemove" src=\'<c:url value="/img/icons/Delete.png" />\' alt="Remove" onclick="javascript:removeTableRow(this);"/></div> </td>'+
              '</tr>';
         $row = $(row);
     	 $("#tbl_codingQuestions").find('tbody') .append($row);
     }

     function addUrcDocument(){
    	 var currentNum= $('#tbl_urcDocuments tbody tr:last td:first').text();
    	 var nextNum =1;
    	 var nextIndex =0;
    	 if (currentNum){
     	    nextNum = parseInt(currentNum, 10) +1;
     	    nextIndex = parseInt(currentNum, 10);
    	 }
         var ud_elementName= "urcFiles["+nextIndex+"]";
     	 var rowHtml =  nextNum+".";
     	 
    	 var row = ' <tr> '+
              '<td>'+ rowHtml +' </td>'+
              '<td><input type="file" name="'+ud_elementName +'" size="70" maxlength="250" />'+
              '</td>'+
              '<td><div class="alignRight"> <img id="iconRemove" src=\'<c:url value="/img/icons/Delete.png" />\' alt="Remove" onclick="javascript:removeTableRow(this);"/></div> </td>'+
              '</tr>';
         $row = $(row);
     	 $("#tbl_urcDocuments").find('tbody') .append($row);
     }  
     
     function addUrcLink(){
    	 var currentNum= $('#tbl_urcLinks tbody tr:last td:first').text();
    	 var nextNum =1;
    	 var nextIndex =0;
    	 if (currentNum){
     	    nextNum = parseInt(currentNum, 10) +1;
     	    nextIndex = parseInt(currentNum, 10);
    	 }
         var ud_elementName= "urcLinks["+nextIndex+"].url";
     	 var rowHtml =  nextNum+".";
     	 
    	 var row = ' <tr> '+
              '<td>'+ rowHtml +' </td>'+
              '<td><input type="text" name="'+ud_elementName +'" size="70" maxlength="250" />'+
              '</td>'+
              '<td><div class="alignRight"> <img id="iconRemove" src=\'<c:url value="/img/icons/Delete.png" />\' alt="Remove" onclick="javascript:removeTableRow(this);"/></div> </td>'+
              '</tr>';
         $row = $(row);
     	 $("#tbl_urcLinks").find('tbody') .append($row);
     }  
     
     
     function addOtherAttachment(){
    	 var currentNum= $('#tbl_otherAttachments tbody tr:last td:first').text();
    	 var nextNum =1;
    	 var nextIndex =0;
    	 if (currentNum){
     	    nextNum = parseInt(currentNum, 10) +1;
     	    nextIndex = parseInt(currentNum, 10);
    	 }
         var oa_elementName= "otherFiles["+nextIndex+"]";
     	 var rowHtml =  nextNum+".";
     	 var row = ' <tr> '+
              '<td>'+ rowHtml +' </td>'+
              '<td><input type="file" name="'+oa_elementName +'" size="70" maxlength="250"  />'+
              '</td>'+
              '<td><div class="alignRight"> <img id="iconRemove" src=\'<c:url value="/img/icons/Delete.png" />\' alt="Remove" onclick="javascript:removeTableRow(this);"/></div> </td>'+
              '</tr>';
         $row = $(row);
     	 $("#tbl_otherAttachments").find('tbody') .append($row);
     }  
     
     function addOtherLink(){
    	 var currentNum= $('#tbl_otherLinks tbody tr:last td:first').text();
    	 var nextNum =1;
    	 var nextIndex =0;
    	 if (currentNum){
     	    nextNum = parseInt(currentNum, 10) +1;
     	    nextIndex = parseInt(currentNum, 10);
    	 }
         var oa_elementName= "otherLinks["+nextIndex+"].url";
     	 var rowHtml =  nextNum+".";
     	 var row = ' <tr> '+
              '<td>'+ rowHtml +' </td>'+
              '<td><input type="text" name="'+oa_elementName +'" size="70" maxlength="250" />'+
              '</td>'+
              '<td><div class="alignRight"> <img id="iconRemove" src=\'<c:url value="/img/icons/Delete.png" />\' alt="Remove" onclick="javascript:removeTableRow(this);"/></div> </td>'+
              '</tr>';
         $row = $(row);
     	 $("#tbl_otherLinks").find('tbody') .append($row);
     }  
     
     function hideShowDiv(iconId, divId){
    	 var isHidden = $('#' + divId).is(':hidden');
    	 if (isHidden){
    	     $('#' + divId).slideDown();
             var img_new = '<c:url value="/img/icons/Expand.png" />';
             $('#'+iconId+' img').attr("src", img_new);
    	 }else{
    		 $('#' + divId).slideUp();
    		 var img_new = '<c:url value="/img/icons/Collapse.png" />';
    		 $('#'+iconId+' img').attr("src", img_new);
    		
    	 }
     }
     
     
 </script>

  

<div class="content">
   
    
   
    <form:form id="changeRequestDTO"  modelAttribute="changeRequestDTO"  method="post" enctype="multipart/form-data">
    
     <div id="button-header" class="fixed">
           <div class="wrapper">
              <div class="alignRight">
                   <input type="button"  value="Save" class="button" onclick="javascript:createChangeRequest();" >&nbsp;&nbsp; 
             </div>
          </div>
     </div>              
    
     <form:errors path="*" cssClass="errorMsg" />
          <div id="basicInfo" class="section"> 
            <div id ="icon_basicInfo" class="left_section">
                   <img src='<c:url value="/img/icons/Expand.png" />' alt="Expanded Mode" onclick="javascript:hideShowDiv('icon_basicInfo','div_basicInfo');"/>
            </div> 
           <div  class="right_section" >
               <div class="sectionHeader" >Basic Information</div>
           </div>
           <div  id="div_basicInfo" class="right_section">
             <table class="accordion_table" >
               <tr>
                 <td ><span class="required">*</span> <span id="categoryLabel"><b> Request Status:</b> </span> </td>
                 <td >
                    <b>New</b>
                 </td>
                
			    <td nowrap><span class="required">*</span><span id="baseClassificationLabel"> Classification: </span> </td>
                 <td>
                   <form:select  path="baseClassification" onchange="javascript:checkDivUrcDocumentsAndCallVersionList();" >
    				   <form:option value=""></form:option>
    				   <c:forEach var="baseClassification" items="${baseClassifications}">
  	  	                  <form:option value="${baseClassification}"> ${baseClassification}  </form:option>
  	  	               </c:forEach>
    			   </form:select>
			     </td>
			     <td ><span class="required">*</span> <span id="categoryLabel"> Request Category: </span> </td>
                 <td >
                     <form:select path="category" id="category"  onchange="javascript:selectCategory($('#category').val());">
                        <form:option value=""></form:option>
    					<form:option value="T">Tabular List</form:option>
    					<form:option value="I">Index</form:option>
    					<form:option value="S">Supplements</form:option>
    			     </form:select>
                 </td>
			 </tr>
			  <tr>
                 <td><span class="required">*</span> Year: </td>
                 <td>
                    <form:select path="baseContextId" id="baseContextId"  >
    					 <c:forEach var="contextIdentifier" items="${openedContextIdentifiers}">
  	  	                   <form:option value="${contextIdentifier.contextId}"> ${contextIdentifier.versionCode} </form:option>
  	  	                </c:forEach>
    			    </form:select>
			     </td>
                <td ><span class="required">*</span><span id="languageCodeLabel">  Language: </span>   </td>
                 <td>
                   <form:select path="languageCode" id="languageCode"  >
    					<form:option value="ENG">English</form:option>
    					<form:option value="FRA">French</form:option>
    					<form:option value="ALL">English & French</form:option>
    			   </form:select>
			    </td>
			     <td >&nbsp; </td>
                 <td >&nbsp; </td>
              </tr>
              <tr>
                 <td ><span class="required">*</span> <span id="nameLabel"> Request Name: </span> </td>
                 <td colspan="5">
                    <form:input path="name" id="name" size="120" maxlength="250"/>
                 </td>
              </tr>   
              <tr>
               
                 <td nowrap><span class="required">*</span> Nature of Change:  </td>
                 <td >
                     <form:select path="changeNatureId" id="changeNatureId"  >
                         <form:option value=""></form:option>
                        <c:forEach var="changeNature" items="${changeNatures}">
                          <form:option value="${changeNature.auxTableValueId}"> ${changeNature.auxEngLable}  </form:option>
  	  	               </c:forEach>
    				 </form:select>
                 </td>
                 <td nowrap><span class="required">*</span> Type of Change:  </td>
                 <td >
                    <form:select path="changeTypeId" id="changeTypeId"  >
                          <form:option value=""></form:option>
                        <c:forEach var="changeType" items="${changeTypes}">
                          <form:option value="${changeType.auxTableValueId}"> ${changeType.auxEngLable}  </form:option>
  	  	               </c:forEach>
    				 </form:select>
                 </td>
                 <td ><span class="required">*</span> Requestor:  </td>
                 <td >
                      <form:select path="requestorId" id="requestorId"  >
                          <form:option value=""></form:option>
                        <c:forEach var="requestor" items="${requestors}">
                          <form:option value="${requestor.auxTableValueId}"> ${requestor.auxEngLable}  </form:option>
  	  	               </c:forEach>
    				 </form:select>
                 </td>
              </tr> 
            
              <tr>
                <td class="alignTop"><span class="required">*</span> <span id="categoryLabel"> Review Groups: </span> </td>
                 <td >
                   <select multiple="multiple" id='lstBox1'>
                      <c:forEach var="reviewGroup" items="${reviewGroups}">
                          <option value="${reviewGroup.distributionlistid}"> ${reviewGroup.name}  </option>
  	  	               </c:forEach>
                   </select>
                 </td>
                 <td style='width:50px;text-align:left;vertical-align:middle;'>
                           <input type='button' id='btnRight' value ='  >>  '/>
                      <br/><input type='button' id='btnLeft' value ='  <<  '/>
                  </td>
                 <td >
           
                 <form:select multiple="true" id="lstBox2" path="reviewGroups"  >
                       <form:option value="9"  disabled="true"> DL-Classification</form:option>
                       <c:forEach var="selectedReviewGroup" items="${selectedReviewGroups}">
                          <form:option value="${selectedReviewGroup.distributionlistid}" > ${selectedReviewGroup.name}  </form:option>
  	  	               </c:forEach>
                 </form:select>
                 
                 </td>
                 <td >&nbsp; </td>
                 <td >
                    &nbsp; 
                 </td>
              </tr> 
              <tr>
                <td colspan="6">
                   <form:checkbox path="indexRequired"/> Index Required &nbsp; 
                   <form:checkbox path="evolutionRequired" id ="evolutionRequired" onchange="javascript:hideShowEvolutionInfo();"/> Evolution Required &nbsp;
                   <form:checkbox path="conversionRequired"/> Conversion Required &nbsp;
                   <form:checkbox path="patternChange" id ="patternChange" onchange="javascript:hideShowPatternTopic();"/> Pattern Change &nbsp;&nbsp;&nbsp;
                   <div id="patternChangeTopic">
                   <span class="required">*</span> Pattern Topic: <form:input path="patternTopic" size="40" maxlength="150"/>
                   </div> 
                </td>
               
              </tr> 
            </table>
            
             <div id="div_evolutionInfo" class="right_section">
               <span class="required">*</span> Evolution Codes :    
                  	<form:textarea path="evolutionInfo.evolutionCodes" rows="3" />
			    <script>
    			   CKEDITOR.replace( 'evolutionInfo.evolutionCodes',{toolbarStartupExpanded : false} );
    	        </script>
    	        <span class="required">*</span> Evolution English Comments :    
                  	<form:textarea path="evolutionInfo.evolutionTextEng" rows="3" />
			    <script>
    			   CKEDITOR.replace( 'evolutionInfo.evolutionTextEng',{toolbarStartupExpanded : false} );
    	        </script>
    	        <span class="required">*</span> Evolution French Comments :      
                  	<form:textarea path="evolutionInfo.evolutionTextFra" rows="3" />
			    <script>
    			   CKEDITOR.replace( 'evolutionInfo.evolutionTextFra',{toolbarStartupExpanded : false} );
    	        </script>
    	        
			 </div>
            
            
            
            </div>
          
        </div>
           
        <div id="rational" class="section">
            <div id ="icon_rational" class="left_section">
                 <img src='<c:url value="/img/icons/Expand.png" />' alt="Expanded Mode" onclick="javascript:hideShowDiv('icon_rational','div_rational');"/>
            </div>
            <div class="right_section">
                 <div class="sectionHeader" > Rationale for Change and Decisions </div>
             </div>
             <div id="div_rational" class="right_section">
               <span class="required">*</span> Rationale for Change :    
                  	<form:textarea path="changeRationalTxt" rows="3" />
			    <script>
    			   CKEDITOR.replace( 'changeRationalTxt',{toolbarStartupExpanded : false} );
    	        </script>
			 </div>
         </div>
       
         	<div id="discussions" class="section">
          	   <div id ="icon_discussions" class="left_section">
          	     <img src='<c:url value="/img/icons/Expand.png" />' alt="Expanded Mode" onclick="javascript:hideShowDiv('icon_discussions','div_discussions');"/>
          	   </div>  
                <div  class="right_section">
                 <div class="sectionHeader" >  Discussions and Comments </div>
			   </div>
                    <div id="div_discussions" class="right_section">     
                    <table id="tbl_discussions">
                     
                       <tr>
                       <td class="alignTop" > 1. </td>
                       <td>	<form:textarea path="commentDiscussions[0].userCommentTxt" rows="3" />
                                        
		               </td>
		              
		               <script>
    			         CKEDITOR.replace( "commentDiscussions[0].userCommentTxt" ,{toolbarStartupExpanded : false});
                       </script>
                     
                       </tr>
                     
		              </table>
		            
		              </div>
		     </div> 
          	
      
          
      <div id="refrences" class="section">
          	   <div id ="icon_refrences" class="left_section">
          	     <img src='<c:url value="/img/icons/Expand.png" />' alt="Expanded Mode" onclick="javascript:hideShowDiv('icon_refrences','div_refrences');"/>
          	   </div>  
                <div  class="right_section">
                 <div class="sectionHeader" >  References </div>
			     <div id="div_refrences" class="right_section">     
                      <div class="tableHeader" >Coding Questions </div>
                      <table id="tbl_codingQuestions">
                      <tbody>
                     <c:forEach items="${changeRequestDTO.codingQuestions}" var="codingQuestion" varStatus="status">
                      <tr>
                          <td>${status.index+1} . </td>
                          <td>ID: &nbsp;<form:input path="codingQuestions[${status.index}].eQueryId"  size="20" maxlength="10"/> </td>
		                  <td>URL: &nbsp;<form:input path="codingQuestions[${status.index}].url" size="70" maxlength="250" /> </td>
                          
                          <td> <div class="alignRight">  <img src='<c:url value="/img/icons/Delete.png" />' alt="Remove" onclick="javascript:removeTableRow(this);"/></div></td>
		              </tr>
                    </c:forEach>
                    </tbody>
                   </table>
		              <div class="alignRight">
		                 <a href="javascript:addCodingQuestion();">Add Coding Question  </a>
		               </div>  
		          <c:if test="${changeRequestDTO.baseClassification !='CCI' }" >   
		           <div id="div_urc_documents">
		             <div class="tableHeader" >URC Documents </div>
                      <table id="tbl_urcDocuments">
                        URC Attachments
                      <tbody>
                     <c:forEach items="${changeRequestDTO.urcAttachments}" var="urcAttachment" varStatus="status">
                      <tr>
                          <td> ${status.index+1} . </td>
                          <td>	
                          <a href="javascript:popupChangeRequestFile('${changeRequestDTO.changeRequestId}','${urcAttachment.documentReferenceId}','urc');">${urcAttachment.fileName}</a> 
                             <form:hidden path="urcAttachments[${status.index}].documentReferenceId"/>
                             <form:hidden path="urcAttachments[${status.index}].fileName"/>
                          </td>
                          <td><div class="alignRight"> <img src='<c:url value="/img/icons/Delete.png" />' alt="Remove" onclick="javascript:removeTableRow(this);"/></div></td>
		              </tr>
		              </c:forEach>
		              </tbody>
                    </table>
                      <div class="alignRight">
		                 <a href="javascript:addUrcDocument();">Add Document </a>
		               </div>
		            <table id="tbl_urcLinks">
		              URC Links
		              <tbody>
		             <c:forEach items="${changeRequestDTO.urcLinks}" var="urcLink" varStatus="status">
                      <tr>
                          <td> ${status.index+1} . </td>
                          <td> <form:input path="urcLinks[${status.index}].url" size="70" maxlength="250"/> </td>
		                 
		                  <td> <div class="alignRight">  <img src='<c:url value="/img/icons/Delete.png" />' alt="Remove" onclick="javascript:removeTableRow(this);"/></div>  </td>
		              </tr>
		              </c:forEach>
		              </tbody>
                    </table>  
		              <div class="alignRight">
		                 <a href="javascript:addUrcLink();">Add Link </a>
		               </div>   
		            </div>
		         </c:if>
		            
		            
		            <div class="tableHeader" >Other Attachments in Support of Change  </div>
                      <table id="tbl_otherAttachments">
                      Other Attachments
                      <tbody>
                      <c:forEach items="${changeRequestDTO.otherAttachments}" var="otherAttachment" varStatus="status">
                      <tr>
                          <td> ${status.index+1} . </td>
                          <td>  <a href="javascript:popupChangeRequestFile('${changeRequestDTO.changeRequestId}','${otherAttachment.documentReferenceId}','other');">${otherAttachment.fileName}</a> 
                             <form:hidden path="otherAttachments[${status.index}].documentReferenceId"/> 
                             <form:hidden path="otherAttachments[${status.index}].fileName"/> 
                          </td>
		                  <td> <div class="alignRight"> <img src='<c:url value="/img/icons/Delete.png" />' alt="Remove" onclick="javascript:removeTableRow(this);"/> </div></td>
		              </tr>
		              </c:forEach>
		              </tbody>
                    </table>
		              <div class="alignRight">
		                 <a href="javascript:addOtherAttachment();">Add Attachment </a>
		               </div> 
		            <table id="tbl_otherLinks">
                      Other Links
                      <tbody>
                      <c:forEach items="${changeRequestDTO.otherLinks}" var="otherLink" varStatus="status">
                      <tr>
                          <td> ${status.index+1} .  </td>
                          <td><form:input path="otherLinks[${status.index}].url" size="70" maxlength="250"/></td>
		                 
		                  <td> <div class="alignRight"><img src='<c:url value="/img/icons/Delete.png" />' alt="Remove" onclick="javascript:removeTableRow(this);"/> </div> </td>
		              </tr>
		              </c:forEach>
		               </tbody>
                    </table>
		              <div class="alignRight">
		                 <a href="javascript:addOtherLink();">Add Link </a>
		               </div>    
		           </div>
	            </div> 
          	</div>
    </form:form>
</div>