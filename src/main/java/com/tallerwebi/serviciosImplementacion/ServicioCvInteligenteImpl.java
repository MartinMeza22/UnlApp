package com.tallerwebi.serviciosImplementacion;

import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.UsuarioMateria;
import com.tallerwebi.dominio.servicios.ServicioUsuarioMateria;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import com.tallerwebi.servicioInterfaz.ServicioCvInteligente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicioCvInteligenteImpl implements ServicioCvInteligente {

    private final ServicioUsuarioMateria servicioUsuarioMateria;
    private final RepositorioUsuario repositorioUsuario;
    private final RestTemplate restTemplate;

    @Autowired
    public ServicioCvInteligenteImpl(ServicioUsuarioMateria servicioUsuarioMateria,
                                     RepositorioUsuario repositorioUsuario,
                                     RestTemplate restTemplate) {
        this.servicioUsuarioMateria = servicioUsuarioMateria;
        this.repositorioUsuario = repositorioUsuario;
        this.restTemplate = restTemplate;
    }

    @Override
    public String generarCv(Long idUsuario) {
        // Buscar usuario
        Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
        if (usuario == null) {
            return "Usuario no encontrado";
        }

        // Obtener idCarrera desde usuario (asumiendo que usuario tiene carrera y su id es String)
        String idCarrera = null;
        if (usuario.getCarrera() != null && usuario.getCarrera().getId() != null) {
            idCarrera = String.valueOf(usuario.getCarrera().getId());
        } else {
            return "Usuario no tiene carrera asignada";
        }

        // Obtener materias aprobadas del usuario
        List<UsuarioMateria> materias = servicioUsuarioMateria.mostrarMateriasDeUsuario(idCarrera, idUsuario);

        // Construir el prompt para Gemini
        String prompt = construirPrompt(usuario, materias);

        // --- Llamada a Gemini ---
        String apiKey = "AIzaSyAVbRk7iTDCEQnYRlvOb03xl00l-UetKxc";
        String geminiEndpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

        String requestBody = String.format(
                "{"
                        + "\"contents\": ["
                        + "  {"
                        + "    \"parts\": ["
                        + "      {"
                        + "        \"text\": \"%s\""
                        + "      }"
                        + "    ]"
                        + "  }"
                        + "]"
                        + "}", prompt.replace("\"", "\\\"") // Escapar comillas en prompt
        );

        var headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Type", "application/json");

        var entity = new org.springframework.http.HttpEntity<>(requestBody, headers);

        var response = restTemplate.postForEntity(geminiEndpoint, entity, String.class);

        try {
            String respuesta = response.getBody();

            if (respuesta == null) {
                return "Respuesta vacía de Gemini.";
            }

            // Parsear con Jackson
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            var jsonNode = mapper.readTree(respuesta);

            // Navegar por la estructura
            var candidates = jsonNode.path("candidates");
            if (candidates.isMissingNode() || !candidates.isArray() || candidates.isEmpty()) {
                return "Respuesta inválida de Gemini: sin candidatos.";
            }

            var text = candidates.get(0).path("content").path("parts").get(0).path("text").asText(null);
            if (text == null) {
                return "Respuesta inválida de Gemini: sin texto.";
            }

            return text;

        } catch (Exception e) {
            return "Error al procesar la respuesta de Gemini: " + e.getMessage();
        }


    }


    private String construirPrompt(Usuario usuario, List<UsuarioMateria> materias) {
        String nombre = usuario.getNombre() + " " + usuario.getApellido();
        String email = usuario.getEmail();
        String carrera = usuario.getCarrera() != null ? usuario.getCarrera().getNombre() : "Carrera no especificada";

        String materiasStr = materias.stream()
                .map(um -> "- " + um.getMateria().getNombre())
                .collect(Collectors.joining("\n"));

        return String.format(
                "Generá el contenido completo y final de un CV académico estilo Harvard para el siguiente usuario.\n"
                        + "El CV debe estar redactado en español, con formato profesional, incluyendo únicamente las secciones relevantes y disponibles.\n"
                        + "Al final coloca una seccion de objetivo y que diga Insertarme en el mercado laboral que tenga que ver con mi carrera e implementar todo lo aprendido o algo asi para hacerlo mas largo.\n"
                        + "Solo devolvé el contenido del CV, listo para ser usado directamente (como el cuerpo de un PDF o documento final).\n\n"
                        + "Datos del usuario:\n"
                        + "Nombre: %s\n"
                        + "Email: %s\n"
                        + "Carrera: %s\n\n"
                        + "Materias aprobadas:\n"
                        + "%s",
                nombre, email, carrera, materiasStr
        );



    }
}
