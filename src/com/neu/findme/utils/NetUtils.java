package com.neu.findme.utils;

import com.neu.findme.R;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * @author cxm
 *提供网络状态的工具类
 *2015-03-11 15:43:05
 */
public class NetUtils {
	private Context context;
	public NetUtils(Context context){
		this.context = context;
	}
	// 网络监测
	public boolean checkNet() {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
			return false;
		}
		return true;
	}

	public void checkKind() {
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				Toast.makeText(context, MyApplication.getApplication().getString(R.string.netType_2g3g), Toast.LENGTH_LONG)
						.show();
			}
			if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				Toast.makeText(context, MyApplication.getApplication().getString(R.string.netType_wifi), Toast.LENGTH_LONG).show();
			}

		}
	}
}
