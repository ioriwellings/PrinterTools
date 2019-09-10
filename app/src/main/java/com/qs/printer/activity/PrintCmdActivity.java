package com.qs.printer.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.qs.printer5802.R;

public class PrintCmdActivity extends ListActivity {
	
	EditText editText_custom_instruction;
	Button button_send_instruction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_print_cmd);

		editText_custom_instruction = (EditText) findViewById(R.id.editText_custom_instruction);
		button_send_instruction = (Button) findViewById(R.id.button_send_instruction);
		button_send_instruction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					String cmdStr = editText_custom_instruction.getText()
							.toString();
					byte[] btcmd = hexStringToBytes(cmdStr);
					MainActivity.pl.write(btcmd);
				} catch (Exception ex) {
					Toast toast = Toast.makeText(PrintCmdActivity.this, ex
							.getMessage().toString(), Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
		setListAdapter(new SimpleAdapter(this, getData("simple-list-item-2"),
				android.R.layout.simple_list_item_2, new String[] { "title",
						"description" }, new int[] { android.R.id.text1,
						android.R.id.text2 }));
	}

	protected void onListItemClick(ListView listView, View v, int position,
			long id) {
		/*
		 * Map map = (Map)listView.getItemAtPosition(position); Toast toast =
		 * Toast.makeText(this, map.get("title")+" is selected.",
		 * Toast.LENGTH_LONG); toast.show();
		 */
		Map map = (Map) listView.getItemAtPosition(position);
		String titleStr = map.get("title").toString();
		String cmd = map.get("description").toString();
		Object lanStr=map.get("language");
		if(lanStr!=null)
		{
//			PrintService.LanguageStr=lanStr.toString();
//			Log.i(TAG, "set print language to "+titleStr+","+PrintService.LanguageStr);
		}
		
		if(titleStr.contains("printerName")||titleStr.contains("蓝牙名称")){
			
			alert_edit();
			
		}else{
		
		editText_custom_instruction.setText(cmd);
		byte[] bt = hexStringToBytes(cmd);
		MainActivity.pl.write(bt);
		
		}
	}
	
    public void alert_edit(){
        final EditText et = new EditText(this);
        new AlertDialog.Builder(this).setTitle("请输入蓝牙名称")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //按下确定键后的事件
//                        Toast.makeText(getApplicationContext(), et.getText().toString(),Toast.LENGTH_LONG).show();
                    	
                    	String str=et.getText().toString();
                    	if(str.length()<=0){
                    		
                    		Toast.makeText(getApplicationContext(), "请输入蓝牙名称", 0).show();
                    		
                    	}else{
                    	
                    	  byte[] str_byte=str.getBytes();
                    	  
                    	  byte[] str_start=new byte[]{0x10,(byte) 0xff,(byte) 0xf9};
                    	  
                    	  byte[] str_1=concat(str_start, str_byte);
                    	  
                    	  byte[] str_end=new byte[]{0x0d,(byte) 0x0a};
                    	  
                    	  byte[] send=concat(str_1, str_end);
                    	  
                    	  MainActivity.pl.write(send);
                    	  
                    	  Toast.makeText(getApplicationContext(), "发送命令成功,重启机器后生效", 0).show();
                    	}
                    	
                    }
                }).setNegativeButton("取消",null).show();
    }

	static byte[] concat(byte[] a, byte[] b) {  
		byte[] c= new byte[a.length+b.length];  
		   System.arraycopy(a, 0, c, 0, a.length);  
		   System.arraycopy(b, 0, c, a.length, b.length);  
		   return c;  
		} 
    
    
	private List<Map<String, String>> getData(String title) {
		List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
		Resources res = getResources();
		String[] cmdStr = res.getStringArray(R.array.cmd);
		for (int i = 0; i < cmdStr.length; i++) {
			String[] cmdArray = cmdStr[i].split(",");
			if (cmdArray.length >= 2) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("title", cmdArray[0]);
				map.put("description", cmdArray[1]);
				if(cmdArray.length==3)
				{
					map.put("language", cmdArray[2]);
				}
				listData.add(map);
			}
		}
		return listData;
	}

	/**
	 * hex String to byte array
	 */
	public static byte[] hexStringToBytes(String hexString) {
		hexString = hexString.toLowerCase();
		String[] hexStrings = hexString.split(" ");
		byte[] bytes = new byte[hexStrings.length];
		for (int i = 0; i < hexStrings.length; i++) {
			char[] hexChars = hexStrings[i].toCharArray();
			bytes[i] = (byte) (charToByte(hexChars[0]) << 4 | charToByte(hexChars[1]));
		}
		return bytes;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789abcdef".indexOf(c);
	}

}
