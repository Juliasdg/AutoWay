package br.com.fatec.autoway.application.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendHtmlConfirmationEmail(String to, String name, String link) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Confirmação de Cadastro");

            String content = "<!DOCTYPE html>" +
                    "<html lang='pt-BR'>" +
                    "<head><meta charset='UTF-8'><title>Confirmação de Conta</title></head>" +
                    "<body style='font-family:Arial,sans-serif;background-color:#f4f6f8;margin:0;padding:0;'>" +
                    "<div style='max-width:600px;margin:30px auto;background:#ffffff;border-radius:8px;" +
                    "box-shadow:0 4px 12px rgba(0,0,0,0.1);overflow:hidden;'>" +

                    "<div style='background:#4E0967;padding:20px;text-align:center;'>" +
                    "<img src='cid:logo' alt='Logo' style='width:307px;height:164px;'/>" +
                    "</div>" +

                    "<div style='padding:30px 20px;text-align:center;'>" +
                    "<h2 style='color:#333;'>Olá " + name + ",</h2>" +
                    "<p style='color:#555;font-size:16px;line-height:1.5;'>Obrigado por se cadastrar! " +
                    "Por favor, confirme seu e-mail clicando no botão abaixo:</p>" +
                    "<a href='" + link + "' style='display:inline-block;padding:12px 25px;margin:20px 0;" +
                    "color:white;background-color:#28a745;text-decoration:none;border-radius:5px;font-weight:bold;'>" +
                    "Confirmar Conta</a>" +
                    "<p style='color:#888;font-size:14px;'>Se você não se cadastrou, apenas ignore este e-mail.</p>" +
                    "</div>" +

                    "<div style='background:#f4f6f8;padding:15px;text-align:center;font-size:12px;color:#999;'>" +
                    "© 2025 Autoway. Todos os direitos reservados." +
                    "</div>" +

                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(content, true);

            ClassPathResource logo = new ClassPathResource("logo.png");
            helper.addInline("logo", logo);

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

            String content = "<!DOCTYPE html>" +
                    "<html lang='pt-BR'>" +
                    "<head><meta charset='UTF-8'><title>Redefinição de Senha</title></head>" +
                    "<body style='font-family:Arial,sans-serif;background-color:#f4f6f8;margin:0;padding:0;'>" +
                    "<div style='max-width:600px;margin:30px auto;background:#ffffff;border-radius:8px;" +
                    "box-shadow:0 4px 12px rgba(0,0,0,0.1);overflow:hidden;'>" +
                    "<div style='background:#4E0967;padding:20px;text-align:center;'>" +
                    "<img src='cid:logo' alt='Logo' style='width:307px;height:164px;'/>" +
                    "</div>" +
                    "<div style='padding:30px 20px;text-align:center;'>" +
                    "<h2 style='color:#333;'>Olá " + name + ",</h2>" +
                    "<p style='color:#555;font-size:16px;line-height:1.5;'>" +
                    "Você solicitou a redefinição de senha. Seu código é:</p>" +
                    "<h1 style='color:#4E0967;'>" + code + "</h1>" +
                    "<p style='color:#888;font-size:14px;'>Se você não solicitou, apenas ignore este e-mail.</p>" +
                    "</div>" +
                    "<div style='background:#f4f6f8;padding:15px;text-align:center;font-size:12px;color:#999;'>" +
                    "© 2025 Autoway. Todos os direitos reservados." +
                    "</div>" +
                    "</div></body></html>";

            helper.setText(content, true);

            ClassPathResource logo = new ClassPathResource("logo.png");
            helper.addInline("logo", logo);

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
            helper.setText(bodyHtmlOrText, true); // html
            // se quiser incluir a logo inline:
            ClassPathResource logo = new ClassPathResource("logo.png");
            if (logo.exists()) {
                helper.addInline("logo", logo);
            }
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendPassagemEmail(String to, String placa, String data, String hora, Double valor) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Nova Passagem Registrada");

            String content = "<html><body>" +
                    "<h3>Nova Passagem Registrada</h3>" +
                    "<p>Placa: " + placa + "</p>" +
                    "<p>Data: " + data + "</p>" +
                    "<p>Hora: " + hora + "</p>" +
                    "<p>Valor: R$ " + valor + "</p>" +
                    "</body></html>";

            helper.setText(content, true);

            // opcional: adicionar logo inline
            ClassPathResource logo = new ClassPathResource("logo.png");
            if (logo.exists()) {
                helper.addInline("logo", logo);
            }

            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


}
