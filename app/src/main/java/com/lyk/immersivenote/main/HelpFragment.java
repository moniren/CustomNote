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
public class HelpFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View forReturn = inflater.inflate(R.layout.fragment_help, container, false);
        return forReturn;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) this.getActivity()).closeDrawer();
    }

}