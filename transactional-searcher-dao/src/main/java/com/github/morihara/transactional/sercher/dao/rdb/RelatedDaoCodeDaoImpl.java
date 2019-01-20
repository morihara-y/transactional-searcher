package com.github.morihara.transactional.sercher.dao.rdb;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.github.morihara.transactional.sercher.dto.RelatedDaoCodeDto;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;
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
                .line(rs.getInt("line"))
                .build();
        return RelatedDaoCodeDto.builder()
                .transactionalMethodId(UUID.fromString(rs.getString("transactional_method_id")))
                .seq(rs.getInt("seq"))
                .relatedDaoCodeVo(relatedDaoCodeVo)
                .build();
    };

    @Override
    public void upsert(UUID transactionalMethodId, List<RelatedDaoCodeDto> relatedDaoCodes) {
        delete(transactionalMethodId);
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

    private void delete(UUID transactionalMethodId) {
        jdbc.update("delete from related_dao_code where transactional_method_id = ?",
                transactionalMethodId);
    }

    private void batchInsert(List<RelatedDaoCodeDto> relatedDaoCodes) {
        final int batchSize = 100;
        final String sql = "insert into related_dao_code ("
                + "transactional_method_id, "
                + "seq, "
                + "package_name, "
                + "class_name, "
                + "method_name, "
                + "method_param, "
                + "method_type, "
                + "line"
                + ") values (?, ?, ?, ?, ?, ?, ?, ?)";
        BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                RelatedDaoCodeDto relatedDaoCodeDto = relatedDaoCodes.get(i); 
                ps.setString(1, relatedDaoCodeDto.getTransactionalMethodId().toString());
            }            
            @Override
            public int getBatchSize() {
                return batchSize;
            }
        };
        jdbc.batchUpdate(sql, bpss);
    }
}
