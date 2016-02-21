/*
Copyright 2013 Will Webberley.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

The full text of the License is available in the root of this
project repository.
*/

package net.willwebberley.gowertides.classes;

import java.text.DecimalFormat;

import java.util.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.preference.PreferenceManager;

import com.androidplot.xy.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

/*
* Class to represent the tide graph shown on each day fragment.
*
* Contains method to modify parts of the graph where necessary, and also the support required to construct
* the graph.
*
* Each instance of TideGraph represents the tidal graph and tidal data for a certain Day object.
 */
public class TideGraph {

	private XYPlot plot;
	private XYSeries series, timeSeries, sunriseSeries, sunsetSeries;
	private Day day;
	private SharedPreferences prefs;

    /*
    * Initialize TideGraph instance with the UI View representing the graph and the application context.
     */
	public TideGraph(XYPlot plotComponent, Context context){
		plot = plotComponent;

		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		initGraph();
	}

    /*
    * Set the Day to be represented by the graph.
    *
    * Method then goes on to format the rest of the graph, including colours etc., and also to draw on support
    * information, such as sunrise/sunset times and the current time.
     */
	public void setDay(Day d){
		// Get the current day and reset graph
		day = d;
		plot.removeSeries(series);
		plot.removeSeries(timeSeries);
		plot.removeSeries(sunriseSeries);
		plot.removeSeries(sunsetSeries);

    // Create the 3 series types...
		createSunSeries();
    createTideSeries();
    createCurrentTimeSeries();

    // Refresh the plot display
    plot.redraw();
	}

    private void createSunSeries(){
        // If the sunset and sunrise times should be drawn...
        if(prefs.getBoolean("show_graph_sunrise_sunset", true)){
            Double sunriseTime = day.getSunriseTimeHours();
            Double sunsetTime = day.getSunsetTimeHours();
            Double[] xValues1 = {-3.0,sunriseTime};
            Double[] xValues2 = {sunsetTime,28.0};
            Double[] yValues = {15.0, 15.0};
            sunriseSeries = new SimpleXYSeries(
                    Arrays.asList(xValues1),
                    Arrays.asList(yValues),
                    "Sunrise");
            sunsetSeries = new SimpleXYSeries(
                    Arrays.asList(xValues2),
                    Arrays.asList(yValues),
                    "Sunrise");
            LineAndPointFormatter formatter = new LineAndPointFormatter(
                    Color.rgb(200, 200, 200),	// line color
                    null,                   	// point color
                    Color.rgb(220, 220, 220),  // fill Color
										null
						);
            Paint lineFill = new Paint();
            lineFill.setAlpha(60);
            formatter.setFillPaint(lineFill);
            plot.addSeries(sunriseSeries, formatter);
            plot.addSeries(sunsetSeries, formatter);
        }
    }

    private void createTideSeries(){
        ArrayList<Tide> tides = day.getTides();
        int numTides = tides.size();

        // Get the tide times and heights and assign to a series
        Double[] heights = new Double[numTides];
        Double[] times = new Double[numTides];

        for(int i = 0; i < numTides; i++){
            heights[i] = tides.get(i).height;
            times[i] = tides.get(i).timeHours;
        }

        if(day.getYesterday() != null){
            Double[] heights2 = new Double[heights.length+1];
            Double[] times2 = new Double[times.length+1];
            heights2[0] = day.getYesterday().getTides().get(day.getYesterday().getTides().size()-1).height;
            times2[0] = day.getYesterday().getTides().get(day.getYesterday().getTides().size()-1).timeHours - 24;
            for(int i = 1; i<=heights.length; i++){
                heights2[i] = heights[i-1];
                times2[i] = times[i-1];
            }
            heights = heights2;
            times = times2;
        }
        if(day.getTomorrow() != null){
            Double[] heights2 = new Double[heights.length+1];
            Double[] times2 = new Double[times.length+1];
            heights2[heights2.length-1] = day.getTomorrow().getTides().get(0).height;
            times2[times2.length-1] = day.getTomorrow().getTides().get(0).timeHours + 24;
            for(int i = 0; i<heights.length; i++){
                heights2[i] = heights[i];
                times2[i] = times[i];
            }
            heights = heights2;
            times = times2;
        }

        series = new SimpleXYSeries(Arrays.asList(times), Arrays.asList(heights), "Tides");

        // Format the series
        LineAndPointFormatter heightsFormat = new LineAndPointFormatter(
                Color.rgb(0, 150, 220),  	// line color
                null,                   	// point color
                Color.rgb(0, 150, 220),   // fill color
								null
				);
        Paint lineFill = new Paint();
        lineFill.setAlpha(150);
        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.rgb(0, 150, 220), Shader.TileMode.CLAMP));
        heightsFormat.setFillPaint(lineFill);

        // Add the series to the graph
        plot.addSeries(series, heightsFormat);

        // Get highest tide height and adjust y-axis accordingly
        Double largestHeight = 0.0;
        for(int i = 0; i < heights.length; i++){
            if(heights[i]>largestHeight){
                largestHeight = heights[i];
            }
        }
        plot.setRangeTopMin((Number)(largestHeight+1));
        plot.setRangeTopMax((Number)(largestHeight+1));
    }

    private void createCurrentTimeSeries(){
        // If the current time should be drawn...
        if(prefs.getBoolean("show_graph_time", true)){
            // If current day, get current time and paint red vertical line on graph
            if(day.isToday()){
                Double currentTime = day.getCurrentTimeHours();
                Double[] xValues = {currentTime,currentTime};
                Double[] yValues = {0.0, 20.0};
                timeSeries = new SimpleXYSeries(
                        Arrays.asList(xValues),
                        Arrays.asList(yValues),
                        "Time");

                LineAndPointFormatter timeFormat = new LineAndPointFormatter(
                        Color.rgb(200, 0, 0),   // line color
                        null,                   // point color
                        Color.rgb(200, 0, 0),		// fill color
												null
								);
                timeFormat.getLinePaint().setStyle(Paint.Style.STROKE);
                timeFormat.getLinePaint().setStrokeWidth(5);
                plot.addSeries(timeSeries, timeFormat);
            }
        }
    }

	/*
	* Initialize the graph by handling its static UI properties (axes, etc.), colours, axes formats, axes titles,
	* axes ranges, etc.
	 */
	private void initGraph(){

		// reduce the number of range labels
		plot.setTicksPerRangeLabel(1);
		// step periods on axes
		plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 4);
		plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 2.0);
		// change format of x-axis to decimal
		plot.setDomainValueFormat(new DecimalFormat("#"));

		// Colours
		try{
			plot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.GRAY);
			plot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.GRAY);
			plot.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);
			plot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
			plot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
			plot.getGraphWidget().getRangeOriginLabelPaint().setColor(Color.BLACK);
			plot.setPlotMargins(0,0,0,0);
			plot.setPlotPadding(0,0,30,0);
			Paint white = new Paint();
			white.setColor(Color.parseColor("#eeeeee"));
			plot.setBackgroundPaint(white);
			plot.setBorderPaint(white);
			plot.getGraphWidget().setBackgroundPaint(white);
			plot.getGraphWidget().setGridBackgroundPaint(white);
		}
		catch(Exception e){
			System.out.println(e);
		}

		// handle axis titles
		plot.getRangeLabelWidget().getLabelPaint().setColor(Color.rgb(0, 150, 220));
		plot.getRangeLabelWidget().getLabelPaint().setTextSize(20);
		plot.getRangeLabelWidget().setMarginRight(10);
		plot.getRangeLabelWidget().setMarginLeft(10);
		plot.getDomainLabelWidget().getLabelPaint().setColor(Color.rgb(0, 150, 220));
		plot.getDomainLabelWidget().getLabelPaint().setTextSize(20);
		plot.getDomainLabelWidget().setMarginTop(25);
		plot.getDomainLabelWidget().setMarginBottom(20);

		// handle size of axis tick labels
		plot.getGraphWidget().getDomainLabelPaint().setTextSize(25);
		plot.getGraphWidget().getDomainOriginLabelPaint().setTextSize(25);
		plot.getGraphWidget().getRangeLabelPaint().setTextSize(20);
		plot.getGraphWidget().getRangeOriginLabelPaint().setTextSize(20);

		// increase margins to ensure labels fit on graph
		plot.getGraphWidget().setMarginBottom(25);
		plot.getGraphWidget().setMarginRight(10);

		// Axis settings
		plot.setRangeLabel("Tide height (m)");
		plot.setDomainLabel("Time of day (h)");
		plot.setDomainLeftMax((Number)0);
		plot.setDomainLeftMin((Number)0);
		plot.setDomainRightMax((Number)24);
		plot.setDomainRightMin((Number)24);
		plot.setRangeBottomMax((Number)0);
		plot.setRangeBottomMin((Number)0);

		// hide the legend
		plot.getLegendWidget().setVisible(false);
	}
}
