<html>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<body>
<form
	id="formQualifier"
	method="POST"
	action="<c:url value="/tabulars/qualifier/add.htm?${automaticContextParams}"/>"
>
<table border="1">
	<tr id="trQualifier">
		<td class="formRequiredLabel"><span class="required">*</span>Comment:</td>
		<td><textarea
			id="qualifierComment"
			name="comment"
			rows="6"
			cols="47"
		></textarea></td> 
	</tr>
	<tr>
		<td
			colspan="2"
			align="right"
		>
		<table width="100%">
			<tr>
				<td>
				<div
					id="statusLoadingQualifier"
					class="info"
					style="display: none"
				></div>
				<div
					id="statusResultQualifier"
					class="success"
					style="display: none"
				></div>
				</td>
				<td id="lnkRequestQualifierTd"
					style="text-align: right"
					width="35"
				><a
					href="#"
					id="lnkRequestQualifier"
				><img
					title="Request"
					height="32"
					src="<c:url value="/img/icons/EmailArrow.png"/>"
				/></a></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</form>

<script>
	$(function() {
		$("#lnkRequestQualifier")
				.click(
						function(event) {
							event.preventDefault();
							var status = $("#statusResultQualifier");
							var comment=$("#qualifierComment").val();
							if (comment == "") {
								status.attr("class", "error");
								status.text("Comment required");
								status.show();
							} else if (comment.length > 500) {
								status.attr("class", "error");
								status.text("Comment must be less then 500 characters");
								status.show();
							} else {
								showQualifierLoading("Requesting a qualifier ...");
								var form = $("#formQualifier");
								$
										.ajax( {
											type : form.attr('method'),
											url : form.attr('action'),
											data : form.serialize(),
											success : function(data) {
												hideQualifierLoading();
												if (data == "") {
													status.attr("class",
															"success");
													status
															.text("Your request for new component or qualifier has been sent to the Administrator. Please resume your update after notification of component or qualifier availability.");
													$("#trQualifier").hide();
													$("#lnkRequestQualifierTd")
															.hide();
												} else {
													status.attr("class",
															"error");
													status.text(data);
												}
												status.show();
											}
										});
							}
						});
	});
	function showQualifierLoading(message) {
		$("#statusLoadingQualifier").show();
		$("#statusLoadingQualifier").text(message);
		$("#statusResultQualifier").hide();
		showProcessingScreen();
	}
	function hideQualifierLoading() {
		$("#statusLoadingQualifier").hide();
		hideProcessingScreen();
	}
</script>
</body>
</html>
