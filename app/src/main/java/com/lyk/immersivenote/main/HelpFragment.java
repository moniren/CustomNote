package com.lyk.immersivenote.main;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyk.immersivenote.R;

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