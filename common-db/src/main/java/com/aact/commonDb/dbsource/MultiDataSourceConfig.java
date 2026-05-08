package com.aact.commonDb.dbsource;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MultiDataSourceConfig {
    private final Environment env;

    @Bean("multiDataSource")
    public Map<com.aact.common.SourcName, DataSource> multiDataSource() {
        Map<com.aact.common.SourcName,DataSource> map = new LinkedHashMap<>();
        addIfPresent(map, com.aact.common.SourcName.MAIN, "db.main", "oracle.jdbc.OracleDriver", "SELECT 1 FROM DUAL");
        addIfPresent(map, com.aact.common.SourcName.EDI, "db.edi", "oracle.jdbc.OracleDriver", "SELECT 1 FROM DUAL");
        addIfPresent(map, com.aact.common.SourcName.INFRA, "db.infra", "oracle.jdbc.OracleDriver", "SELECT 1 FROM DUAL");
        addIfPresent(map, com.aact.common.SourcName.CAPS, "db.caps", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "SELECT 1");
        log.info("등록된 DataSource: {}", map.keySet());
        return map;
    }

    @Bean("mainSelect")
    public String mainSelect(){
        String ret = env.getProperty("db.default");
        if(ret ==null||ret.isBlank()){
            log.error("디폴트 DB가 없습니다.");
            throw new IllegalStateException("디폴트 DB가 없습니다.");
        }
        return ret;
    }

    private void addIfPresent(
            Map<com.aact.common.SourcName, DataSource> map,
            com.aact.common.SourcName name,
            String prefix,
            String driver,
            String testQuery
    ) {
        DataSource ds = createDataSource(name, prefix, driver, testQuery);

        if (ds != null) {
            map.put(name, ds);
        }
    }


    private DataSource createDataSource(
            com.aact.common.SourcName name,
            String prefix,
            String driver,
            String testQuery
    ) {
        String url = env.getProperty(prefix + ".url");
        String username = env.getProperty(prefix + ".username");
        String password = env.getProperty(prefix + ".password");

        if (url == null || url.isBlank()) {
            log.info("{} DB 미사용: {}.url 없음", name, prefix);
            return null;
        }

        if (username == null || username.isBlank()) {
            throw new IllegalStateException(name + " DB username 설정이 없습니다. key=" + prefix + ".username");
        }

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName(driver);
        ds.setMaximumPoolSize(5);
        ds.setMinimumIdle(1);
        ds.setIdleTimeout(300000);
        ds.setMaxLifetime(600000);
        ds.setKeepaliveTime(240000);
        ds.setConnectionTimeout(60000);
        ds.setValidationTimeout(3000);
        ds.setConnectionTestQuery(testQuery);

        try (Connection con = ds.getConnection()) {
            log.info("{} DB 정상 기동", name);
        } catch (SQLException e) {
            ds.close();
            throw new IllegalStateException(name + " DB 연결 실패. url=" + url, e);
        }

        return ds;
    }
}
