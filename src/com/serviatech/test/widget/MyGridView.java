package com.serviatech.test.widget;

import com.serviatech.test.activity.MainActivity;
import com.serviatech.test.application.TestApplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

public class MyGridView extends GridView {
	private Context c;
	private AlarmManager alarmManager;
	public MyGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		c = context;
	}
	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		c = context;
	}
	public MyGridView(Context context) {
		super(context);
		c = context;
	}
	public MyGridView(Context context,AlarmManager alarm) {
		super(context);
		c = context;
		alarmManager = alarm;
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		MainActivity.exitCount = 0;
//		System.out.println("MyGridView event action = "+ev.getAction());
		Intent i = new Intent();
		i.setClassName(c, "com.serviatech.test.activity.TestActivity");
		PendingIntent pendingIntent = PendingIntent.getActivity(c, 0,
				i, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + TestApplication.TIME_OFFSET*1000,pendingIntent);
//		c.sendBroadcast(new Intent("com.android.action_ontouch"));
		return super.onTouchEvent(ev);
	}

}
