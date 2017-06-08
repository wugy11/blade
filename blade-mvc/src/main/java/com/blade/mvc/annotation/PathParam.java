package com.blade.mvc.annotation;

import java.lang.annotation.*;

/**
 * Request Path Parameter Annotation
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathParam {

    String name() default "";

    String defaultValue() default "";

}