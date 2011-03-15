package org.codehaus.mojo.hibernate3.configuration;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.util.ReflectHelper;
import org.codehaus.mojo.hibernate3.HibernateUtils;

import java.io.File;
import java.lang.reflect.Constructor;

public class JDBCComponentConfiguration
    extends AbstractComponentConfiguration
{
// --------------------- Interface ComponentConfiguration ---------------------

    public String getName()
    {
        return "jdbcconfiguration";
    }

    protected Configuration createConfiguration()
    {
        return new JDBCMetaDataConfiguration();
    }

    protected void doConfiguration( Configuration configuration )
    {
        JDBCMetaDataConfiguration jmdc = (JDBCMetaDataConfiguration) configuration;

        super.doConfiguration( jmdc );

        jmdc.setPreferBasicCompositeIds( getExporterMojo().getComponentProperty( "preferbasiccompositeids", true ) );

        ReverseEngineeringStrategy strategy = new DefaultReverseEngineeringStrategy();

        strategy = loadRevengFile( strategy );

        strategy = loadReverseStrategy( strategy );

        ReverseEngineeringSettings qqsettings = new ReverseEngineeringSettings( strategy );

        String packageName = getExporterMojo().getComponentProperty( "packagename" );
        if ( packageName != null )
        {
            qqsettings.setDefaultPackageName( packageName );
        }

        String detectManyToMany = getExporterMojo().getComponentProperty( "detectmanytomany" );
        if ( "false".equals( detectManyToMany ) )
        {
            qqsettings.setDetectManyToMany( false );
        }

        String detectOptimisticLock = getExporterMojo().getComponentProperty( "detectoptmisticlock" );
        if ( "false".equals( detectOptimisticLock ) )
        {
            qqsettings.setDetectOptimisticLock( false );
        }

        strategy.setSettings( qqsettings );

        jmdc.setReverseEngineeringStrategy( strategy );
        jmdc.readFromJDBC();
    }

    private ReverseEngineeringStrategy loadRevengFile( ReverseEngineeringStrategy delegate )
    {
        String revengFile = getExporterMojo().getComponentProperty( "revengfile" );
        if ( revengFile != null )
        {
            OverrideRepository or = new OverrideRepository();

            File rf = HibernateUtils.getFile( getExporterMojo().getProject().getBasedir(), revengFile );
            if ( rf == null )
            {
                getExporterMojo().getLog().info( "Couldn't find the revengfile in the project. Trying absolute path." );
                rf = HibernateUtils.getFile( null, revengFile );
            }

            if ( rf != null )
            {
                or.addFile( rf );
                return or.getReverseEngineeringStrategy( delegate );
            }
        }
        return delegate;
    }

    private ReverseEngineeringStrategy loadReverseStrategy( ReverseEngineeringStrategy delegate )
    {
        String reverseStrategy = getExporterMojo().getComponentProperty( "reversestrategy" );
        if ( reverseStrategy != null )
        {
            try
            {
                Class clazz = ReflectHelper.classForName( reverseStrategy );
                //noinspection RedundantArrayCreation
                Constructor constructor = clazz.getConstructor( new Class[]{ReverseEngineeringStrategy.class} );
                //noinspection RedundantArrayCreation
                return (ReverseEngineeringStrategy) constructor.newInstance( new Object[]{delegate} );
            }
            catch ( NoSuchMethodException e )
            {
                try
                {
                    getExporterMojo().getLog().info( "Could not find public " + reverseStrategy +
                        "(ReverseEngineeringStrategy delegate) constructor on ReverseEngineeringStrategy. Trying no-arg version." );
                    Class clazz = ReflectHelper.classForName( reverseStrategy );
                    ReverseEngineeringStrategy rev = (ReverseEngineeringStrategy) clazz.newInstance();
                    getExporterMojo().getLog().info(
                        "Using non-delegating strategy, thus packagename and revengfile will be ignored." );
                    return rev;
                }
                catch ( Exception eq )
                {
                    getExporterMojo().getLog().error(
                        "Could not create or find " + reverseStrategy + " with default no-arg constructor", eq );
                }
            }
            catch ( Exception e )
            {
                getExporterMojo().getLog().error(
                    "Could not create or find " + reverseStrategy + " with one argument delegate constructor", e );
            }
        }
        return delegate;
    }
}