package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.servicios.ServicioMateria;
import com.tallerwebi.dominio.servicios.ServicioUsuarioMateria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class ControladorPlanDeEstudiosTest {

    private ServicioMateria servicioMateria;
    private ServicioUsuarioMateria servicioUsuarioMateria;
    private ControladorPlanDeEstudios controlador;

    private HttpServletRequest request;
    private HttpSession session;
    private ModelMap modelo;
    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        servicioMateria = mock(ServicioMateria.class);
        servicioUsuarioMateria = mock(ServicioUsuarioMateria.class);
        controlador = new ControladorPlanDeEstudios(servicioMateria, servicioUsuarioMateria);

        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        modelo = new ModelMap();

        when(request.getSession()).thenReturn(session);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCarreraID(1L);
    }

    @Test
    public void queSePuedaListarMateriasDeUnaCarreraParaUnUsuario() {
        // Arrange
        when(session.getAttribute("ID")).thenReturn(1L);
        when(servicioUsuarioMateria.obtenerUsuario(1L)).thenReturn(usuario);

        Materia materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Programación Básica I");

        Materia materia2 = new Materia();
        materia2.setId(2L);
        materia2.setNombre("Informática General");

        MateriasConPromedios materiaConPromedio1 = new MateriasConPromedios(materia1, 5.0, 7.0, 10L);
        MateriasConPromedios materiaConPromedio2 = new MateriasConPromedios(materia2, 6.0, 8.0, 15L);
        
        List<MateriasConPromedios> materiasConPromedios = Arrays.asList(materiaConPromedio1, materiaConPromedio2);

        when(servicioMateria.obtenerMateriasConPromediosPorCarrera("1")).thenReturn(materiasConPromedios);

        // Act
        String vista = controlador.listarMaterias(modelo, request);

        // Assert
        assertThat(vista, is("materias"));
        assertThat(modelo.get("materias"), is(materiasConPromedios));
    }

//    @Test
//    public void queNoFalleSiElUsuarioNoTieneMaterias() {
//        when(session.getAttribute("ID")).thenReturn(1L);
//        when(servicioUsuarioMateria.obtenerUsuario(1L)).thenReturn(usuario);
//        when(servicioMateria.obtenerMateriasConPromediosPorCarrera("1")).thenReturn(List.of());
//
//        String vista = controlador.listarMaterias(modelo, request);
//
//        assertThat(vista, is("materias"));
//        assertThat((List<MateriasConPromedios>) modelo.get("materias"), empty());
//    }

//    @Test
//    public void queSeObtengaElIdDeCarreraCorrectamente() {
//        when(session.getAttribute("ID")).thenReturn(1L);
//        when(servicioUsuarioMateria.obtenerUsuario(1L)).thenReturn(usuario);
//        when(servicioMateria.obtenerMateriasConPromediosPorCarrera("1")).thenReturn(List.of());
//
//        controlador.listarMaterias(modelo, request);
//
//        verify(servicioUsuarioMateria).obtenerUsuario(1L);
//        verify(servicioMateria).obtenerMateriasConPromediosPorCarrera("1");
//    }
}
