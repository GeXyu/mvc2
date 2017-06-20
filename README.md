### github 地址：[传送门](https://github.com/GeXyu/mvc2)
前两天写了几个注解和处理器的Dmoe,然后发现可以写一个小框架。感觉Springmvc比较合适，之前也看过部分源码，然后就动手搞了一个。
代码没有抽取，设计也是一塌糊涂(比较菜，不会设计)，代码不优雅。  
实现了几个核心功能：注解式声明控制类，简单的数据绑定，返回jsosn，支持使用model传递数据，Resultf风格的跳转和重定向（准备添加文件上传功能）。  
如果有志之士可以fork一份 添加功能,修复bug。这里谢过了！  
### 原理 
1，首先初始化的时候扫描指定的包，然后扫描类上的注解，生成一个map。把注解上的值为key,Java类的全定限名为value。  
2，扫描方法上面的映射，维护映射map,把mapping注解的值为key,执行的方法为value。初始化完成！  
3，然后根据核心servlet去分发请求，然后解析返回字符串，进行跳转或者是重定向。  
具体的思路就是这样  
如果有志之士可以fork一份 添加功能,修复bug.  
留个邮箱,用于交流 [gexyuzz@gmail.com](gexyuzz@gmail.com) !不是广告！！！  
#### 打包好了   [Download](http://images.zzcode.cn/mvc2.jar)可以直接使用啦～
用到的jar包:[bcel-6.0.jar](http://mvnrepository.com/artifact/org.apache.bcel/bcel) [commons-beanutils.jar](http://mvnrepository.com/artifact/commons-beanutils/commons-beanutils/1.9.3)  [json-20170516.jar](http://mvnrepository.com/artifact/org.json/json/20160810)
日志组件: [logback](http://mvnrepository.com/artifact/ch.qos.logback/logback-core/1.1.7) 
### 那么怎么使用呢？

首先在action类添加@Action注解,在方法添加@Mapping注解.
```java
@Action("/index")
public class Controller {
	@Mapping(value="/hello",RequestType=Type.get)
	public String sayHello(Model model){
		model.setParameter("root", "Hello");
		return "welcome";
	}
}

```
它会跳转到/WEB-INF/jsp下的welcome.jsp(这里我写死的,前缀/WEB-INF/jsp.后缀.jsp)
```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
${root }
</body>
</html>
```
然后在项目的web.xml文件添加核心servlet
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="2.5" xmlns="http://java.sun.com/xml/ns/javaee"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
	http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd">
	
	<servlet>
		<servlet-name>main</servlet-name>
		<servlet-class>cn.xiuyu.servlet.MainServlet</servlet-class>
		<!-- 设置最先启动保证初始化 -->
		<load-on-startup>1</load-on-startup>
		<init-param>
			<!-- 自定扫描的包 -->
			<param-name>package</param-name>
			<param-value>cn.xiuyu</param-value>
		</init-param>
	</servlet>
	<servlet-mapping>
		<servlet-name>main</servlet-name>
		<url-pattern>/</url-pattern>
	</servlet-mapping>
</web-app>

```
然后访问地址：http:localhost:8080/${项目名}/index/hello  
#### 其实使用上springmvc差不多，就这样。 有什么bug可以告诉我阿！ 告诉我阿 ！ 我阿！ 阿！！！

### 重定向和转发
```java
	//post请求
	@Mapping(value="/post",RequestType=Type.post)
	public String post(){
		System.out.println("---进入到了post请求 --");
		return "redirect:/index/get";
	}
	//get请求value  不备注默认为get
	@Mapping(value="/get",RequestType=Type.get)
	public String get(Model model){
		//model 只支持传值 取值
		model.setParameter("key", "value");
		System.out.println("---进入到了get请求 --");
		return "welcome";
	}
```
### 接受单字段 可以传入model对象（仅能传输对象）
```java
	//接受单字段
	@Mapping(value="/one",RequestType=Type.post)
	public String one(String name,Model model){
		//model 只支持传值 取值
		model.setParameter("key", name);
		return "welcome";
	}
```
### 封装实体
```java
	//封装实体
	@Mapping(value="/object",RequestType=Type.post)
	public String object(User u,Model model){
		//model 只支持传值 取值
		model.setParameter("key", u.getName() + "--" + u.getPassword());
		return "welcome";
	}
```
### 返回json  需要返回json对象并且在方法上添加@Json注解
```java
//返回json
	@Mapping(value="/json",RequestType=Type.get)
	@Json
	public User json(String name,String password,Model model){
		//model 只支持传值 取值
		User u = new User();
		u.setName(name);
		u.setPassword(password);
		System.out.println(u);
		return u;
	}
```
### 上传文件（目前仅能单文件上传），需要把表单设置为enctype="multipart/form-data"，然后在action方法添加FileEntity实体接受文件，上传到/WEB-INF/upload/*/*文件夹下(*为算法生成)
```html
<form action="${pageContext.request.contextPath }/index/uoload" method="post" enctype="multipart/form-data">
	<input type="file" name="testFile">
	<input type="submit" value="file">
</form>
```
action方法
```java
//上传文件
	@Mapping(value="/uoload",RequestType=Type.post)
	public String uoload(FileEntity entity,Model model){
		//model 只支持传值 取值
		model.setParameter("key", entity.getPath() + " " +entity.getNewName());
		return "welcome";
	}
```
可以在文件上传过程接收文件上传状态，返回一个json对象(封装文件大小`pContentLength`和已上传文件`pBytesRead`)
