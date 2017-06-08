package com.blade.mvc.annotation;

import java.lang.annotation.*;

/**
 * Route class notes, identifying whether a class is routed
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Path {

    /**
     * @return namespace
     */
    String value() default "/";

    /**
     * @return route suffix
     */
    String suffix() default "";

    boolean restful() default false;

}