package com.qs.printer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.qs.helper.printer.PrinterClass;
import com.qs.printer5802.R;

/**
 * 打印功能选择界面
 * @author Administrator
 *
 */
public class PrintActivity extends Activity implements OnClickListener{
	
	private TextView textPrint_text,imagePrint_text,barPrint_text,eqPrint_text,setting_text,codeChage_text;
	private ImageView textPrint_image,imagePrint_image,barPrint_image,eqPrint_image,setting_image,codeChage_image;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print_items);
		init();
//		setListAdapter(new SimpleAdapter(this,getData("simple-list-item-2"),android.R.layout.simple_list_item_2,new String[]{"title", "description"},new int[]{android.R.id.text1, android.R.id.text2}));  
		
		Intent intent=getIntent();
		int position=intent.getIntExtra("position",0);
		
		if(MainActivity.pl!=null&&(position==0||position==1)&&MainActivity.pl.getState() != PrinterClass.STATE_CONNECTED)
		{
			intent=new Intent();
			intent.setClass(PrintActivity.this,PrintSettingActivity.class);
			//无连接时候跳转至PrintSettingActivity
			startActivityForResult(intent, 0);
		}
		
	}
	
	/**
	 * 初始化
	 */
	private void init() {
		// TODO Auto-generated method stub
		textPrint_text=(TextView) findViewById(R.id.textPrint_text);
		imagePrint_text=(TextView) findViewById(R.id.imagePrint_text);
		barPrint_text=(TextView) findViewById(R.id.barPrint_text);
		eqPrint_text=(TextView) findViewById(R.id.eqPrint_text);
		setting_text=(TextView) findViewById(R.id.setting_text);
		codeChage_text=(TextView) findViewById(R.id.codeChage_text);
		
		textPrint_image=(ImageView) findViewById(R.id.textPrint_image);
		imagePrint_image=(ImageView) findViewById(R.id.imagePrint_image);
		barPrint_image=(ImageView) findViewById(R.id.barPrint_image);
		eqPrint_image=(ImageView) findViewById(R.id.eqPrint_image);
		setting_image=(ImageView) findViewById(R.id.setting_image);
		codeChage_image=(ImageView) findViewById(R.id.codeChage_image);
		
		
		textPrint_text.setOnClickListener(this);
		imagePrint_text.setOnClickListener(this);
		barPrint_text.setOnClickListener(this);
		eqPrint_text.setOnClickListener(this);
		setting_text.setOnClickListener(this);
		codeChage_text.setOnClickListener(this);
		
		textPrint_image.setOnClickListener(this);
		imagePrint_image.setOnClickListener(this);
		barPrint_image.setOnClickListener(this);
		eqPrint_image.setOnClickListener(this);
		setting_image.setOnClickListener(this);
		codeChage_image.setOnClickListener(this);
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (resultCode)
		{
		case 0:
			if(MainActivity.pl.getState() != PrinterClass.STATE_CONNECTED)
			{
				PrintActivity.this.finish();
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MainActivity.pl.disconnect();
		super.onDestroy();
	}
	
    protected void onListItemClick(ListView listView, View v, int position, long id) {
        /*Map map = (Map)listView.getItemAtPosition(position);
        Toast toast = Toast.makeText(this, map.get("title")+" is selected.", Toast.LENGTH_LONG);
        toast.show();*/
    	Intent intent= new Intent();
    	switch (position) {
		case 0:
			intent.setClass(PrintActivity.this,
	    			PrintTextActivity.class);
			break;
		case 1:
			intent.setClass(PrintActivity.this,
	    			PrintImageActivity.class);
			break;
		case 2:
			intent.setClass(PrintActivity.this,
					PrintCmdActivity.class);
			break;
		case 3:
			intent.setClass(PrintActivity.this,
	    			BlackMarkActivity.class);
			break;
		case 4:
			intent.setClass(PrintActivity.this,
	    			UpdateActivity.class);
			break;
		case 5:
		default:
			break;
		}
		startActivity(intent);
    }

    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent= new Intent();
		switch (v.getId()) {
		//文字打印
		case R.id.textPrint_text:
		case R.id.textPrint_image:
			intent.setClass(PrintActivity.this,
	    			PrintTextActivity.class);
			break;
	    //图片打印
		case R.id.imagePrint_text:
		case R.id.imagePrint_image:
			intent.setClass(PrintActivity.this,
	    			PrintImageActivity.class);
			break;
			//更新
		case R.id.barPrint_text:
		case R.id.barPrint_image:
			intent.setClass(PrintActivity.this,
	    			UpdateActivity.class);
			break;
			//指令发送
		case R.id.setting_text:
		case R.id.setting_image:
			intent.setClass(PrintActivity.this,
	    			PrintCmdActivity.class);
			break;
		//黑标设置类
		case R.id.codeChage_text:
		case R.id.codeChage_image:
			intent.setClass(PrintActivity.this,
	    			BlackMarkActivity.class);
			break;
			//其他的默认跳转至黑标设置类，防止错点
		default:
			intent.setClass(PrintActivity.this,
	    			BlackMarkActivity.class);
			break;
		}
		startActivity(intent);
		
	}
	
}
