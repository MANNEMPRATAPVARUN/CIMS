<?xml version="1.0" encoding="iso-8859-1" ?>

<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ora="http://www.oracle.com/XSL/Transform/java"
	version="1.0" >

<xsl:output 
	encoding="utf-8"
	method="xml" />
	
	<!-- ********************************************** -->
	<!-- *******  Common Template ********************** -->
	<xsl:template match="*">
	  <xsl:text>&#xA;</xsl:text>
	  <xsl:text>&#xA;</xsl:text>
	  <xsl:text>&lt;*** UNHANDLED ELEMENT: </xsl:text>
	  <xsl:value-of select="name()"/>
	  <xsl:text> ***&gt;</xsl:text>
	  <xsl:text>&#xA;</xsl:text>
	  <xsl:apply-templates/>
	  <xsl:text>&#xA;</xsl:text>
	</xsl:template>
	
	<xsl:template name="UNHANDLED">
	  <xsl:param name="unhandled" select="name()" />
	  <xsl:text>&#xA;</xsl:text>
	  <xsl:text>&#xA;</xsl:text>
	  <xsl:text>&lt;*** UNHANDLED ELEMENT: </xsl:text>
	  <xsl:value-of select="$unhandled"/>
	  <xsl:text> ***&gt;</xsl:text>
	  <xsl:text>&#xA;</xsl:text>
	  <xsl:apply-templates/>
	  <xsl:text>&#xA;</xsl:text>
	</xsl:template>
	
	<!-- ***************************************** -->
	<!-- String "functions" -->
	<!-- convert a string to lowercase -->
	<xsl:template name="LOWERCASE">
	  <xsl:param name="string" />
	  <xsl:value-of select="translate($string,
				'ABCDEFGHIJKLMNOPQRSTUVWXYZ',
				'abcdefghijklmnopqrstuvwxyz')" />
	</xsl:template>
	
	<!-- convert a string to uppercase -->
	<xsl:template name="UPPERCASE">
	  <xsl:param name="string" />
	  <xsl:value-of select="translate($string,
				'abcdefghijklmnopqrstuvwxyz',
				'ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
	</xsl:template>
	

	<!-- ********************************** -->
	<!-- ********* para ****************** -->
	<xsl:template match="para">
	   <xsl:choose>
	     <xsl:when test="following-sibling::*[position()=1][name()='graphic']  and not(following-sibling::*[position()=1][name()='graphic'][@mode='below'])">
			  <p class="sm-text">
			     <xsl:attribute name="align">
				     <xsl:call-template name="GET_GRAPHIC_ALIGN">
						<xsl:with-param name="string" select="following-sibling::*[position()=1][name()='graphic']/@align"/>
					 </xsl:call-template>		        
			     </xsl:attribute>
			 	<xsl:apply-templates/>
			  </p>
		  </xsl:when>
		  <xsl:otherwise>
			   <p class="sm-text">
			 	<xsl:apply-templates/>
			  </p>
		  </xsl:otherwise>
	  </xsl:choose>
	</xsl:template>
	
	<!-- ********************************** -->
	<!-- ********* quote ****************** -->
	<xsl:template match="quote">
	  <p class="sm-text-indent">
	    <i>
	 	<xsl:apply-templates/>
	 	</i> 
	  </p>
	</xsl:template>
	
	
	<!-- ******************************************** -->
	<!-- ************ ADDRESS *********************** -->
	<xsl:template match="address">
		<xsl:text>
		</xsl:text>
		<div class="sm-text-indent">
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	
	<xsl:template match="orgname | street | pob | prov | country | phone | fax | email | web">
		<div>
		<xsl:apply-templates/>
		</div>
	</xsl:template>
	
	<xsl:template match="city | postcode">	
			<xsl:apply-templates/><xsl:text> </xsl:text>		 
	</xsl:template>
	
	<!-- ********************************** -->
	<!-- ********* FOOTNOTE LIST*********** -->
	<!--  TODO: -->
	<xsl:template match="fnlist">	 
		<xsl:apply-templates/>	 
	</xsl:template>
	
	<xsl:template match="footnote">
	  <p class="tbl-note">
	    <i>
	 	<xsl:apply-templates/>
	 	</i> 
	  </p>
	</xsl:template>
	
	<!-- ********************************** -->
	<!-- ********* SEPARATOR ************** -->
	<!-- 03/13 AV: Added symbol t`o insert a asterisks as separator marks between clauses, sub-clauses, etc.  -->
	<!--  TODO: -->
	<xsl:template match="symbol">	 
		<xsl:apply-templates/>	 
	</xsl:template>
	
	<!-- ********************************************** -->
	<!-- *************** label ************************ -->
	 
	 <!-- "brace/label" handled in main "brace" template -->
	<xsl:template match="brace/label"><xsl:if test="phrase"><xsl:apply-templates/></xsl:if></xsl:template>

	 
	<!-- ********************************** -->
	<!-- ********* links ****************** -->
	<!-- <xsl:template match="xref">
	  <a>
		<xsl:attribute name="href">
		    <xsl:text>#</xsl:text><xsl:value-of select="@refid" />
		</xsl:attribute>
	 	 <xsl:apply-templates/>
	  </a>   
	</xsl:template>
	-->
	<xsl:template match="xref">
	    <xsl:variable name="refid"><xsl:value-of select="@refid"/></xsl:variable>
	  	<xsl:choose>
	  		<xsl:when test="translate($refid, ' ', '') != ''"> 
		  		<a>
					<xsl:attribute name="href">
					    <xsl:text>#</xsl:text><xsl:value-of select="@refid" />
					</xsl:attribute>
			   		 <xsl:apply-templates/>
			    </a>
	  		</xsl:when>
	  		<xsl:otherwise>
	  			<xsl:apply-templates/>
	  		</xsl:otherwise>
	  	</xsl:choose>     
   </xsl:template>
	
	<!-- <pageref refid="ns277"/> -->
	<xsl:template match="pageref">
	  <xsl:variable name="refid" select="@refid"/>
	  
	  <xsl:text>page </xsl:text>
	  <xsl:value-of select="translate($refid,
	  			'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz',
	  			'')" />
	</xsl:template>
	
	 
	<!-- ********************************** -->
	<!-- ********* phrase formatting ****** -->
	<xsl:template match="phrase[@format='none']">
	   <xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="phrase[@format='bold']">
	  <b><xsl:apply-templates/></b>
	</xsl:template>
	 <xsl:template match="clause/phrase[@format='bold']">
	      <b><xsl:apply-templates/></b>
	</xsl:template>
	
	<xsl:template match="phrase[@format='ital']">
	  <i><xsl:apply-templates/></i>
	</xsl:template>
	
	<xsl:template match="phrase[@format='boldital']">
	  <b><i><xsl:apply-templates/></i></b>
	</xsl:template>
	
	<xsl:template match="phrase[@format='under']">
	  <u><xsl:apply-templates/></u>
	</xsl:template>
	
	<xsl:template match="phrase[@format='super']">
	  <sup><xsl:apply-templates/></sup>
	</xsl:template>
	
	<xsl:template match="phrase[@format='sub']">
	  <sub><xsl:apply-templates/></sub>
	</xsl:template>
	
	<xsl:template match="phrase[@format='linebrk']">
	  <xsl:text>&#x0A;</xsl:text>
	</xsl:template>
	
	<xsl:template match="phrase[@format='title']">
	  <div class="title"><xsl:apply-templates/></div>
	</xsl:template>
	
	<xsl:template match="phrase[@format='emblem']">
	<xsl:variable name="text" select="."/>
		<xsl:choose>
			<xsl:when test="$text = 'o'">
			  <img align="texttop" src="img/icd/cleaf.gif" height="15" width="15"/>
			  </xsl:when>
			  <xsl:otherwise>
			    <xsl:apply-templates/>
			  </xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	<!-- ********************************** -->
	<!-- ********* BRACE STRUCTURES ******* -->
	<xsl:template match="brace">
	
	  <xsl:variable name="label">
		  <xsl:choose>
				<xsl:when test="label/phrase">			    			
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="label"/>
				</xsl:otherwise>
		  </xsl:choose>	  
	  </xsl:variable>
	  
	  <!-- CV - will output non-breaking spaces correctly,
	            but doesn't help with the autosizing
	  -->
	  <xsl:variable name="label-TEST">
	    <xsl:choose>
	     <xsl:when test="label">
	      <xsl:value-of select="label"/>
	     </xsl:when>
	     <xsl:otherwise>
	     <!-- If no label, then fill with lots of non-breaking
	          spaces in for autosizing to work correctly
	          on initial cells containing lists with short items
	          -->
	       <xsl:text disable-output-escaping="yes"></xsl:text>
	     </xsl:otherwise>
	    </xsl:choose>
	  </xsl:variable>
	  
	  <!-- If there is a "label", it must span the table
	       - number of columns = (count(segment) * 2) - 1
	  -->
	  <xsl:variable name="label_span" select="(count(segment)*2)-1"/>
	  
	  <table width="100%">
	   
	   <tbody class="sm-text">
	   
	   <xsl:if test="$label">
	    <tr>
	    <td colspan="{$label_span}"><xsl:value-of select="$label"/></td>
	    </tr>
	   </xsl:if>
	   <tr>
	   <xsl:apply-templates/>
	   </tr>
	  
	   </tbody>
	   
	  </table>
	</xsl:template>
	
	
	
	<xsl:template match="brace//segment">
	  
	  <!-- If brace image size is specified, then use it 
	       - sizes range from [BRAC.1|BRAC.2|...|BRAC.25]
	  -->
	  <xsl:variable name="brace_size">
	    <!-- Allowable sizes are [01|02|...|25] -->
	    <xsl:choose>
	     <xsl:when test="@size != ''">
	      <xsl:value-of select="@size"/>
	     </xsl:when>
	     <xsl:otherwise>
	      <!-- Arbitrary default value when size not specified
	           (using "05" as a reasonable setting) -->
	      <!-- <xsl:text>BRAC.5</xsl:text> -->
	      <xsl:text>05</xsl:text>
	     </xsl:otherwise>
	    </xsl:choose>
	  </xsl:variable>
	  
	  <xsl:variable name="brace_direction">
	    <xsl:choose>
	     <xsl:when test="@bracket != ''">
	      <xsl:value-of select="@bracket"/>
	     </xsl:when>
	     <xsl:otherwise>
	      <!-- Values (right|left) - facing "right" is default -->
	      <xsl:text>right</xsl:text>
	     </xsl:otherwise>
	    </xsl:choose>
	  </xsl:variable>
	
	  <!-- If first segment, limit it's HTML display width -->
	  <xsl:variable name="width">
	   <xsl:choose>
	    <xsl:when test="not(preceding-sibling::segment)">
	     <xsl:text>25%</xsl:text>
	    </xsl:when>
	    <xsl:otherwise>
	     <xsl:text></xsl:text>
	    </xsl:otherwise>
	   </xsl:choose>
	  </xsl:variable>
	
		<xsl:choose>
			<xsl:when test="$width = ''">
				<td wrappable="true"><xsl:apply-templates/></td>
			</xsl:when>
			<xsl:otherwise>
				<td width="{$width}" nowrap="true"><xsl:apply-templates/></td>
			</xsl:otherwise>
		</xsl:choose>
	 
	  
	  
	  <!-- If another "segment" appears after this one, 
	       then must display a BRACE image
	  -->
	  <xsl:if test="following-sibling::segment">
	   <td>
	     <xsl:call-template name="BRACE_GRAPHIC">
	      <xsl:with-param name="brace_size" select="$brace_size"/>
	      <xsl:with-param name="brace_direction" select="$brace_direction"/>
	     </xsl:call-template>
	   </td>
	  </xsl:if>
	  
	  
	</xsl:template>
	
	<xsl:template match="brace//item">
	  <xsl:apply-templates/>
	  
	  <xsl:if test="count(*[(self::ulist)]) = 0">
	  	<br/>
	  </xsl:if>
	</xsl:template>
	
	<xsl:template name="BRACE_GRAPHIC">
	  
	  <xsl:param name="brace_size"/>
	  <xsl:param name="brace_direction"/>
	  
	  <!-- NOTE: - allowable brace size is [01|02|...|25],
	               but not bothering to check
	             - image name should be "brack_{$brace_size}.gif"
	  -->
	  <xsl:variable name="image">
	    <xsl:text>bracket_</xsl:text>
	    <xsl:value-of select="$brace_size"/>
	    <xsl:if test="$brace_direction = 'left'"> 
	    	<xsl:text>_left</xsl:text>
	    </xsl:if>
	    <xsl:text>.gif</xsl:text>
	  </xsl:variable>
	  
	  <img src="img/icd/{$image}" alt="{$image}"/>
	     
	</xsl:template>
	
	
	<!-- copy div, hyperlink, br and img  -->
	
	<xsl:template match="div">
	      <xsl:copy>
	          <xsl:apply-templates select="@*"/>
	          <xsl:apply-templates select="node()"/>
	      </xsl:copy>
	</xsl:template> 
	 <xsl:template match="img">
	      <xsl:copy>
	          <xsl:apply-templates select="@*"/>
	          <xsl:apply-templates select="node()"/>
	      </xsl:copy>
	 </xsl:template> 
	 <xsl:template match="a">
	  	<a><xsl:copy-of select="node() | @*"/>	</a>
	</xsl:template>
	
	<xsl:template match="br">
		<br/>
	</xsl:template>
	   
	
	<!-- ****************************************************** -->
	<!-- ******************Common templates ******************* -->
		<xsl:template name="GET_GRAPHIC_ALIGN">
	 <xsl:param name="string"/>
	 <xsl:choose>
	  <xsl:when test="$string='aleft'">
	     	    <xsl:text>left</xsl:text>
	     	</xsl:when>
	     	<xsl:when test="$string='aright'">
	      	    <xsl:text>right</xsl:text>
	      	</xsl:when>
	      	<xsl:when test="$string='acenter'">
	      	    <xsl:text>center</xsl:text>
	      	</xsl:when>
	      	<xsl:otherwise>
	      	    <xsl:text>center</xsl:text>
	      	</xsl:otherwise>
	  </xsl:choose>
	</xsl:template> 
	
	<xsl:template name="SET_COL_WIDTH">
	  <xsl:param name="string"/>
	  <xsl:choose>
		   <xsl:when test="contains($string,' ')">	
		   			<xsl:variable name="aString" select="normalize-space(substring-before($string, ' ') )"/> 
		   			<xsl:variable name="pixelValue">
		   				<xsl:call-template name="GET_PIXEL_VALUE">
				    	      <xsl:with-param name="string" select="$aString"/>
			             </xsl:call-template>
		   			</xsl:variable>		           		   
					<col><xsl:attribute name="style"><xsl:text>width:</xsl:text><xsl:value-of select="$pixelValue"/><xsl:text>;</xsl:text></xsl:attribute> </col>
					<xsl:call-template name="SET_COL_WIDTH">
							<xsl:with-param name="string" select="substring-after($string, ' ')"/>
					</xsl:call-template>
		   </xsl:when>
		   <xsl:otherwise>
					<col><xsl:attribute name="style"><xsl:text>width:</xsl:text>
					      <xsl:call-template name="GET_PIXEL_VALUE">
				    	      <xsl:with-param name="string" select="$string"/>
			             </xsl:call-template>
			             <xsl:text>;</xsl:text></xsl:attribute>
			        </col>
		   </xsl:otherwise>
	  </xsl:choose>
	</xsl:template>	
	
	<xsl:template name="GET_TD_WIDTH">
	    <xsl:param name="position"/>
	    <xsl:param name="widthString"/>
	    <xsl:param name="pDelim" select="' '"/>
	    
	    <xsl:choose>
	    	<xsl:when test ="$position=1">
	    	    <xsl:choose>
	    	    	<xsl:when test="contains($widthString, ' ')">
	    	    		<xsl:value-of select="normalize-space(substring-before($widthString, $pDelim))"></xsl:value-of>
	    	    	</xsl:when>
	    	    	<xsl:otherwise>
	    	    	    <xsl:value-of select="$widthString"/>
	    	    	</xsl:otherwise>
	    	    </xsl:choose>
	    	</xsl:when>
	    	<xsl:otherwise>
	    	    <xsl:variable name="tmpPosition" select="$position"/>
	    	    <xsl:variable name="tmpString" select="$widthString"/>
	    	    <xsl:call-template name="GET_TD_WIDTH">
	    	    	    <xsl:with-param name="position" select="$tmpPosition - 1"/>
	    				<xsl:with-param name="widthString" select="normalize-space(substring-after($tmpString, $pDelim))"/>
	    				<xsl:with-param name="pDelim" select="$pDelim"/>
			    </xsl:call-template>
	    	</xsl:otherwise>
	    </xsl:choose>
	    
	</xsl:template>
	
	<xsl:template name="GET_PIXEL_VALUE">
      <xsl:param name="string"/>
      
      <xsl:variable name="scaling-factor" >   
	      <xsl:choose>
	        <xsl:when test="contains ($string, 'pt')">1.33</xsl:when>
	        <xsl:when test="contains ($string, 'px')">1</xsl:when>
	        <xsl:when test="contains ($string, 'in')">96</xsl:when>
	        <xsl:when test="contains ($string, 'cm')">37.8</xsl:when>
	        <xsl:when test="contains ($string, 'mm')">3.78</xsl:when>
	        <xsl:when test="contains ($string, 'em')">16</xsl:when>
	        <xsl:otherwise>1</xsl:otherwise>
	      </xsl:choose> 
      </xsl:variable> 
      <xsl:variable name="numeric-value" select="number(translate($string, '-0123456789.ptxcinme', '-0123456789.'))"/>
	  <xsl:value-of select="$numeric-value * $scaling-factor"/><xsl:text>px</xsl:text>        
   </xsl:template> 
 
	 	
	<xsl:template name="GET_TEXT_ALIGN">
	   <xsl:param name="align"/>
	   <xsl:choose>
		   <xsl:when test="$align = 'left'">
		   	    <xsl:text>text-align: left;</xsl:text>
		   </xsl:when>
		   <xsl:when test="$align = 'right'">
		   	    <xsl:text>text-align: right;</xsl:text>
		   </xsl:when>
		   <xsl:when test="$align = 'middle'">
		   	    <xsl:text>text-align: center;</xsl:text>
		   </xsl:when>
		   <xsl:otherwise>
		   		<xsl:text>text-align: left;</xsl:text>
		   </xsl:otherwise>
	  </xsl:choose>
	</xsl:template>
	
	<xsl:template name="GET_TEXT_VALIGN">
	   <xsl:param name="valign"/>
	   <xsl:choose>
		   <xsl:when test="$valign = 'top'">
		   	    <xsl:text>vertical-align: text-top;</xsl:text>
		   </xsl:when>
		   <xsl:when test="$valign = 'center' or $valign='centre'">
		   	    <xsl:text>vertical-align: middle;</xsl:text>
		   </xsl:when>
		   <xsl:when test="$valign = 'bottom'">
		   	    <xsl:text>vertical-align: text-bottom;</xsl:text>
		   </xsl:when>
		   <xsl:otherwise>
		   		<xsl:text>vertical-align: text-top;</xsl:text>
		   </xsl:otherwise>
	  </xsl:choose>
	</xsl:template>
</xsl:stylesheet>
  