package org.codehaus.mojo.mant;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.dom4j.DocumentException;

/**
 * Class to handle execution of an ant task.
 * For an optional task a taskdef will be created using
 * a constructor supplied class and the dependecies for the
 * corresponding classpath.
 * A build.xml temp file is created and executed with the ant
 * api.
 */
public class MantGoal
{
    public static final String JAVA = "JAVA";
    
    public static final String RES = "RES";
    
    public static final String JAVA_GEN = "JAVA_GEN";
    
    public static final String RES_GEN = "RES_GEN";
    
    public static final String WEB_INF_GEN = "WEB_INF_GEN";
    
    public static final String META_INF_GEN = "META_INF_GEN";
    
    private final MavenProject project;

    private final Mojo mojo;

    private String taskdefClass;

    private String task;

    private String[] mappings;
    
    private List classpath;
    
    private HashMap replacements;

    /**
     * Creates this goal using the given mojo and project information.
     * Also supplied is the xml task to be run along with the mappings that
     * specify which maven property to be substituted for the ant attribute.
     * If the task is optional then the a non null taskdef class may be passed in.
     * @param mojo
     * @param project
     * @param taskdefClass
     * @param task
     * @param mappings
     */
    public MantGoal( Mojo mojo, MavenProject project, String taskdefClass, String task, String[] mappings )
    {
        this.mojo = mojo;
        this.project = project;
        this.taskdefClass = taskdefClass;
        this.task = task;
        this.replacements = createReplacements();
        this.mappings = createMappings(mappings);
        this.classpath = createClasspath();
    }
    
    /**
     * Fills up the replacements map.
     * @return
     */
    private HashMap createReplacements()
    {
        HashMap constants = new HashMap();
        constants.put(JAVA, new File(project.getBasedir(), "src/main/java").getPath());
        constants.put(RES, new File(project.getBasedir(), "src/main/resources").getPath());
        constants.put(JAVA_GEN, new File(project.getBuild().getDirectory(), "generated/src/main/java").getPath());
        constants.put(RES_GEN, new File(project.getBuild().getDirectory(), "generated/src/main/resources").getPath());
        constants.put(WEB_INF_GEN, new File(project.getBuild().getDirectory(), "generated/src/main/resources/WEB-INF").getPath());
        constants.put(META_INF_GEN, new File(project.getBuild().getDirectory(), "generated/src/main/resources/META-INF").getPath());
        return constants;
    }

    /**
     * Replaces the tokens in the mappings with their actual maven values.
     * For example if a maven value is "JAVA" it might be replaced
     * by "/Users/proj/src/main/java", known only at runtime, as they depend
     * on the current project object.
     * @param mappings
     * @return
     */
    private String[] createMappings(String[] mappings)
    {
        for ( int i = 1; i < mappings.length; i += 2 )
        {
            String mavenProperty = mappings[i];
            mappings[i] = (String) replacements.get(mavenProperty);
        }
        return mappings;
    }

    /**
     * Executes the ant task whilst the given system property has the classpath string set.
     * This is really just to support xdoclet that for some reason doesn't work with taskdef classpath.
     * @param classpathProperty TODO
     * @throws MojoExecutionException 
     * @throws DocumentException
     * @throws Exception
     */
    public void execute(String classpathProperty) throws MojoExecutionException
    {
        String value = getClasspathString();
        mojo.getLog().info("setting the following system property:");
        mojo.getLog().info(classpathProperty);
        mojo.getLog().info(value);

        System.setProperty( classpathProperty, value );
        execute();
        System.getProperties().remove( classpathProperty );
    }

    /**
     * Executes the underlying ant task.
     * @throws MojoExecutionException
     */
    public void execute()
        throws MojoExecutionException
    {
        try
        {
            executeTask();
            updateProject();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException("goal failed", e);
        }
    }

    /**
     * Executes the ant task by creating the build.xml file and running it through ant.
     * @throws Exception
     */
    public void executeTask()
        throws Exception
    {
        AntTask antTask = new AntTask( task, mappings );
        AntDocument antDocument = new AntDocument( project.getBasedir().getPath(), taskdefClass,
                                                   classpath, antTask.getTask() );
        AntProject antProject = new AntProject( antDocument.getDocument() );
        mojo.getLog().info( antProject.toString() );
        antProject.execute();
    }

    /**
     * Updates the resource and sources project directories to reflect generated code.
     *
     */
    public void updateProject()
    {
        project.addCompileSourceRoot( getJavaGen() );
        Resource resource = new Resource();
        resource.setDirectory( getResGen() );
        project.addResource( resource );
    }

    /**
     * Creates the taskdef classpath from the project artifact dependencies.
     * @return
     */
    private List createClasspath()
    {
        ArrayList classpath = new ArrayList();

        Iterator allArtifacts = project.getArtifacts().iterator();
        while ( allArtifacts.hasNext() )
        {
            Artifact artifact = (Artifact) allArtifacts.next();
            classpath.add( artifact.getFile().getPath() );
        }

        return classpath;
    }
    
    /**
     * Gets the classpath as a single string and always has a colon
     * (or OS equivalent) on the end, when non empty.
     * @return
     */
    public String getClasspathString() {
        StringBuffer pathString = new StringBuffer();
        ListIterator allPaths = classpath.listIterator();
        while ( allPaths.hasNext() )
        {
            String path = (String) allPaths.next();
            pathString.append(path);
            pathString.append(File.pathSeparatorChar);
        }
        return pathString.toString();
    }
    
    public String getJava() {
        return (String) replacements.get(JAVA);
    }
    
    public String getRes() {
        return (String) replacements.get(RES);
    }
    
    public String getJavaGen() {
        return (String) replacements.get(JAVA_GEN);
    }
    
    public String getResGen() {
        return (String) replacements.get(RES_GEN);
    }
    
    public String getWebInfGen() {
        return (String) replacements.get(WEB_INF_GEN);
    }
    
    public String getMetaInfGen() {
        return (String) replacements.get(META_INF_GEN);
    }
}