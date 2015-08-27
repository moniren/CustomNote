package com.lyk.immersivenote.notepad;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lyk.immersivenote.datamodel.SignatureViewModel;

public class SignatureView extends ImageView{
	public static int SPACE = 0;
	public static int IMAGE = 1;

	private Context context;
	private int lineHeight;
	private int type;
	private int lineNum;
	private int pageNum;
	private CursorHolder cursorHolder;
	
	private int posInLine = 0;

	private int viewWidth;

	private Bitmap image;

	//space
	public SignatureView(Context context, int lineHeight, CursorHolder cursorHolder, int pageNum) {
		super(context);
		this.context = context;
		this.lineHeight = lineHeight;
		this.cursorHolder = cursorHolder;
		this.pageNum = pageNum;
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				lineHeight/2, lineHeight);
		this.setLayoutParams(layoutParams);
		this.setBackground(null);
//		this.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				updateCursorPosition();
//			}
//		});
		this.viewWidth = lineHeight/2;
		this.type = SPACE;
	}

	// the normal image
	public SignatureView(Context context, Bitmap image, CursorHolder cursorHolder,int pageNum) {
		super(context);
		this.context = context;
		this.cursorHolder = cursorHolder;
		this.pageNum = pageNum;
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

	//build from the data model
	public SignatureView(SignatureViewModel signatureViewModel){
		super(signatureViewModel.getContext());
		this.setLineNum(signatureViewModel.getLineNum());
		this.setType(signatureViewModel.getType());
		this.setPageNum(signatureViewModel.getPageNum());
		LinearLayout.LayoutParams layoutParams = null;
		if(signatureViewModel.getType()==SPACE){
			layoutParams = new LinearLayout.LayoutParams(
					signatureViewModel.getLineHeight()/2, signatureViewModel.getLineHeight());
			this.viewWidth = signatureViewModel.getLineHeight()/2;
		}
		else if (signatureViewModel.getType() == IMAGE){
			layoutParams = new LinearLayout.LayoutParams(
					signatureViewModel.getImage().getWidth(), signatureViewModel.getImage().getHeight());
			this.setImageBitmap(signatureViewModel.getImage());
			this.image = signatureViewModel.getImage();
			this.viewWidth = image.getWidth();
		}
		this.setLayoutParams(layoutParams);
		this.setBackground(null);
	}

	public void updateCursorPosition(){
//		int[] location = new int[2];
//		getLocationOnScreen(location);
//		cursorHolder.setCusorPosition(location[0]+this.getWidth(), lineNum);
		cursorHolder.setCursorViewLeft(getLeft(), lineNum, getPosInLine());
	}

	public SignatureViewModel getDataModel(){
		SignatureViewModel signatureViewModel = null;
		if(type == SPACE)
			signatureViewModel = new SignatureViewModel(context,type,lineNum,pageNum,cursorHolder,lineHeight);
		else if (type == IMAGE)
			signatureViewModel = new SignatureViewModel(context,type,lineNum,pageNum,cursorHolder,image);
		return signatureViewModel;
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

	public void setPageNum(int pageNum){
		this.pageNum = pageNum;
	}

	public int getPageNum(){
		return this.pageNum;
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
