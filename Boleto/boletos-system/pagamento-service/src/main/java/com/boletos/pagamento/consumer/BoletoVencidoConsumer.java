package com.boletos.pagamento.consumer;
import com.boletos.pagamento.dto.VencimentoEventDTO;
import com.boletos.pagamento.service.PagamentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
@Component
public class BoletoVencidoConsumer {
    private static final Logger log = LoggerFactory.getLogger(BoletoVencidoConsumer.class);
    private final PagamentoService pagamentoService;
    public BoletoVencidoConsumer(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }
    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    @KafkaListener(topics = "${kafka.topics.boleto-vencido}", groupId = "pagamento-group")
    public void consumir(VencimentoEventDTO event) {
        log.info("Processando pagamento para boleto vencido: {}", event.getIdBoleto());
        pagamentoService.processarPagamento(event);
    }
    @DltHandler
    public void handleDlt(VencimentoEventDTO event) {
        log.error("DLT - Falha ao processar pagamento: {}", event.getIdBoleto());
    }
}
