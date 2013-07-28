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
import android.os.Bundle;
import android.widget.TextView;
import net.willwebberley.gowertides.R;
import net.willwebberley.gowertides.classes.Day;


public class SurfDetailActivity extends Activity {

    private Day day;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surfdetail);

        day = (Day)getIntent().getSerializableExtra("day");

        System.out.println(day);
        try{
            updateUI();
        }
        catch(Exception e){
            System.err.println(e);
        }
    }


    private void updateUI(){
        ((TextView)findViewById(R.id.time)).setText(day.toString());
    }

}
