@echo off
echo Stopping Java processes...
taskkill /F /IM java.exe 2>nul

echo.
echo Building project...
C:\mvn\apache-maven-3.6.3\bin\mvn.cmd clean package -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Build successful! Starting application...
    start "BookLoverBox" java -jar target\bookloverbox-0.0.1-SNAPSHOT.jar
    echo Application started in new window
) else (
    echo.
    echo Build failed!
    pause
)
