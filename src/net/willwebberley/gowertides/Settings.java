package net.willwebberley.gowertides;

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

public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener{

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
