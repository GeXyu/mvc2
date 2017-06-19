package cn.xiuyu.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 
 * @author gexyuzz
 * @version date：2017年6月19日 下午2:01:21
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Mapping {
	String value() default "/";
	public enum Type{get,post};
	Type RequestType() default Type.get;
	
}
