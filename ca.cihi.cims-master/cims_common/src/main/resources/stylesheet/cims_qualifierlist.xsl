<?xml version="1.0" encoding="iso-8859-1" ?>

<!-- XSL style sheet for ICD-10-CA tabular data XML files -->

<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" >

<xsl:output 
	encoding="utf-8"
	method="xml" />
	
<xsl:include href="cims_common.xsl"/> 	   
   
<xsl:strip-space elements="*" />
 
<xsl:template match="/"> 	 
    <xsl:apply-templates/> 
</xsl:template> 



<!-- ************************************************** -->
<!-- Flow-through -->

<xsl:template match="concept">
  <xsl:apply-templates/>
</xsl:template> 
  

<!-- ***************************** label ************************** -->
  <xsl:template match="exclude/label|include/label|note/label|also/label|omit/label|definition/label|Include/label|Exclude/label|Definition/label|Omit/label|Note/label|Also/label">
    <xsl:apply-templates/><br/>
  </xsl:template>

<!-- *** End Spanning cells *** -->

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
  <xsl:template match="qualifierlist[@type='includes'] | qualifierlist[@type='excludes'] | qualifierlist[@type='note'] | qualifierlist[@type='definition'] | qualifierlist[@type='also'] | qualifierlist[@type='omit']">
	  <xsl:apply-templates/>
  </xsl:template>  
  
<!-- *** End of qualifierlist @types *** -->

   <xsl:template match="
        qualifierlist/exclude | qualifierlist/include | qualifierlist/note | qualifierlist/also | qualifierlist/definition | qualifierlist/omit">   
    <xsl:choose>  
	    <xsl:when test="child::ulist">
	      <xsl:apply-templates/>
	    </xsl:when>
	    <xsl:otherwise>
	    <!--    CV - January 2005
	               - not wrapping items in <ul>...</ul> anymore
	                 (introduces extraneous bullets in HTML view 
	                  which should be introduced by explicit
	                  <ulist>...</ulist> only)
	               - must include trailing <br/> for proper line breaks 
	                 between items
	    -->
	      <!-- <li><xsl:apply-templates/></li> -->
	      <!-- <li style="list-style-type: none"><xsl:apply-templates/></li> -->
	      <xsl:apply-templates/>
	    </xsl:otherwise>
    </xsl:choose>
  
  </xsl:template>
   
  <xsl:include href="cims_table.xsl"/>
  
</xsl:stylesheet>


