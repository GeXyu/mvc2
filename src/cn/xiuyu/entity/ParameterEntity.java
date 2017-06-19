package cn.xiuyu.entity;

import org.apache.bcel.generic.Type;
/**
 * 
 * @author gexyuzz
 * @version date：2017年6月19日 下午2:01:51
 */
public class ParameterEntity {

	private int sort;
	private String name;
	private Type clazz;
	private Object value;
	
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public ParameterEntity() {
		super();
	}
	public Type getClazz() {
		return clazz;
	}
	public void setClazz(Type clazz) {
		this.clazz = clazz;
	}
	public ParameterEntity(int sort, String name, Type clazz, Object value) {
		super();
		this.sort = sort;
		this.name = name;
		this.clazz = clazz;
		this.value = value;
	}
}
