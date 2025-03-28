package com.liushukov.courseFlow.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import java.util.Map;

@Configuration
public class KafkaConfiguration {
    @Value("${kafka.grade.topic}")
    private String gradeTopicName;
    @Value("${kafka.verification.topic}")
    private String verificationTopicName;
    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServersValue;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of("bootstrap.servers", bootstrapServersValue));
    }

    @Bean
    public NewTopic verificationEmail() {
        return new NewTopic(verificationTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic gradeEmailNotification() {
        return new NewTopic(gradeTopicName, 1, (short) 1);
    }
}
