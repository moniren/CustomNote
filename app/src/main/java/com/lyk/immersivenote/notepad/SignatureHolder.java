package com.lyk.immersivenote.notepad;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by John on 2015/8/16.
 */
public class SignatureHolder extends FrameLayout {

    public static final int INVALID_STROKE = 888;
    public SignatureHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        onTouchEvent(ev);
        return false;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("SigHolder", "onTouch");
        performClick();
//        if(event.getAction() == SignatureCapture.INVALID_STROKE){
//            MotionEvent motionEvent = MotionEvent.obtain(
//                    100,
//                    200,
//                    INVALID_STROKE,
//                    event.getX(),
//                    event.getY(),
//                    0
//            );
//            // Dispatch touch event to view
//            this.dispatchTouchEvent(motionEvent);
//        }
        return false;
    }
}
