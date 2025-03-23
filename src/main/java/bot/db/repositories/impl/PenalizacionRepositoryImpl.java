package bot.db.repositories.impl;

import bot.db.models.Penalizacion;
import bot.db.repositories.PenalizacionRepository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PenalizacionRepositoryImpl extends AbstractRepository<Penalizacion, Long>
        implements PenalizacionRepository {

    @Override
    protected String getTableName() {
        return "penalizaciones";
    }

    @Override
    protected String getIdColumnName() {
        return "id_penalizacion";
    }

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

    @Override
    public Penalizacion save(Penalizacion penalizacion) {
        try (var conn = getConnection()) {
            String sql = "INSERT INTO penalizaciones (id_penalizacion, id_usuario, id_admin_mod, tipo, fecha, razon, duracion_segundos) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (var stmt = conn.prepareStatement(sql)) {
                setStatementParameters(stmt, penalizacion);
                stmt.executeUpdate();
                return penalizacion;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la penalización", e);
        }
    }

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