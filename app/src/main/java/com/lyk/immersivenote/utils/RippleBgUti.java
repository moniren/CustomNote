package com.lyk.immersivenote.utils;

import android.content.Context;
import android.view.View;

import com.lyk.immersivenote.R;
import com.rey.material.drawable.ToolbarRippleDrawable;
import com.rey.material.util.ViewUtil;

/**
 * Created by John on 2015/8/25.
 */
public class RippleBgUti {

    public static void setFlatColorRippleBackground(View view, Context context){
        ToolbarRippleDrawable.Builder mBuilder = new ToolbarRippleDrawable.Builder(context, R.style.FlatColorButtonRippleStyle);
        ViewUtil.setBackground(view, mBuilder.build());
    }

    public static void setFlatRippleBackground(View view, Context context){
        ToolbarRippleDrawable.Builder mBuilder = new ToolbarRippleDrawable.Builder(context, R.style.FlatButtonRippleStyle);
        ViewUtil.setBackground(view, mBuilder.build());
    }
}
