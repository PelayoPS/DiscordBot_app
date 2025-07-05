package backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import backend.commands.ModuleManager;
import backend.controller.UserController;
import backend.core.ServiceFactory;
import backend.db.DatabaseManager;
import backend.facade.BotFacade;
import backend.facade.BotFacadeImpl;
import backend.log.LoggingManager;
import backend.modules.CommandManager;
import backend.modules.mod.ModCommands;
import backend.modules.user.UserCommands;
import backend.services.AIChatService;
import backend.services.ModerationService;
import backend.services.UserService;
import backend.services.UsuarioService;
import backend.services.impl.AIChatServiceImpl;
import backend.services.impl.ModerationServiceImpl;
import backend.services.impl.UserServiceImpl;

/**
 * Configuración de beans para el backend Spring Boot.
 * Define y gestiona los beans principales para la infraestructura del bot.
 * 
 * @author PelayoPS
 */
@Configuration
public class BotConfigSpring {

    /**
     * Crea el bean para el gestor de módulos.
     * 
     * @return instancia de ModuleManager
     */
    @Bean
    public ModuleManager moduleManager() {
        return new ModuleManager();
    }

    /**
     * Crea el bean para el servicio de configuración.
     * 
     * @return instancia de ConfigService
     */
    @Bean
    public ConfigService configService() {
        return new FileConfigService("src/main/resources/config.properties");
    }

    /**
     * Crea el bean para el gestor de base de datos.
     * 
     * @param configService servicio de configuración
     * @return instancia de DatabaseManager
     */
    @Bean
    @Primary
    public DatabaseManager databaseManager(ConfigService configService) {
        return new DatabaseManager(configService);
    }

    /**
     * Crea el bean para el gestor de logs.
     * 
     * @return instancia de LoggingManager
     */
    @Bean
    @Primary
    public LoggingManager loggingManager() {
        return new LoggingManager();
    }

    /**
     * Crea el bean para la fachada principal del bot.
     * 
     * @param usuarioService servicio de usuarios
     * @param moderationService servicio de moderación
     * @param commandManager gestor de comandos
     * @param databaseManager gestor de base de datos
     * @param configService servicio de configuración
     * @param moduleManager gestor de módulos
     * @return instancia de BotFacade
     */
    @Bean
    public BotFacade botFacade(UsuarioService usuarioService, ModerationService moderationService,
            CommandManager commandManager, DatabaseManager databaseManager, ConfigService configService, ModuleManager moduleManager) {
        return new BotFacadeImpl(usuarioService, moderationService, commandManager, databaseManager, configService, moduleManager);
    }

    /**
     * Crea el bean principal para los comandos de moderación.
     * 
     * @param serviceFactory fábrica de servicios
     * @return instancia de ModCommands
     */
    @Bean
    public ModCommands modCommands(ServiceFactory serviceFactory) {
        return new ModCommands(null, serviceFactory);
    }

    /**
     * Crea el bean para el gestor de comandos.
     * 
     * @param modCommands comandos de moderación
     * @return instancia de CommandManager
     */
    @Bean
    @Primary
    public CommandManager commandManager(ModCommands modCommands) {
        return modCommands;
    }

    /**
     * Crea el bean para la fábrica de servicios.
     * 
     * @param configService servicio de configuración
     * @param databaseManager gestor de base de datos
     * @return instancia de ServiceFactory
     */
    @Bean
    public ServiceFactory serviceFactory(ConfigService configService, DatabaseManager databaseManager) {
        return new ServiceFactory(configService, databaseManager);
    }

    /**
     * Crea el bean para el servicio de usuario.
     * 
     * @param serviceFactory fábrica de servicios
     * @return instancia de UsuarioService
     */
    @Bean
    @Primary
    public UsuarioService usuarioService(ServiceFactory serviceFactory) {
        return serviceFactory.getUsuarioService();
    }

    /**
     * Crea el bean para el servicio de moderación.
     * 
     * @param serviceFactory fábrica de servicios
     * @return instancia de ModerationService
     */
    @Bean
    @Primary
    public ModerationService moderationService(ServiceFactory serviceFactory) {
        return new ModerationServiceImpl(serviceFactory.getPenalizacionService());
    }

    /**
     * Crea el bean para el servicio de usuario (implementación).
     * 
     * @return instancia de UserService
     */
    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }

    /**
     * Crea el bean para el servicio de chat IA.
     * 
     * @return instancia de AIChatService
     */
    @Bean
    public AIChatService aiChatService() {
        return new AIChatServiceImpl();
    }

    /**
     * Crea el bean para el controlador de usuario.
     * 
     * @param userService servicio de usuario
     * @param aiChatService servicio de chat IA
     * @return instancia de UserController
     */
    @Bean
    public UserController userController(UserService userService, AIChatService aiChatService) {
        return new UserController(userService, aiChatService);
    }

    /**
     * Crea el bean para los comandos de usuario.
     * 
     * @param userController controlador de usuario
     * @param usuarioService servicio de usuario
     * @return instancia de UserCommands
     */
    @Bean
    public UserCommands userCommands(UserController userController, backend.services.UsuarioService usuarioService) {
        return new UserCommands(userController, usuarioService);
    }
}
