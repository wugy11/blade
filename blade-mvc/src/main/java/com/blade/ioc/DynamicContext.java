package com.blade.ioc;

import com.blade.ioc.reader.ClassInfo;
import com.blade.ioc.reader.ClassPathClassReader;
import com.blade.ioc.reader.ClassReader;
import com.blade.ioc.reader.JarReaderImpl;
import com.blade.kit.StringKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Enumeration;
import java.util.stream.Stream;

/**
 * Get ClassReader by JAR or folder
 */
public final class DynamicContext {

    private static final Logger log = LoggerFactory.getLogger(DynamicContext.class);

    private static final ClassReader classpathReader = new ClassPathClassReader();
    private static final ClassReader jarReader = new JarReaderImpl();

//    private static boolean isJarContext = false;

    private DynamicContext() {
    }

//    public static void init(Class<?> clazz) {
//        String rs = clazz.getResource("").toString();
//        if (rs.contains(".jar")) {
//            isJarContext = true;
//        }
//    }

    public static Stream<ClassInfo> recursionFindClasses(String packageName) {
        return getClassReader(packageName).getClass(packageName, true).stream();
    }

    private static ClassReader getClassReader(String packageName) {
        if (isJarPackage(packageName)) {
            return jarReader;
        }
        return classpathReader;
    }

    private static boolean isJarPackage(String packageName) {
        if (StringKit.isBlank(packageName)) {
            return false;
        }
        try {
            packageName = packageName.replace(".", "/");
            Enumeration<URL> dirs = DynamicContext.class.getClassLoader().getResources(packageName);
            if (dirs.hasMoreElements()) {
                String url = dirs.nextElement().toString();
                return url.contains(".jar") || url.contains(".zip");
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return false;
    }

}