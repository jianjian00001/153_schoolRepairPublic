package com.xpu.repair;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
//@MapperScan("com.xpu.repair.mapper")
@ServletComponentScan("com.xpu.repair.filter")
public class RepairApplication {

    public static void main(String[] args) {
        SpringApplication.run(RepairApplication.class, args);
    }

}
