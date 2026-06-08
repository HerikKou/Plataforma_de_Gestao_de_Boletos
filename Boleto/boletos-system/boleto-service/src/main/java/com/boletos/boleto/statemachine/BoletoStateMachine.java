package com.boletos.boleto.statemachine;
import com.boletos.boleto.exception.BoletoInvalidoException;
import com.boletos.boleto.model.BoletoEvento;
import com.boletos.boleto.model.BoletoStatus;
import org.springframework.stereotype.Component;
@Component
public class BoletoStateMachine {
    public BoletoStatus transition(BoletoStatus statusAtual, BoletoEvento evento) {
        return switch (statusAtual) {
            case CRIADO -> switch (evento) {
                case BOLETO_PERTO_VENCIMENTO -> BoletoStatus.PROXIMO_VENCIMENTO;
                case BOLETO_VENCIDO -> BoletoStatus.VENCIDO;
                default -> throw new BoletoInvalidoException("Transicao invalida: " + statusAtual + " + " + evento);
            };
            case PROXIMO_VENCIMENTO -> switch (evento) {
                case BOLETO_VENCIDO -> BoletoStatus.VENCIDO;
                default -> throw new BoletoInvalidoException("Transicao invalida: " + statusAtual + " + " + evento);
            };
            case VENCIDO -> switch (evento) {
                case PAGAMENTO_REALIZADO -> BoletoStatus.PAGO;
                default -> throw new BoletoInvalidoException("Transicao invalida: " + statusAtual + " + " + evento);
            };
            case PAGO -> throw new BoletoInvalidoException("Boleto ja pago, sem transicoes possiveis.");
        };
    }
}
