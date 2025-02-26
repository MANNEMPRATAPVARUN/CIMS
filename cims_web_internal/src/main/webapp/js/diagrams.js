function manageDiagrams(contextPath, classification, year) {
    var width = 800;
    var height = 800;
    var left = (screen.width/2)-(width/2);
    var top = (screen.height/2)-(height/2);
    var url=contextPath+"/diagrams.htm?" + "year=" + year + "&bc=" + classification;
    var params='resizable=yes, scrollbars=yes, toolbar=no ,location=0, status=no, titlebar=no, menubar=no, ' + 'width=' + width + ', height=' + height + ', top=' + top + ', left='+left;
    //alert(url);
    //alert(params);
    window.open(url, 'Manage Diagrams', params );
}
