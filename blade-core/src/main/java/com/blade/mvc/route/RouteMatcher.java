/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.mvc.route;

import com.blade.Blade;
import com.blade.exception.BladeException;
import com.blade.kit.CollectionKit;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default Route Matcher
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.7.1-release
 */
public class RouteMatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteMatcher.class);

    // Storage URL and route
    private Map<String, Route> routes = null;
    private Map<String, Route> interceptors = null;

    private List<Route> interceptorRoutes = CollectionKit.newArrayList(8);

    private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile(":(\\w+)");

    private static final String PATH_VARIABLE_REPLACE = "([^/]+)";

    private Map<HttpMethod, Map<Integer, FastRouteMappingInfo>> regexRoutes = new HashMap<>();

    private Map<String, Route> staticRoutes = new HashMap<>();
    private Map<HttpMethod, Pattern> regexRoutePatterns = new HashMap<>();
    private Map<HttpMethod, Integer> indexes = new HashMap<>();
    private Map<HttpMethod, StringBuilder> patternBuilders = new HashMap<>();

    public RouteMatcher(Routers routers) {
        this.update(routers);
    }

    public void update() {
        this.update(Blade.$().routers());
    }

    public void update(Routers routers) {
        this.routes = routers.getRoutes();
        this.interceptors = routers.getInterceptors();
        Collection<Route> inters = interceptors.values();
        if (!inters.isEmpty()) {
            this.interceptorRoutes.addAll(inters);
        }
        this.staticRoutes.clear();
        this.regexRoutePatterns.clear();
        this.regexRoutes.clear();
        this.indexes.clear();
        this.register();
    }

    public Route lookupRoute(String httpMethod, String path) throws BladeException {
        path = parsePath(path);
        String routeKey = path + '#' + httpMethod.toUpperCase();
        Route route = staticRoutes.get(routeKey);
        if (null != route) {
            return route;
        }
        route = staticRoutes.get(path + "#ALL");
        if (null != route) {
            return route;
        }

        Map<String, String> uriVariables = CollectionKit.newLinkedHashMap();
        HttpMethod requestMethod = HttpMethod.valueOf(httpMethod);
        try {
            Matcher matcher = regexRoutePatterns.get(requestMethod).matcher(path);
            boolean matched = matcher.matches();
            if (!matched) {
                requestMethod = HttpMethod.ALL;
                matcher = regexRoutePatterns.get(requestMethod).matcher(path);
                matched = matcher.matches();
            }
            if (matched) {
                int i;
                for (i = 1; matcher.group(i) == null; i++) ;
                FastRouteMappingInfo mappingInfo = regexRoutes.get(requestMethod).get(i);
                route = mappingInfo.getRoute();

                // find path variable
                String uriVariable;
                int j = 0;
                while (++i <= matcher.groupCount() && (uriVariable = matcher.group(i)) != null) {
                    uriVariables.put(mappingInfo.getVariableNames().get(j++), uriVariable);
                }
                route.setPathParams(uriVariables);
                LOGGER.trace("lookup path: " + path + " uri variables: " + uriVariables);
            }
            return route;
        } catch (Exception e) {
            throw new BladeException(e);
        }
    }

    /**
     * Find a route
     *
     * @param httpMethod httpMethod
     * @param path       request path
     * @return return route object
     */
    public Route getRoute(String httpMethod, String path) {
        try {
            return lookupRoute(httpMethod, path);
        } catch (BladeException e) {
            Throwable t = e.getCause();
            if (t instanceof NullPointerException) {
            } else {
                LOGGER.warn("", e);
            }
        }
        return null;
    }

    /**
     * Find all in before of the interceptor
     *
     * @param path request path
     * @return return interceptor list
     */
    public List<Route> getBefore(String path) {
        List<Route> befores = CollectionKit.newArrayList();
        String cleanPath = parsePath(path);
        interceptorRoutes.forEach(route -> {
            if (matchesPath(route.getPath(), cleanPath) && route.getHttpMethod() == HttpMethod.BEFORE) {
                befores.add(route);
            }
        });
        this.giveMatch(path, befores);
        return befores;
    }

    /**
     * Find all in after of the interceptor
     *
     * @param path request path
     * @return return interceptor list
     */
    public List<Route> getAfter(String path) {
        List<Route> afters = CollectionKit.newArrayList();
        String cleanPath = parsePath(path);
        interceptorRoutes.forEach(route -> {
            if (matchesPath(route.getPath(), cleanPath) && route.getHttpMethod() == HttpMethod.AFTER) {
                afters.add(route);
            }
        });
        this.giveMatch(path, afters);
        return afters;
    }

    /**
     * Sort of path
     *
     * @param uri    request uri
     * @param routes route list
     */
    private void giveMatch(final String uri, List<Route> routes) {
        Collections.sort(routes, (o1, o2) -> {
            if (o2.getPath().equals(uri)) {
                return o2.getPath().indexOf(uri);
            }
            return -1;
        });
    }

    /**
     * Matching path
     *
     * @param routePath   route path
     * @param pathToMatch match path
     * @return return match is success
     */
    private boolean matchesPath(String routePath, String pathToMatch) {
        routePath = routePath.replaceAll(Path.VAR_REGEXP, Path.VAR_REPLACE);
        return pathToMatch.matches("(?i)" + routePath);
    }

    /**
     * Parse Path
     *
     * @param path route path
     * @return return parsed path
     */
    private String parsePath(String path) {
        path = Path.fixPath(path);
        try {
            URI uri = new URI(path);
            return uri.getPath();
        } catch (URISyntaxException e) {
            throw new BladeException(e);
        }
    }

    // a bad way
    void register() {

        List<Route> routeHandlers = new ArrayList<>(routes.values());
        routeHandlers.addAll(interceptors.values());

        for (Route route : routeHandlers) {
            String path = parsePath(route.getPath());
            Matcher matcher = PATH_VARIABLE_PATTERN.matcher(path);
            boolean find = false;
            List<String> uriVariableNames = new ArrayList<>();
            while (matcher.find()) {
                if (!find) {
                    find = true;
                }
                String group = matcher.group(0);
                uriVariableNames.add(group.substring(1));   // {id} -> id
            }
            HttpMethod httpMethod = route.getHttpMethod();
            if (find || (httpMethod == HttpMethod.AFTER || httpMethod == HttpMethod.BEFORE)) {
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
        for (Map.Entry<HttpMethod, StringBuilder> entry : patternBuilders.entrySet()) {
            HttpMethod httpMethod = entry.getKey();
            StringBuilder patternBuilder = entry.getValue();
            if (patternBuilder.length() > 1) {
                patternBuilder.setCharAt(patternBuilder.length() - 1, '$');
            }
            LOGGER.debug("Fast Route Method: {}, regex: {}", httpMethod, patternBuilder);
            regexRoutePatterns.put(httpMethod, Pattern.compile(patternBuilder.toString()));
        }
    }

    private class FastRouteMappingInfo {

        private Route route;

        private List<String> variableNames;

        public FastRouteMappingInfo(Route route, List<String> variableNames) {
            this.route = route;
            this.variableNames = variableNames;
        }

        public Route getRoute() {
            return route;
        }

        public List<String> getVariableNames() {
            return variableNames;
        }
    }

}