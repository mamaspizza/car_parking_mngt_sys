
set JAVA_HOME=C:\Program Files\Java\jdk1.6.0_26
set PATH=%JAVA_HOME%\bin

set BASEDIR="D:\SDKs\androidpn-server-0.5.0"
set CLASSPATH=%BASEDIR%\conf
start "Androidpn" "%JAVA_HOME%\bin\java" -server -classpath %CLASSPATH% -jar %BASEDIR%\lib\starter.jar
"%JAVA_HOME%\bin\java" -server -classpath %CLASSPATH% -Dbase.dir=%BASEDIR% -jar %BASEDIR%\lib\starter.jar