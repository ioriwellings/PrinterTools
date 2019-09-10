package com.qs.printer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.qs.helper.printer.PrinterClass;
import com.qs.printer5802.R;

/**
 * 黑标类，已经简化为只有黑标打开和关闭操作
 * @author wsl
 *
 */
public class BlackMarkActivity extends Activity implements OnClickListener {

	private Button bm_open, bm_close;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print_blackmark);

		if (MainActivity.pl.getState() != PrinterClass.STATE_CONNECTED) {
			this.finish();
			return;
		}

		init();
		
	}


	private void init() {
		// TODO Auto-generated method stub
		bm_open = (Button) findViewById(R.id.bm_open);
		bm_close = (Button) findViewById(R.id.bm_close);

		bm_open.setOnClickListener(this);
		bm_close.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (MainActivity.pl.getState() != PrinterClass.STATE_CONNECTED) {
			this.finish();
			return;
		}
		switch (v.getId()) {
		case R.id.bm_open:
			
			MainActivity.pl.write(new byte[] { 0x10, (byte) 0xff, 0x05,
					0x01 });
			bm_open.setTextColor(android.graphics.Color.GREEN);
			bm_open.setEnabled(false);
			bm_close.setTextColor(android.graphics.Color.BLACK);
			bm_close.setEnabled(true);
			
			break;
			
		case R.id.bm_close:
			
			MainActivity.pl.write(new byte[] { 0x10, (byte) 0xff, 0x05, 
					0x00 });
			bm_close.setTextColor(android.graphics.Color.RED);
			bm_close.setEnabled(false);
			bm_open.setTextColor(android.graphics.Color.BLACK);
			bm_open.setEnabled(true);
			
			break;
			
		default:
			break;
		}
	}

	
}
