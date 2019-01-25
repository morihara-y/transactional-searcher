package com.github.morihara.transactional.sercher.dao.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
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

    public static CtMethod<?> fetchTargetMethod(CtClass<CtElement> element, SourceCodeVo sourceCodeVo,
            Factory factory) {
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

    public static SourceCodeVo makeSourceCodeVo(String packageName, String className, CtMethod<?> method) {
        return SourceCodeVo.builder()
                .packageName(packageName)
                .className(className)
                .methodName(method.getSimpleName())
                .methodParam(makeParamStr(method))
                .methodType(method.getType().getQualifiedName())
                .build();
    }

    public static SourceCodeVo makeSourceCodeVo(Method method) {
        return SourceCodeVo.builder()
                .packageName(method.getDeclaringClass().getPackage().getName())
                .className(method.getDeclaringClass().getSimpleName())
                .methodName(method.getName())
                .methodParam(makeParamStr(method))
                .methodType(method.getReturnType().getName())
                .build();
    }

    private static String makeParamStr(CtMethod<?> method) {
        List<CtParameter<?>> ctParams = method.getParameters();
        if (CollectionUtils.isEmpty(ctParams)) {
            return StringUtils.EMPTY;
        }
        List<String> params = ctParams.stream()
                .map(ctParam -> ctParam.getType().getQualifiedName())
                .collect(Collectors.toList());
        return makeParamStr(params);
    }

    private static String makeParamStr(Method method) {
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length <= 0) {
            return StringUtils.EMPTY;
        }
        List<String> params = new ArrayList<>();
        for (int i = 0; i < paramTypes.length; i++) {
            params.add(paramTypes[i].getName());
        }
        return makeParamStr(params);
    }

    private static String makeParamStr(List<String> params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i));
            if (i < params.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
