---
root: false
title: 视图
sort: 6
---

{:toc}

# 视图

在一个MVC框架或者项目中 `View` 一定是非常重要的一环，在 Blade 中也集成了几款优秀的模板引擎来做视图展现。本人最常用的 [jetbrick-template](https://github.com/subchen/jetbrick-template-2x)，业界流行的 [velocity](http://velocity.apache.org/) 当然也可以集成其他的，代码也非常少，你可以自定义你喜欢的。

## 视图位置

 在 Blade 新版中已经去除了web方式的项目模型，我们推荐使用一个普通的maven工程来开发一个web应用，我们将所有的模板文件存放在 `resources/templates` 文件夹下方便管理。

## 使用

在路由中使用简直不能再简单了。

```java
@Route("index")
public String index(){
	return "index.html";
}
```

此时访问 `http://127.0.0.1:9000/index` 时就会跳转到 `resources/templates/index.html` 这个文件上，具体的解析操作由模板引擎帮你完成。