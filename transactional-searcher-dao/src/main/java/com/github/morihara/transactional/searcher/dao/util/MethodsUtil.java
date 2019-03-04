package com.github.morihara.transactional.searcher.dao.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;

import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AbstractFilter;

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

    public static CtMethod<?> fetchTargetMethod(CtClass<CtElement> element, SourceCodeVo sourceCodeVo) {
        String methodParamStr = sourceCodeVo.getMethodParam();
        if (StringUtils.isEmpty(methodParamStr)) {
            return element.getMethod(sourceCodeVo.getMethodName());
        }

        List<CtMethod<?>> methods = element.getMethodsByName(sourceCodeVo.getMethodName());
        if (methods.size() == 1) {
            return methods.get(1);
        }

        String[] paramTypes = sourceCodeVo.getMethodParam().split(", ");
        for (CtMethod<?> method : methods) {
            if (equalParams(method, paramTypes)) {
                return method;
            }
        }
        throw new IllegalArgumentException(
                "There is no method. Method:" + sourceCodeVo.toUniqueMethodStr());
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

    public static SourceCodeVo makeSourceCodeVo(CtExecutableReference<?> executableMethod) {
        CtTypeReference<?> executableMethodTypeRef = executableMethod.getDeclaringType();
        return SourceCodeVo.builder()
                .packageName(executableMethodTypeRef.getPackage().getQualifiedName())
                .className(executableMethodTypeRef.getSimpleName())
                .methodName(executableMethod.getSimpleName())
                .methodParam(makeParamStr(executableMethod))
                .methodType(executableMethod.getType().getQualifiedName())
                .isInterface(executableMethodTypeRef.isInterface())
                .build();
    }
    
    @SuppressWarnings("rawtypes")
    public static List<CtExecutableReference<?>> fetchChildExecutableMethods(CtMethod<?> method) {
        List<CtElement> elements = method.getElements(new AbstractFilter<CtElement>(CtElement.class) {
            @Override
            public boolean matches(CtElement element) {
                return element instanceof CtAbstractInvocation;
            }
        });
        return elements.stream()
                .map(element -> {
                    CtAbstractInvocation invocation = (CtAbstractInvocation)element;
                    return invocation.getExecutable();
                })
                .collect(Collectors.toList());
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

    private static String makeParamStr(CtExecutableReference<?> executableMethod) {
        List<CtTypeReference<?>> ctParams = executableMethod.getParameters();
        if (CollectionUtils.isEmpty(ctParams)) {
            return StringUtils.EMPTY;
        }
        List<String> params = ctParams.stream()
                .map(ctParam -> ctParam.getQualifiedName())
                .collect(Collectors.toList());
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

    private static boolean equalParams(CtMethod<?> method, String[] paramTypes) {
        List<CtParameter<?>> params = method.getParameters();
        int paramCnt = params.size();
        if (paramCnt != paramTypes.length) {
            return false;
        }

        int match = 0; 
        for (CtParameter<?> param : params) {
            for (String paramType : paramTypes) {
                if (param.getType().getQualifiedName().equals(paramType)) {
                    match++;
                }
            }
        }
        return match == paramTypes.length;
    }
}
