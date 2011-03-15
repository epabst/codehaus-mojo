<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml">
  
  <xsl:output
    method="html"
    encoding="UTF-8"
    standalone="yes"
    indent="false" omit-xml-declaration="true"  />
  
  <xsl:param name="revisionTitle"  />
  
  <xsl:template match="/log">
    <xsl:element name="log">
      <xsl:attribute name="title">
        SVN ChangeLog <xsl:value-of select="$revisionTitle"/>
      </xsl:attribute>
      <xsl:copy-of select="/log/logentry"/>      
    </xsl:element>
  </xsl:template>
  
  
  
</xsl:stylesheet>
