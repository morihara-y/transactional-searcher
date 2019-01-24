package com.github.morihara.transactional.sercher.dao.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

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

    public static CtMethod<?> fetchTargetMethod(CtClass<CtElement> element, SourceCodeVo sourceCodeVo, Factory factory) {
        String methodParamStr = sourceCodeVo.getMethodParam();
        if (StringUtils.isEmpty(methodParamStr)) {
            return element.getMethod(sourceCodeVo.getMethodName());
        }
        String[] methodTypeStrs = sourceCodeVo.getMethodParam().split(", ");
        int typeCnt = methodTypeStrs.length;
        CtTypeReference<?>[] methodTypes = new CtTypeReference<?>[typeCnt];
        for (int i = 0; i < typeCnt; i++) {
            methodTypes[i] = factory.Type().createReference(methodTypeStrs[i]);
        }
        return element.getMethod(sourceCodeVo.getMethodName(), methodTypes);
    }
}
