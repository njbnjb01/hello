package com.serviatech.test.widget;


import com.serviatech.test.R;
import com.serviatech.test.entity.UartData;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MyDialog extends Dialog implements android.view.View.OnClickListener{
	private Button okBtn ;
	private Button cancelBtn ;
	private TextView infoText ;
	private Context context;
	private int mId;
	private AppearDialogListener listener;
	private String str;
    public MyDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }
    public MyDialog(Context context, int theme,int productId){
        super(context, theme);
        this.context = context;
        mId = productId;
    }
    public MyDialog(Context context,String str,AppearDialogListener appearListener){
    	super(context, R.style.dialog);
    	this.context = context;
    	this.str = str;
//    	setMessage(str);
    	this.listener = appearListener;
    }
    public interface AppearDialogListener{ 
        public void onClick(MyDialog dialog,View view); 
    } 
    public void setMessage(String msg) {
		// TODO Auto-generated method stub
    	infoText.setText(msg);
	}
    public Button getOkBtn() {
		return okBtn;
	}
    public Button getCancelBtn() {
		return cancelBtn;
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog);
        setCanceledOnTouchOutside(false);
        
        okBtn = (Button)findViewById(R.id.dialog_button_ok);
        cancelBtn = (Button)findViewById(R.id.dialog_button_cancel);
        infoText = (TextView)findViewById(R.id.dialog_text);
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        infoText.setText(str);
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		listener.onClick(this,v);
//		switch (v.getId()) {
//		case R.id.dialog_button_ok:
//			UartData.SELECT=true;
//			UartData.productID=mId;
//			break;
//		case R.id.dialog_button_cancel:
//			
//			break;
//
//		default:
//			break;
//		}
//		if(isShowing()){
//			dismiss();
//		}
	}

}