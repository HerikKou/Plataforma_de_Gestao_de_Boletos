package com.boletos.boleto.service;
import com.boletos.boleto.dto.BoletoCriadoEventDTO;
import com.boletos.boleto.dto.BoletoRequestDTO;
import com.boletos.boleto.dto.BoletoResponseDTO;
import com.boletos.boleto.exception.BoletoNotFoundException;
import com.boletos.boleto.model.Boleto;
import com.boletos.boleto.producer.BoletoProducer;
import com.boletos.boleto.repository.BoletoRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class BoletoService {
    private final BoletoRepository boletoRepository;
    private final BoletoProducer boletoProducer;
    public BoletoService(BoletoRepository boletoRepository, BoletoProducer boletoProducer) {
        this.boletoRepository = boletoRepository;
        this.boletoProducer = boletoProducer;
    }
    @Transactional
    public BoletoResponseDTO criarBoleto(BoletoRequestDTO dto) {
        Boleto boleto = new Boleto();
        boleto.setDataCriacao(LocalDateTime.now());
        boleto.setDataVencimento(dto.getDataVencimento());
        boleto.setTipoBoleto(dto.getTipoBoleto());
        boleto.setEnderecoCobranca(dto.getEnderecoCobranca());
        boleto.setCep(dto.getCep());
        boleto.setNumeroResidencia(dto.getNumeroResidencia());
        boleto.setValor(dto.getValor());
        Boleto salvo = boletoRepository.save(boleto);
        BoletoCriadoEventDTO event = new BoletoCriadoEventDTO(
            salvo.getId(), salvo.getDataVencimento(), salvo.getValor(), "cliente@email.com");
        boletoProducer.publicarBoletoCriado(event);
        return toResponseDTO(salvo);
    }
    @Cacheable("boletos")
    public BoletoResponseDTO buscarPorId(UUID id) {
        Boleto boleto = boletoRepository.findById(id)
            .orElseThrow(() -> new BoletoNotFoundException("Boleto nao encontrado: " + id));
        return toResponseDTO(boleto);
    }
    public List<BoletoResponseDTO> listarTodos() {
        return boletoRepository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
    private BoletoResponseDTO toResponseDTO(Boleto b) {
        BoletoResponseDTO dto = new BoletoResponseDTO();
        dto.setId(b.getId());
        dto.setDataCriacao(b.getDataCriacao());
        dto.setDataVencimento(b.getDataVencimento());
        dto.setTipoBoleto(b.getTipoBoleto());
        dto.setEnderecoCobranca(b.getEnderecoCobranca());
        dto.setCep(b.getCep());
        dto.setNumeroResidencia(b.getNumeroResidencia());
        dto.setValor(b.getValor());
        dto.setStatus(b.getStatus());
        return dto;
    }
}
