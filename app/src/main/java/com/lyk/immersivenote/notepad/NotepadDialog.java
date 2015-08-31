package com.lyk.immersivenote.notepad;

import android.content.Context;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;

import com.lyk.immersivenote.R;
import com.rey.material.app.Dialog;
import com.rey.material.widget.Button;
import com.rey.material.widget.CompoundButton;
import com.rey.material.widget.EditText;
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

    private EditText titleText;
    private String previousTitle;

    private RadioButton firstSectionSelected;
    private RadioButton secondSectionSelected;

    public NotepadDialog(Context context) {
        super(context);
        this.setCanceledOnTouchOutside(false);
        singlePageActivity = (SinglePageActivity) context;
        self = this;
        setContentView(R.layout.dialog_notepad_options);

        titleText = (EditText) findViewById(R.id.textfield_note_title);
        titleText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(titleText.getText().toString().length() >= 20){
                    titleText.setHint(R.string.notepad_dialog_title_error);
                }
                else{
                    titleText.setHint(R.string.notepad_dialog_title_hint);
                }
                return false;
            }
        });

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

                if(kanjiRadioBtn.isChecked()){
                    singlePageActivity.getSignatureCapture().setKanjiMode(true);
                }
                else{
                    singlePageActivity.getSignatureCapture().setKanjiMode(false);
                }

                if(titleText.getText() == null || titleText.getText().toString().length()==0){
                    titleText.setText(R.string.notepad_dialog_title_default);
                }
                singlePageActivity.setTitle(titleText.getText().toString());
                self.dismiss();
            }
        });
        //Cancel
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetState();
                self.dismiss();
            }
        });
        //save
        saveNoteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(titleText.getText() == null || titleText.getText().toString().length()==0){
                    titleText.setText(R.string.notepad_dialog_title_default);
                }
                singlePageActivity.setTitle(titleText.getText().toString());
                self.dismiss();
                singlePageActivity.save(false);
            }
        });
    }

    private void saveState(){
        if(blackRadioBtn.isChecked()){
            firstSectionSelected = blackRadioBtn;
        }
        else if (redRadioBtn.isChecked()){
            firstSectionSelected = redRadioBtn;
        }
        else {
            firstSectionSelected = blueRadioBtn;
        }

        if(kanjiRadioBtn.isChecked()){
            secondSectionSelected = kanjiRadioBtn;
        }
        else{
            secondSectionSelected = latinRadioBtn;
        }

        previousTitle = titleText.getText().toString();

    }

    private void resetState(){
        blackRadioBtn.setChecked(false);
        redRadioBtn.setChecked(false);
        blueRadioBtn.setChecked(false);

        kanjiRadioBtn.setChecked(false);
        latinRadioBtn.setChecked(false);

        firstSectionSelected.setChecked(true);
        secondSectionSelected.setChecked(true);

        titleText.setText(previousTitle);
    }

    public void setTitle(String title){
        titleText.setText(title);
    }


    public String getTitle(){
        String title = titleText.getText().toString();
        if(title == null || title.length()==0){
            title = this.getContext().getString(R.string.notepad_dialog_title_default);
        }
        return title;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        saveState();
    }
}
