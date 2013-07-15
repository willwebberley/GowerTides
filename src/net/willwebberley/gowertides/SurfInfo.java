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
import net.willwebberley.gowertides.classes.Surf;

/*
 Class to represent the surf information views in the horizontal scroll bar
 */
public class SurfInfo extends RelativeLayout {

    private View layoutView;
    private Surf surf;

    public SurfInfo(Context context, Surf s){
        super(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutView = inflater.inflate(R.layout.surf_forecast, null);

        surf = s;
        updateUI();
    }

    public View getView(){
        return layoutView;
    }

    private void updateUI(){
        // Format the title textviews
        ((TextView)layoutView.findViewById(R.id.timeField)).setTextColor(Color.rgb(0, 150, 220));
        ((TextView)layoutView.findViewById(R.id.timeField)).setText(surf.hour+":00");

        // Update fields with information

        // Max and min surf heights
        if(surf.max_surf-surf.min_surf == 0){((TextView)layoutView.findViewById(R.id.surfSize)).setText(Html.fromHtml("<b>" + surf.max_surf + "</b> <i>ft</i>"));}
        else{((TextView)layoutView.findViewById(R.id.surfSize)).setText(Html.fromHtml("<b>"+surf.min_surf+"-"+surf.max_surf+"</b> <i>ft</i>"));}
        ((TextView)layoutView.findViewById(R.id.surfSize)).setTextColor(Color.rgb(70, 80, 70));

        // Swell direction
        ((TextView)layoutView.findViewById(R.id.swellDirection)).setText(Html.fromHtml("<b>"+surf.swell_direction+"</b>"));
        RotateAnimation rAnim = new RotateAnimation(0, (float)surf.swell_angle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rAnim.setDuration(200);
        rAnim.setFillEnabled(true);
        rAnim.setFillAfter(true);
        ((ImageView)layoutView.findViewById(R.id.swellDirectionIcon)).startAnimation(rAnim);

        // Swell period
        ((TextView)layoutView.findViewById(R.id.swellPeriod)).setText(Html.fromHtml("<b>"+surf.swell_period+"</b> <i>s</i>"));

        // Swell height
        ((TextView)layoutView.findViewById(R.id.swellHeight)).setText(Html.fromHtml("<b>"+surf.swell_height+"</b> <i>ft</i>"));
    }
}
