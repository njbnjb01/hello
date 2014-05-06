package com.serviatech.test.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import com.serviatech.test.adapter.AppAdapter;
import com.serviatech.test.adapter.MyViewPagerAdapter;
import com.serviatech.test.application.TestApplication;
import com.serviatech.test.entity.UartData;
import com.serviatech.test.utils.Constant;
import com.serviatech.test.utils.FileFilterTest;
import com.serviatech.test.utils.SortFile;
import com.serviatech.test.widget.ExitDialog;
import com.serviatech.test.widget.MyDialog;
import com.serviatech.test.widget.MyGridView;
import com.serviatech.test.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnTouchListener,OnPageChangeListener,
		OnItemClickListener, OnClickListener {
	private static final float APP_PAGE_SIZE = 9.0f;
//	public static int TIME_OFFSET = 60 * 1000;
	private static String TOUCH_ACTION = "com.android.action.ontouch";
	private static String TAG = "MainActivity";
	private Context mContext;
	private MyViewPagerAdapter adapter;
	private ArrayList<MyGridView> arrayGrid;
	private ViewPager viewPager;
	private TextView[] tv;
	private int mCurSel;
	private int mIndex;
	private int pageCount;
	private LinearLayout pointLayout;
	private LinearLayout gridLayout;
	private Button frontBtn;
	private Button nextBtn;
	private Button cuxiaoBtn;
	private Button rexiaoBtn;
	private Button tuijianBtn;
	
	private Button shouyeBtn;
	private Button jishengBtn;
	private Button qijuBtn;
	private Button runhuaBtn;
	private Button waiyongBtn;
	private Button riyongBtn;
	private Button xiaoshangpinBtn;
	
	private Button zhaolingBtn;
	private TextView available_money;
	private TextView exit;
	private AlarmManager alarmManager;
	private GestureDetector gestureDetector;
	private String[] array;
	private UartReceiver uartReceiver ;
	private ExitDialog exitDialog;
	private MyDialog changeDialog;
	public static int exitCount;
	
	private void cancelAlarm() {
		Intent intent = new Intent(MainActivity.this, TestActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pendingIntent);
	}
	
	class MyGestureListener extends SimpleOnGestureListener {

		public MyGestureListener(Context context) {
		}

		@Override
		public boolean onDown(MotionEvent e) {
			alarmHomeView();
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			alarmHomeView();
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			alarmHomeView();
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			alarmHomeView();
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			alarmHomeView();
		}

	}
	class TouchReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			System.out.println("action = "+action);
			if(action.equals(TOUCH_ACTION)){
				cancelAlarm();
			}
		}
		
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		return super.dispatchKeyEvent(event);
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_layout);
		mContext = this;	
//		Log.v(TAG, "onCreate");
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		TestApplication.getInstance().addActivity(this);
		
		uartReceiver = new UartReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(UartData.AVAILABLE_MONEY_ACTION);
		intentFilter.addAction(UartData.CHANGE_ENABLE_ACTION);
		intentFilter.addAction(UartData.BUY_SUCCESS_ACTION);
		intentFilter.addAction(UartData.BACK_ZERO_ACTION);
//		intentFilter.addAction(UartData.STOCK_OUT_ACTION);
//		intentFilter.addAction(UartData.AVAILABLE_PRODUCT_ACTION);
//		intentFilter.addAction(UartData.LOCKED_PRODUCT_ACTION);
		registerReceiver(uartReceiver, intentFilter);
		
		initViews();
		
	}
	
	private void initState() {
		// TODO Auto-generated method stub
		available_money.setText("余额：￥"+((float)UartData.validRMB/10));
		if (UartData.validRMB>0) {
			zhaolingBtn.setEnabled(true);
			zhaolingBtn.setBackgroundResource(R.drawable.button_1_normal);
		}else {
			zhaolingBtn.setEnabled(false);
			zhaolingBtn.setBackgroundResource(R.drawable.button_unable);
		}
//		backBtn.setOnClickListener(this);

		
	}
	
	class UartReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(UartData.AVAILABLE_MONEY_ACTION)) {
				
			}else if (action.equals(UartData.CHANGE_ENABLE_ACTION)) {
				boolean change = intent.getExtras().getBoolean("change");
				if (!change) {
					if (changeDialog!=null && changeDialog.isShowing()) {
						changeDialog.setMessage("找零失败，余额不足");
					}
				}else {
					if (changeDialog!=null && changeDialog.isShowing()) {
						changeDialog.setMessage("找零成功，请取走零钱");
					}
				}
				if (changeDialog!=null && changeDialog.isShowing()) {
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								Thread.sleep(2*1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}finally{
								changeDialog.dismiss();
							}
							
						}
					}).start();
				}
				
			}else if (action.equals(UartData.BACK_ZERO_ACTION)) {
				boolean zero = intent.getExtras().getBoolean("back_zero");
				Log.v(TAG, "zero = "+zero);
			}
			available_money.setText("余额：￥"+((float)UartData.validRMB/10));
			if (UartData.validRMB>0) {
				zhaolingBtn.setEnabled(true);
				zhaolingBtn.setBackgroundResource(R.drawable.button_1_normal);
			}else {
				zhaolingBtn.setEnabled(false);
				zhaolingBtn.setBackgroundResource(R.drawable.button_unable);
			}
		}
		
	}

	/**
	 * 获取系统所有的应用程序，并根据APP_PAGE_SIZE生成相应的MyGridView页面
	 */
	public void initViews() {
		
		pointLayout = (LinearLayout) findViewById(R.id.point_page);
		gridLayout = (LinearLayout) findViewById(R.id.grid_layout);
		
		viewPager = (ViewPager) findViewById(R.id.myviewpager);
		viewPager.setOnPageChangeListener(this);
		frontBtn = (Button) findViewById(R.id.front_page);
		nextBtn = (Button) findViewById(R.id.next_page);
		
		cuxiaoBtn = (Button) findViewById(R.id.cuxiao);
		rexiaoBtn = (Button) findViewById(R.id.rexiao);
		tuijianBtn = (Button) findViewById(R.id.tuijian);
		
		shouyeBtn = (Button) findViewById(R.id.shouye);
		jishengBtn = (Button) findViewById(R.id.jisheng);
		qijuBtn = (Button) findViewById(R.id.qiju);
		runhuaBtn = (Button) findViewById(R.id.runhua);
		waiyongBtn = (Button) findViewById(R.id.waiyong);
		riyongBtn = (Button) findViewById(R.id.riyong);
		xiaoshangpinBtn = (Button) findViewById(R.id.xiaoshangpin);
		zhaolingBtn = (Button) findViewById(R.id.zhaoling);
		available_money = (TextView)findViewById(R.id.available_money_text);
		exit = (TextView)findViewById(R.id.exit);
//		backBtn = (Button) findViewById(R.id.back);
		
		frontBtn.setOnClickListener(this);
		frontBtn.setOnTouchListener(this);
		nextBtn.setOnClickListener(this);
		nextBtn.setOnTouchListener(this);
		
		cuxiaoBtn.setOnClickListener(this);
		cuxiaoBtn.setOnTouchListener(this);
		rexiaoBtn.setOnClickListener(this);
		rexiaoBtn.setOnTouchListener(this);
		tuijianBtn.setOnClickListener(this);
		tuijianBtn.setOnTouchListener(this);
		
		shouyeBtn.setOnClickListener(this);
		shouyeBtn.setOnTouchListener(this);
		jishengBtn.setOnClickListener(this);
		jishengBtn.setOnTouchListener(this);
		qijuBtn.setOnClickListener(this);
		qijuBtn.setOnTouchListener(this);
		runhuaBtn.setOnClickListener(this);
		runhuaBtn.setOnTouchListener(this);
		waiyongBtn.setOnClickListener(this);
		waiyongBtn.setOnTouchListener(this);
		riyongBtn.setOnClickListener(this);
		riyongBtn.setOnTouchListener(this);
		xiaoshangpinBtn.setOnClickListener(this);
		xiaoshangpinBtn.setOnTouchListener(this);
		zhaolingBtn.setOnClickListener(this);
		zhaolingBtn.setOnTouchListener(this);
		exit.setOnClickListener(this);
		gestureDetector = new GestureDetector(this, new MyGestureListener(this));
		
		changeBtnBg();
		loadInfo(Constant.SHOUYE_PATH,shouyeBtn.getId());
		shouyeBtn.setBackgroundResource(R.drawable.button_1_highlight);
		
	}
	
	private void loadInfo(String path ,int viewId) {
		// TODO Auto-generated method stub
		File[] files = null;
		File file = new File(path);
		file.mkdirs();
		files = file.listFiles(new FileFilterTest(".gif",".png"));
		if (files==null || files.length<=0) {
			gridLayout.setVisibility(View.GONE);
			return ;
		}
		int fileLength = files.length;
		gridLayout.setVisibility(View.VISIBLE);
		array = new String[fileLength];
		for (int j = 0; j < fileLength; j++) {
			array[j] = files[j].getAbsolutePath();
		}
		SortFile sortFile = new SortFile();

		sortFile.sortedByName(array);

		pageCount = (int) Math.ceil(fileLength / APP_PAGE_SIZE);
		arrayGrid = new ArrayList<MyGridView>();
		if (pageCount<=0) {
			return;
		}
		tv = new TextView[pageCount];
		pointLayout.removeAllViews();
		for (int i = 0; i < pageCount; i++) {
			MyGridView appPage = new MyGridView(this,alarmManager);
			arrayGrid.add(appPage);

			tv[i] = new TextView(this);
			tv[i].setGravity(Gravity.CENTER);
			tv[i].setBackgroundResource(R.drawable.point_normal);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.leftMargin = 5;
			pointLayout.addView(tv[i], params);
		}
		mCurSel = 0;
		tv[mCurSel].setBackgroundResource(R.drawable.point_highlight);
		mIndex = mCurSel;
		
		adapter = new MyViewPagerAdapter(this,array, arrayGrid);
		viewPager.setAdapter(adapter);
	}
	
	private void changeBtnBg() {
		// TODO Auto-generated method stub
		shouyeBtn.setBackgroundResource(R.drawable.button_1_normal);
		jishengBtn.setBackgroundResource(R.drawable.button_1_normal);
		qijuBtn.setBackgroundResource(R.drawable.button_1_normal);
		runhuaBtn.setBackgroundResource(R.drawable.button_1_normal);
		waiyongBtn.setBackgroundResource(R.drawable.button_1_normal);
		riyongBtn.setBackgroundResource(R.drawable.button_1_normal);
		xiaoshangpinBtn.setBackgroundResource(R.drawable.button_1_normal);
	}
	private void alarmHomeView() {
//		Log.v(TAG, "MainActivity get off_time = "+TestApplication.TIME_OFFSET);
		Intent i = new Intent(MainActivity.this, TestActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+TestApplication.TIME_OFFSET*1000,pi);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		initState();
		if (exitDialog!=null && exitDialog.isShowing()) {
			exitDialog.dismiss();
		}
		alarmHomeView();
		super.onResume();
//		Log.v(TAG, "onResume");
	}
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
//		int value = intent.getExtras().getInt("home",0);
//		if (value==1) {
		Bundle bundle = intent.getExtras();
		int value = bundle.getInt("home",0);
		if (value==1) {
			changeBtnBg();
			shouyeBtn.setBackgroundResource(R.drawable.button_1_highlight);
			loadInfo(Constant.SHOUYE_PATH, shouyeBtn.getId());
		}
		if (exitDialog!=null && exitDialog.isShowing()) {
			exitDialog.dismiss();
		}
		alarmHomeView();
//		}
//		Log.v(TAG, "onNewIntent");
	}

	private void setCurrentPage(int index) {
		tv[index].setBackgroundResource(R.drawable.point_highlight);
		tv[mCurSel].setBackgroundResource(R.drawable.point_normal);
		mCurSel = index;
		mIndex = mCurSel;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		setCurrentPage(arg0);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getClass() == TextView.class) {
			// TODO Auto-generated method stub
//			int pos = (Integer) (v.getTag());
//			viewPager.setCurrentItem(pos);
			switch (v.getId()) {
			case R.id.exit:
				if ((++exitCount)==10) {
					exitDialog = new ExitDialog(MainActivity.this, new ExitDialog.AppearDialogListener() {
						
						@Override
						public void onClick(ExitDialog dialog, View view) {
							// TODO Auto-generated method stub
							switch (view.getId()) {
							case R.id.dialog_button_ok:
								String str = dialog.getText();
								if (str.equals("admin")) {
									Intent i = new Intent(mContext,HideSettingsActivity.class);
									startActivity(i);
									cancelAlarm();
								}
								break;
							case R.id.dialog_button_cancel:
								
								break;

							default:
								break;
							}
							if (dialog.isShowing()) {
								dialog.dismiss();
							}
							
						}
					});
					exitDialog.setOnDismissListener(new OnDismissListener() {
						
						@Override
						public void onDismiss(DialogInterface dialog) {
							// TODO Auto-generated method stub
							exitCount = 0;
							alarmHomeView();
						}
					});
					if (exitDialog!=null && !exitDialog.isShowing()) {
						exitDialog.show();
						cancelAlarm();
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									Thread.sleep(60*1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									Thread.interrupted();
								}
								if (exitDialog!=null && exitDialog.isShowing()) {
									exitDialog.dismiss();
								}
							}
						}).start();
					}
				}
				System.out.println("exitCount = "+exitCount);
				break;

			default:
				break;
			}
		} else if (v.getClass() == Button.class) {
			alarmHomeView();
			switch (v.getId()) {
			case R.id.front_page:
				viewPager.setCurrentItem(mIndex <= 0 ? 0 : --mIndex);
				break;
			case R.id.next_page:
				viewPager.setCurrentItem(mIndex >= pageCount - 1 ? pageCount - 1 : ++mIndex);
				break;
			case R.id.cuxiao:
				changeBtnBg();
				loadInfo(Constant.CUXIAO_PATH,v.getId());
				break;
			case R.id.rexiao:
				changeBtnBg();
				loadInfo(Constant.REXIAO_PATH,v.getId());
				break;
			case R.id.tuijian:
				changeBtnBg();
				loadInfo(Constant.TUIJIAN_PATH,v.getId());
				break;
			case R.id.shouye:
				changeBtnBg();
				v.setBackgroundResource(R.drawable.button_1_highlight);
				loadInfo(Constant.SHOUYE_PATH,v.getId());
				break;
			case R.id.jisheng:
				changeBtnBg();
				v.setBackgroundResource(R.drawable.button_1_highlight);
				loadInfo(Constant.JISHENG_PATH,v.getId());
				break;
			case R.id.qiju:
				changeBtnBg();
				v.setBackgroundResource(R.drawable.button_1_highlight);
				loadInfo(Constant.QIJU_PATH,v.getId());
				break;
			case R.id.runhua:
				changeBtnBg();
				v.setBackgroundResource(R.drawable.button_1_highlight);
				loadInfo(Constant.RUNHUA_PATH,v.getId());
				break;
			case R.id.waiyong:
				changeBtnBg();
				v.setBackgroundResource(R.drawable.button_1_highlight);
				loadInfo(Constant.WAIYONG_PATH,v.getId());
				break;
			case R.id.riyong:
				changeBtnBg();
				v.setBackgroundResource(R.drawable.button_1_highlight);
				loadInfo(Constant.RIYONG_PATH,v.getId());
				break;
			case R.id.xiaoshangpin:
				changeBtnBg();
				v.setBackgroundResource(R.drawable.button_1_highlight);
				loadInfo(Constant.XIAOSHANGPIN_PATH,v.getId());
				break;
			case R.id.zhaoling:
				zhaolingBtn.setBackgroundResource(R.drawable.button_1_highlight);
				if (changeDialog==null) {
					changeDialog = new MyDialog(this,"正在找零，请等待...",new MyDialog.AppearDialogListener() {
						
						@Override
						public void onClick(MyDialog dialog, View view) {
							// TODO Auto-generated method stub
							switch (view.getId()) {
							case R.id.dialog_button_ok:
								break;
							case R.id.dialog_button_cancel:
								
								break;
								
							default:
								break;
							}
							if(dialog.isShowing()){
								dialog.dismiss();
							}
						}
					});
					
					changeDialog.setOnDismissListener(new OnDismissListener() {
						
						@Override
						public void onDismiss(DialogInterface dialog) {
							// TODO Auto-generated method stub
							changeDialog.setMessage("正在找零，请等待...");
							alarmHomeView();
						}
					});
				}
				UartData.CHANGE = true;
				if (changeDialog!=null && !changeDialog.isShowing()) {
					changeDialog.show();
					changeDialog.getOkBtn().setVisibility(View.INVISIBLE);
					changeDialog.getCancelBtn().setVisibility(View.INVISIBLE);
				}
				cancelAlarm();
				break;

			default:
				break;
			}
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

		Map<String,String> map = (Map<String,String>)arg0.getItemAtPosition(arg2);
		String imagePath = map.get("cover_path");
		String descPath = map.get("desc_path");
		String number = map.get("number");
		Intent intent = new Intent(MainActivity.this, DetailInfoActivity.class);
		intent.putExtra("icon", imagePath);
		intent.putExtra("label", descPath);
		intent.putExtra("number", number);
		startActivity(intent);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
//		Log.v(TAG, "onPause");
		exitCount = 0;
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
//		Log.v(TAG, "onStop");
		exitCount = 0;
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		Log.v(TAG, "onDestroy");
		unregisterReceiver(uartReceiver);
		uartReceiver = null;
		cancelAlarm();
		TestActivity.FLAG = true;
		super.onDestroy();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		exitCount = 0;
		return gestureDetector.onTouchEvent(event);
	}
	@Override
	public boolean onTouch(View view, MotionEvent motionevent) {
		// TODO Auto-generated method stub
		exitCount = 0;
		System.out.println("onTouch");
		return false;
	}
}
