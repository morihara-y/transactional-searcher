package com.github.morihara.transactional.searcher.dao.rdb;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.github.morihara.transactional.searcher.dto.RelatedDaoCodeDto;
import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RelatedDaoCodeDaoImpl implements RelatedDaoCodeDao {
    private final JdbcTemplate jdbc;

    private static final RowMapper<RelatedDaoCodeDto> ROW_MAPPER = (rs, i) -> {
        SourceCodeVo relatedDaoCodeVo = SourceCodeVo.builder()
                .packageName(rs.getString("package_name"))
                .className(rs.getString("class_name"))
                .methodName(rs.getString("method_name"))
                .methodParam(rs.getString("method_param"))
                .methodType(rs.getString("method_type"))
                .build();
        return RelatedDaoCodeDto.builder()
                .transactionalMethodId(UUID.fromString(rs.getString("transactional_method_id")))
                .seq(rs.getInt("seq"))
                .relatedDaoCodeVo(relatedDaoCodeVo)
                .build();
    };

    @Override
    public void batchUpsert(List<UUID> transactionalMethodIds, List<RelatedDaoCodeDto> relatedDaoCodes) {
        delete(transactionalMethodIds);
        batchInsert(relatedDaoCodes);
    }

    @Override
    public List<RelatedDaoCodeDto> fetchByRelatedMethodId(UUID transactionalMethodId) {
        String sql = "select * from related_dao_code where transactional_method_id = ? "
                + "order by package_name, class_name, method_name, method_param";
        PreparedStatementSetter pss = new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, transactionalMethodId.toString());
            }
        };
        return jdbc.query(sql, pss, ROW_MAPPER);
    }

    private void delete(List<UUID> transactionalMethodIds) {
        if (CollectionUtils.isEmpty(transactionalMethodIds)) {
            return;
        }
        final String sql = "delete from related_dao_code where transactional_method_id in (:ids)";
        Map<String, List<UUID>> idsParameters = Collections.singletonMap("ids", transactionalMethodIds);
        jdbc.update(sql, idsParameters);
    }

    private void batchInsert(List<RelatedDaoCodeDto> relatedDaoCodes) {
        if (CollectionUtils.isEmpty(relatedDaoCodes)) {
            return;
        }
        final int batchSize = 100;
        final String sql = "insert into related_dao_code ("
                + "transactional_method_id, "
                + "seq, "
                + "package_name, "
                + "class_name, "
                + "method_name, "
                + "method_param, "
                + "method_type"
                + ") values (?, ?, ?, ?, ?, ?, ?)";
        BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                RelatedDaoCodeDto relatedDaoCodeDto = relatedDaoCodes.get(i); 
                int index = 0;
                ps.setString(++index, relatedDaoCodeDto.getTransactionalMethodId().toString());
                ps.setInt(++index, relatedDaoCodeDto.getSeq());
                ps.setString(++index, relatedDaoCodeDto.getRelatedDaoCodeVo().getPackageName());
                ps.setString(++index, relatedDaoCodeDto.getRelatedDaoCodeVo().getClassName());
                ps.setString(++index, relatedDaoCodeDto.getRelatedDaoCodeVo().getMethodName());
                ps.setString(++index, relatedDaoCodeDto.getRelatedDaoCodeVo().getMethodParam());
                ps.setString(++index, relatedDaoCodeDto.getRelatedDaoCodeVo().getMethodType());
            }            
            @Override
            public int getBatchSize() {
                return batchSize;
            }
        };
        jdbc.batchUpdate(sql, bpss);
    }
}
