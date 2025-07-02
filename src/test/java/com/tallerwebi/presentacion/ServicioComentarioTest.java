package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Comentario;
import com.tallerwebi.dominio.Publicacion;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.AccesoDenegado;
import com.tallerwebi.dominio.excepcion.ComentarioInexistente;
import com.tallerwebi.dominio.excepcion.PublicacionInexistente;
import com.tallerwebi.repositorioInterfaz.RepositorioComentario;
import com.tallerwebi.servicioInterfaz.ServicioComentario;
import com.tallerwebi.servicioInterfaz.ServicioPublicacion;
import com.tallerwebi.serviciosImplementacion.ServicioComentarioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ServicioComentarioTest {

    private RepositorioComentario repositorioComentarioMock;
    private ServicioPublicacion servicioPublicacionMock;
    private ServicioComentario servicioComentario;

    private Usuario usuarioMock;
    private Publicacion publicacionMock;

    @BeforeEach
    public void init() {
        repositorioComentarioMock = mock(RepositorioComentario.class);
        servicioPublicacionMock = mock(ServicioPublicacion.class);
        servicioComentario = new ServicioComentarioImpl(repositorioComentarioMock, servicioPublicacionMock);

        usuarioMock = mock(Usuario.class);
        publicacionMock = mock(Publicacion.class);
    }

    @Test
    public void alCrearComentarioDeberiaAsociarloConLaPublicacionYElUsuarioCorrectos() throws PublicacionInexistente {
        Long idPublicacion = 1L;
        String descripcion = "comentario prueba";
        when(servicioPublicacionMock.obtenerPublicacion(idPublicacion)).thenReturn(publicacionMock);

        servicioComentario.crearComentario(idPublicacion, usuarioMock, descripcion);

        ArgumentCaptor<Comentario> captor = ArgumentCaptor.forClass(Comentario.class);
        verify(repositorioComentarioMock, times(1)).guardar(captor.capture());
        Comentario comentarioGuardado = captor.getValue();

        assertThat(comentarioGuardado.getDescripcion(), equalTo(descripcion));
        assertThat(comentarioGuardado.getUsuario(), equalTo(usuarioMock));
        assertThat(comentarioGuardado.getPublicacion(), equalTo(publicacionMock));
    }

    @Test
    public void alCrearComentarioEnPublicacionInexistenteDeberiaLanzarExcepcion() throws PublicacionInexistente {
        Long idPublicacionInexistente = 99L;
        when(servicioPublicacionMock.obtenerPublicacion(idPublicacionInexistente))
                .thenThrow(new PublicacionInexistente("La publicación no existe"));

        assertThrows(PublicacionInexistente.class, () -> {
            servicioComentario.crearComentario(idPublicacionInexistente, usuarioMock, "Test");
        });
        verify(repositorioComentarioMock, never()).guardar(any(Comentario.class));
    }

    @Test
    public void alModificarComentarioAjenoDeberiaLanzarAccesoDenegado() {
        Long idComentario = 1L;
        Long idUsuarioPropietario = 10L;
        Long idUsuarioAjeno = 20L;
        Usuario usuarioPropietario = mock(Usuario.class);
        when(usuarioPropietario.getId()).thenReturn(idUsuarioPropietario);
        Comentario comentario = new Comentario();
        comentario.setUsuario(usuarioPropietario);
        when(repositorioComentarioMock.buscarPorId(idComentario)).thenReturn(comentario);

        assertThrows(AccesoDenegado.class, () -> {
            servicioComentario.modificarComentario(idComentario, "Intento de modificación", idUsuarioAjeno);
        });
    }

    @Test
    public void alEliminarComentarioPropioDeberiaLlamarAEliminarDelRepositorio() throws ComentarioInexistente, AccesoDenegado {
        Long idComentario = 1L;
        Long idUsuario = 10L;
        when(usuarioMock.getId()).thenReturn(idUsuario);

        Comentario comentarioAEliminar = new Comentario();
        comentarioAEliminar.setUsuario(usuarioMock);

        when(repositorioComentarioMock.buscarPorId(idComentario)).thenReturn(comentarioAEliminar);

        servicioComentario.eliminarComentario(idComentario, idUsuario);

        verify(repositorioComentarioMock, times(1)).eliminar(comentarioAEliminar);
    }

    @Test
    public void alModificarComentarioInexistenteDeberiaLanzarExcepcion() {
        Long idComentarioInexistente = 99L;
        when(repositorioComentarioMock.buscarPorId(idComentarioInexistente)).thenReturn(null);

        assertThrows(ComentarioInexistente.class, () -> {
            servicioComentario.modificarComentario(idComentarioInexistente, "nueva descripcion", 1L);
        });

        verify(repositorioComentarioMock, never()).guardar(any(Comentario.class));
    }
}