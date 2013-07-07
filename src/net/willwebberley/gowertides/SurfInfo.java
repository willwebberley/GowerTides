package net.willwebberley.gowertides;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
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
        // Format the title textviews
        ((TextView)layoutView.findViewById(R.id.timeField)).setTextColor(Color.rgb(0, 150, 220));
        ((TextView)layoutView.findViewById(R.id.timeField)).setText(time+":00");

        // Get the relevant data from the Day instance
        int min_surf = (int)day.getMinSurfForTime(time);
        int max_surf = (int)day.getMaxSurfForTime(time);
        String swell_dir = day.getSwellDirection(time);
        float swell_angle = (float)day.getSwellAngle(time)+180;
        int swell_period = (int)day.getSwellPeriod(time);
        double swell_height = day.getSwellHeight(time);

        // Update fields with information

        // Max and min surf heights
        if(max_surf-min_surf == 0){((TextView)layoutView.findViewById(R.id.surfSize)).setText(Html.fromHtml("<b>" + max_surf + "</b> <i>ft</i>"));}
        else{((TextView)layoutView.findViewById(R.id.surfSize)).setText(Html.fromHtml("<b>"+min_surf+"-"+max_surf+"</b> <i>ft</i>"));}
        ((TextView)layoutView.findViewById(R.id.surfSize)).setTextColor(Color.rgb(70, 80, 70));

        // Swell direction
        ((TextView)layoutView.findViewById(R.id.swellDirection)).setText(Html.fromHtml("<b>"+swell_dir+"</b>"));
        RotateAnimation rAnim = new RotateAnimation(0, swell_angle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rAnim.setDuration(200);
        rAnim.setFillEnabled(true);
        rAnim.setFillAfter(true);
        ((ImageView)layoutView.findViewById(R.id.swellDirectionIcon)).startAnimation(rAnim);

        // Swell period
        ((TextView)layoutView.findViewById(R.id.swellPeriod)).setText(Html.fromHtml("<b>"+swell_period+"</b> <i>s</i>"));

        // Swell height
        ((TextView)layoutView.findViewById(R.id.swellHeight)).setText(Html.fromHtml("<b>"+swell_height+"</b> <i>ft</i>"));
    }

}
