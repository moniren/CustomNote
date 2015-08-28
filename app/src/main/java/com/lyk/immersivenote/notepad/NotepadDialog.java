package com.lyk.immersivenote.notepad;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.lyk.immersivenote.R;
import com.rey.material.app.Dialog;
import com.rey.material.widget.Button;
import com.rey.material.widget.CompoundButton;
import com.rey.material.widget.RadioButton;

/**
 * Created by Leelar on 8/27/2015.
 */
public class NotepadDialog extends Dialog {
    private SinglePageActivity singlePageActivity;
    private Button saveNoteBtn;
    private Button cancelBtn;
    private Button okBtn;
    private RadioButton blackRadioBtn;
    private RadioButton redRadioBtn;
    private RadioButton blueRadioBtn;
    private RadioButton kanjiRadioBtn;
    private RadioButton latinRadioBtn;
    private NotepadDialog self;
    public NotepadDialog(Context context) {
        super(context);
        this.setCanceledOnTouchOutside(false);
        singlePageActivity = (SinglePageActivity) context;
        self = this;
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
        saveNoteBtn = (Button) findViewById(R.id.notepad_dialog_saveBtn);
        okBtn =(Button) findViewById(R.id.dialog_show_menu_ok_button);
        cancelBtn = (Button) findViewById(R.id.dialog_show_menu_cancel_button);
        //OK button implementation
        okBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(blackRadioBtn.isChecked()){
                    singlePageActivity.getSignatureCapture().setPaintColor(Color.BLACK);
                }
                else if(redRadioBtn.isChecked()){
                    singlePageActivity.getSignatureCapture().setPaintColor(Color.RED);
                }
                else{
                    singlePageActivity.getSignatureCapture().setPaintColor(Color.BLUE);
                }
                self.dismiss();
            }
        });
        //Cancel
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                self.dismiss();
            }
        });
        //save
        saveNoteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                self.dismiss();
                singlePageActivity.save(false);
            }
        });
    }

    @Override
    protected void onCreate() {
        super.onCreate();

    }
}
