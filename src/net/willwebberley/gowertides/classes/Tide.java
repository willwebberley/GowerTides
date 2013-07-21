package net.willwebberley.gowertides.classes;

import android.database.Cursor;
import android.text.format.DateUtils;
import net.willwebberley.gowertides.ui.DaysActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
        boolean negative = false;
        long milli_diff = time.getTime().getTime() - cal.getTime().getTime();
        if(milli_diff < 0){
            negative = true;
            milli_diff= Math.abs(milli_diff);
        }
        Calendar time_diff = Calendar.getInstance();
        time_diff.setTimeInMillis(milli_diff);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.UK);
        String time_diff_str = sdf.format(time_diff.getTime());

        if(negative){
            return "-"+time_diff_str;
        }
        else{
            return "+"+time_diff_str;
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

