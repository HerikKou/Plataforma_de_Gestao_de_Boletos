package com.boletos.boleto.dto;
import com.boletos.boleto.model.BoletoStatus;
import com.boletos.boleto.model.TipoBoleto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
public class BoletoResponseDTO {
    private UUID id;
    private LocalDateTime dataCriacao;
    private LocalDate dataVencimento;
    private TipoBoleto tipoBoleto;
    private String enderecoCobranca;
    private String cep;
    private String numeroResidencia;
    private BigDecimal valor;
    private BoletoStatus status;
    public BoletoResponseDTO() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime d) { this.dataCriacao = d; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate d) { this.dataVencimento = d; }
    public TipoBoleto getTipoBoleto() { return tipoBoleto; }
    public void setTipoBoleto(TipoBoleto t) { this.tipoBoleto = t; }
    public String getEnderecoCobranca() { return enderecoCobranca; }
    public void setEnderecoCobranca(String e) { this.enderecoCobranca = e; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getNumeroResidencia() { return numeroResidencia; }
    public void setNumeroResidencia(String n) { this.numeroResidencia = n; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal v) { this.valor = v; }
    public BoletoStatus getStatus() { return status; }
    public void setStatus(BoletoStatus s) { this.status = s; }
}
