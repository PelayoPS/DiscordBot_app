package bot.repositories.impl;

import bot.repositories.Repository;
import bot.db.DatabaseManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación abstracta base para los repositorios.
 * Proporciona operaciones CRUD genéricas y requiere que las subclases
 * implementen el mapeo y detalles de tabla.
 * 
 * @author PelayoPS
 */
public abstract class AbstractRepository<T, ID> implements Repository<T, ID> {
    protected final DatabaseManager databaseManager;

    /**
     * Constructor de AbstractRepository.
     * 
     * @param databaseManager Gestor de base de datos
     */
    protected AbstractRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Obtiene una conexión a la base de datos.
     * 
     * @return Conexión SQL
     * @throws SQLException si ocurre un error al conectar
     */
    protected Connection getConnection() throws SQLException {
        return databaseManager.getConnection();
    }

    // Métodos abstractos que las implementaciones específicas deben proporcionar

    /**
     * Devuelve el nombre de la tabla asociada al repositorio.
     * 
     * @return Nombre de la tabla
     */
    protected abstract String getTableName();

    /**
     * Devuelve el nombre de la columna ID de la tabla.
     * 
     * @return Nombre de la columna ID
     */
    protected abstract String getIdColumnName();

    /**
     * Mapea un ResultSet a una entidad.
     * 
     * @param rs ResultSet de la consulta
     * @return Entidad mapeada
     * @throws SQLException si ocurre un error de mapeo
     */
    protected abstract T mapResultSetToEntity(java.sql.ResultSet rs) throws SQLException;

    /**
     * Establece los parámetros de la entidad en un PreparedStatement.
     * 
     * @param stmt   PreparedStatement
     * @param entity Entidad a persistir
     * @throws SQLException si ocurre un error al establecer parámetros
     */
    protected abstract void setStatementParameters(java.sql.PreparedStatement stmt, T entity) throws SQLException;

    /**
     * Guarda una entidad en la base de datos.
     * 
     * @param entity Entidad a guardar
     * @return Entidad guardada o null si no implementado
     */
    public T save(T entity) {
        try (Connection conn = getConnection()) {
            // La implementación específica debe manejar esto
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la entidad", e);
        }
    }

    /**
     * Busca una entidad por su ID.
     * 
     * @param id ID de la entidad
     * @return Optional con la entidad encontrada o vacío si no existe
     */
    public Optional<T> findById(ID id) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setObject(1, id);
                var rs = stmt.executeQuery();
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar por ID", e);
        }
    }

    /**
     * Obtiene todas las entidades de la tabla.
     * 
     * @return Lista de entidades
     */
    public List<T> findAll() {
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM " + getTableName();
            try (var stmt = conn.prepareStatement(sql)) {
                var rs = stmt.executeQuery();
                List<T> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(mapResultSetToEntity(rs));
                }
                return results;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar todas las entidades", e);
        }
    }

    /**
     * Elimina una entidad de la base de datos.
     * 
     * @param entity Entidad a eliminar
     */
    public void delete(T entity) {
        // La implementación específica debe manejar esto
    }

    /**
     * Elimina una entidad por su ID.
     * 
     * @param id ID de la entidad a eliminar
     */
    public void deleteById(ID id) {
        try (Connection conn = getConnection()) {
            String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setObject(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar por ID", e);
        }
    }

    /**
     * Verifica si existe una entidad por su ID.
     * 
     * @param id ID a verificar
     * @return true si existe, false si no
     */
    public boolean existsById(ID id) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT 1 FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setObject(1, id);
                var rs = stmt.executeQuery();
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia por ID", e);
        }
    }
}