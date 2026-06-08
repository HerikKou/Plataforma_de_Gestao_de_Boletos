package com.boletos.boleto.dto;
import com.boletos.boleto.model.TipoBoleto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
public class BoletoRequestDTO {
    @NotNull private LocalDate dataVencimento;
    @NotNull private TipoBoleto tipoBoleto;
    @NotBlank private String enderecoCobranca;
    @NotBlank private String cep;
    @NotBlank private String numeroResidencia;
    @NotNull private BigDecimal valor;
    public BoletoRequestDTO() {}
    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }
    public TipoBoleto getTipoBoleto() { return tipoBoleto; }
    public void setTipoBoleto(TipoBoleto tipoBoleto) { this.tipoBoleto = tipoBoleto; }
    public String getEnderecoCobranca() { return enderecoCobranca; }
    public void setEnderecoCobranca(String e) { this.enderecoCobranca = e; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getNumeroResidencia() { return numeroResidencia; }
    public void setNumeroResidencia(String n) { this.numeroResidencia = n; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}
