package cn.xiuyu.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.xiuyu.entity.MappingEntity;
import cn.xiuyu.entity.Model;

/** 
 * @author gexyuzz
 * @version date：2017年6月19日 下午2:02:09
 */
public class ReturnView {
	private static final ReturnView instance = new ReturnView();
	public static ReturnView getInsatnce(){
		return instance;
	}
	//需要得先到 request设置的model
	private  void  setRequest(HttpServletRequest request){
		Model model = (Model) request.getAttribute("root");
		if(model != null){
			Map<String,Object> maps = model.getMaps();
			if(maps != null){
				for(Map.Entry<String,Object> m:maps.entrySet()){
					request.setAttribute(m.getKey(), m.getValue());
				}
			}
		}
		
	}
	
	//返回值进行设置 然后跳转
	public void jump(Object obj,HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException{
		//先对request处理
		setRequest(request);
		
		
		String status = obj.toString();
		if(status.length() == 0){
			request.getRequestDispatcher("").forward(request, response);
			return;
		}
		//说明是跳转 对字符串切割
		if(status.contains("redirect")){
			String url = status.substring(status.indexOf(":")+1);
			response.sendRedirect(request.getContextPath() + url);
			return;
		}else{
			request.getRequestDispatcher("/WEB-INF/jsp/"+ status + ".jsp").forward(request, response);
			return;
		}
	}
	
	
}
