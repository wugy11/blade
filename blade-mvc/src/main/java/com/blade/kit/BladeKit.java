package com.blade.kit;

import com.blade.ioc.BeanDefine;
import com.blade.ioc.ClassDefine;
import com.blade.ioc.FieldInjector;
import com.blade.ioc.Ioc;
import com.blade.ioc.annotation.Inject;
import com.blade.ioc.annotation.InjectWith;
import com.blade.mvc.http.HttpMethod;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * @author biezhi 2017/5/31
 */
public class BladeKit {

    private BladeKit() {
    }

//    public static String flowAutoShow(int value) {
//        int kb = 1024;
//        int mb = 1048576;
//        int gb = 1073741824;
//        if (Math.abs(value) > gb) {
//            return Math.round(value / gb) + "GB";
//        } else if (Math.abs(value) > mb) {
//            return Math.round(value / mb) + "MB";
//        } else if (Math.abs(value) > kb) {
//            return Math.round(value / kb) + "KB";
//        }
//        return Math.round(value) + "";
//    }

    /**
     * Get @Inject Annotated field
     *
     * @param ioc         ioc container
     * @param classDefine classDefine
     * @return return FieldInjector
     */
    public static List<FieldInjector> getInjectFields(Ioc ioc, ClassDefine classDefine) {
        List<FieldInjector> injectors = CollectionKit.newArrayList(8);
        for (Field field : classDefine.getDeclaredFields()) {
            if (null != field.getAnnotation(InjectWith.class) || null != field.getAnnotation(Inject.class)) {
                injectors.add(new FieldInjector(ioc, field));
            }
        }
        if (injectors.size() == 0) {
            return Collections.emptyList();
        }
        return injectors;
    }

    public static void injection(Ioc ioc, Class<?> type) {
        BeanDefine beanDefine = ioc.getBeanDefine(type);
        ClassDefine classDefine = ClassDefine.create(type);
        List<FieldInjector> fieldInjectors = getInjectFields(ioc, classDefine);
        Object bean = beanDefine.getBean();
        for (FieldInjector fieldInjector : fieldInjectors) {
            fieldInjector.injection(bean);
        }
    }

    public static void injection(Ioc ioc, BeanDefine beanDefine) {
        ClassDefine classDefine = ClassDefine.create(beanDefine.getType());
        List<FieldInjector> fieldInjectors = getInjectFields(ioc, classDefine);
        Object bean = beanDefine.getBean();
        for (FieldInjector fieldInjector : fieldInjectors) {
            fieldInjector.injection(bean);
        }
    }

    public static boolean isWebHook(HttpMethod httpMethod) {
        return httpMethod == HttpMethod.BEFORE || httpMethod == HttpMethod.AFTER;
    }

    public static boolean notIsWebHook(HttpMethod httpMethod) {
        return !isWebHook(httpMethod);
    }

}
