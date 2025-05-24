package backend.core;

import backend.commands.ModuleManager;
import backend.config.ConfigService;
import backend.controller.AdminController;
import backend.controller.UserController;
import backend.db.DatabaseManager;
import backend.facade.BotFacade;
import backend.facade.BotFacadeImpl;
import backend.log.LoggingManager;
import backend.modules.admin.AdminCommands;
import backend.modules.mod.ModCommands;
import backend.modules.user.UserCommands;
import backend.repositories.PenalizacionRepository;
import backend.repositories.UsuarioRepository;
import backend.repositories.impl.PenalizacionRepositoryImpl;
import backend.repositories.impl.UsuarioRepositoryImpl;
import backend.services.*;
import backend.services.impl.*;

public class ServiceFactory {
    private final ConfigService configService;
    private final DatabaseManager databaseManager;
    private final UsuarioRepository usuarioRepository;
    private final PenalizacionRepository penalizacionRepository;

    private final UsuarioService usuarioService;
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
    private final ModuleManager moduleManager;

    /**
     * Constructor de la clase ServiceFactory.
     *
     * @param configService   Servicio de configuración
     * @param databaseManager Gestor de base de datos
     */
    public ServiceFactory(ConfigService configService, DatabaseManager databaseManager) {
        this(configService, databaseManager, new ModuleManager());
    }

    /**
     * Constructor de la clase ServiceFactory con gestor de módulos.
     *
     * @param configService   Servicio de configuración
     * @param databaseManager Gestor de base de datos
     * @param moduleManager   Gestor de módulos
     */
    public ServiceFactory(ConfigService configService, DatabaseManager databaseManager, ModuleManager moduleManager) {
        this.configService = configService;
        this.databaseManager = databaseManager;
        this.usuarioRepository = new UsuarioRepositoryImpl(databaseManager);
        this.penalizacionRepository = new PenalizacionRepositoryImpl(databaseManager);

        this.usuarioService = new UsuarioServiceImpl(usuarioRepository, penalizacionRepository);
        this.penalizacionService = new PenalizacionServiceImpl(penalizacionRepository);
        ModerationService moderationService = new ModerationServiceImpl(penalizacionService);

        this.roleService = new RoleServiceImpl();
        this.userService = new UserServiceImpl();
        this.aiChatService = new AIChatServiceImpl();
        this.userController = new UserController(userService, aiChatService);
        this.adminController = new AdminController(roleService);
        this.adminCommands = new AdminCommands(adminController);
        this.userCommands = new UserCommands(userController, usuarioService);

        this.loggingManager = new LoggingManager();
        this.moduleManager = moduleManager;
        this.botFacade = new BotFacadeImpl(usuarioService, moderationService, null, databaseManager, configService, moduleManager);
    }

    /**
     * Obtiene el servicio de configuración.
     *
     * @return ConfigService
     */
    public ConfigService getConfigService() {
        return configService;
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
