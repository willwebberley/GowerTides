package net.willwebberley.gowertides;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.willwebberley.gowertides.classes.Day;


public class SurfInfo extends RelativeLayout {

    private View layoutView;
    private Day day;
    private int time;

    public SurfInfo(Context context, Day d, int t) {
        super(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutView = inflater.inflate(R.layout.surf_forecast, null);
        day = d;
        time = t;
        updateUI();
    }

    public View getView(){
        return layoutView;
    }

    private void updateUI(){
        ((TextView)layoutView.findViewById(R.id.timeField)).setTextColor(Color.rgb(0, 150, 220));
        ((TextView)layoutView.findViewById(R.id.timeField)).setText(time+":00");
        int min_surf = (int)day.getMinSurfForTime(time);
        int max_surf = (int)day.getMaxSurfForTime(time);
        if(max_surf-min_surf == 0){((TextView)layoutView.findViewById(R.id.surfSize)).setText(Html.fromHtml("<b>" + max_surf + "</b> <i>ft</i>"));}
        else{((TextView)layoutView.findViewById(R.id.surfSize)).setText(Html.fromHtml("<b>"+min_surf+"-"+max_surf+"</b> <i>ft</i>"));}
    }

}
