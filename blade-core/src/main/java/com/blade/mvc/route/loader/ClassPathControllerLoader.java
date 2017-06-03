package com.blade.mvc.route.loader;

import com.blade.Blade;
import com.blade.exception.BladeException;
import com.blade.ioc.Ioc;
import com.blade.kit.StringKit;

public class ClassPathControllerLoader implements ControllerLoader {

	private String basePackage;

	private ClassLoader classLoader = ClassPathControllerLoader.class.getClassLoader();

	private Ioc ioc = Blade.$().ioc();

	public ClassPathControllerLoader() {
		this("");
	}

	public ClassPathControllerLoader(String basePackage) {
		this.basePackage = basePackage;

		if (StringKit.isNotBlank(basePackage)) {
			if (!this.basePackage.endsWith(".")) {
				this.basePackage += '.';
			}
		}
	}

	@Override
	public Object load(String controllerName) throws BladeException {
		String className = basePackage + controllerName;

		try {
			// Load controller instance
			Class<?> controllerClass = classLoader.loadClass(className);

			Object controller = ioc.getBean(controllerClass);
			if (null == controller) {
				ioc.addBean(controllerClass);
				controller = ioc.getBean(controllerClass);
			}
			return controller;
		} catch (Exception e) {
			throw new BladeException(e);
		}
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

}
