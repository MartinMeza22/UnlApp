package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicios.ServicioAnalitico;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.UsuarioMateria;
import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.repositorioInterfaz.RepositorioAnalitico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ServicioAnaliticoTest {

    private RepositorioAnalitico repositorioAnaliticoMock;
    private ServicioAnalitico servicioAnalitico;

    private Usuario usuarioMock;
    private Carrera carreraMock;
    private Materia materiaMock;
    private UsuarioMateria usuarioMateriaMock;

    private final Long USUARIO_ID = 1L;

    @BeforeEach
    public void init() {
        repositorioAnaliticoMock = mock(RepositorioAnalitico.class);
        servicioAnalitico = new ServicioAnalitico(repositorioAnaliticoMock);

        usuarioMock = mock(Usuario.class);
        carreraMock = mock(Carrera.class);
        materiaMock = mock(Materia.class);
        usuarioMateriaMock = mock(UsuarioMateria.class);

        configurarMocksBasicos();
    }

    private void configurarMocksBasicos() {
        when(usuarioMock.getNombre()).thenReturn("Juan");
        when(usuarioMock.getApellido()).thenReturn("Pérez");
        when(usuarioMock.getEmail()).thenReturn("juan@test.com");
        when(usuarioMock.getCarrera()).thenReturn(carreraMock);
        when(carreraMock.getNombre()).thenReturn("Desarrollo Web");
        when(carreraMock.getDescripcion()).thenReturn("Tecnicatura en Desarrollo Web");

        when(usuarioMateriaMock.getMateria()).thenReturn(materiaMock);
        when(usuarioMateriaMock.getEstado()).thenReturn(3); // APROBADA
        when(usuarioMateriaMock.getNota()).thenReturn(8);
        when(usuarioMateriaMock.getDificultad()).thenReturn(2); // MODERADA
        when(usuarioMateriaMock.getFechaAsignacion()).thenReturn(LocalDate.from(LocalDateTime.now()));
        when(materiaMock.getNombre()).thenReturn("Programación Básica I");
        when(materiaMock.getCuatrimestre()).thenReturn(1);
        when(materiaMock.getTipo()).thenReturn("Obligatoria");
        when(materiaMock.getCargaHoraria()).thenReturn(8);
    }

    @Test
    public void alGenerarAnaliticoCompletoDeberiaLlamarATodosLosMetodosDelRepositorio() {
        // Preparación
        List<UsuarioMateria> materias = Arrays.asList(usuarioMateriaMock);
        Object[] estadisticas = {1L, 1L, 0L, 0L, 8.0, 8L}; // totalCursadas, aprobadas, desaprobadas, cursando, promedio, horasAprobadas
        Long totalMaterias = 10L;

        when(repositorioAnaliticoMock.obtenerUsuarioConCarrera(USUARIO_ID)).thenReturn(usuarioMock);
        when(repositorioAnaliticoMock.obtenerDatosCompletosParaAnalitico(USUARIO_ID)).thenReturn(materias);
        when(repositorioAnaliticoMock.obtenerEstadisticasGenerales(USUARIO_ID)).thenReturn(estadisticas);
        when(repositorioAnaliticoMock.contarTotalMateriasDeCarrera(USUARIO_ID)).thenReturn(totalMaterias);

        // Ejecución
        Map<String, Object> resultado = servicioAnalitico.generarAnaliticoCompleto(USUARIO_ID);

        // Validación
        verify(repositorioAnaliticoMock, times(1)).obtenerUsuarioConCarrera(USUARIO_ID);
        verify(repositorioAnaliticoMock, times(1)).obtenerDatosCompletosParaAnalitico(USUARIO_ID);
        verify(repositorioAnaliticoMock, times(1)).obtenerEstadisticasGenerales(USUARIO_ID);
        verify(repositorioAnaliticoMock, times(1)).contarTotalMateriasDeCarrera(USUARIO_ID);

        assertThat(resultado, notNullValue());
        assertThat(resultado.containsKey("estudiante"), is(true));
        assertThat(resultado.containsKey("materias"), is(true));
        assertThat(resultado.containsKey("estadisticas"), is(true));
        assertThat(resultado.containsKey("metadatos"), is(true));
    }

    @Test
    public void alGenerarAnaliticoCompletoConUsuarioInexistenteDeberiaLanzarExcepcion() {
        // Preparación
        when(repositorioAnaliticoMock.obtenerUsuarioConCarrera(USUARIO_ID)).thenReturn(null);

        // Ejecución y Validación
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            servicioAnalitico.generarAnaliticoCompleto(USUARIO_ID);
        });

        assertThat(exception.getMessage(), containsString("Usuario no encontrado con ID: " + USUARIO_ID));
        verify(repositorioAnaliticoMock, times(1)).obtenerUsuarioConCarrera(USUARIO_ID);
        verify(repositorioAnaliticoMock, never()).obtenerDatosCompletosParaAnalitico(anyLong());
    }

    @Test
    public void alGenerarAnaliticoDeberiaCrearEstructuraEstudianteCorrectamente() {
        // Preparación
        List<UsuarioMateria> materias = Arrays.asList(usuarioMateriaMock);
        Object[] estadisticas = {1L, 1L, 0L, 0L, 8.0, 8L};
        Long totalMaterias = 10L;

        when(repositorioAnaliticoMock.obtenerUsuarioConCarrera(USUARIO_ID)).thenReturn(usuarioMock);
        when(repositorioAnaliticoMock.obtenerDatosCompletosParaAnalitico(USUARIO_ID)).thenReturn(materias);
        when(repositorioAnaliticoMock.obtenerEstadisticasGenerales(USUARIO_ID)).thenReturn(estadisticas);
        when(repositorioAnaliticoMock.contarTotalMateriasDeCarrera(USUARIO_ID)).thenReturn(totalMaterias);

        // Ejecución
        Map<String, Object> resultado = servicioAnalitico.generarAnaliticoCompleto(USUARIO_ID);

        // Validación
        @SuppressWarnings("unchecked")
        Map<String, Object> estudiante = (Map<String, Object>) resultado.get("estudiante");

        assertThat((String) estudiante.get("nombre"), equalTo("Juan Pérez"));
        assertThat((String) estudiante.get("email"), equalTo("juan@test.com"));
        assertThat(estudiante.containsKey("carrera"), is(true));

        @SuppressWarnings("unchecked")
        Map<String, Object> carrera = (Map<String, Object>) estudiante.get("carrera");
        assertThat((String) carrera.get("nombre"), equalTo("Desarrollo Web"));
        assertThat((String) carrera.get("descripcion"), equalTo("Tecnicatura en Desarrollo Web"));
    }

    @Test
    public void alGenerarAnaliticoDeberiaCalcularEstadisticasCorrectamente() {
        // Preparación
        List<UsuarioMateria> materias = Arrays.asList(usuarioMateriaMock);
        Object[] estadisticas = {5L, 3L, 1L, 1L, 7.5, 24L}; // 5 cursadas, 3 aprobadas, 1 desaprobada, 1 cursando, promedio 7.5, 24 horas
        Long totalMaterias = 20L;

        when(repositorioAnaliticoMock.obtenerUsuarioConCarrera(USUARIO_ID)).thenReturn(usuarioMock);
        when(repositorioAnaliticoMock.obtenerDatosCompletosParaAnalitico(USUARIO_ID)).thenReturn(materias);
        when(repositorioAnaliticoMock.obtenerEstadisticasGenerales(USUARIO_ID)).thenReturn(estadisticas);
        when(repositorioAnaliticoMock.contarTotalMateriasDeCarrera(USUARIO_ID)).thenReturn(totalMaterias);

        // Ejecución
        Map<String, Object> resultado = servicioAnalitico.generarAnaliticoCompleto(USUARIO_ID);

        // Validación
        @SuppressWarnings("unchecked")
        Map<String, Object> stats = (Map<String, Object>) resultado.get("estadisticas");

        assertThat(stats.get("materiasAprobadas"), equalTo(3L));
        assertThat(stats.get("materiasDesaprobadas"), equalTo(1L));
        assertThat(stats.get("materiasCursando"), equalTo(1L));
        assertThat(stats.get("materiasPendientes"), equalTo(15L)); // 20 - 5
        assertThat(stats.get("totalMateriasCarrera"), equalTo(20L));
        assertThat(stats.get("promedioGeneral"), equalTo(7.5));
        assertThat(stats.get("horasAcademicasAprobadas"), equalTo(24L));
        assertThat(stats.get("porcentajeAvance"), equalTo(15.0)); // (3/20) * 100
    }

    @Test
    public void alGenerarAnaliticoDeberiaIncluirMetadatosCorrectamente() {
        // Preparación
        List<UsuarioMateria> materias = Arrays.asList(usuarioMateriaMock);
        Object[] estadisticas = {1L, 1L, 0L, 0L, 8.0, 8L};
        Long totalMaterias = 10L;

        when(repositorioAnaliticoMock.obtenerUsuarioConCarrera(USUARIO_ID)).thenReturn(usuarioMock);
        when(repositorioAnaliticoMock.obtenerDatosCompletosParaAnalitico(USUARIO_ID)).thenReturn(materias);
        when(repositorioAnaliticoMock.obtenerEstadisticasGenerales(USUARIO_ID)).thenReturn(estadisticas);
        when(repositorioAnaliticoMock.contarTotalMateriasDeCarrera(USUARIO_ID)).thenReturn(totalMaterias);

        // Ejecución
        Map<String, Object> resultado = servicioAnalitico.generarAnaliticoCompleto(USUARIO_ID);

        // Validación
        @SuppressWarnings("unchecked")
        Map<String, Object> metadatos = (Map<String, Object>) resultado.get("metadatos");

        assertThat((String) metadatos.get("version"), equalTo("1.0"));
        assertThat((String) metadatos.get("institucion"), equalTo("Universidad Nacional de La Matanza"));
        assertThat((String) metadatos.get("validez"), containsString("30 días"));
        assertThat(metadatos.containsKey("fechaEmision"), is(true));
    }

    @Test
    public void alGenerarAnaliticoConDatosNulosDeberiaManejarloseCorrectamente() {
        // Preparación
        List<UsuarioMateria> materias = Arrays.asList();
        Object[] estadisticas = {null, null, null, null, null, null}; // Todos nulos
        Long totalMaterias = null;

        when(repositorioAnaliticoMock.obtenerUsuarioConCarrera(USUARIO_ID)).thenReturn(usuarioMock);
        when(repositorioAnaliticoMock.obtenerDatosCompletosParaAnalitico(USUARIO_ID)).thenReturn(materias);
        when(repositorioAnaliticoMock.obtenerEstadisticasGenerales(USUARIO_ID)).thenReturn(estadisticas);
        when(repositorioAnaliticoMock.contarTotalMateriasDeCarrera(USUARIO_ID)).thenReturn(totalMaterias);

        // Ejecución
        Map<String, Object> resultado = servicioAnalitico.generarAnaliticoCompleto(USUARIO_ID);

        // Validación
        @SuppressWarnings("unchecked")
        Map<String, Object> stats = (Map<String, Object>) resultado.get("estadisticas");

        assertThat(stats.get("materiasAprobadas"), equalTo(0L));
        assertThat(stats.get("materiasDesaprobadas"), equalTo(0L));
        assertThat(stats.get("materiasCursando"), equalTo(0L));
        assertThat(stats.get("promedioGeneral"), equalTo(0.0));
        assertThat(stats.get("totalMateriasCarrera"), equalTo(0L));
        assertThat(stats.get("porcentajeAvance"), equalTo(0.0));
    }

    @Test
    public void alGenerarAnaliticoDeberiaManejareExcepcionesInternas() {
        // Preparación
        when(repositorioAnaliticoMock.obtenerUsuarioConCarrera(USUARIO_ID)).thenReturn(usuarioMock);
        when(repositorioAnaliticoMock.obtenerDatosCompletosParaAnalitico(USUARIO_ID))
                .thenThrow(new RuntimeException("Error de base de datos"));

        // Ejecución y Validación
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            servicioAnalitico.generarAnaliticoCompleto(USUARIO_ID);
        });

        assertThat(exception.getMessage(), containsString("Error al generar el analítico académico"));
        assertThat(exception.getCause().getMessage(), equalTo("Error de base de datos"));
    }
}