package net.willwebberley.gowertides.classes;

import android.database.Cursor;

/**
 * Created by flyingsparx on 20/07/13.
 */
public class Weather {

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
