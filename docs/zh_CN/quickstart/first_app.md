---
root: false
title: 第一个Blade程序
sort: 0
---

{:toc}

# 第一个Bldae程序

迫不及待要开始了吗？本页提供了一个很好的 Blade `Hello World` 介绍。

## 开发启程

创建一个 maven 工程，加入 Blade 依赖：

![](https://ooo.0o0.ooo/2016/09/07/57cf914bc1eb3.png)

![](https://ooo.0o0.ooo/2016/09/07/57cf91670569f.png)

![](https://ooo.0o0.ooo/2016/09/07/57cf91702328c.png)

```xml
<dependencies>
	<dependency>
		<groupId>com.bladejava</groupId>
		<artifactId>blade-core</artifactId>
		<version>1.7.0</version>
	</dependency>
	<dependency>
		<groupId>com.bladejava</groupId>
		<artifactId>blade-embed-jetty</artifactId>
		<version>0.0.9</version>
	</dependency>
</dependencies>
```

创建启动类：

```java
package com.xxx.first;

import static com.blade.Blade.$;

import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.RouteHandler;

public class Application {

	public static void main(String[] args) {
		$().get("/", (request, response) -> {
			public void handle(Request request, Response response) {
				response.html("<h1>Hello Blade</h1>");
			}
		}).start(Application.class);
	}
	
}
```

我们还需要加一个 `log4j` 的配置文件，因为 blade 目前默认使用log4j作为日志服务，如果你倾向于其他的也可以配置。

_log4j.properties_

```bash
log4j.rootLogger = info, stdout
log4j.appender.stdout = org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout = org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern = [hello] %d %-5p [%t] %c | %m%n
```

将这个文件放在classpath下即可，如果你不这么做也不会报错，只是控制台看不到日志输出 :)

ok，现在启动 `Application` 的main函数你将看:

```bash
[hello] 2016-09-07 16:06:14,596 DEBUG [main] com.blade.config.ApplicationConfig | Add Resource: /public
[hello] 2016-09-07 16:06:14,597 DEBUG [main] com.blade.config.ApplicationConfig | Add Resource: /assets
[hello] 2016-09-07 16:06:14,597 DEBUG [main] com.blade.config.ApplicationConfig | Add Resource: /static
[hello] 2016-09-07 16:06:14,601 INFO  [main] com.blade.mvc.route.Routers | Add Route => GET	/
[hello] 2016-09-07 16:06:14,601 INFO  [main] com.blade.kit.base.Config | Load config [classpath:app.properties]
[hello] 2016-09-07 16:06:14,609 INFO  [main] com.blade.kit.base.Config | Load config [classpath:jetty.properties]
[hello] 2016-09-07 16:06:14,625 INFO  [main] org.eclipse.jetty.util.log | Logging initialized @188ms
[hello] 2016-09-07 16:06:14,715 INFO  [main] org.eclipse.jetty.server.Server | jetty-9.2.12.v20150709
[hello] 2016-09-07 16:06:14,886 INFO  [main] com.blade.mvc.DispatcherServlet | jdk.version	=> 1.8.0_101
[hello] 2016-09-07 16:06:14,886 INFO  [main] com.blade.mvc.DispatcherServlet | user.dir		=> D:\workspace\first-blade-app
[hello] 2016-09-07 16:06:14,886 INFO  [main] com.blade.mvc.DispatcherServlet | java.io.tmpdir	=> C:\Users\ADMINI~1\AppData\Local\Temp\
[hello] 2016-09-07 16:06:14,886 INFO  [main] com.blade.mvc.DispatcherServlet | user.timezone	=> GMT+08:00
[hello] 2016-09-07 16:06:14,886 INFO  [main] com.blade.mvc.DispatcherServlet | file.encodin	=> UTF-8
[hello] 2016-09-07 16:06:14,888 INFO  [main] com.blade.mvc.DispatcherServlet | blade.webroot	=> D:\workspace\first-blade-app\target\classes
[hello] 2016-09-07 16:06:14,893 INFO  [main] com.blade.mvc.DispatcherServlet | blade.isDev = true

		 __, _,   _, __, __,
		 |_) |   /_\ | \ |_
		 |_) | , | | |_/ |
		 ~   ~~~ ~ ~ ~   ~~~
		 :: Blade :: (v1.7.0-beta)

[hello] 2016-09-07 16:06:14,896 INFO  [main] com.blade.mvc.DispatcherServlet | Blade initialize successfully, Time elapsed: 10 ms.
[hello] 2016-09-07 16:06:14,896 INFO  [main] org.eclipse.jetty.server.handler.ContextHandler | Started o.e.j.w.WebAppContext@5a61f5df{/,file:/D:/workspace/first-blade-app/target/classes/,AVAILABLE}
[hello] 2016-09-07 16:06:14,944 INFO  [main] org.eclipse.jetty.server.ServerConnector | Started ServerConnector@3e6fa38a{HTTP/1.1}{0.0.0.0:9000}
[hello] 2016-09-07 16:06:14,944 INFO  [main] org.eclipse.jetty.server.Server | Started @514ms
[hello] 2016-09-07 16:06:14,944 INFO  [main] com.blade.embedd.EmbedJettyServer | Blade Server Listen on 0.0.0.0:9000
```

打开浏览器，输入 [http://127.0.0.1:9000](http://127.0.0.1:9000)

wow~ 看起来还不错，这就算走进 Blade 的大门了，接下来的旅行将更有[意思](/docs/quickstart/dowhat)。