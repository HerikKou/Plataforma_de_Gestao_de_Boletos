package com.boletos.vencimento.service;
import com.boletos.vencimento.dto.BoletoCriadoEventDTO;
import com.boletos.vencimento.dto.VencimentoEventDTO;
import com.boletos.vencimento.model.Vencimento;
import com.boletos.vencimento.producer.VencimentoProducer;
import com.boletos.vencimento.repository.VencimentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
@Service
public class VencimentoService {
    private static final BigDecimal JUROS = new BigDecimal("0.05");
    private final VencimentoRepository vencimentoRepository;
    private final VencimentoProducer vencimentoProducer;
    public VencimentoService(VencimentoRepository vencimentoRepository, VencimentoProducer vencimentoProducer) {
        this.vencimentoRepository = vencimentoRepository;
        this.vencimentoProducer = vencimentoProducer;
    }
    @Transactional
    public void processarBoletoCriado(BoletoCriadoEventDTO event) {
        boolean jaExiste = vencimentoRepository.findByIdBoleto(event.getIdBoleto()).isPresent();
        if (jaExiste) { return; }
        Vencimento v = new Vencimento();
        v.setIdBoleto(event.getIdBoleto());
        v.setValorOriginal(event.getValor());
        v.setDataVencimento(event.getDataVencimento());
        v.setBoletoJaProcessado(false);
        vencimentoRepository.save(v);
    }
    @Transactional
    public void verificarVencimentos() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(3);
        List<Vencimento> todos = vencimentoRepository.findAll();
        for (Vencimento v : todos) {
            if (v.isBoletoJaProcessado()) continue;
            VencimentoEventDTO event;
            if (!v.getDataVencimento().isAfter(hoje)) {
                BigDecimal juros = v.getValorOriginal().multiply(JUROS).setScale(2, RoundingMode.HALF_UP);
                v.setJuros(juros);
                v.setBoletoJaProcessado(true);
                vencimentoRepository.save(v);
                event = new VencimentoEventDTO(v.getIdBoleto(), v.getValorOriginal(), juros, v.getDataVencimento(), "cliente@email.com");
                vencimentoProducer.publicarVencido(event);
            } else if (!v.getDataVencimento().isAfter(limite)) {
                v.setBoletoJaProcessado(true);
                vencimentoRepository.save(v);
                event = new VencimentoEventDTO(v.getIdBoleto(), v.getValorOriginal(), BigDecimal.ZERO, v.getDataVencimento(), "cliente@email.com");
                vencimentoProducer.publicarPertoVencimento(event);
            }
        }
    }
}
