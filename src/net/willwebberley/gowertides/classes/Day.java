package net.willwebberley.gowertides.classes;

import java.text.*;
import java.util.*;

import android.content.Context;
import android.database.Cursor;
import net.willwebberley.gowertides.ui.DaysActivity;
import net.willwebberley.gowertides.utils.*;

/*
* Class to represent a Day. Holds various fields of information regarding the day, including tidal, weather,
* and sunrise-sunset information.
*
* This is not a UI class, but contains the data represented in a DayFragment fragment.
 */
public class Day {

    /*
    * Calendar representation of Day
     */
	private Calendar day;

    /*
    * Fields to hold tidal data
     */
	/*private String[] highTideTimes;
	private String[] highTideHeights;
	private String[] lowTideTimes;
	private String[] lowTideHeights;
	private Calendar sunrise;
	private Calendar sunset;
	private String moon;*/

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
    * Fields to hold day, tidal, weather, surf data
     */
    private Calendar sunrise;
    private Calendar sunset;
    private String moon;
    private ArrayList<Surf> surf_reports = new ArrayList<Surf>();
    private ArrayList<Tide> tide_forecasts = new ArrayList<Tide>();

    /*
    * Helper fields
     */
	private Context context; // application context
	private Boolean weatherAvailable; // true if weather data is available for today
    private Boolean surfAvailable; // true if surf data is available for today
    private Boolean tidesAvailable; // true if tides data is available for today

	private DayDatabase db; // intance of the database holding tidal data
	private WeatherDatabase weather_db; // instance of the database holding weather and surf data
	private DaysActivity dayView; // Top level activity for application (to access context, databases, etc.)

    /*
    * Instantiate object with a Calendar day to represent, the application context and the main DaysActivity activity.
    * Activity is needed to access the databases.
     */
	public Day(Calendar date, Context con, DaysActivity dv){
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
		// TIDE INFO:
		// 0year 1month 2day 3week_day 4sunrise 5sunset 6moon 7high1_time 8high1_height 9low1_time 10low1_height 
		// 11high2_time 12high2_height 13low2_time 14low2_height 15high3_time 16high3_height
		//
		// WEATHER INFO:
		// 0timestamp 1year 2month 3day 4max_temp_c 5max_temp_f 6min_temp_c 7min_temp_f 8wind_speed_miles
		// 9wind_speed_km 10wind_direction 11wind_degree 12icon_url 13description 14precipitation
        //
        // SURF INFO:
        // 0location 1timestamp 2local_time 3year 4month 5day 6hour 7minute 8faded_rating 9solid_rating 10min_surf 11abs_min_surf 12max_surf
        // 13abs_max_surf 14swell_height 15swell_period 16swell_angle 17swell_direction 18swell_chart 19period_chart
        // 20wind_chart 21pressure_chart 22sst_chart
		Cursor tideInfo = db.getDayInfo(day);
		Cursor weatherInfo = weather_db.getWeatherInfo(day);
        Cursor surfInfo = weather_db.getSurfInfo(day, dayView.locationKeys[dayView.locationIndex]);

        try{
            sunrise = Calendar.getInstance();
            sunrise.setTime(dayView.dayDateFormatter.parse(tideInfo.getString(4).replace("BST", "").replace("GMT","")));
            sunset = Calendar.getInstance();
            sunset.setTime(dayView.dayDateFormatter.parse(tideInfo.getString(5).replace("BS new ArrayList<Tide>();T", "").replace("GMT","")));
            moon = tideInfo.getString(6);
        }
        catch(Exception e){
            System.err.println(e);
        }

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
        * Exception will be thrown if there is no stored weather data for this Day.
         */
		catch(Exception e){
			weatherAvailable = false;
		}

        try{
            // Assumes data from DB is returned ordered by timestamp DESC
            long recent_request_timestamp = surfInfo.getLong(1);
            surf_reports.clear();
            // Only get data for the most recent timestamp:
            while (! surfInfo.isLast() && surfInfo.getLong(1) == recent_request_timestamp){
                Surf surf = new Surf();
                surf.hour = surfInfo.getInt(6);
                surf.location = surfInfo.getInt(0);
                surf.local_time = surfInfo.getLong(2);
                surf.faded_rating = surfInfo.getInt(8);
                surf.solid_rating= surfInfo.getInt(9);
                surf.min_surf = surfInfo.getDouble(10);
                surf.abs_min_surf= surfInfo.getDouble(11);
                surf.max_surf = surfInfo.getDouble(12);
                surf.abs_max_surf= surfInfo.getDouble(13);
                surf.swell_height= surfInfo.getDouble(14);
                surf.swell_period= surfInfo.getDouble(15);
                surf.swell_angle = surfInfo.getDouble(16);
                surf.swell_direction= surfInfo.getString(17);
                surf.swell_chart_url= surfInfo.getString(18);
                surf.period_chart_url= surfInfo.getString(19);
                surf.Wind_chart_url= surfInfo.getString(20);
                surf.pressure_chart_url= surfInfo.getString(21);
                surf.sst_chart_url= surfInfo.getString(22);
                surf_reports.add(surf);

                surfInfo.moveToNext();
            }
            surfAvailable = true;
        }
        /*
        * Exception will be thrown if there is no stored surf data for this Day.
         */
        catch(Exception e){
            surfAvailable = false;
        }

		try{
            tide_forecasts.clear();
            int tideCounter = 1;
            for(int i = 7; i <= 15; i = i+2){
                if(! tideInfo.getString(i).equals("")){
                    Tide tide = new Tide();
                    String type="high";
                    if(tideCounter%2 == 0){type="low";}
                    Calendar time = Calendar.getInstance();
                    time.setTime(dayView.dayDateFormatter.parse(tideInfo.getString(i).replace("BST", "").replace("GMT","")));
                    tide.time = time;
                    tide.timeHours = time.get(Calendar.HOUR_OF_DAY) + (time.get(Calendar.MINUTE) / 60);
                    tide.height = Double.parseDouble((tideInfo.getString(i+1).replace("m","")).trim());
                    tide.type = type;
                    tide_forecasts.add(tide);
                }
                tideCounter++;
            }
		}
		catch(Exception e){
			System.out.println(e);
		}

        /*
        * Close the three cursors needed to get this data.
         */
       try{
        tideInfo.close();
        weatherInfo.close();
        surfInfo.close();
       }
       catch(Exception e){
           System.err.println("Could not close DBs: "+e);
       }
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
    public ArrayList<Surf> getSurfReports(){
        return surf_reports;
    }
    public ArrayList<Tide> getTides(){
        return tide_forecasts;
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

    /*
    * Get the sunrise time of today in hours (for plotting on the graph) (add 0.0 to make it Double type)
     */
    public Double getSunriseTimeHours(){
        return (sunrise.get(Calendar.HOUR_OF_DAY)+(sunrise.get(Calendar.MINUTE)/60)+0.0);
    }

    /*
    * Get the sunset time of today in hours (for plotting on the graph) (add 0.0 to make it Double type)
     */
    public Double getSunsetTimeHours(){
        return (sunset.get(Calendar.HOUR_OF_DAY)+(sunset.get(Calendar.MINUTE)/60)+0.0);
    }

    /*
    * Get the current time of day in hours (for plotting on the graph) (add 0.0 to make it Double type)
     */
    public Double getCurrentTimeHours(){
        Calendar now = Calendar.getInstance();
        return (now.get(Calendar.HOUR_OF_DAY)+(now.get(Calendar.MINUTE)/60)+0.0);
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



}
