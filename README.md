# mvc2  --My Springmvc
闲来无聊按照spring mvc，自己动手实现了一个mvc.  

没有抽取方法属于demo版本，由于个人比较菜，代码不优雅.  

主要实现了注解式声明类,简单的数据绑定(Java实体类,String,request,response),返回josn,可以使用model传递数据,resultf风格的跳转和重定向.  

如果有志之士可以fork一份  添加功能,修复bug.

#### 用到的jar包:[bcel-6.0.jar](http://mvnrepository.com/artifact/org.apache.bcel/bcel) [commons-beanutils.jar](http://mvnrepository.com/artifact/commons-beanutils/commons-beanutils/1.9.3)  [json-20170516.jar](http://mvnrepository.com/artifact/org.json/json/20160810)
#### 日志组件: [logback]() 
## 如何使用？
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
然后访问地址：http:localhost:8080/${项目名}/index/hello


