package bot.facade;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import bot.models.Usuario;
import bot.models.Penalizacion;

/**
 * Controlador REST para exponer las operaciones del bot a través de endpoints
 * HTTP.
 * Permite consultar usuarios, historial, logs, controlar el bot y obtener
 * estadísticas.
 * 
 * @author PelayoPS
 */
@RestController
@RequestMapping("/api")
public class BotRestController {
    private final BotFacade botFacade;

    /**
     * Constructor de la clase BotRestController.
     * 
     * @param botFacade Fachada principal del bot
     */
    public BotRestController(BotFacade botFacade) {
        this.botFacade = botFacade;
    }

    /**
     * Obtiene la información de un usuario por su ID.
     * 
     * @param id ID del usuario
     * @return ResponseEntity con el usuario o 404 si no existe
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<Usuario> getUser(@PathVariable String id) {
        Usuario user = botFacade.getUserInfo(id);
        if (user == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    /**
     * Obtiene el historial de penalizaciones de un usuario.
     * 
     * @param id ID del usuario
     * @return ResponseEntity con la lista de penalizaciones
     */
    @GetMapping("/user/{id}/history")
    public ResponseEntity<List<Penalizacion>> getUserHistory(@PathVariable String id) {
        return ResponseEntity.ok(botFacade.getUserHistory(id));
    }

    /**
     * Obtiene los logs de la aplicación.
     * 
     * @param level Nivel de log (opcional)
     * @param limit Límite de resultados (opcional)
     * @return ResponseEntity con la lista de logs
     */
    @GetMapping("/logs")
    public ResponseEntity<List<String>> getLogs(@RequestParam(defaultValue = "") String level,
            @RequestParam(defaultValue = "1000") int limit) {
        return ResponseEntity.ok(botFacade.getLogs(level, limit));
    }

    /**
     * Banea a un usuario.
     * 
     * @param guildId ID del servidor
     * @param userId  ID del usuario
     * @param reason  Razón del baneo
     * @return ResponseEntity vacío
     */
    @PostMapping("/ban")
    public ResponseEntity<Void> banUser(@RequestParam String guildId, @RequestParam String userId,
            @RequestParam String reason) {
        botFacade.banUser(guildId, userId, reason);
        return ResponseEntity.ok().build();
    }

    // --- Control del Bot ---

    /**
     * Obtiene el estado extendido del bot.
     * 
     * @return ResponseEntity con el estado del bot
     */
    @GetMapping("/bot/status")
    public ResponseEntity<BotStatusDTO> getBotStatus() {
        return ResponseEntity.ok(botFacade.getBotStatusExtended());
    }

    /**
     * Inicia el bot.
     * 
     * @return ResponseEntity vacío
     */
    @PostMapping("/bot/start")
    public ResponseEntity<Void> startBot() {
        botFacade.startBot();
        return ResponseEntity.ok().build();
    }

    /**
     * Detiene el bot.
     * 
     * @return ResponseEntity vacío
     */
    @PostMapping("/bot/stop")
    public ResponseEntity<Void> stopBot() {
        botFacade.stopBot();
        return ResponseEntity.ok().build();
    }

    /**
     * Reinicia el bot.
     * 
     * @return ResponseEntity vacío
     */
    @PostMapping("/bot/restart")
    public ResponseEntity<Void> restartBot() {
        botFacade.restartBot();
        return ResponseEntity.ok().build();
    }

    /**
     * Obtiene el estado de las integraciones del bot.
     * 
     * @return ResponseEntity con el DTO de integraciones
     */
    @GetMapping("/bot/integraciones")
    public ResponseEntity<BotIntegracionesDTO> getBotIntegraciones() {
        return ResponseEntity.ok(botFacade.getIntegracionesStatus());
    }

    /**
     * Obtiene estadísticas de la base de datos.
     * 
     * @return ResponseEntity con el DTO de estadísticas
     */
    @GetMapping("/db/stats")
    public ResponseEntity<DatabaseStatsDTO> getDatabaseStats() {
        return ResponseEntity.ok(botFacade.getDatabaseStats());
    }

}