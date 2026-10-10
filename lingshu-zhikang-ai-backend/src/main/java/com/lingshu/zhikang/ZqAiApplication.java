package com.lingshu.zhikang;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.lingshu.zhikang.mapper")
@SpringBootApplication(exclude = {
    org.springframework.ai.autoconfigure.ollama.OllamaAutoConfiguration.class
})
public class ZqAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZqAiApplication.class, args);
    }

}
