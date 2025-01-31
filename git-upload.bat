@echo off
setlocal

if "%~1"=="" (
    echo Debes proporcionar un mensaje para el commit.
    exit /b 1
)

set COMMIT_MSG=%~1

python uml.py
git add .
git commit -m "%COMMIT_MSG%"
git push

endlocal
