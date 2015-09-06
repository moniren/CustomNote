package com.lyk.immersivenote.main;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;


/**
 * Created by John on 2015/9/4.
 */
public class CircleAbbrev extends LinearLayout {
    private String bgColor;
    public CircleAbbrev (Context context,AttributeSet attrs){
        super(context,attrs);
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }
}
