import org.codehaus.plexus.util.cli.CommandLineUtils
import org.codehaus.plexus.util.cli.CommandLineUtils.StringStreamConsumer
import org.codehaus.plexus.util.cli.Commandline

Commandline cl = new Commandline();
cl.setExecutable( "mvn" )
cl.createArgument().value = "install:install-file"
cl.createArgument().value = "-DgroupId=org.codehaus.mojo.deb"
cl.createArgument().value = "-DartifactId=myapp"
cl.createArgument().value = "-Dversion=1.1"
cl.createArgument().value = "-Dpackaging=jar"
cl.createArgument().value = "-DgeneratePom=true"
cl.createArgument().value = "-Dfile=" + new File( (File)basedir, "myapp-1.1.jar" )

StringStreamConsumer stdout = new CommandLineUtils.StringStreamConsumer()
StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer()
println "Executing"
println cl.toString()

int i = CommandLineUtils.executeCommandLine(cl, stdout, stderr)

String output = stdout.output
println "Output:"
println output

if ( i != 0 )
{
    println "mvn returned a non-0 return value: ${i}"

    return false;
}

return true;
