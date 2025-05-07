package bot.core;

import bot.repositories.*;
import bot.repositories.impl.*;
import bot.services.*;
import bot.services.impl.*;
import bot.modules.admin.AdminCommands;
import bot.modules.mod.ModCommands;
import bot.modules.user.UserCommands;
import bot.facade.BotFacade;
import bot.facade.BotFacadeImpl;
import bot.config.ConfigService;
import bot.controller.AdminController;
import bot.controller.UserController;
import bot.db.DatabaseManager;
import bot.log.LoggingManager;

/**
 * Fábrica de servicios y controladores para el bot.
 * Permite obtener instancias de servicios, controladores y módulos principales.
 * 
 * @author PelayoPS
 */
public class ServiceFactory {
    private final DatabaseManager databaseManager;
    private final UsuarioRepository usuarioRepository;
    private final ExperienciaRepository experienciaRepository;
    private final PenalizacionRepository penalizacionRepository;

    private final UsuarioService usuarioService;
    private final ExperienciaService experienciaService;
    private final PenalizacionService penalizacionService;
    private final RoleService roleService;
    private final UserService userService;
    private final AIChatService aiChatService;

    private final AdminCommands adminCommands;
    private final UserCommands userCommands;

    private final AdminController adminController;
    private final UserController userController;

    // Facade
    private final BotFacade botFacade;

    private final LoggingManager loggingManager;

    /**
     * Constructor de la clase ServiceFactory.
     * 
     * @param configService   Servicio de configuración
     * @param databaseManager Gestor de base de datos
     */
    public ServiceFactory(ConfigService configService, DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.usuarioRepository = new UsuarioRepositoryImpl(databaseManager);
        this.experienciaRepository = new ExperienciaRepositoryImpl(databaseManager);
        this.penalizacionRepository = new PenalizacionRepositoryImpl(databaseManager);

        this.usuarioService = new UsuarioServiceImpl(usuarioRepository, experienciaRepository, penalizacionRepository);
        this.experienciaService = new ExperienciaServiceImpl(experienciaRepository);
        this.penalizacionService = new PenalizacionServiceImpl(penalizacionRepository);
        ModerationService moderationService = new ModerationServiceImpl(penalizacionService);

        this.roleService = new RoleServiceImpl();
        this.userService = new UserServiceImpl(); // Ajusta según implementación real
        this.aiChatService = new AIChatServiceImpl();
        this.userController = new UserController(userService, aiChatService);
        this.adminController = new AdminController(roleService);
        this.adminCommands = new AdminCommands(adminController);
        this.userCommands = new UserCommands(userController);

        this.loggingManager = new LoggingManager();
        this.botFacade = new BotFacadeImpl(usuarioService, moderationService, null, databaseManager);
    }

    /**
     * Obtiene el servicio de usuarios.
     * 
     * @return UsuarioService
     */
    public UsuarioService getUsuarioService() {
        return usuarioService;
    }

    /**
     * Obtiene el servicio de experiencia.
     * 
     * @return ExperienciaService
     */
    public ExperienciaService getExperienciaService() {
        return experienciaService;
    }

    /**
     * Obtiene el servicio de penalizaciones.
     * 
     * @return PenalizacionService
     */
    public PenalizacionService getPenalizacionService() {
        return penalizacionService;
    }

    /**
     * Obtiene los comandos de administración.
     * 
     * @return AdminCommands
     */
    public AdminCommands getAdminCommands() {
        return adminCommands;
    }

    /**
     * Obtiene los comandos de moderación.
     * 
     * @return ModCommands
     */
    public ModCommands getModCommands() {
        // Ahora pasa también la instancia de ServiceFactory
        return new ModCommands(botFacade, this);
    }

    /**
     * Obtiene los comandos de usuario.
     * 
     * @return UserCommands
     */
    public UserCommands getUserCommands() {
        return userCommands;
    }

    /**
     * Obtiene la fachada principal del bot.
     * 
     * @return BotFacade
     */
    public BotFacade getBotFacade() {
        return botFacade;
    }

    /**
     * Obtiene el gestor de base de datos.
     * 
     * @return DatabaseManager
     */
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
