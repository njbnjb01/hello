package com.serviatech.test.entity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TerminalScreen extends View
{
	Paint mPaint;
	TextLine mAllLines[];
	int mLatestLine; // pointer to latest line in mAllLines[], as ring buffer
	int mLineFilled; // number of lines inserted, used for page control, stop counting when = size of mAllLines
	
	int mCurX;
	int mCurY;
	int mCurW;
	int mCurH;
	int mCharW;
	int mCharH;
	int mFontSize;
	int mLatestLineY; // for fast latest line update drwaing
	
	int mState;
	public static final int TSS_IDLE = 0;
	public static final int TSS_INIT = 1;
	public static final int TSS_REDRAW_ALL = 2;
	public static final int TSS_REDRAW_LINE = 3;
	public static final int TSS_AUTO_RESIZE = 4;
	
	public static int BG_COLOR = 0x44000000;
	
	public TerminalScreen(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mPaint = new Paint();
		// uart data can be received only when new line appear, so allocate 2 line default
		mAllLines = new TextLine[2];
		mAllLines[0] = new TextLine(40);
		mAllLines[1] = new TextLine(40);
		
		mLatestLine = 0;
		mLineFilled = 1;
		mCurX = mCurY = mCurW = mCurH = 0;
		changeState(TSS_INIT);
		mCharW = 20;
		mCharH = 30;
		mFontSize = 20;
		mLatestLineY = 0;
	}
	
	public void setCharSize(int w, int h, int fontsize)
	{
		mCharW = w;
		mCharH = h;
		mFontSize = fontsize;
		mCurW = getWidth() / mCharW;
		mCurH = getHeight() / mCharH;
		mCurX = mCurY = 0;
		changeState(TSS_INIT);
	}
	
	// no param version, auto!
	public boolean resizeTerminalAuto()
	{
		mCurW = getWidth() / mCharW;
		mCurH = getHeight() / mCharH;

		if(mCurH <= 0 || mCurW <= 0){ // screen too small
			return false;
		}
		mAllLines = new TextLine[mCurH];
		for(int i=0; i<mCurH; i++){
			mAllLines[i] = new TextLine(mCurW);
		}
		mLatestLine = 0;
		mLineFilled = 1;
		mCurX = mCurY = 0;
		invalidate();
		return true;
	}
	
	public void resizeTerminal()
	{
		changeState(TSS_AUTO_RESIZE);
	}
	
	public boolean resizeTerminal(int xchar, int ychar, int lines)
	{
		if(lines < ychar){ // allocate too less lines
			return false;
		}
		mAllLines = new TextLine[lines];
		for(int i=0; i<lines; i++){
			mAllLines[i] = new TextLine(xchar);
		}
		mLatestLine = 0;
		mLineFilled = 1;
		mCurX = mCurY = 0;
		mCurW = xchar;
		mCurH = ychar;
		changeState(TSS_INIT);
		invalidate();
		return true;
	}
	
	void changeState(int state)
	{
		//Log.i("TerScr", "CS=" + state);
		if(state == TSS_AUTO_RESIZE){
			mState = state;
			invalidate();
			return;
		}
		
		if(mState == TSS_AUTO_RESIZE){
			invalidate();
			return;
		}else if(mState == TSS_INIT || mState == TSS_REDRAW_ALL){
			invalidate();
			return;
		}
		mState = state;
	}
	
	public void putByte(byte b)
	{
		if(b == 0xD || b == '\n'){
			mLatestLine++;
			mLineFilled++;
			if(mLatestLine >= mAllLines.length){
				mLatestLine = 0;
			}
			if(mLineFilled >= mAllLines.length){
				mLineFilled = mAllLines.length;
			}
			mAllLines[mLatestLine].resetLine();
			changeState(TSS_REDRAW_ALL);
			invalidate();
		}else{
			mAllLines[mLatestLine].addByte(b);
			//changeState(TSS_REDRAW_LINE);
			changeState(TSS_REDRAW_ALL);
			//invalidate(0, mLatestLineY - mCharH, getWidth(), mLatestLineY);
			invalidate();
		}
	}
	
	protected void onDraw(Canvas canvas)
	{
		//Log.i("TerScr", "RCS=" + mState);
		if(mState == TSS_IDLE){
			return;
			
		}else if(mState == TSS_REDRAW_LINE){
			// new character input, let's draw it
			TextLine tl = mAllLines[mLatestLine];
			int posx = 0;
			int posy = mCharH * (mCurH - 1); // lowest position
			for(int j=0; j<mCurW; j++){
				if(j < tl.mSize){
					canvas.drawText(new String(tl.mBuf, j, 1), posx, posy, mPaint);
					posx += mCharW;
				}else{
					break;
				}
			}
			
		}else if(mState == TSS_REDRAW_ALL){
			// fill background
			mPaint.setColor(BG_COLOR);
			mPaint.setStyle(Paint.Style.FILL);
			canvas.drawPaint(mPaint);
    		// TODO: consider line wrap
			int lines = mLineFilled;
			if(lines > mCurH){
				lines = mCurH;
			}
			//Log.i("TerScr", "TSS_RESET W="+mCurW+" H="+mCurH+" L="+lines);
			// draw text
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setColor(Color.WHITE);	
			mPaint.setTextSize(mFontSize);
			mPaint.setTypeface(Typeface.MONOSPACE);
			int posy = mLatestLineY;
			for(int i=0; i<lines; i++){
				int lineptr = mLatestLine - i;
				if(lineptr < 0){
					lineptr += mAllLines.length; // ring buffer
				}
				int posx = 0;
				for(int j=0; j<mCurW; j++){
					TextLine tl = mAllLines[lineptr];
					if(j < tl.mSize){
						canvas.drawText(new String(tl.mBuf, j, 1), posx, posy, mPaint);
						posx += mCharW;
					}else{
						break;
					}
				}
				posy -= mCharH;
			}
		}else if(mState == TSS_INIT){
			mLatestLineY = mCharH * mCurH;
			// fill background
			mPaint.setColor(BG_COLOR);
			mPaint.setStyle(Paint.Style.FILL);
			canvas.drawPaint(mPaint);
			
		}else if(mState == TSS_AUTO_RESIZE){
			if(resizeTerminalAuto() == false){
				Log.i("TerScr", "resizeTerminalAuto failed");
			}
			Log.i("TerScr", "New TermSize W=" + mCurW + " H=" + mCurH);
			mState = TSS_INIT;
			return;
		}
		mState = TSS_IDLE;
	}
	
	public void onSizeChanged(int w, int h, int oldW, int oldH)
	{
		changeState(TSS_REDRAW_ALL);
		invalidate();
	}
	
	// class to store each line in terminal
	class TextLine
	{
		public int mSize; // size used in mBuf[]
		public int mCur; // TODO: cursor control
		public byte mBuf[];
		//TODO:  handle color control character
		public byte mColorBuf[];
		public byte mColor;
		
		public TextLine(int maxlen)
		{
			mSize = 0;
			mBuf = new byte[maxlen];
			mColorBuf = new byte[maxlen];
		}
		
		public void addByte(byte b)
		{
			if(b >= 0x20 && b < 0xff){ // normal character
				if(mSize >= mBuf.length){
					mSize = mBuf.length; // prevent index out of bound
					mBuf[mSize - 1] = b;
				}else{
					mBuf[mSize++] = b;
				}
			}else{
				// if color control or other char, handle here
			}
		}
		
		public void resetLine()
		{
			mSize = 0;
		}
	}
}
