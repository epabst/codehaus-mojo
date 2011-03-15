package org.codehaus.mojo.graphing.model.factory;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ResolutionListener;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.logging.Log;

/**
 * Debug Resolution Listener, suitable for use from plugin. 
 */
public class DebugResolutionListener
    implements ResolutionListener
{
    private Log log;

    private String indent = "";

    public DebugResolutionListener( Log logger )
    {
        this.log = logger;
    }

    public void testArtifact( Artifact node )
    {
    }

    public void startProcessChildren( Artifact artifact )
    {
        indent += "  ";
    }

    public void endProcessChildren( Artifact artifact )
    {
        indent = indent.substring( 2 );
    }

    public void includeArtifact( Artifact artifact )
    {
        log.debug( indent + artifact + " (selected for " + artifact.getScope() + ")" );
    }

    public void omitForNearer( Artifact omitted, Artifact kept )
    {
        log.debug( indent + omitted + " (removed - nearer found: " + kept.getVersion() + ")" );
    }

    public void omitForCycle( Artifact omitted )
    {
        log.debug( indent + omitted + " (removed - causes a cycle in the graph)" );
    }

    public void updateScopeCurrentPom( Artifact artifact, String scope )
    {
        log.debug( indent + artifact + " (not setting scope to: " + scope + "; local scope " + artifact.getScope() +
            " wins)" );
    }

    public void updateScope( Artifact artifact, String scope )
    {
        log.debug( indent + artifact + " (setting scope to: " + scope + ")" );
    }

    public void selectVersionFromRange( Artifact artifact )
    {
        log.debug( indent + artifact + " (setting version to: " + artifact.getVersion() + " from range: " +
            artifact.getVersionRange() + ")" );
    }

    public void restrictRange( Artifact artifact, Artifact replacement, VersionRange newRange )
    {
        log.debug( indent + artifact + " (range restricted from: " + artifact.getVersionRange() + " and: " +
            replacement.getVersionRange() + " to: " + newRange + " )" );
    }

    public void manageArtifact( Artifact artifact, Artifact replacement )
    {
        String msg = indent + artifact;
        msg += " (";
        if ( replacement.getVersion() != null )
        {
            msg += "applying version: " + replacement.getVersion() + ";";
        }
        if ( replacement.getScope() != null )
        {
            msg += "applying scope: " + replacement.getScope();
        }
        msg += ")";
        log.debug( msg );
    }
}
