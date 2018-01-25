<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
<protocol>
  <xsl:apply-templates select="object-stream/state" />
</protocol>
</xsl:template>

<xsl:template match="state">
  <room>
    <data class="memento">
      <xsl:copy-of select="." />
    </data>
  </room>
</xsl:template>
</xsl:stylesheet>
