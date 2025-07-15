package com.tallerwebi.presentacion;
import com.tallerwebi.repositorioInterfaz.RepositorioCarrera;
import com.tallerwebi.repositorioInterfaz.RepositorioPublicacion;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import com.tallerwebi.serviciosImplementacion.ServicioAdminImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class ServicioAdminTest {

    private RepositorioUsuario repositorioUsuarioMock;
    private RepositorioPublicacion repositorioPublicacionMock;
    private RepositorioCarrera repositorioCarreraMock;

    private ServicioAdminImpl servicio;

    @BeforeEach
    public void setUp() {
        repositorioUsuarioMock = mock(RepositorioUsuario.class);
        repositorioPublicacionMock = mock(RepositorioPublicacion.class);
        repositorioCarreraMock = mock(RepositorioCarrera.class);

        servicio = new ServicioAdminImpl(
                repositorioUsuarioMock,
                repositorioPublicacionMock,
                repositorioCarreraMock
        );
    }

    @Test
    public void queObtengaUsuariosPorCarrera() {
        // Simular resultado de HQL: List<Object[]>
        List<Object[]> datosMock = List.of(
                new Object[]{"Ingeniería", 10L},
                new Object[]{"Derecho", 5L}
        );

        when(repositorioUsuarioMock.countUsuariosGroupByCarrera()).thenReturn(datosMock);

        Map<String, Long> resultado = servicio.obtenerUsuariosPorCarrera();

        assertThat(resultado.size(), is(2));
        assertThat(resultado, hasEntry("Ingeniería", 10L));
        assertThat(resultado, hasEntry("Derecho", 5L));
        verify(repositorioUsuarioMock).countUsuariosGroupByCarrera();
    }
}

