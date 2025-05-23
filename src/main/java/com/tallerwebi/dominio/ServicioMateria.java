package com.tallerwebi.dominio;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ServicioMateria {

    private final ObjectMapper objectMapper;

    @Autowired
    public ServicioMateria(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<MateriaDB> obtenerTodasLasMaterias() {
        try {
            ClassPathResource classPathResource = new ClassPathResource("mockMateriasDB.json");
            InputStream inputStream = classPathResource.getInputStream();
            return objectMapper.readValue(inputStream, new TypeReference<List<MateriaDB>>() {});
        } catch (Exception e) {
            // Es buena práctica loguear el error completo para depuración
            e.printStackTrace();
            throw new RuntimeException("Error al leer el archivo JSON de materias.", e);
        }
    }

    public Map<Integer, List<MateriaDB>> obtenerMateriasAgrupadasPorCuatrimestre() {
        return obtenerTodasLasMaterias().stream()
                .collect(Collectors.groupingBy(MateriaDB::getCuatrimestre));
    }

    // Método para mantener compatibilidad con el controlador existente
    public List<MateriaDB> obtenerMateriasPorCuatrimestre() {
        return obtenerTodasLasMaterias();
    }
}