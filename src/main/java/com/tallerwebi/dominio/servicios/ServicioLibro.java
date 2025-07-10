package com.tallerwebi.dominio.servicios;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.dominio.googleBookAPI.ApiResponse;
import com.tallerwebi.dominio.googleBookAPI.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServicioLibro {

    private RestTemplate restTemplate;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public ServicioLibro(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Value("AIzaSyBQ5MmjzKZFsq0PH8F4cA46YVNO3Tb8NQo")
    private String apiKey;

//    @Autowired
//    public ServicioLibro(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }

//    public ApiResponse obtenerLibros() {
//        String url = "https://www.googleapis.com/books/v1/volumes";
//
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
//                .queryParam("q", "web")
//                .queryParam("maxResults", 30)
//                .queryParam("key", apiKey);
//
//        try{
//            return restTemplate.getForObject(builder.toUriString(), ApiResponse.class);
//        }catch (Exception e){
//            return null;
//        }
//    }

    public List<Item> obtenerLibros() {
        String url = "https://www.googleapis.com/books/v1/volumes";

        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("q", "desarrollo web")
                .queryParam("maxResult", "40")
                .queryParam("key", apiKey)
                .build()
                .toUri();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ApiResponse apiResponse = this.objectMapper.readValue(response.body(), ApiResponse.class);
                return apiResponse.getItems();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }


}
