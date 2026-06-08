package com.boletos.vencimento.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
@Entity
@Table(name = "vencimentos")
public class Vencimento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotNull
    private UUID idBoleto;
    @NotNull
    private BigDecimal valorOriginal;
    private BigDecimal juros;
    @NotNull
    private LocalDate dataVencimento;
    private boolean boletoJaProcessado = false;
    public Vencimento() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getIdBoleto() { return idBoleto; }
    public void setIdBoleto(UUID idBoleto) { this.idBoleto = idBoleto; }
    public BigDecimal getValorOriginal() { return valorOriginal; }
    public void setValorOriginal(BigDecimal v) { this.valorOriginal = v; }
    public BigDecimal getJuros() { return juros; }
    public void setJuros(BigDecimal juros) { this.juros = juros; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate d) { this.dataVencimento = d; }
    public boolean isBoletoJaProcessado() { return boletoJaProcessado; }
    public void setBoletoJaProcessado(boolean b) { this.boletoJaProcessado = b; }
}
