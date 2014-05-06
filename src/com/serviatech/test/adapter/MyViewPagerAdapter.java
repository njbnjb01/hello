package com.serviatech.test.adapter;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.serviatech.test.activity.DetailInfoActivity;
import com.serviatech.test.activity.MainActivity;
import com.serviatech.test.widget.MyGridView;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
/**
 * 实现ViewPager页卡切换的适配器
 * @author Administrator
 *
 */
public class MyViewPagerAdapter extends PagerAdapter implements OnItemClickListener{
	private List<MyGridView> array;
	private String[] mPath;
	private Context mc;
	/**
	 * 供外部调用（new）的方法
	 * @param context  上下文
	 * @param imageViews    添加的序列对象
	 */
	public MyViewPagerAdapter(Context context, String[] filepath, List<MyGridView> array) {
		mc = context;
		this.array=array;
		mPath = filepath;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return array.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
//		Log.v("ViewPagerAdapter", "isViewFromObject");
		return arg0 == arg1;
	}
	@Override
	public Object instantiateItem(View arg0, int arg1)
	{
		MyGridView gridview = array.get(arg1);
		gridview.setVerticalScrollBarEnabled(false);
		gridview.setAdapter(new AppAdapter(mc, mPath, arg1));
		gridview.setOnItemClickListener(this);
		gridview.setNumColumns(3);
		gridview.setPadding(20, 10, 20, 0);
		gridview.setGravity(Gravity.CENTER);
		((ViewPager) arg0).addView(gridview);
		return gridview;
	}
	

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2)
	{
		((ViewPager) arg0).removeView((View) arg2);
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Map<String,String> map = (Map<String,String>)arg0.getItemAtPosition(arg2);
		String imagePath = map.get("cover_path");
		String descPath = map.get("desc_path");
		String number = map.get("number");
		String name = map.get("name");
		Intent intent = new Intent(mc, DetailInfoActivity.class);
		intent.putExtra("icon", imagePath);
		intent.putExtra("label", descPath);
		intent.putExtra("number", number);
		intent.putExtra("name", name);
		mc.startActivity(intent);
	}

}
