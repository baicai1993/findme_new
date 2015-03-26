package com.neu.findme.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
/**
 * @author cxm
 *json解析的工具类，用于解析按照标准格式封装的列表型、对象型json
 *2015-03-11 15:47:36
 */
public class JsonParseUtils {
	//把json转换为boolean返回
	public static boolean jsonToBoolean(String json) {
		try {
			JSONObject jsonObject=new JSONObject(json);
			if(jsonObject!=null&&Boolean.parseBoolean(jsonObject.getString("flag"))){
				return true;
			}
			return false;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	//把json转换为一个字符串对象返回
	public static String jsonToString(String json,String stringName){
		String string= "";
		try {
			JSONObject jsonObject=new JSONObject(json);
			if(jsonObject!=null){
				if (Boolean.parseBoolean(jsonObject.getString("flag"))) {
					string = jsonObject.getString(stringName);
				}
			}
			return string;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	//把json转换为一个实体对象返回
	public static Object jsonToObject(String json,Class<?> type,String objectName){
		Object object = null;
		try {
			JSONObject jsonObject=new JSONObject(json);
			if(jsonObject!=null){
				if (Boolean.parseBoolean(jsonObject.getString("flag"))) {
					JSONObject content = new JSONObject(jsonObject.getString(objectName));
					object = new Gson().fromJson(content.toString(), type);
				}
			}
			return object;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return object;
		}
	}
	//把json转换为一个实体数组返回
	public static List<?> jsonToEntitylist(String json,Class<?> type){
		List<Object> list = new ArrayList<Object>();
    	try {
			JSONObject jsonObject=new JSONObject(json);
			JSONArray entityListJson = new JSONArray();
			if (jsonObject != null) {
				if (Boolean.parseBoolean(jsonObject.getString("flag"))) {
					entityListJson = jsonObject.getJSONArray("entityList");
					for (int i = 0; i < entityListJson.length(); i++) {
						Gson gson=new Gson();
						list.add((Object)gson.fromJson(entityListJson.opt(i).toString(), type));
					}
				}
			}
			return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return list;
		}
	}
	//把json转换为一个实体数组返回
	public static List<?> jsonToList(String json,Class<?> type){
		List<Object> list = new ArrayList<Object>();
    	try {
			JSONObject jsonObject=new JSONObject(json);
			JSONArray entityListJson = new JSONArray();
			if (jsonObject != null) {
				if (Boolean.parseBoolean(jsonObject.getString("flag"))) {
					entityListJson = jsonObject.getJSONArray("list");
					for (int i = 0; i < entityListJson.length(); i++) {
						Gson gson=new Gson();
						list.add((Object)gson.fromJson(entityListJson.opt(i).toString(), type));
					}
				}
			}
			return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return list;
		}
	}
}
