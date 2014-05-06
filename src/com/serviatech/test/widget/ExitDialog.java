package com.serviatech.test.widget;


import com.serviatech.test.R;
import com.serviatech.test.entity.UartData;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ExitDialog extends Dialog implements android.view.View.OnClickListener{
	private Button okBtn ;
	private Button cancelBtn ;
	private EditText infoText ;
	private Context context;
	private AppearDialogListener listener;
    public ExitDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }
    public ExitDialog(Context context, int theme){
        super(context, theme);
        this.context = context;
    }
    public ExitDialog(Context context,AppearDialogListener appearListener){
    	super(context, R.style.dialog);
    	this.context = context;
    	this.listener = appearListener;
    }
    public interface AppearDialogListener{ 
        public void onClick(ExitDialog dialog,View view); 
    } 
    public Button getOkBtn() {
		return okBtn;
	}
    public Button getCancelBtn() {
		return cancelBtn;
	}
    public String getText() {
		// TODO Auto-generated method stub
    	return infoText.getText().toString().trim();
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.exit_dialog);
        setCanceledOnTouchOutside(false);
        
        okBtn = (Button)findViewById(R.id.dialog_button_ok);
        cancelBtn = (Button)findViewById(R.id.dialog_button_cancel);
        infoText = (EditText)findViewById(R.id.dialog_text);
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		listener.onClick(this,v);
	}

}