<?xml version="1.0"?>

<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:ex="http://application.control4j.cz/extended"
    xmlns:app="http://application.control4j.cz"
    xmlns:fn="http://www.w3.org/2005/xpath-functions">


    <!-- adds a property element -->
    <xsl:template name="setProperty">
        <xsl:param name="key" />
        <xsl:param name="value" />
        <app:property>
            <xsl:attribute name="key" select="$key" />
            <xsl:attribute name="value" select="$value" />
        </app:property>
    </xsl:template>

    <!-- places a signal declaration -->
    <xsl:template name="placeSignalDeclaration">
        <xsl:param name="signal" />
        <xsl:param name="scope" />
        <app:signal>
            <xsl:attribute name="name" select="$signal" />
            <xsl:if test="fn:string-length($scope) gt 0">
                <xsl:attribute name="scope" select="$scope" />
            </xsl:if>
        </app:signal>
    </xsl:template>

    <!-- places an output -->
    <xsl:template name="placeOutput">
        <xsl:param name="index"  />
        <xsl:param name="signal" />
        <xsl:param name="scope"  />
        <xsl:param name="children" as="node()*" select="()" />
        <app:output>
            <xsl:attribute name="index" select="$index" />
            <xsl:attribute name="signal" select="$signal" />
            <xsl:if test="fn:string-length($scope) gt 0">
                <xsl:attribute name="scope" select="$scope" />
            </xsl:if>
            <xsl:apply-templates select="$children" />
        </app:output>
    </xsl:template>

    <!-- places an input -->
    <xsl:template name="placeInput">
        <xsl:param name="index"  />
        <xsl:param name="signal" />
        <xsl:param name="scope"  />
        <xsl:param name="children" as="node()*" select="()" />
        <app:input>
            <xsl:attribute name="index" select="$index" />
            <xsl:attribute name="signal" select="$signal" />
            <xsl:if test="fn:string-length($scope) gt 0">
                <xsl:attribute name="scope" select="$scope" />
            </xsl:if>
            <xsl:apply-templates select="$children" />
        </app:input>
    </xsl:template>

    <!-- places a papouch resource -->
    <xsl:template name="placePapouchResource">
        <xsl:param name="class"   />
        <xsl:param name="name"    />
        <xsl:param name="spinel"  />
        <xsl:param name="address" />

        <app:resource>
            <!-- set class name -->
            <xsl:attribute name="class" select="fn:concat('control4j.resources.papouch.', $class)" />
            <!-- set the name attribute -->
            <xsl:attribute name="name" select="$name" />
            <!-- set a reference to the spinel resource -->
            <xsl:call-template name="setProperty">
                <xsl:with-param name="key">spinel</xsl:with-param> 
                <xsl:with-param name="value" select="$spinel" />
            </xsl:call-template>
            <!-- set address -->
            <xsl:call-template name="setProperty">
                <xsl:with-param name="key">address</xsl:with-param> 
                <xsl:with-param name="value" select="$address" />
            </xsl:call-template>
        </app:resource>

    </xsl:template>


    <!-- Spinel resource -->
    <xsl:template match="ex:spinel">

        <app:resource class="control4j.resources.spinel.Spinel" >
            <!-- copy the name attribute -->
            <xsl:apply-templates select="@name" />
            <!-- create host property element -->
            <xsl:call-template name="setProperty">
                <xsl:with-param name="key">host</xsl:with-param> 
                <xsl:with-param name="value" select="@host" />
            </xsl:call-template>
            <!-- create port property element -->
            <xsl:call-template name="setProperty">
                <xsl:with-param name="key">port</xsl:with-param> 
                <xsl:with-param name="value" select="@port" />
            </xsl:call-template>
        </app:resource>

    </xsl:template>

    <!-- return scope, if not specified, it returns defaul value which is local -->
    <xsl:function name="ex:getScope">
        <xsl:param name="scope"/>
        <xsl:sequence select="if (fn:empty($scope)) then 'local' else $scope"/>
    </xsl:function>

    <!-- return spinel address, if not specified it return default value, which is 254 -->
    <xsl:function name="ex:getSpinelAddress">
        <xsl:param name="address"/>
        <xsl:sequence select="if (fn:empty($address)) then '254' else $address"/>
    </xsl:function>


    <xsl:function name="ex:getPapouchResourceName">
        <xsl:param name="name"/>
        <xsl:param name="spinel"/>
        <xsl:param name="address"/>
        <xsl:sequence select="if (fn:empty($name)) then fn:concat('_', $spinel, '_', $address, '_') else $name"/>
    </xsl:function>

    <!-- TQS3 -->
    <xsl:template match="ex:tqs3[@output]" >
        <xsl:variable name="address" select="ex:getSpinelAddress(@address)" />
        <xsl:variable name="spinel" select="@spinel" />
        <xsl:variable name="output" select="@output" />
        <xsl:variable name="scope" select="ex:getScope(@scope)" />
        <xsl:variable name="resource-id" select="ex:getPapouchResourceName(@name, $spinel, $address)" />

        <!-- place TQS3 resource -->
        <xsl:call-template name="placePapouchResource">
            <xsl:with-param name="class"   select="'TQS3'" />
            <xsl:with-param name="name"    select="$resource-id" />
            <xsl:with-param name="spinel"  select="$spinel" />
            <xsl:with-param name="address" select="$address" />
        </xsl:call-template>

        <!-- define output signal -->
        <xsl:call-template name="placeSignalDeclaration">
            <xsl:with-param name="signal" select="$output" />
            <xsl:with-param name="scope"  select="$scope" />
        </xsl:call-template>

        <!-- place a temperature module -->
        <app:module class="control4j.modules.io.OMTemperature">
            <xsl:call-template name="setProperty">
                <xsl:with-param name="key" select="'hardware'" />
                <xsl:with-param name="value" select="$resource-id" />
            </xsl:call-template>
            <xsl:call-template name="placeOutput">
                <xsl:with-param name="index"  select="'0'" />
                <xsl:with-param name="signal" select="$output" />
                <xsl:with-param name="scope"  select="$scope" />
            </xsl:call-template>
        </app:module>

    </xsl:template>

    <!-- QUIDO88 -->
    <xsl:template match="ex:quido88">
        <xsl:variable name="address" select="ex:getSpinelAddress(@address)" />
        <xsl:variable name="spinel" select="@spinel" />
        <xsl:variable name="resource-id" 
	    select="ex:getPapouchResourceName(@name, $spinel, $address)" />

        <!-- place resource -->
        <xsl:call-template name="placePapouchResource">
            <xsl:with-param name="class"   select="'Quido88'" />
            <xsl:with-param name="name"    select="$resource-id" />
            <xsl:with-param name="spinel"  select="$spinel" />
            <xsl:with-param name="address" select="$address" />
        </xsl:call-template>

    </xsl:template>


    <!-- DA2 -->
    <xsl:template match="ex:da2">
        <xsl:variable name="address" select="ex:getSpinelAddress(@address)" />
        <xsl:variable name="spinel" select="@spinel" />
        <xsl:variable name="resource-id" 
	    select="ex:getPapouchResourceName(@name, $spinel, $address)" />

        <!-- place resource -->
        <xsl:call-template name="placePapouchResource">
            <xsl:with-param name="class"   select="'DA2'" />
            <xsl:with-param name="name"    select="$resource-id" />
            <xsl:with-param name="spinel"  select="$spinel" />
            <xsl:with-param name="address" select="$address" />
        </xsl:call-template>

        <!-- if thre are inputs defined, place a module -->
        <xsl:if test="fn:exists(./app:input)">
            <!-- place a analog output module -->
            <app:module class="control4j.modules.io.IMAnalogOutput">
                <xsl:call-template name="setProperty">
                    <xsl:with-param name="key" select="'hardware'" />
                    <xsl:with-param name="value" select="$resource-id" />
                </xsl:call-template>
                <xsl:apply-templates select="./app:input" />
            </app:module>
        </xsl:if>
        
    </xsl:template>

    <!-- AD4 -->
    <xsl:template match="ex:ad4">
        <xsl:variable name="address" select="ex:getSpinelAddress(@address)" />
        <xsl:variable name="spinel" select="@spinel" />
        <xsl:variable name="resource-id" select="ex:getPapouchResourceName(@name, $spinel, $address)" />

        <!-- place resource -->
        <xsl:call-template name="placePapouchResource">
            <xsl:with-param name="class"   select="'AD4'" />
            <xsl:with-param name="name"    select="$resource-id" />
            <xsl:with-param name="spinel"  select="$spinel" />
            <xsl:with-param name="address" select="$address" />
        </xsl:call-template>

        <!-- if thre are outputs defined, place a module -->
        <xsl:if test="fn:exists(./app:output)">
            <!-- make a definition for signals -->
            <xsl:for-each select="./app:output">
                <xsl:call-template name="placeSignalDeclaration">
                    <xsl:with-param name="signal" select="@signal" />
                    <xsl:with-param name="scope" select="@scope" />
                </xsl:call-template>
            </xsl:for-each>
            <!-- place an analog output module -->
            <app:module class="control4j.modules.io.OMAnalogInput">
                <xsl:call-template name="setProperty">
                    <xsl:with-param name="key" select="'hardware'" />
                    <xsl:with-param name="value" select="$resource-id" />
                </xsl:call-template>
                <xsl:apply-templates select="app:output" />
            </app:module>
        </xsl:if>
        
    </xsl:template>


</xsl:stylesheet>
