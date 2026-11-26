@echo off
setlocal
title Student Result and Grade Management System

echo ==========================================================
echo   Student Result and Grade Management System
echo ==========================================================
echo.

set "PRJ_DIR=%~dp0"
if "%PRJ_DIR:~-1%"=="\" set "PRJ_DIR=%PRJ_DIR:~0,-1%"

:: Check Java
if defined JAVA_HOME goto check_java_home
if exist "C:\Program Files\Java\jdk-26.0.1" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-26.0.1"
    goto check_java_home
)
for /f "tokens=*" %%i in ('where java 2^>nul') do (
    set "JAVA_EXE=%%i"
    goto found_java
)
:check_java_home
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo [ERROR] Java JDK was not found. Please install Java JDK or set JAVA_HOME.
    pause
    exit /b 1
)
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"

:found_java
echo [INFO] Using Java from: %JAVA_EXE%

:: Set Tomcat
if exist "%PRJ_DIR%\apache-tomcat-10.1.33" (
    set "CATALINA_HOME=%PRJ_DIR%\apache-tomcat-10.1.33"
) else if exist "E:\Tools\apache-tomcat-10.1.33" (
    set "CATALINA_HOME=E:\Tools\apache-tomcat-10.1.33"
) else (
    echo [ERROR] Apache Tomcat 10.1 directory not found.
    pause
    exit /b 1
)
echo [INFO] Using Tomcat from: %CATALINA_HOME%

:: Set Maven
if exist "%PRJ_DIR%\apache-maven-3.9.9\bin\mvn.cmd" (
    set "MVN_CMD=%PRJ_DIR%\apache-maven-3.9.9\bin\mvn.cmd"
) else if exist "E:\Tools\apache-maven-3.9.9\bin\mvn.cmd" (
    set "MVN_CMD=E:\Tools\apache-maven-3.9.9\bin\mvn.cmd"
) else (
    set "MVN_CMD=mvn"
)

:: Always rebuild so the deployed WAR matches the source
echo [INFO] Building application WAR with Maven...
cd /d "%PRJ_DIR%"
set "MVN_REPO="
if exist "E:\Tools\m2" set "MVN_REPO=-Dmaven.repo.local=E:\Tools\m2"
call "%MVN_CMD%" -q clean package -DskipTests %MVN_REPO%
if errorlevel 1 (
    echo [ERROR] Maven build failed.
    pause
    exit /b 1
)

:: Deploy to Tomcat webapps as ROOT.war
echo [INFO] Deploying application to Tomcat...
copy /Y "%PRJ_DIR%\target\StudentResultManagement.war" "%CATALINA_HOME%\webapps\ROOT.war" >nul
if exist "%CATALINA_HOME%\webapps\ROOT" (
    rmdir /S /Q "%CATALINA_HOME%\webapps\ROOT" >nul 2>&1
)

:: Start browser after brief pause
echo.
echo ==========================================================
echo   Server is starting up at: http://localhost:8080/
echo   Default Admin Login:
echo     Username: admin
echo     Password: admin123
echo ==========================================================
echo.
start "" cmd /c "timeout /t 4 /nobreak >nul && start http://localhost:8080/"

:: Launch Tomcat
cd /d "%CATALINA_HOME%\bin"
call "%CATALINA_HOME%\bin\catalina.bat" run

endlocal
