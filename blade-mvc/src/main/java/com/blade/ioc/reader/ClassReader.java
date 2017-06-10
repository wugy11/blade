
package com.blade.ioc.reader;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 一个类读取器的接口
 */
public interface ClassReader {

    Set<ClassInfo> getClass(String packageName, boolean recursive);

    Set<ClassInfo> getClass(String packageName, Class<?> parent, boolean recursive);

    Set<ClassInfo> getClassByAnnotation(String packageName, Class<? extends Annotation> annotation, boolean recursive);

    Set<ClassInfo> getClassByAnnotation(String packageName, Class<?> parent, Class<? extends Annotation> annotation,
                                        boolean recursive);

}