package com.serviatech.test.activity;

import java.util.ArrayList;
import java.util.List;

import com.serviatech.test.adapter.AppAdapter;
import com.serviatech.test.adapter.MyViewPagerAdapter;
import com.serviatech.test.widget.MyGridView;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.serviatech.test.R;
/**
 * 实现横向滑动的MyGridView
 * @author xxs
 */
public class HorizonGridActivity extends Activity {
	private static final float APP_PAGE_SIZE = 16.0f;
	private MyViewPagerAdapter adapter;
	private ArrayList<MyGridView>array;
	private ViewPager viewPager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
		viewPager = (ViewPager)findViewById(R.id.myviewpager);
//		adapter = new MyViewPagerAdapter(this, array);//new MyViewPagerAdapter(this, array);
		viewPager.setAdapter(adapter);
	}
	/**
	 * 获取系统所有的应用程序，并根据APP_PAGE_SIZE生成相应的MyGridView页面
	 */
	public void initViews() {
		final PackageManager packageManager = getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // get all apps 
        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
        // the total pages
        final int PageCount = (int)Math.ceil(apps.size()/APP_PAGE_SIZE);
        array = new ArrayList<MyGridView>();
//        for (int i=0; i<PageCount; i++) {
//        	MyGridView appPage = new MyGridView(this);
//        	appPage.setAdapter(new AppAdapter(this, apps, i));
//        	appPage.setNumColumns(4);
//        	array.add(appPage);
//        }
	}
}
