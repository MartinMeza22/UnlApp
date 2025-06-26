package com.tallerwebi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Properties;

@EnableWebMvc
@Configuration
@ComponentScan("com.tallerwebi")
public class MailConfiguration implements WebMvcConfigurer {

    private String emailUser;

    @Bean
    public JavaMailSender getJavaMailSender() {
        // Para pruebas - solo mostrar emails en consola en lugar de enviarlos
        boolean useRealEmail = false; // Cambiar a true cuando quieras emails reales

        if (!useRealEmail) {
            return new JavaMailSender() {
                @Override
                public void send(SimpleMailMessage simpleMessage) throws MailException {
                    System.out.println("=== EMAIL ENVIADO ===");
                    System.out.println("Para: " + simpleMessage.getTo()[0]);
                    System.out.println("Asunto: " + simpleMessage.getSubject());
                    System.out.println("Mensaje: " + simpleMessage.getText());
                    System.out.println("==================");
                }
                @Override public void send(SimpleMailMessage... simpleMessages) throws MailException {
                    for (SimpleMailMessage msg : simpleMessages) { send(msg); }
                }
                // Otros métodos requeridos (implementaciones vacías)
                @Override public MimeMessage createMimeMessage() { return null; }
                @Override public MimeMessage createMimeMessage(InputStream contentStream) { return null; }
                @Override public void send(MimeMessage mimeMessage) {}
                @Override public void send(MimeMessage... mimeMessages) {}
                @Override public void send(MimeMessagePreparator mimeMessagePreparator) {}
                @Override public void send(MimeMessagePreparator... mimeMessagePreparators) {}
            };
        }

        // Configuración de Gmail
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("contacto.unlapp@gmail.com");
        mailSender.setPassword("gskqvuuhljfdezdj");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

}