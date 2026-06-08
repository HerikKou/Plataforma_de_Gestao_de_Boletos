package com.boletos.pagamento.service;
import com.boletos.pagamento.dto.VencimentoEventDTO;
import com.boletos.pagamento.model.Pagamento;
import com.boletos.pagamento.model.StatusPagamento;
import com.boletos.pagamento.repository.PagamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {
    @Mock private PagamentoRepository pagamentoRepository;
    @InjectMocks private PagamentoService pagamentoService;
    @Test
    void deveProcessarPagamentoComSucesso() {
        VencimentoEventDTO event = new VencimentoEventDTO();
        event.setIdBoleto(UUID.randomUUID());
        event.setValorOriginal(new BigDecimal("300.00"));
        event.setJuros(new BigDecimal("15.00"));
        event.setDataVencimento(LocalDate.now().minusDays(1));
        when(pagamentoRepository.findByIdBoleto(any())).thenReturn(Optional.empty());
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(i -> i.getArgument(0));
        pagamentoService.processarPagamento(event);
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }
    @Test
    void deveIgnorarPagamentoDuplicado() {
        UUID id = UUID.randomUUID();
        VencimentoEventDTO event = new VencimentoEventDTO();
        event.setIdBoleto(id);
        event.setValorOriginal(new BigDecimal("200.00"));
        event.setJuros(BigDecimal.ZERO);
        Pagamento pagamentoExistente = new Pagamento();
        pagamentoExistente.setEventoJaProcessado(true);
        when(pagamentoRepository.findByIdBoleto(id)).thenReturn(Optional.of(pagamentoExistente));
        pagamentoService.processarPagamento(event);
        verify(pagamentoRepository, never()).save(any());
    }
}
