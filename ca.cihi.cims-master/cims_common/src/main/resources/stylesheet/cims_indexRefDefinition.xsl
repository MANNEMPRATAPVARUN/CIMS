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

  
<xsl:template match=" BOOK_INDEX_TYPE | ELEMENT_ID | INDEX_TYPE | LEVEL_NUM | SEE_ALSO_FLAG | SITE_INDICATOR |
					  REFERENCE_LINK_DESC| CONTAINER_INDEX_ID |
					  MAIN_CODE_PRESENTATION | MAIN_CONTAINER_CONCEPT_ID | PAIRED_FLAG | SORT_STRING | PAIRED_CODE_PRESENTATION | PAIRED_CONTAINER_CONCEPT_ID |  
					  TF_CONTAINER_CONCEPT_ID | CODE_PRESENTATION" />


<xsl:template match="REFERENCE_LIST">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="index">
	<xsl:choose>
	    <xsl:when test="$index_type='BOOK_INDEX'"></xsl:when>
		<xsl:when test="$index_type='LETTER_INDEX'"></xsl:when>
		<xsl:when test="$level_num  &gt; 0 and $level_num  &lt; 11"> 
				<xsl:choose>
					<xsl:when test="$book_index_type='D'">
					    <td><xsl:call-template name="REFERENCE_LIST"/></td>	
					    <xsl:choose>
						    <xsl:when test="DRUGS_DETAIL/TABULAR_REF">
					    		<xsl:call-template name="displayDrugDetail"/>	
					    	</xsl:when>	
					    	<xsl:otherwise>
					    		<td/><td/><td/><td/><td/>
					    	</xsl:otherwise>
				    	</xsl:choose>
				    			    					    		
					</xsl:when>
					<xsl:when test= "$book_index_type='N'">	
					       <td><xsl:call-template name="REFERENCE_LIST"/></td>	
					       <td>				                   
						      <xsl:choose>
							      <xsl:when test="$site_indicator='$'"> 
							      	<xsl:text>&amp;diams;</xsl:text>
							      </xsl:when>
							      <xsl:otherwise>
							      	<xsl:value-of select="$site_indicator"/>
							      </xsl:otherwise>
						      </xsl:choose>
							</td>	
							 <xsl:choose>
							    <xsl:when test="NEOPLASM_DETAIL/TABULAR_REF">
						    		<xsl:call-template name="displayNeoplasmDetail"/>
						    	</xsl:when>	
						    	<xsl:otherwise>
						    		<td/><td/><td/><td/><td/>
						    	</xsl:otherwise>
					    	</xsl:choose> 
					</xsl:when>	
					<xsl:otherwise>
						<xsl:call-template name="REFERENCE_LIST"/>
					</xsl:otherwise>				
				</xsl:choose>			
	    </xsl:when>	    
	    <xsl:otherwise>
	      <xsl:text>The data is incorrect. Please check the data!!</xsl:text>
	    </xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="displayDrugDetail">
	<xsl:for-each select="DRUGS_DETAIL/TABULAR_REF">
    	<xsl:variable name="containerConceptId">
    		<xsl:value-of select="TF_CONTAINER_CONCEPT_ID"></xsl:value-of>
    	</xsl:variable>    	
    	<td>
	    	<xsl:if test="$containerConceptId != ''"> 
	     	     <xsl:value-of select="CODE_PRESENTATION"></xsl:value-of>
	    	</xsl:if> 
    	</td>
	</xsl:for-each>
</xsl:template>

<xsl:template name="displayNeoplasmDetail">
	<xsl:for-each select="NEOPLASM_DETAIL/TABULAR_REF">
    	<xsl:variable name="containerConceptId">
    		<xsl:value-of select="TF_CONTAINER_CONCEPT_ID"></xsl:value-of>
    	</xsl:variable>
    	
    	<td>
	    	<xsl:if test="$containerConceptId != ''"> 
	     	      	<xsl:value-of select="CODE_PRESENTATION"></xsl:value-of>
	    	</xsl:if> 
    	</td>
	</xsl:for-each>
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
	<xsl:apply-templates select="CATEGORY_REFERENCE"><xsl:sort select="SORT_STRING" lang="en"/></xsl:apply-templates>	
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
    <xsl:variable name="codePresentation">  
        <xsl:choose> 
	    	<xsl:when test="PAIRED_FLAG='Y'">
	    		<xsl:text>(</xsl:text>
	    		<xsl:value-of select="MAIN_CODE_PRESENTATION"/>
	    		<xsl:choose>
   		            <xsl:when test="MAIN_DAGGER_ASTERISK='+'">
   		         	     <xsl:text>&amp;#134;</xsl:text>
   		         	</xsl:when>
   		         	<xsl:otherwise>
   		         	 <xsl:value-of select="MAIN_DAGGER_ASTERISK"/>
   		         	</xsl:otherwise>
   		         </xsl:choose>	
	    		<xsl:text>/</xsl:text>
	    		<xsl:value-of select="PAIRED_CODE_PRESENTATION"/><xsl:value-of select ="PAIRED_DAGGER_ASTERISK"/>
	    		<xsl:text>)</xsl:text>
	    	</xsl:when>
	    	<xsl:otherwise>
	    		<xsl:value-of select="MAIN_CODE_PRESENTATION"/><xsl:value-of select="MAIN_DAGGER_ASTERISK"/>
	    	</xsl:otherwise>
    	</xsl:choose>    	
    </xsl:variable>     	
     <xsl:value-of select="$codePresentation"></xsl:value-of> 
 	<xsl:text> </xsl:text>     
</xsl:template>

<xsl:template match="INDEX_REF"> 
   	<xsl:choose>
   		<xsl:when test="position() = 1">
   			<text> </text>
   		</xsl:when>
   		<xsl:otherwise>
   			<text>, </text>
   		</xsl:otherwise>
   	</xsl:choose>
	<xsl:variable name="containerIndexId"><xsl:value-of select="CONTAINER_INDEX_ID" /></xsl:variable>	   
  	<xsl:choose>
		<xsl:when test="CONTAINER_INDEX_ID = ''">			
			<xsl:value-of select="REFERENCE_LINK_DESC"/>				
		</xsl:when>
		<xsl:otherwise>
			<a href="javascript:popupIndexInfo('viewIndexReference.htm?indexRefId={$containerIndexId}');">
	    	      	<xsl:value-of select="REFERENCE_LINK_DESC"></xsl:value-of>
	    	    </a>
		</xsl:otherwise>
   </xsl:choose>  
</xsl:template>
</xsl:stylesheet>


 
  