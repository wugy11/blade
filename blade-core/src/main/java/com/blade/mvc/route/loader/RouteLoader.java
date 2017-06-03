package com.blade.mvc.route.loader;

import java.text.ParseException;
import java.util.List;

import com.blade.exception.BladeException;
import com.blade.mvc.route.Route;

public interface RouteLoader {

	List<Route> load() throws ParseException, BladeException;

}