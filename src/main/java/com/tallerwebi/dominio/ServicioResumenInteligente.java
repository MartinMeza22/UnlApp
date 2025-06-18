package com.tallerwebi.dominio;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ServicioResumenInteligente {

    private static final String API_KEY = "AIzaSyAYBb9GvrXAYLcDSRzPRiY2V4Z9XZfjTOE";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    public String generarContenido(String prompt) {
        // Armar el body del request según la API
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(part));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Collections.singletonList(content));

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Entity request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // Cliente REST
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> response = restTemplate.postForEntity(ENDPOINT, entity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return "Error al llamar la API de Gemini";
        }

        // Procesar la respuesta
        Map<String, Object> body = response.getBody();

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");

        if (candidates == null || candidates.isEmpty()) {
            return "No hay candidatos en la respuesta";
        }

        Map<String, Object> firstCandidate = candidates.get(0);
        Map<String, Object> contentMap = (Map<String, Object>) firstCandidate.get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");

        return (String) parts.get(0).get("text");
    }
}