package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.DTO.MateriaDTO;
import com.tallerwebi.dominio.ServicioProgreso;
import com.tallerwebi.dominio.ServicioResumenInteligente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class ServicioResumenInteligenteTest {

    private ServicioResumenInteligente servicio;
    private RestTemplate restTemplateMock;
    private ServicioProgreso servicioProgresoMock;

    @BeforeEach
    public void init() {
        restTemplateMock = mock(RestTemplate.class);
        servicioProgresoMock = mock(ServicioProgreso.class);
        servicio = new ServicioResumenInteligente(restTemplateMock, servicioProgresoMock);
    }

    @Test
    public void queSeGenereUnResumenCorrectamenteDesdeLaApi() {
        Long usuarioId = 1L;

        // Mockear materias
        MateriaDTO materia = new MateriaDTO(1L, "Matemática", "Dificil", "APROBADA", 10, 1, true);
        when(servicioProgresoMock.materias(anyString(), anyLong())).thenReturn(List.of(materia));

        // Simular respuesta de la API
        Map<String, Object> body = new HashMap<>();
        body.put("candidates", List.of(Map.of(
                "content", Map.of("parts", List.of(Map.of("text", "Este es un resumen simulado.")))
        )));

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplateMock.postForEntity(
                anyString(),
                org.mockito.ArgumentMatchers.<HttpEntity<Map<String, Object>>>any(),
                eq(Map.class)
        )).thenReturn(responseEntity);

        String prompt = servicio.generarPrompt(List.of(materia), 90.0);
        String resumen = servicio.generarResumenDesdePrompt(prompt);

        assertThat(resumen, is(notNullValue()));
        assertThat(resumen.trim(), not(isEmptyString()));
    }

}
