package com.qs.printer.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qs.printer5802.R;

/**
 * 打印文字
 * 
 * @author
 * 
 */
public class PrintTextActivity extends Activity {
	List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
	private TextView et_input;
	private CheckBox checkBoxAuto;
	private Button bt_print;
	private Thread autoprint_Thread;

	int times = 500;// Automatic print time interval
	boolean isPrint = true;
	String message = "";
	public static String LanguageStr = "GBK";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print_text);
		isPrint = true;
		et_input = (EditText) findViewById(R.id.et_input_1);

		bt_print = (Button) findViewById(R.id.bt_print);
		bt_print.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				message = et_input.getText().toString();

				MainActivity.pl.printText(message+"\n");
//				黑标走纸
				MainActivity.pl.write(new byte[] {  0x0c });
			}
		});

		checkBoxAuto = (CheckBox) findViewById(R.id.checkBoxAuto);
		autoprint_Thread = new Thread() {
			public void run() {
				while (isPrint) {
					if (checkBoxAuto.isChecked()) {
						String message = et_input.getText().toString();
						MainActivity.pl.printText(message+"\n");
						MainActivity.pl.write(new byte[] { 0x1d, 0x0c });
						try {
							Thread.sleep(times);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		};
		autoprint_Thread.start();

	}

	// 将字符串转成字节数组
	public byte[] getText(String textStr) {

		byte[] send1;
		try {
			send1 = textStr.getBytes(LanguageStr);
		} catch (UnsupportedEncodingException var4) {
			send1 = textStr.getBytes();
		}

		return send1;
	}

	// 打印文字
	public boolean printText(String textStr) {
		return MainActivity.pl.write(this.getText(textStr));
	}

	/**
	 * 文字转图片
	 * 
	 * @param text
	 *            文字
	 * @param textSize
	 *            转成图片里面的文字大小
	 * @return
	 */
	public static Bitmap textAsBitmap(String text, float textSize) {

		TextPaint textPaint = new TextPaint();

		textPaint.setColor(Color.BLACK);

		textPaint.setTextSize(textSize);

		StaticLayout layout = new StaticLayout(text, textPaint, 350,
				Alignment.ALIGN_NORMAL, 1.3f, 0.0f, true);
		Bitmap bitmap = Bitmap.createBitmap(layout.getWidth() + 20,
				layout.getHeight() + 20, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.translate(10, 10);
		canvas.drawColor(Color.WHITE);

		layout.draw(canvas);

		return bitmap;

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		Resources res = getResources();
		String[] cmdStr = res.getStringArray(R.array.cmd);
		for (int i = 0; i < cmdStr.length; i++) {
			String[] cmdArray = cmdStr[i].split(",");
			if (cmdArray.length == 2) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("title", cmdArray[0]);
				map.put("description", cmdArray[1]);
				menu.add(0, i, i, cmdArray[0]);
				listData.add(map);
			}
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		Map map = listData.get(item.getItemId());
		String cmd = map.get("description").toString();
		byte[] bt = PrintCmdActivity.hexStringToBytes(cmd);
		MainActivity.pl.write(bt);
		Toast toast = Toast.makeText(this, "Send Success", Toast.LENGTH_SHORT);
		toast.show();
		return false;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		isPrint = false;
		// unregisterReceiver(mReceiver);
		super.onStop();
	}


}
