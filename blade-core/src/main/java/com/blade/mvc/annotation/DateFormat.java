package com.blade.mvc.annotation;

import java.lang.annotation.*;

@Target({ ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DateFormat {

	String pattern() default "yyyy-MM-dd HH:mm:ss";
}
