@ECHO OFF
REM Starts the development copy of VASSAL (the Module Manager window).
REM Run build.bat first. To open a module directly, drag a .vmod file onto
REM this file, or run:  run.bat "C:\path\to\module.vmod"
REM
REM Settings are kept in a separate "vassal-dev-conf" folder next to the
REM repository, so your installed VASSAL and its preferences are not touched.

SET "TARGET=%~dp0..\release-prepare\target"
SET "CONF=%~dp0..\..\vassal-dev-conf"

IF NOT EXIST "%TARGET%\lib\vassal-app-*.jar" (
  ECHO The development build was not found. Run ai-player\build.bat first.
  PAUSE
  EXIT /B 1
)

IF NOT EXIST "%CONF%" MKDIR "%CONF%"

PUSHD "%TARGET%"
IF "%~1"=="" (
  START "VASSAL dev" javaw -DVASSAL.conf="%CONF%" -cp "lib/*" VASSAL.launch.ModuleManager
) ELSE (
  START "VASSAL dev" javaw -Xmx1024M -DVASSAL.conf="%CONF%" -cp "lib/*" VASSAL.launch.Player --load --standalone -- "%~f1"
)
POPD
