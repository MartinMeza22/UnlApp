//package com.tallerwebi.dominio.servicios;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.tallerwebi.dominio.googleBookAPI.ApiResponse; // Todavía la necesitamos temporalmente para la deserialización
//import com.tallerwebi.dominio.googleBookAPI.Item;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.util.Collections; // Para retornar una lista vacía en caso de error
//import java.util.List;
//
//@Service
//public class ServicioLibro {
//
//    private final HttpClient httpClient;
//    private final ObjectMapper objectMapper;
//
//    @Value("AIzaSyBQ5MmjzKZFsq0PH8F4cA46YVNO3Tb8NQo") // Se recomienda externalizar la API Key en application.properties/yml
//    private String apiKey;
//
//    @Autowired
//    public ServicioLibro(HttpClient httpClient, ObjectMapper objectMapper) {
//        this.httpClient = httpClient;
//        this.objectMapper = objectMapper;
//    }
//
//    public List<Item> obtenerLibros() {
//        String url = "https://www.googleapis.com/books/v1/volumes";
//
//        // Construye la URL con los parámetros de consulta
//        URI uri = URI.create(
//                url + "?q=web&maxResults=30&key=" + apiKey
//        );
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(uri)
//                .GET() // Método GET
//                .build();
//
//        try {
//            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//
//            if (response.statusCode() == 200) {
//                // Deserializa la respuesta completa a ApiResponse primero
//                // Luego, extrae la lista de Items
//                ApiResponse apiResponse = objectMapper.readValue(response.body(), ApiResponse.class);
//                return apiResponse != null && apiResponse.getItems() != null ? apiResponse.getItems() : Collections.emptyList();
//            } else {
//                // Manejar otros códigos de estado si es necesario
//                System.err.println("Error al obtener libros. Código de estado: " + response.statusCode());
//                return Collections.emptyList();
//            }
//
//        } catch (IOException | InterruptedException e) {
//            System.err.println("Excepción al obtener libros: " + e.getMessage());
//            return Collections.emptyList(); // Retornar una lista vacía en caso de error
//        }
//    }
//}
//
//
