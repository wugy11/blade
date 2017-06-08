package com.blade.mvc.annotation;

import java.lang.annotation.*;

/**
 * Request Query ParameterAnnotation
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyParam {

    boolean required() default false;

}