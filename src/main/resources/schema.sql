-- Configuración del esquema de la base de datos para el bot de Discord

-- Tabla de Usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario BIGINT PRIMARY KEY,
    tipo_usuario VARCHAR(50) NOT NULL,
    nivel INT NOT NULL DEFAULT 1,
    puntos_xp INT NOT NULL DEFAULT 0,
    timestamp_ultimo_mensaje BIGINT
);

-- Tabla de Penalizaciones
CREATE TABLE IF NOT EXISTS penalizaciones (
    id_penalizacion BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL,
    id_admin_mod BIGINT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    fecha TIMESTAMP NOT NULL,
    razon TEXT NOT NULL,
    duracion_segundos BIGINT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_admin_mod) REFERENCES usuarios(id_usuario)
);
