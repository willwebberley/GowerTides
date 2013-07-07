package net.willwebberley.gowertides.classes;

import java.text.*;
import java.util.*;

import android.content.Context;
import android.database.Cursor;
import net.willwebberley.gowertides.Dayview;
import net.willwebberley.gowertides.utils.*;

/*
* Class to represent a Day. Holds various fields of information regarding the day, including tidal, weather,
* and sunrise-sunset information.
*
* This is not a UI class, but contains the data represented in a DayInfo fragment.
 */
public class Day {

    /*
    * Calendar representation of Day
     */
	private Calendar day;

    /*
    * Fields to hold tidal data
     */
	private String[] highTideTimes;
	private String[] highTideHeights;
	private String[] lowTideTimes;
	private String[] lowTideHeights;
	private Calendar sunrise;
	private Calendar sunset;
	private String moon;

    /*
    * Fields to hold weather data
     */
	private int max_temp_c;
	private int max_temp_f;
	private int min_temp_c;
	private int min_temp_f;
	private int wind_speed_miles;
	private int wind_speed_km;
	private String wind_direction;
	private int wind_degree;
	private String icon_url;
	private String description;
	private Double precipitation;

    /*
    * Fields to hold surf data
     */
    private int[] hour = new int[8];
    private long[] local_time = new long[8];
    private int[] faded_rating = new int[8];
    private int[] solid_rating = new int[8];
    private double[] min_surf = new double[8];
    private double[] abs_min_surf = new double[8];
    private double[] max_surf = new double[8];
    private double[] abs_max_surf = new double[8];
    private double[] swell_height = new double[8];
    private double[] swell_period = new double[8];
    private double[] swell_angle = new double[8];
    private String[] swell_direction = new String[8];
    private String[] swell_chart_url = new String[8];
    private String[] period_chart_url = new String[8];
    private String[] Wind_chart_url = new String[8];
    private String[] pressure_chart_url = new String[8];
    private String[] sst_chart_url = new String[8];

    /*
    * Helper fields
     */
	private Context context; // application context
	private Boolean weatherAvailable; // true if weather data is available for today
    private Boolean surfAvailable; // true if surf data is available for today
	private DayDatabase db; // intance of the database holding tidal data
	private WeatherDatabase weather_db; // instance of the database holding weather and surf data
	private Dayview dayView; // Top level activity for application (to access context, databases, etc.)
	private int dayErrors; // enumerate the number of errors occurred in retrieving data from databases

    /*
    * Instantiate object with a Calendar day to represent, the application context and the main Dayview activity.
    * Activity is needed to access the databases.
     */
	public Day(Calendar date, Context con, Dayview dv){
		day = date;
		context = con;
		db = dv.db;
		weather_db = dv.weather_db;
		dayView = dv;
		getDayInfo();
	}

    /*
    * Set a new Calendar day of the Day object, if needed
     */
	public Day setDay(Calendar date){
		day = date;
		getDayInfo();
		return this;
	}

    /*
    * Load information regarding this day from the databases and put into the fields held by this object.
     */
	public void getDayInfo(){
		dayErrors = 0;
		// TIDE INFO:
		// 0year 1month 2day 3week_day 4sunrise 5sunset 6moon 7high1_time 8high1_height 9low1_time 10low1_height 
		// 11high2_time 12high2_height 13low2_time 14low2_height 15high3_time 16high3_height
		//
		// WEATHER INFO:
		// 0timestamp 1year 2month 3day 4max_temp_c 5max_temp_f 6min_temp_c 7min_temp_f 8wind_speed_miles
		// 9wind_speed_km 10wind_direction 11wind_degree 12icon_url 13description 14precipitation
        //
        // SURF INFO:
        // 0timestamp 1local_time 2year 3month 4day 5hour 6minute 7faded_rating 8solid_rating 9min_surf 10abs_min_surf 11max_surf
        // 12abs_max_surf 13swell_height 14swell_period 15swell_angle 16swell_direction 17swell_chart 18period_chart
        // 19wind_chart 20pressure_chart 21sst_chart

		
		Cursor info = db.getDayInfo(day);
		Cursor weatherInfo = weather_db.getWeatherInfo(day);
        Cursor surfInfo = weather_db.getSurfInfo(day);

		try{
			max_temp_c = weatherInfo.getInt(4);
			max_temp_f = weatherInfo.getInt(5);
			min_temp_c = weatherInfo.getInt(6);
			min_temp_f = weatherInfo.getInt(7);
			wind_speed_miles = weatherInfo.getInt(8);
			wind_speed_km = weatherInfo.getInt(9);
			wind_direction = weatherInfo.getString(10);
			wind_degree = weatherInfo.getInt(11);
			icon_url = weatherInfo.getString(12);
			description = weatherInfo.getString(13);
			precipitation = weatherInfo.getDouble(14);
			weatherAvailable = true;
		}
        /*
        * Exception will be thrown if there s no stored weather data for this Day.
         */
		catch(Exception e){
			weatherAvailable = false;
			dayErrors++;
		}

        try{
            int ind = 7;
            while (! surfInfo.isFirst() && ind >= 0){
                hour[ind] = surfInfo.getInt(5);
                local_time[ind] = surfInfo.getLong(1);
                faded_rating[ind] = surfInfo.getInt(7);
                solid_rating[ind] = surfInfo.getInt(8);
                min_surf[ind] = surfInfo.getDouble(9);
                abs_min_surf[ind] = surfInfo.getDouble(10);
                max_surf[ind] = surfInfo.getDouble(11);
                abs_max_surf[ind] = surfInfo.getDouble(12);
                swell_height[ind] = surfInfo.getDouble(13);
                swell_period[ind] = surfInfo.getDouble(14);
                swell_angle[ind] = surfInfo.getDouble(15);
                swell_direction[ind] = surfInfo.getString(16);
                swell_chart_url[ind] = surfInfo.getString(17);
                period_chart_url[ind] = surfInfo.getString(18);
                Wind_chart_url[ind] = surfInfo.getString(19);
                pressure_chart_url[ind] = surfInfo.getString(20);
                sst_chart_url[ind] = surfInfo.getString(21);
                surfInfo.moveToPrevious();
                ind--;
            }
            surfAvailable = true;
        }
        /*
        * Exception will be thrown if there is no stored surf data for this Day.
         */
        catch(Exception e){
            surfAvailable = false;
            dayErrors ++;
        }

		try{
			sunrise = Calendar.getInstance();
			//sunrise.setTime(new SimpleDateFormat("h:m a z").parse(info.getString(4).replace("BST", "GMT")));
            sunrise.setTime(dayView.dayDateFormatter.parse(info.getString(4).replace("BST", "GMT")));
            //sunrise.setTime(new SimpleDateFormat("h:m a").parse(info.getString(4).replace("BST", "GMT").replace("GMT", "")));
			sunset = Calendar.getInstance();
			sunset.setTime(dayView.dayDateFormatter.parse(info.getString(5).replace("BST", "GMT")));
            //sunset.setTime(new SimpleDateFormat("h:m a").parse(info.getString(5).replace("BST", "GMT").replace("GMT", "")));
			//if(info.getString(4).contains("BST")){
				sunset.add(Calendar.HOUR, -1);
				sunrise.add(Calendar.HOUR, -1);
    		//}
		}
		catch(Exception e){
			System.out.println(e);
			dayErrors++;
		}
		
		moon = info.getString(6);
		
		highTideTimes = new String[3];
		highTideHeights = new String[3];
		lowTideTimes = new String[2];
		lowTideHeights = new String[2];
		
		highTideTimes[0] = info.getString(7);
		highTideTimes[1] = info.getString(11);
		highTideTimes[2] = info.getString(15);
		
		highTideHeights[0] = info.getString(8);
		highTideHeights[1] = info.getString(12);
		highTideHeights[2] = info.getString(16);
		
		lowTideTimes[0] = info.getString(9);
		lowTideTimes[1] = info.getString(13);

		lowTideHeights[0] = info.getString(10);
		lowTideHeights[1] = info.getString(14);

        /*
        * Close the three cursors needed to get this data.
         */
        info.close();
        weatherInfo.close();
        surfInfo.close();
	}

    /*
    * Check to see if there were any errors in retrieving data.
    *
    * Note that if weather is unavailable for this Day, then this will return true.
     */
	public Boolean getErrors(){
		if (dayErrors > 0){
			return true;
		}
		return false;
	}

    /*
    * Check if there is weather data for this day.
     */
	public Boolean isWeatherAvailable(){
		return weatherAvailable;
	}
    /*
    * Check if there is surf data for this day.
     */
    public Boolean isSurfAvailable(){
        return surfAvailable;
    }

    /*
    * Publicly-available standard getter and mutator methods for this class.
     */
    private int getIndexForSurfHour(int hourReq){
        for (int i = 0; i < hour.length; i++){
            if (hour[i] == hourReq){
                return i;
            }
        }
        return -1;
    }
    public double getMinSurfForTime(int hourReq){
        int ind = getIndexForSurfHour(hourReq);
        return min_surf[ind];
    }
    public double getMaxSurfForTime(int hourReq){
        int ind = getIndexForSurfHour(hourReq);
        return max_surf[ind];
    }
	public String getWeatherDescription(){
		return description;
	}
	public int getMaxTemp(Boolean metric){
		if(metric){return max_temp_c;}
		else{return max_temp_f;}
	}
	public int getMinTemp(Boolean metric){
		if(metric){return min_temp_c;}
		else{return min_temp_f;}
	}
	public int getWindSpeed(Boolean metric){
		if(metric){return wind_speed_km;}
		else{return wind_speed_miles;}
	}
	public String getWindDirection(){
		return wind_direction;
	}
	public int getWindDegree(){
		return wind_degree;
	}
	public String getWeatherIcon(){
		String[] tokens = icon_url.split("\\/");
		for(int i = 0; i < tokens.length; i++){
			if(tokens[i].contains("wsymbol_0")){
				return tokens[i];
			}
		}
		return null;
	}
	public Double getPrecipitation(){
		return precipitation;
	}
	public Calendar getSunrise(){
		return sunrise;
	}
	public String getSunriseString(){
		return (new SimpleDateFormat("HH:mm")).format(sunrise.getTime());
	}
	public Calendar getSunset(){
		return sunset;
	}
	public String getSunsetString(){
		return (new SimpleDateFormat("HH:mm")).format(sunset.getTime());
	}
	public String toString(){
		return (new SimpleDateFormat("E, dd MMM yyyy")).format(day.getTime());
	}
	public Calendar getDay(){
		return day;
	}
	public Calendar getNextDay(){
		day.add(Calendar.DATE, 1);
		return day;
	}
	public Calendar getPrevDay(){
		day.add(Calendar.DATE, -1);
		return day;
	}

    /*
    * Check if this Day represents real-life 'today'
     */
	public Boolean isToday(){
		Calendar now = Calendar.getInstance();
		Calendar dayToCheck = getDay();
		if(now.get(Calendar.MONTH) == dayToCheck.get(Calendar.MONTH) && now.get(Calendar.YEAR) == dayToCheck.get(Calendar.YEAR) && now.get(Calendar.DAY_OF_MONTH) == dayToCheck.get(Calendar.DAY_OF_MONTH)){
			return true;
		}
		else{
			return false;
		}
	}

    /*
    * Get the current time of day for plotting on the graph.
     */
	public Double getCurrentTimePlot(){
		Calendar now = Calendar.getInstance();
		Double hour = now.get(Calendar.HOUR_OF_DAY)+0.0;
		Double minute = now.get(Calendar.MINUTE)+0.0;
		Double minuteOfHour = minute/60;
		return (hour+minuteOfHour);
	}
    /*
    * Get the sunrise time of today for plotting on the graph.
     */
	public Double getSunrisePlot(){
		Double hour = sunrise.get(Calendar.HOUR_OF_DAY)+0.0;
		Double minute = sunrise.get(Calendar.MINUTE)+0.0;
		Double minuteOfHour = minute/60;
		return (hour+minuteOfHour);
	}
    /*
    * Get the sunset time of today for plotting on the graph.
     */
	public Double getSunsetPlot(){
		Double hour = sunset.get(Calendar.HOUR_OF_DAY)+0.0;
		Double minute = sunset.get(Calendar.MINUTE)+0.0;
		Double minuteOfHour = minute/60;
		return (hour+minuteOfHour);
	}
    /*
    * Get an array of tide heights to plot on graph, stripping out the numeric values.
     */
	public Double[] getTideHeights(){
		ArrayList heights = new ArrayList();
		for(int i = 0; i < highTideHeights.length; i++){
			String highTideStripped = highTideHeights[i].replaceAll("\\s","");
			if(!highTideStripped.equals("")){
				String[] tokens = highTideStripped.split("m");
				
				if(i == 0){
	        		heights.add(2.0);
	        	}
				heights.add(Double.parseDouble(tokens[0]));
				if(i == 1 && lowTideTimes[1].equals("")){
					heights.add(2.0);
	        	}
				if(i == 2){
					heights.add(2.0);
	        	}
			}
			if(i < 2){
				String lowTideStripped = lowTideHeights[i].replaceAll("\\s","");
				if(!lowTideStripped.equals("")){
					String[] tokens = lowTideStripped.split("m");
					
					if(i == 0 && heights.size() == 0){
						heights.add(8.0);
		        	}
					heights.add(Double.parseDouble(tokens[0]));
					if(i == 1 && highTideTimes[2].equals("")){
						heights.add(8.0);
		        	}
				}
			}
		}
		return (Double[]) heights.toArray(new Double[heights.size()]);
	}
	/*
	* Get the time of tide events for plotting on the graph.
    */
	public Double[] getTideTimesPlot(){
		ArrayList times = new ArrayList();
		for(int i = 0; i < highTideTimes.length; i++){
			String highTimeStripped = highTideTimes[i].trim();
			if(!highTimeStripped.equals("")){
		        try {
                    Date date = dayView.dayDateFormatter.parse(highTideTimes[i].replace("BST", "GMT"));
		        	Calendar cal = Calendar.getInstance();
		        	cal.setTime(date);
		        	//if(highTideTimes[i].contains("BST")){
		        		cal.add(Calendar.HOUR, -1);
		        	//}
		        	int min = cal.get(Calendar.MINUTE);
		        	Double minOfHour = (min+0.0)/60.0;
		        	int hour = cal.get(Calendar.HOUR_OF_DAY);
		        	
		        	if(i == 0){
		        		times.add(hour-6.0);
		        	}
		        	times.add(hour+minOfHour);
		        	if(i == 1 && lowTideTimes[1].equals("")){
		        		times.add(hour+10.0);
		        	}
		        	if(i == 2){
		        		times.add(hour+6.0);
		        	}
				} 
		        catch (ParseException e) {
					System.out.println(e);
				}
			}
			if(i < 2){
				String lowTimeStripped = lowTideTimes[i].trim();
				if(!lowTimeStripped.equals("")){
			        try {
                        Date date = dayView.dayDateFormatter.parse(lowTideTimes[i].replace("BST", "GMT"));
			        	Calendar cal = Calendar.getInstance();
			        	cal.setTime(date);
			        	//if(lowTideTimes[i].contains("BST")){
			        		cal.add(Calendar.HOUR, -1);
			        	//}
			        	int min = cal.get(Calendar.MINUTE);
			        	Double minOfHour = (min+0.0)/60.0;
			        	int hour = cal.get(Calendar.HOUR_OF_DAY);
			        	
			        	if(i == 0 && times.size() == 0){
			        		times.add(hour-6.0);
			        	}
			        	times.add(hour+minOfHour);
			        	if(i == 1 && highTideTimes[2].equals("")){
			        		times.add(hour+6.0);
			        	}
					} 
			        catch (ParseException e) {
						System.out.println(e);
					}
				}
			}
		}
		return (Double[]) times.toArray(new Double[times.size()]);
	}

    /*
    * Get String[] representing order of tide events for the day. (e.g. high, low, high, low)
     */
	public String[] getTideTypes(){
		ArrayList types = new ArrayList();
		for(int i = 0; i < highTideTimes.length; i++){
			String highTimeStripped = highTideTimes[i].trim();
			if(!highTimeStripped.equals("")){
				types.add("high");
			}
			if(i < 2){
				String lowTimeStripped = lowTideTimes[i].trim();
				if(!lowTimeStripped.equals("")){
					types.add("low");
				}
			}
		}
		return (String[]) types.toArray(new String[types.size()]);
	}

    /*
    * Get Calendar[] representing the time of the tide events for the day (in same order as above method).
     */
	public Calendar[] getTideTimes(){
		ArrayList times = new ArrayList();
		for(int i = 0; i < highTideTimes.length; i++){
			String highTimeStripped = highTideTimes[i].trim();
			if(!highTimeStripped.equals("")){
		        try {
                    Date date = dayView.dayDateFormatter.parse(highTideTimes[i].replace("BST", "GMT"));
		        	Calendar cal = Calendar.getInstance();
		        	cal.setTime(date);
		        	//if(highTideTimes[i].contains("BST")){
		        		cal.add(Calendar.HOUR, -1);
		        	//}
		        	times.add(cal);
				} 
		        catch (ParseException e) {
					System.out.println(e);
				}
			}
			if(i < 2){
				String lowTimeStripped = lowTideTimes[i].trim();
				if(!lowTimeStripped.equals("")){
			        try {
                        Date date = dayView.dayDateFormatter.parse(lowTideTimes[i].replace("BST", "GMT"));
			        	Calendar cal = Calendar.getInstance();
			        	cal.setTime(date);
			        	//if(lowTideTimes[i].contains("BST")){
			        		cal.add(Calendar.HOUR, -1);
			        	//}
			        	times.add(cal);
					} 
			        catch (ParseException e) {
						System.out.println(e);
					}
				}
			}
		}
		return (Calendar[]) times.toArray(new Calendar[times.size()]);
	}
}
