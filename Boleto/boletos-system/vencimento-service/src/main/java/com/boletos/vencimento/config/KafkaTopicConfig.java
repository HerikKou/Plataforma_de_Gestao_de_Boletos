package com.boletos.vencimento.config;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
@Configuration
public class KafkaTopicConfig {
    @Value("${kafka.topics.boleto-perto-vencimento}")
    private String pertoVencimentoTopic;
    @Value("${kafka.topics.boleto-vencido}")
    private String vencidoTopic;
    @Bean
    public NewTopic boletoPertoVencimentoTopic() {
        return TopicBuilder.name(pertoVencimentoTopic).partitions(3).replicas(1).build();
    }
    @Bean
    public NewTopic boletoPertoVencimentoDlt() {
        return TopicBuilder.name(pertoVencimentoTopic + ".DLT").partitions(1).replicas(1).build();
    }
    @Bean
    public NewTopic boletoVencidoTopic() {
        return TopicBuilder.name(vencidoTopic).partitions(3).replicas(1).build();
    }
    @Bean
    public NewTopic boletoVencidoDlt() {
        return TopicBuilder.name(vencidoTopic + ".DLT").partitions(1).replicas(1).build();
    }
}
