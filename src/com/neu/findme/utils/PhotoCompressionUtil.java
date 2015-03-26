package com.neu.findme.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

/*
 * by cxm
 * 压缩图片
 */
@SuppressLint("NewApi")
public class PhotoCompressionUtil {

	// 根据传进来的文件压缩图片
	/*
	 * file 传进来的文件 size 压缩的比例
	 */
	public static void compressImage(File file) {

		String path=file.getAbsolutePath();
		Bitmap bitmap1=getSmallBitmap(file.getAbsolutePath());
		file.delete();
		//String name=file.getName();
		RandomAccessFile fileOutStream=null;
		//String filename= name.substring(0,name.lastIndexOf("."))+"(1)"+name.substring(name.lastIndexOf("."));
		File wofile=new File(path);
		try{
		if(!wofile.exists())
		{
		   wofile.createNewFile();
		}
		  ByteArrayOutputStream bos = new ByteArrayOutputStream();
		  bitmap1.compress(Bitmap.CompressFormat.JPEG,70, bos);
		  byte []data=bos.toByteArray();
		  fileOutStream = new RandomAccessFile(wofile, "rwd");
		  fileOutStream.write(data);
		  fileOutStream.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
//	// 根据尺寸压缩图片
			public static Bitmap getimage(String srcPath) {
				BitmapFactory.Options newOpts = new BitmapFactory.Options();
				// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
				newOpts.inJustDecodeBounds = true;
				Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

				newOpts.inJustDecodeBounds = false;
				int w = newOpts.outWidth;
				int h = newOpts.outHeight;
				// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
				float hh = 80f;// 这里设置高度为800f
				float ww = 48f;// 这里设置宽度为480f
				// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
				int be = 1;// be=1表示不缩放
				if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
					be = (int) (newOpts.outWidth / ww);
				} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
					be = (int) (newOpts.outHeight / hh);
				}
				if (be <= 0)
					be = 1;
				newOpts.inSampleSize = be;// 设置缩放比例
				// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
				bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
				//Bitmap bitmap=getSmallBitmap(srcPath);
				return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
				
			}
			// 图片质量压缩
			public static Bitmap compressImage(Bitmap image) {

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//				//int options = 100;
				while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
					ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
					image = BitmapFactory.decodeStream(isBm, null, null);
					baos.reset();// 重置baos即清空baos
					image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩options%，把压缩后的数据存放到baos中
					//options -= 10;// 每次都减少10
				}
				ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
				Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
				return bitmap;
			}
			
//	/*==============================================================================================
//	 * 重新照片加载相关信息
//	 * by VE
//	 */
//	
//	/*
//	 * 获得照片尺寸，在option中存放
//	 */
	 public static Options getOptions(String srcPath){
		 BitmapFactory.Options options = new BitmapFactory.Options();  
		 options.inJustDecodeBounds = true;  
		 BitmapFactory.decodeFile(srcPath, options);  
		 return options;
	 }
	 /*
	  * 获得需要压缩的尺寸
	  * reqWidth ， ReqHeight 是目标尺寸
//	  */
	 public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {  
	    // Raw height and width of image  
	    final int height = options.outHeight;  
	    final int width = options.outWidth;  
	    int inSampleSize = 1;  
	  
	    if (height > reqHeight || width > reqWidth) {  
	  
	        // Calculate ratios of height and width to requested height and width  
	        final int heightRatio = Math.round((float) height / (float) reqHeight);  
	        final int widthRatio = Math.round((float) width / (float) reqWidth);  
	  
	        // Choose the smallest ratio as inSampleSize value, this will guarantee  
	        // a final image with both dimensions larger than or equal to the  
	        // requested height and width.  
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;  
	    }  
	    return inSampleSize;  
	}  
	 
	 /*
	  * 压缩图片
	  */
	 public static Bitmap decodeSampledBitmapFromResource(String srcPath,  
		        int reqWidth, int reqHeight) {  
		  
		    // First decode with inJustDecodeBounds=true to check dimensions  
		 	Options options = getOptions(srcPath);
		  
		    // Calculate inSampleSize  
		    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);  
		    // Decode bitmap with inSampleSize set  
		    options.inJustDecodeBounds = false;  
		    return BitmapFactory.decodeFile(srcPath, options);  
		}

	 
	 public static int caculateInSampleSize(Options options,int reqWidth,int reqHeight)
		{
			 final int height = options.outHeight;
			 final int width = options.outWidth;
			    int inSampleSize = 1;

			    if (height > reqHeight || width > reqWidth) {
			             final int heightRatio = Math.round((float) height/ (float) reqHeight);
			             final int widthRatio = Math.round((float) width / (float) reqWidth);
			             inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			    }
			return inSampleSize;
		}

		public static Bitmap getSmallBitmap(String filePath) {
		    final BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(filePath, options);
		 // Calculate inSampleSize
		options.inSampleSize = caculateInSampleSize(options,314 , 215);
		    // Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
		}

		public static Bitmap ListViewBitmap (String filePath)
		{
			return decodeSampledBitmapFromResource(filePath,  
			        60, 70) ;
		}

}
