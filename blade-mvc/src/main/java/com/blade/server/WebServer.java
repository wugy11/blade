package com.blade.server;

import com.blade.Blade;
import com.blade.Environment;
import com.blade.event.BeanProcessor;
import com.blade.event.EventType;
import com.blade.ioc.BeanDefine;
import com.blade.ioc.DynamicContext;
import com.blade.ioc.Ioc;
import com.blade.ioc.OrderComparator;
import com.blade.ioc.annotation.Bean;
import com.blade.ioc.reader.ClassInfo;
import com.blade.kit.BladeKit;
import com.blade.kit.ReflectKit;
import com.blade.kit.StringKit;
import com.blade.mvc.Const;
import com.blade.mvc.WebContext;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.route.RouteBuilder;
import com.blade.mvc.route.RouteMatcher;
import com.blade.mvc.ui.DefaultUI;
import com.blade.mvc.ui.template.DefaultEngine;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.blade.mvc.Const.CLASSPATH;

/**
 * @author biezhi
 *         2017/5/31
 */
public class WebServer {

    private static final Logger log = LoggerFactory.getLogger(WebServer.class);

    private Blade blade;
    private Environment environment;

    private EventLoopGroup bossGroup, workerGroup;
    private Channel channel;

    private RouteBuilder routeBuilder;

    public void initAndStart(Blade blade, String[] args) throws Exception {
        this.blade = blade;
        this.environment = blade.environment();

        long initStart = System.currentTimeMillis();
        log.info("Blade environment: jdk.version\t=> {}", System.getProperty("java.version"));
        log.info("Blade environment: user.dir\t\t=> {}", System.getProperty("user.dir"));
        log.info("Blade environment: java.io.tmpdir\t=> {}", System.getProperty("java.io.tmpdir"));
        log.info("Blade environment: user.timezone\t=> {}", System.getProperty("user.timezone"));
        log.info("Blade environment: file.encoding\t=> {}", System.getProperty("file.encoding"));
        log.info("Blade environment: classpath\t\t=> {}", CLASSPATH);

        this.loadConfig();
        this.initConfig();

        WebContext.init(blade, "/", false);

        this.initIoc();

        this.startServer(initStart);

    }

    private void initIoc() {
        RouteMatcher routeMatcher = blade.routeMatcher();
        routeMatcher.initMiddlewares(blade.middlewares());

        routeBuilder = new RouteBuilder(routeMatcher);

        blade.scanPackages().stream()
                .flatMap(DynamicContext::recursionFindClasses)
                .map(ClassInfo::getClazz)
                .filter(ReflectKit::isNormalClass)
                .forEach(this::parseCls);

        routeMatcher.register();

        beanProcessors.stream().sorted(new OrderComparator<>()).forEach(b -> b.preHandle(blade));

        Ioc ioc = blade.ioc();
        if (BladeKit.isNotEmpty(ioc.getBeans())) {
            log.info("⬢ Register bean: {}", ioc.getBeans());
        }

        List<BeanDefine> beanDefines = ioc.getBeanDefines();
        if (BladeKit.isNotEmpty(beanDefines)) {
            beanDefines.forEach(b -> BladeKit.injection(ioc, b));
        }

        beanProcessors.stream().sorted(new OrderComparator<>()).forEach(b -> b.processor(blade));

    }

    private void startServer(long startTime) throws Exception {
        // Configure SSL.
        SslContext sslCtx = null;
        boolean SSL = false;

        // Configure the server.
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.option(ChannelOption.SO_BACKLOG, 1024);

        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new HttpServerInitializer(blade, sslCtx));

        String address = environment.get(Const.ENV_KEY_SERVER_ADDRESS, Const.DEFAULT_SERVER_ADDRESS);
        int port = environment.getInt(Const.ENV_KEY_SERVER_PORT, Const.DEFAULT_SERVER_PORT);

        channel = b.bind(address, port).sync().channel();
        String appName = environment.get(Const.ENV_KEY_APP_NAME, "Blade");

        log.info("⬢ {} initialize successfully, Time elapsed: {} ms.", appName, System.currentTimeMillis() - startTime);
        log.info("⬢ Blade start with {}:{}", address, port);
        log.info("⬢ Open your web browser and navigate to {}://{}:{} ⚡", (SSL ? "https" : "http"), address.replace("0.0.0.0", "127.0.0.1"), port);

        blade.eventManager().fireEvent(EventType.SERVER_STARTED, blade);
    }

    private List<BeanProcessor> beanProcessors = new ArrayList<>();

    private void parseCls(Class<?> clazz) {
        if (null != clazz.getAnnotation(Bean.class)) blade.register(clazz);
        if (null != clazz.getAnnotation(Path.class)) {
            if (null == clazz.getAnnotation(Bean.class)) {
                blade.register(clazz);
            }
            Object controller = blade.ioc().getBean(clazz);
            routeBuilder.addRouter(clazz, controller);
        }
        if (ReflectKit.hasInterface(clazz, WebHook.class)) {
            if (null != clazz.getAnnotation(Bean.class)) {
                Object hook = blade.ioc().getBean(clazz);
                routeBuilder.addWebHook(clazz, hook);
            }
        }
        if (ReflectKit.hasInterface(clazz, BeanProcessor.class))
            beanProcessors.add((BeanProcessor) blade.ioc().getBean(clazz));

    }

    private void loadConfig() {

        String bootConf = blade.environment().get(Const.ENV_KEY_BOOT_CONF, "classpath:app.properties");

        Environment bootEnv = Environment.of(bootConf);

        bootEnv.props().forEach((key, value) -> environment.set(key.toString(), value));

        blade.register(environment);

    }

    private void initConfig() {

        if (null != blade.bootClass()) {
            if (blade.scanPackages().size() == 1 && blade.scanPackages().contains(Const.PLUGIN_PACKAGE_NAME)) {
                blade.scanPackages(blade.bootClass().getPackage().getName());
            }
        }

        DefaultUI.printBanner();

        String statics = environment.get(Const.ENV_KEY_STATIC_DIRS, "");
        if (StringKit.isNotBlank(statics)) {
            blade.addStatics(statics.split(","));
        }

        if (environment.getBoolean(Const.ENV_KEY_MONITOR_ENABLE, true)) {
            DefaultUI.registerStatus(blade);
        }

        String templatePath = environment.get(Const.ENV_KEY_TEMPLATE_PATH, "templates");
        if (templatePath.charAt(0) == '/') {
            templatePath = templatePath.substring(1);
        }
        if (templatePath.endsWith("/")) {
            templatePath = templatePath.substring(0, templatePath.length() - 1);
        }
        DefaultEngine.TEMPLATE_PATH = templatePath;
    }

    public void stop() {
        if (this.bossGroup != null) {
            this.bossGroup.shutdownGracefully();
        }
        if (this.workerGroup != null) {
            this.workerGroup.shutdownGracefully();
        }
    }

    public void join() throws InterruptedException {
        try {
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            this.stop();
        }
    }

}
