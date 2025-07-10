package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.googleBookAPI.Item;
import com.tallerwebi.dominio.servicios.ServicioLibro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

public class ControladorLibroTest {

    @Mock
    private ServicioLibro servicioLibroMock;

    @InjectMocks
    private ControladorLibro controladorLibro;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void queAlSolicitarLaBibliotecaSeMuestrenLosLibros() {
        List<Item> librosSimulados = Arrays.asList(new Item(), new Item());
        when(servicioLibroMock.obtenerLibros()).thenReturn(librosSimulados);

        ModelAndView mav = controladorLibro.mostrarLibros();

        assertThat(mav.getViewName(), equalTo("vista-libros"));
        assertThat(mav.getModel().get("libros"), is(notNullValue()));
        assertThat(mav.getModel().get("libros"), is(equalTo(librosSimulados)));
        assertThat(mav.getModel().get("error"), is(nullValue()));
    }

    @Test
    public void queSiElServicioNoDevuelveLibrosSeMuestreUnMensajeDeError() {
        when(servicioLibroMock.obtenerLibros()).thenReturn(null);

        ModelAndView mav = controladorLibro.mostrarLibros();

        assertThat(mav.getViewName(), equalTo("vista-libros"));
        assertThat(mav.getModel().get("error"), is(notNullValue()));
        assertThat(mav.getModel().get("error"), is(equalTo("No se encontraron libros")));
        assertThat(mav.getModel().get("libros"), is(nullValue()));
    }

    @Test
    public void queSiElServicioDevuelveUnaListaVaciaSeMuestreLaVistaSinLibros() {
        when(servicioLibroMock.obtenerLibros()).thenReturn(Collections.emptyList());

        ModelAndView mav = controladorLibro.mostrarLibros();

        assertThat(mav.getViewName(), equalTo("vista-libros"));
        List<Item> librosEnModelo = (List<Item>) mav.getModel().get("libros");
        assertThat(librosEnModelo, is(notNullValue()));
        assertThat(librosEnModelo, is(empty()));
        assertThat(mav.getModel().get("error"), is(nullValue()));
    }
}
