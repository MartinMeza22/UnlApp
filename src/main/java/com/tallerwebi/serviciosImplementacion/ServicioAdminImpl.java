package com.tallerwebi.serviciosImplementacion;

import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import com.tallerwebi.repositorioInterfaz.RepositorioPublicacion;
import com.tallerwebi.repositorioInterfaz.RepositorioCarrera;
import com.tallerwebi.servicioInterfaz.ServicioAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service("servicioAdmin")
@Transactional(readOnly = true)
public class ServicioAdminImpl implements ServicioAdmin {

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioPublicacion repositorioPublicacion;
    private final RepositorioCarrera repositorioCarrera;

    @Autowired
    public ServicioAdminImpl(RepositorioUsuario repoUsuario,
                             RepositorioPublicacion repoPublicacion,
                             RepositorioCarrera repoCarrera) {
        this.repositorioUsuario      = repoUsuario;
        this.repositorioPublicacion  = repoPublicacion;
        this.repositorioCarrera      = repoCarrera;
    }

    @Override
    public Map<String, Long> obtenerUsuariosPorCarrera() {
        // HQL: COUNT usuarios por carrera
        List<Object[]> raw = repositorioUsuario
                .countUsuariosGroupByCarrera(); // <-- crea método proyectado en el repositorio.
        return toLinkedMap(raw);
    }

    @Override
    public Map<String, Long> obtenerUsuariosPorGenero() {
        List<Object[]> raw = repositorioUsuario
                .countUsuariosGroupBySexo();    // <-- agrega en el repositorio.
        return toLinkedMap(raw);
    }

    @Override
    public Map<String, Long> obtenerPublicacionesPorCarrera() {
        List<Object[]> raw = repositorioPublicacion
                .countPublicacionesGroupByCarrera(); // <-- agrega en su repositorio.
        return toLinkedMap(raw);
    }

    /* -------------------------------------------------- */
    /* Helpers                                            */
    /* -------------------------------------------------- */
    private Map<String, Long> toLinkedMap(List<Object[]> raw) {
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] row : raw) {
            map.put(String.valueOf(row[0]), ((Number) row[1]).longValue());
        }
        return map;
    }
}
