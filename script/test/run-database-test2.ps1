# Database test runner
param(
    [string]$DbUrl = "jdbc:h2:mem:testdb",
    [string]$DbUser = "sa",
    [string]$DbPassword = "",
    [string]$SqlFile = "database-test.sql"
)

Write-Host "=== Database Function Test ===" -ForegroundColor Cyan
Write-Host "Database URL: $DbUrl"
Write-Host "SQL file: $SqlFile"
Write-Host ""

if (-Not (Test-Path $SqlFile)) {
    Write-Host "Error: SQL file '$SqlFile' does not exist!" -ForegroundColor Red
    exit 1
}

# Look for H2 JAR in the .m2 repository
$h2Jar = Get-ChildItem -Path "$env:USERPROFILE\.m2\repository\com\h2database\h2\" -Filter h2*.jar -Recurse | Sort-Object LastWriteTime -Descending | Select-Object -First 1

if ($h2Jar) {
    Write-Host "Found H2 JAR: $($h2Jar.FullName)" -ForegroundColor Green
    & java -cp "`$($h2Jar.FullName)`" org.h2.tools.Shell -url $DbUrl -user $DbUser -password $DbPassword -sql $SqlFile
    exit $LASTEXITCODE
} else {
    Write-Host "Warning: H2 JAR file not found in the local Maven repository." -ForegroundColor Yellow
    Write-Host "Please manually execute one of the following commands:" -ForegroundColor Yellow
    Write-Host "1. If you have H2 database command line tools:"
    Write-Host "   java -cp h2.jar org.h2.tools.Shell -url $DbUrl -user $DbUser -password $DbPassword -sql $SqlFile"
    Write-Host "2. Or use a database client to connect to $DbUrl and execute SQL in $SqlFile"
    Write-Host ""
    Write-Host "Note: This test script uses in-memory H2 database (jdbc:h2:mem:testdb), each run is a fresh database." -ForegroundColor Yellow
    exit 0
}