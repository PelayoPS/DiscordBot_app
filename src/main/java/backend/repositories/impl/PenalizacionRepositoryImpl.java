package backend.repositories.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import backend.db.DatabaseManager;
import backend.models.Penalizacion;
import backend.repositories.PenalizacionRepository;

/**
 * Implementación del repositorio de penalizaciones.
 * Permite operaciones CRUD y consultas específicas sobre penalizaciones de
 * usuarios.
 * 
 * @author PelayoPS
 */
public class PenalizacionRepositoryImpl extends AbstractRepository<Penalizacion, Long>
        implements PenalizacionRepository {

    /**
     * Constructor de PenalizacionRepositoryImpl.
     * 
     * @param databaseManager Gestor de base de datos
     */
    public PenalizacionRepositoryImpl(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTableName() {
        return "penalizaciones";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getIdColumnName() {
        return "id_penalizacion";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Penalizacion mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Penalizacion(
                rs.getLong("id_penalizacion"),
                rs.getLong("id_usuario"),
                rs.getLong("id_admin_mod"),
                rs.getString("tipo"),
                rs.getTimestamp("fecha").toLocalDateTime(),
                rs.getString("razon"),
                java.time.Duration.ofSeconds(rs.getLong("duracion_segundos")));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setStatementParameters(PreparedStatement stmt, Penalizacion penalizacion) throws SQLException {
        stmt.setLong(1, penalizacion.getIdPenalizacion());
        stmt.setLong(2, penalizacion.getIdUsuario());
        stmt.setLong(3, penalizacion.getIdAdminMod());
        stmt.setString(4, penalizacion.getTipo());
        stmt.setTimestamp(5, java.sql.Timestamp.valueOf(penalizacion.getFecha()));
        stmt.setString(6, penalizacion.getRazon());
        stmt.setLong(7, penalizacion.getDuracion().getSeconds());
    }

    /**
     * Guarda una penalización en la base de datos.
     * 
     * @param penalizacion Penalización a guardar
     * @return Penalización guardada
     */
    @Override
    public Penalizacion save(Penalizacion penalizacion) {
        try (var conn = getConnection()) {
            String sql;
            boolean autogenId = penalizacion.getIdPenalizacion() == null;
            if (autogenId) {
                sql = "INSERT INTO penalizaciones (id_usuario, id_admin_mod, tipo, fecha, razon, duracion_segundos) VALUES (?, ?, ?, ?, ?, ?)";
            } else {
                sql = "INSERT INTO penalizaciones (id_penalizacion, id_usuario, id_admin_mod, tipo, fecha, razon, duracion_segundos) VALUES (?, ?, ?, ?, ?, ?, ?)";
            }
            try (var stmt = conn.prepareStatement(sql)) {
                int idx = 1;
                if (!autogenId) {
                    stmt.setLong(idx++, penalizacion.getIdPenalizacion());
                }
                stmt.setLong(idx++, penalizacion.getIdUsuario());
                stmt.setLong(idx++, penalizacion.getIdAdminMod());
                stmt.setString(idx++, penalizacion.getTipo());
                stmt.setTimestamp(idx++, java.sql.Timestamp.valueOf(penalizacion.getFecha()));
                stmt.setString(idx++, penalizacion.getRazon());
                stmt.setLong(idx++, penalizacion.getDuracion().getSeconds());
                stmt.executeUpdate();
                return penalizacion;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la penalización", e);
        }
    }

    /**
     * Busca penalizaciones por ID de usuario.
     * 
     * @param idUsuario ID del usuario
     * @return Lista de penalizaciones encontradas
     */
    @Override
    public List<Penalizacion> findByIdUsuario(Long idUsuario) {
        try (var conn = getConnection()) {
            String sql = "SELECT * FROM penalizaciones WHERE id_usuario = ? ORDER BY fecha DESC";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, idUsuario);
                var rs = stmt.executeQuery();
                List<Penalizacion> penalizaciones = new ArrayList<>();
                while (rs.next()) {
                    penalizaciones.add(mapResultSetToEntity(rs));
                }
                return penalizaciones;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar penalizaciones por id de usuario", e);
        }
    }

    /**
     * Busca penalizaciones por ID de administrador/moderador.
     * 
     * @param idAdminMod ID del admin/mod
     * @return Lista de penalizaciones encontradas
     */
    @Override
    public List<Penalizacion> findByIdAdminMod(Long idAdminMod) {
        try (var conn = getConnection()) {
            String sql = "SELECT * FROM penalizaciones WHERE id_admin_mod = ? ORDER BY fecha DESC";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, idAdminMod);
                var rs = stmt.executeQuery();
                List<Penalizacion> penalizaciones = new ArrayList<>();
                while (rs.next()) {
                    penalizaciones.add(mapResultSetToEntity(rs));
                }
                return penalizaciones;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar penalizaciones por id de administrador", e);
        }
    }

    /**
     * Busca penalizaciones por tipo.
     * 
     * @param tipo Tipo de penalización
     * @return Lista de penalizaciones encontradas
     */
    @Override
    public List<Penalizacion> findByTipo(String tipo) {
        try (var conn = getConnection()) {
            String sql = "SELECT * FROM penalizaciones WHERE tipo = ? ORDER BY fecha DESC";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, tipo);
                var rs = stmt.executeQuery();
                List<Penalizacion> penalizaciones = new ArrayList<>();
                while (rs.next()) {
                    penalizaciones.add(mapResultSetToEntity(rs));
                }
                return penalizaciones;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar penalizaciones por tipo", e);
        }
    }

    /**
     * Busca penalizaciones en un rango de fechas.
     * 
     * @param inicio Fecha de inicio
     * @param fin    Fecha de fin
     * @return Lista de penalizaciones encontradas
     */
    @Override
    public List<Penalizacion> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin) {
        try (var conn = getConnection()) {
            String sql = "SELECT * FROM penalizaciones WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setTimestamp(1, java.sql.Timestamp.valueOf(inicio));
                stmt.setTimestamp(2, java.sql.Timestamp.valueOf(fin));
                var rs = stmt.executeQuery();
                List<Penalizacion> penalizaciones = new ArrayList<>();
                while (rs.next()) {
                    penalizaciones.add(mapResultSetToEntity(rs));
                }
                return penalizaciones;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar penalizaciones por rango de fechas", e);
        }
    }
}