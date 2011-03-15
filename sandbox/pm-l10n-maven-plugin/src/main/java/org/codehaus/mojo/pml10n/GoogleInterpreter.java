package org.codehaus.mojo.pml10n;
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

/**
 * An interpreter that uses Google's Translation API to translate text.
 *
 * @author Stephen Connolly
 * @since 03-Oct-2009 14:23:01
 */
public class GoogleInterpreter
    implements Interpreter
{
    private static final Map/*<Locale, String>*/ googleLanguages = getLocaleLanguageMap();

    private static final Map/*<Locale, String>*/ getLocaleLanguageMap()
    {
        Map/*<Locale, String>*/ result = new HashMap/*<Locale, String>*/();
        result.put( new Locale( "af" ), "af" );  // AFRIKAANS
        result.put( new Locale( "sq" ), "sq" ); // ALBANIAN
        result.put( new Locale( "am" ), "am" );  // AMHARIC
        result.put( new Locale( "ar" ), "ar" ); // ARABIC 
        result.put( new Locale( "hy" ), "hy" ); // ARMENIAN 
        result.put( new Locale( "az" ), "az" ); // AZERBAIJANI
        result.put( new Locale( "eu" ), "eu" ); // BASQUE
        result.put( new Locale( "be" ), "be" ); // BELARUSIAN
        result.put( new Locale( "bn" ), "bn" ); // BENGALI
        result.put( new Locale( "bh" ), "bh" ); // BIHARI
        result.put( new Locale( "bg" ), "bg" ); // BULGARIAN
        result.put( new Locale( "my" ), "my" ); // BURMESE
        result.put( new Locale( "ca" ), "ca" ); // CATALAN
        result.put( new Locale( "chr" ), "chr" ); // CHEROKEE
        result.put( new Locale( "zh" ), "zh" ); // CHINESE 
        result.put( new Locale( "zh", "CN" ), "zh-CN" ); // CHINESE_SIMPLIFIED
        result.put( new Locale( "zh", "TW" ), "zh-TW" ); // CHINESE_TRADITIONAL
        result.put( new Locale( "hr" ), "hr" ); // CROATIAN 
        result.put( new Locale( "cs" ), "cs" ); // CZECH 
        result.put( new Locale( "da" ), "da" ); // DANISH 
        result.put( new Locale( "dv" ), "dv" ); // DHIVEHI 
        result.put( new Locale( "nl" ), "nl" ); // DUTCH 
        result.put( new Locale( "en" ), "en" ); // ENGLISH 
        result.put( new Locale( "eo" ), "eo" ); // ESPERANTO 
        result.put( new Locale( "et" ), "et" ); // ESTONIAN 
        result.put( new Locale( "tl" ), "tl" ); // FILIPINO 
        result.put( new Locale( "fi" ), "fi" ); // FINNISH 
        result.put( new Locale( "fr" ), "fr" ); // FRENCH 
        result.put( new Locale( "gl" ), "gl" ); // GALACIAN 
        result.put( new Locale( "ka" ), "ka" ); // GEORGIAN 
        result.put( new Locale( "de" ), "de" ); // GERMAN 
        result.put( new Locale( "el" ), "el" ); // GREEK 
        result.put( new Locale( "gn" ), "gn" ); // GUARANI 
        result.put( new Locale( "gu" ), "gu" ); // GUJARATI 
        result.put( new Locale( "iw" ), "iw" ); // HEBREW 
        result.put( new Locale( "hi" ), "hi" ); // HINDI
        result.put( new Locale( "hu" ), "hu" ); // HUNGARIAN 
        result.put( new Locale( "is" ), "is" ); // ICELANDIC 
        result.put( new Locale( "id" ), "id" ); // INDONESIAN 
        result.put( new Locale( "iu" ), "iu" ); // INUKTITUT 
        result.put( new Locale( "ga" ), "ga" ); // IRISH 
        result.put( new Locale( "it" ), "it" ); // ITALIAN 
        result.put( new Locale( "ja" ), "ja" ); // JAPANESE 
        result.put( new Locale( "kn" ), "kn" ); // KANNADA 
        result.put( new Locale( "kk" ), "kk" ); // KAZAKH 
        result.put( new Locale( "km" ), "km" ); // KHMER 
        result.put( new Locale( "ko" ), "ko" ); // KOREAN 
        result.put( new Locale( "ku" ), "ku" ); // KURDISH 
        result.put( new Locale( "ky" ), "ky" ); // KYRGYZ 
        result.put( new Locale( "lo" ), "lo" ); // LAOTHIAN 
        result.put( new Locale( "lv" ), "lv" ); // LATVIAN 
        result.put( new Locale( "lt" ), "lt" ); // LITHUANIAN 
        result.put( new Locale( "mk" ), "mk" ); // MACEDONIAN 
        result.put( new Locale( "ms" ), "ms" ); // MALAY 
        result.put( new Locale( "ml" ), "ml" ); // MALAYALAM 
        result.put( new Locale( "mt" ), "mt" ); // MALTESE 
        result.put( new Locale( "mr" ), "mr" ); // MARATHI 
        result.put( new Locale( "mn" ), "mn" ); // MONGOLIAN 
        result.put( new Locale( "ne" ), "ne" ); // NEPALI 
        result.put( new Locale( "no" ), "no" ); // NORWEGIAN 
        result.put( new Locale( "or" ), "or" ); // ORIYA 
        result.put( new Locale( "ps" ), "ps" ); // PASHTO 
        result.put( new Locale( "fa" ), "fa" ); // PERSIAN 
        result.put( new Locale( "pl" ), "pl" ); // POLISH 
        result.put( new Locale( "pt" ), "pt" ); // PORTUGUESE 
        result.put( new Locale( "pa" ), "pa" ); // PUNJABI 
        result.put( new Locale( "ro" ), "ro" ); // ROMANIAN                                                 
        result.put( new Locale( "ru" ), "ru" ); // RUSSIAN 
        result.put( new Locale( "sa" ), "sa" ); // SANSKRIT 
        result.put( new Locale( "sr" ), "srf" ); // SERBIAN 
        result.put( new Locale( "sd" ), "sd" ); // SINDHI 
        result.put( new Locale( "si" ), "si" ); // SINHALESE 
        result.put( new Locale( "sk" ), "sk" ); // SLOVAK 
        result.put( new Locale( "sl" ), "sl" ); // SLOVENIAN 
        result.put( new Locale( "es" ), "es" ); // SPANISH 
        result.put( new Locale( "sw" ), "sw" ); // SWAHILI 
        result.put( new Locale( "sv" ), "sv" ); // SWEDISH 
        result.put( new Locale( "tg" ), "tg" ); // TAJIK 
        result.put( new Locale( "ta" ), "ta" ); // TAMIL 
        result.put( new Locale( "tl" ), "tl" ); // TAGALOG 
        result.put( new Locale( "te" ), "te" ); // TELUGU 
        result.put( new Locale( "th" ), "th" ); // THAI 
        result.put( new Locale( "bo" ), "bo" ); // TIBETAN 
        result.put( new Locale( "tr" ), "tr" ); // TURKISH 
        result.put( new Locale( "uk" ), "uk" ); // UKRANIAN 
        result.put( new Locale( "ur" ), "ur" ); // URDU 
        result.put( new Locale( "uz" ), "uz" ); // UZBEK 
        result.put( new Locale( "ug" ), "ug" ); // UIGHUR 
        result.put( new Locale( "vi" ), "vi" ); // VIETNAMESE 
        result.put( new Locale( "cy" ), "cy" ); // WELSH 
        result.put( new Locale( "yi" ), "yi" ); // YIDDISH 
        return result;
    }

    private final String referrer;

    private final String urlPattern;

    private static final String UTF8 = "UTF-8";

    public GoogleInterpreter( String referrer )
        throws IOException
    {
        this.referrer = referrer;
        final Properties properties = new Properties();
        final InputStream inputStream = getClass().getResourceAsStream( getClass().getSimpleName() + ".properties" );
        try
        {
            properties.load( inputStream );
        }
        finally
        {
            inputStream.close();
        }
        urlPattern = properties.getProperty( "url" );
    }

    public String translate( String text, Locale source, Locale destination )
        throws IOException
    {
        List parameters = new ArrayList();
        StringBuffer textBuffer = new StringBuffer( text.length() );
        int i0 = 0;
        int i1 = text.indexOf( '{', i0 );
        int i2;
        while (i1 != -1 && (i2 = text.indexOf( '}', i1 )) != -1) {
            textBuffer.append(text.substring( i0, i1 ));
            textBuffer.append("____");
            textBuffer.append(parameters.size());
            textBuffer.append("____");
            parameters.add( text.substring(i1, i2 + 1 ));
            i0 = i2 + 1;
            i1 = text.indexOf( '{', i0 );            
        }
        textBuffer.append( text.substring(i0 ));
        
        System.out.println( textBuffer );
        
        URL url = new URL( MessageFormat.format( urlPattern,
                                                 new Object[]{URLEncoder.encode( fromLocale( source ), UTF8 ),
                                                     URLEncoder.encode( fromLocale( destination ), UTF8 ),
                                                     URLEncoder.encode( textBuffer.toString(), UTF8 )} ) );
        JSONObject response = retrieveJSON( url );
        try
        {
            if ( response == null || response.getInt( "responseStatus" ) != 200 )
            {
                throw new IOException( "Invalid response" );
            }

            String result = StringEscapeUtils.unescapeHtml(
                URLDecoder.decode( response.getJSONObject( "responseData" ).getString( "translatedText" ), UTF8 ) );
            for (int i = 0; i < parameters.size(); i++) {
                result = StringUtils.replace(  result, "____"+i+"____", (String)parameters.get( i ));
            }
            return result;
        }
        catch ( JSONException e )
        {
            IOException ioe = new IOException( e.getMessage() );
            ioe.initCause( e );
            throw ioe;
        }
    }

    private String fromLocale( Locale source )
    {
        String googleLanguage = (String) googleLanguages.get( source );
        if ( googleLanguage == null )
        {
            googleLanguage = (String) googleLanguages.get( new Locale( source.getLanguage(), source.getCountry() ) );
        }
        if ( googleLanguage == null )
        {
            googleLanguage = (String) googleLanguages.get( new Locale( source.getLanguage() ) );
        }
        return googleLanguage;
    }

    JSONObject retrieveJSON( final URL url )
        throws IOException
    {
        final HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        uc.setRequestProperty( "referer", referrer );
        uc.setDoOutput( false );
        try
        {
            return new JSONObject( IOUtil.toString( uc.getInputStream() ) );
        }
        catch ( JSONException e )
        {
            IOException ioe = new IOException( e.getMessage() );
            ioe.initCause( e );
            throw ioe;
        }
        finally
        {
            uc.getInputStream().close();
            if ( uc.getErrorStream() != null )
            {
                uc.getErrorStream().close();
            }
        }
    }

}
