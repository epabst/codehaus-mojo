import org.codehaus.plexus.util.cli.CommandLineUtils
import org.codehaus.plexus.util.cli.Commandline
import org.codehaus.plexus.util.cli.CommandLineUtils.StringStreamConsumer

File deb = new File( (File) basedir, "target/bar-project-a-1.1-2.deb" )
File debDerivate = new File( (File) basedir, "target/bar-project-a-derivate-1.1-2.deb" )

if(!deb.canRead())
{ 
  println "Can't read ${deb.getAbsolutePath()}"
  return false
}

if(!debDerivate.canRead())
{
  println "Can't read ${debDerivate.getAbsolutePath()}"
  return false
}

Commandline cl = new Commandline();
cl.setExecutable( "dpkg" )
cl.createArgument().value = "-c"
cl.createArgument().value = deb.absolutePath

StringStreamConsumer stdout = new CommandLineUtils.StringStreamConsumer()
StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer()
println "Executing"
println cl.toString()
System.out.flush()

int i = CommandLineUtils.executeCommandLine(cl, stdout, stderr)

String output = stdout.output
println "Output:"
println output

if ( i != 0 )
{
    println "${cl.executable} returned a non-0 return value: ${i}"
    return false;
}

if( output.indexOf("local/systems/myapp/bin/myapp") == -1 ||
    output.indexOf("etc/init.d/myapp") == -1 )
{
    println "Did not find the expected strings in the output."

    return false
}

cl = new Commandline();
cl.setExecutable( "dpkg" )
cl.createArgument().value = "-f"
cl.createArgument().value = deb.absolutePath

stdout = new CommandLineUtils.StringStreamConsumer()
stderr = new CommandLineUtils.StringStreamConsumer()
println "Executing"
println cl.toString()

i = CommandLineUtils.executeCommandLine(cl, stdout, stderr)

output = stdout.output
println "Output:"
println output

if ( i != 0 )
{
  println "${cl.executable} returned a non-0 return value: ${i}"
    return false;
}

return output.indexOf("Version: 1.1-2") != -1
