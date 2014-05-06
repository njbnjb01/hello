package com.serviatech.test.widget;

import com.serviatech.test.activity.MainActivity;
import com.serviatech.test.activity.ScreenSaverActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MyButton extends Button{
	private Context c;
	private AlarmManager alarmManager;
	public MyButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		c = context;
		alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
	}
	public MyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		c = context;
		alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
	}
	public MyButton(Context context) {
		super(context);
		c = context;
		alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		System.out.println("mybutton event.action = "+event.getAction());
		Intent i = new Intent();
		i.setClassName(c, "com.serviatech.test.activity.ScreenSaverActivity");
		PendingIntent pi = PendingIntent.getActivity(c, 0, i,PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pi);
		return super.onTouchEvent(event);
	}
}
