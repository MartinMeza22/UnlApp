//package com.tallerwebi.presentacion;
//
//import com.tallerwebi.dominio.*;
//import com.tallerwebi.dominio.DTO.MateriaDTO;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
//
//import javax.servlet.http.HttpSession;
//
//import static org.hamcrest.MatcherAssert.assertThat; // Para aserciones más legibles
//import static org.hamcrest.Matchers.*; // Para usar is(), hasItem(), hasProperty(), etc.
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.*;
//
//public class ControladorProgresoTest {
//
//    private ControladorProgresoAcademico controladorProgresoAcademico;
//
//    private ServicioProgreso mockServicioProgreso;
//    private ServicioMateria mockServicioMateria;
//    private HttpSession mockHttpSession;
//
//    private MateriaDTO materia1;
//    private MateriaDTO materia2;
//    private Long usuarioId = 1L;
//
//    @BeforeEach
//    public void init() {
//        mockServicioProgreso = mock(ServicioProgreso.class);
//        mockServicioMateria = mock(ServicioMateria.class);
//        mockHttpSession = mock(HttpSession.class);
//
//        controladorProgresoAcademico = new ControladorProgresoAcademico(mockServicioProgreso, mockServicioMateria);
//
//        materia1 = new MateriaDTO(1L, "matematicas", "Facil", "APROBADA", 7, 1);
//        materia2 = new MateriaDTO(2L, "base datos", "Dificil", "CURSANDO", null, 2);
//
//        when(mockHttpSession.getAttribute("ID")).thenReturn(usuarioId);
//    }
//
//    @Test
//    public void queAlAccederAProgresoSeMuestrenTodasLasMateriasSiNoHayCondicion() {
//
//        List<MateriaDTO> materiasEsperadas = Arrays.asList(materia1, materia2);
//        when(mockServicioProgreso.materias(usuarioId)).thenReturn(materiasEsperadas);
//
//        ModelAndView mav = this.controladorProgresoAcademico.verProgreso(null, mockHttpSession);
//
//        assertThat(mav, is(notNullValue()));
//        assertThat(mav.getViewName(), is("progreso"));
//
//        ModelMap model = mav.getModelMap();
//        assertThat(model, is(notNullValue()));
//        assertThat(model.get("carrera"), is(notNullValue()));
//        assertThat(model.get("materiasTotales"), is(notNullValue()));
//        assertThat((List<MateriaDTO>) model.get("materiasTotales"), hasSize(2));
//        assertThat((List<MateriaDTO>) model.get("materiasTotales"), hasItem(hasProperty("nombre", is("matematicas"))));
//        assertThat((List<MateriaDTO>) model.get("materiasTotales"), hasItem(hasProperty("nombre", is("base datos"))));
//        assertThat(model.get("usuarioId"), is(usuarioId));
//        assertThat(model.get("selectedCondicion"), is(nullValue()));
//
//    }
//
//    @Test
//    public void queAlAccederAProgresoConCondicionSeFiltrenLasMaterias() {
//        List<MateriaDTO> materiasFiltradas = Collections.singletonList(materia1);
//        String condicion = "aprobadas";
//        when(mockServicioProgreso.filtrarPor(condicion, usuarioId)).thenReturn(materiasFiltradas);
//
//        ModelAndView mav = controladorProgresoAcademico.verProgreso(condicion, mockHttpSession);
//
//        assertThat(mav.getViewName(), is("progreso"));
//        ModelMap model = mav.getModelMap();
//        assertThat((List<MateriaDTO>) model.get("materiasTotales"), hasSize(1));
//        assertThat((List<MateriaDTO>) model.get("materiasTotales"), hasItem(hasProperty("nombre", is("matematicas"))));
//        assertThat(model.get("selectedCondicion"), is(condicion));
//    }
//
////    @Test
////    public void queAlActualizarDatosDeMateriaRedirijaAProgresoYLlameAlServicio() {
////        // Preparación
////        Double nota = 8.5;
////        Integer dificultad = 5;
////        Long idMateria = 1L;
////
////        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
////
////        doNothing().when(mockServicioProgreso).actualizarDatosMateria(usuarioId, idMateria, nota, dificultad);
////
////        // Ejecución
////        String viewName = controladorProgresoAcademico.actualizarDatosMateria(nota, dificultad, idMateria, mockHttpSession, redirectAttributes);
////
////        // Verificación
////        assertThat(viewName, is("redirect:/progreso"));
////        verify(mockServicioProgreso, times(1)).actualizarDatosMateria(usuarioId, idMateria, nota, dificultad);
////    }
//
////    queAlAccederAProgresoSeMuestrenTodasLasMateriasSiNoHayCondicion
////            queAlAccederAProgresoConCondicionSeFiltrenLasMaterias
////    queAlAccederAProgresoConCondicionInvalidaSeMuestrenTodasLasMaterias
////            queAlActualizarDatosDeMateriaRedirijaAProgresoYLlameAlServicio
////    queAlActualizarDatosDeMateriaSinNotaODificultadIgualRedirijaAProgreso
////            queSiNoHayUsuarioEnSesionAlVerProgresoNoSeLlamenServicios
////    queSiNoHayUsuarioEnSesionAlActualizarMateriaNoSeLlamenServicios
////            queLaVistaContengaLosAtributosEsperadosAlVerProgreso
////    queLaVistaContengaLosAtributosEsperadosAlVerProgresoConFiltro
//
////    @Test
////    public void queSePuedaObtenerTodasLasMateriasDeLaCarrera(){
////
////        List<Materia> materiasEsperadas = new ArrayList<>();
////
////        Materia materia1 = new Materia();
////        materia1.setNombre("Programacion Web Avanzada");;
////        materia1.setCuatrimestre(1);
////        materiasEsperadas.add(materia1);
////
////        Materia materia2 = new Materia();
////        materia2.setNombre("Base de datos");;
////        materia2.setCuatrimestre(2);
////        materiasEsperadas.add(materia2);
////
////        when(servicioMateria.obtenerTodasLasMaterias()).thenReturn(materiasEsperadas);
////
////        ModelAndView mav = controladorProgreso.verProgreso( "",httpSession);
////
////        verify(servicioMateria, times(1)).obtenerTodasLasMaterias();
////
////        assertThat(mav.getViewName(), is("progreso"));
////
////        assertThat(mav.getModel().containsKey("carrera"), is(true));
////
//////        assertThat((Carrera) mav.getModel().get("carrera"));
////
////        assertThat(mav.getModel().containsKey("materias"), is(true));
////        assertThat((List<Materia>) mav.getModel().get("materias"), is(materiasEsperadas));
////
////        assertThat(((List<Materia>) mav.getModel().get("materias")).size(), is(2));
////
////        assertThat((List<Materia>) mav.getModel().get("materias"), hasItem(hasProperty("nombre", is("Programacion Web Avanzada"))));
////    }
//
//}
