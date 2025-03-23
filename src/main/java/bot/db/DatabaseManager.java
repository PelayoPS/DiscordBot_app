package bot.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Clase para gestionar la conexión a la base de datos.
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private final Properties properties;

    private DatabaseManager() {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/config.properties"));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el archivo de configuración", e);
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            // Crear la base de datos si no existe
            createDatabaseIfNotExists();

            // Obtener los datos de conexión desde config.properties
            String url = properties.getProperty("db.url");
            String username = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");

            // Intentar registrar el driver explícitamente
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("No se pudo cargar el driver de MySQL", e);
            }

            // Conectar a la base de datos
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
        // Extraer el nombre de la base de datos de la URL
        String url = properties.getProperty("db.url");
        String dbName = url.substring(url.lastIndexOf('/') + 1);

        // Crear una URL sin la base de datos específica
        String baseUrl = url.substring(0, url.lastIndexOf('/'));

        // Obtener credenciales
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        // Conectar al servidor MySQL sin especificar una base de datos
        try (Connection rootConnection = DriverManager.getConnection(baseUrl, username, password);
                Statement stmt = rootConnection.createStatement()) {

            // Crear la base de datos si no existe
            String createDbSQL = "CREATE DATABASE IF NOT EXISTS " + dbName;
            stmt.executeUpdate(createDbSQL);
            System.out.println("Base de datos '" + dbName + "' creada o verificada con éxito");
        }
    }

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