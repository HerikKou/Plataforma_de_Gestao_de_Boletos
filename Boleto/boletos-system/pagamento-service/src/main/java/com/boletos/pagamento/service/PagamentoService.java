package com.boletos.pagamento.service;
import com.boletos.pagamento.dto.VencimentoEventDTO;
import com.boletos.pagamento.model.Pagamento;
import com.boletos.pagamento.model.StatusPagamento;
import com.boletos.pagamento.repository.PagamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Service
public class PagamentoService {
    private final PagamentoRepository pagamentoRepository;
    public PagamentoService(PagamentoRepository pagamentoRepository) {
        this.pagamentoRepository = pagamentoRepository;
    }
    @Transactional
    public void processarPagamento(VencimentoEventDTO event) {
        boolean jaProcessado = pagamentoRepository.findByIdBoleto(event.getIdBoleto())
            .map(Pagamento::isEventoJaProcessado).orElse(false);
        if (jaProcessado) { return; }
        Pagamento pagamento = new Pagamento();
        pagamento.setIdBoleto(event.getIdBoleto());
        pagamento.setDataPagamento(LocalDateTime.now());
        BigDecimal total = event.getValorOriginal().add(
            event.getJuros() != null ? event.getJuros() : BigDecimal.ZERO);
        pagamento.setValorPago(total);
        pagamento.setStatus(StatusPagamento.REALIZADO);
        pagamento.setEventoJaProcessado(true);
        pagamentoRepository.save(pagamento);
    }
}
