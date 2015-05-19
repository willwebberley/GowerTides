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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import net.willwebberley.gowertides.R;

public class LocationDialog extends DialogFragment {

    View layoutView;
    String[] names;
    int[] keys;

    public LocationDialog(){
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        names = getArguments().getStringArray("names");
        keys = getArguments().getIntArray("keys");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        layoutView = inflater.inflate(R.layout.location_dialog, null);
        builder.setView(layoutView);
        builder.setMessage("Choose a surf report location")
                .setItems(R.array.locationDisplay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                    }

                });
        initList();
        // Create the AlertDialog object and return it
        return builder.create();

    }
    private void selected(int index){
        dismiss();
        DaysActivity dv = (DaysActivity) getActivity();
        dv.updateLocation(index);
    }


   private void initList(){
       ListView mListView = (ListView)layoutView.findViewById(R.id.location_list);
       ArrayAdapter<Object> ad = new ArrayAdapter<Object>(getActivity(),android.R.layout.simple_list_item_1);
       mListView.setAdapter(ad);

       mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               selected(i);
           }
       });

       ad.clear();
       for(int i = 0; i < names.length; i++){
           ad.add(i+1+": "+names[i]);
       }

       mListView.setAdapter(ad);
   }
}
