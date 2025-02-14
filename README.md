# Proyecto Discord Bot con JDA

Este proyecto es un bot de Discord desarrollado en Java utilizando la biblioteca JDA (Java Discord API). El bot está diseñado para ser modular y extensible, permitiendo la adición de nuevas funcionalidades y comandos de manera sencilla.

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

5. Para generar el diagrama de clases UML:

   5a. Utiliza la herramienta creada a partir del script original:
   - [UMLGenerator](https://github.com/PelayoPS/UML-Generator.git)

   5b. Ejecuta el siguiente comando(el script no es mantenido a largo plazo, por lo que se recomienda utilizar la herramienta mencionada en el paso 5a):
   ```sh
   ./gradlew runUmlGenerator
   ```
   5c. Script en python para generar el diagrama UML(no está actualizado para detectar los tipos de relaciones entre las clases, por lo que se recomienda utilizar la herramienta mencionada en el paso 5a):
   ```sh
   python uml.py
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

### Comandos de moderación

- `/ban` - Banear a un usuario
- `/kick` - Expulsar a un usuario
- `/mute` - Silenciar a un usuario

### Comandos de administración

- `/createrole` - Crear un rol
- `/deleterole` - Eliminar un rol

### Comandos de usuario

- `/avatar` - Mostrar el avatar de un usuario


Puedes agregar más comandos según sea necesario en los módulos correspondientes.

## Contribuciones

Las contribuciones son bienvenidas. Si deseas agregar nuevas funcionalidades o mejorar el código, por favor abre un issue o un pull request.

## Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo [LICENSE](./LICENSE) para más detalles.

## Contacto

Para cualquier consulta o soporte, puedes contactarnos a través de [correo electrónico](mailto:pelayops1041@gmail.com).

## Diagrama UML

A continuación se muestra un diagrama UML del proyecto:

![Diagrama UML](./uml_output/diagrama.svg)


## Estructura del Proyecto

El proyecto está organizado de la siguiente manera:

```
Directory structure:
└── pelayops-discordbot_app/
    ├── README.md # Readme del proyecto
    ├── LICENSE # Licencia del proyecto
    ├── git-upload.bat # Script para subir cambios a GitHub y crear la build más reciente
    ├── uml.py # Script en python para generar el diagrama UML
    ├── logs/ # Directorio para almacenar los logs del bot
    ├── src/ # Directorio principal del código fuente
    │   └── main/
    │       ├── java/
    │       │   └── bot/ # Paquete principal del bot
    │       │       ├── Bot.java # Clase principal del bot
    │       │       ├── Main.java # Clase principal del proyecto
    │       │       ├── UMLGenerator.java # Clase para generar el diagrama UML
    │       │       ├── commands/ # Paquete para los comandos del bot
    │       │       │   ├── ICommand.java # Interfaz para los comandos
    │       │       │   ├── ModuleManager.java  # Clase para gestionar los módulos
    │       │       │   └── modules/ # Paquete para los módulos de comandos
    │       │       │       ├── CommandManager.java # Clase para gestionar los comandos
    │       │       │       ├── ManageCommands.java # Clase para gestionar los comandos de administración
    │       │       │       ├── ModCommands.java # Clase para gestionar los comandos de moderación
    │       │       │       ├── UserCommands.java # Clase para gestionar los comandos de usuario
    │       │       │       ├── management/ # Paquete para los comandos de administración
    │       │       │       │   ├── CreateRole.java # Comando para crear un rol
    │       │       │       │   └── DeleteRole.java # Comando para eliminar un rol
    │       │       │       ├── mod/ # Paquete para los comandos de moderación
    │       │       │       │   ├── Ban.java # Comando para banear a un usuario
    │       │       │       │   ├── Kick.java # Comando para expulsar a un usuario
    │       │       │       │   └── Mute.java # Comando para silenciar a un usuario
    │       │       │       └── user/  # Paquete para los comandos de usuario
    │       │       │           └── Avatar.java # Comando para mostrar el avatar de un usuario
    │       │       ├── database/ # Paquete para la gestión de la base de datos
    │       │       │   └── DatabaseManager.java # Clase para gestionar la base de datos
    │       │       ├── events/ # Paquete para los eventos del bot
    │       │       │   └── EventListener.java # Clase para gestionar los eventos
    │       │       ├── gui/ # Paquete para la interfaz gráfica
    │       │       │   └── GuiManager.java # Clase para gestionar la interfaz gráfica
    │       │       └── utils/ # Paquete para las utilidades
    │       │           └── Utils.java # Clase con métodos de utilidad
    │       └── resources/ # Directorio para los recursos del proyecto
    │           ├── config.properties
    │           └── logback.xml
    └── uml_output/ # Directorio para almacenar el diagrama UML
        └── diagrama.svg # Diagrama UML del proyecto

```
