<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<style type="text/css">
	.listTable td {
		line-height:1;
	}
	
   .sectionHeader {
       color: #FFFFFF;
       background-color:#669999;
    }

    .section {
       display:inline-block;
       width: 100%;
       margin: 0px 0px 0px 0px ;
    }
    
    .right_section {
       margin: 7px 0px 0px 0px ;
       text-align: left;
       float:left;
       display:inline-block;
       width :94%;
    }
    
    .left_section {
        display:inline-block;
        float:left;
     } 
     
     .center_section {
       margin: 7px 0px 0px 0px ;
       text-align: left;
       float:right;
       display:inline-block;
       width:94%;
    } 
    
    ins {
	    background-color: #c6ffc6;
	    line-height:1.1;
	}
	
	del {
	    background-color: #ffc6c6;
	    line-height:1.1;
	}
     
</style>

<script type="text/javascript">
	$(document).ready(function(jquery){
		
		$(".listTable tr").prettyTextDiff({
		        cleanup: true,
		        diffContainer: ".diff"
		  });
		
		  $( "#send_back_dialog" ).dialog({
	          autoOpen: false,
	          height: 350,
	          width: 500,
	          modal: true,
	          appendTo: "#changeRequestDTO",
	          buttons: {
	              "Send Back": function() {
	            	$('#rational').val($('#rationaleForIncomplete').val());
	                $( this ).dialog( "close" );
	            	sendBackChangeRequest(); 
	              },
	              Cancel: function() {
	                $( this ).dialog( "close" );
	              }
	          }
	      });
		  <c:if test="${errorMsg!=null}"> 
                    $("#send_back_dialog").dialog("open");
                   
          </c:if>
		
	});
	
    function hideShowDiv(clickedImage) {
    	var imageParent = $(clickedImage).parent();
    	var tabDiv = $(imageParent).parent();
    	var divId = $(tabDiv).find('#right');  	
   	 	var isHidden = $(divId).is(':hidden');
   	 	
   	 	if (isHidden){
			$(divId).slideDown();
            var img_new = '<c:url value="/img/icons/Expand.png" />';
            $(clickedImage).attr("src", img_new);
   	 	} else {
   		 	$(divId).slideUp();
   		 	var img_new = '<c:url value="/img/icons/Collapse.png" />';
   		 	$(clickedImage).attr("src", img_new);   		
   	 	}
	}
	
	function popupXmlProperty1(url){
		newWindow = window.open(url, 'ProposedChangeOld', 'top=300, left=300,height=400,width=850,scrollbars=0,resizable');  
	}
	function popupXmlProperty2(url){
		newWindow = window.open(url, 'ProposedChangeProposed', 'top=300, left=300,height=400,width=850,scrollbars=0,resizable');  	
	}
	function popupXmlProperty6(url){
		newWindow = window.open(url, 'ProposedChangeDiff', 'top=300, left=300,height=400,width=850,scrollbars=0,resizable');  	
	}
	function popupXmlProperty3(url){
		newWindow = window.open(url, 'ProposedChangeConflict', 'top=300, left=300,height=400,width=850,scrollbars=0,resizable'); 	
	}
	function popupXmlProperty4(url){
		newWindow = window.open(url, 'RealizedChangeOld', 'top=300, left=300,height=400,width=850,scrollbars=0,resizable'); 	
	}
	function popupXmlProperty5(url){
		newWindow = window.open(url, 'RealizedChangeNew', 'top=300, left=300,height=400,width=850,scrollbars=0,resizable'); 	
	}
	
	function popupIncompleteReport(url){
		newWindow = window.open(url, 'IncompleteReport', 'top=300, left=300,height=600,width=850,resizable'); 
	}
	
	function popupResolveConflicts(url){
		newWindow = window.open(url, 'ResolveConflicts', 'top=300, left=300,height=600,width=850,resizable'); 
	}
	
	function popupIndexInfo(url){
		var changeRequestId = document.getElementById("changeRequestId").value;
		url = url + "&amp;changeRequestId=" + changeRequestId;
		newWindow = window.open(url, 'LinkedIndexReferences', 'top=300, left=300,height=150,width=300,resizable'); 
	}
	
	
	function printAlert() {
		console.log('Feature not implemented yet');
	}

	
	
	
	
	/*The following javascript are for the buttons on the change summary   */
	
	function disableAllButtons(){
		  $("input[type=button]").attr("disabled", "disabled");
		  $("input[type=button]").attr("class", "disabledButton");
	}
	
	function enableAllButtons(){
		  $("input[type=button]").removeAttr('disabled');
		  $("input[type=button]").attr("class", "button");
	  }
		  
	var errorCallback = function(data) {
		hideLoading();
		var responseData = data.responseText;
		if(responseData != "undefined" && responseData != null) {
			$('#updatedSuccessfully').hide();
			$('#errorMsg').hide();
			$('#concurrentError').text(responseData).show();
			enableAllButtons();
		}
	};
	var replaceContent = function(data) {
	  document.open();
	  document.write(data);
	  document.close();
	};
	
	function submitChangeRequestForm(){
		 var $form = $("#changeRequestDTO");
		    $.ajax({
		        url: $form.attr("action"),
		        data: $form.serialize(),
		        type: "POST",
		        dataType: "html",
		        success: replaceContent,
		        error: errorCallback
		    });
	}
		  
	
	function openSendBackDialogBox(){
		 $("#send_back_dialog" ).dialog( "open" );
    }
	 
    function acceptChangeRequest(){
    	var url = "<c:url value='/acceptChangeRequest.htm'/>";
	    url=turnOnTimestampCheck(url);
	    $("#changeRequestDTO")[0].action=url;
		disableAllButtons();
		submitChangeRequestForm();
	  }
	
	
	function sendBackChangeRequest(){
		var url = "<c:url value='/sendBackChangeRequest.htm'/>";
	    url=turnOnTimestampCheck(url);
	    $("#changeRequestDTO")[0].action=url;
		disableAllButtons();
		submitChangeRequestForm();
	  }
	
	 function readyForRealizeChangeRequest(){
			
		var url = "<c:url value='/readyForRealizeChangeRequest.htm'/>";
	    url=turnOnTimestampCheck(url);
	    $("#changeRequestDTO")[0].action=url;
		disableAllButtons();
		submitChangeRequestForm();
     }
	
	 function readyForTranslationChangeRequest(){
		 var url = "<c:url value='/readyForTranslationChangeRequest.htm'/>";
		    url=turnOnTimestampCheck(url);
		    $("#changeRequestDTO")[0].action=url;
			disableAllButtons();
			submitChangeRequestForm();
     }
	
	 
	 function readyForValidationChangeRequest(){
		 	var url = "<c:url value='/readyForValidationChangeRequest.htm'/>";
		    url=turnOnTimestampCheck(url);
		    $("#changeRequestDTO")[0].action=url;
			disableAllButtons();
			submitChangeRequestForm();
     }
	 
	 function realizeChangeRequest(){
		 var url = "<c:url value='/realizeChangeRequest.htm'/>";
		    url=turnOnTimestampCheck(url);
		    $("#changeRequestDTO")[0].action=url;
			disableAllButtons();
			
			var changeRequestId =${changeRequestDTO.changeRequestId};
			
			submitChangeRequestForm();
			setTimeout(startProgress(changeRequestId),1000);
			
    }
	
	 function qaDoneChangeRequest(){
		 	var url = "<c:url value='/qaDoneChangeRequest.htm'/>";
		    url=turnOnTimestampCheck(url);
		    $("#changeRequestDTO")[0].action=url;
			disableAllButtons();
			submitChangeRequestForm();
     }
	
	function approveChangeRequest(){
			var url = "<c:url value='/approveChangeRequest.htm'/>";
		    url=turnOnTimestampCheck(url);
		    $("#changeRequestDTO")[0].action=url;
			disableAllButtons();
			submitChangeRequestForm();
	 }
	
	function printChangeSummary(changeRequestId){
		  var link = "printChangeSummary.htm?changeRequestId="+changeRequestId ;
		  var newwindow = window.open(link, "changeSummaryPrint"+changeRequestId, "width=700, height=850,resizable=yes,scrollbars=yes ");
		  var left = (screen.width-700)/2;
		  var top = 50;
		  newwindow.moveTo(left,top);
		  if (window.focus)  {
			  newwindow.focus();
		  }
	 }	 
	
	function printIndexChangeSummary(changeRequestId){
		  var link = "printIndexChangeSummary.htm?changeRequestId="+changeRequestId ;
		  var newwindow = window.open(link, "indexChangeSummaryPrint"+changeRequestId, "width=700, height=850,resizable=yes,scrollbars=yes ");
		  var left = (screen.width-700)/2;
		  var top = 50;
		  newwindow.moveTo(left,top);
		  if (window.focus)  {
			  newwindow.focus();
		  }
	 }
	
	function printSupplementChangeSummary(changeRequestId){
		  var link = "printSupplementChangeSummary.htm?changeRequestId="+changeRequestId ;
		  var newwindow = window.open(link, "supplementChangeSummaryPrint"+changeRequestId, "width=700, height=850,resizable=yes,scrollbars=yes ");
		  var left = (screen.width-700)/2;
		  var top = 50;
		  newwindow.moveTo(left,top);
		  if (window.focus)  {
			  newwindow.focus();
		  }
	 }
	
	function startProgress(changeRequestId){
 		
 		document.getElementById('progressStep').style.display = 'block';

 	    document.getElementById('progressStepText').innerHTML = 'processing realization:begins';
 	   	    // wait a little while to make sure the upload has started ..
 	    //window.setTimeout("refreshProgress()", 10);
        refreshProgress(changeRequestId);
 	    return true;
 	}    
	
	
	function refreshProgress(changeRequestId) 	{
	       var uploadInfo = function getUploadInfo() {
	 			$.ajax({
	 				url: "<%=request.getContextPath()%>/getRealizationState.htm?changeRequestId="+changeRequestId,
	 				type: "GET",
	 				cache: false,
	 				dataType: "html",
	 				async: true,
	 				success: function(msg){
	 				    document.getElementById('progressStepText').innerHTML = 'processing realization: ' + msg ;
	 				},
	 				error: function(msg){
	 					
	 				}
	 			});
	 		};
	 		var uploadInfoHandle = setInterval(uploadInfo, 500);
	 }
	
	
</script>