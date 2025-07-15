package com.tallerwebi.servicioInterfaz;

import com.tallerwebi.dominio.googleBookAPI.Item;

import java.util.List;
import java.util.Set;

public interface ServicioLibro {
    List<Item> obtenerLibros();
    void agregarLibroAFavoritos(Long usuarioId, String idLibro);
    Set<String> obtenerIdsDeLibrosFavoritos(Long idUsuario);
    void eliminarLibroDeFavoritos(Long usuarioId, String idGoogleBook);
    List<Item> obtenerLibrosFavoritosDelUsuario(Long usuarioId);
}
