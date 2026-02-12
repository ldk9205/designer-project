package com.designer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.designer.auth.mapper")  // Mapper 패키지 지정
@MapperScan("com.designer.*")
public class DesignerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DesignerApplication.class, args);
    }
}
