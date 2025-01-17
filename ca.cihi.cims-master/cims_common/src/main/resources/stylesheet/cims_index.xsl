<?xml version="1.0" encoding="iso-8859-1" ?>

<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ora="http://www.oracle.com/XSL/Transform/java"
	version="1.0" >		

<xsl:output 
	encoding="utf-8"
	method="xml" />
	
<xsl:include href="cims_common.xsl"/> 

<xsl:strip-space elements="*" />

<xsl:variable name="language">
	<xsl:value-of select="normalize-space(index/@language)" />
</xsl:variable>

<xsl:variable name="book_index_type">
	<xsl:value-of select="normalize-space(index/BOOK_INDEX_TYPE)" />
</xsl:variable>

<xsl:variable name="element_id">
	<xsl:value-of select="normalize-space(index/ELEMENT_ID)" />
</xsl:variable>

<xsl:variable name="index_type">
	<xsl:value-of select="normalize-space(index/INDEX_TYPE)" />
</xsl:variable>

<xsl:variable name="level_num">
	<xsl:value-of select="normalize-space(index/LEVEL_NUM)" />
</xsl:variable>

<xsl:variable name="index_term_desc">
	<xsl:value-of select="normalize-space(index/INDEX_TERM_DESC)" />
</xsl:variable>

<xsl:variable name="see_also_flag">
	<xsl:value-of select="normalize-space(index/SEE_ALSO_FLAG)" />
</xsl:variable>

<xsl:variable name="site_indicator">
	<xsl:value-of select="normalize-space(index/SITE_INDICATOR)" />
</xsl:variable>

<!-- *** Global Variables *** -->
<xsl:variable name="check-param">
  <xsl:choose>
   <xsl:when test="$language = ''">
     <xsl:message terminate="yes">ERROR 001: "$language" UNDEFINED</xsl:message>
   </xsl:when>
   <xsl:when test="$language != 'ENG' and $language != 'FRA'">
     <xsl:message terminate="yes">ERROR 002: "$language" must be "ENG" or "FRA"</xsl:message>
   </xsl:when>   
   <xsl:when test="$book_index_type = ''">
     <xsl:message terminate="yes">ERROR 001: "$book_index_type" UNDEFINED</xsl:message>
   </xsl:when> 
   <xsl:when test="$element_id = ''">
     <xsl:message terminate="yes">ERROR 001: "$element_id" UNDEFINED</xsl:message>
   </xsl:when>  
   <xsl:when test="$index_type = ''">
     <xsl:message terminate="yes">ERROR 001: "$index_type" UNDEFINED</xsl:message>
   </xsl:when>
  </xsl:choose>
</xsl:variable>

<xsl:template match="/">
	<xsl:apply-templates/>
</xsl:template>

  
<xsl:template match=" BOOK_INDEX_TYPE | ELEMENT_ID | INDEX_TYPE | LEVEL_NUM | INDEX_TERM_DESC | SEE_ALSO_FLAG | SITE_INDICATOR |
					  REFERENCE_LINK_DESC| CONTAINER_INDEX_ID |
					  MAIN_CODE_PRESENTATION | MAIN_CONTAINER_CONCEPT_ID | PAIRED_FLAG | SORT_STRING | PAIRED_CODE_PRESENTATION | PAIRED_CONTAINER_CONCEPT_ID |  
					  TF_CONTAINER_CONCEPT_ID | CODE_PRESENTATION" />


<xsl:template match="REFERENCE_LIST">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="index">
	<xsl:choose>
		<xsl:when test="$index_type='BOOK_INDEX'"> 
			<xsl:choose>
				<xsl:when test="$book_index_type='D' or $book_index_type='N'">
					<tr><td height="20px" colspan="4"></td></tr>
					<tr>
						<td class='chp' colspan="3"><a><xsl:attribute name="name"><xsl:value-of select="$element_id"/></xsl:attribute>
									<xsl:value-of disable-output-escaping="yes" select="$index_term_desc"/>
									</a>
						</td>
						<td/>
					</tr>
					<tr><td height="10px" colspan="4"></td></tr>
					<br/>
				</xsl:when>
				<xsl:otherwise>
					<div class="chp">
						<a><xsl:attribute name="name"><xsl:value-of select="$element_id"/></xsl:attribute>
									<xsl:value-of disable-output-escaping="yes" select="$index_term_desc"/>
									</a>
					</div>
				</xsl:otherwise>
			</xsl:choose>
 	 		<xsl:call-template name="DISPLAY_INDEX_TERM_NOTE"/> 	 		
		</xsl:when>
		<xsl:when test="$index_type='LETTER_INDEX'"> 
		   <xsl:choose>
			   <xsl:when test="$book_index_type='D'">
						<tr>
				   			<td height="10px" colspan="4"></td>
				   		</tr>
				   		<tr>
							<td class='bl1' colspan="3" ><a><xsl:attribute name="name"><xsl:value-of select="$element_id"/></xsl:attribute>
										<xsl:value-of disable-output-escaping="yes" select="$index_term_desc"/>
										</a>
							</td>
							<td/>
						</tr>
						<tr><td height="5px" colspan="4"></td></tr> 
					    <tr><td colspan="3">
					         <xsl:choose>
					         	<xsl:when test="$language='ENG'">
					        	      <div id="sticker">
									      <table width="90%" align="center" border="0" cellspacing="0" cellpadding="0" id="tdFixedHeader">
									         <tr>
									            <th width="45%" class="th_drugs_top"></th>
									            <th colspan="4" align="center" class="th_drugs_top">Poisoning</th>
									            <th width="11%" rowspan="2" align="center" class="th_drugs_top">Adverse Effect in Therapeutic Use</th>
									         </tr>
									         <tr>
									            <th width="45%" class="th_drugs_bottom"></th>
									            <th width="11%" align="center" class="th_drugs_bottom">Chapter XIX</th>
									            <th width="11%" align="center" class="th_drugs_bottom">Accidental</th>
									            <th width="11%" align="center" class="th_drugs_bottom">Intentional Self-Harm</th>
									            <th width="11%" align="center" class="th_drugs_bottom">Undetermined Intent</th>
									         </tr>
									      </table>
									 </div>
					         	</xsl:when>
					         	<xsl:when test="$language='FRA'">
					         	     <div id="sticker">
								         	<table width="90%" align="center" border="0" cellspacing="0" cellpadding="0">
											  <tr>
											    <th width="45%"  class="th_drugs_top"/>
											    <th colspan="4" align="center"  class="th_drugs_top">Empoisonnement</th>
											    <th width="11%" rowspan="2"  class="th_drugs_top">Effet<br />
											      indésirable<br />
											      en usage<br />
											    thérapeutique</th>
											  </tr>
											   <tr>
											    <th width="45%" align="left" class="th_drugs_bottom"></th>
											    <th width="11%" align="center" class="th_drugs_bottom">Chapitre XIX</th>
											    <th width="11%" align="center" class="th_drugs_bottom">Accidentel</th>
											    <th width="11%" align="center" class="th_drugs_bottom">Intentionnel<br />
											      auto-induit</th>
											    <th width="11%" align="center" class="th_drugs_bottom">Intention<br />
											      non-déterminée</th>
											  </tr>								
										</table>
									</div>				         	
					         	</xsl:when>
					         </xsl:choose>
						   </td>
						   <td/>
						   </tr> 
					</xsl:when>
					<xsl:otherwise>
						<div class='bl1'><a><xsl:attribute name="name"><xsl:value-of select="$element_id"/></xsl:attribute>
										<xsl:value-of disable-output-escaping="yes" select="$index_term_desc"/>
										</a>
						</div>
					</xsl:otherwise>
				</xsl:choose>
		</xsl:when>
		<xsl:otherwise>
			<xsl:choose>
				<xsl:when test="$book_index_type='N'">
				      <xsl:choose>
				        <xsl:when test="$level_num = 1">
							<tr>
					   			<td height="10px" colspan="4"></td>
					   		</tr>
					   		<tr>
								<td class='bl1' colspan="3" ><a><xsl:attribute name="name"><xsl:value-of select="$element_id"/></xsl:attribute>
											<xsl:value-of disable-output-escaping="yes" select="$index_term_desc"/></a>
								</td>
								<td/>
							</tr>
							<tr><td height="5px" colspan="4"></td></tr> 
						 	<xsl:call-template name="DISPLAY_INDEX_TERM_NOTE"/>
						
						   <tr><td colspan="3">
						        <xsl:choose>
						        	<xsl:when test="$language='ENG'">
						        	   <div id="sticker">
							        		<table width="90%" align="center" border="0" cellspacing="0" cellpadding="0">
												  <tr>
												    <th width="45%" rowspan="2"  class="th_drugs_top"/>
												    <th colspan="2"  class="th_drugs_top" align="center">Malignant</th>
												    <th width="11%" rowspan="2"  class="th_drugs_top" align="center">In situ</th>
												    <th width="11%" rowspan="2"  class="th_drugs_top" align="center">Benign</th>
												    <th width="11%"  class="th_drugs_top" rowspan="2" align="center">Uncertain or Unknown Behaviour</th>
												  </tr>
												  <tr>
												    <th width="11%" class="th_drugs_bottom" align="center">Primary</th>
												    <th width="11%" class="th_drugs_bottom" align="center">Secondary</th>
												  </tr>
											</table>
										</div>
						        	</xsl:when>				        	
						        	<xsl:when test="$language='FRA'">
						        	  <div id="sticker">
							        	   <table width="90%" align="center" border="0" cellspacing="0" cellpadding="0">
											  <tr>
											    <th width="45%" rowspan="2"  class="th_drugs_top"/>
											    <th colspan="2"  class="th_drugs_top" align="center">Malignes</th>
											    <th width="11%" rowspan="2"  class="th_drugs_top" align="center">In situ</th>
											    <th width="11%" rowspan="2"  class="th_drugs_top" align="center">Bénignes</th>
											    <th width="11%"  class="th_drugs_top" rowspan="2" align="center">À<br />
											      évolution<br />
											      imprévisible<br />
											    ou inconnue</th>
											  </tr>
											  <tr>
											    <th width="11%" class="th_drugs_bottom" align="center">Primitives</th>
											    <th width="11%" class="th_drugs_bottom" align="center">Secondaires</th>
											  </tr>
											</table>
										</div>
						        	</xsl:when>
						        </xsl:choose>
							</td>
							<td/>
							</tr>
							<tr><td colspan="3">
									<table width="90%" align="center" border="0" cellspacing="0" cellpadding="0">
									     <tr>		
										     <td width="45%" class="codeText">
												<xsl:call-template  name="printDash">
											   		<xsl:with-param name="levelNum" select="$level_num"/>
											    </xsl:call-template>									            
									            <b>
									              <a><xsl:attribute name="name"><xsl:value-of select="$element_id"/></xsl:attribute>	
											      <xsl:value-of disable-output-escaping="yes" select="$index_term_desc"/></a>
											      <xsl:text> </xsl:text>
											      <xsl:choose>
												      <xsl:when test="$site_indicator='$'"> 
												      	<xsl:text>&amp;diams;</xsl:text>
												      </xsl:when>
												      <xsl:otherwise>
												      	<xsl:value-of select="$site_indicator"/>
												      </xsl:otherwise>
											      </xsl:choose>									      
											      <xsl:call-template name="REFERENCE_LIST"/>
											    </b>
											</td>
								    		<xsl:call-template name="displayNeoplasmDetail"/>
							    		</tr>
							    	  </table>
							</td>
							<td/>
							</tr>
						</xsl:when>
						<xsl:otherwise>
							<tr><td colspan="3">
								<table width="90%" align="center" border="0" cellspacing="0" cellpadding="0">
								     <tr>		
									     <td width="45%" class="codeText">
											<xsl:call-template  name="printDash">
										   		<xsl:with-param name="levelNum" select="$level_num"/>
										    </xsl:call-template>
								            <a><xsl:attribute name="name"><xsl:value-of select="$element_id"/></xsl:attribute> 			            
								                  <xsl:value-of disable-output-escaping="yes" select="$index_term_desc"/></a>
												 	 <xsl:text> </xsl:text>
												 	<xsl:choose>
													      <xsl:when test="$site_indicator='$'"> 
													      	<xsl:text>&amp;diams;</xsl:text>
													      </xsl:when>
													      <xsl:otherwise>
													      	<xsl:value-of select="$site_indicator"/>
													      </xsl:otherwise>
												    </xsl:choose>	
												 	<xsl:call-template name="REFERENCE_LIST"/>												
										</td>
							    		<xsl:call-template name="displayNeoplasmDetail"/>
						    		 </tr>						    		
						    	</table>
							</td>
							<td/>
							</tr>
							<xsl:call-template name="DISPLAY_INDEX_TERM_NOTE"/>
						</xsl:otherwise>	
					</xsl:choose>	   
				</xsl:when>				
				<xsl:otherwise>
				   <xsl:choose>
						<xsl:when test="$level_num  &gt; 0 and $level_num  &lt; 20">
								<xsl:choose>
									<xsl:when test="$book_index_type='D'">	
									    <tr><td colspan="3">
									          <table width="90%" align="center" border="0" cellspacing="0" cellpadding="0">
										        <tr>							           		            
										            <xsl:choose>
											            <xsl:when test="$level_num = '1'">				            
												            <td width="45%" class="codeText bold">
												             <a><xsl:attribute name="name"><xsl:value-of select="$element_id"/></xsl:attribute>	
														      <xsl:value-of disable-output-escaping="yes" select="$index_term_desc"/></a> <xsl:text> </xsl:text>
														      <xsl:choose>
															      <xsl:when test="$site_indicator='$'"> 
															      	<xsl:text>&amp;diams;</xsl:text>
															      </xsl:when>
															      <xsl:otherwise>
															      	<xsl:value-of select="$site_indicator"/>
															      </xsl:otherwise>
														      </xsl:choose>
														      <xsl:call-template name="REFERENCE_LIST"/>
														    </td>
														 </xsl:when>
														 <xsl:otherwise>
														 	<td width="45%" class="codeText">
																<xsl:call-template  name="printDash">
															   		<xsl:with-param name="levelNum" select="$level_num"/>
															    </xsl:call-template>		
															   <a><xsl:attribute name="name"><xsl:value-of select="$element_id"/></xsl:attribute>	
															 	<xsl:value-of disable-output-escaping="yes" select="$index_term_desc"/></a> <xsl:text> </xsl:text>
															 	<xsl:choose>
															      <xsl:when test="$site_indicator='$'"> 
															      	<xsl:text>&amp;diams;</xsl:text>
															      </xsl:when>
															      <xsl:otherwise>
															      	<xsl:value-of select="$site_indicator"/>
															      </xsl:otherwise>
														        </xsl:choose>
															 	<xsl:call-template name="REFERENCE_LIST"/>
														 	</td>
														 </xsl:otherwise>
												    </xsl:choose>
									    		    <xsl:call-template name="displayDrugDetail"/>
								    			</tr>
							    			</table>
							    		   </td>
							    		   <td/>
							    		</tr>
									</xsl:when>
									<xsl:otherwise>							            			            
							            <xsl:choose>
								            <xsl:when test="$level_num = '1'">
								              <div class="codeText bold">
										            <a><xsl:attribute name="name"><xsl:value-of select="$element_id"/></xsl:attribute>
												      <xsl:value-of disable-output-escaping="yes" select="$index_term_desc"/></a> <xsl:text> </xsl:text>
												      <xsl:choose>
														      <xsl:when test="$site_indicator='$'"> 
														      	<xsl:text>&amp;diams;</xsl:text>
														      </xsl:when>
														      <xsl:otherwise>
														      	<xsl:value-of select="$site_indicator"/>
														      </xsl:otherwise>
													  </xsl:choose>
												      <xsl:call-template name="REFERENCE_LIST"/>
											    </div>
											 </xsl:when>
											 <xsl:otherwise>
											     <div class="codeText">
													<xsl:call-template  name="printDash">
												   		<xsl:with-param name="levelNum" select="$level_num"/>
												    </xsl:call-template>
											 		<a><xsl:attribute name="name"><xsl:value-of select="$element_id"/></xsl:attribute>
											 			<xsl:value-of disable-output-escaping="yes" select="$index_term_desc"/></a> <xsl:text> </xsl:text>
												 		<xsl:choose>
													      <xsl:when test="$site_indicator='$'"> 
													      	<xsl:text>&amp;diams;</xsl:text>
													      </xsl:when>
													      <xsl:otherwise>
													      	<xsl:value-of select="$site_indicator"/>
													      </xsl:otherwise>
												      </xsl:choose>
											 		<xsl:call-template name="REFERENCE_LIST"/>
											 	</div>
											 </xsl:otherwise>											 
									    </xsl:choose> 
									</xsl:otherwise>
								</xsl:choose>							 
						    <xsl:call-template name="DISPLAY_INDEX_TERM_NOTE"/>
					    </xsl:when>	
					    <xsl:otherwise>
					      <xsl:text>The data is incorrect. Please check the data!!</xsl:text>
					    </xsl:otherwise>  
				    </xsl:choose>
				</xsl:otherwise> 
		    </xsl:choose>
	    </xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="displayDrugDetail">
	<xsl:for-each select="DRUGS_DETAIL/TABULAR_REF">
    	<xsl:variable name="containerConceptId">
    		<xsl:value-of select="TF_CONTAINER_CONCEPT_ID"></xsl:value-of>
    	</xsl:variable>    	
    	<td align="center" width="11%" class="codeText">
	    	<xsl:if test="$containerConceptId != ''">    	  
	    	   <a href="javascript:navigateFromDynaTree('{$containerConceptId}');">
	     	      	<xsl:value-of select="CODE_PRESENTATION"></xsl:value-of>
	    	   </a>
	    	</xsl:if> 
    	</td>
	</xsl:for-each>
</xsl:template>

<xsl:template name="displayNeoplasmDetail">
	<xsl:for-each select="NEOPLASM_DETAIL/TABULAR_REF">
    	<xsl:variable name="containerConceptId">
    		<xsl:value-of select="TF_CONTAINER_CONCEPT_ID"></xsl:value-of>
    	</xsl:variable>
    	
    	<td align="center"  width="11%" class="codeText">
	    	<xsl:if test="$containerConceptId != ''">    	  
	    	   <a href="javascript:navigateFromDynaTree('{$containerConceptId}');">
	     	      	<xsl:value-of select="CODE_PRESENTATION"></xsl:value-of>
	    	   </a>
	    	</xsl:if> 
    	</td>
	</xsl:for-each>
</xsl:template>

    <!-- print the dash before the index terms at levels 2 to 20 -->
	<xsl:template name="printDash">
	  <xsl:param name="levelNum" />
	  <xsl:if test="$levelNum &gt; 1">
	    <xsl:variable name="tmpind" select="$levelNum"/>
	    <xsl:text>- </xsl:text>
	    <xsl:call-template name="printDash">
	      <xsl:with-param name="levelNum" select="$tmpind - 1" />
	    </xsl:call-template>
	  </xsl:if>
	</xsl:template>
	
	 <!-- print space before Note -->
	<xsl:template name="printSpace">
	  <xsl:param name="levelNum" />
	  <xsl:if test="$levelNum &gt; 0">
	    <xsl:variable name="tmpind" select="$levelNum"/>
	    <xsl:text>&amp;#160; </xsl:text>
	    <xsl:call-template name="printSpace">
	      <xsl:with-param name="levelNum" select="$tmpind - 1" />
	    </xsl:call-template>
	  </xsl:if>
	</xsl:template>

  <xsl:variable name="flag">
    <xsl:choose>
    <xsl:when test="$language = 'ENG'">
      <xsl:choose>
      <xsl:when test="$see_also_flag = 'A'">
       <xsl:text>see also</xsl:text>
      </xsl:when>
      <xsl:when test="$see_also_flag = 'S'">
       <xsl:text>see</xsl:text>
      </xsl:when>
	  <xsl:when test="$see_also_flag = 'Y'">
       <xsl:text>see also</xsl:text>
      </xsl:when>
      <xsl:when test="$see_also_flag = 'N'">
       <xsl:text>see</xsl:text>
      </xsl:when>
      <xsl:when test="$see_also_flag = 'X'">
       <xsl:text></xsl:text>
      </xsl:when>
      <xsl:otherwise>
       <xsl:text>see</xsl:text>
      </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:when test="$language = 'FRA'">
      <xsl:choose>
      <xsl:when test="$see_also_flag = 'A'">
       <xsl:text>voir aussi</xsl:text>
      </xsl:when>
      <xsl:when test="$see_also_flag = 'S'">
       <xsl:text>voir</xsl:text>
      </xsl:when>
	  <xsl:when test="$see_also_flag = 'Y'">
       <xsl:text>voir aussi</xsl:text>
      </xsl:when>
      <xsl:when test="$see_also_flag = 'N'">
       <xsl:text>voir</xsl:text>
      </xsl:when>
      <xsl:when test="$see_also_flag = 'X'">
       <xsl:text></xsl:text>
      </xsl:when>
      <xsl:otherwise>
       <xsl:text>voir</xsl:text>
      </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
     <xsl:text>see</xsl:text>
    </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

<xsl:template name="REFERENCE_LIST">
	<xsl:apply-templates select="REFERENCE_LIST/INDEX_REF_LIST"/>
	<xsl:apply-templates select="REFERENCE_LIST/CATEGORY_REFERENCE_LIST"/>
</xsl:template>

<xsl:template match="CATEGORY_REFERENCE_LIST">  
  <!--    - morphology codes wrapped in brackets
          
          - detecting DAGGER_ASTERISK="Y" to output dagger symbol
            (suppress &amp;#134; dagger if typed in as part of code in database,
             should be going by DAGGER_ASTERISK field only)
          
          - multiple CATEGORY_REFERENCE_DESC should be sorted in this code order:
            [morphology | dagger | regular | asterisk]
          
          -->	  
	<xsl:apply-templates select="CATEGORY_REFERENCE"><xsl:sort select="MAIN_CODE" lang="en"/></xsl:apply-templates>	
</xsl:template>

<xsl:template match="INDEX_REF_LIST">
     <xsl:choose>
        <xsl:when test="$see_also_flag != 'X'">
     	    <xsl:text> (</xsl:text>
		    <xsl:value-of select="$flag"/>
		    <xsl:text> </xsl:text>
		    <xsl:apply-templates select="INDEX_REF"><xsl:sort select="REFERENCE_LINK_DESC" lang="en"/></xsl:apply-templates>
		    <xsl:text>) </xsl:text>
         </xsl:when>
	     <xsl:otherwise>
		 	<xsl:apply-templates select="INDEX_REF"><xsl:sort select="REFERENCE_LINK_DESC" lang="en"/></xsl:apply-templates>
		 </xsl:otherwise>
	 </xsl:choose>
</xsl:template>

<xsl:template match="CATEGORY_REFERENCE">

       <xsl:variable name="catCCId"><xsl:value-of select="MAIN_CONTAINER_CONCEPT_ID"/></xsl:variable>
       <xsl:variable name="catPCId"><xsl:value-of select="PAIRED_CONTAINER_CONCEPT_ID"/></xsl:variable>    
    
       <xsl:choose> 
	    	<xsl:when test="PAIRED_FLAG='Y'">
	    		<xsl:text>(</xsl:text>
	    		 	<a href="javascript:navigateFromDynaTree('{$catCCId}');">
	    		        <xsl:value-of select="MAIN_CODE_PRESENTATION"/>
	    		        <xsl:choose>
	    		            <xsl:when test="MAIN_DAGGER_ASTERISK='+'">
	    		         	     <xsl:text>&amp;#134;</xsl:text>
	    		         	</xsl:when>
	    		         	<xsl:otherwise>
	    		         	 <xsl:value-of select="MAIN_DAGGER_ASTERISK"/>
	    		         	</xsl:otherwise>
	    		         </xsl:choose>
	    		    </a>	
	    		<xsl:text>/</xsl:text>
	    		    <a href="javascript:navigateFromDynaTree('{$catPCId}');">
	    		        <xsl:value-of select="PAIRED_CODE_PRESENTATION"/><xsl:value-of select ="PAIRED_DAGGER_ASTERISK"/>
	    		    </a>	    		
	    		<xsl:text>)</xsl:text>
	    	</xsl:when>
	    	<xsl:otherwise>
	    	    <a href="javascript:navigateFromDynaTree('{$catCCId}');">	    	    
	    			<xsl:value-of select="MAIN_CODE_PRESENTATION"/>	    			
	    			<xsl:choose>
	    		            <xsl:when test="MAIN_DAGGER_ASTERISK='+'">
	    		         	     <xsl:text>&amp;#134;</xsl:text>
	    		         	</xsl:when>
	    		         	<xsl:otherwise>
	    		         	 <xsl:value-of select="MAIN_DAGGER_ASTERISK"/>
	    		         	</xsl:otherwise>
	    		    </xsl:choose>
	    		</a>
	    	</xsl:otherwise>
    	</xsl:choose>
 	<xsl:text> </xsl:text>
     
</xsl:template>

<xsl:template match="INDEX_REF">      
   	<xsl:choose>
   		<xsl:when test="position() = 1">   	
   			<xsl:text> </xsl:text>		
   		</xsl:when>
   		<xsl:otherwise>
   			<xsl:text>, </xsl:text>
   		</xsl:otherwise>
   	</xsl:choose>
    <xsl:variable name="containerIndexId"><xsl:value-of select="CONTAINER_INDEX_ID" /></xsl:variable>
    
   	<xsl:choose>
		<xsl:when test="CONTAINER_INDEX_ID = ''">			
			<xsl:value-of select="REFERENCE_LINK_DESC"/>				
		</xsl:when>
		<xsl:otherwise>		    
			<a href="javascript:navigateFromDynaTree('{$containerIndexId}');">
     	      	<xsl:value-of select="REFERENCE_LINK_DESC"></xsl:value-of>
     	    </a>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>


<!-- ************************************************** -->
<!-- Display Index notes -->

<xsl:template name="DISPLAY_INDEX_TERM_NOTE">
  <xsl:apply-templates select="NOTE_DESC/qualifierlist[@type = 'note']"/>
  <xsl:apply-templates select="NOTE_DESC/index-section"/>
</xsl:template>

<!-- ************************************************** -->
  <xsl:template match="note/label|Note/label">
    <xsl:apply-templates/>
  </xsl:template>


<!-- *** (copied the whole "qualifierlist" section from "icd_display.xsl", 
          but could stand to be vastly simplified both here and there) *** -->

  <xsl:template match="NOTE_DESC/qualifierlist[@type='note'] ">
    <xsl:variable name="caption">
      <xsl:if test="not(note/label/phrase[@format='title'])" >
	      <xsl:choose>
		      <xsl:when test="$language = 'FRA'">
		       <!-- <xsl:text>Remarques:</xsl:text> -->
		       <xsl:text>Note:</xsl:text>
		      </xsl:when>
		      <xsl:otherwise>
		       <xsl:text>Note:</xsl:text>
		      </xsl:otherwise>
	      </xsl:choose>
      </xsl:if>
    </xsl:variable>
    <xsl:choose>
	    <xsl:when test="$book_index_type='D' or $book_index_type='N'">	
		    <tr> 
		      <td colspan="3">
			    <table width="90%" align="center">
				    <tr>		    
					    <xsl:choose>
						    <xsl:when test="not(note/label/phrase[@format='title'])" >	
						        <td valign="top" width="10%" class="codeText"><xsl:value-of select="$caption"/></td>	    
						    	<td valign="top" class="codeText"><xsl:apply-templates/></td>
						     </xsl:when>	
						    <xsl:otherwise>
					        	<td valign="top"><xsl:apply-templates/></td>
					        </xsl:otherwise>
				        </xsl:choose>
				    </tr>
			     </table>
		     	</td>
		     	<td/>
		     </tr>
		     <tr><td colspan="4" height="5px"></td></tr>   
	     </xsl:when>
	     <xsl:otherwise>
	        <xsl:choose>
	            <xsl:when test="not(note/label/phrase[@format='title'])" >
			        <div class="codeText" style="width:80%;">	     	
			     		<div style="width:10%;float:left;">	     		    
			     			<xsl:call-template  name="printSpace">
						   		<xsl:with-param name="levelNum" select="$level_num"/>
						    </xsl:call-template>
							<xsl:value-of select="$caption"/><xsl:text>     </xsl:text>
						</div>
						<div style="display:block;float:right;width:90%;">					      	
			     			<xsl:apply-templates/>	     			 
						</div>
			        </div>
			        <div style="clear:both;"/>
		        </xsl:when>
		        <xsl:otherwise>
		        	<xsl:apply-templates/>
		        </xsl:otherwise>
	        </xsl:choose>
	     </xsl:otherwise>
     </xsl:choose>
  </xsl:template>



  <xsl:template match="qualifierlist/note">    
    <xsl:choose>  
    <xsl:when test="child::ulist">
      <xsl:apply-templates/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates/><br/>
    </xsl:otherwise>
    </xsl:choose>
  
  </xsl:template>
  
 <xsl:template match="NOTE_DESC/index-section">
    <xsl:apply-templates select="./label"/> 
 	<tr>
 		<td colspan="4" class="codeText"> 
 			<xsl:apply-templates select="./table"/>
 		</td>
 	</tr>
 </xsl:template>
 
 <xsl:template match="index-section/label">
 	<tr class="cat1">
    	<td colspan="3"  class="codeText">
    		<xsl:value-of select="."/>
    	</td>
    	<td/>
    </tr>
 </xsl:template>
 
 <!-- ************************************************** -->

<!-- ************************************************** -->
<!-- (also adding generic ulist support) -->

<xsl:template match="ulist">
  <ul>
  <xsl:apply-templates/>
  </ul>
</xsl:template>

<xsl:template match="listitem">
  <li>
  <xsl:apply-templates/>
  </li>
</xsl:template>

<xsl:include href="cims_table.xsl"/>

</xsl:stylesheet>


 
  