package org.codehaus.mojo.javancss;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javancss.Javancss;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.doxia.sink.Sink;

/**
 * Base abstract class for NCSSReport classes.<br>
 * It holds essentially helper methods on top of the Sink Doxia object.
 *
 * @author <a href="jeanlaurent@NOSPAMgmail.com>Jean-Laurent de Morlhon</a>
 * @version $Id$
 */
public abstract class AbstractNcssReportGenerator
{
    private ResourceBundle bundle;

    private Sink sink;

    private Log log;

    /**
     * build a new NcssReportGenerator.
     *
     * @param sink the sink that will be used for reporting.
     * @param bundle the correct RessourceBundle to be used for reporting.
     * @param log the log object enabling logging within maven plugins.
     */
    protected AbstractNcssReportGenerator( Sink sink, ResourceBundle bundle, Log log )
    {
        this.bundle = bundle;
        this.sink = sink;
        this.log = log;
    }

    /**
     * Getter for the Log instance.
     *
     * @return the current log instance associated with this report generator.
     */
    public Log getLog()
    {
        return this.log;
    }

    /**
     * Getter for the Sink instance.
     *
     * @return the current instance of Sink associated with this report generator.
     */
    public Sink getSink()
    {
        return this.sink;
    }

    /**
     * Getter for the RessourceBundle.
     *
     * @return the current ResourceBundle associated with this report generator.
     */
    public ResourceBundle getResourceBundle()
    {
        return this.bundle;
    }

    /**
     * sink helper to write a "code" itemList.
     *
     * @param text the text to write within the code tags.
     */
    protected void codeItemListHelper( String text )
    {
        sink.listItem();
        sink.monospaced();
        sink.text( text );
        sink.monospaced_();
        sink.listItem_();
    }

    /**
     * sink helper to write a paragraph
     *
     * @param text the text to write within the paragraph.
     */
    protected void paragraphHelper( String text )
    {
        sink.paragraph();
        sink.text( text );
        sink.paragraph_();
    }

    /**
     * sink helper to write a subtitle
     *
     * @param text the text to write as a subtitle.
     */
    protected void subtitleHelper( String text )
    {
        sink.paragraph();
        sink.bold();
        sink.text( text );
        sink.bold_();
        sink.paragraph_();
    }

    /**
     * sink helper to write cell containing code.
     *
     * @param text the text to write within a cell and within code tags.
     */
    protected void codeCellHelper( String text )
    {
        sink.tableCell();
        sink.monospaced();
        sink.text( text );
        sink.monospaced_();
        sink.tableCell_();
    }

    /**
     * sink helper to write a simple table header cell.
     *
     * @param text the text to write within a table header cell.
     */
    protected void headerCellHelper( String text )
    {
        sink.tableHeaderCell();
        sink.text( text );
        sink.tableHeaderCell_();
    }

    /**
     * sink helper to write a simple tabke cell.
     *
     * @param text the text to write within a table cell.
     */
    protected void tableCellHelper( String text )
    {
        sink.tableCell();
        sink.text( text );
        sink.tableCell_();
    }

    /**
     * sink helper to start a section.
     *
     * @param link the anchor link.
     * @param title the title of the anchor link.
     */
    protected void startSection( String link, String title )
    {
        sink.section1();
        sink.sectionTitle1();
        sink.text( bundle.getString( title ) );
        sink.sectionTitle1_();

        sink.anchor( bundle.getString( link ) );
        sink.text( bundle.getString( title ) );
        sink.anchor_();
    }

    /**
     * sink helper to end a section
     */
    protected void endSection()
    {
        sink.section1_();
    }

    /**
     * resource bundle helper to get a value.
     *
     * @param key the key for the desired string.
     * @return the string for the given key.
     */
    protected String getString( String key )
    {
        return bundle.getString( key );
    }

    /**
     * Output the report introduction.
     *
     * @param withNavigationBar a boolean stating wether or not the navigationBar should be displayed.
     */
    protected void doIntro( boolean withNavigationBar )
    {
        getSink().section1();
        getSink().sectionTitle1();
        getSink().text( getString( "report.javancss.main.title" ) );
        getSink().sectionTitle1_();
        if ( withNavigationBar )
        {
            navigationBar();
        }
        getSink().paragraph();
        String version = Javancss.class.getPackage().getSpecificationVersion();
        if ( version == null )
        {
            version = "unknown";
        }
        String[] args = { version };
        getSink().text( MessageFormat.format( getString( "report.javancss.main.text" ), args ) );
        getSink().lineBreak();
        getSink().link( "http://www.kclee.de/clemens/java/javancss/" );
        getSink().text( "JavaNCSS web site." );
        getSink().link_();
        getSink().paragraph_();
        getSink().section1_();
    }

    // print out the navigation bar
    protected void navigationBar()
    {
        getSink().paragraph();
        getSink().text( "[ " );
        getSink().link( "#" + getString( "report.javancss.package.link" ) );
        getSink().text( getString( "report.javancss.package.link" ) );
        getSink().link_();
        getSink().text( " ] [ " );
        getSink().link( "#" + getString( "report.javancss.object.link" ) );
        getSink().text( getString( "report.javancss.object.link" ) );
        getSink().link_();
        getSink().text( " ] [ " );
        getSink().link( "#" + getString( "report.javancss.function.link" ) );
        getSink().text( getString( "report.javancss.function.link" ) );
        getSink().link_();
        getSink().text( " ] [ " );
        getSink().link( "#" + getString( "report.javancss.explanation.link" ) );
        getSink().text( getString( "report.javancss.explanation.link" ) );
        getSink().link_();
        getSink().text( " ]" );
        getSink().paragraph_();
    }
}
