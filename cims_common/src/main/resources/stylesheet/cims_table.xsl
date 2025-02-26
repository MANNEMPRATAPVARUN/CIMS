<?xml version="1.0" encoding="iso-8859-1" ?>

<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ora="http://www.oracle.com/XSL/Transform/java"
	version="1.0" >

<xsl:output 
	encoding="utf-8"
	method="xml" />
	
	 <xsl:template match="qualifierlist/table | qualifierlist//table ">
	         <table class="noteTable" style="width:auto !important;">
	              <xsl:attribute name="style">
					<xsl:call-template name="GET_TABLE_STYLE">
						<xsl:with-param name="frame" select="@frame"/>
					 </xsl:call-template>
				  </xsl:attribute>
				  
				   <xsl:if test="not(thead) and @colwidth">
			       		<xsl:call-template name="SET_COL_WIDTH">
							<xsl:with-param name="string" select="normalize-space(translate(@colwidth, ',', ' '))"/>
						</xsl:call-template>
			       </xsl:if>	
				 <xsl:apply-templates/>
		    </table>       
	 </xsl:template>
	
	<xsl:template match="qualifierlist/table/tbody | qualifierlist//table/tbody">
	  <tbody>
	  	<xsl:apply-templates/>
	  </tbody>
	</xsl:template>
	
	<xsl:template match="qualifierlist/table/tfoot | qualifierlist//table/tfoot">
	  <tfoot>
	  	<xsl:apply-templates/>
	  </tfoot>
	</xsl:template>
	   
	<xsl:template match="qualifierlist/table/thead | qualifierlist//table/thead">
	  <thead>
	  	<xsl:apply-templates/>
	  </thead>
	</xsl:template>
	
	<xsl:template match="qualifierlist/table//tr | qualifierlist//table//tr">
	  <tr><xsl:apply-templates /></tr>
	</xsl:template>
		  
	<xsl:template match="qualifierlist/table/thead/tr/td | qualifierlist//table/thead/tr/td">
	   <xsl:variable name="rowsep">
	   		<xsl:choose>
	   			<xsl:when test="@rowsep">
	   				<xsl:value-of select="@rowsep"/>
	   			</xsl:when>
	   			<xsl:when test="not(@rowsep) and ../@rowsep"> 
	   				<xsl:value-of select="../@rowsep"/>
	   			</xsl:when>
	   			<xsl:otherwise>
	   			    <xsl:choose>
	   			    	<xsl:when test="ancestor::table[@frame='none']">
	   			    		<xsl:text>0</xsl:text>
	   			    	</xsl:when>
	   			    	<xsl:otherwise>
	   						<xsl:text>1</xsl:text>
	   					</xsl:otherwise>
	   				</xsl:choose>
	   			</xsl:otherwise>
	   		</xsl:choose>
	   </xsl:variable>
	   <xsl:variable name="colsep">
	   	   <xsl:choose>
	   	   	  	<xsl:when test="@colsep">
	   				<xsl:value-of select="@colsep"/>
	   			</xsl:when>
	   			<xsl:otherwise>
	   				<xsl:choose>
	   			    	<xsl:when test="ancestor::table[@frame='none']">
	   			    		<xsl:text>0</xsl:text>
	   			    	</xsl:when>
	   			    	<xsl:otherwise>
	   						<xsl:text>1</xsl:text>
	   					</xsl:otherwise>
	   				</xsl:choose>
	   			</xsl:otherwise>
	   	   </xsl:choose>
	   </xsl:variable>
	   
	  <th class="codeText">
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
		         <xsl:call-template name="GET_ROW_SEP">
		        	<xsl:with-param name="rowsep"> 
		        		<xsl:value-of select="$rowsep"/>
		        	</xsl:with-param>
		        </xsl:call-template>		        
		        <xsl:if test="following-sibling::td">
			        <xsl:call-template name="GET_COL_SEP">
			        	<xsl:with-param name="colsep"> 
			        		<xsl:value-of select="$colsep"/>
			        	</xsl:with-param>
			        </xsl:call-template>
		        </xsl:if>
		    </xsl:attribute>
	  		<xsl:apply-templates />
	  </th>
	</xsl:template>
	 
	<xsl:template match="qualifierlist/table/tbody/tr/td | qualifierlist//table/tbody/tr/td">
	   <xsl:variable name="rowsep">
	   		<xsl:choose>
	   			<xsl:when test="@rowsep">
	   				<xsl:value-of select="@rowsep"/>
	   			</xsl:when>
	   			<xsl:when test="not(@rowsep) and (../@rowsep)"> 
	   				<xsl:value-of select="../@rowsep"/>
	   			</xsl:when>
	   			<xsl:otherwise>
	   				<xsl:choose>
	   			    	<xsl:when test="ancestor::table[@frame='none']">
	   			    		<xsl:text>0</xsl:text>
	   			    	</xsl:when>
	   			    	<xsl:otherwise>
	   						<xsl:text>1</xsl:text>
	   					</xsl:otherwise>
	   				</xsl:choose>
	   			</xsl:otherwise>
	   		</xsl:choose>
	   </xsl:variable>
	   <xsl:variable name="colsep">
	   	   <xsl:choose>
	   	   	  	<xsl:when test="@colsep">
	   				<xsl:value-of select="@colsep"/>
	   			</xsl:when>
	   			<xsl:otherwise>
	   				<xsl:choose>
	   			    	<xsl:when test="ancestor::table[@frame='none']">
	   			    		<xsl:text>0</xsl:text>
	   			    	</xsl:when>
	   			    	<xsl:otherwise>
	   						<xsl:text>1</xsl:text>
	   					</xsl:otherwise>
	   				</xsl:choose>
	   			</xsl:otherwise>
	   	   </xsl:choose>
	   </xsl:variable>
	  <td class="codeText">	 
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
		        <xsl:if test="../following-sibling::*"> 
			        <xsl:call-template name="GET_ROW_SEP">
			        	<xsl:with-param name="rowsep"> 
			        		<xsl:value-of select="$rowsep"/>
			        	</xsl:with-param>
			        </xsl:call-template>
		         </xsl:if>
		        <xsl:if test="following-sibling::td">
			        <xsl:call-template name="GET_COL_SEP">
			        	<xsl:with-param name="colsep"> 
			        		<xsl:value-of select="$colsep"/>
			        	</xsl:with-param>
			        </xsl:call-template>
		        </xsl:if>
		    </xsl:attribute>
	  		<xsl:apply-templates/>
	  </td>
	</xsl:template>
	
	<!-- ************************************************** -->	
	<xsl:template name="GET_TABLE_STYLE">
	  <xsl:param name="frame"/>
	  <xsl:choose>
	   <xsl:when test="$frame = 'top'">
	   	    <xsl:text>border-top:thin solid;</xsl:text>
	   </xsl:when>
	   <xsl:when test="$frame = 'bottom'">
	   	    <xsl:text>border-bottom:thin solid;</xsl:text>
	   </xsl:when>
	   <xsl:when test="$frame = 'topbot'">
	   	    <xsl:text>border-top:thin solid;border-bottom:thin solid;</xsl:text>
	   </xsl:when>
	   <xsl:when test="$frame = 'all'">
	   	    <xsl:text>border:1px solid black;</xsl:text>
	   </xsl:when>
	   <xsl:when test="$frame = 'sides'">
	   	    <xsl:text>border-left:thin solid;border-right:thin solid;</xsl:text>
	   </xsl:when>
	   <xsl:when test="$frame = 'none'">
	   	    <xsl:text>border:0;</xsl:text>
	   </xsl:when>
	   <xsl:otherwise>
	   	 <xsl:text>border:1px solid;</xsl:text>
	   </xsl:otherwise>
	  </xsl:choose>
	</xsl:template>
	
	<xsl:template name="GET_ROW_SEP">
	   <xsl:param name="rowsep"/>
	   <xsl:choose>
		   <xsl:when test="$rowsep = '0'">
		   	    <xsl:text>border-bottom:0;</xsl:text>
		   </xsl:when>
		   <xsl:otherwise>
		   	 	<xsl:text>border-bottom:thin solid;</xsl:text>
		   </xsl:otherwise>
	  </xsl:choose>
	</xsl:template>
	
	<xsl:template name="GET_COL_SEP">
	   <xsl:param name="colsep"/>
	   <xsl:choose>
		   <xsl:when test="$colsep = '0'">
		   	    <xsl:text>border-right:0;</xsl:text>
		   </xsl:when>
		   <xsl:otherwise>
		   	 	<xsl:text>border-right:thin solid;</xsl:text>
		   </xsl:otherwise>
	  </xsl:choose>
	</xsl:template>	

</xsl:stylesheet>