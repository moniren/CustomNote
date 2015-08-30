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
import com.lyk.immersivenote.utils.PrefUti;
import com.rey.material.widget.Button;
import com.rey.material.widget.Slider;
import com.rey.material.widget.Spinner;

/**
 * Created by John on 2015/8/27.
 */
public class SettingsFragment extends Fragment {

    private TextView languageTitle;
    private TextView themeTitle;

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
    private Slider.OnPositionChangeListener sliderListener;

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

        languageTitle = (TextView) forReturn.findViewById(R.id.settings_language_title);
        themeTitle = (TextView) forReturn.findViewById(R.id.settings_theme_title);

        applyThemeColor();

        sliderListener = new Slider.OnPositionChangeListener() {
            @Override
            public void onPositionChanged(Slider slider, boolean b, float v, float v1, int i, int i1) {
                int color = Color.rgb(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue());
                colorPreview.setBackgroundColor(color);
                changeColor(color);
            }
        };

        setUpLanguageValues();
        setUpLanguageSection();
        setUpThemeColorSection();


        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int selectedLanguage = languageSpinner.getSelectedItemPosition();
                final String selectedThemeColor = "#"+getHexStringForColor(redSlider.getValue())+getHexStringForColor(greenSlider.getValue())+getHexStringForColor(blueSlider.getValue());

                boolean languageChanged = selectedLanguage != previousSelectedLanguage || previousSelectedLanguage== -1;
                boolean colorChanged = !selectedThemeColor.equals(previousSelectedColor);

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
    public void onPause() {
        super.onPause();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectedThemeColor = "#"+getHexStringForColor(redSlider.getValue())+getHexStringForColor(greenSlider.getValue())+getHexStringForColor(blueSlider.getValue());
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
        }
        else{
            languageTitle.setTextColor(homeActivity.getResources().getColor(R.color.color_primary));
            themeTitle.setTextColor(homeActivity.getResources().getColor(R.color.color_primary));
        }
    }

    private void setUpLanguageValues() {
        languages = new String[3];
        languagePrefs = new String[3];

        languages[0] = "English";
        languagePrefs[0] = "en_US";

        languages[1] = "简体中文";
        languagePrefs[1] = "zh_CN";

        languages[2] = "繁體中文";
        languagePrefs[2] = "zh_TW";
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
            greenSlider.setValue(Integer.parseInt(previousSelectedColor.substring(3, 5),16), false);
            blueSlider.setValue(Integer.parseInt(previousSelectedColor.substring(5),16), false);
        }

        redSlider.setOnPositionChangeListener(sliderListener);
        greenSlider.setOnPositionChangeListener(sliderListener);
        blueSlider.setOnPositionChangeListener(sliderListener);

    }

    private void changeColor(int color){
        languageTitle.setTextColor(color);
        themeTitle.setTextColor(color);
        homeActivity.changeColor(color);
    }

    private String getHexStringForColor(int color){
        int first = color/16;
        int second = color%16;
        return getSingleNumberHex(first)+getSingleNumberHex(second);

    }

    private String getSingleNumberHex(int number){
        String forReturn = null;
        if(number > 9){
            switch(number){
                case 10:
                    forReturn = "A";
                    break;
                case 11:
                    forReturn = "B";
                    break;
                case 12:
                    forReturn = "C";
                    break;
                case 13:
                    forReturn = "D";
                    break;
                case 14:
                    forReturn = "E";
                    break;
                case 15:
                    forReturn = "F";
                    break;
            }
        }
        else{
            forReturn = String.valueOf(number);
        }
        return forReturn;
    }

}
