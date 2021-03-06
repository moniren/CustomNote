package com.lyk.immersivenote.notepad;

import com.lyk.immersivenote.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Stack;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SingleLine extends LinearLayout {

    private SinglePage mPage;

    private static final String TAG = "SingleLine";

    private SinglePageActivity singlePageActivity;

    private android.view.ViewGroup.LayoutParams params;

    private int heightPix = 0;

    private int widthPix = 0;

    private int lineNum = 0;

    private static Bitmap background = null;

    private boolean kanjiMode = true;


    public SingleLine(Context context, int width, int height, int lineNum, SinglePage mPage) {
        super(context);
        singlePageActivity = (SinglePageActivity) context;
        params = new android.view.ViewGroup.LayoutParams(width, height);
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setLayoutParams(params);
        this.setBackgroundResource(R.drawable.bg_single_line);
        widthPix = width;
        heightPix = height;
        createBackground();
//        this.setBackground(new BitmapDrawable(this.getContext().getResources(),background));
        this.lineNum = lineNum;
        this.mPage = mPage;
    }

    private void createBackground(){
        if(background == null){
            background = Bitmap.createBitmap(widthPix,heightPix,Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(background);
            Paint paint = new Paint();
            paint.setColor(this.getContext().getResources().getColor(R.color.single_line_line));
            canvas.drawARGB(0xFF, 0xF2, 0xF2, 0xF2);
            canvas.drawRect(0, 2 *heightPix/3,widthPix,2*heightPix/3+heightPix/20,paint);
        }
    }

    public void changeMode(boolean kanjiMode){
        if(kanjiMode && !this.kanjiMode){
            this.kanjiMode = kanjiMode;
            this.setBackgroundResource(R.drawable.bg_single_line);
        }
        else if (!kanjiMode && this.kanjiMode){
            this.kanjiMode = kanjiMode;
            this.setBackground(new BitmapDrawable(this.getContext().getResources(),background));
        }
    }


    public void removeSignature(int pos) {
        // in case the cursor is in front of the first item
        if (pos == -1) {
            // first line first item
            if (lineNum == 0) {
                return;
            } else {
                SingleLine lineAbove = ((SingleLine) ((ViewGroup) this.getParent()).getChildAt(lineNum - 1));
                lineAbove.removeSignature(lineAbove.getChildCount() - 1);
            }
        } else {
            for (int i = pos + 1; i < getChildCount(); i++) {
                SignatureView tempChild = (SignatureView) getChildAt(i);
                tempChild.setPosInLine(tempChild.getPosInLine() - 1);
            }
            int spaceAvailable = getSpaceLeft() + ((SignatureView) getChildAt(pos)).getViewWidth();
            removeViewAt(pos);
            if (lineNum + 1 < SinglePage.NUM_LINES) {
                ((SingleLine) ((ViewGroup) this.getParent()).getChildAt(lineNum + 1)).queueOutSigs(spaceAvailable);
            }
        }
    }

    public void queueOutSigs(int spaceAvailable) {
        int queueWidth = 0;
        int i;
        for (i = 0; i < getChildCount(); i++) {
            queueWidth = queueWidth + ((SignatureView) getChildAt(i)).getViewWidth();
            if (queueWidth > spaceAvailable) {
                // should keep this if we want to use the queueWidth;
//				queueWidth = queueWidth - ((SignatureView) getChildAt(i)).getViewWidth();
                break;
            }
        }
        i = i - 1;
        // if can queue out at least one item
        if (i > -1) {
            ArrayList<SignatureView> queueList = new ArrayList<>();
            for (int j = i + 1; j < getChildCount(); j++) {
                SignatureView tempChild = (SignatureView) getChildAt(j);
                tempChild.setPosInLine(tempChild.getPosInLine() - i - 1);
            }
            for (int j = 0; j <= i; j++) {
                SignatureView tempChild = (SignatureView) getChildAt(0);
                queueList.add(tempChild);
                removeViewAt(0);
            }
            ((SingleLine) ((ViewGroup) this.getParent()).getChildAt(lineNum - 1)).queueInSigs(queueList);
            if (lineNum + 1 < SinglePage.NUM_LINES) {
                ((SingleLine) ((ViewGroup) this.getParent()).getChildAt(lineNum + 1)).queueOutSigs(spaceAvailable);
            }
        }
    }

    public void queueInSigs(ArrayList<SignatureView> queueList) {
        int currentCount = getChildCount();
        for (int i = 0; i < queueList.size(); i++) {
            SignatureView tempChild = queueList.get(i);
            tempChild.setPosInLine(currentCount + i);
            tempChild.setLineNum(lineNum);
            addView(tempChild);
        }
    }

    //for loading the saved notes
    public void addSignature(SignatureView sig){
        sig.setPosInLine(getChildCount());
        addView(sig);
    }


    public void addSignature(SignatureView sig, int pos) {
        if (haveSpaceFor(sig.getViewWidth())) {
            sig.setPosInLine(pos);
            if (pos < getChildCount()) {
                for (int i = pos; i < getChildCount(); i++) {
                    SignatureView tempChild = (SignatureView) getChildAt(i);
                    tempChild.setPosInLine(tempChild.getPosInLine() + 1);
                }
            }
            sig.setPosInLine(pos);
            sig.setLineNum(lineNum);
            addView(sig, pos);
        } else {

            if (pos == getChildCount()) {
                Stack<SignatureView> popStack = new Stack<>();
                popStack.push(sig);
                if (lineNum + 1 < SinglePage.NUM_LINES) {
                    popOutSigs(popStack);
                } else {
                    new SweetAlertDialog(singlePageActivity, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(singlePageActivity.getString(R.string.dialog_add_new_page_title))
                            .setContentText(singlePageActivity.getString(R.string.dialog_current_page_full))
                            .setConfirmText(singlePageActivity.getString(R.string.dialog_yes))
                            .setCancelText(singlePageActivity.getString(R.string.dialog_no))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    singlePageActivity.addPage();
                                    sDialog.setTitleText(singlePageActivity.getString(R.string.dialog_added))
                                            .showCancelButton(false)
                                            .setConfirmText(singlePageActivity.getString(R.string.dialog_okay))
                                            .setConfirmClickListener(null)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                }
                            }).showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    singlePageActivity.resetCursor();
                                    sDialog.cancel();
                                }
                            }).show();
                }
            } else {
                int i = 0;
                int j = 0;
                Stack<SignatureView> popStack = new Stack<>();
                for (i = getChildCount() - 1; i > -1; i--) {
                    if (getSpaceLeft(i) > sig.getViewWidth()) {
                        break;
                    }
                    popStack.push((SignatureView) getChildAt(i));
                }
                for (j = pos; j <= i; j++) {
                    SignatureView tempChild = (SignatureView) getChildAt(j);
                    tempChild.setPosInLine(tempChild.getPosInLine() + 1);
                }
                if (lineNum + 1 < SinglePage.NUM_LINES) {
                    popOutSigs(popStack);
                    sig.setPosInLine(pos);
                    sig.setLineNum(lineNum);
                    addView(sig, pos);
                } else {
//                    mPage.setPageFull(true);
                    new SweetAlertDialog(singlePageActivity, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(singlePageActivity.getString(R.string.dialog_add_new_page_title))
                            .setContentText(singlePageActivity.getString(R.string.dialog_current_page_full))
                            .setConfirmText(singlePageActivity.getString(R.string.dialog_yes))
                            .setCancelText(singlePageActivity.getString(R.string.dialog_no))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    singlePageActivity.addPage();
                                    sDialog.setTitleText(singlePageActivity.getString(R.string.dialog_added))
                                            .showCancelButton(false)
                                            .setConfirmText(singlePageActivity.getString(R.string.dialog_okay))
                                            .setConfirmClickListener(null)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                }
                            }).showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    singlePageActivity.resetCursor();
                                    sDialog.cancel();
                                }
                            }).show();
                }

            }

        }


    }


    private void popOutSigs(Stack<SignatureView> popStack) {
        int stackWidth = 0;
        ArrayList<SignatureView> pushList = new ArrayList<>();
        for (int i = 0; i < popStack.size(); i++) {
            SignatureView tempSig = popStack.pop();
            stackWidth = stackWidth + tempSig.getViewWidth();
            pushList.add(tempSig);
            this.removeView(tempSig);
        }
        ((SingleLine) ((ViewGroup) this.getParent()).getChildAt(lineNum + 1)).pushInSigs(pushList, stackWidth);
    }

    public void pushInSigs(ArrayList<SignatureView> pushList, int listWidth) {
        if (haveSpaceFor(listWidth)) {
            for (int i = 0; i < getChildCount(); i++) {
                SignatureView tempChild = (SignatureView) getChildAt(i);
                tempChild.setPosInLine(i + pushList.size());
            }
        } else {
            int i;
            int j;
            Stack<SignatureView> popStack = new Stack<>();
            for (i = getChildCount() - 1; i > -1; i--) {
                if (getSpaceLeft(i) > listWidth) {
                    break;
                }
                popStack.push((SignatureView) getChildAt(i));
            }
            for (j = 0; j <= i; j++) {
                SignatureView tempChild = (SignatureView) getChildAt(i);
                tempChild.setPosInLine(tempChild.getPosInLine() + 1);
            }
            if (lineNum + 1 < SinglePage.NUM_LINES) {
                popOutSigs(popStack);
            } else {
//                mPage.setPageFull(true);
                new SweetAlertDialog(singlePageActivity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(singlePageActivity.getString(R.string.dialog_add_new_page_title))
                        .setContentText(singlePageActivity.getString(R.string.dialog_current_page_full))
                        .setConfirmText(singlePageActivity.getString(R.string.dialog_yes))
                        .setCancelText(singlePageActivity.getString(R.string.dialog_no))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                singlePageActivity.addPage();
                                sDialog.setTitleText(singlePageActivity.getString(R.string.dialog_added))
                                        .showCancelButton(false)
                                        .setConfirmText(singlePageActivity.getString(R.string.dialog_okay))
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        }).showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                singlePageActivity.resetCursor();
                                sDialog.cancel();
                            }
                        }).show();
            }
        }

        for (int i = 0; i < pushList.size(); i++) {
            SignatureView tempChild = pushList.get(i);
            tempChild.setPosInLine(i);
            tempChild.setLineNum(lineNum);
            ((SingleLine) ((ViewGroup) this.getParent()).getChildAt(lineNum)).addView(tempChild, i);
        }
    }

    public boolean haveSpaceFor(int itemWidthPix) {

        int currentWidth = 0;
        int numChildren = this.getChildCount();
        for (int i = 0; i < numChildren; i++) {
            currentWidth = currentWidth + ((SignatureView) this.getChildAt(i)).getViewWidth();
        }
        return currentWidth + itemWidthPix < widthPix;
    }

    public void setHeight(int heightPix) {
        int heightDip = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX, heightPix, getResources()
                        .getDisplayMetrics());
        params.height = heightDip;
    }

    public void setWidth(int widthPix) {
        int widthDip = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX, widthPix, getResources()
                        .getDisplayMetrics());
        params.width = widthDip;
    }

    public int getSpaceLeft() {
        int currentWidth = 0;
        int numChildren = this.getChildCount();
        for (int i = 0; i < numChildren; i++) {
            currentWidth = currentWidth + ((SignatureView) this.getChildAt(i)).getViewWidth();
        }

        return (widthPix - currentWidth);
    }

    // get the space at the right of a child at pos
    public int getSpaceLeft(int pos) {
        int currentWidth = 0;
        for (int i = 0; i <= pos; i++) {
            currentWidth = currentWidth + ((SignatureView) this.getChildAt(i)).getViewWidth();
        }

        return (widthPix - currentWidth);
    }

    public int getLineNum() {
        return lineNum;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        resetCursor();
        return true;
    }

    private void resetCursor(){
        singlePageActivity.resetCursor(this);
    }

}
