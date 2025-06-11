package com.tallerwebi.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ServicioEmail {

    private final JavaMailSender mailSender;

    public ServicioEmail(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCodigoAOutlook(String email, String codigo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();

        mensaje.setFrom("contacto.unlapp@gmail.com");
        mensaje.setTo(email);
        mensaje.setSubject("Codigo Verificación");
        mensaje.setText("Hola estudiante, \n" +
                "¡Bienvenido a UnlApp! Tu plataforma para gestionar tu carrera\n\n" +
                "Tu codigo de verificación es: " + codigo + "\n\n" +
                "Ingresa este codigo en la página de registro para completar tu verificación\n\n" +
                "Si no solicitaste este correo, puedes ignorarlo\n\n" +
                "Saludos cordiales, \n" +
                "El equipo de UnlApp"
        );

        try {
            mailSender.send(mensaje);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void enviarEmailAUsuario(String email, String asunto, String mensajePersonalizado) {
        SimpleMailMessage mensaje = new SimpleMailMessage();

        mensaje.setFrom("contacto.unlapp@gmail.com");
        mensaje.setTo(email);
        mensaje.setSubject(asunto);
        mensaje.setText(mensajePersonalizado);

        try {
            mailSender.send(mensaje);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
