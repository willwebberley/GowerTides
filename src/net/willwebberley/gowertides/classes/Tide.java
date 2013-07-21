package net.willwebberley.gowertides.classes;

import android.database.Cursor;
import net.willwebberley.gowertides.ui.DaysActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

    public String getTimeDifference(Calendar cal){
        boolean negative = false;
        String hour_d, minute_d;
        int hour_diff = time.get(Calendar.HOUR_OF_DAY) - cal.get(Calendar.HOUR_OF_DAY);
        int minute_diff = time.get(Calendar.MINUTE) - cal.get(Calendar.MINUTE);
        if(hour_diff == 0 && minute_diff <=0){
            negative = true;
            minute_diff=Math.abs(minute_diff);
            minute_diff = 60-minute_diff;
        }
        if(hour_diff < 0){
            negative = true;
            minute_diff=Math.abs(minute_diff);
            minute_diff = 60-minute_diff;
            hour_diff=Math.abs(hour_diff);
        }

        if(negative){
            hour_diff--;
        }

        if(hour_diff < 10){
            hour_d = "0"+hour_diff;
        }
        else{hour_d = ""+hour_diff;}
        if(minute_diff < 10){
            minute_d = "0"+minute_diff;
        }
        else{minute_d = ""+minute_diff;}

        if(negative){
            return "-"+hour_d+":"+minute_d;
        }
        else{
            return "+"+hour_d+":"+minute_d;
        }
    }


    public static ArrayList<Tide> initTides(ArrayList<Tide> tide_forecasts, DaysActivity parent, Cursor tideInfo) throws ParseException {
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







    /*
    * Get the time of tide events for plotting on the graph.
    */
   /* public Double[] getTideTimesPlot(){
        ArrayList times = new ArrayList();
        for(int i = 0; i < highTideTimes.length; i++){
            String highTimeStripped = highTideTimes[i].trim();
            if(!highTimeStripped.equals("")){
                try {
                    Date date = dayView.dayDateFormatter.parse(highTideTimes[i].replace("GMT", ""));
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    //if(highTideTimes[i].contains("BST")){
                    //cal.add(Calendar.HOUR, -1);
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
                        Date date = dayView.dayDateFormatter.parse(lowTideTimes[i].replace("GMT", ""));
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        //if(lowTideTimes[i].contains("BST")){
                        //cal.add(Calendar.HOUR, -1);
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
    }*/

    /*
    * Get String[] representing order of tide events for the day. (e.g. high, low, high, low)
     */
    /*public String[] getTideTypes(){
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
    }*/

    /*
    * Get Calendar[] representing the time of the tide events for the day (in same order as above method).
     */
    /*public Calendar[] getTideTimes(){
        ArrayList times = new ArrayList();
        for(int i = 0; i < highTideTimes.length; i++){
            String highTimeStripped = highTideTimes[i].trim();
            if(!highTimeStripped.equals("")){
                try {
                    Date date = dayView.dayDateFormatter.parse(highTideTimes[i].replace("BST", "GMT"));
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    //if(highTideTimes[i].contains("BST")){
                    //cal.add(Calendar.HOUR, -1);
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
                        //	cal.add(Calendar.HOUR, -1);
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
    }*/
}

