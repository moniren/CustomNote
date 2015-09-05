package com.lyk.immersivenote.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.lyk.immersivenote.R;
import com.lyk.immersivenote.main.HomeActivity;
import com.lyk.immersivenote.utils.PrefUti;

/**
 * Created by John on 2015/9/1.
 */
public class AppDemo extends AppIntro2 {

    public static final String FORCE_VIEW = "ForceView";

    @Override
    public void init(Bundle savedInstanceState) {

        if( ! getIntent().getBooleanExtra(FORCE_VIEW,false)){
            boolean notFirstTimeUse = PrefUti.getBooleanPreference(PrefUti.NOT_FIRST_TIME_USE,this);
            if(notFirstTimeUse){
                loadMainActivity();
                finish();
            }
        }
        addSlide(SingleSlide.newInstance(R.layout.demo_1));
        addSlide(SingleSlide.newInstance(R.layout.demo_2));
        addSlide(SingleSlide.newInstance(R.layout.demo_3));
        addSlide(SingleSlide.newInstance(R.layout.demo_4));
    }

    private void loadMainActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onDonePressed() {
        PrefUti.setBooleanPreference(PrefUti.NOT_FIRST_TIME_USE, true, this);
        if( ! getIntent().getBooleanExtra(FORCE_VIEW,false)){
            loadMainActivity();
        }
        else{
            finish();
        }
    }
}
