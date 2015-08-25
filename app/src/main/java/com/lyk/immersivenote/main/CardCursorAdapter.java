package com.lyk.immersivenote.main;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.lyk.immersivenote.R;
import com.lyk.immersivenote.database.MainDataSource;
import com.lyk.immersivenote.database.MainTable;
import com.lyk.immersivenote.utils.RippleBgUti;
import com.rey.material.util.ViewUtil;
import com.rey.material.widget.Button;
import com.rey.material.widget.ImageButton;

/**
 * Created by John on 2015/8/19.
 */
public class CardCursorAdapter extends CursorAdapter {
    private LayoutInflater inflater;

    private HomeActivity homeActivity;

    private    static  class   ViewHolder  {
//        int    id;
        TextView title;
        TextView   time;
        TextView abbrev;
        ImageButton deleteBtn;
//        ImageView bg;
    }

    private class DeleteOnClickListener implements View.OnClickListener {
        private int id;
        DeleteOnClickListener(int id){
            this.id = id;
            Log.d("DeleteOnClickListener","holding id: "+id);
        }

        @Override
        public void onClick(View v) {
            homeActivity.showDeleteConfirmationDialog(id);
        }
    }

    private class TitleOnClickListener implements View.OnClickListener {
        private int id;
        TitleOnClickListener(int id){
            this.id = id;
            Log.d("TitleOnClickListener","holding id: "+id);
        }

        @Override
        public void onClick(View v) {
            homeActivity.startEditing(id);
        }
    }

    public CardCursorAdapter(Context context, Cursor c, int flags) {
        super(context,c, flags);
        this.homeActivity = (HomeActivity) context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder  =   (ViewHolder)    view.getTag();
        String title = cursor.getString(cursor.getColumnIndex(MainTable.COLUMN_TITLE));
        holder.title.setText(title);
        holder.time.setText(cursor.getString(cursor.getColumnIndex(MainTable.COLUMN_TIME)));
        holder.abbrev.setText(title.substring(0, 1));
//        String imageName = "bg_"+cursor.getInt(cursor.getColumnIndex(MainTable.COLUMN_BACKGROUND));
//        int imgID = context.getResources().getIdentifier(imageName , "drawable", context.getPackageName());
//        holder.bg.setImageResource(imgID);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View   view    =   inflater.inflate(R.layout.view_note_card, null);
        ViewHolder holder  =   new ViewHolder();
//        holder.bg = (ImageView) view.findViewById(R.id.card_img);
        holder.title    =   (TextView)  view.findViewById(R.id.card_title);
        holder.time    =   (TextView)  view.findViewById(R.id.card_time);
//        holder.id   =   cursor.getInt(cursor.getColumnIndex(MainTable.COLUMN_ID));
        holder.abbrev = (TextView) view.findViewById(R.id.card_abbrev);
        holder.deleteBtn = (ImageButton) view.findViewById(R.id.card_delete_note);
        int id = cursor.getInt(cursor.getColumnIndex(MainTable.COLUMN_ID));
        DeleteOnClickListener deleteOnClickListener = new DeleteOnClickListener(id);
        holder.deleteBtn.setOnClickListener(deleteOnClickListener);
        TitleOnClickListener titleOnClickListener = new TitleOnClickListener(id);
        holder.title.setOnClickListener(titleOnClickListener);
        view.setTag(holder);
        ViewUtil.setBackground(holder.title, RippleBgUti.getFlatRippleBackground(context));
        return view;
    }
}