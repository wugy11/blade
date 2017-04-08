---
root: false
title: 系统架构
sort: 1
---

{:toc}

# 系统架构

## 概念

Blade 从Rails 和 Play! 中吸收了许多成熟的设计思想, 许多相同的思想被用到了框架的设计和接口中。

Blade 通过简单的约定来支持 MVC 设计模式，轻量、开发效率高。

## MVC

- 模型 描述基本的数据对象，特定的查询和更新逻辑。
- 视图 一些模板，用于将数据呈现给用户。
- 控制器 执行用户的请求，准备用户所需的数据，并指定模板进行渲染。

## 整体设计

![](https://ooo.0o0.ooo/2016/09/07/57cf843566a9a.png)

`blade`是基于`blade-core`为核心的构建的，是一个高度解耦的框架。

`blade`设计之初就考虑了模块化使用，用独立的组件进行开发，部分组件不依赖`blade`，例如：你可以使用`blade-cache`模块来做你的缓存逻辑；使用`blade-kit`模块来作为你的基础工具包。

## 执行逻辑

既然`blade`是基于核心模块构建的，那么他的执行逻辑是怎么样的呢？`blade`是一个典型的MVC架构，他的执行逻辑如下图所示：

 ![](https://i.imgur.com/joP7aBH.png)

## 项目结构

blade 推荐你使用 `maven` 进行项目的构建，作者认为任何一门语言都需要一个主流统一的包管理器，可能速度在国内不是那么乐观，但框架只有不到300kb，下载非常快，blade做到了一次编码直接部署的功能，我们提倡使用普通maven工程的结构来开发web程序，看起来也更简单，项目结构如下:

一般的`blade`项目的目录如下所示，是一个maven类型的项目：

```bash
├─ src
├── main
├─── java
│	├── Application
│	├── config
│   ├── controller
│   ├── service
│   └── interceptor
├─── resources
│   ├── app.properties
│   ├── static
│   │	├── js
│	│	├── css
└───└── templates
```

这样的结构非常清晰，是一个典型的MVC应用，我们将一个web程序分为如下部分:

**java源码**

- `Application`:启动程序
- `config`: 自定义的配置包文件
- `controller`: 控制器和路由的存储位置
- `service`: 服务接口和实现 (如果需要可以加，非必需)
- `interceptor`: 路由拦截器所在包

**resources资源**

- `app.properties`: 程序主配置文件(如果需要可以加，非必需)
- `static`: 静态资源存放文件夹
- `templates`: 模板文件存放文件夹

如上的程序结构是 `blade` 推荐的，后面我们会详细讲解这个文档站点的开发过程，带你更深入的体验blade的魅力。
