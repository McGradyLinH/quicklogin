package com.example.quicklogin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.quicklogin.mapper")
public class QuickloginApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuickloginApplication.class, args);
    }

}
