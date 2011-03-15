package org.codehaus.mojo.jslint;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.Scanner;
import org.sonatype.plexus.build.incremental.BuildContext;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLint;
import com.googlecode.jslint4java.JSLintBuilder;
import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.Option;

/**
 * Mojo to invoke JSLint verification on JS sources.
 */
/**
 * @author huntc
 */
public abstract class AbstractJSLintMojo
    extends AbstractMojo
{
    /**
     * The current project scope.
     */
    protected enum Scope
    {
        /** */
        COMPILE,
        /** */
        TEST
    };

    /**
     * @parameter default-value="**\/*.js"
     * @required
     */
    private String jsFileExtensions;

    /**
     * Stop the build if things go wrong according to JSLint.
     * 
     * @parameter default-value="true"
     * @required
     */
    private boolean failOnIssues;

    /**
     * true if ADsafe rules should be enforced. @see http://www.ADsafe.org/.
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean adsafe;

    /**
     * true if bitwise operators should not be allowed. @see http://www.jslint.com/lint.html#bitwise
     * 
     * @parameter default-value="true"
     * @required
     */
    private boolean disallowBitwiseOperators;

    /**
     * true if the standard browser globals should be predefined. @see http://www.jslint.com/lint.html#browser
     * 
     * @parameter default-value="true"
     * @required
     */
    private boolean assumeABrowser;

    /**
     * true if Initial Caps must be used with constructor functions. @see http://www.jslint.com/lint.html#new
     * 
     * @parameter default-value="true"
     * @required
     */
    private boolean requireInitialCapsForConstructors;

    /**
     * true if CSS workarounds should be tolerated. @see http://www.jslint.com/lint.html#css
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean tolerateCSSWorkarounds;

    /**
     * true if debugger statements should be allowed. Set this option to false before going into production.
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean tolerateDebuggerStatements;

    /**
     * true if browser globals that are useful in development should be predefined. @see
     * morehttp://www.jslint.com/lint.html#devel
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean assumeConsoleAlertEtc;

    /**
     * true if === and !== should be required. @see http://www.jslint.com/lint.html#equal
     * 
     * @parameter default-value="true"
     * @required
     */
    private boolean disallowEQNE;

    /**
     * true if ES5 syntax should be allowed.
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean tolerateES5Syntax;

    /**
     * true if eval should be allowed. @see http://www.jslint.com/lint.html#evil
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean tolerateEval;

    /**
     * true if unfiltered for in statements should be allowed. @see http://www.jslint.com/lint.html#forin
     * 
     * @parameter default-value="true"
     * @required
     */
    private boolean tolerateUnfilteredForIn;

    /**
     * true if HTML fragments should be allowed. @see http://www.jslint.com/lint.html#html
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean tolerateHTMLFragments;

    /**
     * true if immediate function invocations must be wrapped in parens.
     * 
     * @parameter default-value="true"
     * @required
     */
    private boolean requireParensAroundImmediateInvocations;

    /**
     * true if the ES5 "use strict"; pragma is required. Do not use this option carelessly.
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean strictWhiteSpace;

    /**
     * The number of spaces used for indentation.
     * 
     * @parameter default-value="4"
     * @required
     */
    private Integer strictWhiteSpaceIndentation;

    /**
     * true if statement breaks should not be checked. @see http://www.jslint.com/lint.html#breaking
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean tolerateSloppyLineBreaking;

    /**
     * The maximum number of warnings reported.
     * 
     * @parameter default-value="50"
     * @required
     */
    private Integer maximumNumberOfErrors;

    /**
     * true if upper case HTML should be allowed.
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean tolerateHTMLCase;

    /**
     * true if names should be checked for initial or trailing underbars.
     * 
     * @parameter default-value="true"
     * @required
     */
    private boolean disallowDanglingUnderbarInIdentifiers;

    /**
     * true if HTML event handlers should be allowed. @see http://www.jslint.com/lint.html#html
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean tolerateHTMLEventHandlers;

    /**
     * true if only one var statement per function should be allowed. @see http://www.jslint.com/lint.html#scope
     * 
     * @parameter default-value="true"
     * @required
     */
    private boolean allowOneVarStatementPerFunction;

    /**
     * true if the scan should stop on first error.
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean stopOnFirstError;

    /**
     * true if ++ and -- should not be allowed. @see http://www.jslint.com/lint.html#inc
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean disallowIncrAndDecr;

    /**
     * An array of strings, the names of predefined global variables. predef is used with the option object, but not
     * with the jslint comment. Use the var statement to declare global variables in a script file.
     * 
     * @parameter default-value="false"
     * @required
     */
    private String predefinedVars;

    /**
     * true if . and [^...] should not be allowed in RegExp literals. These forms should not be used when validating in
     * secure applications.
     * 
     * @parameter default-value="true"
     * @required
     */
    private boolean disallowInsecureCharsInRegExp;

    /**
     * true if the Rhino environment globals should be predefined. @see http://www.jslint.com/lint.html#rhino
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean assumeRhino;

    /**
     * true if the safe subset rules are enforced. These rules are used by <a href="http://www.ADsafe.org/">ADsafe</a>.
     * It enforces the safe subset rules but not the widget structure rules.
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean safeSubset;

    /**
     * true if the ES5 "use strict"; pragma is required. Do not use this option carelessly.
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean requireUseStrict;

    /**
     * true if subscript notation may be used for expressions better expressed in dot notation.
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean tolerateInefficientSubscripting;

    /**
     * true if variables must be declared before used. @see http://www.jslint.com/lint.html#undefined
     * 
     * @parameter default-value="true"
     * @required
     */
    private boolean disallowUndefinedVariables;

    /**
     * true if the <a href="http://widgets.yahoo.com/gallery/view.php?widget=37484">Yahoo Widgets</a> globals should be
     * predefined. @see http://www.jslint.com/lint.html#widget
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean assumeAYahooWidget;

    /**
     * true if the Windows globals should be predefined. @see http://www.jslint.com/lint.html#windows
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean assumeWindows;

    /**
     * The linter to use.
     */
    private JSLint jsLint;

    /**
     * The build context so that we can tell Maven certain files have changed if required.
     * 
     * @component
     */
    private BuildContext buildContext;

    /**
     * Perform the lint on files that have been modified.
     * 
     * @param sourceFolder the root folder of source files.
     * @param scope the current mojo scope.
     * @throws MojoExecutionException if something goes wrong.
     */
    public void doExecute( File sourceFolder, Scope scope )
        throws MojoExecutionException
    {
        initJSLint();

        List<Issue> projectIssues = new ArrayList<Issue>();

        Scanner scanner = buildContext.newScanner( sourceFolder );
        scanner.setIncludes( jsFileExtensions.split( "," ) );
        scanner.scan();
        String[] sources = scanner.getIncludedFiles();
        for ( String source : sources )
        {
            File sourceFile = new File( sourceFolder, source );

            getLog().info( "Parsing: " + source );

            try
            {
                FileReader fileReader = new FileReader( sourceFile );
                try
                {
                    BufferedReader bufferedFileReader = new BufferedReader( fileReader );
                    try
                    {
                        JSLintResult result = jsLint.lint( source, bufferedFileReader );
                        List<Issue> issues = result.getIssues();
                        projectIssues.addAll( issues );

                    }
                    finally
                    {
                        bufferedFileReader.close();
                    }
                }
                finally
                {
                    fileReader.close();
                }
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Problem while parsing file: " + source, e );
            }
        }

        if ( projectIssues.size() > 0 )
        {
            for ( Issue issue : projectIssues )
            {
                if ( failOnIssues )
                {
                    getLog().error( issue.toString() );
                }
                else
                {
                    getLog().warn( issue.toString() );
                }
            }
            if ( failOnIssues )
            {
                throw new MojoExecutionException( "Issues found in project." );
            }
        }
    }

    /**
     * @return property.
     */
    public BuildContext getBuildContext()
    {
        return buildContext;
    }

    /**
     * @return property.
     */
    public String getJsFileExtensions()
    {
        return jsFileExtensions;
    }

    /**
     * @return property.
     */
    public JSLint getJsLint()
    {
        return jsLint;
    }

    /**
     * @return property.
     */
    public Integer getMaximumNumberOfErrors()
    {
        return maximumNumberOfErrors;
    }

    /**
     * @return property.
     */
    public String getPredefinedVars()
    {
        return predefinedVars;
    }

    /**
     * @return property.
     */
    public int getStrictWhiteSpaceIndentation()
    {
        return strictWhiteSpaceIndentation;
    }

    /**
     * Initialise the JSLint environment.
     * 
     * @throws MojoExecutionException if the init fails.
     */
    protected void initJSLint()
        throws MojoExecutionException
    {
        try
        {
            jsLint = new JSLintBuilder().fromDefault();

            // Set up options.
            if ( adsafe )
            {
                jsLint.addOption( Option.ADSAFE );
            }
            if ( disallowBitwiseOperators )
            {
                jsLint.addOption( Option.BITWISE );
            }
            if ( assumeABrowser )
            {
                jsLint.addOption( Option.BROWSER );
            }
            if ( tolerateHTMLCase )
            {
                jsLint.addOption( Option.CAP );
            }
            if ( tolerateCSSWorkarounds )
            {
                jsLint.addOption( Option.CSS );
            }
            if ( tolerateDebuggerStatements )
            {
                jsLint.addOption( Option.DEBUG );
            }
            if ( assumeConsoleAlertEtc )
            {
                jsLint.addOption( Option.DEVEL );
            }
            if ( disallowEQNE )
            {
                jsLint.addOption( Option.EQEQEQ );
            }
            if ( tolerateES5Syntax )
            {
                jsLint.addOption( Option.ES5 );
            }
            if ( tolerateEval )
            {
                jsLint.addOption( Option.EVIL );
            }
            if ( tolerateUnfilteredForIn )
            {
                jsLint.addOption( Option.FORIN );
            }
            if ( tolerateHTMLFragments )
            {
                jsLint.addOption( Option.FRAGMENT );
            }
            if ( requireParensAroundImmediateInvocations )
            {
                jsLint.addOption( Option.IMMED );
            }
            if ( tolerateSloppyLineBreaking )
            {
                jsLint.addOption( Option.LAXBREAK );
            }
            jsLint.addOption( Option.MAXERR, maximumNumberOfErrors.toString() );
            if ( requireInitialCapsForConstructors )
            {
                jsLint.addOption( Option.NEWCAP );
            }
            if ( disallowDanglingUnderbarInIdentifiers )
            {
                jsLint.addOption( Option.NOMEN );
            }
            if ( tolerateHTMLEventHandlers )
            {
                jsLint.addOption( Option.ON );
            }
            if ( allowOneVarStatementPerFunction )
            {
                jsLint.addOption( Option.ONEVAR );
            }
            if ( stopOnFirstError )
            {
                jsLint.addOption( Option.PASSFAIL );
            }
            if ( disallowIncrAndDecr )
            {
                jsLint.addOption( Option.PLUSPLUS );
            }
            jsLint.addOption( Option.PREDEF, predefinedVars );
            if ( disallowInsecureCharsInRegExp )
            {
                jsLint.addOption( Option.REGEXP );
            }
            if ( assumeRhino )
            {
                jsLint.addOption( Option.RHINO );
            }
            if ( safeSubset )
            {
                jsLint.addOption( Option.SAFE );
            }
            if ( requireUseStrict )
            {
                jsLint.addOption( Option.STRICT );
            }
            if ( tolerateInefficientSubscripting )
            {
                jsLint.addOption( Option.SUB );
            }
            if ( disallowUndefinedVariables )
            {
                jsLint.addOption( Option.UNDEF );
            }
            if ( strictWhiteSpace )
            {
                jsLint.addOption( Option.WHITE );
                jsLint.addOption( Option.INDENT, strictWhiteSpaceIndentation.toString() );
            }
            if ( assumeAYahooWidget )
            {
                jsLint.addOption( Option.WIDGET );
            }
            if ( assumeWindows )
            {
                jsLint.addOption( Option.WINDOWS );
            }
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Problem while initialising JSLint", e );
        }
    }

    /**
     * @return property.
     */
    public boolean isADsafe()
    {
        return adsafe;
    }

    /**
     * @return property.
     */
    public boolean isAllowOneVarStatementPerFunction()
    {
        return allowOneVarStatementPerFunction;
    }

    /**
     * @return property.
     */
    public boolean isAssumeABrowser()
    {
        return assumeABrowser;
    }

    /**
     * @return property.
     */
    public boolean isAssumeAYahooWidget()
    {
        return assumeAYahooWidget;
    }

    /**
     * @return property.
     */
    public boolean isAssumeConsoleAlertEtc()
    {
        return assumeConsoleAlertEtc;
    }

    /**
     * @return property.
     */
    public boolean isAssumeRhino()
    {
        return assumeRhino;
    }

    /**
     * @return property.
     */
    public boolean isAssumeWindows()
    {
        return assumeWindows;
    }

    /**
     * @return property.
     */
    public boolean isDisallowBitwiseOperators()
    {
        return disallowBitwiseOperators;
    }

    /**
     * @return property.
     */
    public boolean isDisallowDanglingUnderbarInIdentifiers()
    {
        return disallowDanglingUnderbarInIdentifiers;
    }

    /**
     * @return property.
     */
    public boolean isDisallowEQNE()
    {
        return disallowEQNE;
    }

    /**
     * @return property.
     */
    public boolean isDisallowIncrAndDecr()
    {
        return disallowIncrAndDecr;
    }

    /**
     * @return property.
     */
    public boolean isDisallowInsecureCharsInRegExp()
    {
        return disallowInsecureCharsInRegExp;
    }

    /**
     * @return property.
     */
    public boolean isDisallowUndefinedVariables()
    {
        return disallowUndefinedVariables;
    }

    /**
     * @return property.
     */
    public boolean isFailOnIssues()
    {
        return failOnIssues;
    }

    /**
     * @return property.
     */
    public boolean isRequireInitialCapsForConstructors()
    {
        return requireInitialCapsForConstructors;
    }

    /**
     * @return property.
     */
    public boolean isRequireParensAroundImmediateInvocations()
    {
        return requireParensAroundImmediateInvocations;
    }

    /**
     * @return property.
     */
    public boolean isRequireUseStrict()
    {
        return requireUseStrict;
    }

    /**
     * @return property.
     */
    public boolean isSafeSubset()
    {
        return safeSubset;
    }

    /**
     * @return property.
     */
    public boolean isStopOnFirstError()
    {
        return stopOnFirstError;
    }

    /**
     * @return property.
     */
    public boolean isStrictWhiteSpace()
    {
        return strictWhiteSpace;
    }

    /**
     * @return property.
     */
    public boolean isTolerateCSSWorkarounds()
    {
        return tolerateCSSWorkarounds;
    }

    /**
     * @return property.
     */
    public boolean isTolerateDebuggerStatements()
    {
        return tolerateDebuggerStatements;
    }

    /**
     * @return property.
     */
    public boolean isTolerateES5Syntax()
    {
        return tolerateES5Syntax;
    }

    /**
     * @return property.
     */
    public boolean isTolerateEval()
    {
        return tolerateEval;
    }

    /**
     * @return property.
     */
    public boolean isTolerateHTMLCase()
    {
        return tolerateHTMLCase;
    }

    /**
     * @return property.
     */
    public boolean isTolerateHTMLEventHandlers()
    {
        return tolerateHTMLEventHandlers;
    }

    /**
     * @return property.
     */
    public boolean isTolerateHTMLFragments()
    {
        return tolerateHTMLFragments;
    }

    /**
     * @return property.
     */
    public boolean isTolerateInefficientSubscripting()
    {
        return tolerateInefficientSubscripting;
    }

    /**
     * @return property.
     */
    public boolean isTolerateSloppyLineBreaking()
    {
        return tolerateSloppyLineBreaking;
    }

    /**
     * @return property.
     */
    public boolean isTolerateUnfilteredForIn()
    {
        return tolerateUnfilteredForIn;
    }

    /**
     * @param aDsafe set property.
     */
    public void setADsafe( boolean aDsafe )
    {
        adsafe = aDsafe;
    }

    /**
     * @param allowOneVarStatementPerFunction set property.
     */
    public void setAllowOneVarStatementPerFunction( boolean allowOneVarStatementPerFunction )
    {
        this.allowOneVarStatementPerFunction = allowOneVarStatementPerFunction;
    }

    /**
     * @param assumeABrowser set property.
     */
    public void setAssumeABrowser( boolean assumeABrowser )
    {
        this.assumeABrowser = assumeABrowser;
    }

    /**
     * @param assumeAYahooWidget set property.
     */
    public void setAssumeAYahooWidget( boolean assumeAYahooWidget )
    {
        this.assumeAYahooWidget = assumeAYahooWidget;
    }

    /**
     * @param assumeConsoleAlertEtc set property.
     */
    public void setAssumeConsoleAlertEtc( boolean assumeConsoleAlertEtc )
    {
        this.assumeConsoleAlertEtc = assumeConsoleAlertEtc;
    }

    /**
     * @param assumeRhino set property.
     */
    public void setAssumeRhino( boolean assumeRhino )
    {
        this.assumeRhino = assumeRhino;
    }

    /**
     * @param assumeWindows set property.
     */
    public void setAssumeWindows( boolean assumeWindows )
    {
        this.assumeWindows = assumeWindows;
    }

    /**
     * @param buildContext set property.
     */
    public void setBuildContext( BuildContext buildContext )
    {
        this.buildContext = buildContext;
    }

    /**
     * @param disallowBitwiseOperators set property.
     */
    public void setDisallowBitwiseOperators( boolean disallowBitwiseOperators )
    {
        this.disallowBitwiseOperators = disallowBitwiseOperators;
    }

    /**
     * @param disallowDanglingUnderbarInIdentifiers set property.
     */
    public void setDisallowDanglingUnderbarInIdentifiers( boolean disallowDanglingUnderbarInIdentifiers )
    {
        this.disallowDanglingUnderbarInIdentifiers = disallowDanglingUnderbarInIdentifiers;
    }

    /**
     * @param disallowEQNE set property.
     */
    public void setDisallowEQNE( boolean disallowEQNE )
    {
        this.disallowEQNE = disallowEQNE;
    }

    /**
     * @param disallowIncrAndDecr set property.
     */
    public void setDisallowIncrAndDecr( boolean disallowIncrAndDecr )
    {
        this.disallowIncrAndDecr = disallowIncrAndDecr;
    }

    /**
     * @param disallowInsecureCharsInRegExp set property.
     */
    public void setDisallowInsecureCharsInRegExp( boolean disallowInsecureCharsInRegExp )
    {
        this.disallowInsecureCharsInRegExp = disallowInsecureCharsInRegExp;
    }

    /**
     * @param disallowUndefinedVariables set property.
     */
    public void setDisallowUndefinedVariables( boolean disallowUndefinedVariables )
    {
        this.disallowUndefinedVariables = disallowUndefinedVariables;
    }

    /**
     * @param failOnIssues set property.
     */
    public void setFailOnIssues( boolean failOnIssues )
    {
        this.failOnIssues = failOnIssues;
    }

    /**
     * @param jsFileExtensions set property.
     */
    public void setJsFileExtensions( String jsFileExtensions )
    {
        this.jsFileExtensions = jsFileExtensions;
    }

    /**
     * @param maximumNumberOfErrors set property.
     */
    public void setMaximumNumberOfErrors( Integer maximumNumberOfErrors )
    {
        this.maximumNumberOfErrors = maximumNumberOfErrors;
    }

    /**
     * @param predefinedVars set property.
     */
    public void setPredefinedVars( String predefinedVars )
    {
        this.predefinedVars = predefinedVars;
    }

    /**
     * @param requireInitialCapsForConstructors set property.
     */
    public void setRequireInitialCapsForConstructors( boolean requireInitialCapsForConstructors )
    {
        this.requireInitialCapsForConstructors = requireInitialCapsForConstructors;
    }

    /**
     * @param requireParensAroundImmediateInvocations set property.
     */
    public void setRequireParensAroundImmediateInvocations( boolean requireParensAroundImmediateInvocations )
    {
        this.requireParensAroundImmediateInvocations = requireParensAroundImmediateInvocations;
    }

    /**
     * @param requireUseStrict set property.
     */
    public void setRequireUseStrict( boolean requireUseStrict )
    {
        this.requireUseStrict = requireUseStrict;
    }

    /**
     * @param safeSubset set property.
     */
    public void setSafeSubset( boolean safeSubset )
    {
        this.safeSubset = safeSubset;
    }

    /**
     * @param stopOnFirstError set property.
     */
    public void setStopOnFirstError( boolean stopOnFirstError )
    {
        this.stopOnFirstError = stopOnFirstError;
    }

    /**
     * @param strictWhiteSpace set property.
     */
    public void setStrictWhiteSpace( boolean strictWhiteSpace )
    {
        this.strictWhiteSpace = strictWhiteSpace;
    }

    /**
     * @param strictWhiteSpaceIndentation set property.
     */
    public void setStrictWhiteSpaceIndentation( Integer strictWhiteSpaceIndentation )
    {
        this.strictWhiteSpaceIndentation = strictWhiteSpaceIndentation;
    }

    /**
     * @param tolerateCSSWorkarounds set property.
     */
    public void setTolerateCSSWorkarounds( boolean tolerateCSSWorkarounds )
    {
        this.tolerateCSSWorkarounds = tolerateCSSWorkarounds;
    }

    /**
     * @param tolerateDebuggerStatements set property.
     */
    public void setTolerateDebuggerStatements( boolean tolerateDebuggerStatements )
    {
        this.tolerateDebuggerStatements = tolerateDebuggerStatements;
    }

    /**
     * @param tolerateES5Syntax set property.
     */
    public void setTolerateES5Syntax( boolean tolerateES5Syntax )
    {
        this.tolerateES5Syntax = tolerateES5Syntax;
    }

    /**
     * @param tolerateEval set property.
     */
    public void setTolerateEval( boolean tolerateEval )
    {
        this.tolerateEval = tolerateEval;
    }

    /**
     * @param tolerateHTMLCase set property.
     */
    public void setTolerateHTMLCase( boolean tolerateHTMLCase )
    {
        this.tolerateHTMLCase = tolerateHTMLCase;
    }

    /**
     * @param tolerateHTMLEventHandlers set property.
     */
    public void setTolerateHTMLEventHandlers( boolean tolerateHTMLEventHandlers )
    {
        this.tolerateHTMLEventHandlers = tolerateHTMLEventHandlers;
    }

    /**
     * @param tolerateHTMLFragments set property.
     */
    public void setTolerateHTMLFragments( boolean tolerateHTMLFragments )
    {
        this.tolerateHTMLFragments = tolerateHTMLFragments;
    }

    /**
     * @param tolerateInefficientSubscripting set property.
     */
    public void setTolerateInefficientSubscripting( boolean tolerateInefficientSubscripting )
    {
        this.tolerateInefficientSubscripting = tolerateInefficientSubscripting;
    }

    /**
     * @param tolerateSloppyLineBreaking set property.
     */
    public void setTolerateSloppyLineBreaking( boolean tolerateSloppyLineBreaking )
    {
        this.tolerateSloppyLineBreaking = tolerateSloppyLineBreaking;
    }

    /**
     * @param tolerateUnfilteredForIn set property.
     */
    public void setTolerateUnfilteredForIn( boolean tolerateUnfilteredForIn )
    {
        this.tolerateUnfilteredForIn = tolerateUnfilteredForIn;
    }
}
