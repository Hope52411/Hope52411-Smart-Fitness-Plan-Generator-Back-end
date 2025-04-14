package com.xxz.loginhouduan;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoginhouduanApplication {

    public static void main(String[] args) {
        // 加载 .env 文件
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir")) // 指定项目根目录
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        // 手动将 .env 变量注入到 System.setProperty，Spring Boot 解析 application.yml 时会用到
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        // 启动 Spring Boot
        SpringApplication.run(LoginhouduanApplication.class, args);
    }
}
