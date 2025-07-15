package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.DTO.UsuarioYMateriasDTO;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuarioMateria;
import com.tallerwebi.dominio.UsuarioMateria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository("repositorioUsuarioMateria")
public class RepositorioUsuarioMateriaImpl implements RepositorioUsuarioMateria { //Implementamos la interfaz

    private SessionFactory sessionFactory; //Declaramos la session (para conectar la BDD)

    @Autowired
    public RepositorioUsuarioMateriaImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;//Asignamos la conexión de BDD (la trae Spring)
    }

    @Override
    public void guardar(UsuarioMateria usuarioMateria) {
        sessionFactory.getCurrentSession().save(usuarioMateria);//Guarda el usuario en la BDD
    }

    @Override
    public UsuarioMateria buscarPorId(Long id) {
        return sessionFactory.getCurrentSession().get(UsuarioMateria.class, id);
    }

    @Override
    public UsuarioMateria buscarPorUsuarioYMateria(Long usuarioId, Long materiaId) { //Utilización de metodos para hacer consultas SQL
        return (UsuarioMateria) sessionFactory.getCurrentSession()
                .createCriteria(UsuarioMateria.class)
                .createAlias("usuario", "u")
                .createAlias("materia", "m")
                .add(Restrictions.eq("u.id", usuarioId))
                .add(Restrictions.eq("m.id", materiaId))
                .uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UsuarioMateria> buscarPorUsuario(String idCarrera, Long usuarioId) {
        return sessionFactory.getCurrentSession()
                .createCriteria(UsuarioMateria.class)
                .createAlias("usuario", "u")
                .createAlias("materia", "m")
                .add(Restrictions.eq("u.id", usuarioId))
                .add(Restrictions.eq("m.carrera.id", Long.valueOf(idCarrera)))
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UsuarioMateria> buscarTodas() {
        final String hql = "FROM UsuarioMateria um " + //HQL es una consulta con Hibernate
                "JOIN FETCH um.usuario u " +
                "JOIN FETCH um.materia m " +
                "ORDER BY u.email, m.nombre";
        return sessionFactory.getCurrentSession()
                .createQuery(hql)
                .list();
    }

    @Override
    public void actualizar(UsuarioMateria usuarioMateria) {
        sessionFactory.getCurrentSession().update(usuarioMateria);
    }
    //Actualiza una fila existente en la base con nuevos datos

    @Override
    public void eliminar(UsuarioMateria usuarioMateria) {
        sessionFactory.getCurrentSession().delete(usuarioMateria);
    }
    //Elimina una fila existente de la tabla usuario_materia

    @Override
    public boolean existe(Long usuarioId, Long materiaId) {
        UsuarioMateria resultado = buscarPorUsuarioYMateria(usuarioId, materiaId); //Reutilización de otro metodo
        return resultado != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public UsuarioMateria buscarProgresoPersonal(Long usuarioId, Long materiaId) {
        final String hql = "SELECT um.dificultad, um.estado, um.nota " +
                "FROM UsuarioMateria um " +
                "WHERE um.usuario.id = :usuarioId AND um.materia.id = :materiaId";

        return (UsuarioMateria) (sessionFactory.getCurrentSession()
                .createQuery(hql));
    }

    public Long contadorDeMateriasPorUsuario(Long usuarioId) {
        final String hql = "SELECT COUNT(um.materia) " +
                "FROM UsuarioMateria um " +
                "WHERE um.usuario.id = :usuarioId";

        return (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("usuarioId", usuarioId)
                .uniqueResult();
    }

    public Long contadorDeMateriasAprobadasPorUsuario(Long usuarioId) {
        final String hql = "SELECT COUNT(um.materia) " +
                "FROM UsuarioMateria um " +
                "WHERE um.usuario.id = :usuarioId AND um.estado = 3";

        return (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("usuarioId", usuarioId)
                .uniqueResult();
    }

    public Long contadorDeMateriasDesaprobadasPorUsuario(Long usuarioId) {
        final String hql = "SELECT COUNT(um.materia) " +
                "FROM UsuarioMateria um " +
                "WHERE um.usuario.id = :usuarioId AND um.estado = 4";

        return (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("usuarioId", usuarioId)
                .uniqueResult();
    }

    public Long contadorDeMateriasCursandoPorUsuario(Long usuarioId) {
        final String hql = "SELECT COUNT(um.materia) " +
                "FROM UsuarioMateria um " +
                "WHERE um.usuario.id = :usuarioId AND um.estado = 2";

        return (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("usuarioId", usuarioId)
                .uniqueResult();
    }

    //Estadisticas generales
    public Long contadorDeMateriasAprobadasGeneralPorCarrera(Long carreraId) {
        // Contar relaciones usuario-materia con estado = 3 (aprobadas) y carrera coincidente
        final String hql = "SELECT COUNT(um.id) " +
                "FROM UsuarioMateria um " +
                "WHERE um.estado = 3 AND um.usuario.carrera.id = :carreraId";

        return (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("carreraId", carreraId)
                .uniqueResult();
    }

    public Long contadorDeMateriasDesaprobadasGeneralPorCarrera(Long carreraId) {
        // Contar relaciones usuario-materia con estado = 4 (desaprobadas) y carrera coincidente
        final String hql = "SELECT COUNT(um.id) " +
                "FROM UsuarioMateria um " +
                "WHERE um.estado = 4 AND um.usuario.carrera.id = :carreraId";

        return (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("carreraId", carreraId)
                .uniqueResult();
    }

    public Long contadorDeMateriasCursandoGeneralPorCarrera(Long carreraId) {
        // Contar relaciones usuario-materia con estado = 2 (cursando) y carrera coincidente
        final String hql = "SELECT COUNT(um.id) " +
                "FROM UsuarioMateria um " +
                "WHERE um.estado = 2 AND um.usuario.carrera.id = :carreraId";

        return (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("carreraId", carreraId)
                .uniqueResult();
    }

    public Long contadorDeTotalDeUsuariosPorCarrera(Long carreraId) {
        // Contar usuarios únicos con al menos una relación en la carrera indicada
        final String hql = "SELECT COUNT(DISTINCT um.usuario.id) " +
                "FROM UsuarioMateria um " +
                "WHERE um.usuario.carrera.id = :carreraId";

        return (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("carreraId", carreraId)
                .uniqueResult();
    }

    @Override
    public Long contarAprobadasPorCarrera(Long carreraId) {
        final String hql = "SELECT COUNT(um.id) " +
                "FROM UsuarioMateria um " +
                "WHERE um.estado = 3 AND um.usuario.carrera.id = :carreraId";

        return (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("carreraId", carreraId)
                .uniqueResult();
    }

    @Override
    public Long contarDesaprobadasPorCarrera(Long carreraId) {
        final String hql = "SELECT COUNT(um.id) " +
                "FROM UsuarioMateria um " +
                "WHERE um.estado = 4 AND um.usuario.carrera.id = :carreraId";

        return (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("carreraId", carreraId)
                .uniqueResult();
    }

    @Override
    public Long contarCursandoPorCarrera(Long carreraId) {
        final String hql = "SELECT COUNT(um.id) " +
                "FROM UsuarioMateria um " +
                "WHERE um.estado = 2 AND um.usuario.carrera.id = :carreraId";

        return (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("carreraId", carreraId)
                .uniqueResult();
    }

    @Override
    public Long contarTotalRelacionesPorCarrera(Long carreraId) {
        final String hql = "SELECT COUNT(um.id) " +
                "FROM UsuarioMateria um " +
                "WHERE um.usuario.carrera.id = :carreraId";

        Long count = (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("carreraId", carreraId)
                .uniqueResult();
        return count;
    }

    @Override
    public Long contarUsuariosPorCarrera(Long carreraId) {
        final String hql = "SELECT COUNT(DISTINCT um.usuario.id) " +
                "FROM UsuarioMateria um " +
                "WHERE um.usuario.carrera.id = :carreraId";

        return (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("carreraId", carreraId)
                .uniqueResult();
    }

    @Override
    public List<UsuarioYMateriasDTO> obtenerUsuariosConMaterias() {
        try {
            String hql = "FROM UsuarioMateria um " +
                    "JOIN FETCH um.usuario " +
                    "JOIN FETCH um.materia";

            List<UsuarioMateria> todas = sessionFactory.getCurrentSession()
                    .createQuery(hql, UsuarioMateria.class)
                    .getResultList();

            System.out.println("Tengo:" + todas.size() + " UsuarioMateria");

            Map<Usuario, List<UsuarioMateria>> agrupado = todas.stream()
                    .collect(Collectors.groupingBy(UsuarioMateria::getUsuario));

            return agrupado.entrySet().stream()
                    .map(entry -> new UsuarioYMateriasDTO(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Error en obtenerUsuariosConMaterias:");
            e.printStackTrace();
            throw e;
        }


    }
}
//header.createCell(0).setCellValue("Usuario");
//            header.createCell(1).setCellValue("Carrera");
//            header.createCell(2).setCellValue("Materia");
//            header.createCell(3).setCellValue("Estado");
//            header.createCell(4).setCellValue("Nota");
//            header.createCell(5).setCellValue("Dificultad");