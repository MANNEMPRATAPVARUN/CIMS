<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta name="description" content="${project.artifactId} - ${version}" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">

<!-- can be any external url -->

<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<link href="<c:url value="/resources/css/jquery.splitter.css" />" rel="stylesheet">
<link href="<c:url value="/resources/css/snomed.css"/>" rel="stylesheet">

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
<script	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
<script	src="<c:url value="/resources/core/jquery.splitter-0.20.0.js" />"></script>
<script	src="<c:url value="/resources/core/handlebars-v4.0.5.js" />"></script>

<c:set var="baseURL" value="${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, pageContext.request.contextPath)}" />

<link rel="stylesheet" href="${baseURL}/resources/css/snomed.css"/>

<script>
	var xhr = null;
	var thread = null;
	var lastT = "";
	
	function snomedInit() {	  
		$('#snomedModal').on('hidden.bs.modal', function (e) {
			  window.close();
		});
		$('#snomedModal').modal({
				backdrop:'static'
			});	
	    $('#lp_canvas_search_panel_searchBox').keyup(function (e) {
	     	clearTimeout(thread);
	        var $this = $(this);
	        thread = setTimeout(function () {
				var sctVersion = $('#release_version').val();
				var versionDesc = $("#release_version option:selected").html();
				$("#release_selected").val(versionDesc);
				$("#release_selected").prop("disabled", true);
					
	            search($this.val(),sctVersion, false);
	        }, 500);
		 });	
	    
	    $("#lp_canvas_search_clearButton").click(function () {
	        $('#lp_canvas_search_panel_searchBox').val('');
	        $('#lp_canvas_search_panel_typeIcon').removeClass('glyphicon-ok');
	        $('#lp_canvas_search_panel_typeIcon').removeClass('text-success');
	        $('#lp_canvas_search_panel_typeIcon').addClass('glyphicon-remove');
	        $('#lp_canvas_search_panel_typeIcon').addClass('text-danger');
	        $('#lp_canvas_search_panel_resultsTable').html("");
	        lastT = "";
	    });

		$('#btnSnomed').click(function() {
			$('#snomedModal').modal('show');
			var val = $('#snomed_concept_id').val();
			var sctVersion = $('#release_version').val();
			var versionDesc = $("#release_version option:selected").html();
			$("#release_selected").val(versionDesc);
			$("#release_selected").prop("disabled", true);
		
			if (val){
				$('#lp_canvas_search_panel_searchBox').val(val);
			    search(val, sctVersion, false);
			}
		});


	    $("#snomed_concept_id").bind('paste', function(event) {
			var val = $('#snomed_concept_id').val();
			var sctVersion = $('#release_version').val();

			if (val){
				$('#lp_canvas_search_panel_searchBox').val(val);
				var sctVersion = $('#release_version').val();
			    search(val, sctVersion, false);
			}
	    });

	    $('#concept_type').change(function () {
    	   $('#lp_canvas_search_panel_resultsTable').html("");
    	   $('#rp_canvas_descriptions_panel_table_body').html("");
    	   $('#rp_canvas_descriptions_panel_table_top').html("");		
    	   $('#left_pane').scrollTop(0);
    	   var val = $('#lp_canvas_search_panel_searchBox').val();
		   var sctVersion = $('#release_version').val();

		   if (val){
			   //$('#lp_canvas_search_panel_searchBox').val(val);
			   var sctVersion = $('#release_version').val();
			   search(val, sctVersion, false);
		   }
	    });
	}
	
	function search (t, sctVersion, forceSearch){
	    // panel.divElement.id + '-typeIcon
	    if (t != "" && t) {

	        if (t.length < 3) {
	            $('#lp_canvas_search_panel_typeIcon').removeClass('glyphicon-ok');
	            $('#lp_canvas_search_panel_typeIcon').removeClass('text-success');
	            $('#lp_canvas_search_panel_typeIcon').addClass('glyphicon-remove');
	            $('#lp_canvas_search_panel_typeIcon').addClass('text-danger');
	        } else {
	            $('#lp_canvas_search_panel_typeIcon').removeClass('glyphicon-remove');
	            $('#lp_canvas_search_panel_typeIcon').removeClass('text-danger');
	            $('#lp_canvas_search_panel_typeIcon').addClass('glyphicon-ok');
	            $('#lp_canvas_search_panel_typeIcon').addClass('text-success');

	            $('#lp_canvas_search_panel_resultsTable').html("<i class='glyphicon glyphicon-refresh icon-spin'></i>");
	            
	            var resultsHtml = "";
	            if (xhr != null) {
	                xhr.abort();
	                //console.log("aborting call...");
	            }

	            if(typeof String.prototype.trim !== 'function') {
	            	String.prototype.trim = function() {
	            		return this.replace(/^\s+|\s+$/g, ''); 
	            	}
	            }
	            t = t.trim();
	        	var conceptType = $('#concept_type').val();
	            var searchUrl = '${baseURL}/getTerms?term=' + t +'&version=' + sctVersion + '&conceptType='+conceptType;
	            //console.log(searchUrl);
	            xhr = $.getJSON(searchUrl,function (result) {
	
	            }).done(function (result) {
	                Handlebars.registerHelper('if_eq', function(a, b, opts) {
	                    if (opts != "undefined") {
	                        if(a == b)
	                            return opts.fn(this);
	                        else
	                            return opts.inverse(this);
	                    }
	                });
	                
	                
	                $('#lp_canvas_search_panel_resultsTable').find('.more-row').remove();
	                xhr = null;
	                var context = {
	                    result: result
	                };
	
	            	var source   = $("#lp_canvas_search_panel_resultsTable_Tmpl").html();
	            	var template = Handlebars.compile(source);
	
	            	var html = template(context);
	            	$("#lp_canvas_search_panel_resultsTable").html(html);

	            	if (result && result.constructor === Array && result.length >0){
		            	var firstConceptId = result[0].conceptId;

		            	//simulate the click the first description
		            	desClicked(firstConceptId, t);
		            } else {
		            	cleanDetailSection();
			        }
	
	            }).fail(function () {
		            
	                resultsHtml = resultsHtml + "<tr><td class='text-muted'>No Results Found</td></tr>";
	                $('#lp_canvas_search_panel_resultsTable').html(resultsHtml);
	            });
	        }
	    }
	}
	
	function  desClicked(conceptId, t){
	    $('#rp_canvas_descriptions_panel_table_body').html("<i class='glyphicon glyphicon-refresh icon-spin'></i>");
	    
	    var resultsHtml = "";
	    if (xhr != null) {
	        xhr.abort();
	    }

		var sctVersion = $('#release_version').val();
		var conceptType = $('#concept_type').val();
	    var searchUrl = '${baseURL}/getConceptDesps?conceptId=' + conceptId +'&version=' + sctVersion +'&conceptType='+conceptType;
	
	    xhr = $.getJSON(searchUrl,function (result) {
	    }).done(function (result) {
	
	        Handlebars.registerHelper('if_eq', function(a, b, opts) {
	            if (opts != "undefined") {
	                if(a == b)
	                    return opts.fn(this);
	                else
	                    return opts.inverse(this);
	            }
	        });
	        
	        
	        $('#rp_canvas_descriptions_panel_table_body').find('.more-row').remove();
	        xhr = null;
	        var context = {
	            result: result
	        };
	
	    	var source   = $("#rp_canvas_descriptions_panel_table_body_Tmpl").html();
	    	var template = Handlebars.compile(source);
	    	var html = template(context);
	    	$("#rp_canvas_descriptions_panel_table_body").html(html);
	    	
	    	if (result && result.constructor === Array && result.length > 0){
	        	var firstMatch = result[0];
	        	source   = $("#rp_canvas_descriptions_panel_table_top_Tmpl").html();
	        	template = Handlebars.compile(source);
	        	html = template(firstMatch);
	        	$("#rp_canvas_descriptions_panel_table_top").html(html);

	        	//check the possible description id, harmless to miss
				$("#radDesc_" +t).prop("checked", true);
	        	
	    	}
	    	
	    }).fail(function () {
	        resultsHtml = resultsHtml + "<tr><td class='text-muted'>No Results Found</td></tr>";
	        $('#rp_canvas_descriptions_panel_table_body').html(resultsHtml);
	    });
	
	}	
	
	function doneFunction() {
		var rad = $('input[name=radDesc]:checked', '#frmDesc');
		if (rad && rad.val()){
			var term = {
						"selectedTermId" : rad[0].attributes['data-selectedTermId'].value,
						"conceptId" : rad[0].attributes['data-conceptId'].value,
						"conceptType" : rad[0].attributes['data-conceptType'].value,
						"conceptFsnId" : rad[0].attributes['data-conceptFsnId'].value,
						"conceptFsn" : rad[0].attributes['data-conceptFsn'].value,
						"conceptPreferredId" : rad[0].attributes['data-conceptPreferredId'].value,
						"conceptPreferred" : rad[0].attributes['data-conceptPreferred'].value,
						"synonymId" : rad[0].attributes['data-synonymId'].value,
						"synonym" : rad[0].attributes['data-synonym'].value,
						"selectedTermType" : rad[0].attributes['data-selectedTermType'].value,
						"selectedTerm" : rad[0].attributes['data-selectedTerm'].value
					};
			if(rad[0].attributes['data-selectedTermAcceptability'].value=='Preferred'&&rad[0].attributes['data-selectedTermType'].value=='Synonym')
				term.selectedTermType='Preferred';
			if(term.synonymId==0)
				term.synonymId=null;
			window.opener.update(term);
		   window.close();
		}else{
			alert('no value is selected, exit with exit button');
		}
	}

	function cleanFunction() {
	   $('#lp_canvas_search_panel_searchBox').val('');			 
	   $('#lp_canvas_search_panel_resultsTable').html("");
	   $('#rp_canvas_descriptions_panel_table_body').html("");
	   $('#rp_canvas_descriptions_panel_table_top').html("");		
	   $('#left_pane').scrollTop(0);
	}

	function cleanDetailSection() {
	   $('#rp_canvas_descriptions_panel_table_body').html("");
	   $('#rp_canvas_descriptions_panel_table_top').html("");		   
	}
	
</script>

<script id="lp_canvas_search_panel_resultsTable_Tmpl" type="text/x-handlebars-template">
	{{#each result}}
    	<tr class='resultRow selectable-row'>
        	<td class='col-md-7'>
            	<div class='result-item' data-concept-id='{{conceptId}}' data-term='{{selectedTerm}}'>
	                <span class="badge alert-warning" data-concept-id="{{conceptId}}" data-term="{{selectedTerm}}">&equiv;</span>&nbsp;&nbsp;
        	        <a href='javascript:desClicked({{conceptId}});' style='color: inherit;text-decoration: inherit;' data-concept-id='{{conceptId}}' data-term='{{selectedTerm}}'>{{selectedTerm}}</a>
            	</div>
	        </td>
    	    <td class='text-muted small-text col-md-5 result-item' data-term='{{conceptFsn}}' data-concept-id='{{conceptId}}' data-term='{{conceptFsn}}'>
        	    {{conceptFsn}}
       	 </td>
	    </tr>
	{{/each}}

	{{#if_eq result.length 0}}
    	<tr><td class='text-muted'><span data-i18n-id="i18n_no_results" class="i18n">No Results Found</span></td></tr>
	{{/if_eq}}
	</script>
		
	<script id="rp_canvas_descriptions_panel_table_body_Tmpl" type="text/x-jsrender">
	{{#each result}}
		<tr class='{{#if_eq selectedTermType "FSN"}} fsn-row{{else}} synonym-row{{/if_eq}}'>
			<td><input type="radio" name="radDesc" value="{{selectedTermId}}" id="radDesc_{{selectedTermId}}" title="Description {{selectedTermId}}" 
			data-selectedTermId="{{selectedTermId}}" data-version="{{sctVersion}}" data-conceptId="{{conceptId}}" data-conceptType="{{conceptType}}"
			data-conceptFsnId="{{conceptFsnId}}" data-conceptFsn="{{conceptFsn}}" data-conceptPreferredId="{{conceptPreferredId}}" data-conceptPreferred="{{conceptPreferred}}"
			data-selectedTermType="{{selectedTermType}}" data-selectedTerm="{{selectedTerm}}" data-selectedTermAcceptability="{{selectedTermAcceptability}}" data-effectiveDate="{{effectiveDate}}"
            data-synonymId="{{synonymId}}" data-synonym="{{synonym}}"/>
			</td>

			<td>
				{{#if_eq selectedTermType "Fully specified name"}}
					<span rel="tooltip-right" title="FSN">F</span>
				{{else}}
					{{#if_eq selectedTermType "Synonym"}}
                    	<span rel="tooltip-right" title="Synonym">S</span>
					{{else}}
                    	{{#if_eq selectedTermType "D"}}
                        	<span rel="tooltip-right" title="Definition">D</span>
						{{/if_eq}}
					{{/if_eq}}
				{{/if_eq}}

                {{#if_eq selectedTermAcceptability "Preferred"}}
                	{{#if_eq selectedTermType "Fully specified name"}}
                    	&nbsp;<span class="glyphicon glyphicon-star-empty" rel="tooltip-right" title="Preferred"></span>
					{{else}}
                    	&nbsp;<span class="glyphicon glyphicon-star" rel="tooltip-right" title="Preferred"></span>
					{{/if_eq}}
				{{else}}
                	{{#if_eq selectedTermAcceptability "Acceptable"}}
                    	&nbsp;<span rel="tooltip-right" title="Acceptable">&#10004;</span></span>
          			{{else}}
                    	&nbsp;&nbsp;&nbsp;
					{{/if_eq}}
            	{{/if_eq}}
               	&nbsp;&nbsp;&nbsp;{{selectedTerm}}
			</td>

			<td>{{selectedTermId}}</td>

			<td>
            	{{#if_eq selectedTermAcceptability "Preferred"}}
                	<span class='i18n' data-i18n-id='i18n_preferred'>Preferred</span>
				{{else}}
                	{{#if_eq selectedTermAcceptability "Acceptable"}}
                    	<span class='i18n' data-i18n-id='i18n_acceptable'>Acceptable</span>
             		{{else}}
                    	<span class='i18n' data-i18n-id='i18n_not_acceptable'>Not acceptable</span>
         			{{/if_eq}}
				{{/if_eq}}
			</td>
		</tr>
	{{/each}}
	</script>
		
	<script id="rp_canvas_descriptions_panel_table_top_Tmpl" type="text/x-jsrender">
    <table class='table table-bordered' >
	    <tr>
	        <td>
	            <h4 style="background-color: rgb(150, 190, 189);>
					<span class="badge alert-warning" style="background-color: rgb(150, 190, 189);">&equiv;</span>&nbsp;&nbsp;
	                <span style="background-color: rgb(150, 190, 189);">{{conceptFsn}}</span>
	            </h4>
	            <br/>SCTID: {{conceptId}}
	        </td>
	    </tr>
	</table>
	</script>
</head>

<body onLoad="snomedInit();">
<input type="hidden" id="release_version" value="${sctVersion}"/>
	  <!-- Modal -->
	  <div class="modal fade" id="snomedModal" role="dialog">
	    <div class="modal-dialog modal-lg">
	    
	      <!-- Modal content-->
	      <div class="modal-content">
	        <div class="modal-header" style="padding:15px 20px;background-color: rgb(150, 190, 189);">
	          <button type="button" class="close" data-dismiss="modal" onclick="cleanFunction()">&times;</button>
	          <h4 style="background-color: rgb(150, 190, 189);"> CIHI SNOMED Query</h4>
	        </div>
	        <div class="modal-body">
	     		<!-- remote content will be inserted here via jQuery load() -->
				<div class="row splitter_panel" id="spliter-panel" style="width: 100%; height: 500px;">
					<div class="a panel panel-default left_panel" id="left_pane"
						style="border-radius: 0px; width: 400px;">
			
						<!--  marker -->
						<div class="col-md-12" id="lp_canvas" style="margin: 0px; padding: 0px;">
							<div class="panel panel-default" style="margin: 5px; height: 100%; overflow: auto;">
			
								<div class="panel-body" id="lp_canvas_panelBody">
									<div class="tab-content" id="lp_tab_content_canvas">
			
										<div class="tab-pane fade active in" id="lp_details_canvas">
											<div class="panel panel-default" id="lp_canvas_search_panel">
			
												<div
													style="padding: 5px; width: 90%; float: right; display: inline;">
													<form>
														<div class="form-group" style="margin-bottom: 2px;">																												                                                 																																															
										  				  	<label for="lp_canvas_search_panel_description"><strong><span class="i18n">SNOMED CT Search</span></strong></label>
														    <br/>
                                                            <span id="lp_canvas_search_panel_description" style="font-size:11px">Please enter SCT Concept ID , SCT Description ID or SCT Description Text for search</span><br/> 
                                                            <br/>
                                                            <label for="concept_type"><span
																class="i18n" data-i18n-id="i18n_release_selected">Hierarchy</span>
															</label><br/>	
															<select id="concept_type" class="form-control" style="width:260px;">
															    <option value="">All</option>
																<c:forEach items="${conceptTypeList}" var="conceptType"  varStatus="loop">
														        	<option value="${conceptType.conceptTypeCode}">${conceptType.conceptTypePrefDesc}</option>
														    	</c:forEach>
															</select>
															<br/><br/>			
                                                            <label for="lp_canvas_search_panel_searchBox"><span class="i18n" data-i18n-id="i18n_type_3_chars">Type at least 3 characters</span> 
                                                                <i class="glyphicon glyphicon-remove text-danger" id="lp_canvas_search_panel_typeIcon"></i> 
                                                            </label> 
                                                            <br/>                                                       
                                                            <div class="input-w">														
																<span class="i18n">SCT Search:&nbsp;</span><input id="lp_canvas_search_panel_searchBox" class="form-control-sctinput" 
																	        	type="search" placeholder="Search..." onkeypress="return event.keyCode != 13;"></input>	
																			<span class="searchclear" id="lp_canvas_search_clearButton"></span>
															</div>
												            <br/>
														</div>
													</form>
													<fieldset>
	                                                	<legend>Search Result</legend>	     
	                                                	<div class="panel panel-default"
															id="lp_canvas_search_panel_resultsScrollPane"
															style="height: 70%; overflow: auto; margin-bottom: 15px; min-height: 300px;">														
															<table class="table table-bordered">
																<tbody id="lp_canvas_search_panel_resultsTable">
																
																</tbody>
															</table>
														</div>
													</fieldset>
							 					</div>
			
											</div>
										</div>
			
									</div>
								</div>
							</div>
						</div>
						<!--  marker -->
					</div>
			
					<div class="a panel panel-default right_panel" id="right_panel"
						style="border-radius: 0px; width: 470px;">
			
						<!--  marker -->
						<div class="col-md-12" id="rp_canvas" style="margin: 0px; padding: 0px;">
							<div class="panel panel-default" style="margin: 5px; height: 100%; overflow: auto;">
								<div class="panel-heading" id="rp_canvas_panelHeading">
			
									<div class="row">
										<div class="col-md-8" id="rp_canvas_panelTitle">
											&nbsp;&nbsp;&nbsp;<strong><span class="i18n"
												data-i18n-id="i18n_concept_details">SCT Concept Details</span></strong>
										</div>
									</div>
								</div>
								<div class="panel-body" id="rp_canvas_panelBody">
									<span>
										<button style="float: right; display: inline; background-color: rgb(150, 190, 189); border-color: rgb(150, 190, 189)" id="button_done" type="button" onclick="doneFunction()">Done</button>
									</span>
									<p>&nbsp;</p>
									<!-- Nav tabs -->
									<!-- Tab panes -->
									<div class="tab-content" id="details-tab-content-fh-cd1_canvas">
			
										<div class="tab-pane fade active in" id="rp_canvas_details">
											<div id="rp_canvas_descriptions_panel_table_top"></div>
										
											<div class="panel panel-default" id="rp_canvas_descriptions_panel">
												<form action="" method="post" id="frmDesc" name="frmDesc">
			
													<table class="table table-bordered" id="rp_canvas_descriptions_panel_table">
														<thead>
															<tr>
																<th class="text-center" colspan="4">USA English
																	language reference set</th>
															</tr>
															<tr>
																<th></th>
																<th><span class="i18n" data-i18n-id="i18n_term">Term</span></th>
			
																<th>SCTID</th>
			
																<th><span class="i18n" data-i18n-id="i18n_acceptability">Acceptability</span></th>
															</tr>
														</thead>
														<tbody id="rp_canvas_descriptions_panel_table_body">
			
														</tbody>
													</table>
															
												</form>
											</div>
										</div>
			
									</div>
								</div>
							</div>
			
						</div>
			
			
						<!--  marker -->
					</div>
			
				</div>


	        </div>
	        <div class="modal-footer">
	          <button type="submit" class="btn btn-danger btn-default pull-left" data-dismiss="modal" onclick="cleanFunction()"><span class="glyphicon glyphicon-remove"></span> Cancel</button>
	        </div>
	      </div>
	      
	    </div>
	  </div> 
</body>
</html>