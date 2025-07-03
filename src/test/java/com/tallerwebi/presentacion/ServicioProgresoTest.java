package com.tallerwebi.presentacion; // O el paquete donde tengas tus tests

import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.dominio.DTO.MateriaDTO;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.UsuarioMateria;
import com.tallerwebi.dominio.servicios.ServicioProgreso;
import com.tallerwebi.repositorioInterfaz.RepositorioMateria;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuarioMateria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class ServicioProgresoTest {

    private ServicioProgreso servicioProgreso;
    private RepositorioUsuarioMateria repositorioUsuarioMateriaMock;
    private RepositorioMateria repositorioMateriaMock;
    private RepositorioUsuario repositorioUsuarioMock;

    private final Long ID_USUARIO = 1L;
    private final String ID_CARRERA_STRING = "1";
    private final Long ID_CARRERA_LONG = 1L;
    private Carrera carreraDePrueba;

    @BeforeEach
    public void init() {
        repositorioUsuarioMateriaMock = mock(RepositorioUsuarioMateria.class);
        repositorioMateriaMock = mock(RepositorioMateria.class);
        repositorioUsuarioMock = mock(RepositorioUsuario.class);

        servicioProgreso = new ServicioProgreso(repositorioUsuarioMateriaMock, repositorioMateriaMock, repositorioUsuarioMock);

        carreraDePrueba = new Carrera();
        carreraDePrueba.setId(ID_CARRERA_LONG);
        carreraDePrueba.setNombre("Sistemas");
    }

    @Test
    public void queAlPedirMateriasSeConstruyaElDTOCombinandoDatosDeRepositorios() {
        Materia materia1 = new Materia();
        materia1.setId(101L);
        materia1.setNombre("Matemática");
        materia1.setCuatrimestre(1);
        materia1.setCarrera(carreraDePrueba);

        Materia materia2 = new Materia();
        materia2.setId(102L);
        materia2.setNombre("Programación");
        materia2.setCuatrimestre(1);
        materia2.setCorrelativa1("101");
        materia2.setCarrera(carreraDePrueba);

        Materia materia3 = new Materia();
        materia3.setId(103L);
        materia3.setNombre("Laboratorio");
        materia3.setCuatrimestre(2);
        materia3.setCarrera(carreraDePrueba);

        List<Materia> todasLasMateriasDeCarrera = Arrays.asList(materia1, materia2, materia3);

        Usuario usuario = new Usuario();
        UsuarioMateria usuarioMateria1 = new UsuarioMateria(usuario, materia1, 8);
        List<UsuarioMateria> materiasDelUsuario = Collections.singletonList(usuarioMateria1);

        when(repositorioMateriaMock.obtenerMateriasDeUnaCarrera(ID_CARRERA_STRING)).thenReturn(todasLasMateriasDeCarrera);
        when(repositorioUsuarioMateriaMock.buscarPorUsuario(ID_CARRERA_STRING, ID_USUARIO)).thenReturn(materiasDelUsuario);

        List<MateriaDTO> resultado = servicioProgreso.materias(ID_CARRERA_STRING, ID_USUARIO);

        assertThat(resultado, hasSize(3));
        MateriaDTO dtoMatematica = resultado.get(0);
        assertThat(dtoMatematica.getNombre(), equalTo("Matemática"));
        assertThat(dtoMatematica.getEstado(), equalTo("APROBADA"));
        assertThat(dtoMatematica.getEsCursable(), is(true));

        MateriaDTO dtoProgramacion = resultado.get(1);
        assertThat(dtoProgramacion.getNombre(), equalTo("Programación"));
        assertThat(dtoProgramacion.getEstado(), equalTo("PENDIENTE"));
        assertThat(dtoProgramacion.getEsCursable(), is(true));
    }

    @Test
    public void queUnaMateriaNoSeaCursableSiSuCorrelativaNoEstaAprobada() {
        Materia materia1 = new Materia();
        materia1.setId(101L);
        materia1.setNombre("Matemática");
        materia1.setCarrera(carreraDePrueba);

        Materia materia2 = new Materia();
        materia2.setId(102L);
        materia2.setNombre("Programación");
        materia2.setCorrelativa1("101");
        materia2.setCarrera(carreraDePrueba);

        Usuario usuario = new Usuario();
        UsuarioMateria usuarioMateria1 = new UsuarioMateria(usuario, materia1, 3); // Desaprobada

        when(repositorioMateriaMock.obtenerMateriasDeUnaCarrera(ID_CARRERA_STRING)).thenReturn(Arrays.asList(materia1, materia2));
        when(repositorioUsuarioMateriaMock.buscarPorUsuario(ID_CARRERA_STRING, ID_USUARIO)).thenReturn(Collections.singletonList(usuarioMateria1));

        List<MateriaDTO> resultado = servicioProgreso.materias(ID_CARRERA_STRING, ID_USUARIO);

        MateriaDTO dtoProgramacion = resultado.get(1);
        assertThat(dtoProgramacion.getEsCursable(), is(false));
    }

    @Test
    public void queAlObtenerProgresoDeCarreraCalculeElPorcentajeCorrectoDeAprobadas() {
        Materia materia1 = new Materia();
        materia1.setId(101L);
        Materia materia2 = new Materia();
        materia2.setId(102L);
        Materia materia3 = new Materia();
        materia3.setId(103L);
        Materia materia4 = new Materia();
        materia4.setId(104L);

        Usuario usuario = new Usuario();
        UsuarioMateria usuarioMateria1 = new UsuarioMateria(usuario, materia1, 7);

        when(repositorioMateriaMock.obtenerMateriasDeUnaCarrera(ID_CARRERA_STRING)).thenReturn(Arrays.asList(materia1, materia2, materia3, materia4));
        when(repositorioUsuarioMateriaMock.buscarPorUsuario(ID_CARRERA_STRING, ID_USUARIO)).thenReturn(Collections.singletonList(usuarioMateria1));

        Double progreso = servicioProgreso.obtenerProgresoDeCarrera(ID_CARRERA_STRING, ID_USUARIO);

        assertThat(progreso, equalTo(25.0));
    }

    @Test
    public void queSePuedaMarcarMateriaComoCursandoSiCumpleCorrelativas() {
        Usuario usuario = new Usuario();
        usuario.setId(ID_USUARIO);
        usuario.setCarrera(carreraDePrueba);

        Materia correlativa = new Materia();
        correlativa.setId(101L);
        correlativa.setCarrera(carreraDePrueba);

        Materia aCursar = new Materia();
        aCursar.setId(102L);
        aCursar.setCorrelativa1("101");
        aCursar.setCarrera(carreraDePrueba);

        UsuarioMateria correlativaAprobada = new UsuarioMateria(usuario, correlativa, 9);

        when(repositorioUsuarioMock.buscarPorId(ID_USUARIO)).thenReturn(usuario);
        when(repositorioMateriaMock.buscarPorId(aCursar.getId())).thenReturn(aCursar);
        when(repositorioUsuarioMateriaMock.buscarPorUsuarioYMateria(ID_USUARIO, aCursar.getId())).thenReturn(null);
        when(repositorioUsuarioMateriaMock.buscarPorUsuario(ID_CARRERA_STRING, ID_USUARIO)).thenReturn(Collections.singletonList(correlativaAprobada));

        boolean resultado = servicioProgreso.marcarMateriaComoCursando(ID_USUARIO, aCursar.getId());

        assertThat(resultado, is(true));
    }
}