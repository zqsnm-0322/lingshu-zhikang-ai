package com.lingshu.zhikang.config;

import com.lingshu.zhikang.repository.MysqlChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * MySQL聊天记录配置类
 */
@Configuration
@RequiredArgsConstructor
public class MysqlChatConfig {

    @Autowired
    private MysqlChatMemory mysqlChatMemory;
    
    /**
     * 注册MySQL版本的ChatMemory
     * 使用@Primary注解，优先使用此Bean
     */
    @Bean
    @Primary
    public ChatMemory chatMemory() {
        return mysqlChatMemory;
    }
}
