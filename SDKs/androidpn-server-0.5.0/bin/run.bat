@echo off

REM #
:run

set JAVA_HOME="C:\Program/Files\Java\jre1.6.0"

if "%JAVA_HOME%" == "" goto javaerror
if not exist "%JAVA_HOME%\bin\java.exe" goto javaerror
goto run

:javaerror
echo.
echo Error: JAVA_HOME environment variable not set, Androidpn not started.
echo.
goto end

set BASEDIR="H:\androidpn-server-0.5.0"
set CLASSPATH=%BASEDIR%\conf
start "Androidpn" "%JAVA_HOME%\bin\java" -server -classpath %CLASSPATH% -jar %BASEDIR%\lib\starter.jar
"%JAVA_HOME%\bin\java" -server -classpath %CLASSPATH% -Dbase.dir=%BASEDIR% -jar %BASEDIR%\lib\starter.jar
goto end

:end
