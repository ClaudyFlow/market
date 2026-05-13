@echo off
REM =============================================
REM Database Test Script using JDK 22
REM Compiles and runs DatabaseTestRunner which executes database-test.sql
REM =============================================

setlocal

rem Set JAVA_HOME to JDK 22
set "JAVA_HOME=C:\Program Files\Java\jdk-22"
if not exist "%JAVA_HOME%" (
    echo Error: JDK 22 not found at %JAVA_HOME%
    exit /b 1
)

rem Set the H2 JAR path
set "H2_JAR=D:\market\backend\target\h2-lib\h2-2.3.232.jar"
if not exist "%H2_JAR%" (
    echo Error: H2 JAR not found at %H2_JAR%
    echo Please run 'mvn dependency:copy-dependencies -DincludeArtifactIds=h2 -DoutputDirectory=target/h2-lib' in the backend module first.
    exit /b 1
)

rem Set the classpath
set "CLASSPATH=.;%H2_JAR%"

rem Change to the directory containing the Java source
pushd D:\market\script\test

rem Compile the Java file
echo Compiling DatabaseTestRunner.java...
"%JAVA_HOME%\bin\javac" -cp "%CLASSPATH%" DatabaseTestRunner.java
if errorlevel 1 (
    echo Error: Compilation failed.
    popd
    exit /b 1
)

rem Run the compiled class
echo Running DatabaseTestRunner...
"%JAVA_HOME%\bin\java" -cp "%CLASSPATH%" DatabaseTestRunner
set "EXIT_CODE=%errorlevel%"

popd

if %EXIT_CODE% neq 0 (
    echo.
    echo Error: Database test failed with exit code %EXIT_CODE%
) else (
    echo.
    echo Database test completed successfully.
)

endlocal