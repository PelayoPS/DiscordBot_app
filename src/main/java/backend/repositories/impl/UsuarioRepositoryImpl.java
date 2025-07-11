package backend.repositories.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import backend.db.DatabaseManager;
import backend.models.Usuario;
import backend.repositories.UsuarioRepository;

/**
 * Implementación del repositorio de usuarios.
 * Permite operaciones CRUD y consultas específicas sobre usuarios.
 * 
 * @author PelayoPS
 */
public class UsuarioRepositoryImpl extends AbstractRepository<Usuario, Long> implements UsuarioRepository {

    /**
     * Constructor de UsuarioRepositoryImpl.
     * 
     * @param databaseManager Gestor de base de datos
     */
    public UsuarioRepositoryImpl(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTableName() {
        return "usuarios";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getIdColumnName() {
        return "id_usuario";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Usuario mapResultSetToEntity(ResultSet rs) throws SQLException {
        Long idUsuario = rs.getLong("id_usuario");
        String tipoUsuario = rs.getString("tipo_usuario");
        int nivel = rs.getInt("nivel");
        int puntosXp = rs.getInt("puntos_xp");
        long timestampUltimoMensaje = rs.getLong("timestamp_ultimo_mensaje");
        Usuario usuario = new Usuario(idUsuario, tipoUsuario);
        usuario.setNivel(nivel);
        usuario.setPuntosXp(puntosXp);
        usuario.setTimestampUltimoMensaje(timestampUltimoMensaje);
        return usuario;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setStatementParameters(PreparedStatement stmt, Usuario usuario) throws SQLException {
        stmt.setLong(1, usuario.getIdUsuario());
        stmt.setString(2, usuario.getTipoUsuario());
        stmt.setInt(3, usuario.getNivel());
        stmt.setInt(4, usuario.getPuntosXp());
        stmt.setLong(5, usuario.getTimestampUltimoMensaje());
    }

    /**
     * Guarda o actualiza un usuario en la base de datos.
     * 
     * @param usuario Usuario a guardar
     * @return Usuario guardado
     */
    @Override
    public Usuario save(Usuario usuario) {
        try (var conn = getConnection()) {
            String sql = "INSERT INTO usuarios (id_usuario, tipo_usuario, nivel, puntos_xp, timestamp_ultimo_mensaje) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE tipo_usuario = VALUES(tipo_usuario), nivel = VALUES(nivel), puntos_xp = VALUES(puntos_xp), timestamp_ultimo_mensaje = VALUES(timestamp_ultimo_mensaje)";
            try (var stmt = conn.prepareStatement(sql)) {
                setStatementParameters(stmt, usuario);
                stmt.executeUpdate();
                return usuario;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el usuario", e);
        }
    }

    /**
     * Busca un usuario por tipo de usuario.
     * 
     * @param tipoUsuario Tipo de usuario
     * @return Optional con el usuario encontrado o vacío si no existe
     */
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