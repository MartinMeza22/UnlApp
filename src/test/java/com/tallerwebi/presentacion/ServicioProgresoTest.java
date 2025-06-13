package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.DTO.MateriaDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServicioProgresoTest {


    private ServicioProgreso servicioProgreso;

    private RepositorioUsuarioMateria mockRepositorioUsuarioMateria;
    private RepositorioMateria mockRepositorioMateria;
    private RepositorioUsuario mockRepositorioUsuario;

    private Usuario usuario;
    private Materia materia1;
    private Materia materia2;
    private Materia materia3;
    private UsuarioMateria usuarioMateria1;
    private UsuarioMateria usuarioMateria2;
    private String idCarrera = "1";

    @BeforeEach
    public void init() {
        mockRepositorioUsuarioMateria = mock(RepositorioUsuarioMateria.class);
        mockRepositorioMateria = mock(RepositorioMateria.class);
        mockRepositorioUsuario = mock(RepositorioUsuario.class);

        servicioProgreso = new ServicioProgreso(mockRepositorioUsuarioMateria, mockRepositorioMateria, mockRepositorioUsuario);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@test.com");

        materia1 = new Materia();
        materia1.setId(101L);
        materia1.setNombre("Matematicas");
        materia1.setCuatrimestre(1);

        materia2 = new Materia();
        materia2.setId(102L);
        materia2.setNombre("Base de datos");
        materia2.setCuatrimestre(2);
        materia2.setCorrelativa1("101");

        materia3 = new Materia();
        materia3.setId(103L);
        materia3.setNombre("Base de datos 2");
        materia3.setCuatrimestre(3);
        materia3.setCorrelativa2("102");

        usuarioMateria1 = new UsuarioMateria(usuario, materia1, 7);
        usuarioMateria1.setDificultad(1);

        usuarioMateria2 = new UsuarioMateria(usuario, materia2, null);
        usuarioMateria2.setDificultad(5);
    }

    @Test
    public void queAlPedirTodasLasMateriasSeDevuelvanCorrectamenteConSuEstadoYDatos() {
        List<Materia> todasLasMaterias = Arrays.asList(materia1, materia2, materia3);
        List<UsuarioMateria> materiasCursadas = Arrays.asList(usuarioMateria1, usuarioMateria2);

        when(mockRepositorioMateria.buscarTodas()).thenReturn(todasLasMaterias);
        when(mockRepositorioUsuarioMateria.buscarPorUsuario(idCarrera, usuario.getId())).thenReturn(materiasCursadas);

        List<MateriaDTO> resultado = this.servicioProgreso.materias(idCarrera, usuario.getId());

        assertThat(resultado, hasSize(3));

        // Asserts para materia1
        MateriaDTO materiaDTO1 = resultado.stream().filter(mat -> mat.getId().equals(materia1.getId())).findFirst().orElse(null);
        assertThat(materiaDTO1, is(notNullValue()));
        assertThat(materiaDTO1.getNombre(), is("Matematicas"));
        assertThat(materiaDTO1.getEstado(), is("APROBADA"));
        assertThat(materiaDTO1.getCuatrimestre(), is(1));
        assertThat(materiaDTO1.getDificultad(), is("Facil"));

        // Asserts para materia2
        MateriaDTO materiaDTO2 = resultado.stream().filter(mat -> mat.getId().equals(materia2.getId())).findFirst().orElse(null);
        assertThat(materiaDTO2, is(notNullValue()));
        assertThat(materiaDTO2.getNombre(), is("Base de datos"));
        assertThat(materiaDTO2.getEstado(), is("CURSANDO"));
        assertThat(materiaDTO2.getCuatrimestre(), is(2));
        assertThat(materiaDTO2.getDificultad(), is("Medio"));

    }

    @Test
    public void queAlFiltrarPorAprobadasSeDevuelvanSoloLasMateriasAprobadas() {
        List<Materia> todasLasMaterias = Arrays.asList(materia1, materia2, materia3);
        usuarioMateria2.setNota(8);
        List<UsuarioMateria> materiasCursadas = Arrays.asList(usuarioMateria1, usuarioMateria2);

        when(mockRepositorioMateria.buscarTodas()).thenReturn(todasLasMaterias);
        when(mockRepositorioUsuarioMateria.buscarPorUsuario(idCarrera, usuario.getId())).thenReturn(materiasCursadas);

        List<MateriaDTO> materiasAprobadas = this.servicioProgreso.filtrarPor(idCarrera, "APROBADAS", usuario.getId());

        assertThat(materiasAprobadas, hasSize(2));
        assertThat(materiasAprobadas.get(1).getNota(), is(8));

        assertTrue(materiasAprobadas.stream().allMatch(mat -> mat.getNota() != null && mat.getNota() >= 4));

    }

    @Test
    public void queAlActualizarDatosDeMateriaExistenteSeActualiceCorrectamente() {
        Integer nuevaNota = 1;
        Integer nivelDificultad = 1;

        when(mockRepositorioUsuarioMateria.existe(usuario.getId(), materia1.getId())).thenReturn(true);
        when(mockRepositorioUsuarioMateria.buscarPorUsuarioYMateria(usuario.getId(), materia1.getId())).thenReturn(usuarioMateria1);

        Boolean resultado = this.servicioProgreso.actualizarDatosMateria(usuario.getId(), materia1.getId(), nuevaNota, nivelDificultad);

        assertTrue(resultado);
        assertThat(usuarioMateria1.getNota(), is(nuevaNota));
        assertThat(usuarioMateria1.getDificultad(), is(nivelDificultad));
    }

    @Test
    public void queAlActualizarDatosDeMateriaNoExistenteSeGuardeNuevaEntrada() {
        Integer nuevaNota = 6;
        Integer nivelDificultad = 1;

        when(mockRepositorioUsuarioMateria.existe(usuario.getId(), materia3.getId())).thenReturn(false);
        when(mockRepositorioUsuario.buscarPorId(usuario.getId())).thenReturn(usuario);
        when(mockRepositorioMateria.buscarPorId(materia3.getId())).thenReturn(materia3);

        Boolean resultado = this.servicioProgreso.actualizarDatosMateria(usuario.getId(), materia3.getId(), nuevaNota, nivelDificultad);

        assertTrue(resultado);
    }

    @Test
    public void queAlFiltrarPorCursandoSeDevuelvanSoloLasMateriasCursando(){
        List<Materia> todasLasMaterias = Arrays.asList(materia1, materia2, materia3);
        List<UsuarioMateria> materiasCursadas = Arrays.asList(usuarioMateria1, usuarioMateria2);

        when(mockRepositorioMateria.buscarTodas()).thenReturn(todasLasMaterias);
        when(mockRepositorioUsuarioMateria.buscarPorUsuario(idCarrera, usuario.getId())).thenReturn(materiasCursadas);

        List<MateriaDTO> materiasCursando = this.servicioProgreso.filtrarPor(idCarrera, "CURSANDO", usuario.getId());

        assertThat(materiasCursando, hasSize(1));
        assertThat(materiasCursando.get(0).getNota(), is(nullValue()));
    }

//    queAlFiltrarPorPendientesSeDevuelvanSoloLasMateriasPendientes

    @Test
    public void queAlFiltrarPorPendientesSeDevuelvanSoloLasMateriasPendientes(){
        List<Materia> todasLasMaterias = Arrays.asList(materia1, materia2, materia3);
        List<UsuarioMateria> materiasCursadas = Arrays.asList(usuarioMateria1, usuarioMateria2);

        when(mockRepositorioMateria.buscarTodas()).thenReturn(todasLasMaterias);
        when(mockRepositorioUsuarioMateria.buscarPorUsuario(idCarrera, usuario.getId())).thenReturn(materiasCursadas);

        List<MateriaDTO> materiasPendientes = this.servicioProgreso.filtrarPor(idCarrera ,"PENDIENTES", usuario.getId());

        assertThat(materiasPendientes, hasSize(1));
        assertThat(materiasPendientes.get(0).getNota(), is(nullValue()));
        assertThat(materiasPendientes.get(0).getNombre(), is("Base de datos 2"));
    }

//            queAlFiltrarPorTodassSeDevuelvanTodasLasMaterias

    @Test
    public void queAlFiltrarPorTodassSeDevuelvanTodasLasMaterias(){
        List<Materia> todasLasMaterias = Arrays.asList(materia1, materia2, materia3);
        List<UsuarioMateria> materiasCursadas = Arrays.asList(usuarioMateria1, usuarioMateria2);

        when(mockRepositorioMateria.buscarTodas()).thenReturn(todasLasMaterias);
        when(mockRepositorioUsuarioMateria.buscarPorUsuario(idCarrera, usuario.getId())).thenReturn(materiasCursadas);

        List<MateriaDTO> todas = this.servicioProgreso.filtrarPor(idCarrera, "TODAS", usuario.getId());

        assertThat(todas, hasSize(3));
        assertThat(todas.get(0).getNombre(), is("Matematicas"));
    }

//    queAlActualizarDatosDeMateriaNoExistenteSeGuardeNuevaEntrada

    @Test
    public void queUnaMateriaSinCorrelativasSeConsidereCursable(){

    }

//            queUnaMateriaSinCorrelativasSeConsidereCursable
//    queUnaMateriaConCorrelativaAprobadaSeConsidereCursable
//            queUnaMateriaConCorrelativaPendienteSeConsiderePendiente
//    queUnaMateriaConMultiplesCorrelativasAprobadasSeConsidereCursable
//            queUnaMateriaConUnaCorrelativaPendienteEntreVariasSeConsiderePendiente
//    queVerificarDificultadDevuelvaFacilParaDificultadUno
//            queVerificarDificultadDevuelvaMedioParaDificultadCinco
//    queVerificarDificultadDevuelvaDificilParaDificultadDiez
//            queVerificarDificultadDevuelvaNullParaDificultadInvalida
//    queActualizarDatosMateriaLanceExcepcionSiAlgoFallaAlActualizar
//            queActualizarDatosMateriaLanceExcepcionSiAlgoFallaAlGuardar
//    queMateriasRetorneListaVaciaSiNoHayMateriasEnRepositorio
//            queMateriasRetorneSoloMateriasDelUsuarioSiExisten

}
