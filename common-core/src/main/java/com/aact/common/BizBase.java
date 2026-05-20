package com.aact.common;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import oracle.jdbc.internal.OracleTypes;

import javax.sql.DataSource;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class BizBase {
    private final Map<SourcName,DataSource> multiDataSource;
    private final String mainSelect;
    private Connection conn = null;
    private boolean transFlag = false;

    static DbTypeDTO readCol(ResultSet rs, ResultSetMetaData md, int i) throws SQLException {
        int jdbc = md.getColumnType(i);

        switch (jdbc) {
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.NVARCHAR:
            case Types.NCHAR:
                return DbTypeDTO.builder().obj(rs.getObject(i)).type(DbTypeDTO.Type.VARCHAR).build();
            case Types.BLOB:
                Blob b = rs.getBlob(i);
                if(b== null){
                    return DbTypeDTO.builder().obj(null).type(DbTypeDTO.Type.BLOB).build();
                }else{
                    byte[] bytes = b.getBytes(1, (int) b.length());
                    return DbTypeDTO.builder().obj(bytes).type(DbTypeDTO.Type.BLOB).build();
                }
            case Types.CLOB:
            case Types.NCLOB: {
                Clob c = rs.getClob(i);
                if (c == null)
                    return DbTypeDTO.builder().obj("").type(DbTypeDTO.Type.CLOB).build();
                try (Reader r = c.getCharacterStream(); StringWriter w = new StringWriter()) {
                    r.transferTo(w);
                    return DbTypeDTO.builder().obj(w.toString()).type(DbTypeDTO.Type.CLOB).build();
                } catch (Exception e) {
                    // TODO: handle exception
                    throw new SQLException(e);
                }
            }

            case Types.NUMERIC:
            case Types.DECIMAL:
                return DbTypeDTO.builder().obj(rs.getBigDecimal(i)).type(DbTypeDTO.Type.DECIMAL).build();

            case Types.TIMESTAMP:
            case Types.DATE:
                // JDBC 4.2 (ojdbc8) 이상이면 java.time 권장
                return DbTypeDTO.builder().obj(rs.getObject(i, java.time.LocalDateTime.class)).type(DbTypeDTO.Type.DATE).build(); // rs.getObject(i,
            // java.time.LocalDate.class);

            default:
                // 모르는 타입은 드라이버 기본 매핑 그대로 유지
                return DbTypeDTO.builder().obj(rs.getObject(i)).type(DbTypeDTO.Type.NULL).build();
        }
    }

    public void close() {
        // ✅ 여기 후처리 (세션 close, 로그, 정리 등)
        try {

            if (conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
                transFlag = false;
            }

        } catch (Exception ex) {

        }
    }

    public void connect(){
        try{
            SourcName def = SourcName.valueOf(mainSelect.toUpperCase());
            DataSource db = multiDataSource.get(def);
            if(db==null){
                throw new Exception("DataSource IS NULL");
            }

            if(conn == null){
                conn = db.getConnection();
            }else if(conn.isClosed()){
                conn = null;
                conn = db.getConnection();
            }else{
                conn.close();
                conn = null;
                conn = db.getConnection();
            }
            if (transFlag) {
                if (conn != null && !conn.isClosed()) {
                    conn.setAutoCommit(false);
                }
            } else {
                conn.setAutoCommit(true);
            }
        }catch (Exception ex){
            ResponseDTO.setError2("connect",ex);
            transFlag = false;
            conn = null;
        }
    }

    public void connect(SourcName source){
        try{
            DataSource db = multiDataSource.get(source);
            if(db==null){
                throw new Exception("DataSource IS NULL");
            }

            if(conn == null){
                conn = db.getConnection();
            }else if(conn.isClosed()){
                conn = null;
                conn = db.getConnection();
            }else{
                conn.close();
                conn = null;
                conn = db.getConnection();
            }
            if (transFlag) {
                if (conn != null && !conn.isClosed()) {
                    conn.setAutoCommit(false);
                }
            } else {
                conn.setAutoCommit(true);
            }
        }catch (Exception ex){
            ResponseDTO.setError2("connect",ex);
            transFlag = false;
            conn = null;
        }
    }

    public void beginTrans() {
        transFlag = true;
        connect();
    }

    public void beginTrans(SourcName source) {
        transFlag = true;
        connect(source);
    }

    public void commit() {
        try {
            if (transFlag && conn != null && !conn.isClosed()) {
                conn.commit();
                conn.close();
            }
        } catch (Exception ex) {

        } finally {
            transFlag = false;
            conn = null;
        }

    }

    public void rollback() {
        try {
            if (transFlag && conn != null && !conn.isClosed()) {
                conn.rollback();
                conn.close();
            }
        } catch (Exception ex) {

        } finally {
            transFlag = false;
            conn = null;
        }
    }

    public DbDto callProc(String procName, List<DbTypeDTO> inputParams) {
        DbDto ret = new DbDto();

        try {
            // sql 구문작성 파라미터는 물음표로
            StringBuilder sql = new StringBuilder();
            sql.append("{call ");
            sql.append(procName);
            sql.append("(");
            if (inputParams != null && inputParams.size() > 0) {
                for (int i = 0; i < inputParams.size(); i++) {
                    if (i == 0) {
                        sql.append("?");
                    } else {
                        sql.append(", ?");
                    }
                }
            }
            sql.append(")}");

            int curCount = 0;

            // sql 처리부분
            try (CallableStatement cs = conn.prepareCall(sql.toString())) {
                for (int i = 0; i < inputParams.size(); i++) {

                    switch (inputParams.get(i).getInout()) {

                        case OUT:
                            switch (inputParams.get(i).getType()) {
                                case VARCHAR:
                                    cs.registerOutParameter(i + 1, OracleTypes.VARCHAR);
                                    break;
                                case CURSOR:
                                    ret.setResult(new LinkedHashMap<Integer, List<Map<String, DbTypeDTO>>>());
                                    cs.registerOutParameter(i + 1, OracleTypes.CURSOR);
                                    break;
                            }
                            break;
                        default:
                            switch (inputParams.get(i).getType()) {
                                case VARCHAR:
                                    if (inputParams.get(i).getObj() == null) {
                                        cs.setNull(i + 1, java.sql.Types.VARCHAR);
                                    } else {
                                        cs.setString(i + 1, (String) inputParams.get(i).getObj());
                                    }
                                    break;
                                case CLOB:
                                    if (inputParams.get(i).getObj() == null) {
                                        cs.setNull(i + 1, java.sql.Types.CLOB);
                                    } else {
                                        cs.setCharacterStream(i + 1, new StringReader((String) inputParams.get(i).getObj()));
                                    }
                                    break;
                                case BLOB:
                                    if (inputParams.get(i).getObj() == null) {
                                        cs.setNull(i + 1, java.sql.Types.BLOB);
                                    } else {
                                        cs.setBytes(i + 1, (byte[]) inputParams.get(i).getObj());
                                    }
                                    break;
                                case DECIMAL:
                                    if (inputParams.get(i).getObj() == null) {
                                        cs.setNull(i + 1, java.sql.Types.NUMERIC);
                                    } else {
                                        cs.setBigDecimal(i + 1, (BigDecimal) inputParams.get(i).getObj());
                                    }

                                    break;
                                case DATE:
                                    if (inputParams.get(i).getObj() == null) {
                                        cs.setNull(i + 1, java.sql.Types.DATE);
                                    } else {
                                        cs.setDate(i + 1, java.sql.Date.valueOf((String) inputParams.get(i).getObj()));
                                    }

                                    break;
                            }
                            break;
                    }
                }

                // sql 실행
                cs.execute();

                for (int i = 0; i < inputParams.size(); i++) {

                    switch (inputParams.get(i).getInout()) {

                        case OUT:
                            switch (inputParams.get(i).getType()) {
                                case VARCHAR:
                                    switch (inputParams.get(i).getParamName()) {
                                        case "O_ERROR_FLAG":
                                            ret.setErrFlag((String) cs.getObject(i + 1));
                                            break;
                                        case "O_RETURN_CODE":
                                            ret.setErrCode((String) cs.getObject(i + 1));
                                            break;
                                        case "O_RETURN_MESSAGE":
                                            ret.setErrMsg((String) cs.getObject(i + 1));
                                            break;
                                        default:
                                            if (ret.getRetObj() == null) {
                                                ret.setRetObj(new LinkedHashMap<String, Object>());
                                            }
                                            ret.getRetObj().put(inputParams.get(i).getParamName(), cs.getObject(i + 1));
                                            break;
                                    }
                                    break;
                                case CURSOR:
                                    // sql 결과

                                    List<Map<String, DbTypeDTO>> tmp = new ArrayList<Map<String, DbTypeDTO>>();

                                    if (cs.getObject(i + 1) != null) {
                                        try (ResultSet rs = (ResultSet) cs.getObject(i + 1)) {

                                            ResultSetMetaData rsmd = rs.getMetaData();
                                            int columnCount = rsmd.getColumnCount();

                                            while (rs.next()) {
                                                Map<String, DbTypeDTO> row = new LinkedHashMap<>();
                                                for (int j = 1; j <= columnCount; j++) {
                                                    row.put(rsmd.getColumnLabel(j), readCol(rs, rsmd, j));
                                                }
                                                tmp.add(row);
                                            }
                                        }
                                    }

                                    ret.getResult().put(curCount, tmp);
                                    curCount++;

                                    break;
                            }
                            break;
                        case INOUT:
                            switch (inputParams.get(i).getType()) {
                                case DECIMAL:
                                    if (ret.getRetObj() == null) {
                                        ret.setRetObj(new LinkedHashMap<String, Object>());
                                    }
                                    ret.getRetObj().put(inputParams.get(i).getParamName(), cs.getBigDecimal(i + 1));
                                    break;
                            }
                            break;
                    }
                }

            } catch (Exception e) {
                // TODO: handle exception
                throw new Exception(e);
            }

        } catch (Exception e) {
            // TODO: handle exception
            ret = new DbDto();
            ret.setErrFlag("Y");
            ret.setErrCode("SERVER");
            ret.setErrMsg(e.getMessage());

        } finally {
            if (!transFlag) {
                close();
            }
        }
        return ret;
    }

    public DbDto callSql(String sql) {
        try{
            SourcName def = SourcName.valueOf(mainSelect.toUpperCase());
            return callSql(def, sql);
        } catch (Exception e) {
            return DbDto.builder().errFlag("Y").errCode("SERVER").errMsg(e.getMessage()).build();
        }

    }

    public DbDto callSql(SourcName type, String sql) {
        DbDto ret = null;

        try {
            try (Statement cs = conn.createStatement();
                 ResultSet rs = cs.executeQuery(sql)) {

                ret = new DbDto();
                ret.setResult(new LinkedHashMap<Integer, List<Map<String, DbTypeDTO>>>());
                var tmp_array = new ArrayList<Map<String, DbTypeDTO>>();
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();

                while (rs.next()) {
                    Map<String, DbTypeDTO> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(meta.getColumnLabel(i), readCol(rs, meta, i));
                    }
                    tmp_array.add(row);
                }
                ret.getResult().put(0, tmp_array);
                ret.setErrFlag("N");

            } catch (Exception e) {
                // TODO: handle exception
                throw new Exception(e);
            }
        } catch (Exception e) {
            // TODO: handle exception
            ret = DbDto.builder().errFlag("Y").errCode("SERVER").errMsg(e.getMessage()).build();
        }

        return ret;
    }
}
