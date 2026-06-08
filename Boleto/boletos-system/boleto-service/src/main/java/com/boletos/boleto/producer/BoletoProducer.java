package com.boletos.boleto.producer;
import com.boletos.boleto.dto.BoletoCriadoEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
@Component
public class BoletoProducer {
    private static final Logger log = LoggerFactory.getLogger(BoletoProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topics.boleto-criado}")
    private String boletoCriadoTopic;
    public BoletoProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void publicarBoletoCriado(BoletoCriadoEventDTO event) {
        log.info("Publicando evento BoletoCriado: {}", event.getIdBoleto());
        kafkaTemplate.send(boletoCriadoTopic, event.getIdBoleto().toString(), event);
    }
}
