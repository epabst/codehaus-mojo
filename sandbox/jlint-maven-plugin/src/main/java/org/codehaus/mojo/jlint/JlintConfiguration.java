package org.codehaus.mojo.jlint;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.resource.ResourceManager;
import org.codehaus.plexus.resource.loader.FileResourceCreationException;

public class JlintConfiguration
{
    private ResourceManager locator;

    private String configLocation;

    private String configFile;

    private ArrayList<String> rules;

    private Log logger;

    public JlintConfiguration( ResourceManager locator, String configLocation, Log logger )
    {
        this.locator = locator;
        this.configLocation = configLocation;
        this.logger = logger;
    }

    public void loadConfiguration()
    {
        try
        {
            configFile = getConfigFile();
        }
        catch ( MavenReportException e )
        {
            logger.error( e.toString() );
            System.exit( -1 );
        }

        JlintXmlConfigReader configReader = new JlintXmlConfigReader( configFile );
        rules = configReader.readConfiguration();
    }

    private String getConfigFile()
        throws MavenReportException
    {
        try
        {
            File configFile = locator.getResourceAsFile( configLocation, "jlint-config.xml" );

            if ( configFile == null )
            {
                throw new MavenReportException( "Unable to process config location: " + configLocation );
            }
            return configFile.getAbsolutePath();
        }
        catch ( org.codehaus.plexus.resource.loader.ResourceNotFoundException e )
        {
            throw new MavenReportException( "Unable to find configuration file at location " + configLocation, e );
        }
        catch ( FileResourceCreationException e )
        {
            throw new MavenReportException( "Unable to process configuration file location " + configLocation, e );
        }
    }

    public String getCategoriesToDisable( ArrayList<JlintMessageItem> rules )
    {
        Set<String> configuredRules = getConfiguredRules( configFile );
        Set<String> allRules = getAllRules( rules );
        Iterator<String> rulesIterator = allRules.iterator();

        StringBuilder resultCommand = new StringBuilder( " " );

        while ( rulesIterator.hasNext() )
        {

            String category = rulesIterator.next();

            // DEBUG
            // System.out.println("All Rule: " + category);
            // System.out.println("[" + configuredRules.contains(category) + "]");

            if ( configuredRules.contains( category ) == Boolean.FALSE )
            {
                resultCommand.append( "-" );
                resultCommand.append( category.toLowerCase() );
                resultCommand.append( " " );
            }
        }

        return resultCommand.toString();
    }

    public Set<String> getConfiguredRules( String configFilePath )
    {
        // Read XML input file here
        HashSet<String> configuredRuleSet = new HashSet<String>();

        loadConfiguration();

        for ( String rule : rules )
        {
            configuredRuleSet.add( rule );
        }

        // tmp.add("WEAK_CMP");
        // tmp.add("NOT_OVERRIDDEN");

        return configuredRuleSet;
    }

    public HashSet<String> getAllRules( ArrayList<JlintMessageItem> rules )
    {

        HashSet<String> allRules = new HashSet<String>();

        for ( JlintMessageItem ci : rules )
        {
            allRules.add( ci.getCategory() );
        }

        return allRules;
    }

}
