package backend.facade;

import org.springframework.web.bind.annotation.*;

import backend.facade.dto.BotConfigDTO;
import backend.facade.dto.BotIntegracionesDTO;
import backend.facade.dto.BotPresenceDTO;
import backend.facade.dto.BotStatusDTO;
import backend.facade.dto.BotTokenDTO;
import backend.facade.dto.DatabaseStatsDTO;
import backend.facade.dto.GeminiKeyDTO;
import backend.facade.dto.ModuleDTO;
import backend.facade.dto.TokenInfoDTO;
import backend.facade.service.ModuleService;
import backend.models.Penalizacion;
import backend.models.Usuario;

import org.springframework.http.ResponseEntity;
import java.util.List;

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
    private final ModuleService moduleService;
    private final BotFacade botFacade;

    /**
     * Constructor de la clase BotRestController.
     *
     * @param botFacade     Fachada principal del bot
     * @param moduleService Servicio de módulos
     */
    public BotRestController(BotFacade botFacade, ModuleService moduleService) {
        this.botFacade = botFacade;
        this.moduleService = moduleService;
    }

    /**
     * Devuelve la lista de módulos y sus comandos.
     *
     * @return ResponseEntity con la lista de módulos
     */
    @GetMapping("/modules")
    public ResponseEntity<List<ModuleDTO>> getModules() {
        return ResponseEntity.ok(moduleService.getAllModulesWithCommands());
    }

    /**
     * Activa un módulo por nombre.
     *
     * @param nombre Nombre del módulo
     * @return ModuleDTO actualizado o 404 si no existe
     */
    @PostMapping("/modules/{nombre}/enable")
    public ResponseEntity<ModuleDTO> enableModule(@PathVariable String nombre) {
        ModuleDTO dto = moduleService.enableModule(nombre);
        if (dto == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    /**
     * Desactiva un módulo por nombre.
     *
     * @param nombre Nombre del módulo
     * @return ModuleDTO actualizado o 404 si no existe
     */
    @PostMapping("/modules/{nombre}/disable")
    public ResponseEntity<ModuleDTO> disableModule(@PathVariable String nombre) {
        ModuleDTO dto = moduleService.disableModule(nombre);
        if (dto == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    /**
     * Activa un comando individual de un módulo.
     *
     * @param nombreModulo  Nombre del módulo
     * @param nombreComando Nombre del comando
     * @return ResponseEntity vacío o 404 si no existe
     */
    @PostMapping("/modules/{nombreModulo}/commands/{nombreComando}/enable")
    public ResponseEntity<Void> enableCommand(@PathVariable String nombreModulo, @PathVariable String nombreComando) {
        boolean ok = moduleService.enableCommand(nombreModulo, nombreComando);
        if (!ok)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok().build();
    }

    /**
     * Desactiva un comando individual de un módulo.
     *
     * @param nombreModulo  Nombre del módulo
     * @param nombreComando Nombre del comando
     * @return ResponseEntity vacío o 404 si no existe
     */
    @PostMapping("/modules/{nombreModulo}/commands/{nombreComando}/disable")
    public ResponseEntity<Void> disableCommand(@PathVariable String nombreModulo, @PathVariable String nombreComando) {
        boolean ok = moduleService.disableCommand(nombreModulo, nombreComando);
        if (!ok)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok().build();
    }

    /**
     * Devuelve información sobre si el token está configurado (pero nunca el valor
     * real).
     *
     * @return ResponseEntity con información del token
     */
    @GetMapping("/config/bot-token-info")
    public ResponseEntity<TokenInfoDTO> getBotTokenInfo() {
        boolean hasToken = botFacade.hasBotToken();
        return ResponseEntity.ok(new TokenInfoDTO(hasToken));
    }

    /**
     * Devuelve la presencia/actividad actual del bot (status, tipo, nombre, url).
     *
     * @return ResponseEntity con la presencia del bot
     */
    @GetMapping("/config/presence")
    public ResponseEntity<BotPresenceDTO> getBotPresence() {
        BotPresenceDTO presence = botFacade.getBotPresence();
        return ResponseEntity.ok(presence);
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
     *
     * @param limit Límite de líneas
     * @return ResponseEntity con la lista de logs
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
     * Endpoint para añadir una nueva penalización.
     *
     * @param penalizacion El objeto Penalizacion a añadir (en el cuerpo de la
     *                     petición).
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
     *
     * @return ResponseEntity con la lista de nombres de tablas
     */
    @GetMapping("/db/tables")
    public ResponseEntity<List<String>> getTableNames() {
        return ResponseEntity.ok(botFacade.getTableNames());
    }

    /**
     * Endpoint para obtener los nombres y etiquetas de las columnas de una tabla.
     *
     * @param tableName Nombre de la tabla
     * @return ResponseEntity con la lista de columnas (nombre y etiqueta)
     */
    @GetMapping("/db/tables/{tableName}/columns")
    public ResponseEntity<java.util.List<backend.facade.dto.ColumnDTO>> getTableColumns(@PathVariable String tableName) {
        return ResponseEntity.ok(botFacade.getTableColumns(tableName));
    }

    /**
     * Endpoint para obtener el contenido de una tabla.
     *
     * @param tableName Nombre de la tabla
     * @return ResponseEntity con la lista de filas de la tabla
     */
    @GetMapping("/db/tables/{tableName}/data")
    public ResponseEntity<List<java.util.Map<String, Object>>> getTableData(@PathVariable String tableName) {
        return ResponseEntity.ok(botFacade.getTableData(tableName));
    }

    /**
     * Endpoint para obtener estadísticas resumidas de la base de datos.
     *
     * @return ResponseEntity con el DTO de estadísticas
     */
    @GetMapping("/db/stats")
    public ResponseEntity<DatabaseStatsDTO> getDatabaseStats() {
        return ResponseEntity.ok(botFacade.getDatabaseStats());
    }

    /**
     * Devuelve la configuración actual del bot (sin exponer el token).
     *
     * @return ResponseEntity con la configuración del bot
     */
    @GetMapping("/config")
    public ResponseEntity<BotConfigDTO> getBotConfig() {
        return ResponseEntity.ok(botFacade.getBotConfig());
    }

    /**
     * Guarda el token del bot.
     *
     * @param dto DTO con el token a guardar
     * @return ResponseEntity vacío
     */
    @PostMapping("/config/token")
    public ResponseEntity<Void> saveBotToken(@RequestBody BotTokenDTO dto) {
        botFacade.saveBotToken(dto.getToken());
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para obtener el estado de la clave Gemini.
     *
     * @return ResponseEntity con el DTO de clave Gemini
     */
    @GetMapping("/config/gemini-key-info")
    public ResponseEntity<GeminiKeyDTO> getGeminiKeyInfo() {
        boolean hasKey = botFacade.hasGeminiKey();
        return ResponseEntity.ok(new GeminiKeyDTO(hasKey ? "SET" : null));
    }

    /**
     * Endpoint para guardar la clave Gemini.
     *
     * @param dto El objeto GeminiKeyDTO con la clave a guardar
     * @return ResponseEntity vacío
     */
    @PostMapping("/config/gemini-key")
    public ResponseEntity<Void> saveGeminiKey(@RequestBody GeminiKeyDTO dto) {
        botFacade.saveGeminiKey(dto.getKey());
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para actualizar un registro de experiencia.
     *
     * @param id ID del registro a actualizar
     * @param experiencia El objeto con los datos actualizados de experiencia
     * @return ResponseEntity con el registro actualizado o error si falla
     */
    @PutMapping("/db/experiencia/{id}")
    public ResponseEntity<java.util.Map<String, Object>> updateExperiencia(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Object> experiencia) {
        try {
            boolean updated = botFacade.updateExperiencia(id, experiencia);
            if (updated) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para eliminar un registro de experiencia.
     *
     * @param id ID del registro a eliminar
     * @return ResponseEntity vacío o error si falla
     */
    @DeleteMapping("/db/experiencia/{id}")
    public ResponseEntity<Void> deleteExperiencia(@PathVariable Long id) {
        try {
            boolean deleted = botFacade.deleteExperiencia(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}