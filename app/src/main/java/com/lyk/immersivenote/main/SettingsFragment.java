package com.lyk.immersivenote.main;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lyk.immersivenote.R;
import com.lyk.immersivenote.utils.ColorUti;
import com.lyk.immersivenote.utils.PrefUti;
import com.rey.material.widget.Button;
import com.rey.material.widget.CompoundButton;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.Slider;
import com.rey.material.widget.Spinner;

import java.util.Locale;

/**
 * Created by John on 2015/8/27.
 */
public class SettingsFragment extends Fragment {

    private TextView languageTitle;
    private TextView themeTitle;
    private TextView writingModeTitle;
    private TextView intervalTitle;

    private Spinner languageSpinner;
    private String[] languages;
    private String[] languagePrefs;
    private Button applyBtn;

    private SettingsFragment self;
    private int previousSelectedLanguage = -1;

    private String previousSelectedColor;
    private View colorPreview;
    private Slider redSlider;
    private Slider greenSlider;
    private Slider blueSlider;


    private RadioButton kanjiRadioBtn;
    private RadioButton latinRadioBtn;
    private String previousSelectedWritingMode;

    private int previousSelectedTimeInterval;
    private Slider timeSlider;
    private TextView timePreview;

    private Slider.OnPositionChangeListener timeSliderListener;
    private Slider.OnPositionChangeListener colorSliderListener;

    private CompoundButton.OnCheckedChangeListener writingModeListener;

    private HomeActivity homeActivity;


    public SettingsFragment() {
        self = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        homeActivity = (HomeActivity) this.getActivity();
        // Inflate the layout for this fragment
        View forReturn = inflater.inflate(R.layout.fragment_settings, container, false);
        languageSpinner = (Spinner) forReturn.findViewById(R.id.language_spinner);
        applyBtn = (Button) forReturn.findViewById(R.id.apply_settings_btn);

        colorPreview = forReturn.findViewById(R.id.settings_color_preview);
        redSlider = (Slider) forReturn.findViewById(R.id.settings_slider_red);
        greenSlider = (Slider) forReturn.findViewById(R.id.settings_slider_green);
        blueSlider = (Slider) forReturn.findViewById(R.id.settings_slider_blue);

        kanjiRadioBtn = (RadioButton) forReturn.findViewById(R.id.settings_writing_mode_kanji);
        latinRadioBtn = (RadioButton) forReturn.findViewById(R.id.settings_writing_mode_latin);

        timePreview = (TextView) forReturn.findViewById(R.id.settings_time_interval_preview);
        timeSlider = (Slider) forReturn.findViewById(R.id.settings_slider_time);

        languageTitle = (TextView) forReturn.findViewById(R.id.settings_language_title);
        themeTitle = (TextView) forReturn.findViewById(R.id.settings_theme_title);
        writingModeTitle = (TextView) forReturn.findViewById(R.id.settings_writing_mode_title);
        intervalTitle = (TextView) forReturn.findViewById(R.id.settings_time_interval_title);


        applyThemeColor();

        colorSliderListener = new Slider.OnPositionChangeListener() {
            @Override
            public void onPositionChanged(Slider slider, boolean b, float v, float v1, int i, int i1) {
                int color = Color.rgb(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue());
                colorPreview.setBackgroundColor(color);
                changeColor(color);
            }
        };

        writingModeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    kanjiRadioBtn.setChecked(kanjiRadioBtn == buttonView);
                    latinRadioBtn.setChecked(latinRadioBtn == buttonView);
                }
            }
        };

        timeSliderListener = new Slider.OnPositionChangeListener() {
            @Override
            public void onPositionChanged(Slider slider, boolean b, float v, float v1, int i, int i1) {
                timePreview.setText(timeSlider.getValue() + "ms");
            }
        };

        setUpLanguageValues();
        setUpLanguageSection();
        setUpThemeColorSection();
        setUpWritingModeSection();
        setUpTimeIntervalSection();

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int selectedLanguage = languageSpinner.getSelectedItemPosition();
                final String selectedThemeColor = "#"+ColorUti.getHexStringForColor(redSlider.getValue())+ColorUti.getHexStringForColor(greenSlider.getValue())+ColorUti.getHexStringForColor(blueSlider.getValue());

                boolean languageChanged = selectedLanguage != previousSelectedLanguage || previousSelectedLanguage== -1;
                boolean colorChanged = !selectedThemeColor.equals(previousSelectedColor);

                if(kanjiRadioBtn.isChecked()){
                    PrefUti.setStringPreference(PrefUti.WRITING_MODE,PrefUti.KANJI_MODE,homeActivity);
                }
                else{
                    PrefUti.setStringPreference(PrefUti.WRITING_MODE,PrefUti.LATIN_MODE,homeActivity);
                }

                PrefUti.setIntPreference(PrefUti.TIME_INTERVAL,timeSlider.getValue(),homeActivity);

                if (languageChanged || colorChanged) {
                    if (colorChanged) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                PrefUti.setStringPreference(PrefUti.THEME_COLOR, selectedThemeColor, homeActivity);
                            }
                        }).start();
                    }
                    if (languageChanged) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                PrefUti.setStringPreference(PrefUti.CUSTOM_LOCALE, languagePrefs[selectedLanguage], homeActivity);
                                PrefUti.updateLocale(languagePrefs[selectedLanguage], homeActivity);
                            }
                        }).start();
                        homeActivity.reload();
                    }
                    else{
                        homeActivity.switchToNotesFragment();
                    }
                }
                else{
                    homeActivity.switchToNotesFragment();
                }
            }
        });
        return forReturn;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) this.getActivity()).closeDrawer();
    }

    @Override
    public void onPause() {
        super.onPause();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectedThemeColor = "#"+ ColorUti.getHexStringForColor(redSlider.getValue())+ColorUti.getHexStringForColor(greenSlider.getValue())+ColorUti.getHexStringForColor(blueSlider.getValue());
                boolean colorChanged = !selectedThemeColor.equals(previousSelectedColor);
                if (colorChanged) {
                    PrefUti.setStringPreference(PrefUti.THEME_COLOR, selectedThemeColor, homeActivity);
                }
            }
        }).start();

    }

    private void applyThemeColor(){
        String previousSelectedColor = PrefUti.getStringPreference(PrefUti.THEME_COLOR, homeActivity);
        if(previousSelectedColor!=null){
            languageTitle.setTextColor(Color.parseColor(previousSelectedColor));
            themeTitle.setTextColor(Color.parseColor(previousSelectedColor));
            writingModeTitle.setTextColor(Color.parseColor(previousSelectedColor));
            intervalTitle.setTextColor(Color.parseColor(previousSelectedColor));
        }
        else{
            languageTitle.setTextColor(homeActivity.getResources().getColor(R.color.color_primary));
            themeTitle.setTextColor(homeActivity.getResources().getColor(R.color.color_primary));
            writingModeTitle.setTextColor(homeActivity.getResources().getColor(R.color.color_primary));
            intervalTitle.setTextColor(homeActivity.getResources().getColor(R.color.color_primary));
        }
    }

    private void setUpLanguageValues() {
        languages = new String[4];
        languagePrefs = new String[4];

        languages[0] = "English";
        languagePrefs[0] = "en_US";

        languages[1] = "简体中文";
        languagePrefs[1] = "zh_CN";

        languages[2] = "繁體中文";
        languagePrefs[2] = "zh_TW";

        languages[3] = "ภาษาไทย";
        languagePrefs[3] = "th";
    }

    private void setUpLanguageSection() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(homeActivity, R.layout.row_spinner, languages);
        adapter.setDropDownViewResource(R.layout.row_spinner_dropdown);
        languageSpinner.setAdapter(adapter);

        String languageLocale = PrefUti.getStringPreference(PrefUti.CUSTOM_LOCALE, homeActivity);
        if (languageLocale != null) {
            for (int i = 0; i < languagePrefs.length; i++) {
                if (languagePrefs[i].equals(languageLocale)) {
                    languageSpinner.setSelection(i);
                    previousSelectedLanguage = i;
                    break;
                }
            }
        }
        else{
            Locale currentLocale = getResources().getConfiguration().locale;
            boolean foundLocale = false;
            for (int i = 0; i < languagePrefs.length; i++) {
                Locale tempLocale = new Locale(languagePrefs[i]);
                if(languagePrefs[i].equals("zh_CN")){
                    tempLocale = Locale.SIMPLIFIED_CHINESE;
                }
                else if (languagePrefs[i].equals("zh_TW")){
                    tempLocale = Locale.TRADITIONAL_CHINESE;
                }
                if (tempLocale.equals(currentLocale)) {
                    languageSpinner.setSelection(i);
                    foundLocale = true;
                    break;
                }
            }
            if(!foundLocale){
                languageSpinner.setSelection(0);
            }
        }
    }

    private void setUpThemeColorSection() {
        previousSelectedColor = PrefUti.getStringPreference(PrefUti.THEME_COLOR, homeActivity);
        if (previousSelectedColor == null) {
            colorPreview.setBackgroundColor(homeActivity.getResources().getColor(R.color.color_primary));
            redSlider.setValue(230, false);
            greenSlider.setValue(78, false);
            blueSlider.setValue(64, false);
        } else {
            colorPreview.setBackgroundColor(Color.parseColor(previousSelectedColor));
            redSlider.setValue(Integer.parseInt(previousSelectedColor.substring(1, 3),16), false);
            greenSlider.setValue(Integer.parseInt(previousSelectedColor.substring(3, 5), 16), false);
            blueSlider.setValue(Integer.parseInt(previousSelectedColor.substring(5), 16), false);
        }

        redSlider.setOnPositionChangeListener(colorSliderListener);
        greenSlider.setOnPositionChangeListener(colorSliderListener);
        blueSlider.setOnPositionChangeListener(colorSliderListener);

    }

    private void setUpWritingModeSection(){
        previousSelectedWritingMode = PrefUti.getStringPreference(PrefUti.WRITING_MODE,homeActivity);
        if(previousSelectedWritingMode != null){
            kanjiRadioBtn.setCheckedImmediately(previousSelectedWritingMode.equals(PrefUti.KANJI_MODE));
            latinRadioBtn.setCheckedImmediately(previousSelectedWritingMode.equals(PrefUti.LATIN_MODE));
        }
        kanjiRadioBtn.setOnCheckedChangeListener(writingModeListener);
        latinRadioBtn.setOnCheckedChangeListener(writingModeListener);
    }

    private void setUpTimeIntervalSection(){
        previousSelectedTimeInterval = PrefUti.getIntPreference(PrefUti.TIME_INTERVAL,homeActivity);
        if(previousSelectedTimeInterval > 0){
            timeSlider.setValue(previousSelectedTimeInterval,false);
            timePreview.setText(previousSelectedTimeInterval+"ms");
        }
        timeSlider.setOnPositionChangeListener(timeSliderListener);
    }

    private void changeColor(int color){
        languageTitle.setTextColor(color);
        themeTitle.setTextColor(color);
        writingModeTitle.setTextColor(color);
        intervalTitle.setTextColor(color);
        homeActivity.changeColor(color);
    }


}
