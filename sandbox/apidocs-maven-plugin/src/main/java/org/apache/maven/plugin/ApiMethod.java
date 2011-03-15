package org.apache.maven.plugin;

/*
 * LICENSE
 */

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ApiMethod
{
    private JavaMethod method;

    public ApiMethod( JavaMethod method )
    {
        this.method = method;
    }

    public String getAccessLevel()
    {
        if ( method.isPublic() )
        {
            return "public";
        }
        else if ( method.isProtected() )
        {
            return "protected";
        }
        else if ( method.isPrivate() )
        {
            return "private";
        }
        else
        {
            return "package";
        }
    }

    public String getName()
    {
        return method.getName();
    }

    public List getParameters()
    {
        JavaParameter[] parameters = method.getParameters();

        List list = new ArrayList();

        for ( int i = 0; i < parameters.length; i++ )
        {
            list.add( new ApiParameter( parameters[ i ] ) );
        }

        return list;
    }
}
