package com.boletos.notificacao.service;
import com.boletos.notificacao.dto.VencimentoEventDTO;
import com.boletos.notificacao.model.Notificacao;
import com.boletos.notificacao.repository.NotificacaoRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {
    @Mock private NotificacaoRepository notificacaoRepository;
    @Mock private JavaMailSender mailSender;
    @Mock private TemplateEngine templateEngine;
    @InjectMocks private NotificacaoService notificacaoService;
    @Test
    void deveNotificarPertoVencimento() {
        VencimentoEventDTO event = new VencimentoEventDTO();
        event.setIdBoleto(UUID.randomUUID());
        event.setEmailDestino("test@test.com");
        event.setDataVencimento(LocalDate.now().plusDays(2));
        event.setValorOriginal(new BigDecimal("100.00"));
        event.setJuros(BigDecimal.ZERO);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("email-notificacao"), any(Context.class))).thenReturn("^<html^>^</html^>");
        when(notificacaoRepository.save(any(Notificacao.class))).thenReturn(new Notificacao());
        notificacaoService.notificarPertoVencimento(event);
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        verify(notificacaoRepository, times(1)).save(any());
    }
}
