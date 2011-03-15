package org.codehaus.mojo.rspec;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Mojo to run Ruby Spec test
 * 
 * @author Michael Ward
 * @author Mauro Talevi
 * @goal spec
 */
public class RspecRunnerMojo extends AbstractMojo {

	/**
	 * The project base directory
	 * 
	 * @parameter expression="${basedir}"
	 * @required
	 * @readonly
	 */
	protected String basedir;

	/**
	 * The classpath elements of the project being tested.
	 * 
	 * @parameter expression="${project.testClasspathElements}"
	 * @required
	 * @readonly
	 */
	protected List<String> classpathElements;

	/**
	 * The directory containing the RSpec source files
	 * 
	 * @parameter
	 * @required
	 */
	protected String sourceDirectory;

	/**
	 * The directory where the RSpec report will be written to
	 * 
	 * @parameter
	 * @required
	 */
	protected String outputDirectory;

	/**
	 * The name of the RSpec report (optional, defaults to "rspec_report.html")
	 * 
	 * @parameter expression="rspec_report.html"
	 */
	protected String reportName;

	/**
	 * The directory where JRuby is installed (optional, defaults to
	 * "${user.home}/.jruby")
	 * 
	 * @parameter expression="${user.home}/.jruby"
	 */
	protected String jrubyHome;

	/**
	 * The flag to ignore failures (optional, defaults to "false")
	 * 
	 * @parameter expression="false"
	 */
	protected boolean ignoreFailure;

	/**
	 * The flag to skip tests (optional, defaults to "false")
	 * 
	 * @parameter expression="false"
	 */
	protected boolean skipTests;

	/**
	 * List of system properties to set for the tests.
	 * 
	 * @parameter
	 */
	protected Properties systemProperties;

	private RSpecScriptFactory rspecScriptFactory = new RSpecScriptFactory();
	private ShellScriptFactory shellScriptFactory = new ShellScriptFactory();
	
	public RspecRunnerMojo() {
		
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (skipTests) {
			getLog().info("Skipping RSpec tests");
			return;
		}
		getLog().info("Running RSpec tests from " + sourceDirectory);

		String reportPath = outputDirectory + "/" + reportName;

		initScriptFactory( rspecScriptFactory, reportPath);
		initScriptFactory( shellScriptFactory, reportPath);
		
		try {
			rspecScriptFactory.emit();
		} catch (Exception e) {
			getLog().error( "error emitting .rb", e );
		}
		try {
			shellScriptFactory.emit();
		} catch (Exception e) {
			getLog().error( "error emitting .sh", e );
		}

		try {
			runScript(rspecScriptFactory.getScript() );
		} catch (MalformedURLException e) {
			getLog().error( "error running script", e );
		}
	}
	
	private void initScriptFactory(ScriptFactory factory, String reportPath) {
			factory.setBaseDir(basedir);
			factory.setClasspathElements(classpathElements);
			factory.setOutputDir(new File(outputDirectory));
			factory.setReportPath(reportPath);
			factory.setSourceDir(sourceDirectory);
			factory.setSystemProperties( systemProperties );
	}

	private void runScript(String script) {
		//getLog().info( "[[\n\n" + script + "\n\n]]" );

		List<String> cmdLine = new ArrayList<String>();
		cmdLine.add( jrubyHome + "/bin/jruby");

		cmdLine.add( "-J-Dbasedir=" + basedir );
		for (Object propName : systemProperties.keySet()) {
			String propValue = systemProperties.getProperty(propName.toString());
			cmdLine.add("-J-D" + propName + "=" + propValue);
		}
		
		//getLog().info( "cmdline [" + cmdLine + "]" );

		StringBuilder classpath = new StringBuilder();
		boolean first = true;

		for (String element : classpathElements) {
			if (first) {
				first = false;
			} else {
				classpath.append(":");
			}
			classpath.append(element);
		}

		ProcessBuilder builder = new ProcessBuilder(cmdLine);

		builder.environment().put("JRUBY_HOME", jrubyHome);
		builder.environment().put("CLASSPATH", classpath.toString());

		try {
			Process process = builder.start();
			new InputConsumer(process.getInputStream(), System.out).start();
			new InputConsumer(process.getErrorStream(), System.err).start();

			OutputStream out = process.getOutputStream();
			out.write(script.getBytes());
			out.close();

			process.waitFor();
			System.err.println("exit: " + process.exitValue());
		} catch (IOException e) {
			getLog().error(e);
		} catch (InterruptedException e) {
			getLog().error(e);
		}
	}

	private class InputConsumer extends Thread {

		private InputStream input;
		private OutputStream sink;

		public InputConsumer(InputStream input, OutputStream sink) {
			this.input = input;
			this.sink = sink;
		}

		public void run() {
			byte[] buf = new byte[128];
			int len = 0;
			try {
				while ((len = this.input.read(buf)) >= 0) {
					this.sink.write(buf, 0, len);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
