package cn.xiuyu.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.xiuyu.entity.FileEntity;
import cn.xiuyu.entity.MappingEntity;
import cn.xiuyu.entity.ParameterEntity;
import cn.xiuyu.utils.FileUtils;
import cn.xiuyu.utils.InitUtils;
import cn.xiuyu.utils.JsonUtils;
import cn.xiuyu.utils.RequestUtils;
import cn.xiuyu.utils.ReturnView;
/**
 * 
 * @author gexyuzz
 * @version date：2017年6月18日 下午9:00:35
 */
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final InitUtils initutils = InitUtils.getInstance();
	private static final RequestUtils requestUtils = RequestUtils.getInstance();
	private static final ReturnView returnView = ReturnView.getInsatnce();
	private static final FileUtils fileUtils = FileUtils.getInstance();
	private static final Logger  log  =  LoggerFactory.getLogger(MainServlet.class);
	
	
	List<String> packages = new ArrayList<String>();   //
	Map<String, Object> instanceMaps = new HashMap<String, Object>();//
	Map<String,MappingEntity> getmappings = new HashMap<String,MappingEntity>();
	Map<String,MappingEntity> postmappings = new HashMap<String,MappingEntity>();
    @Override
	public void init(ServletConfig config) throws ServletException {
    	
    	String basePackage = config.getInitParameter("package");
    	
    	initutils.scanfPackage(basePackage,packages);
    	
    	
    	initutils.scanfAction(packages, instanceMaps);
    	
    	
    	initutils.requestTomapping(instanceMaps,getmappings,postmappings);
    	
    	log.info("mvc2 Initialization complete...");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); 
		MappingEntity mappringEntity = null;
		//根据请求url去相应的map找运行的方法
		String requesttype = request.getMethod().toLowerCase();
		if(requesttype.equals("get")){
			mappringEntity = requestUtils.requestTOmethod(request,getmappings);
		}else{
			mappringEntity = requestUtils.requestTOmethod(request,postmappings);
		}
		//如果没有任何的注解
		if(mappringEntity ==null){
			System.out.println("no mapping ...");
			return;
		}
		//处理文件上传
		try {
			 fileUtils.FileUpload(request,response);
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		
		//执行返回
		doreturn(mappringEntity,request,response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	
	private void doreturn(MappingEntity mappringEntity,HttpServletRequest request,HttpServletResponse response ) throws IOException, ServletException{
		Object jumpUrl = null;
		try {
			//根据运行方法找到需要注入的属性
			List<ParameterEntity> methodParameter = requestUtils.MethodParameter(mappringEntity,request,response);
			Class<?> clazz = Class.forName(mappringEntity.getClazz().getName());
			Object obj = clazz.newInstance();
			Method method = mappringEntity.getMethod();
			int size = methodParameter.size();
			

			//如果没有参数就直接运行
			if(size<=0){
				//直接运行
				//如果是返回json
				if(mappringEntity.isJson()){
					String json = JsonUtils.getInstance().obj2json(method.invoke(obj, null));
					if(json.length() != 0){
						response.setCharacterEncoding("UTF-8");
						response.setContentType("application/json;charset=utf-8");
						PrintWriter writer = response.getWriter();
						writer.write(json);
						writer.flush();
						writer.close();
					}
					return;
				}else{
					jumpUrl = method.invoke(obj, null);
				}
				
			}else{
				//遍历出来 然后注入进去
				Object[] Parameter = new Object[size];
				for(int i=0;i<size; i++){
					Parameter[i] = methodParameter.get(i).getValue();	
				}
				//如果是json
				if(mappringEntity.isJson()){
	
					String json = JsonUtils.getInstance().obj2json(method.invoke(obj, Parameter));
					if(json.length() != 0){
						response.setCharacterEncoding("UTF-8");
						response.setContentType("application/json;charset=utf-8");
						PrintWriter writer = response.getWriter();
						writer.write(json);
						writer.flush();
						writer.close();
						
					}
					//json为空
					return;
				}else{
					jumpUrl = method.invoke(obj, Parameter);
				}
					
			}
			
				
		} catch (InstantiationException e) {
			
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//跳转
		if(jumpUrl != null){
			returnView.jump(jumpUrl, request, response);
		}
	}
}
