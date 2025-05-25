@echo off
echo ================================
echo Discord Bot Manager - Android
echo Herramientas de desarrollo
echo ================================
echo.

:menu
echo Selecciona una opcion:
echo 1. Sincronizar archivos del backend
echo 2. Construir aplicacion
echo 3. Abrir en Android Studio
echo 4. Ejecutar en dispositivo/emulador
echo 5. Ejecutar con live reload
echo 6. Servir localmente para pruebas
echo 7. Limpiar archivos temporales
echo 8. Ver logs de la aplicacion
echo 9. Salir
echo.

set /p choice="Ingresa tu opcion (1-9): "

if "%choice%"=="1" goto sync
if "%choice%"=="2" goto build
if "%choice%"=="3" goto open
if "%choice%"=="4" goto run
if "%choice%"=="5" goto livereload
if "%choice%"=="6" goto serve
if "%choice%"=="7" goto clean
if "%choice%"=="8" goto logs
if "%choice%"=="9" goto exit

echo Opcion invalida. Intenta de nuevo.
goto menu

:sync
echo.
echo [1/1] Sincronizando archivos...
npm run copy
if %errorlevel% neq 0 (
    echo Error sincronizando archivos
    pause
    goto menu
)
echo ✅ Archivos sincronizados exitosamente
echo.
pause
goto menu

:build
echo.
echo [1/2] Sincronizando archivos...
npm run copy
if %errorlevel% neq 0 (
    echo Error sincronizando archivos
    pause
    goto menu
)
echo [2/2] Construyendo aplicacion...
npm run sync
if %errorlevel% neq 0 (
    echo Error construyendo aplicacion
    pause
    goto menu
)
echo ✅ Aplicacion construida exitosamente
echo.
pause
goto menu

:open
echo.
echo Abriendo en Android Studio...
npm run open
echo ✅ Android Studio abierto
echo.
pause
goto menu

:run
echo.
echo Ejecutando en dispositivo/emulador...
echo (Asegurate de tener un dispositivo conectado o emulador ejecutandose)
npm run run
echo.
pause
goto menu

:livereload
echo.
echo Ejecutando con live reload...
echo (Asegurate de tener un dispositivo conectado o emulador ejecutandose)
npm run live-reload
echo.
pause
goto menu

:serve
echo.
echo Sirviendo localmente en http://localhost:8100...
npm run serve
echo.
pause
goto menu

:clean
echo.
echo Limpiando archivos temporales...
npm run clean
if %errorlevel% neq 0 (
    echo Error limpiando archivos
    pause
    goto menu
)
echo ✅ Archivos temporales eliminados
echo.
pause
goto menu

:logs
echo.
echo Mostrando logs de Capacitor...
echo (Presiona Ctrl+C para salir)
npx cap run android --list
echo.
pause
goto menu

:exit
echo.
echo ¡Hasta luego!
exit
