package bot.db.controllers;

import bot.db.models.Usuario;
import bot.db.services.UsuarioService;
import java.time.Duration;
import java.util.Optional;

public class UsuarioController extends Controller<Usuario, Long> {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        super(usuarioService);
        this.usuarioService = usuarioService;
    }

    public Optional<Usuario> buscarPorTipoUsuario(String tipoUsuario) {
        return usuarioService.findByTipoUsuario(tipoUsuario);
    }

    public void actualizarExperiencia(Long idUsuario, int puntosXp) {
        usuarioService.actualizarExperiencia(idUsuario, puntosXp);
    }

    public void aplicarPenalizacion(Long idUsuario, Long idAdminMod, String tipo, String razon, Duration duracion) {
        usuarioService.agregarPenalizacion(idUsuario, idAdminMod, tipo, razon, duracion);
    }
}