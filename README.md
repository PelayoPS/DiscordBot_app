# Proyecto Discord Bot con JDA

Este proyecto es un bot de Discord desarrollado en Java utilizando la biblioteca JDA (Java Discord API). El bot está diseñado para ser modular y extensible, permitiendo la adición de nuevas funcionalidades y comandos de manera sencilla.

## Estructura del Proyecto

El proyecto está organizado de la siguiente manera:

```
Directory structure:
└── pelayops-discordbot_app/  # Directorio raíz del proyecto
    ├── README.md  # Archivo de documentación principal del proyecto
    ├── LICENSE  # Archivo que contiene la licencia del proyecto
    ├── uml.py  # Script en Python para generar diagramas UML
    ├── src/  # Directorio que contiene el código fuente del proyecto
    │   └── main/  # Directorio principal de código fuente
    │       ├── java/  # Directorio que contiene el código fuente en Java
    │       │   └── bot/  # Paquete principal del bot
    │       │       ├── Bot.java  # Clase principal del bot
    │       │       ├── Main.java  # Clase que contiene el método main para iniciar el bot
    │       │       ├── commands/  # Paquete que contiene las clases relacionadas con los comandos del bot
    │       │       │   ├── ICommand.java  # Interfaz para definir comandos
    │       │       │   ├── ModuleManager.java  # Clase para gestionar los módulos de comandos
    │       │       │   └── modules/  # Subpaquete que contiene los módulos de comandos específicos
    │       │       │       ├── CommandManager.java  # Clase para gestionar los comandos
    │       │       │       ├── ManageCommands.java  # Clase para gestionar comandos administrativos
    │       │       │       ├── ModCommands.java  # Clase para comandos de moderación
    │       │       │       ├── UserCommands.java  # Clase para comandos de usuario
    │       │       │       └── mod/  # Subpaquete que contiene comandos específicos de moderación
    │       │       │           ├── Ban.java  # Clase para el comando de banear usuarios
    │       │       │           ├── Kick.java  # Clase para el comando de expulsar usuarios
    │       │       │           └── Mute.java  # Clase para el comando de silenciar usuarios
    │       │       ├── database/  # Paquete que contiene las clases relacionadas con la base de datos
    │       │       │   └── DatabaseManager.java  # Clase para gestionar la conexión y operaciones con la base de datos
    │       │       ├── events/  # Paquete que contiene las clases relacionadas con los eventos del bot
    │       │       │   └── EventListener.java  # Clase para escuchar y manejar eventos
    │       │       ├── gui/  # Paquete que contiene las clases relacionadas con la interfaz gráfica de usuario
    │       │       │   └── GuiManager.java  # Clase para gestionar la interfaz gráfica de usuario
    │       │       └── utils/  # Paquete que contiene clases utilitarias
    │       │           └── Utils.java  # Clase con métodos utilitarios
    │       └── resources/  # Directorio que contiene recursos adicionales del proyecto
    │           └── config.properties  # Archivo de configuración del proyecto
    ├── uml_output/  # Directorio que contiene la salida de los diagramas UML generados
    │   └── diagram.puml  # Archivo de diagrama UML generado
    └── .github/  # Directorio que contiene configuraciones específicas de GitHub
        └── workflows/  # Directorio que contiene flujos de trabajo de GitHub Actions
            └── generate-uml.yml  # Archivo de configuración para generar diagramas UML automáticamente

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

5. Para generar el diagrama de clases UML, ejecuta el siguiente script con el argumento del path del proyecto:
   ```sh
   python uml.py <ruta_al_proyecto_java>
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

## Diagrama UML

A continuación se muestra un diagrama UML del proyecto:

![Diagrama UML](./uml_output/diagrama.png)