package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.DTO.MateriaDTO;
import com.tallerwebi.dominio.RepositorioResumenInteligente;
import com.tallerwebi.dominio.ResumenInteligente;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.infraestructura.RepositorioResumenInteligenteImpl;
import com.tallerwebi.servicioInterfaz.ServicioUsuario;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Service
public class ServicioResumenInteligente {

    private static final String API_KEY = "AIzaSyAYBb9GvrXAYLcDSRzPRiY2V4Z9XZfjTOE";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    private final RestTemplate restTemplate;
    private final ServicioUsuario servicioUsuario;
    private final RepositorioResumenInteligente repositorioResumenInteligente;


    public ServicioResumenInteligente(RestTemplate restTemplate, ServicioUsuario servicioUsuario, RepositorioResumenInteligente repositorioResumenInteligente) {
        this.restTemplate = restTemplate;
        this.servicioUsuario = servicioUsuario;
        this.repositorioResumenInteligente = repositorioResumenInteligente;
    }

    public String generarPrompt(List<MateriaDTO> materias, Double progreso) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Soy un estudiante universitario. Estas son las materias que he cursado y sus respectivas notas y dificultades:\n\n");

        for (MateriaDTO mat : materias) {
            if ("APROBADA".equalsIgnoreCase(mat.getEstado())) {
                prompt.append("- ").append(mat.getNombre()).append(": nota ")
                        .append(mat.getNota()).append(", dificultad ")
                        .append(mat.getDificultad()).append(".\n");
            }
        }

        prompt.append("\nMaterias desaprobadas:\n");
        for (MateriaDTO mat : materias) {
            if ("DESAPROBADA".equalsIgnoreCase(mat.getEstado())) {
                prompt.append("- ").append(mat.getNombre()).append(": nota ")
                        .append(mat.getNota()).append(", dificultad ")
                        .append(mat.getDificultad()).append(".\n");
            }
        }

        prompt.append("\nActualmente estoy cursando:\n");
        for (MateriaDTO mat : materias) {
            if ("CURSANDO".equalsIgnoreCase(mat.getEstado())) {
                prompt.append("- ").append(mat.getNombre())
                        .append(" (dificultad estimada: ")
                        .append(mat.getDificultad() != null ? mat.getDificultad() : "no definida")
                        .append(")\n");
            }
        }

        prompt.append("\nMi progreso total en la carrera es del ")
                .append(String.format(Locale.US, "%.2f", progreso)).append("%.\n\n");
        prompt.append("Por favor, generá un resumen académico que incluya:\n")
                .append("- Mis fortalezas.\n")
                .append("- Mis debilidades.\n")
                .append("- Qué tipo de materias me cuestan más.\n")
                .append("- Qué estrategias me sugerís para mejorar mi rendimiento académico.\n")
                .append("- Qué materias podría priorizar en el futuro según mis habilidades.\n\n")
                .append("Sé breve y concreto. Respondeme en un solo párrafo y no hagas listas. No agregues más texto que no corresponda al resumen.");

        return prompt.toString();
    }

    public String generarResumenDesdePrompt(String prompt) {
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(part));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Collections.singletonList(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(ENDPOINT, entity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return "Error al generar el resumen.";
        }

        Map<String, Object> body = response.getBody();
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");

        if (candidates == null || candidates.isEmpty()) {
            return "Error al generar el resumen";
        }

        Map<String, Object> firstCandidate = candidates.get(0);
        Map<String, Object> contentMap = (Map<String, Object>) firstCandidate.get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");

        return (String) parts.get(0).get("text");
    }

    public void guardarResumen(String resumen, Usuario usuario) {
        ResumenInteligente entidad = new ResumenInteligente();
        entidad.setResumen(resumen);
        entidad.setFechaGeneracion(LocalDate.now());
        entidad.setUsuario(usuario);

        repositorioResumenInteligente.guardar(entidad);
    }

    public String generarYGuardarResumen(Long usuarioId, List<MateriaDTO> materias, Double progreso) throws UsuarioNoEncontrado {
        Usuario usuario = servicioUsuario.obtenerUsuario(usuarioId);
        String prompt = generarPrompt(materias, progreso);
        String resumen = generarResumenDesdePrompt(prompt);
        guardarResumen(resumen, usuario);
        return resumen;
    }

    public String generarResumenHistorico(Long usuarioId) throws UsuarioNoEncontrado {
        Usuario usuario = servicioUsuario.obtenerUsuario(usuarioId);
        List<ResumenInteligente> resumenes = repositorioResumenInteligente.obtenerPorUsuario(usuario);

        if (resumenes.isEmpty()) {
            return "Todavía no hay suficientes resúmenes para analizar tu historial.";
        }

        StringBuilder historial = new StringBuilder("Soy un estudiante universitario. A continuación se listan los resúmenes académicos generados en distintos momentos de mi carrera:\n\n");

        for (ResumenInteligente r : resumenes) {
            historial.append("- ").append(r.getFechaGeneracion()).append(": ").append(r.getResumen()).append("\n\n");
        }

        historial.append("Basándote en los resúmenes anteriores, ¿qué podrías decir sobre mi evolución académica, mis patrones de conducta, mis fortalezas y debilidades sostenidas en el tiempo, y qué consejo me darías mirando mi historia como estudiante? Sé concretoy usa un solo párrafo. No agregues más texto que no corresponda al resumen.");

        return generarResumenDesdePrompt(historial.toString());
    }



}
