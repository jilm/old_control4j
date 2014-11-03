<?xml version="1.0"?>

<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:ex="http://application.control4j.cz/extended"
    xmlns:app="http://application.control4j.cz"
    xmlns:fn="http://www.w3.org/2005/xpath-functions">


    <!-- Temperature, output declared as an attribute -->
    <xsl:template match="ex:temperature[@output]">
	<!-- place module -->
        <app:module>
            <xsl:attribute name="class" 
	        select="'control4j.modules.io.OMTemperature'" />
            <xsl:call-template name="setProperty">
                <xsl:with-param name="key" select="'hardware'" />
                <xsl:with-param name="value" select="@hw" />
            </xsl:call-template>
            <xsl:call-template name="placeOutput">
                <xsl:with-param name="index" select="'0'" />
                <xsl:with-param name="signal" select="@output" />
                <xsl:with-param name="scope" select="@scope" />
            </xsl:call-template>
        </app:module>
	<!-- place output signal declaration -->
	<xsl:call-template name="placeSignalDeclaration">
	    <xsl:with-param name="signal" select="@output" />
	    <xsl:with-param name="scope" select="@scope" />
	</xsl:call-template>
    </xsl:template>


    <!-- Temperature, outputs are separate elements -->
    <xsl:template match="ex:temperature">
	<!-- place module -->
        <app:module>
            <xsl:attribute name="class" 
	        select="'control4j.modules.io.OMTemperature'" />
            <xsl:call-template name="setProperty">
                <xsl:with-param name="key" select="'hardware'" />
                <xsl:with-param name="value" select="@hw" />
            </xsl:call-template>
            <xsl:apply-templates select="app:output" />
        </app:module>
	<!-- place output signal declarations -->
        <xsl:for-each select="app:output">
	    <xsl:call-template name="placeSignalDeclaration">
	        <xsl:with-param name="signal" select="@signal" />
	        <xsl:with-param name="scope" select="@scope" />
	    </xsl:call-template>
	</xsl:for-each>
    </xsl:template>


    <xsl:template match="ex:binary-input[@output]">
        <app:module>
            <xsl:attribute name="class" 
	        select="'control4j.modules.io.OMBinaryInput'" />
            <xsl:call-template name="setProperty">
                <xsl:with-param name="key" select="'hardware'" />
                <xsl:with-param name="value" select="@hw" />
            </xsl:call-template>
            <xsl:call-template name="placeOutput">
                <xsl:with-param name="index" select="'0'" />
                <xsl:with-param name="signal" select="@output" />
                <xsl:with-param name="scope" select="@scope" />
            </xsl:call-template>
        </app:module>
	<!-- place output signal declaration -->
	<xsl:call-template name="placeSignalDeclaration">
	    <xsl:with-param name="signal" select="@output" />
	    <xsl:with-param name="scope" select="@scope" />
	</xsl:call-template>
    </xsl:template>


    <xsl:template match="ex:binary-input">
        <app:module>
            <xsl:attribute name="class" 
	        select="'control4j.modules.io.OMBinaryInput'" />
            <xsl:call-template name="setProperty">
                <xsl:with-param name="key" select="'hardware'" />
                <xsl:with-param name="value" select="@hw" />
            </xsl:call-template>
            <xsl:apply-templates select="app:output" />
        </app:module>
	<!-- place output signal declarations -->
        <xsl:for-each select="app:output">
	    <xsl:call-template name="placeSignalDeclaration">
	        <xsl:with-param name="signal" select="@signal" />
	        <xsl:with-param name="scope" select="@scope" />
	    </xsl:call-template>
	</xsl:for-each>
    </xsl:template>


    <xsl:template match="ex:binary-output[@input]">
        <app:module>
            <xsl:attribute name="class" 
	        select="'control4j.modules.io.IMBinaryOutput'" />
            <xsl:call-template name="setProperty">
                <xsl:with-param name="key" select="'hardware'" />
                <xsl:with-param name="value" select="@hw" />
            </xsl:call-template>
            <xsl:call-template name="placeInput">
                <xsl:with-param name="index" select="'0'" />
                <xsl:with-param name="signal" select="@input" />
                <xsl:with-param name="scope" select="@scope" />
            </xsl:call-template>
        </app:module>
    </xsl:template>

    <xsl:template match="ex:binary-output">
        <app:module>
            <xsl:attribute name="class" 
	        select="'control4j.modules.io.IMBinaryOutput'" />
            <xsl:call-template name="setProperty">
                <xsl:with-param name="key" select="'hardware'" />
                <xsl:with-param name="value" select="@hw" />
            </xsl:call-template>
            <xsl:apply-templates select="app:input" />
        </app:module>
    </xsl:template>


    <xsl:template match="ex:linear-transform">
        <app:module>
            <xsl:attribute name="class" 
	        select="'control4j.modules.PMLinearTransform'" />
            <xsl:call-template name="setProperty">
                <xsl:with-param name="key" select="'add'" />
                <xsl:with-param name="value" select="@add" />
            </xsl:call-template>
            <xsl:call-template name="setProperty">
                <xsl:with-param name="key" select="'mul'" />
                <xsl:with-param name="value" select="@mul" />
            </xsl:call-template>
            <xsl:call-template name="placeInput">
                <xsl:with-param name="index" select="'0'" />
                <xsl:with-param name="signal" select="@input" />
                <xsl:with-param name="scope" select="@input-scope" />
            </xsl:call-template>
            <xsl:call-template name="placeOutput">
                <xsl:with-param name="index" select="'0'" />
                <xsl:with-param name="signal" select="@output" />
                <xsl:with-param name="scope" select="@output-scope" />
            </xsl:call-template>
        </app:module>
	<!-- place output signal declaration -->
	<xsl:call-template name="placeSignalDeclaration">
	    <xsl:with-param name="signal" select="@output" />
	    <xsl:with-param name="scope" select="@output-scope" />
	</xsl:call-template>
    </xsl:template>


</xsl:stylesheet>
