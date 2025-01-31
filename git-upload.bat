@echo off
REM Desactiva la visualización de comandos en el script.

setlocal
REM Inicia un entorno local para las variables del script.

if "%~1"=="" (
    echo Debes proporcionar un mensaje para el commit.
    exit /b 1
)
REM Verifica si se proporcionó un argumento (mensaje del commit). Si no, muestra un mensaje de error y termina el script.

set COMMIT_MSG=%~1
REM Asigna el primer argumento del script a la variable COMMIT_MSG.

python uml.py
REM Ejecuta el script de Python para generar el diagrama UML.

git add .
REM Añade todos los cambios en el repositorio a la zona de preparación (staging area).

git commit -m "%COMMIT_MSG%"
REM Realiza un commit con el mensaje proporcionado como argumento.

git push
REM Envía los cambios al repositorio remoto.

endlocal
REM Finaliza el entorno local para las variables del script.
