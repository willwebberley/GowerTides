package net.willwebberley.gowertides.utils;

import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherDatabase extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "weather";
	
	public WeatherDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String create = "CREATE TABLE IF NOT EXISTS weather (timestamp INTEGER, " +
				"year INTEGER," +
				"month INTEGER," +
				"day INTEGER," +
				"max_temp_c INTEGER," +
				"max_temp_f INTEGER," +
				"min_temp_c INTEGER," +
				"min_temp_f INTEGER," +
				"wind_speed_miles INTEGER," +
				"wind_speed_km INTEGER," +
				"wind_direction TEXT," +
				"wind_degree INTEGER," +
				"icon_url TEXT," +
				"description TEXT," +
				"precipitation FLOAT)";
		db.execSQL(create);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS weather");
        onCreate(db);
	}
	
	public Boolean insertWeatherData(String data){
		SQLiteDatabase db = this.getWritableDatabase();
		//db.execSQL("DELETE FROM weather");
		try{
			JSONArray jsonArray = new JSONArray(data);
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject array = jsonArray.getJSONObject(i);
				JSONObject jsonObject = array.getJSONObject("weather");
				
				long timestamp = jsonObject.getLong("timestamp");
				int year = jsonObject.getInt("year");
				int month = jsonObject.getInt("month");
				int day = jsonObject.getInt("day");
				int max_temp_c = jsonObject.getInt("max_temp_c");
				int max_temp_f = jsonObject.getInt("max_temp_f");
				int min_temp_c = jsonObject.getInt("min_temp_c");
				int min_temp_f = jsonObject.getInt("min_temp_f");
				int wind_speed_miles = jsonObject.getInt("wind_speed_miles");
				int wind_speed_km = jsonObject.getInt("wind_speed_km");
				String wind_direction = jsonObject.getString("wind_direction");
				int wind_degree = jsonObject.getInt("wind_degree");
				String icon_url = jsonObject.getString("icon_url");
				String description = jsonObject.getString("weather_description");
				Double precipitation = jsonObject.getDouble("precipitation");
				
				String inS = "INSERT INTO weather VALUES(" +
						timestamp+","+year+","+month+","+day+","+max_temp_c+","+max_temp_f+","+min_temp_c+
						","+min_temp_f+","+wind_speed_miles+","+wind_speed_km+",'"+wind_direction+"',"+
						wind_degree+",'"+icon_url+"','"+description+"',"+precipitation+")";
				db.execSQL(inS);
			}
		}
		catch(Exception e){
			System.out.println(e);
			return false;
		}
		return true;
	}
	
	public Cursor getWeatherInfo(Calendar dayToGet){	
		SQLiteDatabase db = this.getReadableDatabase();
		int year = dayToGet.get(Calendar.YEAR);
		int month = dayToGet.get(Calendar.MONTH)+1;
		int day = dayToGet.get(Calendar.DAY_OF_MONTH);
		
		try{
			Cursor result = db.rawQuery("SELECT * FROM weather WHERE year = "+year+" AND MONTH = "+month+" AND DAY = "+day+" ORDER BY timestamp",null);
			result.moveToLast();
			return result;
		}
		catch(Exception e){
			System.out.println(e);
			return null;
		}
	}

}
