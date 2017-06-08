package com.blade.mvc.route.loader;

import com.blade.exception.BladeException;
import com.blade.mvc.route.Route;

import java.text.ParseException;
import java.util.List;

public interface RouteLoader {

	List<Route> load() throws ParseException, BladeException;

}