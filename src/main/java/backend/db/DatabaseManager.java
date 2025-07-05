package backend.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import backend.config.ConfigService;
import backend.log.LoggingManager;

/**
 * Clase para gestionar la conexión a la base de datos.
 * Permite obtener conexiones, crear la base de datos si no existe y cerrar la
 * conexión.
 * 
 * @author PelayoPS
 */
public class DatabaseManager {
    private Connection connection;
    private final ConfigService configService;
    private static final LoggingManager logger = new LoggingManager();

    /**
     * Constructor de la clase DatabaseManager.
     *
     * @param configService Servicio de configuración para obtener los datos de
     *                      conexión
     */
    public DatabaseManager(ConfigService configService) {
        this.configService = configService;
        try {
            createDatabaseIfNotExists();
        } catch (SQLException e) {
            logger.logError("Error al crear o verificar la base de datos durante la inicialización. El bot continuará sin conexión a BD activa.", e);
        }
    }

    /**
     * Obtiene una conexión a la base de datos.
     * Si la conexión no existe o está cerrada, la crea.
     *
     * @return Conexión a la base de datos
     * @throws SQLException si ocurre un error al conectar
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = configService.get("db.url");
            String username = configService.get("db.username");
            String password = configService.get("db.password");
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("No se pudo cargar el driver de MySQL", e);
            }
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    /**
     * Crea la base de datos si no existe.
     *
     * @throws SQLException si hay un error al crear la base de datos
     */
    private void createDatabaseIfNotExists() throws SQLException {
        String url = configService.get("db.url");
        String dbName = url.substring(url.lastIndexOf('/') + 1);
        String baseUrl = url.substring(0, url.lastIndexOf('/'));
        String username = configService.get("db.username");
        String password = configService.get("db.password");
        try (Connection rootConnection = DriverManager.getConnection(baseUrl, username, password);
                Statement stmt = rootConnection.createStatement()) {
            String createDbSQL = "CREATE DATABASE IF NOT EXISTS " + dbName;
            stmt.executeUpdate(createDbSQL);
            logger.logInfo("Base de datos '" + dbName + "' creada o verificada con éxito");
        }
    }

    /**
     * Cierra la conexión a la base de datos si está abierta.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Error al cerrar la conexión", e);
            }
        }
    }
}