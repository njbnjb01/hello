package com.serviatech.test.activity;

import com.serviatech.test.R;
import com.serviatech.test.utils.AppUtils;
import com.serviatech.test.utils.Constant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateActivity extends Activity {
//	private ImageView updateInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_layout);
//		updateInfo = (ImageView)findViewById(R.id.update_image);
		Log.v("-----", "UpdateActivity onCreate");
//		final String originalPath = "/udisk/aiwuji_dir/";
//		final String targetPath = "/sdcard/aiwuji_dir/";
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					AppUtils.copyFolder(Constant.COPY_PATH, Constant.BASE_PATH);
//					AppUtils.copyDirectiory(Constant.COPY_PATH, Constant.BASE_PATH);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Looper.prepare();
					Toast.makeText(UpdateActivity.this, "更新资源失败", 1000).show();
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(1);
			}
		}).start();
	}
	
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Log.v("-----", "更新已完成");
				Intent intent = new Intent(UpdateActivity.this,TestActivity.class);
				intent.putExtra("update", 1);
				startActivity(intent);
				TestActivity.FLAG = true;
				UpdateActivity.this.finish();
				break;

			default:
				break;
			}
		};
	};
}
