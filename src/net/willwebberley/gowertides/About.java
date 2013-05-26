package net.willwebberley.gowertides;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class About extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView aboutText = (TextView)findViewById(R.id.aboutText);
        aboutText.setText("Gower Tides \n\n");
        aboutText.append("Author: Will Webberley (@flyingSparx)\n");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_about, menu);
        return true;
    }
    
    public void goToHelp(View view){
    	String url = "http://www.willwebberley.net/contact";
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
}
