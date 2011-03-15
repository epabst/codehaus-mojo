package org.codehaus.mojo.springbeandoc;

/*
   The MIT License
   .
   Copyright (c) 2005, Ghent University (UGent)
   .
   Permission is hereby granted, free of charge, to any person obtaining a copy of
   this software and associated documentation files (the "Software"), to deal in
   the Software without restriction, including without limitation the rights to
   use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
   of the Software, and to permit persons to whom the Software is furnished to do
   so, subject to the following conditions:
   .
   The above copyright notice and this permission notice shall be included in all
   copies or substantial portions of the Software.
   .
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
   SOFTWARE.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.springframework.beandoc.client.BeanDocClient;
import org.springframework.util.StringUtils;


/**
 * Spring BeanDoc report generator that can be used as a Maven 2 plugin or a Maven 2 site report.
 * @author Jurgen De Landsheer
 * @author Marat Radchenko
 * @author Markus Knittig
 * @goal springbeandoc
 * @phase generate-sources
 * @see <a href="http://spring-beandoc.sourceforge.net/">Spring BeanDoc</a>
 * @see <a href="http://www.graphviz.org/">GraphViz</a>
 */
public class SpringBeanDocMojo extends AbstractMavenReport {

  /** Subdirectory for report. */
  private static final String REPORT_DIRECTORY = "springbeandoc";

  /**
   * Base output directory for reports.
   * @parameter expression="${project.reporting.outputDirectory}"
   * @required
   */
  private File outputDirectory;

  /**
   * Reference to Maven 2 Project.
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  private MavenProject project;

  /**
   * Doxia SiteRender.
   * @component
   * @required
   * @readonly
   */
  private Renderer renderer;

  // ------------------------- CONFIGURABLE SETTINGS -----------------------

  /**
   * Stylesheet URL to use for generated report.
   * @parameter
   */
  private URL cssUrl;

  /**
   * List of {@link JavadocLocation} objects.
   * @parameter
   */
  private JavadocLocation[] javadocLocations = new JavadocLocation[0];

  /**
   * Set of input resources. Resources are standard Spring resources. Note that beandoc defaults to FILE system
   * resources, not classpath resources if no qualifier is listed.
   * @parameter
   */
  private String[] resources = new String[0];

  /**
   * Specifies the names filter of the source files to be used by BeanDoc. This setting only applies to
   * <code>sourceDirectories</code> parameter. Defaults to '**\/*.xml'.
   * @parameter
   */
  private String[] includes = new String[]{ "**/*.xml" };

  /**
   * Set of source directories.
   * @parameter
   */
  private File[] sourceDirectories = new File[0];

  /**
   * Ignore any number of beans based on either their id's/names or class names by specifying one or more
   * Regular Expressions.
   * @parameter
   */
  private String[] ignoreBeans = new String[0];

  /**
   * Rank beans by defining one or more Regular Expression which groups the matching beans, regardless
   * of any references between them. If a bean match more then one pattern, all matching pattern will
   * share the same rank in the graph.
   * @parameter
   */
  private String[] rankBeans = new String[0];

  /**
   * Merge proxies and targets beans where the target bean is not modelled as an anonymous inner bean.
   * @parameter
   */
  private MergeProxy[] mergeProxies = new MergeProxy[0];

  /**
   * Specify colours for patterns based on the bean name (id) or the bean's fully qualified class name.
   * @parameter
   */
  private ColourBean[] colourBeans = new ColourBean[0];

  /**
   * Specify the default colour for beans.
   * @parameter
   */
  private String defaultFillColour;

  /**
   * GraphViz executable location; visualization (images) will be generated only if you install this program and set
   * this property to the executable dot (dot.exe on Win).
   * @parameter alias="graphViz"
   * @required
   */
  private File executable;

  /**
   * Graph output types. Default is png. Possible values: png, jpg, gif, svg.
   * @parameter default-value="png"
   * @required
   */
  private String graphsOutputType;

  /**
   * Documentation title used in the HTML output.
   * @parameter
   */
  private String title;

  /**
   * Documentation footer used in the HTML output.
   * @parameter
   */
  private String footer;

  /**
   * I18N localization used in the HTML output.
   * @parameter
   */
  private String locale;

  /**
   * You can have the XML parser not bother to validate the input files against the DTD/XSD if you so wish. True by
   * default.
   * @parameter default-value="true"
   */
  private boolean validate;

  /**
   * @param locale report locale.
   * @return report description.
   * @see org.apache.maven.reporting.MavenReport#getDescription(java.util.Locale)
   */
  public String getDescription(final Locale locale) {
    return getBundle(locale).getString("report.description");
  }

  /** @see org.apache.maven.reporting.MavenReport#getName(java.util.Locale) */
  public String getName(final Locale locale) {
    return getBundle(locale).getString("report.name");
  }

  /** @see org.apache.maven.reporting.MavenReport#getOutputName() */
  public String getOutputName() {
    return REPORT_DIRECTORY + "/index";
  }

  // ----------------------------- ENTRY POINTS -----------------------------

  /** @see org.apache.maven.plugin.Mojo#execute() */
  public void execute() throws MojoExecutionException {
    this.execute(this.outputDirectory, Locale.getDefault());
  }

  /** @see org.apache.maven.reporting.AbstractMavenReport#executeReport(java.util.Locale) */
  protected void executeReport(final Locale locale) throws MavenReportException {
    try {
      this.execute(this.outputDirectory, locale);
    } catch (MojoExecutionException e) {
      final MavenReportException ex = new MavenReportException(e.getMessage());
      ex.initCause(e.getCause());
      throw ex;
    }
  }

  /**
   * Builds beandoc config file.
   * @param outputDir target directory
   * @param locale    locale
   * @return path to config file.
   * @throws IOException exception
   */
  protected File buildConfig(final File outputDir, final Locale locale) throws IOException, MojoExecutionException {
    outputDir.mkdirs();
    //Exporting properties
    Properties p = new Properties();
    p.setProperty("output.dir", outputDir.getAbsolutePath());
    p.setProperty("graphs.outputType", this.graphsOutputType);
    final String title;
    if (StringUtils.hasText(this.title)) {
      title = this.title;
    } else {
      title = MessageFormat.format(
        this.getBundle(locale).getString("report.site.title"), this.project.getName(), this.project.getVersion()
      );
    }
    p.setProperty("html.title", title);

    if (StringUtils.hasText(this.footer)) {
    	p.setProperty("html.footer", this.footer);
    }

    final String country = locale.getCountry();
    if (StringUtils.hasText(this.locale)) {
      p.setProperty("i18n.locale", this.locale);
    } else if (StringUtils.hasText(country)) {
      p.setProperty("i18n.locale", country);
    }

    if (StringUtils.hasText(defaultFillColour)) {
    	p.setProperty("graphs.defaultFillColour", this.defaultFillColour);
    }

    p.setProperty("compiler.dotExe", this.executable.getAbsolutePath());
    if (this.cssUrl != null) {
      p.setProperty("html.cssUrl", this.cssUrl.toString());
    }
    for (final JavadocLocation location : this.javadocLocations) {
      p.setProperty("javadoc.locations[" + location.getPackagename() + "]", location.getLocation());
    }
    p.setProperty("processor.validateFiles", String.valueOf(this.validate));

    for (final MergeProxy mergeProxy : this.mergeProxies) {
    	p.setProperty("processor.mergeProxies[" + mergeProxy.getProxy() + "]", mergeProxy.getTarget());
    }

    for (final ColourBean colourBean : this.colourBeans) {
    	p.setProperty("graphs.colourBeans[" + colourBean.getPattern() + "]", colourBean.getColour());
    }

    for (int i = 0; i < ignoreBeans.length; i++) {
    	p.setProperty("graphs.ignoreBeans[" + i + "]", ignoreBeans[i]);
    }

    for (int i = 0; i < rankBeans.length; i++) {
    	p.setProperty("graphs.rankBeans[" + i + "]", rankBeans[i]);
    }

    //Exporting input files
    final Set<File> selectedFiles = new HashSet<File>();
    for (final File dir : this.sourceDirectories) {
      final DirectoryScanner scanner = new DirectoryScanner();
      scanner.addDefaultExcludes();
      scanner.setBasedir(dir);
      if (this.includes.length > 0) {
        scanner.setIncludes(this.includes);
      }
      scanner.scan();
      for (final String file : scanner.getIncludedFiles()) {
        selectedFiles.add(new File(dir, file));
      }
    }

    final StringBuilder ipb = new StringBuilder(16);
    final Iterator<File> it = selectedFiles.iterator();

    while (it.hasNext()) {
      final File f = it.next();
      this.getLog().info("Added file for processing: " + f.getAbsolutePath());
      ipb.append(f.getAbsolutePath());
      if (it.hasNext()) {
        ipb.append(",");
      }
    }

    if (this.resources.length > 0) {
      if (ipb.length() > 0) {
        ipb.append(",");
      }
      for (Iterator<String> it1 = Arrays.asList(this.resources).iterator(); it1.hasNext();) {
        final String resource = it1.next();
        final File f = new File(resource);
        final String entry;
        if (f.isDirectory()) {
          entry = new StringBuilder(f.getAbsolutePath()).append(File.separator).append("*.xml").toString();
        } else {
          entry = f.getAbsolutePath();
        }
        ipb.append(entry);
        this.getLog().info("Added entry for processing: " + entry);
        if (it1.hasNext()) {
          ipb.append(",");
        }
      }
    }

    if (ipb.length() == 0) {
      this.getLog().info("No files were selected");
      return null;
    }

    p.setProperty("input.files", ipb.toString());

    final File outputFile = new File(outputDir, "beandoc.properties");
    FileOutputStream os = null;
    try {
      os = new FileOutputStream(outputFile);
      p.store(os, "");
      os.close();
    } finally {
      if (os != null) {
        try {
          os.close();
        } catch (final IOException e) {
          this.getLog().error("Could not close " + outputFile.getAbsolutePath(), e);
        }
      }
    }
    return outputFile;
  }

  /**
   * Executes BeanDoc generator.
   * @param outputDirectory report output directory.
   * @param locale          report locale.
   * @throws MojoExecutionException if there were any execution errors.
   */
  private void execute(final File outputDirectory, final Locale locale) throws MojoExecutionException {
    final File config;
    try {
      config = buildConfig(new File(outputDirectory, REPORT_DIRECTORY), locale);
      if (config == null) return;
    } catch (final IOException e) {
      throw new MojoExecutionException("Could not generate beandoc config", e);
    }
    final ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
    try {
      //TODO: fix SpringLoader classloader bug
      //TODO: avoid using BeanDocClient because it calls System.exit
      Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
      final String[] params = new String[]{ "--properties", config.getAbsolutePath() };
      BeanDocClient.main(params);
    } finally {
      Thread.currentThread().setContextClassLoader(oldClassLoader);
    }
  }

  /**
   * Gets resource bundle for given locale.
   * @param locale locale.
   * @return resource bundle.
   */
  protected ResourceBundle getBundle(final Locale locale) {
    return ResourceBundle.getBundle("maven-springbeandoc-plugin", locale, this.getClass().getClassLoader());
  }

  /** {@inheritDoc} */
  protected Renderer getSiteRenderer() {
    return this.renderer;
  }

  /** {@inheritDoc} */
  protected String getOutputDirectory() {
    return this.outputDirectory.getAbsolutePath();
  }

  /**
   * {@inheritDoc}
   * @return <code>true</code>
   */
  public boolean isExternalReport() {
    return true;
  }

  /** {@inheritDoc} */
  protected MavenProject getProject() {
    return this.project;
  }
}

