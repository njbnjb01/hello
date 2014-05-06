package com.serviatech.test.activity;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.serviatech.test.R;
import com.serviatech.test.application.TestApplication;
import com.serviatech.test.entity.UartData;
import com.serviatech.test.utils.AppUtils;
import com.serviatech.test.utils.Constant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TestActivity extends Activity implements Callback,
		MediaPlayer.OnPreparedListener, OnBufferingUpdateListener,
		OnCompletionListener,OnClickListener {
	/** Called when the activity is first created. */

	private MediaPlayer mMediaPlayer;
	private TextView vtitleText;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private ImageView iv ;
	private TextView titleText ;
	private String strVideoPath = null;
	private static final String TAG = "MediaPlayer";
	public static final int ANIMATION_TIME = 5000; 
	public static boolean FLAG = true;
	private int mVideoWidth;
	private int mVideoHeight;
	private int vindex = 0 ;
	int pindex = 0;
//	private List<Map<String, String>> vlist;
//	private List<Map<String, String>> plist;
	private TestApplication testApp;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_play);
		Log.v(TAG, TAG+" onCreate "+FLAG);
		
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
//        // 去掉界面任务条  
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		TestApplication.getInstance().addActivity(this);
		testApp = (TestApplication)getApplicationContext();
		/* 得到视频目录下的所有可播放视频 */
		Intent intent  = getIntent();
		if (intent!=null) {
			Bundle bundle = intent.getExtras();
			int m = 0;
			if (bundle!=null) {
				m = bundle.getInt("update");
			}
			if (m==1) {
				File video = new File(Constant.VIDEO_PATH);
				File pic = new File(Constant.PICS_PATH);
				if (video.exists()) {
//					Log.i(TAG, "videos exists");
					testApp.videoList = testApp.searchVideo(video);
					testApp.picList = testApp.searchPics(pic);
					if (testApp.videoList.size()<=0 && pic.exists()) {
//						Log.i(TAG, "pics exists");
						testApp.picList = testApp.searchPics(pic);
					}
				}else {
					if (pic.exists()) {
						testApp.picList = testApp.searchPics(pic);
					}
				}
			}
		}
//		vlist = testApp.getVideoList();
//		plist = testApp.getPicsList();
		mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView1);
		iv = (ImageView)findViewById(R.id.iv_animation_logo);
		vtitleText = (TextView) findViewById(R.id.main_more_vtext);
		InitViews();
	}
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (testApp.picList.size()==0) {
					playVideo();
				}else if (testApp.picList.size()>0) {
					try {
						mMediaPlayer.stop();
						mMediaPlayer.release();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					mSurfaceView.setVisibility(View.GONE);
					iv.setVisibility(View.VISIBLE);
					//图片渐变模糊度始终  
					AlphaAnimation aa = new AlphaAnimation(1.0f,0.1f);  
					//渐变时间  
					aa.setDuration(ANIMATION_TIME);  
					aa.setRepeatCount(-1);
					//展示图片渐变动画  
					iv.startAnimation(aa);  
					//渐变过程监听  
					aa.setAnimationListener(new AnimationListener() {  
						
						/** 
						 * 动画开始时 
						 */  
						@Override  
						public void onAnimationStart(Animation animation) {  
//							System.out.println("动画开始...");  
							Drawable drawable = Drawable.createFromPath(testApp.picList.get(pindex).get("picpath"));
							iv.setImageDrawable(drawable);
							pindex++;
						}  
						
						/** 
						 * 重复动画时 
						 */  
						@Override  
						public void onAnimationRepeat(Animation animation) {  
//							System.out.println("动画重复...");  
							if(pindex>=testApp.picList.size()){
								pindex=0;
								handler.sendEmptyMessage(1);
								return;
							}
							Drawable drawable = Drawable.createFromPath(testApp.picList.get(pindex).get("picpath"));
							iv.setImageDrawable(drawable);
							pindex++;
						}  
						
						/** 
						 * 动画结束时 
						 */  
						@Override  
						public void onAnimationEnd(Animation animation) {  
//							System.out.println("动画结束...");  
						}  
					});  
				}else if(testApp.picList.size()==1) {
					Drawable drawable = Drawable.createFromPath(testApp.picList.get(0).get("picpath"));
					iv.setImageDrawable(drawable);
				}
				break;
			case 1:
				mSurfaceView.setVisibility(View.VISIBLE);
				iv.clearAnimation();
				iv.setVisibility(View.GONE);
				playVideo();
				break;

			default:
				break;
			}
		};
	};
	private void InitViews() {
		// TODO Auto-generated method stub
		vtitleText.setText(AppUtils.getText(Constant.TITLE_PATH+"title.txt"));
//		Log.v(TAG, "vlist = "+testApp.videoList.size());
//		Log.v(TAG, "plist = "+testApp.picList.size());
		if (testApp.videoList!=null && testApp.videoList.size()>0) {
			mSurfaceView.setVisibility(View.VISIBLE);
			iv.setVisibility(View.GONE);
			strVideoPath = testApp.videoList.get(vindex).get("videopath");//默认播放第一个
			mSurfaceView.setOnClickListener(this);
			mSurfaceHolder = mSurfaceView.getHolder();
			mSurfaceHolder.addCallback(this);
			mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}else if(testApp.picList!=null && testApp.picList.size()>0){
			mSurfaceView.setVisibility(View.GONE);
			iv.setVisibility(View.VISIBLE);
			if (testApp.picList.size()>1) {
				//图片渐变模糊度始终  
				AlphaAnimation aa = new AlphaAnimation(1.0f,0.1f);  
				//渐变时间  
				aa.setDuration(ANIMATION_TIME);  
				aa.setRepeatCount(-1);
				//展示图片渐变动画  
				iv.startAnimation(aa);  
				//渐变过程监听  
				aa.setAnimationListener(new AnimationListener() {  
					
					/** 
					 * 动画开始时 
					 */  
					@Override  
					public void onAnimationStart(Animation animation) {  
//						System.out.println("动画开始...");  
						Drawable drawable = Drawable.createFromPath(testApp.picList.get(pindex).get("picpath"));
						iv.setImageDrawable(drawable);
						pindex++;
					}  
					
					/** 
					 * 重复动画时 
					 */  
					@Override  
					public void onAnimationRepeat(Animation animation) {  
//						System.out.println("动画重复...");  
						if(pindex>=testApp.picList.size()){
							pindex=0;
							handler.sendEmptyMessage(1);
							return;
						}
						Drawable drawable = Drawable.createFromPath(testApp.picList.get(pindex).get("picpath"));
						iv.setImageDrawable(drawable);
						pindex++;
					}  
					
					/** 
					 * 动画结束时 
					 */  
					@Override  
					public void onAnimationEnd(Animation animation) {  
//						System.out.println("动画结束...");  
					}  
				});  
			}else {
				Drawable drawable = Drawable.createFromPath(testApp.picList.get(0).get("picpath"));
				iv.setImageDrawable(drawable);
			}
		}else {
			mSurfaceView.setVisibility(View.GONE);
			iv.setVisibility(View.VISIBLE);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.main));
		}
	}
	
	private void initViews() {
		// TODO Auto-generated method stub
		vtitleText.setText(AppUtils.getText(Constant.TITLE_PATH+"title.txt"));
		Log.v(TAG, "vlist = "+testApp.videoList.size());
		Log.v(TAG, "plist = "+testApp.picList.size());
		if (testApp.videoList!=null && testApp.videoList.size()>0) {
			mSurfaceView.setVisibility(View.VISIBLE);
			iv.setVisibility(View.GONE);
			strVideoPath = testApp.videoList.get(vindex).get("videopath");//默认播放第一个
			mSurfaceView.setOnClickListener(this);
			mSurfaceHolder = mSurfaceView.getHolder();
			mSurfaceHolder.addCallback(this);
			mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}else if(testApp.picList!=null && testApp.picList.size()>0){
			mSurfaceView.setVisibility(View.GONE);
			iv.setVisibility(View.VISIBLE);
			if (testApp.picList.size()>1) {
	        	//图片渐变模糊度始终  
	        	AlphaAnimation aa = new AlphaAnimation(1.0f,0.1f);  
	        	//渐变时间  
	        	aa.setDuration(ANIMATION_TIME);  
	        	aa.setRepeatCount(-1);
	        	//展示图片渐变动画  
	        	iv.startAnimation(aa);  
	        	//渐变过程监听  
	        	aa.setAnimationListener(new AnimationListener() {  
	        		
	        		/** 
	        		 * 动画开始时 
	        		 */  
	        		@Override  
	        		public void onAnimationStart(Animation animation) {  
//	        			System.out.println("动画开始...");  
	        			Drawable drawable = Drawable.createFromPath(testApp.picList.get(pindex).get("picpath"));
	        			iv.setImageDrawable(drawable);
	        			pindex++;
	        		}  
	        		
	        		/** 
	        		 * 重复动画时 
	        		 */  
	        		@Override  
	        		public void onAnimationRepeat(Animation animation) {  
//	        			System.out.println("动画重复...");  
	        			if(pindex>=testApp.picList.size()){
	        				pindex=0;
	        			}
	        			Drawable drawable = Drawable.createFromPath(testApp.picList.get(pindex).get("picpath"));
	        			iv.setImageDrawable(drawable);
	        			pindex++;
	        		}  
	        		
	        		/** 
	        		 * 动画结束时 
	        		 */  
	        		@Override  
	        		public void onAnimationEnd(Animation animation) {  
//	        			System.out.println("动画结束...");  
	        		}  
	        	});  
			}else {
				Drawable drawable = Drawable.createFromPath(testApp.picList.get(0).get("picpath"));
                iv.setImageDrawable(drawable);
			}
		}else {
			mSurfaceView.setVisibility(View.GONE);
			iv.setVisibility(View.VISIBLE);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.main));
		}
	}
	
//	private void init() {
//		Log.e(TAG, "pic list = "+plist.size());
//		if (vlist!=null && vlist.size()>0) {
//			strVideoPath = vlist.get(vindex).get("videopath");//默认播放第一个
//			setContentView(R.layout.video_play);
//			mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView1);
//			vtitleText = (TextView) findViewById(R.id.main_more_vtext);
//			mSurfaceView.setOnClickListener(this);
//			mSurfaceHolder = mSurfaceView.getHolder();
//			mSurfaceHolder.addCallback(this);
//			vtitleText.setText(AppUtils.getText(Constant.TITLE_PATH+"title.txt"));
//
////			mSurfaceHolder.setFixedSize(520, 220);
//			mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//		}else if(plist!=null && plist.size()>0){
//			setContentView(R.layout.pics_play);
//	        iv = (ImageView)findViewById(R.id.iv_animation_logo);
//	        titleText = (TextView)findViewById(R.id.main_more_ptext);
//			titleText.setText(AppUtils.getText(Constant.TITLE_PATH+"title.txt"));
//	        if (plist.size()>1) {
//	        	//图片渐变模糊度始终  
//	        	AlphaAnimation aa = new AlphaAnimation(1.0f,0.1f);  
//	        	//渐变时间  
//	        	aa.setDuration(ANIMATION_TIME);  
//	        	aa.setRepeatCount(-1);
//	        	//展示图片渐变动画  
//	        	iv.startAnimation(aa);  
//	        	//渐变过程监听  
//	        	aa.setAnimationListener(new AnimationListener() {  
//	        		
//	        		/** 
//	        		 * 动画开始时 
//	        		 */  
//	        		@Override  
//	        		public void onAnimationStart(Animation animation) {  
//	        			System.out.println("动画开始...");  
//	        			Drawable drawable = Drawable.createFromPath(plist.get(pindex).get("picpath"));
//	        			iv.setImageDrawable(drawable);
//	        			pindex++;
//	        		}  
//	        		
//	        		/** 
//	        		 * 重复动画时 
//	        		 */  
//	        		@Override  
//	        		public void onAnimationRepeat(Animation animation) {  
//	        			System.out.println("动画重复...");  
//	        			if(pindex>=plist.size()){
//	        				pindex=0;
//	        			}
//	        			Drawable drawable = Drawable.createFromPath(plist.get(pindex).get("picpath"));
//	        			iv.setImageDrawable(drawable);
//	        			pindex++;
//	        		}  
//	        		
//	        		/** 
//	        		 * 动画结束时 
//	        		 */  
//	        		@Override  
//	        		public void onAnimationEnd(Animation animation) {  
//	        			System.out.println("动画结束...");  
//	        		}  
//	        	});  
//			}else {
//				Drawable drawable = Drawable.createFromPath(plist.get(0).get("picpath"));
//                iv.setImageDrawable(drawable);
//			}
//		}else {
//			setContentView(R.layout.pics_play);
//			iv = (ImageView)findViewById(R.id.iv_animation_logo);
//			iv.setImageDrawable(getResources().getDrawable(R.drawable.main));
//			titleText = (TextView)findViewById(R.id.main_more_ptext);
//			titleText.setText(AppUtils.getText(Constant.TITLE_PATH+"title.txt"));
//		}
////		if (!checkSDCard()) {
////			/* 提醒User未安装SD存储卡 */
////			mMakeTextToast(getResources().getText(R.string.str_err).toString(),true);
////		}
//	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		Log.v(TAG, TAG+" onNewIntent "+FLAG);
//		if (intent!=null && intent.getExtras().getInt("update",0)==1) {
			/*File video = new File(Constant.VIDEO_PATH);
			File pic = new File(Constant.PICS_PATH);
			if (video.exists()) {
				Log.i(TAG, "videos exists");
				vlist = testApp.searchVideo(video);
//			videoList = null;
				if (vlist.size()<=0 && pic.exists()) {
					Log.i(TAG, "pics exists");
					plist = testApp.searchPics(pic);
				}
			}else {
				if (pic.exists()) {
					plist = testApp.searchPics(pic);
				}
			}*/
//		}else if(intent!=null && intent.getExtras().getInt("update",0)==0){
//			vlist = testApp.getVideoList();
//			plist = testApp.getPicsList();
//		}
		InitViews();
//		init();
	}

	private boolean checkSDCard() {

		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public void mMakeTextToast(String str, boolean isLong) {
		if (isLong == true) {
			Toast.makeText(TestActivity.this, str, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(TestActivity.this, str, Toast.LENGTH_SHORT).show();
		}
	}

	private void playVideo() {
		File file = null;
		if(file == null){
			file = new File(strVideoPath);
		}
		if(file.exists()){
			if (mMediaPlayer == null) {
				mMediaPlayer = new MediaPlayer();
			}
			mMediaPlayer.reset();
			try {
				mMediaPlayer.setDataSource(strVideoPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			mMediaPlayer.setDisplay(mSurfaceHolder);
			
			try {
				mMediaPlayer.prepare();
//				mMediaPlayer01.prepareAsync();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mMediaPlayer.setLooping(true);
			mMediaPlayer.setOnBufferingUpdateListener(this);
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
//		Log.i(TAG, "Surface Changed");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
//		Log.i(TAG, "Surface Created");
		playVideo();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
//		Log.i(TAG, "Surface Destroyed");
		super.onDestroy();
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}

	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		mVideoWidth = mp.getVideoWidth();
		mVideoHeight = mp.getVideoHeight();
		if (mVideoWidth != 0 && mVideoHeight != 0) {
			/* 设置视频的宽度和高度 */
			mSurfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
			/* 开始播放 */
			mMediaPlayer.start();
		}
//		Log.v(TAG, "onPrepared called");

	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
//		Log.v(TAG, "onBufferingUpdate");
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		mMediaPlayer.seekTo(0);
//		try {
//			mp.prepare();
//		} catch (IllegalStateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		/*try {
			mp.setDataSource(vlist.get((++index)>=vlist.size() ? 0:index).get("videopath"));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		handler.sendEmptyMessage(0);
//		mMediaPlayer.start();
//		playVideo();
//		Log.v(TAG, "onCompletion  mediaplayer is playing ==>> "+mMediaPlayer.isPlaying());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.mSurfaceView1:
			try {
				mMediaPlayer.stop();
				mMediaPlayer.release();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			exit();
			break;

		default:
			break;
		}
	}

	private void exit() {
		// TODO Auto-generated method stub
		if (FLAG) {
			Intent intent = new Intent(this,MainActivity.class);
			intent.putExtra("home", 0);
			startActivity(intent);
			FLAG = false;
			this.finish();
		}else {
			if (UartData.validRMB>0) {
				this.finish();
			}else {
				Intent intent = new Intent(this,MainActivity.class);
				intent.putExtra("home", 1);
				startActivity(intent);
				this.finish();
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
//		case MotionEvent.ACTION_UP:
//		case MotionEvent.ACTION_MOVE:
			exit();			
//			this.finish();
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}
}
