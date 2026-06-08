package com.boletos.notificacao.service;
import com.boletos.notificacao.dto.VencimentoEventDTO;
import com.boletos.notificacao.exception.NotificacaoException;
import com.boletos.notificacao.model.Notificacao;
import com.boletos.notificacao.repository.NotificacaoRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.time.LocalDateTime;
@Service
public class NotificacaoService {
    private final NotificacaoRepository notificacaoRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    public NotificacaoService(NotificacaoRepository r, JavaMailSender m, TemplateEngine t) {
        this.notificacaoRepository = r;
        this.mailSender = m;
        this.templateEngine = t;
    }
    public void notificarPertoVencimento(VencimentoEventDTO event) {
        String mensagem = "Boleto " + event.getIdBoleto() + " vence em breve: " + event.getDataVencimento();
        enviarEmail(event.getEmailDestino(), "Boleto Proximo do Vencimento", mensagem, event);
        registrar(mensagem, event.getEmailDestino());
    }
    public void notificarVencido(VencimentoEventDTO event) {
        String mensagem = "Boleto " + event.getIdBoleto() + " VENCIDO. Juros: " + event.getJuros();
        enviarEmail(event.getEmailDestino(), "Boleto Vencido", mensagem, event);
        registrar(mensagem, event.getEmailDestino());
    }
    private void enviarEmail(String destino, String assunto, String mensagem, VencimentoEventDTO event) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setTo(destino);
            helper.setSubject(assunto);
            Context ctx = new Context();
            ctx.setVariable("mensagem", mensagem);
            ctx.setVariable("event", event);
            String html = templateEngine.process("email-notificacao", ctx);
            helper.setText(html, true);
            mailSender.send(mime);
        } catch (MessagingException e) {
            throw new NotificacaoException("Erro ao enviar email para: " + destino, e);
        }
    }
    private void registrar(String mensagem, String email) {
        Notificacao n = new Notificacao();
        n.setMensagem(mensagem);
        n.setEmailDestino(email);
        n.setDataEnvio(LocalDateTime.now());
        notificacaoRepository.save(n);
    }
}
