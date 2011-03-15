package org.apache.maven.plugin;

/*
 * LICENSE
 */

import com.thoughtworks.qdox.model.JavaParameter;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ApiParameter
{
    private JavaParameter parameter;

    public ApiParameter( JavaParameter parameter )
    {
        this.parameter = parameter;
    }

    public String getName()
    {
        return parameter.getName();
    }

    public String getType()
    {
        return parameter.getType().getValue();
    }
}
