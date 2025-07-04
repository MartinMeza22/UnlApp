package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.googleBookAPI.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ServicioLibro {

    private RestTemplate restTemplate;

    @Value("AIzaSyBQ5MmjzKZFsq0PH8F4cA46YVNO3Tb8NQo")
    private String apiKey;

    @Autowired
    public ServicioLibro(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ApiResponse obtenerLibros() {
        String url = "https://www.googleapis.com/books/v1/volumes";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("q", "web")
                .queryParam("maxResults", 30)
                .queryParam("key", apiKey);

        try{
            return restTemplate.getForObject(builder.toUriString(), ApiResponse.class);
        }catch (Exception e){
            return null;
        }
    }


}
