package com.definesys.mdm.connector;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/3/29 16:34
 */
@MapperScan("com.definesys.mdm.connector.mapper")
@SpringBootApplication
public class main {
    public static void main(String[] args) {
        SpringApplication.run(main.class,args);
    }
}
