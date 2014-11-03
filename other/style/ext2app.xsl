<?xml version="1.0"?>

<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:ex="http://application.control4j.cz/extended"
    xmlns:app="http://application.control4j.cz"
    xmlns:fn="http://www.w3.org/2005/xpath-functions">

    <xsl:output indent="yes" />

    <xsl:variable name="MNS" select="'control4j.modules.'" />

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template name="signal-declaration">
        <app:signal>
            <!-- set the name attribute of the signal -->
            <xsl:attribute name="name" >
                <xsl:value-of select="@signal" />
            </xsl:attribute>
            <!-- copy scope attribute if present -->
            <xsl:if test="@scope">
                <xsl:apply-templates select="@scope" />
            </xsl:if>
        </app:signal>
    </xsl:template>

    <!-- Const -->
    <xsl:template match="ex:const">

        <xsl:if test="fn:empty(@ex:define-signal) or (@ex:define-signal = 'yes')">
            <xsl:call-template name="signal-declaration" />
        </xsl:if>

        <app:module class="control4j.modules.OMConst">
            <app:property key="value" >
                <xsl:attribute name="value">
                    <xsl:value-of select="@value" />
                </xsl:attribute>
            </app:property>
            <app:output index="0">
                <xsl:attribute name="signal">
                    <xsl:value-of select="@signal" />
                </xsl:attribute>
                <!-- copy scope attribute if present -->
                <xsl:if test="@scope">
                    <xsl:apply-templates select="@scope" />
                </xsl:if>
            </app:output>
        </app:module>

    </xsl:template>

    <xsl:include href="ext2apppapouch.xsl" />
    <xsl:include href="ext2appio.xsl" />
    <xsl:include href="ext2appbasic.xsl" />

</xsl:stylesheet>