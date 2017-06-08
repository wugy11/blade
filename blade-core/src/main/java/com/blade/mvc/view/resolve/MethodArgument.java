package com.blade.mvc.view.resolve;

import com.blade.exception.BladeException;
import com.blade.kit.AsmKit;
import com.blade.kit.Assert;
import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import com.blade.kit.reflect.FieldCallback;
import com.blade.kit.reflect.ReflectKit;
import com.blade.mvc.annotation.*;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.http.wrapper.Session;
import com.blade.mvc.multipart.FileItem;
import com.blade.mvc.view.ModelAndView;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

public final class MethodArgument {

	public static Object[] getArgs(Request request, Response response, Method actionMethod) throws Exception {

		Class<?>[] parameters = actionMethod.getParameterTypes();
		Annotation[][] annotations = actionMethod.getParameterAnnotations();

		Object[] args = new Object[parameters.length];

		// actionMethod.setAccessible(true);
		ReflectKit.forceAccess(actionMethod);

		String[] paramaterNames = AsmKit.getMethodParamNames(actionMethod);

		for (int i = 0, len = parameters.length; i < len; i++) {

			Class<?> argType = parameters[i];
			if (argType == Request.class) {
				args[i] = request;
				continue;
			}

			if (argType == Response.class) {
				args[i] = response;
				continue;
			}

			if (argType == Session.class) {
				args[i] = request.session();
				continue;
			}

			if (argType == ModelAndView.class) {
				args[i] = new ModelAndView();
				continue;
			}

			if (argType == Map.class) {
				args[i] = request.querys();
				continue;
			}

			Annotation annotation = annotations[i][0];
			if (null != annotation) {
				// query param
				Class<? extends Annotation> annotationType = annotation.annotationType();
				if (EntityObj.class.equals(annotationType)) {
					Map<String, String> reqMap = request.querys();
					args[i] = mapToObj(reqMap, argType);
					continue;
				}
				if (annotationType == QueryParam.class) {
					QueryParam queryParam = (QueryParam) annotation;
					String paramName = queryParam.value();
					String val = request.query(paramName);
					boolean required = queryParam.required();

					if (StringKit.isBlank(paramName)) {
						assert paramaterNames != null;
						paramName = paramaterNames[i];
						val = request.query(paramName);
					}
					if (StringKit.isBlank(val)) {
						val = queryParam.defaultValue();
					}

					if (required && StringKit.isBlank(val)) {
						throw new BladeException("query param [" + paramName + "] not is empty.");
					}

					args[i] = getRequestParam(argType, val);
					continue;
				}

				// path param
				if (annotationType == PathParam.class) {
					PathParam pathParam = (PathParam) annotation;
					String paramName = pathParam.value();
					String val = request.pathString(paramName);

					if (StringKit.isBlank(paramName)) {
						assert paramaterNames != null;
						paramName = paramaterNames[i];
						val = request.pathString(paramName);
					}
					if (StringKit.isBlank(val)) {
						val = pathParam.defaultValue();
					}
					args[i] = getRequestParam(argType, val);
				}

				// header param
				if (annotationType == HeaderParam.class) {
					HeaderParam headerParam = (HeaderParam) annotation;
					String paramName = headerParam.value();
					String val = request.header(paramName);
					boolean required = headerParam.required();

					if (StringKit.isBlank(paramName)) {
						assert paramaterNames != null;
						paramName = paramaterNames[i];
						val = request.header(paramName);
					}
					if (StringKit.isBlank(val)) {
						val = headerParam.defaultValue();
					}

					if (required && StringKit.isBlank(val)) {
						throw new BladeException("header param [" + paramName + "] not is empty.");
					}

					args[i] = getRequestParam(argType, val);
					continue;
				}

				// cookie param
				if (annotationType == CookieParam.class) {
					CookieParam cookieParam = (CookieParam) annotation;
					String paramName = cookieParam.value();
					String val = request.cookie(paramName);
					boolean required = cookieParam.required();

					if (StringKit.isBlank(paramName)) {
						assert paramaterNames != null;
						paramName = paramaterNames[i];
						val = request.cookie(paramName);
					}
					if (StringKit.isBlank(val)) {
						val = cookieParam.defaultValue();
					}

					if (required && StringKit.isBlank(val)) {
						throw new BladeException("cookie param [" + paramName + "] not is empty.");
					}
					args[i] = getRequestParam(argType, val);
					continue;
				}

				// form multipart
				if (annotationType == MultipartParam.class && argType == FileItem.class) {

					MultipartParam multipartParam = (MultipartParam) annotation;
					String paramName = multipartParam.value();
					FileItem val = request.fileItem(paramName);

					if (StringKit.isBlank(paramName)) {
						assert paramaterNames != null;
						paramName = paramaterNames[i];
						val = request.fileItem(paramName);
					}
					args[i] = val;
					continue;
				}
			}
		}
		return args;
	}

	public static Object getRequestParam(Class<?> parameterType, String val) {
		Object result = null;
		if (parameterType.equals(String.class)) {
			return val;
		}
		if (StringKit.isBlank(val)) {
			if (parameterType.equals(int.class) || parameterType.equals(double.class)
					|| parameterType.equals(long.class) || parameterType.equals(byte.class)
					|| parameterType.equals(float.class)) {
				result = 0;
			} else if (parameterType.equals(boolean.class)) {
				result = false;
			}
		} else {
			if (parameterType.equals(Integer.class) || parameterType.equals(int.class)) {
				result = Integer.parseInt(val);
			} else if (parameterType.equals(Long.class) || parameterType.equals(long.class)) {
				result = Long.parseLong(val);
			} else if (parameterType.equals(Double.class) || parameterType.equals(double.class)) {
				result = Double.parseDouble(val);
			} else if (parameterType.equals(Float.class) || parameterType.equals(float.class)) {
				result = Float.parseFloat(val);
			} else if (parameterType.equals(Boolean.class) || parameterType.equals(boolean.class)) {
				result = Boolean.parseBoolean(val);
			} else if (parameterType.equals(Byte.class) || parameterType.equals(byte.class)) {
				result = Byte.parseByte(val);
			}
		}
		return result;
	}

	public static <T> T mapToObj(Map<String, String> map, Class<T> clazz) throws Exception {
		T obj = clazz.newInstance();
		FieldCallback callBack = new FieldCallback() {

			@Override
			public void callBack(Field field) throws Exception {
				String name = field.getName();
				for (String key : map.keySet()) {
					if (!key.equalsIgnoreCase(name))
						continue;
					String value = map.get(key);
					if (StringKit.isEmpty(value))
						continue;
					Class<?> propertyType = field.getType();
					if (Date.class.equals(propertyType)) {
						DateFormat dateFormat = field.getAnnotation(DateFormat.class);
						Assert.notNull(dateFormat, "请求参数为日期类型，需指定DateFormat注解");
						String pattern = dateFormat.pattern();
						field.set(obj, DateKit.dateFormat(value, pattern));
					} else {
						field.set(obj, getRequestParam(propertyType, value));
					}
					break;
				}
			}
		};
		callBack.callBackField(clazz);
		return obj;
	}

	public static <T> T mapToObj2(Map<String, String> map, Class<T> clazz) throws Exception {
		T obj = clazz.newInstance();
		BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor descriptor : propertyDescriptors) {
			String name = descriptor.getName();
			for (String key : map.keySet()) {
				if (!key.equalsIgnoreCase(name))
					continue;
				String value = map.get(key);
				if (StringKit.isEmpty(value))
					continue;
				Class<?> propertyType = descriptor.getPropertyType();
				Method writeMethod = descriptor.getWriteMethod();
				if (Date.class.equals(propertyType)) {
					DateFormat dateFormat = writeMethod.getAnnotation(DateFormat.class);
					Assert.notNull(dateFormat, "请求参数为日期类型，需指定DateFormat注解");
					String pattern = dateFormat.pattern();
					writeMethod.invoke(obj, DateKit.dateFormat(value, pattern));
				} else {
					writeMethod.invoke(obj, getRequestParam(propertyType, value));
				}
				break;
			}
		}
		return obj;
	}

}
