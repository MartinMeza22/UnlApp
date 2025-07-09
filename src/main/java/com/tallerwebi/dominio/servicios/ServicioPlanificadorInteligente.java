package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.DTO.MateriaDTO;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Service
public class ServicioPlanificadorInteligente {

    @Autowired
    private ServicioMateria servicioMateria;

    private static final String API_KEY = "AIzaSyAYBb9GvrXAYLcDSRzPRiY2V4Z9XZfjTOE";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    @Autowired
    private ServicioProgreso servicioProgreso;

    @Autowired
    private RestTemplate restTemplate;

    public String generarPlanificacionInteligente(String carreraId, Long idUsuario) throws UsuarioNoEncontrado {
        List<Materia> materiasCarrera = servicioMateria.obtenerMateriasPorCarrera(carreraId);

        List<MateriaDTO> materiasCursadas = servicioProgreso.materias(carreraId, idUsuario);

        String prompt = construirPromptConMaterias(materiasCarrera, materiasCursadas);

        return generarPlanificacionDesdePrompt(prompt);
    }


    private String construirPromptConMaterias(List<Materia> materiasCarrera, List<MateriaDTO> materiasCursadas) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Sos un asistente académico. Estas son TODAS las materias de la carrera, con su cuatrimestre y correlativas:\n\n");

        for (Materia m : materiasCarrera) {
            prompt.append("- ").append(m.getNombre())
                    .append(" (cuatrimestre: ").append(m.getCuatrimestre()).append(")");

            if (m.getCorrelativa1() != null) prompt.append(", correlativa 1: ").append(m.getCorrelativa1());
            if (m.getCorrelativa2() != null) prompt.append(", correlativa 2: ").append(m.getCorrelativa2());
            if (m.getCorrelativa3() != null) prompt.append(", correlativa 3: ").append(m.getCorrelativa3());
            if (m.getCorrelativa4() != null) prompt.append(", correlativa 4: ").append(m.getCorrelativa4());
            if (m.getCorrelativa5() != null) prompt.append(", correlativa 5: ").append(m.getCorrelativa5());
            if (m.getCorrelativa6() != null) prompt.append(", correlativa 6: ").append(m.getCorrelativa6());

            prompt.append(".\n");
        }

        prompt.append("\nMaterias ya cursadas por el estudiante:\n");

        for (MateriaDTO mat : materiasCursadas) {
            prompt.append("- ").append(mat.getNombre()).append(": estado ")
                    .append(mat.getEstado()).append(", nota ")
                    .append(mat.getNota()).append(", dificultad ")
                    .append(mat.getDificultad()).append(".\n");
        }


        prompt.append("\nCon esta información, armá una planificación inteligente de materias para cursar en el futuro. Tené en cuenta las materias ya aprobadas, las correlativas necesarias y la dificultad. No incluyas materias ya aprobadas en el plan. Respondé SOLO con el plan, no agregues ningún texto extra");

        return prompt.toString();
    }


    public String generarPlanificacionDesdePrompt(String prompt) {
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
            return "Error al generar la planificación.";
        }

        Map<String, Object> body = response.getBody();
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");

        if (candidates == null || candidates.isEmpty()) {
            return "Error al generar la planificación.";
        }

        Map<String, Object> firstCandidate = candidates.get(0);
        Map<String, Object> contentMap = (Map<String, Object>) firstCandidate.get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");

        return (String) parts.get(0).get("text");
    }

}

