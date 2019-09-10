package com.qs.printer.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qs.printer5802.R;

public class UpdateActivity extends Activity {

	
	private static int FILE_SELECT_CODE = 1000;

	private EditText menage_et;

	String updateFilePath1 = null;

	
	private TextView tv_download_state;
	
	private ProgressBar pb_download = null;
	
	ArrayList<byte[]> list = new ArrayList<byte[]>();

	int arraysNum = 1000;
	
	int barNum=0;

	NumberFormat numberFormat = NumberFormat.getInstance();
	
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
			"android.permission.READ_EXTERNAL_STORAGE",
			"android.permission.WRITE_EXTERNAL_STORAGE" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_layout);

		menage_et = (EditText) findViewById(R.id.menage_et);
		
		pb_download=(ProgressBar) findViewById(R.id.pb_download);
		
		tv_download_state=(TextView) findViewById(R.id.tv_download_state);
		
		// 设置精确到小数点后0位
		numberFormat.setMaximumFractionDigits(0);

		findViewById(R.id.update).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menage_et.setText("");
				// if (updateFilePath1 == null) {
//				pb_download.setProgress(0);
//				tv_download_state.setText("select");
//				showFileChooser();
				showIsUpdateDialog(updateFilePath1);
			}
		});

		findViewById(R.id.send_update).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showTipDialog(UpdateActivity.this, "请重启");
						
					}
				});
		
//		verifyStoragePermissions(this);
		
	}
	
//	public static void verifyStoragePermissions(Activity activity) {
//
//		try {
//			// 检测是否有写的权限
//			int permission = ActivityCompat.checkSelfPermission(activity,
//					"android.permission.WRITE_EXTERNAL_STORAGE");
//			if (permission != PackageManager.PERMISSION_GRANTED) {
//				// 没有写的权限，去申请写的权限，会弹出对话框
//				ActivityCompat.requestPermissions(activity,
//						PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
//	private void showFileChooser() {
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//		intent.setType("*/*");
//		intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//		try {
//			startActivityForResult(
//					Intent.createChooser(intent, "Select a File to Upload"),
//					FILE_SELECT_CODE);
//		} catch (android.content.ActivityNotFoundException ex) {
//			Toast.makeText(this, "Please install a File Manager.",
//					Toast.LENGTH_SHORT).show();
//		}
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

//		if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
//			// Get the Uri of the selected file
//		
//			showIsUpdateDialog(updateFilePath1);
//		}
	}


	
	private void showIsUpdateDialog(final String updateFilePath) {
//		if (updateFilePath == null || updateFilePath.equals(""))
//			return;
//		final File file = new File(updateFilePath);
//		if (file == null || !file.exists()) {
//			Toast.makeText(UpdateActivity.this, "File path is empty, please check read and write permissions!", Toast.LENGTH_SHORT)
//					.show();
//			return;
//		}
//		showMessage("Path：" + updateFilePath);
		
		new AlertDialog.Builder(UpdateActivity.this).setTitle("Update")
//				.setMessage("Update file Path：" + updateFilePath)
				.setPositiveButton("Update", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						showMessage("Update...");
						new UpdateThread().execute();
					}
				}).setNegativeButton("Cancel", null).show();
	}

	public static String format(byte[] bt) {
		int line = 0;
		StringBuilder buf = new StringBuilder();
		for (byte d : bt) {
			if (line % 16 == 0)
				// buf.append(String.format("%05x: ", line));
				line++;
			buf.append(String.format("%02x ", d));
			if (line % 16 == 0)
				buf.append("\n");
		}
		buf.append("\n");
		return buf.toString();
	}

	public  byte[] readFile(String file) throws IOException {
        Resources resources=this.getResources();
        InputStream is=null;
        is=resources.openRawResource(R.raw.qs975);
        byte buffer[]=new byte[is.available()];
        is.read(buffer);
        is.close();
return buffer;
}
//	public static byte[] readFile(String file) throws IOException {
//		InputStream is = new FileInputStream(file);
//		int length = is.available();
//		byte bt[] = new byte[length];
//		is.read(bt);
//		is.close();
//		return bt;
//	}
	
	class UpdateThread extends AsyncTask<File, Integer, Integer> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			// ProgressDialogUtils.showProgressDialog(PosUpdateActivity.this,
			// "正在升级...");
		}

		@Override
		protected Integer doInBackground(File... params) {
			// TODO Auto-generated method stub
			try {
				
				//读出升级文件的全部字节
				byte[] bt1 = readFile(updateFilePath1);
				//校验和 checksum 
				byte[] bt2=sumMultiIntArray(bt1);
				//长度
				byte[] leng=toLH(bt1.length);
//				//前缀
				byte[] bt3=new byte[]{0x1b,0x23,0x23,0x55,0x50,0x50,0x47};
				//前缀加校验和
				byte[] bt4=concat(bt3, bt2);
				//长度len拼接具体数据
				byte[] bt5=concat(leng, bt1);
				
				//bt4拼接bt5,即最终数组
				byte[] bt=concat(bt4, bt5);
				
				
				for (int i = 0; i < (bt.length / arraysNum + 1); i++) {
					if (arraysNum * (i + 1) >= bt.length) {
						list.add(Arrays.copyOfRange(bt, arraysNum * i,
								bt.length));
					} else {
						list.add(Arrays.copyOfRange(bt, arraysNum * i,
								arraysNum * (i + 1)));
					}
				}
				
				barNum=list.size();
				
				//启动发送线程
				handler.postDelayed(mRun_start, 1000);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}
	
	public static long checksumCrc32(File file) throws FileNotFoundException, IOException {
        CRC32 checkSummer = new CRC32();
        CheckedInputStream cis = null;

        try {
            cis = new CheckedInputStream( new FileInputStream(file), checkSummer);
            byte[] buf = new byte[128];
            while(cis.read(buf) >= 0) {
                // Just read for checksum to get calculated.
            }
            return checkSummer.getValue();
        } finally {
            if (cis != null) {
                try {
                    cis.close();
                } catch (IOException e) {
                }
            }
        }
    }
	
	int sendNum = 0;
	Handler handler = new Handler();
	Runnable mRun_start = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(sendNum>=barNum){
				
				sendNum=0;
				//发送更新指令至打印机
				
				handler.removeCallbacks(mRun_start);
				
				tv_download_state.setText("Update success！");
				
				pb_download.setProgress(100);
				
			}else{
				
				//发送更新指令至打印机
				MainActivity.pl.write(list.get(sendNum));
				
				String result = numberFormat.format((float) sendNum / (float) barNum * 100+1);
				tv_download_state.setText(result+"%");
				pb_download.setProgress(Integer.valueOf(result));
				
				sendNum++;
				
				handler.postDelayed(mRun_start, 200);
				
			}
		};
	};

	static byte[] concat(byte[] a, byte[] b) {  
		byte[] c= new byte[a.length+b.length];  
		   System.arraycopy(a, 0, c, 0, a.length);  
		   System.arraycopy(b, 0, c, a.length, b.length);  
		   return c;  
		}  

	/**
	 * 消息展示
	 */
	public void showMessage(String str) {
		String str1 = menage_et.getText().toString();
		menage_et.setText(str1 + "\n" + str);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public void showTipDialog(Context context, CharSequence message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("" + message);
		builder.setTitle("Tips");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.create().show();
	}

	/**
	 * 小端模式 将int转为低字节在前，高字节在后的byte数组
	 * 
	 * @param n
	 *            int
	 * @return byte[]
	 */
	public static byte[] toLH(int n) {
		byte[] b = new byte[4];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		b[2] = (byte) (n >> 16 & 0xff);
		b[3] = (byte) (n >> 24 & 0xff);
		return b;
	}
	
	/**
	 * 整数转成四字节
	 * @param i
	 * @return
	 */
	   public static byte[] intToByte4(int i) {  
	        byte[] targets = new byte[4];  
	        targets[3] = (byte) (i & 0xFF);  
	        targets[2] = (byte) (i >> 8 & 0xFF);  
	        targets[1] = (byte) (i >> 16 & 0xFF);  
	        targets[0] = (byte) (i >> 24 & 0xFF);  
	        return targets;  
	    }  
	
	public static  byte[] my_int_to_bb_le(int myInteger){
	    return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(myInteger).array();
	}

	/**
	* 将int转为大端，低字节存储高位
	* 
	* @param n
	* int
	* @return byte[]
	*/
	public static byte[] toHH(int n) {
	    byte[] b = new byte[4];
	    b[3] = (byte) (n & 0xff);
	    b[2] = (byte) (n >> 8 & 0xff);
	    b[1] = (byte) (n >> 16 & 0xff);
	    b[0] = (byte) (n >> 24 & 0xff);
	    return b;
	}  
	
	
	/**
	 * 合并数组
	 * 
	 * @param data1
	 * @param data2
	 * @return
	 */
	public byte[] twoToOne(byte[] data1, byte[] data2) {

		byte[] data3 = new byte[data1.length + data2.length];
		System.arraycopy(data1, 0, data3, 0, data1.length);
		System.arraycopy(data2, 0, data3, data1.length, data2.length);
		return data3;

	}
	
	
	/**
	 * 数组累加
	 * @param arrayList
	 * @return
	 */
	public static byte[] sumMultiIntArray(byte[] bt){
//		byte[] write =new byte[(int)file.length()]; //升级文件先写到字节数组中

//		FileInputStream inputStream= null;
//		try {
//		    inputStream = new FileInputStream(file);
//		    inputStream.read(write);
//		    inputStream.close();
//		} catch (FileNotFoundException e) {
//		    e.printStackTrace();
//		} catch (IOException e) {
//		    e.printStackTrace();
//		}


		int fileByte=0;        //做累加
		for (byte b:bt) { 
		    fileByte+=b&0xff;  //b&0xff 就是将字节转为无符号类型的 
		}

		byte[] loB4=intToBytes(fileByte);  //checksum 取低四字节
		
		return loB4;
    }

	/**
	 * 将int数值转换为占四个字节的byte数组
	 * @param value
	 *            要转换的int值
	 * @return byte数组
	 */
	public static byte[] intToBytes( int value )
	{
	    byte[] src = new byte[4];
	    src[3] =  (byte) ((value>>24) & 0xFF);
	    src[2] =  (byte) ((value>>16) & 0xFF);
	    src[1] =  (byte) ((value>>8) & 0xFF);
	    src[0] =  (byte) (value & 0xFF);
	    return src;
	}
	
}
