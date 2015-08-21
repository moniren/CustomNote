package com.lyk.immersivenote.main;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.lyk.immersivenote.R;
import com.rey.material.widget.TextView;

/**
 * Created by john on 2015/7/5.
 */
public class TempAdapter extends ArrayAdapter<String> {
    private String[] dataset;
    private Context context;
    public TempAdapter(Context context, int resource,String[] dataset){
        super(context,resource,dataset);
        this.context = context;
        this.dataset = dataset;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.temp_row_drawer, null);
        }
        v.setTag(position);
        ((TextView)v).setText(dataset[position]);
        return v;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }
}
