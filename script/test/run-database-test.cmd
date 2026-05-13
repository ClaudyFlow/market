@echo off
REM Database test runner batch script (CMD version)
REM Executes database-test.sql for database functionality testing

setlocal

rem Configure database connection and SQL file
set "DB_URL=jdbc:h2:mem:testdb"
set "DB_USER=sa"
set "DB_PASSWORD="
set "SQL_FILE=database-test.sql"

echo ===== Database Function Test =====
echo Database URL: %DB_URL%
echo SQL file: %SQL_FILE%
echo.

rem Check if SQL file exists
if not exist "%SQL_FILE%" (
    echo Error: SQL file '%SQL_FILE%' does not exist!
    exit /b 1
)

rem Find H2 JAR file
set "H2_JAR="

rem First search in current directory and subdirectories
for /r %%I in (h2*.jar) do (
    if not defined H2_JAR set "H2_JAR=%%I"
)

rem If not found, search in backend's target/dependency
if not defined H2_JAR (
    for /r ..\backend\target\dependency\ %%I in (h2*.jar) do (
        if not defined H2_JAR set "H2_JAR=%%I"
    )
)

rem If still not found, try deeper path
if not defined H2_JAR (
    for /r ../../backend/target/dependency\ %%I in (h2*.jar) do (
        if not defined H2_JAR set "H2_JAR=%%I"
    )
)

rem Finally try to find in user's Maven repository
if not defined H2_JAR (
    for /r "%USERPROFILE%\.m2\repository\com\h2database\h2\" %%I in (h2*.jar) do (
        if not defined H2_JAR set "H2_JAR=%%I"
    )
)

if defined H2_JAR (
    echo Found H2 JAR: %H2_JAR%
    java -cp "%H2_JAR%" org.h2.tools.Shell -url %DB_URL% -user %DB_USER% -password %DB_PASSWORD% -sql "%SQL_FILE%"
    exit /b %ERRORLEVEL%
) else (
    echo Warning: H2 JAR file not found.
    echo Please manually execute one of the following commands:
    echo.
    echo 1. If you have H2 database command line tools:
    echo    java -cp h2.jar org.h2.tools.Shell -url %DB_URL% -user %DB_USER% -password %DB_PASSWORD% -sql %SQL_FILE%
    echo.
    echo 2. Or use a database client to connect to %DB_URL% and execute SQL in %SQL_FILE%
    echo.
    echo Note: This test script uses in-memory H2 database (jdbc:h2:mem:testdb), each run is a fresh database.
    exit /b 0
)

endlocal