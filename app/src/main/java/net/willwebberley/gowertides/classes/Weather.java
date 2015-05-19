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

import java.io.Serializable;

/**
 * Created by flyingsparx on 20/07/13.
 */
public class Weather  implements Serializable {

    public int max_temp_c;
    public int max_temp_f;
    public int min_temp_c;
    public int min_temp_f;
    public int wind_speed_miles;
    public int wind_speed_km;
    public String wind_direction;
    public int wind_degree;
    public String icon_url;
    public String description;
    public Double precipitation;


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
	public String getWeatherIcon(){
		String[] tokens = icon_url.split("\\/");
		for(int i = 0; i < tokens.length; i++){
			if(tokens[i].contains("wsymbol_0")){
				return tokens[i];
			}
		}
		return null;
	}


    public static Weather initWeather(Cursor weatherInfo){
        // WEATHER INFO:
        // 0timestamp 1year 2month 3day 4max_temp_c 5max_temp_f 6min_temp_c 7min_temp_f 8wind_speed_miles
        // 9wind_speed_km 10wind_direction 11wind_degree 12icon_url 13description 14precipitation

        Weather weather = new Weather();
        weather.max_temp_c = weatherInfo.getInt(4);
        weather.max_temp_f = weatherInfo.getInt(5);
        weather.min_temp_c = weatherInfo.getInt(6);
        weather.min_temp_f = weatherInfo.getInt(7);
        weather.wind_speed_miles = weatherInfo.getInt(8);
        weather.wind_speed_km = weatherInfo.getInt(9);
        weather.wind_direction = weatherInfo.getString(10);
        weather.wind_degree = weatherInfo.getInt(11);
        weather.icon_url = weatherInfo.getString(12);
        weather.description = weatherInfo.getString(13);
        weather.precipitation = weatherInfo.getDouble(14);
        return weather;
    }
}
