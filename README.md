# Proyecto Discord Bot con JDA

Este proyecto es un bot de Discord desarrollado en Java utilizando la biblioteca JDA (Java Discord API). El bot está diseñado para ser modular y extensible, permitiendo la adición de nuevas funcionalidades y comandos de manera sencilla.

## Estructura del Proyecto

El proyecto está organizado de la siguiente manera:

```
discord-bot
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── bot
│   │   │   │   ├── Main.java               # Punto de entrada de la aplicación
│   │   │   │   ├── commands
│   │   │   │   │   ├── ModuleManager.java  # Gestión de módulos de comandos
│   │   │   │   │   ├── modules
│   │   │   │   │   │   ├── ManageCommands.java # Comandos de gestión
│   │   │   │   │   │   ├── ModCommands.java    # Comandos de moderación
│   │   │   │   │   │   └── UserCommands.java   # Comandos de usuario
│   │   │   │   ├── gui
│   │   │   │   │   └── GuiManager.java      # Gestión de la interfaz gráfica
│   │   │   │   └── utils
│   │   │   │       └── Utils.java           # Métodos de utilidad
│   │   └── resources
│   │       └── config.properties             # Archivo de configuración
├── build.gradle                               # Script de construcción de Gradle
├── settings.gradle                            # Configuración del proyecto en Gradle
└── README.md                                  # Documentación del proyecto
```

## Instalación

1. Clona el repositorio en tu máquina local:
   ```sh
   git clone <URL_DEL_REPOSITORIO>
   ```

2. Navega al directorio del proyecto:
   ```sh
   cd discord-bot
   ```

3. Asegúrate de tener [Gradle](https://gradle.org/install/) instalado en tu sistema.

4. Ejecuta el siguiente comando para construir el proyecto:
   ```sh
   gradle build
   ```

## Configuración

Antes de ejecutar el bot, asegúrate de configurar las credenciales y parámetros necesarios en el archivo `src/main/resources/config.properties`. Aquí hay un ejemplo de cómo debería verse el archivo:

```properties
# Configuración del bot de Discord
token=TU_TOKEN_DE_DISCORD
prefix=!

# Configuración de la base de datos
db.url=jdbc:mysql://localhost:3306/discordbot
db.username=root
db.password=contraseña
```

## Uso

Para ejecutar el bot, utiliza el siguiente comando:

```sh
gradle run
```

## Comandos Disponibles

El bot incluye los siguientes comandos por defecto:

- `/ban` - Banear a un usuario
- `/kick` - Expulsar a un usuario
- `/mute` - Silenciar a un usuario

Puedes agregar más comandos según sea necesario en los módulos correspondientes.

## Contribuciones

Las contribuciones son bienvenidas. Si deseas agregar nuevas funcionalidades o mejorar el código, por favor abre un issue o un pull request.

## Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo [LICENSE](./LICENSE) para más detalles.

## Contacto

Para cualquier consulta o soporte, puedes contactarnos a través de [correo electrónico](mailto:pelayops1041@gmail.com).
