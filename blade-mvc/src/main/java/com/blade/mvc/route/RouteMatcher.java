package com.blade.mvc.route;

import com.blade.BladeException;
import com.blade.ioc.annotation.Order;
import com.blade.kit.BladeKit;
import com.blade.kit.CollectionKit;
import com.blade.kit.PathKit;
import com.blade.kit.ClassKit;
import com.blade.mvc.hook.Invoker;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RouteMatcher {

	private static final Logger log = LoggerFactory.getLogger(RouteMatcher.class);

	private static final String METHOD_NAME = "handle";

	// Storage URL and route
	private Map<String, RouteBean> routes = CollectionKit.newHashMap();
	private Map<String, List<RouteBean>> hooks = CollectionKit.newHashMap();
	private List<RouteBean> middlewares;

	private Map<String, Method[]> classMethosPool = CollectionKit.newConcurrentMap();
	private Map<Class<?>, Object> controllerPool = CollectionKit.newConcurrentMap();

	private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile(":(\\w+)");
	private static final String PATH_VARIABLE_REPLACE = "([^/]+)";

	private Map<HttpMethod, Map<Integer, FastRouteMappingInfo>> regexRoutes = CollectionKit.newHashMap();

	private Map<String, RouteBean> staticRoutes = CollectionKit.newHashMap();
	private Map<HttpMethod, Pattern> regexRoutePatterns = CollectionKit.newHashMap();
	private Map<HttpMethod, Integer> indexes = new HashMap<>();
	private Map<HttpMethod, StringBuilder> patternBuilders = CollectionKit.newHashMap();

	private RouteBean addRoute(HttpMethod httpMethod, String path, RouteHandler handler, String methodName)
			throws NoSuchMethodException {
		Class<?> handleType = handler.getClass();
		Method method = handleType.getMethod(methodName, Request.class, Response.class);
		return addRoute(httpMethod, path, handler, RouteHandler.class, method);
	}

	public RouteBean addRoute(HttpMethod httpMethod, String path, Object controller, Class<?> controllerType,
			Method method) {

		// [/** | /*]
		path = "*".equals(path) ? "/.*" : path;
		path = path.replace("/**", "/.*").replace("/*", "/.*");

		String key = path + "#" + httpMethod.toString();

		// exist
		if (this.routes.containsKey(key)) {
			log.warn("\tRoute {} -> {} has exist", path, httpMethod.toString());
		}

		RouteBean route = new RouteBean(httpMethod, path, controller, controllerType, method);
		if (BladeKit.isWebHook(httpMethod)) {
			Order order = controllerType.getAnnotation(Order.class);
			if (null != order) {
				route.setSort(order.value());
			}
			if (this.hooks.containsKey(key)) {
				this.hooks.get(key).add(route);
			} else {
				List<RouteBean> empty = CollectionKit.newArrayList();
				empty.add(route);
				this.hooks.put(key, empty);
			}
		} else {
			this.routes.put(key, route);
		}
		return route;
	}

	public RouteBean addRoute(String path, RouteHandler handler, HttpMethod httpMethod) {
		try {
			return addRoute(httpMethod, path, handler, METHOD_NAME);
		} catch (Exception e) {
			log.error("", e);
			return null;
		}
	}

	public RouteBean lookupRoute(String httpMethod, String path) {
		path = parsePath(path);
		String routeKey = path + '#' + httpMethod.toUpperCase();
		RouteBean route = staticRoutes.get(routeKey);
		if (null != route) {
			return route;
		}
		route = staticRoutes.get(path + "#ALL");
		if (null != route) {
			return route;
		}

		Map<String, String> uriVariables = new LinkedHashMap<>();
		HttpMethod requestMethod = HttpMethod.valueOf(httpMethod);
		try {
			Pattern pattern = regexRoutePatterns.get(requestMethod);
			if (null == pattern) {
				return null;
			}
			Matcher matcher = pattern.matcher(path);
			boolean matched = matcher.matches();
			if (!matched) {
				requestMethod = HttpMethod.ALL;
				pattern = regexRoutePatterns.get(requestMethod);
				if (null == pattern) {
					return null;
				}
				matcher = pattern.matcher(path);
				matched = matcher.matches();
			}
			if (matched) {
				int i;
				for (i = 1; matcher.group(i) == null; i++)
					;
				FastRouteMappingInfo mappingInfo = regexRoutes.get(requestMethod).get(i);
				route = mappingInfo.getRoute();

				// find path variable
				String uriVariable;
				int j = 0;
				while (++i <= matcher.groupCount() && (uriVariable = matcher.group(i)) != null) {
					uriVariables.put(mappingInfo.getVariableNames().get(j++), uriVariable);
				}
				route.setPathParams(uriVariables);
				log.trace("lookup path: " + path + " uri variables: " + uriVariables);
			}
			return route;
		} catch (Exception e) {
			throw new BladeException(e);
		}
	}

	/**
	 * Find all in before of the hook
	 *
	 * @param path
	 *            request path
	 */
	public List<RouteBean> getBefore(String path) {
		return get(path, HttpMethod.BEFORE);
	}

	/**
	 * Find all in after of the hooks
	 *
	 * @param path
	 *            request path
	 */
	public List<RouteBean> getAfter(String path) {
		return get(path, HttpMethod.AFTER);
	}

	private List<RouteBean> get(String path, HttpMethod httpMethod) {
		String cleanPath = parsePath(path);

		List<RouteBean> routBeanList = hooks.values().stream().flatMap(routes -> routes.stream())
				.sorted(Comparator.comparingInt(route -> route.getSort()))
				.filter(route -> route.getHttpMethod() == httpMethod && matchesPath(route.getPath(), cleanPath))
				.collect(Collectors.toList());

		this.giveMatch(path, routBeanList);
		return routBeanList;
	}

	public List<RouteBean> getMiddlewares() {
		return this.middlewares;
	}

	/**
	 * Sort of path
	 *
	 * @param uri
	 *            request uri
	 * @param routes
	 *            route list
	 */
	private void giveMatch(final String uri, List<RouteBean> routes) {
		routes.stream().sorted((o1, o2) -> {
			if (o2.getPath().equals(uri)) {
				return o2.getPath().indexOf(uri);
			}
			return -1;
		});
	}

	/**
	 * Matching path
	 *
	 * @param routePath
	 *            route path
	 * @param pathToMatch
	 *            match path
	 * @return return match is success
	 */
	private boolean matchesPath(String routePath, String pathToMatch) {
		routePath = routePath.replaceAll(PathKit.VAR_REGEXP, PathKit.VAR_REPLACE);
		return pathToMatch.matches("(?i)" + routePath);
	}

	/**
	 * Parse PathKit
	 *
	 * @param path
	 *            route path
	 * @return return parsed path
	 */
	private String parsePath(String path) {
		path = PathKit.fixPath(path);
		try {
			URI uri = new URI(path);
			return uri.getPath();
		} catch (URISyntaxException e) {
			log.error("parse [" + path + "] error", e);
			return null;
		}
	}

	// a bad way
	public void register() {
		routes.values().forEach(route -> log.info("Add route => {}", route));
		hooks.values().forEach(route -> log.info("Add hook  => {}", route));

		List<RouteBean> routeHandlers = new ArrayList<>(routes.values());
		routeHandlers.addAll(hooks.values().stream().findAny().orElse(new ArrayList<>()));

		Stream.of(routes.values(), hooks.values().stream().findAny().orElse(new ArrayList<>())).flatMap(c -> c.stream())
				.forEach(this::registerRoute);

		patternBuilders.keySet().stream().filter(BladeKit::notIsWebHook).forEach(httpMethod -> {
			StringBuilder patternBuilder = patternBuilders.get(httpMethod);
			if (patternBuilder.length() > 1) {
				patternBuilder.setCharAt(patternBuilder.length() - 1, '$');
			}
			log.debug("Fast Route Method: {}, regex: {}", httpMethod, patternBuilder);
			regexRoutePatterns.put(httpMethod, Pattern.compile(patternBuilder.toString()));
		});
	}

	public void registerRoute(RouteBean route) {
		String path = parsePath(route.getPath());
		Matcher matcher = PATH_VARIABLE_PATTERN.matcher(path);
		boolean find = false;
		List<String> uriVariableNames = new ArrayList<>();
		while (matcher.find()) {
			if (!find) {
				find = true;
			}
			String group = matcher.group(0);
			uriVariableNames.add(group.substring(1)); // {id} -> id
		}
		HttpMethod httpMethod = route.getHttpMethod();
		if (find || BladeKit.isWebHook(httpMethod)) {
			if (regexRoutes.get(httpMethod) == null) {
				regexRoutes.put(httpMethod, new HashMap<>());
				patternBuilders.put(httpMethod, new StringBuilder("^"));
				indexes.put(httpMethod, 1);
			}
			int i = indexes.get(httpMethod);
			regexRoutes.get(httpMethod).put(i, new FastRouteMappingInfo(route, uriVariableNames));
			indexes.put(httpMethod, i + uriVariableNames.size() + 1);
			patternBuilders.get(httpMethod).append("(").append(matcher.replaceAll(PATH_VARIABLE_REPLACE)).append(")|");
		} else {
			String routeKey = path + '#' + httpMethod.toString();
			if (staticRoutes.get(routeKey) == null) {
				staticRoutes.put(routeKey, route);
			}
		}
	}

	public void clear() {
		this.routes.clear();
		this.hooks.clear();
		this.classMethosPool.clear();
		this.controllerPool.clear();
		this.regexRoutePatterns.clear();
		this.staticRoutes.clear();
		this.regexRoutes.clear();
		this.indexes.clear();
		this.patternBuilders.clear();
	}

	public void initMiddlewares(List<WebHook> hooks) {
		this.middlewares = hooks.stream().map(webHook -> {
			Method method = ClassKit.getMethod(WebHook.class, "before", Invoker.class);
			return new RouteBean(HttpMethod.BEFORE, "/.*", webHook, WebHook.class, method);
		}).collect(Collectors.toList());
	}

	private class FastRouteMappingInfo {
		RouteBean route;
		List<String> variableNames;

		public FastRouteMappingInfo(RouteBean route, List<String> variableNames) {
			this.route = route;
			this.variableNames = variableNames;
		}

		public RouteBean getRoute() {
			return route;
		}

		public List<String> getVariableNames() {
			return variableNames;
		}
	}

}