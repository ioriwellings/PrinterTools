package com.qs.printer.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.Iori.PrintUtils;
import com.qs.helper.printer.Device;
import com.qs.helper.printer.PrintService;
import com.qs.helper.printer.PrinterClass;
import com.qs.helper.printer.bt.BtService;
import com.qs.printer5802.R;

/**
 * 首界面
 * @author Administrator
 *
 */
public class MainActivity extends ListActivity {
	
	private static final String ACTION_USB_PERMISSION = "com.wch.wchusbdriver.USB_PERMISSION";

	public static PrinterClass pl = null;// 打印机操作类
	
	protected static final String TAG = "MainActivity";
	public static boolean checkState = true;
	private Thread tv_update;
	TextView textView_state;
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	Handler mhandler = null;
	Handler handler = null;
//	RecyclerView mRecyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_items);
		
		textView_state = (TextView) findViewById(R.id.textView_state);
		setListAdapter(new SimpleAdapter(this, getData("simple-list-item-2"),
				android.R.layout.simple_list_item_2, new String[] { "title",
						"description" }, new int[] { android.R.id.text1,
						android.R.id.text2 }));

		mhandler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				switch (msg.what)
				{
				case MESSAGE_READ:
					byte[] readBuf = (byte[]) msg.obj;
					Log.e(TAG, "readBuf:" + readBuf[0]);
					if (readBuf[0] == 0x13)
					{
						PrintService.isFUll = true;
						ShowMsg(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_bufferfull));
					}
					else if (readBuf[0] == 0x11)
					{
						PrintService.isFUll = false;
						ShowMsg(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_buffernull));
					}
					else if (readBuf[0] == 0x08)
					{
						ShowMsg(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_nopaper));
					}
					else if (readBuf[0] == 0x01)
					{
						//ShowMsg(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_printing));
					}
					else if (readBuf[0] == 0x04)
					{
						ShowMsg(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_hightemperature));
					}
					else if (readBuf[0] == 0x02)
					{
						ShowMsg(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_lowpower));
					}
					else
					{
						String readMessage = new String(readBuf, 0, msg.arg1);
						Log.e("", "readMessage"+readMessage);
						if (readMessage.contains("800"))// 80mm paper
						{
							PrintService.imageWidth = 72;
							Toast.makeText(MainActivity.this, "80mm",
									Toast.LENGTH_SHORT).show();
							Log.e("", "imageWidth:"+"80mm");
						} else if (readMessage.contains("580"))// 58mm paper
						{
							PrintService.imageWidth = 48;
							Toast.makeText(MainActivity.this, "58mm",
									Toast.LENGTH_SHORT).show();
							Log.e("", "imageWidth:"+"58mm");
						}
					}
					break;
				case MESSAGE_STATE_CHANGE:// 蓝牙连接状态
					switch (msg.arg1)
					{
					case PrinterClass.STATE_CONNECTED:// 已经连接
						break;
					case PrinterClass.STATE_CONNECTING:// 正在连接
						Toast.makeText(getApplicationContext(),
								"STATE_CONNECTING", Toast.LENGTH_SHORT).show();
						break;
					case PrinterClass.STATE_LISTEN:
					case PrinterClass.STATE_NONE:
						break;
					case PrinterClass.SUCCESS_CONNECT://连接成功
						pl.write(new byte[] { 0x1b, 0x2b });// 检测打印机型号
						Toast.makeText(getApplicationContext(),
								"SUCCESS_CONNECT", Toast.LENGTH_SHORT).show();
						pl.write(PrintUtils.ALIGN_CENTER);
						pl.write(new byte[]{0x1B, 0x21, 0x00});
                        pl.write(new byte[]{0x1B, 0x4d, 0x00});
                        PrintUtils.printText("美食餐厅\n\n");
                        PrintUtils.selectCommand(PrintUtils.DOUBLE_HEIGHT_WIDTH);
                        PrintUtils.printText("桌号：1号桌\n\n");
                        PrintUtils.selectCommand(PrintUtils.NORMAL);
                        pl.write(new byte[]{0x1B, 0x21, 0x00});
                        pl.write(new byte[]{0x1B, 0x4d, 0x00});
                        PrintUtils.selectCommand(PrintUtils.ALIGN_LEFT);
                        PrintUtils.printText(PrintUtils.printTwoData("订单编号", "201507161515\n"));
                        PrintUtils.printText(PrintUtils.printTwoData("点菜时间", "2016-02-16 10:46\n"));
                        PrintUtils.printText(PrintUtils.printTwoData("上菜时间", "2016-02-16 11:46\n"));
                        PrintUtils.printText(PrintUtils.printTwoData("人数：2人", "收银员：张三\n"));

                        PrintUtils.printText("------------------------------\n");
                        PrintUtils.selectCommand(PrintUtils.BOLD);
                        PrintUtils.printText(PrintUtils.printThreeData("项目", "数量", "金额\n"));
                        PrintUtils.printText("------------------------------\n");
                        PrintUtils.selectCommand(PrintUtils.BOLD_CANCEL);
                        PrintUtils.printText(PrintUtils.printThreeData("面", "1", "0.00\n"));
                        PrintUtils.printText(PrintUtils.printThreeData("米饭", "1", "6.00\n"));
                        PrintUtils.printText(PrintUtils.printThreeData("铁板烧", "1", "26.00\n"));
                        PrintUtils.printText(PrintUtils.printThreeData("一个测试", "1", "226.00\n"));
                        PrintUtils.printText(PrintUtils.printThreeData("牛肉面啊啊", "1", "2226.00\n"));
                        PrintUtils.printText(PrintUtils.printThreeData("牛肉面啊啊啊牛肉面啊啊啊", "888", "98886.00\n"));

                        PrintUtils.printText("--------------------------------\n");
                        PrintUtils.printText(PrintUtils.printTwoData("合计", "53.50\n"));
                        PrintUtils.printText(PrintUtils.printTwoData("抹零", "3.50\n"));
                        PrintUtils.printText("--------------------------------\n");
                        PrintUtils.printText(PrintUtils.printTwoData("应收", "50.00\n"));
                        PrintUtils.printText("--------------------------------\n");

                        PrintUtils.selectCommand(PrintUtils.ALIGN_LEFT);
                        PrintUtils.printText("备注：不要辣、不要香菜");
                        PrintUtils.printText("\n\n\n");
						break;
					case PrinterClass.FAILED_CONNECT://连接失败
						Toast.makeText(getApplicationContext(),
								"FAILED_CONNECT", Toast.LENGTH_SHORT).show();
						break;
					case PrinterClass.LOSE_CONNECT://连接丢失
						Toast.makeText(getApplicationContext(), "LOSE_CONNECT",
								Toast.LENGTH_SHORT).show();
					}
					break;
				case MESSAGE_WRITE:

					break;
				}
				super.handleMessage(msg);
			}
		};

		handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				switch (msg.what)
				{
				case 0:
					break;
				case 1:// 获取蓝牙端口号					
					Device d = (Device) msg.obj; //获取设备信息 deviceAddress-> 78:4F:43:89:70:CB， deviceName->丁春宇的MacBook Pro
					if (d != null)
					{
						if (PrintSettingActivity.deviceList == null)
						{
							PrintSettingActivity.deviceList = new ArrayList<Device>();
						}
						if (!checkData(PrintSettingActivity.deviceList, d))
						{
							PrintSettingActivity.deviceList.add(d);
						}
					}
					break;
				case 2:				
					break;
				}
			}
		};

		tv_update = new Thread()
		{
			public void run()
			{
				while (true)
				{
					if (checkState)
					{
						try
						{
							Thread.sleep(500);
						}
						catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						textView_state.post(new Runnable()
						{
							@Override
							public void run()
							{
								// TODO Auto-generated method stub
								if (MainActivity.pl != null)
								{
									if (MainActivity.pl.getState() == PrinterClass.STATE_CONNECTED)
									{
										//已经连接
										textView_state.setText(MainActivity.this
														.getResources()
														.getString(
																R.string.str_connected));
									}
									else if (MainActivity.pl.getState() == PrinterClass.STATE_CONNECTING)
									{
										//连接中
										textView_state.setText(MainActivity.this
														.getResources()
														.getString(
																R.string.str_connecting));
									}
									else if (MainActivity.pl.getState() == PrinterClass.LOSE_CONNECT
											|| MainActivity.pl.getState() == PrinterClass.FAILED_CONNECT)
									{
										//丢失连接和连接失败
										checkState = false;
										textView_state
												.setText(MainActivity.this
														.getResources()
														.getString(
																R.string.str_disconnected));
										Intent intent = new Intent();
										intent.setClass(MainActivity.this,
												PrintSettingActivity.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
										//跳转至PrintSettingActivity
										startActivity(intent);
									}
									else
									{
										//无连接 state = 0;
										textView_state
												.setText(MainActivity.this
														.getResources()
														.getString(
																R.string.str_disconnected));
									}
								}
							}
						});
					}
				}
			}
		};
		tv_update.start();
		

		MainActivity.pl =new BtService(this, mhandler, handler);
		
		Intent intent = new Intent();
		intent.putExtra("position", 0);
		intent.setClass(MainActivity.this, PrintActivity.class);
		//无连接时候跳转至PrintActivity
		startActivity(intent);
		
	}
	
	 /**
	  * 字节数组转字符串
	  *  
	 */
	 public static StringBuffer bytesToString(byte[] bytes)
	 {
	  StringBuffer sBuffer = new StringBuffer();
	  for (int i = 0; i < bytes.length; i++)
	  {
	   String s = Integer.toHexString(bytes[i] & 0xff);
	   if (s.length() < 2)
	    sBuffer.append('0');
	   sBuffer.append(s + " ");
	  }
	  return sBuffer;
	 }

	
	@Override
	protected void onResume() {
		super.onResume();
	}

	protected void onListItemClick(ListView listView, View v, int position,
			long id) {
        //list表监听
		MainActivity.checkState = true;
		Intent intent = new Intent();
		intent.putExtra("position", position);
		intent.setClass(MainActivity.this, PrintActivity.class);
		
		switch (position) {
		case 0:
			startActivity(intent);
			break;
		case 1:
			startActivity(intent);
			break;
		case 2:
			break;
		}
	}
	private List<Map<String, String>> getData(String title) {
		List<Map<String, String>> listData = new ArrayList<Map<String, String>>();

		Map<String, String> map = new HashMap<String, String>();
		map.put("title", getResources().getString(R.string.mode_bt));
		map.put("description", "");
		listData.add(map);

		return listData;
	}

	private boolean checkData(List<Device> list, Device d) {
		for (Device device : list) {
			if (device.deviceAddress.equals(d.deviceAddress)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		checkState = true;
		super.onRestart();
	}
	
	private void ShowMsg(String msg){
		Toast.makeText(getApplicationContext(), msg,
				Toast.LENGTH_SHORT).show();
	}
	
	
}
