package com.github.morihara.transactional.sercher.service.enumrate;

import java.lang.reflect.Method;

import org.springframework.jdbc.core.JdbcTemplate;

import com.github.morihara.transactional.sercher.dao.util.MethodsUtil;

import lombok.Getter;

public enum TargetMethodEnum {
    JDBC_UPDATE(MethodsUtil.getDeclaredMethods(JdbcTemplate.class, "update")),
    JDBC_BATCH_UPDATE(MethodsUtil.getDeclaredMethods(JdbcTemplate.class, "batchUpdate"));
    
    @Getter
    private final Method[] methods;

    private TargetMethodEnum(final Method[] methods) {
        this.methods = methods;
    }
}
