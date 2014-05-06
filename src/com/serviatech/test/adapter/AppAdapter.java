package com.serviatech.test.adapter;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.serviatech.test.R;
import com.serviatech.test.entity.UartData;
import com.serviatech.test.utils.Constant;
import com.serviatech.test.utils.ViewHolder;
import com.serviatech.test.widget.GifMovieView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 用于GridView装载数据的适配器
 * @author xxs
 *
 */
public class AppAdapter extends BaseAdapter {
	private List<String> mList;//定义一个list对象
	private Context mContext;//上下文
	public static final int APP_PAGE_SIZE = 9;//每一页装载数据的大小
	/**
	 * 构造方法
	 * @param context 上下文
	 * @param list 所有APP的集合
	 * @param page 当前页
	 */
	public AppAdapter(Context context, String[] filePath, int page) {
		mContext = context;
		mList = new ArrayList<String>();
		//根据当前页计算装载的应用，每页只装载16个
		int i = page * APP_PAGE_SIZE;//当前页的其实位置
		int iEnd = i+APP_PAGE_SIZE;//所有数据的结束位置
		while ((i<filePath.length) && (i<iEnd)) {
			mList.add(filePath[i]);
			i++;
		}
	}
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		Map<String, String> map = new HashMap<String, String>();
		String str = mList.get(position);
		String name = str.subSequence(str.lastIndexOf("/")+1, str.length()).toString();
		String number = str.subSequence(str.lastIndexOf("/")+1, str.lastIndexOf(".")).toString();
		String coverPath = Constant.COVER_PATH + name;
		String descPath = Constant.DESC_PATH + number + ".txt";
		map.put("number", number);
		map.put("name", name);
		map.put("cover_path", coverPath);
		map.put("desc_path", descPath);
		return map;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;  
        if(null==convertView)  
        {  
            holder=new ViewHolder();  
            convertView=LayoutInflater.from(mContext).inflate(R.layout.app_item, null); //mContext指的是调用的Activtty  
            holder.title=(TextView)convertView.findViewById(R.id.tvAppId);  
            holder.img_flag=(ImageView)convertView.findViewById(R.id.ivAppIcon);  
            holder.gifView=(GifMovieView)convertView.findViewById(R.id.gifIcon);  
            holder.price = (TextView)convertView.findViewById(R.id.tvAppPrice);
            convertView.setTag(holder);  
        }  
        else  
        {  
            holder=(ViewHolder)convertView.getTag();  
        } 
		
		String path = mList.get(position);
		String str = path.subSequence(path.lastIndexOf(".")+1, path.length()).toString();
		
		Map<String, String> map = (Map<String, String>)getItem(position);
		/*Uri gif_uri=Uri.parse("file://"+path); //图片地址
		ContentResolver cr=mContext.getContentResolver();
		Bitmap bmp;
		try {
		    bmp = BitmapFactory.decodeStream(cr.openInputStream(gif_uri));
		    appIcon.setImageBitmap(bmp);
		} catch (FileNotFoundException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}*/
//		if (index<APP_PAGE_SIZE) {
//			if (str.startsWith("gif")) {
//				holder.gifView.setVisibility(View.VISIBLE);
//				holder.img_flag.setVisibility(View.GONE);
//				holder.gifView.setMovieResource(path);
//				
//			}else {
				holder.img_flag.setVisibility(View.VISIBLE);
				holder.gifView.setVisibility(View.GONE);
				setImage(path,holder.img_flag);
//			}
//		}
		
//		Bitmap b;
		int number = Integer.parseInt(map.get("number"));
		holder.title.setText("编号："+number);
//		b = BitmapFactory.decodeFile(path, null);
//		appIcon.setImageBitmap(b);
		holder.price.setText("	价格："+((float)UartData.priceArray[number]/10));
		
		return convertView;
	}
	private void setImage(String path,ImageView iv) {
		// TODO Auto-generated method stub
		Bitmap bitmap = null;
		ReferenceQueue<Bitmap> queue = new ReferenceQueue<Bitmap>();
		SoftReference<Bitmap> bitmapref = new SoftReference<Bitmap>(bitmap, queue);
		
		if(bitmapref!=null){  
            bitmap = (Bitmap) bitmapref.get();  
        }  
        bitmap = BitmapFactory.decodeFile(path);  
        iv.setImageBitmap(bitmap);  
       
		
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 设置想要的大小
		int newWidth = 172;
		int newHeight = 164;
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		iv.setImageBitmap(bitmap);
		bitmap = null;
	}
	
	
}
