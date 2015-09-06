package com.lyk.immersivenote.main;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.lyk.immersivenote.R;
import com.lyk.immersivenote.database.MainDataSource;
import com.lyk.immersivenote.database.MainTable;
import com.lyk.immersivenote.utils.ColorUti;
import com.rey.material.app.Dialog;
import com.rey.material.widget.Button;
import com.rey.material.widget.Slider;

/**
 * Created by John on 2015/8/31.
 */
public class ChooseColorDialog extends Dialog {

    private int id;
    private String previousColor;
    private GradientDrawable circleShape;
    private CircleAbbrev circle;

    private View colorPreview;
    private Slider redSlider;
    private Slider greenSlider;
    private Slider blueSlider;
    private Slider.OnPositionChangeListener sliderListener;

    private Button okBtn;
    private Button cancelBtn;
    private ChooseColorDialog self;

    public ChooseColorDialog(Context context,int id, CircleAbbrev circle) {
        super(context);
        self = this;
        this.id = id;
        this.previousColor = circle.getBgColor();
        this.setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_choose_color);
        this.circle = circle;
        this.circleShape = (GradientDrawable) circle.getBackground();

        colorPreview = findViewById(R.id.dialog_choose_color_color_preview);
        redSlider = (Slider) findViewById(R.id.dialog_choose_color_slider_red);
        greenSlider = (Slider) findViewById(R.id.dialog_choose_color_slider_green);
        blueSlider = (Slider) findViewById(R.id.dialog_choose_color_slider_blue);

        okBtn = (Button) findViewById(R.id.dialog_choose_color_ok_button);
        cancelBtn = (Button) findViewById(R.id.dialog_choose_color_cancel_button);

        sliderListener = new Slider.OnPositionChangeListener() {
            @Override
            public void onPositionChanged(Slider slider, boolean b, float v, float v1, int i, int i1) {
                int color = Color.rgb(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue());
                colorPreview.setBackgroundColor(color);
                circleShape.setColor(color);
            }
        };

        setUpdColorSection();

        setUpBtns();


    }

    private void setUpdColorSection(){
        colorPreview.setBackgroundColor(Color.parseColor(previousColor));

        redSlider.setValue(Integer.parseInt(previousColor.substring(1, 3),16), false);
        greenSlider.setValue(Integer.parseInt(previousColor.substring(3, 5),16), false);
        blueSlider.setValue(Integer.parseInt(previousColor.substring(5),16), false);

        redSlider.setOnPositionChangeListener(sliderListener);
        greenSlider.setOnPositionChangeListener(sliderListener);
        blueSlider.setOnPositionChangeListener(sliderListener);
    }

    private void setUpBtns(){
        //OK button implementation
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String selectedThemeColor = "#" + ColorUti.getHexStringForColor(redSlider.getValue()) + ColorUti.getHexStringForColor(greenSlider.getValue()) + ColorUti.getHexStringForColor(blueSlider.getValue());
                        if (!selectedThemeColor.equals(previousColor)) {
                            ContentValues values = new ContentValues();
                            values.put(MainTable.COLUMN_BACKGROUND, selectedThemeColor);
                            MainDataSource.updateNote(self.getContext().getApplicationContext(), id, values);
                            circle.setBgColor(selectedThemeColor);
                        }
                    }
                }).start();
                self.dismiss();
            }
        });
        //Cancel
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
                self.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        reset();
        super.onBackPressed();
    }

    private void reset(){
        circleShape.setColor(Color.parseColor(previousColor));
    }



}
