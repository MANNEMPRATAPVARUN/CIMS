<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml"/>

    <!-- Receives the id of the menu being rendered. -->
    <xsl:param name="images-path"/>

    <xsl:strip-space elements="*"/>
    <xsl:template match="temp-container">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="qualifierlist">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="chpfront">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="sub-section">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="clause">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="note">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="label">
        <p>
            <xsl:apply-templates/>
        </p>
    </xsl:template>
    <xsl:template match="xref">
        <xsl:element name="a">
            <xsl:attribute name="href">
                <xsl:text>#</xsl:text><xsl:value-of select="@refid"/>
            </xsl:attribute>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="para">
        <p>
            <xsl:apply-templates/>
        </p>
    </xsl:template>
    <xsl:template match="phrase">
        <span style="font-weight: bold;">
            <xsl:value-of select="."/>
        </span>
    </xsl:template>
    <xsl:template match="table">
        <table>
            <xsl:if test="@frame='all'">
                <xsl:attribute name="style">
                    <xsl:text>border: thin solid black;</xsl:text>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates/>
        </table>
    </xsl:template>
    <xsl:template match="th">
        <th>
            <xsl:if test="ancestor::table[1]/@frame='all'">
                <xsl:attribute name="style">
                    <xsl:text>border: thin solid;</xsl:text>
                </xsl:attribute>
            </xsl:if>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </th>
    </xsl:template>
    <xsl:template match="thead">
        <thead>
            <xsl:apply-templates/>
        </thead>
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
    <xsl:template match="td">
        <td>
            <xsl:attribute name="style">
                <xsl:if test="@colsep=1">border-right: thin solid;</xsl:if>
                <xsl:if test="ancestor::tr/@rowsep=1">border-bottom: thin solid;</xsl:if>
                <xsl:if test="@rowsep=1">border-bottom: thin solid;</xsl:if>
            </xsl:attribute>
            <xsl:copy-of select="@colspan"/>
            <xsl:apply-templates/>
        </td>
    </xsl:template>
    <xsl:template match="olist">
        <div style="text-indent:2em">
            <ol type="1">
                <xsl:apply-templates/>
            </ol>
        </div>
    </xsl:template>
    <xsl:template match="graphic">
        <img>
            <xsl:attribute name="src">
                <xsl:copy-of select="concat($images-path,'/', @*)"/>
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
    <xsl:template match="brace">
        <table>
            <tbody>
                <xsl:for-each select="label">
                    <xsl:apply-templates select="current()"/>
                </xsl:for-each>
                <tr>
                    <xsl:for-each select="segment">
                        <xsl:apply-templates select="current()"/>
                    </xsl:for-each>
                </tr>
            </tbody>
        </table>
    </xsl:template>
    <xsl:template match="brace/label">
        <tr>
            <td>
                <xsl:apply-templates/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="segment">
        <td>
            <xsl:apply-templates/>
        </td>
        <xsl:if test="@bracket">
            <td>
                <xsl:element name="img">
                    <xsl:attribute name="src">
                        <xsl:value-of select="concat($images-path,'/','bracket_',@size)"/>
                        <xsl:if test="@bracket='left'">
                            <xsl:text>_left</xsl:text>
                        </xsl:if>
                        <xsl:text>.gif</xsl:text>
                    </xsl:attribute>
                </xsl:element>
            </td>
        </xsl:if>
    </xsl:template>
    <xsl:template match="ulist">
        <ul>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>
    <xsl:template match="ulist/ulist">
        <li>
            <xsl:value-of select="./label"></xsl:value-of>
            <ul>
                <xsl:apply-templates/>
            </ul>
        </li>
    </xsl:template>
    <xsl:template match="ulist/label">
        <li style="list-style-type:none">
            <xsl:apply-templates/>
        </li>
    </xsl:template>
    <xsl:template match="ulist/ulist/label"/>
    <xsl:template match="listitem">
        <li>
            <xsl:apply-templates/>
        </li>
    </xsl:template>
    <xsl:template match="item">
        <xsl:choose>
            <xsl:when test="not(*)">
                <xsl:value-of select="."></xsl:value-of>
                <br/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>