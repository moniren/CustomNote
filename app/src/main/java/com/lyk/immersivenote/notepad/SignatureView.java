package com.lyk.immersivenote.notepad;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SignatureView extends ImageView{
	public static int SPACE = 0;
	public static int IMAGE = 1;

	private int type;
	private int lineNum;
	private CursorHolder cursorHolder;
	
	private int posInLine = 0;

	private int viewWidth;

	private Bitmap image;

	//space
	public SignatureView(Context context, int lineHeight, CursorHolder cursorHolder) {
		super(context);
		this.cursorHolder = cursorHolder;
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				lineHeight/2, lineHeight);
		this.setLayoutParams(layoutParams);
		this.setBackground(null);
		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateCursorPosition();
			}
		});
		this.viewWidth = lineHeight/2;
		this.type = SPACE;
	}
	
	public SignatureView(Context context, Bitmap image, CursorHolder cursorHolder) {
		super(context);
		this.cursorHolder = cursorHolder;
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				image.getWidth(), image.getHeight());
		this.setLayoutParams(layoutParams);
		this.setBackground(null);
		this.setImageBitmap(image);
		this.image = image;
//		this.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				updateCursorPosition();
//			}
//		});
		this.viewWidth = image.getWidth();
		this.type=IMAGE;
	}
	
	public void updateCursorPosition(){
//		int[] location = new int[2];
//		getLocationOnScreen(location);
//		cursorHolder.setCusorPosition(location[0]+this.getWidth(), lineNum);
		cursorHolder.setCursorViewLeft(getLeft(), lineNum, getPosInLine());
	}

	public Bitmap getImage(){
		return this.image;
	}
	
	public void setPosInLine(int pos){
		this.posInLine = pos;
	}
	
	public int getPosInLine(){
		return this.posInLine;
	}

	public int getViewWidth(){ return this.viewWidth;}


	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}

	public int getLineNum() {
		return this.lineNum;
	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("SignatureView", "onTouch");
		performClick();
		updateCursorPosition();
		return true;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
