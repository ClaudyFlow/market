@echo off
REM =============================================
REM Database Test Script for CMD
REM Runs the database-test.sql against an in-memory H2 database
REM =============================================

setlocal

rem Set the SQL file path
set "SQL_FILE=D:\market\script\test\database-test.sql"

rem Check if SQL file exists
if not exist "%SQL_FILE%" (
    echo Error: SQL file not found at %SQL_FILE%
    exit /b 1
)

rem Set the path to the H2 JAR (from the backend module's target/h2-lib)
set "H2_JAR_DIR=D:\market\backend\target\h2-lib"

rem Check if the H2 JAR directory exists and contains JAR files
if not exist "%H2_JAR_DIR%" (
    echo Error: H2 JAR directory not found at %H2_JAR_DIR%
    echo Please run 'mvn dependency:copy-dependencies -DincludeArtifactIds=h2 -DoutputDirectory=target/h2-lib' in the backend module first.
    exit /b 1
)

rem Find the H2 JAR file (assuming only one)
for %%I in ("%H2_JAR_DIR%\h2*.jar") do set "H2_JAR=%%I"

if not defined H2_JAR (
    echo Error: No H2 JAR found in %H2_JAR_DIR%
    exit /b 1
)

echo Found H2 JAR: %H2_JAR%
echo SQL file: %SQL_FILE%
echo.

rem Run the H2 RunScript tool to execute the SQL file
echo Executing database test...
java -cp "%H2_JAR%" org.h2.tools.RunScript -url jdbc:h2:mem:testdb -user sa -password -script "%SQL_FILE%"

rem Check the exit code
if %errorlevel% neq 0 (
    echo.
    echo Error: Database test failed with exit code %errorlevel%
) else (
    echo.
    echo Database test completed successfully.
)

endlocal