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

<xsl:variable name="supplementId">
	<xsl:value-of select="normalize-space(supplement/@id)" />
</xsl:variable>

<xsl:variable name="language">
	<xsl:value-of select="normalize-space(supplement/@language)" />
</xsl:variable>

<xsl:variable name="clinical_classification_code">
	<xsl:value-of select="normalize-space(supplement/@classification)" />
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
  </xsl:choose>
</xsl:variable>

<xsl:variable name="newline">
   <xsl:text>&amp;#10;</xsl:text>
</xsl:variable>

 
<xsl:template match="/"> 	 
    <xsl:apply-templates/> 
</xsl:template> 


<!-- ************************************************** -->
<!-- Flow-through -->

<xsl:template match="supplement">
	<xsl:apply-templates/>
</xsl:template> 
  
<xsl:template match="section/label | clause/label | sub-clause/label" />

<xsl:template match="block">
  <tr><td colspan="4" height="10px"></td></tr>
  <tr>
		<td class='chp' colspan="3" >
		   <a><xsl:attribute name="name"><xsl:value-of select="$supplementId"/></xsl:attribute>
			 <xsl:value-of disable-output-escaping="yes" select="label"/> 
		   </a>
		</td>
		<td/>
  </tr>
  <tr><td colspan="4" height="3px"></td></tr>
  <tr>
	  <td colspan="3">
	  	<xsl:apply-templates/>
	  </td>
	  <td/>
  </tr>	 
</xsl:template>

<xsl:template match="section"> 
  <tr><td colspan="4" height="10px"></td></tr>
  <tr>
		<td class='chp' colspan="3" >
		  <a><xsl:attribute name="name"><xsl:value-of select="$supplementId"/></xsl:attribute>
			 <xsl:value-of disable-output-escaping="yes" select="label"/> 
		  </a>
		</td>
		<td/>
  </tr>
  <tr><td colspan="4" height="3px"></td></tr>
  <tr>
	  <td colspan="3">
	  	<xsl:apply-templates/>
	  </td>
	  <td/>
  </tr>
</xsl:template>

<xsl:template match="report">
   <xsl:copy-of select="."/>
</xsl:template>

<xsl:template match="clause"> 
  <tr><td colspan="4" height="10px"></td></tr>
  <tr>
		<td class='grp' colspan="3" >
			 <xsl:value-of disable-output-escaping="yes" select="label"/> 
		</td>
		<td/>
  </tr>
  <tr><td colspan="4" height="3px"></td></tr>
  <tr>
	  <td colspan="3">
	  	<xsl:apply-templates/>
	  </td>
	  <td/>
  </tr>
</xsl:template>

<xsl:template match="sub-clause"> 
  <tr><td colspan="4" height="10px"></td></tr>
  <tr>
		<td class='rub' colspan="3" >
			 <xsl:value-of disable-output-escaping="yes" select="label"/> 
		</td>
		<td/>
  </tr>
  <tr><td colspan="4" height="3px"></td></tr>
  <tr>
	  <td colspan="3">
	  	<xsl:apply-templates/>
	  </td>
	  <td/>
  </tr>
</xsl:template>

<xsl:template match="sec/label">
    <xsl:if test="sec[@header]">
    <!-- <xsl:call-template name="BACK-TO-MAIN" /> -->

    </xsl:if>
    <title><xsl:apply-templates/></title>
    <div align="center">
    <h1 class="sec"><xsl:apply-templates/></h1>
    </div>
</xsl:template>  

<xsl:template match="sub-section">
  <xsl:apply-templates/>
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


<!-- Links -->
<!-- note: id's and xrefid's don't match, therefore no linking -->
<!-- handle the graphic <xref refid="E_fig1cci.gif">..</xref> -->

<xsl:template match="para/xref">
    <xsl:variable name="refid" select="normalize-space(@refid)"/>
    
    <xsl:choose>
	   <xsl:when test="contains($refid, '.gif')">
		    <xsl:choose>
		    	<xsl:when test="../following-sibling::graphic[@src=$refid]">
		    	    <a style="text-decoration:none" href="javascript:popupDiagram('popupDiagram.htm?diagramFileName={$refid}');">
				   		<xsl:value-of select="."/> 
					</a>
		    	</xsl:when>
		    	<xsl:otherwise>
		    	    <xsl:choose>
		    	    	<xsl:when test="../following-sibling::graphic[@src='']">
		    	    	  <a STYLE="text-decoration:none" href="javascript:popupMissingGraphicMessage()">
						   		<xsl:value-of select="."/>
						  </a>
		    	    	</xsl:when>
		    	    	<xsl:otherwise>
		    	    	   <a STYLE="text-decoration:none" href="javascript:popupMissingGraphicMessage()">
						   		<xsl:value-of select="."/>
						   </a>
		    	    	</xsl:otherwise>
		    	    </xsl:choose>
		    	</xsl:otherwise>
		    </xsl:choose> 
    	</xsl:when>
    	<xsl:otherwise>
    	    <a>
				<xsl:attribute name="href">
				    <xsl:text>#</xsl:text><xsl:value-of select="$refid" />
				</xsl:attribute>
		        <xsl:apply-templates/>
		    </a>
    	</xsl:otherwise>
     </xsl:choose>
</xsl:template>
  
  <xsl:template match="graphic"> 
        <xsl:variable name="src" select="normalize-space(@src)"/>  
        <xsl:if test="not(preceding-sibling::para/xref)">
			  <div class="graphicDiv">
			     <xsl:attribute name="src"><xsl:value-of select="$src"/></xsl:attribute>
			     <xsl:attribute name="align">			      
				        <xsl:call-template name="GET_GRAPHIC_ALIGN">
							<xsl:with-param name="string" select="@align"/>
						 </xsl:call-template>
				  </xsl:attribute>
			     <xsl:attribute name="style">
				    <xsl:if test="@width"> 
				    	<xsl:text>width:</xsl:text>
				    	<xsl:value-of select="@width"></xsl:value-of>
				    	<xsl:text>;</xsl:text>
				    </xsl:if>
				    <xsl:if test="@height"> 
				    	<xsl:text>height:</xsl:text>
				    	<xsl:value-of select="@height"></xsl:value-of>
				    	<xsl:text>;</xsl:text>
				    </xsl:if>
				    <xsl:if test="@scale"> 
				    	<xsl:text>height:</xsl:text>
				    	<xsl:value-of select="@scale"></xsl:value-of>
				    	<xsl:text>%;</xsl:text>
				    </xsl:if>
			     </xsl:attribute>
			  </div>
        </xsl:if>   
  </xsl:template>
  
 	<xsl:template match="olist"> 	   
 	   <xsl:variable name="listcount" select="count(ancestor::olist)" /> 
 	   
 	   <xsl:choose> 
 	      <xsl:when test="$listcount = 0">
 	         <ol class="ol_level0">
 	         	<xsl:apply-templates/>
 	         </ol>
 	      </xsl:when>  
 	      <xsl:when test="$listcount = 1">
 	         <ol class="ol_level1">
 	         	<xsl:apply-templates/>
 	         </ol>
 	      </xsl:when>	      
 	      <xsl:otherwise>
 	         <ol class="ol_level2">
 	         	<xsl:apply-templates/>
 	         </ol>
 	      </xsl:otherwise>
 	   </xsl:choose>
 	</xsl:template>
 	
 	<xsl:template match="olist/listitem">
 	    <li  class="sm-text">
 		 <xsl:apply-templates/> 
 		</li>
 	</xsl:template> 	
 
  <xsl:template match="td/ulist | ulist">
    
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
		    	<xsl:apply-templates select="child::node()[not(self::label)]"></xsl:apply-templates>
		    </ul>
    	</xsl:when>
    	<xsl:otherwise>
    	    <ul style="margin-top: {$margin-top}; margin-bottom: 0;">
		    	<xsl:apply-templates select="child::node()[not(self::label)]"></xsl:apply-templates>
		    </ul>
    	</xsl:otherwise>
    </xsl:choose>  
   </xsl:template>
   
  <xsl:template match="td/ulist/label | ulist/label | olist/label">
     <xsl:apply-templates />
  </xsl:template>
  
   <xsl:template match="td/ulist/ulist | ulist/ulist">
       <li class="sm-text">
          <xsl:apply-templates select="label"/>
      </li>
      <ul>
       <xsl:apply-templates select="child::node()[not(self::label)]">
       </xsl:apply-templates>
      </ul>
   
   </xsl:template>
  
  <xsl:template match="td/ulist/listitem | ulist/listitem">     
      <li class="sm-text">
         <xsl:apply-templates />
      </li>     
  </xsl:template>


<!-- ********************** Table ****************************** -->
 <xsl:template match="table">  
       <table>
    		<xsl:if test="not(@tabstyle='header')">  
    		   <xsl:attribute name="class">
    		   	<xsl:text>supplementTable</xsl:text> 
    		   </xsl:attribute> 			
    		</xsl:if>		    		
	    	<xsl:attribute name="border">
	    		<xsl:choose>
		    		<xsl:when test="@frame='none'">  
		    		   <xsl:text>0</xsl:text>  			
		    		</xsl:when>
		    		<xsl:otherwise>
		    			<xsl:text>1</xsl:text>
		    		</xsl:otherwise>
		    	</xsl:choose>
	    	</xsl:attribute>
	    	<xsl:attribute name="style">
	    	      <xsl:choose>
	    	      	<xsl:when test="@tabstyle='header'"> 
	    	      	   <xsl:text>width:auto !important; margin:0 auto;</xsl:text>
	    	      	</xsl:when>
	    	      	<xsl:otherwise>
	    	      		<xsl:text>width:auto !important;</xsl:text>
	    	      	</xsl:otherwise>
	    	      </xsl:choose>
	    	</xsl:attribute>
           <xsl:if test="not(thead) and @colwidth">
	       		<xsl:call-template name="SET_COL_WIDTH">
					<xsl:with-param name="string" select="normalize-space(translate(@colwidth, ',', ' '))"/>
				</xsl:call-template>
	       </xsl:if>
      	 <xsl:apply-templates/>
       </table>
  </xsl:template>

    <xsl:template match="tbody">
        <xsl:choose>
    	<xsl:when test="../@tabstyle='header'">
    		<tbody class="rubbody">
    			  <xsl:apply-templates/>
    		</tbody>
    	</xsl:when>
    	<xsl:otherwise>
	      <tbody>
	      	<xsl:apply-templates/>
	      </tbody>
    	</xsl:otherwise>
   	 </xsl:choose>
    </xsl:template>

   
  <xsl:template match="thead">
    <xsl:choose>
    	<xsl:when test="../@tabstyle='header'">
    		<thead class="rubhead">
    			  <xsl:apply-templates/>
    		</thead>
    	</xsl:when>
    	<xsl:otherwise>
    	    <thead>
		    	<xsl:apply-templates/>
		    </thead>
    	</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

<xsl:template match="thead/tr">
  <tr valign="top">
  <xsl:apply-templates /> 
  </tr>
</xsl:template>
  
<xsl:template match="thead//td">
  <th>
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
  </th>
</xsl:template>

<xsl:template match="tr">    
	  <tr>
	  	<xsl:apply-templates/>
	  </tr>
</xsl:template>
 
<xsl:template match="tbody/tr/td">
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
  	<xsl:apply-templates/>
  </td>
</xsl:template> 
</xsl:stylesheet>