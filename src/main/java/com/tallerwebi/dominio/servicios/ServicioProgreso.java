package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.UsuarioMateria;
import com.tallerwebi.repositorioInterfaz.RepositorioMateria;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuarioMateria;
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
    private RepositorioUsuario repositorioUsuario;

    public ServicioProgreso() {
    }

    @Autowired
    public ServicioProgreso(RepositorioUsuarioMateria repositorioUsuarioMateria, RepositorioMateria repositorioMateria, RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuarioMateria = repositorioUsuarioMateria;
        this.repositorioMateria = repositorioMateria;
        this.repositorioUsuario = repositorioUsuario;
    }

    public List<MateriaDTO> materias(String idCarrera, Long idUsuario) {

        List<UsuarioMateria> materiasQueCursoElUsuario = this.repositorioUsuarioMateria.buscarPorUsuario(idCarrera, idUsuario);
        List<Materia> todasLasMaterias = this.repositorioMateria.obtenerMateriasDeUnaCarrera(idCarrera);

        Map<Long, UsuarioMateria> materiasCursadasMap = new HashMap<>();
        for (UsuarioMateria um : materiasQueCursoElUsuario) {
            materiasCursadasMap.put(um.getMateria().getId(), um);
        }

        List<MateriaDTO> materiasDTO = new ArrayList<>();

        for (Materia mat : todasLasMaterias) {
            UsuarioMateria matCurso = materiasCursadasMap.get(mat.getId());
            Boolean esCursable = verificarCorrelativasAprobadas(mat, materiasCursadasMap);

            MateriaDTO materiaDTO;
            if (matCurso != null) {
                String dificultad = verificarDificultad(matCurso.getDificultad());
                materiaDTO = new MateriaDTO(mat.getId(), mat.getNombre(), dificultad, matCurso.getEstadoo(), matCurso.getNota(), mat.getCuatrimestre(), esCursable);
            } else {
                String estado;
//                if (verificarCorrelativasAprobadas(mat, materiasCursadasMap)) {
//                    estado = "CURSANDO";
//                } else {
                    estado = "PENDIENTE";
//                }
                materiaDTO = new MateriaDTO(mat.getId(), mat.getNombre(), null, estado, null, mat.getCuatrimestre(), esCursable);
            }
            materiasDTO.add(materiaDTO);
        }

        return materiasDTO;
    }

    public boolean marcarMateriaComoCursando(Long usuarioId, Long idMateria) {
        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
        Materia materia = repositorioMateria.buscarPorId(idMateria);

        if (usuario == null || materia == null) {
            return false;
        }

        UsuarioMateria umExistente = repositorioUsuarioMateria.buscarPorUsuarioYMateria(usuarioId, idMateria);
        if (umExistente != null) {

            umExistente.reiniciarCursada();
            repositorioUsuarioMateria.actualizar(umExistente);
            return true;
        }

        List<UsuarioMateria> materiasCursadasUsuario = this.repositorioUsuarioMateria.buscarPorUsuario(usuario.getCarreraID().toString(), usuarioId);
        Map<Long, UsuarioMateria> materiasCursadasMapParaCorrelativas = new HashMap<>();
        for (UsuarioMateria um : materiasCursadasUsuario) {
            materiasCursadasMapParaCorrelativas.put(um.getMateria().getId(), um);
        }


        if (!verificarCorrelativasAprobadas(materia, materiasCursadasMapParaCorrelativas)) {
            return false;
        }

        UsuarioMateria nuevoUm = new UsuarioMateria(usuario, materia);
        repositorioUsuarioMateria.guardar(nuevoUm);
        return true;
    }

    public void marcarMateriaComoPendiente(Long idMateria, Long usuarioId) {
        UsuarioMateria um = this.repositorioUsuarioMateria.buscarPorUsuarioYMateria(usuarioId, idMateria);
        if (um != null) {
            um.setNota(null);
            um.setEstado(0);
            this.repositorioUsuarioMateria.actualizar(um);
        }
    }

    private boolean verificarCorrelativasAprobadas(Materia materia, Map<Long, UsuarioMateria> materiasCursadasMap) {
        List<Long> idsCorrelativas = new ArrayList<>();
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

        if (idsCorrelativas.isEmpty()) {
            return true;
        }

        for (Long idCorrelativa : idsCorrelativas) {
            UsuarioMateria correlativaUsuarioMateria = materiasCursadasMap.get(idCorrelativa);

            if (correlativaUsuarioMateria == null || !correlativaUsuarioMateria.estaAprobada()) {
                return false;
            }
        }
        return true;
    }

    private String verificarDificultad(Integer dificultad) {
        String dificultadMat = "";
        if (dificultad != null) {
            if (dificultad == 1) {
                dificultadMat = "Facil";
            } else if (dificultad == 2) {
                dificultadMat = "Medio";
            } else if (dificultad == 3) {
                dificultadMat = "Dificil";
            }
        }
        return dificultadMat;
    }

    public List<MateriaDTO> filtrarPor(String idCarrera, String condicion, Long usuarioId) {
        List<MateriaDTO> materias = this.materias(idCarrera, usuarioId);

        if (condicion == null || condicion.isEmpty() || condicion.equalsIgnoreCase("todas")) {
            return materias;
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

    public List<MateriaDTO> filtrarPorCuatrimestre(String idCarrera, Integer cuatrimestre, Long usuarioId) {
        List<MateriaDTO> materias = this.materias(idCarrera, usuarioId);


        if (cuatrimestre == null || cuatrimestre.equals(0)) {
            return materias;
        }

        if (cuatrimestre.equals(1)) {
            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 1).collect(Collectors.toList());
        } else if (cuatrimestre.equals(2)) {
            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 2).collect(Collectors.toList());
        } else if (cuatrimestre.equals(3)) {
            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 3).collect(Collectors.toList());
        } else if (cuatrimestre.equals(4)) {
            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 4).collect(Collectors.toList());
        } else if (cuatrimestre.equals(5)) {
            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 5).collect(Collectors.toList());
        } else if (cuatrimestre.equals(6)) {
            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 6).collect(Collectors.toList());
        } else if (cuatrimestre.equals(7)) {
            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 7).collect(Collectors.toList());
        } else if (cuatrimestre.equals(8)) {
            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 8).collect(Collectors.toList());
        } else if (cuatrimestre.equals(9)) {
            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 9).collect(Collectors.toList());
        } else if (cuatrimestre.equals(10)) {
            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 10).collect(Collectors.toList());
        }

        return materias;
    }

    public List<MateriaDTO> filtrarPorCuatrimestreYEstado(String idCarrera, Integer cuatrimestre, String condicion, Long usuarioId) {
        List<MateriaDTO> materias = this.materias(idCarrera, usuarioId);

        if (condicion != null && !condicion.equalsIgnoreCase("todas")) {
            materias = filtrarPor(idCarrera, condicion, usuarioId);
        }

        if (cuatrimestre != null && cuatrimestre > 0) {
            final int cuatri = cuatrimestre;
            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null
                    && materia.getCuatrimestre() == cuatri).collect(Collectors.toList());
        }
        return materias;
    }

    public Boolean actualizarDatosMateria(Long usuarioId, Long idMateria, Integer nota, Integer dificultad) {

        Boolean pudoActualizar = false;

        try {

            // Verifico si existe primero los datos en UsuarioMateria
            Boolean existe = this.repositorioUsuarioMateria.existe(usuarioId, idMateria);

            UsuarioMateria um = null;

            if (existe) {
                um = this.repositorioUsuarioMateria.buscarPorUsuarioYMateria(usuarioId, idMateria);
                um.setNota(nota);
                um.setDificultad(dificultad);
                this.repositorioUsuarioMateria.actualizar(um);
            } else {
                // Traigo usuario y materia (porque lo require UsuarioMateria)
                Usuario usuario = this.repositorioUsuario.buscarPorId(usuarioId);
                Materia materia = this.repositorioMateria.buscarPorId(idMateria);

                um = new UsuarioMateria(usuario, materia, nota);
                um.setDificultad(dificultad);
                this.repositorioUsuarioMateria.guardar(um);
            }

            pudoActualizar = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return pudoActualizar;
    }

    public Double obtenerProgresoDeCarrera(String idCarrera, Long usuarioId) {

        List<MateriaDTO> todasLasMaterias = this.materias(idCarrera, usuarioId);
        List<MateriaDTO> materiasAprobadas = this.filtrarPor(idCarrera, "aprobadas", usuarioId);

        return (double) ((materiasAprobadas.size() * 100) / todasLasMaterias.size());

    }

//    public void marcarMateriaComoPendiente(Long idMateria, Long usuarioId) {
//        UsuarioMateria um = this.repositorioUsuarioMateria.buscarPorUsuarioYMateria(usuarioId, idMateria);
//
////        if (um != null && um.getEstado().equalsIgnoreCase("CURSANDO")) {
////            um.setEstado("PENDIENTE");
////        }
//
//        this.repositorioUsuarioMateria.actualizar(um);
//
//    }

    public Double obtenerPorcentajeDeMateriasDesaprobadas(String idCarrera, Long usuarioId) {
        List<MateriaDTO> materias = this.materias(idCarrera, usuarioId);
        List<MateriaDTO> materiasDesaprobadas = new ArrayList<>();

        for (MateriaDTO materia : materias) {
            if (materia.getNota() != null && materia.getNota() < 4) {
                materiasDesaprobadas.add(materia);
            }
        }

        Double porcentajeDesaprobadas = (double) (materiasDesaprobadas.size() * 100 / materias.size());
        return porcentajeDesaprobadas;
    }

    public Double obtenerPorcentajeDeMateriasAprobadas(String idCarrera, Long usuarioId) {
        List<MateriaDTO> materias = this.materias(idCarrera, usuarioId);
        List<MateriaDTO> materiasAprobadas = new ArrayList<>();

        for (MateriaDTO materia : materias) {
            if (materia.getNota() != null && materia.getNota() > 4) {
                materiasAprobadas.add(materia);
            }
        }

        Double porcentajeAprobadas = (double) (materiasAprobadas.size() * 100 / materias.size());
        return porcentajeAprobadas;
    }
}