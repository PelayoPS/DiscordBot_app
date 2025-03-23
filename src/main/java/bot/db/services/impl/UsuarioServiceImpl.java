package bot.db.services.impl;

import bot.db.models.Usuario;
import bot.db.models.Experiencia;
import bot.db.models.Penalizacion;
import bot.db.repositories.UsuarioRepository;
import bot.db.repositories.ExperienciaRepository;
import bot.db.repositories.PenalizacionRepository;
import bot.db.services.UsuarioService;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Optional;

public class UsuarioServiceImpl extends AbstractService<Usuario, Long> implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final ExperienciaRepository experienciaRepository;
    private final PenalizacionRepository penalizacionRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
            ExperienciaRepository experienciaRepository,
            PenalizacionRepository penalizacionRepository) {
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
        this.experienciaRepository = experienciaRepository;
        this.penalizacionRepository = penalizacionRepository;
    }

    @Override
    public Optional<Usuario> findByTipoUsuario(String tipoUsuario) {
        return usuarioRepository.findByTipoUsuario(tipoUsuario);
    }

    @Override
    public void actualizarExperiencia(Long idUsuario, int puntosXp) {
        Optional<Experiencia> experienciaOpt = experienciaRepository.findByIdUsuario(idUsuario);
        if (experienciaOpt.isPresent()) {
            Experiencia experiencia = experienciaOpt.get();
            experiencia.setPuntosXp(experiencia.getPuntosXp() + puntosXp);

            // Verificar si debe subir de nivel (cada 100 puntos)
            if (experiencia.getPuntosXp() >= (experiencia.getNivel() + 1) * 100) {
                experiencia.setNivel(experiencia.getNivel() + 1);
            }

            experienciaRepository.save(experiencia);
        } else {
            // Crear nueva experiencia si no existe
            Experiencia nuevaExperiencia = new Experiencia(null, idUsuario, 1, puntosXp);
            experienciaRepository.save(nuevaExperiencia);
        }
    }

    @Override
    public void agregarPenalizacion(Long idUsuario, Long idAdminMod, String tipo, String razon, Duration duracion) {
        Penalizacion penalizacion = new Penalizacion(
                null,
                idUsuario,
                idAdminMod,
                tipo,
                LocalDateTime.now(),
                razon,
                duracion);
        penalizacionRepository.save(penalizacion);
    }
}