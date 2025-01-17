<div style="margin-left:20px">
	<span class="dark-red-bold parent">${bean.breadCrumbs}</span>
	<span class="green parent">${bean.model.description}
	<c:if test="${bean.model.level ne 0}">(level ${bean.model.level})</c:if> 
	</span>
</div>
<script>
	$(document).ready(function() {	    
	    $("a.tab").click(function(event){
	    	event.preventDefault();
	    	
	    	var data = {isViewMode: false, conceptId: "${bean.model.elementId}", conceptCode: "${bean.model.code}"};
			NavigationController.navigate(this.href, data);
	    });
	});
</script>


