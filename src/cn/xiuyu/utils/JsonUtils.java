package cn.xiuyu.utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonUtils {
	public static final JsonUtils instance = new JsonUtils();
	public static JsonUtils getInstance(){
		return instance;
	}
	public Object json2obj(String str,Class clazz){
		Object obj = null;
		JSONObject json = new JSONObject(str);
		try {
			obj = clazz.newInstance();
			Field[] fields = clazz.getDeclaredFields();
			for(Field fd:fields){
				String name = fd.getName();
				Object value = json.get(name);
				BeanUtils.setProperty(obj, name, value);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return obj;
	}
	
	public String obj2json(Object o){
		if(o instanceof java.util.List){
			return new JSONArray((List)o).toString();
		}else if(o instanceof Map){
			return new JSONObject((Map)o).toString();
		}
		else{
			return new JSONObject(o).toString();
		}
	 
	}
}
