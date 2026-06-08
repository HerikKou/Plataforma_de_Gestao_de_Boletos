package com.boletos.pagamento.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
public class VencimentoEventDTO {
    private UUID idBoleto;
    private BigDecimal valorOriginal;
    private BigDecimal juros;
    private LocalDate dataVencimento;
    private String emailDestino;
    public VencimentoEventDTO() {}
    public UUID getIdBoleto() { return idBoleto; }
    public void setIdBoleto(UUID id) { this.idBoleto = id; }
    public BigDecimal getValorOriginal() { return valorOriginal; }
    public void setValorOriginal(BigDecimal v) { this.valorOriginal = v; }
    public BigDecimal getJuros() { return juros; }
    public void setJuros(BigDecimal j) { this.juros = j; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate d) { this.dataVencimento = d; }
    public String getEmailDestino() { return emailDestino; }
    public void setEmailDestino(String e) { this.emailDestino = e; }
}
