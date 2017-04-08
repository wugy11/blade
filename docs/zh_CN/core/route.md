---
root: false
title: 路由
sort: 1
---

{:toc}

# 路由

讲到路由就有同学问了，路由是个什么东西？哦我不是说脏话，这是疑问句。。

现代 Web 应用的 URL 十分优雅，易于人们辨识记忆。
在 Blade 中, 路由是一个 HTTP 方法配对一个 URL 匹配模型， 每一个路由可以对应一个处理方法。

如何管理呢？Blade 从以下三个方面对资源进行定义：

1. 直观简短的资源地址：URI，比如：http://example.com/resources。
2. 可传输的资源：Web 服务接受与返回的互联网媒体类型，比如：JSON，XML 等。
3. 对资源的操作：Web 服务在该资源上所支持的一系列请求方法（比如：POST，GET，PUT或DELETE）。


前面我们写过下面这个入门示例：

```java
$().get("/", new RouteHandler() {
	public void handle(Request request, Response response) {
		response.html("<h1>Hello Blade</h1>");
	}
}).start(Application.class);
```

下面用一张图来分析

![](https://ooo.0o0.ooo/2016/09/07/57cfe3025dce4.png)

执行main函数之后，因为Blade使用了内嵌Jetty作为web服务器，此时会根据jetty服务来初始化核心调度 `DispatcherServlet` 类，
这里会对配置文件，ioc，路由进行初始化，我们使用了匿名类的方式实现了路由，当执行这个请求的时候会像handle方法注入Request对象
和Response对象，你在方法体内可以进行一些操作，然后使用response对象将内容渲染到浏览器。

下面来讲解Blade中的N种路由的创建方式和我们推荐的使用方式：

## 使用Blade核心类创建

### 回调式路由

我们在第一个Hello Blade的程序中使用的就是回调式路由，他是实现一个 `RouteHandle` 接口的匿名类来注册一个路由。

他的写法是这样的：

```java
$().get("/get", new RouteHandler() {
	@Override
	public void handle(Request request, Response response) {
		System.out.println("come get!!!");
		System.out.println("name = " + request.query("name"));
		request.attribute("base", request.contextPath());
		response.render("get.jsp");
	}
});
```

这种方式如果你使用java8那么写起来就很优雅，但缺陷是只能在方法内部进行注册，匿名类中的外部调用必须是常量。

### 函数式路由

```java
$().route("/hello", NormalController.class, "hello");
$().route("/save_user", NormalController.class, "post:saveUser");
$().route("/delete_user", NormalController.class, "deleteUser", HttpMethod.DELETE);
```

`NormalController` 类如下：

```java
public class NormalController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NormalController.class);
	
	public void hello(Response response, Request request){
		LOGGER.info("进入hello~");
		request.attribute("name", "rose baby");
		response.text("hi");
	}
	
	public void saveUser(Request request, Response response){
		System.out.println("进入saveUser~");
		
		// test request.model()
		Person person = request.model("person", Person.class);
		System.out.println(person);
		
		// save操作
		JSONObject res = new JSONObject();
		res.put("status", 200);
		response.json(res);
	}
	
	public void deleteUser(Request request, Response response){
		System.out.println("进入deleteUser~");
		// delete操作
		JSONObject res = new JSONObject();
		res.put("status", 200);
		response.json(res);
	}

}
```

这种方式使用起来非常灵活，提供了一个让普通类就可以实现注册路由的功能。

`$().route("/hello", NormalController.class, "hello");`

我们传入的是 **请求地址** / 映射的类Class / 对应的类中方法名。
如果是指定Http请求可以使用 `post:hello` 或者多传递一个 `HttpMethod.POST`的方式进行注册。

上面这种路由方式是我在go语言的mvc框架中发现的，我个人不是很习惯这种方式。

## 使用配置文件创建

当路由比较多的时候我们可以将请求地址写在一个配置文件中进行统一注册

_配置文件格式_

```bash
GET     /                   IndexRoute.home
GET     /signin             IndexRoute.show_signin
POST    /signin             IndexRoute.signin
GET     /signout            IndexRoute.signout
POST    /upload_img         UploadRoute.upload_img
```

那对应的 `IndexRoute` 的写法如同 **函数式路由**

## 使用注解创建

```java
/**
 * web开发推荐的方式
 */
@Controller
public class AController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AController.class);
	
	@Inject
	UserService userService;
	
	@Route(value = "post", method = HttpMethod.POST)
	public void post() {
		LOGGER.info("request POST");
	}
	
	@Route("users/:name")
	public String users(Request request, @PathVariable String name) {
		LOGGER.info("request users");
		LOGGER.info("param name = {}", name);
		request.attribute("name", name);
		return "users";
	}
	
	@Route({"/", "index"})
	public ModelAndView index(ModelAndView mav, @RequestParam int age) {
		LOGGER.info("request query age = {}", age);
		userService.sayHello();
		mav.add("name", "jack");
		mav.setView("index");
		return mav;
	}

}
```

看到如上代码你可能觉得比较熟悉，因为我们习惯了springmvc，
所以上面的写法你操作起来就更亲切，作者最推荐的是这种方式注册路由，并不是因为这种方式在性能上有多么好，原因有这几点：

1. 我们习惯了springmvc
2. 控制器是被IOC容器托管的，控制器就可以注入其他的bean
3. 在方法上使用注册以及自动注入更加强大


**@Controller是什么？**

+ `Controller`用来标识这个类是一个控制器，里面存储的都是路由
+ 可以在控制器上配置访问前缀和后缀，便于统一请求
    
**@Route是什么？**

+ `Route`是一个路由的最小单元，用于方法上
+ `Route`的参数有`value`，`method`
+ `value`用于配置路由的URL，也就是http请求的路径
+ 一般不以`/`开头，因为`@Path`上会指定的，写法如下：
    * /hello
    * /user/hello
    * /user/:uid
    * /user/:uid/post

+ Rest风格的路由配置，一定适合你的口味！


**你不知道一个 HTTP 方法是什么？不必担心，这里会简要介绍 HTTP 方法和它们为什么重要：**

_GET_

浏览器告知服务器：只 获取 页面上的信息并发给我。这是最常用的方法。

_HEAD_

浏览器告诉服务器：欲获取信息，但是只关心 消息头 。应用应像处理 GET 请求一样来处理它，但是不分发实际内容。在 Flask 中你完全无需 人工 干预，底层的 Werkzeug 库已经替你打点好了。

_POST_

浏览器告诉服务器：想在 URL 上 发布 新信息。并且，服务器必须确保 数据已存储且仅存储一次。这是 HTML 表单通常发送数据到服务器的方法。

_PUT_

类似 POST 但是服务器可能触发了存储过程多次，多次覆盖掉旧值。你可 能会问这有什么用，当然这是有原因的。考虑到传输中连接可能会丢失，在 这种 情况下浏览器和服务器之间的系统可能安全地第二次接收请求，而 不破坏其它东西。因为 POST 它只触发一次，所以用 POST 是不可能的。

_DELETE_

删除给定位置的信息。


#### 几点说明：

- 路由匹配的顺序是按照他们被定义的顺序执行的，
- 但是，匹配范围较小的路由优先级比匹配范围大的优先级高（详见 **匹配优先级**）。
- 最先被定义的路由将会首先被用户请求匹配并调用。