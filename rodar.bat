@echo off
cd /d "%~dp0"
if not exist build mkdir build
javac -encoding UTF-8 -d build entidades\*.java gui\*.java interfaces\*.java motor\*.java tipos\*.java
if errorlevel 1 (
    pause
    exit /b 1
)
java -cp build gui.Cenario
pause