package org.codehaus.mojo.cis.core;

import java.lang.reflect.UndeclaredThrowableException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;


/**
 * This utility class is used to transform the cisconfig.xml file.
 */
public class CisConfigTransformer
{
    private static class MyXMLFilter extends XMLFilterImpl implements LexicalHandler, DeclHandler {
        private static final String PROP_DECL_HANDLER = "http://xml.org/sax/properties/declaration-handler";
        private static final String PROP_LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
        private final CisConfig cisConfig;
        private LexicalHandler lexicalHandler;
        private DeclHandler declHandler;
        private int level;
        
        MyXMLFilter( CisConfig cisConfig )
        {
            this.cisConfig = cisConfig;
        }

        public void setParent( XMLReader pParent )
        {
            super.setParent( pParent );
            if ( pParent != null )
            {
                try
                {
                    pParent.setProperty( PROP_LEXICAL_HANDLER, lexicalHandler );
                    pParent.setProperty( PROP_DECL_HANDLER, declHandler );
                }
                catch ( SAXException e )
                {
                    throw new UndeclaredThrowableException( e );
                }
            }
        }

        public void startDocument() throws SAXException
        {
            level = 0;
        }

        private void setAttribute( AttributesImpl attrs, String name, String value )
        {
            if ( value != null )
            {
                final int index = attrs.getIndex( "", name );
                if ( index == -1 )
                {
                    attrs.addAttribute( "", name, name, "CDATA", value );
                }
                else
                {
                    attrs.setValue( index, value );
                }
            }
        }

        private void setAttribute( AttributesImpl attrs, String name, Boolean value )
        {
            if ( value != null )
            {
                setAttribute( attrs, name, value.toString() );
            }
        }

        private void setAttribute( AttributesImpl attrs, String name, Integer value )
        {
            if ( value != null )
            {
                setAttribute( attrs, name, value.toString() );
            }
        }

        private Attributes getTransformedAttributes( Attributes attrs )
        {
            if ( cisConfig == null )
            {
                return attrs;
            }
            final AttributesImpl result = new AttributesImpl( attrs );
            setAttribute( result, "startmonitoringthread", cisConfig.getStartMonitoringThread() );
            setAttribute( result, "requestclienthost", cisConfig.getRequestClientHost() );
            setAttribute( result, "debugmode", cisConfig.getDebugMode() );
            setAttribute( result, "multilanguagemanager", cisConfig.getMultiLanguageManager() );
            setAttribute( result, "onlinehelpmanager", cisConfig.getOnlineHelpManager() );
            setAttribute( result, "sessiontimeout", cisConfig.getSessionTimeout() );
            setAttribute( result, "useownclassloader", cisConfig.getUseOwnClassLoader() );
            setAttribute( result, "browserpopuponerror", cisConfig.getBrowserPopupOnError() );
            setAttribute( result, "logtoscreen", cisConfig.getLogToScreen() );
            setAttribute( result, "zipcontent", cisConfig.getZipContent() );
            setAttribute( result, "textencoding", cisConfig.getTextEncoding() );
            return result;
        }

        public void startElement( String uri, String localName, String qName, Attributes atts ) throws SAXException
        {
            final Attributes attrs;
            if ( level++ == 0  &&  "cisconfig".equals( localName )
                            &&  (uri == null  ||  uri.length() == 0))
            {
                attrs = getTransformedAttributes( atts );
            }
            else
            {
                attrs = atts;
            }
            super.startElement( uri, localName, qName, attrs );
        }

        public void endElement( String pUri, String pLocalName, String pName ) throws SAXException
        {
            super.endElement( pUri, pLocalName, pName );
            --level;
        }

        public void comment( char[] pCh, int pStart, int pLength ) throws SAXException
        {
            if ( lexicalHandler != null )
            {
                lexicalHandler.comment( pCh, pStart, pLength );
            }
        }

        public void endCDATA() throws SAXException
        {
            if ( lexicalHandler != null )
            {
                lexicalHandler.endCDATA();
            }
        }

        public void endDTD() throws SAXException
        {
            if ( lexicalHandler != null )
            {
                lexicalHandler.endDTD();
            }
        }

        public void endEntity( String name ) throws SAXException
        {
            if ( lexicalHandler != null )
            {
                lexicalHandler.endEntity( name );
            }
        }

        public void startCDATA() throws SAXException
        {
            if ( lexicalHandler != null )
            {
                lexicalHandler.startCDATA();
            }
        }

        public void startDTD( String name, String publicId, String systemId ) throws SAXException
        {
            if ( lexicalHandler != null )
            {
                lexicalHandler.startDTD( name, publicId, systemId );
            }
        }

        public void startEntity( String name ) throws SAXException
        {
            if ( lexicalHandler != null )
            {
                lexicalHandler.startEntity( name );
            }
        }

        public Object getProperty( String pName ) throws SAXNotRecognizedException, SAXNotSupportedException
        {
            if ( PROP_LEXICAL_HANDLER.equals( pName ) )
            {
                return lexicalHandler;
            }
            if ( PROP_DECL_HANDLER.equals( pName ) )
            {
                return declHandler;
            }
            return super.getProperty( pName );
        }

        public void setProperty( String pName, Object pValue )
            throws SAXNotRecognizedException, SAXNotSupportedException
        {
            if ( PROP_LEXICAL_HANDLER.equals( pName ) )
            {
                lexicalHandler = (LexicalHandler) pValue;
                if ( getParent() == null )
                {
                    return;
                }
            }
            else if ( PROP_DECL_HANDLER.equals( pName ) )
            {
                declHandler = (DeclHandler) pValue;
                if ( getParent() == null )
                {
                    return;
                }
            }
            super.setProperty( pName, pValue );
        }

        public void attributeDecl( String eName, String aName, String type, String mode, String value )
            throws SAXException
        {
            if ( declHandler != null )
            {
                declHandler.attributeDecl( eName, aName, type, mode, value );
            }
        }

        public void elementDecl( String name, String model ) throws SAXException
        {
            if ( declHandler != null )
            {
                declHandler.elementDecl( name, model );
            }
        }

        public void externalEntityDecl( String name, String publicId, String systemId ) throws SAXException
        {
            if ( declHandler != null )
            {
                declHandler.externalEntityDecl( name, publicId, systemId );
            }
        }

        public void internalEntityDecl( String name, String value ) throws SAXException
        {
            if ( declHandler != null )
            {
                internalEntityDecl( name, value );
            }
        }
    }

    private CisConfig cisConfig;

    /**
     * Returns the configuration, which is being applied.
     */
    public CisConfig getCisConfig()
    {
        return cisConfig;
    }

    /**
     * Sets the configuration, which is being applied.
     */
    public void setCisConfig( CisConfig pCisConfig )
    {
        cisConfig = pCisConfig;
    }

    /**
     * Called to transform the {@code cisconfig.xml} file. Reads the
     * original file from the given input source and writes it to the
     * given result.
     */
    public void transform( InputSource pInputSource, Result pResult ) throws CisCoreException
    {
        if ( cisConfig == null )
        {
            throw new CisCoreException( "The CIS Configuration has not been set." );
        }
        try
        {
            final SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating( false );
            spf.setNamespaceAware( true );
            final XMLFilter xmlFilter = new MyXMLFilter( cisConfig );
            xmlFilter.setParent( spf.newSAXParser().getXMLReader() );
            final SAXSource saxSource = new SAXSource( xmlFilter, pInputSource );
            final Transformer t = TransformerFactory.newInstance().newTransformer();
            t.transform( saxSource, pResult );
        }
        catch ( TransformerException e )
        {
            throw new CisCoreException( e.getMessage(), e );
        }
        catch ( ParserConfigurationException e )
        {
            throw new CisCoreException( e.getMessage(), e );
        }
        catch ( SAXException e )
        {
            throw new CisCoreException( e.getMessage(), e );
        }
    }
}
