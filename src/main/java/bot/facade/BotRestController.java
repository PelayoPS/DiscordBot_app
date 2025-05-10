package bot.facade;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import bot.models.Usuario;
import bot.models.Penalizacion;
import bot.models.Experiencia;

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
     * Endpoint para obtener logs filtrados por tipo y fecha.
     * Ejemplo de uso:
     * /api/logs?types=INFO&types=ERROR&from=2025-05-01&to=2025-05-06&limit=0
     */
    @GetMapping("/logs")
    public ResponseEntity<List<String>> getLogs(
            @RequestParam(required = false, defaultValue = "0") int limit) {
        List<String> logs = botFacade.getLogs(limit);

        return ResponseEntity.ok(logs);
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
     * Endpoint para comprobar si el backend está activo (ping).
     * 
     * @return ResponseEntity con status 200 y un mensaje simple
     */
    @GetMapping("/botfacade/ping")
    public ResponseEntity<String> pingBackend() {
        return ResponseEntity.ok("pong");
    }

    // --- Entity Management Endpoints ---

    /**
     * Endpoint para añadir un nuevo usuario.
     * 
     * @param usuario El objeto Usuario a añadir (en el cuerpo de la petición).
     * @return ResponseEntity con el Usuario creado o error si falla.
     */
    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> addUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = botFacade.addUsuario(usuario);
            if (nuevoUsuario != null) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(nuevoUsuario);
            } else {
                return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para añadir o actualizar la experiencia de un usuario.
     * 
     * @param experiencia El objeto Experiencia a añadir/actualizar (en el cuerpo de la petición).
     * @return ResponseEntity con la Experiencia guardada o error si falla.
     */
    @PostMapping("/experiencias")
    public ResponseEntity<Experiencia> addExperiencia(@RequestBody Experiencia experiencia) {
        try {
            Experiencia nuevaExperiencia = botFacade.addExperiencia(experiencia);
            if (nuevaExperiencia != null) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(nuevaExperiencia);
            } else {
                return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para añadir una nueva penalización.
     * 
     * @param penalizacion El objeto Penalizacion a añadir (en el cuerpo de la petición).
     * @return ResponseEntity con la Penalizacion creada o error si falla.
     */
    @PostMapping("/penalizaciones")
    public ResponseEntity<Penalizacion> addPenalizacion(@RequestBody Penalizacion penalizacion) {
        try {
            Penalizacion nuevaPenalizacion = botFacade.addPenalizacion(penalizacion);
            if (nuevaPenalizacion != null) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(nuevaPenalizacion);
            } else {
                return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para obtener los nombres de todas las tablas de la base de datos.
     */
    @GetMapping("/db/tables")
    public ResponseEntity<List<String>> getTableNames() {
        return ResponseEntity.ok(botFacade.getTableNames());
    }

    /**
     * Endpoint para obtener los nombres de las columnas de una tabla.
     */
    @GetMapping("/db/tables/{tableName}/columns")
    public ResponseEntity<List<String>> getTableColumns(@PathVariable String tableName) {
        return ResponseEntity.ok(botFacade.getTableColumns(tableName));
    }

    /**
     * Endpoint para obtener el contenido de una tabla.
     */
    @GetMapping("/db/tables/{tableName}/data")
    public ResponseEntity<List<java.util.Map<String, Object>>> getTableData(@PathVariable String tableName) {
        return ResponseEntity.ok(botFacade.getTableData(tableName));
    }

    /**
     * Endpoint para obtener estadísticas resumidas de la base de datos.
     * @return ResponseEntity con el DTO de estadísticas
     */
    @GetMapping("/db/stats")
    public ResponseEntity<DatabaseStatsDTO> getDatabaseStats() {
        return ResponseEntity.ok(botFacade.getDatabaseStats());
    }
}