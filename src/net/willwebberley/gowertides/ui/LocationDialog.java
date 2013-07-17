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

    ListView lv;
    View layoutView;
    String[] names;
    int[] keys;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
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

    public LocationDialog(String[] n, int[] k){
        super();
        names = n;
        keys = k;
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
