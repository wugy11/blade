---
root: false
title: 配置
sort: 7
---

{:toc}

# 配置

Blade 中的配置也比较简单，它的模式是一个主配置文件 `app.properties`，也可以硬编码进行配置。

## 配置文件

```bash
server.port = 9002
app.name = nice
app.dev = true

app.upload_dir = /Users/biezhi/workspace/annal_www
app.site_url = http://127.0.0.1:9000
app.aes_salt = 0123456789abcdef

# email config
mail.smtp.host=smtp.qq.com
mail.user=xxx
mail.pass=xxx
mail.from=Nice
```

这是一个已经上线的应用的简单配置，我们来解释一下常用的几个：

- server.port：web服务器的启动端口，默认为9000
- app.name：当前应用的别名，在启动时会打印出来
- app.dev：是否是开发者模式，开发者模式错误信息会直接显示
- mvc.statics：设置一个目录为静态资源文件目录，存放在`resources`目录之下
- mvc.view.404：设置当出现404的统一页面
- mvc.view.500：设置当出现500错误的统一页面

## 获取配置

 我们习惯在主配置文件中设置好，比如邮箱配置，在系统启动的时候将它获取到并保存为常量。Blade的约定是将所有读取配置的操作放在基础包下的 `config` 包中，定义一个Java类继承自 `BaseConfig` 接口，在这里可以获取到配置信息。

```java
@Component
@Order(sort = 1)
public class DBConfig implements BaseConfig {

    public ActiveRecord activeRecord;
	
    @Override
    public void config(Configuration configuration) {
        try {
            InputStream in = DBConfig.class.getClassLoader().getResourceAsStream("druid.properties");
            Properties props = new Properties();
            props.load(in);
            DataSource dataSource = DruidDataSourceFactory.createDataSource(props);
            activeRecord = new SampleActiveRecord(dataSource);
            Blade.$().ioc().addBean(activeRecord);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
```

这个操作是加载数据库的配置并注册一个数据库的 `ActiveRecord` 对象到IOC容器中方便后续使用。

```java
@Component
@Order(sort = 2)
public class LoadConfig implements BaseConfig {
	
    @Override
    public void config(Configuration configuration) {
        Config config = configuration.config();
        Constant.MAIL_HOST = config.get("mail.smtp.host");
        Constant.MAIL_USER = config.get("mail.user");
        Constant.MAIL_USERNAME = config.get("mail.from");
        Constant.MAIL_PASS = config.get("mail.pass");

    }

}
```

在这里加载了邮件配置并将它保存在常量中供其他地方使用。

