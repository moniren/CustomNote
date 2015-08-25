package com.lyk.immersivenote.utils;

import android.content.Context;

import com.lyk.immersivenote.R;
import com.rey.material.drawable.ToolbarRippleDrawable;

/**
 * Created by John on 2015/8/25.
 */
public class RippleBgUti {

    public static ToolbarRippleDrawable getFlatColorRippleBackground(Context context) {
        ToolbarRippleDrawable.Builder mBuilder = new ToolbarRippleDrawable.Builder(context, R.style.FlatColorButtonRippleStyle);
        return mBuilder.build();
    }

    public static ToolbarRippleDrawable getFlatRippleBackground(Context context){
        ToolbarRippleDrawable.Builder mBuilder = new ToolbarRippleDrawable.Builder(context,R.style.FlatButtonRippleStyle);
        return  mBuilder.build();
    }
}
