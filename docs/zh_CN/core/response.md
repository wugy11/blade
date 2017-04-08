---
root: false
title: 响应
sort: 5
---

{:toc}

# 响应对象

响应对象也是一个Web框架的核心对象，这里我们来看下Blade中的响应对象该怎么用。

## 常用功能

### 设置Cookie

```java
response.cookie(path, cookieName, value);
```

更详细的 API 可以查看 `/apidocs/index.html`

### JSON格式输出到客户端

```java
String json = "{\"name":\"jack", \"age": 18}";
response.json(json);
```

```java
User user = new User();
user.setAge(21);
user.setUsername("jack w");
response.json(user);
```

### 重定向到其他页面

**重定向到本站其他位置**

```java
response.go("/login");
```

**重定向到其他网站**

```java
response.redirect("https://github.com");
```

### 输出HTML

```java
String html = "<h1>Hello Blade!</h1>";
response.html(html);
```

### 设置头信息

```java
response.header("server", "nginxxxx");
```

## 其他

### 获取原生 `HttpServletResponse` 对象

```java
HttpServletResponse r = response.raw();
```
