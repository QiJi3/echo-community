package com.echo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.echo.mapper")
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class EchoCommunityApplication {
  public static void main(String[] args) {
    SpringApplication.run(EchoCommunityApplication.class, args);
  }
}
