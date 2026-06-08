package com.boletos.notificacao.consumer;
import com.boletos.notificacao.dto.VencimentoEventDTO;
import com.boletos.notificacao.service.NotificacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
@Component
public class VencimentoConsumer {
    private static final Logger log = LoggerFactory.getLogger(VencimentoConsumer.class);
    private final NotificacaoService notificacaoService;
    public VencimentoConsumer(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }
    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    @KafkaListener(topics = "${kafka.topics.boleto-perto-vencimento}", groupId = "notificacao-group")
    public void consumirPertoVencimento(VencimentoEventDTO event) {
        log.info("Notificando perto do vencimento: {}", event.getIdBoleto());
        notificacaoService.notificarPertoVencimento(event);
    }
    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    @KafkaListener(topics = "${kafka.topics.boleto-vencido}", groupId = "notificacao-group")
    public void consumirVencido(VencimentoEventDTO event) {
        log.info("Notificando boleto vencido: {}", event.getIdBoleto());
        notificacaoService.notificarVencido(event);
    }
    @DltHandler
    public void handleDlt(VencimentoEventDTO event) {
        log.error("DLT - Falha ao notificar boleto: {}", event.getIdBoleto());
    }
}
