function loadXmlTemplate(contextPath, templateId, success){
	$.ajax({
	    url: contextPath+"/xmltemplates.htm",
	    contentType: "text/plain; charset=UTF-8",
	    dataType: "text",
	    data: {id:templateId},
	    success: success
	 });
}

function registerXmlTemplateButton(contextPath, templateId, buttonId, xmlControlIds){
	for(var i=0;i<xmlControlIds.length;i++){
		$("#"+xmlControlIds[i]).TextAreaResizer();
	}
	$("#"+buttonId).click(function() {
		for(var i=0;i<xmlControlIds.length;i++){
			var c=$("#"+xmlControlIds[i]);
			if(!(c.val()==""||c.val()==null)){
				alert("XML templates can only be used when no XML content is present");
				c.focus();
				return false;
			}
		}
		loadXmlTemplate(contextPath, templateId, function(data){
			for(var i=0;i<xmlControlIds.length;i++){
				var c=$("#"+xmlControlIds[i]);
				c.val(data);
			}
		});
		return false;
	});
}
