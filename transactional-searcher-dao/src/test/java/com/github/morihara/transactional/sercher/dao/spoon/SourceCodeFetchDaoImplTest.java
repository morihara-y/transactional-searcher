package com.github.morihara.transactional.sercher.dao.spoon;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.github.morihara.transactional.sercher.dao.util.MethodsUtil;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;

@RunWith(JUnit4.class)
public class SourceCodeFetchDaoImplTest {
    private static final String SOURCE_FOLDER_PATH = "src/main/java";
    private static final Method[] JDBC_BATCH_UPDATE = MethodsUtil.getDeclaredMethods(JdbcTemplate.class, "batchUpdate");

    @Test
    public void fetchPackagesBySourceFolderPath() {
        SourceCodeFetchDao dao = new SourceCodeFetchDaoImpl();
        List<String> result = dao.fetchPackagesBySourceFolderPath(SOURCE_FOLDER_PATH);
        assertThat(result.size(), is(3));
    }

    @Test
    public void fetchMethodsByPackageName() {
        SourceCodeFetchDao dao = new SourceCodeFetchDaoImpl();
        List<SourceCodeVo> result = dao.fetchMethodsByPackageName(SOURCE_FOLDER_PATH,
                "com.github.morihara.transactional.sercher.dao.rdb");
        assertThat(result.size(), is(6));
    }

    @Test
    public void hasMethod_inTopLayer() {
        SourceCodeFetchDao dao = new SourceCodeFetchDaoImpl();
        int result = dao.hasMethod(SOURCE_FOLDER_PATH, makeTransactionalMethodDaoSourceCode(), JDBC_BATCH_UPDATE);
        assertThat(result, is(1));
    }

    @Test
    public void hasAnnotation_true() {
        SourceCodeFetchDao dao = new SourceCodeFetchDaoImpl();
        boolean result = dao.hasAnnotation(SOURCE_FOLDER_PATH, makeTransactionalMethodDaoSourceCode(), Override.class);
        assertTrue(result);
    }

    @Test
    public void hasAnnotation_false() {
        SourceCodeFetchDao dao = new SourceCodeFetchDaoImpl();
        boolean result = dao.hasAnnotation(SOURCE_FOLDER_PATH, makeTransactionalMethodDaoSourceCode(), Transactional.class);
        assertFalse(result);
    }

    private SourceCodeVo makeTransactionalMethodDaoSourceCode() {
        return SourceCodeVo.builder()
                .packageName("com.github.morihara.transactional.sercher.dao.rdb")
                .className("TransactionalMethodDaoImpl")
                .methodName("batchInsert")
                .methodParam("java.util.List")
                .methodType("void")
                .build();
    }
}
