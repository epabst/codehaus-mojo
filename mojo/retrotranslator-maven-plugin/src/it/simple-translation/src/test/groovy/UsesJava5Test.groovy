/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
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

import junit.framework.TestCase

import org.objectweb.asm.ClassReader

/**
 * Verifies that translation generates byte code for the right JVM major/minor versions.
 *
 * @version $Id$
 */
class UsesJava5Test
    extends TestCase
{
    void verifyClass(file, major, minor) {
        file = new File("$file")
        assert file.exists()
        
        def reader = new ClassReader(file.newInputStream())
        reader.accept(new CheckJavaVersionVisitor(major, minor), 0)
    }
    
    void testClassMajorMinor() {
        verifyClass('target/classes/UsesJava5.class', 49, 0)
        verifyClass('target/classes-jdk14/UsesJava5.class', 48, 0)
    }
}
