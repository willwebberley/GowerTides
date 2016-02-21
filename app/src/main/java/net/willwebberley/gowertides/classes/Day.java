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

import java.io.Serializable;
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
public class Day implements Serializable{

    /*
    * Calendar representation of Day
     */
	private Calendar day;

    /*
    * Fields to hold day, tidal, weather, surf data
     */
    private Calendar sunrise;
    private Calendar sunset;
    private String moon;
    private Weather weather;
    private ArrayList<Surf> surf_reports = new ArrayList<Surf>();
    private ArrayList<Tide> tide_forecasts = new ArrayList<Tide>();

    private Day yesterday, tomorrow;

    /*
    * Helper fields
     */
	private Context context; // application context
	private Boolean weatherAvailable; // true if weather data is available for day
    private Boolean surfAvailable; // true if surf data is available for day
    private Boolean tidesAvailable; // true if tides data is available for day

    /*
    * Instantiate object with a Calendar day to represent,
     */
    public Day(Calendar date){
        day = date;
    }

    /*
    * Set the day's information (which is processed externally from the databases):
     */
    public void setGeneral(Calendar sunrise, Calendar sunset, String moon){
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.moon = moon;
    }
    public void setTide(ArrayList<Tide> t, Boolean available){
        this.tide_forecasts = t;
        this.tidesAvailable = available;
    }
    public void setWeather(Weather w, Boolean available){
        this.weather = w;
        this.weatherAvailable = available;
    }
    public void setSurf(ArrayList<Surf> s, Boolean available){
        this.surf_reports = s;
        this.surfAvailable = available;
    }


    /*
    * Set the instance of Day representing the day before this one (for showing continuation of tide graph)
     */
    public void setYesterday(Day y){
        yesterday = y;
    }
    public Day getYesterday(){
        return yesterday;
    }


    /*
    * Set instance of Day representing day after this one (for showing continuation of tide graph)
     */
    public void setTomorrow(Day t){
        tomorrow = t;
    }
    public Day getTomorrow(){
        return tomorrow;
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
    * Publicly-available standard getter methods for this class.
     */
    public ArrayList<Surf> getSurfReports(){
        return surf_reports;
    }
    public ArrayList<Tide> getTides(){
        return tide_forecasts;
    }
    public Weather getWeather(){
        return weather;
    }

    /*
    * Get the sunrise time of day in hours (for plotting on the graph) (add 0.0 to make it Double type)
     */
    public Double getSunriseTimeHours(){
        return (sunrise.get(Calendar.HOUR_OF_DAY)+((sunrise.get(Calendar.MINUTE)+0.0)/60)+0.0);
    }

    /*
    * Get the sunset time of day in hours (for plotting on the graph) (add 0.0 to make it Double type)
     */
    public Double getSunsetTimeHours(){
        return (sunset.get(Calendar.HOUR_OF_DAY)+((sunset.get(Calendar.MINUTE)+0.0)/60)+0.0);
    }

    /*
    * Get the current time of day in hours (for plotting on the graph) (add 0.0 to make it Double type)
     */
    public Double getCurrentTimeHours(){
        Calendar now = Calendar.getInstance();
        return (now.get(Calendar.HOUR_OF_DAY)+((now.get(Calendar.MINUTE)+0.0)/60)+0.0);
    }

    /*
    *  Get sunrise and sunset times in different formats
     */
	public String getSunriseString(){
		return (new SimpleDateFormat("HH:mm")).format(sunrise.getTime());
	}
	public String getSunsetString(){
		return (new SimpleDateFormat("HH:mm")).format(sunset.getTime());
	}
    public Calendar getSunset(){
        return sunset;
    }

    /*
    * String representation of this Day (shown at top of main activity)
     */
	public String toString(){
		return (new SimpleDateFormat("E, dd MMM yyyy")).format(day.getTime());
	}

    public Calendar getDay(){
        return day;
    }

    /*
    * Check if this Day represents real-life 'day'
     */
	public Boolean isToday(){
		Calendar now = Calendar.getInstance();
		Calendar dayToCheck = day;
		if(now.get(Calendar.MONTH) == dayToCheck.get(Calendar.MONTH) && now.get(Calendar.YEAR) == dayToCheck.get(Calendar.YEAR) && now.get(Calendar.DAY_OF_MONTH) == dayToCheck.get(Calendar.DAY_OF_MONTH)){
			return true;
		}
		else{
			return false;
		}
	}

}
