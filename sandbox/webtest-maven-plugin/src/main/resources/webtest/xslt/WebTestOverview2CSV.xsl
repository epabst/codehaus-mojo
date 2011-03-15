<?xml version="1.0" encoding="UTF-8"?>
<!-- 
    Transforms WebTest overview results to a CSV file
-->

<!DOCTYPE xsl:stylesheet>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="text" encoding="UTF-8" indent="yes"/>
    
    <xsl:template match="/">
        name,successful,starttime
        <xsl:apply-templates select="overview/folder"></xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="folder">
        <xsl:value-of select="@name"/>,<xsl:value-of select="summary/@successful"/>,<xsl:value-of select="summary/@starttime"/>
        <xsl:text>
        </xsl:text>
    </xsl:template>
    
</xsl:stylesheet>
