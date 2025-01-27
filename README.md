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
   ```
   git clone <URL_DEL_REPOSITORIO>
   ```

2. Navega al directorio del proyecto:
   ```
   cd discord-bot
   ```

3. Asegúrate de tener [Gradle](https://gradle.org/install/) instalado en tu sistema.

4. Ejecuta el siguiente comando para construir el proyecto:
   ```
   gradle build
   ```

## Uso

Para ejecutar el bot, asegúrate de tener configuradas las credenciales de la base de datos y otros parámetros necesarios en el archivo `src/main/resources/config.properties`. Luego, ejecuta la clase `Main.java`:

```
gradle run
```

## Contribuciones

Las contribuciones son bienvenidas. Si deseas agregar nuevas funcionalidades o mejorar el código, por favor abre un issue o un pull request.

## Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo LICENSE para más detalles.
