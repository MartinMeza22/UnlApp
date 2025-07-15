package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.infraestructura.RepositorioResumenUsuarioImpl;
import com.tallerwebi.repositorioInterfaz.RepositorioResumenUsuario;
import com.tallerwebi.servicioInterfaz.ServicioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ServicioGeneradorResumen {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RepositorioResumenUsuarioImpl repositorioResumen;

    private static final String API_KEY = "AIzaSyAYBb9GvrXAYLcDSRzPRiY2V4Z9XZfjTOE";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;
    private final ServicioUsuario servicioUsuario;

    public ServicioGeneradorResumen(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }


    public String generarYGuardarResumen(String tema, Long usuarioId) throws UsuarioNoEncontrado {
        String prompt = "Generá un resumen claro y académico sobre el siguiente tema, asi puedo estudiar de ahí. Incluí todo lo que consideres importante. Usa texto plano, no uses listas, ni negritas, ni nada por el estilo. Podes usar varios parrafos: " + tema;

        String resumenGenerado = obtenerResumenDesdeGemini(prompt);

        Usuario usuario = servicioUsuario.obtenerUsuario(usuarioId);

        ResumenUsuario resumen = new ResumenUsuario();
        resumen.setUsuario(usuario);
        resumen.setResumenGenerado(resumenGenerado);
        resumen.setTextoOriginal(tema);

        repositorioResumen.guardar(resumen);

        return resumenGenerado;
    }

    private String obtenerResumenDesdeGemini(String prompt) {
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
            return "No se pudo generar el resumen. Intentalo más tarde.";
        }

        Map<String, Object> body = response.getBody();
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            return "No se pudo generar el resumen.";
        }

        Map<String, Object> firstCandidate = candidates.get(0);
        Map<String, Object> contentMap = (Map<String, Object>) firstCandidate.get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");

        return (String) parts.get(0).get("text");
    }

    public List<ResumenUsuario> obtenerResúmenesDeUsuario(Long usuarioId) {
        return repositorioResumen.obtenerPorUsuarioId(usuarioId);
    }
}


