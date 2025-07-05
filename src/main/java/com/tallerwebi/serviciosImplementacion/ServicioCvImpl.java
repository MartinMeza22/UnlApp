package com.tallerwebi.serviciosImplementacion;

import com.tallerwebi.dominio.Curriculum;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.UsuarioMateria;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.repositorioInterfaz.RepositorioCurriculum;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class ServicioCvImpl {

    private static final String API_KEY = "AIzaSyAYBb9GvrXAYLcDSRzPRiY2V4Z9XZfjTOE"; // Usás la misma que en el resumen
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-pro:generateContent?key=" + API_KEY;

    private final RestTemplate restTemplate;
    private final RepositorioCurriculum repositorioCurriculum;
    private final RepositorioUsuario repositorioUsuario;

    @Autowired
    public ServicioCvImpl(RestTemplate restTemplate,
                          RepositorioCurriculum repositorioCurriculum,
                          RepositorioUsuario repositorioUsuario) {
        this.restTemplate = restTemplate;
        this.repositorioCurriculum = repositorioCurriculum;
        this.repositorioUsuario = repositorioUsuario;
    }

    public String generarYGuardarCV(Long idUsuario, List<UsuarioMateria> materiasAprobadas) throws UsuarioNoEncontrado {
        Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
        if (usuario == null) throw new UsuarioNoEncontrado();

        String prompt = armarPrompt(usuario, materiasAprobadas);
        String respuesta = enviarPromptAGemini(prompt);

        Curriculum cv = new Curriculum();
        cv.setContenido(respuesta);
        cv.setFechaGeneracion(LocalDate.now());
        cv.setUsuario(usuario);
        repositorioCurriculum.guardar(cv);

        return respuesta;
    }

    private String armarPrompt(Usuario usuario, List<UsuarioMateria> materias) {
        StringBuilder sb = new StringBuilder();
        sb.append("Genera un currículum vitae profesional con formato Harvard para el siguiente estudiante universitario.\n\n");
        sb.append("Nombre: ").append(usuario.getNombre()).append(" ").append(usuario.getApellido()).append("\n");
        sb.append("Email: ").append(usuario.getEmail()).append("\n");
        sb.append("Carrera: ").append(usuario.getCarrera().getNombre()).append("\n");
        sb.append("Materias aprobadas:\n");

        for (UsuarioMateria m : materias) {
            sb.append("- ").append(m.getMateria().getNombre()).append("\n");
        }

        sb.append("\nEl CV debe tener un estilo profesional y limpio, en español. No uses información falsa ni inventada. No incluyas experiencia laboral si no se menciona. No escribas 'generado por IA'. Devolver solo el contenido del CV, sin encabezados adicionales.");
        return sb.toString();
    }

    private String enviarPromptAGemini(String prompt) {
        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> requestBody = Map.of("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(ENDPOINT, entity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return "Error al generar el CV.";
        }

        Map<String, Object> body = response.getBody();
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");

        if (candidates == null || candidates.isEmpty()) {
            return "Error al generar el CV.";
        }

        Map<String, Object> firstCandidate = candidates.get(0);
        Map<String, Object> contentMap = (Map<String, Object>) firstCandidate.get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");

        return (String) parts.get(0).get("text");
    }
}
