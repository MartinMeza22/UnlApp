package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicios.ServicioAnalitico;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class ControladorAnalitico {

    @Autowired
    ServicioAnalitico servicioAnalitico;

    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("/analitico-academico")
    @ResponseBody
    public ResponseEntity<String> generarAnalitico(HttpSession session) {
        try {
            // Validar sesión del usuario
            Long usuarioId = (Long) session.getAttribute("ID");

            if (usuarioId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .header("Content-Type", "application/json")
                        .body(crearErrorJson("Usuario no autenticado"));
            }

            // Generar el analítico completo
            Map<String, Object> analiticoCompleto = servicioAnalitico.generarAnaliticoCompleto(usuarioId);

            // Convertir a JSON usando Jackson
            String jsonResponse = objectMapper.writeValueAsString(analiticoCompleto);

            // Log para debugging (opcional)
            System.out.println("Analítico generado exitosamente para usuario: " + usuarioId);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(jsonResponse);

        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación al generar analítico: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Content-Type", "application/json")
                    .body(crearErrorJson("Datos inválidos: " + e.getMessage()));

        } catch (RuntimeException e) {
            System.err.println("Error de negocio al generar analítico: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("Content-Type", "application/json")
                    .body(crearErrorJson(e.getMessage()));

        } catch (Exception e) {
            System.err.println("Error interno al generar analítico: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body(crearErrorJson("Error interno del servidor. Por favor, inténtelo más tarde."));
        }
    }

    /**
     * Método auxiliar para crear mensajes de error en formato JSON
     */
    private String crearErrorJson(String mensaje) {
        return String.format("{\"error\":\"%s\",\"timestamp\":\"%s\"}",
                mensaje,
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
    }
}