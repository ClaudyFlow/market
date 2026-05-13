@echo off
REM Simple database test instructions
echo ===== Database Function Test =====
echo.
echo SQL test script: database-test.sql
echo.
if not exist "database-test.sql" (
    echo Error: database-test.sql not found!
    exit /b 1
)
echo Found: database-test.sql
echo.
echo To run the test:
echo.
echo For H2 database:
echo   java -cp h2.jar org.h2.tools.Shell -url jdbc:h2:mem:testdb -user sa -password -sql database-test.sql
echo.
echo For PostgreSQL:
echo   1. Ensure PostgreSQL is running
echo   2. Connect with psql or client
echo   3. Execute commands from database-test.sql
echo.
echo See DATABASE_TEST_GUIDE.md for details
echo.