package com.qs.printer.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.qs.printer5802.R;

/**
 * 打印图片
 * @author Administrator
 *
 */
public class PrintImageActivity extends Activity {

	private static final int REQUEST_EX = 1;
	private Bitmap btMap = null;
	private ImageView iv;
	private Button bt_image;
	private Button bt_openpic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print_image);

		iv = (ImageView) findViewById(R.id.iv_test);

		bt_openpic = (Button) findViewById(R.id.bt_openpci);
		bt_openpic.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//选择本地图片
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, REQUEST_EX);
			}
		});

		bt_image = (Button) findViewById(R.id.bt_image);
		bt_image.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (btMap != null) {
					//压缩图片为570*300
					btMap=zoomImg(btMap,200,200);
				    //打印图片
					MainActivity.pl.write(draw2PxPoint(btMap));
//					MainActivity.pl.printImage(btMap);
							//黑标走纸
				    MainActivity.pl.write(new byte[] { 0x0c});
				
				}
			}
		});
//      //默认图片为打印测试图片
		btMap = BitmapFactory.decodeResource(getResources(), R.drawable.demo);
		btMap=zoomImg(btMap,300,300);
//		//显示图片
		iv.setImageBitmap(btMap);

	}
	
	byte[] toPrimitives(Byte[] oBytes)
	{
	    byte[] bytes = new byte[oBytes.length];

	    for(int i = 0; i < oBytes.length; i++) {
	        bytes[i] = oBytes[i];
	    }

	    return bytes;
	}

	/**
	 * 保存到SD卡
	 * 
	 * @param filename
	 * @param filecontent 
	 * @throws Exception
	 */
	public void saveToSDCard(String filename, String filecontent)
			throws Exception {
		File file = new File(Environment.getExternalStorageDirectory(),
				filename);
		FileOutputStream outStream = new FileOutputStream(file);
		outStream.write(filecontent.getBytes());
		outStream.close();
	}

	/**
	 * 选择好图片之后的回调方法
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_EX && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			ContentResolver cr = this.getContentResolver();
			try {
				//获取选择好的图片
				btMap = BitmapFactory.decodeStream(cr
						.openInputStream(selectedImage));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//在界面上展示出来
			iv.setImageBitmap(btMap);
		}
	}
	
	// 缩放图片
		public static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
		   // 获得图片的宽高
		   int width = bm.getWidth();
		   int height = bm.getHeight();
		   // 计算缩放比例
		   float scaleWidth = ((float) newWidth) / width;
		   float scaleHeight = ((float) newHeight) / height;
		   // 取得想要缩放的matrix参数
		   Matrix matrix = new Matrix();
		   matrix.postScale(scaleWidth, scaleHeight);
		   // 得到新的图片
		   Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
		    return newbm;
		}

	/**
	 * 图片去色,返回灰度图片
	 * 
	 * @param bmpOriginal
	 *            传入的彩色图片
	 * @return 去色后的图片
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();
		Bitmap bmpGrayscale = Bitmap.createBitmap(height, width ,
				Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	
	

	/*************************************************************************
	 * 假设一个240*240的图片，分辨率设为24, 共分10行打印 每一行,是一个 240*24 的点阵, 每一列有24个点,存储在3个byte里面。
	 * 每个byte存储8个像素点信息。因为只有黑白两色，所以对应为1的位是黑色，对应为0的位是白色
	 **************************************************************************/
	/**
	 * 把一张Bitmap图片转化为打印机可以打印的字节流
	 * 
	 * @param bmp
	 * @return
	 */
	public static byte[] draw2PxPoint(Bitmap bmp) {
		// 用来存储转换后的 bitmap 数据。为什么要再加1000，这是为了应对当图片高度无法
		// 整除24时的情况。比如bitmap 分辨率为 240 * 250，占用 7500 byte，
		// 但是实际上要存储11行数据，每一行需要 24 * 240 / 8 =720byte 的空间。再加上一些指令存储的开销，
		// 所以多申请 1000byte 的空间是稳妥的，不然运行时会抛出数组访问越界的异常。
		int size = bmp.getWidth() * bmp.getHeight() / 8 + 1000;
		byte[] data = new byte[size];
		int k = 0;
		// 设置行距为0的指令
		data[k++] = 0x1B;
		data[k++] = 0x33;
		data[k++] = 0x00;
		// 逐行打印
		for (int j = 0; j < bmp.getHeight() / 24f; j++) {
			// 打印图片的指令
			data[k++] = 0x1B;
			data[k++] = 0x2A;
			data[k++] = 33;
			data[k++] = (byte) (bmp.getWidth() % 256); // nL
			data[k++] = (byte) (bmp.getWidth() / 256); // nH
			// 对于每一行，逐列打印
			for (int i = 0; i < bmp.getWidth(); i++) {
				// 每一列24个像素点，分为3个字节存储
				for (int m = 0; m < 3; m++) {
					// 每个字节表示8个像素点，0表示白色，1表示黑色
					for (int n = 0; n < 8; n++) {
						byte b = px2Byte(i, j * 24 + m * 8 + n, bmp);
						data[k] += data[k] + b;
					}
					k++;
				}
			}
			data[k++] = 10;// 换行
		}
		return data;
	}

	
	/**
	 * 灰度图片黑白化，黑色是1，白色是0
	 * 
	 * @param x
	 *            横坐标
	 * @param y
	 *            纵坐标
	 * @param bit
	 *            位图
	 * @return
	 */
	public static byte px2Byte(int x, int y, Bitmap bit) {
		if (x < bit.getWidth() && y < bit.getHeight()) {
			byte b = 0;
			int pixel = bit.getPixel(x, y);
			int red = (pixel & 0x00ff0000) >> 16; // 取高两位
			int green = (pixel & 0x0000ff00) >> 8; // 取中两位
			int blue = pixel & 0x000000ff; // 取低两位
			int gray = RGB2Gray(red, green, blue);
			Log.e("", "gray:"+gray);
			if (gray < 128) {
				b = 1;
			} else {
				b = 0;
			}
			return b;
		}
		return 0;
	}

	/**
	 * 图片灰度的转化
	 */
	private static int RGB2Gray(int r, int g, int b) {
		int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b); // 灰度转化公式
		return gray;
	}
 
	/**
	 * 保存图片至SD卡跟目录
	 * @param mBitmap 要保存的图片
	 * @param bitName 图片名
	 */
	public void saveMyBitmap(Bitmap mBitmap, String bitName) {
		File f = new File("/sdcard/" + bitName + ".jpg");
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

}
