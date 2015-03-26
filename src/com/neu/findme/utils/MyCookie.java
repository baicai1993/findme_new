package com.neu.findme.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author cxm
 *一个shareprefrence对象 名称为cookie 模拟web的cookie来方便存储一些持久化数据
 *2015-03-11 15:45:45
 */
public class MyCookie {
	private static SharedPreferences cookie = MyApplication.getApplication().getSharedPreferences("cookie",Context.MODE_PRIVATE);
	private static SharedPreferences.Editor cookieEditor = cookie.edit();
	public static void removeCookie(){
		cookieEditor.clear().commit();
	}
	public static void putString(String key,String value){
		cookieEditor.putString(key, value);
		cookieEditor.commit();
	}
	public static String getString(String key,String defaultValue){
		if(cookie.contains(key))
		return cookie.getString(key, defaultValue);
		return defaultValue;
	}
}
