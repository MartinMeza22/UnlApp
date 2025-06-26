package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.DTO.MateriaDTO;
import com.tallerwebi.dominio.DTO.ProgresoDTO;
import com.tallerwebi.dto.MateriasDTO;
import com.tallerwebi.dto.MateriasWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class ControladorProgresoTest {

    private ControladorProgresoAcademico controladorProgresoAcademico;
    private ServicioUsuarioMateria servicioUsuarioMateriaMock;
    private ServicioProgreso servicioProgresoMock;
    private ServicioMateria servicioMateriaMock;
    private HttpSession sessionMock;
    private RedirectAttributes redirectAttributesMock;


    @BeforeEach
    public void init() {
        servicioUsuarioMateriaMock = mock(ServicioUsuarioMateria.class);
        servicioProgresoMock = mock(ServicioProgreso.class);
        servicioMateriaMock = mock(ServicioMateria.class);
        sessionMock = mock(HttpSession.class);
        redirectAttributesMock = mock(RedirectAttributes.class);
        controladorProgresoAcademico = new ControladorProgresoAcademico(servicioProgresoMock, servicioMateriaMock, servicioUsuarioMateriaMock);
    }

    @Test
    public void queCuandoIngreseAverProgresoSinFiltrosDeberiaMostrarTodasLasMateriasYProgreso() {
        Long usuarioId = 1L;
        String idCarrera = "1";
        Usuario usuario = new Usuario();
        usuario.setCarreraID(1L);
        List<MateriaDTO> materias = new ArrayList<>();
        materias.add(new MateriaDTO());
        materias.add(new MateriaDTO());
        List<Integer> cuatrimestres = Arrays.asList(1, 2, 3);

        when(sessionMock.getAttribute("ID")).thenReturn(usuarioId);
        when(servicioUsuarioMateriaMock.obtenerUsuario(usuarioId)).thenReturn(usuario);
        when(servicioMateriaMock.obtenerCantidadDeCuatrimestres()).thenReturn(cuatrimestres);
        when(servicioProgresoMock.materias(idCarrera, usuarioId)).thenReturn(materias);
        when(servicioProgresoMock.obtenerProgresoDeCarrera(idCarrera, usuarioId)).thenReturn(75.0);
        when(servicioProgresoMock.filtrarPor(idCarrera, "aprobadas", usuarioId)).thenReturn(Arrays.asList(new MateriaDTO()));
        when(servicioProgresoMock.filtrarPor(idCarrera, "cursando", usuarioId)).thenReturn(Arrays.asList(new MateriaDTO(), new MateriaDTO()));
        when(servicioProgresoMock.obtenerPorcentajeDeMateriasDesaprobadas(idCarrera, usuarioId)).thenReturn(10.0);
        when(servicioProgresoMock.obtenerPorcentajeDeMateriasAprobadas(idCarrera, usuarioId)).thenReturn(60.0);

        ModelAndView mav = controladorProgresoAcademico.verProgreso(null, null, sessionMock);

        assertThat(mav.getViewName(), equalToIgnoringCase("progreso"));
        ModelMap model = mav.getModelMap();
        assertThat(model.get("materiasTotales"), is(materias));
        assertThat(model.get("porcentajeCarrera"), is(75.0));
        assertThat(model.get("cantidadMateriasAprobadas"), is(1));
        assertThat(model.get("cantidadMateriasTotales"), is(2));
        assertThat(model.get("materiasEnCurso"), is(2));
        assertThat(model.get("porcentajeDesaprobadas"), is(10.0));
        assertThat(model.get("porcentajeAprobadas"), is(60.0));
        assertThat(model.get("cuatrimestresDisponibles"), is(4));
        verify(servicioUsuarioMateriaMock, times(1)).obtenerUsuario(usuarioId);
        verify(servicioMateriaMock, times(1)).obtenerCantidadDeCuatrimestres();
        verify(servicioProgresoMock, times(2)).materias(idCarrera, usuarioId);
        verify(servicioProgresoMock, times(1)).obtenerProgresoDeCarrera(idCarrera, usuarioId);
        verify(servicioProgresoMock, times(1)).filtrarPor(idCarrera, "aprobadas", usuarioId);
        verify(servicioProgresoMock, times(1)).filtrarPor(idCarrera, "cursando", usuarioId);
        verify(servicioProgresoMock, times(1)).obtenerPorcentajeDeMateriasDesaprobadas(idCarrera, usuarioId);
        verify(servicioProgresoMock, times(1)).obtenerPorcentajeDeMateriasAprobadas(idCarrera, usuarioId);
    }

    @Test
    public void queCuandoSeIngreseFiltroPorCondicionDeMateriasAprobadasSeDebeMostrarMateriasFiltradasPorAprobadas() {
        Long usuarioId = 1L;
        String idCarrera = "1";
        Usuario usuario = new Usuario();
        usuario.setCarreraID(1L);
        List<MateriaDTO> materiasAprobadas = new ArrayList<>();
        materiasAprobadas.add(new MateriaDTO());
        List<Integer> cuatrimestres = Arrays.asList(1, 2, 3);

        when(sessionMock.getAttribute("ID")).thenReturn(usuarioId);
        when(servicioUsuarioMateriaMock.obtenerUsuario(usuarioId)).thenReturn(usuario);
        when(servicioMateriaMock.obtenerCantidadDeCuatrimestres()).thenReturn(cuatrimestres);
        when(servicioProgresoMock.filtrarPor(idCarrera, "aprobadas", usuarioId)).thenReturn(materiasAprobadas);
        when(servicioProgresoMock.materias(idCarrera, usuarioId)).thenReturn(Arrays.asList(new MateriaDTO(), new MateriaDTO())); // Total de materias
        when(servicioProgresoMock.obtenerProgresoDeCarrera(idCarrera, usuarioId)).thenReturn(50.0);
        when(servicioProgresoMock.filtrarPor(idCarrera, "cursando", usuarioId)).thenReturn(Arrays.asList(new MateriaDTO()));
        when(servicioProgresoMock.obtenerPorcentajeDeMateriasDesaprobadas(idCarrera, usuarioId)).thenReturn(5.0);
        when(servicioProgresoMock.obtenerPorcentajeDeMateriasAprobadas(idCarrera, usuarioId)).thenReturn(45.0);


        ModelAndView mav = controladorProgresoAcademico.verProgreso("aprobadas", null, sessionMock);

        assertThat(mav.getViewName(), equalToIgnoringCase("progreso"));
        ModelMap model = mav.getModelMap();
        assertThat(model.get("materiasTotales"), is(materiasAprobadas));
        assertThat(model.get("selectedCondicion").toString(), equalToIgnoringCase("aprobadas"));
        verify(servicioProgresoMock, times(1)).materias(idCarrera, usuarioId);
    }

    @Test
    public void vqueCuandoSeIngreseFiltroPorCuatrimestre1DeberiaMostrarMateriasFiltradasPorPrimerCuatrimestre() {
        Long usuarioId = 1L;
        String idCarrera = "1";
        Usuario usuario = new Usuario();
        usuario.setCarreraID(1L);
        List<MateriaDTO> materiasCuatrimestre = new ArrayList<>();
        materiasCuatrimestre.add(new MateriaDTO());
        materiasCuatrimestre.add(new MateriaDTO());
        List<Integer> cuatrimestres = Arrays.asList(1, 2, 3);


        when(sessionMock.getAttribute("ID")).thenReturn(usuarioId);
        when(servicioUsuarioMateriaMock.obtenerUsuario(usuarioId)).thenReturn(usuario);
        when(servicioMateriaMock.obtenerCantidadDeCuatrimestres()).thenReturn(cuatrimestres);
        when(servicioProgresoMock.filtrarPorCuatrimestre(idCarrera, 1, usuarioId)).thenReturn(materiasCuatrimestre);
        when(servicioProgresoMock.materias(idCarrera, usuarioId)).thenReturn(Arrays.asList(new MateriaDTO(), new MateriaDTO(), new MateriaDTO())); // Total de materias
        when(servicioProgresoMock.obtenerProgresoDeCarrera(idCarrera, usuarioId)).thenReturn(60.0);
        when(servicioProgresoMock.filtrarPor(idCarrera, "aprobadas", usuarioId)).thenReturn(Arrays.asList(new MateriaDTO()));
        when(servicioProgresoMock.filtrarPor(idCarrera, "cursando", usuarioId)).thenReturn(Arrays.asList(new MateriaDTO()));
        when(servicioProgresoMock.obtenerPorcentajeDeMateriasDesaprobadas(idCarrera, usuarioId)).thenReturn(8.0);
        when(servicioProgresoMock.obtenerPorcentajeDeMateriasAprobadas(idCarrera, usuarioId)).thenReturn(52.0);


        ModelAndView mav = controladorProgresoAcademico.verProgreso(null, 1, sessionMock);

        assertThat(mav.getViewName(), equalToIgnoringCase("progreso"));
        ModelMap model = mav.getModelMap();
        assertThat(model.get("materiasTotales"), is(materiasCuatrimestre));
        assertThat(model.get("selectedCuatrimestre"), is(cuatrimestres));
        verify(servicioProgresoMock, times(1)).filtrarPorCuatrimestre(idCarrera, 1, usuarioId);
        verify(servicioProgresoMock, times(1)).materias(idCarrera, usuarioId);
    }

    @Test
    public void queCuandoSeIngreseFiltroPorCondicionAprobadasYCuatrimestre1DeberiaMostrarMateriasFiltradasPorMateriasAprobadasEnElPrimerCuatrimestre() {
        Long usuarioId = 1L;
        String idCarrera = "1";
        Usuario usuario = new Usuario();
        usuario.setCarreraID(1L);
        List<MateriaDTO> materiasFiltradas = new ArrayList<>();
        materiasFiltradas.add(new MateriaDTO());
        List<Integer> cuatrimestres = Arrays.asList(1, 2, 3);


        when(sessionMock.getAttribute("ID")).thenReturn(usuarioId);
        when(servicioUsuarioMateriaMock.obtenerUsuario(usuarioId)).thenReturn(usuario);
        when(servicioMateriaMock.obtenerCantidadDeCuatrimestres()).thenReturn(cuatrimestres);
        when(servicioProgresoMock.filtrarPorCuatrimestreYEstado(idCarrera, 1, "aprobadas", usuarioId)).thenReturn(materiasFiltradas);
        when(servicioProgresoMock.materias(idCarrera, usuarioId)).thenReturn(Arrays.asList(new MateriaDTO(), new MateriaDTO(), new MateriaDTO(), new MateriaDTO())); // Total de materias
        when(servicioProgresoMock.obtenerProgresoDeCarrera(idCarrera, usuarioId)).thenReturn(80.0);
        when(servicioProgresoMock.filtrarPor(idCarrera, "aprobadas", usuarioId)).thenReturn(Arrays.asList(new MateriaDTO(), new MateriaDTO()));
        when(servicioProgresoMock.filtrarPor(idCarrera, "cursando", usuarioId)).thenReturn(Arrays.asList(new MateriaDTO()));
        when(servicioProgresoMock.obtenerPorcentajeDeMateriasDesaprobadas(idCarrera, usuarioId)).thenReturn(2.0);
        when(servicioProgresoMock.obtenerPorcentajeDeMateriasAprobadas(idCarrera, usuarioId)).thenReturn(78.0);



        ModelAndView mav = controladorProgresoAcademico.verProgreso("aprobadas", 1, sessionMock);

        assertThat(mav.getViewName(), equalToIgnoringCase("progreso"));
        ModelMap model = mav.getModelMap();
        assertThat(model.get("materiasTotales"), is(materiasFiltradas));
        assertThat(model.get("selectedCondicion").toString(), equalToIgnoringCase("aprobadas"));
        assertThat(model.get("selectedCuatrimestre"), is(cuatrimestres));
        verify(servicioProgresoMock, times(1)).filtrarPorCuatrimestreYEstado(idCarrera, 1, "aprobadas", usuarioId);
        verify(servicioProgresoMock, times(1)).materias(idCarrera, usuarioId);
    }

    @Test
    public void queCuandoElUsuarioGuardeMateriaConNotaYDificultadDeberiaAsignarALaMateriaCorrectamente() {
        Long usuarioId = 1L;
        MateriasWrapper listadoMaterias = new MateriasWrapper();
        MateriasDTO materia1 = new MateriasDTO();
        materia1.setId(10L);
        materia1.setNota(8);
        materia1.setDificultad(3);
        materia1.setEstado(1);
        MateriasDTO materia2 = new MateriasDTO();
        materia2.setId(20L);
        materia2.setEstado(2);
        listadoMaterias.setMaterias(Arrays.asList(materia1, materia2));

        when(sessionMock.getAttribute("ID")).thenReturn(usuarioId);

        ModelAndView mav = controladorProgresoAcademico.guardarMateria(listadoMaterias, new DatosLogin(), sessionMock);

        verify(servicioUsuarioMateriaMock, times(1)).asignarMateria(usuarioId, 10L, 8, 3, 1);
        verify(servicioUsuarioMateriaMock, times(1)).asignarMateria(usuarioId, 20L, 2);
    }

    @Test
    public void queCuandoElUsuarioDeseeActualizarDatosDeUnaMateriaQueSeGuardenLosCambiosYSeActualizenLosDatos() {
        Long usuarioId = 1L;
        Long idMateria = 10L;
        Integer nota = 7;
        Integer dificultad = 4;
        String action = "guardarCambios";

        when(sessionMock.getAttribute("ID")).thenReturn(usuarioId);

        String viewName = controladorProgresoAcademico.actualizarDatosMateria(nota, dificultad, action, idMateria, sessionMock, redirectAttributesMock);

        assertThat(viewName, equalToIgnoringCase("redirect:/progreso"));
        verify(servicioProgresoMock, times(1)).actualizarDatosMateria(usuarioId, idMateria, nota, dificultad);
    }

    @Test
    public void queCuandoElUsuarioDeseeDejarUnaMateriaEsaMateriaQuedeComoPendiente() {
        Long usuarioId = 1L;
        Long idMateria = 10L;
        String action = "dejarDeCursar";

        when(sessionMock.getAttribute("ID")).thenReturn(usuarioId);

        String viewName = controladorProgresoAcademico.actualizarDatosMateria(null, null, action, idMateria, sessionMock, redirectAttributesMock);

        assertThat(viewName, equalToIgnoringCase("redirect:/progreso"));
        verify(servicioProgresoMock, times(1)).marcarMateriaComoPendiente(idMateria, usuarioId);
    }

    @Test
    public void cursarMateriaExitoDeberiaMarcarMateriaComoCursandoYRedirigir() {
        Long usuarioId = 1L;
        Long idMateria = 10L;

        when(sessionMock.getAttribute("ID")).thenReturn(usuarioId);
        when(servicioProgresoMock.marcarMateriaComoCursando(usuarioId, idMateria)).thenReturn(true);

        String viewName = controladorProgresoAcademico.cursarMateria(idMateria, sessionMock, redirectAttributesMock);

        assertThat(viewName, equalToIgnoringCase("redirect:/progreso"));
        verify(servicioProgresoMock, times(1)).marcarMateriaComoCursando(usuarioId, idMateria);
        verify(redirectAttributesMock, never()).addFlashAttribute(anyString(), anyString()); // No debería agregar atributos de error
    }

    @Test
    public void cursarMateriaFalloDeberiaAgregarMensajeDeErrorYRedirigir() {
        Long usuarioId = 1L;
        Long idMateria = 10L;

        when(sessionMock.getAttribute("ID")).thenReturn(usuarioId);
        when(servicioProgresoMock.marcarMateriaComoCursando(usuarioId, idMateria)).thenReturn(false);

        String viewName = controladorProgresoAcademico.cursarMateria(idMateria, sessionMock, redirectAttributesMock);

        assertThat(viewName, equalToIgnoringCase("redirect:/progreso"));
        verify(servicioProgresoMock, times(1)).marcarMateriaComoCursando(usuarioId, idMateria);
        verify(redirectAttributesMock, times(1)).addFlashAttribute("error", "No se pudo cursar la materia. Verifique las correlativas.");
    }
}