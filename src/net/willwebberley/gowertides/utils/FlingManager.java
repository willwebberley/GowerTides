package net.willwebberley.gowertides.utils;

import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class FlingManager implements OnTouchListener {
	private final GestureDetector gdt = new GestureDetector(new GestureManager());

  public boolean onTouch(final View v, final MotionEvent event) {
     return gdt.onTouchEvent(event);
     
  }

  private final class GestureManager extends SimpleOnGestureListener {

     private static final int SWIPE_MIN_DISTANCE = 100;
     private static final int SWIPE_THRESHOLD_VELOCITY = 200;

     @Override
     public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
           onRightToLeft();
           return true;
        } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
           onLeftToRight();
           return true;
        }
        return false;
     }
  }

  public abstract void onRightToLeft();

  public abstract void onLeftToRight();

}