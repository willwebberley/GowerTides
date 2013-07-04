package net.willwebberley.gowertides;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import net.willwebberley.gowertides.classes.*;
import net.willwebberley.gowertides.utils.DayDatabase;
import net.willwebberley.gowertides.utils.WeatherDatabase;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

/*
 * Main Activity of application
 *
 * This class maintains the pageviewer of days as well as indicating the currently selected day and handles some button
  * press events.
  *
  * Pageviewer contains a list of fragments, each representing a day.
  *
  * This class ia also responsible for network tasks (getting weather), and communicating this to the day fragments.
  *
 */
public class Dayview extends FragmentActivity {

	private SharedPreferences prefs;
	private ViewPager infoPager;
    private int todayFragmentIndex;
    private int currentFragmentIndex;
    private Calendar[] infoArray;
	private Calendar currentDay, firstDay, lastDay;
	private List<DayInfo> fragments;
	private PagerAdapter mPagerAdapter;
	public DayDatabase db;
	public WeatherDatabase weather_db;
    public Boolean weatherSycing, isPaused;
    public SimpleDateFormat dayDateFormatter;

    private final int DAYS_TO_StORE = 100;

	private TextView dateText;
	private ImageView revertButton;
    private RelativeLayout buildProgressHolder;
    private ProgressBar buildProgress;


    /*
    * onCreate() called upon activity start.
    *
    * Responsible for initializing major components, loading app preferences and initializing some global variables.
    * Creates the viewpager object and assigns an adapter.
    *
    * Starts the StartupTasks thread to handle long-running startup tasks (while progress bar shows on UI).
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dayview);
        /*
        * Following two variables used by day fragments to check the status of parent activity
         */
        weatherSycing = false;
        isPaused = false;
        /*
        * Following one variable stored by Parent activity to speed up startup on Android v2.2.
        * (Previously each Day class responsibe for maintaining, which caused slowdowns)
         */
        dayDateFormatter = new SimpleDateFormat("h:m a z");
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        initComponents();
        fragments = new Vector<DayInfo>();
        mPagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);
        infoPager.setAdapter(mPagerAdapter);

        new StartupTasks().execute("");

    }

    /*
    * Called from StartupTasks thread and is used to populate the viewpager with the day fragments.
    *
    * Creates DAYS_TO_STORE number of day fragments equally distributed around the current day and calculates and stores
    * the index of the current day of the list.
     */
    private void populatePager(Calendar newToday, int daysToLoad){
        fragments.clear();
        Calendar startDay = (Calendar)newToday.clone();
        // Change the day to start the pager at (e.g., if 4, will start at today - DAYS_TO_STORE/4 and end at
        // 3*DAYS_TO_STORE/4.
        startDay.add(Calendar.DATE,-(daysToLoad/4));
        for(int i =0; i < DAYS_TO_StORE; i++){
            Calendar newDay = (Calendar)startDay.clone();
            newDay.add(Calendar.DATE,i);
            infoArray[i] = newDay;
            DayInfo dayToAdd = new DayInfo(new Day(newDay, getApplicationContext(), this), prefs, this);
            if (currentDay.getTimeInMillis() == newDay.getTimeInMillis()){
                todayFragmentIndex = i;
            }
            fragments.add(dayToAdd);
            if (newDay.getTimeInMillis() == lastDay.getTimeInMillis()){
                break;
            }
        }
    }

    /*
    * Inner class to act as the PagerAdapter for the viewpager.
    *
    * Implements various methods to assist with handling the pager.
     */
    class PagerAdapter extends FragmentPagerAdapter {
        private List<DayInfo> fragments;
        public PagerAdapter(android.support.v4.app.FragmentManager fm, List<DayInfo> fragments) {
            super(fm);
            this.fragments = fragments;
        }
        @Override
        public DayInfo getItem(int position) {
            return this.fragments.get(position);
        }
        @Override
        public int getCount() {
            return this.fragments.size();
        }
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    /*
     * Invoke this method (in onCreate()) for testing.
     * Allows the app to be started at a desired date instead of 'today'
     */
    private Calendar setDayForTesting(String test){
    	Date tester = null;
    	try{
    		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    		tester = (Date)sdf.parse(test);
    	}
    	catch(Exception e){
    		System.err.println(e);
    	}

    	Calendar cal = Calendar.getInstance();
    	return cal;
    }

    /*
     * Generate options menu (from activity_dayview.xml in menu/)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_dayview, menu);
        return true;
    }

    /******
     *
     * Activity methods
     */

    /*
     * When application resumes, refresh the UI of each day fragment..
     */
    public void onResume(){
    	super.onResume();
        isPaused = false;
        try{
    	    fragmentsRefreshUI();
        }
        catch(Exception e){
            System.err.println(e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        isPaused = true;
    }

        /******
         *
         * MENU METHODS
         */

    /*
     * Listen for click events on the options menu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_about:
            	Intent intent_about = new Intent(this, About.class);
                startActivity(intent_about);
                return true;
            case R.id.menu_settings:
            	Intent intent_settings = new Intent(this, Settings.class);
                startActivity(intent_settings);
                return true;
            case R.id.menu_revert:
            	toDay(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /******
     *
     * BUTTON CLICK METHODS
     */
    /*
     * Executed if there's an issue loading the requested date. If exception thrown
     * assume date doesn't exist and make the error message visible.
     */
    private void dayNotFound(){
    	((TextView)findViewById(R.id.dayNotFound)).setVisibility(View.VISIBLE);
    	((ScrollView)findViewById(R.id.scrollView)).setVisibility(View.GONE);
    }

    /*
     * Set the current day to the current day. If error, assume no data for day.
     * (Hopefully this will never result in a error if app is updated!)
     */
    public void toDay(View view){
    	try{
            infoPager.setCurrentItem(todayFragmentIndex, true);
    	}
    	catch(Exception e){
    		System.err.println("Could not load day");
    		dayNotFound();
    	}
    }

    /*
     * If a network connection is available, sync the weather. Else show an error.
     *
     * Weather is synced using a threaded AsyncTask. If net unavailable, toast an error.
     */
    public void syncWeather(View view){
        weatherSycing = true;
        fragmentsStartWeatherSync();
    	if(this.isOnline()){
    		new SyncWeatherTask().execute("http://tides.flyingsparx.net/fetch");
    	}
    	else{
    		Toast.makeText(getApplicationContext(), "Unable to sync weather: network unavailable.", Toast.LENGTH_LONG).show();
            weatherSycing = false;
            fragmentsFinishWeatherSync();
    	}
    }

    /*
     * If a network connection is available, sync the weather. Else show an error.
     *
     * Weather is synced using a threaded AsyncTask. If net unavailable, toast an error.
     */
    public void syncSurf(View view){
        weatherSycing = true;
        //fragmentsStartWeatherSync();
        if(this.isOnline()){
            new SyncSurfTask().execute("http://tides.flyingsparx.net/fetch/surf");
        }
        else{
            Toast.makeText(getApplicationContext(), "Unable to sync surf: network unavailable.", Toast.LENGTH_LONG).show();
            //weatherSycing = false;
            //fragmentsFinishWeatherSync();
        }
    }

    /******
     *
     * UTILITY METHODS
     */

    /*
     * Check for network connection. If Internet connection return true. Else, return false.
     */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    /*
    * Update Fragments in ViewPager. Will only update the three Fragments currently in memory.
    *
    * These three methods execute different methods within the loaded pages.
     */
    private void fragmentsFinishWeatherSync(){
        ((DayInfo)fragments.get(currentFragmentIndex-1)).finishWeatherSync();
        ((DayInfo)fragments.get(currentFragmentIndex)).finishWeatherSync();
        ((DayInfo)fragments.get(currentFragmentIndex+1)).finishWeatherSync();
    }
    private void fragmentsStartWeatherSync(){
        ((DayInfo)fragments.get(currentFragmentIndex-1)).startWeatherSync();
        ((DayInfo)fragments.get(currentFragmentIndex)).startWeatherSync();
        ((DayInfo)fragments.get(currentFragmentIndex+1)).startWeatherSync();
    }
    private void fragmentsRefreshUI(){
        ((DayInfo)fragments.get(currentFragmentIndex-1)).refreshUI();
        ((DayInfo)fragments.get(currentFragmentIndex)).refreshUI();
        ((DayInfo)fragments.get(currentFragmentIndex+1)).refreshUI();
    }

    /******
     *
     * INITIALIZATION METHODS
     */

    /*
     * Get the layout components initialized and make their variable names global.
     */
    private void initComponents(){
        infoPager = (ViewPager)findViewById(R.id.infoPager);
    	dateText = (TextView)findViewById(R.id.dateText);
        buildProgress = (ProgressBar)findViewById(R.id.buildProgress);
        buildProgressHolder = (RelativeLayout)findViewById(R.id.buildProgressHolder);
    	dateText.setTextColor(Color.rgb(0, 150, 220));
    	revertButton = (ImageView)findViewById(R.id.revertButton);
    }

    /******
     *
     * THREADED CODE
     */

    /*
    * Threaded task to run startup routine (while progressbar shows on UI).
    *
    * Responsible for initialising view pager and other general tasks.
    * On finish, onPostExecute() is called.
     */
    private class StartupTasks extends AsyncTask<String, Integer, Boolean>{
        @Override
        protected Boolean doInBackground(String... arg0) {
            try{
                db = new DayDatabase(getApplicationContext());
                weather_db = new WeatherDatabase(getApplicationContext());
                firstDay = db.getFirstDay();
                lastDay = db.getLastDay();
            }
            catch(Exception e){
                System.err.println(e);
            }
            infoArray = new Calendar[DAYS_TO_StORE];
            currentDay = Calendar.getInstance();
            //currentDay = setDayForTesting("31/12/2016");

            populatePager(currentDay, DAYS_TO_StORE);

            infoPager.setOnPageChangeListener(new OnPageChangeListener() {
                public void onPageScrollStateChanged(int state) {}
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
                public void onPageSelected(int position) {
                    currentFragmentIndex = position;
                    DayInfo myNow = mPagerAdapter.getItem(position);
                    dateText.setText(myNow.today.toString());
                    if(position == todayFragmentIndex){
                        revertButton.setVisibility(View.INVISIBLE);
                    }
                    else{
                        revertButton.setVisibility(View.VISIBLE);
                    }
                }
            });

            return true;
        }
        protected void onPostExecute(Boolean result) {
            infoPager.setCurrentItem(todayFragmentIndex); // set initial pager position to current day
            /*
            * If preference is to sync weather on startup, then sync weather task now.
            * (this is done after initialising day fragments due to UI updates on the fragments during this task.)
             */
            if(prefs.getBoolean("weather_sync", true)){
                syncWeather(null);
            }
            syncSurf(null);
            ((RelativeLayout)findViewById(R.id.controls)).setVisibility(View.VISIBLE);
            buildProgressHolder.setVisibility(View.GONE);
            buildProgress.setVisibility(View.GONE);
            infoPager.setVisibility(View.VISIBLE);
            //new PagerFillerTask().execute(null);
        }
    }

    /*
     * AsyncTask to fetch new weather data.
     */
    private class SyncWeatherTask extends AsyncTask<String, Integer, Boolean>{
		@Override
		protected Boolean doInBackground(String... arg0) {
			BufferedReader reader = null;
			StringBuffer completeData = new StringBuffer();
			String androidVersion = android.os.Build.VERSION.RELEASE;
			String model = android.os.Build.MANUFACTURER+"-"+android.os.Build.MODEL.replace(" ", "-");

			arg0[0] = arg0[0]+"?dev="+model+"&ver="+androidVersion;
			try {
				URL url = new URL(arg0[0]);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				InputStream in = con.getInputStream();
			    reader = new BufferedReader(new InputStreamReader(in));
			    String line = "";
			    while ((line = reader.readLine()) != null) {
			    	completeData.append(line);
			    }
			    Boolean success = weather_db.insertWeatherData(completeData.toString());
			    if(!success){
			    	return false;
			    }
			}
			catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
				}
			}
			return true;
		}
        /*
        * On finish, update day fragments to show weather and flag the process as complete.
        *
        * If unsuccessful, for any reason, show an error.
         */
		protected void onPostExecute(Boolean result) {
            weatherSycing = false;
            fragmentsFinishWeatherSync();
	         if(!result){
	        	 Toast.makeText(getApplicationContext(), "Error syncing weather. Please try again later.", Toast.LENGTH_LONG).show();

	         }
	     }
    }

    /*
     * AsyncTask to fetch new weather data.
     */
    private class SyncSurfTask extends AsyncTask<String, Integer, Boolean>{
        @Override
        protected Boolean doInBackground(String... arg0) {
            BufferedReader reader = null;
            StringBuffer completeData = new StringBuffer();
            String androidVersion = android.os.Build.VERSION.RELEASE;
            String model = android.os.Build.MANUFACTURER+"-"+android.os.Build.MODEL.replace(" ", "-");

            arg0[0] = arg0[0]+"?dev="+model+"&ver="+androidVersion;
            try {
                URL url = new URL(arg0[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                InputStream in = con.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    completeData.append(line);
                }
                Boolean success = weather_db.insertSurfData(completeData.toString());
                if(!success){
                    return false;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
            return true;
        }
        /*
        * On finish, update day fragments to show surf and flag the process as complete.
        *
        * If unsuccessful, for any reason, show an error.
         */
        protected void onPostExecute(Boolean result) {
            //weatherSycing = false;
            //fragmentsFinishWeatherSync();
            if(!result){
                Toast.makeText(getApplicationContext(), "Error syncing surf. Please try again later.", Toast.LENGTH_LONG).show();

            }
        }
    }
}
