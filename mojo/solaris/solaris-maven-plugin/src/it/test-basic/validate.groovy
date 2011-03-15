import org.codehaus.plexus.util.cli.CommandLineUtils
import org.codehaus.plexus.util.cli.CommandLineUtils.StringStreamConsumer
import org.codehaus.plexus.util.cli.Commandline

File pkg = new File((File) basedir, "target/solaris").listFiles().toList().find {return it.name.endsWith(".pkg")}

Commandline cl = new Commandline();
cl.setExecutable("/usr/sbin/pkgchk")
cl.createArgument().value = "-l"
cl.createArgument().value = "-d"
cl.createArgument().value = pkg.absolutePath
cl.createArgument().value = "all"

StringStreamConsumer stdout = new CommandLineUtils.StringStreamConsumer()
StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer()
println "Executing"
println cl.toString()
System.out.flush()

int i = CommandLineUtils.executeCommandLine(cl, stdout, stderr)

String output = stdout.output
println "Output:"
println output

if (i != 0)
{
    println "${cl.executable} returned a non-0 return value: ${i}"
    return false;
}

boolean ok = true

[
    """Pathname: /opt/myapp/bin
Type: directory
Expected mode: 0755
""",
    // Binares are supposed to be executable
    """Pathname: /opt/myapp/bin/myapp
Type: regular file
Expected mode: 0755
""",
    """Pathname: /opt/myapp/etc/myapp.conf
Type: regular file
Expected mode: 0644
""",
    // Override one file to be owned by root
    """Pathname: /etc/opt/myapp/myapp.conf
Type: regular file
Expected mode: 0700
Expected owner: root
Expected group: root
""",
    // Check directory entry with no leading slash
    """Pathname: /opt/myapp/logs
Type: directory
Expected mode: 0700
Expected owner: myapp
Expected group: myapp
""",
].each {string ->
    if( output.indexOf(string ) == -1 )
    {
        println "Did not find the expected string in the output:"
        println string

        ok = false
    }
}

return ok
