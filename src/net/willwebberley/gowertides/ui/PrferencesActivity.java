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

package net.willwebberley.gowertides.ui;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import net.willwebberley.gowertides.R;

public class PrferencesActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{

	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.layout.preferences);
	        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	        onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()), "");
	    }

	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		CheckBoxPreference timeBox = (CheckBoxPreference)getPreferenceScreen().findPreference("show_graph_time");
		CheckBoxPreference sunriseSunsetBox = (CheckBoxPreference)getPreferenceScreen().findPreference("show_graph_sunrise_sunset");
		ListPreference metric = (ListPreference)getPreferenceScreen().findPreference("unitFormat");
		CheckBoxPreference timerBox = (CheckBoxPreference)getPreferenceScreen().findPreference("show_sunset_timer");

		if(!arg0.getBoolean("show_graph", true)){
			timeBox.setEnabled(false);
			sunriseSunsetBox.setEnabled(false);
		}
		else{
			timeBox.setEnabled(true);
			sunriseSunsetBox.setEnabled(true);
		}
		
		if(arg0.getString("unitFormat", "true").equals("true")){
			metric.setSummary("Display weather units in metric (km/h, Celcius).");
		}
		else{
			metric.setSummary("Display weather units in imperial (mph, Faranheit).");
		}
		
		if(!arg0.getBoolean("show_sunrise_sunset", true)){
			timerBox.setEnabled(false);
		}
		else{
			timerBox.setEnabled(true);
		}
				
	}
}
