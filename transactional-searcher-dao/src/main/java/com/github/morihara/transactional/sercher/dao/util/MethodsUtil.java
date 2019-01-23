package com.github.morihara.transactional.sercher.dao.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodsUtil {
    private MethodsUtil() {
    }

    public static Method[] getDeclaredMethods(Class<?> c, String methodName) {
        try {
            List<Method> results = new ArrayList<>();
            Method[] methods = c.getDeclaredMethods();
            for (Method method : methods) {
                if (methodName.equals(method.getName())) {
                    results.add(method);
                }
            }
            return results.toArray(new Method[results.size()]);
        } catch (SecurityException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
