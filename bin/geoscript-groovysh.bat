@echo off
setlocal enableextensions enabledelayedexpansion

if not "%GROOVY_HOME%" == "" goto groovy_home_set

echo.
echo ERROR: GROOVY_HOME not found in your environment.
echo Please set the GROOVY_HOME variable in your environment to match the
echo location of your Groovy installation
echo.
goto error

:groovy_home_set

if exist "%GROOVY_HOME%\bin\groovysh.bat" goto groovy_home_ok

echo.
echo ERROR: GROOVY_HOME is set to an invalid directory.
echo GROOVY_HOME = "%GROOVY_HOME%"
echo Please set the GROOVY_HOME variable in your environment to match the
echo location of your Groovy installation
echo.
goto error

:groovy_home_ok
set GROOVY="%GROOVY_HOME%\bin\groovysh.bat"

@REM change directory to the lib directory
set CWD=%CD%
cd %~dp0../lib

@REM build up the classpath
set CLASSPATH=;
FOR /R %%G IN (*.jar) DO (
     SET CLASSPATH=!CLASSPATH!;%%G
)

cd %CWD%
%GROOVY% -cp "%CLASSPATH%" %*
goto end

:error
     set ERROR_CODE=1

:end
