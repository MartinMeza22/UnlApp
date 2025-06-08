package com.tallerwebi.dominio;

import com.tallerwebi.dominio.DTO.MateriaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service("servicioProgreso")
@Transactional
public class ServicioProgreso {

    private RepositorioUsuarioMateria repositorioUsuarioMateria;
    private RepositorioMateria repositorioMateria;

    public ServicioProgreso() {
    }

    @Autowired
    public ServicioProgreso(RepositorioUsuarioMateria repositorioUsuarioMateria, RepositorioMateria repositorioMateria) {
        this.repositorioUsuarioMateria = repositorioUsuarioMateria;
        this.repositorioMateria = repositorioMateria;
    }

    public List<MateriaDTO> materias(Long idUsuario) {

        List<Materia> todasLasMaterias = this.repositorioMateria.buscarTodas();
        List<UsuarioMateria> materiasQueCursoElUsuario = this.repositorioUsuarioMateria.buscarPorUsuario(idUsuario);

        Map<Long, UsuarioMateria> materiasCursadasMap = new HashMap<>();
        for (UsuarioMateria um : materiasQueCursoElUsuario) {
            materiasCursadasMap.put(um.getMateria().getId(), um);
        }

        List<MateriaDTO> materiasDTO = new ArrayList<>();

        for (Materia mat : todasLasMaterias) {
            UsuarioMateria matCurso = materiasCursadasMap.get(mat.getId());

            MateriaDTO materiaDTO;
            if (matCurso != null) {
                // Asegúrate de que la dificultad se maneje correctamente, incluso si es null en la base de datos
                String dificultad = verificarDificultad(matCurso.getDificultad());
                materiaDTO = new MateriaDTO(mat.getId(), mat.getNombre(), dificultad, matCurso.getEstado(), matCurso.getNota(), mat.getCuatrimestre());
            } else {
                String estado;
                if (verificarCorrelativasAprobadas(mat, materiasCursadasMap)) {
                    estado = "PENDIENTE"; // Si puede cursar pero no la cursó, es pendiente.
                } else {
                    estado = "PENDIENTE"; // No cumple las correlativas, sigue pendiente.
                }
                // Si la materia no fue cursada, la nota y dificultad son null
                materiaDTO = new MateriaDTO(mat.getId(), mat.getNombre(), null, estado, null, mat.getCuatrimestre());
            }
            materiasDTO.add(materiaDTO);
        }

        return materiasDTO;
    }

    private boolean verificarCorrelativasAprobadas(Materia materia, Map<Long, UsuarioMateria> materiasCursadasMap) {
        List<Long> idsCorrelativas = new ArrayList<>();
        // Recolectar todos los IDs de las correlativas de la materia
        if (materia.getCorrelativa1() != null && !materia.getCorrelativa1().isEmpty())
            idsCorrelativas.add(Long.valueOf(materia.getCorrelativa1()));
        if (materia.getCorrelativa2() != null && !materia.getCorrelativa2().isEmpty())
            idsCorrelativas.add(Long.valueOf(materia.getCorrelativa2()));
        if (materia.getCorrelativa3() != null && !materia.getCorrelativa3().isEmpty())
            idsCorrelativas.add(Long.valueOf(materia.getCorrelativa3()));
        if (materia.getCorrelativa4() != null && !materia.getCorrelativa4().isEmpty())
            idsCorrelativas.add(Long.valueOf(materia.getCorrelativa4()));
        if (materia.getCorrelativa5() != null && !materia.getCorrelativa5().isEmpty())
            idsCorrelativas.add(Long.valueOf(materia.getCorrelativa5()));
        if (materia.getCorrelativa6() != null && !materia.getCorrelativa6().isEmpty())
            idsCorrelativas.add(Long.valueOf(materia.getCorrelativa6()));

        // Si la materia no tiene correlativas, se puede cursar directamente
        if (idsCorrelativas.isEmpty()) {
            return true;
        }

        // Verificar cada correlativa
        for (Long idCorrelativa : idsCorrelativas) {
            UsuarioMateria correlativaUsuarioMateria = materiasCursadasMap.get(idCorrelativa);

            // Si la correlativa no está en el mapa (el usuario nunca la cursó)
            // O si la cursó y NO está aprobada, entonces la condición no se cumple.
            if (correlativaUsuarioMateria == null || !correlativaUsuarioMateria.estaAprobada()) {
                return false; // Al menos una correlativa no está aprobada
            }
        }
        return true; // Todas las correlativas están aprobadas
    }

    private String verificarDificultad(Integer dificultad) {
        String dificultadMat = "";
        if (dificultad != null) { // Asegúrate de que dificultad no sea null
            if (dificultad == 1) {
                dificultadMat = "Facil";
            } else if (dificultad == 5) {
                dificultadMat = "Medio";
            } else if (dificultad == 10) {
                dificultadMat = "Dificil";
            }
        }
        return dificultadMat;
    }

    public List<MateriaDTO> filtrarPor(String condicion, Long usuarioId) {
        List<MateriaDTO> materias = this.materias(usuarioId);

        if (condicion == null || condicion.isEmpty() || condicion.equalsIgnoreCase("todas")) {
            return materias; // No filtrar si no hay condición o es "todas"
        }

        if (condicion.equalsIgnoreCase("aprobadas")) {
            materias = materias.stream()
                    .filter(materia -> materia.getNota() != null && materia.getNota() >= 4)
                    .collect(Collectors.toList());
        } else if (condicion.equalsIgnoreCase("desaprobadas")) {
            materias = materias.stream()
                    .filter(materia -> materia.getNota() != null && materia.getNota() < 4)
                    .collect(Collectors.toList());
        } else if (condicion.equalsIgnoreCase("cursando")) {
            materias = materias.stream()
                    .filter(materia -> "CURSANDO".equalsIgnoreCase(materia.getEstado()))
                    .collect(Collectors.toList());
        } else if (condicion.equalsIgnoreCase("pendientes")) {
            materias = materias.stream()
                    .filter(materia -> "PENDIENTE".equalsIgnoreCase(materia.getEstado()))
                    .collect(Collectors.toList());
        }
        return materias;
    }
}