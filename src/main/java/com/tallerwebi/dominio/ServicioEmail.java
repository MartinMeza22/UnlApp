package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.CodigoVerificacionExpirado;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Transactional
public class ServicioEmail {

    private final JavaMailSender mailSender;
    private final RepositorioLogin repositorioLogin;
    private final RepositorioCodigoVerificacion repositorioCodigoVerificacion;
    private final RepositorioUsuario repositorioUsuario;

    public ServicioEmail(JavaMailSender mailSender, RepositorioLogin repositorioLogin, RepositorioCodigoVerificacion repositorioCodigoVerificacion, RepositorioUsuario repositorioUsuario) {
        this.mailSender = mailSender;
        this.repositorioLogin = repositorioLogin;
        this.repositorioCodigoVerificacion = repositorioCodigoVerificacion;
        this.repositorioUsuario = repositorioUsuario;
    }

    public String generarCodigoVerificacion(){
        Random random = new Random();
        Integer codigo = 1000 + random.nextInt(9000);
        return String.valueOf(codigo);
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

    public void guardarYEnviarCodigoDeVerificacion(Usuario usuario) {
        String codigo = this.generarCodigoVerificacion();
        LocalDateTime fechaExpiracion = LocalDateTime.now().plusMinutes(15);

        CodigoVerificacion codigoExistente =this.repositorioCodigoVerificacion.buscarPorUsuario(usuario);
        if(codigoExistente!=null){
            this.repositorioCodigoVerificacion.eliminar(codigoExistente);
        }

        CodigoVerificacion nuevoCodigo = new CodigoVerificacion(usuario, codigo,fechaExpiracion );
        this.repositorioCodigoVerificacion.guardar(nuevoCodigo);
        enviarCodigoAOutlook(usuario.getEmail(), codigo);
    }

    public void verificarCodigo(Long idUsuario, String codigo){
        Usuario usuario = this.repositorioUsuario.buscarPorId(idUsuario);
        if(usuario == null){
            throw new IllegalArgumentException("El usuario no encontrado");
        }

        CodigoVerificacion codigoVerificacion = this.repositorioCodigoVerificacion.buscarPorUsuarioYCodigo(usuario, codigo);

        if(codigoVerificacion.estaExpirado()){
            this.repositorioCodigoVerificacion.eliminar(codigoVerificacion);
            // Aca tambien deberia eliminar al usuario ya que nunca se activo su cuenta
            throw new CodigoVerificacionExpirado("El codigo de verificacion ha expirado. Registrese nuevamente");
        }

        usuario.setActivo(true);
        this.repositorioUsuario.modificar(usuario);
        this.repositorioCodigoVerificacion.eliminar(codigoVerificacion);

    }

}
