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
  <xsl:template match="table[@type='portrait']">
    <table style="padding-left: 3em; padding-bottom: 1em; border-collapse: collapse;">
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </table>
  </xsl:template>
  <xsl:template match="table[@type!='portrait']">
    <table style="border-collapse: collapse;">
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </table>
  </xsl:template>
  <xsl:template match="table">
    <table style="padding-left: 3em; padding-bottom: 1em; border-collapse: collapse;">
      <xsl:apply-templates/>
    </table>
  </xsl:template>
  <xsl:template match="th">
    <th>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </th>
  </xsl:template>
  <xsl:template match="tbody">
    <tbody>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </tbody>
  </xsl:template>
  <xsl:template match="tr">
    <tr>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </tr>
  </xsl:template>
  <xsl:template match="td">
    <td>
      <xsl:copy-of select="@*"/>
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