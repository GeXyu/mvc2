package cn.xiuyu.entity;

import java.util.HashMap;
import java.util.Map;

public class Model {
	private Map<String,Object> maps = new HashMap<String,Object>();
	private static final Model instance = new Model();
	public static Model getInstance(){
		return instance;
	}
	public void setParameter(String key, Object value){
		maps.put(key, value);
	}
	public <T> T  getParameter(String key){
		return (T) maps.get(key); 
	}
	public Map<String, Object> getMaps() {
		return maps;
	}
	public void setMaps(Map<String, Object> maps) {
		this.maps = maps;
	}
	
}
