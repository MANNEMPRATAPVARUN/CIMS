<?xml version="1.0" encoding="iso-8859-1" ?>

<!-- XSL style sheet for ICD-10-CA tabular data XML files -->

<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0" >

<xsl:output 
	encoding="utf-8"
	method="xml" />
	
<xsl:include href="cims_common.xsl"/> 	   
   

<xsl:strip-space elements="*" />

<xsl:variable name="language">
	<xsl:value-of select="normalize-space(concept/language)" />
</xsl:variable>

<xsl:variable name="clinical_classification_code">
	<xsl:value-of select="normalize-space(concept/classification)" />
</xsl:variable>

<!-- Set the Title variables for the current page -->
<xsl:variable name="code">
  <xsl:value-of select="normalize-space(concept/CODE)"/>
</xsl:variable>

<xsl:variable name="type_code" >
	<xsl:value-of select="normalize-space(concept/TYPE_CODE)"/>
</xsl:variable>

<xsl:variable name="presentation_type_code" >
	<xsl:value-of select="normalize-space(concept/PRESENTATION_TYPE_CODE)"/>
</xsl:variable>

<xsl:variable name="presentation_code" >
	<xsl:value-of select="normalize-space(concept/PRESENTATION_CODE)"/>
</xsl:variable>

<xsl:variable name="caFlag">
	<xsl:value-of select="normalize-space(concept/CA_ENHANCEMENT_FLAG)"/>
</xsl:variable>

<xsl:variable name="nos">
      <xsl:choose>
      <xsl:when test="$language = 'FRA'">
       <xsl:text>SAI</xsl:text>
      </xsl:when>
      <xsl:otherwise>
       <xsl:text>NOS</xsl:text>
      </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

<xsl:template name="CA_ENHANCEMENT_FLAG">
  <xsl:if test="$caFlag = 'true'">  
   <xsl:text> </xsl:text>
   <img align="texttop" src="img/icd/cleaf.gif" height="15" width="15"/>
  </xsl:if>  
</xsl:template>


<xsl:variable name="heading_user_desc">
	<xsl:value-of disable-output-escaping="yes" select="normalize-space(concept/USER_DESC)"/>
	
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
    <xsl:when test="$clinical_classification_code = ''">
     <xsl:message terminate="yes">ERROR 001: "$clinical_classification_code" UNDEFINED</xsl:message>
   </xsl:when> 
   <xsl:when test="$code = ''">
     <xsl:message terminate="yes">ERROR 001: "$code" UNDEFINED</xsl:message>
   </xsl:when>
    <xsl:when test="$heading_user_desc = ''">
     <xsl:message terminate="yes">ERROR 001: "$heading_user_desc" UNDEFINED</xsl:message>
   </xsl:when>   
  </xsl:choose>
</xsl:variable>

 
<xsl:template match="/"> 	 
    <xsl:apply-templates/> 
</xsl:template> 


<!-- ************************************************** -->
<!-- Flow-through -->

<xsl:template match="concept">
	<xsl:choose>
	
	<!--  ICD types -->
    <xsl:when test="$presentation_type_code = 'CHAPTER'">
		<tr><td colspan="4" height="10px"></td></tr>
		<tr>
			<td class='chp' colspan="3" >
			<a>
				<xsl:attribute name="name"><xsl:value-of select="$code"/></xsl:attribute>
				<xsl:choose>
			     <xsl:when test="$language = 'ENG'">
			      <xsl:text>Chapter </xsl:text>
			     </xsl:when>
			     <xsl:when test="$language = 'FRA'">
			      <xsl:text>Chapitre </xsl:text>
			     </xsl:when>
			    </xsl:choose>
				 <xsl:value-of select="$presentation_code"/>
				 <xsl:call-template name="CA_ENHANCEMENT_FLAG"/>
				 <xsl:text> - </xsl:text>
				 <xsl:value-of disable-output-escaping="yes" select="$heading_user_desc"/>        
				</a>
			</td>
			<td/>
		</tr>
		<tr><td colspan="4" height="10px"></td></tr>
		<br/>
   </xsl:when>

   <xsl:when test="$presentation_type_code = 'BLOCK1'">
		<tr><td colspan="4" height="15px"/></tr>
		<tr>
			<td class='bl1' colspan="3" >
				<a><xsl:attribute name="name"><xsl:value-of select="$code"/></xsl:attribute>
				   <xsl:value-of disable-output-escaping="yes" select="$heading_user_desc"/>
				</a>
			</td>
			<td/>
		</tr>
		<tr><td colspan="4" height="5px"></td></tr>
      </xsl:when>

   <xsl:when test="$presentation_type_code = 'BLOCK2'">
	<tr><td colspan="4" height="15px"/></tr>
	<tr>
		<td class='bl2' colspan="3" >
			<a><xsl:attribute name="name"><xsl:value-of select="$code"/></xsl:attribute> 
		 		<xsl:value-of disable-output-escaping="yes" select="$heading_user_desc"/>
		 	</a>
		</td>
		<td/>
	</tr>
	<tr><td colspan="4" height="5px"></td></tr>   
    </xsl:when>
    
    <xsl:when test="$presentation_type_code = 'CHAPTER22_BLOCK2'"> 
        <tr><td colspan="4" height="15px"></td></tr>
		<xsl:if test="$presentation_code!=''"> 
			<tr>
				<td class='cat1' colspan="3">
				      <a>
				      		<xsl:attribute name="name"><xsl:value-of select="$code"/></xsl:attribute>
				     		<xsl:value-of disable-output-escaping="yes" select="$heading_user_desc"/>
				      </a>
				</td>
				<td/>
			</tr>
			<tr><td colspan="4" height="5px"></td></tr> 			
        </xsl:if>            
    </xsl:when>

   <xsl:when test="$presentation_type_code = 'BLOCK3'">
	<tr><td colspan="4" height="15px"/></tr>
	<tr>
		<td class='bl3' colspan="3" > 
			<a>
				<xsl:attribute name="name"><xsl:value-of select="$code"/></xsl:attribute>
				<xsl:value-of disable-output-escaping="yes" select="$heading_user_desc"/>
			</a>
		</td>
		<td/>
	</tr>
	<tr><td colspan="4" height="5px"></td></tr>   
    </xsl:when>

   <xsl:when test="$presentation_type_code = 'CATEGORY1' or ($presentation_type_code = 'CODE' and $type_code='CATEGORY' and string-length($code)=3) ">
		<tr><td colspan="4" height="15px"></td></tr>
		<tr>
		<td class='cat1'><a><xsl:attribute name="name">
		<xsl:value-of select="$code"/></xsl:attribute><xsl:value-of select="CONCEPT_CODE_WITH_DECIMAL_DAGGAR" /></a>
		    <xsl:call-template name="CA_ENHANCEMENT_FLAG"/></td>
		<td class='cat1' colspan="2"><xsl:value-of disable-output-escaping="yes" select="$heading_user_desc"/>		
		<xsl:if test="not($type_code='BLOCK') ">
			<xsl:choose>			  		   
	  			<xsl:when test="HAS_VALIDATION='true'"> 
	  			    	<a href="javascript:popupIcdValidation('icdValidationPopup.htm?refid={$code}&amp;language={$language}&amp;classification={$clinical_classification_code}');">
				   	   		 <img src="img/v purple.png" alt=" V "/>
					    </a>
			    </xsl:when>
			    <xsl:otherwise> 
			 		 <img src="img/v grey.png" alt=" V "/>
			    </xsl:otherwise>
			 </xsl:choose>
	    </xsl:if>
		</td>
		<td/>
		</tr>
		<tr><td colspan="4" height="3px"></td></tr>
    </xsl:when>

   <xsl:when test="$presentation_type_code = 'CATEGORY2'"> 
         <tr><td colspan="4" height="15px"></td></tr>
		<xsl:if test="$presentation_code!=''"> 
			<tr>
				<td class='cat2'><a><xsl:attribute name="name">
				<xsl:value-of select="$code"/></xsl:attribute><xsl:value-of select="CONCEPT_CODE_WITH_DECIMAL_DAGGAR" /></a>
				<xsl:call-template name="CA_ENHANCEMENT_FLAG"/></td>
				<td class='cat2' colspan="2"> <xsl:value-of disable-output-escaping="yes" select="$heading_user_desc"/></td>
				<td/>
			</tr>
        </xsl:if>
        <tr><td colspan="4" height="3px"></td></tr>	    
    </xsl:when>

   <xsl:when test="$presentation_type_code = 'CATEGORY3'">
       <tr><td colspan="4" height="15px"></td></tr>
	    <xsl:if test="$presentation_code!=''">
			<tr>
				<td class='cat3'><a><xsl:attribute name="name">
					<xsl:value-of select="$code"/></xsl:attribute><xsl:value-of select="CONCEPT_CODE_WITH_DECIMAL_DAGGAR" /></a>
					<xsl:call-template name="CA_ENHANCEMENT_FLAG"/></td>
				<td class='cat3' colspan="2"><xsl:value-of disable-output-escaping="yes" select="$heading_user_desc"/></td>
				<td/>
			</tr> 
		</xsl:if>  
		<tr><td colspan="4" height="3px"></td></tr>   
   </xsl:when> 
 

   <xsl:when test="$presentation_type_code = 'CODE' and not ($type_code='CATEGORY' and string-length($code)=3)">  
        <tr><td colspan="4" height="10px"></td></tr> 
		<tr>
			<td class="code"><a><xsl:attribute name="name">
			    <xsl:value-of select="$code"/></xsl:attribute><xsl:value-of select="CONCEPT_CODE_WITH_DECIMAL_DAGGAR" /></a>			    
			    <xsl:if test="CONCEPT_CODE_WITH_DECIMAL_DAGGAR != ''"> 
			 		<xsl:call-template name="CA_ENHANCEMENT_FLAG"/>
			 	</xsl:if>
			 </td>
			<td class="code" colspan="2"><xsl:value-of disable-output-escaping="yes" select="$heading_user_desc"/>		
			
			<xsl:if test="$type_code='CATEGORY' and contains($code, '/')">
				<xsl:choose>			  		   
	  			<xsl:when test="HAS_VALIDATION='true'"> 
	  			    	<a href="javascript:popupIcdValidation('icdValidationPopup.htm?refid={$code}&amp;language={$language}&amp;classification={$clinical_classification_code}');">
				   	   		 <img src="img/v purple.png" alt=" V "/>
					    </a>
			    </xsl:when>
			    <xsl:otherwise> 
			 		 <img src="img/v grey.png" alt=" V "/>
			    </xsl:otherwise>
			 </xsl:choose>
			</xsl:if>
			</td>
			<td/>	   
		</tr>   
      </xsl:when>
    
    <!-- CCI types -->
      <xsl:when test="$presentation_type_code = 'SECTION'">
			<tr><td colspan="4" height="10px"></td></tr>
			<tr>
				<td class='sec' colspan="3" >
				<a>
					<xsl:attribute name="name"><xsl:value-of select="$code"/></xsl:attribute>
					<xsl:choose>
				     <xsl:when test="$language = 'ENG'">
				      <xsl:text>Section </xsl:text>
				     </xsl:when>
				     <xsl:when test="$language = 'FRA'">
				      <xsl:text>Section </xsl:text>
				     </xsl:when>
				    </xsl:choose>
					 <xsl:value-of select="$code"/>
					 <xsl:text> - </xsl:text>
					 <xsl:value-of disable-output-escaping="yes" select="$heading_user_desc"/>        
					</a>
				</td>
				<td/>
			</tr>
			<tr><td colspan="4" height="10px"></td></tr>
			<br/>
	     </xsl:when>
	    <xsl:when test="$presentation_type_code = 'CCIBLOCK1'">
	        <tr><td colspan="4" height="15px"></td></tr>			
			<tr>
				<td class='cci_bl1' colspan="3" >
				  <a><xsl:attribute name="name"><xsl:value-of select="$code"/></xsl:attribute>
				   <xsl:value-of disable-output-escaping="yes" select="$heading_user_desc"/>
				   </a>
				</td>
				<td/>
			</tr>
			<tr><td colspan="4" height="5px"></td></tr>   
	    </xsl:when>
	    
	    <xsl:when test="$presentation_type_code = 'CCIBLOCK2'">
	        <tr><td colspan="4" height="15px"></td></tr>
			<tr>
				<td class='cci_bl2' colspan="3" >
				 	<a><xsl:attribute name="name"><xsl:value-of select="$code"/></xsl:attribute>
				 	 	<xsl:value-of disable-output-escaping="yes" select="$heading_user_desc"/>
				 	 </a>
				</td>
				<td/>
			</tr>
			<tr><td colspan="4" height="5px"></td></tr>   
	    </xsl:when> 
	       
	    <xsl:when test="$presentation_type_code = 'CCIBLOCK3'">
	        <tr><td colspan="4" height="15px"></td></tr>
			<tr>
				<td class='cci_bl3' colspan="3" >
					<a><xsl:attribute name="name"><xsl:value-of select="$code"/></xsl:attribute>
				 	<xsl:value-of disable-output-escaping="yes" select="$heading_user_desc"/>
				 	</a>
				</td>
				<td/>
			</tr>
			<tr><td colspan="4" height="5px"></td></tr>   
	    </xsl:when> 
	     
	     <xsl:when test="$presentation_type_code = 'GROUP'">
			<tr><td colspan="4" height="15px"></td></tr>
			<tr>
			<td class='grp'><a><xsl:attribute name="name"><xsl:value-of select="$code"/></xsl:attribute><xsl:value-of select="$code"/></a>
			</td>
			<td class='grp' colspan="2"><xsl:value-of disable-output-escaping="yes" select="$heading_user_desc"/></td><td/>
			</tr>
			<tr><td colspan="4" height="3px"></td></tr>
	    </xsl:when>
	    
	    <xsl:when test="$presentation_type_code = 'RUBRIC'">
	        <tr><td colspan="4" height="10px"></td></tr>
			<tr>
				<td class='rub' valign="top"><a><xsl:attribute name="name"><xsl:value-of select="$code"/></xsl:attribute><xsl:value-of select="$code"/></a>
				</td>
				<td class='rub' colspan="2">
				<table border="0" valign="bottom">
				  <tr>
					 <td width="610px" valign="bottom"><xsl:value-of disable-output-escaping="yes" select="$heading_user_desc"/></td>
					 
					<xsl:call-template name="ATTRIBUTES"/> 
					
					<td width="50px">
				  		<xsl:choose>			  		   
				  			<xsl:when test="HAS_VALIDATION='true'"> 
				  			    	<a href="javascript:popupCciValidation('cciValidationPopup.htm?refid={$code}&amp;language={$language}&amp;classification={$clinical_classification_code}');">
							   	   		 <img src="img/v purple.png" alt=" V "/>
								    </a>
						    </xsl:when>
						    <xsl:otherwise> 
						 		 <img src="img/v grey.png" alt=" V "/>
						    </xsl:otherwise>
						 </xsl:choose>
					 </td>
				 </tr>
				 </table>
				</td>
				<td/>
			</tr>
			<tr><td colspan="4" height="3px"></td></tr>    
	    </xsl:when>
  </xsl:choose>     
  <xsl:apply-templates/>
</xsl:template> 

<xsl:template match="CLOB | BLOCK_LIST | ASTERISK_LIST | CODE_CLOB | ATTRIBUTES | ATTRIBUTE | CODE_LIST" >
 	<xsl:apply-templates/> 
</xsl:template>
  
<xsl:template match="language | classification | CODE | PRESENTATION_CODE | CA_ENHANCEMENT_FLAG | USER_DESC | TYPE_CODE | PRESENTATION_TYPE_CODE |  CONCEPT_CODE_WITH_DECIMAL_DAGGAR | CONCEPT_CODE_WITH_DECIMAL |   CODE_CONCEPT_CODE | CODE_CONCEPT_TYPE_CODE | CODE_CONCEPT_USER_DESC | HAS_VALIDATION | TYPE | HAS_REF | REF_CODE | MANDATORY" />

<!-- Display attributes for CCI -->

<xsl:template name="ATTRIBUTES">
    <xsl:for-each select="ATTRIBUTES/ATTRIBUTE"> 
		<xsl:variable name="type"><xsl:value-of select="TYPE"/></xsl:variable>
		<xsl:variable name="mandatory"><xsl:value-of select="MANDATORY"/></xsl:variable>
		<xsl:variable name="hasRef"><xsl:value-of select="HAS_REF"/></xsl:variable>
		<xsl:variable name="refCode"><xsl:value-of select="REF_CODE"/></xsl:variable>
		<xsl:variable name="refCodeNum">
			<xsl:value-of select="normalize-space(substring($refCode, 2))"></xsl:value-of>
		</xsl:variable>
		 
		<td width="50px">	   	   	
			<xsl:choose>
				<xsl:when test="$hasRef='false'">
					<xsl:choose>
						<xsl:when test="$type='S'">
						   	<img src="img/cci/s grey.png" alt="S" />
						</xsl:when>	
						<xsl:when test="$type='L'" >
							<img src="img/cci/l grey.png" alt="L" />
						</xsl:when>
						<xsl:when test="$type='E'" >
							<img src="img/cci/e grey.png" alt="E" />
						</xsl:when>
					</xsl:choose>	
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>					    
						<xsl:when test="$mandatory='true'" >
						    <xsl:choose>
								<xsl:when test="$type='S'">
									<a href="javascript:popupAttribute('attributePopup.htm?refid={$refCode}&amp;language={$language}&amp;classification={$clinical_classification_code}');">
			    	   					<img src="img/cci/s pink.png" alt="S" /></a><span class="superscript"><xsl:value-of select="$refCodeNum"/></span>	
			    	   				
								</xsl:when>	
								<xsl:when test="$type='L'" >								
									<a href="javascript:popupAttribute('attributePopup.htm?refid={$refCode}&amp;language={$language}&amp;classification={$clinical_classification_code}');">
			    	   					<img src="img/cci/l pink.png" alt="L" /></a><span class="superscript"><xsl:value-of select="$refCodeNum"/></span>	
								</xsl:when>
								<xsl:when test="$type='M'" >
									<a href="javascript:popupAttribute('attributePopup.htm?refid={$refCode}&amp;language={$language}&amp;classification={$clinical_classification_code}');">
			    	   					<img src="img/cci/m pink.png" alt="M" /></a><span class="superscript"><xsl:value-of select="$refCodeNum"/></span>				    	   				
								</xsl:when>
								<xsl:when test="$type='E'" >
									<a href="javascript:popupAttribute('attributePopup.htm?refid={$refCode}&amp;language={$language}&amp;classification={$clinical_classification_code}');">
			    	   					<img src="img/cci/e pink.png" alt="E" /></a><span class="superscript"><xsl:value-of select="$refCodeNum"/></span>
								</xsl:when>
							</xsl:choose>
						</xsl:when>
					    <xsl:otherwise>
							<xsl:choose>
								<xsl:when test="$type='S'">
									<a href="javascript:popupAttribute('attributePopup.htm?refid={$refCode}&amp;language={$language}&amp;classification={$clinical_classification_code}');">
			    	   					<img src="img/cci/s yellow.png" alt="S" /></a><span class="superscript"><xsl:value-of select="$refCodeNum"/></span>
								</xsl:when>	
								<xsl:when test="$type='L'" >
									<a href="javascript:popupAttribute('attributePopup.htm?refid={$refCode}&amp;language={$language}&amp;classification={$clinical_classification_code}');">
			    	   					<img src="img/cci/l yellow.png" alt="L" /></a><span class="superscript"><xsl:value-of select="$refCodeNum"/></span>
								</xsl:when>
								<xsl:when test="$type='M'" >
									<a href="javascript:popupAttribute('attributePopup.htm?refid={$refCode}&amp;language={$language}&amp;classification={$clinical_classification_code}');">
			    	   					<img src="img/cci/m yellow.png" alt="L" /></a><span class="superscript"><xsl:value-of select="$refCodeNum"/></span>
								</xsl:when>
								<xsl:when test="$type='E'" >
									<a href="javascript:popupAttribute('attributePopup.htm?refid={$refCode}&amp;language={$language}&amp;classification={$clinical_classification_code}');">
			    	   					<img src="img/cci/e yellow.png" alt="E" /></a><span class="superscript"><xsl:value-of select="$refCodeNum"/></span>
								</xsl:when>
							</xsl:choose>	
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
	    </td>
    </xsl:for-each>
</xsl:template>

<!--  Present CODE List for Rubrics -->
<xsl:template match="CODE_LIST[@hasCode='true']">
	<tr valign="top">
		<td />
		<td colspan="2">
		    <table width="80%" class="codeTable">
			    <xsl:apply-templates/>
			</table>
			<br/>
		</td>
		<td/> 
	</tr>
	<tr><td colspan="4" height="10px"></td></tr>  
</xsl:template>
<!--  End of Present CODE List for Rubrics -->

<xsl:template match="codeConcept">
	<xsl:if test="CODE_CONCEPT_TYPE_CODE = 'CCICODE'">
		<tr class="sm-text">
			<td width="20%"><a><xsl:attribute name="name"><xsl:value-of select="CODE_CONCEPT_CODE"/></xsl:attribute><xsl:value-of select="CODE_CONCEPT_CODE"/></a></td>
			<td><xsl:value-of disable-output-escaping="yes" select="CODE_CONCEPT_USER_DESC"/></td>
		</tr>
		 <xsl:apply-templates/>
    </xsl:if>
</xsl:template>

<!-- **************************** -->
<!--  Present Block List for Chapters -->
<xsl:template match="BLOCK_LIST[@hasBlock = 'true']">
   <tr><td colspan="4" height="15px"></td></tr>
   <tr> 
      <td class="code" colspan="3"> 
			<xsl:choose>
			     <xsl:when test="$language = 'ENG'">
			     	<xsl:choose>
			     		<xsl:when test="$clinical_classification_code='ICD-10-CA'"> 
				      		<xsl:text>This chapter contains the following blocks: </xsl:text>
				      	</xsl:when>
				      	<xsl:when test="$clinical_classification_code='CCI'">
				      		<xsl:text>This section contains the following blocks: </xsl:text>
				      	</xsl:when>
			      	</xsl:choose>
			     </xsl:when>
			     <xsl:when test="$language = 'FRA'">
			     	<xsl:choose>
			     		<xsl:when test="$clinical_classification_code='ICD-10-CA'"> 
				      		<xsl:text>Ce chapitre comprend les groupes suivants: </xsl:text>
				      	</xsl:when>
				      	<xsl:when test="$clinical_classification_code='CCI'">
				      		<xsl:text>Cette section comprend les groupes suivants: </xsl:text>
				      	</xsl:when>
			      	</xsl:choose>
			     </xsl:when>
		     </xsl:choose>
	    </td>
	    <td/>
    </tr>
	<xsl:apply-templates/> 
</xsl:template>

<xsl:template match="BLOCK">
   <tr>
	    <xsl:choose>
	    	<xsl:when test="@prependCodeToUserdesc='false'"> 
		    	<td nowrap="true" class="codeText">			
					<a>	
						<xsl:attribute name="href">
						    <xsl:text>#</xsl:text><xsl:value-of select="@code" />
						</xsl:attribute>
						<xsl:value-of select="@code" />
					</a>			
				 </td>
				<td colspan="2" class="codeText">
					<xsl:value-of select = "@shortDesc"/>
				</td>
				<td/>
	    	</xsl:when>
	    	<xsl:otherwise>
		    	<td nowrap="true" colspan="3"  class="codeText">			
					<a>	
						<xsl:attribute name="href">
						    <xsl:text>#</xsl:text><xsl:value-of select="@code" />
						</xsl:attribute>
						<xsl:value-of select = "@shortDesc"/>
					</a>			
				 </td>
				 <td/>
	    	</xsl:otherwise>
	    </xsl:choose>
	</tr>

</xsl:template>

<!-- End of Presenting Block List for Chapters-->

<!-- **************************** -->
<!--  Present Asterisk List for Chapters -->
<xsl:template match="ASTERISK_LIST[@hasAsterisk = 'true']">
	<tr><td colspan="4" height="15px"></td></tr>
   <tr> 
      <td class="code" colspan="3"> 
			<xsl:choose>
			     <xsl:when test="$language = 'ENG'">
			      <xsl:text>Asterisk categories for this chapter are provided as follows: </xsl:text>
			     </xsl:when>
			     <xsl:when test="$language = 'FRA'">
			      <xsl:text>Les categories de ce chapitre comprenant des astérisques sont les suivantes: </xsl:text>
			     </xsl:when>
		     </xsl:choose>
	    </td>
	    <td/>
    </tr>
	<xsl:apply-templates/>   
</xsl:template>

<xsl:template match="ASTERISK">
	<tr>
		<td  class="codeText">
			<a>	<xsl:attribute name="href">
			    <xsl:text>#</xsl:text><xsl:value-of select="@code" />
			</xsl:attribute>
			<xsl:value-of select="@code" /><xsl:text>*</xsl:text>
			</a>
		 </td>
		<td colspan="2"  class="codeText">
			<xsl:value-of select = "@shortDesc"/>
		</td>
		<td/>
	</tr>

</xsl:template>

<!-- End of Presenting Block List and Asterisk List for Chapters-->

<!-- ************************************************** -->
<!-- New Stuff -->
<xsl:template match="CONCEPT_DETAIL">
	<xsl:choose>
	    <xsl:when test="$clinical_classification_code = 'ICD-10-CA'">  	
		  <xsl:apply-templates select="CLOB/qualifierlist[@type = 'chpfront']"/>    
		  <xsl:apply-templates select="CLOB/qualifierlist[@type = 'definition']"/> 				   
		  <xsl:apply-templates select="CLOB/qualifierlist[@type = 'note']"/>
		  <xsl:apply-templates select="CLOB/qualifierlist[@type = 'includes']"/>  
		  <xsl:apply-templates select="CLOB/qualifierlist[@type = 'also']"/>
		  <xsl:apply-templates select="CLOB/qualifierlist[@type = 'excludes']"/> 
		  <xsl:apply-templates select="CLOB/table"/> 
	    </xsl:when>
	    <xsl:when test="$clinical_classification_code = 'CCI'">  	
		  <xsl:apply-templates select="CLOB/qualifierlist[@type = 'chpfront']"/>   
		  <xsl:apply-templates select="CLOB/qualifierlist[@type = 'includes']"/>   	
		  <xsl:apply-templates select="CLOB/qualifierlist[@type = 'excludes']"/> 	
		  <xsl:apply-templates select="CLOB/qualifierlist[@type = 'also']"/>	   
		  <xsl:apply-templates select="CLOB/qualifierlist[@type = 'note']"/> 
		  <xsl:apply-templates select="CLOB/qualifierlist[@type = 'omit']"/> 
		  <xsl:apply-templates select="CLOB/table"/> 
	    </xsl:when>	  
     </xsl:choose>
</xsl:template>

<xsl:template match="CODE_DETAIL">   
	  <xsl:apply-templates select="CODE_CLOB/qualifierlist[@type='includes']"/>   	
	  <xsl:apply-templates select="CODE_CLOB/qualifierlist[@type='excludes']"/> 	
	  <xsl:apply-templates select="CODE_CLOB/qualifierlist[@type='also']"/>	   
	  <xsl:apply-templates select="CODE_CLOB/qualifierlist[@type='note']"/>
	  <xsl:apply-templates select="CODE_CLOB/qualifierlist[@type='omit']"/>  
</xsl:template>

<!-- *********************** -->
<!-- (some "chpfront" stuff) -->
<xsl:template match="CLOB/qualifierlist[@type = 'chpfront']">
  <tr>
  <td colspan="3"> 
  <table width="80%" align="center">
  <tr>
  <td>
  <xsl:apply-templates/>
  </td>
  </tr>
  </table>
  </td>
  <td/>
  </tr>
</xsl:template>

<xsl:template match="chpfront | sub-section | clause">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="chpfront//table">
   <table cols="{@cols}" border="0" width="50%" class="sm-text">
     <xsl:apply-templates />
   </table>
</xsl:template>

<xsl:template match="chpfront/sub-section/label">
  <xsl:variable name="string" select="."/>
  <br/><br/>
  <!-- sometimes coded as <label>Note:</label>, sometimes as <label>Note</label>
       so strip out ":" if exists and add back at end -->
    <p class="sm-text">
    <b>
    <xsl:value-of select="translate($string,':','')" />
    <xsl:text>:</xsl:text>
    </b>
    </p>
</xsl:template>
  

<xsl:template match="clause/label">
  <p class="sm-text">
  <xsl:apply-templates/>
  </p>
</xsl:template>

<!-- *********************** -->


<!-- ***************************** label ************************** -->
<xsl:template match="sec/label">
       <xsl:if test="sec[@header]">
       <!-- <xsl:call-template name="BACK-TO-MAIN" /> -->
   
       </xsl:if>
       <title><xsl:apply-templates/></title>
       <div align="center">
       <h1 class="sec"><xsl:apply-templates/></h1>
       </div>
</xsl:template> 


<!-- ********************** rub table ************************************** -->        
      <!-- standardizing the column widths -->   
    <xsl:template match="CLOB/table">
      <tr>
	      <td colspan="4"> 
		      <table border="1" style="width:auto !important; margin:0 auto;">
		             <xsl:if test="not(thead) and @colwidth">
				       		<xsl:call-template name="SET_COL_WIDTH">
								<xsl:with-param name="string" select="normalize-space(translate(@colwidth, ',', ' '))"/>
							</xsl:call-template>
				      </xsl:if>
				  <xsl:apply-templates/>
		      </table>
	       </td>
      </tr> 
      <tr><td colspan="4" height="15px"></td></tr> 
    </xsl:template>

    <xsl:template match="CLOB/table/tbody">
      <tbody class="rubbody">
      	<xsl:apply-templates/>
      </tbody>
    </xsl:template>
    
  <xsl:template match="CLOB/table/thead">
    <thead class="rubhead">
    	<xsl:apply-templates/>
    </thead>
  </xsl:template>

<xsl:template match="CLOB/table/thead/tr">
  <tr valign="top">
  <xsl:apply-templates /> 
  </tr>
</xsl:template>

<xsl:template match="CLOB/table/tbody/tr">
  <tr valign="center">
  <xsl:apply-templates /> 
  </tr>
</xsl:template>

<xsl:template match="CLOB/table/thead/tr/td">
   <td>
       <xsl:if test="@colspan"> 
	       <xsl:attribute name="colspan">
	       		<xsl:value-of select="@colspan"></xsl:value-of>
	       </xsl:attribute>
	    </xsl:if>
	    <xsl:if test="@rowspan"> 
	       <xsl:attribute name="rowspan">
	       		<xsl:value-of select="@rowspan"></xsl:value-of>
	       </xsl:attribute>
	    </xsl:if>
	    
	    <xsl:if test="ancestor::table[@colwidth]"> 
	    	 <xsl:attribute name="width">
	    	 	 <xsl:call-template name="GET_PIXEL_VALUE">	    	 	      
		    	      <xsl:with-param name="string">
				             <xsl:call-template name="GET_TD_WIDTH">
				             	    <xsl:with-param name="position" select="position()"/>
				    				<xsl:with-param name="widthString" select="normalize-space(translate(ancestor::table/@colwidth, ',', ' '))"/>
				             </xsl:call-template>
				     </xsl:with-param>
	             </xsl:call-template>
	            </xsl:attribute>
	    </xsl:if>	    	
	    <xsl:attribute name="style">
	         <xsl:call-template name="GET_TEXT_ALIGN">
	        	<xsl:with-param name="align"> 
	        		<xsl:value-of select="@align"/>
	        	</xsl:with-param>
	        </xsl:call-template>
	        <xsl:call-template name="GET_TEXT_VALIGN">
	        	<xsl:with-param name="valign"> 
	        		<xsl:value-of select="@valign"/>
	        	</xsl:with-param>
	        </xsl:call-template>
		 </xsl:attribute>
	  	 <xsl:apply-templates />
	  </td>
</xsl:template>


<xsl:template match="CLOB/table/tbody/tr/td">
     <xsl:choose>
	   <xsl:when test="@colspan">
		  <td class="rubbody-subtitle">
		      <xsl:if test="@colspan"> 
			       <xsl:attribute name="colspan">
			       		<xsl:value-of select="@colspan"></xsl:value-of>
			       </xsl:attribute>
			    </xsl:if>
			    <xsl:if test="@rowspan"> 
			       <xsl:attribute name="rowspan">
			       		<xsl:value-of select="@rowspan"></xsl:value-of>
			       </xsl:attribute>
			    </xsl:if>
		     <xsl:attribute name="style">
		         <xsl:call-template name="GET_TEXT_ALIGN">
		        	<xsl:with-param name="align"> 
		        		<xsl:value-of select="@align"/>
		        	</xsl:with-param>
		        </xsl:call-template>
		        <xsl:call-template name="GET_TEXT_VALIGN">
		        	<xsl:with-param name="valign"> 
		        		<xsl:value-of select="@valign"/>
		        	</xsl:with-param>
		        </xsl:call-template>
		     </xsl:attribute>
		 	 <xsl:apply-templates />
		  </td>
	  </xsl:when>
	  <xsl:otherwise>
	    <td>
	        <xsl:if test="@colspan"> 
		       <xsl:attribute name="colspan">
		       		<xsl:value-of select="@colspan"></xsl:value-of>
		       </xsl:attribute>
		    </xsl:if>
		    <xsl:if test="@rowspan"> 
		       <xsl:attribute name="rowspan">
		       		<xsl:value-of select="@rowspan"></xsl:value-of>
		       </xsl:attribute>
		    </xsl:if>
	    	<xsl:attribute name="style">
		         <xsl:call-template name="GET_TEXT_ALIGN">
		        	<xsl:with-param name="align"> 
		        		<xsl:value-of select="@align"/>
		        	</xsl:with-param>
		        </xsl:call-template>
		        <xsl:call-template name="GET_TEXT_VALIGN">
		        	<xsl:with-param name="valign"> 
		        		<xsl:value-of select="@valign"/>
		        	</xsl:with-param>
		        </xsl:call-template>
			 </xsl:attribute>
		   	<xsl:apply-templates />
		 </td>
      </xsl:otherwise>
    </xsl:choose>   
</xsl:template>


<!-- *********************** popup ****************************** -->

<!-- Updated for CCI Viewer - "popupref.xsql" calls "popupref_clob.xsql"
     which does a CLOB cleanup prior to generating HTML output -->
<!-- ICD Viewer - calling "icd_popupref.xsql", but should make totally 
     generic by passing "db-connection", "clinical_classification_code", etc.
     from the startup page (i.e. maybe use "index.xsql" instead of "index.html") -->
  <xsl:template match="popupref">   	
	<span class="popupref">
		<xsl:variable name="refid" select="@refid"/>
	    <a href="javascript:popupConceptDetail ('conceptDetailPopup.htm?refid={$refid}&amp;language={$language}&amp;classification={$clinical_classification_code}');" style="text-decoration:none;">
   	   	  <span class="superscript_popup">++</span>
	    </a>
	    
    </span>
	<xsl:apply-templates/>    
  </xsl:template>

  
  <!-- sometimes the popupref wrapper was missed in the xml -->
  <xsl:template match="popupref/ulist|td/ulist|ulist">
    
    <!-- If inside a brace, add a little bit of margin-top for 
         better display
         (all other ulists should be flush with surrounding text)
    -->
     <xsl:if test="label">
       <xsl:apply-templates select="label"/>
     </xsl:if>
     <xsl:variable name="margin-top">
      <xsl:choose>
       <xsl:when test="ancestor::brace">
        <xsl:text>5</xsl:text>
       </xsl:when>
       <xsl:otherwise>
        <xsl:text>0</xsl:text>
       </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:choose>
    	<xsl:when test='@mark="none"'> 
    		<ul style="list-style:none; margin-top: {$margin-top}; margin-bottom: 0;">
		    	 <xsl:choose>
    	        	<xsl:when test="count(preceding::brace) = 0 and count(following::brace) = 0">
				    	<xsl:apply-templates select="child::node()[not(self::label)]">
					    	<xsl:sort select="not(starts-with(normalize-space(.),$nos)) "/>
					    	<xsl:sort select='normalize-space(translate(lower-case(/ulist/label|.),"àâäçéèêëiîïôöùûü","aaaceeeeiiioouuu"))' />
				    	</xsl:apply-templates>
			    	</xsl:when>
			    	<xsl:otherwise>
			    		<xsl:apply-templates select="child::node()[not(self::label)]"></xsl:apply-templates>
			    	</xsl:otherwise>
    	        </xsl:choose>
		    </ul>
    	</xsl:when>
    	<xsl:otherwise>
    	    <ul style="margin-top: {$margin-top}; margin-bottom: 0;">
    	        <xsl:choose>
    	        	<xsl:when test="count(preceding::brace) = 0 and count(following::brace) = 0">
				    	<xsl:apply-templates select="child::node()[not(self::label)]">
					    	<xsl:sort select="not(starts-with(normalize-space(.),$nos)) "/>
					    	<xsl:sort select='normalize-space(translate(lower-case(/ulist/label|.),"àâäçéèêëiîïôöùûü","aaaceeeeiiioouuu"))' />
				    	</xsl:apply-templates>
			    	</xsl:when>
			    	<xsl:otherwise>
			    		<xsl:apply-templates select="child::node()[not(self::label)]"></xsl:apply-templates>
			    	</xsl:otherwise>
    	        </xsl:choose>
		    </ul>
    	</xsl:otherwise>
    </xsl:choose>  
   </xsl:template>
  
 
  <xsl:template match="popupref/ulist/label | td/ulist/label | ulist/label">
     <xsl:apply-templates />
  </xsl:template>
  
   <xsl:template match="popupref/ulist/ulist | td/ulist/ulist | ulist/ulist">
       <li class="sm-text">
          <xsl:apply-templates select="label"/>
      </li>
      <ul>
       <xsl:apply-templates select="child::node()[not(self::label)]">
              <xsl:sort select="not(starts-with(normalize-space(.),$nos)) "/>
              <xsl:sort select='normalize-space(translate(lower-case(/ulist/label|.),"àâäçéèêëiîïôöùûü","aaaceeeeiiioouuu"))' />
       </xsl:apply-templates>
      </ul>
   
   </xsl:template>
  
  <xsl:template match="popupref/ulist/listitem | td/ulist/listitem | ulist/listitem">
     
      <li class="sm-text">
         <xsl:apply-templates />
      </li>
     
  </xsl:template>
  
<!-- *************************** -->
<!-- Note: conditional [@type="includes"] branching doesn't seem to 
     be supported by IE5, but can still do the hard way:
        includes:	becomes 	Includes:
        excludes:	becomes 	Excludes:
        note:		becomes 	Note:
        also:		becomes 	Code Also:
        omit:		becomes		Omit Code:
-->

<!-- 1. Qualifier types - Non-Code level -->
<!--    CV - January 2005
           - not wrapping items in <ul>...</ul> anymore
             (introduces extraneous bullets in HTML view 
              which should be introduced by explicit
              <ulist>...</ulist> only)
-->
  <xsl:template match="CLOB/qualifierlist[@type='includes'] | chpfront//qualifierlist[@type='includes'] | sec/qualifierlist[@type='includes']">
    <xsl:variable name="caption">
      <xsl:choose>
      <xsl:when test="$language = 'FRA'">
       <xsl:text>Comprend:</xsl:text>
      </xsl:when>
      <xsl:otherwise>
       <xsl:text>Includes:</xsl:text>
      </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
   
    
    <tr valign="top">
		<td class='include'></td> 
		<xsl:choose>
			<xsl:when test="$code = 'CODE'">
				<td class='include' colspan="2" >
				    <xsl:choose>
		              <xsl:when test="count(include/label|include/ulist/label) > 1 and count(include/brace) = 0">
				        <xsl:apply-templates select="include"> 
				            <xsl:sort select="not(starts-with(normalize-space(label),$nos)) and not(starts-with(normalize-space(ulist/label),$nos))"/>  
				            <xsl:sort select='normalize-space(translate(lower-case(label|ulist/label),"àâäçéèêëiîïôöùûü","aaaceeeeiiioouuu"))' />
				        </xsl:apply-templates>
				     </xsl:when>
		             <xsl:otherwise>
			           <xsl:apply-templates select="include"/>
			        </xsl:otherwise>
		         </xsl:choose>  
				</td>				
			</xsl:when>
			<xsl:otherwise>
				<td class='includelabel'><xsl:value-of select="$caption"/></td>
				<td class='include' >
					    <xsl:choose>
			              <xsl:when test="count(include/label|include/ulist/label) > 1 and count(include/brace) = 0">
					        <xsl:apply-templates select="include"> 
					            <xsl:sort select="not(starts-with(normalize-space(label),$nos)) and not(starts-with(normalize-space(ulist/label),$nos))"/>  
					            <xsl:sort select='normalize-space(translate(lower-case(label|ulist/label),"àâäçéèêëiîïôöùûü","aaaceeeeiiioouuu"))' />
					        </xsl:apply-templates>
					     </xsl:when>
			             <xsl:otherwise>
				           <xsl:apply-templates select="include"/>
				        </xsl:otherwise>
			         </xsl:choose>  
				</td>
			</xsl:otherwise>
		</xsl:choose>
		<td/>
	</tr>
  </xsl:template>

  <xsl:template match="CLOB/qualifierlist[@type='excludes'] | chpfront//qualifierlist[@type='excludes'] | sec/qualifierlist[@type='excludes']">
    <xsl:variable name="caption">
      <xsl:choose>
      <xsl:when test="$language = 'FRA'">
       <xsl:text>À l' exclusion de:</xsl:text>
      </xsl:when>
      <xsl:otherwise>
       <xsl:text>Excludes:</xsl:text>
      </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
	<tr valign="top">
	<td class='exclude'></td>
	<td class='excludelabel'><xsl:value-of select="$caption"/></td>
	<td class='exclude'>
	      <xsl:choose>
            <xsl:when test="count(exclude/label|exclude/ulist/label) > 1 and count(exclude/brace) = 0">
	            <xsl:apply-templates select="exclude"> 
	                   <xsl:sort select="not(starts-with(normalize-space(label),$nos)) and not(starts-with(normalize-space(ulist/label),$nos))"/> 
	                    <xsl:sort select='normalize-space(translate(lower-case(label|ulist/label),"àâäçéèêëiîïôöùûü","aaaceeeeiiioouuu"))' />
	            </xsl:apply-templates>
	        </xsl:when>
            <xsl:otherwise>
	           <xsl:apply-templates select="exclude"/>
	        </xsl:otherwise>
         </xsl:choose>  
	 </td>
	 <td/>
	</tr>    
  </xsl:template>

 <xsl:template match="CLOB/qualifierlist[@type='note'] | chpfront//qualifierlist[@type='note'] | sec/qualifierlist[@type='note']">
    <xsl:variable name="caption">
      <xsl:choose>
      <xsl:when test="$language = 'FRA'">
       <xsl:text>Note:</xsl:text>
      </xsl:when>
      <xsl:otherwise>
       <xsl:text>Note:</xsl:text>
      </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
	<tr valign="top">
		<td class='note'></td> 
		<td class='notelabel'><xsl:value-of select="$caption"/></td>
		<td class='note'> 
	           <xsl:apply-templates select="note | table"/>
		</td>
		<td/>
	</tr>
  </xsl:template>
 
  
 <xsl:template match="CLOB/qualifierlist[@type='definition'] | chpfront//qualifierlist[@type='definition'] | sec/qualifierlist[@type='definition']">
	<tr valign="top">
	<td/>
	<td class='definition' colspan="2"><xsl:apply-templates/></td>
	<td/>
	</tr>
  </xsl:template>

 <xsl:template match="CLOB/qualifierlist[@type='also'] | chpfront//qualifierlist[@type='also'] | sec/qualifierlist[@type='also']">
    <xsl:variable name="caption">
      <xsl:choose>
      <xsl:when test="$language = 'FRA'">
       <xsl:text>À codifier aussi:</xsl:text>
      </xsl:when>
      <xsl:otherwise>
       <xsl:text>Code Also:</xsl:text>
      </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
	<tr valign="top">
		<td class='codealso'></td>
		<td class='codealsolabel'><xsl:value-of select="$caption"/></td> 
		<td class='codealso'>    
		  <xsl:choose>
            <xsl:when test="count(also/label|also/ulist/label) > 1 and count(also/brace) = 0">
	            <xsl:apply-templates select="also"> 
	                   <xsl:sort select="not(starts-with(normalize-space(label),$nos)) and not(starts-with(normalize-space(ulist/label),$nos))"/> 
	                   <xsl:sort select='normalize-space(translate(lower-case(label|ulist/label),"àâäçéèêëiîïôöùûü","aaaceeeeiiioouuu"))' />
	            </xsl:apply-templates>
	        </xsl:when>
            <xsl:otherwise>
	           <xsl:apply-templates select="also"/>
	        </xsl:otherwise>
         </xsl:choose> 
		</td>
		<td/>
	</tr>    
  </xsl:template>
  
   <xsl:template match="CLOB/qualifierlist[@type='omit']">
    <xsl:variable name="caption">
      <xsl:choose>
      <xsl:when test="$language = 'FRA'">
       <xsl:text>Ne pas utiliser:</xsl:text>
      </xsl:when>
      <xsl:otherwise>
       <xsl:text>Omit Code:</xsl:text>
      </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
	<tr valign="top">
		<td class='omit'></td> 
		<td class='omitlabel'><xsl:value-of select="$caption"/></td>
		<td class='omit'>	 
           <xsl:choose>
            <xsl:when test="count(omit/label|omit/ulist/label) > 1 and count(omit/brace) = 0">
	            <xsl:apply-templates select="omit"> 
	                   <xsl:sort select="not(starts-with(normalize-space(label),$nos)) and not(starts-with(normalize-space(ulist/label),$nos))"/> 
	                   <xsl:sort select='normalize-space(translate(lower-case(label|ulist/label),"àâäçéèêëiîïôöùûü","aaaceeeeiiioouuu"))' />
	            </xsl:apply-templates>
	        </xsl:when>
            <xsl:otherwise>
	           <xsl:apply-templates select="omit"/>
	        </xsl:otherwise>
         </xsl:choose>  
		</td>
		<td/>
	</tr>
  </xsl:template>

<!-- 2. Qualifier types - CCI Code level -->
  <xsl:template match="CODE_CLOB/qualifierlist[@type='includes']">
    <xsl:variable name="caption">
      <xsl:choose>
      <xsl:when test="$language = 'FRA'">
       <xsl:text>Comprend:</xsl:text>
      </xsl:when>
      <xsl:otherwise>
       <xsl:text>Includes:</xsl:text>
      </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <tr>
    <td valign="top" class='includelabel'><xsl:value-of select="$caption"/></td>
    <td valign="top" class="tbl-note">
        <xsl:choose>
              <xsl:when test="count(include/label|include/ulist/label) > 1 and count(include/brace) = 0">
		        <xsl:apply-templates select="include"> 
		            <xsl:sort select="not(starts-with(normalize-space(label),$nos)) and not(starts-with(normalize-space(ulist/label),$nos))"/> 
		             <xsl:sort select='normalize-space(translate(lower-case(label|ulist/label),"àâäçéèêëiîïôöùûü","aaaceeeeiiioouuu")) '  />
		        </xsl:apply-templates>
		     </xsl:when>
             <xsl:otherwise>
	           <xsl:apply-templates select="include"/>
	        </xsl:otherwise>
         </xsl:choose>  
    </td>
    </tr>
  </xsl:template>

  <xsl:template match="CODE_CLOB/qualifierlist[@type='excludes']">
      <xsl:variable name="caption">
      <xsl:choose>
      <xsl:when test="$language = 'FRA'">
       <xsl:text>À l' exclusion de:</xsl:text>
      </xsl:when>
      <xsl:otherwise>
       <xsl:text>Excludes:</xsl:text>
      </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <tr>
	    <td valign="top" class='excludelabel'><xsl:value-of select="$caption"/> </td>
	    <td valign="top"  class="tbl-note">   <xsl:choose>
            <xsl:when test="count(exclude/label|exclude/ulist/label) > 1 and count(exclude/brace) = 0">
	            <xsl:apply-templates select="exclude"> 
	                <xsl:sort select="not(starts-with(normalize-space(label),$nos)) and not(starts-with(normalize-space(ulist/label),$nos))"/> 
	                <xsl:sort select='normalize-space(translate(lower-case(label|ulist/label),"àâäçéèêëiîïôöùûü","aaaceeeeiiioouuu"))'  />
	            </xsl:apply-templates>
	        </xsl:when>
            <xsl:otherwise>
	           <xsl:apply-templates select="exclude"/>
	        </xsl:otherwise>
         </xsl:choose> 
	    </td>
    </tr>
  </xsl:template>

  <xsl:template match="CODE_CLOB/qualifierlist[@type='note']">
      <xsl:variable name="caption">
      <xsl:choose>
      <xsl:when test="$language = 'FRA'">
       <xsl:text>Note:</xsl:text>
      </xsl:when>
      <xsl:otherwise>
       <xsl:text>Note:</xsl:text>
      </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <tr>
	    <td valign="top" class='notelabel'><xsl:value-of select="$caption"/> </td>
	    <td valign="top"  class="tbl-note">
	           <xsl:apply-templates select="note | table"/>
	    </td>
    </tr>
  </xsl:template>

  <xsl:template match="CODE_CLOB/qualifierlist[@type='also']">
      <xsl:variable name="caption">
      <xsl:choose>
      <xsl:when test="$language = 'FRA'">
       <xsl:text>À codifier aussi:</xsl:text>
      </xsl:when>
      <xsl:otherwise>
       <xsl:text>Code Also:</xsl:text>
      </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <tr>
	    <td valign="top" class='codealsolabel'><xsl:value-of select="$caption"/></td>
	    <td valign="top"  class="tbl-note">
	       <xsl:choose>
            <xsl:when test="count(also/label|also/ulist/label) > 1 and count(also/brace) = 0">
	            <xsl:apply-templates select="also"> 
	                   <xsl:sort select="not(starts-with(normalize-space(label),$nos)) and not(starts-with(normalize-space(ulist/label),$nos))"/> 
	                   <xsl:sort select='normalize-space(translate(lower-case(label|ulist/label),"àâäçéèêëiîïôöùûü","aaaceeeeiiioouuu"))' />
	            </xsl:apply-templates>
	        </xsl:when>
            <xsl:otherwise>
	           <xsl:apply-templates select="also"/>
	        </xsl:otherwise>
         </xsl:choose>  
	    </td>
    </tr>
  </xsl:template>

  <xsl:template match="CODE_CLOB/qualifierlist[@type='omit']">
      <xsl:variable name="caption">
      <xsl:choose>
      <xsl:when test="$language = 'FRA'">
       <xsl:text>Ne pas utiliser:</xsl:text>
      </xsl:when>
      <xsl:otherwise>
       <xsl:text>Omit Code:</xsl:text>
      </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <tr>
	    <td valign="top" class='omitlabel'><xsl:value-of select="$caption"/></td>
	    <td valign="top"  class="tbl-note">
	      <xsl:choose>
            <xsl:when test="count(omit/label|omit/ulist/label) > 1 and count(omit/brace) = 0">
	            <xsl:apply-templates select="omit"> 
	                   <xsl:sort select="not(starts-with(normalize-space(label),$nos)) and not(starts-with(normalize-space(ulist/label),$nos))"/> 
	                   <xsl:sort select='normalize-space(translate(lower-case(label|ulist/label),"àâäçéèêëiîïôöùûü","aaaceeeeiiioouuu"))' />
	            </xsl:apply-templates>
	        </xsl:when>
            <xsl:otherwise>
	           <xsl:apply-templates select="omit"/>
	        </xsl:otherwise>
         </xsl:choose>  
	    </td>
    </tr>
  </xsl:template>

<!-- *** End of qualifierlist @types *** -->

   <xsl:template match="
        CLOB/qualifierlist/exclude | CLOB/qualifierlist/include | CLOB/qualifierlist/note | CLOB/qualifierlist/also | CLOB/qualifierlist/definition | CLOB/qualifierlist/omit
        | CODE_CLOB/qualifierlist/exclude | CODE_CLOB/qualifierlist/include | CODE_CLOB/qualifierlist/note | CODE_CLOB/qualifierlist/also | CODE_CLOB/qualifierlist/omit
        | chpfront//exclude | chpfront//include | chpfront//note | chpfront//also | chpfront//definition
        | sec/qualifierlist/exclude | sec/qualifierlist/include | sec/qualifierlist/note | sec/qualifierlist/also | sec/qualifierlist/definition">   
      <xsl:apply-templates/>  
  </xsl:template>
  
   
   <xsl:template match="exclude/label   
                       |include/label 
                       |definition/label
                       | also/label
                       | note/label 
                       | omit/label">
     <xsl:apply-templates/> <br/>
  </xsl:template>
  
  
   <xsl:include href="cims_table.xsl"/>
 
</xsl:stylesheet>


