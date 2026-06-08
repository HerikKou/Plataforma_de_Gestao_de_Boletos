package com.boletos.boleto.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "boletos")
public class Boleto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotNull
    private LocalDateTime dataCriacao;
    @NotNull
    private LocalDate dataVencimento;
    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoBoleto tipoBoleto;
    @NotBlank
    private String enderecoCobranca;
    @NotBlank
    private String cep;
    @NotBlank
    private String numeroResidencia;
    @NotNull
    private BigDecimal valor;
    @Enumerated(EnumType.STRING)
    private BoletoStatus status = BoletoStatus.CRIADO;
    public Boleto() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }
    public TipoBoleto getTipoBoleto() { return tipoBoleto; }
    public void setTipoBoleto(TipoBoleto tipoBoleto) { this.tipoBoleto = tipoBoleto; }
    public String getEnderecoCobranca() { return enderecoCobranca; }
    public void setEnderecoCobranca(String enderecoCobranca) { this.enderecoCobranca = enderecoCobranca; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getNumeroResidencia() { return numeroResidencia; }
    public void setNumeroResidencia(String numeroResidencia) { this.numeroResidencia = numeroResidencia; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public BoletoStatus getStatus() { return status; }
    public void setStatus(BoletoStatus status) { this.status = status; }
}
