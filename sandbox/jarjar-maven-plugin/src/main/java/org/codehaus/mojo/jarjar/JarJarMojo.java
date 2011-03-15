package org.codehaus.mojo.jarjar;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.Plugin;
import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.plugin.PluginExecutionResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* ====================================================================
 *   Copyright 2001-2004 The Apache Software Foundation.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * ====================================================================
 */

/**
 * @goal java
 *
 * @phase process-classes
 *
 * @description Bundles dependencies into compiled classes and renames packages.
 *
 * @parameter name="outputDirectory"
 * type="String"
 * required="true"
 * validator=""
 * expression="#project.build.outputDirectory"
 * description=""
 *
 * @parameter name="classpathIncludes"
 * type="String"
 * required="false"
 * validator=""
 * expression="#classpathIncludes"
 * description="The comma-delimited list of included classpath elements in the format groupId:artifactId"
 *
 * @parameter name="classpathExcludes"
 * type="String"
 * required="false"
 * validator=""
 * expression="#classpathExcludes"
 * description="The comma-delimited list of excluded classpath elements in the format groupId:artifactId"
 *
 * @parameter name="classpathElements"
 * type="java.util.List"
 * required="true"
 * validator=""
 * expression="#project.runtimeClasspathElements"
 * description="The project's runtime classpath elements."
 *
 * @parameter
 * name="rules"
 * type="java.lang.String"
 * required="false"
 * validator=""
 * expression="#rules"
 * description="The comma-delimited rule mapping where format is 'searchPattern=replacePattern"
 *
 * @parameter
 * name="zaps"
 * type="java.lang.String"
 * required="false"
 * validator=""
 * expression="#zaps"
 * description="Comma-delimited list of zap patterns."
 *
 * @parameter
 * name="kills"
 * type="java.lang.String"
 * required="false"
 * validator=""
 * expression="#kills"
 * description="Comma-delimited list of kill patterns. Not sure what this is for currently."
 * 
 * @parameter
 * name="ruleFile"
 * type="java.lang.String"
 * required="false"
 * validator=""
 * expression="#basedir/#ruleFile"
 * description="File path (relative to project basedir) that contains all
 *   rules, zaps, and kills to be used"
 *
 * @author jdcasey
 * @version $Id$
 */
public class JarJarMojo
    implements Plugin
{

    public void execute( PluginExecutionRequest request, PluginExecutionResponse response ) throws Exception
    {
        String outputDirectory = (String)request.getParameter("outputDirectory");
        List classpathElements = (List)request.getParameter("classpathElements");
        
        String classpathIncludes = (String)request.getParameter("classpathIncludes");
        List cpIncludeList = null;
        if(classpathIncludes != null)
        {
            cpIncludeList = parseCommaDelimitedList(classpathIncludes);
        }
        else
        {
            cpIncludeList = Collections.EMPTY_LIST;
        }
        
        String classpathExcludes = (String)request.getParameter("classpathExcludes");
        List cpExcludeList = null;
        if(classpathExcludes != null)
        {
            cpExcludeList = parseCommaDelimitedList(classpathExcludes);
        }
        else
        {
            cpExcludeList = Collections.EMPTY_LIST;
        }
        
        createJarJarStructs(outputDirectory, classpathElements, cpIncludeList, cpExcludeList);
        
        String rulePath = (String)request.getParameter("ruleFile");
        if(rulePath != null)
        {
            File ruleFile = new File(rulePath);
        }
        else
        {
            String rules = (String)request.getParameter("rules");
            Map ruleMap = null;
            if(rules != null)
            {
                ruleMap = parseCommaDelimitedMap(rules);
            }
            else
            {
                ruleMap = Collections.EMPTY_MAP;
            }
            
            String zaps = (String)request.getParameter("zaps");
            List zapList = null;
            if(zaps != null)
            {
                zapList = parseCommaDelimitedList(zaps);
            }
            else
            {
                zapList = Collections.EMPTY_LIST;
            }
            
            String kills = (String)request.getParameter("kills");
            List killList = null;
            if(kills != null)
            {
                killList = parseCommaDelimitedList(kills);
            }
            else
            {
                killList = Collections.EMPTY_LIST;
            }
        }
    }
    
    private List createJarJarStructs( String outputDirectory, List classpathElements, List includes, List excludes )
        throws IOException
    {
        List toExtract = new ArrayList();
        for ( Iterator it = classpathElements.iterator(); it.hasNext(); )
        {
            Artifact cpArtifact = (Artifact) it.next();
            String id = cpArtifact.getGroupId() + ":" + cpArtifact.getArtifactId();
            
            // How do we compute classpath entries to include?
            // 1. If entry is in includes, ADD
            // 2. If includes are empty, and entry is not in excludes, ADD.
            // 3. If both includes and excludes are empty, ADD.
            if(includes.contains(id))
            {
                toExtract.add(cpArtifact);
            }
            else if(includes.isEmpty() && (excludes.isEmpty() || !excludes.contains(id)))
            {
                toExtract.add(cpArtifact);
            }
        }
        
        List structs = new ArrayList();
        for ( Iterator it = toExtract.iterator(); it.hasNext(); )
        {
            Artifact artifact = (Artifact) it.next();
            JarFile jarFile = new JarFile(artifact.getFile());
            for(Enumeration entries = jarFile.entries(); entries.hasMoreElements(); )
            {
                JarEntry entry = (JarEntry)entries.nextElement();
                
                File outFile = new File(outputDirectory, entry.getName());
                outFile.mkdirs();
                
            }
        }
        
        return structs;
    }

    private List parseCommaDelimitedList(String input)
    {
        StringTokenizer tokenizer = new StringTokenizer(",");
        List result = new ArrayList();
        
        while(tokenizer.hasMoreTokens())
        {
            String token = tokenizer.nextToken();
            if(token != null)
            {
                token = token.trim();
            }
            
            result.add(token);
        }
        
        return result;
    }

    private Map parseCommaDelimitedMap(String input)
    {
        StringTokenizer tokenizer = new StringTokenizer(",");
        Map result = new TreeMap();
        
        while(tokenizer.hasMoreTokens())
        {
            String token = tokenizer.nextToken();
            if(token != null)
            {
                token = token.trim();
            }
            
            Pattern mappingPattern = Pattern.compile("([^=]+)=(.+)");
            Matcher matcher = mappingPattern.matcher(token);
            if(matcher.matches())
            {
                String key = matcher.group(1);
                String value = matcher.group(2);
                
                result.put(key, value);
            }
        }
        
        return result;
    }

}