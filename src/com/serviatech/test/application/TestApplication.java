package com.serviatech.test.application;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.serviatech.test.entity.UartRxTask;
import com.serviatech.test.utils.Constant;
import com.serviatech.test.utils.FileFilterTest;
import com.via.SmartETK;
import com.via.UartConfig;


import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class TestApplication extends Application {
	private static String TAG = "TestApplication";
	private Map<String, String> videoItems;
	private Map<String, String> picsItems;
	public List<Map<String, String>> videoList;
	public List<Map<String, String>> picList;
	private UartRxTask mRxTask;
	private FileOutputStream mFOS;
	public File[] mFiles;
	public static int TIME_OFFSET = 0;

	private List<Activity> activityList = new LinkedList<Activity>();
	private static TestApplication instance;


	// 单例模式中获取唯一的TestApplication实例
	public static TestApplication getInstance() {
//		if (null == instance) {
//			instance = new TestApplication();
//		}
		return instance;

	}

	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// 遍历所有Activity并finish

	public void exit() {
		for (Activity activity : activityList) {
			if (activity!=null && !activity.isFinishing()) {
//				System.out.println(activity.getComponentName());
				activity.finish();
			}
		}
		System.exit(0);
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		SharedPreferences sharedPreferences = getSharedPreferences("save_time",
				Context.MODE_PRIVATE);
		TIME_OFFSET = sharedPreferences.getInt("time", 60);
		Log.i(TAG, "TestApplication onCreate off_time = " + TIME_OFFSET);

		videoList = new ArrayList<Map<String, String>>();
		picList = new ArrayList<Map<String, String>>();
		File video = new File(Constant.VIDEO_PATH);
		File pic = new File(Constant.PICS_PATH);
		if (video.exists()) {
			Log.i(TAG, "videos exists");
			videoList = searchVideo(video);
			picList = searchPics(pic);
			// videoList = null;
			if (videoList.size() <= 0 && pic.exists()) {
				Log.i(TAG, "pics exists");
				picList = searchPics(pic);
			}
		} else {
			if (pic.exists()) {
				picList = searchPics(pic);
			}
		}
		mFiles = getShouyeFiles();
		uartConnect();
		udiskPluggedin();
	}

	public File[] getShouyeFiles() {
		File file = new File(Constant.SHOUYE_PATH);
		file.mkdirs();
		File[] files = file.listFiles(new FileFilterTest(".gif",".png"));
		return files;
	}

	/* 监听U盘插拔 */
	private void udiskPluggedin() {
		// TODO Auto-generated method stub

		IntentFilter filter = new IntentFilter();
		filter.addAction("android.hardware.usb.action.USB_STATE");
		filter.addAction("android.hardware.action.USB_DISCONNECTED");
		filter.addAction("android.hardware.action.USB_CONNECTED");

		filter.addAction("android.intent.action.UMS_CONNECTED");
		filter.addAction("android.intent.action.UMS_DISCONNECTED");
//		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
//		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
//		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
//		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		filter.addAction(Intent.ACTION_MEDIA_CHECKING);
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_EJECT);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addDataScheme("file");
		UdiskReceiver mReceiver = new UdiskReceiver();
		registerReceiver(mReceiver, filter);

	}

	private class UdiskReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action = arg1.getAction();
			Log.v(TAG, "action = " + action);
			if (action.equals(Intent.ACTION_MEDIA_CHECKING)) {
				// Toast.makeText(getApplicationContext(), "正在挂载U盘",
				// Toast.LENGTH_SHORT).show();
			} else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
				// Toast.makeText(getApplicationContext(), "U盘挂载成功",
				// Toast.LENGTH_SHORT).show();
				for(Activity activity : activityList){
//					String cls = activity.getComponentName().getClassName();
//					System.out.println("class name = "+cls);
//					if (cls.equals("TestActivity")) {
					if (activity!=null && !activity.isFinishing()) {
						activity.finish();
					}
				}
				Intent intent = new Intent();
				intent.setClassName("com.serviatech.test",
						"com.serviatech.test.activity.UpdateActivity");
				intent.setFlags(/* Intent.FLAG_ACTIVITY_CLEAR_TOP | */Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);

			} else if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
				Toast.makeText(getApplicationContext(), "U盘已移除",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	/**
	 * 串口建立连接
	 */
	private void uartConnect() {
		// TODO Auto-generated method stub
		SmartETK.Init();
		String notePath;

		if (mRxTask != null) {
			Toast.makeText(this, "Please disconnect !!", Toast.LENGTH_LONG)
					.show();
			return;
		}

		// if (mTxtDevName.getText().length()>0) {
		// notePath = "/dev/" + mTxtDevName.getText().toString();
		// } else {
		// notePath = "/dev/" + mTxtDevName.getHint().toString();
		// }
		notePath = "/dev/ttyS2";
		try {
			File f = new File(notePath); // select device file
			mFOS = new FileOutputStream(f); // using FileOutputStream
			FileDescriptor fd = mFOS.getFD(); // get FileDescriptor from
												// FileOutputStream
			UartConfig cfg = SmartETK.Uart_GetConfig(fd); // get current uart
															// setting
			// cfg.ospeed = UartConfig.B115200; // setup out speed
			// cfg.ispeed = UartConfig.B115200; // setup in speed
			// cfg.c_lflag = UartConfig.ECHOE | UartConfig.ECHOK |
			// UartConfig.ECHOKE | UartConfig.ECHOCTL;
			// cfg.c_iflag = UartConfig.ICRNL | UartConfig.IXON;
			// cfg.c_oflag = UartConfig.OPOST | UartConfig.ONLCR; //
			// UartConfig.OPOST will effect APC double line,
			// but will not return line head in PC

			cfg.ospeed = UartConfig.B9600; // setup out speed
			cfg.ispeed = UartConfig.B9600; // setup in speed
			cfg.c_iflag = 0;
			cfg.c_oflag = UartConfig.OCRNL | UartConfig.ONLCR;
			cfg.c_lflag = 0;
			cfg.c_cflag = UartConfig.CS8 | UartConfig.CLOCAL | UartConfig.CREAD
					| UartConfig.CSTOPB;

			Log.i("UART",
					"Set " + notePath + ": " + SmartETK.Uart_SetConfig(fd, cfg)); // apply
																					// setting
			mRxTask = new UartRxTask(getApplicationContext(),/* mHandler, */fd,
					notePath); // open thread to read
			mRxTask.start(); // run thread
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<Map<String, String>> getVideoList() {
		return videoList;
	}

	public List<Map<String, String>> getPicsList() {
		return picList;
	}

	public List<Map<String, String>> searchVideo(File filepath) {

		// 判断SD卡是否存在
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File[] files = filepath.listFiles();
			List<Map<String, String>> video = new ArrayList<Map<String, String>>();
			if (files.length > 0) {
				for (File file : files) {
					if (file.isDirectory()) {
						// 如果目录可读就执行（一定要加，不然会挂掉）
						if (file.canRead()) {
							searchVideo(file); // 如果是目录，递归查找
						}
					} else {
						// 判断是文件，则进行文件名判断
						try {
							/*
							 * if (file.getName().indexOf(keyword) > -1 ||
							 * file.getName().indexOf(keyword.toUpperCase()) >
							 * -1) { rowItem = new HashMap<String, Object>();
							 * rowItem.put("number", index); // 加入序列号
							 * rowItem.put("bookName", file.getName());// 加入名称
							 * rowItem.put("path", file.getPath()); // 加入路径
							 * rowItem.put("size", file.length()); // 加入文件大小
							 * list.add(rowItem); index++; }
							 */
							String fileName = file.getName();
							String extension = fileName.subSequence(
									fileName.lastIndexOf(".") + 1,
									fileName.length()).toString();
							if (extension.toLowerCase().equals("mp4")
									|| extension.toLowerCase().equals("3gp")) {
								videoItems = new HashMap<String, String>();
								// rowItem.put("videoname", file.getName());//
								// 加入名称
								videoItems.put("videopath", file.getPath()); // 加入路径
//								System.out.println(file.getPath());
								// rowItem.put("videosize", "" + file.length());
								// // 加入文件大小
								video.add(videoItems);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			}
			return video;
		}
		return videoList;
	}

	public List<Map<String, String>> searchPics(File filepath) {

		// 判断SD卡是否存在
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			List<Map<String, String>> pic = new ArrayList<Map<String, String>>();
			File[] files = filepath.listFiles();

			if (files.length > 0) {
				for (File file : files) {
					if (file.isDirectory()) {
						// 如果目录可读就执行（一定要加，不然会挂掉）
						if (file.canRead()) {
							searchPics(file); // 如果是目录，递归查找
						}
					} else {
						// 判断是文件，则进行文件名判断
						try {
							String fileName = file.getName();
							String extension = fileName.subSequence(
									fileName.lastIndexOf(".") + 1,
									fileName.length()).toString();
							if (extension.toLowerCase().equals("jpg")
									|| extension.toLowerCase().equals("png")
									|| extension.toLowerCase().equals("gif")
									|| extension.toLowerCase().equals("bmp")) {
								picsItems = new HashMap<String, String>();
								picsItems.put("picpath", file.getPath()); // 加入路径
//								System.out.println(file.getPath());
								pic.add(picsItems);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			}
			return pic;
		}
		return picList;
	}

}
