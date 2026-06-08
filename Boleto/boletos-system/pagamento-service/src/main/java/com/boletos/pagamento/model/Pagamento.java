package com.boletos.pagamento.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "pagamentos")
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotNull
    private UUID idBoleto;
    @NotNull
    @Enumerated(EnumType.STRING)
    private StatusPagamento status;
    @NotNull
    private LocalDateTime dataPagamento;
    @NotNull
    private BigDecimal valorPago;
    private boolean eventoJaProcessado = false;
    public Pagamento() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getIdBoleto() { return idBoleto; }
    public void setIdBoleto(UUID idBoleto) { this.idBoleto = idBoleto; }
    public StatusPagamento getStatus() { return status; }
    public void setStatus(StatusPagamento status) { this.status = status; }
    public LocalDateTime getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDateTime d) { this.dataPagamento = d; }
    public BigDecimal getValorPago() { return valorPago; }
    public void setValorPago(BigDecimal v) { this.valorPago = v; }
    public boolean isEventoJaProcessado() { return eventoJaProcessado; }
    public void setEventoJaProcessado(boolean e) { this.eventoJaProcessado = e; }
}
