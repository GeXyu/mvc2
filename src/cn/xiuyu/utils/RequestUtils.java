package cn.xiuyu.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.generic.Type;

import cn.xiuyu.entity.MappingEntity;
import cn.xiuyu.entity.Model;
import cn.xiuyu.entity.ParameterEntity;
/**
 * 
 * @author gexyuzz
 * @version date：2017年6月19日 下午2:02:05
 */
public class RequestUtils {
	private static final RequestUtils instance = new RequestUtils();
	public static RequestUtils getInstance(){
		return instance;
	}
	
	//根据url从存储映射的map中找到要运行的方法
	public MappingEntity requestTOmethod(HttpServletRequest request,Map<String,MappingEntity> mappings){
		
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String url = requestURI.replaceAll(contextPath, "");
		
		if(mappings.containsKey(url)){
			try{
				return mappings.get(url);
			}catch(Exception e){
				System.out.println("No Mapping ...");
			}
				
		}	
		return null;
	}
	
	//获取和返回运行需要参数
	public List<ParameterEntity> MethodParameter(MappingEntity mappringEntity, HttpServletRequest request,
			HttpServletResponse response) throws InstantiationException, IllegalAccessException {
		try {
			//设置获取方法名字和设置参数
			List<ParameterEntity> parameteValue = getMethodparameterName(mappringEntity,request,response);
			return parameteValue;
			
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	//获取运行方法的名字 并对他设置值
	private List<ParameterEntity> getMethodparameterName(MappingEntity mapprEntity, HttpServletRequest request,
			HttpServletResponse response ) throws NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		List<ParameterEntity> list = new ArrayList<ParameterEntity>();
		
		//方法名  类加载器  方法类型  方法全定限名
		//方法全定限名
		if(mapprEntity == null){
			return list;
		}
		//获取有效的方法数
		int methodlength = mapprEntity.getMethod().getParameterTypes().length;
		if(methodlength <=0){
			return list;
		}
		Class clazz = mapprEntity.getClazz();
		String clazzName = clazz.getName();
		Method declaredMethod = clazz.getDeclaredMethod(mapprEntity.getMethod().getName(),
				mapprEntity.getMethod().getParameterTypes());
		JavaClass lookupClass = Repository.lookupClass(clazz);
		org.apache.bcel.classfile.Method bcelMethod = lookupClass.getMethod(declaredMethod);
		LocalVariableTable lvt = bcelMethod.getLocalVariableTable();
		
		
		LocalVariable[] localVariableTable = lvt.getLocalVariableTable();
		for(int i=1; i<=methodlength; i++){
			LocalVariable localVariable =localVariableTable[i];
			ParameterEntity en = new ParameterEntity();
			en.setName(localVariable.getName());
			en.setSort(i-1);
			Type returnType = Type.getReturnType(localVariable.getSignature());
			en.setClazz(returnType);

			if(returnType.toString().equals(HttpServletRequest.class.getName())){
				en.setValue(request);
			}else if(returnType.toString().equals(HttpServletResponse.class.getName())){
				en.setValue(response);
			} else if(returnType.toString().equals(String.class.getName())){
				en.setValue(request.getParameter(en.getName()));
			}else if(returnType.toString().equals(Model.class.getName())){
				//如果是modil获取model,然后获取他并把它存到request
				Model model = Model.getInstance();
				request.setAttribute("root", model);
				en.setValue(model);
			}
			else{
				en.setValue(seParameterValues(en, request));
			}
			
			list.add(en);
		}
		
		return list;	
	}
	//对普通的对象进行设置值
	private Object seParameterValues(ParameterEntity en,HttpServletRequest request) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		Class<?> clazz = Class.forName(en.getClazz().toString());
		Object obje = clazz.newInstance();
		
		Field[] fields = clazz.getDeclaredFields();
		for(Field f:fields){
			f.setAccessible(true);
			String name = f.getName();//字段名
			String value = null;
			try {
				value = request.getParameter(name);
			} catch (Exception e) {
				continue;
			}
			f.set(obje, value);
		}
		
		return obje;
	}
	
	
}
