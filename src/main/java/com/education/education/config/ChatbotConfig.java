package com.education.education.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for Chatbot integration
 * This configures the necessary beans for our custom chatbot service
 */
@Configuration
public class ChatbotConfig {

    @Bean
    public RestTemplate chatbotRestTemplate() {
        return new RestTemplate();
    }
}