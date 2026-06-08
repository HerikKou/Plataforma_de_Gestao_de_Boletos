package com.boletos.vencimento.service;
import com.boletos.vencimento.dto.BoletoCriadoEventDTO;
import com.boletos.vencimento.model.Vencimento;
import com.boletos.vencimento.producer.VencimentoProducer;
import com.boletos.vencimento.repository.VencimentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class VencimentoServiceTest {
    @Mock private VencimentoRepository vencimentoRepository;
    @Mock private VencimentoProducer vencimentoProducer;
    @InjectMocks private VencimentoService vencimentoService;
    @Test
    void deveProcessarBoletoCriadoComSucesso() {
        BoletoCriadoEventDTO event = new BoletoCriadoEventDTO();
        event.setIdBoleto(UUID.randomUUID());
        event.setDataVencimento(LocalDate.now().plusDays(10));
        event.setValor(new BigDecimal("200.00"));
        when(vencimentoRepository.findByIdBoleto(any())).thenReturn(Optional.empty());
        when(vencimentoRepository.save(any())).thenReturn(new Vencimento());
        vencimentoService.processarBoletoCriado(event);
        verify(vencimentoRepository, times(1)).save(any());
    }
    @Test
    void deveIgnorarBoletoDuplicado() {
        BoletoCriadoEventDTO event = new BoletoCriadoEventDTO();
        UUID id = UUID.randomUUID();
        event.setIdBoleto(id);
        event.setDataVencimento(LocalDate.now().plusDays(5));
        event.setValor(new BigDecimal("100.00"));
        when(vencimentoRepository.findByIdBoleto(id)).thenReturn(Optional.of(new Vencimento()));
        vencimentoService.processarBoletoCriado(event);
        verify(vencimentoRepository, never()).save(any());
    }
}
