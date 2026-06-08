package com.boletos.vencimento.consumer;
import com.boletos.vencimento.dto.BoletoCriadoEventDTO;
import com.boletos.vencimento.service.VencimentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
@Component
public class BoletoCriadoConsumer {
    private static final Logger log = LoggerFactory.getLogger(BoletoCriadoConsumer.class);
    private final VencimentoService vencimentoService;
    public BoletoCriadoConsumer(VencimentoService vencimentoService) {
        this.vencimentoService = vencimentoService;
    }
    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    @KafkaListener(topics = "${kafka.topics.boleto-criado}", groupId = "vencimento-group")
    public void consumir(BoletoCriadoEventDTO event) {
        log.info("Recebendo BoletoCriado: {}", event.getIdBoleto());
        vencimentoService.processarBoletoCriado(event);
    }
    @DltHandler
    public void handleDlt(BoletoCriadoEventDTO event) {
        log.error("DLT - Falha ao processar BoletoCriado: {}", event.getIdBoleto());
    }
}
