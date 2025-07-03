package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.DTO.MateriaDTO;
import com.tallerwebi.dominio.excepcion.CorrelatividadInvalidaException;
import com.tallerwebi.dominio.servicios.ServicioMateria;
import com.tallerwebi.dominio.servicios.ServicioProgreso;
import com.tallerwebi.dominio.servicios.ServicioUsuarioMateria;
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
import java.util.stream.Collectors;

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
    public void queCuandoSeIngreseFiltroPorCondicionDeMateriasAprobadasSeDebeMostrarMateriasFiltradasPorAprobadas() {
        Long usuarioId = 1L;
        String idCarrera = "1";
        Usuario usuario = new Usuario();
        Carrera carrera = new Carrera();
        carrera.setId(1L);
        usuario.setCarrera(carrera);
        List<Integer> cuatrimestres = Arrays.asList(1, 2, 3);

        // ARREGLO: Creamos datos de prueba que puedan ser filtrados por el decorador.
        MateriaDTO aprobada = new MateriaDTO();
        aprobada.setNota(8);
        aprobada.setEstado("APROBADA");

        MateriaDTO desaprobada = new MateriaDTO();
        desaprobada.setNota(2);
        desaprobada.setEstado("DESAPROBADA");

        List<MateriaDTO> todasLasMaterias = Arrays.asList(aprobada, desaprobada);
        List<MateriaDTO> materiasEsperadas = Arrays.asList(aprobada);

        when(sessionMock.getAttribute("ID")).thenReturn(usuarioId);
        when(servicioUsuarioMateriaMock.obtenerUsuario(usuarioId)).thenReturn(usuario);
        when(servicioMateriaMock.obtenerCantidadDeCuatrimestres()).thenReturn(cuatrimestres);
        // ARREGLO: Mockeamos el método `materias` para que devuelva la lista completa.
        when(servicioProgresoMock.materias(anyString(), anyLong())).thenReturn(todasLasMaterias);

        // Mocks para las estadísticas al final del método
        when(servicioProgresoMock.obtenerProgresoDeCarrera(idCarrera, usuarioId)).thenReturn(50.0);
        when(servicioProgresoMock.obtenerPorcentajeDeMateriasDesaprobadas(idCarrera, usuarioId)).thenReturn(5.0);
        when(servicioProgresoMock.obtenerPorcentajeDeMateriasAprobadas(idCarrera, usuarioId)).thenReturn(45.0);

        ModelAndView mav = controladorProgresoAcademico.verProgreso("aprobadas", null, sessionMock);

        assertThat(mav.getViewName(), equalToIgnoringCase("progreso"));
        ModelMap model = mav.getModelMap();
        // ARREGLO: Comparamos el resultado con nuestra lista esperada.
        assertThat(model.get("materiasTotales"), is(materiasEsperadas));
        assertThat(model.get("selectedCondicion").toString(), equalToIgnoringCase("aprobadas"));

        // ARREGLO: Se verifica que el método base `materias` fue llamado.
        verify(servicioProgresoMock, atLeastOnce()).materias(idCarrera, usuarioId);
    }

    @Test
    public void vqueCuandoSeIngreseFiltroPorCuatrimestre1DeberiaMostrarMateriasFiltradasPorPrimerCuatrimestre() {
        Long usuarioId = 1L;
        String idCarrera = "1";
        Usuario usuario = new Usuario();
        Carrera carrera = new Carrera();
        carrera.setId(1L);
        usuario.setCarrera(carrera);
        List<Integer> cuatrimestres = Arrays.asList(1, 2, 3);

        // ARREGLO: Creamos datos de prueba para el decorador de cuatrimestre.
        MateriaDTO materiaCuatri1 = new MateriaDTO();
        materiaCuatri1.setCuatrimestre(1);
        MateriaDTO materiaCuatri2 = new MateriaDTO();
        materiaCuatri2.setCuatrimestre(2);

        List<MateriaDTO> todasLasMaterias = Arrays.asList(materiaCuatri1, materiaCuatri2);
        List<MateriaDTO> materiasEsperadas = Arrays.asList(materiaCuatri1);

        when(sessionMock.getAttribute("ID")).thenReturn(usuarioId);
        when(servicioUsuarioMateriaMock.obtenerUsuario(usuarioId)).thenReturn(usuario);
        when(servicioMateriaMock.obtenerCantidadDeCuatrimestres()).thenReturn(cuatrimestres);
        // ARREGLO: Mockeamos el método `materias`.
        when(servicioProgresoMock.materias(anyString(), anyLong())).thenReturn(todasLasMaterias);

        // Mocks para estadísticas
        when(servicioProgresoMock.obtenerProgresoDeCarrera(idCarrera, usuarioId)).thenReturn(60.0);
        when(servicioProgresoMock.obtenerPorcentajeDeMateriasDesaprobadas(idCarrera, usuarioId)).thenReturn(8.0);
        when(servicioProgresoMock.obtenerPorcentajeDeMateriasAprobadas(idCarrera, usuarioId)).thenReturn(52.0);

        ModelAndView mav = controladorProgresoAcademico.verProgreso(null, 1, sessionMock);

        assertThat(mav.getViewName(), equalToIgnoringCase("progreso"));
        ModelMap model = mav.getModelMap();
        assertThat(model.get("materiasTotales"), is(materiasEsperadas));
        assertThat(model.get("selectedCuatrimestre"), is(cuatrimestres));
        // ARREGLO: Ya no verificamos `filtrarPorCuatrimestre`, sino que el resultado sea correcto.
        verify(servicioProgresoMock, atLeastOnce()).materias(idCarrera, usuarioId);
    }

    @Test
    public void queCuandoSeIngreseFiltroPorCondicionAprobadasYCuatrimestre1DeberiaMostrarMateriasFiltradasPorMateriasAprobadasEnElPrimerCuatrimestre() {
        Long usuarioId = 1L;
        String idCarrera = "1";
        Usuario usuario = new Usuario();
        Carrera carrera = new Carrera();
        carrera.setId(1L);
        usuario.setCarrera(carrera);
        List<Integer> cuatrimestres = Arrays.asList(1, 2, 3);

        // ARREGLO: Creamos datos para ambos decoradores.
        MateriaDTO aprobadaCuatri1 = new MateriaDTO();
        aprobadaCuatri1.setNota(10);
        aprobadaCuatri1.setCuatrimestre(1);

        MateriaDTO desaprobadaCuatri1 = new MateriaDTO();
        desaprobadaCuatri1.setNota(2);
        desaprobadaCuatri1.setCuatrimestre(1);

        MateriaDTO aprobadaCuatri2 = new MateriaDTO();
        aprobadaCuatri2.setNota(8);
        aprobadaCuatri2.setCuatrimestre(2);

        List<MateriaDTO> todasLasMaterias = Arrays.asList(aprobadaCuatri1, desaprobadaCuatri1, aprobadaCuatri2);
        List<MateriaDTO> materiasEsperadas = Arrays.asList(aprobadaCuatri1);

        when(sessionMock.getAttribute("ID")).thenReturn(usuarioId);
        when(servicioUsuarioMateriaMock.obtenerUsuario(usuarioId)).thenReturn(usuario);
        when(servicioMateriaMock.obtenerCantidadDeCuatrimestres()).thenReturn(cuatrimestres);
        // ARREGLO: Mockeamos `materias`.
        when(servicioProgresoMock.materias(anyString(), anyLong())).thenReturn(todasLasMaterias);

        // Mocks para estadísticas
        when(servicioProgresoMock.obtenerProgresoDeCarrera(idCarrera, usuarioId)).thenReturn(80.0);
        when(servicioProgresoMock.obtenerPorcentajeDeMateriasDesaprobadas(idCarrera, usuarioId)).thenReturn(2.0);
        when(servicioProgresoMock.obtenerPorcentajeDeMateriasAprobadas(idCarrera, usuarioId)).thenReturn(78.0);

        ModelAndView mav = controladorProgresoAcademico.verProgreso("aprobadas", 1, sessionMock);

        assertThat(mav.getViewName(), equalToIgnoringCase("progreso"));
        ModelMap model = mav.getModelMap();
        assertThat(model.get("materiasTotales"), is(materiasEsperadas));
        assertThat(model.get("selectedCondicion").toString(), equalToIgnoringCase("aprobadas"));
        assertThat(model.get("selectedCuatrimestre"), is(cuatrimestres));
        // ARREGLO: Ya no verificamos el método `filtrarPorCuatrimestreYEstado`.
        verify(servicioProgresoMock, atLeastOnce()).materias(idCarrera, usuarioId);
    }

    // ... Los demás tests que ya pasaban no necesitan cambios ...
    // (Omitidos por brevedad)

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
    public void queCuandoElUsuarioDeseeActualizarDatosDeUnaMateriaQueSeGuardenLosCambiosYSeActualizenLosDatos() throws CorrelatividadInvalidaException {
        Long usuarioId = 1L;
        Long idMateria = 10L;
        Integer nota = 7;
        Integer dificultad = 4;
        String action = "guardarCambios";

        when(sessionMock.getAttribute("ID")).thenReturn(usuarioId);

        String viewName = controladorProgresoAcademico.actualizarDatosMateria(nota, dificultad, action, idMateria, sessionMock, redirectAttributesMock);

        assertThat(viewName, equalToIgnoringCase("redirect:/progreso"));
    }

    @Test
    public void queCuandoElUsuarioDeseeDejarUnaMateriaEsaMateriaQuedeComoPendiente() throws CorrelatividadInvalidaException {
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