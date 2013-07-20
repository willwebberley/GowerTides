package net.willwebberley.gowertides.ui;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.willwebberley.gowertides.R;
import net.willwebberley.gowertides.classes.Tide;

/*
 Class to represent the tidal information views in the horizontal scroll bar
 */
public class TideFragment extends RelativeLayout {

    private View layoutView;
    private Tide tide;

    public TideFragment(Context context, Tide t){
        super(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutView = inflater.inflate(R.layout.tide_table, null);

        tide = t;
        updateUI();
    }

    public View getView(){
        return layoutView;
    }

    private void updateUI(){
        ((TextView)layoutView.findViewById(R.id.time)).setTextColor(Color.rgb(0, 150, 220));
        ((TextView)layoutView.findViewById(R.id.time)).setText(tide.getTimeString());


        if(tide.getType() == tide.LOW){
            ((ImageView)layoutView.findViewById(R.id.tide_icon)).setImageResource(R.drawable.low);
        }
        if(tide.getType() == tide.HIGH){
            ((ImageView)layoutView.findViewById(R.id.tide_icon)).setImageResource(R.drawable.high);
        }
    }
}
