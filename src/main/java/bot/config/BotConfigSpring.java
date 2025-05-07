package bot.config;

import bot.core.ServiceFactory;
import bot.db.DatabaseManager;
import bot.log.LoggingManager;
import bot.modules.CommandManager;
import bot.modules.mod.ModCommands;
import bot.modules.user.UserCommands;
import bot.services.AIChatService;
import bot.services.ModerationService;
import bot.services.UsuarioService;
import bot.services.UserService;
import bot.services.impl.AIChatServiceImpl;
import bot.services.impl.ModerationServiceImpl;
import bot.services.impl.UserServiceImpl;
import bot.controller.UserController;
import bot.facade.BotFacade;
import bot.facade.BotFacadeImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuración de beans para el backend Spring Boot.
 * Define y gestiona los beans principales para la infraestructura del bot.
 * 
 * @author PelayoPS
 */
@Configuration
public class BotConfigSpring {
    /**
     * Bean para el servicio de configuración.
     * 
     * @return ConfigService instancia de configuración
     */
    @Bean
    public ConfigService configService() {
        return new FileConfigService("src/main/resources/config.properties");
    }

    /**
     * Bean para el gestor de base de datos.
     * 
     * @param configService Servicio de configuración
     * @return DatabaseManager instancia de gestor de base de datos
     */
    @Bean
    @Primary
    public DatabaseManager databaseManager(ConfigService configService) {
        return new DatabaseManager(configService);
    }

    /**
     * Bean para el gestor de logs.
     * 
     * @return LoggingManager instancia de gestor de logs
     */
    @Bean
    @Primary
    public LoggingManager loggingManager() {
        return new LoggingManager();
    }

    /**
     * Bean para la fachada principal del bot.
     * 
     * @param usuarioService    Servicio de usuarios
     * @param moderationService Servicio de moderación
     * @param commandManager    Gestor de comandos
     * @param databaseManager   Gestor de base de datos
     * @return BotFacade instancia de la fachada del bot
     */
    @Bean
    public BotFacade botFacade(UsuarioService usuarioService, ModerationService moderationService,
            CommandManager commandManager, DatabaseManager databaseManager) {
        return new BotFacadeImpl(usuarioService, moderationService, commandManager, databaseManager);
    }

    /**
     * Bean principal para los comandos de moderación.
     * 
     * @param serviceFactory Fábrica de servicios
     * @return ModCommands instancia de comandos de moderación
     */
    @Bean
    public ModCommands modCommands(ServiceFactory serviceFactory) {
        return new ModCommands(null, serviceFactory);
    }

    /**
     * Bean para el gestor de comandos.
     * 
     * @param modCommands Comandos de moderación
     * @return CommandManager instancia del gestor de comandos
     */
    @Bean
    @Primary
    public CommandManager commandManager(ModCommands modCommands) {
        return modCommands;
    }

    /**
     * Bean para la fábrica de servicios.
     * 
     * @param configService   Servicio de configuración
     * @param databaseManager Gestor de base de datos
     * @return ServiceFactory instancia de la fábrica de servicios
     */
    @Bean
    public ServiceFactory serviceFactory(ConfigService configService, DatabaseManager databaseManager) {
        return new ServiceFactory(configService, databaseManager);
    }

    /**
     * Bean para el servicio de usuario.
     * 
     * @param serviceFactory Fábrica de servicios
     * @return UsuarioService instancia del servicio de usuario
     */
    @Bean
    @Primary
    public UsuarioService usuarioService(ServiceFactory serviceFactory) {
        return serviceFactory.getUsuarioService();
    }

    /**
     * Bean para el servicio de moderación.
     * 
     * @param serviceFactory Fábrica de servicios
     * @return ModerationService instancia del servicio de moderación
     */
    @Bean
    @Primary
    public ModerationService moderationService(ServiceFactory serviceFactory) {
        return new ModerationServiceImpl(serviceFactory.getPenalizacionService());
    }

    /**
     * Bean para el servicio de usuario (implementación).
     * 
     * @return UserService instancia del servicio de usuario
     */
    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }

    /**
     * Bean para el servicio de chat IA.
     * 
     * @return AIChatService instancia del servicio de chat IA
     */
    @Bean
    public AIChatService aiChatService() {
        return new AIChatServiceImpl();
    }

    /**
     * Bean para el controlador de usuario.
     * 
     * @param userService   Servicio de usuario
     * @param aiChatService Servicio de chat IA
     * @return UserController instancia del controlador de usuario
     */
    @Bean
    public UserController userController(UserService userService, AIChatService aiChatService) {
        return new UserController(userService, aiChatService);
    }

    /**
     * Bean para los comandos de usuario.
     * 
     * @param userController Controlador de usuario
     * @return UserCommands instancia de comandos de usuario
     */
    @Bean
    public UserCommands userCommands(UserController userController) {
        return new UserCommands(userController);
    }
}
