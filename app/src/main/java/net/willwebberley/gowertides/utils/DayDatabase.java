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

    // Will force upgrade databases less than this version to the one stored in /assets.
    // When updating tide database, increment both of these values (version and upgrade_version).
    private static final int UPGRADE_VERSION = 1;

	public DayDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgradeVersion(UPGRADE_VERSION);
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
		try{
			SQLiteDatabase db = getReadableDatabase();
			Cursor c = db.rawQuery("SELECT * FROM data WHERE year="+year+" AND month="+month+" AND day="+day, null);

			c.moveToFirst();
			return c;
		}
		catch(Exception e){
			System.err.println(e);
		}


		return null;

	}
}
