package bot.db.controllers;

import bot.db.models.Penalizacion;
import bot.db.services.PenalizacionService;
import java.time.LocalDateTime;
import java.util.List;

public class PenalizacionController extends Controller<Penalizacion, Long> {
    private final PenalizacionService penalizacionService;

    public PenalizacionController(PenalizacionService penalizacionService) {
        super(penalizacionService);
        this.penalizacionService = penalizacionService;
    }

    public List<Penalizacion> buscarPorIdUsuario(Long idUsuario) {
        return penalizacionService.findByIdUsuario(idUsuario);
    }

    public List<Penalizacion> buscarPorIdAdminMod(Long idAdminMod) {
        return penalizacionService.findByIdAdminMod(idAdminMod);
    }

    public List<Penalizacion> buscarPorTipo(String tipo) {
        return penalizacionService.findByTipo(tipo);
    }

    public List<Penalizacion> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return penalizacionService.findByFechaBetween(inicio, fin);
    }

    public boolean verificarPenalizacionActiva(Long idUsuario, String tipo) {
        return penalizacionService.tienePenalizacionActiva(idUsuario, tipo);
    }

    public void revocarPenalizacion(Long idPenalizacion, Long idAdminMod) {
        penalizacionService.revocarPenalizacion(idPenalizacion, idAdminMod);
    }
}