package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Comentario;
import com.tallerwebi.dominio.Publicacion;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.AccesoDenegado;
import com.tallerwebi.dominio.excepcion.ComentarioInexistente;
import com.tallerwebi.dominio.excepcion.PublicacionInexistente;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.repositorioInterfaz.RepositorioComentario;
import com.tallerwebi.servicioInterfaz.ServicioComentario;
import com.tallerwebi.servicioInterfaz.ServicioPublicacion;
import com.tallerwebi.servicioInterfaz.ServicioUsuario;
import com.tallerwebi.serviciosImplementacion.ServicioComentarioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ServicioComentarioTest {

    private RepositorioComentario repositorioComentarioMock;
    private ServicioPublicacion servicioPublicacionMock;
    private ServicioUsuario servicioUsuarioMock;
    private ServicioComentario servicioComentario;

    private Usuario usuarioMock;
    private Publicacion publicacionMock;

    @BeforeEach
    public void init() {
        repositorioComentarioMock = mock(RepositorioComentario.class);
        servicioPublicacionMock = mock(ServicioPublicacion.class);
        servicioUsuarioMock = mock(ServicioUsuario.class);
        servicioComentario = new ServicioComentarioImpl(repositorioComentarioMock, servicioPublicacionMock, servicioUsuarioMock);

        usuarioMock = mock(Usuario.class);
        publicacionMock = mock(Publicacion.class);
    }

    @Test
    public void alCrearComentarioDeberiaAsociarloConLaPublicacionYElUsuarioCorrectos() throws PublicacionInexistente, AccesoDenegado {
        when(usuarioMock.getRol()).thenReturn("ALUMNO");
        Long idPublicacion = 1L;
        String descripcion = "comentario prueba";
        when(servicioPublicacionMock.obtenerPublicacion(idPublicacion)).thenReturn(publicacionMock);

        servicioComentario.crearComentario(idPublicacion, usuarioMock, descripcion);

        ArgumentCaptor<Comentario> captor = ArgumentCaptor.forClass(Comentario.class);
        verify(repositorioComentarioMock, times(1)).guardar(captor.capture());
        Comentario comentarioGuardado = captor.getValue();

        assertThat(comentarioGuardado.getDescripcion(), equalTo(descripcion));
        assertThat(comentarioGuardado.getUsuario(), equalTo(usuarioMock));
    }

    @Test
    public void siUnAdminIntentaCrearUnComentarioDeberiaLanzarAccesoDenegado() {
        when(usuarioMock.getRol()).thenReturn("ADMIN");

        assertThrows(AccesoDenegado.class, () -> {
            servicioComentario.crearComentario(1L, usuarioMock, "Test de admin");
        });
        verify(repositorioComentarioMock, never()).guardar(any());
    }

    @Test
    public void unAdminDeberiaPoderEliminarCualquierComentario() throws ComentarioInexistente, AccesoDenegado, UsuarioNoEncontrado {
        Long idComentario = 1L;
        Long idPropietario = 10L;
        Long idAdmin = 99L;

        Usuario propietario = mock(Usuario.class);
        when(propietario.getId()).thenReturn(idPropietario);

        Usuario admin = mock(Usuario.class);
        when(admin.getRol()).thenReturn("ADMIN");

        Comentario comentario = new Comentario();
        comentario.setUsuario(propietario);

        when(repositorioComentarioMock.buscarPorId(idComentario)).thenReturn(comentario);
        when(servicioUsuarioMock.obtenerUsuario(idAdmin)).thenReturn(admin);

        servicioComentario.eliminarComentario(idComentario, idAdmin);

        verify(repositorioComentarioMock, times(1)).eliminar(comentario);
    }

    @Test
    public void unAlumnoNoDeberiaPoderEliminarComentarioAjeno() throws UsuarioNoEncontrado {
        Long idComentario = 1L;
        Long idPropietario = 10L;
        Long idAjeno = 20L;

        Usuario propietario = mock(Usuario.class);
        when(propietario.getId()).thenReturn(idPropietario);

        Usuario ajeno = mock(Usuario.class);
        when(ajeno.getRol()).thenReturn("ALUMNO");

        Comentario comentario = new Comentario();
        comentario.setUsuario(propietario);

        when(repositorioComentarioMock.buscarPorId(idComentario)).thenReturn(comentario);
        when(servicioUsuarioMock.obtenerUsuario(idAjeno)).thenReturn(ajeno);

        assertThrows(AccesoDenegado.class, () -> {
            servicioComentario.eliminarComentario(idComentario, idAjeno);
        });
    }

    @Test
    public void unAdminDeberiaPoderModificarCualquierComentario() throws ComentarioInexistente, AccesoDenegado, UsuarioNoEncontrado {
        Long idComentario = 1L;
        Long idPropietario = 10L;
        Long idAdmin = 99L;
        String nuevaDescripcion = "Contenido editado por admin";

        Usuario propietario = mock(Usuario.class);
        when(propietario.getId()).thenReturn(idPropietario);

        Usuario admin = mock(Usuario.class);
        when(admin.getRol()).thenReturn("ADMIN");

        Comentario comentario = new Comentario();
        comentario.setUsuario(propietario);
        comentario.setDescripcion("Contenido original");

        when(repositorioComentarioMock.buscarPorId(idComentario)).thenReturn(comentario);
        when(servicioUsuarioMock.obtenerUsuario(idAdmin)).thenReturn(admin);

        servicioComentario.modificarComentario(idComentario, nuevaDescripcion, idAdmin);

        ArgumentCaptor<Comentario> captor = ArgumentCaptor.forClass(Comentario.class);
        verify(repositorioComentarioMock, times(1)).guardar(captor.capture());
        assertThat(captor.getValue().getDescripcion(), equalTo(nuevaDescripcion));
    }
}
