package com.lyk.immersivenote.main;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyk.immersivenote.R;
import com.lyk.immersivenote.database.MainTable;
import com.lyk.immersivenote.utils.RippleBgUti;
import com.pkmmte.view.CircularImageView;
import com.rey.material.widget.ImageButton;

/**
 * Created by John on 2015/8/19.
 */
public class CardCursorAdapter extends CursorAdapter {
    private LayoutInflater inflater;

    private HomeActivity homeActivity;
    private NotesFragment notesFragment;

    private DeleteOnClickListener deleteOnClickListener;
    private EditOnClickListener editOnClickListener;
    private CircleOnClickListener circleOnClickListener;

    private    static  class   ViewHolder  {
        LinearLayout circle;
        TextView title;
        TextView   time;
        TextView abbrev;
        ImageButton deleteBtn;
        ImageButton editBtn;
    }

    private class CircleOnClickListener implements View.OnClickListener{
        private int id;
        private LinearLayout circle;

        CircleOnClickListener(int id, LinearLayout circle){
            this.id = id;
            this.circle = circle;
        }

        @Override
        public void onClick(View v) {
            ChooseColorDialog dialog = new ChooseColorDialog(homeActivity,id,circle);
            dialog.show();
        }
    }

    private class DeleteOnClickListener implements View.OnClickListener {
        private int id;
        DeleteOnClickListener(int id){
            this.id = id;
            Log.d("DeleteOnClickListener","holding id: "+id);
        }

        @Override
        public void onClick(View v) {
            notesFragment.showDeleteConfirmationDialog(id);
        }
    }

    private class EditOnClickListener implements View.OnClickListener {
        private int id;
        private String title;
        EditOnClickListener(int id, String title){
            this.id = id;
            this.title = title;
            Log.d("TitleOnClickListener","holding id: "+id);
        }

        @Override
        public void onClick(View v) {
            homeActivity.startEditing(id,title);
        }
    }

    public CardCursorAdapter(Context context, Cursor c, int flags, NotesFragment notesFragment) {
        super(context,c, flags);
        this.homeActivity = (HomeActivity) context;
        this.notesFragment = notesFragment;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder  =   (ViewHolder)    view.getTag();
        String title = cursor.getString(cursor.getColumnIndex(MainTable.COLUMN_TITLE));
        String bgColor = cursor.getString(cursor.getColumnIndex(MainTable.COLUMN_BACKGROUND));



        int id = cursor.getInt(cursor.getColumnIndex(MainTable.COLUMN_ID));



        circleOnClickListener = new CircleOnClickListener(id,holder.circle);
        deleteOnClickListener = new DeleteOnClickListener(id);
        editOnClickListener = new EditOnClickListener(id,title);

        holder.circle.setOnClickListener(circleOnClickListener);
        ((GradientDrawable) holder.circle.getBackground()).setColor(Color.parseColor(bgColor));
        holder.deleteBtn.setOnClickListener(deleteOnClickListener);
        holder.editBtn.setOnClickListener(editOnClickListener);

        if(title.length() > 10){
            title = title.substring(0,7)+"...";
        }
        holder.title.setText(title);
        holder.time.setText(cursor.getString(cursor.getColumnIndex(MainTable.COLUMN_TIME)));
        holder.abbrev.setText(title.substring(0, 1));
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View   view    =   inflater.inflate(R.layout.view_note_card, null);
        ViewHolder holder  =   new ViewHolder();
        holder.circle = (LinearLayout) view.findViewById(R.id.card_circle);
        holder.title    =   (TextView)  view.findViewById(R.id.card_title);
        holder.time    =   (TextView)  view.findViewById(R.id.card_time);
        holder.abbrev = (TextView) view.findViewById(R.id.card_abbrev);
        holder.deleteBtn = (ImageButton) view.findViewById(R.id.card_delete_note);
        holder.editBtn = (ImageButton) view.findViewById(R.id.card_edit_note);
        view.setTag(holder);
        return view;
    }
}