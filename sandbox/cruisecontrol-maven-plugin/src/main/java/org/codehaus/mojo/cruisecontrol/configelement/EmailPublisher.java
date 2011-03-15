package org.codehaus.mojo.cruisecontrol.configelement;

/**
 * Copyright 2006 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Configurationobject for the (html)email publisher elements, or the
 * (html)email plugin element. 
 * 
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 *
 */
public class EmailPublisher
{
    String buildresultsurl;

    String defaultsuffix;

    boolean failasimportant;

    String mailhost = "localhost";

    int mailport = 25;

    String username;

    String password;

    String reportsuccess;

    String returnaddress = "noreply@maven-cruisecontrol.yourhost.com";

    String returnname;

    String skipusers;

    String spamwhilebroken;

    String subjectprefix;

    boolean usessl;

    boolean htmlemail;

    String[] failures;

    String[] successes;

    String charset;

    String css = "webapps/cruisecontrol/css/cruisecontrol.css";

    String logdir;

    String xsldir = "webapps/cruisecontrol/xsl";

    String xslfile;

    String xslfilelist;

    EmailMapper[] maps;

    public EmailPublisher( )
    {
    }
    
    public EmailPublisher( boolean useDefaults )
    {
        if ( !useDefaults )
        {
            xsldir = null;
            css = null;
            returnaddress = null;
            mailport = -1;
            mailhost = null;
        }
    }

    public String getBuildresultsurl()
    {
        return buildresultsurl;
    }

    public void setBuildresultsurl( String buildresultsurl )
    {
        this.buildresultsurl = buildresultsurl;
    }

    public String getDefaultsuffix()
    {
        return defaultsuffix;
    }

    public void setDefaultsuffix( String defaultsuffix )
    {
        this.defaultsuffix = defaultsuffix;
    }

    public boolean isFailasimportant()
    {
        return failasimportant;
    }

    public void setFailasimportant( boolean failasimportant )
    {
        this.failasimportant = failasimportant;
    }

    public String getMailhost()
    {
        return mailhost;
    }

    public void setMailhost( String mailhost )
    {
        this.mailhost = mailhost;
    }

    public int getMailport()
    {
        return mailport;
    }

    public void setMailport( int mailport )
    {
        this.mailport = mailport;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getReportsuccess()
    {
        return reportsuccess;
    }

    public void setReportsuccess( String reportsuccess )
    {
        this.reportsuccess = reportsuccess;
    }

    public String getReturnaddress()
    {
        return returnaddress;
    }

    public void setReturnaddress( String returnaddress )
    {
        this.returnaddress = returnaddress;
    }

    public String getReturnname()
    {
        return returnname;
    }

    public void setReturnname( String returnname )
    {
        this.returnname = returnname;
    }

    public String getSkipusers()
    {
        return skipusers;
    }

    public void setSkipusers( String skipusers )
    {
        this.skipusers = skipusers;
    }

    public String getSpamwhilebroken()
    {
        return spamwhilebroken;
    }

    public void setSpamwhilebroken( String spamwhilebroken )
    {
        this.spamwhilebroken = spamwhilebroken;
    }

    public String getSubjectprefix()
    {
        return subjectprefix;
    }

    public void setSubjectprefix( String subjectprefix )
    {
        this.subjectprefix = subjectprefix;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public boolean isUsessl()
    {
        return usessl;
    }

    public void setUsessl( boolean usessl )
    {
        this.usessl = usessl;
    }

    public String[] getFailures()
    {
        return failures;
    }

    public void setFailures( String[] failures )
    {
        this.failures = failures;
    }

    public String[] getSuccesses()
    {
        return successes;
    }

    public void setSuccesses( String[] successes )
    {
        this.successes = successes;
    }

    public String getCharset()
    {
        return charset;
    }

    public void setCharset( String charset )
    {
        this.charset = charset;
    }

    public String getCss()
    {
        return css;
    }

    public void setCss( String css )
    {
        this.css = css;
    }

    public String getLogdir()
    {
        return logdir;
    }

    public void setLogdir( String logdir )
    {
        this.logdir = logdir;
    }

    public String getXsldir()
    {
        return xsldir;
    }

    public void setXsldir( String xsldir )
    {
        this.xsldir = xsldir;
    }

    public String getXslfile()
    {
        return xslfile;
    }

    public void setXslfile( String xslfile )
    {
        this.xslfile = xslfile;
    }

    public String getXslfilelist()
    {
        return xslfilelist;
    }

    public void setXslfilelist( String xslfilelist )
    {
        this.xslfilelist = xslfilelist;
    }

    public boolean isHtmlemail()
    {
        return htmlemail;
    }

    public void setHtmlemail( boolean htmlemail )
    {
        this.htmlemail = htmlemail;
    }

    public EmailMapper[] getMaps()
    {
        return maps;
    }

    public void setMaps( EmailMapper[] maps )
    {
        this.maps = maps;
    }

}
