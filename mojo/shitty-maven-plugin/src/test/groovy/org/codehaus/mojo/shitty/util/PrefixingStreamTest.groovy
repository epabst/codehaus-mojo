/*
 * Copyright (C) 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.mojo.shitty.util

/**
 * Tests for the {@link PrefixingStream} class.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
class PrefixingStreamTest
    extends GroovyTestCase
{
    ByteArrayOutputStream buffer

    PrefixingStream out
    
    void setUp() {
        buffer = new ByteArrayOutputStream()
        out = new PrefixingStream('OUT: ', buffer)
    }
    
    def assertExpected(expect, have) {
        println "EXPECT:>>>\n$expect<<<"
        println "HAVE:>>>\n$have<<<"
        
        assert expect == have
    }
    
    void testSimple() {
        out.println('hello')
        out.print('hi')
        out.println(' there')
        out.flush()
        
        def expect = '''OUT: hello
OUT: hi there
'''
        assertExpected(expect, buffer.toString())
    }
    
    void testSimpleToFile() {
        def file = File.createTempFile('test', '.txt')
        def out = new PrefixingStream('OUT: ', file.newOutputStream())
        
        out.println('hello')
        out.print('hi')
        out.println(' there')
        out.flush()
        
        def expect = '''OUT: hello
OUT: hi there
'''
        assertExpected(expect, file.text)
        
        file.delete()
    }
    
    void testMultline() {
        out.println('hello')
        out.print('hi\n\n')
        out.println('there')
        out.flush()
        
        def expect = '''OUT: hello
OUT: hi

OUT: there
'''
       
        assertExpected(expect, buffer.toString())
    }
    
    void testMultline2() {
        out.println('1')
        out.println('a\nb\nc\n')
        out.println('d')
        out.flush()
        
        def expect = '''OUT: 1
OUT: a
b
c
OUT: 
OUT: d
'''
        assertExpected(expect, buffer.toString())
    }
    
    /*
    void testMultline3() {
        out.println("Hi");
        out.println "yo yo yo"
        out.println ""
        out.println "abcdefg\n1234567890"
        out.flush()
    
        def expect = '''OUT: Hi
OUT: yo yo yo
OUT: 
OUT: abcdefg
OUT: 1234567890
'''
        assertExpected(expect, buffer.toString())
    }
    */
    
    void testDynamicPrefix() {
        def makePrefix = { line ->
            return "[$line] "
        }
        
        out = new PrefixingStream(makePrefix, buffer)
        
        out.println('1')
        out.println('2')
        out.println('3')
        out.flush()
        
        def expect = '''[1] 1
[2] 2
[3] 3
'''
        assertExpected(expect, buffer.toString())
    }
}
