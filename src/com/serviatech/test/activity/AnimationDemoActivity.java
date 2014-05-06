package com.serviatech.test.activity;

import com.serviatech.test.R;

import android.app.Activity;  
import android.os.Bundle;  
import android.view.Window;  
import android.view.WindowManager;  
import android.view.animation.AlphaAnimation;  
import android.view.animation.Animation;  
import android.view.animation.Animation.AnimationListener;  
import android.widget.ImageView;
  
/** 
 * 图片渐变动画 
 */  
public class AnimationDemoActivity extends Activity {  
      
    public static final int ANIMATION_TIME = 5000;  
    private ImageView iv ;
    private int[] ids;
    int i = 0;
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
        // 去掉界面任务条  
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        setContentView(R.layout.pics_play);  
        
        ids = new int[]{R.drawable.bg,R.drawable.ic_launcher};
        
        //图片渐变模糊度始终  
        AlphaAnimation aa = new AlphaAnimation(1.0f,0.1f);  
        //渐变时间  
        aa.setDuration(ANIMATION_TIME);  
        aa.setRepeatCount(-1);
        //展示图片渐变动画  
        iv = (ImageView)findViewById(R.id.iv_animation_logo);
        iv.startAnimation(aa);  
          
        //渐变过程监听  
        aa.setAnimationListener(new AnimationListener() {  
              
            /** 
             * 动画开始时 
             */  
            @Override  
            public void onAnimationStart(Animation animation) {  
                System.out.println("动画开始...");  
                iv.setImageDrawable(getResources().getDrawable(ids[i]));
                i++;
            }  
              
            /** 
             * 重复动画时 
             */  
            @Override  
            public void onAnimationRepeat(Animation animation) {  
                System.out.println("动画重复...");  
                if(i>=ids.length){
                	i=0;
                }
                iv.setImageDrawable(getResources().getDrawable(ids[i]));
                i++;
            }  
              
            /** 
             * 动画结束时 
             */  
            @Override  
            public void onAnimationEnd(Animation animation) {  
                System.out.println("动画结束...");  
            }  
        });  
    }  
}  
