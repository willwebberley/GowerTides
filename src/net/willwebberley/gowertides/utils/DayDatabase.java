package net.willwebberley.gowertides.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import net.willwebberley.gowertides.classes.Day;


public class DayDatabase extends SQLiteAssetHelper {

	private static final String DATABASE_NAME = "tides";
	private static final int DATABASE_VERSION = 1;

	public DayDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);  
	}

    public Calendar getFirstDay(){
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM data ORDER BY year ASC, month ASC, day ASC", null);
        c.moveToFirst();
        Calendar cal = parseString(c.getInt(2)+"/"+c.getInt(1)+"/"+c.getInt(0));
        c.close();
        return cal;
    }
    public Calendar getLastDay(){
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM data ORDER BY year ASC, month ASC, day ASC", null);
        c.moveToLast();
        Calendar cal = parseString(c.getInt(2)+"/"+c.getInt(1)+"/"+c.getInt(0));
        c.close();
        return cal;
    }

    private Calendar parseString(String test){
        Date tester = null;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            tester = (Date)sdf.parse(test);
        }
        catch(Exception e){
            System.err.println(e);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(tester);
        return cal;
    }

	public Cursor getDayInfo(java.util.Calendar todayDate) {
		int month = (todayDate.get(Calendar.MONTH))+1;
		int year = todayDate.get(Calendar.YEAR);
		int day = todayDate.get(Calendar.DAY_OF_MONTH);
		
		SQLiteDatabase db = getReadableDatabase();

		Cursor c = db.rawQuery("SELECT * FROM data WHERE year="+year+" AND month="+month+" AND day="+day, null);

		c.moveToFirst();
		return c;

	}
}
