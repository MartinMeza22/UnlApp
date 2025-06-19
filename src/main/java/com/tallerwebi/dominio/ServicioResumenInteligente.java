package com.tallerwebi.dominio;

import com.tallerwebi.dominio.DTO.MateriaDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ServicioResumenInteligente {

    private static final String API_KEY = "AIzaSyAYBb9GvrXAYLcDSRzPRiY2V4Z9XZfjTOE";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    public String generarPrompt(List<MateriaDTO> materias, Double progreso) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Soy un estudiante de una tecnicatura en desarrollo web. Estas son las materias que he cursado y sus respectivas notas y dificultades:\n\n");

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

        prompt.append("\nMi progreso total en la carrera es del ").append(String.format(Locale.US, "%.2f", progreso)).append("%.\n\n");
        prompt.append("Por favor, generá un resumen académico que incluya:\n")
                .append("- Mis fortalezas.\n")
                .append("- Mis debilidades.\n")
                .append("- Qué tipo de materias me cuestan más.\n")
                .append("- Qué estrategias me sugerís para mejorar mi rendimiento académico.\n")
                .append("- Qué materias podría priorizar en el futuro según mis habilidades.\n\n")
                .append("Sé breve y concreto. Respondeme en un solo párrafo y no hagas listas. No agregues mas texto que no corresponda al resumen");

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

        RestTemplate restTemplate = new RestTemplate();
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
}
