package bot.db.repositories.impl;

import bot.db.models.Usuario;
import bot.db.repositories.UsuarioRepository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UsuarioRepositoryImpl extends AbstractRepository<Usuario, Long> implements UsuarioRepository {

    @Override
    protected String getTableName() {
        return "usuarios";
    }

    @Override
    protected String getIdColumnName() {
        return "id_usuario";
    }

    @Override
    protected Usuario mapResultSetToEntity(ResultSet rs) throws SQLException {
        Long idUsuario = rs.getLong("id_usuario");
        String tipoUsuario = rs.getString("tipo_usuario");
        return new Usuario(idUsuario, tipoUsuario);
    }

    @Override
    protected void setStatementParameters(PreparedStatement stmt, Usuario usuario) throws SQLException {
        stmt.setLong(1, usuario.getIdUsuario());
        stmt.setString(2, usuario.getTipoUsuario());
    }

    @Override
    public Usuario save(Usuario usuario) {
        try (var conn = getConnection()) {
            String sql = "INSERT INTO usuarios (id_usuario, tipo_usuario) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE tipo_usuario = ?";
            try (var stmt = conn.prepareStatement(sql)) {
                setStatementParameters(stmt, usuario);
                stmt.setString(3, usuario.getTipoUsuario());
                stmt.executeUpdate();
                return usuario;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el usuario", e);
        }
    }

    @Override
    public Optional<Usuario> findByTipoUsuario(String tipoUsuario) {
        try (var conn = getConnection()) {
            String sql = "SELECT * FROM usuarios WHERE tipo_usuario = ?";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, tipoUsuario);
                var rs = stmt.executeQuery();
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario por tipo", e);
        }
    }
}