package com.lyk.immersivenote.notepad;

import android.content.Context;

import com.lyk.immersivenote.R;
import com.rey.material.app.Dialog;
import com.rey.material.widget.CompoundButton;
import com.rey.material.widget.RadioButton;

/**
 * Created by Leelar on 8/27/2015.
 */
public class NotepadDialog extends Dialog {
    private RadioButton blackRadioBtn;
    private RadioButton redRadioBtn;
    private RadioButton blueRadioBtn;
    private RadioButton kanjiRadioBtn;
    private RadioButton latinRadioBtn;
    public NotepadDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_show_menu);
        blackRadioBtn = (RadioButton) findViewById(R.id.ink_color_black);
        redRadioBtn = (RadioButton) findViewById(R.id.ink_color_red);
        blueRadioBtn = (RadioButton) findViewById(R.id.ink_color_blue);
        kanjiRadioBtn = (RadioButton) findViewById(R.id.writing_mode_kanji);
        latinRadioBtn = (RadioButton) findViewById(R.id.writing_mode_latin);


        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(buttonView != kanjiRadioBtn && buttonView != latinRadioBtn){
                        blackRadioBtn.setChecked(blackRadioBtn == buttonView);
                        redRadioBtn.setChecked(redRadioBtn == buttonView);
                        blueRadioBtn.setChecked(blueRadioBtn == buttonView);
                    }
                    else{
                        kanjiRadioBtn.setChecked(kanjiRadioBtn == buttonView);
                        latinRadioBtn.setChecked(latinRadioBtn == buttonView);
                    }
                }
            }

        };

        blackRadioBtn.setOnCheckedChangeListener(listener);
        redRadioBtn.setOnCheckedChangeListener(listener);
        blueRadioBtn.setOnCheckedChangeListener(listener);
        kanjiRadioBtn.setOnCheckedChangeListener(listener);
        latinRadioBtn.setOnCheckedChangeListener(listener);
    }

    @Override
    protected void onCreate() {
        super.onCreate();

    }
}
