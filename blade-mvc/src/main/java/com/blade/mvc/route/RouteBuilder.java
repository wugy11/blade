package com.blade.mvc.route;

import com.blade.kit.CollectionKit;
import com.blade.kit.ClassKit;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.annotation.Route;
import com.blade.mvc.hook.Invoker;
import com.blade.mvc.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class RouteBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(RouteBuilder.class);

	private RouteMatcher routeMatcher;

	public RouteBuilder(RouteMatcher routeMatcher) {
		this.routeMatcher = routeMatcher;
	}

	public void addWebHook(final Class<?> webHook, Object hook) {
		Path path = webHook.getAnnotation(Path.class);
		String pattern = "/.*";
		if (null != path) {
			pattern = path.value();
		}

		Method before = ClassKit.getMethod(webHook, "before", Invoker.class);
		Method after = ClassKit.getMethod(webHook, "after", Invoker.class);
		buildRoute(webHook, hook, before, pattern, HttpMethod.BEFORE);
		buildRoute(webHook, hook, after, pattern, HttpMethod.AFTER);
	}

	/**
	 * Parse all routing in a controller
	 *
	 * @param router
	 *            resolve the routing class
	 */
	public void addRouter(final Class<?> router, Object controller) {

		Method[] methods = router.getMethods();
		if (CollectionKit.isEmpty(methods))
			return;

		String nameSpace = null, suffix = null;

		Path pathAnnotation = router.getAnnotation(Path.class);
		if (null != pathAnnotation) {
			nameSpace = pathAnnotation.value();
			suffix = pathAnnotation.suffix();
		}

		if (null == nameSpace) {
			LOGGER.warn("Route [{}] not controller annotation", router.getName());
			return;
		}

		for (Method method : methods) {
			Route mapping = method.getAnnotation(Route.class);
			if (null == mapping)
				continue;
			HttpMethod methodType = mapping.method();
			String[] paths = mapping.values();
			for (String path : paths) {
				String pathV = getRoutePath(path, nameSpace, suffix);
				this.buildRoute(router, controller, method, pathV, methodType);
			}
		}
	}

	private String getRoutePath(String value, String nameSpace, String suffix) {
		String path = value.startsWith("/") ? value : "/" + value;
		nameSpace = nameSpace.startsWith("/") ? nameSpace : "/" + nameSpace;
		path = nameSpace + path;
		path = path.replaceAll("[/]+", "/");
		path = path.length() > 1 && path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
		path = path + suffix;
		return path;
	}

	/**
	 * Build a route
	 *
	 * @param clazz
	 *            route target execution class
	 * @param execMethod
	 *            route execution method
	 * @param path
	 *            route path
	 * @param method
	 *            route httpmethod
	 */
	private void buildRoute(Class<?> clazz, Object controller, Method execMethod, String path, HttpMethod method) {
		routeMatcher.addRoute(method, path, controller, clazz, execMethod);
	}

}