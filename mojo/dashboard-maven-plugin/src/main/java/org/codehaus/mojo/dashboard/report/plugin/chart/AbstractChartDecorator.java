package org.codehaus.mojo.dashboard.report.plugin.chart;

/*
 * Copyright 2008 David Vicente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import org.jfree.chart.JFreeChart;

public abstract class AbstractChartDecorator implements IChartRenderer{

	protected IChartRenderer decoratedChart;
	protected List results;
	/**
	 * Wrapped chart.
	 */
	protected JFreeChart report = null;

	public AbstractChartDecorator(IChartRenderer chartToDecorate, List markersToPlot) {
		super();
		this.decoratedChart = chartToDecorate;
		this.results = markersToPlot;
		//this.decoratedChart.createChart();
		this.report = this.decoratedChart.getChart();
		createChart();
	}
	public abstract void createChart();

	public BufferedImage createBufferedImage(int imageWidth, int imageHeight) {
		return this.decoratedChart.createBufferedImage(imageWidth, imageHeight);
	}

	public String getFileExtension() {
		return this.decoratedChart.getFileExtension();
	}

	public String getMimeType() {
		return this.decoratedChart.getMimeType();
	}

	public boolean isEmpty() {
		return this.decoratedChart.isEmpty();
	}

	public void saveToFile(String filename) throws IOException {
		this.decoratedChart.saveToFile(filename);
	}

	public void setHeight(int _height) {
		this.decoratedChart.setHeight(_height);
	}

	public void setWidth(int _width) {
		this.decoratedChart.setWidth(_width);
	}

	public JFreeChart getChart() {
		return this.report;
	}

}