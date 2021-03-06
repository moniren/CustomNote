package com.lyk.immersivenote.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconToolbar;
import com.lyk.immersivenote.R;
import com.lyk.immersivenote.demo.AppDemo;
import com.lyk.immersivenote.notepad.SinglePageActivity;
import com.lyk.immersivenote.utils.PrefUti;
import com.lyk.immersivenote.utils.RippleBgUti;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by lyk on 2015/7/4.
 */
public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private boolean isDrawerOpened;
    private RelativeLayout llDrawer;

    private MaterialMenuIconToolbar materialMenu;

    private TextView drawerNotes;
    private TextView drawerSettings;
    private TextView drawerHelp;
    private TextView drawerAbout;
    private TextView drawerViewIntro;

    private FragmentManager fragmentManager;
    private Fragment fragment;
    private Toolbar toolbar;

    private int themeColor;

    private HomeActivity self;
    private final static String APP_PNAME = "com.lyk.immersivenote";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setUpLocale();
        setContentView(R.layout.activity_main);
        initCustomActionBar();
        initDrawer();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        llDrawer = (RelativeLayout) findViewById(R.id.main_ll_drawer);

        applyThemeColor();

        llDrawer.setOnClickListener(null);
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

        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new NotesFragment();
        fragmentTransaction.add(R.id.home_fragment_place_holder,fragment);
        fragmentTransaction.commit();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        isDrawerOpened = drawerLayout.isDrawerOpen(GravityCompat.START); // or END, LEFT, RIGHT
    }

    @Override
    protected void onResume(){
        super.onResume();
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        materialMenu.onSaveInstanceState(outState);
    }

    private void setUpLocale(){
        String localePref = PrefUti.getStringPreference(PrefUti.CUSTOM_LOCALE,this);
        if(localePref != null) {
            PrefUti.updateLocale(localePref, this);
        }
    }

    private void initCustomActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbarHome);
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.navigation_drawer_notes));
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

        RippleBgUti.setFlatColorRippleBackground(getToolbarNavigationIcon(toolbar), this);
    }

    private void initDrawer(){
        drawerNotes = (TextView) findViewById(R.id.drawer_notes);
        drawerSettings = (TextView) findViewById(R.id.drawer_settings);
        drawerHelp = (TextView) findViewById(R.id.drawer_help);
        drawerAbout = (TextView) findViewById(R.id.drawer_about);
        drawerViewIntro = (TextView) findViewById(R.id.drawer_view_demo);
        drawerNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(fragment instanceof NotesFragment)) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(fragment);
                    fragmentTransaction.commit();
                    fragment = new NotesFragment();
                    FragmentTransaction fragmentTransactionNew = fragmentManager.beginTransaction();
                    fragmentTransactionNew.add(R.id.home_fragment_place_holder, fragment);
                    fragmentTransactionNew.commit();
                    setTitle(getResources().getString(R.string.navigation_drawer_notes));
                }
            }
        });
        drawerSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(fragment instanceof SettingsFragment)) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(fragment);
                    fragmentTransaction.commit();
                    fragment = new SettingsFragment();
                    FragmentTransaction fragmentTransactionNew = fragmentManager.beginTransaction();
                    fragmentTransactionNew.add(R.id.home_fragment_place_holder, fragment);
                    fragmentTransactionNew.commit();
                    setTitle(getResources().getString(R.string.navigation_drawer_settings));
                }
            }
        });
        drawerHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(fragment instanceof HelpFragment)) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(fragment);
                    fragmentTransaction.commit();
                    fragment = new HelpFragment();
                    FragmentTransaction fragmentTransactionNew = fragmentManager.beginTransaction();
                    fragmentTransactionNew.add(R.id.home_fragment_place_holder, fragment);
                    fragmentTransactionNew.commit();
                    setTitle(getResources().getString(R.string.navigation_drawer_help));
                }
            }
        });
        drawerAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(fragment instanceof AboutFragment)) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(fragment);
                    fragmentTransaction.commit();
                    fragment = new AboutFragment();
                    FragmentTransaction fragmentTransactionNew = fragmentManager.beginTransaction();
                    fragmentTransactionNew.add(R.id.home_fragment_place_holder, fragment);
                    fragmentTransactionNew.commit();
                    setTitle(getResources().getString(R.string.navigation_drawer_about));
                }
            }
        });
        drawerViewIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(self, AppDemo.class);
                intent.putExtra(AppDemo.FORCE_VIEW, true);
                startActivity(intent);
//                self.finish();
            }
        });
        RippleBgUti.setFlatRippleBackground(drawerNotes, this);
        RippleBgUti.setFlatRippleBackground(drawerSettings, this);
        RippleBgUti.setFlatRippleBackground(drawerHelp, this);
        RippleBgUti.setFlatRippleBackground(drawerAbout, this);
        RippleBgUti.setFlatRippleBackground(drawerViewIntro, this);
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

    private void applyThemeColor(){
        String previousSelectedColor = PrefUti.getStringPreference(PrefUti.THEME_COLOR, this);
        if(previousSelectedColor!=null){
            themeColor = Color.parseColor(previousSelectedColor);
        }
        else{
            themeColor = this.getResources().getColor(R.color.color_primary);
        }
        toolbar.setBackgroundColor(themeColor);
    }

    public void changeColor(int color){
        toolbar.setBackgroundColor(color);
    }

    public int getThemeColor(){
        return themeColor;
    }

    public void switchToNotesFragment(){
        if (!(fragment instanceof NotesFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
            fragment = new NotesFragment();
            FragmentTransaction fragmentTransactionNew = fragmentManager.beginTransaction();
            fragmentTransactionNew.add(R.id.home_fragment_place_holder, fragment);
            fragmentTransactionNew.commit();
        }
    }

    public void startWriting(View view){
        Intent intent = new Intent(this, SinglePageActivity.class);
        intent.putExtra(SinglePageActivity.WRITE_EDIT_INTENT, SinglePageActivity.START_WRITING);
        startActivity(intent);
    }

    public void showRateDialog(View view){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setOwnerActivity(this);
        sweetAlertDialog.setTitleText(this.getString(R.string.about_dialog_rate))
                .setConfirmText(this.getString(R.string.dialog_yes))
                .setCancelText(this.getString(R.string.dialog_no))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
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

    public void startEditing(int editPageId,String title){
        Intent intent = new Intent(this, SinglePageActivity.class);
        intent.putExtra(SinglePageActivity.WRITE_EDIT_INTENT, SinglePageActivity.START_EDITING);
        intent.putExtra(SinglePageActivity.EDIT_PAGE_ID, editPageId);
        intent.putExtra(SinglePageActivity.NOTE_TITLE,title);
        startActivity(intent);
    }

    public void reload() {

        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    public void closeDrawer(){
        drawerLayout.closeDrawer(llDrawer);
    }

    private void setTitle(String title){

        getSupportActionBar().setTitle(title);
    }

}