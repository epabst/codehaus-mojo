#!/usr/bin/env bash
cd `dirname $0`
cd ..
CLASSPATH=
for jarfile in lib/*.jar
do
  CLASSPATH="$CLASSPATH$jarfile:"
done
java -cp $CLASSPATH foo.bar.Foo
