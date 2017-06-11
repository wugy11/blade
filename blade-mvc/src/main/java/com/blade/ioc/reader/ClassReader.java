
package com.blade.ioc.reader;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 一个类读取器的接口
 */
public interface ClassReader {

    Set<Class<?>> getClass(String packageName);

    Set<Class<?>> getClass(String packageName, Class<?> parent);

    Set<Class<?>> getClassByAnnotation(Class<? extends Annotation> annotation);

    Set<Class<?>> getClassByAnnotation(String packageName, Class<? extends Annotation> annotation);

}