/*
package com.tallerwebi.presentacion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.MateriasWrapper;
import com.tallerwebi.dominio.ServicioMateria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource; // Necesario para la prueba

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class ServicioMateriaTest {

    @InjectMocks
    private ServicioMateria servicioMateria;

    @Mock
    private ObjectMapper mockObjectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void deberiaObtenerMateriasYLaPrimerMateriaDeberiaTenerElCodigoEsperado() throws Exception {
        ClassPathResource testResource = new ClassPathResource("materias_cuatrimestre.json");
        InputStream testInputStream = testResource.getInputStream();

        Materia materia1 = new Materia();
        materia1.setCodigo("2619");
        materia1.setNombre("Programación Básica I");

        Materia materia2 = new Materia();
        materia2.setCodigo("2620");
        materia2.setNombre("Informática General");

        MateriasWrapper expectedWrapper = new MateriasWrapper();
        expectedWrapper.setMaterias(List.of(materia1, materia2));

        when(mockObjectMapper.readValue(org.mockito.ArgumentMatchers.any(InputStream.class),
                org.mockito.ArgumentMatchers.eq(MateriasWrapper.class)))
                .thenReturn(expectedWrapper);

        List<Materia> materiasObtenidas = servicioMateria.obtenerMaterias();

        assertNotNull(materiasObtenidas);
        assertEquals(2, materiasObtenidas.size());
        assertEquals("2619", materiasObtenidas.get(0).getCodigo());
        assertEquals("Programación Básica I", materiasObtenidas.get(0).getNombre());
    }

    @Test
    public void deberiaManejarErrorDeLecturaDeArchivo() throws Exception {
        when(mockObjectMapper.readValue(org.mockito.ArgumentMatchers.any(InputStream.class),
                org.mockito.ArgumentMatchers.eq(MateriasWrapper.class)))
                .thenThrow(new java.io.IOException("Error de lectura simulado"));

        try {
            servicioMateria.obtenerMaterias();
            org.junit.jupiter.api.Assertions.fail("Se esperaba una RuntimeException"); // Si llega aquí, el test falla
        } catch (RuntimeException e) {
            assertNotNull(e.getCause(), "La causa de la excepción no debería ser nula");
            assertEquals(java.io.IOException.class, e.getCause().getClass(), "La causa debería ser IOException");
            assertEquals("Error de lectura simulado", e.getCause().getMessage(), "El mensaje de error no coincide");
        }
    }
}
*/