---
root: false
title: 拦截器
sort: 3
---

{:toc}

# 拦截器

## 什么是拦截器

拦截器在概念上和servlet过滤器或JDKs代理类一样。拦截器允许横切功能在动作和框架中单独实现。你可以使用拦截器实现下面的内容：

- 在动作被调用之前提供预处理逻辑
- 在动作被调用之后提供预处理逻辑
- 捕获异常，以便可以执行交替处理

Blade 中的拦截器是在一个请求执行前，后可以做一些自定义的处理，比如存储数据，校验数据，过滤请求等。

## 实现原理

Blade 中实现拦截器的方式非常简单，应用启动的时候框架会加载用户定义好的拦截器组件到IOC容器中，当处理一个路由的请求前后去检查在此之前和之后注册的拦截器组件，找到匹配的拦截器执行。

## 如何使用？

拦截器的使用方式也分多种，我们推荐按照blade的约定来写一个web程序，在你项目的基础包下面建立一个名为 `interceptor` 的包，我们将拦截器组件存放在这里，这里给出一个拦截器组件的示例。

```java
@Intercept
public class BaseInterceptor implements Interceptor {
	
	private static final Logger LOGGE = LoggerFactory.getLogger(BaseInterceptor.class);
	
	@Override
	public boolean before(Request request, Response response) {
		LOGGE.info("request user-agent：" + request.userAgent());
		return true;
	}
	
	@Override
	public boolean after(Request request, Response response) {
		return true;
	}
	
}
```

这个拦截器非常的简单，在每次请求之前输出一下当前请求的 `user-agent` 请求头信息。

### 具体步骤

1. 编写一个java类，实现`Interceptor`接口的`before`,`after`方法
2. 在该类添加注解`@Intercept`，用于配置拦截表达式。默认拦截所有请求
3. 拦截器的方法返回true表示允许本次请求，反之禁止继续请求



