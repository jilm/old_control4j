<?xml version="1.0"?>

<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:app="http://application.control4j.cz"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:ld="http://ld.control4j.cz"
>

    <xsl:output indent="yes" />

    <xsl:variable name="MNS" select="'control4j.modules.bool'" />
    <xsl:variable name="NOT" select="concat($MNS, '.PMNot')" />
    <xsl:variable name="AND" select="concat($MNS, '.PMAnd')" />
    <xsl:variable name="OR" select="concat($MNS, '.PMOr')" />

    <xsl:function name="ld:getNotSignalName">
        <xsl:param name="signal" />
        <xsl:sequence select="fn:concat('_NOT_', $signal)" />
    </xsl:function>

    <xsl:function name="ld:getTempSignalName">
        <xsl:param name="signal" />
        <xsl:sequence select="fn:concat('_TEMP_', $signal)" />
    </xsl:function>

    <xsl:function name="ld:getContactType">
        <xsl:param name="type" />
        <xsl:sequence select="if (fn:empty($type)) then 'XIO' else $type" />
    </xsl:function>

    <xsl:function name="ld:getInputSignalName">
        <xsl:param name="signal" />
        <xsl:param name="type" />
        <xsl:choose>
            <xsl:when test="$type eq 'XIO'">
                <xsl:sequence select="$signal" />
            </xsl:when>
            <xsl:when test="$type eq 'XIC'">
                <xsl:sequence select="ld:getNotSignalName($signal)" />
            </xsl:when>
        </xsl:choose>
    </xsl:function>

    <xsl:function name="ld:getInputSignalScope">
        <xsl:param name="type" />
        <xsl:choose>
            <xsl:when test="$type eq 'XIO'">
                <xsl:sequence select="'global'" />
            </xsl:when>
            <xsl:when test="$type eq 'XIC'">
                <xsl:sequence select="'local'" />
            </xsl:when>
        </xsl:choose>
    </xsl:function>

    <xsl:template name="placeInput">
        <!-- expects contact node -->
        <xsl:param name="contact" />
        <xsl:variable name="type" select="ld:getContactType($contact/@type)" />
        <xsl:variable name="name" select="ld:getInputSignalName($contact/@name, $type)" /> 
        <xsl:variable name="scope" select="ld:getInputSignalScope($type)" />
        <app:input>
            <xsl:attribute name="signal" select="$name" />
            <xsl:attribute name="scope"  select="$scope" />
        </app:input>
    </xsl:template>

    <xsl:template name="placeNotModule">
        <xsl:param name="input" />
        <xsl:param name="input-scope" />
        <xsl:param name="output" />
        <xsl:param name="output-scope" />
        <app:module>
            <xsl:attribute name="class" select="$NOT" />
            <app:input>
                <xsl:attribute name="index" select="'0'" />
                <xsl:attribute name="signal" select="$input" />
                <xsl:attribute name="scope" select="$input-scope" />
            </app:input>
            <app:output>
                <xsl:attribute name="index" select="'0'" />
                <xsl:attribute name="signal" select="$output" />
                <xsl:attribute name="scope" select="$output-scope" />
            </app:output>
        </app:module>
    </xsl:template>

    <xsl:template name="placeSignalDeclaration">
        <xsl:param name="signal" />
        <xsl:param name="scope" />
        <app:signal>
            <xsl:attribute name="name" select="$signal" />
            <xsl:attribute name="scope" select="$scope" />
        </app:signal>
    </xsl:template>

    <xsl:template match="ld:ld">
        <app:application>
            <app:scope>
                <!-- prepare negated signals for all of the XIC contacts -->
                <xsl:for-each select="fn:distinct-values(//ld:contact[@type='XIC']/@name)">
                    <xsl:call-template name="placeSignalDeclaration">
                        <xsl:with-param name="signal" select="ld:getNotSignalName(.)" />
                        <xsl:with-param name="scope" select="'local'" />
                    </xsl:call-template>
                    <xsl:call-template name="placeNotModule">
                        <xsl:with-param name="input" select="." />
                        <xsl:with-param name="input-scope" select="'global'" />
                        <xsl:with-param name="output" select="ld:getNotSignalName(.)" />
                        <xsl:with-param name="output-scope" select="'local'" />
                    </xsl:call-template>
                </xsl:for-each>
                <!-- translate the whole ladder diagram -->
                <xsl:apply-templates select="ld:rung" />
            </app:scope>
        </app:application>
    </xsl:template>

    <xsl:template match="ld:rung">
        <xsl:variable name="temp-id" select="fn:concat('r', fn:position())" />
        <xsl:comment select="fn:concat(' rung #', fn:position(), ' ')" />
        <xsl:apply-templates select="ld:coil">
            <xsl:with-param name="parent-temp-id" select="$temp-id" />
        </xsl:apply-templates>
    </xsl:template>

    <!-- for each coil -->
    <xsl:template match="ld:coil">
        <xsl:param name="parent-temp-id" />
        <xsl:variable name="temp-id" select="fn:concat($parent-temp-id, 'c', fn:position())" />
        <!-- generate global signal declaration -->
        <xsl:call-template name="placeSignalDeclaration">
            <xsl:with-param name="signal" select="@name" />
            <xsl:with-param name="scope" select="'global'" />
        </xsl:call-template>
        <!-- and translate contact interpretation -->
        <xsl:apply-templates select="../ld:contact | ../ld:serial | ../ld:parallel">
            <xsl:with-param name="output" select="@name" />
            <xsl:with-param name="scope">global</xsl:with-param>
            <xsl:with-param name="parent-temp-id" select="$temp-id" />
        </xsl:apply-templates>
    </xsl:template>


    <!-- Serial contact block is interpreted as and function -->
    <xsl:template match="ld:serial">
        <!-- give me a name of the output and scope of output for this serial block -->
        <xsl:param name="output" />
        <xsl:param name="scope" />
        <xsl:param name="parent-temp-id" />

        <!-- first, generate and module and all of its inputs and outputs -->
        <app:module>
            <xsl:attribute name="class" select="$AND" />
            <xsl:for-each select="ld:contact">
                <xsl:call-template name="placeInput">
                    <xsl:with-param name="contact" select="." />
                </xsl:call-template>
            </xsl:for-each>
            <xsl:for-each select="ld:parallel">
                <xsl:variable name="temp-id" select="fn:concat($parent-temp-id, 'p', fn:position())" />
                <app:input>
                    <xsl:attribute name="signal" select="$temp-id" />
                    <xsl:attribute name="scope" select="'local'" />
                </app:input>
            </xsl:for-each>
            <app:output>
                <xsl:attribute name="signal" select="$output" />
                <xsl:attribute name="scope" select="$scope" />
            </app:output>
        </app:module>

        <xsl:for-each select="ld:parallel">
            <xsl:variable name="temp-id" select="fn:concat($parent-temp-id, 'p', fn:position())" />
            <xsl:call-template name="placeSignalDeclaration">
                <xsl:with-param name="signal" select="$temp-id" />
                <xsl:with-param name="scope" select="'local'" />
            </xsl:call-template>
            <xsl:apply-templates select=".">
                <xsl:with-param name="output" select="$temp-id" />
                <xsl:with-param name="scope" select="'local'" />
            </xsl:apply-templates>
        </xsl:for-each>

    </xsl:template>

    <xsl:template match="ld:parallel">
        <xsl:param name="output" />
        <xsl:param name="scope" />
        <xsl:param name="parent-temp-id" />

        <app:module>
            <xsl:attribute name="class" select="$OR" />
            <xsl:for-each select="ld:contact">
                <xsl:call-template name="placeInput">
                    <xsl:with-param name="contact" select="." />
                </xsl:call-template>
            </xsl:for-each>
            <xsl:for-each select="ld:serial">
                <xsl:variable name="temp-id" select="fn:concat($parent-temp-id, 's', fn:position())" />
                <app:input>
                    <xsl:attribute name="scope" select="'local'" />
                    <xsl:attribute name="signal" select="$temp-id" />
                </app:input>
            </xsl:for-each>
            <app:output>
                <xsl:attribute name="signal" select="$output" />
                <xsl:attribute name="scope" select="$scope" />
            </app:output>
        </app:module>

        <xsl:for-each select="ld:serial">
            <xsl:variable name="temp-id" select="fn:concat($parent-temp-id, 's', fn:position())" />
            <xsl:call-template name="placeSignalDeclaration">
                <xsl:with-param name="signal" select="$temp-id" />
                <xsl:with-param name="scope" select="'local'" />
            </xsl:call-template>
            <xsl:apply-templates select=".">
                <xsl:with-param name="output" select="$temp-id" />
                <xsl:with-param name="scope" select="'local'" />
            </xsl:apply-templates>
        </xsl:for-each>

    </xsl:template>

    <xsl:template match="ld:contact">
        <xsl:param name="output" />
        <xsl:param name="scope" />
        <xsl:param name="parent-temp-id" />
        <xsl:variable name="type" select="ld:getContactType(@type)" />

        <xsl:choose>
            <xsl:when test="$type='XIO'">
                <app:module>
                    <xsl:attribute name="class" select="$OR" />
                    <app:input>
                        <xsl:attribute name="signal" select="@name" />
                        <xsl:attribute name="scope" select="'global'" />
                    </app:input>
                    <app:output>
                        <xsl:attribute name="signal" select="$output" />
                        <xsl:attribute name="scope" select="$scope" />
                    </app:output>
                </app:module>
            </xsl:when>
            <xsl:when test="$type='XIC'">
                <xsl:call-template name="placeNotModule">
                    <xsl:with-param name="input" select="@name" />
                    <xsl:with-param name="input-scope" select="'global'" />
                    <xsl:with-param name="output" select="$output" />
                    <xsl:with-param name="output-scope" select="$scope" />
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>

    </xsl:template>



</xsl:stylesheet>