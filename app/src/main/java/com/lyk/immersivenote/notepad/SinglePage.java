package com.lyk.immersivenote.notepad;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.lyk.immersivenote.R;
import com.lyk.immersivenote.settings.PrefManager;

/**
 * Created by John on 2015/7/22.
 */
public class SinglePage extends LinearLayout{

    private int pageNumber;
    private boolean pageFull = false;
    private int lineHeight = 0;
    private int lineWidth = 0;
    public static int NUM_LINES = 15;

    public SinglePage(Context context){
        super(context);
        initialize();
        android.view.ViewGroup.LayoutParams params = new android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        this.setOrientation(LinearLayout.VERTICAL);
        this.setLayoutParams(params);
        this.setDuplicateParentStateEnabled(true);
    }

    private void initialize(){
        this.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((SinglePageActivity) getContext()).resetCursor();
            }
        });

        this.removeAllViews();
        lineHeight = PrefManager.getIntPreference(PrefManager.NOTE_LINE_HEIGHT, getContext());
        lineWidth = PrefManager.getIntPreference(PrefManager.NOTE_PAGE_WIDTH, getContext());

        for (int i = 0; i < NUM_LINES; i++) {
            SingleLine currentLine = new SingleLine(getContext(), lineWidth, lineHeight, i, this);
            this.addView(currentLine);
        }
    }

    public void setPageFull(boolean pageFull) {
        this.pageFull = pageFull;
    }

    public boolean isPageFull() {
        return pageFull;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

//    public LinearLayout getLinesBase(){
//        return this;
//    }
}
