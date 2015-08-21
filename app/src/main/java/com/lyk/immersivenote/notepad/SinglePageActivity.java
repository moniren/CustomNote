package com.lyk.immersivenote.notepad;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
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
import android.view.MotionEvent;
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
import com.lyk.immersivenote.settings.PrefManager;
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_pad);
        initCustomActionBar();

        singlePageBase = (FrameLayout) findViewById(R.id.singlePageBase);

        // this should be put into the base activity
        if (PrefManager.getIntPreference(PrefManager.NOTE_PAGE_WIDTH, this) == -1) {
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
            PrefManager.setIntPreference(PrefManager.NOTE_LINE_HEIGHT,
                    lineHeight, this);
            PrefManager.setIntPreference(PrefManager.NOTE_PAGE_HEIGHT,
                    pageHeight, this);
            PrefManager.setIntPreference(PrefManager.NOTE_PAGE_WIDTH,
                    pageWidth, this);
            PrefManager.setIntPreference(PrefManager.SCREEN_HEIGHT,
                    screenHeight, this);
            Log.d(TAG, "Setting---lineHeight: " + lineHeight + " pageHeight: "
                    + pageHeight + " pageWidth: " + pageWidth);
        }

        lineHeight = PrefManager.getIntPreference(PrefManager.NOTE_LINE_HEIGHT, this);
        lineWidth = PrefManager.getIntPreference(PrefManager.NOTE_PAGE_WIDTH, this);

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
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }
        });


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
        SinglePage tempPage = new SinglePage(this);
        tempPage.setPageNumber(0);
        pages.add(tempPage);
        mPagerAdapter.addView(tempPage, mPager, circleIndicator);
        mPager.setAdapter(mPagerAdapter);
//        circleIndicator.setViewPager(mPager);
    }

    @Override
    public void onBackPressed() {

        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Save the note?")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        ContentValues values = new ContentValues();
                        values.put(MainTable.COLUMN_TITLE, "no title");
                        values.put(MainTable.COLUMN_BACKGROUND, 0);
                        values.put(MainTable.COLUMN_ENCRYPTED, "false");
                        DateFormat dateFormat = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss");
                        String currentTime = dateFormat.format(Calendar.getInstance().getTime());
                        values.put(MainTable.COLUMN_TIME, currentTime);
                        MainDataSource.insertNote(sDialog.getContext().getApplicationContext(), values);
                        sDialog.setTitleText("Saved!")
                                .setContentText("The note is saved!")
                                .setConfirmText("OK")
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
                        .setTitleText("Sure to delete the page?")
                        .setContentText("Won't be able to recover this page!")
                        .setConfirmText("Yes")
                        .setCancelText("No")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                //must set the page numbers of the pages behind the deleting page
                                for (int i = mPager.getCurrentItem() + 1; i < pages.size(); i++) {
                                    pages.get(i).setPageNumber(i - 1);
                                }
                                pages.remove(mPager.getCurrentItem());
                                mPagerAdapter.removeView(mPager, mPager.getCurrentItem(), circleIndicator);
                                sDialog.setTitleText("Deleted!")
                                        .setContentText("The page is deleted!")
                                        .setConfirmText("OK")
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


    private class SpaceBtnClickListener implements OnClickListener {
        private SinglePageActivity context;
        private SignatureView spaceSig;

        public SpaceBtnClickListener(SinglePageActivity context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            spaceSig = new SignatureView(context, lineHeight, cursorHolder);
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
        new TaskAddSig(this, image).execute();
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
            Log.d(TAG, "taskAddSig running");
            Log.d(TAG,
                    "imageW: " + image.getWidth() + " imageH: "
                            + image.getHeight());

            image = Bitmap.createScaledBitmap(image, image.getWidth()
                    * lineHeight / image.getHeight(), lineHeight, true);
//            context.updateLastAvailable(image.getWidth());

            Log.d(TAG,
                    "tempSigW: " + image.getWidth() + " tempSigH: "
                            + image.getHeight());

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            tempSig = new SignatureView(context, image,
                    cursorHolder);
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
        for (int i = mPager.getCurrentItem() + 1; i < pages.size(); i++) {
            pages.get(i).setPageNumber(i + 1);
        }
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
