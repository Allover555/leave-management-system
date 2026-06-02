@echo off
setlocal
chcp 65001 >nul

set "ROOT_DIR=%~dp0"
set "SCHEMA_FILE=%ROOT_DIR%backend\src\main\resources\schema.sql"
set "DB_NAME=leave_management"

if "%MYSQL_CMD%"=="" set "MYSQL_CMD=mysql"
if "%MYSQL_USER%"=="" set "MYSQL_USER=root"

set "MYSQL_ARGS=-u%MYSQL_USER% --default-character-set=utf8mb4"
if not "%MYSQL_PASSWORD%"=="" (
    set "MYSQL_ARGS=%MYSQL_ARGS% -p%MYSQL_PASSWORD%"
) else (
    set "MYSQL_ARGS=%MYSQL_ARGS% -p"
)

echo Initializing database "%DB_NAME%"...
"%MYSQL_CMD%" %MYSQL_ARGS% -e "DROP DATABASE IF EXISTS %DB_NAME%; CREATE DATABASE %DB_NAME% DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
if errorlevel 1 exit /b 1

"%MYSQL_CMD%" %MYSQL_ARGS% %DB_NAME% < "%SCHEMA_FILE%"
if errorlevel 1 exit /b 1

echo DONE
