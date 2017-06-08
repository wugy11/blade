package com.blade.aop.annotation;

import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Aop{
	
	Class<? extends MethodInterceptor> value();
    
	String methodPrefix() default "";
	
}