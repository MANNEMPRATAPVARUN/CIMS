<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />
        </xsl:copy>
    </xsl:template>
    <xsl:template match="REFERENCE_LIST/INDEX_REF_LIST">
        <xsl:copy>
            <xsl:apply-templates>
                <xsl:sort select="REFERENCE_LINK_DESC"  order="ascending"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="*/text()[not(normalize-space())]" />
</xsl:stylesheet>