package org.apache.maven.diagrams.connectors.classes.test_classes;

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

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class Triangle extends Shape
{
    private Integer a;

    private Integer b;

    private Integer c;

    public Triangle()
    {
        a = 0;
        b = 0;
        c = 0;
    }

    public Triangle( Integer a )
    {
        this.a = a;
        this.b = a;
        this.c = a;
    }

    public Triangle( Integer a, Integer b, Integer c )
    {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double countAreaSize()
    {
        return heron( a, b, c );
    }

    protected static double heron( int a, int b, int c )
    {
        Integer p = ( a + b + c ) / 2;
        return Math.sqrt( p * ( a - p ) * ( a - b ) * ( a - c ) );
    }

}
