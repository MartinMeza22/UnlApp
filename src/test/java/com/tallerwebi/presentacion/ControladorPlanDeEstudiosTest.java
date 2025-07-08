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

    @Test
    public void queNoFalleSiElUsuarioNoTieneMaterias() {
        when(session.getAttribute("ID")).thenReturn(1L);
        when(servicioUsuarioMateria.obtenerUsuario(1L)).thenReturn(usuario);
        when(servicioMateria.obtenerMateriasConPromediosPorCarrera("1")).thenReturn(List.of());

        String vista = controlador.listarMaterias(modelo, request);

        assertThat(vista, is("materias"));
        assertThat((List<MateriasConPromedios>) modelo.get("materias"), empty());
    }

    @Test
    public void queSeObtengaElIdDeCarreraCorrectamente() {
        when(session.getAttribute("ID")).thenReturn(1L);
        when(servicioUsuarioMateria.obtenerUsuario(1L)).thenReturn(usuario);
        when(servicioMateria.obtenerMateriasConPromediosPorCarrera("1")).thenReturn(List.of());

        controlador.listarMaterias(modelo, request);

        verify(servicioUsuarioMateria).obtenerUsuario(1L);
        verify(servicioMateria).obtenerMateriasConPromediosPorCarrera("1");
    }

    @Test
    public void queSeRetornenMateriasConTextoDeDificultadCorrecto() {
        // Arrange
        when(session.getAttribute("ID")).thenReturn(1L);
        when(servicioUsuarioMateria.obtenerUsuario(1L)).thenReturn(usuario);

        Materia materiaFacil = new Materia();
        materiaFacil.setId(1L);
        materiaFacil.setNombre("Materia Fácil");

        Materia materiaModerada = new Materia();
        materiaModerada.setId(2L);
        materiaModerada.setNombre("Materia Moderada");

        Materia materiaDificil = new Materia();
        materiaDificil.setId(3L);
        materiaDificil.setNombre("Materia Difícil");

        // Dificultades: 2.0 (Fácil), 5.0 (Moderada), 8.0 (Difícil)
        MateriasConPromedios materiaConPromedioFacil = new MateriasConPromedios(materiaFacil, 2.0, 7.0, 5L);
        MateriasConPromedios materiaConPromedioModerada = new MateriasConPromedios(materiaModerada, 5.0, 7.5, 8L);
        MateriasConPromedios materiaConPromedioDificil = new MateriasConPromedios(materiaDificil, 8.0, 6.0, 12L);
        
        List<MateriasConPromedios> materiasConPromedios = Arrays.asList(
            materiaConPromedioFacil, materiaConPromedioModerada, materiaConPromedioDificil
        );

        when(servicioMateria.obtenerMateriasConPromediosPorCarrera("1")).thenReturn(materiasConPromedios);

        // Act
        String vista = controlador.listarMaterias(modelo, request);
        List<MateriasConPromedios> resultado = (List<MateriasConPromedios>) modelo.get("materias");

        // Assert
        assertThat(vista, is("materias"));
        assertThat(resultado, hasSize(3));
        
        // Verificar textos de dificultad
        assertThat(resultado.get(0).getDificultadTexto(), is("Fácil"));
        assertThat(resultado.get(1).getDificultadTexto(), is("Moderada"));
        assertThat(resultado.get(2).getDificultadTexto(), is("Difícil"));
    }

    @Test
    public void queSeAsignenClasesCSSCorrectasParaCadaNivelDeDificultad() {
        // Arrange - Crear materias con diferentes niveles de dificultad
        Materia materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Test Materia");

        // Crear MateriasConPromedios con diferentes dificultades
        MateriasConPromedios materiaFacil = new MateriasConPromedios(materia1, 1.5, 8.0, 10L);     // Fácil
        MateriasConPromedios materiaModerada = new MateriasConPromedios(materia1, 4.5, 7.0, 8L);   // Moderada
        MateriasConPromedios materiaDificil = new MateriasConPromedios(materia1, 9.0, 6.5, 15L);   // Difícil
        MateriasConPromedios materiaSinDatos = new MateriasConPromedios(materia1, null, null, 0L); // Sin datos

        // Assert - Verificar clases CSS
        assertThat(materiaFacil.getDificultadCssClass(), is("badge-facil"));
        assertThat(materiaModerada.getDificultadCssClass(), is("badge-moderada"));
        assertThat(materiaDificil.getDificultadCssClass(), is("badge-dificil"));
        assertThat(materiaSinDatos.getDificultadCssClass(), is("badge-sin-datos"));
    }

    @Test
    public void queSeCalculenCorrectamenteLosPromediosFormateados() {
        // Arrange
        Materia materia = new Materia();
        materia.setId(1L);
        materia.setNombre("Test Materia");

        // Crear MateriasConPromedios con valores decimales precisos
        MateriasConPromedios materiaConPromedios = new MateriasConPromedios(materia, 7.333333, 8.666666, 20L);
        MateriasConPromedios materiaSinDatos = new MateriasConPromedios(materia, null, null, 0L);

        // Assert - Verificar formato de promedios
        assertThat(materiaConPromedios.getPromedioDificultadFormateado(), is("7.3"));
        assertThat(materiaConPromedios.getPromedioNotaFormateado(), is("8.7"));
        assertThat(materiaConPromedios.tienePromedios(), is(true));
        
        assertThat(materiaSinDatos.getPromedioDificultadFormateado(), is("N/A"));
        assertThat(materiaSinDatos.getPromedioNotaFormateado(), is("N/A"));
        assertThat(materiaSinDatos.tienePromedios(), is(false));
    }
}
