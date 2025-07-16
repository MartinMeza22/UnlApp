package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.DTO.UsuarioYMateriasDTO;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.UsuarioMateria;
import com.tallerwebi.dominio.servicios.ServicioUsuarioMateria;
import com.tallerwebi.infraestructura.RepositorioUsuarioMateriaImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsuarioMateriaTest {

    private UsuarioMateria usuarioMateriaMock;
    private Usuario usuarioMock;
    private Materia materiaMock;
    private ServicioUsuarioMateria servicioUsuarioMateriaMock;
    private RepositorioUsuarioMateriaImpl repositorioUsuarioMateriaMock;
    private UsuarioYMateriasDTO dto;
    @BeforeEach
    public void init() {
        usuarioMateriaMock = new UsuarioMateria();
        usuarioMock = mock(Usuario.class);
        materiaMock = mock(Materia.class);
        servicioUsuarioMateriaMock = mock(ServicioUsuarioMateria.class);
        repositorioUsuarioMateriaMock = mock(RepositorioUsuarioMateriaImpl.class);
        dto = mock(UsuarioYMateriasDTO.class);
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
        usuarioMateriaMock.setNota(null);

        // Validación
        assertThat(usuarioMateriaMock.estaCursando(), is(true));
        assertThat(usuarioMateriaMock.estaAprobada(), is(false));
        assertThat(usuarioMateriaMock.estaDesaprobada(), is(false));
        assertThat(usuarioMateriaMock.getEstadoo(), equalTo("CURSANDO"));
    }

    @Test
    public void estaAprobadaConNotaMayorOIgualA4DeberiaRetornarTrue() {
        // Preparación y Ejecución - Nota exactamente 4
        usuarioMateriaMock.setNota(4);
        assertThat(usuarioMateriaMock.estaAprobada(), is(true));
        assertThat(usuarioMateriaMock.getEstadoo(), equalTo("APROBADA"));

        // Preparación y Ejecución - Nota mayor a 4
        usuarioMateriaMock.setNota(7);
        assertThat(usuarioMateriaMock.estaAprobada(), is(true));
        assertThat(usuarioMateriaMock.getEstadoo(), equalTo("APROBADA"));

        // Preparación y Ejecución - Nota 10
        usuarioMateriaMock.setNota(10);
        assertThat(usuarioMateriaMock.estaAprobada(), is(true));
        assertThat(usuarioMateriaMock.getEstadoo(), equalTo("APROBADA"));
    }

    @Test
    public void estaDesaprobadaConNotaMenorA4DeberiaRetornarTrue() {
        // Preparación y Ejecución - Nota 3.9
        usuarioMateriaMock.setNota(3);
        assertThat(usuarioMateriaMock.estaDesaprobada(), is(true));
        assertThat(usuarioMateriaMock.getEstadoo(), equalTo("DESAPROBADA"));

        // Preparación y Ejecución - Nota 0
        usuarioMateriaMock.setNota(0);
        assertThat(usuarioMateriaMock.estaDesaprobada(), is(true));
        assertThat(usuarioMateriaMock.getEstadoo(), equalTo("DESAPROBADA"));

        // Preparación y Ejecución - Nota 2.5
        usuarioMateriaMock.setNota(2);
        assertThat(usuarioMateriaMock.estaDesaprobada(), is(true));
        assertThat(usuarioMateriaMock.getEstadoo(), equalTo("DESAPROBADA"));
    }

    // ========== Tests para Métodos de Acción ==========

    @Test
    public void aprobarConNotaValidaDeberiaEstablecerNotaYFecha() {
        // Preparación
        Integer notaAprobacion = 8;

        // Ejecución
        usuarioMateriaMock.aprobar(notaAprobacion);

        // Validación
        assertThat(usuarioMateriaMock.getNota(), equalTo(notaAprobacion));
        assertThat(usuarioMateriaMock.estaAprobada(), is(true));
        assertThat(usuarioMateriaMock.getEstadoo(), equalTo("APROBADA"));
        assertThat(usuarioMateriaMock.getFechaModificacion(), is(notNullValue()));
    }

    @Test
    public void aprobarConNotaMenorA4DeberiaLanzarExcepcion() {
        // Ejecución y Validación
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioMateriaMock.aprobar(3)
        );

        assertThat(exception.getMessage(), containsString("Para aprobar, la nota debe ser mayor o igual a 4.0"));
    }

    @Test
    public void aprobarConNotaNulaDeberiaLanzarExcepcion() {
        // Ejecución y Validación
        assertThrows(IllegalArgumentException.class, () -> usuarioMateriaMock.aprobar(null));
    }

    @Test
    public void desaprobarConNotaValidaDeberiaEstablecerNotaYFecha() {
        // Preparación
        Integer notaDesaprobacion = 3;

        // Ejecución
        usuarioMateriaMock.desaprobar(notaDesaprobacion);

        // Validación
        assertThat(usuarioMateriaMock.getNota(), equalTo(notaDesaprobacion));
        assertThat(usuarioMateriaMock.estaDesaprobada(), is(true));
        assertThat(usuarioMateriaMock.getEstadoo(), equalTo("DESAPROBADA"));
        assertThat(usuarioMateriaMock.getFechaModificacion(), is(notNullValue()));
    }

    @Test
    public void desaprobarConNotaMayorOIgualA4DeberiaLanzarExcepcion() {
        // Ejecución y Validación - Nota 4.0
        assertThrows(IllegalArgumentException.class, () -> usuarioMateriaMock.desaprobar(4));

        // Ejecución y Validación - Nota mayor a 4
        assertThrows(IllegalArgumentException.class, () -> usuarioMateriaMock.desaprobar(7));
    }

    @Test
    public void desaprobarConNotaNulaDeberiaLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> usuarioMateriaMock.desaprobar(null));
    }

    @Test
    public void reiniciarCursadaDeberiaPonerNotaNulaYActualizarFecha() {
        // Preparación - Materia con nota
        usuarioMateriaMock.setNota(8);
        assertThat(usuarioMateriaMock.estaAprobada(), is(true));

        // Ejecución
        usuarioMateriaMock.reiniciarCursada();

        // Validación
        assertThat(usuarioMateriaMock.getNota(), is(nullValue()));
        assertThat(usuarioMateriaMock.estaCursando(), is(true));
        assertThat(usuarioMateriaMock.getEstadoo(), equalTo("CURSANDO"));
        assertThat(usuarioMateriaMock.getFechaModificacion(), is(notNullValue()));
    }

    // ========== Tests para Dificultad ==========

    @Test
    public void esDificilConDificultadMayorOIgualA7DeberiaRetornarTrue() {
        // Preparación y Ejecución
        usuarioMateriaMock.setDificultad(7);
        assertThat(usuarioMateriaMock.esDificil(), is(true));

        usuarioMateriaMock.setDificultad(10);
        assertThat(usuarioMateriaMock.esDificil(), is(true));

        usuarioMateriaMock.setDificultad(8);
        assertThat(usuarioMateriaMock.esDificil(), is(true));
    }

    @Test
    public void esDificilConDificultadMenorA7DeberiaRetornarFalse() {
        // Preparación y Ejecución
        usuarioMateriaMock.setDificultad(6);
        assertThat(usuarioMateriaMock.esDificil(), is(false));

        usuarioMateriaMock.setDificultad(1);
        assertThat(usuarioMateriaMock.esDificil(), is(false));
    }

    @Test
    public void esFacilConDificultadMenorOIgualA3DeberiaRetornarTrue() {
        // Preparación y Ejecución
        usuarioMateriaMock.setDificultad(1);
        assertThat(usuarioMateriaMock.esFacil(), is(true));

        usuarioMateriaMock.setDificultad(3);
        assertThat(usuarioMateriaMock.esFacil(), is(true));

        usuarioMateriaMock.setDificultad(2);
        assertThat(usuarioMateriaMock.esFacil(), is(true));
    }

    @Test
    public void esModeradaConDificultadEntre4Y6DeberiaRetornarTrue() {
        // Preparación y Ejecución
        usuarioMateriaMock.setDificultad(4);
        assertThat(usuarioMateriaMock.esModerada(), is(true));

        usuarioMateriaMock.setDificultad(5);
        assertThat(usuarioMateriaMock.esModerada(), is(true));

        usuarioMateriaMock.setDificultad(6);
        assertThat(usuarioMateriaMock.esModerada(), is(true));
    }

    @Test
    public void dificultadNulaDeberiaRetornarFalseEnTodosLosCasos() {
        // Preparación
        usuarioMateriaMock.setDificultad(null);

        // Validación
        assertThat(usuarioMateriaMock.esDificil(), is(false));
        assertThat(usuarioMateriaMock.esFacil(), is(false));
        assertThat(usuarioMateriaMock.esModerada(), is(false));
    }

    // ========== Tests para Getters y Setters ==========

    @Test
    public void setNotaDeberiaActualizarFechaModificacion() throws InterruptedException {
        // Preparación
        usuarioMateriaMock.setNota(5);
        Thread.sleep(1); // Asegurar diferencia de tiempo

        // Ejecución
        usuarioMateriaMock.setNota(7);

        // Validación
        assertThat(usuarioMateriaMock.getNota(), equalTo(7));
        assertThat(usuarioMateriaMock.getFechaModificacion(), is(notNullValue()));
    }

    @Test
    public void setDificultadDeberiaActualizarFechaModificacion() {
        // Ejecución
        usuarioMateriaMock.setDificultad(8);

        // Validación
        assertThat(usuarioMateriaMock.getDificultad(), equalTo(8));
        assertThat(usuarioMateriaMock.getFechaModificacion(), is(notNullValue()));
    }

    // ========== Tests para toString ==========

    @Test
    public void toStringDeberiaIncluirInformacionRelevante() {
        // Preparación
        when(usuarioMock.getEmail()).thenReturn("test@unlam.com");
        when(materiaMock.getNombre()).thenReturn("Algoritmos");

        usuarioMateriaMock.setUsuario(usuarioMock);
        usuarioMateriaMock.setMateria(materiaMock);
        usuarioMateriaMock.setNota(8);
        usuarioMateriaMock.setDificultad(3);

        // Ejecución
        String resultado = usuarioMateriaMock.toString();

        // Validación
        assertThat(resultado, containsString("test@unlam.com"));
        assertThat(resultado, containsString("Algoritmos"));
        assertThat(resultado, containsString("APROBADA"));
        assertThat(resultado, containsString("8"));
        assertThat(resultado, containsString("3"));
    }


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

    @Test
    void obtenerUsuariosConMaterias_deberiaDevolverListaDeDTOs() {
        when(repositorioUsuarioMateriaMock.obtenerUsuariosConMaterias()).thenReturn(List.of(dto));

        List<UsuarioYMateriasDTO> resultado = servicioUsuarioMateriaMock.obtenerUsuariosConMaterias();

        assertThat(resultado, is(notNullValue()));
        verify(servicioUsuarioMateriaMock).obtenerUsuariosConMaterias();
    }

    @Test
    void obtenerUsuariosConMaterias_deberiaDevolverListaVaciaSiNoHayDatos() {
        when(repositorioUsuarioMateriaMock.obtenerUsuariosConMaterias()).thenReturn(List.of());

        List<UsuarioYMateriasDTO> resultado = servicioUsuarioMateriaMock.obtenerUsuariosConMaterias();

        assertThat(resultado, is(empty()));
    }

    @Test
    void obtenerUsuariosConMaterias_deberiaLanzarExcepcionSiRepositorioFalla() {
        // Arrange
        when(repositorioUsuarioMateriaMock.obtenerUsuariosConMaterias()).thenThrow(new RuntimeException("Error en repo"));

        // Act & Assert
        try {
            servicioUsuarioMateriaMock.obtenerUsuariosConMaterias();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Error en repo"));
        }

        verify(servicioUsuarioMateriaMock).obtenerUsuariosConMaterias();
    }


}