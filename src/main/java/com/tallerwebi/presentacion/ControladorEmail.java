package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicios.ServicioEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ControladorEmail {

    private final ServicioEmail servicioEmail;

    @Autowired
    public ControladorEmail(ServicioEmail servicioEmail) {
        this.servicioEmail = servicioEmail;
    }

    private String generarCodigo(){ // solo 4 digitos, cambiar el for para numero con mas digitos
        String codigo = "";
        for(int i = 0; i < 4; i++){
            codigo += (int)(Math.random()*10);
        }
        return codigo;
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<?> receiveRequestEmail(@RequestBody Usuario usuario) { // ResponseEntity solo para verlo en Postman

        String codigo = this.generarCodigo();
        String mensaje = "Hola estudiante \n" +
                "¡Bienvenido a UnlApp! Tu plataforma para gestionar tu carrera\n\n" +
                "Tu codigo de verificación es: " + codigo + "\n\n" +
                "Ingresa este codigo en la página de registro para completar tu verificación\n\n" +
                "Si no solicitaste este correo, puedes ignorarlo\n\n" +
                "Saludos cordiales, \n" +
                "El equipo de UnlApp"
                ;
        servicioEmail.enviarEmailAUsuario(usuario.getEmail(), "Codigo de confirmacion", mensaje);

        Map<String, String> response = new HashMap<>();
        response.put("estado", "Enviado");
        response.put("mensaje", "Codigo de verificacion enviado exitosamente a " + usuario.getEmail());

        return ResponseEntity.ok(response);
    }

}
