package com.blade.aop;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;

public class Invocaction {
	
	private Object target;
	private Object[] args;
	private MethodProxy proxy;
	
	public Invocaction(Object target, Object[] args, MethodProxy proxy) {
		this.target = target;
		this.args = args;
		this.proxy = proxy;
	}
	
	public Object invoke() throws Throwable{
		try {
			return proxy.invokeSuper(target, args);
		} catch (IllegalAccessException e) {
			throw e;
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (InvocationTargetException e) {
			throw e;
		} catch (Throwable e) {
			throw e;
		}
	}
	
}
