package net.willwebberley.gowertides.utils;


import net.willwebberley.gowertides.R;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Locale;

public class Constants {

    private static SimpleDateFormat dayDateFormatter = new SimpleDateFormat("h:m a", Locale.US);
    private static String dataURL = "http://tides.flyingsparx.net/fetch/both/";

    public static SimpleDateFormat getDateFormat(){
        dayDateFormatter.setTimeZone(TimeZone.getDefault());
        return dayDateFormatter;
    }

    public static String getDataURL(){
        return dataURL;
    }

}
