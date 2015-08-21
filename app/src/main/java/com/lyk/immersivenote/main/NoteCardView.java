package com.lyk.immersivenote.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;

import com.lyk.immersivenote.R;

/**
 * Created by John on 2015/8/19.
 */
public class NoteCardView extends CardView {

    private android.view.ViewGroup.LayoutParams params;
    private int id;
    private String title;
    private String time;
    private Bitmap bg;


    public NoteCardView(Context context, int id, int title, int bgNumber) {
        super(context);
        this.id = id;
//        this.title = title;
        params = new android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(params);
//        this.setBackgroundResource(R.drawable.bg_single_line_even);
    }
}
