package com.blade.mvc.annotation;

import java.lang.annotation.*;

/**
 * Request Header ParameterAnnotation
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HeaderParam {

    boolean required() default false;

    String value() default "";

    String defaultValue() default "";

}