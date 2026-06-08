package com.boletos.vencimento.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
public class BoletoCriadoEventDTO {
    private UUID idBoleto;
    private LocalDate dataVencimento;
    private BigDecimal valor;
    private String emailDestino;
    public BoletoCriadoEventDTO() {}
    public UUID getIdBoleto() { return idBoleto; }
    public void setIdBoleto(UUID idBoleto) { this.idBoleto = idBoleto; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate d) { this.dataVencimento = d; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal v) { this.valor = v; }
    public String getEmailDestino() { return emailDestino; }
    public void setEmailDestino(String e) { this.emailDestino = e; }
}
