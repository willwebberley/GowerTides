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

import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import net.willwebberley.gowertides.R;

public class PreferencesActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{

	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.layout.preferences);
	        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	        onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()), "");

		 Toolbar bar;

		 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			 LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
			 bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
			 root.addView(bar, 0); // insert at top
		 } else {
			 ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
			 ListView content = (ListView) root.getChildAt(0);

			 root.removeAllViews();

			 bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);


			 int height;
			 TypedValue tv = new TypedValue();
			 if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
				 height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
			 }else{
				 height = bar.getHeight();
			 }

			 content.setPadding(0, height, 0, 0);

			 root.addView(content);
			 root.addView(bar);
		 }

		 bar.setNavigationOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 finish();
             }
         });
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
