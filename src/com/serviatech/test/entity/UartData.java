package com.serviatech.test.entity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import android.content.Context;
import android.content.Intent;

public class UartData {
	//����������
	public static Boolean SELECT = false;//��Ʒѡ�б�ʶ
	public static Boolean CHANGE = false;//�����ʶ
	public static int productID = 0;//ѡ����Ʒ�ı��
	public static Boolean QUERY = false;//查询商品是否有效标志，在需要查询商品是否有效时，将该位置为true，将productID置为当前商品编号
	
	public static String AVAILABLE_MONEY_ACTION = "com.android.action.available_money_action";
	public static String BUY_SUCCESS_ACTION = "com.android.action.buy_success_action";
	public static String CHANGE_ENABLE_ACTION = "com.android.action.change_enable_action";
	public static String STOCK_OUT_ACTION = "com.android.action.stock_out_action";
	public static String AVAILABLE_PRODUCT_ACTION = "com.android.action.available_product_action";
	public static String LOCKED_PRODUCT_ACTION = "com.android.action.locked_product_action";
	public static String BACK_ZERO_ACTION = "com.android.action.back_zero_action";

	//�ڲ��������
	public static int validRMB = 0;//��ǰ���õ�����ң���λ��Ԫ��
	public static int priceArray[]=new int[64];
	private FileOutputStream fos;
	private Context mc;
	//�ɱ�ָ��
	private byte select[]={(byte)0xFA,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0xFF};//ѡ����Ʒ�������ֽ�Ϊ��Ʒ���
	private byte query[]={(byte)0xFA,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0xFF};//查询指令
	//����ָ��
	private final byte nothing[]={(byte)0xFA,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xFF};//��λ�����ʱ��Ӧ���ָ��
	private final byte change[] ={(byte)0xFA,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0xFF};//��λ����������
	
	public UartData(Context context,FileOutputStream os) {
		// TODO Auto-generated constructor stub
		mc = context;
		fos=os;
	}
	public void handleData(int cmdArray[]){//串口数据处理

		if(cmdArray.length<5){
			return;
		}
		if(cmdArray[1]==0x0A){//查询
			if(QUERY==true){//查询商品是否有效
				if(productID!=0){
					query[2]=(byte)productID;
					QUERY=false;
					answer(query);
					return;
				}
			}
			if(SELECT==true){//当前要购买商品
				if(productID!=0){
					select[2]=(byte)productID;
					SELECT=false;
					answer(select);
					return;
				}
			}
			if(CHANGE==true){//有找零请求
				CHANGE=false;
				answer(change);
				return;
			}
			answer(nothing);
			
		}else if(cmdArray[1]==0x0B){//收到钱，在交易界面时，应该显示可用钱数（设法通知界面比如用Handler）
			validRMB+=(cmdArray[2]<<8)+cmdArray[3];
			Intent intent = new Intent(AVAILABLE_MONEY_ACTION);
			intent.putExtra("money", validRMB);
			mc.sendBroadcast(intent);
			answer(nothing);
		}else if(cmdArray[1]==0x0D){//以下所有操作结果信息应反馈至界面显示
			if(cmdArray[2]==0){//出货成功
				validRMB-=priceArray[productID];
				Intent intent = new Intent(BUY_SUCCESS_ACTION);
				intent.putExtra("buy_success", true);
				mc.sendBroadcast(intent);
				if(validRMB==0){//应该让找零按钮失效
					
				}
			}else if(cmdArray[2]==1){//找零成功，应该让找零按钮失效
				validRMB = 0;
				Intent intent = new Intent(CHANGE_ENABLE_ACTION);
				intent.putExtra("change", true);
				mc.sendBroadcast(intent);
			}else if(cmdArray[2]==2){//商品缺货
				Intent intent = new Intent(STOCK_OUT_ACTION);
				mc.sendBroadcast(intent);
			}else if(cmdArray[2]==3){//商品无效
				Intent intent = new Intent(AVAILABLE_PRODUCT_ACTION);
				intent.putExtra("available_product", false);
				mc.sendBroadcast(intent);
			}else if(cmdArray[2]==4){//找零失败，（只找了部分钱）
				validRMB=(cmdArray[3]<<8)+cmdArray[4];
				Intent intent = new Intent(CHANGE_ENABLE_ACTION);
				intent.putExtra("change", false);
//				intent.putExtra("money", validRMB);
				mc.sendBroadcast(intent);
			}else if(cmdArray[2]==5){//卡货
				Intent intent = new Intent(LOCKED_PRODUCT_ACTION);
				intent.putExtra("locked_product", true);
				mc.sendBroadcast(intent);
			}else if(cmdArray[2]==6){//商品有效
				Intent intent = new Intent(AVAILABLE_PRODUCT_ACTION);
				intent.putExtra("available_product", true);
				mc.sendBroadcast(intent);
			}else if(cmdArray[2]==7){//正在出货
			
			}else{//其他情况不发回应包
				return;
			}
				
			answer(nothing);
		}else if(cmdArray[1]==0x0F){//商品价格
			if(cmdArray[2]<64){
				priceArray[cmdArray[2]]=(cmdArray[3]<<8)+cmdArray[4];
				answer(nothing);
			}
		}else if(cmdArray[1]==0x10){//清零
			validRMB = 0;
			Intent intent = new Intent(BACK_ZERO_ACTION);
			intent.putExtra("back_zero", true);
			mc.sendBroadcast(intent);
			answer(nothing);
		}
	}
	private void answer(byte ans[]){//Ӧ��ָ��
		try{
			fos.write(ans);
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	public float getPrice(){//�õ���ǰѡ����Ʒ�۸�
		if(productID==0){
			return 0;
		}
		return priceArray[productID];
	}
	public float getValidRMB(){//�õ�����Ǯ��
		
		return validRMB;
	}
}
