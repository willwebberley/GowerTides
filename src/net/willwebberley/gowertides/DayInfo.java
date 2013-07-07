package net.willwebberley.gowertides;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.widget.*;
import com.androidplot.xy.XYPlot;

import net.willwebberley.gowertides.classes.Day;
import net.willwebberley.gowertides.classes.TideGraph;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;


/*
* class (Fragment) to represent a day.
*
* Each day holds various tidal, weather, sunset/sunrise data and this fragment is responsible for displaying this
* data and updating it where necessary.
 */
@SuppressLint("ValidFragment")
public class DayInfo extends Fragment {
	
	private Dayview dayView;
	
	private SharedPreferences prefs;
	private Handler updaterHandler;
	
	public Day today;
	private Calendar rightNow;
	private TideGraph tideGraph;
	
	private TextView tideTypeField;
	private TextView tideTimeField;
	private TextView tideTimeLeftField;
	private TextView sunriseText;
	private TextView sunsetText;
	private TextView sunsetCountField;
	private TextView weatherDescriptionView;
	private ProgressBar weatherProgress;
	private ImageButton weatherSync;
    private ProgressBar surfProgress;
    private ImageButton surfSync;
	
	private View layoutView;
    private ViewGroup c;

    /*
    * Called when the fragment is loaded into the viewpager's memory.
    *
    * Method responsible for loading the fragment's UI from layout XML and updating the UI components.
    *
    * If weather sync is currently ongoing, then show progressbar and hide sync button.
    * Else, hide progressbar and show sync button.
     */
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
        layoutView =  inflater.inflate(R.layout.fragment_day_info, container, false);
        c = container;
        updaterHandler = new Handler();
        initComponents();
        showPreferredComponents();
    	updateUI();
    	updater();

        if(dayView.weatherSycing){
            startWeatherSync();
        }
        else{
            finishWeatherSync();
        }

        if(dayView.surfSyncing){
            startSurfSync();
        }
        else{
            finishSurfSync();
        }

        return layoutView;
    }

    /*
    * Initialize the fragment with the Day it is to represent, the application preferences and the parent activity.
     */
	public DayInfo(Day day, SharedPreferences p, Dayview d){
		today = day;
		prefs = p;
		dayView = d;    	
	}

    /*
    * Empty constructor also required for a valid fragment.
     */
	public DayInfo(){
		
	}
	
	/******
     * 
     * UI METHODS
     */

    /*
    * Called from parent activity when weather is to start syncing.
    * Responsible for hiding the sync button and showing the progress bar.
     */
    public void startWeatherSync(){
        try{
            weatherSync.setVisibility(View.INVISIBLE);
            weatherProgress.setVisibility(View.VISIBLE);
        }
        catch(Exception e){
            System.err.println("error starting sync on fragment: "+e);
        }
    }

    /*
    * Called from parent activity when weather sync is completed.
    * Hides progressbar and shows sync button.
    * Updates the UI.
     */
    public void finishWeatherSync(){
        try{
            weatherSync.setVisibility(View.VISIBLE);
            weatherProgress.setVisibility(View.INVISIBLE);
            updateUI();
            showPreferredComponents();
        }
        catch(Exception e){
            System.err.println("error finishing sync on fragment: "+e);
        }
    }

    /*
   * Called from parent activity when weather is to start syncing.
   * Responsible for hiding the sync button and showing the progress bar.
    */
    public void startSurfSync(){
        try{
            surfSync.setVisibility(View.INVISIBLE);
            surfProgress.setVisibility(View.VISIBLE);
        }
        catch(Exception e){
            System.err.println("error starting sync on fragment: "+e);
        }
    }

    /*
    * Called from parent activity when weather sync is completed.
    * Hides progressbar and shows sync button.
    * Updates the UI.
     */
    public void finishSurfSync(){
        try{
            surfSync.setVisibility(View.VISIBLE);
            surfProgress.setVisibility(View.INVISIBLE);
            updateUI();
            showPreferredComponents();
        }
        catch(Exception e){
            System.err.println("error finishing sync on fragment: "+e);
        }
    }

    /*
    * Called from parent activity when it requires the fragment to refresh its UI.
    * (Typically onResume() calls it to make it reload the components which are set to show in preferences.)
     */
    public void refreshUI(){
        showPreferredComponents();
        updateUI();
    }
    
    /*
     * Internal method that checks preferences and hides or shows components based on what the
     * user wants.
     */
    private void showPreferredComponents(){
    	if(prefs.getBoolean("show_graph", true)){layoutView.findViewById(R.id.tideGraphComponent).setVisibility(View.VISIBLE);}
    	else{layoutView.findViewById(R.id.tideGraphComponent).setVisibility(View.GONE);}
    	
    	if(prefs.getBoolean("show_table", true)){layoutView.findViewById(R.id.tideTable).setVisibility(View.VISIBLE);}
    	else{layoutView.findViewById(R.id.tideTable).setVisibility(View.GONE);}
    	
    	if(prefs.getBoolean("show_sunrise_sunset", true)){layoutView.findViewById(R.id.sunrise_sunset).setVisibility(View.VISIBLE);}
    	else{layoutView.findViewById(R.id.sunrise_sunset).setVisibility(View.GONE);}
    	
    	if(prefs.getBoolean("show_sunset_timer", true)){layoutView.findViewById(R.id.sunsetCountField).setVisibility(View.VISIBLE);}
    	else{layoutView.findViewById(R.id.sunsetCountField).setVisibility(View.GONE);}
    	   	
    	if(prefs.getBoolean("show_weather", true)){layoutView.findViewById(R.id.weatherHolder).setVisibility(View.VISIBLE);}
    	else{layoutView.findViewById(R.id.weatherHolder).setVisibility(View.GONE);}
    }
   
    /*
     * Main UI updater. Sets the textfields, tide graph, etc to the selected day.
     */
    public void updateUI(){
        today.getDayInfo();
    	rightNow = Calendar.getInstance();

       	// Put in try-catch as getting the strings returned null pointers on some devices
    	try{
    		sunriseText.setText(today.getSunriseString());
    		sunsetText.setText(today.getSunsetString());
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	try{
    		tideGraph.setDay(today);
        	
        	// Draw the tide table with correct values
        	setTideTableInfo();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	try{
	    	// Check if selected day is today. If so, show further information
	    	if(today.isToday()){
	    		setSunsetTime();
	    		setTimeToTide();
	    	}
	    	else{
	    		sunsetCountField.setText("");
	    	}
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	// Check if there is weather data for selected day. If yes, set the weather info view to visible
    	// and remove error message and fill text fields with correct data. Else hide weather info
    	// and show error message.
    	if(today.isWeatherAvailable()){
    		layoutView.findViewById(R.id.weather).setVisibility(View.VISIBLE);
    		((TextView)layoutView.findViewById(R.id.weather_error)).setVisibility(View.GONE);
    		setWeatherInfo();
    	}
    	else{
    		layoutView.findViewById(R.id.weather).setVisibility(View.GONE);
    		((TextView)layoutView.findViewById(R.id.weather_description)).setText("Weather unavailable");
    		((TextView)layoutView.findViewById(R.id.weather_error)).setVisibility(View.VISIBLE);
    	}
        if(today.isSurfAvailable()){
            layoutView.findViewById(R.id.surf).setVisibility(View.VISIBLE);
            ((TextView)layoutView.findViewById(R.id.surf_error)).setVisibility(View.GONE);
            setSurfInfo();
        }
        else{
            layoutView.findViewById(R.id.surf).setVisibility(View.GONE);
            ((TextView)layoutView.findViewById(R.id.surf_error)).setVisibility(View.VISIBLE);
        }
    }
    
    /*
     * Set the weather fields and images for the current day.
     */
    private void setWeatherInfo(){
    	String weather_description = today.getWeatherDescription();
    	String unitType = prefs.getString("unitFormat", "true");
    	Boolean metric = false;
    	if(unitType.equals("true")){
    		metric = true;
    	}
    	
    	int max_temp = today.getMaxTemp(metric);
    	int min_temp = today.getMinTemp(metric);
    	int wind_speed = today.getWindSpeed(metric);
    	Double prep = today.getPrecipitation();
    	String direction = today.getWindDirection();
    	Spanned temp=null,wind=null;
    	if(metric){
    		temp = Html.fromHtml("<b>"+min_temp+"&deg;C - "+max_temp+"&deg;C</b>");
        	wind = Html.fromHtml("<b>"+wind_speed+"km/h</b> from <b>"+direction+"</b>");
        	
    	}
    	else{
    		temp = Html.fromHtml("<b>"+min_temp+"&deg;F - "+max_temp+"&deg;F</b>");
        	wind = Html.fromHtml("<b>"+wind_speed+"mph</b> from <b>"+direction+"</b>");
    	}
    	Spanned precipitation = Html.fromHtml("<b>"+prep+"mm</b>");
    	
		((TextView)layoutView.findViewById(R.id.weather_description)).setText(weather_description);
		((TextView)layoutView.findViewById(R.id.weatherTemp)).setText(temp);
        ((TextView)layoutView.findViewById(R.id.weatherTemp)).setTextColor(Color.rgb(100, 100, 100));
		((TextView)layoutView.findViewById(R.id.weatherWind)).setText(wind);
        ((TextView)layoutView.findViewById(R.id.weatherWind)).setTextColor(Color.rgb(100, 100, 100));
		((TextView)layoutView.findViewById(R.id.weatherPrecipitation)).setText(precipitation);
        ((TextView)layoutView.findViewById(R.id.weatherPrecipitation)).setTextColor(Color.rgb(100, 100, 100));
		String icon = today.getWeatherIcon();
		try {
			 InputStream ims = getActivity().getAssets().open("icons/"+icon);
			 Drawable d = Drawable.createFromStream(ims, null);
			 ((ImageView)layoutView.findViewById(R.id.weatherIcon)).setImageDrawable(d);
			 InputStream ims2 = getActivity().getAssets().open("icons/arrow.png");
			 Drawable d2 = Drawable.createFromStream(ims2, null);
			 ((ImageView)layoutView.findViewById(R.id.weatherWindIcon)).setImageDrawable(d2);
			 
			 RotateAnimation rAnim = new RotateAnimation(0, today.getWindDegree(), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			 rAnim.setDuration(500);
			 rAnim.setFillEnabled(true);
			 rAnim.setFillAfter(true);
			 ((ImageView)layoutView.findViewById(R.id.weatherWindIcon)).startAnimation(rAnim);
		}
		catch(Exception e) {System.err.println(e);}
    }

    /*
     * Set the surf fields and images for the current day.
     */
    private void setSurfInfo(){
        LinearLayout surf = (LinearLayout)layoutView.findViewById(R.id.surf); // Get the linear layout to add the surf details to
        // Set some basic layout params (last arg is weight - set to 0.2)
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT, 0.2f);
        // Calculate the pixel density (in dpi)...
        double x = dayView.getApplicationContext().getResources().getDisplayMetrics().density;
        // ... and use this to set the horizontal margins of the views to be added to the LinearLayout
        param.setMargins((int)(5*x), 0, (int)(5*x), 0);

        // Finally remove all views in there already, before repopulating with the layoutparams specified above.
        surf.removeAllViews();
        SurfInfo surfTime1 = new SurfInfo(dayView.getApplicationContext(), today, 9);
        SurfInfo surfTime2 = new SurfInfo(dayView.getApplicationContext(),today, 12);
        SurfInfo surfTime3 = new SurfInfo(dayView.getApplicationContext(),today, 15);
        surf.addView(surfTime1.getView(), param);
        surf.addView(surfTime2.getView(), param);
        surf.addView(surfTime3.getView(), param);

    }


    /*
     * Draws the tide table (3 columns: type(high/low),time,timeToGo)
     * This method responsible for first two columns (since final one depends on current time).
     */
    private void setTideTableInfo(){
    	tideTypeField.setText("");
    	tideTimeField.setText("");
    	tideTimeLeftField.setText("");
    	tideTypeField.append((Html.fromHtml("<b>TIDE</b><br />")));
    	tideTimeField.append((Html.fromHtml("<b>TIME</b><br />")));
    	
    	String[] types = today.getTideTypes();
    	Calendar[] times = today.getTideTimes();
    	
    	for(int i = 0; i < types.length; i++){
    		tideTypeField.append(types[i]);
    		tideTimeField.append((new SimpleDateFormat("HH:mm")).format(times[i].getTime()));
    		if(i < types.length-1){
    			tideTypeField.append("\n");
    			tideTimeField.append("\n");
    		}
      	}
    }
    
    /*
     * Updates tide table's final column with the time left until the next high/low tide.
     */
    private void setTimeToTide(){
    	tideTimeLeftField.setText("");
    	tideTimeLeftField.append((Html.fromHtml("<b>TO GO</b><br />")));
    	
    	Calendar[] times = today.getTideTimes();
    	for(int i = 0; i < times.length; i++){
	    	int hoursDiff = times[i].get(Calendar.HOUR_OF_DAY) - rightNow.get(Calendar.HOUR_OF_DAY);
		    int minsDiff = times[i].get(Calendar.MINUTE) - rightNow.get(Calendar.MINUTE);
		    if(minsDiff < 0){
		    	hoursDiff--;
		    	minsDiff = 60+minsDiff;
		    }
		    if(hoursDiff < 0){
		    	tideTimeLeftField.append("--:--");
		    }
		    if(hoursDiff >= 0){
		    	if(minsDiff < 10){tideTimeLeftField.append((Html.fromHtml("<b>"+hoursDiff+":0"+minsDiff+"</b>")));}
	    	    if(minsDiff >=10){tideTimeLeftField.append((Html.fromHtml("<b>"+hoursDiff+":"+minsDiff+"</b>")));}
		    }
		    if(i < times.length-1){
		    	tideTimeLeftField.append((Html.fromHtml("<br />")));
    		}
    	}
    }
    
    /*
     * Set data for the sunset timer (again, depends on whether selected day is 'today')
     */
    private void setSunsetTime(){
    	Calendar sunsetTime = today.getSunset();
	    int hoursDiff = sunsetTime.get(Calendar.HOUR_OF_DAY) - rightNow.get(Calendar.HOUR_OF_DAY);
	    int minsDiff = sunsetTime.get(Calendar.MINUTE) - rightNow.get(Calendar.MINUTE);
	    if(minsDiff < 0){
	    	hoursDiff--;
	    	minsDiff = 60+minsDiff;
	    }
	    if(hoursDiff < 0){
	    	sunsetCountField.setText("sun has set");
	    }
	    if(hoursDiff >= 0){
	    	if(minsDiff < 10){sunsetCountField.setText(hoursDiff+":0"+minsDiff+" 'til sunset");}
    	    if(minsDiff >=10){sunsetCountField.setText(hoursDiff+":"+minsDiff+" 'til sunset");}
	    }
    }
    
    /******
     * 
     * INITIALIZATION METHODS
     */
    
    /*
     * Get the layout components initialized and make their variable names global.
     */
    private void initComponents(){
    	
    	tideGraph = new TideGraph((XYPlot)layoutView.findViewById(R.id.tideGraphComponent), dayView.getApplicationContext());

    	tideTypeField = (TextView)layoutView.findViewById(R.id.tideTypes);
    	tideTimeField = (TextView)layoutView.findViewById(R.id.tideTimes);
    	tideTimeLeftField = (TextView)layoutView.findViewById(R.id.tideTimesLeft);
    	tideTimeLeftField.setTextColor(Color.rgb(0, 100, 0));
    	
    	sunriseText = (TextView)layoutView.findViewById(R.id.sunriseText);
    	sunriseText.setTextColor(Color.rgb(0, 100, 0));
    	sunsetText = (TextView)layoutView.findViewById(R.id.sunsetText);
    	sunsetText.setTextColor(Color.rgb(100, 0, 0));
    	sunsetCountField = (TextView)layoutView.findViewById(R.id.sunsetCountField);
    	sunsetCountField.setTextColor(Color.rgb(100, 25, 25));

        surfProgress = (ProgressBar)layoutView.findViewById(R.id.surfProgress);
        surfSync = (ImageButton)layoutView.findViewById(R.id.surfSync);
    	weatherProgress = (ProgressBar)layoutView.findViewById(R.id.weatherProgress);
    	weatherSync = (ImageButton)layoutView.findViewById(R.id.weatherSync);
    	weatherDescriptionView = (TextView)layoutView.findViewById(R.id.weather_description);
    	weatherDescriptionView.setTextColor(Color.rgb(0, 150, 220));

        ((TextView)layoutView.findViewById(R.id.surf_title)).setTextColor(Color.rgb(0, 150, 220));

    }
    
    // Method to update the interface automatically every 60 seconds,
    // so that timers, etc., are up-to-date.
    public void updater() {
        Runnable runnable = new Runnable() {
          public void run() {
            for (;;) {
              try {
                Thread.sleep(60000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              updaterHandler.post(new Runnable() {
                public void run() {
                    if(!dayView.isPaused){
                        updateUI();
                    }
                }
              });
            }
          }
        };
        new Thread(runnable).start();
      }
}