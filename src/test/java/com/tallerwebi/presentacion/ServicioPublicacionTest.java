package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.AccesoDenegado;
import com.tallerwebi.dominio.excepcion.PublicacionInexistente;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.repositorioInterfaz.RepositorioMateria;
import com.tallerwebi.repositorioInterfaz.RepositorioPublicacion;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import com.tallerwebi.serviciosImplementacion.ServicioPublicacionImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ServicioPublicacionTest {

    private RepositorioPublicacion repositorioPublicacionMock;
    private RepositorioMateria repositorioMateriaMock;
    private RepositorioUsuario repositorioUsuarioMock;
    private ServicioPublicacionImpl servicioPublicacion;

    private Usuario usuarioMock;
    private Materia materiaMock;
    private Carrera carreraMock;

    @BeforeEach
    public void init() {
        repositorioPublicacionMock = mock(RepositorioPublicacion.class);
        repositorioMateriaMock = mock(RepositorioMateria.class);
        repositorioUsuarioMock = mock(RepositorioUsuario.class);
        servicioPublicacion = new ServicioPublicacionImpl(repositorioPublicacionMock, repositorioMateriaMock, repositorioUsuarioMock);

        usuarioMock = mock(Usuario.class);
        materiaMock = mock(Materia.class);
        carreraMock = mock(Carrera.class);
    }

    @Test
    public void alBuscarPublicacionesSinFiltrarPorMateriaDeberiaLlamarAlRepositorioConMateriaNula() {
        String orden = "likes";
        when(repositorioPublicacionMock.buscarPublicaciones(carreraMock, null, orden))
                .thenReturn(Collections.emptyList());

        List<Publicacion> resultado = servicioPublicacion.buscarPublicaciones(carreraMock, null, orden);

        verify(repositorioMateriaMock, never()).buscarPorId(anyLong());
        verify(repositorioPublicacionMock, times(1)).buscarPublicaciones(carreraMock, null, orden);
        assertThat(resultado, is(notNullValue()));
    }

    @Test
    public void alCrearPublicacionDeberiaLlamarAGuardarDelRepositorio() {
        String titulo = "Mi Duda";
        String desc = "No entiendo este tema";
        Long idMateria = 1L;
        when(repositorioMateriaMock.buscarPorId(idMateria)).thenReturn(materiaMock);

        servicioPublicacion.crearPublicacion(titulo, desc, usuarioMock, idMateria);

        ArgumentCaptor<Publicacion> captor = ArgumentCaptor.forClass(Publicacion.class);
        verify(repositorioPublicacionMock, times(1)).guardar(captor.capture());
        Publicacion publicacionGuardada = captor.getValue();
        assertThat(publicacionGuardada.getTitulo(), equalTo(titulo));
        assertThat(publicacionGuardada.getDescripcion(), equalTo(desc));
    }

    @Test
    public void alEliminarPublicacionAjenaDeberiaLanzarAccesoDenegado() {
        Long idPublicacion = 1L;
        Long idUsuarioPropietario = 10L;
        Long idUsuarioAjeno = 20L;
        Usuario usuarioPropietario = mock(Usuario.class);
        when(usuarioPropietario.getId()).thenReturn(idUsuarioPropietario);
        Publicacion publicacion = new Publicacion();
        publicacion.setUsuario(usuarioPropietario);
        when(repositorioPublicacionMock.buscarPorId(idPublicacion)).thenReturn(publicacion);

        assertThrows(AccesoDenegado.class, () -> {
            servicioPublicacion.eliminarPublicacion(idPublicacion, idUsuarioAjeno);
        });
        verify(repositorioPublicacionMock, never()).eliminar(any(Publicacion.class));
    }

    @Test
    public void alDarLikePorPrimeraVezDeberiaAgregarElLike() throws PublicacionInexistente, UsuarioNoEncontrado {
        Long idPublicacion = 1L;
        Long idUsuario = 10L;
        Publicacion publicacion = new Publicacion();
        publicacion.setId(idPublicacion);

        when(repositorioPublicacionMock.buscarPorId(idPublicacion)).thenReturn(publicacion);
        when(repositorioUsuarioMock.buscarPorId(idUsuario)).thenReturn(usuarioMock);

        servicioPublicacion.cambiarEstadoLike(idPublicacion, idUsuario);

        assertThat(publicacion.getLikes(), equalTo(1));
        assertThat(publicacion.usuarioDioLike(usuarioMock), is(true));
        verify(repositorioPublicacionMock, times(1)).guardar(publicacion);
    }

    @Test
    public void alModificarPublicacionPropiaDeberiaActualizarTituloYDescripcion() throws PublicacionInexistente, AccesoDenegado {
        Long idPublicacion = 1L;
        Long idUsuario = 10L;
        String nuevoTitulo = "Titulo Nuevo";
        String nuevaDesc = "Descripcion Nueva";

        when(usuarioMock.getId()).thenReturn(idUsuario);
        Publicacion publicacionOriginal = new Publicacion();
        publicacionOriginal.setTitulo("Original");
        publicacionOriginal.setDescripcion("Original");
        publicacionOriginal.setUsuario(usuarioMock);

        when(repositorioPublicacionMock.buscarPorId(idPublicacion)).thenReturn(publicacionOriginal);

        servicioPublicacion.modificarPublicacion(idPublicacion, nuevoTitulo, nuevaDesc, idUsuario);

        ArgumentCaptor<Publicacion> captor = ArgumentCaptor.forClass(Publicacion.class);
        verify(repositorioPublicacionMock, times(1)).guardar(captor.capture());
        Publicacion publicacionModificada = captor.getValue();

        assertThat(publicacionModificada.getTitulo(), equalTo(nuevoTitulo));
        assertThat(publicacionModificada.getDescripcion(), equalTo(nuevaDesc));
    }

    @Test
    public void alModificarPublicacionInexistenteDeberiaLanzarExcepcion() {
        Long idPublicacionInexistente = 99L;
        when(repositorioPublicacionMock.buscarPorId(idPublicacionInexistente)).thenReturn(null);

        assertThrows(PublicacionInexistente.class, () -> {
            servicioPublicacion.modificarPublicacion(idPublicacionInexistente, "a", "b", 1L);
        });
    }

    @Test
    public void alDarLikeConUsuarioInexistenteDeberiaLanzarExcepcion() {
        Long idPublicacion = 1L;
        Long idUsuarioInexistente = 99L;
        Publicacion publicacion = new Publicacion();
        when(repositorioPublicacionMock.buscarPorId(idPublicacion)).thenReturn(publicacion);
        when(repositorioUsuarioMock.buscarPorId(idUsuarioInexistente)).thenReturn(null);

        assertThrows(UsuarioNoEncontrado.class, () -> {
            servicioPublicacion.cambiarEstadoLike(idPublicacion, idUsuarioInexistente);
        });
    }

    @Test
    public void alDarLikeAPublicacionInexistenteDeberiaLanzarExcepcion() {
        Long idPublicacionInexistente = 99L;
        Long idUsuario = 1L;
        when(repositorioPublicacionMock.buscarPorId(idPublicacionInexistente)).thenReturn(null);

        assertThrows(PublicacionInexistente.class, () -> {
            servicioPublicacion.cambiarEstadoLike(idPublicacionInexistente, idUsuario);
        });
    }
}