package com.tallerwebi.dominio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsuarioMateriaTest {

    private UsuarioMateria usuarioMateria;
    private Usuario usuarioMock;
    private Materia materiaMock;

    @BeforeEach
    public void init() {
        usuarioMateria = new UsuarioMateria();
        usuarioMock = mock(Usuario.class);
        materiaMock = mock(Materia.class);
    }

    // ========== Constructor Tests ==========

    @Test
    public void constructorVacioDeberiaInicializarFechasYNotaNula() {
        // Ejecución
        UsuarioMateria nueva = new UsuarioMateria();

        // Validación
        assertThat(nueva.getFechaAsignacion(), is(notNullValue()));
        assertThat(nueva.getFechaModificacion(), is(notNullValue()));
        assertThat(nueva.getNota(), is(nullValue())); // null = cursando
        assertThat(nueva.estaCursando(), is(true));
    }

    @Test
    public void constructorConUsuarioYMateriaDeberiaInicializarCorrectamente() {
        // Ejecución
        UsuarioMateria nueva = new UsuarioMateria(usuarioMock, materiaMock);

        // Validación
        assertThat(nueva.getUsuario(), equalTo(usuarioMock));
        assertThat(nueva.getMateria(), equalTo(materiaMock));
        assertThat(nueva.getNota(), is(nullValue()));
        assertThat(nueva.estaCursando(), is(true));
    }

    @Test
    public void constructorConNotaDeberiaInicializarCorrectamente() {
        // Ejecución
        Integer nota = 8;
        UsuarioMateria nueva = new UsuarioMateria(usuarioMock, materiaMock, nota);

        // Validación
        assertThat(nueva.getUsuario(), equalTo(usuarioMock));
        assertThat(nueva.getMateria(), equalTo(materiaMock));
        assertThat(nueva.getNota(), equalTo(nota));
        assertThat(nueva.estaAprobada(), is(true));
    }

    // ========== Tests para Estado Calculado ==========

    @Test
    public void estaCursandoConNotaNulaDeberiaRetornarTrue() {
        // Preparación
        usuarioMateria.setNota(null);

        // Validación
        assertThat(usuarioMateria.estaCursando(), is(true));
        assertThat(usuarioMateria.estaAprobada(), is(false));
        assertThat(usuarioMateria.estaDesaprobada(), is(false));
        assertThat(usuarioMateria.getEstadoo(), equalTo("CURSANDO"));
    }

    @Test
    public void estaAprobadaConNotaMayorOIgualA4DeberiaRetornarTrue() {
        // Preparación y Ejecución - Nota exactamente 4
        usuarioMateria.setNota(4);
        assertThat(usuarioMateria.estaAprobada(), is(true));
        assertThat(usuarioMateria.getEstadoo(), equalTo("APROBADA"));

        // Preparación y Ejecución - Nota mayor a 4
        usuarioMateria.setNota(7);
        assertThat(usuarioMateria.estaAprobada(), is(true));
        assertThat(usuarioMateria.getEstadoo(), equalTo("APROBADA"));

        // Preparación y Ejecución - Nota 10
        usuarioMateria.setNota(10);
        assertThat(usuarioMateria.estaAprobada(), is(true));
        assertThat(usuarioMateria.getEstadoo(), equalTo("APROBADA"));
    }

    @Test
    public void estaDesaprobadaConNotaMenorA4DeberiaRetornarTrue() {
        // Preparación y Ejecución - Nota 3.9
        usuarioMateria.setNota(3);
        assertThat(usuarioMateria.estaDesaprobada(), is(true));
        assertThat(usuarioMateria.getEstadoo(), equalTo("DESAPROBADA"));

        // Preparación y Ejecución - Nota 0
        usuarioMateria.setNota(0);
        assertThat(usuarioMateria.estaDesaprobada(), is(true));
        assertThat(usuarioMateria.getEstadoo(), equalTo("DESAPROBADA"));

        // Preparación y Ejecución - Nota 2.5
        usuarioMateria.setNota(2);
        assertThat(usuarioMateria.estaDesaprobada(), is(true));
        assertThat(usuarioMateria.getEstadoo(), equalTo("DESAPROBADA"));
    }

    // ========== Tests para Métodos de Acción ==========

    @Test
    public void aprobarConNotaValidaDeberiaEstablecerNotaYFecha() {
        // Preparación
        Integer notaAprobacion = 8;

        // Ejecución
        usuarioMateria.aprobar(notaAprobacion);

        // Validación
        assertThat(usuarioMateria.getNota(), equalTo(notaAprobacion));
        assertThat(usuarioMateria.estaAprobada(), is(true));
        assertThat(usuarioMateria.getEstadoo(), equalTo("APROBADA"));
        assertThat(usuarioMateria.getFechaModificacion(), is(notNullValue()));
    }

    @Test
    public void aprobarConNotaMenorA4DeberiaLanzarExcepcion() {
        // Ejecución y Validación
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioMateria.aprobar(3)
        );

        assertThat(exception.getMessage(), containsString("Para aprobar, la nota debe ser mayor o igual a 4.0"));
    }

    @Test
    public void aprobarConNotaNulaDeberiaLanzarExcepcion() {
        // Ejecución y Validación
        assertThrows(IllegalArgumentException.class, () -> usuarioMateria.aprobar(null));
    }

    @Test
    public void desaprobarConNotaValidaDeberiaEstablecerNotaYFecha() {
        // Preparación
        Integer notaDesaprobacion = 3;

        // Ejecución
        usuarioMateria.desaprobar(notaDesaprobacion);

        // Validación
        assertThat(usuarioMateria.getNota(), equalTo(notaDesaprobacion));
        assertThat(usuarioMateria.estaDesaprobada(), is(true));
        assertThat(usuarioMateria.getEstadoo(), equalTo("DESAPROBADA"));
        assertThat(usuarioMateria.getFechaModificacion(), is(notNullValue()));
    }

    @Test
    public void desaprobarConNotaMayorOIgualA4DeberiaLanzarExcepcion() {
        // Ejecución y Validación - Nota 4.0
        assertThrows(IllegalArgumentException.class, () -> usuarioMateria.desaprobar(4));

        // Ejecución y Validación - Nota mayor a 4
        assertThrows(IllegalArgumentException.class, () -> usuarioMateria.desaprobar(7));
    }

    @Test
    public void desaprobarConNotaNulaDeberiaLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> usuarioMateria.desaprobar(null));
    }

    @Test
    public void reiniciarCursadaDeberiaPonerNotaNulaYActualizarFecha() {
        // Preparación - Materia con nota
        usuarioMateria.setNota(8);
        assertThat(usuarioMateria.estaAprobada(), is(true));

        // Ejecución
        usuarioMateria.reiniciarCursada();

        // Validación
        assertThat(usuarioMateria.getNota(), is(nullValue()));
        assertThat(usuarioMateria.estaCursando(), is(true));
        assertThat(usuarioMateria.getEstadoo(), equalTo("CURSANDO"));
        assertThat(usuarioMateria.getFechaModificacion(), is(notNullValue()));
    }

    // ========== Tests para Dificultad ==========

    @Test
    public void esDificilConDificultadMayorOIgualA7DeberiaRetornarTrue() {
        // Preparación y Ejecución
        usuarioMateria.setDificultad(7);
        assertThat(usuarioMateria.esDificil(), is(true));

        usuarioMateria.setDificultad(10);
        assertThat(usuarioMateria.esDificil(), is(true));

        usuarioMateria.setDificultad(8);
        assertThat(usuarioMateria.esDificil(), is(true));
    }

    @Test
    public void esDificilConDificultadMenorA7DeberiaRetornarFalse() {
        // Preparación y Ejecución
        usuarioMateria.setDificultad(6);
        assertThat(usuarioMateria.esDificil(), is(false));

        usuarioMateria.setDificultad(1);
        assertThat(usuarioMateria.esDificil(), is(false));
    }

    @Test
    public void esFacilConDificultadMenorOIgualA3DeberiaRetornarTrue() {
        // Preparación y Ejecución
        usuarioMateria.setDificultad(1);
        assertThat(usuarioMateria.esFacil(), is(true));

        usuarioMateria.setDificultad(3);
        assertThat(usuarioMateria.esFacil(), is(true));

        usuarioMateria.setDificultad(2);
        assertThat(usuarioMateria.esFacil(), is(true));
    }

    @Test
    public void esModeradaConDificultadEntre4Y6DeberiaRetornarTrue() {
        // Preparación y Ejecución
        usuarioMateria.setDificultad(4);
        assertThat(usuarioMateria.esModerada(), is(true));

        usuarioMateria.setDificultad(5);
        assertThat(usuarioMateria.esModerada(), is(true));

        usuarioMateria.setDificultad(6);
        assertThat(usuarioMateria.esModerada(), is(true));
    }

    @Test
    public void dificultadNulaDeberiaRetornarFalseEnTodosLosCasos() {
        // Preparación
        usuarioMateria.setDificultad(null);

        // Validación
        assertThat(usuarioMateria.esDificil(), is(false));
        assertThat(usuarioMateria.esFacil(), is(false));
        assertThat(usuarioMateria.esModerada(), is(false));
    }

    // ========== Tests para Getters y Setters ==========

    @Test
    public void setNotaDeberiaActualizarFechaModificacion() throws InterruptedException {
        // Preparación
        usuarioMateria.setNota(5);
        Thread.sleep(1); // Asegurar diferencia de tiempo

        // Ejecución
        usuarioMateria.setNota(7);

        // Validación
        assertThat(usuarioMateria.getNota(), equalTo(7));
        assertThat(usuarioMateria.getFechaModificacion(), is(notNullValue()));
    }

    @Test
    public void setDificultadDeberiaActualizarFechaModificacion() {
        // Ejecución
        usuarioMateria.setDificultad(8);

        // Validación
        assertThat(usuarioMateria.getDificultad(), equalTo(8));
        assertThat(usuarioMateria.getFechaModificacion(), is(notNullValue()));
    }

    // ========== Tests para toString ==========

    @Test
    public void toStringDeberiaIncluirInformacionRelevante() {
        // Preparación
        when(usuarioMock.getEmail()).thenReturn("test@unlam.com");
        when(materiaMock.getNombre()).thenReturn("Algoritmos");

        usuarioMateria.setUsuario(usuarioMock);
        usuarioMateria.setMateria(materiaMock);
        usuarioMateria.setNota(8);
        usuarioMateria.setDificultad(3);

        // Ejecución
        String resultado = usuarioMateria.toString();

        // Validación
        assertThat(resultado, containsString("test@unlam.com"));
        assertThat(resultado, containsString("Algoritmos"));
        assertThat(resultado, containsString("APROBADA"));
        assertThat(resultado, containsString("8"));
        assertThat(resultado, containsString("3"));
    }

    // ========== Tests para Equals y HashCode ==========

    @Test
    public void equalsDeberiaSerTrueParaMismoUsuarioYMateria() {
        // Preparación
        UsuarioMateria um1 = new UsuarioMateria(usuarioMock, materiaMock);
        UsuarioMateria um2 = new UsuarioMateria(usuarioMock, materiaMock);

        // Validación
        assertThat(um1.equals(um2), is(true));
        assertThat(um1.hashCode(), equalTo(um2.hashCode()));
    }

    @Test
    public void equalsDeberiaSerFalseParaDiferenteUsuarioOMateria() {
        // Preparación
        Usuario otroUsuario = new Usuario();
        Materia otraMateria = new Materia();

        UsuarioMateria um1 = new UsuarioMateria(usuarioMock, materiaMock);
        UsuarioMateria um2 = new UsuarioMateria(otroUsuario, materiaMock);
        UsuarioMateria um3 = new UsuarioMateria(usuarioMock, otraMateria);

        // Validación
        assertThat(um1.equals(um2), is(false));
        assertThat(um1.equals(um3), is(false));
    }
}