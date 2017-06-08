package com.blade.mvc.annotation;

import java.lang.annotation.*;

/**
 * Form Multipart ParameterAnnotation
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MultipartParam {

    String value() default "file";

}