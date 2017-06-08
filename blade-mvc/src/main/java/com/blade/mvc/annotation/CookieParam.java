package com.blade.mvc.annotation;

import java.lang.annotation.*;

/**
 * Request Cookie ParameterAnnotation
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CookieParam {

    boolean required() default false;

    String value() default "";

    String defaultValue() default "";

}