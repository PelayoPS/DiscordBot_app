package bot.db.repositories.impl;

import bot.db.models.Experiencia;
import bot.db.repositories.ExperienciaRepository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExperienciaRepositoryImpl extends AbstractRepository<Experiencia, Long> implements ExperienciaRepository {

    @Override
    protected String getTableName() {
        return "experiencias";
    }

    @Override
    protected String getIdColumnName() {
        return "id_experiencia";
    }

    @Override
    protected Experiencia mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Experiencia(
                rs.getLong("id_experiencia"),
                rs.getLong("id_usuario"),
                rs.getInt("nivel"),
                rs.getInt("puntos_xp"));
    }

    @Override
    protected void setStatementParameters(PreparedStatement stmt, Experiencia experiencia) throws SQLException {
        stmt.setLong(1, experiencia.getIdExperiencia());
        stmt.setLong(2, experiencia.getIdUsuario());
        stmt.setInt(3, experiencia.getNivel());
        stmt.setInt(4, experiencia.getPuntosXp());
    }

    @Override
    public Experiencia save(Experiencia experiencia) {
        try (var conn = getConnection()) {
            String sql = "INSERT INTO experiencias (id_experiencia, id_usuario, nivel, puntos_xp) " +
                    "VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE nivel = ?, puntos_xp = ?";
            try (var stmt = conn.prepareStatement(sql)) {
                setStatementParameters(stmt, experiencia);
                stmt.setInt(5, experiencia.getNivel());
                stmt.setInt(6, experiencia.getPuntosXp());
                stmt.executeUpdate();
                return experiencia;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la experiencia", e);
        }
    }

    @Override
    public Optional<Experiencia> findByIdUsuario(Long idUsuario) {
        try (var conn = getConnection()) {
            String sql = "SELECT * FROM experiencias WHERE id_usuario = ?";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, idUsuario);
                var rs = stmt.executeQuery();
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar experiencia por id de usuario", e);
        }
    }

    @Override
    public List<Experiencia> findByNivelGreaterThan(int nivel) {
        try (var conn = getConnection()) {
            String sql = "SELECT * FROM experiencias WHERE nivel > ?";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, nivel);
                var rs = stmt.executeQuery();
                List<Experiencia> experiencias = new ArrayList<>();
                while (rs.next()) {
                    experiencias.add(mapResultSetToEntity(rs));
                }
                return experiencias;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar experiencias por nivel", e);
        }
    }
}