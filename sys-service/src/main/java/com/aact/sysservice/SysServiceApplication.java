package com.aact.sysservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {
        org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration.class
})
@ComponentScan(basePackages = {
        "com.aact.sysservice",
        "com.aact.web",
        "com.aact.commonDb.dbsource"
})
public class SysServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SysServiceApplication.class, args);
    }

}
