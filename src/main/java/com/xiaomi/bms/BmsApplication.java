package com.xiaomi.bms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
/**
 * @author zhuzhe1018
 * @date 2024/6/16
 */
@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableAsync
@MapperScan({"com.xiaomi.bms.mapper"})
public class BmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BmsApplication.class, args);
    }

}
