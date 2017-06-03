package com.blade.mvc.route.loader;

import com.blade.exception.BladeException;

public interface ControllerLoader {

	Object load(String controllerName) throws BladeException;

}
