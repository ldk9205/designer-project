package com.designer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.designer.*.mapper")

public class DesignerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DesignerApplication.class, args);
    }
}
