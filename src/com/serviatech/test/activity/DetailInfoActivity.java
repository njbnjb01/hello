package com.serviatech.test.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.serviatech.test.R;
import com.serviatech.test.application.TestApplication;
import com.serviatech.test.entity.UartData;
import com.serviatech.test.widget.GifMovieView;
import com.serviatech.test.widget.MyDialog;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailInfoActivity extends Activity implements OnClickListener, OnTouchListener{
	private static final String TAG = "DetailInfoActivity";
	private static String TOUCH_ACTION = "com.android.action.ontouch";
	private ImageView iconImage;
	private GifMovieView iconGif;
	private ScrollView scroll;
	private TextView labelText;
	private TextView product_name;
	private TextView product_price;
	private TextView balance;
	private AlarmManager alarmManager;
	private Button homeBtn ;
	private Button backBtn ;
	private Button zhaolingBtn ;
	private Button buyBtn ;
	private GestureDetector gestureDetector;
	private UartReceiver uartReceiver ;
	private IntentFilter intentFilter ;
	private MyDialog buyDialog;
	private MyDialog changeDialog;
	private MyDialog mDialog;
	private Context context;
	private String number;
	private boolean product_available = false;
	private boolean flag = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_info);
		context = this;
		TestApplication.getInstance().addActivity(this);
		
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		if (uartReceiver==null) {
			uartReceiver = new UartReceiver();
		}
		if (intentFilter==null) {
			intentFilter = new IntentFilter();
		}
		
		intentFilter.addAction(UartData.AVAILABLE_MONEY_ACTION);
		intentFilter.addAction(UartData.BUY_SUCCESS_ACTION);
		intentFilter.addAction(UartData.CHANGE_ENABLE_ACTION);
		intentFilter.addAction(UartData.STOCK_OUT_ACTION);
		intentFilter.addAction(UartData.AVAILABLE_PRODUCT_ACTION);
		intentFilter.addAction(UartData.LOCKED_PRODUCT_ACTION);
		intentFilter.addAction(UartData.BACK_ZERO_ACTION);
		registerReceiver(uartReceiver, intentFilter);
		iconImage = (ImageView)findViewById(R.id.infoIcon);
		labelText = (TextView)findViewById(R.id.infoName);
		product_name = (TextView)findViewById(R.id.product_name);
		product_price = (TextView)findViewById(R.id.product_price);
		balance = (TextView)findViewById(R.id.balance);
		
		scroll = (ScrollView)findViewById(R.id.info_scroll);
		scroll.setOnTouchListener(this);
		
		
		iconGif = (GifMovieView)findViewById(R.id.infoGif);
		initInfo(getIntent());
		UartData.QUERY = true;
		UartData.productID = Integer.parseInt(number);
		homeBtn = (Button)findViewById(R.id.home);
		backBtn = (Button)findViewById(R.id.back);
		buyBtn = (Button)findViewById(R.id.buy);
		buyBtn.setEnabled(false);
		zhaolingBtn = (Button)findViewById(R.id.detail_zhaoling_btn);
		
		homeBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		buyBtn.setOnClickListener(this);
		zhaolingBtn.setOnClickListener(this);
		
		gestureDetector = new GestureDetector(this, new MyGestureListener(this));
		
	}
	private void initInfo(Intent i) {
		// TODO Auto-generated method stub
		String icon = i.getExtras().getString("icon");
		String label = i.getExtras().getString("label");
		String name = i.getExtras().getString("name");
		number = i.getExtras().getString("number");
		if (name.endsWith("gif")) {
			iconGif.setMovieResource(icon);
			iconImage.setVisibility(View.GONE);
			iconGif.setVisibility(View.VISIBLE);
		}else {
			setImage(icon);
			iconImage.setVisibility(View.VISIBLE);
			iconGif.setVisibility(View.GONE);
		}
		setDesc(label);
		product_price.setText("￥"+((float)UartData.priceArray[Integer.parseInt(number)]/10));
	}
	private void setDesc(String path) {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		File file = new File(path);
		BufferedReader br;
		boolean flag = true;
		String name = "";
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file),"GB2312");
			br = new BufferedReader(read);
			String line = "";
			while((line = br.readLine())!=null){
				if (flag) {
					flag = false;
					name = line;
				}
				sb.append(line);
			}
			labelText.setText(sb);
			product_name.setText(name);
			br.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			labelText.setText("暂无描述信息");
			e.printStackTrace();
		}
	}
	private void setImage(String path) {
		// TODO Auto-generated method stub
//		path = "/sdcard/serviatech/gg.gif";
		Bitmap bitMap = BitmapFactory.decodeFile(path);
		if (bitMap==null) {
			bitMap = BitmapFactory.decodeResource(getResources(), R.drawable.default_cover);
		}
		int width = bitMap.getWidth();
		int height = bitMap.getHeight();
		// 设置想要的大小
		int newWidth = 500;
		int newHeight = 500;
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		bitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);
		iconImage.setImageBitmap(bitMap);
//		bitMap.recycle();
	}
	
	class UartReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			balance.setText("余额：￥"+((float)UartData.validRMB/10));
			String action = intent.getAction();
			reset();
			if (action.equals(UartData.AVAILABLE_MONEY_ACTION)) {
				
			}else if (action.equals(UartData.STOCK_OUT_ACTION)) {
				/*if (mDialog==null) {
					mDialog = new MyDialog(context);
				}
				if (mFlag && mDialog!=null && !mDialog.isShowing()) {
					mDialog.show();
					mDialog.setMessage("对不起，此商品缺货");
					mDialog.findViewById(R.id.dialog_button_ok).setVisibility(View.INVISIBLE);
					mDialog.findViewById(R.id.dialog_button_cancel).setVisibility(View.INVISIBLE);
				}
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
							mDialog.dismiss();
							mFlag = false;
						}
					}
				}).start();*/
				if (buyDialog != null && buyDialog.isShowing()) {
					buyDialog.setMessage("对不起，此商品缺货");
					buyDialog.findViewById(R.id.dialog_button_ok).setVisibility(View.INVISIBLE);
					buyDialog.findViewById(R.id.dialog_button_cancel).setVisibility(View.INVISIBLE);
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
								buyDialog.dismiss();
							}
							
						}
					}).start();
				}
				
			}else if (/*action.equals(UartData.STOCK_OUT_ACTION) || */action.equals(UartData.AVAILABLE_PRODUCT_ACTION)) {
				if (!buyBtn.isEnabled()) {
					buyBtn.setEnabled(true);
				}
				product_available = intent.getExtras().getBoolean("available_product");
				if (!product_available) {
					if (mDialog==null) {
						mDialog = new MyDialog(context,"对不起，此商品已售完",null);
					}
					if (mDialog!=null && !mDialog.isShowing() && !DetailInfoActivity.this.isFinishing()) {
						mDialog.show();
						mDialog.setMessage("对不起，此商品已售完");
						mDialog.findViewById(R.id.dialog_button_ok).setVisibility(View.INVISIBLE);
						mDialog.findViewById(R.id.dialog_button_cancel).setVisibility(View.INVISIBLE);
					}
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
								mDialog.dismiss();
							}
						}
					}).start();
					
				}else {
					if (flag) {
						if (buyDialog == null) {
							buyDialog = new MyDialog(DetailInfoActivity.this, "是否购买",
									new MyDialog.AppearDialogListener() {

										@Override
										public void onClick(MyDialog dialog, View view) {
											// TODO Auto-generated method stub
											switch (view.getId()) {
											case R.id.dialog_button_ok:
												if (dialog.getOkBtn().getText().equals("确定")) {
													cancel();
													UartData.SELECT = true;
													UartData.productID = Integer.parseInt(number);
													dialog.setMessage("正在交易，请等待...");
													dialog.getOkBtn().setVisibility(View.INVISIBLE);
													dialog.getCancelBtn().setVisibility(View.INVISIBLE);
												}else {
													if (dialog.isShowing()) {
														dialog.dismiss();
													}
													Intent i = new Intent(DetailInfoActivity.this,MainActivity.class);
													i.putExtra("home", 1);
													startActivity(i);
													DetailInfoActivity.this.finish();
												}
												break;
											case R.id.dialog_button_cancel:
												if (dialog.getCancelBtn().getText().equals("取消")) {
													if (dialog.isShowing()) {
														dialog.dismiss();
													}
												}else {
													UartData.CHANGE = true;
													buyDialog.setMessage("正在找零，请等待...");
													dialog.getOkBtn().setVisibility(View.INVISIBLE);
													dialog.getCancelBtn().setVisibility(View.INVISIBLE);
												}
												break;

											default:
												break;
											}
										}
									});
							buyDialog.setOnDismissListener(new OnDismissListener() {

								@Override
								public void onDismiss(DialogInterface dialog) {
									// TODO Auto-generated method stub
									buyDialog.setMessage("是否购买");
									buyDialog.getOkBtn().setText("确定");
									buyDialog.getCancelBtn().setText("取消");
									buyDialog.findViewById(R.id.dialog_button_ok)
											.setVisibility(View.VISIBLE);
									buyDialog.findViewById(R.id.dialog_button_cancel)
											.setVisibility(View.VISIBLE);
									buyDialog.findViewById(R.id.dialog_button_ok)
											.setEnabled(true);
									buyDialog.findViewById(R.id.dialog_button_ok)
											.setBackgroundResource(R.drawable.buy_selector);
									buyDialog.findViewById(R.id.dialog_button_cancel)
											.setEnabled(true);
									buyDialog.findViewById(R.id.dialog_button_cancel)
											.setBackgroundResource(R.drawable.buy_selector);
									reset();
								}
							});
						}
						if (buyDialog != null && !buyDialog.isShowing() && !DetailInfoActivity.this.isFinishing()) {
							buyDialog.show();
							if (product_available) {
								if (UartData.validRMB == 0 || UartData.validRMB < UartData.priceArray[Integer.parseInt(number)]) {
									buyDialog.setMessage("余额不足，请投币");
									buyDialog.getOkBtn().setVisibility(View.INVISIBLE);
									buyDialog.getCancelBtn().setVisibility(View.INVISIBLE);
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
												buyDialog.dismiss();
											}
											
										}
									}).start();
								}
							}/*else {
								if (buyDialog != null && buyDialog.isShowing()) {
									buyDialog.setMessage("对不起，此商品无效");
//									buyDialog.findViewById(R.id.dialog_button_cancel).setEnabled(true);
//									buyDialog.findViewById(R.id.dialog_button_cancel).setBackgroundResource(R.drawable.buy_selector);
									buyDialog.findViewById(R.id.dialog_button_ok).setVisibility(View.INVISIBLE);
									buyDialog.findViewById(R.id.dialog_button_cancel).setVisibility(View.INVISIBLE);
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
												buyDialog.dismiss();
											}
											
										}
									}).start();
								}
							}*/
						}
					}
				}
				/*if (buyDialog != null && buyDialog.isShowing()) {
					buyDialog.setMessage("对不起，此商品无效");
//					buyDialog.findViewById(R.id.dialog_button_cancel).setEnabled(true);
//					buyDialog.findViewById(R.id.dialog_button_cancel).setBackgroundResource(R.drawable.buy_selector);
					buyDialog.findViewById(R.id.dialog_button_ok).setVisibility(View.INVISIBLE);
					buyDialog.findViewById(R.id.dialog_button_cancel).setVisibility(View.INVISIBLE);
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
								buyDialog.dismiss();
							}
							
						}
					}).start();
				}*/
				
			} else if (action.equals(UartData.CHANGE_ENABLE_ACTION)) {
				boolean change = intent.getExtras().getBoolean("change");
				if (!change) {
					if (changeDialog!=null && changeDialog.isShowing()) {
						changeDialog.setMessage("找零失败,零钱不足");
					}
					if (buyDialog!=null && buyDialog.isShowing()) {
						buyDialog.setMessage("找零失败,零钱不足");
					}
				}else {
					if (buyDialog!=null && buyDialog.isShowing()) {
						buyDialog.setMessage("找零成功，请取走零钱");
					}
					if (changeDialog!=null && changeDialog.isShowing()) {
						changeDialog.setMessage("找零成功，请取走零钱");
					}
				}
				if (buyDialog!=null && buyDialog.isShowing()) {
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
								buyDialog.dismiss();
							}
							
						}
					}).start();
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
				
			}else if (action.equals(UartData.BUY_SUCCESS_ACTION)) {
				balance.setText("余额：￥" + ((float) UartData.validRMB / 10));
				if (buyDialog.isShowing()) {
					buyDialog.setMessage("出货成功,请取走您购买的商品");
					if (UartData.validRMB>0) {
						buyDialog.getOkBtn().setVisibility(View.VISIBLE);
						buyDialog.getCancelBtn().setVisibility(View.VISIBLE);
						buyDialog.getOkBtn().setEnabled(true);
						buyDialog.getCancelBtn().setEnabled(true);
						buyDialog.getOkBtn().setText("继续购买");
						buyDialog.getCancelBtn().setText("找零");
						buyDialog.getOkBtn().setBackgroundResource(R.drawable.buy_selector);
						buyDialog.getCancelBtn().setBackgroundResource(R.drawable.buy_selector);
					}else {
						buyDialog.findViewById(R.id.dialog_button_ok).setVisibility(View.INVISIBLE);
						buyDialog.findViewById(R.id.dialog_button_cancel).setVisibility(View.INVISIBLE);
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
									buyDialog.dismiss();
								}
								
							}
						}).start();
					}
				}
			} else if (action.equals(UartData.LOCKED_PRODUCT_ACTION)) {
				if (buyDialog.isShowing()) {
					buyDialog.setMessage("交易失败，货道卡住");
					buyDialog.findViewById(R.id.dialog_button_ok).setVisibility(View.INVISIBLE);
					buyDialog.findViewById(R.id.dialog_button_cancel).setVisibility(View.INVISIBLE);
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
								buyDialog.dismiss();
							}
							
						}
					}).start();
				}
			}else if (action.equals(UartData.BACK_ZERO_ACTION)) {
				boolean zero = intent.getExtras().getBoolean("back_zero");
				System.out.println("money = "+UartData.validRMB);
			}
			if (UartData.validRMB<=0) {
				zhaolingBtn.setEnabled(false);
				zhaolingBtn.setBackgroundResource(R.drawable.zhaoling_unable);
			}else {
				zhaolingBtn.setEnabled(true);
				zhaolingBtn.setBackgroundResource(R.drawable.buy_selector);
			}
		}
		
	}
	private void reset() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(DetailInfoActivity.this, TestActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.set(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + TestApplication.TIME_OFFSET*1000,pendingIntent);
	}
	private void cancel() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(DetailInfoActivity.this, TestActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pendingIntent);
	}
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		if (iconGif==null) {
			iconGif = (GifMovieView)findViewById(R.id.infoGif);
		}
		initInfo(intent);
		UartData.QUERY = true;
		UartData.productID = Integer.parseInt(number);
		reset();
//		Log.v(TAG, "onNewIntent");
		super.onNewIntent(intent);
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
//		Log.v(TAG, "onResume");
		
		reset();
		if (UartData.validRMB<=0) {
			zhaolingBtn.setBackgroundResource(R.drawable.zhaoling_unable);
			zhaolingBtn.setEnabled(false);
		}else {
			zhaolingBtn.setBackgroundResource(R.drawable.buy_selector);
			zhaolingBtn.setEnabled(true);
		}
		balance.setText("余额：￥"+((float)UartData.validRMB/10));
		super.onResume();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
//		Log.v(TAG, "onStop");
		if (iconGif!=null && !iconGif.isPaused()) {
			iconGif.destroyDrawingCache();
			iconGif.setStop();
		}
		iconGif = null;
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		Log.v(TAG, "onDestroy");
		if (buyDialog!=null) {
			buyDialog.dismiss();
		}
		if (mDialog!=null) {
			mDialog.dismiss();
		}
		if (changeDialog!=null) {
			changeDialog.dismiss();
		}
		super.onDestroy();
		unregisterReceiver(uartReceiver);
		uartReceiver = null;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		reset();
		switch (v.getId()) {
		case R.id.home:
			Intent intent = new Intent(this,MainActivity.class);
			intent.putExtra("home", 1);
			startActivity(intent);
			this.finish();
			break;
		case R.id.back:
			this.finish();
			break;
		case R.id.buy:
//			System.out.println("UartData.validRMB = "+UartData.validRMB);
//			System.out.println("UartData.priceArray[Integer.parseInt("+number+")] = "+UartData.priceArray[Integer.parseInt(number)]);
			UartData.QUERY = true;
			UartData.productID = Integer.parseInt(number);
			flag = true;
			break;
		case R.id.detail_zhaoling_btn:
			if (UartData.validRMB>0) {
				if (changeDialog==null) {
					changeDialog = new MyDialog(this,"正在找零，请等待...",null);
					
					changeDialog.setOnDismissListener(new OnDismissListener() {
						
						@Override
						public void onDismiss(DialogInterface dialog) {
							// TODO Auto-generated method stub
							changeDialog.setMessage("正在找零，请等待...");
//							changeDialog.findViewById(R.id.dialog_button_ok).setVisibility(View.VISIBLE);
//							changeDialog.findViewById(R.id.dialog_button_cancel).setVisibility(View.VISIBLE);
//							changeDialog.findViewById(R.id.dialog_button_ok).setEnabled(true);
//							changeDialog.findViewById(R.id.dialog_button_ok).setBackgroundResource(R.drawable.buy_selector);
//							changeDialog.findViewById(R.id.dialog_button_cancel).setEnabled(true);
//							changeDialog.findViewById(R.id.dialog_button_cancel).setBackgroundResource(R.drawable.buy_selector);
							reset();
						}
					});
				}
				UartData.CHANGE = true;
				if (changeDialog!=null && !changeDialog.isShowing() && !DetailInfoActivity.this.isFinishing()) {
					changeDialog.show();
					changeDialog.getOkBtn().setVisibility(View.INVISIBLE);
					changeDialog.getCancelBtn().setVisibility(View.INVISIBLE);
//					changeDialog.getOkBtn().setEnabled(false);
//					changeDialog.getOkBtn().setBackgroundResource(R.drawable.zhaoling_unable);
//					changeDialog.getCancelBtn().setEnabled(false);
//					changeDialog.getCancelBtn().setBackgroundResource(R.drawable.zhaoling_unable);
				}
				cancel();
			}else {
//				Toast.makeText(context, "找零失败，余额不足", Toast.LENGTH_SHORT).show();
			}
			/*if (progressDialog==null) {
				progressDialog = new ProgressDialog(this);
				progressDialog.setMessage("Please wait...");
				progressDialog.setIndeterminate(true);
				progressDialog.setCancelable(false);
			}
			if (!progressDialog.isShowing()) {
				progressDialog.show();
			}*/
			
			break;
		default:
			break;
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return gestureDetector.onTouchEvent(event);
	}
	class MyGestureListener extends SimpleOnGestureListener {

		public MyGestureListener(Context context) {
		}

		@Override
		public boolean onDown(MotionEvent e) {
//			Log.v(TAG, "DOWN");
			reset();
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
//			Log.v(TAG, "SHOW");
			reset();
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
//			Log.v(TAG, "SINGLE UP");
			reset();
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
//			Log.v(TAG, "SCROLL");
			reset();
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
//			Log.v(TAG, "LONG");
			reset();
		}

	}
	class TouchReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			System.out.println("action = "+action);
			if(action.equals(TOUCH_ACTION)){
				Intent i = new Intent(DetailInfoActivity.this, TestActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(DetailInfoActivity.this, 0,
						i, PendingIntent.FLAG_UPDATE_CURRENT);
				alarmManager.cancel(pendingIntent);
			}
		}
		
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onTouch");
		reset();
		return false;
	}
}
