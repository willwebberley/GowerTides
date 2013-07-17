package net.willwebberley.gowertides.ui;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import net.willwebberley.gowertides.R;

public class AboutActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_about, menu);
        return true;
    }
    
    public void goToHelp(View view){
    	String url = "http://www.flyingsparx.net/contact";
    	Intent i = new Intent(Intent.ACTION_VIEW);
    	i.setData(Uri.parse(url));
    	startActivity(i);
    }
    
    public void openTwitter(View view){
    	String url = "http://www.twitter.com/flyingSparx";
    	Intent i = new Intent(Intent.ACTION_VIEW);
    	i.setData(Uri.parse(url));
    	startActivity(i);
    }
    
    public void goToWeather(View view){
    	String url = "http://www.worldweatheronline.com";
    	Intent i = new Intent(Intent.ACTION_VIEW);
    	i.setData(Uri.parse(url));
    	startActivity(i);
    }

    public void goToMSW(View view){
        String url = "http://www.magicseaweed.com";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void goToWeatherIcon(View view){
        String url = "http://digitalchet.deviantart.com/art/Novacons-Weather-Icons-13133337";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void goToIcon(View view){
        String url = "http://adamwhitcroft.com/climacons";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void goToBatch(View view){
        String url = "http://adamwhitcroft.com/batch";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
