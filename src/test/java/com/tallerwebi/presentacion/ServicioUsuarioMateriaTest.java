package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicioUsuarioMateriaTest {

    private ServicioUsuarioMateria servicioUsuarioMateria;
    private RepositorioUsuarioMateria repositorioUsuarioMateriaMock;
    private RepositorioUsuario repositorioUsuarioMock;
    private RepositorioMateria repositorioMateriaMock;

    private Usuario usuarioMock;
    private Materia materiaMock;
    private UsuarioMateria usuarioMateriaMock;
    private String idCarrera = "1";

    @BeforeEach
    public void init() {
        // Create mocks
        repositorioUsuarioMateriaMock = mock(RepositorioUsuarioMateria.class);
        repositorioUsuarioMock = mock(RepositorioUsuario.class);
        repositorioMateriaMock = mock(RepositorioMateria.class);

        usuarioMock = mock(Usuario.class);
        materiaMock = mock(Materia.class);
        usuarioMateriaMock = mock(UsuarioMateria.class);

        // Initialize service
        servicioUsuarioMateria = new ServicioUsuarioMateria(
            repositorioUsuarioMateriaMock,
            repositorioUsuarioMock,
            repositorioMateriaMock
        );
    }

    // ========== Tests for asignarMateria() ==========

    @Test
    public void asignarMateriaConParametrosValidosDeberiaCrearYGuardarRelacion() {
        // Preparación
        Long usuarioId = 1L;
        Long materiaId = 2L;
        Integer dificultad = 3;

        when(repositorioUsuarioMock.buscarPorId(usuarioId)).thenReturn(usuarioMock);
        when(repositorioMateriaMock.buscarPorId(materiaId)).thenReturn(materiaMock);
        when(repositorioUsuarioMateriaMock.existe(usuarioId, materiaId)).thenReturn(false);

        // Ejecución
        UsuarioMateria resultado = servicioUsuarioMateria.asignarMateria(usuarioId, materiaId, dificultad);

        // Validación
        assertThat(resultado, is(notNullValue()));
        assertThat(resultado.getUsuario(), equalTo(usuarioMock));
        assertThat(resultado.getMateria(), equalTo(materiaMock));
//        assertThat(resultado.getDificultad(), equalTo(dificultad));
        assertThat(resultado.getNota(), is(nullValue())); // Empieza cursando

        verify(repositorioUsuarioMock, times(1)).buscarPorId(usuarioId);
        verify(repositorioMateriaMock, times(1)).buscarPorId(materiaId);
        verify(repositorioUsuarioMateriaMock, times(1)).existe(usuarioId, materiaId);
        verify(repositorioUsuarioMateriaMock, times(1)).guardar(ArgumentMatchers.any(UsuarioMateria.class));
    }

    @Test
    public void asignarMateriaSinDificultadDeberiaFuncionar() {
        // Preparación
        Long usuarioId = 1L;
        Long materiaId = 2L;

        when(repositorioUsuarioMock.buscarPorId(usuarioId)).thenReturn(usuarioMock);
        when(repositorioMateriaMock.buscarPorId(materiaId)).thenReturn(materiaMock);
        when(repositorioUsuarioMateriaMock.existe(usuarioId, materiaId)).thenReturn(false);

        // Ejecución
        UsuarioMateria resultado = servicioUsuarioMateria.asignarMateria(usuarioId, materiaId);

        // Validación
        assertThat(resultado, is(notNullValue()));
        assertThat(resultado.getDificultad(), is(nullValue()));
        verify(repositorioUsuarioMateriaMock, times(1)).guardar(ArgumentMatchers.any(UsuarioMateria.class));
    }

    @Test
    public void asignarMateriaConUsuarioIdNuloDeberiaLanzarExcepcion() {
        // Ejecución y Validación
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> servicioUsuarioMateria.asignarMateria(null, 1L, 5)
        );

        assertThat(exception.getMessage(), equalTo("El ID del usuario es obligatorio"));
        verify(repositorioUsuarioMateriaMock, never()).guardar(ArgumentMatchers.any());
    }

    @Test
    public void asignarMateriaConMateriaIdNulaDeberiaLanzarExcepcion() {
        // Ejecución y Validación
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> servicioUsuarioMateria.asignarMateria(1L, null, 5)
        );

        assertThat(exception.getMessage(), equalTo("El ID de la materia es obligatorio"));
        verify(repositorioUsuarioMateriaMock, never()).guardar(ArgumentMatchers.any());
    }

    @Test
    public void asignarMateriaConUsuarioInexistenteDeberiaLanzarExcepcion() {
        // Preparación
        Long usuarioId = 999L;
        Long materiaId = 1L;

        when(repositorioUsuarioMock.buscarPorId(usuarioId)).thenReturn(null);

        // Ejecución y Validación
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> servicioUsuarioMateria.asignarMateria(usuarioId, materiaId, 5)
        );

        assertThat(exception.getMessage(), equalTo("El usuario con ID " + usuarioId + " no existe"));
        verify(repositorioUsuarioMateriaMock, never()).guardar(ArgumentMatchers.any());
    }

    @Test
    public void asignarMateriaConMateriaInexistenteDeberiaLanzarExcepcion() {
        // Preparación
        Long usuarioId = 1L;
        Long materiaId = 999L;

        when(repositorioUsuarioMock.buscarPorId(usuarioId)).thenReturn(usuarioMock);
        when(repositorioMateriaMock.buscarPorId(materiaId)).thenReturn(null);

        // Ejecución y Validación
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> servicioUsuarioMateria.asignarMateria(usuarioId, materiaId, 5)
        );

        assertThat(exception.getMessage(), equalTo("La materia con ID " + materiaId + " no existe"));
        verify(repositorioUsuarioMateriaMock, never()).guardar(ArgumentMatchers.any());
    }

    @Test
    public void asignarMateriaYaAsignadaDeberiaLanzarExcepcion() {
        // Preparación
        Long usuarioId = 1L;
        Long materiaId = 2L;

        when(repositorioUsuarioMock.buscarPorId(usuarioId)).thenReturn(usuarioMock);
        when(repositorioMateriaMock.buscarPorId(materiaId)).thenReturn(materiaMock);
        when(repositorioUsuarioMateriaMock.existe(usuarioId, materiaId)).thenReturn(true);

        // Ejecución y Validación
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> servicioUsuarioMateria.asignarMateria(usuarioId, materiaId, 5)
        );

        assertThat(exception.getMessage(), equalTo("El usuario ya tiene asignada esta materia"));
        verify(repositorioUsuarioMateriaMock, never()).guardar(ArgumentMatchers.any());
    }

    @Test
    public void asignarMateriaConDificultadInvalidaDeberiaLanzarExcepcion() {
        // Preparación
        Long usuarioId = 1L;
        Long materiaId = 2L;

        when(repositorioUsuarioMock.buscarPorId(usuarioId)).thenReturn(usuarioMock);
        when(repositorioMateriaMock.buscarPorId(materiaId)).thenReturn(materiaMock);
        when(repositorioUsuarioMateriaMock.existe(usuarioId, materiaId)).thenReturn(false);

        // Ejecución y Validación - Dificultad menor a 1
        assertThrows(IllegalArgumentException.class,
            () -> servicioUsuarioMateria.asignarMateria(usuarioId, materiaId, 0));

        // Ejecución y Validación - Dificultad mayor a 10
        assertThrows(IllegalArgumentException.class,
            () -> servicioUsuarioMateria.asignarMateria(usuarioId, materiaId, 11));
    }

    // ========== Tests for modificarMateriaCursada() ==========

    @Test
    public void modificarMateriaCursadaConParametrosValidosDeberiaActualizar() {
        // Preparación
        Long usuarioMateriaId = 1L;
        Integer nota = 8;
        Integer dificultad = 2;

        when(repositorioUsuarioMateriaMock.buscarPorId(usuarioMateriaId)).thenReturn(usuarioMateriaMock);

        // Ejecución
        UsuarioMateria resultado = servicioUsuarioMateria.modificarMateriaCursada(
            usuarioMateriaId, nota, dificultad);

        // Validación
        assertThat(resultado, equalTo(usuarioMateriaMock));
        verify(repositorioUsuarioMateriaMock, times(1)).buscarPorId(usuarioMateriaId);
        verify(repositorioUsuarioMateriaMock, times(1)).actualizar(usuarioMateriaMock);
        verify(usuarioMateriaMock, times(1)).setNota(nota);
        verify(usuarioMateriaMock, times(1)).setDificultad(dificultad);
    }

    @Test
    public void modificarMateriaCursadaConNotaNullDeberiaPermitirReiniciarCursada() {
        // Preparación
        Long usuarioMateriaId = 1L;
        when(repositorioUsuarioMateriaMock.buscarPorId(usuarioMateriaId)).thenReturn(usuarioMateriaMock);

        // Ejecución - Poner nota null explícitamente
        servicioUsuarioMateria.modificarMateriaCursada(usuarioMateriaId, null, null);

        // Validación
        verify(usuarioMateriaMock, times(1)).setNota(null);
        verify(repositorioUsuarioMateriaMock, times(1)).actualizar(usuarioMateriaMock);
    }

    @Test
    public void modificarMateriaCursadaConIdNuloDeberiaLanzarExcepcion() {
        // Ejecución y Validación
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> servicioUsuarioMateria.modificarMateriaCursada(null, 8, 5)
        );

        assertThat(exception.getMessage(), equalTo("El ID de la relación usuario-materia es obligatorio"));
    }

    @Test
    public void modificarMateriaCursadaInexistenteDeberiaLanzarExcepcion() {
        // Preparación
        Long usuarioMateriaId = 999L;
        when(repositorioUsuarioMateriaMock.buscarPorId(usuarioMateriaId)).thenReturn(null);

        // Ejecución y Validación
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> servicioUsuarioMateria.modificarMateriaCursada(usuarioMateriaId, 8, 5)
        );

        assertThat(exception.getMessage(), containsString("no existe"));
        verify(repositorioUsuarioMateriaMock, never()).actualizar(ArgumentMatchers.any());
    }

    @Test
    public void modificarMateriaCursadaConNotaInvalidaDeberiaLanzarExcepcion() {
        // Preparación
        Long usuarioMateriaId = 1L;
        when(repositorioUsuarioMateriaMock.buscarPorId(usuarioMateriaId)).thenReturn(usuarioMateriaMock);

        // Ejecución y Validación - Nota negativa
        assertThrows(IllegalArgumentException.class,
            () -> servicioUsuarioMateria.modificarMateriaCursada(usuarioMateriaId, -1, null));

        // Ejecución y Validación - Nota mayor a 10
        assertThrows(IllegalArgumentException.class,
            () -> servicioUsuarioMateria.modificarMateriaCursada(usuarioMateriaId, 11, null));
    }

    @Test
    public void modificarMateriaCursadaConDificultadInvalidaDeberiaLanzarExcepcion() {
        // Preparación
        Long usuarioMateriaId = 1L;
        when(repositorioUsuarioMateriaMock.buscarPorId(usuarioMateriaId)).thenReturn(usuarioMateriaMock);

        // Ejecución y Validación - Dificultad menor a 1
        assertThrows(IllegalArgumentException.class,
            () -> servicioUsuarioMateria.modificarMateriaCursada(usuarioMateriaId, null, 0));

        // Ejecución y Validación - Dificultad mayor a 10
        assertThrows(IllegalArgumentException.class,
            () -> servicioUsuarioMateria.modificarMateriaCursada(usuarioMateriaId, null, 11));
    }

    // ========== Tests for eliminarMateria() ==========

    @Test
    public void eliminarMateriaPorUsuarioYMateriaDeberiaEliminar() {
        // Preparación
        Long usuarioId = 1L;
        Long materiaId = 2L;

        when(repositorioUsuarioMateriaMock.buscarPorUsuarioYMateria(usuarioId, materiaId))
            .thenReturn(usuarioMateriaMock);

        // Ejecución
        servicioUsuarioMateria.eliminarMateria(usuarioId, materiaId);

        // Validación
        verify(repositorioUsuarioMateriaMock, times(1)).buscarPorUsuarioYMateria(usuarioId, materiaId);
        verify(repositorioUsuarioMateriaMock, times(1)).eliminar(usuarioMateriaMock);
    }

    @Test
    public void eliminarMateriaPorIdDeberiaEliminar() {
        // Preparación
        Long usuarioMateriaId = 1L;

        when(repositorioUsuarioMateriaMock.buscarPorId(usuarioMateriaId)).thenReturn(usuarioMateriaMock);

        // Ejecución
        servicioUsuarioMateria.eliminarMateria(usuarioMateriaId);

        // Validación
        verify(repositorioUsuarioMateriaMock, times(1)).buscarPorId(usuarioMateriaId);
        verify(repositorioUsuarioMateriaMock, times(1)).eliminar(usuarioMateriaMock);
    }

    @Test
    public void eliminarMateriaInexistenteDeberiaLanzarExcepcion() {
        // Preparación
        Long usuarioId = 1L;
        Long materiaId = 999L;

        when(repositorioUsuarioMateriaMock.buscarPorUsuarioYMateria(usuarioId, materiaId)).thenReturn(null);

        // Ejecución y Validación
        assertThrows(IllegalArgumentException.class,
            () -> servicioUsuarioMateria.eliminarMateria(usuarioId, materiaId));

        verify(repositorioUsuarioMateriaMock, never()).eliminar(ArgumentMatchers.any());
    }

    @Test
    public void eliminarMateriaConParametrosNulosDeberiaLanzarExcepcion() {
        // Ejecución y Validación - Usuario ID nulo
        assertThrows(IllegalArgumentException.class,
            () -> servicioUsuarioMateria.eliminarMateria(null, 1L));

        // Ejecución y Validación - Materia ID nula
        assertThrows(IllegalArgumentException.class,
            () -> servicioUsuarioMateria.eliminarMateria(1L, null));

        // Ejecución y Validación - UsuarioMateria ID nulo
        assertThrows(IllegalArgumentException.class,
            () -> servicioUsuarioMateria.eliminarMateria(null));
    }

    // ========== Tests for mostrarTodasLasMateriasDeUsuarios() ==========

    @Test
    public void mostrarTodasLasMateriasDeUsuariosDeberiaRetornarListaCompleta() {
        // Preparación
        List<UsuarioMateria> materiasEsperadas = Arrays.asList(usuarioMateriaMock);
        when(repositorioUsuarioMateriaMock.buscarTodas()).thenReturn(materiasEsperadas);

        // Ejecución
        List<UsuarioMateria> resultado = servicioUsuarioMateria.mostrarTodasLasMateriasDeUsuarios();

        // Validación
        assertThat(resultado, equalTo(materiasEsperadas));
        verify(repositorioUsuarioMateriaMock, times(1)).buscarTodas();
    }

    // ========== Tests for mostrarMateriasDeUsuario() ==========

    @Test
    public void mostrarMateriasDeUsuarioDeberiaRetornarMateriasDelUsuario() {
        // Preparación
        Long usuarioId = 1L;
        List<UsuarioMateria> materiasEsperadas = Arrays.asList(usuarioMateriaMock);
        when(repositorioUsuarioMateriaMock.buscarPorUsuario(idCarrera, usuarioId)).thenReturn(materiasEsperadas);

        // Ejecución
        List<UsuarioMateria> resultado = servicioUsuarioMateria.mostrarMateriasDeUsuario(idCarrera, usuarioId);

        // Validación
        assertThat(resultado, equalTo(materiasEsperadas));
        verify(repositorioUsuarioMateriaMock, times(1)).buscarPorUsuario(idCarrera, usuarioId);
    }

    @Test
    public void mostrarMateriasDeUsuarioConIdNuloDeberiaLanzarExcepcion() {
        // Ejecución y Validación
        assertThrows(IllegalArgumentException.class,
            () -> servicioUsuarioMateria.mostrarMateriasDeUsuario(idCarrera,null));
    }

    // ========== Tests for obtenerEstadisticasUsuario() ==========

    @Test
    public void obtenerEstadisticasUsuarioDeberiaCalcularCorrectamente() {
        // Preparación
        Long usuarioId = 1L;

        // Create real UsuarioMateria objects for proper testing
        UsuarioMateria cursando = new UsuarioMateria();
        cursando.setNota(null); // Cursando

        UsuarioMateria aprobada1 = new UsuarioMateria();
        aprobada1.setNota(8); // Aprobada

        UsuarioMateria aprobada2 = new UsuarioMateria();
        aprobada2.setNota(6); // Aprobada

        UsuarioMateria desaprobada = new UsuarioMateria();
        desaprobada.setNota(2); // Desaprobada

        List<UsuarioMateria> materias = Arrays.asList(cursando, aprobada1, aprobada2, desaprobada);
        when(repositorioUsuarioMateriaMock.buscarPorUsuario(idCarrera, usuarioId)).thenReturn(materias);

        // Ejecución
        ServicioUsuarioMateria.EstadisticasUsuario stats = servicioUsuarioMateria.obtenerEstadisticasUsuario(idCarrera, usuarioId);

        // Validación
        assertThat(stats.getTotalMaterias(), equalTo(4));
        assertThat(stats.getMateriasAprobadas(), equalTo(2L));
        assertThat(stats.getMateriasCursando(), equalTo(1L));
        assertThat(stats.getMateriasDesaprobadas(), equalTo(1L));


        // Promedio: (8 + 6 + 2) / 3 = 5.33 (aproximadamente)
        assertThat(stats.getPromedioNotas(), closeTo(5.33, 0.01));


        verify(repositorioUsuarioMateriaMock, times(1)).buscarPorUsuario(idCarrera, usuarioId);
    }

    @Test
    public void obtenerEstadisticasUsuarioSinMateriasDeberiaRetornarEstadisticasVacias() {
        // Preparación
        Long usuarioId = 1L;
        when(repositorioUsuarioMateriaMock.buscarPorUsuario(idCarrera, usuarioId)).thenReturn(Arrays.asList());

        // Ejecución
        ServicioUsuarioMateria.EstadisticasUsuario stats = servicioUsuarioMateria.obtenerEstadisticasUsuario(idCarrera, usuarioId);

        // Validación
        assertThat(stats.getTotalMaterias(), equalTo(0));
        assertThat(stats.getMateriasAprobadas(), equalTo(0L));
        assertThat(stats.getMateriasCursando(), equalTo(0L));
        assertThat(stats.getMateriasDesaprobadas(), equalTo(0L));
        assertThat(stats.getPromedioNotas(), equalTo(0.0));
    }

    @Test
    public void obtenerEstadisticasUsuarioConSoloMateriasCursandoDeberiaCalcularCorrectamente() {
        // Preparación
        Long usuarioId = 1L;

        UsuarioMateria cursando1 = new UsuarioMateria();
        cursando1.setNota(null);

        UsuarioMateria cursando2 = new UsuarioMateria();
        cursando2.setNota(null);

        List<UsuarioMateria> materias = Arrays.asList(cursando1, cursando2);
        when(repositorioUsuarioMateriaMock.buscarPorUsuario(idCarrera, usuarioId)).thenReturn(materias);

        // Ejecución
        ServicioUsuarioMateria.EstadisticasUsuario stats = servicioUsuarioMateria.obtenerEstadisticasUsuario(idCarrera, usuarioId);

        // Validación
        assertThat(stats.getTotalMaterias(), equalTo(2));
        assertThat(stats.getMateriasAprobadas(), equalTo(0L));
        assertThat(stats.getMateriasCursando(), equalTo(2L));
        assertThat(stats.getMateriasDesaprobadas(), equalTo(0L));
        assertThat(stats.getPromedioNotas(), equalTo(0.0)); // No hay notas
    }

    @Test
    public void obtenerEstadisticasUsuarioConIdNuloDeberiaLanzarExcepcion() {
        // Ejecución y Validación
        assertThrows(IllegalArgumentException.class,
            () -> servicioUsuarioMateria.obtenerEstadisticasUsuario(idCarrera,null));
    }

 
}