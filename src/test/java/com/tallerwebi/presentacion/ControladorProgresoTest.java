package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.ServicioMateria;
import com.tallerwebi.dominio.ServicioProgreso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

import static org.hamcrest.MatcherAssert.assertThat; // Para aserciones más legibles
import static org.hamcrest.Matchers.*; // Para usar is(), hasItem(), hasProperty(), etc.
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class ControladorProgresoTest {

    private ServicioProgreso servicioProgreso;
    private ServicioMateria servicioMateria;
    private ControladorProgresoAcademico controladorProgreso;
    private HttpSession httpSession;

    @BeforeEach
    public void init(){
        servicioProgreso = mock(ServicioProgreso.class);
        servicioMateria = mock(ServicioMateria.class);
        controladorProgreso = new ControladorProgresoAcademico(servicioProgreso, servicioMateria);
        httpSession = mock(HttpSession.class);
    }

//    @Test
//    public void queSePuedaObtenerTodasLasMateriasDeLaCarrera(){
//
//        List<Materia> materiasEsperadas = new ArrayList<>();
//
//        Materia materia1 = new Materia();
//        materia1.setNombre("Programacion Web Avanzada");;
//        materia1.setCuatrimestre(1);
//        materiasEsperadas.add(materia1);
//
//        Materia materia2 = new Materia();
//        materia2.setNombre("Base de datos");;
//        materia2.setCuatrimestre(2);
//        materiasEsperadas.add(materia2);
//
//        when(servicioMateria.obtenerTodasLasMaterias()).thenReturn(materiasEsperadas);
//
//        ModelAndView mav = controladorProgreso.verProgreso( "",httpSession);
//
//        verify(servicioMateria, times(1)).obtenerTodasLasMaterias();
//
//        assertThat(mav.getViewName(), is("progreso"));
//
//        assertThat(mav.getModel().containsKey("carrera"), is(true));
//
////        assertThat((Carrera) mav.getModel().get("carrera"));
//
//        assertThat(mav.getModel().containsKey("materias"), is(true));
//        assertThat((List<Materia>) mav.getModel().get("materias"), is(materiasEsperadas));
//
//        assertThat(((List<Materia>) mav.getModel().get("materias")).size(), is(2));
//
//        assertThat((List<Materia>) mav.getModel().get("materias"), hasItem(hasProperty("nombre", is("Programacion Web Avanzada"))));
//    }

}
