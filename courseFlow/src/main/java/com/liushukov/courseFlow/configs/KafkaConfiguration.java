package com.liushukov.courseFlow.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

    @Value("${kafka.verification.topic}")
    private String verificationTopicName;
    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of("bootstrap.servers", "localhost:9092"));
    }

    @Bean
    public NewTopic verificationEmail() {
        return new NewTopic(verificationTopicName, 1, (short) 1);
    }
}