package org.codehaus.mojo.hibernate3.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.project.path.DefaultPathTranslator;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;

public class HibernateExpressionEvaluator
    extends PluginParameterExpressionEvaluator
    implements ExpressionEvaluator
{
// --------------------------- CONSTRUCTORS ---------------------------

    public HibernateExpressionEvaluator( MavenSession session )
    {
        super( session, new MojoExecution( new MojoDescriptor() ), new DefaultPathTranslator(), null,
               session.getCurrentProject(), session.getExecutionProperties() );
    }

// -------------------------- OTHER METHODS --------------------------

    public String evaluateString( String expression )
    {
        try
        {
            return String.valueOf( evaluate( expression ) );
        }
        catch ( ExpressionEvaluationException e )
        {
            return null;
        }
    }
}
