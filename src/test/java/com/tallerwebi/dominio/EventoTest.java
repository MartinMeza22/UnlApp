package com.tallerwebi.dominio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class EventoTest {

    private Evento evento;
    private Usuario usuario;
    private Materia materia;
    private Carrera carrera;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    @BeforeEach
    public void init() {
        // Preparar datos de prueba
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@test.com");
        
        carrera = new Carrera();
        carrera.setId(1L);
        carrera.setNombre("Tecnicatura en Desarrollo Web");
        
        materia = new Materia();
        materia.setId(1L);
        materia.setNombre("Programación Web");
        materia.setCarrera(carrera);
        
        fechaInicio = LocalDateTime.now().plusDays(1);
        fechaFin = fechaInicio.plusHours(2);
        
        evento = new Evento();
    }

    // ========== Test 1: Constructor vacío ==========
    @Test
    public void constructorVacioDeberiaCrearEventoConValoresPorDefecto() {
        // Ejecución
        Evento nuevoEvento = new Evento();
        
        // Validación
        assertThat(nuevoEvento.getCompletado(), is(false));
        assertThat(nuevoEvento.getActivo(), is(true));
        assertThat(nuevoEvento.getFechaCreacion(), is(notNullValue()));
        assertThat(nuevoEvento.getFechaModificacion(), is(notNullValue()));
    }

    // ========== Test 2: Constructor con parámetros ==========
    @Test
    public void constructorConParametrosDeberiaInicializarCorrectamente() {
        // Ejecución
        Evento nuevoEvento = new Evento("Examen Final", fechaInicio, usuario);
        
        // Validación
        assertThat(nuevoEvento.getTitulo(), equalTo("Examen Final"));
        assertThat(nuevoEvento.getFechaInicio(), equalTo(fechaInicio));
        assertThat(nuevoEvento.getUsuario(), equalTo(usuario));
        assertThat(nuevoEvento.getCompletado(), is(false));
        assertThat(nuevoEvento.getActivo(), is(true));
    }

    // ========== Test 3: Evento académico vs personal ==========
    @Test
    public void deberiaDistinguirCorrectamenteEntreEventoAcademicoYPersonal() {
        // Preparación - Evento académico
        evento.setMateria(materia);
        
        // Validación - Con materia es académico
        assertThat(evento.esEventoAcademico(), is(true));
        assertThat(evento.esEventoPersonal(), is(false));
        
        // Preparación - Evento personal
        evento.setMateria(null);
        
        // Validación - Sin materia es personal
        assertThat(evento.esEventoPersonal(), is(true));
        assertThat(evento.esEventoAcademico(), is(false));
    }

    // ========== Test 4: Evento vencido ==========
    @Test
    public void eventoDeberiaEstarVencidoCuandoFechaHaPasado() {
        // Preparación - Fecha pasada
        LocalDateTime fechaPasada = LocalDateTime.now().minusDays(1);
        evento.setFechaInicio(fechaPasada);
        
        // Validación
        assertThat(evento.estaVencido(), is(true));
        
        // Preparación - Fecha futura
        LocalDateTime fechaFutura = LocalDateTime.now().plusDays(1);
        evento.setFechaInicio(fechaFutura);
        
        // Validación
        assertThat(evento.estaVencido(), is(false));
    }

    // ========== Test 5: Completar evento ==========
    @Test
    public void deberiaPoderMarcarYDesmarcarEventoComoCompletado() {
        // Preparación inicial
        evento.setCompletado(false);
        
        // Ejecución - Marcar como completado
        evento.marcarComoCompletado();
        
        // Validación
        assertThat(evento.getCompletado(), is(true));
        
        // Ejecución - Marcar como incompleto
        evento.marcarComoIncompleto();
        
        // Validación
        assertThat(evento.getCompletado(), is(false));
    }

    // ========== Test 6: Activar/Desactivar evento ==========
    @Test
    public void deberiaPoderActivarYDesactivarEvento() {
        // Preparación inicial
        evento.setActivo(true);
        
        // Ejecución - Desactivar
        evento.desactivar();
        
        // Validación
        assertThat(evento.getActivo(), is(false));
        
        // Ejecución - Activar
        evento.activar();
        
        // Validación
        assertThat(evento.getActivo(), is(true));
    }

    // ========== Test 7: Estado del evento ==========
    @Test
    public void deberiaRetornarEstadoCorrectoPorPrioridad() {
        // Test 1: INACTIVO (mayor prioridad)
        evento.setActivo(false);
        evento.setCompletado(true);
        assertThat(evento.getEstado(), equalTo("INACTIVO"));
        
        // Test 2: COMPLETADO
        evento.setActivo(true);
        evento.setCompletado(true);
        assertThat(evento.getEstado(), equalTo("COMPLETADO"));
        
        // Test 3: VENCIDO
        evento.setActivo(true);
        evento.setCompletado(false);
        evento.setFechaInicio(LocalDateTime.now().minusDays(1));
        assertThat(evento.getEstado(), equalTo("VENCIDO"));
        
        // Test 4: HOY
        evento.setFechaInicio(LocalDateTime.now().plusMinutes(2));
        assertThat(evento.getEstado(), equalTo("HOY"));
    }

    // ========== Test 8: Evento es hoy/mañana ==========
    @Test
    public void deberiaIdentificarCorrectamenteEventosDeHoyYMañana() {
        // Test - Evento de hoy
        evento.setFechaInicio(LocalDateTime.now());
        assertThat(evento.esHoy(), is(true));
        assertThat(evento.esMañana(), is(false));
        
        // Test - Evento de mañana
        evento.setFechaInicio(LocalDateTime.now().plusDays(1));
        assertThat(evento.esMañana(), is(true));
        assertThat(evento.esHoy(), is(false));
        
        // Test - Evento de otro día
        evento.setFechaInicio(LocalDateTime.now().plusDays(2));
        assertThat(evento.esHoy(), is(false));
        assertThat(evento.esMañana(), is(false));
    }

    // ========== Test 9: Nombre de materia ==========
    @Test
    public void deberiaRetornarNombreMateriaCorrecto() {
        // Test - Con materia
        evento.setMateria(materia);
        assertThat(evento.getNombreMateria(), equalTo("Programación Web"));
        
        // Test - Sin materia
        evento.setMateria(null);
        assertThat(evento.getNombreMateria(), equalTo("Sin materia"));
    }

    // ========== Test 10: Actualización de fecha de modificación ==========
    @Test
    public void deberiaActualizarFechaModificacionAlCambiarCampos() {
        // Preparación
        LocalDateTime fechaModificacionOriginal = evento.getFechaModificacion();
        
        // Pequeña pausa para asegurar diferencia en timestamp
        try { Thread.sleep(1); } catch (InterruptedException e) {}
        
        // Ejecución - Cambiar título
        evento.setTitulo("Nuevo Título");
        
        // Validación
        assertThat(evento.getFechaModificacion(), is(greaterThanOrEqualTo(fechaModificacionOriginal)));
        
        // Resetear fecha para próxima prueba
        fechaModificacionOriginal = evento.getFechaModificacion();
        try { Thread.sleep(1); } catch (InterruptedException e) {}
        
        // Ejecución - Cambiar descripción
        evento.setDescripcion("Nueva descripción");
        
        // Validación
        assertThat(evento.getFechaModificacion(), is(greaterThanOrEqualTo(fechaModificacionOriginal)));
    }

    // ========== Tests Adicionales de Casos Límite ==========
    
    @Test
    public void eventoConFechaFinAnteriorAInicioDeberiaPermitirse() {
        // Nota: La validación se hace en el servicio, no en la entidad
        LocalDateTime fechaFinAnterior = fechaInicio.minusHours(2);
        
        evento.setFechaInicio(fechaInicio);
        evento.setFechaFin(fechaFinAnterior);
        
        assertThat(evento.getFechaInicio(), equalTo(fechaInicio));
        assertThat(evento.getFechaFin(), equalTo(fechaFinAnterior));
    }

    @Test
    public void eventoConTituloLargoDeberiaSerValido() {
        String tituloLargo = "Este es un título muy largo para un evento que podría tener hasta 200 caracteres según la definición de la columna en la base de datos para verificar que funciona correctamente.";
        
        evento.setTitulo(tituloLargo);
        
        assertThat(evento.getTitulo(), equalTo(tituloLargo));
        assertThat(evento.getTitulo().length(), lessThanOrEqualTo(200));
    }

    @Test
    public void toStringDeberiaIncluirInformacionRelevante() {
        // Preparación
        evento.setId(1L);
        evento.setTitulo("Test Evento");
        evento.setFechaInicio(fechaInicio);
        evento.setUsuario(usuario);
        evento.setMateria(materia);
        
        // Ejecución
        String resultado = evento.toString();
        
        // Validación
        assertThat(resultado, containsString("id=1"));
        assertThat(resultado, containsString("titulo='Test Evento'"));
        assertThat(resultado, containsString("usuario=test@test.com"));
        assertThat(resultado, containsString("materia=Programación Web"));
    }

    @Test
    public void equalsYHashCodeDeberianSerConsistentes() {
        // Preparación
        Evento evento1 = new Evento("Examen", fechaInicio, usuario);
        Evento evento2 = new Evento("Examen", fechaInicio, usuario);
        Evento evento3 = new Evento("Parcial", fechaInicio, usuario);
        
        // Validación equals
        assertThat(evento1.equals(evento2), is(true));
        assertThat(evento1.equals(evento3), is(false));
        assertThat(evento1.equals(null), is(false));
        assertThat(evento1.equals("string"), is(false));
        
        // Validación hashCode
        assertThat(evento1.hashCode(), equalTo(evento2.hashCode()));
    }
}