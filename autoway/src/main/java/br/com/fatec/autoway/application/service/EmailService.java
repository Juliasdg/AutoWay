package br.com.fatec.autoway.application.service;

import br.com.fatec.autoway.application.util.DateUtils;
import br.com.fatec.autoway.domain.model.Boleto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final BoletoPdfService boletoPdfService;

    public EmailService(JavaMailSender mailSender, BoletoPdfService boletoPdfService) {
        this.mailSender = mailSender;
        this.boletoPdfService = boletoPdfService;
    }

    private String buildEmailTemplate(String titulo, String conteudoHtml) {
        return "<!DOCTYPE html>" +
                "<html lang='pt-BR'>" +
                "<head><meta charset='UTF-8'><title>" + titulo + "</title></head>" +
                "<body style='font-family:Arial,sans-serif;background-color:#f4f6f8;margin:0;padding:0;'>" +
                "<div style='max-width:600px;margin:30px auto;background:#ffffff;border-radius:8px;" +
                "box-shadow:0 4px 12px rgba(0,0,0,0.1);overflow:hidden;'>" +

                "<div style='background:#4E0967;padding:20px;text-align:center;'>" +
                "<img src='cid:logo' alt='Logo' style='width:307px;height:164px;'/>" +
                "</div>" +

                "<div style='padding:30px 20px;text-align:center;'>" +
                conteudoHtml +
                "</div>" +

                "<div style='background:#f4f6f8;padding:15px;text-align:center;font-size:12px;color:#999;'>" +
                "© 2025 Autoway. Todos os direitos reservados." +
                "</div>" +

                "</div>" +
                "</body>" +
                "</html>";
    }

    private void addLogo(MimeMessageHelper helper) throws MessagingException {
        ClassPathResource logo = new ClassPathResource("static/logo.png");
        if (logo.exists()) {
            helper.addInline("logo", logo);
        }
    }

    /** ------------------------------
     * E-mails específicos
     * ------------------------------ */

    @Async
    public void sendHtmlConfirmationEmail(String to, String name, String link) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Confirmação de Cadastro");

            String conteudo = "<h2 style='color:#333;'>Olá " + name + ",</h2>" +
                    "<p style='color:#555;font-size:16px;line-height:1.5;'>Obrigado por se cadastrar! " +
                    "Por favor, confirme seu e-mail clicando no botão abaixo:</p>" +
                    "<a href='" + link + "' style='display:inline-block;padding:12px 25px;margin:20px 0;" +
                    "color:white;background-color:#28a745;text-decoration:none;border-radius:5px;font-weight:bold;'>" +
                    "Confirmar Conta</a>" +
                    "<p style='color:#888;font-size:14px;'>Se você não se cadastrou, apenas ignore este e-mail.</p>";

            helper.setText(buildEmailTemplate("Confirmação de Cadastro", conteudo), true);
            addLogo(helper);
            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendPasswordResetEmail(String to, String name, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Redefinição de Senha");

            String conteudo = "<h2 style='color:#333;'>Olá " + name + ",</h2>" +
                    "<p style='color:#555;font-size:16px;line-height:1.5;'>Você solicitou a redefinição de senha. Seu código é:</p>" +
                    "<h1 style='color:#4E0967;'>" + code + "</h1>" +
                    "<p style='color:#888;font-size:14px;'>Se você não solicitou, apenas ignore este e-mail.</p>";

            helper.setText(buildEmailTemplate("Redefinição de Senha", conteudo), true);
            addLogo(helper);
            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendGenericEmail(String to, String subject, String bodyHtmlOrText) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);

            String conteudo = "<p style='color:#555;font-size:16px;line-height:1.5;'>" +
                    bodyHtmlOrText + "</p>";

            helper.setText(buildEmailTemplate(subject, conteudo), true);
            addLogo(helper);
            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendPassagemEmail(String to, String placa, LocalDate data, LocalTime hora, Double valor) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Nova Passagem Registrada");

            String conteudo = "<h2 style='color:#333;'>Nova Passagem Registrada</h2>" +
                    "<p style='color:#555;'>Placa: <b>" + placa + "</b></p>" +
                    "<p style='color:#555;'>Data: <b>" + DateUtils.formatToBR(data) + "</b></p>" +
                    "<p style='color:#555;'>Hora: <b>" + hora + "</b></p>" +
                    "<p style='color:#555;'>Valor: <b>R$ " + valor + "</b></p>";

            helper.setText(buildEmailTemplate("Nova Passagem", conteudo), true);
            addLogo(helper);
            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendGenericEmailToAdmins(List<String> emails, String subject, String bodyHtmlOrText) {
        for (String adminEmail : emails) {
            sendGenericEmail(adminEmail, subject, bodyHtmlOrText);
        }
    }

    public void enviarBoletoPorEmail(Boleto boleto, String emailDestinatario) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(emailDestinatario);
        helper.setSubject("AutoWay - Seu Boleto");

        String conteudo = "<h2 style='color:#333;'>Olá!</h2>" +
                "<p style='color:#555;font-size:16px;line-height:1.5;'>Segue em anexo o seu boleto referente ao período:</p>" +
                "<p><b>" + DateUtils.formatToBR(boleto.dataInicio()) + " até " + DateUtils.formatToBR(boleto.dataFim()) + "</b></p>";

        helper.setText(buildEmailTemplate("Boleto AutoWay", conteudo), true);
        addLogo(helper);

        byte[] pdfBytes = boletoPdfService.gerarPdfBoleto(boleto);
        helper.addAttachment("boleto.pdf", new ByteArrayResource(pdfBytes));

        mailSender.send(message);
    }
}
