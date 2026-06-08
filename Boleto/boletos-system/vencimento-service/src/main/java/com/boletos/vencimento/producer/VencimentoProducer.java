package com.boletos.vencimento.producer;
import com.boletos.vencimento.dto.VencimentoEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
@Component
public class VencimentoProducer {
    private static final Logger log = LoggerFactory.getLogger(VencimentoProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topics.boleto-perto-vencimento}")
    private String pertoVencimentoTopic;
    @Value("${kafka.topics.boleto-vencido}")
    private String vencidoTopic;
    public VencimentoProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void publicarPertoVencimento(VencimentoEventDTO event) {
        log.info("Publicando BoletoPertoDoVencimento: {}", event.getIdBoleto());
        kafkaTemplate.send(pertoVencimentoTopic, event.getIdBoleto().toString(), event);
    }
    public void publicarVencido(VencimentoEventDTO event) {
        log.info("Publicando BoletoVencido: {}", event.getIdBoleto());
        kafkaTemplate.send(vencidoTopic, event.getIdBoleto().toString(), event);
    }
}
