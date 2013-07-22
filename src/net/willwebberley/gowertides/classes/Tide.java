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

import android.database.Cursor;
import net.willwebberley.gowertides.ui.DaysActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;

/*
* Class representing a tidal event (type (low/high), time, height, etc.)
 */
public class Tide {
    public final int LOW = 0;
    public final int HIGH = 1;

    public Double height;
    public Calendar time;
    public double timeHours;
    public String type;

    // Used for showing tides on tide graph
    public String getTimeString(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(time.getTime());
    }

    // Used for determining tide type for tide graph
    public int getType(){
        if(type.equals("high")){return HIGH;}
        else{return LOW;}
    }

    // Get difference between a specified time and the time of this tide event as a String (HH:mm)
    public String getTimeDifference(Calendar cal){

        int h_diff = Math.abs(Hours.hoursBetween(new DateTime(time.getTime()), new DateTime(cal.getTime())).getHours() % 24);
        int m_diff = Math.abs(Minutes.minutesBetween(new DateTime(time.getTime()), new DateTime(cal.getTime())).getMinutes() % 60);

        String h_diff_s = h_diff+"";
        String m_diff_s = m_diff+"";
        if(m_diff < 10){
            m_diff_s = "0"+m_diff_s;
        }

        boolean negative = true; // negative if time of tide BEHIND current time
        long milli_diff = cal.getTime().getTime() - time.getTime().getTime();
        if(milli_diff < 0){
            negative = false;
        }

        if(negative){
            return "-"+h_diff_s+":"+m_diff_s;
        }
        else{
            return "+"+h_diff_s+":"+m_diff_s;
        }
    }


    /*
    * Read database cursors and generate list of Tide events for representative day.
     */
    public static ArrayList<Tide> initTides(ArrayList<Tide> tide_forecasts, DaysActivity parent, Cursor tideInfo, Day day) throws ParseException {
        // TIDE INFO:
        // 0year 1month 2day 3week_day 4sunrise 5sunset 6moon 7high1_time 8high1_height 9low1_time 10low1_height
        // 11high2_time 12high2_height 13low2_time 14low2_height 15high3_time 16high3_height

        tide_forecasts.clear();
        int tideCounter = 1;
        for(int i = 7; i <= 15; i = i+2){
            if(! tideInfo.getString(i).equals("")){
                Tide tide = new Tide();
                String type="high";
                if(tideCounter%2 == 0){type="low";}
                Calendar time = Calendar.getInstance();
                time.setTime(parent.dayDateFormatter.parse(tideInfo.getString(i).replace("BST", "").replace("GMT","")));
                time.set(Calendar.YEAR, day.getDay().get(Calendar.YEAR));
                time.set(Calendar.MONTH, day.getDay().get(Calendar.MONTH));
                time.set(Calendar.DAY_OF_MONTH, day.getDay().get(Calendar.DAY_OF_MONTH));
                tide.time = time;
                tide.timeHours = time.get(Calendar.HOUR_OF_DAY) + ((time.get(Calendar.MINUTE)+0.0) / 60);
                tide.height = Double.parseDouble((tideInfo.getString(i+1).replace("m","")).trim());
                tide.type = type;
                tide_forecasts.add(tide);
            }
            tideCounter++;
        }
        return tide_forecasts;
    }
}

