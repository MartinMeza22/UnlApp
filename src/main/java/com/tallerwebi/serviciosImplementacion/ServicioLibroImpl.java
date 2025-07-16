package com.tallerwebi.serviciosImplementacion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.dominio.LibroFavorito;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.FavoritoYaExisteException;
import com.tallerwebi.dominio.googleBookAPI.ApiResponse;
import com.tallerwebi.dominio.googleBookAPI.Item;
import com.tallerwebi.repositorioInterfaz.RepositorioLibroFavorito;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import com.tallerwebi.servicioInterfaz.ServicioLibro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
@Transactional
public class ServicioLibroImpl implements ServicioLibro{

    private final RepositorioLibroFavorito repositorioLibroFavorito;
    private final RepositorioUsuario repositorioUsuario;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("AIzaSyBQ5MmjzKZFsq0PH8F4cA46YVNO3Tb8NQo")
    private String apiKey;

    @Autowired
    public ServicioLibroImpl(HttpClient httpClient, ObjectMapper objectMapper, RepositorioLibroFavorito repositorioLibroFavorito, RepositorioUsuario repositorioUsuario) {
        this.repositorioLibroFavorito = repositorioLibroFavorito;
        this.repositorioUsuario = repositorioUsuario;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public List<Item> obtenerLibros() {
        String url = "https://www.googleapis.com/books/v1/volumes";

        URI uri = URI.create(
                url + "?q=web&maxResults=30&key=" + apiKey
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ApiResponse apiResponse = objectMapper.readValue(response.body(), ApiResponse.class);
                return apiResponse != null && apiResponse.getItems() != null ? apiResponse.getItems() : Collections.emptyList();
            } else {
                System.err.println("Error al obtener libros. Código de estado: " + response.statusCode());
                return Collections.emptyList();
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Excepción al obtener libros: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public void agregarLibroAFavoritos(Long usuarioId, String idGoogleBook) throws FavoritoYaExisteException{
        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);

        LibroFavorito nuevoFavorito = new LibroFavorito(usuario, idGoogleBook);

        repositorioLibroFavorito.guardar(nuevoFavorito);
    }

    @Override
    public Set<String> obtenerIdsDeLibrosFavoritos(Long idUsuario) {
        if (idUsuario == null) {
            return Collections.emptySet();
        }
        Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
        if (usuario == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(repositorioLibroFavorito.obtenerIdsPorUsuario(usuario));

    }

    @Override
    public void eliminarLibroDeFavoritos(Long usuarioId, String idGoogleBook) {
        if (usuarioId == null) return;

        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
        if (usuario != null) {
            repositorioLibroFavorito.eliminar(usuario, idGoogleBook);
        }
    }

    @Override
    public List<Item> obtenerLibrosFavoritosDelUsuario(Long usuarioId) {
        Set<String> idsFavoritos = obtenerIdsDeLibrosFavoritos(usuarioId);

        if (idsFavoritos.isEmpty()) {
            return Collections.emptyList();
        }

        List<Item> librosCompletos = new ArrayList<>();
        for (String bookId : idsFavoritos) {
            try {
                Item libro = obtenerLibroPorId(bookId);
                if (libro != null) {
                    librosCompletos.add(libro);
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Error al obtener el libro con ID: " + bookId + " - " + e.getMessage());
            }
        }
        return librosCompletos;
    }
    private Item obtenerLibroPorId(String bookId) throws IOException, InterruptedException {
        String url = "https://www.googleapis.com/books/v1/volumes/" + bookId + "?key=" + apiKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Item.class);
        }

        return null;
    }

}
