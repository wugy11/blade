package com.blade.ioc.reader;

import com.blade.kit.CollectionKit;
import com.blade.kit.StringKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;

/**
 * 抽象类读取器
 */
public abstract class ClassReaderTemplate {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    public final Set<Class<?>> getClass(String packageName) {
        Set<Class<?>> classSet = CollectionKit.newHashSet();
        if (StringKit.isBlank(packageName))
            packageName = "com.blade";
        String packageDirName = packageName.replaceAll(".", "/");
        try {
            Enumeration<URL> resources = this.getClass().getClassLoader().getResources(packageName);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                if (null == url)
                    continue;
                String protocol = url.getProtocol();
                if ("file".equalsIgnoreCase(protocol)) {
                    String packagePath = url.getPath().replace("20%", " ");
                    addClass(classSet, packagePath, packageDirName);
                    continue;
                }
                if ("jar".equalsIgnoreCase(protocol)) {
                    JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                    Enumeration<JarEntry> entries = jarURLConnection.getJarFile().entries();
                    while (entries.hasMoreElements()) {
                        String name = entries.nextElement().getName();
                        if (!name.startsWith(packageDirName) && !name.endsWith(".class"))
                            continue;
                        String className = StringKit.subAtLast(name, ".class")
                                .replaceAll("/", ".");
                        // String className = name.substring(packageName.length() + 1, name.length() - 6);
                        doAddClass(classSet, /*packageName + '.' + */className);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("读取类文件失败：" + e);
        }
        return classSet;
    }

    private void addClass(Set<Class<?>> classSet, String packagePath, String packageName) {
        File[] files = new File(packagePath).listFiles(file -> file.isFile() &&
                file.getName().endsWith(".class") || file.isDirectory());
        if (CollectionKit.isEmpty(files))
            return;
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()) { // 文件
                String className = StringKit.subAtFirst(fileName, ".class");
                if (StringKit.isNotBlank(packageName))
                    className = packageName + "." + className;
                doAddClass(classSet, className);
                continue;
            }
            // 目录
            String subPackagePath = fileName;
            if (StringKit.isNotBlank(packagePath))
                subPackagePath = packagePath + "/" + subPackagePath;
            String subPackageName = fileName;
            if (StringKit.isNotBlank(packageName))
                subPackageName = packageName + "." + subPackageName;

            // 递归子目录
            addClass(classSet, subPackagePath, subPackageName);
        }
    }

    private void doAddClass(Set<Class<?>> classSet, String className) {
        try {
            Class<?> clazz = Class.forName(className);
            if (checkAddClass(clazz))
                classSet.add(clazz);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("加载类文件失败：" + e);
        }
    }

    public abstract boolean checkAddClass(Class<?> clazz);

}