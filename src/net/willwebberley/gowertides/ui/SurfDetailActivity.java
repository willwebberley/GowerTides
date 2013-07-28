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

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.willwebberley.gowertides.R;
import net.willwebberley.gowertides.classes.Day;
import net.willwebberley.gowertides.classes.Surf;
import net.willwebberley.gowertides.utils.Utilities;

import java.util.ArrayList;


public class SurfDetailActivity extends Activity {

    private Day day;
    private String location;
    private ArrayList<Surf> reports;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surfdetail);

        day = (Day)getIntent().getSerializableExtra("day");
        location = getIntent().getStringExtra("location");
        reports = day.getSurfReports();

        try{
            updateUI();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }


    private void updateUI(){

        ((TextView)findViewById(R.id.date)).setText(day.toString());
        ((TextView)findViewById(R.id.title)).setText("Details for "+location);

        double x = getApplicationContext().getResources().getDisplayMetrics().density;

        LinearLayout list = (LinearLayout)findViewById(R.id.detail_list); // Get the linear layout to add the surf details to
        // Set some basic layout params (last arg is weight - set to 0.2)
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        // Calculate the pixel density (in dpi)...

        // ... and use this to set the horizontal margins of the views to be added to the LinearLayout (i.e. 5dpi left and right)
        param.setMargins((int)(5*x), 0, (int)(5*x), 0);

        // Finally remove all views in there already, before repopulating with the layoutparams specified above.
        list.removeAllViews();

        for(int i = 0; i < reports.size(); i++){
            SurfDetailFragment frag = new SurfDetailFragment(getApplicationContext(), reports.get(i), day);
            list.addView(frag.getView(), param);
        }
    }

    /*
   * Listen for clicks on MSW logo, and open up their site if clicked.
    */
    public void openMSW(View view){
        String url = "http://www.magicseaweed.com";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

}
