package com.lyk.immersivenote.main;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lyk.immersivenote.R;
import com.lyk.immersivenote.utils.PrefUti;
import com.rey.material.widget.FloatingActionButton;

/**
 * Created by John on 2015/9/5.
 */
public class AboutFragment extends Fragment {
    private TextView openSourceDesc;
    private FloatingActionButton fab;
    private int themeColor;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View forReturn = inflater.inflate(R.layout.fragment_about, container, false);
        openSourceDesc = (TextView) forReturn.findViewById(R.id.about_opensrc_link);
        openSourceDesc.setMovementMethod(LinkMovementMethod.getInstance());
        fab = (FloatingActionButton) forReturn.findViewById(R.id.aboutFabButton);
        applyThemeColor();
        return forReturn;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) this.getActivity()).closeDrawer();
    }

    private void applyThemeColor(){
        String previousSelectedColor = PrefUti.getStringPreference(PrefUti.THEME_COLOR, this.getActivity());
        if(previousSelectedColor!=null){
            themeColor = Color.parseColor(previousSelectedColor);
        }
        else{
            themeColor = this.getActivity().getResources().getColor(R.color.color_primary);
        }
        fab.setBackgroundColor(themeColor);
    }


}
