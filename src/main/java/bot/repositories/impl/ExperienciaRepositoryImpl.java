package bot.repositories.impl;

import bot.models.Experiencia;
import bot.repositories.ExperienciaRepository;
import bot.db.DatabaseManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio de experiencia de usuario.
 * Permite operaciones CRUD y consultas específicas sobre la experiencia.
 * 
 * @author PelayoPS
 */
public class ExperienciaRepositoryImpl extends AbstractRepository<Experiencia, Long> implements ExperienciaRepository {

    /**
     * Constructor de ExperienciaRepositoryImpl.
     * 
     * @param databaseManager Gestor de base de datos
     */
    public ExperienciaRepositoryImpl(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTableName() {
        return "experiencias";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getIdColumnName() {
        return "id_experiencia";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Experiencia mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Experiencia(
                rs.getLong("id_experiencia"),
                rs.getLong("id_usuario"),
                rs.getInt("nivel"),
                rs.getInt("puntos_xp"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setStatementParameters(PreparedStatement stmt, Experiencia experiencia) throws SQLException {
        stmt.setLong(1, experiencia.getIdExperiencia());
        stmt.setLong(2, experiencia.getIdUsuario());
        stmt.setInt(3, experiencia.getNivel());
        stmt.setInt(4, experiencia.getPuntosXp());
    }

    /**
     * Guarda o actualiza una experiencia en la base de datos.
     * 
     * @param experiencia Experiencia a guardar
     * @return Experiencia guardada
     */
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

    /**
     * Busca una experiencia por el ID de usuario.
     * 
     * @param idUsuario ID del usuario
     * @return Optional con la experiencia encontrada o vacío si no existe
     */
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

    /**
     * Busca todas las experiencias con nivel mayor que el indicado.
     * 
     * @param nivel Nivel mínimo (exclusivo)
     * @return Lista de experiencias encontradas
     */
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