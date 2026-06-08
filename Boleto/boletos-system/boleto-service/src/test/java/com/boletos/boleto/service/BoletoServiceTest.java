package com.boletos.boleto.service;
import com.boletos.boleto.dto.BoletoRequestDTO;
import com.boletos.boleto.dto.BoletoResponseDTO;
import com.boletos.boleto.exception.BoletoNotFoundException;
import com.boletos.boleto.model.Boleto;
import com.boletos.boleto.model.TipoBoleto;
import com.boletos.boleto.producer.BoletoProducer;
import com.boletos.boleto.repository.BoletoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class BoletoServiceTest {
    @Mock private BoletoRepository boletoRepository;
    @Mock private BoletoProducer boletoProducer;
    @InjectMocks private BoletoService boletoService;
    private BoletoRequestDTO requestDTO;
    private Boleto boleto;
    @BeforeEach
    void setUp() {
        requestDTO = new BoletoRequestDTO();
        requestDTO.setDataVencimento(LocalDate.now().plusDays(10));
        requestDTO.setTipoBoleto(TipoBoleto.AGUA);
        requestDTO.setEnderecoCobranca("Rua A, 123");
        requestDTO.setCep("01001-000");
        requestDTO.setNumeroResidencia("123");
        requestDTO.setValor(new BigDecimal("150.00"));
        boleto = new Boleto();
        boleto.setId(UUID.randomUUID());
        boleto.setDataCriacao(LocalDateTime.now());
        boleto.setDataVencimento(requestDTO.getDataVencimento());
        boleto.setTipoBoleto(TipoBoleto.AGUA);
        boleto.setEnderecoCobranca("Rua A, 123");
        boleto.setCep("01001-000");
        boleto.setNumeroResidencia("123");
        boleto.setValor(new BigDecimal("150.00"));
    }
    @Test
    void deveCriarBoletoComSucesso() {
        when(boletoRepository.save(any(Boleto.class))).thenReturn(boleto);
        doNothing().when(boletoProducer).publicarBoletoCriado(any());
        BoletoResponseDTO response = boletoService.criarBoleto(requestDTO);
        assertNotNull(response);
        assertEquals(boleto.getId(), response.getId());
        verify(boletoRepository, times(1)).save(any());
        verify(boletoProducer, times(1)).publicarBoletoCriado(any());
    }
    @Test
    void deveLancarExcecaoQuandoBoletoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(boletoRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(BoletoNotFoundException.class, () -> boletoService.buscarPorId(id));
    }
    @Test
    void deveBuscarBoletoPorId() {
        when(boletoRepository.findById(boleto.getId())).thenReturn(Optional.of(boleto));
        BoletoResponseDTO response = boletoService.buscarPorId(boleto.getId());
        assertNotNull(response);
        assertEquals(boleto.getId(), response.getId());
    }
}
