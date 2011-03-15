/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.codehaus.mojo.retrotranslator

import org.codehaus.groovy.maven.mojo.GroovyMojo

import net.sf.retrotranslator.transformer.Retrotranslator

import org.apache.commons.lang.SystemUtils

/**
 * Support for retrotranlsation mojos.
 * 
 * @version $Id$
 */
abstract class RetrotranslateMojoSupport
    extends GroovyMojo
{
    /**
     * Project classpath.
     * 
     * @parameter expression="${project.compileClasspathElements}"
     * @required
     * @readonly
     */
    List classpathElements
    
    /**
     * The classpath for the verification including rt.jar, jce.jar, jsse.jar (from JRE 1.4).
     * The retrotranslator-runtime-n.n.n.jar, and backport-util-concurrent-n.n.jar
     * are included by default, they are not required to be defined here.
     * 
     * @parameter
     */
    List verifyClasspath

    /**
     * Asks the translator to strip signature (generics) information.
     * 
     * @parameter expression="${stripsign}" default-value="false"
     */
    boolean stripsign

    /**
     * Asks the translator for verbose output.
     * 
     * @parameter expression="${verbose}" default-value="false"
     */
    boolean verbose

    /**
     * Asks the translator to examine translated bytecode for references 
     * to classes, methods, or fields that cannot be found in the provided classpath.
     * 
     * @parameter expression="${verify}" default-value="false"
     */
    boolean verify

    /**
     * Asks the translator to only transform classes compiled 
     * with a target greater than the current one.
     * 
     * @parameter expression="${lazy}" default-value="false"
     */
    boolean lazy

    /**
     * Fails build when verification has failed.
     * 
     * @parameter expression="${failonwarning}" default-value="true"
     */
    boolean failonwarning
    
    /**
     * Whether to use alternative implementations of Java 1.4 
     * classes and methods for better Java 5 compatibility.
     *
     * @parameter expression="${advanced}" default-value="false"
     */
    boolean advanced

    /**
     * The package name for a private copy of retrotranslator-runtime-n.n.n.jar 
     * and backport-util-concurrent-n.n.jar to be put with translated classes.
     * 
     * @parameter expression="${embed}"
     */
    String embed

    /**
     * Informs the translator about user-defined backport packages.
     * Package names should be separated by semicolons.
     * 
     * @parameter expression="${backport}"
     */
    String backport

    /**
     * To make Java 6 classes compatible with Java 5 set this option to 1.5
     * and supply user-defined backport packages.
     * 
     * @parameter expression="${target}" default-value="1.4"
     */
    String target
    
    /**
     * Asks the translator to modify classes for JVM 1.4 compatibility 
     * but keep use of Java 5 API.
     *
     * @parameter expression="${retainapi}" default-value="false"
     */
    boolean retainapi
    
    /**
     * Asks the translator to keep Java 5 specific access modifiers.
     *
     * @parameter expression="${retainflags}" default-value="false"
     */
    boolean retainflags
    
    /**
     * The wildcard pattern specifying files that should be translated (either bytecode 
     * or UTF-8 text), e.g. "*.class*.tld". There are three special characters: "*?".
     * 
     * @parameter expression="${srcmask}" default-value="*.class"
     */
    String srcmask
    
    //
    // Mojo
    //
    
    void execute() {
        def trans = new Retrotranslator()
        trans.logger = new RetrotranslatorLogger(log)

        configureRetrotranslator(trans)

        trans.verbose = verbose
        trans.stripsign = stripsign
        trans.lazy = lazy
        trans.verify = verify
        trans.advanced = advanced
        trans.embed = embed
        trans.backport = backport
        trans.retainapi = retainapi
        trans.retainflags = retainflags
        
        if (target) {
            trans.target = target
        }
        
        if (srcmask) {
            trans.srcmask = srcmask
        }
        
        if (classpathElements) {
            classpathElements.each {
                def file = new File(it)
                trans.addClasspathElement(file)
            }
        }
        
        if (verify) {
            if (SystemUtils.IS_JAVA_1_4) {
                trans.classLoader = ClassLoader.systemClassLoader
            }
            else {
                if (!verifyClasspath) {
                    fail("Verify classpath must be specified for non-1.4 JVM's")
                }
            }
            
            if (verifyClasspath) {
                if (SystemUtils.IS_JAVA_1_4) {
                    log.warn('Invoking JVM is Java 1.4, extra classpath for verify is unneeded')
                }
                
                verifyClasspath.each {
                    def file = new File("$it")
                    trans.addClasspathElement(file)
                }
            }
        }
        
        boolean verified = trans.run()
        if (!verified && failonwarning) {
            fail('Verification failed')
        }
    }

    protected abstract void configureRetrotranslator(Retrotranslator trans)
}
