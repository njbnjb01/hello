package com.serviatech.test.activity;

import com.serviatech.test.R;
import com.serviatech.test.application.TestApplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;

public class HideSettingsActivity extends Activity implements OnClickListener {
	private static final String TAG = "HideSettingsActivity";
	private EditText timeEdit;
	private Button saveBtn;
	private Button quitBtn;
	private Button exitBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hide_settings_layout);
		TestApplication.getInstance().addActivity(this);
		Log.v(TAG, "onCreate()");
		timeEdit = (EditText)findViewById(R.id.screen_saver_time);
		saveBtn = (Button)findViewById(R.id.save_btn);
		quitBtn = (Button)findViewById(R.id.quit_btn);
		exitBtn = (Button)findViewById(R.id.exit_btn);
		
		saveBtn.setOnClickListener(this);
		quitBtn.setOnClickListener(this);
		exitBtn.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.save_btn:
			String str = timeEdit.getText().toString().trim();
			if (!str.equals("")) {
				SharedPreferences sharedPreferences = getSharedPreferences("save_time", Context.MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();//获取编辑器
				editor.putInt("time", Integer.parseInt(str));
				editor.commit();//提交修改
				SharedPreferences sharedPreference = getSharedPreferences("save_time", Context.MODE_PRIVATE);
				TestApplication.TIME_OFFSET = sharedPreference.getInt("time", 60);
				System.out.println(TAG+"==>>TestApplication.TIME_OFFSET = "+TestApplication.TIME_OFFSET);
				this.finish();
			}
			break;
		case R.id.quit_btn:
			this.finish();
			break;
		case R.id.exit_btn:
//			Intent intent = new Intent(Intent.ACTION_MAIN);
//			intent.addCategory(Intent.CATEGORY_HOME);
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);
//			android.os.Process.killProcess(android.os.Process.myPid());
			cancel();
			TestApplication.getInstance().exit();
			break;

		default:
			break;
		}
	}
	private void cancel() {
		// TODO Auto-generated method stub
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(HideSettingsActivity.this, TestActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pendingIntent);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		timeEdit.setText("");
		super.onDestroy();
	}

}
