# Discord Bot Manager - Aplicación de Escritorio

Esta es la aplicación de escritorio para gestionar el bot de Discord. Proporciona una interfaz gráfica para configurar y monitorear el bot.

## Requisitos

- Node.js 16.x o superior
- npm 7.x o superior

## Instalación

1. Instala las dependencias:
```bash
npm install
```

2. Para desarrollo:
```bash
npm run dev
```

3. Para producción:
```bash
npm start
```

## Construcción

Para construir la aplicación para tu plataforma:

```bash
npm run build
```

Los archivos de distribución se generarán en la carpeta `dist`.

## Características

- Interfaz gráfica para gestionar el bot
- Configuración del bot a través de la interfaz
- Monitoreo del estado del bot
- Gestión de comandos y eventos
- Configuración de permisos

## Estructura del Proyecto

```
desktop/
├── main.js           # Punto de entrada principal
├── package.json      # Dependencias y scripts
└── README.md         # Este archivo
```

## Desarrollo

La aplicación está construida con Electron y se comunica con el backend a través de una API REST. El backend debe estar ejecutándose en `http://localhost:8080` por defecto, pero esto se puede configurar en la aplicación.

## Licencia

ISC 