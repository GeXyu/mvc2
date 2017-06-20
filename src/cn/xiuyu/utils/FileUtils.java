package cn.xiuyu.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.xiuyu.entity.FileEntity;
import cn.xiuyu.entity.FileJson;
import cn.xiuyu.servlet.MainServlet;

public class FileUtils {
	private static final FileUtils instance = new FileUtils();
	private static final Logger  log  =  LoggerFactory.getLogger(FileUtils.class);
	public static FileUtils getInstance(){
		return instance;
	}
	private static DiskFileItemFactory factory = null;
	private static ServletFileUpload upload = null;
	static{
		factory = new DiskFileItemFactory();
		upload = new ServletFileUpload(factory);
	}
	//处理文件上传
	public void FileUpload(HttpServletRequest request, final HttpServletResponse response) throws FileUploadException, IOException{
		//说明不是文件上传
		if(!upload.isMultipartContent(request)){
			return;
		}
		log.info("uploadding file ....");
		//设置临时目录
		File tempfile = new File(request.getServletContext().getRealPath("/WEB-INF/upload/temp"));
		//创建文件
		if(tempfile.exists()){
			tempfile.mkdir();
		}
		factory.setRepository(tempfile);
		//上传文件根目录
		String realPath = request.getServletContext().getRealPath("/WEB-INF/upload");
		//监听上传传进度
		upload.setProgressListener( new ProgressListener() {
			FileJson json = new FileJson();
			@Override
			public void update(long pBytesRead, long pContentLength, int arg2) {
				try {
					response.setCharacterEncoding("UTF-8");
					response.setContentType("appliaction/json;charset=utf-8");
					json.setpBytesRead(pBytesRead);
					json.setpContentLength(pContentLength);
					response.getWriter().write(JsonUtils.getInstance().obj2json(json));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		//得到所有文件
		List<FileItem> list = upload.parseRequest(request);
		for(FileItem item:list){
			
			//说明是普通输入项
			if(item.isFormField()){
				//是的不处理,单纯的上传文件
				//String name = item.getFieldName();
				continue;
			}else{
				FileEntity entity = new FileEntity();
				
				//上传的是文件
				String fileName = item.getName();
				//文件名不合理
				if(fileName == null || fileName.trim().equals("")){
					continue;
				}
				//得到原文件名
				fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
				String FileType = fileName.substring(fileName.lastIndexOf(".")+1);
				String newName = makeFileName(fileName);
				entity.setName(fileName);
				entity.setType(FileType);
				entity.setNewName(newName);
				
				InputStream in = item.getInputStream();
				String filePath = makePath(newName, realPath);
				
				FileOutputStream out = new FileOutputStream(filePath + "\\" + newName);
				byte[] buff = new byte[1024];
				int length = 0;
				while((length=in.read(buff))>0){
					out.write(buff, 0, length);
				}
				entity.setPath(filePath);
				request.setAttribute("fileUpload", entity);
				//关闭资源
				in.close();
				out.flush();
				out.close();
				item.delete(); //删除临时文件
				return;
			}
		}
		return;
		
	}
	
	private String makeFileName(String name){
		return UUID.randomUUID().toString()+ "_" + name;
	}
	
	private String makePath(String filename,String savePath){
		
		int hashcode = filename.hashCode();
		int dir1 = hashcode&0xf;  
		int dir2 = (hashcode&0xf0)>>4;  
		
		String dir = savePath + "\\" + dir1 + "\\" + dir2;
		File file = new File(dir);
		if(!file.exists()){
			file.mkdirs();
		}
		return dir;
	}
}
