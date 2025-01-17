<html>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body>
<style type="text/css">
   .btn_line {margin:5px 5px 5px 750px;}
   .btn_line_alignment {margin:5px 5px 5px 750px;
                 display:inline-block;
   }
   
   select {width: 170px;}
   #lstBox1, #lstBox2{
       height: 100px;
       width:180 px;
   }
 
   #patternChangeTopic {
     display:inline-block;
   }

 
</style>

<script type="text/javascript" src="<c:url value="/ckeditor/ckeditor.js"/>"></script>

 <script type="text/javascript">  
 
 $("body").keydown(function(e){
	 if ((e.which || e.keyCode) == 116){
	    e.preventDefault();
	    document.location.href='<c:url value="manageChangeRequest.htm?changeRequestId=" />'+${changeRequestDTO.changeRequestId};
	 }
});
 $(document).ready(function(){    
     
	
	 showPatternTopic();
     showEvolutionInfo();
     checkCategory();
     checkDivUrcDocuments();
     closeWindowsIfDeleted();
     <c:if test="${lastActiveSectionDiv!='div_questionForReviewers'}">
        hideDiv('icon_questions','div_questionForReviewers');
     </c:if>
     <c:if test="${lastActiveSectionDiv!='div_discussions'}"> 
        hideDiv('icon_discussions','div_discussions');
     </c:if>
     <c:if test="${lastActiveSectionDiv!='div_advices'}"> 
         hideDiv('icon_advices','div_advices');
     </c:if>  
     <c:if test="${lastActiveSectionDiv!='div_refrences'}"> 
        hideDiv('icon_refrences','div_refrences');
     </c:if>
     
     globalCheckingReviewGroups();
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
  					                 term : request.term
  					             },
  					        success: function(data) {
  					                response(data);
  					        }
  					     });
  				},
  		     minLength:2
  						
  		});
      
      $( "#assign_request_dialog" ).dialog({
          autoOpen: false,
          height: 300,
          width: 400,
          modal: true,
          appendTo: "#changeRequestDTO",
          buttons: {
              "Assign": function() {
            	$(this).appendTo('form#changeRequestDTO');
            	assignChangeRequest(); 
               $( this ).dialog( "close" );
              },
              Cancel: function() {
                $( this ).dialog( "close" );
              }
          }
      });
    
      $( "#owner_transfer_dialog" ).dialog({
          autoOpen: false,
          height: 300,
          width: 400,
          modal: true,
          appendTo: "#changeRequestDTO",
          buttons: {
        	  <c:choose>
              <c:when test='${cf:hasExecuteAccess(currentUser,"BUTTON_ASSIGNOWNER")}'>
               "Assign": function() {  
            	   $(this).appendTo('form#changeRequestDTO');
            	   assignAndTransferChangeRequest();
            	   $( this ).dialog( "close" );
               },
              </c:when>
              <c:otherwise>
                "Transfer": function() {
            	   $(this).appendTo('form#changeRequestDTO');
            	   transferChangeRequestOwnerShip(); 
                   $( this ).dialog( "close" );
               },
              </c:otherwise>
              </c:choose>
              Cancel: function() {
                $( this ).dialog( "close" );
              }
          }
      });
      
      $( "#get_advice_dialog" ).dialog({
          autoOpen: false,
          height: 350,
          width: 700,
          modal: true,
          appendTo: "#changeRequestDTO",
          open: function( event, ui ) {
        	  adviceMessageEditor= CKEDITOR.replace( 'advice.message' );
          },
          buttons: {
              "Get Advice": function() {
            	$(this).appendTo('form#changeRequestDTO');
            	getAdviceForChangeRequest(); 
                $( this ).dialog( "close" );
              },
              Cancel: function() {
                $( this ).dialog( "close" );
              }
          }
      });
      
      
      
      var isLink = false;
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
	$("#div_defer_subsection").hide();
 	checkDivUrcDocuments();
 	//callVersionList();
 	callVersionListWithNoCache();
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

  function checkDivUrcDocuments(){    
      if ($("#baseClassification").val() =='CCI'){
 		  $("#div_urc_documents").hide();
 	 }else{
 		 $("#div_urc_documents").show();
 	 }
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
  
  function refreshDeferDropDown(){
	    // $("#div_defer_subsection").hide();
	     $.get("<c:url value='/refreshDeferDropDown.htm'/>", 
	        	  {baseClassification: $("#baseClassification").val(),
	 		       baseContextId: $("#baseContextId").val() }, 
	    		  function(data) {   
	 		    	  if(data.length == 0){
	 		    		 $("#div_defer_subsection").hide();
	 		    	  }else{
	 		    		 $("#div_defer_subsection").show();
	                     $("#deferredToBaseContextId").empty();    
	                     $.each(data, function(key, item) {    
	                	    var optionvalue= '<option value="' + item.contextId +'" >' + item.versionCode + '</option>';   
	                	    $("#deferredToBaseContextId").append(optionvalue);    
	                     });  
	 		    	 }
	         });    
	  }
  
  

 function selectBilingual(){
	 $("#languageCode option[value='ALL']").removeAttr('disabled');
	 $("#languageCode").val( 'ALL' );
	
  }
 
  function disableBilingual(){
	 //$("#languageCode option[value='ENG']").removeAttr('disabled');
	 //$("#languageCode option[value='FRA']").removeAttr('disabled');
     $("#languageCode").val( 'ENG' );
	 $("#languageCode option[value='ALL']").attr("disabled","disabled");
  }
 
  function disableAllButtons(){
	  $("input[type=button]").attr("disabled", "disabled");
	  $("input[type=button]").attr("class", "disabledButton");
  }
  
  function enableAllButtons(){
	  $("input[type=button]").removeAttr('disabled');
	  $("input[type=button]").attr("class", "button");
  }
	 
  function closeWindowsIfDeleted(){
	  <c:if test="${changeRequestDTO.status.statusCode=='Deleted'}">
	       self.close();
	  </c:if>
  }
  
  function hideShowPatternTopic(){
	 if ($('#patternChange').attr('checked') ){
		 $("#patternChangeTopic").show();
     }else{
    	 $("#patternTopic").val('');
         $("#patternChangeTopic").hide();
     }
  }
 
  function showPatternTopic(){
	  <c:if test="${changeRequestDTO.patternChange}">
	       $("#patternChangeTopic").show();
	  </c:if>
	  <c:if test="${!changeRequestDTO.patternChange}">
         $("#patternChangeTopic").hide();
       </c:if>
  }
  
  function showEvolutionInfo(){
	  <c:if test="${changeRequestDTO.evolutionRequired}">
	      $("#div_evolutionInfo").show();
	  </c:if>
	  <c:if test="${!changeRequestDTO.evolutionRequired}">
         $("#div_evolutionInfo").hide();
      </c:if>
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

   function globalCheckingReviewGroups(){
	   var selectedVals = $('.reviewerDropDown :selected').map(function () {
           return this.value;
          }).get();
       $("#lstBox2 option").removeAttr('disabled');
	   $("#lstBox2 option").each(function(){
		  if ($.inArray($(this).val(),selectedVals)!=-1){
			 $(this).attr("disabled","disabled");  
		  }else{
			 $(this).removeAttr('disabled'); 
		  }
	   });
	   $("#lstBox2 option[value='9']").attr("disabled","disabled");
   }
   
   
   
   function openAssignDialogBox(){
		 $("#assign_request_dialog" ).dialog( "open" );
   }
	  
   function openTransferOwnerDialogBox(){
		  $("#owner_transfer_dialog" ).dialog( "open" );
   }
	  
   function openGetAdviceDialogBox(){
	   $("#get_advice_dialog" ).dialog( "open" );
   }

	var errorCallback = function(data) {
		hideLoading();
		var responseData = data.responseText;
		if(responseData != "undefined" && responseData != null) {
			$('#updatedSuccessfully').hide();
			$('.errorMsg').hide();
			$('#concurrentError').text(responseData).show();
			enableAllButtons();
		}
	};
	
	var replaceContent = function(data) {
		document.open();
		document.write(data);
		document.close();
		window.parent.location.reload();
	};
	
	function submitChangeRequestForm(){
		 
		 $form=$("#changeRequestDTO");
		 for ( instance in CKEDITOR.instances )
			    CKEDITOR.instances[instance].updateElement();
		 var formData = new FormData(document.getElementById("changeRequestDTO"));
		    $.ajax({
		        url: $form.attr("action"),
		        data: formData,
		        processData: false,
		        contentType: false,
		        type: "POST",
		        success: replaceContent,
		        error: errorCallback
		    });
	}

	
  function saveChangeRequest(){
	    $("#lstBox2 option").removeAttr('disabled');
 	    $("#lstBox2 option").attr('selected', 'selected');
 	   // $('form#changeRequestDTO').serialize();
 	    var url = "<c:url value='/saveChangeRequest.htm'/>";
	    url=turnOnTimestampCheck(url);
	    $("#changeRequestDTO")[0].action=url;
 	    $(window).unbind("beforeunload");
 	    disableAllButtons();
 	   submitChangeRequestForm();
  }
  
  
  function submitChangeRequest(){
	    $("#lstBox2 option").removeAttr('disabled');
	    $("#lstBox2 option").attr('selected', 'selected');
	    $('form#changeRequestDTO').serialize();
 	    $("#changeRequestDTO")[0].action="<c:url value='/submitChangeRequest.htm'/>";
	    $(window).unbind("beforeunload");
	    disableAllButtons();
  	    $("#changeRequestDTO")[0].submit();
   
  }

  
  function validateChangeRequest(){
	    $("#lstBox2 option").removeAttr('disabled');
	    $("#lstBox2 option").attr('selected', 'selected');
	    var url = "<c:url value='/validateChangeRequest.htm'/>";
	    url=turnOnTimestampCheck(url);
	    $("#changeRequestDTO")[0].action=url;
	  	$(window).unbind("beforeunload");
	  	 disableAllButtons();
	  	submitChangeRequestForm();
   }
  
  function takeOverChangeRequest(){
	  $("#lstBox2 option").removeAttr('disabled');
	  $("#lstBox2 option").attr('selected', 'selected');
	  var url = "<c:url value='/takeOverChangeRequest.htm'/>";
	  url=turnOnTimestampCheck(url);
	  $("#changeRequestDTO")[0].action=url;
	  $(window).unbind("beforeunload");
	  disableAllButtons();
	  submitChangeRequestForm();
  }
  
 
  
  function deferChangeRequest(){
	  $("#lstBox2 option").removeAttr('disabled');
	  $("#lstBox2 option").attr('selected', 'selected');
	  var url = "<c:url value='/deferChangeRequest.htm'/>";
	  url=turnOnTimestampCheck(url);
	  $("#changeRequestDTO")[0].action=url;
	  $(window).unbind("beforeunload");
	  disableAllButtons();
	  submitChangeRequestForm();
  }
  
  function rejectChangeRequest(){
	  $("#lstBox2 option").removeAttr('disabled');
	  $("#lstBox2 option").attr('selected', 'selected');
	  var url = "<c:url value='/rejectChangeRequest.htm'/>";
	  url=turnOnTimestampCheck(url);
	  $("#changeRequestDTO")[0].action=url;
	  $(window).unbind("beforeunload");
	  disableAllButtons();
	  submitChangeRequestForm();
  }
  
  function sendForReviewChangeRequest(questionIndex){
	  $("#lstBox2 option").removeAttr('disabled');
	  $("#lstBox2 option").attr('selected', 'selected');
	  var url = "<c:url value='/sendForReviewChangeRequest.htm'/>?questionIndex=" + questionIndex;
	  url=turnOnTimestampCheck(url);
	  $("#changeRequestDTO")[0].action=url;
	  $(window).unbind("beforeunload");
	  disableAllButtons();
	  submitChangeRequestForm();
  }
  
  function addCommentForQuestion(questionId){
	  $("#lstBox2 option").removeAttr('disabled');
	  $("#lstBox2 option").attr('selected', 'selected');
	  var url = "<c:url value='/addCommentForQuestion.htm'/>?questionId=" + questionId;
	  url=turnOnTimestampCheck(url);
	  $("#changeRequestDTO")[0].action=url;
	  $(window).unbind("beforeunload");
	  disableAllButtons();
	  submitChangeRequestForm();
  }
  
  
  function  addCommentForAdvice(adviceId) {
	  $("#lstBox2 option").removeAttr('disabled');
	  $("#lstBox2 option").attr('selected', 'selected');
	  var url = "<c:url value='/addCommentForAdvice.htm'/>?adviceId="+adviceId;
	  url=turnOnTimestampCheck(url);
	  $("#changeRequestDTO")[0].action=url;
	  $(window).unbind("beforeunload");
	  disableAllButtons();
	  submitChangeRequestForm();
  }
  
  function assignChangeRequest(){
	  $("#lstBox2 option").removeAttr('disabled');
	  $("#lstBox2 option").attr('selected', 'selected');
	  var url = "<c:url value='/assignChangeRequest.htm'/>";
	  url=turnOnTimestampCheck(url);
	  $("#changeRequestDTO")[0].action=url;
	  $(window).unbind("beforeunload");
	  disableAllButtons();
	  submitChangeRequestForm();
  }
  
  
  function assignAndTransferChangeRequest(){
	  $("#lstBox2 option").removeAttr('disabled');
	  $("#lstBox2 option").attr('selected', 'selected');
	  var url = "<c:url value='/assignAndTransferChangeRequest.htm'/>";
	  url=turnOnTimestampCheck(url);
	  $("#changeRequestDTO")[0].action=url;
	  $(window).unbind("beforeunload");
	  disableAllButtons();
	  submitChangeRequestForm();
  }
  
  function transferChangeRequestOwnerShip(){
	  $("#lstBox2 option").removeAttr('disabled');
	  $("#lstBox2 option").attr('selected', 'selected');
	  var url = "<c:url value='/transferChangeRequestOwnership.htm'/>";
	  url=turnOnTimestampCheck(url);
	  $("#changeRequestDTO")[0].action=url;
	  disableAllButtons();
	  $(window).unbind("beforeunload");
	  submitChangeRequestForm();
  }
  
   function confirmDeleteChangeRequest(){
	  if (confirm('You are about to delete this change request. All the changes,including changes to classification, If applicable, will be gone. Are you sure?')){
		  deleteChangeRequest();
	  }
   }
    
  
  
  function deleteChangeRequest(){
	  $("#lstBox2 option").removeAttr('disabled');
	  $("#lstBox2 option").attr('selected', 'selected');
	  var url = "<c:url value='/deleteChangeRequest.htm'/>";
	  url=turnOnTimestampCheck(url);
	  $("#changeRequestDTO")[0].action=url;
	  $(window).unbind("beforeunload");
	  disableAllButtons();
	  submitChangeRequestForm();
	 // opener.location.reload(); // or opener.location.href = opener.location.href;
    //  setTimeout( self.close(),3000);
	 // self.close();   // will trigger the   window.onunload event
   }
  
  
  var adviceMessageEditor;
  
  function getAdviceForChangeRequest(){
	  $("#lstBox2 option").removeAttr('disabled');
	  $("#lstBox2 option").attr('selected', 'selected');
	  var adviceMsg = adviceMessageEditor.getData();
	 // alert(adviceMsg);
	  $('#adviceMsg').val(adviceMsg);
	  var url = "<c:url value='/getAdviceForChangeRequest.htm'/>";
	  url=turnOnTimestampCheck(url);
	  $("#changeRequestDTO")[0].action=url;
	  $(window).unbind("beforeunload");
	  disableAllButtons();
	  submitChangeRequestForm();
  }
  
  
  function readyForAccept(){
	  $("#lstBox2 option").removeAttr('disabled');
	  $("#lstBox2 option").attr('selected', 'selected');
	  var url = "<c:url value='/readyForAccept.htm'/>";
	  url=turnOnTimestampCheck(url);
	  $("#changeRequestDTO")[0].action=url;
	  $(window).unbind("beforeunload");
	  disableAllButtons();
	  submitChangeRequestForm();
  }
  
  function acceptChangeRequest(){
	  $("#lstBox2 option").removeAttr('disabled');
	  $("#lstBox2 option").attr('selected', 'selected');
	  var url = "<c:url value='/acceptChangeRequest.htm'/>";
	  url=turnOnTimestampCheck(url);
	  $("#changeRequestDTO")[0].action=url;
	  $(window).unbind("beforeunload");
	  disableAllButtons();
	  submitChangeRequestForm();
  }
  
  
  
 function removeTableRow(icon){
 	$(icon).closest('tr').remove();
 }

 function removeTableRowWithEditor(icon, editorIndex){
	 editors[editorIndex].destroy();
	 removeTableRow(icon);
 }
 
 var editors=[]; 
 var addedEditorIndex =0;  

  function addQuestionForReviewer(){
 	 var currentNum= $('#tbl_questions tbody tr:last td:first').text();
 	 var nextNum =1;
 	 var nextIndex =0;
 	 if (currentNum){
  	    nextNum = parseInt(currentNum, 10) +1;
  	    nextIndex = parseInt(currentNum, 10);
 	 }
  	 var reviewTxt_elementId ="questionForReviewers"+nextIndex+"questionForReviewerTxt";
  	 var reviewTxt_elementName= "questionForReviewers["+nextIndex+"].questionForReviewerTxt";
  	 var reviewerId_elementId ="questionForReviewers"+nextIndex+"reviewerId";
 	 var reviewerId_elementName= "questionForReviewers["+nextIndex+"].reviewerId";
 	 
  	 var rowHtml =  nextNum+".";
  	 
  	 var row = ' <tr> '+
           '<td class="alignTop" >'+ rowHtml +' </td>'+
           '<td>	<textarea cols="70" id="'+reviewTxt_elementId +'" name="'+reviewTxt_elementName +'" rows="3">'+
                  '</textarea>'+ 
                  '<div class="alignRight">'+
                 ' Reviewer :  <select id="' +reviewerId_elementId +'" name="'+reviewerId_elementName +'" class="reviewerDropDown">'+
                                '</select>'+
                                '&nbsp;&nbsp;<input type="button"  value="Send for Review" class="button" onclick="javascript:sendForReviewChangeRequest('+nextIndex+');" >&nbsp;&nbsp;'+            
	                 '</div>'+
            '</td>'+
            '<td class="alignTop" > <div class="alignRight">  <img src=\'<c:url value="/img/icons/Delete.png" />\' alt="Remove" onclick="javascript:removeTableRowWithEditor(this,'+addedEditorIndex+');"/></div> </td>'+
            '</tr>';
      $row = $(row);
  	 $("#tbl_questions").find('tbody') .append($row);
  	 $("#lstBox2 option").removeAttr('disabled');
  	 $('#'+reviewerId_elementId).append($('#lstBox2 option').clone());
     globalCheckingReviewGroups()
  	
  	editors[addedEditorIndex]= CKEDITOR.replace( reviewTxt_elementId );
   	addedEditorIndex++;
  }
 
 
  function addDiscussion(){
   	 var currentNum= $('#tbl_discussions tbody tr:last td:first').text();
   	 var nextNum =1;
 	 var nextIndex =0;
 	 if (currentNum){
   	    nextNum = parseInt(currentNum, 10) +1;
   	    nextIndex = parseInt(currentNum, 10);
  	 }
   	 
   	 var elementId ="commentDiscussions"+nextIndex+".userCommentTxt";
   	 var elementName= "commentDiscussions["+nextIndex+"].userCommentTxt";
   	
   	 var rowHtml =  nextNum+".";
   	 
   	 var row = ' <tr> '+
            '<td class="alignTop" >'+ rowHtml +' </td>'+
            '<td>	<textarea cols="70" id="'+elementId +'" name="'+elementName +'" rows="3">'+
                   '</textarea>'+ 
             '</td>'+
             '<td class="alignTop" ><div class="alignRight">  <img src=\'<c:url value="/img/icons/Delete.png" />\' alt="Remove" onclick="javascript:removeTableRowWithEditor(this,'+addedEditorIndex+');"/></div> </td>'+
            '</tr>';
     $row = $(row);
    
   	 $("#tbl_discussions").find('tbody') .append($row);
   
   	editors[addedEditorIndex]= CKEDITOR.replace( elementId );
   	addedEditorIndex++;
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
           '<td>ID: &nbsp;<input id="'+cq_eQueryId_elementId +'" name="'+cq_eQueryId_elementName +'" type="text" value=""  size="10" maxlength="10" /></td>'+
           '<td>URL: &nbsp;	<input id="'+cq_url_elementId +'" name="'+cq_url_elementName +'" type="text" value="" size="50" maxlength="250"/></td>'+
           '<td></td>'+
           '<td> <div class="alignRight"> <img src=\'<c:url value="/img/icons/Delete.png" />\' alt="Remove" onclick="javascript:removeTableRow(this);"/></div></td>'+
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
           '<td><input type="file" name="'+ud_elementName +'" size="50" maxlength="250"  /></td>'+
           '<td><div class="alignRight"> <img src=\'<c:url value="/img/icons/Delete.png" />\' alt="Remove" onclick="javascript:removeTableRow(this);"/> </div></td>'+
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
           '<td><input type="text" name="'+ud_elementName +'" size="50" maxlength="250" /></td>'+
           '<td>&nbsp;</td>'+
           '<td><div class="alignRight"> <img src=\'<c:url value="/img/icons/Delete.png" />\' alt="Remove" onclick="javascript:removeTableRow(this);"/></div> </td>'+
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
           '<td><input type="file" name="'+oa_elementName +'" size="50" maxlength="250" />'+
           '</td>'+
           '<td ><div class="alignRight">  <img src=\'<c:url value="/img/icons/Delete.png" />\' alt="Remove" onclick="javascript:removeTableRow(this);"/></div> </td>'+
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
           '<td><input type="text" name="'+oa_elementName +'" size="50" maxlength="250" /></td>'+
           '<td>&nbsp;</td>'+
           '<td ><div class="alignRight">  <img src=\'<c:url value="/img/icons/Delete.png" />\' alt="Remove" onclick="javascript:removeTableRow(this);"/></div> </td>'+
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
          $("#lastActiveSectionDiv").val( divId );
 	 }else {
 		 $('#' + divId).slideUp();
 		 var img_new = '<c:url value="/img/icons/Collapse.png" />';
 		 $('#'+iconId+' img').attr("src", img_new);
 		 $("#lastActiveSectionDiv").val('');
 	  }
  }
  
  
  
  
  function hideDiv(iconId, divId){
	 	$('#' + divId).hide();
	 	var img_new = '<c:url value="/img/icons/Collapse.png" />';
	 	$('#'+iconId+' img').attr("src", img_new);
  }
	  
  
  
  function popupChangeRequestFile(changeRequestId, attachmentId,attachmentType) {  // other or urc
     var link = "openChangeRequestFile.htm?changeRequestId="+changeRequestId +"&attachmentId="+attachmentId+"&attachmentType=" + attachmentType  ;
	 var newwindow = window.open(link, "changeRequestFile", "width=700,height=750,resizable=yes,scrollbars=yes ");
	 if (window.focus)  {
	     newwindow.focus();
	 }
  }  
  
  function popupChangeRequestViewer(changeRequestId) {
      var link = "manageChangeRequest.htm?changeRequestId="+changeRequestId ;
	  var newwindow = window.open(link, "changeRequest", "width=1200,height=750,resizable=yes,scrollbars=yes ");
	  if (window.focus)  {
		  newwindow.focus();
	  }
  }	
  


  
  function printChangeRequest(changeRequestId){
	  var link = "printChangeRequest.htm?changeRequestId="+changeRequestId ;
	  var newwindow = window.open(link, "changeRequestPrint"+changeRequestId, "width=700, height="+(screen.height-(screen.height/20))+",resizable=yes,scrollbars=yes ");
	  var left = (screen.width-700)/2;
	  newwindow.moveTo(left,0);
	  if (window.focus)  {
		  newwindow.focus();
	  }
  }
  
  
 
 </script>

 

<div class="content">
   
    <form:form id="changeRequestDTO"  modelAttribute="changeRequestDTO" method="post"  enctype="multipart/form-data" >
         <div id="button-header" class="fixed">
           <div class="wrapper">
            <div class="alignRight">
            
                <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_SUBMIT")}'>   
	               <span class="required"> If change request is ready to be released, press Submit button.  &nbsp; &nbsp; </span>
	            </c:if> 
            
	            <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_SAVE") || allowClosedCommenting}'>
	              <input type="button"  value="Save" class="button" onclick="javascript:saveChangeRequest();" >&nbsp;&nbsp; 
	            </c:if>
	            <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_SUBMIT")}'> 
	              <input type="button"  value="Submit" class="button" onclick="javascript:submitChangeRequest();" >&nbsp;&nbsp; 
	            </c:if>
	           
	            <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_TAKE_OVER")}'>
	                <security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR')">
	                  <input type="button"  value="Take Over" class="button" onclick="javascript:takeOverChangeRequest();" >&nbsp;&nbsp; 
	                </security:authorize>
	                <security:authorize access="!hasAnyRole('ROLE_ADMINISTRATOR')">
	                  <c:if test="${changeRequestDTO.assigneeDLId !=null && cf:isUserInGroup(currentUser,changeRequestDTO.assigneeDLId)}" >   <!--only when it is assign to a DL  -->
	                    <input type="button"  value="Take Over" class="button" onclick="javascript:takeOverChangeRequest();" >&nbsp;&nbsp; 
	                  </c:if>  
	                </security:authorize>
	            </c:if>
	            <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_ASSIGN")}'>
	               <input type="button"  value="Assign" class="button" onclick="javascript:openAssignDialogBox();" >&nbsp;&nbsp; 
	            </c:if>   
	        
	            <!-- only admin has this access, change the assignee and owner at same time -->
	            <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_ASSIGNOWNER")}'>
	               <input type="button"  value="Assign" class="button" onclick="javascript:openTransferOwnerDialogBox();" >&nbsp;&nbsp; 
	            </c:if>   
	            
	           
	            <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_DELETE")}'>
	                <input type="button"  value="Delete" class="button" onclick="javascript:confirmDeleteChangeRequest();" >&nbsp;&nbsp; 
	            </c:if>   
	           
	            <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_OWNER_TRANSFER")}'>
	              <input type="button"  value="Transfer Ownership" class="button" onclick="javascript:openTransferOwnerDialogBox();" >&nbsp;&nbsp; 
	            </c:if>  
	            <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_READY_FOR_ACCEPT")}'>
	              <input type="button"  value="Ready for Accept" class="button" onclick="javascript:readyForAccept();" >&nbsp;&nbsp; 
	            </c:if>
	            <c:if test="${changeRequestDTO.changeRequestId gt 0 }">
	            <input type="button"  value="Print" class="button" onclick="javascript:printChangeRequest(${changeRequestDTO.changeRequestId});" >&nbsp;&nbsp; 
	          	</c:if>
	              
	            
	            <%-- comment out for now
	            <input type="button"  value="Accept" class="button" onclick="javascript:acceptChangeRequest();" >&nbsp;&nbsp; 
	            <input type="button"  value="Send Back" class="button" onclick="javascript:sendBackChangeRequest();" >&nbsp;&nbsp;
	            <input type="button"  value="Ready For Realization" class="button" onclick="javascript:sendBackChangeRequest();" >&nbsp;&nbsp;
	            <input type="button"  value="Ready For Translation" class="button" onclick="javascript:sendBackChangeRequest();" >&nbsp;&nbsp; 
	            <input type="button"  value="Ready For Validation" class="button" onclick="javascript:sendBackChangeRequest();" >&nbsp;&nbsp; 
	            <input type="button"  value="QA Done" class="button" onclick="javascript:sendBackChangeRequest();" >&nbsp;&nbsp; 
	            
	            <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_PRINT")}'>
	              <input type="button"  value="Print" class="button" onclick="javascript:deleteChangeRequest();" >&nbsp;&nbsp; 
	            </c:if>
	            --%>
	      
           <div class="btn_alignRight">
               <c:if test="${changeRequestDTO.deferredChangeRequestId !=null }">
                 Original Deferring CR: &nbsp;  <a href="javascript:popupChangeRequestViewer(${changeRequestDTO.deferredChangeRequestId});"> ${changeRequestDTO.deferredChangeRequestId}</a> &nbsp;&nbsp;&nbsp;
               </c:if>
              
               <c:if test="${changeRequestDTO.deferredTo !=null }">
                 This change request has been deferred to ${changeRequestDTO.deferredTo.baseVersionCode} ; Deferred CR:&nbsp; <a href="javascript:popupChangeRequestViewer(${changeRequestDTO.deferredTo.changeRequestId});"> ${changeRequestDTO.deferredTo.changeRequestId}</a> &nbsp;&nbsp;&nbsp;
               </c:if>
              
              
              Owner: &nbsp; ${changeRequestDTO.owner.username}  &nbsp;&nbsp;&nbsp;
              Assignee:   &nbsp; 
             <c:choose>
                <c:when test="${changeRequestDTO.userAssignee !=null }">
                    ${changeRequestDTO.userAssignee.username}
               </c:when>
                <c:otherwise>
                    ${changeRequestDTO.dlAssignee.name}
               </c:otherwise>  
             </c:choose>    
           </div>
         </div>
         </div>
       </div>
       
      
       
          <div id="assign_request_dialog" class="dialog_box" title="Assign Change Request - ${changeRequestDTO.changeRequestId}">
               Please specify the following
               <table class="accordion_table">
                     <tr>
                         <td >Recipient :  </td>
                         <td >
                          <form:select  path="assignedTo" >
    				          <c:forEach var="recipent" items="${allAssigneeRecipents}">
  	  	                         <form:option value="${recipent.code}"> ${recipent.description}  </form:option>
  	  	                      </c:forEach>
    			          </form:select>
                          </td>
                     </tr>
                  
              </table> 
                
          </div>
          
          <c:choose>
              <c:when test='${cf:hasExecuteAccess(currentUser,"BUTTON_ASSIGNOWNER")}'>
                  <c:set var="titleTxt"  value="Assign Change Request"/>
              </c:when>
              <c:otherwise>
                 <c:set var="titleTxt"  value="Transfer Ownership for Change Request"/>
              </c:otherwise>
         </c:choose>     
         
          <div id="owner_transfer_dialog" class="dialog_box" title="${titleTxt} - ${changeRequestDTO.changeRequestId}">
               Please specify the following
               <table class="accordion_table">
                     <tr>
                         <td >Recipient :  </td>
                         <td >
                          <form:select  path="transferedTo" >
    				          <c:forEach var="recipent" items="${allOwnerRecipents}">
  	  	                         <form:option value="${recipent.userId}"> ${recipent.username}  </form:option>
  	  	                      </c:forEach>
    			          </form:select>
                          </td>
                     </tr>
              </table> 
          </div>
         
      
         
         
        <input type="hidden" id="adviceMsg" name="adviceMsg" >
         
       <div id="get_advice_dialog" class="dialog_box" title="Get Advice For Change Request - ${changeRequestDTO.changeRequestId}">
               Please specify the following
               <table class="accordion_table">
                     <tr>
                         <td >Recipient :  </td>
                         <td >
                          <form:select  path="adviceRecipient" >
    				          <c:forEach var="recipent" items="${allAdvisors}">
  	  	                         <form:option value="${recipent.code}"> ${recipent.description}  </form:option>
  	  	                      </c:forEach>
    			          </form:select>
                          </td>
                     </tr>
                     <tr>
                         <td valign="top">Message :  </td>
                         <td >
                            <form:textarea path="advice.message"   cssStyle=" height:60px; width:300px; white-space: normal; overflow-y: scroll; overflow-x: hidden;"  />
    				      </td>
                     </tr>
                </table>     
        </div>
         
         
         
          <form:hidden path="changeRequestId"/>
          <form:hidden path="baseVersionCode"/>
          
          <form:hidden path="assigneeUserId"/>
          <form:hidden path="assigneeDLId"/>
          <form:hidden path="ownerId"/>
          <form:hidden path="status"/>
          <form:hidden path="assignorId"/>
          <form:hidden path="deferredChangeRequestId"/>
          <form:hidden path="owner.username"/>
          <form:hidden path="userAssignee.username"/>
          <form:hidden path="dlAssignee.name"/>
        
          <form:hidden path="lastUpdatedTime"/>
        
        <c:if test="${updatedSuccessfully}"> 
        <div id="updatedSuccessfully" class="success" >
             Changes have been updated successfully. 
        </div>
        </c:if>
       
        
        
        <div id="concurrentError" class="error" style="display:none;">
             
        </div>
        
         <c:if test="${changeRequestDTO.status=='VALID_INCOMPLETE' || changeRequestDTO.status=='ACCEPTED_INCOMPLETE' }"> 
          <form:hidden path="rationaleForIncomplete"/>
           <div  class="notice" >
             The change request is sent back due to: ${changeRequestDTO.rationaleForIncomplete}
           </div>
        </c:if>
        
        
        
         <form:errors path="*" cssClass="errorMsg" />
        
        <input type="hidden" id="lastActiveSectionDiv" name="lastActiveSectionDiv" value="${lastActiveSectionDiv}">
        
         <c:choose>
            <c:when test='${cf:hasWriteAccess(currentUser,"SECTION_CHANGE_REQUEST_BASIC")}'>
                    <jsp:include page="section_basicInfo.jsp"/>
            </c:when>
            <c:otherwise>
                <jsp:include page="section_basicInfo_readonly.jsp"/>
            </c:otherwise>
         </c:choose>
          
      
 
    <div id="rational" class="section">
            <div id ="icon_rational" class="left_section">
                 <img src='<c:url value="/img/icons/Expand.png" />' alt="Expanded Mode" onclick="javascript:hideShowDiv('icon_rational','div_rational');"/>
            </div>
            <div  class="right_section">
              <div class="sectionHeader" > Rationale for Change and Decisions </div>
                <div id ="div_rational">
                    <div id="label_rationalForChange"><span class="required">*</span>  Rationale for Change: </div>   
                  	<form:textarea path="changeRationalTxt" rows="3"  readonly="${cf:hasWriteAccess(currentUser,'SECTION_CHANGE_REQUEST_BASIC') ? 'false' : 'true'}"/>
			    <script>
    			   CKEDITOR.replace( 'changeRationalTxt', {readOnly : ${cf:hasWriteAccess(currentUser,'SECTION_CHANGE_REQUEST_BASIC') ? false : true}} );
    	        </script>
    	       <br/>
    	      
    	       <c:if test='${cf:hasReadAccess(currentUser,"SECTION_RATIONALE_FOR_STATUS_CHANGE")}'> 
    	        <div id="label_rationalForValid"><span class="required">*</span> Rationale for "Valid" Status :  </div>
                  	<form:textarea path="rationaleForValid" rows="3" />
			    <script>
    			   CKEDITOR.replace( 'rationaleForValid',{readOnly : ${cf:hasWriteAccess(currentUser,'SECTION_RATIONALE_FOR_STATUS_CHANGE') ? false : true}}  );
    	        </script>
    	        <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_VALIDATE")}'>
    	          <div class="btn_alignRight">
	                         <input type="button"  value="Validate" class="button" onclick="javascript:validateChangeRequest();" >&nbsp;&nbsp; 
                 </div>
                 </c:if>
                 <c:if test='${!cf:hasExecuteAccess(currentUser,"BUTTON_VALIDATE")}'>
    	          <div class="btn_alignRight">
	                         <input type="button"  value="Validate" class="disabledButton" disabled="disabled" onclick="javascript:validateChangeRequest();" >&nbsp;&nbsp; 
                 </div>
                 </c:if>
                <div id="div_defer_subsection">
                <c:if test="${fn:length(deferableContextIdentifiers) gt 0}">
    	        <span class="required">*</span>Rationale for "Closed-Deferred" Status :    
                  	<form:textarea path="rationaleForClosedDeferred" rows="3" />
			    <script>
    			   CKEDITOR.replace( 'rationaleForClosedDeferred' ,{readOnly : ${cf:hasWriteAccess(currentUser,'SECTION_RATIONALE_FOR_STATUS_CHANGE') ? false : true}});
    	        </script>
    	        <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_DEFER")}'>
    	        Defer to Year :  <form:select path="deferredToBaseContextId" id="deferredToBaseContextId">
    	                              <form:option value=""></form:option>
    	                              <c:forEach var="deferableContextIdentifier" items="${deferableContextIdentifiers}">
  	  	                                 <form:option value="${deferableContextIdentifier.contextId}"> ${deferableContextIdentifier.versionCode} </form:option>
  	  	                              </c:forEach>
    	                         </form:select>   
    	          
    	          <div class="btn_alignRight">
	                <input type="button"  value="Defer" class="button" onclick="javascript:deferChangeRequest();">&nbsp;&nbsp; 
                  </div>
                  </c:if>
                  <c:if test='${!cf:hasExecuteAccess(currentUser,"BUTTON_DEFER")}'>
                  Defer to Year :  <form:select path="deferredToBaseContextId" id="deferredToBaseContextId" disabled="true">
    	                              <form:option value=""></form:option>
    	                              <c:forEach var="deferableContextIdentifier" items="${deferableContextIdentifiers}">
  	  	                                 <form:option value="${deferableContextIdentifier.contextId}"> ${deferableContextIdentifier.versionCode} </form:option>
  	  	                              </c:forEach>
    	                         </form:select>   
    	          <div class="btn_alignRight">
	                <input type="button"  value="Defer" class="disabledButton" disabled="disabled" javascript:deferChangeRequest();">&nbsp;&nbsp; 
                  </div>
                  </c:if>
                 </c:if>
                </div>    
                
    	         <span class="required">*</span>Rationale for "Rejected" Status :    
                  	<form:textarea path="rationaleForReject" rows="3" />
			    <script>
    			   CKEDITOR.replace( 'rationaleForReject' ,{readOnly : ${cf:hasWriteAccess(currentUser,'SECTION_RATIONALE_FOR_STATUS_CHANGE') ? false : true}});
    	        </script>
    	         <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_REJECT")}'>
    	          <div class="btn_alignRight">
	                   <input type="button"  value="Reject" class="button" onclick="javascript:rejectChangeRequest();">&nbsp;&nbsp; 
                  </div>
                  </c:if>
                 <c:if test='${!cf:hasExecuteAccess(currentUser,"BUTTON_REJECT")}'>
    	          <div class="btn_alignRight">
	                   <input type="button"  value="Reject" class="disabledButton" disabled="disabled" javascript:rejectChangeRequest();">&nbsp;&nbsp; 
                  </div>
                  </c:if>
               </c:if>   
                  
                </div>
			 </div>
         </div>

         <c:choose>
            <c:when test='${cf:hasWriteAccess(currentUser,"SECTION_Q_FOR_R_QUESTION")}'>
                    <jsp:include page="section_questionForReviewers.jsp"/>
            </c:when>
            <c:otherwise>
                <jsp:include page="section_questionForReviewers_readonly.jsp"/>
            </c:otherwise>
         </c:choose>      


 
        <div id="discussions" class="section">
          	   <div id ="icon_discussions" class="left_section">
          	     <img src='<c:url value="/img/icons/Expand.png" />' alt="Expanded Mode" onclick="javascript:hideShowDiv('icon_discussions','div_discussions');"/>
          	   </div>  
                <div  class="right_section">
                 <div class="sectionHeader" >  Discussions and Comments </div>
			   </div>
                    <div id="div_discussions" class="right_section">     
                    <table id="tbl_discussions">
                    <tbody>
                      <c:forEach items="${changeRequestDTO.commentDiscussions}" var="commentDiscussion" varStatus="status">
                       <tr>
                       <td class="alignTop" > ${status.index+1} . </td>
                       <td>	
                       		<form:hidden path="commentDiscussions[${status.index}].userCommentId" />
                       		<form:textarea path="commentDiscussions[${status.index}].userCommentTxt" rows="3" readonly="${cf:hasWriteAccess(currentUser,'SECTION_CHANGE_REQUEST_BASIC') ? 'false' : 'true'}"/> 
                       </td>
                       <td>&nbsp;&nbsp; ${commentDiscussion.commmentUser.username}  </td>
                       
		                <script>
		                	CKEDITOR.replace( "commentDiscussions[${status.index}].userCommentTxt", {readOnly : ${cf:hasWriteAccess(currentUser,'SECTION_CHANGE_REQUEST_BASIC') ? false : true}} );
                        </script>
                       </tr>
                       </c:forEach> 
                      
                     </tbody>
		              </table>
		                <c:if test='${cf:hasWriteAccess(currentUser,"SECTION_DISC_COMMENT") || allowClosedCommenting}'>
		                <div class="alignRight">
		                 <a href="javascript:addDiscussion();">Add Discussion or Comment  </a>
		               </div>  
		               </c:if>
		           </div>
		     </div> 
          <c:if test='${cf:hasReadAccess(currentUser,"SECTION_ADVICE_COMMENT")}'>
          <div id="advices" class="section">
          	   <div id ="icon_advices" class="left_section">
          	     <img src='<c:url value="/img/icons/Expand.png" />' alt="Expanded Mode" onclick="javascript:hideShowDiv('icon_advices','div_advices');"/>
          	   </div>  
                <div  class="right_section">
                 <div class="sectionHeader" >  Received Advices </div>
			   </div>
                    <div id="div_advices" class="right_section">   
                      
                    <table id="tbl_advices">
                     <tbody>
                      <c:forEach items="${changeRequestDTO.advices}" var="advice" varStatus="status">
                      <tr>
                       <td class="alignTop" > ${status.index+1} . </td>
                       <td>	
                           <form:hidden path="advices[${status.index}].adviceId" />
                           <form:hidden path="advices[${status.index}].senderId" />
                           <form:hidden path="advices[${status.index}].sender.username" />
                           <form:hidden path="advices[${status.index}].userAdvisor.username" />
                           <form:hidden path="advices[${status.index}].dlAdvisor.name" />
                           <form:hidden path="advices[${status.index}].lastUpdatedTime" />
                           <form:hidden path="advices[${status.index}].userProfileId" />
                           <form:hidden path="advices[${status.index}].distributionListId" />
                           
                          <form:textarea path="advices[${status.index}].message" rows="3" cssStyle=" height:60px; width:850px; white-space: normal; overflow-y: scroll; overflow-x: hidden;"  readonly="true"/>
                         
                             <script>
    			               CKEDITOR.replace( 'advices[${status.index}].message' ,{readOnly :  true });
                             </script>
                         
                          </br>
                          <div class="alignRight">
		                   Date: <fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${advice.lastUpdatedTime}" /> &nbsp;&nbsp; 
		                   Requestor: ${advice.sender.username}  &nbsp;&nbsp; 
		                   Recipient: ${advice.userAdvisor.username}&nbsp; ${advice.dlAdvisor.name}
		                  </div>
                           <c:forEach items="${advice.adviceComments}" var="adviceComment" varStatus="statusComment">
    		                <div class="alignTop">
    		                 <form:hidden path="advices[${status.index}].adviceComments[${statusComment.index}].userCommentId" />
    		                   Comment ${status.index+1} - ${statusComment.index+1} .  &nbsp;&nbsp; Commented by: ${adviceComment.commmentUser.username}   
    		                         <form:textarea path="advices[${status.index}].adviceComments[${statusComment.index}].userCommentTxt" disabled="true" cssStyle=" height:50px; width:650px; white-space: normal; overflow-y: scroll; overflow-x: hidden;"/>
    		                  <script>
    			               CKEDITOR.replace( 'advices[${status.index}].adviceComments[${statusComment.index}].userCommentTxt' ,{readOnly :  true });
                             </script>
    		                </div>
    		                <br/>
    		                </c:forEach>
                             <c:if test='${cf:isUserAdviceRecipient(currentUser,advice)}'>
                                <c:set var="adviceCommentsLength"  value="${fn:length(advice.adviceComments)}"/>
                              <div class="alignTop">
    		                     Comment ${status.index+1} - ${fn:length(advice.adviceComments)+1} .
    		                         <form:textarea path="advices[${status.index}].adviceComments[${fn:length(advice.adviceComments)}].userCommentTxt" cssStyle=" height:50px; width:650px; white-space: normal; overflow-y: scroll; overflow-x: hidden;" />
    		                  <script>
    			                  CKEDITOR.replace( 'advices[${status.index}].adviceComments[${adviceCommentsLength}].userCommentTxt' ,{readOnly :  false });
                             </script>
    		                  </div>
    		                  <div class="btn_alignRight">
    		                     <input type="button"  value="Add Comment" class="button" onclick="javascript:addCommentForAdvice(${advice.adviceId});" >&nbsp;&nbsp; 
    		                   </div> 
    		                 </c:if>  
                       </td>
                    </tr>
		            </c:forEach>
                     </tbody>
		            </table>
		               <c:if test='${cf:hasExecuteAccess(currentUser,"BUTTON_GET_ADVICE")}'>
	                      <div class="alignRight">
		                    <a href="javascript:openGetAdviceDialogBox();">Get Advice  </a>
	                        <%--<input type="button"  value="Get Advice" class="button" onclick="javascript:openGetAdviceDialogBox();">&nbsp;&nbsp; ----%>
                          </div>
	                  </c:if>
		         </div>
		     </div> 
		     </c:if>
	
	      <c:choose>
            <c:when test='${cf:hasWriteAccess(currentUser,"SECTION_REFERENCES")}'>
                    <jsp:include page="section_references.jsp"/>
            </c:when>
            <c:otherwise>
                <jsp:include page="section_references_readonly.jsp"/>
            </c:otherwise>
         </c:choose>         
          
    </form:form>
</div>
</body>
</html>

