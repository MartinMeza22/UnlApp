package com.tallerwebi.dominio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class MateriaTest {

    private Materia materia;

    @BeforeEach
    public void init() {
        materia = new Materia();
    }

    // ========== Constructor Tests ==========
    
    @Test
    public void constructorVacioDeberiaCrearMateriaConActivaTrue() {
        // Ejecución
        Materia nuevaMateria = new Materia();
        
        // Validación
        assertThat(nuevaMateria.getActiva(), is(true));
        assertThat(nuevaMateria.estaActiva(), is(true));
    }

    @Test
    public void constructorConParametrosDeberiaInicializarCorrectamente() {
        // Ejecución
        Materia nuevaMateria = new Materia("Algoritmos", "SISTEMAS", 2);
        
        // Validación
        assertThat(nuevaMateria.getNombre(), equalTo("Algoritmos"));
        assertThat(nuevaMateria.getCarreraId(), equalTo("SISTEMAS"));
        assertThat(nuevaMateria.getCuatrimestre(), equalTo(2));
        assertThat(nuevaMateria.getActiva(), is(true));
        assertThat(nuevaMateria.estaActiva(), is(true));
    }

    // ========== Tests for estaActiva() ==========
    
    @Test
    public void estaActivaConActivaTrueDeberiaRetornarTrue() {
        // Preparación
        materia.setActiva(true);
        
        // Ejecución y Validación
        assertThat(materia.estaActiva(), is(true));
    }

    @Test
    public void estaActivaConActivaFalseDeberiaRetornarFalse() {
        // Preparación
        materia.setActiva(false);
        
        // Ejecución y Validación
        assertThat(materia.estaActiva(), is(false));
    }

    @Test
    public void estaActivaConActivaNulaDeberiaRetornarFalse() {
        // Preparación
        materia.setActiva(null);
        
        // Ejecución y Validación
        assertThat(materia.estaActiva(), is(false));
    }

    // ========== Tests for activar() ==========
    
    @Test
    public void activarDeberiaPonerActivaEnTrue() {
        // Preparación
        materia.setActiva(false);
        
        // Ejecución
        materia.activar();
        
        // Validación
        assertThat(materia.getActiva(), is(true));
        assertThat(materia.estaActiva(), is(true));
    }

    @Test
    public void activarConActivaNulaDeberiaPonerActivaEnTrue() {
        // Preparación
        materia.setActiva(null);
        
        // Ejecución
        materia.activar();
        
        // Validación
        assertThat(materia.getActiva(), is(true));
        assertThat(materia.estaActiva(), is(true));
    }

    // ========== Tests for desactivar() ==========
    
    @Test
    public void desactivarDeberiaPonerActivaEnFalse() {
        // Preparación
        materia.setActiva(true);
        
        // Ejecución
        materia.desactivar();
        
        // Validación
        assertThat(materia.getActiva(), is(false));
        assertThat(materia.estaActiva(), is(false));
    }

    @Test
    public void desactivarConActivaNulaDeberiaPonerActivaEnFalse() {
        // Preparación
        materia.setActiva(null);
        
        // Ejecución
        materia.desactivar();
        
        // Validación
        assertThat(materia.getActiva(), is(false));
        assertThat(materia.estaActiva(), is(false));
    }

    // ========== Tests for Getters and Setters ==========
    
    @Test
    public void deberiaPoderEstablecerYObtenerTodosLosCampos() {
        // Preparación
        Long id = 1L;
        String nombre = "Base de Datos I";
        String carreraId = "SISTEMAS";
        String descripcion = "Introducción a bases de datos relacionales";
        String tipo = "OBLIGATORIA";
        Integer cargaHoraria = 80;
        Integer cuatrimestre = 3;
        String correlativa1 = "MAT001";
        String correlativa2 = "ALG001";
        
        // Ejecución
        materia.setId(id);
        materia.setNombre(nombre);
        materia.setCarreraId(carreraId);
        materia.setDescripcion(descripcion);
        materia.setTipo(tipo);
        materia.setCargaHoraria(cargaHoraria);
        materia.setCuatrimestre(cuatrimestre);
        materia.setCorrelativa1(correlativa1);
        materia.setCorrelativa2(correlativa2);
        
        // Validación
        assertThat(materia.getId(), equalTo(id));
        assertThat(materia.getNombre(), equalTo(nombre));
        assertThat(materia.getCarreraId(), equalTo(carreraId));
        assertThat(materia.getDescripcion(), equalTo(descripcion));
        assertThat(materia.getTipo(), equalTo(tipo));
        assertThat(materia.getCargaHoraria(), equalTo(cargaHoraria));
        assertThat(materia.getCuatrimestre(), equalTo(cuatrimestre));
        assertThat(materia.getCorrelativa1(), equalTo(correlativa1));
        assertThat(materia.getCorrelativa2(), equalTo(correlativa2));
    }

    @Test
    public void deberiaPoderEstablecerTodasLasCorrelativas() {
        // Preparación
        String corr1 = "MAT001", corr2 = "FIS001", corr3 = "ALG001";
        String corr4 = "PRG001", corr5 = "EST001", corr6 = "CAL001";
        
        // Ejecución
        materia.setCorrelativa1(corr1);
        materia.setCorrelativa2(corr2);
        materia.setCorrelativa3(corr3);
        materia.setCorrelativa4(corr4);
        materia.setCorrelativa5(corr5);
        materia.setCorrelativa6(corr6);
        
        // Validación
        assertThat(materia.getCorrelativa1(), equalTo(corr1));
        assertThat(materia.getCorrelativa2(), equalTo(corr2));
        assertThat(materia.getCorrelativa3(), equalTo(corr3));
        assertThat(materia.getCorrelativa4(), equalTo(corr4));
        assertThat(materia.getCorrelativa5(), equalTo(corr5));
        assertThat(materia.getCorrelativa6(), equalTo(corr6));
    }

    // ========== Tests for toString() ==========
    
    @Test
    public void toStringDeberiaIncluirCamposImportantes() {
        // Preparación
        materia.setId(1L);
        materia.setNombre("Test Materia");
        materia.setCarreraId("TEST");
        materia.setCuatrimestre(1);
        materia.setActiva(true);
        
        // Ejecución
        String resultado = materia.toString();
        
        // Validación
        assertThat(resultado, containsString("id=1"));
        assertThat(resultado, containsString("nombre='Test Materia'"));
        assertThat(resultado, containsString("carreraId='TEST'"));
        assertThat(resultado, containsString("cuatrimestre=1"));
        assertThat(resultado, containsString("activa=true"));
    }

    // ========== Edge Cases and Business Logic Tests ==========
    
    @Test
    public void materiaConCargaHorariaCeroDeberiaSerValida() {
        // Preparación y Ejecución
        materia.setCargaHoraria(0);
        
        // Validación
        assertThat(materia.getCargaHoraria(), equalTo(0));
    }

    @Test
    public void materiaConCuatrimestreNuloDeberiaSerValida() {
        // Preparación y Ejecución
        materia.setCuatrimestre(null);
        
        // Validación
        assertThat(materia.getCuatrimestre(), is(nullValue()));
    }

    @Test
    public void materiaConDescripcionLargaDeberiaSerValida() {
        // Preparación
        String descripcionLarga = "Esta es una descripción muy larga que podría tener hasta 1000 caracteres según la definición de la columna en la base de datos. " +
                "Aquí podemos incluir información detallada sobre la materia, sus objetivos, contenidos, metodología y evaluación.";
        
        // Ejecución
        materia.setDescripcion(descripcionLarga);
        
        // Validación
        assertThat(materia.getDescripcion(), equalTo(descripcionLarga));
        assertThat(materia.getDescripcion().length(), lessThanOrEqualTo(1000));
    }
}