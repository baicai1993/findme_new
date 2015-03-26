package com.neu.findme.server_locate;




import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

//向外部提供经纬度和地理位置描述的类
/**
 * @author cxm
 *为外部界面提供定位服务的类
 *2015-03-09 21:09:33
 */
public class AmapLocateServer implements AMapLocationListener {
	private static LocationManagerProxy aMapLocManager = null;
	private static String locationStr = "";//地理位置描述
	private static float latitude = 0;// 经度
	private static float longitude = 0;// 纬度
	
	public static float getLatitude() {
		return latitude;
	}
	public static void setLatitude(float latitude) {
		AmapLocateServer.latitude = latitude;
	}
	public static float getLongitude() {
		return longitude;
	}
	public static void setLongitude(float longitude) {
		AmapLocateServer.longitude = longitude;
	}
	
	public static String getLocationStr() {
		return locationStr;
	}
	public static void setLocationStr(String locationStr) {
		AmapLocateServer.locationStr = locationStr;
	}
	@SuppressWarnings("deprecation")
	public void startLocate(Activity activity) {
		locationStr = "";
		longitude = 0;
		latitude = 0;
		// 高德地理位置解析
		if(aMapLocManager!=null){
			//销毁原来的定位者，防止多个manager一起定位
			aMapLocManager.destory();
		}
		aMapLocManager = LocationManagerProxy.getInstance(activity);
		aMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 5, this);
	}
//每隔1分钟，或者移动范围超过10米时调用
	@Override
	public void onLocationChanged(AMapLocation location) {
		// TODO Auto-generated method stub
		if (location != null) {
			Bundle locBundle = location.getExtras();
			if (locBundle != null) {
				AmapLocateServer.setLocationStr(locBundle.getString("desc"));
			}
			AmapLocateServer.setLatitude((float) location.getLatitude());
			AmapLocateServer.setLongitude((float) location.getLongitude());
		}
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

}
