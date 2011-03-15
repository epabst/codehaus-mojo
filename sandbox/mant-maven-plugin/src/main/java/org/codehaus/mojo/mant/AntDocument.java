package org.codehaus.mojo.mant;

import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Used to create an ant document from the given node.
 */
public class AntDocument
{
    //TODO might be a lot tidier if a stylesheet is used instead - maybe slower though
    private static final String TARGET_NAME = "run";

    public static final String PATH_ID = "project.class.path";

    private final String basedir;

    private final String classname;

    private final List classpath;

    private final Element task;

    private Document document;

    /**
     * Use this constructor for core tasks
     * @param basedir
     * @param task
     */
    public AntDocument( String basedir, Element task )
    {
        this( basedir, null, null, task );
    }

    /**
     * Use this constructor for optional tasks.
     * @param basedir
     * @param classname
     * @param classpath
     * @param task
     */
    public AntDocument( String basedir, String classname, List classpath, Element task )
    {
        this.basedir = basedir;
        this.classname = classname;
        this.classpath = classpath;
        this.task = task;
        this.document = createDocument();
    }

    public Document getDocument()
    {
        return document;
    }

    /**
     * Constructs the document.
     * Delegates to createXXX methods to create children.
     * @return
     */
    private Document createDocument()
    {
        Document document = DocumentHelper.createDocument();
        Element project = document.addElement( "project" );
        project.addAttribute( "default", TARGET_NAME );
        project.addAttribute( "basedir", basedir );

        createPath( project );
        createTarget( project );
        return document;
    }

    private void createTarget( Element parent )
    {
        Element target = parent.addElement( "target" );
        target.addAttribute( "name", TARGET_NAME );
        createTaskdef( target );
        target.add( task );
    }

    private void createTaskdef( Element parent )
    {
        if ( isOptional() )
        {
            Element taskdef = parent.addElement( "taskdef" );
            taskdef.addAttribute( "name", task.getName() );
            taskdef.addAttribute( "classname", classname );
            taskdef.addAttribute( "classpathref", PATH_ID );
        }
    }

    private void createPath( Element parent )
    {
        if ( isOptional() )
        {
            Element path = parent.addElement( "path" );
            path.addAttribute( "id", PATH_ID );
            Iterator allPaths = classpath.iterator();
            while ( allPaths.hasNext() )
            {
                String currentPath = (String) allPaths.next();
                path.addElement( "pathelement" ).addAttribute( "location", currentPath );
            }
        }
    }

    /**
     * Whether or not this is building an optional task.
     * If yes then taskdef task and classpath will be created.
     * @return
     */
    private boolean isOptional()
    {
        return classname != null;
    }

}
