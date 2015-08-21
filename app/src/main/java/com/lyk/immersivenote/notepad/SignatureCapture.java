package com.lyk.immersivenote.notepad;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class SignatureCapture extends View {
	public static final int INVALID_STROKE = 999;
	private static float strokeWidth;
	private static float halfStrokeWidth;
	private Paint paint = new Paint();
	private Path path = new Path();

	private android.view.ViewGroup.LayoutParams params;

	private float lastTouchX;
	private float lastTouchY;
	private final RectF dirtyRect = new RectF();
	private RectF pathRect = new RectF();
	private Bitmap mBitmap = null;

	private FrameLayout sigHolder;

	private LinearLayout signatureAuxiliaryLayer;
	private boolean flagAuxiliary = false;

	private boolean kanjiMode = true;

	private int lineH;
	private int baseW = 0;
	private int baseH = 0;
	private int cropX = 0;
	private int cropY = 0;
	private int cropW = 0;
	private int cropH = 0;
	private int numStroke = 0;

	private CustomCountDownTimer timer = null;

	private PropertyChangeSupport pcs = null;



	public SignatureCapture(Context context, AttributeSet attrs, int lineH,
							FrameLayout sigHolder) {
		super(context, attrs);
		strokeWidth = lineH / 8;
		halfStrokeWidth = strokeWidth / 2;
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeWidth(strokeWidth);
		this.setBackgroundColor(Color.TRANSPARENT);

		this.sigHolder = sigHolder;
		this.lineH = lineH;

		params = new android.view.ViewGroup.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		this.setLayoutParams(params);

		pcs = new PropertyChangeSupport(this);

	}


	public void setBaseW(int width){
		baseW = width;
	}

	public void setBaseH(int height){
		baseH = height;
	}

	public void setSignatureAuxiliaryLayer(LinearLayout layer){
		signatureAuxiliaryLayer = layer;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void save() {
		// new TaskSave(cropX, cropY, cropW, cropH, mBitmap).execute();
		path.computeBounds(pathRect, true);
		cropX = Math.max(0, (int) (pathRect.left - strokeWidth));
		cropY = Math.max(0, (int) (pathRect.top - strokeWidth));
		cropW = Math.min(baseW - cropX,
				(int) (pathRect.width() + 2 * strokeWidth));
		cropH = Math.min(baseH - cropY,
				(int) (pathRect.height() + 2 * strokeWidth));

		if(cropH > 6*strokeWidth || cropW > 6*strokeWidth){
			padMBitmap();
//		new TaskSave().execute();
//		new TaskCompress().execute();
			pcs.firePropertyChange("Signature", null, mBitmap);
			paint.setStrokeWidth(strokeWidth);
		}
		else{
			MotionEvent motionEvent = MotionEvent.obtain(
					100,
					200,
					INVALID_STROKE,
					lastTouchX,
					lastTouchY,
					0
			);

			// Dispatch touch event to view
			this.dispatchTouchEvent(motionEvent);
		}
		clear();
	}

//	private class TaskCompress extends AsyncTask<Void, Void, Void> {
//
//		@Override
//		protected Void doInBackground(Void... params) {
//			ByteArrayOutputStream out = new ByteArrayOutputStream();
//			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//			mBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			super.onPostExecute(result);
//			pcs.firePropertyChange("Signature", null, mBitmap);
//			clear();
//			paint.setStrokeWidth(strokeWidth);
//		}
//
//
//
//	}
//	private class TaskSave extends AsyncTask<Void, Void, Void> {
//
//		@Override
//		protected Void doInBackground(Void... params) {
//			baseW = sigHolder.getWidth();
//			baseH = sigHolder.getHeight();
//			path.computeBounds(pathRect, true);
//			cropX = Math.max(0, (int) (pathRect.left - strokeWidth));
//			cropY = Math.max(0, (int) (pathRect.top - strokeWidth));
//			cropW = Math.min(baseW - cropX,
//					(int) (pathRect.width() + 2 * strokeWidth));
//			cropH = Math.min(baseH - cropY,
//					(int) (pathRect.height() + 2 * strokeWidth));
//			int paddingX =  lineH;
//			int paddingY = 0;
//			Bitmap paddedBitmap = null;
//			boolean isUpper = false;
//			boolean isLower = false;
//
//			// need to render the larger bitmap first before getting the
//			// smaller one
//
//			mBitmap = Bitmap.createBitmap(baseW, baseH, Bitmap.Config.ARGB_4444);
//			mBitmap.setHasAlpha(true);
//			Canvas canvas = new Canvas(mBitmap);
//
//
//
//			if (cropY + cropH < lineH * 3) {
//				isUpper = true;
//				if (cropW / cropH > 1.5 && numStroke == 1) {
//					paint.setStrokeWidth((int) (strokeWidth * 4.5));
//					sigHolder.draw(canvas);
//					mBitmap = Bitmap.createBitmap(mBitmap, cropX, 0, cropW,
//							lineH * 9);
//					paddingY = 0;
//
//				} else {
//
//					paint.setStrokeWidth((int)((strokeWidth *cropH)/(lineH)));
//					sigHolder.draw(canvas);
//					paddingY = 2 * cropH;
//					mBitmap = Bitmap.createBitmap(mBitmap, cropX, cropY, cropW, cropH);
//					Log.d("padMBitmap", "upper case");
//				}
//
//			} else if (cropY > lineH * 12) {
//				isLower = true;
//				if (cropW / cropH > 1.5 && numStroke == 1) {
//					paint.setStrokeWidth((int) (strokeWidth * 4.5));
//					sigHolder.draw(canvas);
//					mBitmap = Bitmap.createBitmap(mBitmap, 0, lineH *6, baseW,
//							lineH * 9);
//					paddingY = 0;
//
//				} else {
//					paddingY = 2 * cropH;
//					paint.setStrokeWidth((int)((strokeWidth *cropH)/(lineH)));
//					sigHolder.draw(canvas);
//					mBitmap = Bitmap.createBitmap(mBitmap, cropX, cropY, cropW, cropH);
//					Log.d("padMBitmap", "lower case");
//				}
//			} else {
//				if(kanjiMode){
//					paint.setStrokeWidth((int) ((strokeWidth * Math.max(cropW,cropH)) / (lineH * 1.8)));
//					sigHolder.draw(canvas);
//					mBitmap = Bitmap.createBitmap(mBitmap, cropX, cropY, cropW,
//							cropH);
//					if(cropW > cropH){
//						paddingY = cropW - cropH + paddingX;
//					}
//					else{
//						paddingY = paddingX;
//						paddingX = cropH - cropW + paddingX;
//					}
//				}
//				else{
//					if (cropW / cropH > 1.5 && numStroke == 1) {
//						// category = Category.CASE_;
//
//						paint.setStrokeWidth((int) (strokeWidth * 4.5));
//						sigHolder.draw(canvas);
//						mBitmap = Bitmap.createBitmap(mBitmap, 0, lineH * 3, baseW,
//								lineH * 9);
//						paddingY = 0;
//						Log.d("padMBitmap", "'-' case");
//
//					} else {
//						paint.setStrokeWidth((int) ((strokeWidth * cropH) / (lineH * 1.8)));
//						sigHolder.draw(canvas);
//						mBitmap = Bitmap.createBitmap(mBitmap, cropX, cropY, cropW,
//								cropH);
//						paddingY = (int) 2 * cropH / 5;
//						Log.d("padMBitmap", "normal case");
//					}
//				}
//			}
//
//			// reset the numStroke after using
//			numStroke = 0;
//
//			// mBitmap = Bitmap.createBitmap(mBitmap,cropX,cropY,cropW,cropH);
//
//			paddedBitmap = Bitmap.createBitmap(mBitmap.getWidth() + paddingX,
//					mBitmap.getHeight() + paddingY, Bitmap.Config.ARGB_4444);
//
//			canvas = new Canvas(paddedBitmap);
//			canvas.drawARGB(0x00, 0xFF, 0xFF, 0xFF); // this represents transparent
//
//			if (isUpper) {
//				canvas.drawBitmap(mBitmap, paddingX / 2, 0, new Paint(
//						Paint.FILTER_BITMAP_FLAG));
//			} else if (isLower) {
//				canvas.drawBitmap(mBitmap, paddingX / 2, paddingY, new Paint(
//						Paint.FILTER_BITMAP_FLAG));
//			} else {
//				canvas.drawBitmap(mBitmap, paddingX / 2, paddingY / 2, new Paint(
//						Paint.FILTER_BITMAP_FLAG));
//			}
//			paddedBitmap.setHasAlpha(true);
//			mBitmap = paddedBitmap;
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			super.onPostExecute(result);
//			pcs.firePropertyChange("Signature", null, mBitmap);
//			clear();
//			paint.setStrokeWidth(strokeWidth);
//		}
//
//
//
//	}

	private void padMBitmap() {


		int paddingX =  lineH;
		int paddingY = 0;
		Bitmap paddedBitmap = null;
		boolean isUpper = false;
		boolean isLower = false;

		// need to render the larger bitmap first before getting the
		// smaller one

		mBitmap = Bitmap.createBitmap(baseW, baseH, Bitmap.Config.ARGB_8888);
		mBitmap.setHasAlpha(true);
		Canvas canvas = new Canvas(mBitmap);



		if (cropY + cropH < lineH * 3) {
			isUpper = true;
			if (cropW / cropH > 1.5 && numStroke == 1) {
				paint.setStrokeWidth((int) (strokeWidth * 4.5));
				sigHolder.draw(canvas);
				mBitmap = Bitmap.createBitmap(mBitmap, cropX, 0, cropW,
						lineH * 9);
				paddingY = 0;

			} else {

				paint.setStrokeWidth((int)((strokeWidth *cropH)/(lineH)));
				sigHolder.draw(canvas);
				paddingY = 2 * cropH;
				mBitmap = Bitmap.createBitmap(mBitmap, cropX, cropY, cropW, cropH);
				Log.d("padMBitmap", "upper case");
			}

		} else if (cropY > lineH * 12) {
			isLower = true;
			if (cropW / cropH > 1.5 && numStroke == 1) {
				paint.setStrokeWidth((int) (strokeWidth * 4.5));
				sigHolder.draw(canvas);
				mBitmap = Bitmap.createBitmap(mBitmap, 0, lineH *6, baseW,
						lineH * 9);
				paddingY = 0;

			} else {
				paddingY = 2 * cropH;
				paint.setStrokeWidth((int)((strokeWidth *cropH)/(lineH)));
				sigHolder.draw(canvas);
				mBitmap = Bitmap.createBitmap(mBitmap, cropX, cropY, cropW, cropH);
				Log.d("padMBitmap", "lower case");
			}
		} else {
			if(kanjiMode){
				paint.setStrokeWidth((int) ((strokeWidth * Math.max(cropW,cropH)) / (lineH * 1.8)));
				sigHolder.draw(canvas);
				mBitmap = Bitmap.createBitmap(mBitmap, cropX, cropY, cropW,
						cropH);
				if(cropW > cropH){
					paddingY = cropW - cropH + paddingX;
				}
				else{
					paddingY = paddingX;
					paddingX = cropH - cropW + paddingX;
				}
			}
			else{
				if (cropW / cropH > 1.5 && numStroke == 1) {
					// category = Category.CASE_;

					paint.setStrokeWidth((int) (strokeWidth * 4.5));
					sigHolder.draw(canvas);
					mBitmap = Bitmap.createBitmap(mBitmap, 0, lineH * 3, baseW,
							lineH * 9);
					paddingY = 0;
					Log.d("padMBitmap", "'-' case");

				} else {
					paint.setStrokeWidth((int) ((strokeWidth * cropH) / (lineH * 1.8)));
					sigHolder.draw(canvas);
					mBitmap = Bitmap.createBitmap(mBitmap, cropX, cropY, cropW,
							cropH);
					paddingY = (int) 2 * cropH / 5;
					Log.d("padMBitmap", "normal case");
				}
			}
		}

		// reset the numStroke after using
		numStroke = 0;

		// mBitmap = Bitmap.createBitmap(mBitmap,cropX,cropY,cropW,cropH);

		paddedBitmap = Bitmap.createBitmap(mBitmap.getWidth() + paddingX,
				mBitmap.getHeight() + paddingY, Bitmap.Config.ARGB_4444);

		canvas = new Canvas(paddedBitmap);
		canvas.drawARGB(0x00, 0xFF, 0xFF, 0xFF); // this represents transparent

		if (isUpper) {
			canvas.drawBitmap(mBitmap, paddingX / 2, 0, new Paint(
					Paint.FILTER_BITMAP_FLAG));
		} else if (isLower) {
			canvas.drawBitmap(mBitmap, paddingX / 2, paddingY, new Paint(
					Paint.FILTER_BITMAP_FLAG));
		} else {
			canvas.drawBitmap(mBitmap, paddingX / 2, paddingY / 2, new Paint(
					Paint.FILTER_BITMAP_FLAG));
		}
		paddedBitmap.setHasAlpha(true);
		mBitmap = paddedBitmap;

	}

	public void clear() {
		// cropX = 0;
		// cropY = 0;
		cropW = 0;
		cropH = 0;
		path.reset();
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawPath(path, paint);
	}

	@Override
	public boolean performClick() {
		// TODO Auto-generated method stub
		return super.performClick();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		performClick();
		float eventX = event.getX();
		float eventY = event.getY();

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!flagAuxiliary) {
//					sigHolder.addView(signatureAuxiliaryLayer, 0);
					signatureAuxiliaryLayer.setVisibility(View.VISIBLE);
					flagAuxiliary = true;

				}

				if (timer != null) {
					timer.cancel();
					timer = null;
				}
				path.moveTo(eventX, eventY);
				lastTouchX = eventX;
				lastTouchY = eventY;
				return true;

			case MotionEvent.ACTION_MOVE:
				resetDirtyRect(eventX, eventY);
				int historySize = event.getHistorySize();
				for (int i = 0; i < historySize; i++) {
					float historicalX = event.getHistoricalX(i);
					float historicalY = event.getHistoricalY(i);
					expandDirtyRect(historicalX, historicalY);
					path.lineTo(historicalX, historicalY);
				}
				path.lineTo(eventX, eventY);
				break;

			case MotionEvent.ACTION_UP:
				numStroke += 1;
				resetDirtyRect(eventX, eventY);
				historySize = event.getHistorySize();
				for (int i = 0; i < historySize; i++) {
					float historicalX = event.getHistoricalX(i);
					float historicalY = event.getHistoricalY(i);
					expandDirtyRect(historicalX, historicalY);
					path.lineTo(historicalX, historicalY);
				}
				path.lineTo(eventX, eventY);
				timer = new CustomCountDownTimer(300, this);
				timer.start();
				break;
			case INVALID_STROKE:
				Log.d("SignatureCapture","invalid stroke case");
				return false;
			default:
				Log.d("SignatureCapture","ignored case");
				return false;
		}

		invalidate((int) (dirtyRect.left - halfStrokeWidth),
				(int) (dirtyRect.top - halfStrokeWidth),
				(int) (dirtyRect.right + halfStrokeWidth),
				(int) (dirtyRect.bottom + halfStrokeWidth));
		// updateCrop();

		lastTouchX = eventX;
		lastTouchY = eventY;

		return true;
	}

	private void debug(String string) {
	}

	private void expandDirtyRect(float historicalX, float historicalY) {
		if (historicalX < dirtyRect.left) {
			dirtyRect.left = historicalX;
		} else if (historicalX > dirtyRect.right) {
			dirtyRect.right = historicalX;
		}

		if (historicalY < dirtyRect.top) {
			dirtyRect.top = historicalY;
		} else if (historicalY > dirtyRect.bottom) {
			dirtyRect.bottom = historicalY;
		}
	}

	private void resetDirtyRect(float eventX, float eventY) {
		dirtyRect.left = Math.min(lastTouchX, eventX);
		dirtyRect.right = Math.max(lastTouchX, eventX);
		dirtyRect.top = Math.min(lastTouchY, eventY);
		dirtyRect.bottom = Math.max(lastTouchY, eventY);
	}

	// private void updateCrop() {
	//
	// cropW = Math.max(
	// (int) (dirtyRect.left + halfStrokeWidth) - cropX, cropW);
	//
	// cropH = Math.max((int) (dirtyRect.top + halfStrokeWidth) - cropY,
	// cropH);
	//
	// if (cropX == 0) {
	// cropX = (int) (dirtyRect.left - halfStrokeWidth);
	// } else {
	// cropX = Math.min((int) (dirtyRect.left - halfStrokeWidth), cropX);
	// }
	//
	// if (cropY == 0) {
	// cropY = (int) (dirtyRect.top - halfStrokeWidth);
	// } else {
	// cropY = Math.min((int) (dirtyRect.top - halfStrokeWidth), cropY);
	// }
	//
	// cropW = Math.max((int) (dirtyRect.left + halfStrokeWidth) - cropX,
	// cropW);
	//
	// cropH = Math
	// .max((int) (dirtyRect.top + halfStrokeWidth) - cropY, cropH);
	//
	// Log.d("updateCrop", "" + cropX + ";" + cropY + ";" + cropW + ";"
	// + cropH);
	// }

	private Bitmap CropBitmapTransparency(Bitmap sourceBitmap) {
		int minX = sourceBitmap.getWidth();
		int minY = sourceBitmap.getHeight();
		int maxX = -1;
		int maxY = -1;
		for (int y = 0; y < sourceBitmap.getHeight(); y++) {
			for (int x = 0; x < sourceBitmap.getWidth(); x++) {
				int alpha = (sourceBitmap.getPixel(x, y) >> 24) & 255;
				if (alpha > 0) // pixel is not 100% transparent
				{
					if (x < minX)
						minX = x;
					if (x > maxX)
						maxX = x;
					if (y < minY)
						minY = y;
					if (y > maxY)
						maxY = y;
				}
			}
		}
		if ((maxX < minX) || (maxY < minY))
			return null; // Bitmap is entirely transparent

		// crop bitmap to non-transparent area and return:
		return Bitmap.createBitmap(sourceBitmap, minX, minY, (maxX - minX) + 1,
				(maxY - minY) + 1);
	}

	private class CustomCountDownTimer extends CountDownTimer {
		private SignatureCapture sig;

		public CustomCountDownTimer(long millisInFuture, SignatureCapture sig) {
			super(millisInFuture, millisInFuture);
			this.sig = sig;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onFinish() {
//			sigHolder.removeViewAt(0);
			signatureAuxiliaryLayer.setVisibility(View.INVISIBLE);
			flagAuxiliary = false;
			sig.save();
//			sig.clear();
		}
	}
}
