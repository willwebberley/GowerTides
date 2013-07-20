package net.willwebberley.gowertides.classes;


import android.database.Cursor;

import java.util.ArrayList;

public class Surf {

    public int hour;
    public int location;
    public long local_time;
    public int faded_rating;
    public int solid_rating;
    public double min_surf;
    public double abs_min_surf;
    public double max_surf;
    public double abs_max_surf;
    public double swell_height;
    public double swell_period;
    public double swell_angle;
    public String swell_direction;
    public String swell_chart_url;
    public String period_chart_url;
    public String Wind_chart_url;
    public String pressure_chart_url;
    public String sst_chart_url;


    public static ArrayList<Surf> initSurf(ArrayList<Surf> surf_reports, Cursor surfInfo){
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
        return surf_reports;
    }

}
