package com.neu.findme.utils;


import java.util.HashMap;
import java.util.Map;

import android.app.Application;

/**
 * @author cxm
 *继承自application类，内部包含一个hashMap负责存储内存中常用的一些全局数据，类似web的session层
 *2015-03-11 15:46:39
 */
public class MyApplication extends Application {
	private static MyApplication instance;
	private static Map<Object, Object> objectContainer;
	public static MyApplication getApplication(){
		return instance;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		objectContainer = new HashMap<Object, Object>();
		instance = this;

	}
	public static void put(Object key, Object value){

		  objectContainer.put(key, value);
		}
		public static Object get(Object key){
		  
		  return objectContainer.get(key);
		}
		public static void cleanUp(){
		  objectContainer.clear();
		}
		public static void remove(Object key){
		  objectContainer.remove(key);
		}
	
}
