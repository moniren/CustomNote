package com.lyk.immersivenote.notepad;

import com.lyk.immersivenote.R;
import com.lyk.immersivenote.settings.PrefManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CursorHolder extends View {

	private SinglePageActivity singlePageActivity;
	private int cursorLeft = 0;
	private int cursorTop = 0;
	// the position of the child which has the cursor in front of it
	private int cursorPos = 0;
	private int lineNum = 0;
	private int lineH;
	private Bitmap cursor;
	private Paint mCursorPaint = new Paint();
	private Runnable cursorAnimation = new Runnable() {
		public void run() {
			// Switch the cursor visibility and set it
			int newAlpha = (mCursorPaint.getAlpha() == 0) ? 255 : 0;
			mCursorPaint.setAlpha(newAlpha);
			// Call onDraw() to draw the cursor with the new Paint
			invalidate();
			// Wait 500 milliseconds before calling self again
			postDelayed(cursorAnimation, 500);
		}
	};

	public CursorHolder(Context context) {
		super(context);
		singlePageActivity = (SinglePageActivity) context;
		android.view.ViewGroup.LayoutParams params = new android.view.ViewGroup.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		lineH = PrefManager.getIntPreference(PrefManager.NOTE_LINE_HEIGHT,
				context);

//		cursor = BitmapFactory.decodeResource(this.getContext().getResources(),
//				R.drawable.ic_navigation_black);
//		cursorLeft = -(int) cursor.getWidth() / 2;
//		cursorTop = (int) lineH - (int) cursor.getHeight() / 2;
		post(cursorAnimation);
		this.setLayoutParams(params);
		this.setBackgroundColor(Color.TRANSPARENT);
	}

	public CursorHolder(Context context, AttributeSet attrs) {
		super(context, attrs);
		lineH = PrefManager.getIntPreference(PrefManager.NOTE_LINE_HEIGHT,
				context);

//		cursor = BitmapFactory.decodeResource(this.getContext().getResources(),
//				R.drawable.ic_navigation_black);
//		cursorLeft = -(int) cursor.getWidth() / 2;
//		cursorTop = (int) lineH - (int) cursor.getHeight() / 2;
		this.cursorTop = lineH/7;
		post(cursorAnimation);
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		canvas.drawBitmap(cursor, (float) cursorLeft, (float) cursorTop,
//				mCursorPaint);
		canvas.drawRect((float) cursorLeft, (float) cursorTop, (float) cursorLeft + lineH / 10, (float) cursorTop + (5 * lineH) / 7,
				mCursorPaint);
	}

	public void setCusorViewRight(int viewRight, int lineNum, int cursorPos) {
//		this.cursorLeft = viewRight - (int) cursor.getWidth() / 2;
//		this.cursorTop = lineNum * lineH + lineH - (int) cursor.getHeight() / 2;
		this.cursorLeft = viewRight;
		this.cursorTop = lineNum * lineH + lineH/7;

		this.lineNum = lineNum;
		this.cursorPos = cursorPos;
		Log.d("cursorHolder", "setCursor at left: " + cursorLeft + " top: "
				+ cursorTop);
		this.invalidate();
	}

	public void setCursorViewLeft(int viewLeft, int lineNum, int cursorPos) {
//		this.cursorLeft = viewLeft - (int) cursor.getWidth() / 2;
//		this.cursorTop = lineNum * lineH + lineH - (int) cursor.getHeight() / 2;
		this.cursorLeft = viewLeft;
		this.cursorTop = lineNum * lineH + lineH/7;

		this.lineNum = lineNum;
		this.cursorPos = cursorPos;
		Log.d("cursorHolder", "setCursor at left: " + cursorLeft + " top: "
				+ cursorTop);
		this.invalidate();
	}

	/**
	 * @return the first item is cursorLeft Position, the second is line
	 *         number(start from 0)
	 */
	public int[] getCursorPosition() {
		int[] cursorPos = new int[2];
		cursorPos[0] = cursorLeft;
		cursorPos[1] = lineNum;
		return cursorPos;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("CursorHolder", "onTouchEvent");
		performClick();
//		switch(event.getAction()){
//			case MotionEvent.ACTION_DOWN:
//				singlePageActivity.showIndicator();
//				break;
//			case MotionEvent.ACTION_UP:
//				singlePageActivity.hideIndicator();
//				break;
//			default:
//				break;
//		}
		return false;
	}

	public void setCursorPos(int pos) {
		this.cursorPos = pos;
	}

	public int getCursorPos() {
		return cursorPos;
	}

	public int getCursorLeft() {
		return cursorLeft;
	}

	public int getLineNum() {
		return lineNum;
	}

}
