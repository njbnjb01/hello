package com.serviatech.test.activity;

import com.serviatech.test.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Bundle;
import android.view.View;

public class TestAnim extends Activity { 
    
    private Movie mMovie; 
    private long mMovieStart;
    /** Called when the activity is first created. */ 
    @Override 
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(new CustomGifView(this)); 
        }
    class CustomGifView extends View {
        public CustomGifView(Context context) { 
            super(context); 
            mMovie = Movie.decodeStream(getResources().openRawResource( 
                    R.drawable.animation));
        } 
        
        public void onDraw(Canvas canvas) {
            long now = android.os.SystemClock.uptimeMillis(); 
            
            if (mMovieStart == 0) { // first time 
                mMovieStart = now; 
            } 
            if (mMovie != null) { 
                
                int dur = mMovie.duration(); 
                if (dur == 0) { 
                    dur = 1000; 
                } 
                int relTime = (int) ((now - mMovieStart) % dur);                
                mMovie.setTime(relTime); 
                mMovie.draw(canvas, 0, 0); 
                invalidate(); 
            } 
        }
    }
}
