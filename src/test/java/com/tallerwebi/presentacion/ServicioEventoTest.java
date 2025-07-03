package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Evento;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicios.ServicioEvento;
import com.tallerwebi.repositorioInterfaz.RepositorioEvento;
import com.tallerwebi.repositorioInterfaz.RepositorioMateria;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ServicioEventoTest {

    private RepositorioEvento repositorioEventoMock;
    private RepositorioUsuario repositorioUsuarioMock;
    private RepositorioMateria repositorioMateriaMock;
    private ServicioEvento servicioEvento;

    private Usuario usuarioMock;
    private Materia materiaMock;
    private Evento eventoMock;

    @BeforeEach
    public void init() {
        repositorioEventoMock = mock(RepositorioEvento.class);
        repositorioUsuarioMock = mock(RepositorioUsuario.class);
        repositorioMateriaMock = mock(RepositorioMateria.class);
        servicioEvento = new ServicioEvento(repositorioEventoMock, repositorioUsuarioMock, repositorioMateriaMock);

        usuarioMock = mock(Usuario.class);
        materiaMock = mock(Materia.class);
        eventoMock = mock(Evento.class);
    }

    @Test
    public void alCrearEventoDeberiaLlamarAlMetodoGuardarDelRepositorio() {
        // Preparación
        Evento evento = new Evento();
        evento.setTitulo("Evento de prueba");
        evento.setFechaInicio(LocalDateTime.now().plusDays(1));
        evento.setUsuario(usuarioMock);

        // Ejecución
        servicioEvento.crearEvento(evento);

        // Validación
        verify(repositorioEventoMock, times(1)).guardar(evento);
    }

    @Test
    public void alObtenerEventoPorIdDeberiaLlamarAlMetodoBuscarPorIdDelRepositorio() {
        // Preparación
        Long idBuscado = 1L;
        when(repositorioEventoMock.buscarPorId(idBuscado)).thenReturn(eventoMock);

        // Ejecución
        Evento resultado = servicioEvento.obtenerEventoPorId(idBuscado);

        // Validación
        verify(repositorioEventoMock, times(1)).buscarPorId(idBuscado);
        assertThat(resultado, equalTo(eventoMock));
    }

    @Test
    public void alActualizarEventoDeberiaLlamarAlMetodoActualizarDelRepositorio() {
        // Preparación
        Evento evento = new Evento();
        evento.setTitulo("Evento actualizado");
        evento.setFechaInicio(LocalDateTime.now().plusDays(1));
        evento.setUsuario(usuarioMock);

        // Ejecución
        servicioEvento.actualizarEvento(evento);

        // Validación
        verify(repositorioEventoMock, times(1)).actualizar(evento);
    }

    @Test
    public void alEliminarEventoDeberiaLlamarAlMetodoEliminarDelRepositorio() {
        // Preparación
        Long idEvento = 1L;

        // Ejecución
        servicioEvento.eliminarEvento(idEvento);

        // Validación
        verify(repositorioEventoMock, times(1)).eliminar(idEvento);
    }

    @Test
    public void alCrearExamenDeberiaAsociarloConUsuarioYMateriaCorrectos() {
        // Preparación
        String titulo = "Examen de Matemáticas";
        LocalDateTime fecha = LocalDateTime.now().plusDays(7);
        Long usuarioId = 1L;
        Long materiaId = 1L;
        Boolean notificarRecordatorio = true;

        when(repositorioUsuarioMock.buscarPorId(usuarioId)).thenReturn(usuarioMock);
        when(repositorioMateriaMock.buscarPorId(materiaId)).thenReturn(materiaMock);
        when(materiaMock.getNombre()).thenReturn("Matemáticas");

        // Ejecución
        servicioEvento.crearExamen(titulo, fecha, usuarioId, materiaId, notificarRecordatorio);

        // Validación
        ArgumentCaptor<Evento> captor = ArgumentCaptor.forClass(Evento.class);
        verify(repositorioEventoMock, times(1)).guardar(captor.capture());
        Evento eventoGuardado = captor.getValue();

        assertThat(eventoGuardado.getTitulo(), equalTo(titulo));
        assertThat(eventoGuardado.getFechaInicio(), equalTo(fecha));
        assertThat(eventoGuardado.getUsuario(), equalTo(usuarioMock));
        assertThat(eventoGuardado.getMateria(), equalTo(materiaMock));
        assertThat(eventoGuardado.getTipo(), equalTo("EXAMEN"));
        assertThat(eventoGuardado.getNotificarRecordatorio(), equalTo(notificarRecordatorio));
    }

    @Test
    public void alCrearExamenConUsuarioInexistenteDeberiaLanzarExcepcion() {
        // Preparación
        Long usuarioIdInexistente = 99L;
        when(repositorioUsuarioMock.buscarPorId(usuarioIdInexistente)).thenReturn(null);

        // Ejecución y Validación
        assertThrows(IllegalArgumentException.class, () -> {
            servicioEvento.crearExamen("Examen", LocalDateTime.now().plusDays(1), usuarioIdInexistente, 1L, true);
        });
        verify(repositorioEventoMock, never()).guardar(any(Evento.class));
    }

    @Test
    public void alCrearExamenConMateriaInexistenteDeberiaLanzarExcepcion() {
        // Preparación
        Long materiaIdInexistente = 99L;
        when(repositorioUsuarioMock.buscarPorId(1L)).thenReturn(usuarioMock);
        when(repositorioMateriaMock.buscarPorId(materiaIdInexistente)).thenReturn(null);

        // Ejecución y Validación
        assertThrows(IllegalArgumentException.class, () -> {
            servicioEvento.crearExamen("Examen", LocalDateTime.now().plusDays(1), 1L, materiaIdInexistente, true);
        });
        verify(repositorioEventoMock, never()).guardar(any(Evento.class));
    }

    @Test
    public void alCrearTareaDeberiaAsociarlaConUsuarioYMateriaCorrectos() {
        // Preparación
        String titulo = "Tarea de Historia";
        LocalDateTime fechaEntrega = LocalDateTime.now().plusDays(3);
        Long usuarioId = 1L;
        Long materiaId = 1L;
        String descripcion = "Ensayo sobre la Revolución Francesa";
        Boolean notificarRecordatorio = false;

        when(repositorioUsuarioMock.buscarPorId(usuarioId)).thenReturn(usuarioMock);
        when(repositorioMateriaMock.buscarPorId(materiaId)).thenReturn(materiaMock);

        // Ejecución
        servicioEvento.crearTarea(titulo, fechaEntrega, usuarioId, materiaId, descripcion, notificarRecordatorio);

        // Validación
        ArgumentCaptor<Evento> captor = ArgumentCaptor.forClass(Evento.class);
        verify(repositorioEventoMock, times(1)).guardar(captor.capture());
        Evento eventoGuardado = captor.getValue();

        assertThat(eventoGuardado.getTitulo(), equalTo(titulo));
        assertThat(eventoGuardado.getFechaInicio(), equalTo(fechaEntrega));
        assertThat(eventoGuardado.getUsuario(), equalTo(usuarioMock));
        assertThat(eventoGuardado.getMateria(), equalTo(materiaMock));
        assertThat(eventoGuardado.getTipo(), equalTo("TAREA"));
        assertThat(eventoGuardado.getDescripcion(), equalTo(descripcion));
        assertThat(eventoGuardado.getNotificarRecordatorio(), equalTo(notificarRecordatorio));
    }

    @Test
    public void alCrearSesionEstudioDeberiaAsociarlaConUsuarioYMateriaCorrectos() {
        // Preparación
        String titulo = "Sesión de Estudio Física";
        LocalDateTime fechaInicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fechaFin = fechaInicio.plusHours(2);
        Long usuarioId = 1L;
        Long materiaId = 1L;
        Boolean notificarRecordatorio = true;

        when(repositorioUsuarioMock.buscarPorId(usuarioId)).thenReturn(usuarioMock);
        when(repositorioMateriaMock.buscarPorId(materiaId)).thenReturn(materiaMock);
        when(materiaMock.getNombre()).thenReturn("Física");

        // Ejecución
        servicioEvento.crearSesionEstudio(titulo, fechaInicio, fechaFin, usuarioId, materiaId, notificarRecordatorio);

        // Validación
        ArgumentCaptor<Evento> captor = ArgumentCaptor.forClass(Evento.class);
        verify(repositorioEventoMock, times(1)).guardar(captor.capture());
        Evento eventoGuardado = captor.getValue();

        assertThat(eventoGuardado.getTitulo(), equalTo(titulo));
        assertThat(eventoGuardado.getFechaInicio(), equalTo(fechaInicio));
        assertThat(eventoGuardado.getFechaFin(), equalTo(fechaFin));
        assertThat(eventoGuardado.getUsuario(), equalTo(usuarioMock));
        assertThat(eventoGuardado.getMateria(), equalTo(materiaMock));
        assertThat(eventoGuardado.getTipo(), equalTo("ESTUDIO"));
        assertThat(eventoGuardado.getNotificarRecordatorio(), equalTo(notificarRecordatorio));
    }

    @Test
    public void alCrearEventoPersonalDeberiaAsociarloConUsuarioCorrectoYRetornarlo() {
        // Preparación
        String titulo = "Evento Personal";
        LocalDateTime fecha = LocalDateTime.now().plusDays(2);
        Long usuarioId = 1L;
        String tipo = "PERSONAL";
        Boolean notificarRecordatorio = false;

        when(repositorioUsuarioMock.buscarPorId(usuarioId)).thenReturn(usuarioMock);

        // Ejecución
        Evento resultado = servicioEvento.crearEventoPersonal(titulo, fecha, usuarioId, tipo, notificarRecordatorio);

        // Validación
        ArgumentCaptor<Evento> captor = ArgumentCaptor.forClass(Evento.class);
        verify(repositorioEventoMock, times(1)).guardar(captor.capture());
        Evento eventoGuardado = captor.getValue();

        assertThat(resultado, equalTo(eventoGuardado));
        assertThat(eventoGuardado.getTitulo(), equalTo(titulo));
        assertThat(eventoGuardado.getFechaInicio(), equalTo(fecha));
        assertThat(eventoGuardado.getUsuario(), equalTo(usuarioMock));
        assertThat(eventoGuardado.getTipo(), equalTo(tipo));
        assertThat(eventoGuardado.getNotificarRecordatorio(), equalTo(notificarRecordatorio));
    }

    @Test
    public void alCrearEventoPersonalSinTipoDeberiaAsignarTipoPersonalPorDefecto() {
        // Preparación
        Long usuarioId = 1L;
        when(repositorioUsuarioMock.buscarPorId(usuarioId)).thenReturn(usuarioMock);

        // Ejecución
        servicioEvento.crearEventoPersonal("Título", LocalDateTime.now().plusDays(1), usuarioId, null, false);

        // Validación
        ArgumentCaptor<Evento> captor = ArgumentCaptor.forClass(Evento.class);
        verify(repositorioEventoMock, times(1)).guardar(captor.capture());
        Evento eventoGuardado = captor.getValue();

        assertThat(eventoGuardado.getTipo(), equalTo("PERSONAL"));
    }

    @Test
    public void alObtenerEventosHoyDeberiaLlamarAlMetodoCorrespondienteDelRepositorio() {
        // Preparación
        Long usuarioId = 1L;
        List<Evento> eventosEsperados = Arrays.asList(eventoMock);
        when(repositorioEventoMock.buscarEventosHoy(usuarioId)).thenReturn(eventosEsperados);

        // Ejecución
        List<Evento> resultado = servicioEvento.obtenerEventosHoy(usuarioId);

        // Validación
        verify(repositorioEventoMock, times(1)).buscarEventosHoy(usuarioId);
        assertThat(resultado, equalTo(eventosEsperados));
    }

    @Test
    public void alObtenerProximosEventosDeberiaLlamarAlMetodoCorrespondienteDelRepositorio() {
        // Preparación
        Long usuarioId = 1L;
        int cantidad = 5;
        List<Evento> eventosEsperados = Arrays.asList(eventoMock);
        when(repositorioEventoMock.buscarProximosEventos(usuarioId, cantidad)).thenReturn(eventosEsperados);

        // Ejecución
        List<Evento> resultado = servicioEvento.obtenerProximosEventos(usuarioId, cantidad);

        // Validación
        verify(repositorioEventoMock, times(1)).buscarProximosEventos(usuarioId, cantidad);
        assertThat(resultado, equalTo(eventosEsperados));
    }

    @Test
    public void alMarcarComoCompletadoDeberiaActualizarElEvento() {
        // Preparación
        Long eventoId = 1L;
        when(repositorioEventoMock.buscarPorId(eventoId)).thenReturn(eventoMock);

        // Ejecución
        servicioEvento.marcarComoCompletado(eventoId);

        // Validación
        verify(repositorioEventoMock, times(1)).buscarPorId(eventoId);
        verify(eventoMock, times(1)).marcarComoCompletado();
        verify(repositorioEventoMock, times(1)).actualizar(eventoMock);
    }

    @Test
    public void alMarcarComoCompletadoEventoInexistenteNoDeberiaLanzarExcepcion() {
        // Preparación
        Long eventoIdInexistente = 99L;
        when(repositorioEventoMock.buscarPorId(eventoIdInexistente)).thenReturn(null);

        // Ejecución
        servicioEvento.marcarComoCompletado(eventoIdInexistente);

        // Validación
        verify(repositorioEventoMock, times(1)).buscarPorId(eventoIdInexistente);
        verify(repositorioEventoMock, never()).actualizar(any(Evento.class));
    }

    @Test
    public void alObtenerResumenHoyDeberiaRetornarResumenConDatosCorrectos() {
        // Preparación
        Long usuarioId = 1L;
        Evento eventoCompletado = mock(Evento.class);
        Evento eventoPendiente = mock(Evento.class);
        when(eventoCompletado.getCompletado()).thenReturn(true);
        when(eventoPendiente.getCompletado()).thenReturn(false);

        List<Evento> eventosHoy = Arrays.asList(eventoCompletado, eventoPendiente);
        when(repositorioEventoMock.buscarEventosHoy(usuarioId)).thenReturn(eventosHoy);

        // Ejecución
        ServicioEvento.ResumenEventos resumen = servicioEvento.obtenerResumenHoy(usuarioId);

        // Validación
        assertThat(resumen, notNullValue());
        assertThat(resumen.getEventosHoy(), equalTo(eventosHoy));
        assertThat(resumen.getTotalEventos(), equalTo(2));
        assertThat(resumen.getEventosCompletados(), equalTo(1));
        assertThat(resumen.getEventosPendientes(), equalTo(1));
    }

    @Test
    public void alCrearEventoSinTituloDeberiaLanzarExcepcion() {
        // Preparación
        Evento eventoSinTitulo = new Evento();
        eventoSinTitulo.setFechaInicio(LocalDateTime.now().plusDays(1));
        eventoSinTitulo.setUsuario(usuarioMock);

        // Ejecución y Validación
        assertThrows(IllegalArgumentException.class, () -> {
            servicioEvento.crearEvento(eventoSinTitulo);
        });
        verify(repositorioEventoMock, never()).guardar(any(Evento.class));
    }

    @Test
    public void alCrearEventoSinFechaInicioDeberiaLanzarExcepcion() {
        // Preparación
        Evento eventoSinFecha = new Evento();
        eventoSinFecha.setTitulo("Evento de prueba");
        eventoSinFecha.setUsuario(usuarioMock);

        // Ejecución y Validación
        assertThrows(IllegalArgumentException.class, () -> {
            servicioEvento.crearEvento(eventoSinFecha);
        });
        verify(repositorioEventoMock, never()).guardar(any(Evento.class));
    }

    @Test
    public void alCrearEventoSinUsuarioDeberiaLanzarExcepcion() {
        // Preparación
        Evento eventoSinUsuario = new Evento();
        eventoSinUsuario.setTitulo("Evento de prueba");
        eventoSinUsuario.setFechaInicio(LocalDateTime.now().plusDays(1));

        // Ejecución y Validación
        assertThrows(IllegalArgumentException.class, () -> {
            servicioEvento.crearEvento(eventoSinUsuario);
        });
        verify(repositorioEventoMock, never()).guardar(any(Evento.class));
    }

    @Test
    public void alCrearEventoConFechaFinAnteriorAFechaInicioDeberiaLanzarExcepcion() {
        // Preparación
        LocalDateTime fechaInicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fechaFin = fechaInicio.minusHours(1);

        Evento eventoConFechasIncorrectas = new Evento();
        eventoConFechasIncorrectas.setTitulo("Evento de prueba");
        eventoConFechasIncorrectas.setFechaInicio(fechaInicio);
        eventoConFechasIncorrectas.setFechaFin(fechaFin);
        eventoConFechasIncorrectas.setUsuario(usuarioMock);

        // Ejecución y Validación
        assertThrows(IllegalArgumentException.class, () -> {
            servicioEvento.crearEvento(eventoConFechasIncorrectas);
        });
        verify(repositorioEventoMock, never()).guardar(any(Evento.class));
    }
}