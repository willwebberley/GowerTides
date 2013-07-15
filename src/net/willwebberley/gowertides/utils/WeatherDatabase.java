package net.willwebberley.gowertides.utils;

import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherDatabase extends SQLiteOpenHelper{

    // Version 2: added 'surf' table
    // Version 3: added 'location' column to 'surf' table
	private static final int DATABASE_VERSION = 3;
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
        String create2 = "CREATE TABLE IF NOT EXISTS surf (" +
                "location INTEGER," +
                "timestamp INTEGER," +
                "local_time INTEGER," +
                "year INTEGER," +
                "month INTEGER," +
                "day INTEGER," +
                "hour INTEGER," +
                "minute INTEGER," +
                "faded_rating INTEGER," +
                "solid_rating INTEGER," +
                "min_surf REAL," +
                "abs_min_surf REAL," +
                "max_surf REAL," +
                "abs_max_surf REAL," +
                "swell_height REAL," +
                "swell_period REAL," +
                "swell_angle REAL," +
                "swell_direction TEXT," +
                "swell_chart_url TEXT," +
                "period_chart_url TEXT," +
                "wind_chart_url TEXT," +
                "pressure_chart_url TEXT," +
                "sst_chart_url TEXT)";
        db.execSQL(create2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("upgrading db");
		db.execSQL("DROP TABLE IF EXISTS weather");
        db.execSQL("DROP TABLE IF EXISTS surf");
        onCreate(db);
	}
	
	public Boolean insertWeatherData(String data, SQLiteDatabase db){
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

    public Boolean insertSurfData(String data, SQLiteDatabase db){
        try{
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject surf = jsonArray.getJSONObject(i);
                int location = surf.getInt("location");
                long timestamp = surf.getLong("timestamp");
                long localtime = surf.getLong("local_time");
                int year = surf.getInt("year");
                int month = surf.getInt("month");
                int day = surf.getInt("day");
                int hour = surf.getInt("hour");
                int minute = surf.getInt("minute");
                int faded_rating = surf.getInt("faded_rating");
                int solid_rating = surf.getInt("solid_rating");
                double min_surf = surf.getDouble("min_surf_height");
                double abs_min_surf = surf.getDouble("abs_min_surf_height");
                double max_surf = surf.getDouble("max_surf_height");
                double abs_max_surf = surf.getDouble("abs_max_surf_height");
                double swell_height = surf.getDouble("swell_height");
                double swell_period = surf.getDouble("swell_period");
                double swell_angle = surf.getDouble("swell_angle");
                String swell_direction = surf.getString("swell_direction");
                String swell_chart_url = surf.getString("swell_chart");
                String period_chart_url = surf.getString("period_chart");
                String wind_chart_url = surf.getString("wind_chart");
                String pressure_chart_url = surf.getString("pressure_chart");
                String sst_chart_url = surf.getString("sst_chart");

                

                String inS = "INSERT INTO surf VALUES(" +
                        location+","+timestamp+","+localtime+","+year+","+month+","+day+","+hour+","+minute+","+faded_rating+","+
                        solid_rating+","+min_surf+","+abs_min_surf+","+max_surf+","+
                        abs_max_surf+","+swell_height+","+swell_period+","+swell_angle+",'"+swell_direction+"','"+
                        swell_chart_url+"','"+period_chart_url+"','"+wind_chart_url+"','"+pressure_chart_url+"','"+sst_chart_url+"')";
                db.execSQL(inS);
            }
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
        return true;
    }

    public Boolean insertAllData(String data){
        JSONObject jsonArray = null;
        JSONArray weatherArray = null;
        JSONArray surfArray = null;

        try{
            jsonArray = new JSONObject(data);
            weatherArray = jsonArray.getJSONArray("weather");
            surfArray = jsonArray.getJSONArray("surf");
        }
        catch(Exception e){
            System.err.println("couldn't parse JSON");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        int err_count = 0;

        /* Insert weather data */
        db.beginTransaction();
        Boolean weatherSuccess = insertWeatherData(weatherArray.toString(), db);
        if(!weatherSuccess){
            err_count ++;
            System.err.println("Error storing weather");
            db.endTransaction();
        }
        else{
            db.setTransactionSuccessful();
            db.endTransaction();
        }


        /* Insert surf data - using transactions to help performance */
        db.beginTransaction();
        Boolean surfSuccess = insertSurfData(surfArray.toString(), db);
        if(!surfSuccess){
            err_count ++;
            System.err.println("Error storing surf");
            db.endTransaction();
        }
        else{
            db.setTransactionSuccessful();
            db.endTransaction();
        }

        if(err_count > 0){
            db.endTransaction();
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

    public Cursor getSurfInfo(Calendar dayToGet, int location){
        SQLiteDatabase db = this.getReadableDatabase();
        int year = dayToGet.get(Calendar.YEAR);
        int month = dayToGet.get(Calendar.MONTH)+1;
        int day = dayToGet.get(Calendar.DAY_OF_MONTH);
        try{
            Cursor result = db.rawQuery("SELECT * FROM surf WHERE year = "+year+" AND month = "+month+" AND day = "+day+" AND location = "+location+" ORDER BY timestamp DESC",null);

            result.moveToFirst();
            return result;
        }
        catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

}
