package com.lyk.immersivenote.datamodel;

import android.content.Context;
import android.graphics.Bitmap;

import com.lyk.immersivenote.notepad.CursorHolder;

/**
 * Created by John on 2015/8/26.
 */
public class SignatureViewModel {
    private Context context;
    private int type;
    private int lineNum;
    private CursorHolder cursorHolder;
    private int lineHeight;
    private int pageNum;
    private Bitmap image;

    public SignatureViewModel(Context context,int type, int lineNum,int pageNum,CursorHolder cursorHolder,int lineHeight){
        this.context = context;
        this.type = type;
        this.lineNum = lineNum;
        this.pageNum = pageNum;
        this.cursorHolder = cursorHolder;
        this.lineHeight = lineHeight;
    }

    public SignatureViewModel(Context context,int type, int lineNum,int pageNum,CursorHolder cursorHolder,Bitmap bitmap){
        this.context = context;
        this.type = type;
        this.lineNum = lineNum;
        this.pageNum = pageNum;
        this.cursorHolder = cursorHolder;
        this.image = bitmap;
    }

    public Context getContext() {
        return context;
    }

    public int getType() {
        return type;
    }

    public int getLineNum() {
        return lineNum;
    }

    public int getPageNum(){
        return pageNum;
    }

    public CursorHolder getCursorHolder() {
        return cursorHolder;
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public Bitmap getImage() {
        return image;
    }
}
