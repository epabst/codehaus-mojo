package org.codehaus.mojo.repositorytools;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.maven.archiva.discoverer.ArtifactDiscoverer;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.codehaus.mojo.repositorytools.components.CLITools;

/**
 * Examines all artifacts in a local repository, and exports basic info to a CSV
 * file.
 * 
 * @goal export-csv
 * @requiresProject false
 * @author tom
 * 
 */
public class ExportCSVMojo extends AbstractMojo
{

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	private ArtifactDiscoverer discoverer;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	private MavenProjectBuilder builder;

	/**
	 * The output filename.
	 * 
	 * @parameter default-value="repository.txt"
	 */
	private File output;

	/**
	 * The local repository to deploy. Defaults to a 'local' subdirectory of the
	 * working directory.
	 * 
	 * @parameter expression="${local}" default-value="local"
	 */
	private File local;
	
	private CLITools cliTools;

	public void execute() throws MojoExecutionException, MojoFailureException
	{
		try
		{
			ArtifactRepository localRepository = cliTools.createLocalRepository(local); 
			
			List artifacts = discoverer.discoverArtifacts(
					localRepository, Collections.EMPTY_LIST,
					new ArtifactFilter()
					{
						public boolean include(Artifact artifact)
						{
							getLog().info(artifact.toString());
							return true;
						}
					});

			WritableWorkbook workbook = Workbook.createWorkbook(new File(
					"repository.xls"));
			WritableSheet sheet = workbook.createSheet("Artifacts", 0);
			WritableCellFormat format = new WritableCellFormat();
			WritableCellFormat wrapFormat = new WritableCellFormat();
			wrapFormat.setWrap(true);
			int row = 0;
			addRow(sheet, row, new String[]
			{ "Group", "Artifact", "Version", "Packaging", "Description" },
					new CellFormat[]
					{ format, format, format, format, format });

			Pattern pattern = Pattern.compile("\\s{2,}");
			PrintWriter writer = new PrintWriter(new FileWriter(output));
			for (Iterator iterator = artifacts.iterator(); iterator.hasNext();) {
				Artifact artifact = (Artifact) iterator.next();
				if (artifact.getType().equals("pom"))
				{
					row++;
					MavenProject project = builder.buildFromRepository(
							artifact, Collections.EMPTY_LIST, localRepository);
					String description = project.getDescription();
					description = description == null ? null : pattern.matcher(
							description).replaceAll(" ");
					String[] data = new String[]
					{ project.getGroupId(), project.getArtifactId(),
							project.getVersion(), project.getPackaging(),
							description };
					// TODO fixme
					// writer.printf("%s\t%s\t%s\t%s\t%s\n", (Object[]) data);

					addRow(sheet, row, data, new CellFormat[]
					{ format, format, format, format, wrapFormat });
				}
			}
			writer.close();
			workbook.write();
			workbook.close();
		}
		catch (Exception e)
		{
			throw new MojoExecutionException("", e);
		}
	}

	private void addRow(WritableSheet sheet, int row, String[] data,
			CellFormat[] format) throws WriteException
	{
		for (int col = 0; col < data.length; col++)
		{
			Label cell = new Label(col, row, data[col]);
			cell.setCellFormat(format[col]);
			sheet.addCell(cell);
		}
	}

}
