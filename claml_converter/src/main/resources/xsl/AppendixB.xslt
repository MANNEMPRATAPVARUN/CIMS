<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml"/>
  
  <!-- Receives the id of the menu being rendered. -->
  <xsl:param name="images-path" />

  <xsl:strip-space elements="*"/>
  <xsl:template match="temp-container">
      <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="section">
      <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="section/label">
    <h1><xsl:value-of select="."/></h1>
  </xsl:template>
  <xsl:template match="label">
    <span style="font-weight: bold;"><p><xsl:value-of select="."/></p></span>
  </xsl:template>
  <xsl:template match="clause">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="sub-clause">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="para">
    <p><xsl:apply-templates/></p>
  </xsl:template>
  <xsl:template match="phrase">
    <span style="font-weight: bold;">
      <xsl:value-of select="."/>
    </span> 
  </xsl:template>
  <xsl:template match="table[@class='conceptTable']">
    <br/>
    <table style="border: none">
      <xsl:apply-templates/>
    </table>
    <br/>
  </xsl:template>
  <xsl:template match="table[@style='width:auto']">
    <table style="font-size: 10px; border-collapse: collapse;">
      <xsl:apply-templates/>
    </table>
  </xsl:template>
  <xsl:template match="table">
    <table>
      <xsl:apply-templates/>
    </table>
    <br/>
    <br/>
  </xsl:template>
  <xsl:template match="th">
    <th>
      <xsl:apply-templates/>
    </th>
  </xsl:template>
  <xsl:template match="tbody">
    <tbody>
      <xsl:apply-templates/>
    </tbody>
  </xsl:template>
  <xsl:template match="tr">
    <tr>
      <xsl:apply-templates/>
    </tr>
  </xsl:template>
  
  <xsl:template match="table[@style='width:auto']/tr/td[1]">
    <td style="border: 1px solid; padding: 3px; width:120px">
      <xsl:apply-templates/>
    </td>
  </xsl:template>
  <xsl:template match="table[@style='width:auto']/tr/td[2]">
    <td style="border: 1px solid; padding: 3px; width:25px">
      <xsl:apply-templates/>
    </td>
  </xsl:template>
  <xsl:template match="table[@style='width:auto']/tr/td[position()>2]">
    <td style="border: 1px solid; padding: 3px; width:50px">
      <xsl:apply-templates/>
    </td>
  </xsl:template>
  
  <!--  th before last row -->
  <xsl:template match="table[@style='width:auto']/tr[not(position()=last())]/th[1]">
    <th style="text-align:left; padding: 3px; width:300px;">
      <xsl:apply-templates/>
    </th>
  </xsl:template>
  <xsl:template match="table[@style='width:auto']/tr[not(position()=last())]/th[2]">
    <th style="text-align:left; padding: 3px; width:300px;">
      <xsl:apply-templates/>
    </th>
  </xsl:template>
  <xsl:template match="table[@style='width:auto']/tr[not(position()=last())]/th[position()>2]">
    <th style="text-align:left; padding: 3px;">
      <xsl:apply-templates/>
    </th>
  </xsl:template>
  
  <!--  th last row -->
  <xsl:template match="table[@style='width:auto']/tr[position() = last()]">
    <th style="text-align:left;" colspan="100">
      <table style="border: 1px solid; border-collapse: collapse;">
        <xsl:apply-templates/>
      </table>
    </th>
  </xsl:template>
  <xsl:template match="table[@style='width:auto']/tr[position() = last()]/th[1]">
    <th style="border: 1px solid; text-align:left; padding: 3px; width:120px">
      <xsl:apply-templates/>
    </th>
  </xsl:template>
  <xsl:template match="table[@style='width:auto']/tr[position() = last()]/th[2]">
    <th style="border: 1px solid; text-align:left; padding: 3px; width:25px">
      <xsl:apply-templates/>
    </th>
  </xsl:template>
  <xsl:template match="table[@style='width:auto']/tr[position() = last()]/th[position()>2]">
    <th style="border:1px solid; text-align:left; padding: 3px; width:50px;">
      <xsl:apply-templates/>
    </th>
  </xsl:template>
  <!-- end th -->
  
  <xsl:template match="td">
    <td>
      <xsl:apply-templates/>
    </td>
  </xsl:template>
  
  <xsl:template match="olist">
    <ol style="list-style-type: decimal">
      <xsl:apply-templates/>
    </ol>
  </xsl:template>
  <xsl:template match="olist/olist">
    <ol style="list-style-type: lower-alpha">
      <xsl:apply-templates/>
    </ol>
  </xsl:template>
  <xsl:template match="listitem | li">
      <li><xsl:value-of select="."/></li>
  </xsl:template>
  <xsl:template match="olist/listitem">
    <li><xsl:value-of select="."/></li>
  </xsl:template>
  <xsl:template match="ulist[@mark='none']">
    <ul style="list-style-type:none;">
      <xsl:apply-templates/>
    </ul>
  </xsl:template>
  <xsl:template match="ulist[@mark!='none'] | ul">
    <ul>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </ul>
  </xsl:template>
  <xsl:template match="graphic">
    <img>
      <xsl:attribute name="src">
        <xsl:value-of select="concat($images-path, encode-for-uri(@src))"/>
      </xsl:attribute>
    </img>
  </xsl:template>
  <xsl:template match="block">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="span">
    <span>
      <xsl:copy-of select="@*"/>
      <xsl:value-of select="."/>
    </span>
  </xsl:template>
  <xsl:template match="address">
    <div style="padding-left: 3em;padding-bottom: 1em">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  <xsl:template match="orgname">
    <div><xsl:copy-of select="."/></div>
  </xsl:template>
  <xsl:template match="street">
    <div><xsl:copy-of select="."/></div>
  </xsl:template>
  <xsl:template match="city">
    <div><xsl:copy-of select="normalize-space(concat(., ' ', ../prov/.))"/></div>
  </xsl:template>
  <xsl:template match="prov">
  </xsl:template>
  <xsl:template match="postcode">
    <div><xsl:copy-of select="."/></div>
  </xsl:template>
  <xsl:template match="country">
    <div><xsl:copy-of select="."/></div>
  </xsl:template>
  <xsl:template match="quote">
    <p style="padding-left: 3em;font-style: italic;"><xsl:copy-of select="."/></p>
  </xsl:template>
  <xsl:template match="reportdetails">
    <xsl:apply-templates select="reportdetail">
      <xsl:sort select="@name"/>
    </xsl:apply-templates>
  </xsl:template>
  <xsl:template match="reportdetail | main">
    <xsl:apply-templates/>
  </xsl:template>
</xsl:stylesheet>