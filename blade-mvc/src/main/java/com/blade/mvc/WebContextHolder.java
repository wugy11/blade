package com.blade.mvc;

import com.blade.Blade;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import io.netty.util.concurrent.FastThreadLocal;

/**
 * Blade Web Context
 *
 * @author biezhi 2017/6/1
 */
public class WebContextHolder {

	// used netty fast theadLocal
	private static final FastThreadLocal<WebContextHolder> fastThreadLocal = new FastThreadLocal<>();

	private static Blade blade;
	private static String contextPath;
	private static boolean ssl;

	private Request request;
	private Response response;

	public WebContextHolder(Request request, Response response) {
		this.request = request;
		this.response = response;
	}

	public static void set(WebContextHolder webContext) {
		fastThreadLocal.set(webContext);
	}

	public static WebContextHolder get() {
		return fastThreadLocal.get();
	}

	public static void remove() {
		fastThreadLocal.remove();
	}

	public static Request request() {
		WebContextHolder webContext = get();
		return null != webContext ? webContext.request : null;
	}

	public static Response response() {
		WebContextHolder webContext = get();
		return null != webContext ? webContext.response : null;
	}

	public static void init(Blade blade_, String contextPath_, boolean ssl_) {
		blade = blade_;
		contextPath = contextPath_;
		ssl = ssl_;
	}

	public static Blade blade() {
		return blade;
	}

	public static boolean isSSL() {
		return ssl;
	}

	public static String contextPath() {
		return contextPath;
	}

}
