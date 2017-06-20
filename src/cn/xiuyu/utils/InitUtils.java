package cn.xiuyu.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.xiuyu.annotation.Action;
import cn.xiuyu.annotation.Json;
import cn.xiuyu.annotation.Mapping;
import cn.xiuyu.entity.MappingEntity;
import cn.xiuyu.servlet.MainServlet;
/**
 * 
 * @author gexyuzz
 * @version date：2017年6月18日 下午9:48:54
 */
public class InitUtils {
	public static final InitUtils instance = new InitUtils();
	private static final Logger  log  =  LoggerFactory.getLogger(InitUtils.class);
	public static InitUtils getInstance(){
		return instance;
	}


	public void scanfPackage(String basePackage,List<String> lists){
		String rootpath = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/")).getFile();
		File[] Files = new File(rootpath).listFiles();
		if(Files.length == 0){
			return;
		}
		for(File f:Files){
		
			if(f.isDirectory()){
				scanfPackage(basePackage + "." + f.getName(), lists);
			}else{
				lists.add(basePackage + "." + f.getName().substring(0, f.getName().lastIndexOf(".class")));
			}
		}
	}
	

	public void scanfAction(List<String> packages,Map<String, Object> instanceMaps){
		if(packages.size()==0){
			return;
		}
		try {
			for(String clazz:packages){
				Class<?> clzzname  = Class.forName(clazz);				
				if(clzzname.isAnnotationPresent(Action.class)){
					Action actionanno = clzzname.getAnnotation(Action.class);
					instanceMaps.put(actionanno.value(), clzzname.newInstance());
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	public void requestTomapping(Map<String, Object> instanceMaps, Map<String, MappingEntity> getmappings,Map<String, MappingEntity> postmappings) {
		if(instanceMaps.size() == 0){
			return;
		}
		
		for(Map.Entry<String, Object> maps: instanceMaps.entrySet()){
			
			Method[] methods = maps.getValue().getClass().getMethods();
			for(Method m:methods){
				if(m.isAnnotationPresent(Mapping.class)){
					
					Mapping mappinganno = m.getAnnotation(Mapping.class);
					String MethodType = mappinganno.RequestType().name();
					String requestUrl = mappinganno.value();
					
					if(MethodType!=null){
						String type = MethodType.toLowerCase();
						if(m.isAnnotationPresent(Json.class)){
							if(type.equals("get")){
								MappingEntity en = new MappingEntity();
								en.setClazz(maps.getValue().getClass());
								en.setMapper(maps.getKey() +requestUrl);
								en.setType(mappinganno.RequestType());
								en.setMethod(m);
								en.setJson(true);
								getmappings.put(en.getMapper(), en);
								log.info(en.getMapper() + "--json get--Add mapping success .....");
							}else if(type.equals("post")){		
								MappingEntity en = new MappingEntity();
								en.setClazz(maps.getValue().getClass());
								en.setMapper(maps.getKey() +requestUrl);
								en.setType(mappinganno.RequestType());
								en.setMethod(m);
								en.setJson(true);
								postmappings.put(en.getMapper(), en);
								log.info(en.getMapper() + "--json post --Add mapping success .....");
							}
						}else{
							if(type.equals("get")){
								MappingEntity en = new MappingEntity();
								en.setClazz(maps.getValue().getClass());
								en.setMapper(maps.getKey() +requestUrl);
								en.setType(mappinganno.RequestType());
								en.setMethod(m);
								en.setJson(false);
								getmappings.put(en.getMapper(), en);
								log.info(en.getMapper() + "---get--Add mapping success .....");
							}else if(type.equals("post")){		
								MappingEntity en = new MappingEntity();
								en.setClazz(maps.getValue().getClass());
								en.setMapper(maps.getKey() +requestUrl);
								en.setType(mappinganno.RequestType());
								en.setMethod(m);
								en.setJson(false);
								postmappings.put(en.getMapper(), en);
								log.info(en.getMapper() + "--post--Add mapping success .....");
							}
						}
						
					}else{
						throw new RuntimeException("No value in @Mapping...");
					}	
				}
			}
		}
		
	}
}
