package net.willwebberley.gowertides.utils;

import net.willwebberley.gowertides.R;
import net.willwebberley.gowertides.Dayview;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;

public class AnimationManager {

	public static final int SLIDE_UP_LEFT = 1;
	public static final int SLIDE_UP_RIGHT = 2;
	public static final int SWAP_OUT_LEFT = 4;
	public static final int SWAP_OUT_RIGHT = 5;
	public static final int SLIDE_OUT_LEFT = 6;
	public final static int SLIDE_OUT_RIGHT = 7;
	public final static int SLIDE_IN_LEFT = 8;
	public static final int SLIDE_IN_RIGHT = 9;
	public static final int INITIAL_LOAD = 10;
	public static final int RESET = 11;
	public static final int FADE_OUT = 12;
	public static final int FADE_IN = 13;
	
	
	public static void animate(View view, int type, Dayview dv){
		if(type == SWAP_OUT_LEFT){swapScreen(view, dv, SLIDE_OUT_LEFT, SLIDE_IN_RIGHT);}
		if(type == SWAP_OUT_RIGHT){swapScreen(view, dv, SLIDE_OUT_RIGHT, SLIDE_IN_LEFT);}
		if(type == RESET){swapScreen(view, dv, FADE_OUT, FADE_IN);}
		if(type == INITIAL_LOAD){initialLoad(view, dv);}
	}
	
	public static void initialLoad(View view, Dayview dayview){
		Animation leftUp = AnimationUtils.loadAnimation(dayview, R.animator.slide_up_left);
		Animation rightUp = AnimationUtils.loadAnimation(dayview, R.animator.slide_up_right);
    		//LayoutAnimationController controller =new LayoutAnimationController(leftUp);
    		LinearLayout table = (LinearLayout)view;
    		for (int i = 0; i < table.getChildCount(); i++)
    		{
    			View row = (View) table.getChildAt(i);
    			if(row.getVisibility() == View.VISIBLE){
    				if(i%2 == 0){
    					row.startAnimation(leftUp);
    				}
    				else{
    					row.startAnimation(leftUp);
    				}
    			}
    		}
	}
	
	private static void swapScreen(View view, Dayview dayview, int first, int last){
		Animation anim1 = null, anim2 = null;
		if (first == SLIDE_OUT_LEFT){anim1 = AnimationUtils.loadAnimation(dayview, R.animator.slide_out_left);}
		if (first == SLIDE_OUT_RIGHT){anim1 = AnimationUtils.loadAnimation(dayview, R.animator.slide_out_right);}
		if (first == FADE_OUT){anim1 = AnimationUtils.loadAnimation(dayview, R.animator.fade_out);}
		if (last == SLIDE_IN_LEFT){anim2 = AnimationUtils.loadAnimation(dayview, R.animator.slide_in_left);}
		if (last == SLIDE_IN_RIGHT){anim2 = AnimationUtils.loadAnimation(dayview, R.animator.slide_in_right);}
		if (last == FADE_IN){anim2 = AnimationUtils.loadAnimation(dayview, R.animator.fade_in);}
		anim1.setAnimationListener(new Sequencer(dayview, view, anim2));
		view.startAnimation(anim1);
	}
	
	private static void slideInRight(View view, Dayview dayview){
		Animation anim = AnimationUtils.loadAnimation(dayview, R.animator.slide_in_right);
		view.startAnimation(anim);
	}
	
	private static void slideInLeft(View view, Dayview dayview){
		Animation anim = AnimationUtils.loadAnimation(dayview, R.animator.slide_in_left);
		view.startAnimation(anim);
	}
	
	private static class Sequencer implements Animation.AnimationListener{
		Dayview dv;
		View v;
		Animation a;
		public Sequencer(Dayview dayview, View view, Animation next){
			dv=dayview;v=view;a = next;
			//dv.readyToChange = false;
		}
		public void onAnimationEnd(Animation animation) {
			//dv.updateUI();
			v.startAnimation(a);
			//dv.readyToChange = true;
		}
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
		}
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
		}
		
	}
}
