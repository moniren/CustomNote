package com.lyk.immersivenote.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lyk.immersivenote.R;
import com.lyk.immersivenote.database.MainDataSource;
import com.lyk.immersivenote.database.MainTable;
import com.lyk.immersivenote.database.MyDatabaseHelper;
import com.lyk.immersivenote.database.NoteDataSource;
import com.lyk.immersivenote.database.NoteTable;
import com.lyk.immersivenote.datamodel.SignatureViewModel;
import com.lyk.immersivenote.utils.PrefUti;
import com.lyk.immersivenote.utils.Base64Uti;
import com.lyk.immersivenote.utils.DBUti;
import com.rey.material.widget.ImageButton;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.relex.circleindicator.CircleIndicator;

public class SinglePageActivity extends FragmentActivity implements
        PropertyChangeListener {

    private static final String TAG = "NotePadActivity";

    private boolean onePageOnly = true;
    private Animation fadeOutAddPageBtn;
    private Animation fadeOutRemovePageBtn;
    private Animation fadeOutSpaceBtn;
    private Animation fadeOutBackSpaceBtn;
    private Animation fadeOutViewWriteBtn;
    private Animation fadeOutIndicator;

    private Animation fadeInAddPageBtn;
    private Animation fadeInRemovePageBtn;
    private Animation fadeInSpaceBtn;
    private Animation fadeInBackSpaceBtn;
    private Animation fadeInViewWriteBtn;
    private Animation fadeInIndicator;

    private ImageButton addPageBtn;
    private ImageButton removePageBtn;
    private ImageButton spaceBtn;
    private ImageButton backspaceBtn;
    private ImageButton viewWriteBtn;


    private CircleIndicator circleIndicator;
    private boolean viewing = false;

    private FrameLayout singlePageBase;

    private SignatureHolder sigHolder;
    private SignatureCapture sigCapture;
    private CursorHolder cursorHolder;

    //    private int lastAvailable = 0;
    private int lineHeight = 0;
    private int lineWidth = 0;

    private SinglePageAdapter mPagerAdapter;
    private ViewPager mPager;
    private ArrayList<SinglePage> pages;

    private static MyDatabaseHelper myDBHelper = null;
    private static SQLiteDatabase database = null;

    public static String WRITE_EDIT_INTENT = "write_edit_intent";
    public static int START_WRITING = 0;
    public static int START_EDITING = 1;
    public static String EDIT_PAGE_ID = "edit_page_id";
    //use the sign of this id as a flag
    private int noteId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set the noteId by default as -1
        noteId = -1;

        if(myDBHelper == null){
            myDBHelper = new MyDatabaseHelper(this.getApplication());
            database = myDBHelper.getDB();
        }
        setContentView(R.layout.activity_note_pad);
        initCustomActionBar();

        singlePageBase = (FrameLayout) findViewById(R.id.singlePageBase);

        // this should be put into the base activity
        if (PrefUti.getIntPreference(PrefUti.NOTE_PAGE_WIDTH, this) == -1) {
            // get screen info
            WindowManager wm = (WindowManager) this
                    .getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            // get the actionBarHeight
            TypedValue tv = new TypedValue();
            this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv,
                    true);
            int actionBarHeight = getResources().getDimensionPixelSize(
                    tv.resourceId);
            // get the statusBarHeight
            int statusBarHeight = 0;
            int resourceId = getResources().getIdentifier("status_bar_height",
                    "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(
                        resourceId);
            }
            // use them to config the prefs
            int pageWidth;
            int pageHeight;
            int lineHeight;
            int screenHeight = metrics.heightPixels;
//            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                pageWidth = metrics.heightPixels;
//                pageHeight = metrics.widthPixels - actionBarHeight
//                        - statusBarHeight;
//                lineHeight = pageHeight / SinglePageFragment.NUM_LINES;
//
//            } else {
            pageWidth = metrics.widthPixels;
            // "96" is the dp of the height of actionbar and the circle indicator bar  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics())
            pageHeight = (int) (metrics.heightPixels - actionBarHeight
                    - statusBarHeight);
            lineHeight = pageHeight / SinglePage.NUM_LINES;
//            }
            PrefUti.setIntPreference(PrefUti.NOTE_LINE_HEIGHT,
                    lineHeight, this);
            PrefUti.setIntPreference(PrefUti.NOTE_PAGE_HEIGHT,
                    pageHeight, this);
            PrefUti.setIntPreference(PrefUti.NOTE_PAGE_WIDTH,
                    pageWidth, this);
            PrefUti.setIntPreference(PrefUti.SCREEN_HEIGHT,
                    screenHeight, this);
            Log.d(TAG, "Setting---lineHeight: " + lineHeight + " pageHeight: "
                    + pageHeight + " pageWidth: " + pageWidth);
        }

        lineHeight = PrefUti.getIntPreference(PrefUti.NOTE_LINE_HEIGHT, this);
        lineWidth = PrefUti.getIntPreference(PrefUti.NOTE_PAGE_WIDTH, this);

        // set the cursor layer
        cursorHolder = (CursorHolder) findViewById(R.id.cursorHolder);

        sigHolder = (SignatureHolder) findViewById(R.id.sigHolder);

        sigCapture = new SignatureCapture(this, null, lineHeight, sigHolder);
        sigCapture.addPropertyChangeListener(this);
        sigHolder.addView(sigCapture);

        singlePageBase.setDrawingCacheEnabled(true);

        fadeInAddPageBtn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInRemovePageBtn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInSpaceBtn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInBackSpaceBtn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInViewWriteBtn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInIndicator = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        fadeOutAddPageBtn = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOutRemovePageBtn = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOutViewWriteBtn = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOutSpaceBtn = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOutBackSpaceBtn = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOutIndicator = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        fadeInAddPageBtn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                addPageBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeInRemovePageBtn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                removePageBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeInBackSpaceBtn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                backspaceBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeInSpaceBtn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                spaceBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeInIndicator.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                circleIndicator.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        circleIndicator.startAnimation(fadeOutIndicator);
                    }
                }, 300);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeOutBackSpaceBtn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                backspaceBtn.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeOutRemovePageBtn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                removePageBtn.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeOutAddPageBtn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                addPageBtn.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeOutSpaceBtn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                spaceBtn.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeOutIndicator.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                circleIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeOutViewWriteBtn.setAnimationListener(new Animation.AnimationListener() {

            // Other callback methods omitted for clarity.

            public void onAnimationEnd(Animation animation) {

                // Modify the resource of the ImageButton
                viewWriteBtn.setImageResource(viewing ? R.drawable.ic_edit_white : R.drawable.ic_visibility_white);

                // Create the new Animation to apply to the ImageButton.
                viewWriteBtn.startAnimation(fadeInViewWriteBtn);

                viewing = !viewing;
                sigHolder.setVisibility(viewing ? View.GONE : View.VISIBLE);
                backspaceBtn.startAnimation(viewing ? fadeOutBackSpaceBtn : fadeInBackSpaceBtn);
                spaceBtn.startAnimation(viewing ? fadeOutSpaceBtn : fadeInSpaceBtn);
                addPageBtn.startAnimation(viewing ? fadeInAddPageBtn : fadeOutAddPageBtn);
                if (!onePageOnly) {
                    removePageBtn.startAnimation(viewing ? fadeInRemovePageBtn : fadeOutRemovePageBtn);
                } else if (viewing && removePageBtn.getVisibility() == View.VISIBLE) {
                    removePageBtn.startAnimation(fadeOutRemovePageBtn);
                }
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        initPages();


    }

    @Override
    public void onBackPressed() {
        save();
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // Here you can get the size!
        sigCapture.setBaseW(sigHolder.getWidth());
        sigCapture.setBaseH(sigHolder.getHeight());
        sigCapture.setSignatureAuxiliaryLayer((LinearLayout) findViewById(R.id.sig_auxi));

    }

    private void initCustomActionBar() {
        addPageBtn = (ImageButton) findViewById(R.id.actionbar_add_page);
        removePageBtn = (ImageButton) findViewById(R.id.actionbar_remove_page);
        spaceBtn = (ImageButton) findViewById(R.id.actionbar_space);
        backspaceBtn = (ImageButton) findViewById(R.id.actionbar_backspace);
        viewWriteBtn = (ImageButton) findViewById(R.id.actionbar_edit_view);

        addPageBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                addPage();
//                mPagerAdapter.addView(new SinglePage(view.getContext()),mPager.getCurrentItem()+1,mPager,circleIndicator);
//                mPagerAdapter.addPage(SinglePageFragment.newInstance());
            }
        });
        removePageBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getString(R.string.dialog_delete_page_title))
                        .setContentText(getString(R.string.dialog_delete_page_label))
                        .setConfirmText(getString(R.string.dialog_yes))
                        .setCancelText(getString(R.string.dialog_no))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                //must set the page numbers of the pages behind the deleting page
                                for (int i = mPager.getCurrentItem() + 1; i < pages.size(); i++) {
//                                    pages.get(i).setPageNumber(i - 1);
                                }
                                pages.remove(mPager.getCurrentItem());
                                mPagerAdapter.removeView(mPager, mPager.getCurrentItem(), circleIndicator);
                                sDialog.setTitleText(getString(R.string.dialog_deleted))
                                        .setConfirmText(getString(R.string.dialog_okay))
                                        .showContentText(false)
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        }).showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        }).show();
            }
        });
        spaceBtn.setOnClickListener(new SpaceBtnClickListener(this));
        backspaceBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                SingleLine tempLine = (SingleLine) mPagerAdapter.getmCurrentPage().getChildAt(cursorHolder.getLineNum());
                if (cursorHolder.getLineNum() > 0 && cursorHolder.getCursorPos() == 0) {
                    tempLine = (SingleLine) mPagerAdapter.getmCurrentPage().getChildAt(cursorHolder.getLineNum() - 1);
                    // wait for "\n" function
                    if (tempLine.getChildCount() > 0) {
                        ((SignatureView) tempLine.getChildAt(tempLine.getChildCount() - 1)).updateCursorPosition();
                        tempLine.removeSignature(cursorHolder.getCursorPos());
                    }
                } else if (cursorHolder.getCursorPos() > 0) {
                    ((SignatureView) tempLine.getChildAt(cursorHolder.getCursorPos() - 1)).updateCursorPosition();
                    //after moving the cursor position, no need to -1
                    tempLine.removeSignature(cursorHolder.getCursorPos());
                }
            }
        });
        viewWriteBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                viewWriteBtn.startAnimation(fadeOutViewWriteBtn);
            }
        });
    }

    private void initPages(){


        circleIndicator = (CircleIndicator) findViewById(R.id.indicator_pager);
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.notesPager);
        mPagerAdapter = new SinglePageAdapter(this);
        //this line is important for keepin the fragment from garbage collected
//        mPager.setOffscreenPageLimit(5 - 1); //NUM_ITEMS-1
//        mPager.setPageTransformer(true, new DepthPageTransformer());
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                showIndicator();
            }

            public void onPageSelected(int position) {
                resetCursor();
            }
        });
        //initialize the arraylist keeping track of the pages
        pages = new ArrayList<>();
        Intent intent = getIntent();
        if(intent.getIntExtra(WRITE_EDIT_INTENT,0) == START_WRITING){
            SinglePage tempPage = new SinglePage(this);
//        tempPage.setPageNumber(0);
            pages.add(tempPage);
            mPagerAdapter.addView(tempPage, mPager, circleIndicator);
            resetCursor();

//        circleIndicator.setViewPager(mPager);
        }
        else if (intent.getIntExtra(WRITE_EDIT_INTENT,0) == START_EDITING){
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.color_primary));
            sweetAlertDialog.setTitleText(getString(R.string.dialog_loading));
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.show();

            //set the noteId
            noteId = intent.getIntExtra(EDIT_PAGE_ID, 1);


            new TaskLoadNote(this,sweetAlertDialog,noteId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        }
        mPager.setAdapter(mPagerAdapter);

    }

    private void save(){
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.dialog_save_note_title))
                .setConfirmText(getString(R.string.dialog_yes))
                .setCancelText(getString(R.string.dialog_no))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sDialog) {
                        //show a progress dialog
                        sDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        sDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.color_primary));
                        sDialog.setTitleText(getString(R.string.dialog_saving));
                        sDialog.setCancelable(false);
                        sDialog.showContentText(false);
                        sDialog.showCancelButton(false);

                        //set up the models for the async task
                        ArrayList<SignatureViewModel> signatureViewModels = new ArrayList<>();
                        int pageNumber = pages.size();
                        for(int i = 0;i<pageNumber;i++){
                            SinglePage tempPage = pages.get(i);
                            for(int j = 0;j<SinglePage.NUM_LINES;j++){
                                SingleLine tempLine = tempPage.getSingleLines().get(j);
                                int viewNumber = tempLine.getChildCount();
                                for(int k = 0;k<viewNumber;k++){
                                    SignatureView tempSig = (SignatureView) tempLine.getChildAt(k);
                                    signatureViewModels.add(tempSig.getDataModel());
                                }
                            }
                        }
                        new TaskSaveNote(sDialog,signatureViewModels).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    }
                }).showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        sDialog.getOwnerActivity().finish();
                    }
                });
        dialog.setOwnerActivity(this);
        dialog.show();

    }


    private class SpaceBtnClickListener implements OnClickListener {
        private SinglePageActivity context;
        private SignatureView spaceSig;

        public SpaceBtnClickListener(SinglePageActivity context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            spaceSig = new SignatureView(context, lineHeight, cursorHolder, mPager.getCurrentItem());
            spaceSig.setType(SignatureView.SPACE);
            SingleLine currentLine = ((SingleLine) mPagerAdapter.getmCurrentPage()
                    .getChildAt(cursorHolder.getLineNum()));
            currentLine.addSignature(spaceSig, cursorHolder.getCursorPos());

            // int viewX = lineWidth - currentLine.getSpaceLeft();
            int viewX = lineWidth
                    - currentLine.getSpaceLeft(spaceSig.getPosInLine());

            Log.d(TAG, "current tempSig lineNum: " + spaceSig.getLineNum());
            cursorHolder.setCusorViewRight(viewX, spaceSig.getLineNum(),
                    spaceSig.getPosInLine() + 1);
            cursorHolder.invalidate();
        }

    }

//    private void updateLastAvailable(int itemWidth) {
//        int i = 1;
//        for (i = lastAvailable; i < NUM_LINES; i++) {
//            if (((SingleLine) linesBase.getChildAt(i)).haveSpaceFor(itemWidth)) {
//                break;
//            }
//        }
//        if (lastAvailable != i) {
//            lastAvailable = i;
//        }
//
//    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        Bitmap image = (Bitmap) event.getNewValue();
        new TaskAddSig(this, image).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class TaskSaveNote extends AsyncTask<Void, Void, Void> {

        private SweetAlertDialog sDialog;
        private ArrayList<SignatureViewModel> signatureViewModels;

        public TaskSaveNote(SweetAlertDialog sDialog, ArrayList<SignatureViewModel> signatureViewModels){
            this.sDialog = sDialog;
            this.signatureViewModels = signatureViewModels;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //saving
            ContentValues values = new ContentValues();
            values.put(MainTable.COLUMN_TITLE, "no title");
            values.put(MainTable.COLUMN_BACKGROUND, 0);
            values.put(MainTable.COLUMN_ENCRYPTED, "false");
            DateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            String currentTime = dateFormat.format(Calendar.getInstance().getTime());
            values.put(MainTable.COLUMN_TIME, currentTime);
            String tableName;
            if(noteId > 0){
                tableName = DBUti.getTableNameById(noteId);
                MainDataSource.updateNote(sDialog.getContext().getApplicationContext(),noteId,values);
                NoteDataSource.removeNoteTable(tableName);
            }
            else{
                tableName = DBUti.getTableNameById(MainDataSource.insertNote(sDialog.getContext().getApplicationContext(), values));
            }
            //reset noteId (actually no need, just a reminder here in case of future change)
            noteId = -1;

            NoteDataSource.createNoteTable(tableName);
            int viewNumber = signatureViewModels.size();
            for(int i = 0;i<viewNumber;i++){
                        values = new ContentValues();
                        values.put(NoteTable.COLUMN_LINE_NO,signatureViewModels.get(i).getLineNum());
                        values.put(NoteTable.COLUMN_PAGE_NO,signatureViewModels.get(i).getPageNum());
                        values.put(NoteTable.COLUMN_TYPE,signatureViewModels.get(i).getType());
                        if(signatureViewModels.get(i).getType() == SignatureView.IMAGE){
                            values.put(NoteTable.COLUMN_BITMAP, Base64Uti.encodeTobase64(signatureViewModels.get(i).getImage()));
                        }
                        else if (signatureViewModels.get(i).getType() == SignatureView.SPACE){
                            values.put(NoteTable.COLUMN_BITMAP, "null");
                        }
                        NoteDataSource.insertNoteTable(sDialog.getContext().getApplicationContext(), values, tableName);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //save completed
            sDialog.setTitleText(sDialog.getContext().getString(R.string.dialog_saved))
                    .setConfirmText(sDialog.getContext().getString(R.string.dialog_okay))
                    .showCancelButton(false)
                    .setCancelClickListener(null)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                            sDialog.getOwnerActivity().finish();
                        }
                    })
                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        }


    }

    private class TaskLoadNote extends AsyncTask<Void, Void, Void> {

        private SinglePageActivity context = null;
        private SweetAlertDialog sDialog;
        private SignatureView tempSig;
        private int noteId;
        private int pageNumber;
        private ArrayList<ArrayList<SignatureViewModel>> signatureViewModels = null;

        public TaskLoadNote(SinglePageActivity context, SweetAlertDialog sDialog, int noteId) {
            Log.d(TAG, "taskAddSig created");
            this.context = context;
            this.sDialog = sDialog;
            this.noteId = noteId;
            signatureViewModels = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String tableName =  DBUti.getTableNameById(noteId);
            //set up the pages
            pageNumber = NoteDataSource.getMaxPageNumber(tableName);
            for(int i=0;i<=pageNumber;i++){
                ArrayList<SignatureViewModel> tempSignatureViewModels = NoteDataSource.getSignaturesForPage(tableName,i,context,cursorHolder,lineHeight);
                signatureViewModels.add(tempSignatureViewModels);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            for(int i=0;i<=pageNumber;i++){
                SinglePage tempPage = new SinglePage(context);
                pages.add(tempPage);
                mPagerAdapter.addView(tempPage, mPager, circleIndicator);
                int pageViewSize = signatureViewModels.get(i).size();
                for(int j=0;j<pageViewSize;j++){
                    if(signatureViewModels.get(i).get(j) == null){
                        Log.d(TAG,"The signatureViewModel is null");
                    }
                    if(signatureViewModels.get(i).get(j).getContext() == null){
                        Log.d(TAG,"The signatureViewModel's context is null");
                    }
                    tempSig = new SignatureView(signatureViewModels.get(i).get(j));
                    tempPage.getSingleLines().get(tempSig.getLineNum()).addSignature(tempSig);
                }
            }
            sDialog.dismiss();
            resetCursor();
        }
    }

    private class TaskAddSig extends AsyncTask<Void, Void, Void> {

        private SinglePageActivity context = null;
        private Bitmap image = null;
        private SignatureView tempSig;

        public TaskAddSig(SinglePageActivity context, Bitmap image) {
            Log.d(TAG, "taskAddSig created");
            this.context = context;
            this.image = image;
        }

        @Override
        protected Void doInBackground(Void... params) {
//            Log.d(TAG, "taskAddSig running");
//            Log.d(TAG,
//                    "imageW: " + image.getWidth() + " imageH: "
//                            + image.getHeight());
            image = Bitmap.createScaledBitmap(image, image.getWidth()
                    * lineHeight / image.getHeight(), lineHeight, true);
//            context.updateLastAvailable(image.getWidth());

//            Log.d(TAG,
//                    "tempSigW: " + image.getWidth() + " tempSigH: "
//                            + image.getHeight());

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            tempSig = new SignatureView(context, image,
                    cursorHolder,mPager.getCurrentItem());
            tempSig.setType(SignatureView.IMAGE);
//            if (lastAvailable >= NUM_LINES) {
//                Toast toast = Toast.makeText(context, "Notepad is full!",
//                        Toast.LENGTH_SHORT);
//                toast.show();
//            } else {
//            if (!mPagerAdapter.getmCurrentPage().isPageFull()) {
            SingleLine currentLine = ((SingleLine) mPagerAdapter.getmCurrentPage()
                    .getChildAt(cursorHolder.getLineNum()));
            currentLine.addSignature(tempSig, cursorHolder.getCursorPos());
            int viewX = lineWidth
                    - currentLine.getSpaceLeft(tempSig.getPosInLine());

            Log.d(TAG, "current tempSig lineNum: " + tempSig.getLineNum());
            cursorHolder.setCusorViewRight(viewX,
                    tempSig.getLineNum(), tempSig.getPosInLine() + 1);
            cursorHolder.invalidate();
//            }
//                Toast toastText = Toast.makeText(context,
//                        "Signature capture successful!", Toast.LENGTH_SHORT);
//                toastText.show();
        }
    }

    public void resetCursor() {
        if (mPagerAdapter.getmCurrentPage() != null && mPagerAdapter.getmCurrentPage() != null) {
            int i;
            for (i = mPagerAdapter.getmCurrentPage().getChildCount() - 1; i >= 0; i--) {
                if (((SingleLine) mPagerAdapter.getmCurrentPage().getChildAt(i)).getChildCount() > 0) {
                    break;
                }
            }
            //in case it's the first line
            i = Math.max(0, i);
            SingleLine currentLine = ((SingleLine) mPagerAdapter.getmCurrentPage()
                    .getChildAt(i));
            int viewX = lineWidth - currentLine.getSpaceLeft();
            cursorHolder.setCusorViewRight(viewX, i,
                    currentLine.getChildCount());
            cursorHolder.invalidate();
        }
    }

    public void resetCursor(SingleLine currentLine) {
        int viewX = lineWidth - currentLine.getSpaceLeft();
        cursorHolder.setCusorViewRight(viewX, currentLine.getLineNum(),
                currentLine.getChildCount());
        cursorHolder.invalidate();
    }

    public void addPage() {
        SinglePage tempPage = new SinglePage(this);
        //change the page numbers of the pages after the adding page
//        for (int i = mPager.getCurrentItem() + 1; i < pages.size(); i++) {
//            pages.get(i).setPageNumber(i + 1);
//        }
        pages.add(mPager.getCurrentItem() + 1, tempPage);
        mPagerAdapter.addView(tempPage, mPager.getCurrentItem() + 1, mPager, circleIndicator);
    }

    public void showIndicator() {
        Log.d(TAG, "Try to show indicator");
        if (viewing) {
            Log.d(TAG, "should show indicator");
            circleIndicator.startAnimation(fadeInIndicator);
        }
    }

    public void hideIndicator() {
        if (removePageBtn.getVisibility() == View.VISIBLE && viewing)
            circleIndicator.startAnimation(fadeOutIndicator);
    }

    public void showRemoveBtn() {
        if (removePageBtn.getVisibility() != View.VISIBLE)
            removePageBtn.startAnimation(fadeInRemovePageBtn);
    }

    public void hideRemoveBtn() {
        if (removePageBtn.getVisibility() == View.VISIBLE)
            removePageBtn.startAnimation(fadeOutRemovePageBtn);
    }

    public boolean isOnePageOnly() {
        return onePageOnly;
    }

    public boolean isViewing() {
        return viewing;
    }

    public void setOnePageOnly(boolean onePageOnly) {
        this.onePageOnly = onePageOnly;
    }
}
