package com.github.morihara.transactional.searcher.dao.spoon;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.github.morihara.transactional.searcher.dao.spoon.SourceCodeFetchDao;
import com.github.morihara.transactional.searcher.dao.spoon.SourceCodeFetchDaoImpl;
import com.github.morihara.transactional.searcher.dao.util.MethodsUtil;
import com.github.morihara.transactional.searcher.dto.vo.BeanDefinitionVo;
import com.github.morihara.transactional.searcher.dto.vo.MetadataResourceVo;
import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;

@RunWith(JUnit4.class)
@Ignore
public class SourceCodeFetchDaoImplTest {
    private static final Map<String, MetadataResourceVo> METADATA_RESOURCE_MAP = new HashMap<>();
    private static final Method[] JDBC_BATCH_UPDATE = MethodsUtil.getDeclaredMethods(JdbcTemplate.class,
            "batchUpdate");
    private static List<String> PACKAGE_PREFIX_LIST = Arrays.asList("com.github.morihara");

    @Before
    public void makeMetadataMap() {
        SourceCodeFetchDao dao = new SourceCodeFetchDaoImpl();
        dao.walkJarFile("src/test/resournes/spoon-test.jar", "spoon-test", METADATA_RESOURCE_MAP);
    }

    @Test
    public void walkJarFile() {
        assertThat(METADATA_RESOURCE_MAP.size(), is(9));
    }

    @Test
    public void updateBeanDefinitionMap() {
        SourceCodeFetchDao dao = new SourceCodeFetchDaoImpl();
        Map<String, List<BeanDefinitionVo>> result = new HashMap<>();
        Class<?>[] configClass = {Configuration.class};
        dao.makeBeanDefinitionMap(configClass, result, METADATA_RESOURCE_MAP);
        assertThat(result.size(), is(3));
    }

    @Test
    public void fetchPublicMethodsByAnotation() {
        SourceCodeFetchDao dao = new SourceCodeFetchDaoImpl();
        List<SourceCodeVo> result = dao.fetchImplementedMethodsByClassAnotation(Repository.class, METADATA_RESOURCE_MAP);
        assertThat(result.size(), is(3));
    }

    @Test
    public void fetchCalledMethodsByMethod_private() {
        SourceCodeFetchDao dao = new SourceCodeFetchDaoImpl();
        List<SourceCodeVo> result = dao.fetchCalledMethodsByMethod(makeRelatedDaoCodeDaoSourceCode(),
                PACKAGE_PREFIX_LIST, METADATA_RESOURCE_MAP);
        assertThat(result.size(), is(2));
    }

    @Test
    public void fetchCalledMethodsByMethod_includeImpl() {
        SourceCodeFetchDao dao = new SourceCodeFetchDaoImpl();
        List<SourceCodeVo> result = dao.fetchCalledMethodsByMethod(makeTransactionalMethodDaoSourceCode(),
                PACKAGE_PREFIX_LIST, METADATA_RESOURCE_MAP);
        assertThat(result.size(), is(0));
    }

    @Test
    public void hasMethod() {
        SourceCodeFetchDao dao = new SourceCodeFetchDaoImpl();
        int result = dao.hasMethod(makeTransactionalMethodDaoSourceCode(), JDBC_BATCH_UPDATE, METADATA_RESOURCE_MAP);
        assertThat(result, is(1));
    }

    @Test
    public void hasAnnotation_true() {
        SourceCodeFetchDao dao = new SourceCodeFetchDaoImpl();
        boolean result = dao.hasAnnotation(makeTransactionalMethodDaoSourceCode(), Override.class,
                METADATA_RESOURCE_MAP);
        assertTrue(result);
    }

    @Test
    public void hasAnnotation_false() {
        SourceCodeFetchDao dao = new SourceCodeFetchDaoImpl();
        boolean result = dao.hasAnnotation(makeTransactionalMethodDaoSourceCode(), Transactional.class,
                METADATA_RESOURCE_MAP);
        assertFalse(result);
    }

    private SourceCodeVo makeTransactionalMethodDaoSourceCode() {
        return SourceCodeVo.builder()
                .packageName("com.github.morihara.transactional.searcher.dao.rdb")
                .className("TransactionalMethodDaoImpl")
                .methodName("batchInsert")
                .methodParam("java.util.List")
                .methodType("void")
                .build();
    }

    private SourceCodeVo makeRelatedDaoCodeDaoSourceCode() {
        return SourceCodeVo.builder()
                .packageName("com.github.morihara.transactional.searcher.dao.rdb")
                .className("RelatedDaoCodeDaoImpl")
                .methodName("batchUpsert")
                .methodParam("java.util.List, java.util.List")
                .methodType("void")
                .build();
    }
}
