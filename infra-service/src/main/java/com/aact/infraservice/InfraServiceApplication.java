package com.aact.infraservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class
})
@ComponentScan(basePackages = {
        "com.aact.infraservice",
        "com.aact.commonDb.dbsource",
        "com.aact.web",
        "com.aact.commonClient"
})
public class InfraServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InfraServiceApplication.class, args);
    }
}
