package com.serviatech.test.utils;

import com.serviatech.test.widget.GifMovieView;

import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {
	
	public TextView title;  
	public ImageView img_flag;
	public GifMovieView gifView;
	public TextView price;
    
	public TextView getTitle() {
		return title;
	}
	public void setTitle(TextView title) {
		this.title = title;
	}
	public ImageView getImg_flag() {
		return img_flag;
	}
	public void setImg_flag(ImageView img_flag) {
		this.img_flag = img_flag;
	}
	public GifMovieView getGifView() {
		return gifView;
	}
	public void setGifView(GifMovieView gifView) {
		this.gifView = gifView;
	}
}   
