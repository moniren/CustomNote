package com.lyk.immersivenote.main;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lyk.immersivenote.R;
import com.lyk.immersivenote.utils.PrefUti;
import com.rey.material.widget.Button;
import com.rey.material.widget.Spinner;

import java.util.Locale;

/**
 * Created by John on 2015/8/27.
 */
public class SettingsFragment extends Fragment {
    private Spinner languageSpinner;
    private String[] languages;
    private String[] languagePrefs;
    private Button applyBtn;

    private SettingsFragment self;
    private int previousSelectedLanguage = 0;

    public SettingsFragment(){
        self = this;
    }
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View forReturn = inflater.inflate(R.layout.fragment_settings, container, false);
        languageSpinner = (Spinner) forReturn.findViewById(R.id.language_spinner);
        applyBtn = (Button) forReturn.findViewById(R.id.apply_settings_btn);

        setUpValues();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.row_spinner, languages);
        adapter.setDropDownViewResource(R.layout.row_spinner_dropdown);
        languageSpinner.setAdapter(adapter);

        String languageLocale = PrefUti.getStringPreference(PrefUti.CUSTOM_LOCALE,this.getActivity());
        if(languageLocale != null){
            Log.d("SettingsFragment","language locale not null - "+languageLocale);
            for(int i=0;i<languagePrefs.length;i++){
                if(languagePrefs[i].equals(languageLocale)){
                    languageSpinner.setSelection(i);
                    previousSelectedLanguage = i;
                    break;
                }
            }
        }

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedLanguage = languageSpinner.getSelectedItemPosition();
                PrefUti.setStringPreference(PrefUti.CUSTOM_LOCALE, languagePrefs[selectedLanguage], self.getActivity());
                PrefUti.updateLocale(languagePrefs[selectedLanguage], self.getActivity());
                Log.d("SettingsFragment","applying locale : "+languagePrefs[selectedLanguage]);
                if(selectedLanguage != previousSelectedLanguage){
                    ((HomeActivity) (self.getActivity())).reload();
                }
            }
        });
        return forReturn;
    }

    private void setUpValues(){
        languages = new String[3];
        languagePrefs = new String[3];

        languages[0] = "English";
        languagePrefs[0] = "en_US";

        languages[1] = "简体中文";
        languagePrefs[1] = "zh_CN";

        languages[2] = "繁體中文";
        languagePrefs[2] = "zh_TW";
    }

}
