@ECHO OFF
REM Builds the development copy of VASSAL from the source in this repository.
REM Double-click this file, or run it from a terminal. Takes about a minute.
REM Tests, style checks and the PDF guides are skipped to keep it fast.

PUSHD "%~dp0.."
CALL mvnw.cmd -B package -DskipTests=true -Dcheckstyle.skip=true -Dspotbugs.skip=true -Dmaven.javadoc.skip=true -Dasciidoctor.skip=true -Dclirr.skip=true -Dlicense.skipDownloadLicenses
SET RESULT=%ERRORLEVEL%
POPD

ECHO.
IF %RESULT% EQU 0 (
  ECHO Build finished. Run ai-player\run.bat to start it.
) ELSE (
  ECHO Build FAILED. Scroll up to the first line marked ERROR.
)
PAUSE
EXIT /B %RESULT%
