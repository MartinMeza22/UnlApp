package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.DTO.MateriaDiagramaDTO;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.UsuarioMateria;
import com.tallerwebi.dominio.excepcion.CorrelatividadInvalidaException;
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

//    public List<MateriaDTO> filtrarPor(String idCarrera, String condicion, Long usuarioId) {
//        List<MateriaDTO> materias = this.materias(idCarrera, usuarioId);
//
//        if (condicion == null || condicion.isEmpty() || condicion.equalsIgnoreCase("todas")) {
//            return materias;
//        }
//
//        if (condicion.equalsIgnoreCase("aprobadas")) {
//            materias = materias.stream()
//                    .filter(materia -> materia.getNota() != null && materia.getNota() >= 4)
//                    .collect(Collectors.toList());
//        } else if (condicion.equalsIgnoreCase("desaprobadas")) {
//            materias = materias.stream()
//                    .filter(materia -> materia.getNota() != null && materia.getNota() < 4)
//                    .collect(Collectors.toList());
//        } else if (condicion.equalsIgnoreCase("cursando")) {
//            materias = materias.stream()
//                    .filter(materia -> "CURSANDO".equalsIgnoreCase(materia.getEstado()))
//                    .collect(Collectors.toList());
//        } else if (condicion.equalsIgnoreCase("pendientes")) {
//            materias = materias.stream()
//                    .filter(materia -> "PENDIENTE".equalsIgnoreCase(materia.getEstado()))
//                    .collect(Collectors.toList());
//        }
//        return materias;
//    }

//    public List<MateriaDTO> filtrarPorCuatrimestre(String idCarrera, Integer cuatrimestre, Long usuarioId) {
//        List<MateriaDTO> materias = this.materias(idCarrera, usuarioId);
//
//
//        if (cuatrimestre == null || cuatrimestre.equals(0)) {
//            return materias;
//        }
//
//        if (cuatrimestre.equals(1)) {
//            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 1).collect(Collectors.toList());
//        } else if (cuatrimestre.equals(2)) {
//            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 2).collect(Collectors.toList());
//        } else if (cuatrimestre.equals(3)) {
//            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 3).collect(Collectors.toList());
//        } else if (cuatrimestre.equals(4)) {
//            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 4).collect(Collectors.toList());
//        } else if (cuatrimestre.equals(5)) {
//            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 5).collect(Collectors.toList());
//        } else if (cuatrimestre.equals(6)) {
//            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 6).collect(Collectors.toList());
//        } else if (cuatrimestre.equals(7)) {
//            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 7).collect(Collectors.toList());
//        } else if (cuatrimestre.equals(8)) {
//            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 8).collect(Collectors.toList());
//        } else if (cuatrimestre.equals(9)) {
//            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 9).collect(Collectors.toList());
//        } else if (cuatrimestre.equals(10)) {
//            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null && materia.getCuatrimestre() == 10).collect(Collectors.toList());
//        }
//
//        return materias;
//    }

//    public List<MateriaDTO> filtrarPorCuatrimestreYEstado(String idCarrera, Integer cuatrimestre, String condicion, Long usuarioId) {
//        List<MateriaDTO> materias = this.materias(idCarrera, usuarioId);
//
//        if (condicion != null && !condicion.equalsIgnoreCase("todas")) {
//            materias = filtrarPor(idCarrera, condicion, usuarioId);
//        }
//
//        if (cuatrimestre != null && cuatrimestre > 0) {
//            final int cuatri = cuatrimestre;
//            materias = materias.stream().filter(materia -> materia.getCuatrimestre() != null
//                    && materia.getCuatrimestre() == cuatri).collect(Collectors.toList());
//        }
//        return materias;
//    }

    public Boolean actualizarDatosMateria(Long usuarioId, Long idMateria, Integer nota, Integer dificultad) throws CorrelatividadInvalidaException {

        if(nota != null && nota < 4){
            List<Materia> materiasDependientes = this.repositorioMateria.buscarMateriasQueTienenComoCorrelativas(idMateria);

            if(!materiasDependientes.isEmpty()){
                Usuario usuario = this.repositorioUsuario.buscarPorId(usuarioId);
                List<UsuarioMateria> materiasCursadasPorUsuario = this.repositorioUsuarioMateria.buscarPorUsuario(usuario.getCarreraID().toString(), usuarioId);

                Set<Long> idsMateriasCursadasAprobadas = materiasCursadasPorUsuario.stream()
                        .filter(um -> um.estaAprobada())
                        .map(um -> um.getMateria().getId())
                        .collect(Collectors.toSet());

                List<String> conflictos = new ArrayList<>();
                for(Materia dependiente : materiasDependientes){
                    if(idsMateriasCursadasAprobadas.contains(dependiente.getId())){
                        conflictos.add(dependiente.getNombre());
                    }
                }

                if(!conflictos.isEmpty()){
                    throw new CorrelatividadInvalidaException("No se puede desaprobar. Las siguiente materias dependen de ella: \n" + String.join(" - ", conflictos));
                }
            }
        }

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
        //List<MateriaDTO> materiasAprobadas = this.filtrarPor(idCarrera, "aprobadas", usuarioId);

        if(todasLasMaterias.isEmpty()){
            return 0.0;
        }
        Long cantidadAprobadas = todasLasMaterias.stream()
                .filter(materia -> materia.getNota() != null && materia.getNota() >= 4)
                .count();

        return (double) (cantidadAprobadas * 100) / todasLasMaterias.size();

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
        if (materias.isEmpty()) {
            return 0.0;
        }
        long cantidadDesaprobadas = materias.stream()
                .filter(m -> m.getNota() != null && m.getNota() < 4)
                .count();
        return (double) (cantidadDesaprobadas * 100) / materias.size();
    }

    public Double obtenerPorcentajeDeMateriasAprobadas(String idCarrera, Long usuarioId) {
        List<MateriaDTO> materias = this.materias(idCarrera, usuarioId);
        if (materias.isEmpty()) {
            return 0.0;
        }
        long cantidadAprobadas = materias.stream()
                .filter(m -> m.getNota() != null && m.getNota() > 4)
                .count();
        return (double) (cantidadAprobadas * 100) / materias.size();
    }

    public List<MateriaDiagramaDTO> obtenerMateriasParaDiagrama(String idCarrera, Long idUsuario) {
        List<Materia> todasLasMaterias = repositorioMateria.obtenerMateriasDeUnaCarrera(idCarrera);

        // Opcional: Obtener el estado del usuario para resaltar si ya aprobó materias
        List<UsuarioMateria> materiasQueCursoElUsuario = repositorioUsuarioMateria.buscarPorUsuario(idCarrera, idUsuario);
        Map<Long, UsuarioMateria> materiasCursadasMap = materiasQueCursoElUsuario.stream()
                .collect(Collectors.toMap(um -> um.getMateria().getId(), um -> um));

        List<MateriaDiagramaDTO> materiasDiagrama = new ArrayList<>();
        Map<Long, Materia> mapaMaterias = todasLasMaterias.stream()
                .collect(Collectors.toMap(Materia::getId, m -> m));

        for (Materia materia : todasLasMaterias) {
            MateriaDiagramaDTO dto = new MateriaDiagramaDTO(materia.getId(), materia.getNombre(), materia.getCuatrimestre());

            // Agrega las correlativas.
            // Necesitas mapear los nombres de correlativas a sus IDs.
            // Esto es crucial para que el frontend pueda dibujar las líneas.
            if (materia.getCorrelativa1() != null && !materia.getCorrelativa1().isEmpty()) {
                // Aquí necesitarías una forma de obtener el ID de la materia a partir del nombre
                // dado que tus correlativas son Strings.
                // UNA SOLUCIÓN MÁS ROBUSTA SERÍA QUE correlativa_1 fuera el ID de la Materia directamente
                // o que Materia tuviera una lista de Materias como correlativas.
                // Por ahora, asumimos que 'correlativa_1' es el ID de la materia correlativa.
                // Si 'correlativa_1' almacena el *nombre* de la materia, necesitarías
                // un método para buscar el ID de la materia por su nombre.
                try {
                    Long idCorrelativa = Long.valueOf(materia.getCorrelativa1());
                    if (mapaMaterias.containsKey(idCorrelativa)) {
                        dto.addCorrelativa(idCorrelativa);
                    }
                } catch (NumberFormatException e) {
                    // Manejar error si el String no es un número (ej. si guarda el nombre)
                    System.err.println("Correlativa1 no es un ID numérico: " + materia.getCorrelativa1());
                    // Si tus correlativas son nombres, aquí buscarías el ID por nombre
                    // Ejemplo: Materia correlativaObj = repositorioMateria.buscarPorNombre(materia.getCorrelativa1());
                    // if (correlativaObj != null) dto.addCorrelativa(correlativaObj.getId());
                }
            }
            if (materia.getCorrelativa2() != null && !materia.getCorrelativa2().isEmpty()) {
                try {
                    Long idCorrelativa = Long.valueOf(materia.getCorrelativa2());
                    if (mapaMaterias.containsKey(idCorrelativa)) {
                        dto.addCorrelativa(idCorrelativa);
                    }
                } catch (NumberFormatException e) {}
            }
            if (materia.getCorrelativa3() != null && !materia.getCorrelativa3().isEmpty()) {
                try {
                    Long idCorrelativa = Long.valueOf(materia.getCorrelativa3());
                    if (mapaMaterias.containsKey(idCorrelativa)) {
                        dto.addCorrelativa(idCorrelativa);
                    }
                } catch (NumberFormatException e) {}
            }
            if (materia.getCorrelativa4() != null && !materia.getCorrelativa4().isEmpty()) {
                try {
                    Long idCorrelativa = Long.valueOf(materia.getCorrelativa4());
                    if (mapaMaterias.containsKey(idCorrelativa)) {
                        dto.addCorrelativa(idCorrelativa);
                    }
                } catch (NumberFormatException e) {}
            }
            if (materia.getCorrelativa5() != null && !materia.getCorrelativa5().isEmpty()) {
                try {
                    Long idCorrelativa = Long.valueOf(materia.getCorrelativa5());
                    if (mapaMaterias.containsKey(idCorrelativa)) {
                        dto.addCorrelativa(idCorrelativa);
                    }
                } catch (NumberFormatException e) {}
            }
            if (materia.getCorrelativa6() != null && !materia.getCorrelativa6().isEmpty()) {
                try {
                    Long idCorrelativa = Long.valueOf(materia.getCorrelativa6());
                    if (mapaMaterias.containsKey(idCorrelativa)) {
                        dto.addCorrelativa(idCorrelativa);
                    }
                } catch (NumberFormatException e) {}
            }

            // Opcional: Agregar información sobre el estado de la materia (aprobada, cursando, etc.)
            // Puedes agregar un booleano `isAprobada` al DTO si quieres que JavaScript lo use.
            // UsuarioMateria um = materiasCursadasMap.get(materia.getId());
            // if (um != null && um.estaAprobada()) { dto.setAprobada(true); }

            materiasDiagrama.add(dto);
        }
        return materiasDiagrama;
    }
}