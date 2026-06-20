@echo off
chcp 65001 >nul
if not exist out (
    echo Execute primeiro: compilar.bat
    pause
    exit /b 1
)
java -cp out Main