package com.tallerwebi.serviciosImplementacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.servicioInterfaz.ServicioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service("servicioUsuario")
@Transactional
public class ServicioUsuarioImpl implements ServicioUsuario {
    private final RepositorioUsuario repositorioUsuario;

    @Autowired
    public ServicioUsuarioImpl(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }



    @Override
    public void eliminarUsuario(Long id) throws UsuarioNoEncontrado {
        Usuario usuario = repositorioUsuario.buscarPorId(id);
        if (usuario == null) {
            throw new UsuarioNoEncontrado();
        }
        repositorioUsuario.eliminar(usuario);
    }
    @Override
    public Usuario obtenerUsuario(Long id) throws UsuarioNoEncontrado {
        Usuario usuario = repositorioUsuario.buscarPorId(id);
        if (usuario == null) {
            throw new UsuarioNoEncontrado();
        }
        return usuario;
    }

    @Override
    public void actualizarPerfil(Long id, String nombre, String apellido, String email, String nuevaPassword) throws UsuarioNoEncontrado {
        Usuario usuario = repositorioUsuario.buscarPorId(id);
        if (usuario == null) {
            throw new UsuarioNoEncontrado();
        }

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        if (nuevaPassword != null && !nuevaPassword.trim().isEmpty()) {
            usuario.setPassword(nuevaPassword);
        }

        repositorioUsuario.modificar(usuario);
    }

}