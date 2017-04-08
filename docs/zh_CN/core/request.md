---
root: false
title: 请求
sort: 4
---

{:toc}

# 请求对象

在 Blade 中的核心对象 `Request` 和 `Response`，本次讲解 `Request`，它是对 `HttpServletRequest` 的一次包装，尽管 `HttpServletRequest` 已经够我们用，但不够优雅，在我的开发过程中发现还有一些总是写在 `BaseAction` 里的方法都可以在这里直接作为 `API`。

## 常用功能

### 获取URL/表单的数据

```java
String q1 = request.query("q1");
String q2 = request.query("q2", "hello");
int q3 = request.queryInt("page", 1);
```

这里第二个参数是当 `q2` 没有值的时候默认值是 `hello`

### 获取PATH/路径上的数据

比如定义了一个路由URL为：`/users/:uid` 我们需要获取 `uid`

```java
int uid = request.pathInt("uid");
```

### 获取上传文件信息

```java
FileItem fileItem = request.fileItem("upload_img");
File file = fileItem.file();
String fileName = fileItem.fileName();
```

获取在 `form` 表单中定义了 `input` 类型为 file 且 name 为`upload_img`
的文件，我们将它封装为 `FileItem` 对象，在这里可以通过相关方法获取到文件和文件名以及文件大小等相关信息。

**获取所有文件**

```java
FileItem[] fileItems = request.fileItems();
```

### 获取头信息/Header

```java
String userAgent = request.header("user-agent");
```

### 获取Cookie数据

```java
String sid = request.cookie("sid");
```

### 存储数据到Request Attribute

```java
request.attribute("name", "jack");
```

### 读取Session数据

```java
Session session = request.session();
User user = session.attribute("login_user");
```

### 读取Request Body

```java
BodyParser body = request.body();
String strBody = body.asString();
```

## 其他

### 获取原生 `HttpServletRequest` 对象

```java
HttpServletRequest r = request.raw();
```

### 根据form表单name前缀将请求封装为Java类

```java
User user = request.model("u", User.class);
```

此时你的form表单形如下面：
 
```html
<form method="post" action="/users/save">
	<input type="text" name="u.username" />
	<input type="text" name="u.age" />
	<input type="email" name="u.email" />
	<button type="submit">提交</button>
</form>
```

### 获取当前请求的的路由对象

```java
Route route = request.route();
```

