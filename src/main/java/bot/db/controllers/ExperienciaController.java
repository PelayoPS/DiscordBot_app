package bot.db.controllers;

import bot.db.models.Experiencia;
import bot.db.services.ExperienciaService;
import java.util.List;
import java.util.Optional;

public class ExperienciaController extends Controller<Experiencia, Long> {
    private final ExperienciaService experienciaService;

    public ExperienciaController(ExperienciaService experienciaService) {
        super(experienciaService);
        this.experienciaService = experienciaService;
    }

    public Optional<Experiencia> buscarPorIdUsuario(Long idUsuario) {
        return experienciaService.findByIdUsuario(idUsuario);
    }

    public List<Experiencia> buscarPorNivelMayorQue(int nivel) {
        return experienciaService.findByNivelGreaterThan(nivel);
    }

    public void aumentarExperiencia(Long idUsuario, int puntos) {
        experienciaService.aumentarExperiencia(idUsuario, puntos);
    }

    public void subirNivel(Long idUsuario) {
        experienciaService.subirNivel(idUsuario);
    }
}