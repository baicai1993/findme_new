package com.neu.findme.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * @author cxm
 *提供对bitmap进行旋转处理的方法
 *2015-03-11 15:48:36
 */
public class ChangeImageTools {
	//rotate 为顺时针旋转的角度值
	public static Bitmap rotateImage(Bitmap image,int rotate){
		if(image!=null){
			Matrix m = new Matrix();
			m.setRotate(rotate);
			return Bitmap.createBitmap(image,0,0,image.getWidth(),image.getHeight(),m,true);
		}else {
			return null;
		}
	}
}
