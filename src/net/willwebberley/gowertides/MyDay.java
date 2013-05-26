package net.willwebberley.gowertides;

import java.util.Calendar;

import net.willwebberley.gowertides.classes.Day;
import net.willwebberley.gowertides.classes.TideGraph;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MyDay extends View {

	private Dayview dayView;
	
	private SharedPreferences prefs;
	private Handler updaterHandler;
	
	private Day today;
	private Calendar rightNow;
	private TideGraph tideGraph;
	
	private TextView dateText;
	private TextView tideTypeField;
	private TextView tideTimeField;
	private TextView tideTimeLeftField;
	private TextView sunriseText;
	private TextView sunsetText;
	private TextView sunsetCountField;
	private TextView weatherDescriptionView;
	private ImageView revertButton;
	private ProgressBar weatherProgress;
	private ImageButton weatherSync;

	public MyDay(Context context) {
		super(context);
	}

	

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

	}

}
	