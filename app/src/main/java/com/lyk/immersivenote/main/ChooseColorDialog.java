package com.lyk.immersivenote.main;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.view.View;
import android.widget.LinearLayout;

import com.lyk.immersivenote.R;
import com.rey.material.app.Dialog;
import com.rey.material.widget.Button;
import com.rey.material.widget.Slider;

/**
 * Created by John on 2015/8/31.
 */
public class ChooseColorDialog extends Dialog {

    private int id;
    private String color;
    private ShapeDrawable circleShape;

    private View colorPreview;
    private Slider redSlider;
    private Slider greenSlider;
    private Slider blueSlider;
    private Slider.OnPositionChangeListener sliderListener;

    private Button okBtn;
    private Button cancelBtn;
    private ChooseColorDialog self;

    public ChooseColorDialog(Context context,int id, LinearLayout circle) {
        super(context);
        self = this;
        this.id = id;
        this.setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_choose_color);

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
            }
        };

        setUpdColorSection();

        setUpBtns();


    }

    private void setUpdColorSection(){
        redSlider.setOnPositionChangeListener(sliderListener);
        greenSlider.setOnPositionChangeListener(sliderListener);
        blueSlider.setOnPositionChangeListener(sliderListener);
    }

    private void setUpBtns(){
        //OK button implementation
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    }


}
