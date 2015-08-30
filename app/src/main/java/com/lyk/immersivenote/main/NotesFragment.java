package com.lyk.immersivenote.main;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lyk.immersivenote.R;
import com.lyk.immersivenote.database.MainDataSource;
import com.lyk.immersivenote.database.MyDatabaseHelper;
import com.lyk.immersivenote.database.NoteDataSource;
import com.lyk.immersivenote.utils.DBUti;
import com.lyk.immersivenote.utils.PrefUti;
import com.rey.material.widget.FloatingActionButton;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by John on 2015/8/27.
 */
public class NotesFragment extends Fragment {

    private SweetAlertDialog sweetAlertDialog = null;
    private CardCursorAdapter dataAdapter;
    private ListView listView;
    private int deleteID;
    // to reference itself in private classes
    private NotesFragment self;

    private FloatingActionButton fab;

    private static MyDatabaseHelper myDBHelper = null;
    private static SQLiteDatabase database = null;

    private int themeColor;

    public NotesFragment(){
        self = this;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(myDBHelper == null){
            myDBHelper = new MyDatabaseHelper(this.getActivity().getApplication());
            database = myDBHelper.getDB();
            NoteDataSource.setDatabase(database);
            MainDataSource.setDatabase(database);
            MainDataSource.createMainTable();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View forReturn = inflater.inflate(R.layout.fragment_notes, container, false);
        listView = (ListView) forReturn.findViewById(R.id.notes_list);
        fab = (FloatingActionButton) forReturn.findViewById(R.id.homeFabButton);
        applyThemeColor();
        return forReturn;
    }

    @Override
    public void onResume() {
        super.onResume();
        showMaterialProgress("Loading...");
        new TaskLoadNote(this.getActivity(),this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    private void showMaterialProgress(String title){
        if(sweetAlertDialog == null)
            sweetAlertDialog = new SweetAlertDialog(this.getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        else
            sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(themeColor);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
    }
    

    public void showDeleteConfirmationDialog(int id){
        deleteID = id;
        sweetAlertDialog = new SweetAlertDialog(this.getActivity(), SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setOwnerActivity(this.getActivity());
        sweetAlertDialog.setTitleText(this.getActivity().getString(R.string.dialog_delete_note_title))
                .setContentText(this.getActivity().getString(R.string.dialog_delete_note_label))
                .setConfirmText(this.getActivity().getString(R.string.dialog_yes))
                .setCancelText(this.getActivity().getString(R.string.dialog_no))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        sDialog.getProgressHelper().setBarColor(themeColor);
                        sDialog.setTitleText(self.getActivity().getString(R.string.dialog_deleting));
                        sDialog.setCancelable(false);
                        sDialog.showContentText(false);
                        sDialog.showCancelButton(false);
                        new TaskDeleteNote(sDialog.getOwnerActivity(), sDialog, self).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    }
                }).showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                });
        sweetAlertDialog.show();
    }

    public void dismissMaterialProgress(){
        if(sweetAlertDialog!=null){
            sweetAlertDialog.dismiss();
        }
    }

    public void dismissMaterialProgress(String title){
        if(sweetAlertDialog != null){
            sweetAlertDialog.setTitleText(title)
                    .setConfirmText(this.getActivity().getString(R.string.dialog_okay))
                    .showContentText(false)
                    .showCancelButton(false)
                    .setCancelClickListener(null)
                    .setConfirmClickListener(null)
                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        }
    }

    private class TaskDeleteNote extends AsyncTask<Void, Void, Void> {
        private SweetAlertDialog sDialog;
        private Context context;
        private NotesFragment notesFragment;

        public TaskDeleteNote(Context context, SweetAlertDialog sDialog,NotesFragment notesFragment){
            this.context = context;
            this.sDialog = sDialog;
            this.notesFragment = notesFragment;
        }

        @Override
        protected Void doInBackground(Void... params) {
            NoteDataSource.removeNoteTable(DBUti.getTableNameById(deleteID));
            MainDataSource.removeNote(sDialog.getContext(), deleteID);
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            new TaskLoadNote(context,notesFragment).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private class TaskLoadNote extends AsyncTask<Void, Void, Void> {

        private Context context;
        private NotesFragment notesFragment;

        public TaskLoadNote(Context context, NotesFragment notesFragment){
            this.context = context;
            this.notesFragment = notesFragment;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Cursor cursor = MainDataSource.getWholeCursor();
            dataAdapter = new CardCursorAdapter(context,cursor,0,notesFragment);
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            // Assign adapter to ListView
            listView.setAdapter(dataAdapter);
            dismissMaterialProgress();
        }
    }

}
