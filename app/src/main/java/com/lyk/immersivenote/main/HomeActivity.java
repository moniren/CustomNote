package com.lyk.immersivenote.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconToolbar;
import com.lyk.immersivenote.R;
import com.lyk.immersivenote.database.MainDataSource;
import com.lyk.immersivenote.database.MainTable;
import com.lyk.immersivenote.database.MyDatabaseHelper;
import com.lyk.immersivenote.notepad.SinglePageActivity;
import com.rey.material.drawable.ToolbarRippleDrawable;
import com.rey.material.util.ViewUtil;
import com.rey.material.widget.Button;
import com.rey.material.widget.FloatingActionButton;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by lyk on 2015/7/4.
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout drawerLayout;
    private boolean isDrawerOpened;
    private RelativeLayout llDrawer;
    private Button btnStartWriting;
    private FloatingActionButton fabStartWriting;

    //    private MaterialMenuView materialMenu;
    private MaterialMenuIconToolbar materialMenu;

    private static MyDatabaseHelper myDBHelper = null;
    private static SQLiteDatabase database = null;

    private CardCursorAdapter dataAdapter;

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private BroadcastReceiver noteChangeReceiver;

    private SweetAlertDialog sweetAlertDialog = null;

    private int deleteID;

    // this is a private custom BroadcastReceiver which can access the
    // ViewAlertActivity to trigger the ui update
    private class DatabaseBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
                Log.d("HomeActivity", "received broadcast");
                loadNotes();
                dismissMaterialProgress("Deleted!");
                return;
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(myDBHelper == null){
            myDBHelper = new MyDatabaseHelper(this.getApplication());
            database = myDBHelper.getDB();
            MainDataSource.createMainTable(database);
        }
        setContentView(R.layout.activity_main);
        initCustomActionBar();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        llDrawer = (RelativeLayout) findViewById(R.id.main_ll_drawer);
        btnStartWriting = (Button) findViewById(R.id.button_start_writing);
        btnStartWriting.setOnClickListener(this);
        fabStartWriting = (FloatingActionButton) findViewById(R.id.homeFabButton);
        fabStartWriting.setOnClickListener(this);
        drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                materialMenu.setTransformationOffset(
                        MaterialMenuDrawable.AnimationState.BURGER_ARROW,
                        isDrawerOpened ? 2 - slideOffset : slideOffset
                );
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                isDrawerOpened = true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isDrawerOpened = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_IDLE) {
                    if (isDrawerOpened) materialMenu.setState(MaterialMenuDrawable.IconState.ARROW);
                    else materialMenu.setState(MaterialMenuDrawable.IconState.BURGER);
                }
            }
        });
        noteChangeReceiver = new DatabaseBroadcastReceiver();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        isDrawerOpened = drawerLayout.isDrawerOpen(GravityCompat.START); // or END, LEFT, RIGHT
    }

    @Override
    protected void onResume(){
        super.onResume();

        showMaterialProgress("Loading...");
        loadNotes();
        dismissMaterialProgress();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.lyk.immersivenote.database.insert."
                + MainTable.TABLE_MAIN);
        filter.addAction("com.lyk.immersivenote.database.remove." + MainTable.TABLE_MAIN);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(noteChangeReceiver, filter);
    }

    @Override
    protected void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(noteChangeReceiver);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        materialMenu.onSaveInstanceState(outState);
    }

    private void loadNotes(){
        Cursor cursor = MainDataSource.getWholeCursor();

//        String[] columns = {MainTable.COLUMN_ID,
//                MainTable.COLUMN_TITLE, MainTable.COLUMN_TIME,
//                MainTable.COLUMN_BACKGROUND};
//        int[] to = {
//                R.id.card_id,
//                R.id.card_title,
//                R.id.card_time,
//                R.id.card_bg_id
//        };
//        dataAdapter = new SimpleCursorAdapter(
//                this, R.layout.view_note_card,
//                cursor,
//                columns,
//                to,
//                0);
        dataAdapter = new CardCursorAdapter(this,cursor,0);
        ListView listView = (ListView) findViewById(R.id.notes_list);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
    }

    private void initCustomActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarHome);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (materialMenu.getState() == MaterialMenuDrawable.IconState.ARROW && isDrawerOpened) {
                    materialMenu.animatePressedState(MaterialMenuDrawable.IconState.BURGER);
                    drawerLayout.closeDrawer(llDrawer);
                } else if (!isDrawerOpened) {
                    materialMenu.animatePressedState(MaterialMenuDrawable.IconState.ARROW);
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        materialMenu = new MaterialMenuIconToolbar(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN) {
            @Override
            public int getToolbarViewId() {
                return R.id.toolbarHome;
            }
        };

        ViewUtil.setBackground(getToolbarNavigationIcon(toolbar), getRippleBackground());
    }

    // used for setting the ripple effect for the materialMenu icon
    private View getToolbarNavigationIcon(Toolbar toolbar) {
        //check if contentDescription previously was set
        boolean hadContentDescription = TextUtils.isEmpty(toolbar.getNavigationContentDescription());
        String contentDescription = !hadContentDescription ? toolbar.getNavigationContentDescription().toString() : "navigationIcon";
        toolbar.setNavigationContentDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews, contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setNavigationContentDescription ensures its existence
        View navIcon = null;
        if (potentialViews.size() > 0) {
            navIcon = potentialViews.get(0); //navigation icon is ImageButton
        }
        //Clear content description if not previously present
        if (hadContentDescription)
            toolbar.setNavigationContentDescription(null);
        return navIcon;
    }


    private ToolbarRippleDrawable getRippleBackground() {
        ToolbarRippleDrawable.Builder mBuilder = new ToolbarRippleDrawable.Builder(this, R.style.FlatColorButtonRippleStyle);
        return mBuilder.build();
    }

    public void showDeleteConfirmationDialog(int id){
        deleteID = id;
        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Sure to delete the note?")
                .setContentText("Won't be able to recover !")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        sDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        sDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.color_primary));
                        sDialog.setTitleText("Deleting...");
                        sDialog.setCancelable(false);
                        sDialog.showContentText(false);
                        MainDataSource.removeNote(sDialog.getContext(), deleteID);
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

    public void showMaterialProgress(String title){
        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.color_primary));
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setCancelable(false);
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
                    .setConfirmText("OK")
                    .showContentText(false)
                    .showCancelButton(false)
                    .setCancelClickListener(null)
                    .setConfirmClickListener(null)
                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_start_writing) {
            Intent intent = new Intent(this, SinglePageActivity.class);
            startActivity(intent);
            return;
        }

        if (v.getId() == R.id.homeFabButton) {
            Intent intent = new Intent(this, SinglePageActivity.class);
            startActivity(intent);
            return;
        }
    }

}