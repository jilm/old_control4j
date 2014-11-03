<?xml version="1.0"?>

<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:ex="http://application.control4j.cz/extended"
    xmlns:app="http://application.control4j.cz"
    xmlns:fn="http://www.w3.org/2005/xpath-functions">


    <xsl:template match="ex:comparator">

        <xsl:variable name="ref-index" 
                      select="if (@invert eq 'yes') then '1' else '0'" />
        <xsl:variable name="input-index" 
                      select="if (@invert eq 'yes') then '0' else '1'" />

        <app:module>
            <xsl:attribute name="class" 
	                   select="fn:concat($MNS, 'PMComparator')" />

            <!-- property -->
            <xsl:if test="@hysteresis">
                <xsl:call-template name="setProperty">
                    <xsl:with-param name="key" select="'hysteresis'" />
                    <xsl:with-param name="value" select="@hysteresis" />
                </xsl:call-template>
            </xsl:if>

            <!-- reference input -->
            <xsl:call-template name="placeInput">
                <xsl:with-param name="index" 
                                select="$ref-index" />
                <xsl:with-param name="signal"
                                select="if (@ref-input) then @ref-input else ex:ref/@signal" />
                <xsl:with-param name="scope"
                                select="if (@ref-input) then @ref-input-scope else ex:ref/@scope" />
                <xsl:with-param name="children"
                                select="if (@ref-input) then () else ex:ref/*" />
            </xsl:call-template>

            <!-- input -->
            <xsl:call-template name="placeInput">
                <xsl:with-param name="index" 
                                select="$input-index" />
                <xsl:with-param name="signal"
                                select="if (@input) then @input else ex:input/@signal" />
                <xsl:with-param name="scope"
                                select="if (@input) then @input-scope else ex:input/@scope" />
                <xsl:with-param name="children"
                                select="if (@input) then () else ex:input/*" />
            </xsl:call-template>

            <!-- output -->
            <xsl:call-template name="placeOutput">
                <xsl:with-param name="index" 
                                select="'0'" />
                <xsl:with-param name="signal"
                                select="if (@output) then @output else ex:output/@signal" />
                <xsl:with-param name="scope"
                                select="if (@output) then @output-scope else ex:output/@scope" />
                <xsl:with-param name="children"
                                select="if (@output) then () else ex:output/*" />
            </xsl:call-template>
            
        </app:module>

	<!-- make output signal declaration -->
	<app:signal>
	    <xsl:attribute name="name" 
                select="if (@output) then @output else ex:output/@signal" />
	    <xsl:attribute name="scope"
                select="if (@output) then @output-scope else ex:output/@scope" />
	</app:signal>

    </xsl:template>


    <xsl:template match="ex:multiplexer">

        <app:module>
            <xsl:attribute name="class" select="fn:concat($MNS, 'PMMultiplexer')" />

            <!-- select input -->
            <xsl:call-template name="placeInput">
                <xsl:with-param name="index" 
                                select="'0'" />
                <xsl:with-param name="signal"
                                select="if (@select) then @select else ex:select/@signal" />
                <xsl:with-param name="scope"
                                select="if (@select) then @select-scope else ex:select/@scope" />
                <xsl:with-param name="children"
                                select="if (@select) then () else ex:select/*" />
            </xsl:call-template>

            <!-- inputs -->
            <xsl:apply-templates select="app:input" />

            <!-- output -->
            <xsl:call-template name="placeOutput">
                <xsl:with-param name="index" 
                                select="'0'" />
                <xsl:with-param name="signal"
                                select="if (@output) then @output else ex:output/@signal" />
                <xsl:with-param name="scope"
                                select="if (@output) then @output-scope else ex:output/@scope" />
                <xsl:with-param name="children"
                                select="if (@output) then () else ex:output/*" />
            </xsl:call-template>

        </app:module>

	<!-- make output signal declaration -->
	<app:signal>
	    <xsl:attribute name="name" 
                select="if (@output) then @output else ex:output/@signal" />
	    <xsl:attribute name="scope"
                select="if (@output) then @output-scope else ex:output/@scope" />
	</app:signal>

    </xsl:template>


</xsl:stylesheet>
