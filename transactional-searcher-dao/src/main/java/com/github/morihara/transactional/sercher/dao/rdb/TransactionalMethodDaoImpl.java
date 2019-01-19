package com.github.morihara.transactional.sercher.dao.rdb;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.github.morihara.transactional.sercher.dto.TransactionalMethodDto;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TransactionalMethodDaoImpl implements TransactionalMethodDao {
    private final JdbcTemplate jdbc;

    private final RelatedDaoCodeDao relatedDaoCodeDao;
    
    private static final RowMapper<TransactionalMethodDto> ROW_MAPPER = (rs, i) -> {
        SourceCodeVo sourceCodeVo = SourceCodeVo.builder()
                .packageName(rs.getString("package_name"))
                .className(rs.getString("class_name"))
                .methodName(rs.getString("method_name"))
                .methodParam(rs.getString("method_param"))
                .methodType(rs.getString("method_type"))
                .line(rs.getInt("line"))
                .build();
        return TransactionalMethodDto.builder()
                .transactionMethodId(UUID.fromString(rs.getString("transaction_method_id")))
                .sourceCodeVo(sourceCodeVo)
                .isDeveloped(rs.getBoolean("is_developed"))
                .build();
    };
    
    @Override
    public void insert(TransactionalMethodDto transactionalMethodDto) {
        jdbc.update("insert into transactional_method ("
                + "transaction_method_id, "
                + "package_name, "
                + "class_name, "
                + "method_name, "
                + "method_param, "
                + "method_type, "
                + "line, "
                + "is_developed"
                + ") values (?, ?, ?, ?, ?, ?, ?, ?)",
                transactionalMethodDto.getTransactionMethodId(),
                transactionalMethodDto.getSourceCodeVo().getPackageName(),
                transactionalMethodDto.getSourceCodeVo().getClassName(),
                transactionalMethodDto.getSourceCodeVo().getMethodName(),
                transactionalMethodDto.getSourceCodeVo().getMethodParam(),
                transactionalMethodDto.getSourceCodeVo().getMethodType(),
                transactionalMethodDto.getSourceCodeVo().getLine(),
                transactionalMethodDto.isDeveloped());
        relatedDaoCodeDao.upsert(transactionalMethodDto.getTransactionMethodId(),
                transactionalMethodDto.getRelatedDaoCodes());
    }
    
    @Override
    public void updateDevelopStatus(TransactionalMethodDto transactionalMethodDto) {
        jdbc.update("update transactional_method set is_developed = ? where transaction_method_id = ?",
                transactionalMethodDto.isDeveloped(), transactionalMethodDto.getTransactionMethodId());
    }
    
    @Override
    public void delete(UUID transactionalMethodId) {
        jdbc.update("delete from transactional_method where transaction_method_id = ?",
                transactionalMethodId);
    }
    
    @Override
    public List<TransactionalMethodDto> fetchByPackageName(String packageName) {
        String sql = "select * from transactional_method where package_name = ? "
                + "order by package_name, class_name, method_name, method_param";
        PreparedStatementSetter pss = new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, packageName);
            }
        };
        return jdbc.query(sql, pss, ROW_MAPPER);
    }
}
