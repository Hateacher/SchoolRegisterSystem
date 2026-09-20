package com.login.schoolregistersystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.login.schoolregistersystem.mapper")
public class SchoolRegisterSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchoolRegisterSystemApplication.class, args);
    }

}
