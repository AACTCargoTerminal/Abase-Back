package com.aact.infraservice.config;


import com.aact.common.SourcName;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@Slf4j
@MapperScan(basePackages = "com.aact.infraservice.repo", sqlSessionTemplateRef = "capsSqlSessionTemplate")
@RequiredArgsConstructor
public class CapsMyBatisConfig {

    private final Map<SourcName,DataSource> multiDataSource;

    @PostConstruct
    public void init() {
        log.info(multiDataSource.keySet().toString());
    }

    @Bean("capsSqlSessionFactory")
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public SqlSessionFactory capsSqlSessionFactory()
            throws Exception {

        DataSource dt = multiDataSource.get(SourcName.CAPS);
        if(dt==null){
            log.error("capsSqlSessionFactory : {}","CAPS 데이터 소스 없음.");
            throw new IllegalStateException("CAPS 데이터 소스 없음.");
        }

        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dt);

        // CAPS 전용 mapper xml 경로
        factoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:/caps/*.xml"));

        return factoryBean.getObject();
    }

    @Bean("capsSqlSessionTemplate")
    public SqlSessionTemplate capsSqlSessionTemplate(
            @Qualifier("capsSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}