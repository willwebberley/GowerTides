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

package net.willwebberley.gowertides.utils;

import android.database.Cursor;
import android.os.AsyncTask;
import net.willwebberley.gowertides.classes.Day;
import net.willwebberley.gowertides.classes.Surf;
import net.willwebberley.gowertides.classes.Tide;
import net.willwebberley.gowertides.classes.Weather;
import net.willwebberley.gowertides.ui.DaysActivity;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class Utilities {

    /*
    * Create a new day instance from the database using the Calendar day specified
     */
    public static Day createDay(DayDatabase db1, WeatherDatabase db2, Calendar cal, int location){
        Day day = new Day(cal);
        reprocessDay(db1, db2, day, location);
        return day;
    }

    /*
    * Carry out the actual processing of the data from the database for the specified day.
    * Used when first creating Days and also for refreshing data from the DBs after creation.
    * (Does not return a Day - instead simply modifies a Day passed as a parameter).
     */
    public static void reprocessDay(DayDatabase db1, WeatherDatabase db2, Day day, int location){
        Cursor tideInfo = db1.getDayInfo(day.getDay());
        Cursor weatherInfo = db2.getWeatherInfo(day.getDay());
        Cursor surfInfo = db2.getSurfInfo(day.getDay(), location);

        Weather w = null;
        ArrayList<Surf> s = null;
        ArrayList<Tide> t = null;
        Boolean w_available = false;
        Boolean s_available = false;
        Boolean t_available = false;
        Calendar sunrise = null, sunset = null;
        String moon = null;

        try{
            // TIDE INFO:
            // 0year 1month 2day 3week_day 4sunrise 5sunset 6moon 7high1_time 8high1_height 9low1_time 10low1_height
            // 11high2_time 12high2_height 13low2_time 14low2_height 15high3_time 16high3_height
            sunrise = (Calendar.getInstance());
            sunrise.setTime(Constants.getDateFormat().parse(tideInfo.getString(4).replace("BST", "").replace("GMT","")));
            sunset = Calendar.getInstance();
            sunset.setTime(Constants.getDateFormat().parse(tideInfo.getString(5).replace("BST", "").replace("GMT","")));
            moon = tideInfo.getString(6);
        }catch(Exception e){}
        try{
            w = Weather.initWeather(weatherInfo);
            w_available = true;
        }catch(Exception e){}
        try{
            s = Surf.initSurf(surfInfo);
            s_available = true;
        }catch(Exception e){}
        try{
            t = Tide.initTides(tideInfo, day.getDay());
            t_available = true;
        }catch(Exception e){}

        try{
            tideInfo.close();
            weatherInfo.close();
            surfInfo.close();
        }catch(Exception e){System.err.println("Could not close DBs: "+e);}

        day.setGeneral(sunrise,sunset,moon);
        day.setSurf(s, s_available);
        day.setTide(t, t_available);
        day.setWeather(w, w_available);
    }


    /*
     * AsyncTask to fetch new surf and weather data. Notifies parent Activity upon completion.
     */
    public static  class DataGetter extends AsyncTask<Object, Integer, Boolean> {

        private DaysActivity parent;
        private int location;
        private WeatherDatabase db;

        public DataGetter(DaysActivity d, int l, WeatherDatabase w){
            parent = d;
            location = l;
            db = w;
        }

        @Override
        protected Boolean doInBackground(Object... arg0) {
            System.out.println("Starting download...");

            // Prepare the readers:
            BufferedReader reader = null;
            StringBuffer completeData = new StringBuffer();

            // Get some data on the device
            String androidVersion = android.os.Build.VERSION.RELEASE;
            String model = android.os.Build.MANUFACTURER+"-"+android.os.Build.MODEL.replace(" ", "-");

            // Prepare the query URL:
            String address = Constants.getDataURL()+"?dev="+model+"&ver="+androidVersion+"&loc="+location;

            // Make the request:
            try {
                URL url = new URL(address);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                InputStream in = con.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    completeData.append(line);
                }

                // Now save the data returned by the server:
                System.out.println("Finished. Adding to db...");

                Boolean success = db.insertAllData(completeData.toString());

                // All done.
                System.out.println("Done.");
                if(!success){
                    return false;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
            return true;
        }

        /*
        * On finish, update day fragments to show surf and flag the process as complete.
        *
        * If unsuccessful, for any reason, show an error.
         */
        protected void onPostExecute(Boolean result) {
            parent.notifySyncFinished(result);
        }
    }



}
