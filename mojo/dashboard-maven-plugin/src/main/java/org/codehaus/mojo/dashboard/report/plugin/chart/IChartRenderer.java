package org.codehaus.mojo.dashboard.report.plugin.chart;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.jfree.chart.JFreeChart;

public interface IChartRenderer {

	public boolean isEmpty();

	/**
	 * Return the file extension of the document : <tt>png</tt>.
	 * @return
	 */
	public String getFileExtension();

	/**
	 * Return the mime type of the document.
	 * @return
	 */
	public String getMimeType();

	/**
	 * Save the report with the specified filename. The filename can contain a relative or absolute path.
	 * <p>
	 * If the file exists, it is overwritten.
	 * </p>
	 *
	 * @param filename
	 *            Name of the output file.
	 * @throws IOException
	 *             If an I/O exception occurs.
	 * @see net.logAnalyzer.reports.LAReport#saveToFile(java.lang.String)
	 */
	public void saveToFile(String filename) throws IOException;

	/**
	 * Create an image from the report as a {@link BufferedImage}.
	 *
	 * @param imageWidth
	 *            Image width.
	 * @param imageHeight
	 *            Image height.
	 * @return Image from the report; <tt>null</tt> if unsupported feature.
	 * @see JFreeChart#createBufferedImage(int, int)
	 */
	public BufferedImage createBufferedImage(int imageWidth, int imageHeight);

	/**
	 * set the height of the image saved as file
	 * @param _height
	 */
	public void setHeight(int _height);

	/**
	 * set the width of the image saved as file
	 * @param _width
	 */
	public void setWidth(int _width);

	public abstract void createChart();

	public abstract JFreeChart getChart();

	public Color getBackgroundColor();

}