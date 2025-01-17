

<script type="text/javascript">
function confirmManage(){
	return confirm('<fmt:message key="confirm.manage"/>');
	document.changeRequest.submit();
}
</script>
<h2 class="pageTitle"><fmt:message key="manage.change.request" /></h2>
<div class="appTab">
<form:form method="POST" modelAttribute="requestViewBean" name="changeRequest">
	<table border="0" cellspacing="10">
		<tr>
			<td class="fieldlabel"><fmt:message key="change.request.id" />:</td>
			<td class="fieldlabel">${requestViewBean.requestId}</td>
			<td class="fieldlabel"><fmt:message key="change.request.classification" />:</td>
			<td class="fieldlabel">${requestViewBean.classificationCode}</td>
			<td class="fieldlabel"><fmt:message key="change.request.fiscalYear" />:</td>
			<td class="fieldlabel">${requestViewBean.versionCode}</td>
			<td class="fieldlabel">Request Category:</td>
			<td class="fieldlabel">Tabular List</td>
		</tr>
	</table>
	<form:hidden path="actionType"/>
	<c:if test="${requestViewBean.status == 'V' || requestViewBean.status == 'VD'}">
	<script>	
	$(document).ready(function() {     
		$("#idTab3").show();
      }); 	
    </script>
	</c:if>
   
	<ul>
		<li><a href="#tab1"><fmt:message key="manage.change.request" /></a></li>
		<li><a href="#tab2"><fmt:message key="change.request.modification.history" /></a></li>
		<!--<li><a href="#tab3" onclick=loadTree();><fmt:message key="manage.classification" /></a></li>--> 
		<li style="display:none;" id="idTab3"><a href="#tab3" onclick=loadTree();><fmt:message key="manage.classification" /></a></li>
		<li><a href="#tab4" onclick=reload();><fmt:message key="classification.change.summary" /></a></li>
	</ul>

<div id="tab1"><h3 class="contentTitle">Manage Change Request</h3>
	<table border="0">		
		<tr>
			<td class="fieldlabel" width=20%>Request Id:</td>
			<td>${requestViewBean.requestId}</td>
		</tr>		
		<tr>
			<td class="fieldlabel" width=20%>Request Context</td>
			<td>change A00</td>
		</tr>		
	</table>
		<div style="padding-top: 10px;">
		<button class="button" type="submit" onClick="confirmManage();"><fmt:message key="common.save" /></button>
		</div>
</div>

<div id="tab2"><h3 class="contentTitle">Change Request History</h3>
<fieldset>
	<legend><fmt:message key="change.request.modification.history"/></legend>	
 </fieldset>
</div>

<div id="tab3"><h3 class="contentTitle">Manage classification</h3>
<script type="text/javascript">
function loadTree(){ 
	$("#tree") .dynatree(
			{
				title : "Classification Tree",
				fx : {
					height : "toggle",
					cache  : false,
					duration : 200
				},
				autoFocus : false, // Set focus to first child, when expanding or lazy-loading.			
				initAjax : {
					url : "/cims_web_internal/getTreeData.htm?classification=${requestViewBean.classificationCode}&fiscalYear=${requestViewBean.versionCode}&language=${requestViewBean.languageCode}"
				},
				onActivate : function(node){
					window.open("/cims_web_internal/contents.htm"
							+"?classification="+node.data.classification
							+"&fiscalYear="+node.data.fiscalYear
							+"&language="+node.data.language
							+"&conceptId="+node.data.containerConceptId
							+"#"+ node.data.conceptCode, 
							"contents");					
				}, 

				onLazyRead : function(node) {
					node.appendAjax({
						url : "/cims_web_internal/getTreeData.htm"
						+"?classification="+node.data.classification
						+"&fiscalYear="+node.data.fiscalYear
						+"&language="+node.data.language
						+"&conceptId=" + node.data.conceptId
						+"&containerConceptId=" + node.data.containerConceptId				
	               });
				}
	  });
}
</script> 
  <table border="0">
		
		<tr>
		  <td>
			<div style="padding-left: 100px; padding-top: 10px;">
			<button class="button" type="button" ><fmt:message key="common.edit" /></button>
			</div>
		  </td>
		</tr>
		
		<tr>
			<td>			
				<div id="tree" style="float: left; width: 300px; height: 600px; padding-bottom:12px; overflow:hidden;"></div>
			</td>
					<!-- 
					<td>
						<div id="content"
							style="width: 600px; height: 400px; overflow: auto; padding: 0 0 0 2px;">
							<h1>Overview</h1>
							Concept Id:<span id="nodeKey"></span><br /> Title:<span
								id="nodeTitle"></span><br /> Concept Code:<span id="nodeCode"></span><br />
						</div>
					</td>
					-->			 		
			
			<td>			
				<div >			
				<iframe name="contents" id="contents" src="/cims_web_internal/contents.htm?classification=${requestViewBean.classificationCode}&fiscalYear=${requestViewBean.versionCode}&language=${requestViewBean.languageCode}" width="600px" height="600px" scrolling="yes" marginheight="0" marginwidth="0" frameborder="0">
				<p>Your browser does not support iframes</p>
				</iframe>
				</div>			
			</td>
		
		</tr>
	</table>
</div>

<div id="tab4"><h3 class="contentTitle">Change Summary</h3>
 <fieldset>
	<legend><fmt:message key="change.request.summary"/></legend>	
 </fieldset>
</div>
</form:form> 
<script>
	(function($) {
		$(".appTab").tabs( {
			select : function(event, ui) {
				window.location.replace(ui.tab.hash);
			}
		});
	})(jQuery);
</script>
</div>
