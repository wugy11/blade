package com.blade.mvc;

import io.netty.handler.codec.http.HttpVersion;

/**
 * @author biezhi
 *         2017/6/2
 */
public interface Const {

    String VERSION = "2.0.0-SNAPSHOT";

    String CLASSPATH = Const.class.getResource("/").getPath();

    String CONTENT_TYPE_XML = "text/xml; charset=UTF-8";
    String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";
    String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";
    String CONTENT_TYPE_TEXT = "text/plain; charset=UTF-8";

    String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

    HttpVersion HTTP_VERSION = HttpVersion.HTTP_1_1;

    String WEB_JARS = "/webjars/";

    String PLUGIN_PACKAGE_NAME = "com.blade.plugin";

    String DEFAULT_SERVER_ADDRESS = "0.0.0.0";
    int DEFAULT_SERVER_PORT = 9000;

    //-------------environment key---------------//
    String ENV_KEY_DEV_MODE = "app.devMode";
    String ENV_KEY_APP_NAME = "app.name";
    String ENV_KEY_MONITOR_ENABLE = "app.monitor.enable";

    String ENV_KEY_GZIP_ENABLE = "http.gzip.enable";
    String ENV_KEY_SESSION_KEY = "http.session.key";
    String ENV_KEY_SESSION_TIMEOUT = "http.session.timeout";

    String ENV_KEY_PAGE_404 = "mvc.view.404";
    String ENV_KEY_PAGE_500 = "mvc.view.500";
    String ENV_KEY_STATIC_DIRS = "mvc.statics";
    String ENV_KEY_STATIC_LIST = "mvc.statics.list";
    String ENV_KEY_TEMPLATE_PATH = "mvc.template.path";

    String ENV_KEY_SERVER_ADDRESS = "server.address";
    String ENV_KEY_SERVER_PORT = "server.port";

    String ENV_KEY_BOOT_CONF = "boot_conf";

}
