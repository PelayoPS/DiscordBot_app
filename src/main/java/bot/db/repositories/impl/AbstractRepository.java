package bot.db.repositories.impl;

import bot.db.DatabaseManager;
import bot.db.repositories.Repository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractRepository<T, ID> implements Repository<T, ID> {
    protected final DatabaseManager databaseManager;

    protected AbstractRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    protected Connection getConnection() throws SQLException {
        return databaseManager.getConnection();
    }

    // Métodos abstractos que las implementaciones específicas deben proporcionar
    protected abstract String getTableName();

    protected abstract String getIdColumnName();

    protected abstract T mapResultSetToEntity(java.sql.ResultSet rs) throws SQLException;

    protected abstract void setStatementParameters(java.sql.PreparedStatement stmt, T entity) throws SQLException;

    @Override
    public T save(T entity) {
        try (Connection conn = getConnection()) {
            // La implementación específica debe manejar esto
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la entidad", e);
        }
    }

    @Override
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

    @Override
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

    @Override
    public void delete(T entity) {
        // La implementación específica debe manejar esto
    }

    @Override
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

    @Override
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