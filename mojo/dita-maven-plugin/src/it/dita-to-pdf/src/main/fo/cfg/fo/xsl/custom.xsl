<?xml version='1.0'?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:fo="http://www.w3.org/1999/XSL/Format"
  xmlns:rx="http://www.renderx.com/XSL/Extensions"
  version="1.1">

  <!-- ============================================================================= -->
  <!--  give bottom footer more room to add 2 more extra lines-->
  <!-- ============================================================================= -->
  <xsl:variable name="page-margin-bottom" select="'30mm'"/>

  <!-- ============================================================================= -->
  <!--  Override default font-familty from Sans to Helvetica which is very closed to Arial -->
  <!--  Note latest version of Adobe reader map Helvetica to Arial                   -->
  <!-- ============================================================================= -->
  <xsl:attribute-set name="__fo__root">
    <xsl:attribute name="font-family">Sans</xsl:attribute>
    <xsl:attribute name="font-size"><xsl:value-of select="$default-font-size"/></xsl:attribute>
    <xsl:attribute name="rx:link-back">true</xsl:attribute>
  </xsl:attribute-set>
    
  <!-- ============================================================================= -->
  <!--  Global vars used in this file -->
  <!-- ============================================================================= -->
  
  <!-- pick up productVersion, buildDate generated before Ant build starts -->
  <xsl:include href="../../../../../../target/dita/resources/build-properties.xsl"/>
    
  <xsl:variable name="productName" select="$map//*[contains(@class,' bookmap/booklibrary ')]" />
  <xsl:variable name="bookid">
    <xsl:call-template name="insertVariable">
      <xsl:with-param name="theVariableID" select="'bookId'" />
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="bookName" select="$map//*[contains(@class,' bookmap/mainbooktitle ')]" />
  <xsl:variable name="feedbackStr">
    <xsl:call-template name="insertVariable">
      <xsl:with-param name="theVariableID" select="'feedbackStr'" />
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="versionStr">
    <xsl:call-template name="insertVariable">
      <xsl:with-param name="theVariableID" select="'versionStr'" />
    </xsl:call-template>
  </xsl:variable>

  <!-- ============================================================================= -->
  <!--  override cover page transformation                                           -->
  <!--  due to a bug in FO PDF plugin, this template also creates notice page right  -->
  <!--   after cover page                                                            -->
  <!-- ============================================================================= -->
  <xsl:template name="createFrontMatter_1.0">
    <xsl:message>
      calling customized createFrontMatter_1.0 ...
    </xsl:message>
    <fo:page-sequence master-reference="front-matter" xsl:use-attribute-sets="__force__page__count">
      <!-- xsl:call-template name="insertFrontMatterStaticContents" /-->
      <fo:flow flow-name="xsl-region-body">

        <fo:block xsl:use-attribute-sets="__frontmatter__banner__image">
          <fo:inline id=""><fo:external-graphic 
            src="url({concat($artworkPrefix, '/Customization/OpenTopic/common/artwork/mojo-logo.png')})"/>    
          </fo:inline>      
        </fo:block>

        <!-- set the product name -->
        <xsl:call-template name="insertCoverPageProjectName" />

        <!-- set the version -->
        <fo:block xsl:use-attribute-sets="__frontmatter__product__version">
          <xsl:value-of select="$versionStr" />&#xA0;
          <xsl:value-of select="$productVersion" />
        </fo:block>

        <!-- set book name -->
        <fo:block xsl:use-attribute-sets="__frontmatter__product__bookname">
          <xsl:value-of select="$bookName" />
        </fo:block>

        <!-- set codehaus logo -->
        <fo:block xsl:use-attribute-sets="__frontmatter__company__logo" break-after="page">
          <fo:inline id=""><fo:external-graphic 
            src="url({concat($artworkPrefix, '/Customization/OpenTopic/common/artwork/codehaus-logo-small.png')})"/>    
          </fo:inline>      
        </fo:block>

        <xsl:call-template name="insertNotices" />

      </fo:flow>

    </fo:page-sequence>
  </xsl:template>

  <!-- private -->
  <xsl:template name="insertCoverPageProjectName">
    <fo:block xsl:use-attribute-sets="__frontmatter__product__name">
      <fo:inline xsl:use-attribute-sets="__frontmatter__product__name__text">
        <xsl:value-of select="$productName" />
      </fo:inline>
      <fo:inline xsl:use-attribute-sets="__frontmatter__product__name__trademark">
        <xsl:call-template name="insertVariable">
          <xsl:with-param name="theVariableID" select="'trademarkChar'" />
        </xsl:call-template>
      </fo:inline>
    </fo:block>
  </xsl:template>

  <!-- override disable mini toc at the chapter's 1st page -->
  <xsl:template match="*" mode="createMiniToc">
  </xsl:template>

  <!-- override: remove fo:block from original template -->
  <xsl:template match="*[contains(@class, ' bookmap/booktitlealt ')]">
    <xsl:apply-templates />
  </xsl:template>

  <!-- ============================================================================= -->
  <!-- Notice page processing                                                        -->
  <!-- ============================================================================= -->

  <!-- private -->
  <xsl:template name="insertNotices">

    <fo:block xsl:use-attribute-sets="notices.page">
      <xsl:value-of select="$productName" />&#xA0;
      <xsl:value-of select="$bookName" />
    </fo:block>

    <fo:block xsl:use-attribute-sets="notices.text">
      <xsl:value-of select="$versionStr" />&#xA0;
      <xsl:value-of select="$productVersion" />
    </fo:block>

    <fo:block xsl:use-attribute-sets="notices.text">
      <xsl:value-of select="$buildDate" />
    </fo:block>


    <fo:block xsl:use-attribute-sets="notices.text">
      <xsl:value-of select="$bookid" />
    </fo:block>

    <fo:block xsl:use-attribute-sets="notices.text">
      <xsl:call-template name="insertVariable">
        <xsl:with-param name="theVariableID" select="'notices.copyright.text'" />
      </xsl:call-template>
    </fo:block>

    <fo:block xsl:use-attribute-sets="notices.bold">
      <xsl:call-template name="insertVariable">
        <xsl:with-param name="theVariableID" select="'notices.trademarks.label'" />
      </xsl:call-template>
    </fo:block>

    <fo:block xsl:use-attribute-sets="notices.text">
      <xsl:call-template name="insertVariable">
        <xsl:with-param name="theVariableID" select="'notices.trademarks.text'" />
      </xsl:call-template>
    </fo:block>

    <fo:block xsl:use-attribute-sets="notices.bold">
      <xsl:call-template name="insertVariable">
        <xsl:with-param name="theVariableID" select="'notices.regulatoryCompliance.label'" />
      </xsl:call-template>
    </fo:block>

    <fo:block xsl:use-attribute-sets="notices.text">
      <xsl:call-template name="insertVariable">
        <xsl:with-param name="theVariableID" select="'notices.regulatoryCompliance.text'" />
      </xsl:call-template>
    </fo:block>

  </xsl:template>

  <!-- private -->
  <xsl:attribute-set name="notices.page">
    <xsl:attribute name="padding-top">60pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
  </xsl:attribute-set>

  <!-- private -->
  <xsl:attribute-set name="notices.bold">
    <xsl:attribute name="font-weight">bold</xsl:attribute>
  </xsl:attribute-set>

  <!-- private -->
  <xsl:attribute-set name="notices.text">
    <xsl:attribute name="margin-top">10pt</xsl:attribute>
    <xsl:attribute name="margin-bottom">10pt</xsl:attribute>
  </xsl:attribute-set>

  <!-- ============================================================================= -->
  <!-- Footer/header                                                                 -->
  <!-- ============================================================================= -->

  <!-- private -->
  <xsl:template name="createFooterLine1">
    <xsl:value-of select="$productName" />&#xA0;
    <xsl:value-of select="$versionStr" />&#xA0;
    <xsl:value-of select="$productVersion" />&#xA0;
    <xsl:value-of select="$bookName" />
  </xsl:template>

  <!-- private -->
  <xsl:template name="createFooterLine3">
    http://mojo.codehaus.org
    <fo:basic-link external-destination="url('http://mojo.codehaus.org/dita-maven-plugin')">
      •&#xA0;<xsl:value-of select="$feedbackStr" />
    </fo:basic-link>
  </xsl:template>

  <!-- private -->
  <xsl:template name="insertOddFooterContent">
    <fo:block xsl:use-attribute-sets="__body__odd__footer__1">
      <xsl:call-template name="createFooterLine1" />
    </fo:block>

    <!--  line 2 -->
    <fo:table>
      <fo:table-column column-number="1" />
      <fo:table-column column-number="2" />
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell>
            <fo:block xsl:use-attribute-sets="__body__odd__footer__2_pagenumber">
              <fo:page-number />
            </fo:block>
          </fo:table-cell>
          <fo:table-cell>
            <fo:block xsl:use-attribute-sets="__body__odd__footer__2">
              <xsl:value-of select="$bookid" />
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>

    <fo:block xsl:use-attribute-sets="__body__odd__footer__3">
      <xsl:call-template name="createFooterLine3" />
    </fo:block>

  </xsl:template>

  <!-- override -->
  <xsl:template name="insertEvenFooterContent">
    <fo:block xsl:use-attribute-sets="__body__even__footer__1">
      <xsl:call-template name="createFooterLine1" />
    </fo:block>

    <!--  line 2 -->
    <fo:table>
      <fo:table-column column-number="1" />
      <fo:table-column column-number="2" />
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell>
            <fo:block xsl:use-attribute-sets="__body__even__footer__2">
              <xsl:value-of select="$bookid" />
            </fo:block>
          </fo:table-cell>
          <fo:table-cell>
            <fo:block xsl:use-attribute-sets="__body__even__footer__2_pagenumber">
              <fo:page-number />
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>

    <fo:block xsl:use-attribute-sets="__body__even__footer__3">
      <xsl:call-template name="createFooterLine3" />
    </fo:block>
  </xsl:template>

  <!-- override -->
  <xsl:template name="insertTocOddFooter">
    <fo:static-content flow-name="odd-toc-footer">
      <xsl:call-template name="insertOddFooterContent" />
    </fo:static-content>
  </xsl:template>

  <!-- override -->
  <xsl:template name="insertTocEvenFooter">
    <fo:static-content flow-name="even-toc-footer">
      <xsl:call-template name="insertEvenFooterContent" />
    </fo:static-content>
  </xsl:template>

  <!-- override -->
  <xsl:template name="insertBodyFirstFooter">
    <fo:static-content flow-name="first-body-footer">
      <xsl:call-template name="insertOddFooterContent" />
    </fo:static-content>
  </xsl:template>


  <!-- override -->
  <xsl:template name="insertBodyOddFooter">
    <fo:static-content flow-name="odd-body-footer">
      <xsl:call-template name="insertOddFooterContent" />
    </fo:static-content>
  </xsl:template>

  <!-- override -->
  <xsl:template name="insertBodyEvenFooter">
    <fo:static-content flow-name="even-body-footer">
      <xsl:call-template name="insertEvenFooterContent" />
    </fo:static-content>
  </xsl:template>


  <!-- private -->
  <xsl:attribute-set name="__frontmatter__banner__image">
    <xsl:attribute name="margin-top">18pt</xsl:attribute>
    <xsl:attribute name="text-align">center</xsl:attribute>
  </xsl:attribute-set>

  <!-- private -->
  <xsl:attribute-set name="__frontmatter__product__name">
    <xsl:attribute name="margin-top">115pt</xsl:attribute>
    <xsl:attribute name="margin-right">90pt</xsl:attribute>
    <xsl:attribute name="text-align">right</xsl:attribute>
    <xsl:attribute name="font-family">Sans</xsl:attribute>
  </xsl:attribute-set>

  <!-- private -->
  <xsl:attribute-set name="__frontmatter__product__name__text">
    <xsl:attribute name="font-size">36pt</xsl:attribute>
  </xsl:attribute-set>

  <!-- private -->
  <xsl:attribute-set name="__frontmatter__product__name__trademark">
    <xsl:attribute name="font-size">24pt</xsl:attribute>
  </xsl:attribute-set>


  <!-- private -->
  <xsl:attribute-set name="__frontmatter__product__version">
    <xsl:attribute name="margin-top">40pt</xsl:attribute>
    <xsl:attribute name="margin-right">90pt</xsl:attribute>
    <xsl:attribute name="text-align">right</xsl:attribute>
    <xsl:attribute name="font-size">19pt</xsl:attribute>
    <xsl:attribute name="font-family">Sans</xsl:attribute>
  </xsl:attribute-set>

  <!-- private -->
  <xsl:attribute-set name="__frontmatter__product__bookname">
    <xsl:attribute name="margin-top">5pt</xsl:attribute>
    <xsl:attribute name="margin-right">90pt</xsl:attribute>
    <xsl:attribute name="text-align">right</xsl:attribute>
    <xsl:attribute name="font-size">10.5pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
  </xsl:attribute-set>

  <!-- private -->
  <xsl:attribute-set name="__frontmatter__company__logo">
    <xsl:attribute name="margin-top">250pt</xsl:attribute>
    <xsl:attribute name="text-align">center</xsl:attribute>
  </xsl:attribute-set>

  <!-- override -->
  <xsl:attribute-set name="__body__odd__header">
    <xsl:attribute name="padding-top">20pt</xsl:attribute>
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="border-bottom-width">1pt</xsl:attribute>
    <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
    <xsl:attribute name="border-bottom-color">black</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
  </xsl:attribute-set>

  <!-- override -->
  <xsl:attribute-set name="__body__even__header">
    <xsl:attribute name="padding-top">20pt</xsl:attribute>
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="border-bottom-width">1pt</xsl:attribute>
    <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
    <xsl:attribute name="border-bottom-color">black</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
  </xsl:attribute-set>

  <!-- private -->
  <xsl:attribute-set name="__body__even__footer__1">
    <xsl:attribute name="margin-top">10pt</xsl:attribute>
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="border-top-width">1pt</xsl:attribute>
    <xsl:attribute name="border-top-style">solid</xsl:attribute>
    <xsl:attribute name="border-top-color">black</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
  </xsl:attribute-set>

  <!-- private -->
  <xsl:attribute-set name="__body__even__footer__2">
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
  </xsl:attribute-set>

  <!-- private -->
  <xsl:attribute-set name="__body__even__footer__2_pagenumber">
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="text-align">right</xsl:attribute>
  </xsl:attribute-set>

  <!-- private -->
  <xsl:attribute-set name="__body__even__footer__3">
    <xsl:attribute name="color">blue</xsl:attribute>
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="padding-bottom">20pt</xsl:attribute>
  </xsl:attribute-set>

  <!-- private -->
  <xsl:attribute-set name="__body__odd__footer__1">
    <xsl:attribute name="margin-top">10pt</xsl:attribute>
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="border-top-width">1pt</xsl:attribute>
    <xsl:attribute name="border-top-style">solid</xsl:attribute>
    <xsl:attribute name="border-top-color">black</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
    <xsl:attribute name="text-align">right</xsl:attribute>
  </xsl:attribute-set>

  <!-- private -->
  <xsl:attribute-set name="__body__odd__footer__2">
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
    <xsl:attribute name="text-align">right</xsl:attribute>
  </xsl:attribute-set>

  <!-- private -->
  <xsl:attribute-set name="__body__odd__footer__2_pagenumber">
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="text-align">left</xsl:attribute>
  </xsl:attribute-set>

  <!-- private -->
  <xsl:attribute-set name="__body__odd__footer__3">
    <xsl:attribute name="color">blue</xsl:attribute>
    <xsl:attribute name="text-align">right</xsl:attribute>
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="padding-bottom">20pt</xsl:attribute>
  </xsl:attribute-set>

  <!-- override -->
  <xsl:attribute-set name="__toc__odd__header">
    <xsl:attribute name="padding-top">20pt</xsl:attribute>
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="border-bottom-width">1pt</xsl:attribute>
    <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
    <xsl:attribute name="border-bottom-color">black</xsl:attribute>
  </xsl:attribute-set>

  <!-- override -->
  <xsl:attribute-set name="__toc__even__header">
    <xsl:attribute name="padding-top">20pt</xsl:attribute>
    <xsl:attribute name="margin-right">40pt</xsl:attribute>
    <xsl:attribute name="margin-left">40pt</xsl:attribute>
    <xsl:attribute name="border-bottom-width">1pt</xsl:attribute>
    <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
    <xsl:attribute name="border-bottom-color">black</xsl:attribute>
  </xsl:attribute-set>
  
  <!-- ============================================================================= -->
  <!-- TOC numbering alignment, should move this back to DITAOT.                     -->
  <!-- ============================================================================= -->
  
  <!-- override -->
  <xsl:attribute-set name="__toc__topic__content">
    <xsl:attribute name="text-align">justify</xsl:attribute>
  </xsl:attribute-set>
  

</xsl:stylesheet>